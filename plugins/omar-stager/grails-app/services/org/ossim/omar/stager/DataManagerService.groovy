package org.ossim.omar.stager

import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationContext
import org.apache.commons.collections.map.CaseInsensitiveMap
import org.ossim.omar.core.HttpStatus
import org.ossim.omar.core.Repository

/**
 * This is a service class that manages adding and removing data within the OMAR tables.
 * Currently we implement the add and remove for raster and video.
 */
class DataManagerService implements ApplicationContextAware
{
  static transactional = true
  def processSuccessLog
  def processFailureLog
  def filterFileLog
  def acceptFileLog
  def rejectFileLog
  def dataLog

  def dataInfoService

  ApplicationContext applicationContext

  /**
   * This service allows one to add a raster to the omar tables.  
   *
   * @param httpStatusMessage Is used to populate the http response.  This will
   *                          identify the status code messages and any additional
   *                          header paramters that need to be added to the response.
   * @param filename is the file you wish to add to the OMAR tables
   */
  def addRaster(def httpStatusMessage, def params)
  {
    def filename = params.filename as File
    httpStatusMessage.status = HttpStatus.OK
    httpStatusMessage.message = "Added raster ${filename}"


    if ( !filename?.exists() )
    {
      httpStatusMessage.status = HttpStatus.NOT_FOUND
      httpStatusMessage.message = "Not Found: ${filename}"
      log.error(httpStatusMessage.message)
    }
    else if ( !filename?.canRead() )
    {
      httpStatusMessage.status = HttpStatus.FORBIDDEN
      httpStatusMessage.message = "Not Readable ${filename}"
      log.error(httpStatusMessage.message)
    }
    else
    {
      def xml = dataInfoService.getInfo(params.filename)

      if ( xml )
      {
        def background = false;
        try
        {
          background = params.background ? Boolean.valueOf(params.background) : false
        }
        catch (Exception e)
        {

        }
        if ( background )
        {
          log.info("submitting ${filename} for background processing")
          DataManagerQueueItem.addItem([file: "${filename}", dataManagerAction: "addRaster"],
                  true);
        }
        else
        {
          def oms = new XmlSlurper().parseText(xml)
          def omsInfoParser = applicationContext.getBean("rasterInfoParser")
          def repository = findRepositoryForFile(filename)
          def rasterDataSets = omsInfoParser.processDataSets(oms, repository)

          if ( rasterDataSets.size() < 1 )
          {
            httpStatusMessage.status = HttpStatus.UNSUPPORTED_MEDIA_TYPE
            httpStatusMessage.message = "Not a raster file: ${filename}"
            log.error(httpStatusMessage.message)
          }
          else
          {

            rasterDataSets.each {rasterDataSet ->
              def savedRaster = true
              try
              {
                if ( rasterDataSet.save() )
                {
                  //stagerHandler.processSuccessful(filename, xml)
                  httpStatusMessage.status = HttpStatus.OK
                  log.info(httpStatusMessage.message)
                }
                else
                {
                  savedRaster = false
                  httpStatusMessage.status = HttpStatus.UNSUPPORTED_MEDIA_TYPE
                  httpStatusMessage.message = "Unable to save image ${filename}, image probably already exists"
                  log.error(httpStatusMessage.message)
                }
              }
              catch (Exception e)
              {
                httpStatusMessage.status = HttpStatus.UNSUPPORTED_MEDIA_TYPE
                httpStatusMessage.message = "Unable to save image ${filename}, image probably already exists\n${e.message}"
                log.error(httpStatusMessage.message)
              }
            }
            //new org.ossim.omar.DataManagerQueueItem(file: filename.absolutePath, baseDir: parent.baseDir, dataInfo: xml).save()
          }
        }
      }
      else
      {
        httpStatusMessage.message = "Unable to get information on file ${filename}"
        httpStatusMessage.status = HttpStatus.UNSUPPORTED_MEDIA_TYPE
        log.error(httpStatusMessage.message)
      }
    }
  }

