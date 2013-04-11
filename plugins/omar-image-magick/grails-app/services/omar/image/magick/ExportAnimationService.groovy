package omar.image.magick
import org.ossim.omar.core.Utility

class ExportAnimationService 
{
	def grailsApplication

	def export(def imageFileNames, def format) 
	{
		def date = new Date().getTime()
		def tempFilesLocation = grailsApplication.config.export.workDir + "/"
        def tempFilesLocationAsFile = new File(tempFilesLocation)
        def tempFileTimeLapse = File.createTempFile("timeLapse",
                ".${format}", tempFilesLocationAsFile);

		def command = []
		command[0] = "convert"
		for (item in imageFileNames) { command += item }
		command += tempFileNorthArrow.toString()
		executeCommand(command)

		def file
		for (item in imageFileNames)
		{
			file = new File( "${item}" )
			if ( file.exists() )
			{
                file.delete()
				//command = "rm ${file}"
				//executeCommand(command)
			}
		}

                return tempFileTimeLapse.toString()
	}

	def executeCommand(def executableCommand)
	{
        return Utility.executeCommand(executableCommand, true).text
	}
}
