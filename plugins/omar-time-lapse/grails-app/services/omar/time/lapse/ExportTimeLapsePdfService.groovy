package omar.time.lapse

class ExportTimeLapsePdfService 
{
	def grailsApplication

	def serviceMethod(def imageUrls) 
	{
		def date = new Date().getTime()
		def tempFilesLocation = grailsApplication.config.export.workDir + "/"
	
		def command = []
		command[0] = "convert"
		for (item in imageUrls)
		{
			command += item
		}
		command += "${tempFilesLocation}${date}timeLapse.pdf"
		
		executeCommand(command)
                return "${tempFilesLocation}${date}timeLapse.pdf"
	}

	def executeCommand(def executableCommand)
	{
		def script = executableCommand.execute()
		script.waitFor()
		return script.text
	}
}
