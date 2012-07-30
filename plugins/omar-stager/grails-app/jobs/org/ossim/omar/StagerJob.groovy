package org.ossim.omar

import groovy.io.FileType
import groovy.io.FileVisitResult

import org.apache.commons.io.FilenameUtils

import org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin
import org.ossim.omar.core.Repository
import org.ossim.omar.stager.StagerUtil

import java.util.concurrent.atomic.AtomicInteger

import static groovyx.gpars.GParsPool.withPool

/**
 * Created by IntelliJ IDEA.
 * User: sbortman
 * Date: 11/18/11
 * Time: 8:56 PM
 * To change this template use File | Settings | File Templates.
 */
class StagerJob
{
  static triggers = {}

  enum Mode {
    SEQ, POOL
  }

  enum Action {
    BUILD_OVRS,
    INDEX_ONLY
  }

  def dataInfoService
  def ingestService

  def sessionFactory
  def index

  def filesLog = ( "/tmp/files.txt" as File ).newPrintWriter()
  def rejectsLog = ( "/tmp/rejects.txt" as File ).newPrintWriter()

  def propertyInstanceMap = DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP

  def parserPool

  def grailsApplication

  def batchSize

  def baseDir

  def filterDir( def dir )
  {
    def status = FileVisitResult.CONTINUE
    def atoc = new File( dir, "a.toc" )

    if ( atoc.exists() )
    {
      processFile( atoc )
      status = FileVisitResult.SKIP_SUBTREE
    }

    def dht = new File( dir, "dht" )

    if ( dht.exists() )
    {
      processFile( dht )
      status = FileVisitResult.SKIP_SUBTREE
    }

    def noScan = new File( dir, "__OSSIM_NO_SCAN__" )

    if ( noScan.exists() )
    {
      status = FileVisitResult.SKIP_SUBTREE
    }

    return status
  }

  def filterFile( def file )
  {
    def ext = FilenameUtils.getExtension( file.name ).toLowerCase()
    def status = true

    switch ( ext )
    {
    // OSSIM Support files
    case "omd":
    case "ovr":
    case "his":
    case "kwl":
    case "statistics":
    case "geom":
      // Shape support files
    case "shp":  // Skip shape files for now...
    case "prj":
    case "dbf":
    case "shx":
    case "sbx":
    case "sbn":
      // Documentation files
    case "txt":
    case "pdf":
    case "xml":
    case "xsl":
    case "doc":
    case "rtf":
    case ~/htm[l]?/:
    case "kml":
    case "kmz":
    case "csv":
      // MacOSX System files
    case "ds_store":
    case ~/.*~/:
    case "tmp":
    case ~/b[a]?k/:
      // Archive files
    case "tgz":
    case "tar":
    case "gz":
    case "zip":
      // Projection support files:
    case "nfw":
    case "sdw":
      // NITF Support files
    case "hdr":
    case "imd":
    case "rpb":
    case "til":
    case "dim":
      // J2K support files
    case "aux":
      status = false
      break
    // LandSet7
    case "fst":
      def openLandSat = ~/.*b[1-8][0-2].fst/
      status = !( openLandSat.matcher( file.name.toLowerCase() ).matches() )
      break
    }

    return status
  }

  def processFile( def file )
  {
    try
    {
      //filesLog.append("${file.absolutePath}\n")
      def start = System.currentTimeMillis()


      def xml = null
      def action = Action.BUILD_OVRS

      switch ( action )
      {
      case Action.BUILD_OVRS:
        xml = StagerUtil.getInfo( file )
        break
      case Action.INDEX_ONLY:
        xml = dataInfoService.getInfo( file.absolutePath )
        break
      }

      if ( xml )
      {
        def parser = parserPool.borrowObject()
        def oms = new XmlSlurper( parser ).parseText( xml )

        parserPool.returnObject( parser )

        def (status, message) = ingestService.ingest( oms, baseDir )

        switch ( status )
        {
        case 200:
          filesLog.println file.absolutePath
          break
        case 500:
          rejectsLog.println "${file.absolutePath} ${message}"
          break
        }
      }
      else
      {
        rejectsLog.println file.absolutePath
      }

      if ( index?.incrementAndGet() % batchSize == 0 )
      {
        cleanUpGorm()
      }

      def end = System.currentTimeMillis()
    }

    catch ( Exception e )
    {
      println "ERROR: ${file} ${e.message}"
    }
  }

  def cleanUpGorm( )
  {
    def session = sessionFactory.currentSession
    session.flush()
    session.clear()
    propertyInstanceMap.get().clear()
  }


  def execute( def context )
  {
    baseDir = context.mergedJobDataMap['baseDir'] as File

    def options = [
            type: FileType.FILES,
            preDir: this.&filterDir,
            filter: this.&filterFile
    ]

    index = new AtomicInteger( 0 )
    batchSize = grailsApplication.config.hibernate.jdbc.batch_size as int


    if ( baseDir?.exists() )
    {
      def mode = Mode.SEQ

      switch ( mode )
      {
      case Mode.SEQ:
        baseDir?.traverse( options, this.&processFile )
        break
      case Mode.POOL:
        withPool {
          baseDir.traverse( options ) { file -> this.&processFile.callAsync( file ) }
        }
        break
      }
    }

    cleanUpGorm()

    Repository.withTransaction {
      def repository = Repository.findByBaseDir( baseDir?.absolutePath )
      repository.scanEndDate = new Date()
      repository.save()
    }

    filesLog.flush()
    filesLog.close()

    rejectsLog.flush()
    rejectsLog.close()
  }

}
