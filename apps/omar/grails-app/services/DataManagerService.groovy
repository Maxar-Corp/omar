import joms.oms.DataInfo

class DataManagerService
{
  boolean transactional = true

  def addRaster(def httpStatusMessage, def filename)
  {
    httpStatusMessage.status  = HttpStatus.OK
    httpStatusMessage.message = "Added raster ${filename}"

    def file = filename as File

    if(!file?.exists())
    {
      httpStatusMessage.status = HttpStatus.NOT_FOUND
      httpStatusMessage.message = "Not Found: ${filename}"
    }
    else if(!file?.canRead())
    {
      httpStatusMessage.status = HttpStatus.FORBIDDEN
      httpStatusMessage.message = "Not Readable ${filename}"
    }
    else
    {
      def xml = StagerUtil.getInfo(file)

      if ( xml )
      {
        def oms = new XmlSlurper().parseText(xml)
        def omsInfoParser = new OmsInfoParser()
        def repository = findRepositoryForFile(file)
        def rasterDataSets = omsInfoParser.processRasterDataSets(oms, repository)
        if(rasterDataSets.size() < 1)
        {
          httpStatusMessage.status  = HttpStatus.UNSUPPORTED_MEDIA_TYPE
          httpStatusMessage.message = "Not a raster file: ${file}"
        }
        else
        {

          rasterDataSets.each {rasterDataSet ->
            if ( rasterDataSet.save() )
            {
               //stagerHandler.processSuccessful(file, xml)
               httpStatusMessage.status  = HttpStatus.OK
             }
             else
             {
               httpStatusMessage.status  = HttpStatus.UNSUPPORTED_MEDIA_TYPE
               httpStatusMessage.message = "Unable to save image ${file}, image probably already exists"
               //stagerHandler.processRejected(file)
             }
          }
          //new StagerQueueItem(file: file.absolutePath, baseDir: parent.baseDir, dataInfo: xml).save()
        }
      }
      else
      {
        httpStatusMessage.message = "Unable to get information on file ${file}"
        httpStatusMessage.status  = HttpStatus.UNSUPPORTED_MEDIA_TYPE
      }
    }
  }

  def addVideo(def httpStatusMessage, def filename)
  {
    httpStatusMessage.status  = HttpStatus.OK
    httpStatusMessage.message = "Added video ${filename}"

    def file = filename as File

    if(!file?.exists())
    {
      httpStatusMessage.status = HttpStatus.NOT_FOUND
      httpStatusMessage.message = "Video Not Found: ${filename}"
    }
    else if(!file?.canRead())
    {
      httpStatusMessage.status = HttpStatus.FORBIDDEN
      httpStatusMessage.message = "Video Not Readable ${filename}"
    }
    else
    {
      def dataInfo = new DataInfo()
      def canOpen = dataInfo.open(file.absolutePath)

      if ( canOpen )
      {
        def xml = dataInfo.getInfo()?.trim()
        dataInfo.close()
        if ( xml )
        {
          def oms = new XmlSlurper().parseText(xml)
          def omsInfoParser = new OmsInfoParser()
          def repository = findRepositoryForFile(file)
          def videoDataSets = omsInfoParser.processVideoDataSets(oms, repository)
          if(videoDataSets.size() < 1)
          {
            httpStatusMessage.status  = HttpStatus.UNSUPPORTED_MEDIA_TYPE
            httpStatusMessage.message = "Not a video file: ${file}"
          }
          else
          {
            videoDataSets.each {videoDataSet ->
              if ( !videoDataSet.save() )
              {
                httpStatusMessage.status  = HttpStatus.UNSUPPORTED_MEDIA_TYPE
                httpStatusMessage.message = "Unable to save ${file}, the file probably alread exists"
                //stagerHandler.processRejected(file)
              }
            }
            //new StagerQueueItem(file: file.absolutePath, baseDir: parent.baseDir, dataInfo: xml).save()
          }
        }
        else
        {
          httpStatusMessage.message = "Unable to save information on file ${file}"
          httpStatusMessage.status  = HttpStatus.UNSUPPORTED_MEDIA_TYPE
        }
      }
      else
      {
         httpStatusMessage.message = "Unable to get information on file ${file}"
         httpStatusMessage.status  = HttpStatus.UNSUPPORTED_MEDIA_TYPE
      }
 
      dataInfo.close()
      dataInfo.delete();
    }
  }

  def removeRaster(def httpStatusMessage, def filename)
  {
    def status = false
    def file = filename as File

    def rasterFile = RasterFile.findByNameAndType(file.absolutePath, "main")

    if ( rasterFile )
    {
      rasterFile?.rasterDataSet.delete(flush: true)
      httpStatusMessage.status = HttpStatus.OK
      httpStatusMessage.message = "Removed raster ${filename}"
    }
    else
    {
      httpStatusMessage.status = HttpStatus.NOT_FOUND
      httpStatusMessage.message = "Raster not found: ${filename}"
    }
  }

  def removeVideo(def httpStatusMessage, def filename)
  {
    def status = false
    def file = filename as File

    def videoFile = VideoFile.findByNameAndType(file.absolutePath, "main")

    if ( videoFile )
    {
      videoFile?.videoDataSet.delete(flush: true)
      httpStatusMessage.status = HttpStatus.OK
      httpStatusMessage.message = "Removed video ${filename}"
    }
    else
    {
      httpStatusMessage.status = HttpStatus.NOT_FOUND
      httpStatusMessage.message = "Video not found: ${filename}"
    }
  }

  synchronized def findRepositoryForFile(def file)
  {
    def repositories = (Repository.list()?.sort { it.baseDir.size() })?.reverse()
    def repository = null

    if ( repositories )
    {
      def filename = file?.absolutePath

      for ( it in repositories )
      {
        if ( filename?.startsWith(it.baseDir) )
        {
          repository = it
          break
        }
      }
    }

    if ( !repository )
    {
      repository = new Repository(baseDir: file?.parentFile?.absolutePath)
      repository.save(flush: true)
    }

    return repository
  }
}
