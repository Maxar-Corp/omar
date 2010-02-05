class RasterDataSet
{
  static hasMany = [fileObjects: RasterFile, rasterEntries: RasterEntry]

  Repository repository

  static mapping = {
  }
}
