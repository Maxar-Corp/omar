package omar.image.magick

import org.codehaus.groovy.grails.web.mapping.LinkGenerator

class HeaderGeneratorService 
{
	LinkGenerator grailsLinkGenerator

	def DEBUG = false
	def grailsApplication
	def northArrowGeneratorService

	def serviceMethod(def gradientColorBottom, def gradientColorTop, def headerDescriptionText, def headerDescriptionTextColor, def headerSecurityClassificationText, def headerSecurityClassificationTextColor, def headerTitleText, def headerTitleTextColor, def imageHeight, def imageWidth, def logo)
	{
        def command
		def date = new Date().getTime()
		def tempFilesLocation = grailsApplication.config.export.workDir + "/"
		def logoFilesLocation = grailsLinkGenerator.resource(absolute: true, dir: 'images', plugin: 'omar-image-magick') + "/"
		def font = "web-app/fonts/ArialBold.ttf"

		//#######################################################################################################################
		//################################################## Header Generation ##################################################
		//#######################################################################################################################
		if (DEBUG) { println "##### Header Adjustment #####" }

		//########## Generate the blank header
		def headerHeight = 0.1 * imageHeight.toInteger()
		headerHeight = headerHeight.toInteger()
		def headerWidth = imageWidth
		headerWidth = headerWidth.toInteger()
		
		if (DEBUG) { println "Generate the blank header:" }
		command = [
				"convert", 
				"-size", 
				"${headerWidth}x${headerHeight}", 
				"gradient: #${gradientColorTop}-#${gradientColorBottom}", 
				"${tempFilesLocation}${date}header.png"
		]
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//###############################################################################################################
		//################################################## Logo Icon ##################################################
		//###############################################################################################################
		if (DEBUG) { println "##### Logo Icon #####" }

		//########## Scale the logo
		if (DEBUG) { println "Determine the width of the logo:" }
		def logoWidth = 0.8 * headerHeight
		logoWidth = logoWidth.toInteger()
		if (DEBUG) { println "${logoWidth} pixels" }

		if (DEBUG) { println "Determine the height of the logo:" }
		def logoHeight = logoWidth
		if (DEBUG) { println "${logoHeight} pixels" }

		if (DEBUG) { println "Scale the logo:" }
		command = [
				"convert", 
				"${logoFilesLocation}${logo}.png", 
				"-resize", 
				"${logoWidth}x${logoHeight}", 
				"${tempFilesLocation}${date}${logo}Scaled.png"
		]
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//########## Add the logo to the header
		if (DEBUG) { println "Determine the offset of the logo:" }
		def logoOffset = (headerHeight - logoHeight) / 2
		logoOffset = logoOffset.toInteger()
		if (DEBUG) { println "${logoOffset} pixels" }

		if (DEBUG) { println "Add the logo to the header:" }
		command = [
				"composite", 
				"${tempFilesLocation}${date}${logo}Scaled.png", 
				"-gravity", 
				"West", 
				"-geometry", 
				"+${logoOffset}+0", 
				"${tempFilesLocation}${date}header.png", 
				"${tempFilesLocation}${date}header.png"
		]
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//#################################################################################################################
		//################################################## Header Text ##################################################
		//#################################################################################################################
		if (DEBUG) { println "##### Header Text #####" }

		//########## Determine the maximum width for each line of text
		if (DEBUG) { println "Determine the maximum width for each line of text:" }
		def headerTextWidth = headerWidth - (2 * logoWidth) - (5 * logoOffset)
		if (DEBUG) { println "${headerTextWidth} pixels" }

		//########## Generate the header security classification line of text
		if (DEBUG) { println "Determine the height of the header security classification line of text:" }
		def headerSecurityClassificationTextHeight = 0.25 * logoHeight
		headerSecurityClassificationTextHeight = headerSecurityClassificationTextHeight.toInteger()
		if (DEBUG) { println "${headerSecurityClassificationTextHeight} pixels" }
		if (DEBUG) { println "Generate header security classification text:" }
		command = [
				"convert", 
				"-alpha",
				"set",
				"-background", 
				"none", 
				"-fill", 
				"#${headerSecurityClassificationTextColor}", 
				"-size", 
				"${headerTextWidth}x${headerSecurityClassificationTextHeight}", 
				"-gravity", 
				"West", 
				"-font",
				"${font}",
				"caption:${headerSecurityClassificationText}", 
				"${tempFilesLocation}${date}headerSecurityClassificationText.png"
		]
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//########## Generate the header title line of text
		if (DEBUG) { println "Determine the height of the header title line of text:" }
		def headerTitleTextHeight = 0.43 * logoHeight
		headerTitleTextHeight = headerTitleTextHeight.toInteger()
		if (DEBUG) { println "${headerTitleTextHeight} pixels" }
		if (DEBUG) { println "Generate the header title text:" }
		command = [
				"convert", 
				"-alpha",
				"set",
				"-background", 
				"none", 
				"-fill", 
				"#${headerTitleTextColor}", 
				"-size", 
				"${headerTextWidth}x${headerTitleTextHeight}", 
				"-gravity", 
				"West",
				"-font",
				"${font}", 
				"caption:${headerTitleText}", 
				"${tempFilesLocation}${date}headerTitleText.png"
		]
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//########## Generate the header description line of text
		if (DEBUG) { println "Determine the height of the header description line of text:" }
		def headerDescriptionTextHeight = 0.32 * logoHeight
		headerDescriptionTextHeight = headerDescriptionTextHeight.toInteger()
		if (DEBUG) { println "${headerDescriptionTextHeight} pixels" }
		if (DEBUG) { println "Generate the header description line of text:" }
		command = [
				"convert",
				"-alpha",
				"set",
				"-background", 
				"none", 
				"-fill", 
				"#${headerDescriptionTextColor}", 
				"-size", 
				"${headerTextWidth}x${headerDescriptionTextHeight}", 
				"-gravity", 
				"West", 
				"-font",
				"${font}",
				"caption:${headerDescriptionText}", 
				"${tempFilesLocation}${date}headerDescriptionText.png"
		]
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//#######################################################################################################################
		//################################################## Header Adjustment ##################################################
		//#######################################################################################################################
		if (DEBUG) { println "##### Header Adjustment #####" }

		//########## Add the header text to the header
		if (DEBUG) { println "Determine the header text offset:" }
		def headerTextOffset = 2 * logoOffset + logoWidth
		if (DEBUG) { println "${headerTextOffset} pixels" }
		
		if (DEBUG) { println "Add the security classification text to the header:" }
		def headerSecurityClassificationTextOffset = logoOffset
		command = [
                                "composite",
                                "${tempFilesLocation}${date}headerSecurityClassificationText.png",
                                "-gravity",
                                "NorthWest",
                                "-geometry",
                                "+${headerTextOffset}+${headerSecurityClassificationTextOffset}",
                                "${tempFilesLocation}${date}header.png",
                                "${tempFilesLocation}${date}header.png"
                ]
                if (DEBUG) { println "${command}" }
                executeCommand(command)

		if (DEBUG) { println "Add the title text to the header:" }
		def headerTitleTextOffset = headerSecurityClassificationTextOffset + headerSecurityClassificationTextHeight
		command = [
                                "composite",
                                "${tempFilesLocation}${date}headerTitleText.png",
                                "-gravity",
                                "NorthWest",
                                "-geometry",
                                "+${headerTextOffset}+${headerTitleTextOffset}",
                                "${tempFilesLocation}${date}header.png",
                                "${tempFilesLocation}${date}header.png"
                ]
                if (DEBUG) { println "${command}" }
                executeCommand(command)

		if (DEBUG) { println "Add the description text to the header:" }
		def headerDescriptionTextOffset = logoOffset
		command = [
                                "composite",
                                "${tempFilesLocation}${date}headerDescriptionText.png",
                                "-gravity",
                                "SouthWest",
                                "-geometry",
                                "+${headerTextOffset}+${headerDescriptionTextOffset}",
                                "${tempFilesLocation}${date}header.png",
                                "${tempFilesLocation}${date}header.png"
                ]
                if (DEBUG) { println "${command}" }
                executeCommand(command)

		//#################################################################################################################
		//################################################## North Arrow ##################################################
		//#################################################################################################################
		//if (DEBUG) { println "##### North Arrow #####" }	
	
		//########## Determine the size of the north arrow
		//if (DEBUG) { println "Determine the size of the north arrow:" }
		//def northArrowWidth = logoWidth
		//def northArrowHeight = logoHeight
		//if (DEBUG) { println "${northArrowWidth}x${northArrowHeight} pixels" }

		//def northArrowSize = northArrowWidth
		//def northArrowFilename = northArrowGeneratorService.serviceMethod( northArrowSize, northAngle, northArrowColor, northArrowBackgroundColor )

		//#######################################################################################################################
		//################################################## Header Adjustment ##################################################
		//#######################################################################################################################
		if (DEBUG) { println "##### Header Adjustment #####" }

		//########## Add the north arrow to the header
		//if (DEBUG) { println "Determine the north arrow offset:" }
		//def northArrowOffset = logoOffset;
		//if (DEBUG) { println "${northArrowOffset} pixels" }
		
		//if (DEBUG) { println "Add the north arrow to the header:"  }
		//command = [
		//		"composite", 
		//		"${northArrowFilename}", 
		//		"-gravity", 
		//		"East", 
		//		"-geometry", 
		//		"+${northArrowOffset}+0", 
		//		"${tempFilesLocation}${date}header.png", 
		//		"${tempFilesLocation}${date}header.png"
		//]
		//if (DEBUG) { println "${command}" }
		//executeCommand(command)

		//#############################################################################################################################
		//################################################## Temporary File Deletion ##################################################
		//#############################################################################################################################

		//########## Delete the header description text file
		if (DEBUG) { println "Delete the description text file:" }
		command = "rm ${tempFilesLocation}${date}headerDescriptionText.png"
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//########## Delete the header security classification text file
		if (DEBUG) { println "Delete the header security classification text file:" }
		command = "rm ${tempFilesLocation}${date}headerSecurityClassificationText.png"
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//########## Delete the header title text file
		if (DEBUG) { println "Delete the header title text file:" }
		command = "rm ${tempFilesLocation}${date}headerTitleText.png"
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//##########  Delete the north arrow rotated file
		//if (DEBUG) { println "Delete the north arrow rotated file:" }
		//command = "rm ${northArrowFilename}"
		//if (DEBUG) { println "${command}" }
		//executeCommand(command)
	
		//########## Delete the scaled logo file
		if (DEBUG) { println "Delete the scaled logo file:" }
		command = "rm ${tempFilesLocation}${date}${logo}Scaled.png"
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		return "${tempFilesLocation}${date}header.png"
	}

	def executeCommand(def executableCommand)
	{
        return Utility.executeCommand(executableCommand, true).text
	}
}
