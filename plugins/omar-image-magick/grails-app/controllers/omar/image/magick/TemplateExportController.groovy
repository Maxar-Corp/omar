package omar.image.magick

class TemplateExportController
{
	def gradientGeneratorService
	def grailsApplication
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

	def export( )
	{
		def acquisitionDate = params.acquisitionDate
		def acquisitionDateTextColor = params.acquisitionDateTextColor
		def country = params.country
		def description = params.description
		def descriptionTextColor = params.descriptionTextColor
		def gradientColorBottom = params.gradientColorBottom
		def gradientColorTop = params.gradientColorTop
		def imageFile = params.imageURL
		def includeOverviewMap = params.includeOverviewMap
		def location = params.location
		def locationTextColor = params.locationTextColor
		def logo = params.logo
    		def northAngle = params.northArrowAngle
		def securityClassification = params.securityClassification
		def securityClassificationTextColor = params.securityClassificationTextColor
		if ("${securityClassification}" == "")
		{                
			securityClassification = "UNK"
		}
		def title = params.title
		def titleTextColor = params.titleTextColor

    		def fileName = templateExportService.serviceMethod( acquisitionDate, acquisitionDateTextColor, country, description, descriptionTextColor, gradientColorBottom, gradientColorTop, imageFile, includeOverviewMap, location, locationTextColor, logo, northAngle, securityClassification, securityClassificationTextColor, title, titleTextColor ) 

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

	def exportPreview( )
	{
		def acquisitionDate = params.acquisitionDate
		def acquisitionDateTextColor = params.acquisitionDateTextColor
		def country = params.country
		def description = params.description
		def descriptionTextColor = params.descriptionTextColor
		def gradientColorBottom = params.gradientColorBottom
		def gradientColorTop = params.gradientColorTop
		def imageFile = params.imageURL
		def includeOverviewMap = params.includeOverviewMap
		def location = params.location
		def locationTextColor = params.locationTextColor
		def logo = params.logo
		def northAngle = params.northArrowAngle
		def securityClassification = params.securityClassification
		def securityClassificationTextColor = params.securityClassificationTextColor
		if ("${securityClassification}" == "")
		{
			securityClassification = "UNK"
		}
		def title = params.title
		def titleTextColor = params.titleTextColor

		def fileName = templateExportService.serviceMethod( acquisitionDate, acquisitionDateTextColor, country, description, descriptionTextColor, gradientColorBottom, gradientColorTop, imageFile, includeOverviewMap, location, locationTextColor, logo, northAngle, securityClassification, securityClassificationTextColor, title, titleTextColor )

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

	def gradientGenerator()
	{
		def gradientColorTop = params.gradientColorTop
		def gradientColorBottom = params.gradientColorBottom
		def gradientHeight = params.gradientHeight

		def fileName = gradientGeneratorService.serviceMethod( gradientColorTop, gradientColorBottom, gradientHeight )

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
