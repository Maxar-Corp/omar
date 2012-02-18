package org.ossim.omar.ogc

import java.awt.Graphics2D
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

class ImageUtilService
{

  static transactional = false

  def rotateImage(def inputImage, def imageHeight, def imageWidth, def angleInDegrees)
  {
    def radians = Math.toRadians(angleInDegrees)
    def outputImage = new BufferedImage(inputImage.width, inputImage.height, BufferedImage.TYPE_INT_RGB)
    def g2d = (Graphics2D) outputImage.graphics

    g2d.drawRenderedImage(inputImage, AffineTransform.getRotateInstance(radians, inputImage.width / 2, inputImage.height / 2))
    g2d.dispose()

      def xStart = (int)(Math.round((inputImage.width - imageWidth)/2))
      def yStart = (int)(Math.round((inputImage.height - imageHeight)/2))
      def xEnd = (int)(imageWidth)
      def yEnd = (int)(imageHeight)


    outputImage = outputImage.getSubimage(xStart, yStart, xEnd, yEnd)
    return outputImage
  }

  def overlayImages(def inputImage, def arrowImage, def angleInDegrees)
  {
    def radians = Math.toRadians(angleInDegrees)
    def arrowOutputImage = new BufferedImage(arrowImage.width, arrowImage.height, BufferedImage.TYPE_INT_ARGB)
    def g2d1 = (Graphics2D) arrowOutputImage.graphics
    g2d1.drawRenderedImage(arrowImage, AffineTransform.getRotateInstance(radians, arrowImage.width / 2, arrowImage.height / 2))
    g2d1.dispose()

    def inputOutputImage = new BufferedImage(inputImage.width, inputImage.height, BufferedImage.TYPE_INT_ARGB)
    def g2d2 = (Graphics2D) inputOutputImage.graphics
    g2d2.drawRenderedImage(inputImage, AffineTransform.getRotateInstance(radians, inputImage.width / 2, inputImage.height / 2))
    g2d2.dispose()

    def outputImage = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB)
    def g2d3 = (Graphics2D) outputImage.graphics
    g2d3.drawRenderedImage(inputOutputImage, AffineTransform.getTranslateInstance(0, 0))
    g2d3.drawRenderedImage(arrowOutputImage, AffineTransform.getTranslateInstance(20, 20))
    g2d3.dispose()

    //def g2d = (Graphics2D) outputImage.graphics


          //g2d.drawImage(i.image, null, i.x, i.y)


    //def outputImage = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB)
    return outputImage
  }


  def readImage(def fileOrUrl)
  {
    def image = ImageIO.read(fileOrUrl)

    return image
  }

  def writeImageToStream(def image, def type, def stream)
  {
    ImageIO.write(image, type, stream)
  }
}
