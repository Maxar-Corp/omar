package omar.image.magick

import java.awt.image.BufferedImage
import java.awt.Image
import java.awt.RenderingHints

import javax.imageio.ImageIO

import org.codehaus.groovy.grails.web.mapping.LinkGenerator

class OverviewMapGeneratorService
{
	LinkGenerator grailsLinkGenerator

	def grailsApplication

	def serviceMethod(def country, def overviewMapHeight)
	{
		overviewMapHeight = overviewMapHeight as Double

		def overviewMapFilesLocation = grailsLinkGenerator.resource(absolute: true, dir: 'images/overviewMaps', plugin: 'omar-image-magick') + "/"

		def overviewMapImageUrl = new URL("${overviewMapFilesLocation}${country}.gif")
		def overviewMapBufferedImage = ImageIO.read(overviewMapImageUrl)
		def overviewMapBufferedImageHeight = overviewMapBufferedImage.getHeight()
		def overviewMapBufferedImageWidth = overviewMapBufferedImage.getWidth()
                     
		def overviewMapScale = overviewMapHeight / overviewMapBufferedImageHeight
		def scaledOverviewMapHeight = overviewMapHeight as Integer
		def scaledOverviewMapWidth = overviewMapScale * overviewMapBufferedImageWidth as Integer
		def scaledOverviewMapImage = overviewMapBufferedImage.getScaledInstance(scaledOverviewMapWidth, scaledOverviewMapHeight, Image.SCALE_SMOOTH)
		def scaledOverviewMapBufferedImage = new BufferedImage(scaledOverviewMapWidth, scaledOverviewMapHeight, BufferedImage.TYPE_4BYTE_ABGR)
		
		def overviewMapGraph = scaledOverviewMapBufferedImage.createGraphics()
		def renderingHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
		overviewMapGraph.setRenderingHints(renderingHints)
		
		overviewMapGraph.drawImage(scaledOverviewMapImage, 0, 0, null)

                overviewMapGraph.dispose()


		return scaledOverviewMapBufferedImage
	}
}
