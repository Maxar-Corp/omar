package org.ossim.omar.raster

import java.awt.Point
import java.awt.Rectangle
import java.awt.image.BufferedImage
import java.awt.image.ColorModel
import java.awt.image.DataBuffer
import java.awt.image.DataBufferByte
import java.awt.image.WritableRaster

import joms.oms.Chain
import joms.oms.ossimDpt
import joms.oms.ossimDptVector
import joms.oms.ossimGpt
import joms.oms.ossimGptVector
import joms.oms.ossimImageGeometry
import joms.oms.ossimImageGeometryPtr
import joms.oms.ossimImageSource
import joms.oms.ossimKeywordlist
import joms.oms.ossimUnitConversionTool
import joms.oms.Util
import joms.oms.WmsMap
import joms.oms.WmsView

import org.ossim.oms.image.omsImageSource

import org.ossim.omar.core.TransparentFilter
import org.ossim.omar.core.Utility
import org.ossim.omar.core.WmsLayers

import org.ossim.omar.ogc.WMSCapabilities

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware

class WebMappingService implements ApplicationContextAware
{
  def grailsApplication
  def imageChainService

  def parserPool

  ApplicationContext applicationContext

  public static final String BLANK = "blank"

  static transactional = false

  def transparent = new TransparentFilter()


  WMSQuery setupQuery(def wmsRequest)
  {
    def wmsQuery = new WMSQuery()
    def params = wmsRequest.toMap();
    wmsQuery.caseInsensitiveBind( wmsRequest.toMap() )
    def max = params.max ? params.max as Integer : 10
    if ( max > 10 ) max = 10
    wmsQuery.max = max
    if ( wmsQuery.layers?.toLowerCase() == "raster_entry" )
    {
      wmsQuery.layers = null
    }
    // for now we will sort by the date field if no layers are given
    //
    if ( !wmsQuery.layers && ( wmsQuery.time || wmsQuery.startDate || wmsQuery.endDate ) )
    {
      wmsQuery.sort = wmsQuery.sort ?: "acquisitionDate"
      wmsQuery.order = wmsQuery.order ?: "desc"
    }
    wmsQuery
  }

