package omar.image.magick

import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.RenderingHints

import javax.imageio.ImageIO

import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.ossim.omar.core.Utility

class TemplateExportService
{
	LinkGenerator grailsLinkGenerator

	def gradientGeneratorService
	def grailsApplication
	def logoGeneratorService
	def northArrowGeneratorService
	def textGeneratorService

	def serviceMethod(def footerAcquisitionDateText, def footerLocationText, def footerSecurityClassificationText, def headerDescriptionText, def headerSecurityClassificationText, def headerTitleText, def imageUrl, def logo, def northAngle)
	{
		// download image
		def omarImageUrl = new URL("${imageUrl}")
		def omarImageBufferedImage = ImageIO.read(omarImageUrl)
		def omarImageHeight = omarImageBufferedImage.getHeight()
		def omarImageWidth = omarImageBufferedImage.getWidth()
		
		// generate blank template
		def templateHeight = 1.16 * omarImageHeight as Integer
		def templateWidth = omarImageWidth
		def templateBufferedImage = new BufferedImage(templateWidth, templateHeight, BufferedImage.TYPE_4BYTE_ABGR)
		def templateGraphic = templateBufferedImage.createGraphics()
		templateGraphic.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON))
		templateGraphic.setRenderingHints(new RenderingHints(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR))

		// generate header
		def headerHeight = 0.1 * omarImageHeight as Integer
		def headerGradientBufferedImage = gradientGeneratorService.serviceMethod(headerHeight)
		for (i in 0..omarImageWidth) { templateGraphic.drawImage(headerGradientBufferedImage, i, 0, null) }

		// generate logo
		def logoSize = 0.8 * headerHeight
		def logoBufferedImage = logoGeneratorService.serviceMethod(logo, logoSize)		

		def logoOffset = (headerHeight - logoSize) / 2 as Integer
		templateGraphic.drawImage(logoBufferedImage, logoOffset, logoOffset, null)

		// header security classification
		def headerSecurityClassificationTextHeight = 0.25 * logoSize as Integer 
		def headerSecurityClassificationTextWidth = omarImageWidth - 2 * logoSize - 4 * logoOffset as Integer
		def headerSecurityClassificationTextBufferedImage = textGeneratorService.serviceMethod(headerSecurityClassificationText, "left", Color.CYAN, headerSecurityClassificationTextHeight, headerSecurityClassificationTextWidth)

		def headerSecurityClassificationTextOffsetX = logoSize + 2 * logoOffset as Integer
		def headerSecurityClassificationTextOffsetY = logoOffset as Integer
		templateGraphic.drawImage(headerSecurityClassificationTextBufferedImage, headerSecurityClassificationTextOffsetX, headerSecurityClassificationTextOffsetY, null)

		// header title text
		def headerTitleTextHeight = 0.43 * logoSize as Integer
		def headerTitleTextWidth = omarImageWidth - 2 * logoSize - 4 * logoOffset as Integer
		def headerTitleTextBufferedImage = textGeneratorService.serviceMethod(headerTitleText, "left", Color.YELLOW, headerTitleTextHeight, headerTitleTextWidth)

		def headerTitleTextOffsetX = logoSize + 2 * logoOffset as Integer
		def headerTitleTextOffsetY = headerSecurityClassificationTextOffsetY + headerSecurityClassificationTextHeight as Integer
		templateGraphic.drawImage(headerTitleTextBufferedImage, headerTitleTextOffsetX, headerTitleTextOffsetY, null)
		 
		// header description text	
		def headerDescriptionTextHeight = 0.32 * logoSize as Integer
		def headerDescriptionTextWidth = omarImageWidth - 2 * logoSize - 4 * logoOffset as Integer
		def headerDescriptionTextBufferedImage = textGeneratorService.serviceMethod(headerDescriptionText, "left", Color.WHITE, headerDescriptionTextHeight, headerDescriptionTextWidth)

		def headerDescriptionTextOffsetX = logoSize + 2 * logoOffset as Integer
		def headerDescriptionTextOffsetY = headerTitleTextOffsetY + headerTitleTextHeight as Integer
		templateGraphic.drawImage(headerDescriptionTextBufferedImage, headerDescriptionTextOffsetX, headerDescriptionTextOffsetY, null)
	
		// generate north arrow
		def northArrowSize = logoSize
		def northArrowBufferedImage = northArrowGeneratorService.serviceMethod(northAngle, northArrowSize)

		def northArrowOffsetX = omarImageWidth - northArrowSize - logoOffset as Integer
		def northArrowOffsetY = logoOffset as Integer
		templateGraphic.drawImage(northArrowBufferedImage, northArrowOffsetX, northArrowOffsetY, null)		

		// add omar image
		def omarImageOffsetY = headerHeight as Integer
		templateGraphic.drawImage(omarImageBufferedImage, 0, omarImageOffsetY, null)

		// generate footer
                def footerHeight = 0.03 * omarImageHeight as Integer
                def footerGradientBufferedImage = gradientGeneratorService.serviceMethod(footerHeight)

		def footerGradientImageOffsetY = headerHeight + omarImageHeight as Integer
                for (i in 0..omarImageWidth) { templateGraphic.drawImage(headerGradientBufferedImage, i, footerGradientImageOffsetY, null) }

		// footer security classification
		def footerSecurityClassificationTextHeight = footerHeight as Integer
		def footerSecurityClassificationTextWidth = omarImageWidth / 3 as Integer
		def footerSecurityClassificationTextBufferedImage = textGeneratorService.serviceMethod(footerSecurityClassificationText, "left", Color.CYAN, footerSecurityClassificationTextHeight, footerSecurityClassificationTextWidth)
 
		def footerSecurityClassificationTextOffsetX = logoOffset as Integer
		def footerSecurityClassificationTextOffsetY = headerHeight + omarImageHeight as Integer
		templateGraphic.drawImage(footerSecurityClassificationTextBufferedImage, footerSecurityClassificationTextOffsetX, footerSecurityClassificationTextOffsetY, null)

		// footer location text
		def footerLocationTextHeight = footerHeight as Integer
                def footerLocationTextWidth = omarImageWidth / 3 as Integer
                def footerLocationTextBufferedImage = textGeneratorService.serviceMethod(footerLocationText, "center", Color.CYAN, footerLocationTextHeight, footerLocationTextWidth)

                def footerLocationTextOffsetX = omarImageWidth / 3 as Integer
                def footerLocationTextOffsetY = headerHeight + omarImageHeight as Integer
                templateGraphic.drawImage(footerLocationTextBufferedImage, footerLocationTextOffsetX, footerLocationTextOffsetY, null)

		// footer acquisition date text
		def footerAcquisitionDateTextHeight = footerHeight as Integer
                def footerAcquisitionDateTextWidth = omarImageWidth / 3 as Integer
                def footerAcquisitionDateTextBufferedImage = textGeneratorService.serviceMethod(footerAcquisitionDateText, "right", Color.CYAN, footerAcquisitionDateTextHeight, footerAcquisitionDateTextWidth)

                def footerAcquisitionDateTextOffsetX = omarImageWidth * 2/3 as Integer
                def footerAcquisitionDateTextOffsetY = headerHeight + omarImageHeight as Integer
                templateGraphic.drawImage(footerAcquisitionDateTextBufferedImage, footerAcquisitionDateTextOffsetX, footerAcquisitionDateTextOffsetY, null)

		templateGraphic.dispose()

		def tempFilesLocation = grailsApplication.config.export.workDir + "/"
                def tempFilesLocationAsFile = new File(tempFilesLocation)
                def templateFile = File.createTempFile("finishedProduct", ".png", tempFilesLocationAsFile)
		ImageIO.write(templateBufferedImage, "png", templateFile as File)
		return templateFile.toString()
	}
}
