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
	def overviewMapGeneratorService
	def textGeneratorService

	def serviceMethod(def country, def footerAcquisitionDateText, def footerAcquisitionDateTextColor, def footerLocationText, def footerLocationTextColor, def footerSecurityClassificationText, def footerSecurityClassificationTextColor, def headerDescriptionText, def headerDescriptionTextColor, def headerSecurityClassificationText, def headerSecurityClassificationTextColor, def headerTitleText, def headerTitleTextColor, def imageUrl, def includeOverviewMap, def logo, def northAngle, def northArrowColor, def northArrowBackgroundColor)
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
		def headerGradientBufferedImage = gradientGeneratorService.serviceMethod(Color.BLACK, headerHeight, Color.GRAY)
		for (i in 0..omarImageWidth) { templateGraphic.drawImage(headerGradientBufferedImage, i, 0, null) }

		// add omar image
                def omarImageOffsetY = headerHeight as Integer
                templateGraphic.drawImage(omarImageBufferedImage, 0, omarImageOffsetY, null)

		// generate logo
		def logoSize = 0.8 * headerHeight
		def logoBufferedImage = logoGeneratorService.serviceMethod(logo, logoSize)		

		def logoImageOffset = (headerHeight - logoSize) / 2 as Integer
		templateGraphic.drawImage(logoBufferedImage, logoImageOffset, logoImageOffset, null)

		// header security classification
		def headerSecurityClassificationTextHeight = 0.25 * logoSize as Integer 
		def headerSecurityClassificationTextWidth = omarImageWidth - 2 * logoSize - 4 * logoImageOffset as Integer
		def headerSecurityClassificationTextBufferedImage = textGeneratorService.serviceMethod(headerSecurityClassificationText, "left", headerSecurityClassificationTextColor, headerSecurityClassificationTextHeight, headerSecurityClassificationTextWidth)

		def headerSecurityClassificationTextOffsetX = logoSize + 2 * logoImageOffset as Integer
		def headerSecurityClassificationTextOffsetY = logoImageOffset as Integer
		templateGraphic.drawImage(headerSecurityClassificationTextBufferedImage, headerSecurityClassificationTextOffsetX, headerSecurityClassificationTextOffsetY, null)

		// overview map
                includeOverviewMap = Boolean.valueOf("${includeOverviewMap}")
		def overviewMapWidth = 0
                if (includeOverviewMap)
                {
                        def overviewMapHeight = 1.2 * headerHeight as Integer
                        def overviewMapBufferedImage = overviewMapGeneratorService.serviceMethod(country, overviewMapHeight)
			
			overviewMapWidth = logoImageOffset + overviewMapBufferedImage.getWidth()
                        def overviewMapImageOffsetX = omarImageWidth - overviewMapWidth
                        def overviewMapImageOffsetY = logoImageOffset

                        templateGraphic.drawImage(overviewMapBufferedImage, overviewMapImageOffsetX, overviewMapImageOffsetY, null)
                }

		// header title text
		def headerTitleTextHeight = 0.43 * logoSize as Integer
		def headerTitleTextWidth = omarImageWidth - 2 * logoSize - 4 * logoImageOffset as Integer
		def headerTitleTextBufferedImage = textGeneratorService.serviceMethod(headerTitleText, "left", headerTitleTextColor, headerTitleTextHeight, headerTitleTextWidth)

		def headerTitleTextOffsetX = logoSize + 2 * logoImageOffset as Integer
		def headerTitleTextOffsetY = headerSecurityClassificationTextOffsetY + headerSecurityClassificationTextHeight as Integer
		templateGraphic.drawImage(headerTitleTextBufferedImage, headerTitleTextOffsetX, headerTitleTextOffsetY, null)
		 
		// header description text	
		def headerDescriptionTextHeight = 0.32 * logoSize as Integer
		def headerDescriptionTextWidth = omarImageWidth - 2 * logoSize - 4 * logoImageOffset as Integer
		def headerDescriptionTextBufferedImage = textGeneratorService.serviceMethod(headerDescriptionText, "left", headerDescriptionTextColor, headerDescriptionTextHeight, headerDescriptionTextWidth)

		def headerDescriptionTextOffsetX = logoSize + 2 * logoImageOffset as Integer
		def headerDescriptionTextOffsetY = headerTitleTextOffsetY + headerTitleTextHeight as Integer
		templateGraphic.drawImage(headerDescriptionTextBufferedImage, headerDescriptionTextOffsetX, headerDescriptionTextOffsetY, null)
	
		// generate north arrow
		def northArrowSize = logoSize
		def northArrowBufferedImage = northArrowGeneratorService.serviceMethod(northAngle, northArrowColor, northArrowBackgroundColor, northArrowSize)

		def northArrowOffsetX = omarImageWidth - northArrowSize - logoImageOffset - overviewMapWidth as Integer
		def northArrowOffsetY = logoImageOffset as Integer
		templateGraphic.drawImage(northArrowBufferedImage, northArrowOffsetX, northArrowOffsetY, null)		

		// generate footer
                def footerHeight = 0.03 * omarImageHeight as Integer
                def footerGradientBufferedImage = gradientGeneratorService.serviceMethod(Color.BLACK, footerHeight, Color.GRAY)

		def footerGradientImageOffsetY = headerHeight + omarImageHeight as Integer
                for (i in 0..omarImageWidth) { templateGraphic.drawImage(footerGradientBufferedImage, i, footerGradientImageOffsetY, null) }

		// footer security classification
		def footerSecurityClassificationTextHeight = footerHeight as Integer
		def footerSecurityClassificationTextWidth = omarImageWidth / 3 as Integer
		def footerSecurityClassificationTextBufferedImage = textGeneratorService.serviceMethod(footerSecurityClassificationText, "left", footerSecurityClassificationTextColor, footerSecurityClassificationTextHeight, footerSecurityClassificationTextWidth)
 
		def footerSecurityClassificationTextOffsetX = logoImageOffset as Integer
		def footerSecurityClassificationTextOffsetY = headerHeight + omarImageHeight as Integer
		templateGraphic.drawImage(footerSecurityClassificationTextBufferedImage, footerSecurityClassificationTextOffsetX, footerSecurityClassificationTextOffsetY, null)

		// footer location text
		def footerLocationTextHeight = footerHeight as Integer
                def footerLocationTextWidth = omarImageWidth / 3 as Integer
                def footerLocationTextBufferedImage = textGeneratorService.serviceMethod(footerLocationText, "center", footerLocationTextColor, footerLocationTextHeight, footerLocationTextWidth)

                def footerLocationTextOffsetX = omarImageWidth / 3 as Integer
                def footerLocationTextOffsetY = headerHeight + omarImageHeight as Integer
                templateGraphic.drawImage(footerLocationTextBufferedImage, footerLocationTextOffsetX, footerLocationTextOffsetY, null)

		// footer acquisition date text
		def footerAcquisitionDateTextHeight = footerHeight as Integer
                def footerAcquisitionDateTextWidth = omarImageWidth / 3 as Integer
                def footerAcquisitionDateTextBufferedImage = textGeneratorService.serviceMethod(footerAcquisitionDateText, "right", footerAcquisitionDateTextColor, footerAcquisitionDateTextHeight, footerAcquisitionDateTextWidth)

                def footerAcquisitionDateTextOffsetX = omarImageWidth * 2/3 as Integer
                def footerAcquisitionDateTextOffsetY = headerHeight + omarImageHeight as Integer
                templateGraphic.drawImage(footerAcquisitionDateTextBufferedImage, footerAcquisitionDateTextOffsetX, footerAcquisitionDateTextOffsetY, null)

		// disclaimer text
		def disclaimerHeight = 0.03 * omarImageHeight as Integer
                def disclaimerGradientBufferedImage = gradientGeneratorService.serviceMethod(Color.YELLOW, disclaimerHeight, Color.YELLOW)

                def disclaimerGradientImageOffsetY = headerHeight + omarImageHeight + footerHeight as Integer
                for (i in 0..omarImageWidth) { templateGraphic.drawImage(disclaimerGradientBufferedImage, i, disclaimerGradientImageOffsetY, null) }

		def disclaimerText = "Not an intelligence product // For information use only // Not certified for targeting"
		def disclaimerTextHeight = disclaimerHeight as Integer
		def disclaimerTextWidth = omarImageWidth as Integer
		def disclaimerTextBufferedImage = textGeneratorService.serviceMethod(disclaimerText, "center", "000000", disclaimerTextHeight, disclaimerTextWidth)

		def disclaimerTextOffsetX = 0
		def disclaimerTextOffsetY = headerHeight + omarImageHeight + footerHeight as Integer
		templateGraphic.drawImage(disclaimerTextBufferedImage, disclaimerTextOffsetX, disclaimerTextOffsetY, null)

		templateGraphic.dispose()

		def tempFilesLocation = grailsApplication.config.export.workDir + "/"
                def tempFilesLocationAsFile = new File(tempFilesLocation)
                def templateFile = File.createTempFile("finishedProduct", ".png", tempFilesLocationAsFile)
		ImageIO.write(templateBufferedImage, "png", templateFile as File)
		return templateFile.toString()
	}
}
