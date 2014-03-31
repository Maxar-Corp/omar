package chipper

import geoscript.filter.Filter
import geoscript.workspace.WFS

class WfsClientService
{
  def getColumns(def serviceAddress, def typeName, def columnNames)
  {
    WFS wfs = createWorkspace( serviceAddress )

    //wfs?.layers?.each { println it.name }

    def columns = wfs[typeName]?.schema?.fields?.grep { it.name in columnNames }?.collect {
      [field: it.name, sortable: true, title: ( it.name.split( '_' )*.capitalize() ).join( ' ' )]
    }

    wfs?.close()

    columns
  }

  private static WFS createWorkspace(def serviceAddress)
  {
    def url = "${serviceAddress}?service=WFS&version=1.0.0&request=GetCapabilities"
    def wfs = new WFS( url )
    wfs
  }

  def getFeatureCount(def serviceAddress, def typeName, def filter)
  {
    def wfs = createWorkspace( serviceAddress )
    def count = wfs[typeName]?.count( filter ?: Filter.PASS )

    wfs?.close()
    count
  }

  def getFeature(def serviceAddress, def typeName, def columnNames, def options)
  {
    def wfs = createWorkspace( serviceAddress )
    def count = getFeatureCount( serviceAddress, typeName, options.filter )

    def data = wfs[typeName].collectFromFeature(
        max: options.rows,
        start: ( ( options.page - 1 ) * options.rows ),
        sort: [[options.sort ?: 'id', options.order ?: 'asc']],
        fields: columnNames,
        filter: options.filter ?: Filter.PASS
    ) {
      def row = it?.attributes?.inject( [:] ) { a, b ->
        switch ( b.key )
        {
        case 'ground_geom':
          a['ground_geom'] = b?.value?.bounds?.toString()
          break
        default:
          a[b.key] = b.value
        }
        a
      }
      row
    }

    wfs?.close()

    [total: count, rows: data]
  }
}