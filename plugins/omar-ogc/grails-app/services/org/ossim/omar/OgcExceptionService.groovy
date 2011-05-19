package org.ossim.omar

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

class OgcExceptionService {

    static transactional = false

    /**
     *
     * @param message is the actual text string message to write to an image
     * @param ogcParams additional ogc params that can be used to determine the output
     *                  image type, width, and height
     * @return
     */
    def createErrorImage (def message, def ogcParams)
    {
        def imageWidth = ogcParams.width ? ogcParams.width as Integer : 512
        def imageHeight = ogcParams.height ? ogcParams.height as Integer : 256
        def image = null
        def transparent = false
        def format = ogcParams.format ? ogcParams.format.toLowerCase() : "image/gif"

        switch (format) {
            case "image/png":
            case "image/gif":
                transparent = true
                break
            default:
                transparent = false
                break
        }
        if (!transparent) {
            image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB)
        }
        else {
            image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB)
        }

        Graphics2D g2d = image.createGraphics()
        g2d.setColor(Color.WHITE)
        g2d.setBackground(Color.WHITE)
        g2d.fillRect(0, 0, imageWidth, imageHeight)
        g2d.setColor(Color.BLACK)
        //g2d.drawString(message, 0, imageHeight/2)


        // Now let's implement a text wrapper if the error string is longer than the width of
        // the image it will wrap in the output
        //
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
        while (!done &&(measurer.getPosition() < characterIterator.getEndIndex())) {
            TextLayout textLayout = measurer.nextLayout(imageWidth);
            y += textLayout.getAscent(); //Have tried changing y to x
            if(y < imageHeight)
            {
                textLayout.draw(g2d, (int) x, (int) y);

                y += textLayout.getDescent() + textLayout.getLeading();
            }
            else  // we have exceeded the height of the image
            {
                done = true
            }
        }

        image
    }
    def determineOutputType(def ogcParams)
    {
        def exception = ogcParams.exception?:"text/plain"
        def result
        switch(exception.toLowerCase())
        {
            case "application/vnd.ogc.se_inimage":
            case "inimage":
                result = "image"
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
                result = "text"
                break
        }
        result
    }
    /**
     * @param params contains a map of status, message, and mimeType
     * @param response controller's response interface
     * @return
     */
    def writeResponse(def response, def params)
    {
        response.status = params.status
        if(params.message instanceof BufferedImage)
        {
            response.contentType = params.mimeType
            ImageIO.write(params.message, response.contentType?.split("/")[-1], response.outputStream)
            response.outputStream.close()
        }
        else
        {
            response.contentType = params.mimeType
            response.outputStream.write(params.message.bytes)
            response.outputStream.close()
            //render(params.message)
        }
    }
    def formatWcsException(WcsCommand cmd)
    {
        def result = [status:null,
                      message:null,
                      mimeType:null]
        def params = cmd.toMap()
        if(cmd.hasErrors())
        {
            result.status = org.ossim.omar.HttpStatus.BAD_REQUEST

            def outputType = determineOutputType(params)
            switch(outputType)
            {
                case "text":
                    result."mimeType" = "text/plain"
                    result.message = "WCS server error: "
                    result.message  +=  cmd.createErrorString()
                    break;
                case "xml":
                    def xmlbuilder = new StreamingMarkupBuilder()
                    xmlbuilder.encoding = "UTF-8"
                    def xmlNode = {
                      mkp.xmlDeclaration()
                        ServiceExceptionReport{
                        ServiceException(cmd.createErrorString())
                        }
                    }

                    result."mimeType" = "text/xml"
                    result.message    =  xmlbuilder.bind(xmlNode).toString()
                    break;
                case "image":
                    def mimeType = "image/gif"
                    switch(params.format)
                    {
                        case "image/jpeg":
                        case "image/png":
                            mimeType = params.format
                            break
                        default:
                            mimeType = "image/gif"
                    }
                    result.mimeType = mimeType
                    result.message    = createErrorImage("WCS server error: " + cmd.createErrorString(), params)
                    break;
            }
            result

        }

        result
    }
}
