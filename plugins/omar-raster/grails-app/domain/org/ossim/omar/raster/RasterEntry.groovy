package org.ossim.omar.raster

import com.vividsolutions.jts.geom.Geometry
import com.vividsolutions.jts.geom.Polygon
import com.vividsolutions.jts.io.WKTReader
import org.hibernate.spatial.GeometryType
import org.joda.time.DateTime
import org.ossim.omar.core.DateUtil

class RasterEntry
{
  def grailsApplication

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
  Polygon groundGeom
  Date acquisitionDate
  Integer validModel

  DateTime accessDate
  DateTime ingestDate
  DateTime receiveDate

  BigInteger releaseId

  String fileType
  String className

  String otherTagsXml

  static transients = ["otherTagsMap"]

  Map<String, String> otherTagsMap = [:]

  /** **************** END ADDING TAGS FROM MetaData to here  ******************/

  static belongsTo = [rasterDataSet: RasterDataSet]

  static hasMany = [fileObjects: RasterEntryFile]

  Collection fileObjects

  static namedQueries = {
    compositeId { compositeId ->
      or {
        if ( compositeId ==~ /\d+/ )
        {
          eq( 'id', compositeId as Long )
        }
        eq( 'indexId', compositeId )
        eq( 'title', compositeId )
      }
    }
  }

  static mapping = {
    accessDate index: 'raster_entry_access_date_idx'
    acquisitionDate index: 'raster_entry_acquisition_date_idx'
    beNumber index: 'raster_entry_be_number_idx'
    className index: 'raster_entry_class_name_idx'
    countryCode index: 'raster_entry_country_code_idx'
    entryId index: 'raster_entry_entry_id_idx'
    fileType index: 'raster_entry_filetype_idx'
    filename index: 'raster_entry_filename_idx'
    groundGeom type: GeometryType, sqlType: 'geometry(POLYGON, 4326)'
    imageCategory index: 'raster_entry_image_category_idx'
    imageId index: 'raster_entry_image_id_idx'
    imageRepresentation index: 'raster_entry_image_representation_idx'
    indexId index: 'raster_entry_index_id_idx', unique:true
    ingestDate index: 'raster_entry_ingest_date_idx'
    missionId index: 'raster_entry_mission_id_idx'
    niirs index: 'raster_entry_niirs_idx'
    otherTagsXml type: 'text'//, index: 'raster_entry_metadata_other_tags_idx'
    productId index: 'raster_entry_product_id_idx'
    rasterDataSet index: 'raster_entry_raster_data_set_idx'
    receiveDate index: 'raster_entry_receive_date_idx'
    releaseId index: 'raster_entry_release_id_idx'
    securityClassification index: 'raster_entry_security_classification_idx'
    securityCode index: 'raster_entry_security_code_idx'
    sensorId index: 'raster_entry_sensor_id_idx'
    targetId index: 'raster_entry_target_id_idx'
    tiePointSet type: 'text'
    title index: 'raster_entry_title_idx'
    title wacCode: 'raster_entry_wac_code_idx'
    validModel index: 'raster_entry_valid_model_idx'
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

  def getGeometryCenter()
  {
    def result = [:]
    if(groundGeom)
    {
      def point = groundGeom.centroid
      result.x = point.x
      result.y = point.y
    }

    result
  }
  def getGeometryBounds()
  {
    def result = [:]

    if(groundGeom)
    {
      def envelope = groundGeom.envelopeInternal
      result.minx = envelope.minX
      result.miny = envelope.minY
      result.maxx = envelope.maxX
      result.maxy = envelope.maxY
    }

    result
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
      double hours = ( currentMil - currentAccessMil ) / millisPerHour
      if ( hours > everyNHours )
      {
        accessDate = current
      }
    }
  }

  def getFileFromObjects(def type)
  {
    return fileObjects?.find { it.type == type }
  }

  def getMetersPerPixel()
  {
    // need to check unit type but for mow assume meters
    return gsdY; // use Y since X may decrease along lat.
  }

  def getMainFile()
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

