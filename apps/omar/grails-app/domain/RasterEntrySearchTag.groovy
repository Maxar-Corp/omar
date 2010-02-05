class RasterEntrySearchTag {
  String name
  String description

  static constraints = {
    name(unique:true)
    description(unique:true)
  }

  static mapping = {
    columns {
      name index: "raster_entry_search_tag_name_idx"
    }
  }
}
