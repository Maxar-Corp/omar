package omar.image.magick

import org.codehaus.groovy.grails.web.mapping.LinkGenerator

class ImageDownloadService 
{
	def DEBUG = false

	def grailsApplication

	def command
	def serviceMethod(def imageUrl)
	{ 
		def date = new Date().getTime()
		def tempFilesLocation = grailsApplication.config.export.workDir + "/"

		//##############################################################################################################################
		//############################################################ Image Download ##################################################
		//##############################################################################################################################
		if (DEBUG) { println "##### Image Download #####" }

		//########## Image filename once it is downloaded
		if (DEBUG) { println "Image filename once it is downloaded: " }
		def imageFile = "${tempFilesLocation}${date}omarImage.png";
		if (DEBUG) { println "${imageFile}" }

		//########## Download the image file
		if (DEBUG) { println "Download the image file:" }
		command = 
		[
				"curl", 
				"${imageUrl}", 
				"-o", 
				"${imageFile}"
		]
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//########## Change the image to RGB colorspace
		if (DEBUG) { println "Change the image to RGB colorspace:" }
		command = 
		[
				"convert",
				"${imageFile}",
				"-type",
				"TrueColor",
				"${tempFilesLocation}${date}omarImage.tif"
		]
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//#############################################################################################################################
		//################################################## Temporary File Deletion ##################################################
		//#############################################################################################################################

		//########## Delete the image file
		if (DEBUG) { println "Delete the image file:" }
		command = "rm ${imageFile}"
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		return "${tempFilesLocation}${date}omarImage.tif"
	}

	def executeCommand(def executableCommand)
	{
		def script = executableCommand.execute()
		script.waitFor()
		return script.text
	}
}
