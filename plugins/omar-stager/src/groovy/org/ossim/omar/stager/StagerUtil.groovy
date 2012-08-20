package org.ossim.omar.stager
/**
 * Created by IntelliJ IDEA.
 * User: sbortman
 * Date: Dec 18, 2009
 * Time: 9:19:25 PM
 * To change this template use File | Settings | File Templates.
 */

import joms.oms.ImageStager
import joms.oms.DataInfo

class StagerUtil
{
  static DataInfo sharedDataInfo = new DataInfo()
  static ImageStager sharedImageStager = new ImageStager()

  public static def getInfo( File file )
  {
    def xml = null
    DataInfo dataInfo = new DataInfo()
    ImageStager imageStager

    try
    {

      def canOpen = dataInfo.open( file.absolutePath )

      if ( canOpen )
      {
//        imageStager = new ImageStager()
//
//        if ( imageStager.open( file.absolutePath ) )
//        {
//          imageStager.setUseFastHistogramStagingFlag( true )
//          def generated = imageStager.stageAll()
//
        xml = dataInfo.getInfo()?.trim()

        if ( needsOvrs( xml ) )
        {
          buildOvrsAndHis( file )
          dataInfo.close()
          dataInfo.open( file.absolutePath )
          xml = dataInfo.getInfo()?.trim()
        }
//        }

      }
    }
    catch ( Exception e )
    {
      xml = null
      println "ERROR: ${file}"
    }
    finally
    {
//      if ( imageStager )
//      {
//        imageStager.delete()
//      }

      dataInfo.close()
      dataInfo.delete()
    }

    return xml
  }


  public static synchronized def getInfoSynchronized( File file )
  {
    def canOpen = sharedDataInfo.open( file.absolutePath )
    def xml = null

    if ( canOpen )
    {
      sharedImageStager.filename = file.absolutePath

      def generated = sharedImageStager.stageAll()

      if ( generated )
      {
        sharedDataInfo.close()
        sharedDataInfo.open( file.absolutePath )
      }

      xml = sharedDataInfo.getInfo()?.trim()

//      if ( xml )
//      {
//        def oms = new XmlSlurper().parseText(xml)
//      }
    }

    sharedDataInfo.close()

    return xml
  }

  static def buildOvrsAndHis( def file )
  {
    def fileRoot = file.name.substring( 0, file.name.lastIndexOf( '.' ) )
    def hisFile = new File( file.parent, "${fileRoot}.his" )
    def hisFlag = ( hisFile.exists() ) ? '' : '--create-histogram-fast'
    def cmd = "ossim-img2rr ${hisFlag} ${file.absolutePath}"
    def proc = cmd.execute()

    proc.consumeProcessOutput()

    def exitCode = proc.waitFor()

    return exitCode
  }

  static def needsOvrs( def xml )
  {
    def oms = new XmlSlurper().parseText( xml )
    def status = false

    for ( def x in oms.dataSets.RasterDataSet )
    {
      for ( def y in x.rasterEntries.RasterEntry )
      {
        def width = y?.width?.text()?.toInteger()
        def height = y.height?.text()?.toInteger()
        def numberOfResLevels = y.numberOfResLevels?.text()?.toInteger()

        status = (status || needsOvrs(width, height, numberOfResLevels))
        //def size = Math.max( width, height )
        //numberOfResLevels.times { size /= 2 }
        //status = ( status || size > 128 )

//        def debug = [
//          width: width,
//          height: height,
//          numberOfResLevels: numberOfResLevels,
//          status: status
//        ]
//        println debug
      }
    }

    return status
  }
  static def needsOvrs(def fullResWidth, def fullResHeight, def nRlevels)
  {
      def size = Math.max( fullResWidth, fullResHeight )
      nRlevels.times { size /= 2 }
      (size > 64)
  }
}
