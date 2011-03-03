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
    def format  = paramsIgnoreCase.format?:"image/jpeg"
    paramsIgnoreCase.remove("tilewidth")
    paramsIgnoreCase.remove("tileheight")
    paramsIgnoreCase.remove("z")
    paramsIgnoreCase.x      = x
    paramsIgnoreCase.y      = y
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
		  paramsIgnoreCase.scale = "${scale}"
		  if(!paramsIgnoreCase.pivot)
		  {
			  paramsIgnoreCase.pivot = "${rasterEntry.width*0.5},${rasterEntry.height*0.5}"
		  }
        image = icpService.getPixels(
                rect,
				rasterEntry,
                 paramsIgnoreCase)
        }
        response.contentType = format
        ImageIO.write(image, response.contentType?.split("/")[-1], response.outputStream)
      }
    }
    catch (Exception e) {
      log.error(e.message)
    }
    return null
  }


  def getTile = {
    def paramsIgnoreCase = new CaseInsensitiveMap(params)
    def image   = null

    def width = 0
    def height = 0
    def x = 0
    def y = 0
    def format  = paramsIgnoreCase.format?:"image/jpeg"
    try
    {
      if(!params.id)
      {
        throw new Exception("No 'id' value given to chip a tile from, please use id=<value>")
      }
      // check if minimal params are available
      if(paramsIgnoreCase.startLine&&paramsIgnoreCase.startSample&&
         paramsIgnoreCase.endLine&&paramsIgnoreCase.endSample)
      {
        def line   = [paramsIgnoreCase.startLine   as Integer,
                      paramsIgnoreCase.endLine as Integer]
        def sample   = [paramsIgnoreCase.startSample   as Integer,
                        paramsIgnoreCase.endSample as Integer]
        if(line[1] < line[0])     line = [line[1],line[0]]
        if(sample[1] < sample[0]) sample = [sample[1],sample[0]]
        width   = (sample[1]-sample[0])+1
        height  = (line[1]-line[0])+1
        x       = sample[0]
        y       = line[0]
      }
      else if(paramsIgnoreCase.width&&paramsIgnoreCase.height&&
              paramsIgnoreCase.x&&paramsIgnoreCase.y)
      {
        width   = paramsIgnoreCase.width?.toDouble() as Integer
        height  = paramsIgnoreCase.height?.toDouble() as Integer
        x       = paramsIgnoreCase.x?.toDouble() as Integer
        y       = paramsIgnoreCase.y?.toDouble() as Integer
      }
      else
      {
          throw new Exception("Improper argument list\nRequires x,y,width,height or startLine, endLine, startSample, endSample")
      }
    }
    catch(Exception e)
    {
      log.error(e.message)
      println e.message
      response.contentType = "text/plain"
      return null
    }
    def scale   = paramsIgnoreCase.scale?paramsIgnoreCase.scale.toDouble():1.0
    try {
      Rectangle rect  = new Rectangle(x, y, width, height)
      def rasterEntry = RasterEntry.findByIndexId(paramsIgnoreCase.id)?:RasterEntry.findByTitle(paramsIgnoreCase.id)?:RasterEntry.findById(paramsIgnoreCase.id)
      if(rasterEntry==null)
      {
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
      }
      else
      {
        String inputFile = rasterEntry?.mainFile.name

        width  = rasterEntry?.width
        height = rasterEntry?.height
        def bands = rasterEntry?.numberOfBands

        int entry = rasterEntry.entryId?.toInteger()

		paramsIgnoreCase.scale = "${scale}"
		if(!paramsIgnoreCase.pivot)
		{
			
			paramsIgnoreCase.pivot = "${rasterEntry.width*0.5},${rasterEntry.height*0.5}"
		}
        image = icpService.getPixels(
                rect,
				rasterEntry,
                paramsIgnoreCase)

        response.contentType = format
        ImageIO.write(image, response.contentType?.split("/")[-1], response.outputStream)
      }
    }
    catch (Exception e) {
      log.error(e.message)
    }
    return null;
  }
}
