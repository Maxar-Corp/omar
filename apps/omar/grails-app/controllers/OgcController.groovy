import javax.imageio.ImageIO
import java.awt.Rectangle
import java.awt.image.BufferedImage
import java.awt.image.*;
import java.awt.*;

import javax.media.jai.JAI

import org.grails.plugins.springsecurity.service.AuthenticateService

class OgcController {
  def rasterEntrySearchService
  def videoDataSetSearchService
  def webMappingService
  def grailsApplication
  AuthenticateService authenticateService
  KmlService kmlService

  def footprints = {

    def tempMap = [:]

    if (params.max == null){
      params.max = grailsApplication.config.wms.maxvectorresults
    }
    // Convert param names to lower case
    params.each { tempMap.put(it.key.toLowerCase(), it.value)}

    // Populate WMSCapabilities Request object
    def wmsRequest = new WMSRequest()
    bindData(wmsRequest, tempMap)
    // default to geographic bounds
    if (!wmsRequest.srs) {
      wmsRequest.srs = "EPSG:4326"
    }
    Graphics2D g = (Graphics2D) null
    try {
      def image = (BufferedImage) null
      if (wmsRequest.getTransparentFlag()) {
        image = new BufferedImage(wmsRequest.width.toInteger(),
                wmsRequest.height.toInteger(),
                BufferedImage.TYPE_INT_ARGB)
      }
      else {
        image = new BufferedImage(wmsRequest.width.toInteger(),
                wmsRequest.height.toInteger(),
                BufferedImage.TYPE_INT_RGB)
      }
      g = image.createGraphics()

      if (wmsRequest.bgcolor) {
        g.setPaint(wmsRequest.getBackgroundColor())
        g.fillRect(0, 0, w, h)
      }
      def minx = -180.0
      def maxx = 180.0
      def miny = -90.0
      def maxy = 90.0
      if (wmsRequest.bbox) {
        def bounds = wmsRequest.bbox.split(',')
        minx = bounds[0] as double
        miny = bounds[1] as double
        maxx = bounds[2] as double
        maxy = bounds[3] as double
      }

      def dateRange = wmsRequest.getDateRange();
      def startDate = (Date)null
      def endDate   = (Date)null
      if (dateRange) {
        if (dateRange.size() > 0) {
          startDate = dateRange[0]
          if (dateRange.size() > 1)
            endDate = dateRange[1]
        }
      }
      String[] styles = wmsRequest.styles?.split(",")
      String[] layers = wmsRequest.layers?.split(",")

      def geometries = []
      int styleIdx = 0
      def style = "default"
      layers.each {
        try {
          style = styles[styleIdx]
        }
        catch (java.lang.Exception e) {
          style = "default"
        }
        if (it == "Imagery" ||
            it == "ImageryData") {
          def queryParams = new RasterEntryQuery()
          bindData(queryParams, params)
          queryParams.aoiMaxLat = maxy
          queryParams.aoiMinLat = miny
          queryParams.aoiMaxLon = maxx
          queryParams.aoiMinLon = minx
          queryParams.startDate = startDate
          queryParams.endDate = endDate
          def rasterEntries = rasterEntrySearchService.runQuery(queryParams, params, false).rasterEntries
          rasterEntries.each {rasterEntry ->
            geometries.add(rasterEntry.groundGeom.geom)
          }
        }
        else if (it == "Videos" ||
                 it == "VideoData")
        {
          def queryParams = new VideoDataSetQuery()
          bindData(queryParams, params)
          queryParams.aoiMaxLat = maxy
          queryParams.aoiMinLat = miny
          queryParams.aoiMaxLon = maxx
          queryParams.aoiMinLon = minx
          queryParams.startDate = startDate
          queryParams.endDate = endDate
          def videoDataSets = videoDataSetSearchService.runQuery(queryParams, params)
          videoDataSets.each {videoDataSet ->
            geometries.add(videoDataSet.groundGeom.geom)
          }
        }

        webMappingService.drawCoverage(g, wmsRequest, geometries, style)
        geometries.clear()
        ++styleIdx
      }

      switch (wmsRequest?.format?.toLowerCase()) {
        case "jpeg":
        case "jpg":
        case "image/jpeg":
        case "image/jpg":
          if (wmsRequest?.transparent?.equalsIgnoreCase("true")) {
            wmsRequest.format = "image/png"
            response.contentType = "image/png"
          }
          else {
            response.contentType = "image/jpeg"
          }
          break
        case "png":
        case "image/png":
          response.contentType = "image/png"
          break
        case "gif":
        case "image/gif":
          response.contentType = "image/gif"
          break
        default:
          response.contentType = "image/png"
          break;
      }
      response.contentType = wmsRequest.format
      if ((response.contentType == "image/gif") &&
              (wmsRequest.getTransparentFlag())) {
        ImageIO.write(ImageGenerator.convertRGBAToIndexed((BufferedImage) image), "gif", response.outputStream)
      }
      else {
        ImageIO.write(image, response.contentType?.split("/")[-1], response.outputStream)
      }

    }
    catch (java.lang.Exception e) {
      println e
    }
    if (g) {
      g.dispose()
    }
  }

