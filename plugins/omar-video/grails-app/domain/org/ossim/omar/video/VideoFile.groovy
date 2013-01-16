package org.ossim.omar.video

class VideoFile
{
  String name
  String type
  String format

  static belongsTo = [videoDataSet: VideoDataSet]

  static constraints = {
    name( unique: true )
    type()
    format()
  }

  static mapping = {
      name index: 'video_file_name_idx'
      type index: 'video_file_type_idx'
      format index: 'video_file_format_idx'
      videoDataSet index: 'video_file_video_data_set_idx'
  }


  static VideoFile initVideoFile(def videoFileNode)
  {
    def videoFile = new VideoFile()

    videoFile.name = new File( videoFileNode?.name?.text() ).absolutePath
    videoFile.format = videoFileNode?.@format?.text()
    videoFile.type = videoFileNode?.@type?.text()
    return videoFile
  }
}
