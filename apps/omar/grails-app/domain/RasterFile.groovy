class RasterFile
{
  String name
  String type
  String format

  static belongsTo = [rasterDataSet: RasterDataSet]

  static constraints = {
    name(unique:true)
    type()
    format()
  }

  static mapping = {
    columns {
      name index: 'raster_file_name_idx'
      type index: 'raster_file_type_idx'
      format index: 'raster_file_format_idx'            
    }
  }
}
