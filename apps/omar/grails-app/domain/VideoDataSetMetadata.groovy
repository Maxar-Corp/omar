class VideoDataSetMetadata
{
  Geometry groundGeom
  Date startDate
  Date endDate
  String otherTagsXml

  static transients = ["otherTagsMap"]
  
  Map<String, String> otherTagsMap = [:]

  VideoDataSet videoDataSet

  static mapping = {
    columns {
      otherTagsXml type: 'text'//, index: 'video_data_set_metadata_other_tags_idx'
      startDate column: 'start_date', index: 'video_data_set_metadata_start_date_idx,video_data_set_time_idx'
      endDate column: 'end_date', index: 'video_data_set_metadata_end_date_idx,video_data_set_time_idx'
    }
  }

  static constraints = {
    otherTagsXml(nullable: true, blank: false)
    videoDataSet(nullable: true)
    startDate(nullable: true)
    endDate(nullable: true)
    groundGeom(nullable: true)
  }
}
