import groovy.util.slurpersupport.GPathResult

/**
 * Created by IntelliJ IDEA.
 * User: sbortman
 * Date: May 16, 2009
 * Time: 7:36:25 PM
 * To change this template use File | Settings | File Templates.
 */

public class OmsInfoParser
{
  def dateUtil = new DateUtil()
  def additionalTags
  //def tagFile = new File("tags.txt")

  public def processRasterDataSets(GPathResult oms, Repository repository = null)
  {
    def rasterDataSets = []

    oms?.dataSets?.RasterDataSet.each {rasterDataSetNode ->

      def rasterDataSet = new RasterDataSet()

      rasterDataSetNode.fileObjects.RasterFile.each {rasterFileNode ->
        RasterFile rasterFile = initRasterFile(rasterFileNode)

        rasterDataSet.addToFileObjects(rasterFile)
      }

      rasterDataSetNode.rasterEntries.RasterEntry.each {rasterEntryNode ->
        RasterEntry rasterEntry = initRasterEntry(rasterEntryNode)

        if ( rasterEntry.groundGeom )
        {
          rasterDataSet.addToRasterEntries(rasterEntry)
        }
      }

      if ( rasterDataSet.rasterEntries )
      {
        rasterDataSet.repository = repository
        rasterDataSets << rasterDataSet
        //repository?.addToRasterDataSets(rasterDataSet)
      }
    }

    return rasterDataSets
  }

  public def processVideoDataSets(GPathResult oms, Repository repository = null)
  {
    def videoDataSets = []

    oms?.dataSets?.VideoDataSet.each {videoDataSetNode ->

      def videoDataSet = new VideoDataSet()

      videoDataSetNode.fileObjects.VideoFile.each {videoFileNode ->
        VideoFile videoFile = initVideoFile(videoFileNode)

        videoDataSet.addToFileObjects(videoFile)
      }

      videoDataSet.width = videoDataSetNode?.width?.toLong()
      videoDataSet.height = videoDataSetNode?.height?.toLong()


      def start = videoDataSetNode?.TimeSpan?.begin?.toString()
      def end = videoDataSetNode?.TimeSpan?.end?.toString()

      videoDataSet.startDate = dateUtil.parseDate(start)
      videoDataSet.endDate = dateUtil.parseDate(end)

      def srs = videoDataSetNode?.groundGeom?.@srs?.toString() - "epsg:"
      def wkt = videoDataSetNode?.groundGeom

      if ( srs && wkt )
      {
        videoDataSet.groundGeom = Geometry.fromString("SRID=${srs};${wkt}")
      }

      videoDataSet.repository = repository
      videoDataSets << videoDataSet
      //repository?.addToVideoDataSets(videoDataSet)
    }

    return videoDataSets
  }

  private VideoFile initVideoFile(videoFileNode)
  {
    def videoFile = new VideoFile()

    videoFile.name = videoFileNode.name
    videoFile.format = videoFileNode.@format
    videoFile.type = videoFileNode.@type
    return videoFile
  }


  private RasterEntry initRasterEntry(rasterEntryNode)
  {
    def rasterEntry = new RasterEntry()

    rasterEntry.entryId = rasterEntryNode.entryId
    rasterEntry.width = rasterEntryNode?.width?.toLong()
    rasterEntry.height = rasterEntryNode?.height?.toLong()
    rasterEntry.numberOfBands = rasterEntryNode?.numberOfBands?.toInteger()
    rasterEntry.numberOfResLevels = rasterEntryNode?.numberOfResLevels?.toInteger()
    rasterEntry.bitDepth = rasterEntryNode?.bitDepth?.toInteger()
    rasterEntry.dataType = rasterEntryNode?.dataType

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

    rasterEntry.groundGeom = initGroundGeom(rasterEntryNode)
    rasterEntry.acquisitionDate = initAcquisitionDate(rasterEntryNode)

    rasterEntryNode.fileObjects?.RasterEntryFile.each {rasterEntryFileNode ->
      RasterEntryFile rasterEntryFile = initRasterEntryFile(rasterEntryFileNode)

      rasterEntry.addToFileObjects(rasterEntryFile)
    }

    def metadataNode = rasterEntryNode.metadata

    initMetadataTags(metadataNode, rasterEntry)

    additionalTags?.each {k, v -> re1.addToMetadataTags(new MetadataTag(name: k, value: v)) }

    initMetadataXml(rasterEntry)

    initRasterEntryMetadata(rasterEntry)

    return rasterEntry
  }

  private def initMetadataTags(metadataNode, rasterEntry)
  {
    metadataNode.children().each {tagNode ->

      if ( tagNode.tag?.size() > 0 )
      {

        def name = tagNode.name().toString().toUpperCase()

        //tagFile.append("${name}\n")

        switch ( name )
        {
//          case "DTED_ACC_RECORD":
//          case "ICHIPB":
//          case "PIAIMC":
//          case "RPC00B":
//          case "STDIDC":
//          case "USE00A":
//            break
          default:
            initMetadataTags(tagNode, rasterEntry)
        }
      }
      else
      {
        MetadataTag metadataTag = initMetadataTag(tagNode)

        if ( metadataTag )
        {
          rasterEntry.addToMetadataTags(metadataTag)
        }
      }
    }
  }

