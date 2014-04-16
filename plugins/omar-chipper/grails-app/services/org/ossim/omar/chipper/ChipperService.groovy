package org.ossim.omar.chipper

import geoscript.geom.Bounds
import org.ossim.omar.chipper.ChipCommand


import org.ossim.omar.raster.RasterEntry
import org.ossim.omar.core.TransparentFilter

import java.awt.Point
import java.awt.Transparency
import java.awt.color.ColorSpace
import java.awt.image.BufferedImage
import java.awt.image.ComponentColorModel
import java.awt.image.DataBuffer
import java.awt.image.DataBufferByte
import java.awt.image.PixelInterleavedSampleModel
import java.awt.image.Raster

import javax.imageio.ImageIO
import javax.media.jai.JAI
import javax.media.jai.PlanarImage
import joms.oms.Chipper
import joms.oms.ElevMgr
import joms.oms.StringVector

import java.awt.image.RenderedImage

class ChipperService
{
  static transactional = false
  def grailsApplication

  enum RenderMode {
    BLANK, CHIPPER
  }


  def defaultHillShadeOpts = [
      azimuthAngle   : 270,
      colorBlue      : 139,
      colorGreen     : 26,
      colorRed       : 85,
      elevationAngle : 45,
      gain           : 2.5,
      resamplerFilter: 'cubic',
      writer         : 'ossim_png'
  ]
/*
  private Map<String, String> createPanSharpenParams(ChipCommand chpCmd)
  {
    def layers = chpCmd?.layers?.split( ',' )
    def (minLon, minLat, maxLon, maxLat) = chpCmd?.bbox?.split( ',' )?.collect { it as double }

    def chipperOptionsMap = [
            cut_min_lon: minLon as String,
            cut_min_lat: minLat as String,
            cut_max_lon: maxLon as String,
            cut_max_lat: maxLat as String,
            cut_height: chpCmd.height as String,
            cut_width: chpCmd.width as String,
            scale_2_8_bit: 'true',
            srs: chpCmd?.srs,
            'hist-op': 'auto-minmax',
            operation: 'psm',
            resampler_filter: 'sinc'
    ]

    chpCmd?.layers?.split( ',' )?.eachWithIndex { file, i ->
      chipperOptionsMap["image${i}.file"] = file
    }


    if ( chpCmd['bands'] )
    {
      chipperOptionsMap['bands'] = chpCmd?.bands
    }

    return chipperOptionsMap
  }
*/

  private Map<String, String> createTwoColorMultiParams(def chpCmd)
  {
    // println "createTwoColorMultiParams: entered............."
    def filenames = []
    def entries = []
    def newLayers = chpCmd?.new_layers?.split( ',' )
    def oldLayers = chpCmd?.old_layers?.split( ',' )
    def layers = []

    // later we will support 2 different layers for now merge them back in
    //
    newLayers.each { layers << it }
    oldLayers.each { layers << it }
    if ( layers )
    {
      // println "************************${layers}"
      layers.each {
        def rasterEntry = RasterEntry.get( it )

        if ( rasterEntry )
        {
          filenames << rasterEntry.filename
          entries << rasterEntry.entryId
        }
      }
    }
    def (minLon, minLat, maxLon, maxLat) = chpCmd?.bbox?.split( ',' )?.collect { it as double }

    def chipperOptionsMap = [
        cut_min_lon     : minLon as String,
        cut_min_lat     : minLat as String,
        cut_max_lon     : maxLon as String,
        cut_max_lat     : maxLat as String,
        cut_height      : chpCmd.height as String,
        cut_width       : chpCmd.width as String,
        scale_2_8_bit   : 'true',
        srs             : chpCmd?.srs,
        'hist-op'       : 'auto-minmax',
        operation       : '2cmv',
        resampler_filter: 'bilinear',
        bands           : "0"
    ]

    filenames?.eachWithIndex { file, i ->
      chipperOptionsMap["image${i}.file"] = file
      chipperOptionsMap["image${i}.entry"] = entries[i].toString()
    }

    return chipperOptionsMap
  }

