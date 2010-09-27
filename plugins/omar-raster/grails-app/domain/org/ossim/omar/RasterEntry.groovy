package org.ossim.omar



//import org.ossim.postgis.Geometry

import java.util.regex.Pattern
import java.lang.String
import com.vividsolutions.jts.geom.Geometry
import com.vividsolutions.jts.geom.Polygon
import com.vividsolutions.jts.io.WKTReader
import org.joda.time.DateTime

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
  String indexId

  /** **************** BEGIN ADDING TAGS FROM MetaData to here  ******************/
  String filename
  String imageId
  String targetId
  String productId
  String sensorId
  String missionId
  String imageCategory
  String imageRepresentation
  Double azimuthAngle
  Double grazingAngle
  String securityClassification
  String title
  String isorce
  String organization
  String description
  String countryCode
  String beNumber
  Double niirs
  Integer wacCode
  double sunElevation
  double sunAzimuth
  double cloudCover
  BigInteger styleId
  boolean keepForever
  //Geometry groundGeom
  Polygon groundGeom
  //DateTime acquisitionDate
  Date acquisitionDate

  DateTime accessDate
  DateTime ingestDate

  // Just for testing...
  String fileType
  String className

  String otherTagsXml

  static transients = ["otherTagsMap"]

  Map<String, String> otherTagsMap = [:]

  /** **************** END ADDING TAGS FROM MetaData to here  ******************/

