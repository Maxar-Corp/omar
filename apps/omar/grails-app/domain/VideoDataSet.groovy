class VideoDataSet
{

  long width
  long height
  Geometry groundGeom
  Date startDate
  Date endDate

  static hasMany = [fileObjects: VideoFile]
  Repository repository

  VideoDataSetMetadata metadata


  static mapping = {
    columns {
      startDate column: 'start_date', index: 'video_data_set_start_date_idx'
      endDate column: 'end_date', index: 'video_data_set_end_date_idx'
    }
  }

  static constraints = {
    width(min: 0L)
    height(min: 0L)
    groundGeom(nullable: false)
    startDate(nullable: true)
    endDate(nullable: true)
    metadata(nullable: true)
  }

  def getMainFile()
  {
    def mainFile = null //videoDataSet?.fileObjects?.find { it.type == 'main' }

    if ( !mainFile )
    {
      //mainFile = RasterFile.findByRasterDataSetAndType(videoDataSet, "main")

      mainFile = VideoFile.createCriteria().get {
        eq("type", "main")
        createAlias("videoDataSet", "d")
        eq("videoDataSet", this)
      }

    }

    return mainFile
  }
}