  def getHillShade(def params)
  {
//    println '*' * 40
//    println params
//    println '*' * 40

    def inputParams = [
        mapImage       : RasterEntry.read( params.layers as Long ),
        azimuthAngle   : ( params.azimuthAngle ?: defaultHillShadeOpts.azimuthAngle ) as Double,
        colorBlue      : ( params.colorBlue ?: defaultHillShadeOpts.colorBlue ) as Integer,
        colorGreen     : ( params.colorGreen ?: defaultHillShadeOpts.colorGreen ) as Integer,
        colorRed       : ( params.colorRed ?: defaultHillShadeOpts.colorRed ) as Integer,
        elevationAngle : ( params.elevationAngle ?: defaultHillShadeOpts.elevationAngle ) as Double,
        gain           : ( params.gain ?: defaultHillShadeOpts.gain ) as Double,
        resamplerFilter: params.resamplerfilter ?: defaultHillShadeOpts.resampleFilter,
        writer         : params.writer ?: defaultHillShadeOpts.writer
    ]

//    println "@" * 40
//    println inputParams
//    println "@" * 40

    def type = 'png'
    def (minX, minY, maxX, maxY) = params.bbox.split( ',' ).collect { it as double }
    def bbox = new Bounds( minX, minY, maxX, maxY, params.srs )
    def ostream = new ByteArrayOutputStream()

    def outputParams = [
        bbox       : bbox,
        size       : [width: params.width as Integer, height: params.height as Integer],
        type       : type,
        output     : ostream,
        transparent: true
    ]

    createHillShade( inputParams, outputParams )

    [contentType: "image/${type}", content: ostream.toByteArray()]
  }

  def createHillShade(def inputParams, def outputParams)
  {
    def opts = [
        operation       : 'hillshade',

        cut_min_lon     : outputParams.bbox?.minX as String,
        cut_min_lat     : outputParams.bbox?.minY as String,
        cut_max_lon     : outputParams.bbox?.maxX as String,
        cut_max_lat     : outputParams.bbox?.maxY as String,
        cut_height      : ( outputParams.size?.height as Integer ) as String,
        cut_width       : ( outputParams.size?.width as Integer ) as String,
        srs             : outputParams.bbox?.proj?.id,


        azimuth_angle   : inputParams.azimuthAngle as String,
        color_blue      : inputParams.colorBlue as String,
        color_green     : inputParams.colorGreen as String,
        color_red       : inputParams.colorRed as String,
        elevation_angle : inputParams.elevationAngle as String,
        gain            : inputParams.gain as String,

        scale_2_8_bit   : 'true',
        'hist-op'       : 'auto-minmax',
        resampler_filter: 'bilinear',

        'image0.file'   : inputParams.mapImage?.filename,
        'image0.entry'  : inputParams.mapImage?.entryId,
    ]

    def dems = findElevationCells(
        grailsApplication?.config?.chipper?.hillShade?.elevationPath as String,
        outputParams.bbox )

    println "dems: ${dems}"

    dems?.eachWithIndex { file, index -> opts["dem${index}.file"] = file }

    runChipper( opts, outputParams )
  }

