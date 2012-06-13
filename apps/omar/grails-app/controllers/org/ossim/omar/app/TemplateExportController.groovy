package org.ossim.omar.app

class TemplateExportController 
{
	def grailsApplication
	def index = 
        {
		def acquisitionDate = params.acquisitionDate
		def countryCode = params.countryCode
		def imageId = params.imageId
		def imageURL = params.imageURL
		def mgrs = params.mgrs
		def northArrowAngle = params.northArrowAngle
		
		render(
			view:"templateExport.gsp", 
			model:
			[
				acquisitionDate: acquisitionDate, 
				countryCode: countryCode, 
				imageId: imageId,
				imageURL: imageURL,
				mgrs: mgrs,
				northArrowAngle: northArrowAngle
			]
		)
        }

	def export = 
	{
		def imageFile = params.imageURL
		//println "${imageFile}"
		def logo = params.logo
		//prinln "${logo}"
		def line1 = params.line1
		//println "${line1}"
		def line2 = params.line2
		//println "${line2}"
		def line3 = params.line3
		//println "${line3}"
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
		//println "${fileName}"
		
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
