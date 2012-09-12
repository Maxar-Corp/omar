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
  def parserPool

  ApplicationContext applicationContext

  def add( httpStatusMessage, params )
  {
    def tempMap = new CaseInsensitiveMap( params )
    if ( tempMap.datainfo )
    {
      def oms = null
      try
      {
        def parser = parserPool.borrowObject()
        oms = new XmlSlurper( parser ).parseText( tempMap.datainfo )
        parserPool.returnObject( parser )
      }
      catch ( java.lang.Exception e )
      {
        log.error( "Error parsing datainfo xml string ${tempMap.datainfo}" )
        httpStatusMessage.status = HttpStatus.EXPECTATION_FAILED
        httpStatusMessage.message = "Error parsing the xml string passed in"
      }
      try
      {
        if ( oms )
        {
          def omsInfoParsers = applicationContext.getBeansOfType( OmsInfoParser.class )
          def repository = findRepositoryForFile( new File( "/" ) )
          omsInfoParsers?.each { name, value ->
            def dataSets = value.processDataSets( oms, repository )

            dataSets?.each {dataSet ->
              def fileObject = dataSet.fileObjects.find { it.type == "main" }
              if ( dataSet.save() )
              {
               // log.info( "Saved ${fileObject.name}" )
              }
              else
              {
                log.error( "Error saving ${fileObject.name}, could already exist" )
                httpStatusMessage.status = HttpStatus.EXPECTATION_FAILED
                httpStatusMessage.message = "Error Saving ${fileObject.name}, could already exist"
              }
            }
          }
        }
        else
        {
          log.error( "Error parsing datainfo xml string ${tempMap.datainfo}" )
          httpStatusMessage.status = HttpStatus.EXPECTATION_FAILED
          httpStatusMessage.message = "Error parsing the xml string passed in"
        }
      }
      catch ( java.lang.Exception e )
      {
        log.error( e.message )
        httpStatusMessage.status = HttpStatus.EXPECTATION_FAILED
        httpStatusMessage.message = "Error adding OMS formatted XML"
      }
    }
  }

  synchronized def findRepositoryForFile( def file )
  {
    def repositories = ( Repository.list()?.sort { it.baseDir.size() } )?.reverse()
    def repository = null

    if ( repositories )
    {
      def filename = file?.absolutePath

      for ( it in repositories )
      {
        if ( filename?.startsWith( it.baseDir ) )
        {
          repository = it
          break
        }
      }
    }

    if ( !repository )
    {
      repository = new Repository( baseDir: file?.parentFile?.absolutePath )
      repository.save( flush: true )
      log.debug( "Creating default repository ${file?.parentFile?.absolutePath}" )
    }
    else
    {
      log.debug( "Found repository ${repository.baseDir}" )
    }
    return repository
  }
}
