class VideoDataSetMetadata
{
  String otherTagsXml

  static transients = ["otherTagsMap"]

  Map<String, String> otherTagsMap = [:]


  static belongsTo = [videoDataSet: VideoDataSet]


  static mapping = {
    columns {
      otherTagsXml type: 'text'//, index: 'video_data_set_metadata_other_tags_idx'
    }
  }

  static constraints = {
    otherTagsXml(nullable: true, blank: false)
  }
}
