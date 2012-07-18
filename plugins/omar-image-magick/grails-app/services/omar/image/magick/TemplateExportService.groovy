package omar.image.magick

import org.codehaus.groovy.grails.web.mapping.LinkGenerator

class TemplateExportService 
{

	LinkGenerator grailsLinkGenerator

	def DEBUG = false
	def grailsApplication

	def command
	def serviceMethod(def imageUrl, def logo, def line1, def line2, def line3, def includeOutlineMap, def includeOverviewMap, def country, def northAngle, def securityClassification)
	{
		def date = new Date().getTime()

		def tempFilesLocation = grailsApplication.config.export.workDir + "/"
		def logoFilesLocation = grailsLinkGenerator.resource(absolute: true, dir: 'images', plugin: 'omar-image-magick') + "/"
		def mapFilesLocation = logoFilesLocation + "overviewMaps/"

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

		//################################################################################################################################
		//############################################################ Image Dimensions ##################################################
		//################################################################################################################################
		if (DEBUG) { println "##### Image Dimensions #####" }
		//########## Determine the width of the image
		if (DEBUG) { println "Determine the width of the image: " }
		command = [
				"identify", 
				"-format", 
				"%w", 
				"${imageFile}"
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
				"${imageFile}"
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
		def headerWidth = 0.96 * imageWidth
		headerWidth = headerWidth.toInteger()
		if (DEBUG) { println "${headerWidth} pixels" }
		
		if (DEBUG) { println "Determine the height of the header:" }
		def headerHeight = 0.14 * imageHeight
		headerHeight = headerHeight.toInteger()
		if (DEBUG) { println "${headerHeight} pixels" }

		if (DEBUG) { println "Generate the header:" }
		command = [
				"convert", 
				"-size", 
				"${headerWidth}x${headerHeight}", 
				"xc:#00000000", 
				"-transparent", 
				"black", 
				"-fill", 
				"white", 
				"-draw", 
				"roundrectangle 0,0 ${headerWidth},${headerHeight} 10,10", 
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
		def logoWidth = 0.75 * headerHeight
		logoWidth = logoWidth.toInteger()
		if (DEBUG) { println "${logoWidth} pixels" }

		if (DEBUG) { println "Determine the height of the logo:" }
		def logoHeight = logoWidth
		logoHeight = logoHeight.toInteger()
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
		if (DEBUG) { println "${logoOffset}" }

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

		//########## Delete the scaled logo file
		if (DEBUG) { println "Delete the scaled logo file:" }
		command = "rm ${tempFilesLocation}${date}${logo}Scaled.png"
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//#################################################################################################################
		//################################################## Outline Map ##################################################
		//#################################################################################################################
		if (DEBUG) { println "##### Outline Map #####" }
		def outlineMapWidth = 0
		//########## Determine the height of the outline map
		if (DEBUG) { println "Determine the height of the outline map:" }
		def outlineMapHeight = 0.2 * imageHeight
		outlineMapHeight = outlineMapHeight.toInteger()
		if (DEBUG) { print "${outlineMapHeight} pixels" }

		//##########
		if (("${includeOutlineMap}".toString()).equals("on"))
		{
			//########## Scale the outline map
			if (DEBUG) { println "Scale the outline map:" }
			command = [
					"convert", 
					"${mapFilesLocation}${country}.gif", 
					"-resize", 
					"x${outlineMapHeight}", 
					"${tempFilesLocation}${date}outlineMapScaled.png"
			]
			if (DEBUG) { println "${command}" }
			executeCommand(command)

			//########## Add a shadow to the outline map
			if (DEBUG) { println "Generate a shadow image for the outline map:" }
			command = [
					"convert", 
					"${tempFilesLocation}${date}outlineMapScaled.png",
					"-background",
					"black",
					"-shadow",
					"60x4+4+4",
					"${tempFilesLocation}${date}outlineMapScaledShadow.png"
			]
			if (DEBUG) { println "${command}" }
			executeCommand(command)		
			
			if (DEBUG) { println "Add the shadow image to the outline map:" }
			command = [
					"convert", 
					"-page", 
					"+4+4", 
					"${tempFilesLocation}${date}outlineMapScaled.png", 
					"-matte", 
					"${tempFilesLocation}${date}outlineMapScaledShadow.png",
					 "+swap", 
					"-background", 
					"none", 
					"-mosaic", 
					"${tempFilesLocation}${date}outlineMapScaled.png"
			]
			if (DEBUG) { println "${command}" }
			executeCommand(command)

			//########## Delete the outline map shadow image file
			if (DEBUG) { println "Delete the outline map shadow image file:" }
			command = "rm ${tempFilesLocation}${date}outlineMapScaledShadow.png"
			if (DEBUG) { println "${command}" }
			executeCommand(command)

			//########## Determine the width of the outline map with a shadow
			if (DEBUG) { print "Determine the width of the outline map with a shadow:" }
			command  = [
					"identify",
					"-format",
					"%w", 
					"${tempFilesLocation}${date}outlineMapScaled.png"
			]
			outlineMapWidth = executeCommand(command)	
			outlineMapWidth = outlineMapWidth.replaceAll("\n", "")
			outlineMapWidth = outlineMapWidth.toInteger()
			if (DEBUG) { println "${outlineMapWidth} pixels" }
		}

		//##################################################################################################################
		//################################################## Overview Map ##################################################
		//##################################################################################################################
		if (DEBUG) { println "##### Overview Map #####" }
		def overviewMapWidth = 0
		def overviewMapHeight
		if (("${includeOverviewMap}".toString()).equals("on"))
		{
        		//########## Scale the overview map
        		overviewMapHeight = outlineMapHeight
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

			//########## Delete the outline map shadow image file
			if (DEBUG) { println "Delete the overview map shadow image file:" }
			command = "rm ${tempFilesLocation}${date}overviewMapScaledShadow.png"
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
		def textWidth = headerWidth - (2 * logoWidth) - (5 * logoOffset) - outlineMapWidth - overviewMapWidth
		if (DEBUG) { println "${textWidth} pixels" }

		//########## Generate 1st line of text
		if (DEBUG) { println "Determine the height of the 1st line of text:" }
		def line1Height = 0.41 * logoHeight
		line1Height = line1Height.toInteger()
		if (DEBUG) { println "${line1Height} pixels" }
		if (DEBUG) { println "Generate 1st line of text:" }
		command = [
				"convert", 
				"-background", 
				"white", 
				"-fill", 
				"black", 
				"-size", 
				"${textWidth}x${line1Height}", 
				"-gravity", 
				"West", 
				"caption:${line1}", 
				"${tempFilesLocation}${date}line1.png"
		]
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//########## Generate 2nd line of text
		if (DEBUG) { println "Determine the height of the 2nd line of text:" }
		def line2Height = 0.33 * logoHeight
		line2Height = line2Height.toInteger()
		if (DEBUG) { println "${line2Height} pixels" }
		if (DEBUG) { println "Generate 2nd line of text:" }
		command = [
				"convert", 
				"-background", 
				"white", 
				"-fill", 
				"black", 
				"-size", 
				"${textWidth}x${line2Height}", 
				"-gravity", 
				"West", 
				"caption:${line2}", 
				"${tempFilesLocation}${date}line2.png"
		]
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//########## Generate 3rd line of text
		if (DEBUG) { println "Determine the height of the 3rd line of text:" }
		def line3Height = 0.28 * logoHeight
		line3Height = line3Height.toInteger()
		if (DEBUG) { println "${line3Height} pixels" }
		if (DEBUG) { println "Generate 3rd line of text:" }
		command = [
				"convert", 
				"-background", 
				"white", 
				"-fill", 
				"black", 
				"-size", 
				"${textWidth}x${line3Height}", 
				"-gravity", 
				"West", 
				"caption:${line3}", 
				"${tempFilesLocation}${date}line3.png"
		]
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//########## Combine all three lines of text
		if (DEBUG) { println "Combine all three lines of text:" }
		command = [
				"convert", 
				"${tempFilesLocation}${date}line1.png", 
				"${tempFilesLocation}${date}line2.png", 
				"${tempFilesLocation}${date}line3.png", 
				"-append", 
				"${tempFilesLocation}${date}text.png"
		]
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//########## Delete the 1st line of text file
		if (DEBUG) { println "Delete the 1st line of text file:" }
		command = "rm ${tempFilesLocation}${date}line1.png"
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//########## Delete the 2nd line of text file
		if (DEBUG) { println "Delete the 2nd line of text file:" }
		command = "rm ${tempFilesLocation}${date}line2.png"
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//########## Delete the 3rd line of text file
		if (DEBUG) { println "Delete the 3rd line of text file:" }
		command = "rm ${tempFilesLocation}${date}line3.png"
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//#######################################################################################################################
		//################################################## Header Adjustment ##################################################
		//#######################################################################################################################
		if (DEBUG) { println "##### Header Adjustment #####" }
		//########## Add the header text to the header
		if (DEBUG) { println "Determine the text offset:" }
		def textOffset = 2 * logoOffset + logoWidth
		if (DEBUG) { println "${textOffset}" }
		if (DEBUG) { println "Add the header text to the header:" }
		command = [
				"composite", 
				"${tempFilesLocation}${date}text.png", 
				"-gravity",
				"West", 
				"-geometry",
				"+${textOffset}+0",
				"${tempFilesLocation}${date}header.png",
				"${tempFilesLocation}${date}header.png"
		]
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//########## Delete the header text file
		if (DEBUG) { println "Delete the header text file:" }
		command = "rm ${tempFilesLocation}${date}text.png"
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//#################################################################################################################
		//################################################## North Arrow ##################################################
		//#################################################################################################################
		if (DEBUG) { println "##### North Arrow #####" }
		//########## Scale the north arrow
		if (DEBUG) { println "Scale the north arrow:" }
		command = [
				"convert", 
				"${logoFilesLocation}northArrow.png", 
				"-resize", 
				"${logoWidth}x${logoHeight}", 
				"${tempFilesLocation}${date}northArrowScaled.png"
		]
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//########## Rotate the north arrow
		if (DEBUG) { println "Rotate the north arrow:" }
		command = [
				"convert", 
				"${tempFilesLocation}${date}northArrowScaled.png", 
				"-rotate", 
				"${northAngle}", 
				"${tempFilesLocation}${date}northArrowRotated.png"
		]
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//########## Determine the width of the scaled north arrow
		if (DEBUG) { print "Determine the width of the scaled north arrow:" }
		command = [
				"identify", 
				"-format", 
				"%w", 
				"${tempFilesLocation}${date}northArrowScaled.png"
		]
		def northArrowWidth = executeCommand(command)
		northArrowWidth = northArrowWidth.replaceAll("\n", "")
		northArrowWidth = northArrowWidth.toInteger()
		if (DEBUG) { println "${northArrowWidth}" }

		//########## Determine the height of the scaled north arrow
		if (DEBUG) { println "Determine the height of the scaled north arrow:" }
		command = [
				"identify", 
				"-format", 
				"%h", 
				"${tempFilesLocation}${date}northArrowScaled.png"
		]
		def northArrowHeight = executeCommand(command)
		northArrowHeight = northArrowHeight.replaceAll("\n", "")
		northArrowHeight = northArrowHeight.toInteger()
		if (DEBUG) { println "${northArrowHeight}" }

		//########## Delete the scaled north arrow file
		if (DEBUG) { println "Delete the scaled north arrow file:" }
		command = "rm ${tempFilesLocation}${date}northArrowScaled.png"
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//########## Crop the north rotated north arrow
		if (DEBUG) { println "Crop the north rotated north arrow:" }
		command = [
				"convert", 
				"${tempFilesLocation}${date}northArrowRotated.png", 
				"-crop", "${northArrowWidth}x${northArrowHeight}+0+0", 
				"+repage", 
				"${tempFilesLocation}${date}northArrowRotated.png"
		]
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//#######################################################################################################################
		//################################################## Header Adjustment ##################################################
		//#######################################################################################################################
		if (DEBUG) { println "##### Header Adjustment #####" }
		//########## Add the north arrow to the header
		if (DEBUG) { println "Determine the north arrow offset:" }
		def northArrowOffset = logoOffset;
		if (DEBUG) { println "${northArrowOffset}" }
		if (DEBUG) { println "Add the north arrow to the header:"  }
		command = [
				"composite", 
				"${tempFilesLocation}${date}northArrowRotated.png", 
				"-gravity", 
				"East", 
				"-geometry", 
				"+${northArrowOffset}+0", 
				"${tempFilesLocation}${date}header.png", 
				"${tempFilesLocation}${date}header.png"
		]
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//##########  Delete the north arrow rotated file
		if (DEBUG) { println "Delete the north arrow rotated file:" }
		command = "rm ${tempFilesLocation}${date}northArrowRotated.png"
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//#######################################################################################################################
		//################################################## Header Adjustment ##################################################
		//#######################################################################################################################
		if (DEBUG) { println "##### Header Adjustment #####" }
		//########## Add a shadow to the header
		if (DEBUG) { println "Generate a shadow image for the header:" }
		command = [
				"convert", 
				"${tempFilesLocation}${date}header.png", 
				"-background", 
				"black", 
				"-shadow", 
				"60x4+4+4", 
				"${tempFilesLocation}${date}headerShadow.png"
		]
		if (DEBUG) { println "${command}" }
		executeCommand(command)
		
		if (DEBUG) { println "Add the shadow file to the header:" }
		command = [
				"convert", 
				"-page", 
				"+4+4", 
				"${tempFilesLocation}${date}header.png", 
				"-matte", 
				"${tempFilesLocation}${date}headerShadow.png", 
				"+swap", 
				"-background", 
				"none", 
				"-mosaic", 
				"${tempFilesLocation}${date}header.png"
		]
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//########## Delete the shadow file for the header
		if (DEBUG) { println "Delete the shadow file for the header:" }
		command = "rm ${tempFilesLocation}${date}headerShadow.png"
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//########## Add the header to the image
		if (DEBUG) { println "Determine the header offset:" }
		def headerOffset = (imageWidth - 0.96 * imageWidth) / 4
		headerOffset = headerOffset.toInteger()
		if (DEBUG) { println "${headerOffset} pixels" }
		if (DEBUG) { println "Add the header to the image:" }
		command = [
				"composite",
				"${tempFilesLocation}${date}header.png",
				"-gravity",
				"North",
				"-geometry",
				"+0+${headerOffset}",
				"${imageFile}",
				"${tempFilesLocation}${date}finishedProduct.png"
		]
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//#######################################################################################################################
		//################################################## Report Adjustment ##################################################
		//#######################################################################################################################
		if (DEBUG) { println "##### Report Adjustment #####" }
		if (DEBUG) { println "Determine the outline map offset:" }
		def outlineMapOffset = (imageWidth - headerWidth) / 2 + northArrowWidth + 2 * northArrowOffset
		outlineMapOffset = outlineMapOffset.toInteger()
		if (DEBUG) { println "${outlineMapOffset} pixels" }

		//########## Add the outline map to the finished product
		if (("${includeOutlineMap}".toString()).equals("on"))
		{
			if (DEBUG) { println "Add the outline map to the finished product:" }
			command = [
					"composite",
					"${tempFilesLocation}${date}outlineMapScaled.png",
					"-gravity",
					"NorthEast",
					"-geometry",
					"+${outlineMapOffset}+0",
					"${tempFilesLocation}${date}finishedProduct.png",
					"${tempFilesLocation}${date}finishedProduct.png"
			]
			if (DEBUG) { println "${command}" }
			executeCommand(command)
		}
		else
		{
			if (DEBUG) { println "The outline map is not included" }
		}

		//#######################################################################################################################
		//################################################## Report Adjustment ##################################################
		//#######################################################################################################################
		if (DEBUG) { println "##### Report Adjustment #####" }
		//########## Add the overview map to the finished product
		if (("${includeOverviewMap}".toString()).equals("on"))
		{
			
			def overviewMapOffset = outlineMapOffset + outlineMapWidth
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

		//#####################################################################################################################
		//################################################## Security Banner ##################################################
		//#####################################################################################################################
		if (DEBUG) { print "##### Security Banner #####" }
		//########## Generate security banner text
		if (DEBUG) { println "Determine the security text height:" }
		def securityTextHeight = 0.25 * headerHeight
		securityTextHeight = securityTextHeight.toInteger()
		if (DEBUG) { println "${securityTextHeight} pixels" }
		if (DEBUG) { println "Generate security banner text:" }
		command = [
				"convert", 
				"-background",
				"white",
				"-fill",
				"black",
				"-size",
				"x${securityTextHeight}",
				"-gravity",
				"West",
				"label:${securityClassification}",
				"${tempFilesLocation}${date}securityText.png"
		]
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//########## Determine the width of the security banner
		if (DEBUG) { println "Determine the width of the security banner:" }
		command =[
				"identify",
				"-format",
				"%w",
				"${tempFilesLocation}${date}securityText.png"
		]
		def securityBannerWidth = executeCommand(command)
		securityBannerWidth = securityBannerWidth.toInteger()
		securityBannerWidth = 1.1 * securityBannerWidth
		securityBannerWidth = securityBannerWidth.toInteger()
		if (DEBUG) { println "${securityBannerWidth} pixels" }

		//########## Determine the height of the security banner
		if (DEBUG) { println "Determine the height of the security banner:" }
		command = [
				"identify",
				"-format",
				"%h",
				"${tempFilesLocation}${date}securityText.png"
		]
		def securityBannerHeight = executeCommand(command)
		securityBannerHeight = securityBannerHeight.toInteger()
		securityBannerHeight = 1.1 * securityBannerHeight
		securityBannerHeight = securityBannerHeight.toInteger()

		//########## Generate security banner
		if (DEBUG) { println "Generate security banner:" }
		command = [
				"convert",
				"-size",
				"${securityBannerWidth}x${securityBannerHeight}",
				"xc:#00000000",
				"-transparent",
				"black",
				"-fill",
				"white",
				"-draw",
				"roundrectangle 0,0 ${securityBannerWidth},${securityBannerHeight} 10,10",
				"${tempFilesLocation}${date}securityBanner.png"
		]
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//########## Add text to the security banner
		if (DEBUG) { println "Add text to security banner:" }		
		command = [
				"composite", 
				"${tempFilesLocation}${date}securityText.png",
				"-gravity",
				"Center",
				"-geometry",
				"+0+0",
				"${tempFilesLocation}${date}securityBanner.png",
				"${tempFilesLocation}${date}securityBanner.png"
		]
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//########## Delete the security text file
		if (DEBUG) { println "Delete the security text file:" }
		command = "rm ${tempFilesLocation}${date}securityText.png"
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//########## Add a shadow to the security banner
		if (DEBUG) { println "Generate a shadow image for the security banner:" }
		command = [
				"convert",
				"${tempFilesLocation}${date}securityBanner.png",
				"-background",
				"black",
				"-shadow",
				"60x4+4+4",
				"${tempFilesLocation}${date}securityBannerShadow.png"
		]
		if (DEBUG) { println "${command}" }
		executeCommand(command)
		
		if (DEBUG) { println "Add the shadow file to security banner:" }
		command = [
				"convert",
				"-page",
				"+4+4",
				"${tempFilesLocation}${date}securityBanner.png",
				"-matte",
				"${tempFilesLocation}${date}securityBannerShadow.png",
				"+swap",
				"-background",
				"none",
				"-mosaic",
				"${tempFilesLocation}${date}securityBanner.png"
		]
		if (DEBUG) { println "${command}" }
		executeCommand(command)
		
		//########## Delete the shadow file for the security banner
		if (DEBUG) { println "Delete the shadow file for the security banner:" }
		command = "rm ${tempFilesLocation}${date}securityBannerShadow.png"
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//########## Add security banner to finished product
		def securityBannerOffsetX = (imageWidth - 0.96 * imageWidth) / 4
		securityBannerOffsetX = securityBannerOffsetX.toInteger()
		def securityBannerOffsetY = securityBannerOffsetX / 2
		securityBannerOffsetY = securityBannerOffsetY.toInteger()
		if (DEBUG) { println "Add security banner to finished product:" }
		command = [
				"composite",
				"${tempFilesLocation}${date}securityBanner.png",
				"-gravity",
				"SouthWest",
				"-geometry",
				"+${securityBannerOffsetX}+${securityBannerOffsetY}",
				"${tempFilesLocation}${date}finishedProduct.png",
				"$tempFilesLocation${date}finishedProduct.png"
		]
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//##########
		outlineMapHeight = 0
		overviewMapHeight = 0
		if (("${includeOutlineMap}".toString()).equals("on"))
		{
			if (DEBUG) { println "Determine the height of the security banner offset:" }
        		command = [
					"identify",
					"-format",
					"%h",
					"${tempFilesLocation}${date}outlineMapScaled.png"
			]		
			outlineMapHeight = executeCommand(command)
			outlineMapHeight = outlineMapHeight.toInteger()
        		securityBannerOffsetY = outlineMapHeight
			if (DEBUG) { println "${securityBannerOffsetY}" }
		}
		else if (("${includeOverviewMap}".toString()).equals("on"))
		{
			if (DEBUG) { println "Determine the height of the security banner offset:" }
			command = [
					"identify",
					"-format",
					"%h",
					"${tempFilesLocation}${date}overviewMapScaled.png"
			]
			overviewMapHeight = executeCommand(command)
        		overviewMapHeight = overviewMapHeight.toInteger()
        		securityBannerOffsetY = overviewMapHeight
			if (DEBUG) { println "${securityBannerOffsetY}" }
		}
		else
		{
			if (DEBUG) { println "Determine the height of the security banner offset:" }
			command = [
					"identify",
					"-format",
					"%h",
					"${tempFilesLocation}${date}header.png"
			]
			headerHeight = executeCommand(command)
        		headerHeight = headerHeight.toInteger()
        		securityBannerOffsetY = headerHeight + headerOffset
			if (DEBUG) { println "${securityBannerOffsetY}" }
		}

		command = [
				"composite",
				"${tempFilesLocation}${date}securityBanner.png",
				"-gravity",
				"NorthEast",
				"-geometry",
				"+${securityBannerOffsetX}+${securityBannerOffsetY}",
				"${tempFilesLocation}${date}finishedProduct.png",
				"${tempFilesLocation}${date}finishedProduct.png"
		]
		executeCommand(command)

		//########## Delete the header file
		if (DEBUG) { println "Delete the header file:" }
		command = "rm ${tempFilesLocation}${date}header.png"
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//########## Delete the outline map file
		if (DEBUG) { println "Delete the outline map file:" }
		command = "rm ${tempFilesLocation}${date}outlineMapScaled.png"
		if (DEBUG) { println "${command}" }		
		executeCommand(command)
	
		//########## Delete the overview map file
		if (DEBUG) { println "Delete the overview map file:" }
		command = "rm ${tempFilesLocation}${date}overviewMapScaled.png"
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//########## Delete the security banner file
		if (DEBUG) { println "Delete the security banner file:" }
		command = "rm ${tempFilesLocation}${date}securityBanner.png"
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//####################################################################################################################
		//################################################## Diclaimer Text ##################################################
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
				"label:${disclaimerText}", 
				"-append", 
				"${tempFilesLocation}${date}finishedProduct.png"
		]
		if (DEBUG) { println "${command}" } 
		executeCommand(command)

		//########## Delete the image file
		if (DEBUG) { println "Delete the image file:" }
		command = "rm ${tempFilesLocation}${date}omarImage.png"
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
}
