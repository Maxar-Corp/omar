//import javax.jws.WebParam

class VideoDataSetSearchService
{
  //static expose = ['xfire']

  static transactional = true

  List<VideoDataSet> runQuery(
  /*@WebParam (name = "videoDataSetQuery", header = true)*/
  VideoDataSetQuery videoDataSetQuery,
  /*@WebParam (name = "params", header = true)*/
  Map<String, String> params)
  {
    def x = {
      if ( videoDataSetQuery?.groundGeom )
      {
        addToCriteria(videoDataSetQuery.createIntersection("groundGeom"))
      }
      if ( videoDataSetQuery?.startDate || videoDataSetQuery?.endDate )
      {
        addToCriteria(videoDataSetQuery.createDateRange("startDate", "endDate"))
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
            sortColumn = "v.${params?.sort}"
            break
          default:
            sortColumn = params?.sort
        }
        order(sortColumn, params?.order)
      }
      videoDataSetQuery.searchTagNames?.size()?.times {i ->
        if ( videoDataSetQuery.searchTagNames[i] && videoDataSetQuery.searchTagValues[i] )
        {
          ilike(videoDataSetQuery.searchTagNames[i], "%${videoDataSetQuery.searchTagValues[i]}%")
        }
      }
    }


    def metadata = VideoDataSetMetadata.createCriteria().list(x)

    return metadata*.videoDataSet
  }


  List<Geometry> getGeometries(VideoDataSetQuery videoDataSetQuery, Map<String, String> params)
  {
    def x = {
      projections { property("groundGeom") }
      if ( videoDataSetQuery?.groundGeom )
      {
        addToCriteria(videoDataSetQuery.createIntersection("groundGeom"))
      }
      if ( videoDataSetQuery?.startDate || videoDataSetQuery?.endDate )
      {
        addToCriteria(videoDataSetQuery.createDateRange("startDate", "endDate"))
      }
      if ( params?.max )
      {
        maxResults(params.max as Integer)
      }
      if ( params?.offset )
      {
        firstResult(params.offset as Integer)
      }
      videoDataSetQuery.searchTagNames?.size()?.times {i ->
        if ( videoDataSetQuery.searchTagNames[i] && videoDataSetQuery.searchTagValues[i] )
        {
          ilike(videoDataSetQuery.searchTagNames[i], "%${videoDataSetQuery.searchTagValues[i]}%")
        }
      }
    }

    def geometries = RasterEntryMetadata.createCriteria().list(x)

    return geometries
  }

  int getCount(VideoDataSetQuery videoDataSetQuery)
  {
    def totalCount = VideoDataSetMetadata.createCriteria().get {
      projections { rowCount() }
      if ( videoDataSetQuery?.groundGeom )
      {
        addToCriteria(videoDataSetQuery.createIntersection("groundGeom"))
      }
      if ( videoDataSetQuery?.startDate || videoDataSetQuery?.endDate )
      {
        addToCriteria(videoDataSetQuery.createDateRange("startDate", "endDate"))
      }
      videoDataSetQuery.searchTagNames?.size()?.times {i ->
        if ( videoDataSetQuery.searchTagNames[i] && videoDataSetQuery.searchTagValues[i] )
        {
          ilike(videoDataSetQuery.searchTagNames[i], "%${videoDataSetQuery.searchTagValues[i]}%")
        }
      }
    }

    return totalCount
  }
}