  def findElevationCells(String path, def bounds)
  {
    def cells = new StringVector()
    def filenames = []

    ElevMgr.instance().getCellsForBounds( path, bounds.minY, bounds.minX, bounds.maxY, bounds.maxX, cells )

    for ( x in ( 0..<cells?.size() ) )
    {
      filenames << cells?.get( x as int )
    }

    return filenames
  }

/*
  private Map<String, String> createHillShadeParams(ChipCommand chpCmd)
  {
    def layers = chpCmd?.layers?.split( ',' )
    def (minLon, minLat, maxLon, maxLat) = chpCmd?.bbox?.split( ',' )?.collect { it as double }
    def bounds = new Bounds( minLon, minLat, maxLon, maxLat )

    def chipperOptionsMap = [

            azimuth_angle: chpCmd.azimuth_angle ?: '270',
            color_blue: chpCmd.color_blue ?: '139',
            color_green: chpCmd.color_green ?: '26',
            color_red: chpCmd.color_red ?: '85',

            cut_min_lon: minLon as String,
            cut_min_lat: minLat as String,
            cut_max_lon: maxLon as String,
            cut_max_lat: maxLat as String,
            cut_height: chpCmd.height as String,
            cut_width: chpCmd.width as String,
            elevation_angle: chpCmd.elevation_angle ?: '45',
            gain: chpCmd.gain ?: '1.5',
//        meters:  '20',
            operation: 'hillshade',
//        output_file:  '/data1/pmr_20131209/outputs/hillshade.png',
            output_radiometry: 'U8',
//        projection:  'geo-scaled',
//        projection:  'geo',
            srs: chpCmd.srs,
            resampler_filter: chpCmd.resampler_filter ?: 'cubic',
            writer: chpCmd.writer ?: 'ossim_png'
    ]

    // Add DEMs

    def dems = findElevationCells( grailsApplication?.config?.chipper?.hillShade?.elevationPath as String, bounds )

    dems?.eachWithIndex { file, index -> chipperOptionsMap["dem${index}.file"] = file }

    // Add Images
    chpCmd?.layers?.split( ',' )?.eachWithIndex { file, i ->
      chipperOptionsMap["image${i}.file"] = file
    }

    return chipperOptionsMap
  }
*/

//  def getChip(ChipCommand chpCmd)
//  {
//    // println chpCmd
//
//    // def renderMode = RenderMode.BLANK
//    def renderMode = RenderMode.CHIPPER
//    def ostream = new ByteArrayOutputStream()
//
//    switch ( renderMode )
//    {
//    case RenderMode.BLANK:
//      createBlankTile( chpCmd, ostream )
//      break
//
//    case RenderMode.CHIPPER:
//      Map<String, String> chipperOptionsMap = createChipParams( chpCmd )
//
//      if ( chipperOptionsMap )
//      {
//        if ( !populateTile( chipperOptionsMap, chpCmd, ostream ) )
//        {
//          createBlankTile( chpCmd, ostream )
//        }
//      }
//      break
//
//    // End: case RenderMode.CHIPPER:
//
//    } // End: switch( renderMode
//
//    [contentType: chpCmd?.format, buffer: ostream.toByteArray()]
//  }

  private void createBlankTile(ChipCommand chpCmd, ByteArrayOutputStream ostream)
  {
    def image = new BufferedImage(
        chpCmd?.width, chpCmd?.height, BufferedImage.TYPE_INT_ARGB )
    ImageIO.write( image, chpCmd?.format?.split( '/' )[-1], ostream )
  }

  private def fixImageForOutputFormat(def inputImage, def outputFormat)
  {
    def image = inputImage
    switch ( outputFormat.toLowerCase() )
    {
    case "jpg":
    case "jpeg":
      if ( inputImage.sampleModel.numBands > 3 )
      {
        def threeBand = JAI.create( "BandSelect", image, [0, 1, 2] as int[] )
        outputFormat = "jpeg"
        image = threeBand
      }
      break
    default:
      break;
    }
    image
  }

  private boolean populateTile(chipperOptionsMap, ChipCommand chpCmd, ByteArrayOutputStream ostream)
  {
    Chipper chipper = new Chipper()
    boolean status = false

//    def numBands = chipperOptionsMap?.bands?.split(',')?.size() ?: 3
    def numBands = 4
    def mask = ( ( 0..<numBands ).collect { 8 } ) as int[]

    //println "${numBands} ${mask}"

    try
    {
      // println "calling chipper.initialize( ${chipperOptionsMap} )"

      if ( chipper.initialize( chipperOptionsMap ) )
      {
        def sampleModel = new PixelInterleavedSampleModel(
            DataBuffer.TYPE_BYTE,
            chpCmd.width,             // width
            chpCmd.height,            // height
            numBands,                 // pixelStride
            chpCmd.width * numBands,  // scanlineStride
            ( 0..<numBands ) as int[] // band offsets
        )

        def dataBuffer = sampleModel.createDataBuffer()

        if ( chipper.getChip( dataBuffer.data, true ) )
        {
          // println "chipper.getChip good..."

          def cs = ColorSpace.getInstance( ColorSpace.CS_sRGB )

          def colorModel = new ComponentColorModel( cs, mask,
              true, false, Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE )

          def raster = Raster.createRaster( sampleModel, dataBuffer, new Point( 0, 0 ) )
          def image = new BufferedImage( colorModel, raster, false, null )
          def outputFormat = chpCmd?.format?.split( '/' )[-1];
          image = fixImageForOutputFormat( image, outputFormat )
          ImageIO.write( image, outputFormat, ostream )

          status = true
        }
      }
    }
    catch ( e )
    {
      e.printStackTrace()
    }
    finally
    {
      chipper.delete()
    }

    return status
  } // End: def getChip(def chpCmd)

