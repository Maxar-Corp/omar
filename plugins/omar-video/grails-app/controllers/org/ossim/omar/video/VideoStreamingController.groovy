package org.ossim.omar.video

import org.springframework.beans.factory.InitializingBean

import org.apache.commons.io.FilenameUtils

class VideoStreamingController implements InitializingBean
{
  def grailsApplication

  def flashDirRoot
  def flashUrlRoot
  def videoKmlService

  def show( )
  {
    def flvUrl
    def title

    if ( !VideoDataSet.findByIndexId( params.id ) )
    {
      render "Alert: No video matched with id param: " + params.id
      return
    }

    def videoDataSet = VideoDataSet.findByIndexId( params.id ) ?: VideoDataSet.get( params.id )

    //println params.id

    if ( videoDataSet )
    {
      VideoFile mainFile = videoDataSet.mainFile
      File videoFile = mainFile.name as File
      File flvFile = null

      switch ( mainFile.format?.toUpperCase() )
      {
      case "MPEG":
        flvFile = "${flashDirRoot}/${FilenameUtils.getBaseName( videoFile.name )}.flv" as File

        if ( !flvFile.exists() )
        {
          def cmd = "ffmpeg -i ${videoFile.absolutePath} -an -vb 2048k -r 15 ${flvFile.absolutePath}"
          println cmd
          def process = cmd.execute()
          process.waitFor()
        }

        flvUrl = new URL( "${flashUrlRoot}/${flvFile.name}" )

        break

      case "FLV":
      case "SWF":
        //def flvContext = videoFile.absolutePath - videoDataSet.repository.baseDir
        def flvContext = videoFile.absolutePath - flashDirRoot

        flvUrl = new URL( "${flashUrlRoot}/${flvContext}" )
        break
      }

      title = videoFile.name
    }
    else
    {
      flash.message = "VideoDataSet not found with id ${params.id}"
      redirect( controller: "videoDataSet", action: "list", params: [flash: flash] )
    }

    [flvUrl: flvUrl, videoDataSet: videoDataSet, title: title]
  }

  def show_mobile( )
  {
    def flvUrl
    def title

    def videoDataSet = VideoDataSet.findByIndexId( params.id ) ?: VideoDataSet.get( params.id )

    if ( videoDataSet )
    {
      VideoFile mainFile = videoDataSet.mainFile
      File videoFile = mainFile.name as File
      File flvFile = null
      File mp4 = null

      switch ( mainFile.format?.toUpperCase() )
      {
      case "MPEG":
        flvFile = "${flashDirRoot}/${FilenameUtils.getBaseName( videoFile.name )}.flv" as File
        mp4 = "${flashDirRoot}/${FilenameUtils.getBaseName( videoFile.name )}.mp4" as File

        if ( !mp4.exists() )
        {
          def cmd = "ffmpeg -i ${videoFile.absolutePath} -an -vcodec mpeg4 -b 1200kb -mbd 2 -flags +4mv+aic -trellis 1  -cmp 2 -subcmp 2 -s 720x480 ${mp4.absolutePath}"

          def process = cmd.execute()
          process.waitFor()
        }

        flvUrl = new URL( "${flashUrlRoot}/${mp4.name}" )

        break
      case "FLV":
        println "two"
      case "SWF":
        println "three"
        //def flvContext = videoFile.absolutePath - videoDataSet.repository.baseDir
        def flvContext = videoFile.absolutePath - flashDirRoot

        flvUrl = new URL( "${flashUrlRoot}/${flvContext}" )
        break
      }

      title = videoFile.name
    }
    else
    {
      flash.message = "VideoDataSet not found with id ${params.id}"
      redirect( controller: "videoDataSet", action: "list", params: [flash: flash] )
    }

    [flvUrl: flvUrl, videoDataSet: videoDataSet, title: title]
  }

  def getKML( )
  {

    def videoDataSet = VideoDataSet.findByIndexId( params.id ) ?: VideoDataSet.get( params.id )
    def videoDataSetList = [videoDataSet]

    File mpegFile = videoDataSet.mainFile.name as File

    def kml = videoKmlService.createVideosKml( videoDataSetList, params )

    response.setHeader( "Content-disposition", "attachment; filename=${FilenameUtils.getBaseName( mpegFile.name )}.kml" )
    render( contentType: "application/vnd.google-earth.kml+xml", text: "${kml}", encoding: "UTF-8" )
  }

  public void afterPropertiesSet( )
  {
    flashDirRoot = grailsApplication.config.videoStreaming.flashDirRoot
    flashUrlRoot = grailsApplication.config.videoStreaming.flashUrlRoot
  }
}
