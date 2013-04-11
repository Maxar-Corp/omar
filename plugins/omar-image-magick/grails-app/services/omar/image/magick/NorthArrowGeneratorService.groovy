package omar.image.magick

import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.ossim.omar.core.Utility

class NorthArrowGeneratorService
{
	LinkGenerator grailsLinkGenerator

	def DEBUG = false
	def grailsApplication

	def serviceMethod(def northAngle, def northArrowBackgroundColor, def northArrowColor, def northArrowSize)
	{
        def command
		def date = new Date().getTime()
		def tempFilesLocation = grailsApplication.config.export.workDir + "/"
		def tempFilesLocationAsFile = new File(tempFilesLocation)
        def logoFilesLocation = grailsLinkGenerator.resource(absolute: true, dir: 'images', plugin: 'omar-image-magick') + "/"

        def tempFileNorthArrow = File.createTempFile("northArrow",
                ".png", tempFilesLocationAsFile);
        def tempFileNorthArrowInnerCircle = File.createTempFile("northArrowInnerCircle",
                ".png", tempFilesLocationAsFile);
        def tempFileNorthArrowCircle = File.createTempFile("northArrowCircle",
                ".png", tempFilesLocationAsFile);
        def tempFileNorthArrowTriangle = File.createTempFile("northArrowTriangle",
                ".png", tempFilesLocationAsFile);
        def tempFileNorthArrowN = File.createTempFile("northArrowN",
                ".png", tempFilesLocationAsFile);
        def tempFileNorthArrowLine = File.createTempFile("northArrowLine",
                ".png", tempFilesLocationAsFile);
        def tempFileNorthArrowScaled = File.createTempFile("northArrowScaled",
                ".png", tempFilesLocationAsFile);
        def tempFileNorthArrowRotated = File.createTempFile("northArrowRotated",
                ".png", tempFilesLocationAsFile);

		//######################################################################################################################
		//################################################## North Arrow Base ##################################################
		//######################################################################################################################
		if (DEBUG) { println "##### North Arrow Base #####" }	
	
		//########## North Arrow Size 
		if (DEBUG) { println "North Arrow Size:" }
		def northArrowSizeFullResolution = 2000
		if (DEBUG) { println "${northArrowSizeFullResolution} pixels" }
		
		//########## North Arrow Stroke Width
		if (DEBUG) { println "North Arrow Stroke Width:" }
		def strokeWidth = 0.035 * northArrowSizeFullResolution
		if (DEBUG) { println "${strokeWidth} pixels" }

		//########## Determine the north arrow circle base center x,y positions
		if (DEBUG) { println "Determine the north arrow circle base center x position:" }
		def northArrowCircleBaseCenterX = northArrowSizeFullResolution / 2
		northArrowCircleBaseCenterX = northArrowCircleBaseCenterX.toInteger()
		if (DEBUG) { println "${northArrowCircleBaseCenterX}" }

		if (DEBUG) { println "Determine the north arrow circle base center y position:" }
		def northArrowCircleBaseCenterY = northArrowSizeFullResolution / 2
		northArrowCircleBaseCenterY = northArrowCircleBaseCenterY.toInteger()
		if (DEBUG) { println "${northArrowCircleBaseCenterY}" }

		//########## Generate the north arrow circle base
		if (DEBUG) { println "Generate the north arrow circle base:" }
		command = [
			"convert",
			"-size",
			"${northArrowSizeFullResolution}x${northArrowSizeFullResolution}",
			"xc: #00000000",
			"-fill",
			"#${northArrowBackgroundColor}",
			"-stroke",
			"#${northArrowBackgroundColor}",
			"-draw",
			"circle ${northArrowCircleBaseCenterX},${northArrowCircleBaseCenterY} ${northArrowCircleBaseCenterX},${northArrowSizeFullResolution}",
			tempFileNorthArrow.toString()
		]
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//##############################################################################################################################
		//################################################## North Arrow Inner Circle ##################################################
		//##############################################################################################################################
		if (DEBUG) { println "##### North Arrow Inner Circle #####" }

		//########## North Arrow Inner Circle Size
		if (DEBUG) { println "Determine the north arrow inner circle size:" }
		def northArrowInnerCircleSize = 0.9 * northArrowSizeFullResolution
		northArrowInnerCircleSize = northArrowInnerCircleSize.toInteger()
		if (DEBUG) { println "${northArrowInnerCircleSize} pixels" }
		
		//########## Generate north arrow inner circle
		if (DEBUG) { println "Generate north arrow inner circle:" }
		command = [
			"convert",
			"-size",
			"${northArrowInnerCircleSize + strokeWidth}x${northArrowInnerCircleSize + strokeWidth}",
			"xc: #00000000",
			tempFileNorthArrowInnerCircle.toString()
		]
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//########################################################################################################################
		//################################################## North Arrow Circle ##################################################
		//########################################################################################################################
		if (DEBUG) { println "##### North Arrow Circle #####" }

		//########## North Arrow Circle Size
		if (DEBUG) { println "Determine the north arrow circle size:" }
		def northArrowCircleSize = 0.70 * northArrowSizeFullResolution
		northArrowCircleSize = northArrowCircleSize.toInteger()
		if (DEBUG) { println "${northArrowCircleSize} pixels" }	

		if (DEBUG) { println "Determine the north arrow circle center x position:" }
		def northArrowCircleCenterX = northArrowCircleSize / 2
		northArrowCircleCenterX = northArrowCircleCenterX.toInteger()
		if (DEBUG) { println "${northArrowCircleCenterX}" }

		if (DEBUG) { println "Determine the north arrow circle center y position:" }
		def northArrowCircleCenterY = northArrowCircleCenterX
		if (DEBUG) { println "${northArrowCircleCenterY}" }
		
		//########## Generate the north arrow circle
		if (DEBUG) { println "Generate the north arrow circle:" }
		command = [
        		"convert",
			"-size",
			"${northArrowCircleSize}x${northArrowCircleSize}",
			"xc: #00000000",
			"-fill",
			"#${northArrowBackgroundColor}",
			"-stroke",
			"#${northArrowColor}",
			"-strokewidth",
			"${strokeWidth}",
			"-draw",
			"circle ${northArrowCircleCenterX},${northArrowCircleCenterY} ${northArrowCircleCenterX},${strokeWidth}",
			tempFileNorthArrowCircle.toString()
        	]
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		if (DEBUG) { println "Combine the north arrow circle with the north arrow inner circle:" }
		command = [
			"composite",
                tempFileNorthArrowCircle.toString(),
			"-gravity",
			"South",
			tempFileNorthArrowInnerCircle.toString(),
            tempFileNorthArrowInnerCircle.toString()
		]
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//###########################################################################################################################
		//################################################## North Arrow Triangle  ##################################################
		//###########################################################################################################################
		if (DEBUG) { println "##### North Arrow Triangle #####" }

		//########## North Arrow Triangle
		if (DEBUG) { println "Determine the north arrow triangle size:" }
		def northArrowTriangleSize = 0.23 * northArrowSizeFullResolution
		northArrowTriangleSize = northArrowTriangleSize.toInteger()
		if (DEBUG) { println "${northArrowTriangleSize} pixels" }

		if (DEBUG) { println "Determine the north arrow triangle mid-point" }
		def northArrowTriangleMidPoint = northArrowTriangleSize / 2
		northArrowTriangleMidPoint = northArrowTriangleMidPoint.toInteger()
		if (DEBUG) { println "${northArrowTriangleMidPoint}" }
		command = [
			"convert",
			"-size",
			"${northArrowTriangleSize}x${northArrowTriangleSize}",
			"xc: #00000000",
			"-fill",
			"#${northArrowColor}",
			"-draw",
			"polygon 0,${northArrowTriangleSize} ${northArrowTriangleMidPoint},0 ${northArrowTriangleSize},${northArrowTriangleSize} 0,$northArrowTriangleSize}",
			tempFileNorthArrowTriangle.toString()
		]
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		if (DEBUG) { println "Combine the north arrow triangle with the north arrow inner circle:" }
		command = [
			"composite",
            tempFileNorthArrowTriangle.toString(),
			"-gravity",
			"North",
            tempFileNorthArrowInnerCircle.toString(),
            tempFileNorthArrowInnerCircle.toString()
		]
		if (DEBUG) { println "${command}" }
		executeCommand(command)		

		//####################################################################################################################
		//################################################## North Arrow N  ##################################################
		//####################################################################################################################
		if (DEBUG) { println "##### North Arrow N #####" }

		//########## North Arrow N
		if (DEBUG) { println "Determine the north arrow N height:" }
		def northArrowNHeight = 0.27 * northArrowSizeFullResolution
		northArrowNHeight = northArrowNHeight.toInteger()
		if (DEBUG) { println "${northArrowNHeight} pixels" }	

		if (DEBUG) { println "Determine the north arrow N width:" }
		def northArrowNWidth = 0.75 * northArrowNHeight
		northArrowNWidth = northArrowNWidth.toInteger()
		if (DEBUG) { println "${northArrowNWidth} pixels" }

		if (DEBUG) { println "Determine the north arrow N height mid-point:" }
		def northArrowNHeightMidPoint = northArrowNHeight / 2
		northArrowNHeightMidPoint = northArrowNHeightMidPoint.toInteger()
		if (DEBUG) { println "${northArrowNHeightMidPoint}" }

		if (DEBUG) { println "Determine the north arrow N width mid-point:" }
		def northArrowNWidthMidPoint = northArrowNWidth / 2
		northArrowNWidthMidPoint = northArrowNWidthMidPoint.toInteger()
		if (DEBUG) { println "${northArrowNWidthMidPoint}" }
	
		if (DEBUG) { println "Generate the north arrow N:" }
		command = [
			"convert",
			"-size",
			"${northArrowNWidth + strokeWidth}x${northArrowNHeight}",
			"xc: #00000000",
			"-fill",
			"none",
			"-stroke",
			"#${northArrowColor}",
			"-strokewidth",
			"${strokeWidth}",
			"-draw",
			"polyline ${strokeWidth},${northArrowNHeight} ${strokeWidth},0 ${northArrowNWidth},${northArrowNHeight} ${northArrowNWidth},0",
			tempFileNorthArrowN.toString()
		]
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		if (DEBUG) { println "Combine the north arrow N with the north arrow inner circle:" }
		command = [
			"composite",
                tempFileNorthArrowN.toString(),
			"-gravity",
			"Center",
			"-geometry",
			"-${northArrowNWidthMidPoint - (strokeWidth / 2) + 1}+${northArrowNHeightMidPoint}",
                tempFileNorthArrowInnerCircle.toString(),
                tempFileNorthArrowInnerCircle.toString()
		]
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//########## North Arrow Line 
		if (DEBUG) { println "Determine the north arrow line length:" }
		def northArrowLineLength = 2 * northArrowNHeight
		if (DEBUG) { println "${northArrowLineLength}" }	

		if (DEBUG) { println "Generate the north arrow line" }
		command = [
			"convert",
			"-size",
			"${strokeWidth + 1}x${northArrowLineLength}",
			"xc: #${northArrowColor}",
                tempFileNorthArrowLine.toString()
		]
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		if (DEBUG) { println "Combine the north arrow line with the north arrow inner circle:" }
		command = [
			"composite",
                tempFileNorthArrowLine.toString(),
			"-gravity",
			"Center",
                tempFileNorthArrowInnerCircle.toString(),
                tempFileNorthArrowInnerCircle.toString()
		]
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		if (DEBUG) { println "Generate the north arrow:" }
		command = [
			"composite",
                tempFileNorthArrowInnerCircle.toString(),
			"-gravity",
			"Center",
			tempFileNorthArrow.toString(),
                tempFileNorthArrow.toString()
		]
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//########## Determine the size of the north arrow
		if (DEBUG) { println "Determine the size of the north arrow:" }
		def northArrowWidth = northArrowSize
		def northArrowHeight = northArrowSize
		if (DEBUG) { println "${northArrowWidth}x${northArrowHeight} pixels" }

		//########## Scale the north arrow
		if (DEBUG) { println "Scale the north arrow:" }
		command = [
				"convert",
                tempFileNorthArrow.toString(),
				"-resize", 
				"${northArrowWidth}x${northArrowHeight}", 
				tempFileNorthArrowScaled.toString()
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
                tempFileNorthArrowScaled.toString(),
				"-rotate", 
				"${northAngle}",
                tempFileNorthArrowRotated.toString()
		]
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//########## Crop the rotated north arrow
		if (DEBUG) { println "Crop the rotated north arrow:" }
		command = [
				"convert",
                tempFileNorthArrowRotated.toString(),
				"+repage",
				"-gravity",
				"center", 
				"-crop", "${northArrowWidth}x${northArrowHeight}+0+0",
                tempFileNorthArrowRotated.toString()
		]
		if (DEBUG) { println "${command}" }
		executeCommand(command)

		//#############################################################################################################################
		//################################################## Temporary File Deletion ##################################################
		//#############################################################################################################################
        tempFileNorthArrow.delete()
        tempFileNorthArrowInnerCircle.delete()
        tempFileNorthArrowCircle.delete()
        tempFileNorthArrowTriangle.delete()
        tempFileNorthArrowN.delete()
        tempFileNorthArrowLine.delete()
        tempFileNorthArrowScaled.delete()

		return tempFileNorthArrowRotated.toString()
	}

	def executeCommand(def executableCommand)
	{
         return Utility.executeCommand(executableCommand, true).text
		//def script = executableCommand.execute()
		//script.waitFor()
		//return script.text
	}
}
