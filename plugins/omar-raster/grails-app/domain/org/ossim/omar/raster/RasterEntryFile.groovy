package org.ossim.omar.raster

class RasterEntryFile
{
  String name
  String type

  static belongsTo = [rasterEntry: RasterEntry]

  static constraints = {
    name()
    type()
  }

  static mapping = {
    columns {
      name index: 'raster_entry_file_name_idx'
      type index: 'raster_entry_file_type_idx'
    }
  }

  static RasterEntryFile initRasterEntryFile( def rasterEntryFileNode )
  {
    def rasterEntryFile = new RasterEntryFile()

    rasterEntryFile.name = new File( rasterEntryFileNode?.name?.text() ).absolutePath
    rasterEntryFile.type = rasterEntryFileNode?.@type?.text()
    return rasterEntryFile
  }

}
