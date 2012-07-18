package omar.image.magick

class TemplateExportController 
{	
	def templateExportService
	def index = 
        {
		def acquisitionDate = params.acquisitionDate
		def countryCode = params.countryCode
		def imageId = params.imageId
		def imageURL = params.imageURL
		def mgrs = params.mgrs
		def northArrowAngle = params.northArrowAngle

		def securityClassification = "UNK"
		
		render(
			view:"templateExport.gsp", 
			model:
			[
				acquisitionDate: acquisitionDate, 
				countryCode: countryCode, 
				imageId: imageId,
				imageURL: imageURL,
				mgrs: mgrs,
				northArrowAngle: northArrowAngle,
				securityClassification: securityClassification
			]
		)
        }

	def export = 
	{
		def imageFile = params.imageURL
		def logo = params.logo
		def line1 = params.line1
		def line2 = params.line2
		def line3 = params.line3
		def includeOutlineMap = params.includeOutlineMap
		def includeOverviewMap = params.includeOverviewMap
		def country = params.country
		def northAngle = params.northArrowAngle
		def securityClassification = params.securityClassification	
	
 		def fileName = templateExportService.serviceMethod(imageFile, logo, line1, line2, line3, includeOutlineMap, includeOverviewMap, country, northAngle, securityClassification)

		def file = new File("${fileName}")
		if (file.exists())
		{
			response.setContentType("application/octet-stream")
			response.setHeader("Content-disposition", "filename=${file.name}")
			response.outputStream << file.bytes

			def removeImageFile = "rm ${file}"
			def removeImageFileProc = removeImageFile.execute()
			removeImageFileProc.waitFor()
		}
	}

	def exportPreview =
	{
		def imageFile = params.imageURL
		def logo = params.logo
		def line1 = params.line1
		def line2 = params.line2
		def line3 = params.line3
		def includeOutlineMap = params.includeOutlineMap
		def includeOverviewMap = params.includeOverviewMap
		def country = params.country
		def northAngle = params.northArrowAngle
		def securityClassification = params.securityClassification
	
		def fileName = templateExportService.serviceMethod(imageFile, logo, line1, line2, line3, includeOutlineMap, includeOverviewMap, country, northAngle, securityClassification)

		def file = new File("${fileName}")
		if (file.exists())
		{
			response.setHeader("Content-length", "" + file.bytes.length)
			response.contentType = "image/png"
			response.outputStream << file.bytes
			response.outputStream.flush()

			def removeImageFile = "rm ${file}"
			def removeImageFileProc = removeImageFile.execute()
			removeImageFileProc.waitFor()
		}
        }
}
