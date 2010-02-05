class MetadataTag
{
  String name
  String value

  static belongsTo = [rasterEntry: RasterEntry]

  static constraints = {
    name()
    value(maxSize: 1024)
  }

  static mapping = {
    columns {
      name index: 'metadata_tag_name_idx'
      value index: 'metadata_tag_value_idx'
    }
  }
}
