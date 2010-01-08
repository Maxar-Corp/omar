class MetadataTag
{
  String name
  String value

  static belongsTo = [rasterEntry: RasterEntry]

  /*
   public String toString()
  {
    return [ name:name, value:value ] as String
  }
  */
  static constraints = {
    name()
    value(maxSize:1024)
  }

  static mapping = {
    cache true
    columns {
      name index: 'metadata_tag_name_idx'
      value index: 'metadata_tag_value_idx'
    }
  }
}
