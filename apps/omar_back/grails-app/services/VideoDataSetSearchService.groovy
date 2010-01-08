//import javax.jws.WebParam

class VideoDataSetSearchService
{
  //static expose = ['xfire']

  static transactional = true

  List<VideoDataSet> runQuery(/*@WebParam (name = "videoDataSetQuery", header = true)*/ VideoDataSetQuery videoDataSetQuery,
                              /*@WebParam (name = "params", header = true)*/ Map<String, String> params)
  {
    def clause = videoDataSetQuery.createClause()

    def videos = VideoDataSet.createCriteria().list(params) {
      if ( clause )
      {
        addToCriteria(clause)
      }
    }

    // Hack to force eager loading
    videos.each {it?.fileObjects?.size()}

    return videos
  }
}
