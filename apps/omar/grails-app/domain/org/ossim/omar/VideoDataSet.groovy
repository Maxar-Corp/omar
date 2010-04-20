package org.ossim.omar

import org.ossim.omar.Repository

class VideoDataSet
{

  long width
  long height

  static hasMany = [fileObjects: VideoFile]
  Repository repository

  static hasOne = [metadata: VideoDataSetMetadata]

  static mapping = {
    columns {
    }
  }

  static constraints = {
    width(min: 0L)
    height(min: 0L)
    metadata(nullable: true)
  }

  def getMainFile()
  {
    def mainFile = null

    if ( !mainFile )
    {
      //mainFile = org.ossim.omar.RasterFile.findByRasterDataSetAndType(videoDataSet, "main")

      mainFile = VideoFile.createCriteria().get {
        eq("type", "main")
        createAlias("videoDataSet", "d")
        eq("videoDataSet", this)
      }

    }

    return mainFile
  }

  static VideoDataSet initVideoDataSet(def videoDataSetNode)
  {
    def videoDataSet = new VideoDataSet()

    videoDataSetNode.fileObjects.VideoFile.each {videoFileNode ->
      VideoFile videoFile = VideoFile.initVideoFile(videoFileNode)

      videoDataSet.addToFileObjects(videoFile)
    }

    videoDataSet.width = videoDataSetNode?.width?.toLong()
    videoDataSet.height = videoDataSetNode?.height?.toLong()

    videoDataSet.metadata = VideoDataSetMetadata.initVideoDataSetMetadata(videoDataSetNode)
    videoDataSet.metadata.videoDataSet = videoDataSet

    VideoDataSetMetadata.initVideoDataSetMetadata(videoDataSetNode.metadata, videoDataSet)
    VideoDataSetMetadata.initVideoDataSetOtherTagsXml(videoDataSet.metadata)
    
    return videoDataSet
  }
}