  /*
  def getPSM(ChipCommand chpCmd)
  {
    // println chpCmd

    // def renderMode = RenderMode.BLANK
    def renderMode = RenderMode.CHIPPER
    def ostream = new ByteArrayOutputStream()

    switch ( renderMode )
    {
      case RenderMode.BLANK:
        createBlankTile( chpCmd, ostream )
        break

      case RenderMode.CHIPPER:
        Map<String, String> chipperOptionsMap = createPanSharpenParams( chpCmd )

        if ( chipperOptionsMap )
        {
          if ( !populateTile( chipperOptionsMap, chpCmd, ostream ) )
          {
            createBlankTile( chpCmd, ostream )
          }
          break
          // End: case RenderMode.CHIPPER:

        } // End: switch( renderMode
    }
    [contentType: chpCmd?.format, buffer: ostream.toByteArray()]
  } // End: def getPSM(def chpCmd)
*/

  def get2CMV(ChipCommand chpCmd)
  {
    // println chpCmd

    //def renderMode = RenderMode.BLANK
    def renderMode = RenderMode.CHIPPER
    def ostream = new ByteArrayOutputStream()

    switch ( renderMode )
    {
    case RenderMode.BLANK:
      createBlankTile( chpCmd, ostream )
      break

    case RenderMode.CHIPPER:
      Map<String, String> chipperOptionsMap = createTwoColorMultiParams( chpCmd )

      if ( chipperOptionsMap )
      {
        if ( !populateTile( chipperOptionsMap, chpCmd, ostream ) )
        {
          createBlankTile( chpCmd, ostream )
        }
        break
        // End: case RenderMode.CHIPPER:

      } // End: switch( renderMode
    }
    [contentType: chpCmd?.format, buffer: ostream.toByteArray()]
  } // End: def get2CMV(def chpCmd)

  /*
   def getHillShade(ChipCommand chpCmd)
   {
     println chpCmd

     // def renderMode = RenderMode.BLANK
     def renderMode = RenderMode.CHIPPER
     def ostream = new ByteArrayOutputStream()

     switch ( renderMode )
     {
       case RenderMode.BLANK:
         createBlankTile( chpCmd, ostream )
         break

       case RenderMode.CHIPPER:
         Map<String, String> chipperOptionsMap = createHillShadeParams( chpCmd )

         if ( chipperOptionsMap )
         {
           if ( !populateTile( chipperOptionsMap, chpCmd, ostream ) )
           {
             createBlankTile( chpCmd, ostream )
           }
           break
           // End: case RenderMode.CHIPPER:

         } // End: switch( renderMode
     }
     [contentType: chpCmd?.format, buffer: ostream.toByteArray()]
   } // End: def getHillShade(def chpCmd)
   */
/*
  def findElevationCells(String path, Bounds bounds)
  {
    def cells = new StringVector()
    def filenames = []

    ElevMgr.instance().getCellsForBounds( path, bounds.minY, bounds.minX, bounds.maxY, bounds.maxX, cells )

    for ( x in ( 0..<cells?.size() ) )
    {
      filenames << cells?.get( x as int )
    }

    return filenames
  }
  */