  def getMap(def wmsRequest, def layers = null)
  {
    def result = [image: null, errorMessage: null]
    def params = wmsRequest.toMap();
    def bounds = wmsRequest.bounds
    def maxBands = 1
    def wmsQuery = layers ? null : setupQuery( wmsRequest );
    def stretchMode = wmsRequest?.stretch_mode ? wmsRequest?.stretch_mode.toLowerCase() : null
    def stretchModeRegion = wmsRequest?.stretch_mode_region ?: null
    def wmsView = new WmsView()
    def srs = wmsRequest?.srs
    if ( !wmsView.setProjection( srs ) )
    {
      result.errorMessage = "Unsupported projection ${ srs }"
      log.error( result )
      return result
    }
    if ( !wmsView.setViewDimensionsAndImageSize( bounds.minx,
        bounds.miny,
        bounds.maxx,
        bounds.maxy,
        bounds.width,
        bounds.height ) )
    {
      result.errorMessage = "Unable to set the dimensions for the view bounds"
      log.error( result )
      return result
    }



    def rasterEntries = layers;

    if ( wmsQuery )
    {
      def x = {
        maxResults( 10 )
      }

      def criteriaBuilder = RasterEntry.createCriteria();
      def criteria = criteriaBuilder.buildCriteria( x )

      criteria.add( wmsQuery?.createClause() )

      def eachCriteria = criteria.scroll()
      def status = eachCriteria.first()
      rasterEntries = []

      while ( status )
      {
        rasterEntries << eachCriteria.get( 0 )

        status = eachCriteria.next()
      }

      eachCriteria.close()
    }

    //params.viewGeom = wmsView.getImageGeometry();
    params.wmsView = wmsView
    params.keepWithinScales = true
    def kwlString = ""
    if ( rasterEntries )
    {
      rasterEntries = rasterEntries?.reverse()
      def srcChains = []
      for ( def rasterEntry in rasterEntries )
      {
        def chainMap = imageChainService.createImageChain( rasterEntry, params )
        //chain.print()
        if ( chainMap && chainMap.chain && ( chainMap.chain.getChain() != null ) )
        {
          def outputBands = chainMap.chain?.getChainAsImageSource()?.getNumberOfOutputBands()
          if ( outputBands > maxBands ) maxBands = outputBands
          srcChains.add( chainMap )
        }
        chainMap = null
      }
      if ( srcChains )
      {
        def connectionId = 10000
        kwlString = "type:ossimImageChain\n"
        def objectPrefixIdx = 0
        if ( srcChains.size() > 1 )
        {
          // now establish mosaic and cut to match the output dimensions
          kwlString += "object${ objectPrefixIdx }.type:ossimImageMosaic\n"
          ++objectPrefixIdx
        }
        def imageRect = wmsView.getViewImageRect()
        def midPoint = imageRect.midPoint()
        def x = (int)( midPoint.x + 0.5 )
        def y = (int)( midPoint.y + 0.5 )
        x -= ( bounds.width * 0.5 );
        y -= ( bounds.height * 0.5 );
        def w = bounds.width
        def h = bounds.height
        imageRect = null
        midPoint = null

        // for now scale all WMS requests to 8-bit
        kwlString += "object${ objectPrefixIdx }.type:ossimScalarRemapper\n"
        kwlString += "object${ objectPrefixIdx }.id:${ connectionId }\n"
        ++connectionId
        ++objectPrefixIdx
        // and make it either 1 band or 3 band output
        //
        if ( maxBands == 2 )
        {
          kwlString += "object${ objectPrefixIdx }.type:ossimBandSelector\n"
          kwlString += "object${ objectPrefixIdx }.bands:(0)\n"
          kwlString += "object${ objectPrefixIdx }.id:${ connectionId }\n"
          ++connectionId
          ++objectPrefixIdx
        }
        else if ( maxBands > 3 )
        {
          kwlString += "object${ objectPrefixIdx }.type:ossimBandSelector\n"
          kwlString += "object${ objectPrefixIdx }.bands:(0,1,2)\n"
          kwlString += "object${ objectPrefixIdx }.id:${ connectionId }\n"
          ++connectionId
          ++objectPrefixIdx
        }
        kwlString += "object${ objectPrefixIdx }.type:ossimRectangleCutFilter\n"
        kwlString += "object${ objectPrefixIdx }.rect:(${ x },${ y },${ w },${ h },lh)\n"
        kwlString += "object${ objectPrefixIdx }.cut_type:null_outside\n"
        kwlString += "object${ objectPrefixIdx }.id:${ connectionId }\n"
        ++objectPrefixIdx
        if ( ( stretchModeRegion == "viewport" ) &&
            ( stretchMode != "none" ) )
        {
          kwlString += "object${ objectPrefixIdx }.type:ossimImageHistogramSource\n"
          kwlString += "object${ objectPrefixIdx }.id:${ connectionId + 1 }\n"
          ++objectPrefixIdx
          kwlString += "object${ objectPrefixIdx }.type:ossimHistogramRemapper\n"
          kwlString += "object${ objectPrefixIdx }.id:${ connectionId + 2 }\n"
          kwlString += "object${ objectPrefixIdx }.stretch_mode:${ stretchMode }\n"
          kwlString += "object${ objectPrefixIdx }.input_connection1:${ connectionId }\n"
          kwlString += "object${ objectPrefixIdx }.input_connection2:${ connectionId + 1 }\n"
          ++objectPrefixIdx
          connectionId += 2
        }
      }
      else
      {
        kwlString = "type:ossimMemoryImageSource\n"
        if ( params.width && params.height )
        {
          kwlString += "rect:(0,0,${ bounds.width },${ bounds.height },lh)\n"
          kwlString += "scalar_type:ossim_uint8\n"
          kwlString += "number_bands:1\n"
        }
      }
      def mosaic = new joms.oms.Chain();
      mosaic.loadChainKwlString( kwlString )
      for ( def srcChain in srcChains )
      {
        mosaic.connectMyInputTo( srcChain.chain )
      }
      result.image = imageChainService.grabOptimizedImageFromChain( mosaic, params )
      mosaic?.deleteChain()
      for ( def it in srcChains )
      {
        it.chain.deleteChain()
        it.kwl = ""
      }
      params.clear()
      mosaic = null
      srcChains?.clear()
      srcChains = null
      wmsView?.delete()
      wmsView = null
    }
    else // setup an empty chain
    {
      result.image = null
      result.errorMessage = "No images found for the specified request"
//        kwlString = "type:ossimMemoryImageSource\n"
      //        if(params.width&&params.height)
      //       {
      //           kwlString += "rect:(0,0,${params.width},${params.height},lh)\n"
      //           kwlString += "scalar_type:ossim_uint8\n"
      //           kwlString += "number_bands:1\n"
      //      }
      //      def chain = new joms.oms.Chain();
      //      chain.loadChainKwlString(kwlString)
      //      result.image = imageChainService.grabOptimizedImageFromChain(chain, params)
    }

    return result
  }

