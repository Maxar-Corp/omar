package org.ossim.omar

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
  String tiePointSet

  static hasOne = [metadata: RasterEntryMetadata]

  static belongsTo = [rasterDataSet: RasterDataSet]

  static hasMany = [fileObjects: RasterEntryFile]

  static mapping = {
    columns {
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
      //mainFile = org.ossim.omar.RasterFile.findByRasterDataSetAndType(rasterDataSet, "main")

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
    if ( tiePointSet )
    {
      def tiepoints = new XmlSlurper().parseText(tiePointSet)
      def imageCoordinates = tiepoints.Image.toString().trim()
      def groundCoordinates = tiepoints.Ground.toString().trim()
      def splitImageCoordinates = imageCoordinates.split(" ");
      def splitGroundCoordinates = groundCoordinates.split(" ");
      splitImageCoordinates.each {
        def point = it.split(",")
        if ( point.size() >= 2 )
        {
          dptArray.add(new ossimDpt(Double.parseDouble(point.getAt(0)),
              Double.parseDouble(point.getAt(1))))
        }
      }
      splitGroundCoordinates.each {
        def point = it.split(",")
        if ( point.size() >= 2 )
        {
          gptArray.add(new ossimGpt(Double.parseDouble(point.getAt(1)),
              Double.parseDouble(point.getAt(0))))
        }
      }
    }
    else // lets do a fall back if the tiepoint set is not set.
    {
      def groundGeom = groundGeom.geom
      if ( groundGeom.numPoints() >= 4 )
      {
        def w = width as double
        def h = height as double
        (0..<4).each {
          def point = groundGeom.getPoint(it);
          gptArray.add(new ossimGpt(point.y, point.x));
        }
        dptArray.add(new ossimDpt(0.0, 0.0))
        dptArray.add(new ossimDpt(w, 0.0))
        dptArray.add(new ossimDpt(w, h))
        dptArray.add(new ossimDpt(0.0, h))
      }
    }

    return Util.createBilinearModel(dptArray, gptArray)
  }

  static RasterEntry initRasterEntry(def rasterEntryNode)
  {
    def rasterEntry = new RasterEntry()

    rasterEntry.entryId = rasterEntryNode.entryId
    rasterEntry.width = rasterEntryNode?.width?.toLong()
    rasterEntry.height = rasterEntryNode?.height?.toLong()
    rasterEntry.numberOfBands = rasterEntryNode?.numberOfBands?.toInteger()
    rasterEntry.numberOfResLevels = rasterEntryNode?.numberOfResLevels?.toInteger()
    rasterEntry.bitDepth = rasterEntryNode?.bitDepth?.toInteger()
    rasterEntry.dataType = rasterEntryNode?.dataType
    rasterEntry.tiePointSet = ""
    if ( rasterEntryNode?.TiePointSet )
    {
      rasterEntry.tiePointSet = "<TiePointSet><Image><coordinates>${rasterEntryNode?.TiePointSet.Image.coordinates.toString().replaceAll("\n", "")}</coordinates></Image>"
      rasterEntry.tiePointSet += "<Ground><coordinates>${rasterEntryNode?.TiePointSet.Ground.coordinates.toString().replaceAll("\n", "")}</coordinates></Ground></TiePointSet>"
    }
    def gsdNode = rasterEntryNode?.gsd
    def dx = gsdNode?.@dx?.toString()
    def dy = gsdNode?.@dy?.toString()
    def gsdUnit = gsdNode?.@unit.toString()
    if ( dx && dy && gsdUnit )
    {
      rasterEntry.gsdX = (dx != "nan") ? dx?.toDouble() : null
      rasterEntry.gsdY = (dy != "nan") ? dy?.toDouble() : null
      rasterEntry.gsdUnit = gsdUnit
    }

    rasterEntry.metadata = new RasterEntryMetadata()
    rasterEntry.metadata.rasterEntry = rasterEntry
    rasterEntry.metadata.groundGeom = OmsInfoParser.initGroundGeom(rasterEntryNode)
    rasterEntry.metadata.acquisitionDate = RasterEntryMetadata.initAcquisitionDate(rasterEntryNode)


    if ( rasterEntry?.metadata?.groundGeom && !rasterEntry.tiePointSet )
    {
      def groundGeom = rasterEntry?.metadata?.groundGeom.geom
      def w = rasterEntry?.width as double
      def h = rasterEntry?.height as double
      if ( groundGeom.numPoints() >= 4 )
      {
        rasterEntry.tiePointSet = "<TiePointSet><Image><coordinates>0.0,0.0 ${w},0.0 ${w},${h} 0.0,${h}</coordinates></Image><Ground><coordinates>"
        (0..<4).each {
          def point = groundGeom.getPoint(it);
          rasterEntry.tiePointSet += "${point.x},${point.y}"
          if ( it != 3 )
          {
            rasterEntry.tiePointSet += " "
          }
        }
        rasterEntry.tiePointSet += "</coordinates></Ground></TiePointSet>"
      }
    }

    rasterEntryNode.fileObjects?.RasterEntryFile.each {rasterEntryFileNode ->
      RasterEntryFile rasterEntryFile = RasterEntryFile.initRasterEntryFile(rasterEntryFileNode)

      rasterEntry.addToFileObjects(rasterEntryFile)
    }

    def metadataNode = rasterEntryNode.metadata

    RasterEntryMetadata.initRasterEntryMetadata(metadataNode, rasterEntry)
    RasterEntryMetadata.initRasterEntryOtherTagsXml(rasterEntry.metadata)

    return rasterEntry
  }

}
