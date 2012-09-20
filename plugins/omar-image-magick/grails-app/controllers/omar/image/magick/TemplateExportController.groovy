package omar.image.magick

class TemplateExportController
{
	def footerGradientGeneratorService
	def grailsApplication
	def headerGradientGeneratorService
	def northArrowGeneratorService
	def templateExportService

	def index( )
	{
		def acquisitionDate = params.acquisitionDate
		def centerGeo = params.centerGeo
		def countryCode = params.countryCode
		def imageId = params.imageId
		def imageUrl = params.imageURL
		def northArrowAngle = params.northArrowAngle
		def securityClassification = grailsApplication.config.security[grailsApplication.config.security.level].description

		render(
			view: "templateExport.gsp",
			model:
			[
				acquisitionDate: acquisitionDate,
				centerGeo: centerGeo,
				countryCode: countryCode,
				imageId: imageId,
				imageURL: imageUrl,
				northArrowAngle: northArrowAngle,
				securityClassification: securityClassification
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
		def imageFile = params.imageUrl
		def includeOverviewMap = params.includeOverviewMap
		def logo = params.logo
    		def northAngle = params.northArrowAngle

    		def fileName = templateExportService.serviceMethod( country, footerAcquisitionDateText, footerAcquisitionDateTextColor, footerLocationText, footerLocationTextColor, footerSecurityClassificationText, footerSecurityClassificationTextColor, gradientColorBottom, gradientColorTop, headerDescriptionText, headerDescriptionTextColor, headerSecurityClassificationText, headerSecurityClassificationTextColor, headerTitleText, headerTitleTextColor, imageFile, includeOverviewMap, logo, northAngle ) 

		def file = new File( "${fileName}" )
		if ( file.exists() )
		{
			response.setContentType( "application/octet-stream" )
			response.setHeader( "Content-disposition", "attachment; filename=${file.name}" )
			response.outputStream << file.bytes

			def removeImageFile = "rm ${file}"
			def removeImageFileProc = removeImageFile.execute()
			removeImageFileProc.waitFor()
		}
	}

	def footerGradientGenerator()
	{
		def gradientColorTop = params.gradientColorTop
		def gradientColorBottom = params.gradientColorBottom
		def gradientHeight = params.gradientHeight

		def fileName = footerGradientGeneratorService.serviceMethod( gradientColorTop, gradientColorBottom, gradientHeight )

		def file = new File( "${fileName}" )
		if ( file.exists() )
		{
			response.setHeader( "Content-length", "" + file.bytes.length )
			response.contentType = "image/png"
			response.outputStream << file.bytes
			response.outputStream.flush()

			def removeImageFile = "rm ${file}"
			def removeImageFileProc = removeImageFile.execute()
			removeImageFileProc.waitFor()
		}
	}

	def headerGradientGenerator()
	{
                def gradientColorTop = params.gradientColorTop
                def gradientColorBottom = params.gradientColorBottom
                def gradientHeight = params.gradientHeight

                def fileName = headerGradientGeneratorService.serviceMethod( gradientColorTop, gradientColorBottom, gradientHeight )

                def file = new File( "${fileName}" )
                if ( file.exists() )
                {
                        response.setHeader( "Content-length", "" + file.bytes.length )
                        response.contentType = "image/png"
                        response.outputStream << file.bytes
                        response.outputStream.flush()

                        def removeImageFile = "rm ${file}"
                        def removeImageFileProc = removeImageFile.execute()
                        removeImageFileProc.waitFor()
                }
        }

	def northArrowGenerator()
	{
		def northArrowSize = params.northArrowSize
		def northAngle = params.northAngle

		def fileName = northArrowGeneratorService.serviceMethod( northArrowSize, northAngle )

		def file = new File( "${fileName}" )
		if ( file.exists() )
		{
			response.setHeader( "Content-length", "" + file.bytes.length )
			response.contentType = "image/png"
			response.outputStream << file.bytes
			response.outputStream.flush()
			
			def removeImageFile = "rm ${file}"
			def removeImageFileProc = removeImageFile.execute()
			removeImageFileProc.waitFor()
		}
	}

}