  def getHistogramFile()
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
      if ( !obj )
      {
        RasterEntryFile rasterEntryFile = RasterEntryFile.initRasterEntryFile( rasterEntryFileNode )
        if ( rasterEntryFile )
        {
          rasterEntry.addToFileObjects( rasterEntryFile )
        }
      }
    }
    def metadataNode = rasterEntryNode.metadata

    initRasterEntryMetadata( metadataNode, rasterEntry )
    if(rasterEntry.grailsApplication.config.stager.includeOtherTags)
    {
      initRasterEntryOtherTagsXml( rasterEntry )
    }

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

  static Geometry initGroundGeom(def groundGeomNode)
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

  static initRasterEntryMetadata(def metadataNode, def rasterEntry)
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
              if ( value )
              {
                rasterEntry.filename = value as File
              }
              break
            case "imageid":
              if ( value )
              {
                rasterEntry.imageId = value
              }
              break;
            case "iid":
              if ( value && !rasterEntry.imageId )
              {
                rasterEntry.imageId = value
              }
              break
            case "irep":
              if ( value && !rasterEntry.imageRepresentation )
              {
                rasterEntry.imageRepresentation = value
              }
              break
            case "imagerepresentation":
              if ( value  )
              {
                rasterEntry.imageRepresentation = value
              }
              break
            case "tgtid":
              if ( value && !rasterEntry.targetId )
              {
                rasterEntry.targetId = value
              }
              break;
            case "targetid":
              if ( value )
              {
                rasterEntry.targetId = value
              }
              break;
            case "productid":
              if ( value )
              {
                rasterEntry.productId = value
              }
              break;
            case "be":
              if ( value &&!rasterEntry.beNumber)
              {
                rasterEntry.beNumber = value;
              }
              break
            case "benumber":
              if ( value )
              {
                rasterEntry.beNumber = value;
              }
              break;
            case "sensorid":
            case "sensor_id":
              if ( value )
              {
                rasterEntry.sensorId = value
              }
              break;
            case "sensor_type":
              if ( value && !rasterEntry.sensorId )
              {
                rasterEntry.sensorId = value
              }
              break
            case "country":
              if ( value && !rasterEntry.countryCode )
              {
                rasterEntry.countryCode = value
              }
              break
            case "countrycode":
              if ( value )
              {
                rasterEntry.countryCode = value
              }
              break
            case "fsctlh":
              if(value &&!rasterEntry.securityCode)
              {
                rasterEntry.securityCode = value
              }
              break
            case "security_code":
              if ( value && !rasterEntry.securityCode )
              {
                rasterEntry.securityCode = value
              }
              break;
            case "securityCode":
              if ( value )
              {
                rasterEntry.securityCode = value
              }
              break;
            case "mission":
            case "missionid":
              if ( value && !rasterEntry.missionId )
              {
                rasterEntry.missionId = value
              }
              break;
            case "isorce":
              if ( value && !rasterEntry.isorce )
              {
                rasterEntry.isorce = value
              }
              break;
            case "imagecategory":
              if ( value  )
              {
                rasterEntry.imageCategory = value
              }
              break
            case "icat":
              if ( value && !rasterEntry.imageCategory )
              {
                rasterEntry.imageCategory = value
              }
              break
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
              if ( value && (value != "nan") )
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

            case "classification":
              if ( value &&!rasterEntry.securityClassification )
              {
                rasterEntry.securityClassification = value
              }

              break
            case "securityclassification":
              if ( value )
              {
                rasterEntry.securityClassification = value
              }
              break
            case "isclas":
              if ( value && !rasterEntry.securityClassification )
              {
                switch(value.toUpperUpperCase())
                {
                  case "U":
                    rasterEntry.securityClassification = "UNCLASSIFIED"
                    break
                  case "R":
                    rasterEntry.securityClassification = "RESTRICTED"
                    break
                  case "S":
                    rasterEntry.securityClassification = "SECRET"
                    break
                  case "T":
                  case "TS":
                    rasterEntry.securityClassification = "TOP SECRET"
                    break
                  default:
                    rasterEntry.securityClassification = value
                    break
                }
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
              if ( value && (rasterEntry.validModel==null ))
              {
                rasterEntry.validModel = value as Integer
              }
              break;
            case "acquisition_date":
            case "acquisitiondate":
              if(value && !rasterEntry.acquisitionDate)
              {
                rasterEntry.acquisitionDate = DateUtil.parseDate(value)
              }
              break;
            case "sunazimuth":
              if(value )
              {
                try{
                  rasterEntry.sunAzimuth = value.toDouble()
                }
                catch(e)
                {

                }
              }
              break;
            case "sunelevation":
              if(value )
              {
                try{
                  rasterEntry.sunElevation = value.toDouble()
                }
                catch(e)
                {

                }
              }
              break;
            default:
              if(rasterEntry.grailsApplication.config.stager.includeOtherTags)
              {
                rasterEntry.otherTagsMap[name] = value
              }
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
          for ( def entry in rasterEntry.otherTagsMap )
          {
            "${entry.key}"( entry.value )
          }
        }
      }

      rasterEntry.otherTagsXml = builder.toString()
    }
  }

  static Date initAcquisitionDate(rasterEntryNode)
  {
    def when = rasterEntryNode?.TimeStamp?.when

    return DateUtil.parseDate( when?.text() )
  }

  static update(def file, def entryId)
  {
    def rasterFile = RasterFile.findWhere( name: file )
  }
}
