package org.ossim.omar.raster
class RasterEntrySearchTag {
  String name
  String description

  static constraints = {
    name(unique:true, blank:false)
    description(unique:true, blank:false)
  }

  static mapping = {
    columns {
      name index: "raster_entry_search_tag_name_idx"
    }
  }
}
