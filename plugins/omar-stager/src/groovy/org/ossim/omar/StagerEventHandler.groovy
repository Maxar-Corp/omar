package org.ossim.omar

import org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationContext

/**
 * Created by IntelliJ IDEA.
 * User: sbortman
 * Date: Dec 18, 2009
 * Time: 7:36:57 PM
 * To change this template use File | Settings | File Templates.
 */
class StagerEventHandler implements FileFilterEventListener, ApplicationContextAware
{
  def prefix
  def processSuccessLog
  def processFailureLog
  def filterFileLog
  def acceptFileLog
  def rejectFileLog
  def dataLog

  def repository

  def count = 0
  def batchSize = 100
  def sessionFactory

  ApplicationContext applicationContext

  public def init(String output = ".", String prefix = "oms")
  {
    File logDir = new File(output)

    if ( !logDir.exists() )
    {
      logDir.mkdir()
    }

    processSuccessLog = new File(logDir, "${prefix}-processSuccess.txt")
    processFailureLog = new File(logDir, "${prefix}-processFailure.txt")

    filterFileLog = new File(logDir, "${prefix}-filterFile.txt")
    acceptFileLog = new File(logDir, "${prefix}-acceptFile.txt")
    rejectFileLog = new File(logDir, "${prefix}-rejectFile.txt")

    dataLog = new File(logDir, "${prefix}-data.dat")

    processSuccessLog.write("")
    processFailureLog.write("")

    filterFileLog.write("")
    acceptFileLog.write("")
    rejectFileLog.write("")

    dataLog.write("")
  }

  def void processFailure(FileFilterEvent fileEventObject)
  {
    processFailureLog << fileEventObject.file << "\n"
  }

  def void processSuccess(FileFilterEvent fileEventObject)
  {
    processSuccessLog << fileEventObject.file << "\n"
  }

  def void filterFile(FileFilterEvent fileEventObject)
  {
    filterFileLog << fileEventObject.file << "\n"
  }

  def void acceptFile(FileFilterEvent fileEventObject)
  {
    acceptFileLog << fileEventObject.file << "\n"
  }

  def void rejectFile(FileFilterEvent fileEventObject)
  {
    rejectFileLog << fileEventObject.file << "\n"
  }

  def void processData(FileFilterEvent fileEventObject)
  {
    try
    {

      def oms = new XmlSlurper().parseText(fileEventObject.data)
      def omsInfoParsers = applicationContext.getBeansOfType(OmsInfoParser.class)


      omsInfoParsers?.each { name, value ->

        def dataSets = value.processDataSets(oms, repository)

        dataSets?.each {dataSet ->
          if ( dataSet.save() )
          {
            dataLog << fileEventObject.file << "\n"
          }
          else
          {
            processFailureLog << fileEventObject.file << "\n"
            //dataSet.errors.allErrors.each { println it }
          }
        }
      }
    }
    catch (java.lang.Exception e)
    {
      processFailureLog << "${fileEventObject.file} ${e.message}" << "\n"
    }

//    def stagerQueueItem = new org.ossim.omar.StagerQueueItem(
//        baseDir: repository.baseDir,
//        file: fileEventObject.file.absolutePath,
//        dataInfo: fileEventObject.data)
//
//    if ( stagerQueueItem.save() )
//    {
//      dataLog << fileEventObject.file << "\n"
//    }
//    else
//    {
//      //processFailureLog << fileEventObject.file << "\n"
//      println fileEventObject
//    }

    if ( ++count % batchSize == 0 )
    {
      cleanupGorm()
    }
  }


  def cleanupGorm()
  {
    try
    {

      def session = sessionFactory.currentSession

      session.flush()
      session.clear()
      DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP.get().clear()
    }
    catch (Exception e)
    {

    }
  }
}
