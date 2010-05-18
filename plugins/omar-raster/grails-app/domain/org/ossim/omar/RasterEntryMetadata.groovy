package org.ossim.omar

import org.ossim.omar.RasterEntry

//import org.ossim.postgis.Geometry
import com.vividsolutions.jts.geom.Polygon


class RasterEntryMetadata
{
  String imageId
  String targetId
  String productId
  String sensorId
  String missionId
  String imageCategory
  Double azimuthAngle
  Double grazingAngle
  String securityClassification
  String title
  String organization
  String description
  Double niirs

  //Geometry groundGeom
  Polygon groundGeom
	
  Date acquisitionDate

  // Just for testing...
  String fileType
  String className

  String otherTagsXml

  RasterEntry rasterEntry

  static transients = ["otherTagsMap"]

  Map<String, String> otherTagsMap = [:]


  static mapping = {
    columns {
      imageId index: 'raster_entry_metadata_image_id_idx'
      targetId index: 'raster_entry_metadata_target_id_idx'
      productId index: 'raster_entry_metadata_product_id_idx'
      sensorId index: 'raster_entry_metadata_sensor_id_idx'
      missionId index: 'raster_entry_metadata_mission_id_idx'
      imageCategory index: 'raster_entry_metadata_image_category_idx'
      securityClassification index: 'raster_entry_metadata_security_classification_idx'

      // Just for testing
      fileType index: 'raster_entry_metadata_file_type_idx'
      className index: 'raster_entry_metadata_class_name_idx'

      otherTagsXml type: 'text'//, index: 'raster_entry_metadata_other_tags_idx'

      acquisitionDate index: 'raster_entry_metadata_acquisition_date_idx'
	  
	  groundGeom type: org.hibernatespatial.GeometryUserType
    
    }
  }

  static constraints = {
    imageId(nullable: true, blank: false/*, unique: true*/)
    targetId(nullable: true)
    productId(nullable: true)
    sensorId(nullable: true)
    missionId(nullable: true)
    imageCategory(nullable: true)
    azimuthAngle(nullable: true)
    grazingAngle(nullable: true)
    securityClassification(nullable: true)
    title(nullable: true)
    niirs(nullable: true)
    organization(nullable: true)
    description(nullable: true)

    // Just for testing
    fileType(nullable: true)
    className(nullable: true)

    otherTagsXml(nullable: true, blank: false)

    groundGeom(nullable: false)
    acquisitionDate(nullable: true)


    rasterEntry(nullable: true)
  }

  static initRasterEntryMetadata(def metadataNode, def rasterEntry)
  {
    if ( !rasterEntry.metadata )
    {
      rasterEntry.metadata = new RasterEntryMetadata()
      rasterEntry.metadata.rasterEntry = rasterEntry
    }

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
            case "imageid":
            case "iid2":
              rasterEntry.metadata.imageId = value
              break;
            case "targetid":
            case "tgtid":
              rasterEntry.metadata.targetId = value
              break;
            case "productid":
              rasterEntry.metadata.productId = value
              break;
            case "sensorid":
              rasterEntry.metadata.sensorId = value
              break;
            case "missionid":
            case "isorce":
              rasterEntry.metadata.missionId = value
              break;
            case "imagecategory":
            case "icat":
              rasterEntry.metadata.imageCategory = value
              break;
            case "azimuthangle":
            case "angletonorth":
              rasterEntry.metadata.azimuthAngle = value as Double
              break;
            case "grazingangle":
              rasterEntry.metadata.grazingAngle = value as Double
              break;
            case "oblang":
              rasterEntry.metadata.grazingAngle = 90 - (value as Double)
              break;

            case "securityclassification":
            case "isclas":
              rasterEntry.metadata.securityClassification = value
              break;
            case "title":
            case "iid2":
            case "ititle":
              rasterEntry.metadata.title = value
              break;
            case "organization":
            case "oname":
              rasterEntry.metadata.organization = value
              break;
            case "description":
              rasterEntry.metadata.description = value
              break;
            case "niirs":
              rasterEntry.metadata.niirs = value as Double
              break;

          // Just for testing
            case "filetype":
            case "file_type":
              rasterEntry.metadata.fileType = value
              break

            case "classname":
            case "class_name":
              rasterEntry.metadata.className = value
              break

            default:
              rasterEntry.metadata.otherTagsMap[name] = value
          }
        }
      }
    }

    //println "RASTERENTRY METADATA = ${rasterEntry.metadata}"

    if ( !rasterEntry.metadata.imageId )
    {
      rasterEntry.metadata.imageId = System.currentTimeMillis() as String
    }
  }

  static initRasterEntryOtherTagsXml(RasterEntryMetadata rasterEntryMetadata)
  {
    if ( rasterEntryMetadata )
    {
      def builder = new groovy.xml.StreamingMarkupBuilder().bind {
        metadata {
          rasterEntryMetadata.otherTagsMap.each {k, v ->
            "${k}"(v)
          }
        }
      }

      rasterEntryMetadata.otherTagsXml = builder.toString()
    }
  }

  static Date initAcquisitionDate(rasterEntryNode)
  {
    def when = rasterEntryNode?.TimeStamp?.when

    return DateUtil.parseDate(when?.toString())
  }
}
