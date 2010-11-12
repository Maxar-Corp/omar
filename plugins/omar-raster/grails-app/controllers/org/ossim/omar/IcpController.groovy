package org.ossim.omar

import javax.imageio.ImageIO
import java.awt.Rectangle
import java.awt.image.BufferedImage
import org.apache.commons.collections.map.CaseInsensitiveMap
class IcpController {
  def icpService

  def index = { }
  def getTileOpenLayers = {
    def paramsIgnoreCase    = new CaseInsensitiveMap(params)
    def width = paramsIgnoreCase.tilewidth?.toInteger()
    def height = paramsIgnoreCase.tileheight?.toInteger()
    def x = Math.round(paramsIgnoreCase.x?.toDouble() * width) as Integer;
    def y = Math.round(paramsIgnoreCase.y?.toDouble() * height) as Integer;
    def z       = paramsIgnoreCase.z
    paramsIgnoreCase.remove("tilewidth")
    paramsIgnoreCase.remove("tileheight")
    paramsIgnoreCase.remove("z")
    paramsIgnoreCase.x = x
    paramsIgnoreCase.y = y
    paramsIgnoreCase.width  = width
    paramsIgnoreCase.height = height
    def image = null
    try {
      Rectangle rect  = new Rectangle(x, y, width, height)
      if(paramsIgnoreCase.id)
      {
        def rasterEntry = RasterEntry.findByIndexId(paramsIgnoreCase.id)?:RasterEntry.findByTitle(paramsIgnoreCase.id)?:RasterEntry.findById(paramsIgnoreCase.id)
        if(rasterEntry==null)
        {
          def w = width?:256
          def h = height?:256
          image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

          response.contentType = "image/jpeg"
          ImageIO.write(image, "jpeg", response.outputStream)
        }
        else
        {
          String inputFile = rasterEntry?.mainFile.name

          width = rasterEntry?.width
          height = rasterEntry?.height
          def bands = rasterEntry?.numberOfBands
          def numRLevels = 1
          def tileSize = 256
          def targetFullRect = (2 ** z.toInteger()) * tileSize;

          def maxDimension = width;
          if (maxDimension < height)
          {
            maxDimension = height;
          }
          BigDecimal scale = (double) targetFullRect / (double) maxDimension;
          def outputType = "jpeg"
          def resLevel = numRLevels - z.toInteger() - 1
          int entry = rasterEntry.entryId?.toInteger()

          image = icpService.getPixels(
                  rect,
                  inputFile,
                  entry,
                  bands as Integer,
                  scale,
                  paramsIgnoreCase)
          response.contentType = "image/jpeg"
          ImageIO.write(image, "jpeg", response.outputStream)
        }
      }
    }
    catch (Exception e) {
      log.error(e.message)
//      image = new BufferedImage(tileWidth, tileHeight, BufferedImage.TYPE_INT_RGB)

//      response.contentType = "image/jpeg"
//      ImageIO.write(image, "jpeg", response.outputStream)


    }
    return null
  }


  def getTile = {
    def paramsIgnoreCase = new CaseInsensitiveMap(params)
    def image   = null
    def width   = paramsIgnoreCase.width?.toDouble() as Integer
    def height  = paramsIgnoreCase.height?.toDouble() as Integer
    def x       = paramsIgnoreCase.x?.toDouble() as Integer
    def y       = paramsIgnoreCase.y?.toDouble() as Integer
    def scale   = paramsIgnoreCase.scale?paramsIgnoreCase.scale.toDouble():1.0
    try {
      Rectangle rect  = new Rectangle(x, y, width, height)
      def rasterEntry = RasterEntry.findByIndexId(paramsIgnoreCase.id)?:RasterEntry.findByTitle(paramsIgnoreCase.id)?:RasterEntry.findById(paramsIgnoreCase.id)
      if(rasterEntry==null)
      {
        def w = width?:256
        def h = height?:256
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

        response.contentType = "image/jpeg"
        ImageIO.write(image, "jpeg", response.outputStream)
      }
      else
      {
        String inputFile = rasterEntry?.mainFile.name

        width  = rasterEntry?.width
        height = rasterEntry?.height
        def bands = rasterEntry?.numberOfBands

        def outputType = "jpeg"
        int entry = rasterEntry.entryId?.toInteger()

        image = icpService.getPixels(
                rect,
                inputFile,
                entry,
                bands as Integer,
                scale,
                paramsIgnoreCase)

        response.contentType = "image/jpeg"
        ImageIO.write(image, "jpeg", response.outputStream)
      }
    }
    catch (Exception e) {
      log.error(e.message)
//      image = new BufferedImage(tileWidth, tileHeight, BufferedImage.TYPE_INT_RGB)

//      response.contentType = "image/jpeg"
//      ImageIO.write(image, "jpeg", response.outputStream)


    }
    return null;
  }
}
