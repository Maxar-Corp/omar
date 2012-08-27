package org.ossim.omar.raster

//import org.ossim.postgis.Geometry

import com.vividsolutions.jts.geom.Geometry
import com.vividsolutions.jts.geom.Polygon
import com.vividsolutions.jts.io.WKTReader
import org.joda.time.DateTime
import org.ossim.omar.core.DateUtil

class RasterEntry
{
  String entryId
  String excludePolicy
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
  String securityCode
  String title
  String isorce
  String organization
  String description
  String countryCode
  String beNumber
  Double niirs
  String wacCode
  Double sunElevation
  Double sunAzimuth
  Double cloudCover
  BigInteger styleId
  Boolean keepForever
  //Geometry groundGeom
  Polygon groundGeom
  //DateTime acquisitionDate
  Date acquisitionDate
  Integer validModel

  DateTime accessDate
  DateTime ingestDate
  DateTime receiveDate

  BigInteger releaseId
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
  Collection fileObjects

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
      securityCode index: 'raster_entry_security_code_idx'
      countryCode index: 'raster_entry_country_code_idx'
      beNumber index: 'raster_entry_be_number_idx'
      validModel index: 'raster_entry_valid_model_idx'

      // Just for testing
      fileType index: 'raster_entry_filetype_idx'
      className index: 'raster_entry_class_name_idx'

      otherTagsXml type: 'text'//, index: 'raster_entry_metadata_other_tags_idx'

      acquisitionDate index: 'raster_entry_acquisition_date_idx'
      accessDate index: 'raster_entry_access_date_idx'
      ingestDate index: 'raster_entry_ingest_date_idx'
      receiveDate index: 'raster_entry_receive_date_idx'
      releaseId index: 'raster_entry_release_id_idx'

