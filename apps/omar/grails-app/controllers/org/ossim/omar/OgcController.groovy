package org.ossim.omar

import javax.imageio.ImageIO
import java.awt.Rectangle
import java.awt.image.BufferedImage

import java.awt.*;

import javax.media.jai.JAI
import org.apache.commons.collections.map.CaseInsensitiveMap
import geoscript.geom.Geometry
import org.geotools.geometry.jts.LiteShape
import geoscript.geom.MultiPolygon

class OgcController
{
  def rasterEntrySearchService
  def videoDataSetSearchService
  def webMappingService
  def grailsApplication
  def authenticateService
  def kmlService

  def footprints = {
    Utility.removeEmptyParams(params)

    if ( params.max == null )
    {
      params.max = grailsApplication.config.wms.vector.maxcount
    }

    // Convert param names to lower case
    def tempMap = new CaseInsensitiveMap(params)

    // Populate org.ossim.omar.WMSCapabilities Request object
    def wmsRequest = new WMSRequest()

    bindData(wmsRequest, tempMap)

    // default to geographic bounds
    if ( !wmsRequest.srs )
    {
      wmsRequest.srs = "EPSG:4326"
    }

    Graphics2D g2d = null

    try
    {
      def image = null
      def width = wmsRequest.width.toInteger()
      def height = wmsRequest.height.toInteger()

      if ( wmsRequest.transparentFlag )
      {
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
      }
      else
      {
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
      }

      g2d = image.createGraphics()

      if ( wmsRequest.bgcolor )
      {
        g2d.setPaint(wmsRequest.backgroundColor)
        g2d.fillRect(0, 0, width, height)
      }

      def dateRange = wmsRequest.dateRange
      def startDate = null
      def endDate = null

      if ( dateRange )
      {
        if ( dateRange.size() > 0 )
        {
          startDate = dateRange[0]

          if ( dateRange.size() > 1 )
          {
            endDate = dateRange[1]
          }
        }
      }

      String[] layers = wmsRequest.layers?.split(",")
      String[] styles = wmsRequest.styles?.split(",")

      layers.eachWithIndex { layer, styleIdx ->
        //println "${layer} ${styleIdx}"

        def styleName = null
        def style = null

        try
        {
          styleName = styles[styleIdx]
          style = grailsApplication.config.wms.styles[styleName]
        }
        catch (Exception e)
        {
          styleName = "default"
          style = grailsApplication.config.wms.styles[styleName]
        }

        //println "${styleName}: ${style}"

        webMappingService.drawLayer(
                style,
                layer,
                params,
                startDate,
                endDate,
                wmsRequest,
                g2d)
      }

      if ( (wmsRequest.format == "image/gif") && wmsRequest.transparentFlag )
      {
        image = ImageGenerator.convertRGBAToIndexed(image)
      }

      def formatName = wmsRequest.format?.split("/")[-1]

      response.contentType = wmsRequest.format
      ImageIO.write(image, formatName, response.outputStream)
    }
    catch (java.lang.Exception e)
    {
      log.error("Exception OGC:FOOTPRINTS: ${e.message}")
    }

    if ( g2d )
    {
      g2d.dispose()
    }
  }

  def wms = {
    def tempMap = [:]
    // Convert param names to lower case
    params?.each { tempMap.put(it.key.toLowerCase(), it.value)}
    // Populate org.ossim.omar.WMSCapabilities Request object
    def wmsRequest = new WMSRequest()

    bindData(wmsRequest, tempMap)
    // println tempMap
    try
    {
      switch ( wmsRequest?.request?.toLowerCase() )
      {
      case "getmap":

        switch ( wmsRequest?.format?.toLowerCase() )
        {
        case "jpeg":
        case "jpg":
        case "image/jpeg":
        case "image/jpg":
          if ( wmsRequest?.transparent?.equalsIgnoreCase("true") )
          {
            wmsRequest.format = "image/png"
            response.contentType = "image/png"
          }
          else
          {
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
        def filename = "image.kml"
        def rasterEntries = RasterEntry.createCriteria().list() {
          rasterIdList.each() {name ->
            try
            {
              eq('id', java.lang.Long.valueOf(name))
            }
            catch (java.lang.Exception e)
            {
              or
              {
                eq('indexId', name)
                eq('imageId', name)
              }
            }
          }
          }
        if(rasterEntries.size>0)
        {
            def file = (rasterEntries[0].mainFile.name as File).name
            filename = "${file}.kml"
        }
        def kml = kmlService.createKml(rasterEntries, tempMap)
        response.setHeader("Content-disposition", "attachment; filename=${filename}")
        render(contentType: "application/vnd.google-earth.kml+xml", text: kml, encoding: "UTF-8")
        break
      default:
        log.error("ERROR: Unknown action: ${wmsRequest?.request}")
      }
    }
    catch (java.lang.Exception e)
    {
      log.error("OGC::WMS exception: ${e.message}")
//       println "OGC::WMS Error: ${e.message}"
    }
    return null
  }

  def getTile = {
    def image = null
    def tileWidth = params.tileWidth?.toInteger()
    def tileHeight = params.tileHeight?.toInteger()
    def offsetX = Math.round(params.x?.toDouble() * tileWidth) as Integer;
    def offsetY = Math.round(params.y?.toDouble() * tileHeight) as Integer;


    try
    {
      //println "params: $params"

      def mode = "OSSIM"
      Rectangle rect = new Rectangle(offsetX, offsetY, tileWidth, tileHeight)
      def width
      def height

      switch ( mode )
      {
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
        if ( maxDimension < height )
        {
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

        if ( stretchModeRegion.equals("viewport") )
        {
          viewportStretchMode = stretchMode;
          stretchMode = "";
        }
        else
        {
          viewportStretchMode = "";
        }
        switch ( mode2 )
        {
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
    catch (Exception e)
    {
//      image = new BufferedImage(tileWidth, tileHeight, BufferedImage.TYPE_INT_RGB)

//      response.contentType = "image/jpeg"
//      ImageIO.write(image, "jpeg", response.outputStream)

      return null;

    }
  }
}
