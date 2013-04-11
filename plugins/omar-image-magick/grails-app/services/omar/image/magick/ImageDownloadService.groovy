package omar.image.magick

import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.ossim.omar.core.Utility

class ImageDownloadService 
{
	def DEBUG = false

	def grailsApplication

	def serviceMethod(def imageUrl)
	{
        def command
		def date = new Date().getTime()
		def tempFilesLocation = grailsApplication.config.export.workDir + "/"
        def tempFilesLocationAsFile = new File(tempFilesLocation)
        def tempFileOmarImageTif = File.createTempFile("omarImage",
                ".tif", tempFilesLocationAsFile);
        def tempFileOmarImagePng = File.createTempFile("omarImage",
                ".png", tempFilesLocationAsFile);

		//##############################################################################################################################
		//############################################################ Image Download ##################################################
		//##############################################################################################################################
		if (DEBUG) { println "##### Image Download #####" }

		//########## Image filename once it is downloaded
		if (DEBUG) { println "Image filename once it is downloaded: " }
		def imageFile = tempFileOmarImagePng.toString();
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

        if(tempFileOmarImage.exists())
        {
            //########## Change the image to RGB colorspace
            if (DEBUG) { println "Change the image to RGB colorspace:" }
            command =
                [
                        "convert",
                        "${imageFile}",
                        "-type",
                        "TrueColor",
                        tempFileOmarImageTif.toString()
                ]
            if (DEBUG) { println "${command}" }
            executeCommand(command)
        }

		//#############################################################################################################################
		//################################################## Temporary File Deletion ##################################################
		//#############################################################################################################################

		//########## Delete the image file
		//if (DEBUG) { println "Delete the image file:" }
		//command = "rm ${imageFile}"
		//if (DEBUG) { println "${command}" }
		//executeCommand(command)
        tempFileOmarImagePng.delete()

		return tempFileOmarImageTif.tif()
	}

	def executeCommand(def executableCommand)
	{
        Utility.executeCommand(executableCommand, true).text
	}
}
