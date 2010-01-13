class Repository
{
  String baseDir
  Date scanStartDate
  Date scanEndDate

  //static hasMany = [rasterDataSets: RasterDataSet, videoDataSets: VideoDataSet]

  /*
   public String toString()
  {
    return [ baseDir:baseDir ] as String 
  }
  */

  static constraints = {
    baseDir(unique: true, blank: false)
    scanStartDate(nullable: true)
    scanEndDate(nullable: true)
    //rasterDataSets()
    //videoDataSets()
  }

  static mapping = {
    cache true
    columns {
      baseDir column: 'base_dir', index: 'base_dir_idx'
    }
  }
}
