package omar.image.magick

class TemplateExportController
{
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
		def country = params.country
		def description = params.description
		def imageFile = params.imageURL
		def includeOverviewMap = params.includeOverviewMap
		def location = params.location
		def logo = params.logo
    		def northAngle = params.northArrowAngle
		def securityClassification = params.securityClassification
		if ("${securityClassification}" == "")
		{                
			securityClassification = "UNK"
		}
		def title = params.title

    		def fileName = templateExportService.serviceMethod( acquisitionDate, country, description, imageFile, includeOverviewMap, location, logo, northAngle, securityClassification, title ) 

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
		def country = params.country
		def description = params.description
		def imageFile = params.imageURL
		def includeOverviewMap = params.includeOverviewMap
		def location = params.location
		def logo = params.logo
		def northAngle = params.northArrowAngle
		def securityClassification = params.securityClassification
		if ("${securityClassification}" == "")
		{
			securityClassification = "UNK"
		}
		def title = params.title

		def fileName = templateExportService.serviceMethod( acquisitionDate, country, description, imageFile, includeOverviewMap, location, logo, northAngle, securityClassification, title )

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