      groundGeom type: org.hibernatespatial.GeometryUserType
    }
  }

  static constraints = {
    entryId()
    excludePolicy( nullable: true )
    width( min: 0l )
    height( min: 0l )
    numberOfBands( min: 0 )
    bitDepth( min: 0 )
    dataType()

    numberOfResLevels( nullable: true )
    gsdUnit( nullable: true )
    gsdX( nullable: true )
    gsdY( nullable: true )

    tiePointSet( nullable: true )

    filename( nullable: true )
    indexId( nullable: false, unique: false, blank: false )
    imageId( nullable: true, blank: false/*, unique: true*/ )
    targetId( nullable: true )
    productId( nullable: true )
    sensorId( nullable: true )
    missionId( nullable: true )
    imageCategory( nullable: true )
    imageRepresentation( nullable: true )
    azimuthAngle( nullable: true )
    grazingAngle( nullable: true )
    securityClassification( nullable: true )
    securityCode( nullable: true )
    title( nullable: true )
    niirs( nullable: true )
    isorce( nullable: true )
    wacCode( nullable: true )
    sunElevation( nullable: true )
    sunAzimuth( nullable: true )
    cloudCover( nullable: true )
    organization( nullable: true )
    description( nullable: true )
    countryCode( nullable: true )
    beNumber( nullable: true )
    accessDate( nullable: true )
    ingestDate( nullable: true )
    receiveDate( nullable: true )
    releaseId( nullable: true )
    styleId( nullable: true )
    keepForever( nullable: true )
    validModel( nullable: true )
    // Just for testing
    fileType( nullable: true )
    className( nullable: true )

    otherTagsXml( nullable: true, blank: false )

    groundGeom( nullable: false )
    acquisitionDate( nullable: true )
  }

  def beforeInsert = {
    if ( !ingestDate )
    {
      ingestDate = new DateTime();
      if ( !indexId )
      {
        def mainFile = rasterEntry.rasterDataSet.getFileFromObjects( "main" )
        if ( mainFile )
        {
          def value = "${entryId}-${mainFile}"
          indexId = mainFile.omarIndexId;
        }
      }
    }
  }

  def adjustAccessTimeIfNeeded( def everyNHours = 24 )
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
      double hours = ( currentMil - currentAccessMil ) / millisPerHour
      if ( hours > everyNHours )
      {
        accessDate = current
      }
    }
  }

  def getFileFromObjects( def type )
  {
    return fileObjects?.find { it.type == type }
  }

  def getMetersPerPixel( )
  {
    // need to check unit type but for mow assume meters
    return gsdY; // use Y since X may decrease along lat.
  }

  def getMainFile( )
  {
    def mainFile = null//rasterDataSet?.fileObjects?.find { it.type == 'main' }

    if ( !mainFile )
    {
      //mainFile = org.ossim.omar.raster.RasterFile.findByRasterDataSetAndType(rasterDataSet, "main")

      mainFile = RasterFile.createCriteria().get {
        eq( "type", "main" )
        createAlias( "rasterDataSet", "d" )
        eq( "rasterDataSet", this.rasterDataSet )
      }

    }

    return mainFile
  }
  def getAssociationType(def type)
  {
      def tempFile = RasterEntryFile.createCriteria().get {
          eq( "type", "${type}" )
          createAlias( "rasterEntry", "r" )
          eq( "rasterEntry", this )
      }

      tempFile;
  }
  def getHistogramFile( )
  {
    def result = getFileFromObjects( "histogram" )?.name
    if ( !result )
    {
      result = mainFile?.name
      if ( result )
      {
        def nEntries = rasterDataSet?.rasterEntries?.size() ?: 1
        def ext = result.substring( result.lastIndexOf( "." ) )
        if ( ext )
        {
          if ( nEntries > 1 )
          {
            result = result.replace( ext, "_e${entryId}.his" )
          }
          else
          {
            result = result.replace( ext, ".his" )
          }
        }
        else
        {
          if ( nEntries > 1 )
          {
            result = result + "_e${entryId}.his"
          }
          else
          {
            result = result + ".his"
          }
        }
      }
      //println result
    }

    result
  }

  static RasterEntry initRasterEntry( def rasterEntryNode, RasterEntry rasterEntry = null )
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
      rasterEntry.tiePointSet = "<TiePointSet><Image><coordinates>${rasterEntryNode?.TiePointSet.Image.coordinates.text().replaceAll( "\n", "" )}</coordinates></Image>"
      rasterEntry.tiePointSet += "<Ground><coordinates>${rasterEntryNode?.TiePointSet.Ground.coordinates.text().replaceAll( "\n", "" )}</coordinates></Ground></TiePointSet>"
    }
    def gsdNode = rasterEntryNode?.gsd
    def dx = gsdNode?.@dx?.text()
    def dy = gsdNode?.@dy?.text()
    def gsdUnit = gsdNode?.@unit.text()
    if ( dx && dy && gsdUnit )
    {
      rasterEntry.gsdX = ( dx != "nan" ) ? dx?.toDouble() : null
      rasterEntry.gsdY = ( dy != "nan" ) ? dy?.toDouble() : null
      rasterEntry.gsdUnit = gsdUnit
    }
    rasterEntry.groundGeom = initGroundGeom( rasterEntryNode?.groundGeom )
    rasterEntry.acquisitionDate = initAcquisitionDate( rasterEntryNode )

    if ( rasterEntry.groundGeom && !rasterEntry.tiePointSet )
    {
      def groundGeom = rasterEntry?.groundGeom.geom
      def w = rasterEntry.width as double
      def h = rasterEntry.height as double
      if ( groundGeom.numPoints() >= 4 )
      {
        rasterEntry.tiePointSet = "<TiePointSet><Image><coordinates>0.0,0.0 ${w},0.0 ${w},${h} 0.0,${h}</coordinates></Image><Ground><coordinates>"
        for ( def i in ( 0..<4 ) )
        {
          def point = groundGeom.getPoint( i );
          rasterEntry.tiePointSet += "${point.x},${point.y}"

          if ( i != 3 )
          {
            rasterEntry.tiePointSet += " "
          }
        }
        rasterEntry.tiePointSet += "</coordinates></Ground></TiePointSet>"
      }
    }
    for ( def rasterEntryFileNode in rasterEntryNode.fileObjects?.RasterEntryFile )
    {
        def obj = rasterEntry?.fileObjects?.find { it.name == rasterEntryFileNode?.name?.text() }
        if(!obj)
        {
            RasterEntryFile rasterEntryFile = RasterEntryFile.initRasterEntryFile( rasterEntryFileNode )
            if(rasterEntryFile)
            {
                rasterEntry.addToFileObjects( rasterEntryFile )
            }
        }
    }
    def metadataNode = rasterEntryNode.metadata

    initRasterEntryMetadata( metadataNode, rasterEntry )
    initRasterEntryOtherTagsXml( rasterEntry )

    def mainFile = rasterEntry.rasterDataSet.getFileFromObjects( "main" )
    def filename = mainFile?.name
    if ( !rasterEntry.filename && filename )
    {
      rasterEntry.filename = filename
    }
    if ( !rasterEntry.indexId )
    {
      rasterEntry.indexId = "${rasterEntry.entryId}-${filename}".encodeAsSHA256()
    }
    if ( rasterEntry.validModel == null )
    {
      rasterEntry.validModel = 1
    }
    return rasterEntry
  }

  static Geometry initGroundGeom( def groundGeomNode )
  {
    def wkt = groundGeomNode?.text().trim()
    def srs = groundGeomNode?.@srs?.text().trim()
    def groundGeom = null

    if ( wkt && srs )
    {
      try
      {
        srs -= "epsg:"

        //def geomString = "SRID=${srs};${wkt}"

        //groundGeom = Geometry.fromString(geomString)
        groundGeom = new WKTReader().read( wkt )
        groundGeom.setSRID( Integer.parseInt( srs ) )
      }
      catch ( Exception e )
      {
        System.err.println( "Cannt create geom for: srs=${srs} wkt=${wkt}" )
      }

    }

    return groundGeom
  }

  static initRasterEntryMetadata( def metadataNode, def rasterEntry )
  {
//    if ( !rasterEntry.metadata )
//    {
//      rasterEntry.metadata = new RasterEntryMetadata()
//      rasterEntry.metadata.rasterEntry = rasterEntry
//    }

    for ( def tagNode in metadataNode.children() )
    {

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
          initRasterEntryMetadata( tagNode, rasterEntry )
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
            if ( value && !rasterEntry.filename )
            {
              rasterEntry.filename = value as File
            }
            break;
          case "imageid":
          case "iid":
            if ( value && !rasterEntry.imageId )
            {
              rasterEntry.imageId = value
            }
            break;
          case "irep":
            if ( value && !rasterEntry.imageRepresentation )
            {
              rasterEntry.imageRepresentation = value
            }
            break;
          case "targetid":
          case "tgtid":
            if ( value && !rasterEntry.targetId )
            {
              rasterEntry.targetId = value
            }
            break;
          case "productid":
            if ( value && !rasterEntry.productId )
            {
              rasterEntry.productId = value
            }
            break;
          case "benumber":
            if ( value )
            {
              rasterEntry.beNumber = value;
            }
            break;
           case "sensorid":
           case "sensor_id":
           case "sensor_type":
            if ( value && !rasterEntry.sensorId )
            {
              rasterEntry.sensorId = value
            }
            break;
          case "country":
          case "countryCode":
            if ( value && !rasterEntry.countryCode )
            {
              rasterEntry.countryCode = value
            }
            break;
          case "mission":
          case "missionid":
          case "isorce":
            if ( value && !rasterEntry.missionId )
            {
              rasterEntry.missionId = value
            }
            break;
          case "imagecategory":
          case "icat":
            if ( value && !rasterEntry.imageCategory )
            {
              rasterEntry.imageCategory = value
            }
            break;
          case "azimuthangle":
            if ( value && value != "nan" )
            {
              rasterEntry.azimuthAngle = value as Double
            }
            break
          case "angletonorth":
            if ( value && value != "nan" && !rasterEntry.azimuthAngle )
            {
              rasterEntry.azimuthAngle = ( ( value as Double ) + 90.0 ) % 360.0;
            }
            break;
          case "grazingangle":
            if ( value && value != "nan" && !rasterEntry.grazingAngle )
            {
              rasterEntry.grazingAngle = value as Double
            }
            break;
          case "oblang":
            if ( value && value != "nan" && !rasterEntry.grazingAngle )
            {
              rasterEntry.grazingAngle = 90 - ( value as Double )
            }
            break;

          case "securityclassification":
          case "isclas":
            if ( value && !rasterEntry.securityClassification )
            {
              rasterEntry.securityClassification = value
            }
            break;
          case "title":
          case "ititle":
          case "iid2":
            if ( value && !rasterEntry.title )
            {
              rasterEntry.title = value
            }
            break;
          case "organization":
          case "oname":
            if ( value && !rasterEntry.organization )
            {
              rasterEntry.organization = value
            }
            break;
          case "description":
            if ( value && !rasterEntry.description )
            {
              rasterEntry.description = value
            }
            break;
          case "wac":
            if ( value && !rasterEntry.wacCode )
            {
              rasterEntry.wacCode = value
            }
            break;
          case "niirs":
            if ( value && value != "nan" && !rasterEntry.niirs )
            {
              rasterEntry.niirs = value as Double
            }
            break;

          // Just for testing
          case "filetype":
          case "file_type":
            if ( value && !rasterEntry.fileType )
            {
              rasterEntry.fileType = value
            }
            break

          case "classname":
          case "class_name":
            if ( value && !rasterEntry.className )
            {
              rasterEntry.className = value
            }
            break
          case "validmodel":
            if ( value && !rasterEntry.className )
            {
              rasterEntry.validModel = value as Integer
            }
            break;
          default:
            rasterEntry.otherTagsMap[name] = value
          }
        }
      }
    }

    //println "RASTERENTRY METADATA = ${rasterEntry.metadata}"

  }

  static initRasterEntryOtherTagsXml( RasterEntry rasterEntry )
  {
    if ( rasterEntry )
    {
      def builder = new groovy.xml.StreamingMarkupBuilder().bind {
        metadata {
          for ( def entry in rasterEntry.otherTagsMap )
          {
            "${entry.key}"( entry.value )
          }
        }
      }

      rasterEntry.otherTagsXml = builder.toString()
    }
  }

  static Date initAcquisitionDate( rasterEntryNode )
  {
    def when = rasterEntryNode?.TimeStamp?.when

    return DateUtil.parseDate( when?.text() )
  }

  static update(def file, def entryId)
  {
    def rasterFile = RasterFile.findWhere(name:file)
  }
}
