package org.ossim.omar
class StagerQueueItem
{
  String file
  String dataInfo
  String baseDir
  String status = "new"

  static constraints = {
  }

  static mapping = {
    columns {
      dataInfo type: 'text'
    }
  }

}
