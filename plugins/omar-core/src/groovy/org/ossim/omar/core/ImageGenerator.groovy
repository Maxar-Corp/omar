package org.ossim.omar.core

import java.awt.image.*;
import java.awt.*;
class ImageGenerator {

  static Image createErrorImage(int w, int h, def message) {
    def errorImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB)
    Graphics g = errorImage.getGraphics();
    FontMetrics fontMetrics= g.getFontMetrics()

    //float fontHeight = fontMetrics.height;
    def stringArray = message.split('\n');
    float originY = 0.0;
    float originX = 0.0;
    g.setPaint(Color.white);
    g.setStroke(new BasicStroke(1));
    stringArray.each{ str->
        def bounds = fontMetrics.getStringBounds(str, g);
        originY+=bounds.getHeight();
        originX = (w/2 - (bounds.getWidth()/2));
        if(originX < 0) originX = 0;
        g.drawString(str, originX, originY);
    }
    //g.setPaint(Color.red);
    //g.setStroke(new BasicStroke(4));
    //g.drawLine(0, 0, w, h);
    //g.drawLine(w, 0, 0, h);

    return errorImage
  }

  public static BufferedImage convertRGBAToIndexed(BufferedImage src)
  {
    BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_INDEXED);
    Graphics g = dest.getGraphics();
    g.setColor(new Color(231, 20, 189));
    g.fillRect(0, 0, dest.getWidth(), dest.getHeight()); //fill with a hideous color and make it transparent
    dest = makeTransparent(dest, 0, 0);
    dest.createGraphics().drawImage(src, 0, 0, null);
    return dest;
  }

  public static BufferedImage makeTransparent(BufferedImage image, int x, int y) {
    ColorModel cm = image.getColorModel();
    if (!(cm instanceof IndexColorModel))
      return image; //sorry...
    IndexColorModel icm = (IndexColorModel) cm;
    WritableRaster raster = image.getRaster();
    int pixel = raster.getSample(x, y, 0); //pixel is offset in ICM's palette
    int size = icm.getMapSize();
    byte[] reds = new byte[size];
    byte[] greens = new byte[size];
    byte[] blues = new byte[size];
    icm.getReds(reds);
    icm.getGreens(greens);
    icm.getBlues(blues);
    IndexColorModel icm2 = new IndexColorModel(8, size, reds, greens, blues, pixel);
    return new BufferedImage(icm2, raster, image.isAlphaPremultiplied(), null);
  }
}