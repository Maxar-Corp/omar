import org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin

/**
 * Created by IntelliJ IDEA.
 * User: sbortman
 * Date: Dec 18, 2009
 * Time: 7:36:57 PM
 * To change this template use File | Settings | File Templates.
 */
class StagerEventHandler implements FileFilterEventListener
{
  def prefix
  def processSuccessLog
  def processFailureLog
  def filterFileLog
  def acceptFileLog
  def rejectFileLog
  def dataLog

  def omsInfoParser = new OmsInfoParser()
  def repository

  def count = 0
  def batchSize = 100
  def sessionFactory

  public StagerEventHandler(String output = ".", String prefix = "oms")
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
    def oms = new XmlSlurper().parseText(fileEventObject.data)
    def rasterDataSets = omsInfoParser.processRasterDataSets(oms, repository)
    def videoDataSets = omsInfoParser.processVideoDataSets(oms, repository)

    rasterDataSets?.each {rasterDataSet ->
      if ( rasterDataSet.save() )
      {
        dataLog << fileEventObject.file << "\n"
      }
      else
      {
        processFailureLog << fileEventObject.file << "\n"
        rasterDataSet.errors.allErrors.each { println it }
      }
    }

    videoDataSets?.each {videoDataSet ->
      if ( videoDataSet.save() )
      {
        dataLog << fileEventObject.file << "\n"
      }
      else
      {
        processFailureLog << fileEventObject.file << "\n"
      }
    }

//    def stagerQueueItem = new StagerQueueItem(
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
      try
      {
        cleanupGorm()
      }
      catch (Exception e)
      {

      }
    }
  }


  def cleanupGorm()
  {
    def session = sessionFactory.currentSession

    session.flush()
    session.clear()
    DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP.get().clear()
  }
}
