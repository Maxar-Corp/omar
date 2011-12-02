package org.ossim.omar

import org.apache.commons.collections.map.CaseInsensitiveMap
import java.util.zip.ZipOutputStream
import java.util.zip.ZipEntry
import javax.imageio.ImageIO
import org.springframework.beans.factory.InitializingBean
import java.awt.image.BufferedImage
import java.awt.Graphics2D
import groovy.xml.StreamingMarkupBuilder

class WmsController extends OgcController implements InitializingBean
{
  def rasterEntrySearchService
  def rasterKmlService
  def webMappingService
  def wmsLogService
  def scratchDir

  def wms = {WmsCommand cmd ->

    //println params

    cmd.clearErrors()  // because validation happens on entry so clear errors and re-bind
    Utility.simpleCaseInsensitiveBind(cmd, params);
    if ( !cmd.validate() )
    {
      log.error(cmd.createErrorString())
      ogcExceptionService.writeResponse(response, ogcExceptionService.formatWmsException(cmd))
    }
    else
    {
      def starttime = System.currentTimeMillis()
      def internaltime = starttime
      def endtime = starttime

      def wmsLogParams = cmd.toMap()

      wmsLogParams.startDate = new Date()

      def logParameters = true
      try
      {
        switch ( cmd?.request?.toLowerCase() )
        {
        case "getmap":
          forward(action: "getMap", params: params)
          break
        case "getcapabilities":
          forward(action: "getCapabilities", params: params)
          break
        case "getkml":
          forward(action: "getKml", params: params)
          break
        case "getkmz":
          forward(action: "getKmz", params: params)
          break
        default:
          logParameters = false
          log.error("ERROR: Unknown action: ${cmd?.request}")
          break
        }
/*
      println "*"*80
      request.getHeaderNames().each{name->
        println "${name} = ${request.getHeader(name)}"
      }
*/
        endtime = System.currentTimeMillis()
/*
      wmsLogParams.domain = authenticateService.userDomain()
*/
        def principal = springSecurityService?.principal
        def hasUserInformation = !(springSecurityService?.principal instanceof String)
        def secUser = hasUserInformation ? SecUser.findByUsername(principal.username) : null
        wmsLogParams.userName = secUser ? secUser.username : principal
        wmsLogParams.domain = ""
        def domain = null
        def clientIp = request.getHeader('Client-ip')
        def XForwarded = request.getHeader('X-Forwarded-For')
        wmsLogParams.ip = XForwarded
        if ( clientIp )
        {
          if ( wmsLogParams.ip )
          {
            wmsLogParams.ip += ", ${clientIp}"
          }
          else
          {
            wmsLogParams.ip = clientIp
          }
        }

        if ( !wmsLogParams.ip )
        {
          wmsLogParams.ip = request.getRemoteAddr()
        }

        if ( logParameters )
        {
          def urlTemp = createLink([controller: 'ogc', action: 'wms', absolute: true, params: params])
          wmsLogParams.with {
            endDate = new Date()
            internalTime = (internaltime - starttime) / 1000.0
            renderTime = (endtime - internaltime) / 1000.0
            totalTime = (endtime - starttime) / 1000.0
            url = urlTemp
          }

          //wmsLogService.logParams(wmsLogParams)
        }
      }
      catch (java.lang.Exception e)
      {
        log.error("OGC::WMS exception: ${e.message}")
      }
    }
    return null
  }

