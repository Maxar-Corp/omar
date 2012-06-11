package org.ossim.omar.ogc

import java.awt.image.BufferedImage
import java.awt.Graphics2D
import java.awt.Color
import javax.imageio.ImageIO
import groovy.xml.StreamingMarkupBuilder
import java.text.AttributedString
import java.text.AttributedCharacterIterator
import java.awt.font.FontRenderContext
import java.awt.font.LineBreakMeasurer
import java.awt.font.TextLayout
import org.ossim.omar.core.HttpStatus
import org.ossim.omar.core.ImageGenerator

class OgcExceptionService
{

  static transactional = false

  def getBackgroundColor(def color, def alpha)
  {
    def result = new Color(255, 255, 255, alpha)
    if ( color )
    {
      if ( color.size() == 8 )
      {
        // skip 0x
        result = new Color(Integer.decode("0x" + color[2] + color[3]),
                Integer.decode("0x" + color[4] + color[5]),
                Integer.decode("0x" + color[6] + color[7]))
      }
    }

    return result
  }
  /**
   *
   * @param message is the actual text string message to write to an image
   * @param ogcParams additional ogc params that can be used to determine the output
   *                  image type, width, and height
   * @return BufferedImage
   */
  def createErrorImage(def message, def ogcParams)
  {
    def imageWidth = ogcParams.width ? ogcParams.width as Integer : 512
    def imageHeight = ogcParams.height ? ogcParams.height as Integer : 256
    def image = null
    def transparent = ogcParams?.transparent ?: false
    def format = ogcParams.format ? ogcParams.format.toLowerCase() : "image/gif"

    if ( imageWidth > 2048 ) imageWidth = 2048
    if ( imageHeight > 2048 ) imageHeight = 2048
    // check forced transparency
    if ( transparent && format?.contains("jpeg") )
    {
      format = "image/gif"
    }
    else  //determine if we are transparent
    {
      switch ( format )
      {
      case "image/png":
      case "image/gif":
        transparent = true
        break
      default:
        transparent = false
        break
      }
    }
    if ( !transparent )
    {
      image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB)
    }
    else
    {
      image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB)
    }

    Graphics2D g2d = image.createGraphics()
    def background = getBackgroundColor(ogcParams."bgcolor", transparent ? 0 : 255)
    g2d.setColor(background)
    g2d.setBackground(background)
    g2d.fillRect(0, 0, imageWidth, imageHeight)

    g2d.setColor(Color.BLACK)
    // Now let's implement a text wrapper if the error string is longer than the width of
    // the image it will wrap in the output
    //
    if ( message )
    {
      def x = 0
      def y = 0
      AttributedString attrStr = new AttributedString(message);
      // Get iterator for string:
      AttributedCharacterIterator characterIterator = attrStr.getIterator();
      // Get font context from graphics:
      FontRenderContext fontRenderContext = g2d.getFontRenderContext();
      // Create measurer:
      LineBreakMeasurer measurer = new LineBreakMeasurer(characterIterator,
              fontRenderContext);
      def done = false
      while ( !done && (measurer.getPosition() < characterIterator.getEndIndex()) )
      {
        TextLayout textLayout = measurer.nextLayout(imageWidth);
        y += textLayout.getAscent(); //Have tried changing y to x
        if ( y < imageHeight )
        {
          textLayout.draw(g2d, (int) x, (int) y);

          y += textLayout.getDescent() + textLayout.getLeading();
        }
        else  // we have exceeded the height of the image
        {
          done = true
        }
      }
    }
    if ( (format == "image/gif") && transparent )
    {
      image = ImageGenerator.convertRGBAToIndexed(image)
    }

    image
  }
  /**
   *
   * @param ogcParams is a Map of aprams and will look at the exception param to
   *                  determine the output format to use
   *
   * @return currently returns a text string and can have value "image","xml", or "text"
   */
  def determineOutputType(def ogcParams)
  {
    def exception = (ogcParams.exceptions ?: "text/plain").toLowerCase()
    def result

      if(exception.contains("xml"))
      {
          result = "xml"
      }
      else if(exception.contains("blank")){
          result = "blank"
      }
      else if(exception.contains("text"))
      {
          result = "text"
      }
      else if(exception.contains("image"))
      {
          result = "image"
      }
      /*
    switch ( exception )
    {
    case "application/vnd.ogc.se_inimage":
    case "inimage":
      result = "image"
      break
    case "application/vnd.ogc.se_blank":
    case "blank":
      result = "blank"
      break
    case "application/vnd.ogc.se_xml":
    case "text/xml":
    case "xml":
      result = "xml"
      break
    case "text/plain":
      result = "text"
      break
    default:
      result = "xml"
      break
    }
    */
    result
  }
  /**
   * @param params contains a map of status, message, and mimeType.
   *               The message can be text based (i.e xml, plain text) or
   *               a BufferedImage for inimage exceptions
   * @param response controller's response interface
   * @return null
   */
  def writeResponse(def response, def params)
  {
    response.status = params.status
    if ( params.message instanceof BufferedImage )
    {
      response.contentType = params.mimeType
      ImageIO.write(params.message, response.contentType?.split("/")[-1], response.outputStream)
    }
    else
    {
      response.contentType = params.mimeType
      response.outputStream.write(params?.message?.toString().bytes)
    }
    response.outputStream.close()
    null
  }

  def formatOgcException(def params, def message)
  {
    def result = [status: null,
            message: null,
            mimeType: null]
    result.status = HttpStatus.BAD_REQUEST

    def outputType = determineOutputType(params)
    switch ( outputType )
    {
    case "text":
      result."mimeType" = "text/plain"
      result.message = message
      break;
    case "xml":
      def xmlbuilder = new StreamingMarkupBuilder()
      xmlbuilder.encoding = "UTF-8"
      def xmlNode = {
        mkp.xmlDeclaration()
        ServiceExceptionReport {
          ServiceException(message)
        }
      }

      result."mimeType" = "text/xml"
      result.message = xmlbuilder.bind(xmlNode).toString()
      break;
    case "blank":
    case "image":
      def mimeType = "image/gif"
      switch ( params.format )
      {
      case "image/jpeg":
      case "image/png":
        mimeType = params.format
        break
      default:
        mimeType = "image/gif"
      }
      result.mimeType = mimeType
      if ( outputType == "blank" )
      {
        result.message = createErrorImage("", params)
      }
      else
      {
        result.message = createErrorImage(message, params)

      }
      break;
    }
    result
  }

  def formatWcsException(def cmd)
  {
    def result = [status: null,
            message: null,
            mimeType: null]
    if ( cmd.hasErrors() )
    {
      result = formatOgcException(cmd.toMap(), "WCS server Error: " + cmd.createErrorString())
    }

    result
  }

  def formatWmsException(def cmd)
  {
    def result = [status: null,
            message: null,
            mimeType: null]
    if ( cmd.hasErrors() )
    {
      result = formatOgcException(cmd.toMap(), "WMS server Error: " + cmd.createErrorString())
    }

    result
  }
}
