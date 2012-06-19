package org.ossim.omar.app

class TemplateExportController 
{
	def grailsApplication
	def index = 
        {
		def acquisitionDate = params.acquisitionDate
		def countryCode = params.countryCode
		def imageId = params.imageId
		flash.imageURL = params.imageURL
		def mgrs = params.mgrs
		def northArrowAngle = params.northArrowAngle
		
		render(
			view:"templateExport.gsp", 
			model:
			[
				acquisitionDate: acquisitionDate, 
				countryCode: countryCode, 
				imageId: imageId,
				mgrs: mgrs,
				northArrowAngle: northArrowAngle
			]
		)
        }

	def export = 
	{
		def imageFile = flash.imageURL
		def logo = params.logo
		def line1 = params.line1
		def line2 = params.line2
		def line3 = params.line3
		def northAngle = params.northArrowAngle
		
 		def pathToImageMagick = grailsApplication.config.pathToImageMagick
                def logoFilesLocation = grailsApplication.config.logoFilesLocation
                def tempFilesLocation = grailsApplication.config.tempFilesLocation
                def scriptLocation = grailsApplication.config.templateExportScriptLocation
               
		def date = new Date().getTime() 
		def paramsFile = new File("${tempFilesLocation}${date}" + "paramsFile.txt")
		paramsFile.write("${pathToImageMagick}\n")
		paramsFile.append("${imageFile}\n")
		paramsFile.append("${logo}\n")
		paramsFile.append("${line1}\n")
		paramsFile.append("${line2}\n")
		paramsFile.append("${line3}\n")
		paramsFile.append("${northAngle}\n")
		paramsFile.append("${logoFilesLocation}\n")
		paramsFile.append("${tempFilesLocation}\n")
		paramsFile.append("${date}\n")
		
		def script = "${scriptLocation}templateExport.pl ${tempFilesLocation}${date}"+ "paramsFile.txt"
		def scriptProc = script.execute()
		scriptProc.waitFor()
		def fileName = scriptProc.text
		
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
		
		def removeParamsFile = "rm ${tempFilesLocation}${date}" + "paramsFile.txt"
		def removeParamsFileProc = removeParamsFile.execute()
		removeParamsFileProc.waitFor()
	}
}