  def wms = {

    def tempMap = [:]

    // Convert param names to lower case
    params.each { tempMap.put(it.key.toLowerCase(), it.value)}

    // Populate WMSCapabilities Request object
    def wmsRequest = new WMSRequest()
    def debugFlag = false;
    if (tempMap.containsKey("debug")) {
      debugFlag = tempMap.debug.toString().equals("true");
    }

    bindData(wmsRequest, tempMap)

    if (debugFlag) {
      println "Starting ${wmsRequest.layers}"
    }
    try {
      switch (wmsRequest?.request?.toLowerCase()) {
        case "getmap":

          switch (wmsRequest?.format?.toLowerCase()) {
            case "jpeg":
            case "jpg":
            case "image/jpeg":
            case "image/jpg":
              if (wmsRequest?.transparent?.equalsIgnoreCase("true")) {
                wmsRequest.format = "image/png"
                response.contentType = "image/png"
              }
              else {
                response.contentType = "image/jpeg"
              }
              break
            case "png":
            case "image/png":
              response.contentType = "image/png"
              break
            case "gif":
            case "image/gif":
              response.contentType = "image/gif"
              break
          }

          def starttime = System.currentTimeMillis()
          def image = webMappingService.getMap(wmsRequest)

          ImageIO.write(image, response.contentType?.split("/")[-1], response.outputStream)

          def endtime = System.currentTimeMillis()
          def principal = authenticateService.principal()

          //if ( principal != "anonymousUser" )
          //{
          //def user = principal.username

          def logData = [
                  TYPE: "wms_getmap",
                  START: new Date(starttime),
                  END: new Date(endtime),
                  ELAPSE_TIME_MILLIS: endtime - starttime,
                  //USER: user,
                  PARAMS: wmsRequest,
                  MODE: webMappingService.mode
          ]
          log.info(logData)
          break
        case "getcapabilities":
          def serviceAddress = createLink(controller: "ogc", action: "wms", absolute: true) as String
          def capabilities = webMappingService?.getCapabilities(wmsRequest, serviceAddress)

          render(contentType: "text/xml", text: capabilities)
          break
        case "getkml":

          def wmsParams = [:]

          // Convert param names to lower case
          params?.each { wmsParams?.put(it.key.toLowerCase(), it.value)}

          def rasterIdList = params.layers.split(",")

          //  def serviceAddress = createLink(controller: "ogc", action: "wms", absolute: true)
          //  def kml = webMappingService.getKML(wmsRequest, serviceAddress)
          def rasterEntryList = []
          def filename = "image.kml"
          params?.layers.split(',').each {item ->
            def rasterEntry = RasterEntry.get(item)
            rasterEntryList.add(rasterEntry)
            def file = (rasterEntry.mainFile.name as File).name
            filename = "${file}.kml"
          }
          def kml = kmlService.createKml(rasterEntryList)
          response.setHeader("Content-disposition", "attachment; filename=${filename}")
          render(contentType: "application/vnd.google-earth.kml+xml", text: kml, encoding: "UTF-8")
          break
        default:
          println "ERROR: Unknown action: ${wmsRequest?.request}"
      }
    }
    catch (java.lang.Exception e) {
      // println "OGC::WMS Error: ${e.message}"
    }
    if (debugFlag) {
      println "done  ${wmsRequest.layers}"
    }

    return null
  }

