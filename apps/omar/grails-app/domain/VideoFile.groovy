class VideoFile
{
  String name
  String type
  String format

  static belongsTo = [videoDataSet: VideoDataSet]

  static constraints = {
    name(unique: true)
    type()
    format()
  }

  static mapping = {
    columns {
      name index: 'video_file_name_idx'
      type index: 'video_file_type_idx'
      format index: 'video_file_format_idx'
    }
  }
}
