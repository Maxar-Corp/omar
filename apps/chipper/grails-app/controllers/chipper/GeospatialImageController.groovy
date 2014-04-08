package chipper

import grails.converters.JSON

class GeospatialImageController
{
  def grailsApplication
  def sessionFactory
  def grailsLinkGenerator

//  def scaffold = true
  def index()
  {
    def domain = grailsApplication.getDomainClass( GeospatialImage.class.name )

    def columns = domain.persistantProperties.collect {
      [field: it.name, title: it.naturalName, type: it.type, sortable: true]
    }

    def tableModel = [
        url       : grailsLinkGenerator.link( action: 'getData' ),
        method    : 'get',
        columns   : [columns]
    ]

    [tableModel: tableModel]
  }

  def getData()
  {
    println params

    def data = [
        total: GeospatialImage.count(),
        rows : GeospatialImage.list(
            max: params.int( 'rows' ) ?: 10,
            offset: params.int( 'page' ) - 1 ?: 0,
            sort: params.sort ?: 'id',
            order: params.order ?: 'asc'
        )
    ]


    render contentType: 'application/json', text: data as JSON
  }


  private def getColumnNameMapping(Class clazz)
  {
    def domain = grailsApplication.getDomainClass( clazz.name )

    println domain.clazz.name

    def classMetadata = sessionFactory.getClassMetadata( clazz )

//println classMetadata .class.name

    def names = domain.persistantProperties.inject( [:] ) { a, b ->
      a[b.name] = classMetadata.getPropertyColumnNames( b.name ).first()
      a
    }

    names
  }
}
