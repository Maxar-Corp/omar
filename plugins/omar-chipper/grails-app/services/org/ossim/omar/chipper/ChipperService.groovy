package org.ossim.omar.chipper

import org.ossim.omar.chipper.ChipCommand


import org.ossim.omar.raster.RasterEntry
import java.awt.Point
import java.awt.Transparency
import java.awt.color.ColorSpace
import java.awt.image.BufferedImage
import java.awt.image.ComponentColorModel
import java.awt.image.DataBuffer
import java.awt.image.PixelInterleavedSampleModel
import java.awt.image.Raster

import javax.imageio.ImageIO
import javax.media.jai.JAI
import javax.media.jai.PlanarImage
import joms.oms.Chipper
import joms.oms.ElevMgr
import joms.oms.StringVector

class ChipperService
{
  static transactional = false
  def grailsApplication

  enum RenderMode {
    BLANK, CHIPPER
  }


  private Map<String, String> createChipParams(def chpCmd)
  {
    def (minLon, minLat, maxLon, maxLat) = chpCmd?.bbox?.split( ',' )?.collect { it as double }
    def chipperOptionsMap = null
//    def bounds = new Bounds( minLon, minLat, maxLon, maxLat, chpCmd.srs )

//    if ( bounds.geometry.intersects(new Bounds( 0, 0, 90, 90, 'epsg:4326').geometry ) )
//    {
    chipperOptionsMap = [
            cut_min_lon: minLon as String,
            cut_min_lat: minLat as String,
            cut_max_lon: maxLon as String,
            cut_max_lat: maxLat as String,
            cut_height: chpCmd?.height as String,
            cut_width: chpCmd?.width as String,
            'hist-op': 'auto-minmax',
            operation: 'ortho',
            scale_2_8_bit: 'true',
            'srs': chpCmd?.srs,
            three_band_out: 'true'
    ]
//    }

    chpCmd?.layers?.split( ',' )?.eachWithIndex { file, i ->
      chipperOptionsMap["image${i}.file"] = file
    }


    if ( chpCmd['bands'] )
    {
      chipperOptionsMap['bands'] = chpCmd?.bands
    }


    return chipperOptionsMap
  }
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
    def entries   = []
    def layers    = chpCmd?.layers?.split( ',' )
    if(layers)
    {
     // println "************************${layers}"
      layers.each{
        def rasterEntry = RasterEntry.get(it)

        if(rasterEntry)
        {
          filenames << rasterEntry.filename
          entries   << rasterEntry.entryId
        }
      }
    }
    def (minLon, minLat, maxLon, maxLat) = chpCmd?.bbox?.split( ',' )?.collect { it as double }

    def chipperOptionsMap = [
            cut_min_lon: minLon as String,
            cut_min_lat: minLat as String,
            cut_max_lon: maxLon as String,
            cut_max_lat: maxLat as String,
            cut_height:  chpCmd.height as String,
            cut_width:   chpCmd.width as String,
            scale_2_8_bit: 'true',
            srs: chpCmd?.srs,
            'hist-op': 'auto-minmax',
            operation: '2cmv',
            resampler_filter: 'bilinear',
            bands:"0"
    ]

    filenames?.eachWithIndex { file, i ->
      chipperOptionsMap["image${i}.file"]  = file
      chipperOptionsMap["image${i}.entry"] = entries[i].toString()
    }

    return chipperOptionsMap
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

  def getChip(ChipCommand chpCmd)
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
        Map<String, String> chipperOptionsMap = createChipParams( chpCmd )

        if ( chipperOptionsMap )
        {
          if ( !populateTile( chipperOptionsMap, chpCmd, ostream ) )
          {
            createBlankTile( chpCmd, ostream )
          }
        }
        break

    // End: case RenderMode.CHIPPER:

    } // End: switch( renderMode

    [contentType: chpCmd?.format, buffer: ostream.toByteArray()]
  }

  private void createBlankTile(ChipCommand chpCmd, ByteArrayOutputStream ostream)
  {
    def image = new BufferedImage(
            chpCmd?.width, chpCmd?.height, BufferedImage.TYPE_INT_ARGB )
    ImageIO.write( image, chpCmd?.format?.split( '/' )[-1], ostream )
  }
  private def fixImageForOutputFormat(def inputImage, def outputFormat)
  {
    def image = inputImage
    switch(outputFormat.toLowerCase())
    {
      case "jpg":
      case "jpeg":
        if(inputImage.sampleModel.numBands > 3)
        {
          def threeBand = JAI.create("BandSelect", image, [0,1,2] as int[])
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
          image = fixImageForOutputFormat(image, outputFormat)
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
} // End: class ChipperService
