class RasterDataSet
{

  static hasMany = [fileObjects: RasterFile, rasterEntries: RasterEntry]
  //static belongsTo = [repository: Repository]
  Repository repository

//  static fetchMode = [
//      fileObjects: "join"
//  ]


  /*
   public String toString()
  {
    return [ fileObjects:fileObjects, rasterEntries:rasterEntries ] as String
  }
  */

  static mapping = {
    cache true
  }  
}