  def getThumbnail(def params)
  {
    def rasterEntry = RasterEntry.read( params.id )

    def inputParams = [
        filename: rasterEntry.filename,
        entryId : rasterEntry.entryId
    ]

    def type = 'jpeg'
    def ostream = new ByteArrayOutputStream()

    def outputParams = [
        size       : [width: ( params.size as Integer ) ?: 128, height: ( params.size as Integer ) ?: 128],
        type       : type,
        output     : ostream,
        transparent: false
    ]

    createThumbnail( inputParams, outputParams )

    [contentType: "image/${type}", content: ostream.toByteArray()]
//
//    // HACK - Need a better way to determine default bands
//    if ( rasterEntry.numberOfBands > 3 )
//    {
//      chipperOptionsMap.bands = '3,2,1'
//    }
  }

  def createThumbnail(def inputParams, def outputParams)
  {
    def opts = [
        operation           : 'ortho',
        thumbnail_resolution: outputParams.size.width as String,
        'hist-op'           : 'auto-minmax',
        srs                 : 'epsg:4326',
        three_band_out      : 'true',
        'image0.file'       : inputParams.filename,
        'image0.entry'      : inputParams.entryId,
        pad_thumbnail       : 'true',
        output_radiometry   : 'U8'
    ]

    runChipper( opts, outputParams )
  }

  def getChip(def params)
  {
    def inputParams

    if ( params.layers )
    {
      inputParams = [
          layers: [
              RasterEntry.read( params.layers )
          ]
      ]
    }
    else if ( params.filename )
    {
      inputParams = [
          layers: [
              [filename: params.filename, entryId: '0']
          ]
      ]
    }

    def type = 'png'
    def (minX, minY, maxX, maxY) = params.bbox.split( ',' ).collect { it as double }
    def bbox = new Bounds( minX, minY, maxX, maxY, params.srs )
    def ostream = new ByteArrayOutputStream()

    def outputParams = [
        bbox       : bbox,
        size       : [width: params.width as Integer, height: params.height as Integer],
        type       : type,
        output     : ostream,
        transparent: true
    ]

    createChip( inputParams, outputParams )

    [contentType: "image/${type}", content: ostream.toByteArray()]
  }

  def createChip(def inputParams, def outputParams)
  {
    def opts = [
        operation     : 'ortho',
        cut_min_lon   : outputParams?.bbox.minX as String,
        cut_min_lat   : outputParams?.bbox.minY as String,
        cut_max_lon   : outputParams?.bbox.maxX as String,
        cut_max_lat   : outputParams?.bbox.maxY as String,
        cut_height    : outputParams?.size.height as String,
        cut_width     : outputParams?.size.width as String,
        'hist-op'     : 'auto-minmax',
        scale_2_8_bit : 'true',
        srs           : outputParams?.bbox?.proj?.id,
        three_band_out: 'true'

    ]


    inputParams?.layers?.eachWithIndex { image, i ->
      opts["image${i}.file"] = image.filename
      opts["image${i}.entry"] = image.entryId
    }

    runChipper( opts, outputParams )
  }

  def get2CMV(def params)
  {
    def inputParams = [
        redImage : RasterEntry.read( params.redImage ),
        blueImage: RasterEntry.read( params.blueImage )
    ]

    def type = 'png'
    def ostream = new ByteArrayOutputStream()
    def (minX, minY, maxX, maxY) = params.bbox.split( ',' ).collect { it as double }
    def bbox = new Bounds( minX, minY, maxX, maxY, params.srs )

    def outputParams = [
        bbox       : bbox,
        size       : [width: params.width as Integer, height: params.height as Integer],
        type       : type,
        output     : ostream,
        transparent: true
    ]

    create2CMV( inputParams, outputParams )

    [contentType: "image/${type}", content: ostream.toByteArray()]
  }

  def create2CMV(def inputParams, def outputParams)
  {
    def opts = [
        operation       : '2cmv',
        cut_min_lon     : outputParams.bbox?.minX as String,
        cut_min_lat     : outputParams.bbox?.minY as String,
        cut_max_lon     : outputParams.bbox?.maxX as String,
        cut_max_lat     : outputParams.bbox?.maxY as String,
        cut_height      : ( outputParams.size?.height as Integer ) as String,
        cut_width       : ( outputParams.size?.width as Integer ) as String,
        scale_2_8_bit   : 'true',
        srs             : outputParams.bbox?.proj?.id,
        'hist-op'       : 'auto-minmax',
        resampler_filter: 'bilinear',
        'image0.file'   : inputParams.redImage?.filename,
        'image0.entry'  : inputParams.redImage?.entryId,
        'image1.file'   : inputParams.blueImage?.filename,
        'image1.entry'  : inputParams.blueImage?.entryId,
        bands           : '0'
    ]

    runChipper( opts, outputParams )
  }

