package org.ossim.omar.video

import org.ossim.omar.core.Repository
import org.ossim.omar.core.HttpStatus
import org.ossim.omar.stager.DataManagerService

class VideoDataSetService extends DataManagerService
{

  static transactional = true

  def parserPool

  def deleteFromRepository( Repository repository )
  {
    def videoDataSets = VideoDataSet.findAllByRepository( repository )

    videoDataSets?.each { it.delete() }
  }

  def addVideo( def httpStatusMessage, def params )
  {
    def filename = params.filename as File

    httpStatusMessage.status = HttpStatus.OK
    httpStatusMessage.message = "Added video ${filename}"

    def file = params.filename as File

    if ( !file?.exists() )
    {
      httpStatusMessage.status = HttpStatus.NOT_FOUND
      httpStatusMessage.message = "Video Not Found: ${filename}"
      log.error( httpStatusMessage.message )
    }
    else if ( !file?.canRead() )
    {
      httpStatusMessage.status = HttpStatus.FORBIDDEN
      httpStatusMessage.message = "Video Not Readable ${filename}"
      log.error( httpStatusMessage.message )
    }
    else
    {
      def xml = dataInfoService.getInfo( params.filename )
      if ( xml )
      {
        def parser = parserPool.borrowObject()
        def oms = new XmlSlurper( parser ).parseText( xml )
        parserPool.returnObject( parser )
        def omsInfoParser = applicationContext.getBean( "videoInfoParser" )
        def repository = findRepositoryForFile( file )
        def videoDataSets = omsInfoParser.processDataSets( oms, repository )

        if ( videoDataSets.size() < 1 )
        {
          httpStatusMessage.status = HttpStatus.UNSUPPORTED_MEDIA_TYPE
          httpStatusMessage.message = "Not a video file: ${file}"
          log.error( httpStatusMessage.message )
        }
        else
        {
          videoDataSets.each {videoDataSet ->
            if ( !videoDataSet.save() )
            {
              httpStatusMessage.status = HttpStatus.UNSUPPORTED_MEDIA_TYPE
              httpStatusMessage.message = "Unable to save ${file}, the file probably alread exists"
              log.error( httpStatusMessage.message )
              //stagerHandler.processRejected(file)
            }
            else
            {
              log.info( "Added file ${file}" )
            }
          }
          //new org.ossim.omar.DataManagerQueueItem(file: file.absolutePath, baseDir: parent.baseDir, dataInfo: xml).save()
        }
      }
      else
      {
        httpStatusMessage.message = "Unable to save information on file ${file}"
        httpStatusMessage.status = HttpStatus.UNSUPPORTED_MEDIA_TYPE
        log.error( httpStatusMessage.message )
      }
    }
  }

  def removeVideo( def httpStatusMessage, def params )
  {
    def status = false
    def filename = params.filename as File

    def videoFile = VideoFile.findByNameAndType( filename.absolutePath, "main" )

    if ( videoFile )
    {
      videoFile?.videoDataSet.delete( flush: true )
      httpStatusMessage.status = HttpStatus.OK
      httpStatusMessage.message = "Removed video ${filename}"
      log.info( httpStatusMessage.message )
    }
    else
    {
      httpStatusMessage.status = HttpStatus.NOT_FOUND
      httpStatusMessage.message = "Video file does not exist in the database: ${filename}"
      log.error( httpStatusMessage.message )
    }
  }

  def deleteVideo( def httpStatusMessage, def params )
  {
    removeVideo( httpStatusMessage, params )
  }
}
