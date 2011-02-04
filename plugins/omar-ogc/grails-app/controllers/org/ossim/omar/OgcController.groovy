package org.ossim.omar

import javax.imageio.ImageIO
import java.awt.Rectangle
import java.awt.image.BufferedImage

import java.awt.*;

import org.apache.commons.collections.map.CaseInsensitiveMap
import javax.imageio.IIOImage

class OgcController
{
  def rasterEntrySearchService
  def videoDataSetSearchService
  def webMappingService
  def wmsLogService
  def grailsApplication
  def authenticateService
  def kmlService

  def footprints = {
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
  }

  def wms = {
    def starttime = System.currentTimeMillis()
    def internaltime = starttime
    def endtime = starttime
    // Populate org.ossim.omar.WMSCapabilities Request object
    def wmsRequest = new WMSRequest()


    Utility.simpleCaseInsensitiveBind(wmsRequest, params);
    def wmsLogParams = wmsRequest.toMap()
    wmsLogParams.startDate = new Date()

    def tempMap = new CaseInsensitiveMap(params)
    def logParameters = true
    try
    {
      switch ( wmsRequest?.request?.toLowerCase() )
      {
      case "getmap":
        wmsLogParams.request = "getmap"
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


        def image = webMappingService.getMap(wmsRequest)

        internaltime = System.currentTimeMillis()
        if ( !image )
        {
          log.error("No image found for layers ${wmsRequest.layers}")
        }
        else
        {
          /*
          def writers = ImageIO.getImageWritersByMIMEType(response.contentType)
          if ( writers.hasNext() )
          {
            def writer = writers.next()
            if ( writer )
            {
              def writeParam = writer.getDefaultWriteParam()
              if ( writeParam.canWriteCompressed() )
              {
                //               writeParam.compressionMode = javax.imageio.ImageWriteParam.MODE_EXPLICIT
                //               writeParam.compressionQuality = 0.1;
                //               writeParam.setProgressiveMode(javax.imageio.ImageWriteParam.MODE_COPY_FROM_METADATA)
              }
              writer.output = ImageIO.createImageOutputStream(response.outputStream)
              def iioimage = new IIOImage(image, [], null)
              writer.write(writer.getDefaultStreamMetadata(writeParam), iioimage, writeParam)
              writer.output.close()
            }
          }
          else
          {
          */
            ImageIO.write(image, response.contentType?.split("/")[-1], response.outputStream)
            response.outputStream.close()
          //}
        }

        break
      case "getcapabilities":
        wmsLogParams.request = "getcapabilities"
        def serviceAddress = createLink(controller: "ogc", action: "wms", absolute: true) as String
        def capabilities = webMappingService?.getCapabilities(wmsRequest, serviceAddress)
        internaltime = System.currentTimeMillis();
        render(contentType: "text/xml", text: capabilities)
        break
      case "getkml":
        def wmsParams = [:]
        wmsLogParams.request = "getkml"

        // Convert param names to lower case
        params?.each { wmsParams?.put(it.key.toLowerCase(), it.value)}

        def rasterIdList = params.layers.split(",")

        //  def serviceAddress = createLink(controller: "ogc", action: "wms", absolute: true)
        //  def kml = webMappingService.getKML(wmsRequest, serviceAddress)
        def filename = "image.kml"
        def rasterEntries = findRasterEntries(rasterIdList)

        def kml = null;
        if ( rasterEntries?.size > 0 )
        {
          def file = (rasterEntries[0].mainFile.name as File).name
          filename = "${file}.kml"
          kml = kmlService.createImagesKml(rasterEntries, wmsRequest.toMap(), tempMap)
        }
        else
        {
          kml = ""
          filename = "empty.kml"
        }
        internaltime = System.currentTimeMillis();
        response.setHeader("Content-disposition", "attachment; filename=${filename}")
        render(contentType: "application/vnd.google-earth.kml+xml", text: kml, encoding: "UTF-8")
        break
      case "getkmz":
        render contentType: "text/plain", text: new Date() as String
        break
      default:
        logParameters = false
        log.error("ERROR: Unknown action: ${wmsRequest?.request}")
        break
      }
/*
      println "*"*80
      request.getHeaderNames().each{name->
        println "${name} = ${request.getHeader(name)}"
      }
*/
      endtime = System.currentTimeMillis()
      wmsLogParams.domain = authenticateService.userDomain()
      wmsLogParams.userName = "nobody"
      def domain = null
      def clientIp = request.getHeader('Client-ip')
      def XForwarded = request.getHeader('X-Forwarded-For')
      wmsLogParams.ip = XForwarded
      if(clientIp)
      {
        if(wmsLogParams.ip)
        {
          wmsLogParams.ip += ", ${clientIp}"
        }
        else
        {
          wmsLogParams.ip = clientIp
        }
      }

      if ( !wmsLogParams.ip)
      {
        wmsLogParams.ip = request.getRemoteAddr()
      }
      if ( wmsLogParams.domain )
      {
        def authUser = AuthUser.get(wmsLogParams.domain.id)
        wmsLogParams.userName = authUser?.username
        wmsLogParams.domain = authUser?.email.split('@')[1]
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
        wmsLogService.logParams(wmsLogParams)
      }
    }
    catch (java.lang.Exception e)
    {
      log.error("OGC::WMS exception: ${e.message}")
//       println "OGC::WMS Error: ${e.message}"
    }
    return null
  }

  def findRasterEntries(def rasterIdList)
  {
    def rasterEntries = RasterEntry.createCriteria().list() {
      rasterIdList.each() {name ->
        if ( name ==~ /\d+/ )
        {
          eq('id', Long.valueOf(name))
        }
        else
        {
          or {
            eq('indexId', name)
            eq('title', name)
          }
        }
      }
    }

    return rasterEntries
  }

  def getTile = {
    log.warn("OgcController getTile is deprecated and image space operations should go through ../icp/getTile\ninstead of /ogc/getTile")
    redirect(controller: "icp", action: "getTile", params: params)
  }
}
