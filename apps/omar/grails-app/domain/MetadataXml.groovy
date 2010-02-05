class MetadataXml
{

  // Format is <metadata><foo>bar</foo>...</metadata>
  String namevalue

  static belongsTo = [rasterEntry: RasterEntry]


  static constraints = {
    namevalue()
  }

  static mapping = {
    columns {
      namevalue type: 'text' , index: 'metadata_xml_namevalue_idx'
    }
  }    
}
