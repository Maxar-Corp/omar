//import javax.jws.WebParam


class RasterEntrySearchService
{
  def grailsApplication

  //static expose = ['xfire']

  static transactional = false


  Map<String, Object> runQuery(RasterEntryQuery rasterEntryQuery, Map<String, String> params, boolean includeCount = true)
  {
    def x = {
      createAlias("rasterEntry", "r")
      if ( rasterEntryQuery?.groundGeom )
      {
        addToCriteria(rasterEntryQuery.createIntersection("r.groundGeom"))
      }
      if ( rasterEntryQuery?.startDate || rasterEntryQuery?.endDate )
      {
        addToCriteria(rasterEntryQuery.createDateRange("r.acquisitionDate"))
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
          case "acquisitionDate":
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
    }

    def metadata = RasterEntryMetadata.createCriteria().list(x)
    def rasterEntries = metadata?.collect {it.rasterEntry}

    //rasterEntries?.each { it.mainFile }

    def totalCount = null

    if ( includeCount )
    {
      totalCount = getCount(rasterEntryQuery)
    }

    return [totalCount: totalCount, rasterEntries: rasterEntries]
  }

  int getCount(RasterEntryQuery rasterEntryQuery)
  {
    def totalCount = RasterEntryMetadata.createCriteria().get {
      projections { rowCount() }
      createAlias("rasterEntry", "r")
      if ( rasterEntryQuery?.groundGeom )
      {
        addToCriteria(rasterEntryQuery.createIntersection("r.groundGeom"))
      }
      if ( rasterEntryQuery?.startDate || rasterEntryQuery?.endDate )
      {
        addToCriteria(rasterEntryQuery.createDateRange("r.acquisitionDate"))
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
