class VideoDataSet
{

  long width
  long height
  Geometry groundGeom
  Date startDate
  Date endDate

  static hasMany = [fileObjects: VideoFile]
  //static belongsTo = [repository: Repository]
  Repository repository

  static mapping = {
    cache true
    groundGeom type: GeometryType, column: 'ground_geom'
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
  }

  def getMainFile()
  {
    def mainFile

//    VideoFile.withTransaction {

    mainFile = fileObjects?.find { it.type == 'main' }

    if ( !mainFile )
    {
//      println "lazy"

      mainFile = VideoFile.findByVideoDataSetAndType(this, "main")
    }
//    else
//      println "eager"
//    }

    return mainFile
  }
}
