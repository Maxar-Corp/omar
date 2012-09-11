package org.ossim.omar.raster

import joms.oms.Chain
import joms.oms.WmsMap
import joms.oms.ossimKeywordlist
import joms.oms.ossimGptVector
import joms.oms.ossimDptVector
import joms.oms.Util
import joms.oms.ossimDpt
import joms.oms.ossimGpt
import org.ossim.oms.image.omsImageSource
import org.ossim.oms.image.omsRenderedImage
import java.awt.image.*
import org.ossim.omar.core.TransparentFilter
import org.ossim.omar.core.ImageGenerator
import org.ossim.omar.core.Utility;

class ImageChainService
{

  static transactional = false

  def parserPool

  /**
   * @param numberOfInputBands
   * @param bandSelection
   * @return true if the bandSelection list is valid or false otherwise
   */
  static def validBandSelection( def numberOfInputBands, def bandSelection )
  {
    def bandArray = []
    def validBands = true
    if(bandSelection == "default") return validBands
    if ( bandSelection instanceof String )
    {
      bandArray = bandSelection.split( "," )
    }
    // validate that the ban list is within the desired ranges
    // the http request is 0 based.
    if ( bandArray.size() < 1 )
    {
      validBands = false
    }
    else
    {
      bandArray.each {
        try
        {
          if ( Integer.parseInt( it ) >= numberOfInputBands )
          {
            validBands = false;
          }
        }
        catch ( Exception e )
        {
          validBands = false
        }
      }
    }
    validBands
  }
  /**
   * @param entry
   * @param params
   * @return a map that allocates a chain if specified and the keywordlist string representing the parameters
   */
  def createImageChain( def entry, def params, def allocateChain = true )
  {
    def quickLookFlagString = params?.quicklook ?: "false"
    def interpolation = params.interpolation ? params.interpolation : "bilinear"
    def sharpenMode = params?.sharpen_mode ?: ""
    def crs = params.srs ? params.srs : params.crs ?: null
    def nullFlip = params?.null_flip ?: null
    def requestFormat = params?.format?.toLowerCase()
    def sharpenWidth = params?.sharpen_width ?: null
    def sharpenSigma = params?.sharpen_sigma ?: null
    def stretchMode = params?.stretch_mode ? params?.stretch_mode.toLowerCase() : null
    def stretchModeRegion = params?.stretch_mode_region ?: null
    def bands = params?.bands ?: ""
    def rotate = params?.rotate ?: null
    def scale = params?.scale ?: null
    def pivot = params?.pivot ?: null
    def tempHistogramFile = entry?.getHistogramFile()//getFileFromObjects("histogram")?.name
    def tempOverviewFile = entry?.getFileFromObjects( "overview" )?.name
    def histogramFile = new File( tempHistogramFile ?: "" )
    def overviewFile = new File( tempOverviewFile ?: "" )
    def objectPrefixIdx = 0
    def kwlString = "type: ossimImageChain\n"
    def quickLookFlag = false
    def enableCache = true
    def viewGeom = params.wmsView ? params.wmsView.getImageGeometry() : params.viewGeom
    def keepWithinScales = params.keepWithinScales ?: false
    def brightness = params.brightness ?: 0
    def contrast = params.contrast ?: 1
    // we will use this for a crude check to see if we are within decimation levels
    //
    def geomPtr = createModelFromTiePointSet( entry );
    double scaleCheck = 1.0

    if ( ( geomPtr != null ) && params.wmsView && keepWithinScales )
    {
      scaleCheck = params.wmsView.getScaleChangeFromInputToView( geomPtr.get() )
    }
    if ( ( scaleCheck < 0.9 ) && entry )
    {
      // do we have enough zoom levels?
      // check to see if the decimation puts us smaller than the bounding rect of the smallest
      // res level scale
      //
      long maxSize = ( entry.width > entry.height ) ? entry.width : entry.height
      if ( ( maxSize * scaleCheck ) < ( maxSize / ( 2 ** entry.numberOfResLevels ) ) )
      {
        return [chain: null, kwl: "", prefixIdx: 0]
      }
    }

    switch ( quickLookFlagString?.toLowerCase() )
    {
    case "true":
    case "on":
      quickLookFlag = true
      break
    }
    if ( entry )
    {
      // CONSTRUCT HANDLER
      //
      kwlString += "object${objectPrefixIdx}.type:${entry.className ? entry.className : 'ossimImageHandler'}\n"
      kwlString += "object${objectPrefixIdx}.entry:${entry.entryId}\n"
      kwlString += "object${objectPrefixIdx}.filename:${entry.mainFile.name}\n"
      kwlString += "object${objectPrefixIdx}.width:${entry.width}\n"
      kwlString += "object${objectPrefixIdx}.height:${entry.height}\n"
      if ( overviewFile.exists() )
      {
        kwlString += "object${objectPrefixIdx}.overview_file:${overviewFile}\n"
      }
    }
    ++objectPrefixIdx

    // CONSTRUCT BAND SELECTION IF NEEDED
    //
    if ( bands )
    {
      if ( entry )
      {
        if ( validBandSelection( entry.numberOfBands, bands ) )
        {
          // the keywordlist in ossim takes a list of integers surrounded
          // by parenthesis
          //
          kwlString += "object${objectPrefixIdx}.type:ossimBandSelector\n"
            if(bands!="default")
            {
                kwlString += "object${objectPrefixIdx}.bands:(${bands})\n"
            }
          ++objectPrefixIdx
        }
        else
        {
          log.error( "Invalid band selection (${bands}) for image ${entry.id}" )
        }
      }
      else
      {
        def validBands = true
        if ( params.maxBands )
        {
          validBands = validBandSelection( maxBands, bands )
        }
        if ( validBands )
        {
          // the keywordlist in ossim takes a list of integers surrounded
          // by parenthesis
          //
          kwlString += "object${objectPrefixIdx}.type:ossimBandSelector\n"
            if(bands!="default")
            {
                kwlString += "object${objectPrefixIdx}.bands:(${bands})\n"
            }
          ++objectPrefixIdx
        }
      }
    }

    if ( nullFlip )
    {
      kwlString += "object${objectPrefixIdx}.type:ossimNullPixelFlip\n"
      ++objectPrefixIdx
    }
    // CONSTRUCT HISTOGRAM STRETCHING IF NEEDED
    //
    if ( stretchMode && stretchModeRegion )
    {
      if ( ( stretchModeRegion == "global" ) && ( stretchMode != "none" ) )
      {
        if ( histogramFile.exists() )
        {
          kwlString += "object${objectPrefixIdx}.type:ossimHistogramRemapper\n"
          kwlString += "object${objectPrefixIdx}.histogram_filename:${histogramFile}\n"
          kwlString += "object${objectPrefixIdx}.stretch_mode:${stretchMode}\n"
          ++objectPrefixIdx
        }
        else
        {
          log.error( "Histogram file does not exist and will ignore the stretch: ${histogramFile}" )
        }
      }
    }
    // if we are not the identity then add
    if ( ( brightness != 0 ) || ( contrast != 1 ) )
    {
      kwlString += "object${objectPrefixIdx}.type:ossimBrightnessContrastSource\n"
      kwlString += "object${objectPrefixIdx}.brightness: ${brightness ?: 0.0}\n"
      kwlString += "object${objectPrefixIdx}.contrast: ${contrast ?: 0.0}\n"

      ++objectPrefixIdx
    }
    // CONSTRUCT SHARPENING IF NEEDED
    //
    if ( sharpenMode )
    {
      switch ( sharpenMode )
      {
      case "light":
        sharpenSigma = 0.5
        sharpenWidth = 3
        break
      case "heavy":
        sharpenSigma = 1.0
        sharpenWidth = 5.0
        break
      default:
        break
      }

    }
    if ( sharpenSigma && sharpenWidth )
    {
      kwlString += "object${objectPrefixIdx}.type:ossimImageSharpenFilter\n"
      kwlString += "object${objectPrefixIdx}.kernel_sigma:${sharpenSigma}\n"
      kwlString += "object${objectPrefixIdx}.kernel_width:${sharpenWidth}\n"
      ++objectPrefixIdx
    }

    if ( crs )
    {
      //CONSTRUCT IMAGE CACHE
      //
      kwlString += "object${objectPrefixIdx}.type:ossimCacheTileSource\n"
      kwlString += "object${objectPrefixIdx}.tile_size_xy:(64,64)\n"
      kwlString += "object${objectPrefixIdx}.enable_cache:${enableCache}\n"

      ++objectPrefixIdx
      //CONSTRUCT RENDERER
      //
      kwlString += "object${objectPrefixIdx}.type:ossimImageRenderer\n"
      kwlString += "object${objectPrefixIdx}.max_levels_to_compute:0\n"
      kwlString += "object${objectPrefixIdx}.resampler.magnify_type:  ${interpolation}\n"
      kwlString += "object${objectPrefixIdx}.resampler.minify_type:  ${interpolation}\n"
      def kwl = new ossimKeywordlist()
      kwl.add( "object${objectPrefixIdx}.image_view_trans.type", "ossimImageViewProjectionTransform" )
      if ( viewGeom?.get() )
      {
        viewGeom?.get().saveState( kwl, "object${objectPrefixIdx}.image_view_trans.view_geometry." )
      }
      if ( quickLookFlag && entry )
      {
        if ( geomPtr != null )
        {
          geomPtr.get().saveState( kwl, "object${objectPrefixIdx}.image_view_trans.image_geometry." )

        }
        geomPtr.delete()
      }
      kwlString += "${kwl.toString()}\n"
      kwl.delete()
      kwl = null
      ++objectPrefixIdx
      //CONSTRUCT VIEW CACHE
      //
      kwlString += "object${objectPrefixIdx}.type:ossimCacheTileSource\n"
      kwlString += "object${objectPrefixIdx}.tile_size_xy:(64,64)\n"
      kwlString += "object${objectPrefixIdx}.enable_cache:${enableCache}\n"
      ++objectPrefixIdx
    }
    else if ( rotate || scale || pivot )
    {
      //CONSTRUCT IMAGE CACHE
      //
      kwlString += "object${objectPrefixIdx}.type:ossimCacheTileSource\n"
      kwlString += "object${objectPrefixIdx}.enable_cache:${enableCache}\n"

      ++objectPrefixIdx
      //CONSTRUCT RENDERER
      //
      kwlString += "object${objectPrefixIdx}.type:ossimImageRenderer\n"
      kwlString += "object${objectPrefixIdx}.max_levels_to_compute:0\n"
      kwlString += "object${objectPrefixIdx}.resampler.magnify_type:  ${interpolation}\n"
      kwlString += "object${objectPrefixIdx}.resampler.minify_type:  ${interpolation}\n"
      kwlString += "object${objectPrefixIdx}.image_view_trans.type: ossimImageViewAffineTransform\n"
      if ( rotate )
      {
        kwlString += "object${objectPrefixIdx}.image_view_trans.rotate: ${rotate}\n"
      }
      if ( scale )
      {
        kwlString += "object${objectPrefixIdx}.image_view_trans.scale: (${scale},${scale})\n"
      }
      if ( pivot )
      {
        kwlString += "object${objectPrefixIdx}.image_view_trans.pivot: (${pivot})\n"
      }
      //CONSTRUCT VIEW CACHE
      //
      kwlString += "object${objectPrefixIdx}.type:ossimCacheTileSource\n"
      kwlString += "object${objectPrefixIdx}.enable_cache:${enableCache}\n"
      kwlString += "object${objectPrefixIdx}.tile_size_xy:(64,64)\n"
      ++objectPrefixIdx
    }
    else
    {
      //CONSTRUCT image cache depending on if parameters were supplied
      //
      if ( params )
      {
        kwlString += "object${objectPrefixIdx}.type:ossimCacheTileSource\n"
        kwlString += "object${objectPrefixIdx}.enable_cache:${enableCache}\n"
        kwlString += "object${objectPrefixIdx}.tile_size_xy:(64,64)\n"
        ++objectPrefixIdx
      }
      else
      {
        // because this is straight to an image let's just use the default
        // tile size
        kwlString += "object${objectPrefixIdx}.type:ossimCacheTileSource\n"
        ++objectPrefixIdx
      }
    }
    def chain = null
    if ( allocateChain )
    {
      chain = new joms.oms.Chain()
      chain.loadChainKwlString( kwlString )
    }
    [chain: chain, kwl: kwlString, prefixIdx: objectPrefixIdx]
  }