  BufferedImage getUnprojectedTile(Rectangle rect,
                                   String inputFile,
                                   int entry,
                                   def inputBandCount,
                                   BigDecimal scale,
                                   int startSample, int endSample, int startLine, int endLine,
                                   def params)
  {
    def sharpenMode = params.sharpen_mode ?: ""
    def bands = params?.bands ?: ""
    def rotate = params?.rotate ?: "0.0"
    int viewableBandCount = 1
    if ( sharpenMode.equals( "light" ) )
    {
      params.sharpen_width = "3"
      params.sharpen_sigma = ".5"
    }
    else if ( sharpenMode.equals( "heavy" ) )
    {
      params.sharpen_width = "5"
      params.sharpen_sigma = "1"
    }
    if ( inputBandCount > 3 )
    {
      viewableBandCount = 3
    }
    def bandSelectorCount = bands ? bands.split( "," ).length : 0
    if ( bandSelectorCount > 0 )
    {
      if ( bandSelectorCount >= 3 )
      {
        viewableBandCount = 3;
      }
      else
      {
        viewableBandCount = 1;
      }
    }
//    println params
    //    println rect
    //    println "${inputFile} ${entry}"
    //    println stretchMode
    //    println viewportStretchMode
    //    println scale
    //    println "${startSample} ${endSample} ${startLine} ${endLine}"

    byte[] data = new byte[rect.width * rect.height * 3]
    def kwl = new ossimKeywordlist();
    for ( def param in params )
    {
      kwl.add( param.key, param.value )
    }
    kwl.add( "viewable_bands", "${ viewableBandCount }" )
    kwl.add( "rotate", "${ rotate }" )
    WmsMap.getUnprojectedMap(
        inputFile,
        entry,
        scale,
        startSample, endSample, startLine, endLine,
        data,
        kwl
    )
    DataBuffer dataBuffer = new DataBufferByte( data, data.size() )
    int pixelStride = viewableBandCount
    int lineStride = viewableBandCount * rect.width
    int[] bandOffsets = null;
    if ( viewableBandCount == 1 )
    {
      bandOffsets = [0] as int[]
    }
    else
    {
      bandOffsets = [0, 1, 2] as int[]
    }
    def image;
    if ( viewableBandCount == 1 )
    {
      image = Utility.convertToColorIndexModel( dataBuffer, rect.width as Integer, rect.height as Integer, false )
    }
    else
    {
      Point location = null
      WritableRaster raster = WritableRaster.createInterleavedRaster(
          dataBuffer,
          rect.width as Integer,
          rect.height as Integer,
          lineStride,
          pixelStride,
          bandOffsets,
          location )

      ColorModel colorModel = omsImageSource.createColorModel( raster.sampleModel )

      boolean isRasterPremultiplied = true
      Hashtable<?, ?> properties = null

      image = new BufferedImage(
          colorModel,
          raster,
          isRasterPremultiplied,
          properties )
    }

    return image
  }

  String getCapabilities(def wmsRequest, String serviceAddress)
  {
    def imageDataSearchService = applicationContext.getBean( "imageDataSearchService" )
    def layerNames = wmsRequest?.layers?.split( ',' ) as String[]
    def filter = wmsRequest?.filter
    def layers

    if ( layerNames )
    {
      layers = imageDataSearchService?.getWmsImageLayers( layerNames )
    }
    else if ( filter )
    {
      layers = imageDataSearchService?.getWmsImageLayers( filter )
    }

    def wmsCapabilites = new WMSCapabilities( layers, serviceAddress )

    return wmsCapabilites.getCapabilities()
  }

  String getKML(def wmsRequest, String serviceAddress)
  {
    def imageDataSearchService = applicationContext.getBean( "imageDataSearchService" )
    def layerNames = wmsRequest?.layers?.split( ',' ) as String[]
    def filter = wmsRequest?.filter

    def layers

    if ( layerNames )
    {
      layers = imageDataSearchService?.getWmsImageLayers( layerNames )
    }
    else if ( filter )
    {
      layers = imageDataSearchService?.getWmsImageLayers( filter )
    }

    def wmsCapabilities = new WMSCapabilities( layers, serviceAddress )

    return wmsCapabilities.getKML()
  }

