class RasterEntrySearchTag {
  String name
  String description

  static constraints = {
    name(unique:true)
    description(unique:true)
  }

  static mapping = {
    cache true
    columns {
      name index: "raster_entry_search_tag_name_idx"
    }
  }
}