  /**
   * @param params
   * @return A Map that contains the content-type and the chain object
   */
  def createWriterChain( def params, def prefix = "" )
  {
    def requestFormat = params?.format?.toLowerCase()
    def temporaryDirectory = params?.temporaryDirectory
    def tempFilenamePrefix = params?.filenamePrefix ?: "imageChainService"

    def ext = null
    def contentType = null
    def kwlString = ""
    switch ( requestFormat )
    {
    case ~/.*jpeg.*/:
      kwlString += "type:ossimJpegWriter\n"
      kwlString += "create_external_geometry:false\n"
      contentType = "image/jpeg"
      ext = ".jpg"
      break
    case ~/.*tiff.*/:
      kwlString += "type:ossimTiffWriter\n"
      kwlString += "image_type:tiff_tiled\n"
      kwlString += "create_external_geometry:false\n"
      contentType = "image/tiff"
      ext = ".tif"
      break
    case ~/.*jp2.*/:
      kwlString += "type:ossimKakaduJp2Writer\n"
      kwlString += "create_external_geometry:false\n"
      contentType = "image/jp2"
      ext = ".jp2"
      break
    case ~/.*png.*/:
      kwlString += "type:ossimPngWriter\n"
      kwlString += "create_external_geometry:false\n"
      contentType = "image/png"
      ext = ".png"
      break
    default:
      log.error( "Unsupported FORMAT=${requestFormat}" )
      break
    }
    def writer = null
    def outputFileName = null
    def tempFile = null
    if ( ext != null )
    {
      tempFile = File.createTempFile( tempFilenamePrefix, ext, temporaryDirectory ? new File( temporaryDirectory ) : null );
      // now establish a writer
      //
      kwlString += "filename:${tempFile}\n"
      writer = new joms.oms.Chain();
      writer.loadChainKwlString( kwlString )
    }

    return [chain: writer, contentType: contentType, file: tempFile, ext: ext]
  }

