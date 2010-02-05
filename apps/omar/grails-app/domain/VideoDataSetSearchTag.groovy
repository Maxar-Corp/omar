class VideoDataSetSearchTag
{
  String name
  String description

  static constraints = {
    name(unique: true)
    description(unique: true)
  }

  static mapping = {
    cache true
    columns {
      name index: "video_data_set_search_tag_name_idx"
    }
  }
}
