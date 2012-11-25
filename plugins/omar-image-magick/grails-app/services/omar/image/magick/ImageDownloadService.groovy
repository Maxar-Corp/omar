package omar.image.magick

import org.codehaus.groovy.grails.web.mapping.LinkGenerator

class ImageDownloadService 
{
	LinkGenerator grailsLinkGenerator

	def DEBUG = false
	def grailsApplication

	def command
	def serviceMethod( def imageUrl, def markerLocations )
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

		//######################################################################################################################
		//################################################## Marker Placement ##################################################
		//######################################################################################################################
		if (DEBUG) { println "##### Marker Placement #####" }
		if (markerLocations[0] != "null")
		{
			if (DEBUG) { println "Define marker image file:" }
			def markerImageFileLocation = grailsLinkGenerator.resource(absolute: true, dir: '/js/img', file: 'marker-blue.png', plugin: 'openlayers')
			if (DEBUG) { println "${markerImageFileLocation}" }
			
			if (DEBUG) { println "Determine the number of markers:" }
			def numberOfMarkers = (markerLocations.length / 2) - 1
			if (DEBUG) { println "${numberOfMarkers + 1}" }

			if (DEBUG) { println "Add markers to the image file:" }
			for (i in 0..numberOfMarkers)
			{
				command = 
				[
					"composite",
					markerImageFileLocation,
					"-gravity",
					"NorthWest",
					"-geometry",
					"+${markerLocations[2 * i]}+${markerLocations[2 * i + 1]}",
					"${tempFilesLocation}${date}omarImage.tif",
					"${tempFilesLocation}${date}omarImage.tif"
				]
				if (DEBUG) { println "${command}" }
				executeCommand(command)
			}
		}
		else 
		{
			if (DEBUG) { println "No markers to place." } 
		}

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
