package omar.image.magick

import java.awt.Color

import javax.imageio.ImageIO

import static groovyx.gpars.dataflow.Dataflow.task
import groovyx.gpars.dataflow.DataflowVariable

class TemplateExportController
{
	def exportAnimationService
	def gradientGeneratorService
	def grailsApplication
	def northArrowGeneratorService
	def templateExportService

	def index()
	{
		def securityClassification = grailsApplication.config.security[grailsApplication.config.security.level].description

		def countryCode = params.countryCode
		def footerAcquisitionDateTextArray = params.footerAcquisitionDateText?.split(",")
		def footerLocationTextArray = params.footerLocationText?.split(",")

		def footerSecurityClassificationTextArray = []
		footerLocationTextArray.eachWithIndex
		{
			obj, i -> footerSecurityClassificationTextArray[i] = securityClassification
		}

		def format = params.format
		def headerDescriptionTextArray = params.headerDescriptionText?.split(",")
	
		def headerSecurityClassificationTextArray = []
		headerDescriptionTextArray.eachWithIndex 
		{
			obj, i -> headerSecurityClassificationTextArray[i] = securityClassification
		}

		def headerTitleTextArray = params.headerTitleText?.split(",")
		def imageUrlArray = params.imageUrl?.split(">")
		def northAngleArray = params.northAngle?.split(",")

		render(
			view: "templateExport.gsp",
			model:
			[
				countryCode: countryCode,
				footerAcquisitionDateTextArray: footerAcquisitionDateTextArray,
				footerLocationTextArray: footerLocationTextArray,
				footerSecurityClassificationTextArray: footerSecurityClassificationTextArray,
				format: format,
				headerDescriptionTextArray: headerDescriptionTextArray,
				headerSecurityClassificationTextArray: headerSecurityClassificationTextArray,
				headerTitleTextArray: headerTitleTextArray,
				imageUrlArray: imageUrlArray,
				northAngleArray: northAngleArray
			]
		)
	}

	def export()
	{
		def country = params.country
		def footerAcquisitionDateText = params.footerAcquisitionDateText	
		def footerAcquisitionDateTextColor = params.footerAcquisitionDateTextColor
		def footerLocationText = params.footerLocationText
		def footerLocationTextColor = params.footerLocationTextColor
		def footerSecurityClassificationText = params.footerSecurityClassificationText
		def footerSecurityClassificationTextColor = params.footerSecurityClassificationTextColor
		def gradientColorBottom = params.gradientColorBottom
		def gradientColorTop = params.gradientColorTop
		def headerDescriptionText = params.headerDescriptionText
		def headerDescriptionTextColor = params.headerDescriptionTextColor
		def headerTitleText = params.headerTitleText
		def headerTitleTextColor = params.headerTitleTextColor
		def headerSecurityClassificationText = params.headerSecurityClassificationText
		def headerSecurityClassificationTextColor = params.headerSecurityClassificationTextColor
		def imageUrl = params.imageUrl
		def includeOverviewMap = params.includeOverviewMap
		def logo = params.logo
		def northAngle = params.northArrowAngle
		def northArrowColor = params.northArrowColor
		def northArrowBackgroundColor = params.northArrowBackgroundColor
		def northArrowSize = params.northArrowSize

		def templateImageFilename = templateExportService.serviceMethod(country, footerAcquisitionDateText, footerAcquisitionDateTextColor, footerLocationText, footerLocationTextColor, footerSecurityClassificationText, footerSecurityClassificationTextColor, headerDescriptionText, headerDescriptionTextColor, headerSecurityClassificationText, headerSecurityClassificationTextColor, headerTitleText, headerTitleTextColor, imageUrl, includeOverviewMap, logo, northAngle, northArrowColor, northArrowBackgroundColor)
		render templateImageFilename
		
	}

	def flipBookGenerator()
	{
		def format = params.format
		def imageFileNameArray = params.fileNames?.split(">")
                
		def finishedProductFileName = exportAnimationService.export(imageFileNameArray, format)
		render finishedProductFileName
        }

	def gradientGenerator()
	{
		def gradientHeight = params.gradientHeight
		def gradientImage = gradientGeneratorService.serviceMethod(Color.BLACK, gradientHeight, Color.GRAY)

		response.contentType = "image/png"
		ImageIO.write(gradientImage, 'png', response.outputStream)
	}

	def northArrowGenerator()
	{
		def northAngle = params.northAngle
		def northArrowColor = params.northArrowColor
                def northArrowBackgroundColor = params.northArrowBackgroundColor
		def northArrowSize = params.northArrowSize
		

		def northArrowImage = northArrowGeneratorService.serviceMethod(northAngle, northArrowColor, northArrowBackgroundColor, northArrowSize)
		response.contentType = "image/png"
		ImageIO.write(northArrowImage, "png", response.outputStream)
	}

	def viewProduct()
	{
		def fileName = params.fileName
		def file = new File( "${fileName}" )
		if ( file.exists() )
		{
			response.setContentType( "application/octet-stream" )
			response.setHeader( "Content-disposition", "attachment; filename=${file.name}" )
			response.outputStream << file.bytes

            file.delete()
		}
	}

}