  def createModelFromTiePointSet( def entry )
  {
    def gptArray = new ossimGptVector();
    def dptArray = new ossimDptVector();
    if ( entry?.tiePointSet )
    {
      def parser = parserPool.borrowObject()
      def tiepoints = new XmlSlurper( parser ).parseText( entry?.tiePointSet )
      parserPool.returnObject( parser )
      def imageCoordinates = tiepoints.Image.toString().trim()
      def groundCoordinates = tiepoints.Ground.toString().trim()
      def splitImageCoordinates = imageCoordinates.split( " " );
      def splitGroundCoordinates = groundCoordinates.split( " " );
      splitImageCoordinates.each {
        def point = it.split( "," )
        if ( point.size() >= 2 )
        {
          dptArray.add( new ossimDpt( Double.parseDouble( point.getAt( 0 ) ),
                  Double.parseDouble( point.getAt( 1 ) ) ) )
        }
      }
      splitGroundCoordinates.each {
        def point = it.split( "," )
        if ( point.size() >= 2 )
        {
          gptArray.add( new ossimGpt( Double.parseDouble( point.getAt( 1 ) ),
                  Double.parseDouble( point.getAt( 0 ) ) ) )
        }
      }
    }
    else if ( entry?.groundGeom ) // lets do a fall back if the tiepoint set is not set.
    {
      def coordinates = entry?.groundGeom.getCoordinates();
      if ( coordinates.size() >= 4 )
      {
        def w = width as double
        def h = height as double
        ( 0..<4 ).each {
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

  def grabOptimizedImageFromChain( def inputChain, def params )
  {
    def imageSource = new omsImageSource( inputChain.getChainAsImageSource() )
    def renderedImage = new omsRenderedImage( imageSource )
    def image = renderedImage.getData();

    ColorModel colorModel = renderedImage.colorModel

    boolean isRasterPremultiplied = true
    Hashtable<?, ?> properties = null

    def result = null
    def transparentFlag = params?.transparent?.equalsIgnoreCase( "true" )
    if ( image.numBands == 1 )
    {
      result = Utility.convertToColorIndexModel( image.dataBuffer,
              image.width,
              image.height,
              transparentFlag )
    }
    else
    {
      result = new BufferedImage(
              colorModel,
              image,
              isRasterPremultiplied,
              properties
      )
      if ( image.numBands == 3 )
      {
        if ( transparentFlag )
        {
          result = TransparentFilter.fixTransparency( new TransparentFilter(), result )
        }
        if ( params?.format?.equalsIgnoreCase( "image/gif" ) )
        {
          result = ImageGenerator.convertRGBAToIndexed( result )
        }
      }
    }
    result
  }
}
