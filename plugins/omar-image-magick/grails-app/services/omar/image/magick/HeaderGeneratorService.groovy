package omar.image.magick

import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.ossim.omar.core.Utility

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
        def tempFilesLocationAsFile = new File(tempFilesLocation)
        def logoFilesLocation = grailsLinkGenerator.resource(absolute: true, dir: 'images', plugin: 'omar-image-magick') + "/"
        def font = "web-app/fonts/ArialBold.ttf"
        def tempFileHeader = File.createTempFile("header",
                ".png", tempFilesLocationAsFile);
        def tempFileScaled = File.createTempFile("scaled",
                ".png", tempFilesLocationAsFile);
        def tempFileHeaderSecurityClassificationText = File.createTempFile("headerSecurityClassificationText",
                ".png", tempFilesLocationAsFile);
        def tempFileHeaderTitleText = File.createTempFile("headerTitleText",
                ".png", tempFilesLocationAsFile);
        def tempFileHeaderDescriptionText = File.createTempFile("headerDescriptionText",
                ".png", tempFilesLocationAsFile);



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
                tempFileHeader.toString()
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
                tempFileScaled.toString()
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
                tempFileScaled.toString(),
                "-gravity",
                "West",
                "-geometry",
                "+${logoOffset}+0",
                tempFileHeader.toString(),
                tempFileHeader.toString()
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
                tempFileHeaderSecurityClassificationText.toString()
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
                tempFileHeaderTitleText.toString()
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
                tempFileHeaderDescriptionText.toString()
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
                tempFileHeaderSecurityClassificationText.toString(),
                "-gravity",
                "NorthWest",
                "-geometry",
                "+${headerTextOffset}+${headerSecurityClassificationTextOffset}",
                tempFileHeader.toString(),
                tempFileHeader.toString()
        ]
        if (DEBUG) { println "${command}" }
        executeCommand(command)

        if (DEBUG) { println "Add the title text to the header:" }
        def headerTitleTextOffset = headerSecurityClassificationTextOffset + headerSecurityClassificationTextHeight
        command = [
                "composite",
                tempFileHeaderTitleText.toString(),
                "-gravity",
                "NorthWest",
                "-geometry",
                "+${headerTextOffset}+${headerTitleTextOffset}",
                tempFileHeader.toString(),
                tempFileHeader.toString()
        ]
        if (DEBUG) { println "${command}" }
        executeCommand(command)

        if (DEBUG) { println "Add the description text to the header:" }
        def headerDescriptionTextOffset = logoOffset
        command = [
                "composite",
                tempFileHeaderDescriptionText.toString(),
                "-gravity",
                "SouthWest",
                "-geometry",
                "+${headerTextOffset}+${headerDescriptionTextOffset}",
                tempFileHeader.toString(),
                tempFileHeader.toString()
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

        tempFileScaled.delete()
        tempFileHeaderSecurityClassificationText.delete()
        tempFileHeaderTitleText.delete()
        tempFileHeaderDescriptionText.delete()

        return tempFileHeader.toString()
    }

    def executeCommand(def executableCommand)
    {
        return Utility.executeCommand(executableCommand, true).text
    }
}
