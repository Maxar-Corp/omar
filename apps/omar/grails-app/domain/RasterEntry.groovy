import joms.oms.ossimDpt
import joms.oms.ossimGpt
import joms.oms.ossimGptVector
import joms.oms.ossimDptVector
import joms.oms.Util


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
  String tiePointSet

  static hasOne = [metadata: RasterEntryMetadata]

  static belongsTo = [rasterDataSet: RasterDataSet]

  static hasMany = [
      fileObjects: RasterEntryFile
  ]

  static mapping = {
    columns {
      acquisitionDate index: 'raster_entry_acquisition_date_idx'
      tiePointSet type: 'text'
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
    
    tiePointSet(nullable: true)
    
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
  def createModelFromTiePointSet()
  {
    def gptArray = new ossimGptVector();
    def dptArray = new ossimDptVector();
    if(tiePointSet)
    {
      def tiepoints = new XmlSlurper().parseText(tiePointSet)
      def imageCoordinates  = tiepoints.Image.toString().trim()
      def groundCoordinates = tiepoints.Ground.toString().trim()
      def splitImageCoordinates  = imageCoordinates.split(" ");
      def splitGroundCoordinates = groundCoordinates.split(" ");
      splitImageCoordinates.each{
        def point = it.split(",")
        if(point.size() >= 2)
        {
          dptArray.add(new ossimDpt(Double.parseDouble(point.getAt(0)),
                                    Double.parseDouble(point.getAt(1))))
        }
      }
      splitGroundCoordinates.each{
        def point = it.split(",")
        if(point.size() >= 2)
        {
          gptArray.add(new ossimGpt(Double.parseDouble(point.getAt(1)),
                                    Double.parseDouble(point.getAt(0))))
        }
      }
    }
    else // lets do a fall back if the tiepoint set is not set.
    {
      def groundGeom = groundGeom.geom
      if(groundGeom.numPoints() >=4)
      {
        def w = width as double
        def h = height as double
         (0..<4).each{
            def point = groundGeom.getPoint(it);
            gptArray.add(new ossimGpt(point.y, point.x));
         }
         dptArray.add(new ossimDpt(0.0,0.0))
         dptArray.add(new ossimDpt( w,0.0))
         dptArray.add(new ossimDpt(w ,h))
         dptArray.add(new ossimDpt(0.0,h))
      }
    }

    return Util.createBilinearModel(dptArray, gptArray)
  }
}
