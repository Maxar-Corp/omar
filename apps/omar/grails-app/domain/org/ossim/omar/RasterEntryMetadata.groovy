package org.ossim.omar

import org.ossim.omar.RasterEntry

import org.ossim.postgis.Geometry

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

  Geometry groundGeom
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
}