  def getPSM(def params)
  {
    def inputParams = [
        colorImage: RasterEntry.read( params.colorImage ),
        panImage  : RasterEntry.read( params.panImage )
    ]

    def type = 'png'
    def ostream = new ByteArrayOutputStream()
    def (minX, minY, maxX, maxY) = params.bbox.split( ',' ).collect { it as double }
    def bbox = new Bounds( minX, minY, maxX, maxY, params.srs )

    def outputParams = [
        bbox       : bbox,
        size       : [width: params.width as Integer, height: params.height as Integer],
        type       : type,
        output     : ostream,
        transparent: true
    ]

    createPSM( inputParams, outputParams )
    [contentType: "image/${type}", content: ostream.toByteArray()]
  }

  def createPSM(def inputParams, def outputParams)
  {
    def opts = [
        operation       : 'psm',
        cut_min_lon     : outputParams.bbox?.minX as String,
        cut_min_lat     : outputParams.bbox?.minY as String,
        cut_max_lon     : outputParams.bbox?.maxX as String,
        cut_max_lat     : outputParams.bbox?.maxY as String,
        cut_height      : ( outputParams.size?.height as Integer ) as String,
        cut_width       : ( outputParams.size?.width as Integer ) as String,
        scale_2_8_bit   : 'true',
        srs             : outputParams.bbox?.proj?.id,
        'hist-op'       : 'auto-minmax',
        resampler_filter: 'bilinear',
        'image0.file'   : inputParams.colorImage?.filename,
        'image0.entry'  : inputParams.colorImage?.entryId,
        'image1.file'   : inputParams.panImage?.filename,
        'image1.entry'  : inputParams.panImage?.entryId,
        bands           : '3,2,1'
    ]

    runChipper( opts, outputParams )
  }

  private synchronized def runChipper(def opts, def outputParams)
  {
    println opts

    def numBands = ( outputParams.transparent ) ? 4 : 3
    def buffer = new byte[outputParams.size.width * outputParams.size.height * numBands]
    def chipper = new Chipper()

    if ( chipper.initialize( opts ) )
    {
      println 'initialize: good'
      if ( chipper.getChip( buffer, outputParams.transparent ) > 1 )
      {
        println 'getChip: good'
      }
      else
      {
        println 'getChip: bad'
      }
    }
    else
    {
      println 'initialize: bad'
    }

    def dataBuffer = new DataBufferByte( buffer, buffer.size() )

    def sampleModel = new PixelInterleavedSampleModel(
        DataBuffer.TYPE_BYTE,
        outputParams.size.width,               // width
        outputParams.size.height,               // height
        numBands,                        // pixelStride
        outputParams.size.width * numBands,    // scanlineStride
        ( 0..<numBands ) as int[]        // band offsets
    )

    def cs = ColorSpace.getInstance( ColorSpace.CS_sRGB )
    def mask = ( ( 0..<sampleModel.numBands ).collect { 8 } ) as int[]

    def colorModel = new ComponentColorModel( cs, mask,
        outputParams.transparent, false, ( outputParams.transparent ) ? Transparency.TRANSLUCENT : Transparency.OPAQUE,
        DataBuffer.TYPE_BYTE )

    def raster = Raster.createRaster( sampleModel, dataBuffer, new Point( 0, 0 ) )
    def image = new BufferedImage( colorModel, raster, false, null )

//    if ( outputParams.transparent )
//    {
//      println 'running filter'
//      image = TransparentFilter.fixTransparency( new TransparentFilter(), image )
//    }


    ImageIO.write( image, outputParams.type, outputParams.output )
  }

} // End: class ChipperService
