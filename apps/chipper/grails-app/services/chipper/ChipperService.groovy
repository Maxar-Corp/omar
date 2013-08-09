package chipper

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

class ChipperService
{
  enum RenderMode {
    BLANK, CHIPPER
  }

  def getChip(def chpCmd)
  {
    println chpCmd

    def renderMode = RenderMode.BLANK
    def ostream = new ByteArrayOutputStream()

    switch ( renderMode )
    {
    case RenderMode.BLANK:
      def image = new BufferedImage( chpCmd?.width, chpCmd?.height, BufferedImage.TYPE_INT_ARGB )

      ImageIO.write( image, chpCmd?.format.split( '/' )[-1], ostream )
      break
    case RenderMode.CHIPPER:
      break
    }

    [contentType: chpCmd?.format, buffer: ostream.toByteArray()]
  }
}