  def getTile = {


    def image = null
    def tileWidth = params.tileWidth?.toInteger()
    def tileHeight = params.tileHeight?.toInteger()
    def offsetX = Math.round(params.x?.toDouble() * tileWidth) as Integer;
    def offsetY = Math.round(params.y?.toDouble() * tileHeight) as Integer;


    try {
      //println "params: $params"

      def mode = "OSSIM"
      Rectangle rect = new Rectangle(offsetX, offsetY, tileWidth, tileHeight)
      def width
      def height

      switch (mode) {
        case "JAI":
          image = JAI.create("imageread", inputFile)

          def raster = image.getData(rect).createTranslatedChild(0, 0)

          image = new BufferedImage(image.colorModel, raster, false, null)
          width = image.width
          height = image.height
          break

        case "OSSIM":
          def rasterEntry = RasterEntry.get(params.id)
          String inputFile = rasterEntry.mainFile.name

          width = rasterEntry?.width
          height = rasterEntry?.height

          def numRLevels = 1
          def tileSize = 256
          def targetFullRect = (2 ** params.z?.toInteger()) * tileSize;

//          while ( width > tileSize )
//          {
//            width /= 2
//            height /= 2
//            numRLevels++
//          }
          def maxDimension = width;
          if (maxDimension < height) {
            maxDimension = height;
          }
          BigDecimal scale = (double) targetFullRect / (double) maxDimension;
          def outputType = "jpeg"
          def resLevel = numRLevels - params.z?.toInteger() - 1
          int startSample = rect.x
          int endSample = rect.x + rect.width - 1
          int startLine = rect.y
          int endLine = rect.y + rect.height - 1
          int entry = rasterEntry.entryId?.toInteger()

          def mode2 = "LIBCALL"
          String stretchMode = params?.stretch_mode ?: "linear_auto_min_max";
          String stretchModeRegion = params?.stretch_mode_region ?: "global"
          String viewportStretchMode = ""

          if (stretchModeRegion.equals("viewport")) {
            viewportStretchMode = stretchMode;
            stretchMode = "";
          }
          else {
            viewportStretchMode = "";
          }
          switch (mode2) {
            case "SYSCALL":
              def outputFile = File.createTempFile("ogcoms", ".jpg")
              def cmd = "icp --res-level ${resLevel} --start-sample ${startSample} --end-sample ${endSample} --start-line ${startLine} --end-line ${endLine}  --use-scalar-remapper --entry ${entry} --writer-prop 'create_external_geometry=false' ${outputType} ${inputFile}  ${outputFile}"

              //println "$cmd"

              def process = cmd.execute()

              process.consumeProcessOutput()
              process.waitFor();
              image = ImageIO.read(outputFile)
              outputFile.delete()
              // delete the geom file
              new File(outputFile.absolutePath - "jpg" + "geom").delete()
              break
            case "LIBCALL":
              image = webMappingService.getUnprojectedTile(
                      rect,
                      inputFile,
                      entry,
                      stretchMode,
                      viewportStretchMode,
                      scale,
                      startSample,
                      endSample,
                      startLine,
                      endLine)

              break
          }
          break
      }
      response.contentType = "image/jpeg"
      ImageIO.write(image, "jpeg", response.outputStream)
    }
    catch (Exception e) {
//      image = new BufferedImage(tileWidth, tileHeight, BufferedImage.TYPE_INT_RGB)

//      response.contentType = "image/jpeg"
//      ImageIO.write(image, "jpeg", response.outputStream)

      return null;

    }
  }
}
