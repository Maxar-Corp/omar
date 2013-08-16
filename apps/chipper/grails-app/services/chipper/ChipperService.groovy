package chipper

import java.awt.image.DataBuffer
import java.awt.image.DataBufferByte
import java.util.Map
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.awt.image.Raster
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
          def chipperOptionsMap =
            ['cut_min_lon': bboxArray[0],
                'cut_min_lat': bboxArray[1],
                'cut_max_lon': bboxArray[2],
                'cut_max_lat': bboxArray[3],
                'cut_height': h as String,
                'cut_width': w as String,
                // 'hist-op'         : 'auto-minmax',
//                    'image0.file'     : '/data1/bmng/world_200406.tif', // Temp hard coded.
                'image0.file': '/data/bmng/world.200406.A1.tif', // Temp hard coded.
                'operation': 'ortho',
                'scale_2_8_bit': 'true',
                'src': 'chpCmp?.srs',
                'three_band_out': 'true'
            ]

          // println "calling chipper.initialize( myMap )"
          if ( chipper.initialize( chipperOptionsMap ) )
          {
            def sizeInBytes = sizeInPixels * 4

            def image = new BufferedImage( w, h, BufferedImage.TYPE_4BYTE_ABGR )
            byte[] data = ( (DataBufferByte)image.raster.dataBuffer ).data

            if ( chipper.getChip( data, true ) )
            {
              // println "chipper.getChip good..."

              int i = 0;
              for ( int y = 0; y < h; y++ ) // line loop
              {
                for ( int x = 0; x < w; x++ ) // Sample loop
                {
                  int argb = ( ( data[i] & 0xFF ) << 8 ) | ( ( data[i + 1] & 0xFF ) << 16 ) |
                      ( ( data[i + 2] & 0xFF ) << 24 ) | ( ( data[i + 3] & 0xFF ) << 0 )

                  image.setRGB( x, y, argb )
                  i += 4
                }
              }

              ImageIO.write( image, chpCmd?.format.split( '/' )[-1], ostream )
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
