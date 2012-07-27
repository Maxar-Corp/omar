package org.ossim.omar.raster

import org.ossim.omar.core.Repository
import org.ossim.omar.core.HttpStatus
import org.ossim.omar.stager.DataManagerService
import org.ossim.omar.stager.StagerUtil

class RasterDataSetService extends DataManagerService
{

  static transactional = true

  def parserPool

  def deleteFromRepository( Repository repository )
  {
    def rasterDataSets = RasterDataSet.findAllByRepository( repository )

    rasterDataSets?.each { it.delete() }
  }

  /**
   * This service allows one to add a raster to the omar tables.
   *
   * @param httpStatusMessage Is used to populate the http response.  This will
   *                          identify the status code messages and any additional
   *                          header paramters that need to be added to the response.
   * @param filename is the file you wish to add to the OMAR tables
   */
  def addRaster( def httpStatusMessage, def params )
  {
    def filename = params.filename as File
    httpStatusMessage.status = HttpStatus.OK
    httpStatusMessage.message = "Added raster ${filename}"


    if ( !filename?.exists() )
    {
      httpStatusMessage.status = HttpStatus.NOT_FOUND
      httpStatusMessage.message = "Not Found: ${filename}"
      log.error( httpStatusMessage.message )
    }
    else if ( !filename?.canRead() )
    {
      httpStatusMessage.status = HttpStatus.FORBIDDEN
      httpStatusMessage.message = "Not Readable ${filename}"
      log.error( httpStatusMessage.message )
    }
    else
    {
      def xml

      if ( params.buildOvrs?.toBoolean() == true )
      {
        xml = StagerUtil.getInfo( params.filename as File )
      }
      else
      {
        xml = dataInfoService.getInfo( params.filename )
      }

      if ( xml )
      {
        def background = false;
        try
        {
          background = params.background ? Boolean.valueOf( params.background ) : false
        }
        catch ( Exception e )
        {

        }
        if ( background )
        {
          log.info( "submitting ${filename} for background processing" )
          DataManagerQueueItem.addItem( [file: "${filename}", dataManagerAction: "addRaster"],
                  true );
        }
        else
        {
          def parser = parserPool.borrowObject()
          def oms = new XmlSlurper( parser ).parseText( xml )

          parserPool.returnObject( parser )
          def omsInfoParser = applicationContext.getBean( "rasterInfoParser" )
          def repository = findRepositoryForFile( filename )
          def rasterDataSets = omsInfoParser.processDataSets( oms, repository )

          if ( rasterDataSets.size() < 1 )
          {
            httpStatusMessage.status = HttpStatus.UNSUPPORTED_MEDIA_TYPE
            httpStatusMessage.message = "Not a raster file: ${filename}"
            log.error( httpStatusMessage.message )
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
                  log.info( httpStatusMessage.message )
                  def ids = rasterDataSet.rasterEntries.collect {it.id}.join( "," )
                  httpStatusMessage.message = "Added raster ${ids}:${filename}"
                }
                else
                {
                  savedRaster = false
                  httpStatusMessage.status = HttpStatus.UNSUPPORTED_MEDIA_TYPE
                  httpStatusMessage.message = "Unable to save image ${filename}, image probably already exists"
                  log.error( httpStatusMessage.message )
                }
              }
              catch ( Exception e )
              {
                httpStatusMessage.status = HttpStatus.UNSUPPORTED_MEDIA_TYPE
                httpStatusMessage.message = "Unable to save image ${filename}, image probably already exists\n${e.message}"
                log.error( httpStatusMessage.message )
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
        log.error( httpStatusMessage.message )
      }
    }
  }

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
            def parser = parserPool.borrowObject()
            def oms = new XmlSlurper(parser).parseText(xml)
            parserPool.returnObject(parser)
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
                  def parser = parserPool.borrowObject()
                  def oms = new XmlSlurper(parser).parseText(xml)
                  parserPool.returnObject(parser)
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

  def removeRaster( def httpStatusMessage, def params )
  {
    def status = false
    def filename = params.filename as File

    // println filename

    def rasterFile = RasterFile.findByNameAndType( filename.absolutePath, "main" )

    if ( rasterFile )
    {
      rasterFile?.rasterDataSet.delete( flush: true )
      httpStatusMessage.status = HttpStatus.OK
      def ids = rasterFile?.rasterDataSet?.rasterEntries.collect {it.id}.join( "," )
      httpStatusMessage.message = "removed raster ${ids}:${filename}"
      log.info( httpStatusMessage.message )
    }
    else
    {
      httpStatusMessage.status = HttpStatus.NOT_FOUND
      httpStatusMessage.message = "Raster file does not exist in the database: ${filename}"
      log.error( httpStatusMessage.message )
    }
  }


  def deleteRaster( def httpStatusMessage, def params )
  {
    removeRaster( httpStatusMessage, params )
  }

}
