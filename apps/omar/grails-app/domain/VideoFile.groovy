class VideoFile
{
  String name
  String type
  String format
  String policy = 'POLICY_01'
  String excludePolicy = 'POLICY_01'

  static belongsTo = [videoDataSet: VideoDataSet]

  static constraints = {
    name(unique: true)
    type()
    policy()
    excludePolicy()
  }

  static mapping = {
    cache true
    columns {
      name index: 'video_file_name_idx'
      type index: 'video_file_type_idx'
      format index: 'video_file_format_idx'            
    }
  }
}
