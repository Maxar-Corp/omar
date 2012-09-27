package omar.image.magick

import org.codehaus.groovy.grails.web.mapping.LinkGenerator

class TemplateExportOldService 
{

	LinkGenerator grailsLinkGenerator

	def DEBUG = false
	def grailsApplication
	def northArrowGeneratorService

	def command
	def serviceMethod( def country, def footerAcquisitionDateText, def footerAcquisitionDateTextColor, def footerLocationText, def footerLocationTextColor, def footerSecurityClassificationText, def footerSecurityClassificationTextColor, def gradientColorBottom, def gradientColorTop, def headerDescriptionText, def headerDescriptionTextColor, def headerSecurityClassificationText, def headerSecurityClassificationTextColor, def headerTitleText, def headerTitleTextColor, def imageUrl, def includeOverviewMap, def logo, def northAngle, def northArrowBackgroundColor, def northArrowColor )
	{
		def date = new Date().getTime()

		def tempFilesLocation = grailsApplication.config.export.workDir + "/"
		def logoFilesLocation = grailsLinkGenerator.resource(absolute: true, dir: 'images', plugin: 'omar-image-magick') + "/"
		def mapFilesLocation = logoFilesLocation + "overviewMaps/"
		def font = "web-app/fonts/ArialBold.ttf"

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
		command = [
				"curl", 
				"${imageUrl}", 
				"-o", 
				"${imageFile}"
		]
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//########## Change the image to RGB colorspace
		if (DEBUG) { println "Change the image to RGB colorspace:" }
		command = [
				"convert",
				"${imageFile}",
				"-type",
				"TrueColor",
				"${tempFilesLocation}${date}omarImage.tif"
		]
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//################################################################################################################################
		//############################################################ Image Dimensions ##################################################
		//################################################################################################################################
		if (DEBUG) { println "##### Image Dimensions #####" }

		//########## Determine the width of the image
		if (DEBUG) { println "Determine the width of the image:" }
		command = [
				"identify", 
				"-format", 
				"%w", 
				"${tempFilesLocation}${date}omarImage.tif"
		]
		if (DEBUG) { println "${command}" }
		def imageWidth = executeCommand(command)
		imageWidth = imageWidth.replaceAll("\n", "")
		imageWidth = imageWidth.toInteger()
		if (DEBUG) { println "${imageWidth} pixels" }

		//########## Determine the height of the image
		if (DEBUG) { println "Determine the height of the image: " }
		command = [
				"identify", 
				"-format", 
				"%h", 
				"${tempFilesLocation}${date}omarImage.tif"
		]
		if (DEBUG) { println "${command}"; }
		def imageHeight = executeCommand(command)
		imageHeight = imageHeight.replaceAll("\n", "")
		imageHeight = imageHeight.toInteger()
		if (DEBUG) { println "${imageHeight} pixels" }

		//#######################################################################################################################
		//################################################## Header Adjustment ##################################################
		//#######################################################################################################################
		if (DEBUG) { println "##### Header Adjustment #####" }

		//########## Generate blank header
		if (DEBUG) { println "Determine the width of the header:" }
		def headerWidth = imageWidth
		if (DEBUG) { println "${headerWidth} pixels" }
		
		if (DEBUG) { println "Determine the height of the header:" }
		def headerHeight = 0.1 * imageHeight
		headerHeight = headerHeight.toInteger()
		if (DEBUG) { println "${headerHeight} pixels" }

		if (DEBUG) { println "Generate the header:" }
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

		//##################################################################################################################
		//################################################## Overview Map ##################################################
		//##################################################################################################################
		if (DEBUG) { println "##### Overview Map #####" }
		def overviewMapWidth = 0

		if (("${includeOverviewMap}".toString()).equals("true"))
		{
			//########## Determine the height of the overview map	
			if (DEBUG) { println "Determine the height of the overview map:" }
        		def overviewMapHeight = 0.2 * imageHeight
			overviewMapHeight = overviewMapHeight.toInteger()
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

		//#################################################################################################################
		//################################################## Header Text ##################################################
		//#################################################################################################################
		if (DEBUG) { println "##### Header Text #####" }

		//########## Determine the maximum width for each line of text
		if (DEBUG) { println "Determine the maximum width for each line of text:" }
		def headerTextWidth = headerWidth - (2 * logoWidth) - (5 * logoOffset) - overviewMapWidth
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

		//########## Combine all lines of header text
		if (DEBUG) { println "Combine all lines of header text:" }
		command = [
				"convert", 
				"${tempFilesLocation}${date}headerSecurityClassificationText.png", 
				"${tempFilesLocation}${date}headerTitleText.png", 
				"${tempFilesLocation}${date}headerDescriptionText.png", 
				"-append", 
				"${tempFilesLocation}${date}headerText.png"
		]
		if (DEBUG) { println "${command}" }
		//executeCommand(command)

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
		if (DEBUG) { println "##### North Arrow #####" }	
	
		//########## Determine the size of the north arrow
		if (DEBUG) { println "Determine the size of the north arrow:" }
		def northArrowWidth = logoWidth
		def northArrowHeight = logoHeight
		if (DEBUG) { println "${northArrowWidth}x${northArrowHeight} pixels" }

		def northArrowSize = northArrowWidth
		def northArrowFilename = northArrowGeneratorService.serviceMethod( northArrowSize, northAngle, northArrowColor, northArrowBackgroundColor )

		//#######################################################################################################################
		//################################################## Header Adjustment ##################################################
		//#######################################################################################################################
		if (DEBUG) { println "##### Header Adjustment #####" }

		//########## Add the north arrow to the header
		if (DEBUG) { println "Determine the north arrow offset:" }
		def northArrowOffset = logoOffset;
		if (DEBUG) { println "${northArrowOffset} pixels" }
		
		if (DEBUG) { println "Add the north arrow to the header:"  }
		command = [
				"composite", 
				"${northArrowFilename}", 
				"-gravity", 
				"East", 
				"-geometry", 
				"+${northArrowOffset}+0", 
				"${tempFilesLocation}${date}header.png", 
				"${tempFilesLocation}${date}header.png"
		]
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//#######################################################################################################################
		//################################################## Header Adjustment ##################################################
		//#######################################################################################################################
		if (DEBUG) { println "##### Header Adjustment #####" }

		//########## Add the header to the image
		if (DEBUG) { println "Add the header to the image:" }
		command = [
				"convert",
				"${tempFilesLocation}${date}header.png",
				"${tempFilesLocation}${date}omarImage.tif",
				"-append",
				"${tempFilesLocation}${date}finishedProduct.png"
		]
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//#######################################################################################################################
		//################################################## Report Adjustment ##################################################
		//#######################################################################################################################
		if (DEBUG) { println "##### Report Adjustment #####" }
		//########## Add the overview map to the finished product
		if (("${includeOverviewMap}".toString()).equals("true"))
		{
			def overviewMapOffset = (imageWidth - headerWidth) / 2 + northArrowWidth + 2 * northArrowOffset
			if (DEBUG) { println "Add the overview map to the finished product:" }			
			command = [
					"composite",
					"${tempFilesLocation}${date}overviewMapScaled.png",
					"-gravity",
					"NorthEast",
					"-geometry",
					"+${overviewMapOffset}+0",
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

		//######################################################################################################################
		//################################################## FooterAdjustment ##################################################
		//######################################################################################################################
		if (DEBUG) { print "##### Footer Adjustment #####" }

		//########## Determine the footer size
		if (DEBUG) { println "Determine the footer size:" }
		def footerWidth = imageWidth
		footerWidth = footerWidth.toInteger()
		def footerHeight = 0.035 * imageHeight
		footerHeight = footerHeight.toInteger()
		if (DEBUG) { println "${footerWidth}x${footerHeight} pixels" }

		//########## Generate the blank footer
		if (DEBUG) { println "Generate the blank footer:" }
		command = [
                                "convert",
                                "-size",
                                "${footerWidth}x${footerHeight}",
                                "gradient: #${gradientColorTop}-#${gradientColorBottom}",
                                "${tempFilesLocation}${date}footer.png"
                ]
                if (DEBUG) { println "${command}" }
                executeCommand(command)

		//########## Determine the footer text size
		if (DEBUG) { println "Determine the footer text size:" }
		def footerTextWidth = footerWidth / 3
		footerTextWidth = footerTextWidth.toInteger()
		def footerTextHeight = footerHeight
		footerTextHeight = footerTextHeight.toInteger()
		if (DEBUG) { println "${footerTextWidth}x${footerTextHeight} pixels" }

		//########## Generate the footer security classification text
		if (DEBUG) { println "Generate the footer security classification text:" }
		command = [
                                "convert",
				"-alpha",
				"set",
                                "-background",
                                "none",
                                "-fill",
                                "#${footerSecurityClassificationTextColor}",
                                "-size",
                                "${footerTextWidth}x${footerTextHeight}",
                                "-gravity",
                                "West",
				"-font",
				"${font}",
                                "caption: ${footerSecurityClassificationText}",
                                "${tempFilesLocation}${date}footerSecurityClassificationText.png"
                ]
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//########## Add the footer security classification text to the info banner
		if (DEBUG) { println "Add the footer security classification text to the footer:" }
		command = [
                                "composite",
                                "${tempFilesLocation}${date}footerSecurityClassificationText.png",
                                "-gravity",
                                "West",
                                "${tempFilesLocation}${date}footer.png",
                                "${tempFilesLocation}${date}footer.png"
                ]
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//########## Generate the footer location text
		if (DEBUG) { println "Generate the footer location text:" } 
		command = [
                                "convert",
				"-alpha",
				"set",
                                "-background",
                                "none",
                                "-fill",
                                "#${footerLocationTextColor}",
                                "-size",
                                "${footerTextWidth}x${footerTextHeight}",
                                "-gravity",
                                "Center",
				"-font",
				"${font}",
                                "caption:${footerLocationText}",
                                "${tempFilesLocation}${date}footerLocationText.png"
                ]
		if (DEBUG) { println "${command}" }
                executeCommand(command)

		//########## Add the footer location text to the footer
                if (DEBUG) { println "Add the footer location text to the footer:" }
                command = [
                                "composite",
                                "${tempFilesLocation}${date}footerLocationText.png",
                                "-gravity",
                                "Center",
                                "${tempFilesLocation}${date}footer.png",
                                "${tempFilesLocation}${date}footer.png"
                ]
		if (DEBUG) { println "${command}" }
                executeCommand(command)

		//########## Generate the footer acquisition date text
                if (DEBUG) { println "Generate the footer acquisition date text:" }
		command = [
                                "convert",
				"-alpha",
				"set",
                                "-background",
                                "none",
                                "-fill",
                                "#${footerAcquisitionDateTextColor}",
                                "-size",
                                "${footerTextWidth}x${footerTextHeight}",
                                "-gravity",
                                "East",
				"-font",
				"${font}",
                                "caption:${footerAcquisitionDateText} ",
                                "${tempFilesLocation}${date}footerAcquisitionDateText.png"
                ]
		if (DEBUG) { println "${command}" }
                executeCommand(command)

		//########## Add the footer acquisition date text to the footer
		if (DEBUG) { println "Add the footer banner acquisition date text to the footer:" }
                command = [
                                "composite",
                                "${tempFilesLocation}${date}footerAcquisitionDateText.png",
                                "-gravity",
                                "East",
                                "${tempFilesLocation}${date}footer.png",
                                "${tempFilesLocation}${date}footer.png"
                ]
		if (DEBUG) { println "${command}" }
                executeCommand(command)

		//########## Add the footer to the finished product
		if (DEBUG) { println "Add the footer to the finished product:" }
		command = [
                                "convert",
                                "${tempFilesLocation}${date}finishedProduct.png",
                                "${tempFilesLocation}${date}footer.png",
                                "-append",
                                "${tempFilesLocation}${date}finishedProduct.png"
                ]
		if (DEBUG) { println "${command}" }
                executeCommand(command)

		//####################################################################################################################
		//################################################## Disclaimer Text #################################################
		//####################################################################################################################
		if (DEBUG) { println "##### Disclaimer Text #####" }
		//########## Generate disclaimer text
		def disclaimerTextWidth = imageWidth
		def disclaimerTextHeight = 0.035 * imageHeight
		disclaimerTextHeight = disclaimerTextHeight.toInteger()
		def disclaimerText = "Not an intelligence product  //  For informational use only  //  Not certified for targeting"
		if (DEBUG) { println "Generate disclaimer text:" }
		command = [
				"convert", 
				"${tempFilesLocation}${date}finishedProduct.png", 
				"-background", 
				"yellow", 
				"-fill", 
				"black", 
				"-size", 
				"${disclaimerTextWidth}x${disclaimerTextHeight}", 
				"-gravity", 
				"center", 
				"-font",
				"${font}",
				"label:${disclaimerText}", 
				"-append", 
				"${tempFilesLocation}${date}finishedProduct.png"
		]
		if (DEBUG) { println "${command}" } 
		executeCommand(command)

		//#############################################################################################################################
		//################################################## Temporary File Deletion ##################################################
		//#############################################################################################################################

                //########## Delete the footer file
                if (DEBUG) { println "Delete the footer file:" }
                command = "rm ${tempFilesLocation}${date}footer.png"
                if (DEBUG) { println "${command}" }
                executeCommand(command)

                //########## Delete the footer acquisition date text file
                if (DEBUG) { println "Delete the footer acquisition date text file:" }
                command = "rm ${tempFilesLocation}${date}footerAcquisitionDateText.png"
                if (DEBUG) { println "${command}" }
                executeCommand(command)

                //########## Delete the footer location text file
                if (DEBUG) { println "Delete the footer location text file:" }
                command = "rm ${tempFilesLocation}${date}footerLocationText.png"
                if (DEBUG) { println "${command}" }
                executeCommand(command)

                //########## Delete the footer security classification text file
                if (DEBUG) { println "Delete the footer security classification text file:" }
                command = "rm ${tempFilesLocation}${date}footerSecurityClassificationText.png"
                if (DEBUG) { println "${command}" }
                executeCommand(command)

		//########## Delete the header file
		if (DEBUG) { println "Delete the header file:" }
		command = "rm ${tempFilesLocation}${date}header.png"
		if (DEBUG) { println "${command}" }
		executeCommand(command)

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

		//########## Delete the header text file
		if (DEBUG) { println "Delete the header text file:" }
		command = "rm ${tempFilesLocation}${date}headerText.png"
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//########## Delete the header title text file
		if (DEBUG) { println "Delete the header title text file:" }
		command = "rm ${tempFilesLocation}${date}headerTitleText.png"
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//##########  Delete the north arrow rotated file
		if (DEBUG) { println "Delete the north arrow rotated file:" }
		command = "rm ${northArrowFilename}"
		if (DEBUG) { println "${command}" }
		executeCommand(command)
	
		//########## Delete the image file
		if (DEBUG) { println "Delete the image file:" }
		command = "rm ${tempFilesLocation}${date}omarImage.tif"
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

		//########## Delete the scaled logo file
		if (DEBUG) { println "Delete the scaled logo file:" }
		command = "rm ${tempFilesLocation}${date}${logo}Scaled.png"
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//########## Delete the image file
		if (DEBUG) { println "Delete the image file:" }
		command = "rm ${imageFile}"
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		return "${tempFilesLocation}${date}finishedProduct.png"
	}

	def executeCommand(def executableCommand)
	{
		def script = executableCommand.execute()
		script.waitFor()
		return script.text
	}

	//North Arrow Creation
	//def northArrowSize = 2000
	//def strokeWidth = 0.035 * northArrowSize

	//def northArrowCircleBaseCenterX = northArrowSize / 2
	//northArrowCircleBaseCenterX = northArrowCircleBaseCenterX.toInteger()
	//def northArrowCircleBaseCenterY = northArrowSize / 2
	//northArrowCircleBaseCenterY = northArrowCircleBaseCenterY.toInteger()
	//command = [
	//		"convert",
	//		"-size",
	//		"${northArrowSize}x${northArrowSize}",
	//		"xc: #00000000",
	//		"-fill",
	//		"black",
	//		"-stroke",
	//		"black",
	//		"-draw",
	//		"circle ${northArrowCircleBaseCenterX},${northArrowCircleBaseCenterY} ${northArrowCircleBaseCenterX},${northArrowSize}",
	//		"${tempFilesLocation}${date}northArrow.png"			
	//]
	//executeCommand(command)

	//def northArrowInnerCircleSize = 0.9 * northArrowSize
	//northArrowInnerCircleSize = northArrowInnerCircleSize.toInteger()
	//command = [
	//		"convert",
	//		"-size",
	//		"${northArrowInnerCircleSize + strokeWidth}x${northArrowInnerCircleSize + strokeWidth}",
	//		"xc: #00000000",
	//		"${tempFilesLocation}${date}northArrowInnerCircle.png"
	//]
	//executeCommand(command)

	//def northArrowCircleSize = 0.70 * northArrowSize
	//northArrowCircleSize = northArrowCircleSize.toInteger()
	//def northArrowCircleCenterX = northArrowCircleSize / 2
	//northArrowCircleCenterX = northArrowCircleCenterX.toInteger()
	//def northArrowCircleCenterY = northArrowCircleCenterX
	//command = [
        //		"convert",
	//		"-size",
	//		"${northArrowCircleSize}x${northArrowCircleSize}",
	//		"xc: #00000000",
	//		"-fill",
	//		"black",
	//		"-stroke",
	//		"white",
	//		"-strokewidth",
	//		"${strokeWidth}",
	//		"-draw",
	//		"circle ${northArrowCircleCenterX},${northArrowCircleCenterY} ${northArrowCircleCenterX},${strokeWidth}",
	//		"${tempFilesLocation}${date}northArrowCircle.png"
        //]
	//executeCommand(command)

	//command = [
	//		"composite",
	//		"${tempFilesLocation}${date}northArrowCircle.png",
	//		"-gravity",
	//		"South",
	//		"${tempFilesLocation}${date}northArrowInnerCircle.png",
	//		"${tempFilesLocation}${date}northArrowInnerCircle.png"
	//]
	//executeCommand(command)

	//def northArrowTriangleSize = 0.23 * northArrowSize
	//northArrowTriangleSize = northArrowTriangleSize.toInteger()
	//def northArrowTriangleMidPoint = northArrowTriangleSize / 2
	//northArrowTriangleMidPoint = northArrowTriangleMidPoint.toInteger()
	//command = [
	//		"convert",
	//		"-size",
	//		"${northArrowTriangleSize}x${northArrowTriangleSize}",
	//		"xc: #00000000",
	//		"-fill",
	//		"white",
	//		"-draw",
	//		"polygon 0,${northArrowTriangleSize} ${northArrowTriangleMidPoint},0 ${northArrowTriangleSize},${northArrowTriangleSize} 0,$northArrowTriangleSize}",
	//		"${tempFilesLocation}${date}northArrowTriangle.png"
	//]
	//executeCommand(command)

	//command = [
	//		"composite",
	//		"${tempFilesLocation}${date}northArrowTriangle.png",
	//		"-gravity",
	//		"North",
	//		"${tempFilesLocation}${date}northArrowInnerCircle.png",
	//		"${tempFilesLocation}${date}northArrowInnerCircle.png"
	//]
	//executeCommand(command)		

	//def northArrowNHeight = 0.27 * northArrowSize
	//northArrowNHeight = northArrowNHeight.toInteger()
	//def northArrowNWidth = 0.75 * northArrowNHeight
	//northArrowNWidth = northArrowNWidth.toInteger()

	//def northArrowNHeightMidPoint = northArrowNHeight / 2
	//northArrowNHeightMidPoint = northArrowNHeightMidPoint.toInteger()
	//def northArrowNWidthMidPoint = northArrowNWidth / 2
	//northArrowNWidthMidPoint = northArrowNWidthMidPoint.toInteger()
	//command = [
	//		"convert",
	//		"-size",
	//		"${northArrowNWidth + strokeWidth}x${northArrowNHeight}",
	//		"xc: #00000000",
	//		"-stroke",
	//		"white",
	//		"-strokewidth",
	//		"${strokeWidth}",
	//		"-draw",
	//		"polyline ${strokeWidth},${northArrowNHeight} ${strokeWidth},0 ${northArrowNWidth},${northArrowNHeight} $northArrowNWidth},0",
	//		"${tempFilesLocation}${date}northArrowN.png"
	//]
	//executeCommand(command)

	//command = [
	//		"composite",
	//		"${tempFilesLocation}${date}northArrowN.png",
	//		"-gravity",
	//		"Center",
	//		"-geometry",
	//		"-${northArrowNWidthMidPoint - (strokeWidth / 2) + 1}+${northArrowNHeightMidPoint}",
	//		"${tempFilesLocation}${date}northArrowInnerCircle.png",
	//		"${tempFilesLocation}${date}northArrowInnerCircle.png"
	//]
	//executeCommand(command)

	//def northArrowLineLength = 2 * northArrowNHeight
	//command = [
	//		"convert",
	//		"-size",
	//		"${strokeWidth + 1}x${northArrowLineLength}",
	//		"xc: white",
	//		"${tempFilesLocation}${date}northArrowLine.png"
	//]
	//executeCommand(command)

	//command = [
	//		"composite",
	//		"${tempFilesLocation}${date}northArrowLine.png",
	//		"-gravity",
	//		"Center",
	//		"${tempFilesLocation}${date}northArrowInnerCircle.png",
	//		"${tempFilesLocation}${date}northArrowInnerCircle.png"
	//]
	//executeCommand(command)

	//command = [
	//		"composite",
	//		"${tempFilesLocation}${date}northArrowInnerCircle.png",
	//		"-gravity",
	//		"Center",
	//		"${tempFilesLocation}${date}northArrow.png",
	//		"${tempFilesLocation}${date}northArrow.png"
	//]
	//executeCommand(command)

	//command = "rm ${tempFilesLocation}${date}northArrowCircle.png"
	//executeCommand(command)

	//command = "rm ${tempFilesLocation}${date}northArrowInnerCircle.png"
	//executeCommand(command)
		
	//command = "rm ${tempFilesLocation}${date}northArrowLine.png"
	//executeCommand(command)

	//command = "rm ${tempFilesLocation}${date}northArrowN.png"
	//executeCommand(command)

	//command = "rm ${tempFilesLocation}${date}northArrowTriangle.png"
	//executeCommand(command)
}
