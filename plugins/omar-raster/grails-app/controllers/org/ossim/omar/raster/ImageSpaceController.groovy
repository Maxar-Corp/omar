package org.ossim.omar.raster

import javax.imageio.ImageIO
import java.awt.Rectangle
import java.awt.image.BufferedImage
import org.apache.commons.collections.map.CaseInsensitiveMap
import org.ossim.omar.raster.RasterEntry
import org.ossim.omar.oms.ProjectionService
import grails.converters.JSON
import org.ossim.omar.raster.ImageSpaceService

class ImageSpaceController {
  def imageSpaceService
  def projectionService
  def index = { }


  def getTile = {
    def paramsIgnoreCase = new CaseInsensitiveMap(params)
    def image            = null

    def width = 0
    def height = 0
    def x = 0
    def y = 0
    def format  = paramsIgnoreCase.format?:"image/jpeg"
    def ext = ".jpg"
      switch ( paramsIgnoreCase.format?.toLowerCase() )
      {
          case "jpeg":
          case "jpg":
          case "image/jpeg":
          case "image/jpg":
                format = "image/png"
                ext = ".jpg"
              break
          case "png":
          case "image/png":
               format = "image/png"
               ext = ".png"
              break
          case "gif":
          case "image/gif":
              format = "image/gif"
              ext = ".gif"
              break
          default:
              format = "image/jpeg"
              ext = ".jpg"
              break;
      }
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


        image = imageSpaceService.getPixels(
                rect,
				rasterEntry,
                paramsIgnoreCase)

          response.contentType = format
          def attachment = "filename=getTile${ext}"
          response.setHeader("Content-disposition", "attachment; ${attachment}")
          ImageIO.write(image, response.contentType?.split("/")[-1], response.outputStream)
      }
    }
    catch (Exception e) {
      log.error(e.message)
    }
    return null;
  }
  def imageToGround = {
       def result = [:]
       def paramsIgnoreCase    = new CaseInsensitiveMap(params)
       def rasterEntry = RasterEntry.findByIndexId(paramsIgnoreCase.id)?:
                         RasterEntry.findByTitle(paramsIgnoreCase.id)?:
                         RasterEntry.findById(paramsIgnoreCase.id) 
      if(rasterEntry)
      {
          String inputFile = rasterEntry?.mainFile.name
          if(paramsIgnoreCase.x&&paramsIgnoreCase.y)
          {
              result = projectionService.imageSpaceToGroundSpace(inputFile,
                      paramsIgnoreCase.x as double,
                      paramsIgnoreCase.y as double,
                      rasterEntry.entryId as Integer)
          }
          else if(paramsIgnoreCase.imagePoints)
          {
             def jsonPoints = JSON.parse(paramsIgnoreCase.imagePoints);
              //println "*"*20 + "== " +jsonPoints
             result = projectionService.imageSpaceListToGroundSpace(inputFile,
                     jsonPoints,
                     // paramsIgnoreCase.y as double,
                      rasterEntry.entryId as Integer)
              //println "*"*20 + "RESUKT== "+ result
          }
     }
     render(result as JSON);
  }
  def measure = {
      def data =  request.reader.text;
      def paramsIgnoreCase    = new CaseInsensitiveMap(params)
      def jsonData
      def jsonFeature

      if(data)
      {
          jsonData =  JSON.parse(data);
      }
      if(!paramsIgnoreCase.id)
      {
          paramsIgnoreCase.id = jsonData?.id
      }
      if(!params.feature)
      {
          jsonFeature = jsonData?.feature
      }
      else
      {
          jsonFeature = JSON.parse(paramsIgnoreCase.feature)
      }

      def result = [:]
        def rasterEntry = RasterEntry.findByIndexId(paramsIgnoreCase.id)?:
                          RasterEntry.findByTitle(paramsIgnoreCase.id)?:
                          RasterEntry.findById(paramsIgnoreCase.id)
        if(rasterEntry)
        {
            String inputFile = rasterEntry?.mainFile.name;
            result = projectionService.imageSpaceWKTMeasure([filename: inputFile,
                                                             wkt: jsonFeature.wkt,
                                                             entryId: rasterEntry.entryId as Integer]);
        }
        render(result as JSON);
    }
}
