package omar.image.magick
import org.ossim.omar.core.Utility

class HeaderGradientGeneratorService 
{
	def DEBUG = false
	def grailsApplication

	def serviceMethod(def gradientColorTop, def gradientColorBottom, def gradientHeight)
	{
		def date = new Date().getTime()
		def tempFilesLocation = grailsApplication.config.export.workDir + "/"
        def tempFilesLocationAsFile = new File(tempFilesLocation)
        def tempFileGradient = File.createTempFile("gradient",
                ".png", tempFilesLocationAsFile);
        if (tempFilesLocationAsFile.exists())
        {

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

		return ""
	}

	def executeCommand(def executableCommand)
	{
        return Utility.executeCommand(executableCommand, true).text
	}
}
