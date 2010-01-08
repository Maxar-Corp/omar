class StagerQueueItem
{
  String file
  String dataInfo
  String baseDir
  String status = "new"

  static constraints = {
  }

  static mapping = {
    cache true
    columns {
      dataInfo type: 'text'
    }
  }

}
