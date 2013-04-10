package omar.image.magick

import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.ossim.omar.core.Utility

class TemplateExportService
{
	LinkGenerator grailsLinkGenerator

	def DEBUG = false
	def grailsApplication
	def northArrowGeneratorService

	def serviceMethod(def country, def footerFile, def headerFile, def imageFile, def imageHeight, def includeOverviewMap, def northArrowFile)
	{
        def command
		def date = new Date().getTime()
		def tempFilesLocation = grailsApplication.config.export.workDir + "/"
		def logoFilesLocation = grailsLinkGenerator.resource(absolute: true, dir: 'images', plugin: 'omar-image-magick') + "/"
		def mapFilesLocation = logoFilesLocation + "overviewMaps/"

		//#####################################################################################################################
		//################################################## Header Addition ##################################################
		//#####################################################################################################################
		if (DEBUG) { println "##### Header Addition #####" }

                //########## Add the header file to the image file
                if (DEBUG) { println "Add the header file to the image file:" }
                command = [
                                "convert",
                                "${headerFile}",
                                "${imageFile}",
				"${footerFile}",
                                "-append",
                                "${tempFilesLocation}${date}finishedProduct.png"
                ]
                if (DEBUG) { println "${command}" }
                executeCommand(command)

		//##################################################################################################################
		//################################################## Overview Map ##################################################
		//##################################################################################################################
		if (DEBUG) { println "##### Overview Map #####" }
	
		def overviewMapWidth = 0
		if (("${includeOverviewMap}".toString()).equals("true"))
		{
			//########## Determine the height of the overview map	
			if (DEBUG) { println "Determine the height of the overview map:" }
        		def overviewMapHeight = 0.2 * imageHeight.toInteger()
			if (DEBUG) { print "${overviewMapHeight} pixels" }
			
			//########## Scale the overview map
			if (DEBUG) { println "Scale the overview map:" }        
			command = [
					"convert",
					"${mapFilesLocation}${country}.gif", 
					"-resize", 
					"x${overviewMapHeight}",
					"${tempFilesLocation}${date}overviewMapScaled.png"
			]
        		if (DEBUG) { println "${command}" }
			executeCommand(command)

			//########## Add a shadow to the overview map
			if (DEBUG) { println "Generate a shadow image for the overview map:" }
			command = [
					"convert",
					"${tempFilesLocation}${date}overviewMapScaled.png",
					"-background",
					"black",
					"-shadow",
					"60x4+4+4",
					"${tempFilesLocation}${date}overviewMapScaledShadow.png"
			]
			if (DEBUG) { println "${command}" }
			executeCommand(command)

			if (DEBUG) { println "Add the shadow image to the overview map:" }        			
			command = [
					"convert",
					"-page",
					"+4+4",
					"${tempFilesLocation}${date}overviewMapScaled.png",
					"-matte",
					"${tempFilesLocation}${date}overviewMapScaledShadow.png",
					"+swap",
					"-background",
					"none",
					"-mosaic",
					"${tempFilesLocation}${date}overviewMapScaled.png"
			]
			if (DEBUG) { println "${command}" }
			executeCommand(command)

			//########## Determine the width of the overview map
			if (DEBUG) { println "Determine the width of the overview map:" }       
			command = [
					"identify",
					"-format",
					"%w",
					"${tempFilesLocation}${date}overviewMapScaled.png"
			]
			overviewMapWidth = executeCommand(command)
			overviewMapWidth = overviewMapWidth.replaceAll("\n", "")
			overviewMapWidth = overviewMapWidth.toInteger()
			if (DEBUG) { println "${overviewMapWidth} pixels" }
		}

		//#######################################################################################################################
		//################################################## Report Adjustment ##################################################
		//#######################################################################################################################
		if (DEBUG) { println "##### Report Adjustment #####" }
	
		//########## Add the overview map to the finished product
		if (("${includeOverviewMap}".toString()).equals("true"))
		{
			if (DEBUG) { println "Add the overview map to the finished product:" }			
			command = [
					"composite",
					"${tempFilesLocation}${date}overviewMapScaled.png",
					"-gravity",
					"NorthEast",
					"${tempFilesLocation}${date}finishedProduct.png",
					"${tempFilesLocation}${date}finishedProduct.png"
			]
			if (DEBUG) { println "${command}" }
			executeCommand(command)
		}
		else 
		{
			if (DEBUG) { println "The overview map is not included" }
		}

		//##########################################################################################################################
                //################################################## North Arrow Addition ##################################################
                //##########################################################################################################################
		if (DEBUG) { println "##### North Arrow Addition #####" }

		//########## Add the north arrow to the finished product
		if (DEBUG) { println "Determine the north arrow offset:" }
		def northArrowOffset = 0.01 * imageHeight.toInteger()
		if (DEBUG) { println "${northArrowOffset} pixels" }

		if (DEBUG) { println "Add the north arrow to the finished product:"  }
		command = [
			"composite",
			"${northArrowFile}",
			"-gravity",
			"NorthEast",
			"-geometry",
			"+${northArrowOffset + overviewMapWidth}+${northArrowOffset}",
			"${tempFilesLocation}${date}finishedProduct.png",
			"${tempFilesLocation}${date}finishedProduct.png"
		]
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//#############################################################################################################################
		//################################################## Temporary File Deletion ##################################################
		//#############################################################################################################################

		//########## Delete the footer file
		if (DEBUG) { println "Delete the footer file:" }
		command = "rm ${footerFile}"
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//########## Delete the header file
		if (DEBUG) { println "Delete the header file:" }
		command = "rm ${headerFile}"
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//########## Delete the overview map file
		if (DEBUG) { println "Delete the overview map file:" }
		command = "rm ${tempFilesLocation}${date}overviewMapScaled.png"
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//########## Delete the overview map shadow image file
		if (DEBUG) { println "Delete the overview map shadow image file:" }
		command = "rm ${tempFilesLocation}${date}overviewMapScaledShadow.png"
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//########## Delete the image file
		if (DEBUG) { println "Delete the image file:" }
		command = "rm ${imageFile}"
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//########## Delete the north arrow file
		if (DEBUG) { println "Delete the north arrow file:" }
		command = "rm ${northArrowFile}"
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		return "${tempFilesLocation}${date}finishedProduct.png"
	}

	def executeCommand(def executableCommand)
	{
        return Utility.executeCommand(executableCommand)
        /*
		def script = executableCommand.execute()
        def err = new ByteArrayOutputStream()
        def out = new ByteArrayOutputStream()
        script.consumeProcessOutput(out, err)
		script.waitFor()
		return out.toString()
		*/
	}
}