  def footprints = {
//    def start = System.currentTimeMillis()
    Utility.removeEmptyParams(params)
    if ( params.max == null )
    {
      params.max = grailsApplication.config.wms.vector.maxcount
    }
    def wmsRequest = new WMSRequest()

    Utility.simpleCaseInsensitiveBind(wmsRequest, params);

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

      for ( def index in 0..<layers.size() )
      {
        //println "${layers[index]}"

        def styleName = null
        def style = null

        try
        {
          styleName = styles[index]
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
                layers[index],
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
      response.outputStream.close()
    }
    catch (java.lang.Exception e)
    {
      log.error("Exception OGC:FOOTPRINTS: ${e.message}")
    }

    if ( g2d )
    {
      g2d.dispose()
    }
//    def stop = System.currentTimeMillis()
    //    println "${wmsRequest.bbox}: ${stop - start}ms"
  }

  def getKmz = {WmsCommand cmd ->
    cmd.clearErrors()  // because validation happens on entry so clear errors and re-bind
    Utility.simpleCaseInsensitiveBind(cmd, params);
    if ( !cmd.validate() )
    {
      log.error(cmd.createErrorString())
      ogcExceptionService.writeResponse(response, ogcExceptionService.formatWmsException(cmd))
    }
    else
    {

      def kmlbuilder = new StreamingMarkupBuilder()
      kmlbuilder.encoding = "UTF-8"


      Utility.simpleCaseInsensitiveBind(cmd, params);
      // will only support png or jpegs
      def format = cmd.format ?: "image/png"
      def ext = ".png"

      switch ( format.toLowerCase() )
      {
      case ~/.*jpeg.*/:
        format = "image/jpeg"
        ext = ".jpg"
        break
      case ~/.*png.*/:
        format = "image/png"
        ext = ".png"
        break
      default:
        format = "image/png"
        ext = ".png"
        break
      }
      cmd.format = format
      cmd.request = "GetMap"
      cmd.srs = "EPSG:4326"
      def wmsQuery = webMappingService.setupQuery(cmd);
      def rasterEntryList = wmsQuery.getRasterEntriesAsList();

      def image = webMappingService.getMap(cmd, rasterEntryList).image
      def tempDescription = rasterEntryList ? rasterKmlService.createImageKmlDescription(rasterEntryList[0]) : "No images found for the kmz query"
      if ( image && (rasterEntryList.size() > 0) )
      {
        def nameString = rasterEntryList[0].title
        nameString = nameString ?: rasterEntryList[0].indexId
        def bounds = cmd.bounds
        def kmlnode = {
          mkp.xmlDeclaration()
          kml("xmlns": "http://earth.google.com/kml/2.1") {
            Document() {
              GroundOverlay() {
                name("${nameString}")
                Snippet()
                description { mkp.yieldUnescaped("<![CDATA[${tempDescription}]]>") }
                open("1")
                visibility("1")
                Icon() {
                  href { mkp.yieldUnescaped("images/image${ext}") }
                }
                LatLonBox() {
                  north(bounds.maxy)
                  south(bounds.miny)
                  east(bounds.maxx)
                  west(bounds.minx)
                }
              }
            }
          }
        }

        response.contentType = "application/vnd.google-earth.kmz"
        response.setHeader("Content-disposition", "attachment; filename=output.kmz")
        def zos = new ZipOutputStream(response.outputStream)
        //create a new zip entry
        def anEntry = null

        anEntry = new ZipEntry("doc.kml");
        //place the zip entry in the ZipOutputStream object
        zos.putNextEntry(anEntry);

        zos << kmlbuilder.bind(kmlnode).toString()
        anEntry = new ZipEntry("images/image${ext}");
        //place the zip entry in the ZipOutputStream object
        zos.putNextEntry(anEntry);
        if ( image )
        {
          ImageIO.write(image, format.split("/")[-1], zos);
        }
        zos.close();
        response.outputStream.close()
      }
      else
      {
        render(contentType: "text/plain", text: "Unable to chip image for KMZ given parameters ${params}")
      }
    }
    null
  }

  def getCapabilities = { WmsCommand cmd ->
    cmd.clearErrors()  // because validation happens on entry so clear errors and re-bind
    Utility.simpleCaseInsensitiveBind(cmd, params);
    if ( !cmd.validate() )
    {
      log.error(cmd.createErrorString())
      ogcExceptionService.writeResponse(response, ogcExceptionService.formatWmsException(cmd))
    }
    else
    {

      //wmsLogParams.request = "getcapabilities"
      def serviceAddress = createLink(controller: "ogc", action: "wms", absolute: true) as String
      def capabilities = webMappingService?.getCapabilities(cmd, serviceAddress)
      //internaltime = System.currentTimeMillis();
      render(contentType: "text/xml", text: capabilities)
    }
  }

  def getKml = { WmsCommand cmd ->
    cmd.clearErrors()  // because validation happens on entry so clear errors and re-bind
    Utility.simpleCaseInsensitiveBind(cmd, params);
    if ( !cmd.validate() )
    {
      log.error(cmd.createErrorString())
      ogcExceptionService.writeResponse(response, ogcExceptionService.formatWmsException(cmd))
    }
    else
    {

      def wmsParams = [:]
      //wmsLogParams.request = "getkml"

      // Convert param names to lower case
      params?.each { wmsParams?.put(it.key.toLowerCase(), it.value)}

      def rasterIdList = params.layers.split(",")

      //  def serviceAddress = createLink(controller: "ogc", action: "wms", absolute: true)
      //  def kml = webMappingService.getKML(wmsRequest, serviceAddress)
      def filename = "image.kml"
      def rasterEntries = rasterEntrySearchService.findRasterEntries(rasterIdList)

      def kml = null;
      if ( rasterEntries?.size > 0 )
      {
        def tempMap = new CaseInsensitiveMap(params)
        def file = (rasterEntries[0].mainFile.name as File).name

        filename = "${file}.kml"
        kml = rasterKmlService.createImagesKml(rasterEntries, cmd.toMap(), tempMap)
      }
      else
      {
        kml = ""
        filename = "empty.kml"
      }
      //internaltime = System.currentTimeMillis();
      response.setHeader("Content-disposition", "attachment; filename=${filename}")
      render(contentType: "application/vnd.google-earth.kml+xml", text: kml, encoding: "UTF-8")
    }
  }

  def getMap = { WmsCommand cmd ->
    cmd.clearErrors()  // because validation happens on entry so clear errors and re-bind
    Utility.simpleCaseInsensitiveBind(cmd, params);
    if ( !cmd.validate() )
    {
      log.error(cmd.createErrorString())
      ogcExceptionService.writeResponse(response, ogcExceptionService.formatWmsException(cmd))
    }
    else
    {
      //wmsLogParams.request = "getmap"
      switch ( cmd?.format?.toLowerCase() )
      {
      case "jpeg":
      case "jpg":
      case "image/jpeg":
      case "image/jpg":
        if ( cmd?.transparent?.equalsIgnoreCase("true") )
        {
          cmd.format = "image/png"
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

      def mapResult = webMappingService.getMap(cmd)

      //internaltime = System.currentTimeMillis()

      if ( mapResult.errorMessage )
      {
        def message = "WMS server Error: ${mapResult.errorMessage}"
        // no data to process
        log.error(message)

        def ogcFormattedException = ogcExceptionService.formatOgcException(cmd.toMap(), message)
        ogcExceptionService.writeResponse(response, ogcFormattedException)
      }
      else
      {
        def writerType = response.contentType?.split("/")[-1]
        ImageIO.write(mapResult.image, writerType, response.outputStream)
        response.outputStream.close()
      }
    }
  }

  public void afterPropertiesSet()
  {
    scratchDir = grailsApplication.config.export.workDir ?: "/tmp";
  }
}
