class RasterEntry
{
  String entryId
  Long width
  Long height
  Integer numberOfBands

  Integer numberOfResLevels
  String gsdUnit
  Double gsdX
  Double gsdY

  Integer bitDepth
  String dataType
  Geometry groundGeom
  Date acquisitionDate


  RasterEntryMetadata metadata

  static belongsTo = [rasterDataSet: RasterDataSet]

  static hasMany = [
      fileObjects: RasterEntryFile
  ]

  static mapping = {
    columns {
      acquisitionDate index: 'raster_entry_acquisition_date_idx'
    }
  }

  static constraints = {
    entryId()
    width(min: 0l)
    height(min: 0l)
    numberOfBands(min: 0)
    bitDepth(min: 0)
    dataType()

    numberOfResLevels(nullable: true)
    gsdUnit(nullable: true)
    gsdX(nullable: true)
    gsdY(nullable: true)


    groundGeom(nullable: false)
    acquisitionDate(nullable: true)

    metadata(nullable: true)
  }

  def getMetersPerPixel()
  {
    // need to check unit type but for mow assume meters
    return gsdY; // use Y since X may decrease along lat.
  }

  def getMainFile()
  {
    def mainFile = null //rasterDataSet?.fileObjects?.find { it.type == 'main' }

    if ( !mainFile )
    {
      //mainFile = RasterFile.findByRasterDataSetAndType(rasterDataSet, "main")

      mainFile = RasterFile.createCriteria().get {
        eq("type", "main")
        createAlias("rasterDataSet", "d")
        eq("rasterDataSet", this.rasterDataSet)
      }

    }

    return mainFile
  }
}
