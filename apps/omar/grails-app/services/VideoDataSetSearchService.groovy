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
      createAlias("videoDataSet", "v")
      if ( videoDataSetQuery?.groundGeom )
      {
        addToCriteria(videoDataSetQuery.createIntersection("v.groundGeom"))
      }
      if ( videoDataSetQuery?.startDate || videoDataSetQuery?.endDate )
      {
        addToCriteria(videoDataSetQuery.createDateRange("v.startDate", "v.endDate"))
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
          case "startDate":
          case "endDate":
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

  int getCount(VideoDataSetQuery videoDataSetQuery)
  {
    def totalCount = VideoDataSetMetadata.createCriteria().get {
      projections { rowCount() }
      createAlias("videoDataSet", "v")
      if ( videoDataSetQuery?.groundGeom )
      {
        addToCriteria(videoDataSetQuery.createIntersection("v.groundGeom"))
      }
      if ( videoDataSetQuery?.startDate || videoDataSetQuery?.endDate )
      {
        addToCriteria(videoDataSetQuery.createDateRange("v.startDate", "v.endDate"))
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
