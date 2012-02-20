package org.ossim.omar

import javax.imageio.ImageIO
import org.springframework.beans.factory.InitializingBean
import java.awt.image.*;
import java.awt.*
import org.ossim.omar.core.HttpStatusMessage
import org.ossim.omar.core.HttpStatus
import org.ossim.omar.core.ImageGenerator
import org.ossim.omar.raster.RasterEntry

class ThumbnailController implements InitializingBean
{

  def grailsApplication
  def thumbnailService
  def nullImage = new BufferedImage(128,128,BufferedImage.TYPE_INT_RGB);


  def list =
  {
    try
    {
      response.contentType = "image/jpeg"
      ImageIO.write(nullImage, "jpeg", response.outputStream)
    }
    catch(Exception e)
    {
      
    }
  }
  def show =
  {
    try
    {
      def httpStatusMessage = new HttpStatusMessage()
      httpStatusMessage.status = HttpStatus.OK
      def rasterEntry = RasterEntry.findByIndexId(params.id) ?:RasterEntry.get(params.id);
      def image = null
      def mimeType = "image/jpeg"
      if ( !rasterEntry )
      {
        httpStatusMessage.message = "RasterEntry not found with id ${params.id}"
        httpStatusMessage.status = HttpStatus.NOT_FOUND
        image = ImageGenerator.createErrorImage(128, 128);
        log.error(httpStatusMessage)
      }
      else
      {
          params.mimeType = mimeType
          File outputFile = thumbnailService.getRasterEntryThumbnailFile(httpStatusMessage, rasterEntry, params)
          if ( (httpStatusMessage.status == HttpStatus.OK)&&
                  outputFile.exists() &&
                  (outputFile.length() > 0) )
          {
            image = ImageIO.read(outputFile)
          }
          if(!image)
          {
            image = ImageGenerator.createErrorImage(128, 128);
          }
      }
      httpStatusMessage.initializeResponse(response)
      response.contentType = mimeType
      ImageIO.write(image, "jpeg", response.outputStream)
    }
    catch (Exception e)
    {
      log.error("exception ${e.message}")
    }
  }


  def frame = {
    try
    {
      def videoDataSet = VideoDataSet.findByIndexId(params.id) ?:VideoDataSet.get(params.id);
      def image = (Image)null
      def mimeType = "image/jpeg"
      def httpStatusMessage = new HttpStatusMessage()
      httpStatusMessage.status = HttpStatus.OK

      if ( !videoDataSet )
      {
        httpStatusMessage.message = "VideoDataSet not found with id ${params.id}"
        httpStatusMessage.status = HttpStatus.NOT_FOUND
      }
      else
      {
        File outputFile = thumbnailService.getVideoDataSetThumbnailFile(httpStatusMessage, videoDataSet, params)

        if(!outputFile.exists())
        {
          httpStatusMessage.message = "Unable to write thumbnail for video dataset ${videoDataSet.getMainFile()}"
          httpStatusMessage.status = HttpStatus.NOT_FOUND
        }
        // check the status for errors
        if((httpStatusMessage.status == HttpStatus.OK))
        {
          image = ImageIO.read(outputFile)
        }
      }
      if(httpStatusMessage.status != HttpStatus.OK)
      {
        if(!image)
        {
          image = ImageGenerator.createErrorImage(128, 128);
        }
      }
      httpStatusMessage.initializeResponse(response)
      response.contentType = "image/jpeg"
      ImageIO.write(image, "jpeg", response.outputStream)
    }
    catch (Exception e)
    {
      println "Error: ${e.message}"
    }
  }

  def asHTML = {
    def size = params.size

    if ( !size )
    grailsApplication.config.thumbnail.defaultSize

    render(contentType: "text/html") {
      html {
        body {
          img(src: createLink(action: "show", id: params.id, params: params), width: size, height: size)
        }
      }
    }
  }

  def proxy = {

    def url = "${params.url}"

    params.each {
      url += "&${it}"
    }

    println url

    def text = url.toURL().text

    println text

    render(contentType: "text/xml", text: text)
  }

  public void afterPropertiesSet()
  {
    //To change body of implemented methods use File | Settings | File Templates.
  }
}