  //
  // HACK:  Need to move this!!!
  //
  /*
  def updateRaster(def httpStatusMessage, def params)
  {
    if ( params.id )
    {
      def rasterEntry = RasterEntry.get(params.id)

      if ( rasterEntry )
      {
        def omsInfoParser = applicationContext.getBean("rasterInfoParser")
        def dataInfo = new DataInfo()
        def canOpen = dataInfo.open(rasterEntry.mainFile?.name)

        if ( canOpen )
        {
          def xml = dataInfo.getImageInfo(rasterEntry.entryId as int)?.trim()
          dataInfo.close()
          if ( xml )
          {
            def oms = new XmlSlurper().parseText(xml)
            RasterEntry.initRasterEntry(oms?.dataSets?.RasterDataSet?.rasterEntries?.RasterEntry, rasterEntry)
            rasterEntry.save()
          }
        }
      }
      else
      {
        httpStatusMessage.message = "Query could not find record id  ${params.id} to update"
        httpStatusMessage.status = HttpStatus.NOT_FOUND
      }
    }
    else if ( params.filename )
    {
      def result = RasterDataSet.createCriteria().list {
        fileObjects {
          and {
            eq('type', "main")
            like('name', "%${params.filename}%")
          }
        }
      }
      if ( result.size() > 0 )
      {
        result.each {dataset ->
          dataset.fileObjects.each {fileObject ->
            dataset.rasterEntries.each {rasterEntry ->
              def dataInfo = new DataInfo()
              def canOpen = dataInfo.open(fileObject.name)

              if ( canOpen )
              {
                def xml = dataInfo.getImageInfo(rasterEntry.entryId as int)?.trim()
                dataInfo.close()
                dataInfo = null;
                if ( xml )
                {
                  def oms = new XmlSlurper().parseText(xml)
                  rasterEntry.initRasterEntry(oms?.dataSets?.RasterDataSet?.rasterEntries?.RasterEntry)
                  rasterEntry.save()
                }
              }
            }
          }
        }
      }
      else
      {
        httpStatusMessage.message = "Query could not find file  ${params.filename} to update"
        httpStatusMessage.status = HttpStatus.NOT_FOUND
      }
    }
  }
  */

  def addVideo(def httpStatusMessage, def params)
  {
    httpStatusMessage.status = HttpStatus.OK
    httpStatusMessage.message = "Added video ${filename}"

    def file = params.filename as File

    if ( !file?.exists() )
    {
      httpStatusMessage.status = HttpStatus.NOT_FOUND
      httpStatusMessage.message = "Video Not Found: ${filename}"
      log.error(httpStatusMessage.message)
    }
    else if ( !file?.canRead() )
    {
      httpStatusMessage.status = HttpStatus.FORBIDDEN
      httpStatusMessage.message = "Video Not Readable ${filename}"
      log.error(httpStatusMessage.message)
    }
    else
    {
      def xml = dataInfoService.getInfo(params.filename)
      if ( xml )
      {
        def oms = new XmlSlurper().parseText(xml)
        def omsInfoParser = applicationContext.getBean("videoInfoParser")
        def repository = findRepositoryForFile(file)
        def videoDataSets = omsInfoParser.processDataSets(oms, repository)

        if ( videoDataSets.size() < 1 )
        {
          httpStatusMessage.status = HttpStatus.UNSUPPORTED_MEDIA_TYPE
          httpStatusMessage.message = "Not a video file: ${file}"
          log.error(httpStatusMessage.message)
        }
        else
        {
          videoDataSets.each {videoDataSet ->
            if ( !videoDataSet.save() )
            {
              httpStatusMessage.status = HttpStatus.UNSUPPORTED_MEDIA_TYPE
              httpStatusMessage.message = "Unable to save ${file}, the file probably alread exists"
              log.error(httpStatusMessage.message)
              //stagerHandler.processRejected(file)
            }
            else
            {
              log.info("Added file ${file}")
            }
          }
          //new org.ossim.omar.DataManagerQueueItem(file: file.absolutePath, baseDir: parent.baseDir, dataInfo: xml).save()
        }
      }
      else
      {
        httpStatusMessage.message = "Unable to save information on file ${file}"
        httpStatusMessage.status = HttpStatus.UNSUPPORTED_MEDIA_TYPE
        log.error(httpStatusMessage.message)
      }
    }
  }

