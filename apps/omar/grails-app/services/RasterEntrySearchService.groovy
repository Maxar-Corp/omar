//import javax.jws.WebParam
import org.hibernate.FetchMode as FM

class RasterEntrySearchService
{
  def grailsApplication

  //static expose = ['xfire']

  static transactional = false


  List<RasterEntryQuery> runQuery(RasterEntryQuery rasterEntryQuery, Map<String, String> params)
  {
    def x = {
      if ( rasterEntryQuery?.groundGeom )
      {
        addToCriteria(rasterEntryQuery.createIntersection("groundGeom"))
      }
      if ( rasterEntryQuery?.startDate || rasterEntryQuery?.endDate )
      {
        addToCriteria(rasterEntryQuery.createDateRange("acquisitionDate"))
      }
      if ( params?.max )
      {
        maxResults(params.max as Integer)
      }
      if ( params?.offset )
      {
        firstResult(params.offset as Integer)
      }
      if ( params?.sort && params?.order )
      {
        def sortColumn

        // HACK:  Need to find a better way to do this
        switch ( params?.sort )
        {
          case "width":
          case "height":
          case "numberOfBands":
          case "bitDepth":
          case "dataType":
            sortColumn = "r.${params?.sort}"
            break
          default:
            sortColumn = params?.sort
        }
        order(sortColumn, params?.order)
      }
      rasterEntryQuery.searchTagNames?.size()?.times {i ->
        if ( rasterEntryQuery.searchTagNames[i] && rasterEntryQuery.searchTagValues[i] )
        {
          ilike(rasterEntryQuery.searchTagNames[i], "%${rasterEntryQuery.searchTagValues[i]}%")
        }
      }
      fetchMode("rasterEntry", FM.JOIN)
    }

    def metadata = RasterEntryMetadata.createCriteria().list(x)
    def rasterEntries = metadata?.collect {it.rasterEntry}
//    def rasterEntries = metadata?.rasterEntry

    //rasterEntries?.each { it.mainFile }

    return rasterEntries
  }



  List<Geometry> getGeometries(RasterEntryQuery rasterEntryQuery, Map<String, String> params)
  {
    def x = {
      projections { property("groundGeom") }
      if ( rasterEntryQuery?.groundGeom )
      {
        addToCriteria(rasterEntryQuery.createIntersection("groundGeom"))
      }
      if ( rasterEntryQuery?.startDate || rasterEntryQuery?.endDate )
      {
        addToCriteria(rasterEntryQuery.createDateRange("acquisitionDate"))
      }
      if ( params?.max )
      {
        maxResults(params.max as Integer)
      }
      if ( params?.offset )
      {
        firstResult(params.offset as Integer)
      }
      rasterEntryQuery.searchTagNames?.size()?.times {i ->
        if ( rasterEntryQuery.searchTagNames[i] && rasterEntryQuery.searchTagValues[i] )
        {
          ilike(rasterEntryQuery.searchTagNames[i], "%${rasterEntryQuery.searchTagValues[i]}%")
        }
      }
    }

    def geometries = RasterEntryMetadata.createCriteria().list(x)

    return geometries
  }

  int getCount(RasterEntryQuery rasterEntryQuery)
  {
    def totalCount = RasterEntryMetadata.createCriteria().get {
      projections { rowCount() }
      if ( rasterEntryQuery?.groundGeom )
      {
        addToCriteria(rasterEntryQuery.createIntersection("groundGeom"))
      }
      if ( rasterEntryQuery?.startDate || rasterEntryQuery?.endDate )
      {
        addToCriteria(rasterEntryQuery.createDateRange("acquisitionDate"))
      }
      rasterEntryQuery.searchTagNames?.size()?.times {i ->
        if ( rasterEntryQuery.searchTagNames[i] && rasterEntryQuery.searchTagValues[i] )
        {
          ilike(rasterEntryQuery.searchTagNames[i], "%${rasterEntryQuery.searchTagValues[i]}%")
        }
      }
    }

    return totalCount
  }
}
