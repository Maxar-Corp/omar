package org.ossim.omar

import javax.imageio.ImageIO
import java.awt.Rectangle
import java.awt.image.BufferedImage
import groovy.xml.StreamingMarkupBuilder

import java.awt.*;

import org.apache.commons.collections.map.CaseInsensitiveMap
import javax.imageio.IIOImage
import org.springframework.beans.factory.InitializingBean
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class OgcController implements InitializingBean
{
  def rasterEntrySearchService
  def videoDataSetSearchService
  def webMappingService
  def webCoverageService
  def wmsLogService
  def grailsApplication
  def scratchDir
  def ogcExceptionService
/*
def authenticateService
*/
  def springSecurityService
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
  def wcs = {WcsCommand cmd ->
      cmd.clearErrors()  // because validation happens on entry so clear errors and re-bind
      // for now until we can develop a plugin for the WCS
      // we will hardcode the output format test list here
      //
      def starttime = System.currentTimeMillis()
      def internaltime = starttime
      def endtime      = starttime
      Utility.simpleCaseInsensitiveBind(cmd, params)
      if(!cmd.validate())
      {
          log.error(cmd.createErrorString())
          ogcExceptionService.writeResponse(response, ogcExceptionService.formatWcsException(cmd))
      }
      else
      {
          try
          {
            switch ( cmd?.request?.toLowerCase() )
            {
                case "getcoverage":
                    def wmsQuery  = new WMSQuery()
                    def wcsParams    = cmd.toMap();

                    // we will use layers for for the WMSQuery object is already setup to use
                    // layers param and there is not much difference for query params
                    // given WCS or WMS
                    wcsParams.layers = params.coverage;
                    if(wcsParams.layers&&wcsParams.layers.toLowerCase() == "raster_entry")
                    {
                        wcsParams.layers = ""
                    }
                    // for now I will hard code a max mosaic size
                    //
                    def max    = params.max?params.max as Integer:10
                    if(max > 10) max = 10
                    Utility.simpleCaseInsensitiveBind(wmsQuery, wcsParams)
                    wmsQuery.max = max

                    // for now we will sort by the date field if no layers are given
                    //
                    if(!wmsQuery.layers)
                    {
                        wmsQuery.sort  = wmsQuery.sort?:"acquisitionDate"
                        wmsQuery.order = wmsQuery.order?:"desc"
                    }
                    def rasterEntries = wmsQuery.getRasterEntriesAsList();
                    if(rasterEntries)
                    {
                        rasterEntries = rasterEntries?.reverse()
                    }
                    if(!rasterEntries)
                    {
                        def ogcParams = cmd.toMap();
                        def message = "WCS server Error: No coverage found for ${coverage}"
                        // no data to process
                        log.error(message)

                        def ogcFormattedException = ogcExceptionService.formatOgcException(ogcParams, message)
                        ogcExceptionService.writeResponse(response, ogcFormattedException)
                    }
                    else
                    {
                        def result = webCoverageService.getCoverage(rasterEntries, cmd)
                        if(result)
                        {
                            def imageFile = result.file
                            def attachment = result.outputName?"filename=${result.outputName}":""
                            response.setHeader("Content-disposition", "attachment; ${attachment}")
                            response.contentType = result.contentType
                            try {
                                Utility.writeFileToOutputStream(imageFile, response.outputStream, 4096);
                            }
                            catch(Exception e)
                            {
                                log.error(e)
                            }
                            response.outputStream.flush()
                            response.outputStream.close()

                            imageFile.delete()
                        }
                    }
                    break
                default:
                    break
            }
          }
          catch(Exception e)
          {
            log.error(e)
          }
      }
      null
  }
  def wms = {WmsCommand cmd->
      cmd.clearErrors()  // because validation happens on entry so clear errors and re-bind
      Utility.simpleCaseInsensitiveBind(cmd, params);
      if(!cmd.validate())
      {
          log.error(cmd.createErrorString())
          ogcExceptionService.writeResponse(response, ogcExceptionService.formatWmsException(cmd))
      }
      else
      {
          def starttime = System.currentTimeMillis()
          def internaltime = starttime
          def endtime = starttime
          // Populate org.ossim.omar.WMSCapabilities Request object
          //def wmsRequest = new WMSRequest()


          def wmsLogParams = cmd.toMap()
	
          wmsLogParams.startDate = new Date()

          def tempMap = new CaseInsensitiveMap(params)
          def logParameters = true
          try
          {
            switch ( cmd?.request?.toLowerCase() )
            {
            case "getmap":
              wmsLogParams.request = "getmap"
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

              internaltime = System.currentTimeMillis()
              if ( mapResult.errorMessage )
              {
                  def message = "WCS server Error: ${mapResult.errorMessage}"
                  // no data to process
                  log.error(message)

                  def ogcFormattedException = ogcExceptionService.formatOgcException(ogcParams, message)
                  ogcExceptionService.writeResponse(response, ogcFormattedException)
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
                  ImageIO.write(mapResult.image, response.contentType?.split("/")[-1], response.outputStream)
                  response.outputStream.close()
                //}
              }

              break
            case "getcapabilities":
              wmsLogParams.request = "getcapabilities"
              def serviceAddress = createLink(controller: "ogc", action: "wms", absolute: true) as String
              def capabilities = webMappingService?.getCapabilities(cmd, serviceAddress)
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
                kml = kmlService.createImagesKml(rasterEntries, cmd.toMap(), tempMap)
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
                this.kmz(cmd)
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
            def secUser = hasUserInformation?SecUser.findByUsername(principal.username):null
            wmsLogParams.userName = secUser?secUser.username:principal
            wmsLogParams.domain = ""
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
//      if ( wmsLogParams.domain )
//      {
/*
        def authUser = AuthUser.get(wmsLogParams.domain.id)
*/
          //	  println "GETTING AUTH USER"
            //  def authUser = SecUser.findByUsername(springSecurityService.principal.username)
          //	  println "AUTH USER: ${authUser}"
          //    wmsLogParams.userName = authUser?.username
          //    wmsLogParams.domain = authUser?.email.split('@')[1]
//      }
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
      }
    return null
  }
  def kmz = {WmsCommand cmd->
      cmd.clearErrors();

      def kmlbuilder = new StreamingMarkupBuilder()
      kmlbuilder.encoding = "UTF-8"


      Utility.simpleCaseInsensitiveBind(cmd, params);
      // will only support png or jpegs
      def format =  cmd.format?:"image/png"
      def ext =  ".png"

      switch(format.toLowerCase())
      {
          case ~/.*jpeg.*/:
              format = "image/jpeg"
              ext = ".jpg"
              break
          case ~/.*png.*/:
              format  = "image/png"
              ext = ".png"
              break
          default:
              format  = "image/png"
              ext = ".png"
          break
      }
      cmd.format = format
      cmd.request = "GetMap"
      cmd.srs     = "EPSG:4326"
      def wmsQuery = webMappingService.setupQuery(cmd);
      def rasterEntryList = wmsQuery.getRasterEntriesAsList();

      def image = webMappingService.getMap(cmd, rasterEntryList).image
      def tempDescription = rasterEntryList?kmlService.createImageKmlDescription(rasterEntryList[0]):"No images found for the kmz query"
      if(image&&(rasterEntryList.size()>0) )
      {
          def nameString = rasterEntryList[0].title
          nameString = nameString?:rasterEntryList[0].indexId
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
          def zos =  new ZipOutputStream(response.outputStream)
          //create a new zip entry
          def anEntry = null

          anEntry = new ZipEntry("doc.kml");
          //place the zip entry in the ZipOutputStream object
          zos.putNextEntry(anEntry);

          zos << kmlbuilder.bind(kmlnode).toString()
          anEntry = new ZipEntry("images/image${ext}");
          //place the zip entry in the ZipOutputStream object
          zos.putNextEntry(anEntry);
          if(image)
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
      null
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

  public void afterPropertiesSet()
  {
     scratchDir = grailsApplication.config.export.workDir?:"/tmp";
  }
}
