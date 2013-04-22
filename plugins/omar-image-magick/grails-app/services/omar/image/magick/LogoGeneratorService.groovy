package omar.image.magick

import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.awt.Image
import java.awt.RenderingHints

import javax.imageio.ImageIO

import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.ossim.omar.core.Utility

class LogoGeneratorService
{
	LinkGenerator grailsLinkGenerator

	def grailsApplication

	def serviceMethod(def logo, def logoSize)
	{
		logoSize = logoSize as Double

		def logoFilesLocation = grailsLinkGenerator.resource(absolute: true, dir: 'images', plugin: 'omar-image-magick') + "/"

		def logoImageUrl = new URL("${logoFilesLocation}${logo}.png")
		def logoBufferedImage = ImageIO.read(logoImageUrl)
		def logoBufferedImageHeight = logoBufferedImage.getHeight()
		def logoBufferedImageWidth = logoBufferedImage.getWidth()
                     
		def scaledLogoSize = logoSize as Integer
		def scaledLogoImage = logoBufferedImage.getScaledInstance(scaledLogoSize, scaledLogoSize, Image.SCALE_SMOOTH)
		def scaledLogoBufferedImage = new BufferedImage(scaledLogoSize, scaledLogoSize, BufferedImage.TYPE_4BYTE_ABGR)
		
		def logoGraph = scaledLogoBufferedImage.createGraphics()
		def renderingHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
		logoGraph.setRenderingHints(renderingHints)
		
		logoGraph.drawImage(scaledLogoImage, 0, 0, null)

                logoGraph.dispose()


		return scaledLogoBufferedImage
	}
}
