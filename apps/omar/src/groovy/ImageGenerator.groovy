import java.awt.image.*;
import java.awt.*;
class ImageGenerator{

  static Image createErrorImage(int w, int h)
  {
    def errorImage = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB)
    Graphics g = errorImage.getGraphics();
    g.setPaint(Color.red);
    g.setStroke(new BasicStroke(4));
    g.drawLine(0,0,w,h);
    g.drawLine(w,0,0,h);

    return errorImage
  }

}