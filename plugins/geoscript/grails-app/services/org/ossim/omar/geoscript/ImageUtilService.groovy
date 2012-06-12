package org.ossim.omar.geoscript

import java.awt.image.BufferedImage
import java.awt.Color
import java.awt.image.IndexColorModel

class ImageUtilService
{
  def createIndexedImage( def width, def height )
  {
    def cm = createIndexColorModel()
    def im = new BufferedImage( width, height, BufferedImage.TYPE_BYTE_INDEXED, cm )
    def g = im.createGraphics()

    g.setColor( new Color( 0, 0, 0, 0 ) ) //transparent
    g.fillRect( 0, 0, width, height )

    g.setColor( Color.RED )
    g.fillRect( 0, 0, width / 2 as int, height / 2 as int )
    g.setColor( Color.GREEN )
    g.fillRect( width / 2 as int, height / 2 as int, width, height )

    g.dispose()
    return im
  }

  def convertToIndexImage( def image )
  {
    def cm = createIndexColorModel()
    def im = new BufferedImage( image.width, image.height, BufferedImage.TYPE_BYTE_INDEXED, cm )
    def g = im.createGraphics()

    g.setColor( new Color( 0, 0, 0, 0 ) ) //transparent
    g.fillRect( 0, 0, image.width, image.height )

    g.drawImage( image, 0, 0, null )

    g.dispose()

    return im
  }


  def createIndexColorModel( )
  {
    def ex = new BufferedImage( 1, 1, BufferedImage.TYPE_BYTE_INDEXED )
    def icm = ex.getColorModel()
    int SIZE = 256
    byte[] r = new byte[SIZE]
    byte[] g = new byte[SIZE]
    byte[] b = new byte[SIZE]
    byte[] a = new byte[SIZE]
    icm.getReds( r )
    icm.getGreens( g )
    icm.getBlues( b )
    java.util.Arrays.fill( a, (byte)255 )
    r[0] = g[0] = b[0] = a[0] = 0 //transparent
    return new IndexColorModel( 8, SIZE, r, g, b, a )
  }

}
