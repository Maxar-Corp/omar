package org.ossim.omar.core;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: sbortman
 * Date: Mar 3, 2009
 * Time: 12:58:53 PM
 * To change this template use File | Settings | File Templates.
 */

public class TransparentFilter extends AbstractFilter
{
  protected Color transparentColor;
  protected Color backgroundColor;

  public TransparentFilter()
  {
    this( Color.black, Color.black );
  }

  public TransparentFilter( Color transparentColor )
  {
    this( transparentColor, Color.black );
  }

  public TransparentFilter( Color transparentColor, Color backgroundColor )
  {
    this.transparentColor = transparentColor;
    this.backgroundColor = backgroundColor;
  }

  public BufferedImage filter( BufferedImage src, BufferedImage dst )
  {
    if ( dst == null )
    {
      DirectColorModel directCM = new DirectColorModel( 32,
          0x00FF0000, 0x0000FF00, 0x000000FF, 0xFF000000 );
      dst = createCompatibleDestImage( src, directCM );
    }

    int width = src.getWidth();
    int height = src.getHeight();
    int[] pixels = new int[width * height];

    // set background color of dst image
    Graphics2D g2d = dst.createGraphics();
    g2d.setBackground( backgroundColor );
    g2d.clearRect( 0, 0, width, height);
    g2d.dispose();

    getPixels( src, 0, 0, width, height, pixels );
    makeTransparent( pixels );
    setPixels( dst, 0, 0, width, height, pixels );


    return dst;
  }

  // This method is called for every pixel in the image
  private void makeTransparent( int[] pixels )
  {
    int red = transparentColor.getRed();
    int green = transparentColor.getGreen();
    int blue = transparentColor.getBlue();

    for ( int i = 0; i < pixels.length; i++ )
    {
      //int a = ( pixels[i] >> 24 ) & 0xFF;
      int r = ( ( pixels[i] << 8 ) >> 24 ) & 0xFF;
      int g = ( ( pixels[i] << 16 ) >> 24 ) & 0xFF;
      int b = ( ( pixels[i] << 24 ) >> 24 ) & 0xFF;

      if ( r == red && g == green && b == blue )
      {
        pixels[i] &= 0x00FFFFFF;
      }
    }
  }

  public static int[] getPixels( BufferedImage img,
                                 int x, int y, int w, int h, int[] pixels )
  {
    if ( w == 0 || h == 0 )
    {
      return new int[0];
    }

    if ( pixels == null )
    {
      pixels = new int[w * h];
    }
    else if ( pixels.length < w * h )
    {
      throw new IllegalArgumentException( "pixels array must have a length" +
          " >= w*h" );
    }

    int imageType = img.getType();
    if ( imageType == BufferedImage.TYPE_INT_ARGB ||
        imageType == BufferedImage.TYPE_INT_RGB )
    {
      Raster raster = img.getRaster();
      return (int[]) raster.getDataElements( x, y, w, h, pixels );
    }

    // Unmanages the image
    return img.getRGB( x, y, w, h, pixels, 0, w );
  }

  public static void setPixels( BufferedImage img,
                                int x, int y, int w, int h, int[] pixels )
  {
    if ( pixels == null || w == 0 || h == 0 )
    {
      return;
    }
    else if ( pixels.length < w * h )
    {
      throw new IllegalArgumentException( "pixels array must have a length" +
          " >= w*h" );
    }

    int imageType = img.getType();
    if ( imageType == BufferedImage.TYPE_INT_ARGB ||
        imageType == BufferedImage.TYPE_INT_RGB )
    {
      WritableRaster raster = img.getRaster();
      raster.setDataElements( x, y, w, h, pixels );
    }
    else
    {
      // Unmanages the image
      img.setRGB( x, y, w, h, pixels, 0, w );
    }
  }

  public static BufferedImage fixTransparency( TransparentFilter transparent, BufferedImage image )
  {
    int width = image.getWidth();
    int height = image.getHeight();
    BufferedImage alphaImage = new BufferedImage( width, height, BufferedImage.TYPE_4BYTE_ABGR );
    Graphics2D g2d = alphaImage.createGraphics();

    int tileSize = 256;

    int[] xLimits = new int[]{
        width / tileSize,
        width % tileSize
    };

    int[] yLimits = new int[]{
        height / tileSize,
        height % tileSize
    };

    if ( xLimits[1] > 0 )
    {
      xLimits[0] += 1;
    }

    if ( yLimits[1] > 0 )
    {
      yLimits[0] += 1;
    }

    for ( int y = 0; y < yLimits[0]; y++ )
    {
      for ( int x = 0; x < xLimits[0]; x++ )
      {
        int xOffset = x * tileSize;
        int yOffset = y * tileSize;

        int xWidth = ( x == ( xLimits[0] - 1 ) && xLimits[1] > 0 ) ? xLimits[1] : tileSize;
        int yHeight = ( y == ( yLimits[0] - 1 ) && yLimits[1] > 0 ) ? yLimits[1] : tileSize;

        if ( xWidth > 0 && yHeight > 0 )
        {
          BufferedImage subImage = image.getSubimage( xOffset, yOffset, xWidth, yHeight );

          g2d.drawImage( subImage, transparent, xOffset, yOffset );
        }
        else
        {
          Map<String, Object> info = new HashMap<String, Object>();

          info.put( "x", x );
          info.put( "y", y );
          info.put( "xLimits", xLimits );
          info.put( "yLimits", yLimits );
          info.put( "xOffset", xOffset );
          info.put( "yOffset", yOffset );
          info.put( "xWidth", xWidth );
          info.put( "yHeight", yHeight );

          System.err.println( info );
        }
      }
    }

    g2d.dispose();

    return alphaImage;
  }

}