//  static hasOne = [metadata: RasterEntryMetadata]

  static belongsTo = [rasterDataSet: RasterDataSet]

  static hasMany = [fileObjects: RasterEntryFile]

  static mapping = {
    columns {
      tiePointSet type: 'text'

      indexId index: 'raster_entry_index_id_idx'
      filename index: 'raster_entry_filename_idx'
      imageId index: 'raster_entry_image_id_idx'
      targetId index: 'raster_entry_target_id_idx'
      productId index: 'raster_entry_product_id_idx'
      sensorId index: 'raster_entry_sensor_id_idx'
      missionId index: 'raster_entry_mission_id_idx'
      imageCategory index: 'raster_entry_image_category_idx'
      imageRepresentation index: 'raster_entry_image_representation_idx'
      securityClassification index: 'raster_entry_security_classification_idx'
      countryCode index: 'raster_entry_country_code_idx'
      beNumber index: 'raster_entry_be_number_idx'

      // Just for testing
      fileType index: 'raster_entry_filetype_idx'
      className index: 'raster_entry_class_name_idx'

      otherTagsXml type: 'text'//, index: 'raster_entry_metadata_other_tags_idx'

      acquisitionDate index: 'raster_entry_acquisition_date_idx'
      accessDate index: 'raster_entry_access_date_idx'
      ingestDate index: 'raster_entry_ingest_date_idx' 

      groundGeom type: org.hibernatespatial.GeometryUserType

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

    filename(nullable:true)
    indexId(nullable:false, unique:false, blank: false)
    imageId(nullable: true, blank: false/*, unique: true*/)
    targetId(nullable: true)
    productId(nullable: true)
    sensorId(nullable: true)
    missionId(nullable: true)
    imageCategory(nullable: true)
    imageRepresentation(nullable: true)
    azimuthAngle(nullable: true)
    grazingAngle(nullable: true)
    securityClassification(nullable: true)
    title(nullable: true)
    niirs(nullable: true)
    isorce(nullable: true)
    wacCode(nullable: true)
    sunElevation(nullable: true)
    sunAzimuth(nullable: true)
    cloudCover(nullable: true)
    organization(nullable: true)
    description(nullable: true)
    countryCode(nullable: true)
    beNumber(nullable: true)
    accessDate(nullable: true)
    ingestDate(nullable: true)
    styleId(nullable: true)
    keepForever(nullable: true)
    
    // Just for testing
    fileType(nullable: true)
    className(nullable: true)

    otherTagsXml(nullable: true, blank: false)

    groundGeom(nullable: false)
    acquisitionDate(nullable: true)
  }

  def beforeInsert = {
    if ( !ingestDate )
    {
      ingestDate = new DateTime();
      if(!indexId)
      {
        def mainFile = rasterEntry.rasterDataSet.getFileFromObjects("main")
        if(mainFile)
        {
          def value = "${entryId}-${mainFile}"
          indexId = mainFile.omarIndexId;
        }
      }
    }
  }

  def adjustAccessTimeIfNeeded(def everyNHours = 24)
  {
    if ( !accessDate )
    {
      accessDate = new DateTime();
    }
    else
    {
      DateTime current = new DateTime();
      long currentAccessMil = accessDate.getMillis()
      long currentMil = current.getMillis()
      double millisPerHour = 3600000 // 60*60*1000  <seconds>*<minutes in an hour>*<milliseconds>
      double hours = (currentMil - currentAccessMil) / millisPerHour
      if ( hours > everyNHours )
      {
        accessDate = current
      }
    }
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

  static RasterEntry initRasterEntry(def rasterEntryNode, RasterEntry rasterEntry = null)
  {
    rasterEntry = rasterEntry ?: new RasterEntry()

    rasterEntry.entryId = rasterEntryNode.entryId
    rasterEntry.width = rasterEntryNode?.width?.toLong()
    rasterEntry.height = rasterEntryNode?.height?.toLong()
    rasterEntry.numberOfBands = rasterEntryNode?.numberOfBands?.toInteger()
    rasterEntry.numberOfResLevels = rasterEntryNode?.numberOfResLevels?.toInteger()
    rasterEntry.bitDepth = rasterEntryNode?.bitDepth?.toInteger()
    rasterEntry.dataType = rasterEntryNode?.dataType
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
    rasterEntry.groundGeom = initGroundGeom(rasterEntryNode?.groundGeom)
    rasterEntry.acquisitionDate = initAcquisitionDate(rasterEntryNode)

    if ( rasterEntry.groundGeom && !rasterEntry.tiePointSet )
    {
      def groundGeom = rasterEntry?.groundGeom.geom
      def w = rasterEntry.width as double
      def h = rasterEntry.height as double
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

    initRasterEntryMetadata(metadataNode, rasterEntry)
    initRasterEntryOtherTagsXml(rasterEntry)

    def mainFile = rasterEntry.rasterDataSet.getFileFromObjects("main")
    def filename = mainFile?.name
    if(!rasterEntry.filename&&filename)
    {
      rasterEntry.filename = (filename as File).getName()
    }
    if(!rasterEntry.indexId)
    {
      rasterEntry.indexId = "${rasterEntry.entryId}-${filename}".encodeAsSHA256()
    }
    return rasterEntry
  }
  static Geometry initGroundGeom(def groundGeomNode)
  {
    def wkt = groundGeomNode?.toString().trim()
    def srs = groundGeomNode?.@srs?.toString().trim()
    def groundGeom = null

    if ( wkt && srs )
    {
      try
      {
        srs -= "epsg:"

        //def geomString = "SRID=${srs};${wkt}"

        //groundGeom = Geometry.fromString(geomString)
        groundGeom = new WKTReader().read(wkt)
        groundGeom.setSRID(Integer.parseInt(srs))
      }
      catch (Exception e)
      {
        System.err.println("Cannt create geom for: srs=${srs} wkt=${wkt}")
      }

    }

    return groundGeom
  }

  static initRasterEntryMetadata(def metadataNode, def rasterEntry)
  {
//    if ( !rasterEntry.metadata )
//    {
//      rasterEntry.metadata = new RasterEntryMetadata()
//      rasterEntry.metadata.rasterEntry = rasterEntry
//    }

    metadataNode.children().each {tagNode ->

      if ( tagNode.children().size() > 0 )
      {
        def name = tagNode.name().toString().toUpperCase()

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
          initRasterEntryMetadata(tagNode, rasterEntry)
        }
      }
      else
      {
        def name = tagNode.name().toString().trim()
        def value = tagNode.text().toString().trim()

// Need to add following check in there
//        if ( !key.startsWith("LINE_NUM") &&
//            !key.startsWith("LINE_DEN") &&
//            !key.startsWith("SAMP_NUM") &&
//            !key.startsWith("SAMP_DEN") &&
//            !key.startsWith("SECONDARY_BE") &&
//            !key.equals("ENABLED") &&
//            !key.equals("ENABLE_CACHE")


        if ( name && value )
        {
          switch ( name.toLowerCase() )
          {
            case "filename":
              if(value&&!rasterEntry.filename)
              {
                rasterEntry.filename = (value as File).getName()
              }
              break;
          case "imageid":
          case "iid":
            if(value&&!rasterEntry.imageId)
            {
              rasterEntry.imageId = value
            }
            break;
          case "irep":
            if(value&&!rasterEntry.imageRepresentation)
            {
              rasterEntry.imageRepresentation = value
            }
            break;
          case "targetid":
          case "tgtid":
            if(value&&!rasterEntry.targetId)
            {
              rasterEntry.targetId = value
            }
            break;
          case "productid":
            if(value&&!rasterEntry.productId)
            {
              rasterEntry.productId = value
            }
            break;
          case "benumber":
            if(value)
            {
              rasterEntry.beNumber = value;
            }
            break;
          case "sensorid":
            if(value&&!rasterEntry.sensorId)
            {
              rasterEntry.sensorId = value
            }
            break;
          case "country":
          case "countryCode":
            if(value&&!rasterEntry.countryCode)
            {
              rasterEntry.countryCode = value
            }
            break;
          case "mission":
          case "missionid":
          case "isorce":
            if(value&&!rasterEntry.missionId)
            {
              rasterEntry.missionId = value
            }
            break;
          case "imagecategory":
          case "icat":
            if(value&&!rasterEntry.imageCategory)
            {
              rasterEntry.imageCategory = value
            }
            break;
          case "azimuthangle":
          case "angletonorth":
            if(value&&!rasterEntry.azimuthAngle)
            {
              rasterEntry.azimuthAngle = value as Double
            }
            break;
          case "grazingangle":
            if(value&&!rasterEntry.grazingAngle)
            {
              rasterEntry.grazingAngle = value as Double
            }
            break;
          case "oblang":
            if(value&&!rasterEntry.grazingAngle)
            {
              rasterEntry.grazingAngle = 90 - (value as Double)
            }
            break;

          case "securityclassification":
          case "isclas":
            if(value&&!rasterEntry.securityClassification)
            {
              rasterEntry.securityClassification = value
            }
            break;
          case "title":
          case "ititle":
          case "iid2":
            if(value&&!rasterEntry.title)
            {
              rasterEntry.title = value
            }
            break;
          case "organization":
          case "oname":
            if(value&&!rasterEntry.organization)
            {
              rasterEntry.organization = value
            }
            break;
            case "description":
              if(value&&!rasterEntry.description)
              {
                rasterEntry.description = value
              }
              break;
            case "wac":
              if(value&&!rasterEntry.wacCode)
              {
                rasterEntry.wac = value as Integer
              }
              break;
          case "niirs":
            if(value&&!rasterEntry.niirs)
            {
              rasterEntry.niirs = value as Double
            }
            break;

          // Just for testing
          case "filetype":
          case "file_type":
            if(value&&!rasterEntry.fileType)
            {
              rasterEntry.fileType = value
            }
            break

          case "classname":
          case "class_name":
            if(value&&!rasterEntry.className)
            {
              rasterEntry.className = value
            }
            break

          default:
            rasterEntry.otherTagsMap[name] = value
          }
        }
      }
    }

    //println "RASTERENTRY METADATA = ${rasterEntry.metadata}"

  }

  static initRasterEntryOtherTagsXml(RasterEntry rasterEntry)
  {
    if ( rasterEntry )
    {
      def builder = new groovy.xml.StreamingMarkupBuilder().bind {
        metadata {
          rasterEntry.otherTagsMap.each {k, v ->
            "${k}"(v)
          }
        }
      }

      rasterEntry.otherTagsXml = builder.toString()
    }
  }
  static Date initAcquisitionDate(rasterEntryNode)
  {
    def when = rasterEntryNode?.TimeStamp?.when

    return DateUtil.parseDate(when?.toString())
  }
}