  private def initMetadataXml(rasterEntry)
  {
    def writer = new StringWriter()
    def metadataBuilder = new groovy.xml.MarkupBuilder(writer)

    metadataBuilder.metadata {
      rasterEntry?.metadataTags?.each {metadataTag ->
        def name = metadataTag.name.toLowerCase()
        def value = metadataTag.value

        "${name}"(value)

      }
    }

    def metadataXml = new MetadataXml()

    metadataXml.namevalue = writer.buffer

    //rasterEntry?.addToMetadataXml(metadataXml)
    rasterEntry.metadataXml = metadataXml

    //println rasterEntry.metadataXml.data
  }

  private MetadataTag initMetadataTag(tagNode)
  {
    def metadataTag = null
    def name = tagNode.name()?.toString().trim()
    def value = tagNode?.text().toString().trim()

    if ( name && value )
    {
      def key = name.toUpperCase()

      if ( !key.startsWith("LINE_NUM") &&
          !key.startsWith("LINE_DEN") &&
          !key.startsWith("SAMP_NUM") &&
          !key.startsWith("SAMP_DEN") &&
          !key.startsWith("SECONDARY_BE") &&
          !key.equals("ENABLED") &&
          !key.equals("ENABLE_CACHE")
      )
      {
        metadataTag = new MetadataTag(name: name, value: value)
      }
    }

    return metadataTag
  }

  private def initGroundGeom(rasterEntryNode)
  {
    def wkt = rasterEntryNode?.groundGeom?.toString().trim()
    def srs = rasterEntryNode?.groundGeom?.@srs?.toString().trim()
    def groundGeom = null

    if ( wkt && srs )
    {
      try
      {
        srs -= "epsg:"

        def geomString = "SRID=${srs};${wkt}"

        groundGeom = Geometry.fromString(geomString)
      }
      catch (Exception e)
      {
        System.err.println("Cannt create geom for: srs=${srs} wkt=${wkt}")
      }

    }

    return groundGeom
  }

  private def initAcquisitionDate(rasterEntryNode)
  {
    def when = rasterEntryNode?.TimeStamp?.when

    return dateUtil.parseDate(when?.toString())
  }

  private RasterEntryFile initRasterEntryFile(rasterEntryFileNode)
  {
    def rasterEntryFile = new RasterEntryFile()

    rasterEntryFile.name = rasterEntryFileNode?.name
    rasterEntryFile.type = rasterEntryFileNode?.@type
    return rasterEntryFile
  }

  private RasterFile initRasterFile(rasterFileNode)
  {
    def rasterFile = new RasterFile()

    rasterFile.name = rasterFileNode.name
    rasterFile.format = rasterFileNode.@format
    rasterFile.type = rasterFileNode.@type
    return rasterFile
  }


  private initRasterEntryMetadata(rasterEntry)
  {
    rasterEntry.metadata = new RasterEntryMetadata()


    rasterEntry?.metadataTags?.each {metadataTag ->
      switch ( metadataTag.name.toLowerCase() )
      {
        case "imageid":
          rasterEntry.metadata.imageId = metadataTag.value
          break;
        case "targetid":
          rasterEntry.metadata.targetId = metadataTag.value
          break;
        case "productid":
          rasterEntry.metadata.productId = metadataTag.value
          break;
        case "sensorid":
          rasterEntry.metadata.sensorId = metadataTag.value
          break;
        case "missionid":
          rasterEntry.metadata.missionId = metadataTag.value
          break;
        case "imagecategory":
          rasterEntry.metadata.imageCategory = metadataTag.value
          break;
        case "azimuthangle":
          rasterEntry.metadata.azimuthAngle = metadataTag.value as Double
          break;
        case "grazingangle":
          rasterEntry.metadata.grazingAngle = metadataTag.value as Double
          break;
        case "securityclassification":
          rasterEntry.metadata.securityClassification = metadataTag.value
          break;
        case "title":
          rasterEntry.metadata.title = metadataTag.value
          break;
        case "organization":
          rasterEntry.metadata.organization = metadataTag.value
          break;
        case "description":
          rasterEntry.metadata.description = metadataTag.value
          break;
        case "niirs":
          rasterEntry.metadata.niirs = metadataTag.value
          break;

      // Just for testing
        case "filetype":
          rasterEntry.metadata.fileType = metadataTag.value
          break

        case "classname":
          rasterEntry.metadata.className = metadataTag.value
          break
      }
    }

    //println "RASTERENTRY METADATA = ${rasterEntry.metadata}"
    
    if ( !rasterEntry.metadata.imageId )
    {
      rasterEntry.metadata.imageId = System.currentTimeMillis() as String
    }
  }

}
