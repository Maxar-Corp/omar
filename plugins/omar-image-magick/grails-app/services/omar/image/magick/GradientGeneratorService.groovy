package omar.image.magick

class GradientGeneratorService 
{
	def DEBUG = true
	def grailsApplication

	def serviceMethod(def gradientColorTop, def gradientColorBottom, def gradientHeight)
	{
		def date = new Date().getTime()
		def tempFilesLocation = grailsApplication.config.export.workDir + "/"

		def command = [
				"convert", 
				"-size", 
				"1x${gradientHeight}", 
				"gradient: #${gradientColorTop}-#${gradientColorBottom}", 
				"${tempFilesLocation}${date}gradient.png"
		]
		executeCommand(command)

		return "${tempFilesLocation}${date}gradient.png"
	}

	def executeCommand(def executableCommand)
	{
        return Utility.executeCommand(executableCommand, true).text
	}
}