  def computeScales(def rasterEntries)
  {
    def unitConversion = new ossimUnitConversionTool( 1.0 )
    def fullResScale = 0.0 // default to 1 unit per pixel
    //    def minResLevels  = 0 // default to 1 unit per pixel
    def smallestScale = 0.0
    def largestScale = 0.0
    def testScale = 0.0

    for ( def rasterEntry in rasterEntries )
    {
      if ( rasterEntry.gsdY )
      {
        unitConversion.setValue( rasterEntry.gsdY );
        def testValue = unitConversion.getDegrees();
        if ( ( fullResScale == 0.0 ) || ( testValue < fullResScale ) )
        {
          fullResScale = testValue
        }
        if ( smallestScale == 0.0 )
        {
          smallestScale = fullResScale
          largestScale = fullResScale
        }
      }
      if ( rasterEntry.numberOfResLevels )
      {
        testScale = 2 ** rasterEntry.numberOfResLevels * fullResScale;
        if ( testScale > largestScale )
        {
          largestScale = testScale
        }
      }
      // now allow at least 32x zoom in
      testScale = 1.0 / ( 2 ** 6 ) * fullResScale
      if ( testScale < smallestScale )
      {
        smallestScale = testScale;
      }
    }

    return [fullResScale: fullResScale, smallestScale: smallestScale, largestScale: largestScale]
  }

  def computeBounds(def rasterEntries)
  {
    def unionBounds = null
    for ( def rasterEntry in rasterEntries )
    {
      def groundGeom = rasterEntry?.groundGeom
      if ( unionBounds )
      {
        unionBounds = unionBounds.union( groundGeom )
      }
      else
      {
        unionBounds = groundGeom
      }
    }
    def coords = unionBounds?.envelope?.coordinates
    def minx = 9999999999
    def maxx = -9999999999
    def miny = 9999999999
    def maxy = -9999999999
    for ( def coord in coords )
    {
      if ( coord.x < minx ) minx = coord.x
      if ( coord.x > maxx ) maxx = coord.x
      if ( coord.y < miny ) miny = coord.y
      if ( coord.y > maxy ) maxy = coord.y
    }

    return [left: minx, right: maxx, top: maxy, bottom: miny]
  }

  def createModelFromTiePointSet(def rasterEntry)
  {
    def gptArray = new ossimGptVector();
    def dptArray = new ossimDptVector();
    if ( rasterEntry?.tiePointSet )
    {
      def parser = parserPool.borrowObject()
      def tiepoints = new XmlSlurper( parser ).parseText( rasterEntry?.tiePointSet )
      parserPool.returnObject( parser )
      def imageCoordinates = tiepoints.Image.toString().trim()
      def groundCoordinates = tiepoints.Ground.toString().trim()
      def splitImageCoordinates = imageCoordinates.split( " " );
      def splitGroundCoordinates = groundCoordinates.split( " " );
      for ( def it in splitImageCoordinates )
      {
        def point = it.split( "," )
        if ( point.size() >= 2 )
        {
          dptArray.add( new ossimDpt( Double.parseDouble( point.getAt( 0 ) ),
              Double.parseDouble( point.getAt( 1 ) ) ) )
        }
      }
      for ( def it in splitGroundCoordinates )
      {
        def point = it.split( "," )
        if ( point.size() >= 2 )
        {
          gptArray.add( new ossimGpt( Double.parseDouble( point.getAt( 1 ) ),
              Double.parseDouble( point.getAt( 0 ) ) ) )
        }
      }
    }
    else if ( rasterEntry?.groundGeom ) // lets do a fall back if the tiepoint set is not set.
    {
      def coordinates = rasterEntry?.groundGeom.getCoordinates();
      if ( coordinates.size() >= 4 )
      {
        def w = width as double
        def h = height as double
        for ( def it in ( 0..<4 ) )
        {
          def point = coordinates[it];
          gptArray.add( new ossimGpt( coordinates[it].y, coordinates[it].x ) );
        }
        dptArray.add( new ossimDpt( 0.0, 0.0 ) )
        dptArray.add( new ossimDpt( w - 1, 0.0 ) )
        dptArray.add( new ossimDpt( w - 1, h - 1 ) )
        dptArray.add( new ossimDpt( 0.0, h - 1 ) )
      }
    }
    if ( ( gptArray.size() < 1 ) || ( dptArray.size() < 1 ) )
    {
      return null
    }
    return Util.createBilinearModel( dptArray, gptArray )
  }

  def getBaseLayers()
  {
    def baseWMS = grailsApplication.config.wms.base.layers


    def wmsLayers = WmsLayers.list()

    for ( def wmsLayer in wmsLayers )
    {
      def newLayer = [
          name: wmsLayer.name,
          url: wmsLayer.url,
          params: wmsLayer.params ?: grailsApplication.config.wms.base.defaultParams,
          options: wmsLayer.options ?: [:]
      ]

      if ( !baseWMS.find { it.name == newLayer.name } )
      {
        baseWMS << newLayer
      }
    }

    return baseWMS

  }

}

