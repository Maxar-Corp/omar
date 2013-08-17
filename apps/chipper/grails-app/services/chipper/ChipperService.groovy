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

class ChipperService
{
  enum RenderMode {
    BLANK, CHIPPER
  }

  def getChip(def chpCmd)
  {
    // println chpCmd

    // def renderMode = RenderMode.BLANK
    def renderMode = RenderMode.CHIPPER
    def ostream = new ByteArrayOutputStream()

    switch ( renderMode )
    {
    case RenderMode.BLANK:
      def image = new BufferedImage(
          chpCmd?.width, chpCmd?.height, BufferedImage.TYPE_INT_ARGB )
      ImageIO.write( image, chpCmd?.format.split( '/' )[-1], ostream )
      break

    case RenderMode.CHIPPER:

      joms.oms.Chipper chipper = new joms.oms.Chipper()

      def bboxArray = chpCmd?.bbox.split( ',' )
      if ( bboxArray.size() == 4 )
      {
        int w = chpCmd?.width
        int h = chpCmd?.height
        int sizeInPixels = w * h

        if ( sizeInPixels )
        {
          def chipperOptionsMap = [
              'cut_min_lon': bboxArray[0],
              'cut_min_lat': bboxArray[1],
              'cut_max_lon': bboxArray[2],
              'cut_max_lat': bboxArray[3],
              'cut_height': h as String,
              'cut_width': w as String,
              // 'hist-op'         : 'auto-minmax',
              //'image0.file': '/data/bmng/world.200406.A1.tif', // Temp hard coded.
              'image0.file': '/data/celtic/staged/001/celtic/rpf_cadrg_1060889007_48858/a.toc',
              'operation': 'ortho',
              'scale_2_8_bit': 'true',
              'src': 'chpCmp?.srs',
              'three_band_out': 'true'
          ]

          // println "calling chipper.initialize( myMap )"
          if ( chipper.initialize( chipperOptionsMap ) )
          {
            def sampleModel = new PixelInterleavedSampleModel(
                DataBuffer.TYPE_BYTE,
                w,                     // width
                h,                     // height
                4,                     // pixelStride
                w * 4,                 // scanlineStride
                [0, 1, 2, 3] as int[]  // band offsets
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
            }
          }
        }
      }

      chipper.delete()
      break

    // End: case RenderMode.CHIPPER:

    } // End: switch( renderMode

    [contentType: chpCmd?.format, buffer: ostream.toByteArray()]

  } // End: def getChip(def chpCmd)

} // End: class ChipperService
