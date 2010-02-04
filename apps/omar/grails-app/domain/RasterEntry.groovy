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

  MetadataXml metadataXml  // Now a one-to-one

  RasterEntryMetadata metadata

  static belongsTo = [rasterDataSet: RasterDataSet]

  static hasMany = [
      //metadataXml: MetadataXml,   // Get rid of one-to-many
      metadataTags: MetadataTag,
      fileObjects: RasterEntryFile
  ]

  static mapping = {
    cache true
    groundGeom type: GeometryType, column: 'ground_geom'
    metadataXml unique: true
    columns {
      acquisitionDate index: 'raster_entry_acquisition_date_idx'
    }
  }

//  static fetchMode = [
//      metadataTags: "eager"
//  ]

  /*
  public String toString()
  {
    return [
        entryId:entryId,
        width:width,
        height:height,
        numberOfBands:numberOfBands,
        bitDepth:bitDepth,
        dataType:dataType,
        groundGeom:groundGeom,
        //srs:srs
    ] as String
  }
  */

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

    //metadataXml()

    groundGeom(nullable: false)
    acquisitionDate(nullable: true)
    metadataTags()

    metadata(nullable: true)
  }
  def getMetersPerPixel()
  {
    // need to check unit type but for mow assume meters
    return gsdY; // use Y since X may decrease along lat.
  }

  def getMainFile()
  {
    def mainFile = null

//    RasterFile.withTransaction {

    mainFile = rasterDataSet?.fileObjects?.find { it.type == 'main' }

    if ( !mainFile )
    {
//      println "lazy"

      mainFile = RasterFile.findByRasterDataSetAndType(rasterDataSet, "main")
    }
    else
//      println "eager"
//    }

    return mainFile
  }
}
