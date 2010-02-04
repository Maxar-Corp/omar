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

  static belongsTo = [rasterEntry: RasterEntry]
  
  static mapping = {
    cache true
    columns {
      imageId index: 'raster_entry_metadata_image_id_idx'
      targetId index: 'raster_entry_metadata_target_id_idx'
      productId index: 'raster_entry_metadata_product_id_idx'
      sensorId index: 'raster_entry_metadata_sensor_id_idx'
      missionId index: 'raster_entry_metadata_mission_id_idx'
      imageCategory index: 'raster_entry_metadata_image_category_idx'
      securityClassification index: 'raster_entry_metadata_file_classification_idx'
    }
  }
  static constraints = {
    imageId(blank: false/*, unique: true*/)
    targetId(nullable: true)
    productId(nullable: true)
    sensorId(nullable: true)
    missionId(nullable: true)
    imageCategory(nullable: true)
    azimuthAngle(nullable: true)
    grazingAngle(nullable: true)
    securityClassification(nullable: true)
    title(nullable: true)
    organization(nullable: true)
    description(nullable: true)

  }
}
