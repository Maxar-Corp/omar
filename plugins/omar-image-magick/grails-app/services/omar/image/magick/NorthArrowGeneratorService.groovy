package omar.image.magick

import org.codehaus.groovy.grails.web.mapping.LinkGenerator

class NorthArrowGeneratorService 
{
	LinkGenerator grailsLinkGenerator

	def DEBUG = false
	def grailsApplication

	def command
	def serviceMethod( def northArrowSize, def northAngle )
	{
		def date = new Date().getTime()

		def tempFilesLocation = grailsApplication.config.export.workDir + "/"
		def logoFilesLocation = grailsLinkGenerator.resource(absolute: true, dir: 'images', plugin: 'omar-image-magick') + "/"

		//#################################################################################################################
		//################################################## North Arrow ##################################################
		//#################################################################################################################
		if (DEBUG) { println "##### North Arrow #####" }	
	
		//########## Determine the size of the north arrow
		if (DEBUG) { println "Determine the size of the north arrow:" }
		def northArrowWidth = northArrowSize
		def northArrowHeight = northArrowSize
		if (DEBUG) { println "${northArrowWidth}x${northArrowHeight} pixels" }

		//########## Scale the north arrow
		if (DEBUG) { println "Scale the north arrow:" }
		command = [
				"convert", 
				"${logoFilesLocation}northArrow.png", 
				"-resize", 
				"${northArrowWidth}x${northArrowHeight}", 
				"${tempFilesLocation}${date}northArrowScaled.png"
		]
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//########## Rotate the north arrow
		if (DEBUG) { println "Rotate the north arrow:" }
		command = [
				"convert",
				"-alpha",
				"set",
				"-background",
				"none",
				"${tempFilesLocation}${date}northArrowScaled.png", 
				"-rotate", 
				"${northAngle}", 
				"${tempFilesLocation}${date}northArrowRotated.png"
		]
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//########## Crop the rotated north arrow
		if (DEBUG) { println "Crop the rotated north arrow:" }
		command = [
				"convert",
				"${tempFilesLocation}${date}northArrowRotated.png",
				"+repage",
				"-gravity",
				"center", 
				"-crop", "${northArrowWidth}x${northArrowHeight}+0+0",   
				"${tempFilesLocation}${date}northArrowRotated.png"
		]
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//#############################################################################################################################
		//################################################## Temporary File Deletion ##################################################
		//#############################################################################################################################

		//########## Delete the scaled north arrow file
		if (DEBUG) { println "Delete the scaled north arrow file:" }
		command = "rm ${tempFilesLocation}${date}northArrowScaled.png"
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		return "${tempFilesLocation}${date}northArrowRotated.png"
	}

	def executeCommand(def executableCommand)
	{
		def script = executableCommand.execute()
		script.waitFor()
		return script.text
	}
}
