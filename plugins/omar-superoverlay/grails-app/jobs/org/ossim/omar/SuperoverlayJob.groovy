package org.ossim.omar

import joms.oms.Chain
import org.ossim.omar.raster.RasterEntry

class SuperoverlayJob
{

//	def timeout = 5000 // Execute job once in 5 seconds
//	def startDelay = 30000 // Delay for about 60 seconds before starting
  def sessionFactory
  def propertyInstanceMap = org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP
  def imageChainService
  def parserPool

  static triggers = {}

  def execute( )
  {
    def record = null

    SuperOverlayQueueItem.withTransaction {
      record = SuperOverlayQueueItem.findByStatus( "ready" )
      if ( record )
      {
        record.status = "submitted"
        record.save( flush: true )
      }
    }
    if ( record )
    {
      def chain = new joms.oms.Chain()
      if ( chain.loadChainKwlString( record.kwl ) )
      {
        def outputDir = new File( record.baseDir, record.indexId );
        if ( !outputDir.exists() ) outputDir.mkdirs()
        def outputFile = new File( outputDir.toString(), "${record.indexId}.tif" )
        def kwlString = "type:ossimImageChain\n"
        kwlString += "object0.type:ossimTiffWriter\n"
        kwlString += "object0.image_type:tiff_tiled\n"
        kwlString += "object0.create_external_geometry:false\n"
        kwlString += "object0.filename:${outputFile.toString()}\n"
        def writer = new joms.oms.Chain()
        if ( writer.loadChainKwlString( kwlString ) )
        {
          writer.connectMyInputTo( chain )
          writer.executeChain()
        }
        writer.deleteChain()
        writer = null
      }
      else
      {
      }
      chain.deleteChain()
      chain = null
      //return


      def rasterEntry = RasterEntry.findByIndexId( record.indexId )

      if ( rasterEntry )
      {
        def parser = parserPool.borrowObject()
        def tiepoints = new XmlSlurper(parser).parseText( rasterEntry?.tiePointSet )
        parserPool.returnObject(parser)
        def imageCoordinates = tiepoints.Image.toString().trim()
        def groundCoordinates = tiepoints.Ground.toString().trim()
        def splitImageCoordinates = imageCoordinates.split( " " );
        def splitGroundCoordinates = groundCoordinates.split( " " );
        def gdalTranslate = null

        if ( splitImageCoordinates.size() == splitGroundCoordinates.size() )
        {
          def idx = 0
          gdalTranslate = "gdal_translate -of VRT -a_srs EPSG:4326 "

          for ( idx = 0; idx < splitImageCoordinates.size(); ++idx )
          {
            def gpt = splitGroundCoordinates[idx].replaceAll( ",", " " )
            def ipt = splitImageCoordinates[idx].replaceAll( ",", " " )
            gdalTranslate += " -gcp ${ipt} ${gpt}"
          }
        }
        def inputImage = rasterEntry.rasterDataSet.getFileFromObjects( "main" )?.name;
        if ( inputImage )
        {

          def messageBuffer = new StringBuffer()

          /** Process the input image */

          // gdal_translate
          def outFileBaseDir = new File( record.baseDir ).path
          outFileBaseDir += "/${rasterEntry.indexId}"
          new File( outFileBaseDir ).mkdirs()
          def vrtOutFile = "${outFileBaseDir}/${rasterEntry.indexId}.vrt"
          gdalTranslate += " ${inputImage} ${vrtOutFile}"
          messageBuffer << "Translating input... \n"
          def gdalTranslateProc = gdalTranslate.execute()
          def gdalTranslateProcText = gdalTranslateProc.text

          // gdalwarp
          def vrtWarpOutFile = "${outFileBaseDir}/${rasterEntry.indexId}-warp.vrt"
          def gdalWarp = "gdalwarp -of VRT -t_srs EPSG:4326 ${vrtOutFile} ${vrtWarpOutFile}"
          messageBuffer << "Warping input... \n"
          def gdalWarpProc = gdalWarp.execute()
          gdalWarpProc.waitFor()
          SuperOverlayQueueItem.withTransaction {
            record.message = messageBuffer.toString()
            record.save()
          }

          // gdal2tiles
          def gdal2tiles = "gdal2tiles.py -p geodetic -k ${vrtWarpOutFile} ${outFileBaseDir}"
          messageBuffer << "Generating KML SuperOverlay... \n"
          def gdal2tilesProc = gdal2tiles.execute()
          def timeStamp = System.currentTimeMillis()
          def currentTime = timeStamp
          def deltaTime = 0
          gdal2tilesProc.in.each {c ->
            messageBuffer << (char)c
            deltaTime = ( ( currentTime - timeStamp ) / 1000.0 )
            if ( deltaTime > 5 )
            {
              SuperOverlayQueueItem.withTransaction {
                record.message = messageBuffer.toString()
                record.save()
              }
              timeStamp = currentTime
            }
            currentTime = System.currentTimeMillis()
          }
          gdal2tilesProc.waitFor()
          SuperOverlayQueueItem.withTransaction {
            record.message = messageBuffer.toString()
            record.save()
          }

          messageBuffer << "Done processing Super-Overlay..."
        }

        // Now mark as completed
        SuperOverlayQueueItem.withTransaction {
          record.status = "finished"
          record.save( flush: true )
        }
      }
      else
      {
        SuperOverlayQueueItem.withTransaction {
          record.status = "error"
          record.message = "Index Id: ${record.indexId} not found"
          record.save( flush: true )
        }
      }
    }
  }

  def cleanUpGorm( )
  {
    def session = sessionFactory.currentSession
    session.flush()
    session.clear()
    propertyInstanceMap.get().clear()
  }
}