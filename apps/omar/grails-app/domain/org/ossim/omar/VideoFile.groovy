package org.ossim.omar

import org.ossim.omar.VideoDataSet

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
      type index: 'video_file_type_idx,video_file_vds_type'
      format index: 'video_file_format_idx'
      videoDataSet index: 'video_file_vds_type'
    }
  }


  static VideoFile initVideoFile(def videoFileNode)
  {
    def videoFile = new VideoFile()

    videoFile.name = videoFileNode.name
    videoFile.format = videoFileNode.@format
    videoFile.type = videoFileNode.@type
    return videoFile
  }
}
