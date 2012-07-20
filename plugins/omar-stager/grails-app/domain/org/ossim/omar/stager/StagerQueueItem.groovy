package org.ossim.omar.stager

class StagerQueueItem
{
  String status = "new"
  String file
  String baseDir
  String dataInfo

  Date dateCreated
  Date lastUpdated

  static constraints = {
    status()
    file(unique: true)
    baseDir()
    dataInfo( nullable: true )
    dateCreated()
    lastUpdated()
  }

  static mapping = {
    columns {
      status index: 'stager_queue_item_status_idx'
      file index: 'stager_queue_item_file_idx'
      baseDir index: 'stager_queue_item_baseDir_idx'
      dataInfo type: 'text'
      dateCreated index: 'stager_queue_item_date_created_idx'
      lastUpdated index: 'stager_queue_item_last_updated_idx'
    }
  }
}
