package omar.image.magick

import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.ossim.omar.core.Utility

class TemplateExportService
{
    LinkGenerator grailsLinkGenerator

    def DEBUG = true
    def grailsApplication
    def northArrowGeneratorService

    def serviceMethod(def country, def footerFile, def headerFile, def imageFile, def imageHeight, def includeOverviewMap, def northArrowFile)
    {
        def command
        def date = new Date().getTime()
        def tempFilesLocation = grailsApplication.config.export.workDir + "/"
        def tempFilesLocationAsFile = new File(tempFilesLocation)
        def logoFilesLocation = grailsLinkGenerator.resource(absolute: true, dir: 'images', plugin: 'omar-image-magick') + "/"
        def mapFilesLocation = logoFilesLocation + "overviewMaps/"
        def tempFileFinishedProduct = File.createTempFile("finishedProduct",
                ".png", tempFilesLocationAsFile);
        def tempFileOverviewMapScaled = File.createTempFile("overviewMapScaled",
                ".png", tempFilesLocationAsFile);
        def tempFileOverviewMapScaledShadow = File.createTempFile("overviewMapScaledShadow",
                ".png", tempFilesLocationAsFile);

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
                tempFileFinishedProduct.toString()
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
                    tempFileOverviewMapScaled.toString()
            ]
            if (DEBUG) { println "${command}" }
            executeCommand(command)

            //########## Add a shadow to the overview map
            if (DEBUG) { println "Generate a shadow image for the overview map:" }
            command = [
                    "convert",
                    tempFileOverviewMapScaled.toString(),
                    "-background",
                    "black",
                    "-shadow",
                    "60x4+4+4",
                    tempFileOverviewMapScaledShadow.toString()
            ]
            if (DEBUG) { println "${command}" }
            executeCommand(command)

            if (DEBUG) { println "Add the shadow image to the overview map:" }
            command = [
                    "convert",
                    "-page",
                    "+4+4",
                    tempFileOverviewMapScaled.toString(),
                    "-matte",
                    tempFileOverviewMapScaledShadow.toString(),
                    "+swap",
                    "-background",
                    "none",
                    "-mosaic",
                    tempFileOverviewMapScaled.toString()
            ]
            if (DEBUG) { println "${command}" }
            executeCommand(command)

            //########## Determine the width of the overview map
            if (DEBUG) { println "Determine the width of the overview map:" }
            command = [
                    "identify",
                    "-format",
                    "%w",
                    tempFileOverviewMapScaled.toString()
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
                    tempFileOverviewMapScaled.toString(),
                    "-gravity",
                    "NorthEast",
                    tempFileFinishedProduct.toString(),
                    tempFileFinishedProduct.toString()
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
                tempFileFinishedProduct.toString(),
                tempFileFinishedProduct.toString()
        ]
        if (DEBUG) { println "${command}" }
        executeCommand(command)


        //#############################################################################################################################
        //################################################## Temporary File Deletion ##################################################
        //#############################################################################################################################

        try{
            new File(imageFile).delete()
            new File(footerFile).delete()
            new File(headerFile).delete()
            new File(northArrowFile).delete()
            tempFileOverviewMapScaled.delete()
            tempFileOverviewMapScaledShadow.delete()
        }
        catch(def e)
        {
        }

        return tempFileFinishedProduct.toString()
    }

    def executeCommand(def executableCommand)
    {
        return Utility.executeCommand(executableCommand, true).text
    }
}
