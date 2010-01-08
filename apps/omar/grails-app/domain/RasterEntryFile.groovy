class RasterEntryFile
{
  String name
  String type

  static belongsTo = [rasterEntry: RasterEntry]

  /*
  public String toString()
  {
    return [ name:name, type:type ]  as String
  }
  */

  static constraints = {
    name()
    type()
  }

  static mapping = {
    cache true
    columns {
      name index: 'raster_entry_file_name_idx'
      type index: 'raster_entry_file_type_idx'      
    }
  }
}