  def removeRaster(def httpStatusMessage, def params)
  {
    def status = false
    def filename = params.filename as File

    def rasterFile = RasterFile.findByNameAndType(filename.absolutePath, "main")

    if ( rasterFile )
    {
      rasterFile?.rasterDataSet.delete(flush: true)
      httpStatusMessage.status = HttpStatus.OK
      httpStatusMessage.message = "Removed raster ${filename}"
      log.info(httpStatusMessage.message)
    }
    else
    {
      httpStatusMessage.status = HttpStatus.NOT_FOUND
      httpStatusMessage.message = "Raster file does not exist in the database: ${filename}"
      log.error(httpStatusMessage.message)
    }
  }

  def removeVideo(def httpStatusMessage, def params)
  {
    def status = false
    def filename = params.filename as File

    def videoFile = VideoFile.findByNameAndType(filename.absolutePath, "main")

    if ( videoFile )
    {
      videoFile?.videoDataSet.delete(flush: true)
      httpStatusMessage.status = HttpStatus.OK
      httpStatusMessage.message = "Removed video ${filename}"
      log.info(httpStatusMessage.message)
    }
    else
    {
      httpStatusMessage.status = HttpStatus.NOT_FOUND
      httpStatusMessage.message = "Video file does not exist in the database: ${filename}"
      log.error(httpStatusMessage.message)
    }
  }

  def add(httpStatusMessage, params)
  {
    def tempMap = new CaseInsensitiveMap(params)
    if ( tempMap.datainfo )
    {
      def oms = null
      try
      {
        oms = new XmlSlurper().parseText(tempMap.datainfo)
      }
      catch (java.lang.Exception e)
      {
        log.error("Error parsing datainfo xml string ${tempMap.datainfo}")
        httpStatusMessage.status = HttpStatus.EXPECTATION_FAILED
        httpStatusMessage.message = "Error parsing the xml string passed in"
      }
      try
      {
        if ( oms )
        {
          def omsInfoParsers = applicationContext.getBeansOfType(OmsInfoParser.class)
          def repository = findRepositoryForFile(new File("/"))
          omsInfoParsers?.each { name, value ->
            def dataSets = value.processDataSets(oms, repository)

            dataSets?.each {dataSet ->
              def fileObject = dataSet.fileObjects.find { it.type == "main" }
              if ( dataSet.save() )
              {
                log.info("Saved ${fileObject.name}")
              }
              else
              {
                log.error("Error saving ${fileObject.name}, could already exist")
                httpStatusMessage.status = HttpStatus.EXPECTATION_FAILED
                httpStatusMessage.message = "Error Saving ${fileObject.name}, could already exist"
              }
            }
          }
        }
        else
        {
          log.error("Error parsing datainfo xml string ${tempMap.datainfo}")
          httpStatusMessage.status = HttpStatus.EXPECTATION_FAILED
          httpStatusMessage.message = "Error parsing the xml string passed in"
        }
      }
      catch (java.lang.Exception e)
      {
        log.error(e.message)
        httpStatusMessage.status = HttpStatus.EXPECTATION_FAILED
        httpStatusMessage.message = "Error adding ${fileObject}"
      }
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
      log.debug("Creating default repository ${file?.parentFile?.absolutePath}")
    }
    else
    {
      log.debug("Found repository ${repository.baseDir}")
    }
    return repository
  }
}
