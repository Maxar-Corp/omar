package org.ossim.omar.video
class VideoDataSetSearchTag
{
  String name
  String description

  static constraints = {
    name(unique: true)
    description(unique: true)
  }

  static mapping = {
    columns {
      name index: "video_data_set_search_tag_name_idx"
    }
  }
}
