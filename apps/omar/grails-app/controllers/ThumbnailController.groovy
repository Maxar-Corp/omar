import javax.imageio.ImageIO
import org.springframework.beans.factory.InitializingBean
import java.awt.image.BufferedImage

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
    def rasterEntry = RasterEntry.get(params.id)
    if ( !rasterEntry )
    {
      flash.message = "RasterEntry not found with id ${params.id}"
      redirect(action: list)
    }
    else
    {
      try
      {
        def mimeType = "image/jpeg"

        params.mimeType = mimeType
        
        File outputFile = thumbnailService.getRasterEntryThumbnailFile(rasterEntry, params)

        if ( outputFile.exists() && (outputFile.length() > 0) )
        {
          def image = ImageIO.read(outputFile)

          response.contentType = mimeType
          ImageIO.write(image, "jpeg", response.outputStream)
        }
      }
      catch (Exception e)
      {
        println "Error: ${e.message}"
      }
    }
  }


  def frame = {
    def videoDataSet = VideoDataSet.get(params.id)

    if ( !videoDataSet )
    {
      flash.message = "VideoDataSet not found with id ${params.id}"
      redirect(action: list)
    }
    else
    {
      try
      {
        File outputFile = thumbnailService.getVideoDataSetThumbnailFile(videoDataSet, params)

        def image = ImageIO.read(outputFile)

        response.contentType = "image/jpeg"
        ImageIO.write(image, "jpeg", response.outputStream)
      }
      catch (Exception e)
      {
        println "Error: ${e.message}"
      }
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
