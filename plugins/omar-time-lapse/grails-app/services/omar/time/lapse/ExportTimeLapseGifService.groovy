package omar.time.lapse

class ExportTimeLapseGifService 
{
	def grailsApplication

	def exportGif(def imageUrls)
	{
		def date = new Date().getTime()
		def tempFilesLocation = grailsApplication.config.export.workDir + "/"	

		def command = []
		command[0] = "convert"
		command += "-delay"
		command += "100"
		for (item in imageUrls) { command += item }
		command += "${tempFilesLocation}${date}timeLapse.gif"
		executeCommand(command)	

		return "${tempFilesLocation}${date}timeLapse.gif"
	}

	def executeCommand(def executableCommand)
	{
		def script = executableCommand.execute()
		script.waitFor()
		return script.text
	}
}
