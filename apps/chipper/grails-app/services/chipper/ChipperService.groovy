package chipper

import java.awt.Point
import java.awt.Transparency
import java.awt.color.ColorSpace
import java.awt.image.BufferedImage
import java.awt.image.ComponentColorModel
import java.awt.image.DataBuffer
import java.awt.image.PixelInterleavedSampleModel
import java.awt.image.Raster

import javax.imageio.ImageIO
import joms.oms.Chipper

import geoscript.geom.Bounds

class ChipperService
{
  static transactional = false

  enum RenderMode {
    BLANK, CHIPPER
  }


  private Map<String, String> createChipParams(ChipCommand chpCmd)
  {
    def (minLon, minLat, maxLon, maxLat) = chpCmd?.bbox?.split( ',' )?.collect { it as double }
    def chipperOptionsMap = null
    def bounds = new Bounds( minLon, minLat, maxLon, maxLat, chpCmd.srs )

//    if ( bounds.geometry.intersects(new Bounds( 0, 0, 90, 90, 'epsg:4326').geometry ) )
//    {
    chipperOptionsMap = [
        'cut_min_lon': minLon as String,
        'cut_min_lat': minLat as String,
        'cut_max_lon': maxLon as String,
        'cut_max_lat': maxLat as String,
        'cut_height': chpCmd?.height as String,
        'cut_width': chpCmd?.width as String,
        'hist-op': 'auto-minmax',
        'image0.file': chpCmd.layers,
        'operation': 'ortho',
        'scale_2_8_bit': 'true',
        'src': chpCmd?.srs,
        'three_band_out': 'true'
    ]
//    }


    if ( chpCmd['bands'] )
    {
      chipperOptionsMap['bands'] = chpCmd?.bands
    }


    return chipperOptionsMap
  }

  private Map<String, String> createPanSharpenParams(ChipCommand chpCmd)
  {
    def layers = chpCmd?.layers?.split( ',' )
    def (minLon, minLat, maxLon, maxLat) = chpCmd?.bbox?.split( ',' )?.collect { it as double }

    def chipperOptionsMap = [
        'cut_min_lon': minLon as String,
        'cut_min_lat': minLat as String,
        'cut_max_lon': maxLon as String,
        'cut_max_lat': maxLat as String,
        'cut_height': chpCmd.height as String,
        'cut_width': chpCmd.width as String,
        'scale_2_8_bit': 'true',
        'src': chpCmd?.srs,
        'hist-op': 'auto-minmax',
        'image0.file': layers[0],
        'image1.file': layers[1],

        operation: 'psm',
        resampler_filter: 'sinc'
    ]

    if ( chpCmd['bands'] )
    {
      chipperOptionsMap['bands'] = chpCmd?.bands
    }

    return chipperOptionsMap
  }

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

  private boolean populateTile(chipperOptionsMap, ChipCommand chpCmd, ByteArrayOutputStream ostream)
  {
    Chipper chipper = new Chipper()
    boolean status = false

    try
    {
      println "calling chipper.initialize( ${chipperOptionsMap} )"

      if ( chipper.initialize( chipperOptionsMap ) )
      {
        def sampleModel = new PixelInterleavedSampleModel(
            DataBuffer.TYPE_BYTE,
            chpCmd.width,         // width
            chpCmd.height,        // height
            4,                    // pixelStride
            chpCmd.width * 4,     // scanlineStride
            [0, 1, 2, 3] as int[] // band offsets
        )

        def dataBuffer = sampleModel.createDataBuffer()

        if ( chipper.getChip( dataBuffer.data, true ) )
        {
          // println "chipper.getChip good..."

          def cs = ColorSpace.getInstance( ColorSpace.CS_sRGB )

          def colorModel = new ComponentColorModel( cs, [8, 8, 8, 8] as int[],
              true, false, Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE )

          def raster = Raster.createRaster( sampleModel, dataBuffer, new Point( 0, 0 ) )
          def image = new BufferedImage( colorModel, raster, false, null )

          ImageIO.write( image, chpCmd?.format?.split( '/' )[-1], ostream )
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

} // End: class ChipperService