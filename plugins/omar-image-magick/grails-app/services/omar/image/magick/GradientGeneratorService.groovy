package omar.image.magick
import org.ossim.omar.core.Utility

class GradientGeneratorService 
{
	def DEBUG = true
	def grailsApplication

	def serviceMethod(def gradientColorTop, def gradientColorBottom, def gradientHeight)
	{
		def date = new Date().getTime()
		def tempFilesLocation = grailsApplication.config.export.workDir + "/"
        def tempFilesLocationAsFile = new File(tempFilesLocation)
        def tempFileGradient = File.createTempFile("gradient",
                ".png", tempFilesLocationAsFile);

		def command = [
				"convert", 
				"-size", 
				"1x${gradientHeight}", 
				"gradient: #${gradientColorTop}-#${gradientColorBottom}",
                tempFileGradient.toString()
		]
		executeCommand(command)

		return tempFileGradient.toString()
	}

	def executeCommand(def executableCommand)
	{
        return Utility.executeCommand(executableCommand, true).text
	}
}
