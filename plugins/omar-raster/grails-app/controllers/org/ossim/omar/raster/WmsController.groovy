package org.ossim.omar.raster

import org.apache.commons.collections.map.CaseInsensitiveMap

import org.springframework.beans.factory.InitializingBean

import java.awt.image.BufferedImage

import java.util.zip.ZipOutputStream
import java.util.zip.ZipEntry
import javax.imageio.ImageIO

import groovy.xml.StreamingMarkupBuilder

import org.ossim.omar.core.ImageGenerator
import org.ossim.omar.core.WMSRequest
import org.ossim.omar.ogc.OgcController
import org.ossim.omar.ogc.WmsCommand
import org.ossim.omar.security.SecUser

import org.ossim.omar.core.DateUtil

class WmsController extends OgcController implements InitializingBean
{
  def rasterEntrySearchService
  def rasterKmlService
  def webMappingService
  def wmsLogService
  def scratchDir
  def exportService
  def grailsApplication
  def drawService

  def wms()
  {
    //println params

    WmsCommand cmd = new WmsCommand()

    bindData( cmd, new CaseInsensitiveMap( params ) )


    if ( !cmd.validate() )
    {
      log.error( cmd.createErrorString() )
      //   println cmd.createErrorString()
      ogcExceptionService.writeResponse( response, ogcExceptionService.formatWmsException( cmd ) )
    }
    else
    {
      try
      {
        switch ( cmd?.request?.toLowerCase() )
        {
        case "getmap":
          forward( action: "getMap_", params: params )
          break
        case "getfeatureinfo":
          forward( action: "getFeatureInfo_", params: params )
          break
        case "getcapabilities":
          forward( action: "getCapabilities_", params: params )
          break
        case "getkmz":
          forward( action: "getKmz_", params: params )
          break
        case "getkml":
          forward( action: "getKml_", params: params )
          break
        default:
          log.error( "ERROR: Unknown action: ${ cmd?.request }" )
          break
        }
        /*
              println "*"*80
              request.getHeaderNames().each{name->
                println "${name} = ${request.getHeader(name)}"
              }
        */
        //endtime = System.currentTimeMillis()
        /*
              wmsLogParams.domain = authenticateService.userDomain()
        */
      }
      catch ( java.lang.Exception e )
      {
        log.error( "OGC::WMS exception: ${ e.message }" )
      }
    }

    return null
  }

  def footprints()
  {
//    def start = System.currentTimeMillis()

    if ( params.max == null )
    {
      params.max = grailsApplication.config.wms.vector.maxcount
    }
    def wmsRequest = new WMSRequest()

    def newParams = new CaseInsensitiveMap( params )

    bindData( wmsRequest, newParams )

    // default to geographic bounds
    if ( !wmsRequest.srs )
    {
      wmsRequest.srs = "EPSG:4326"
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

    if ( !startDate && !endDate )
    {
      startDate = DateUtil.initializeDate( "startDate", params )
      endDate = DateUtil.initializeDate( "endDate", params )
    }


    def bytes = drawService.drawLayers( wmsRequest, startDate, endDate, params )

    response.contentType = wmsRequest.format
    response.contentLength = bytes?.size()
    response.outputStream << bytes

    //    def stop = System.currentTimeMillis()
    //    println "${wmsRequest.bbox}: ${stop - start}ms"

    return null
  }

  def getKmz_()
  {

    WmsCommand cmd = new WmsCommand()

    bindData( cmd, new CaseInsensitiveMap( params ) )

    //cmd.clearErrors()  // because validation happens on entry so clear errors and re-bind

    if ( !cmd.validate( [
        'request',
        'layers',
        'format',
        'transparent',
        'bbox',
        'srs',
        'width',
        'height'
    ] ) )
    {
      cmd.errors.each { println it }
      log.error( cmd.createErrorString() )
      ogcExceptionService.writeResponse( response, ogcExceptionService.formatWmsException( cmd ) )
    }
    else
    {
      def bytes = createKMZ( cmd )
      response.setHeader( "Content-disposition", "attachment; filename=output.kmz" )
      response.contentType = "application/vnd.google-earth.kmz"
      response.contentLength = bytes.size()
      response.outputStream << bytes
    }

    null
  }

  def getCapabilities_()
  {

    WmsCommand cmd = new WmsCommand()

    //cmd.clearErrors()  // because validation happens on entry so clear errors and re-bind

    bindData( cmd, new CaseInsensitiveMap( params ) )

    if ( !cmd.validate( [/*'service', , 'version',*/ 'request'] ) )
    {
      cmd.errors.each { println it }
      log.error( cmd.createErrorString() )
      ogcExceptionService.writeResponse( response, ogcExceptionService.formatWmsException( cmd ) )
    }
    else
    {
      //wmsLogParams.request = "getcapabilities"
      def serviceAddress = createLink( controller: "ogc", action: "wms", base: "${ grailsApplication.config.omar.serverURL }", absolute: true ) as String
      def capabilities = webMappingService?.getCapabilities( cmd, serviceAddress )
      //internaltime = System.currentTimeMillis();
      render( contentType: "text/xml", text: capabilities )
    }
    null
  }

  def getKml_()
  {

    WmsCommand cmd = new WmsCommand()

    bindData( cmd, new CaseInsensitiveMap( params ) )

    //cmd.clearErrors()  // because validation happens on entry so clear errors and re-bind
    if ( !cmd.validate( [
//		'bands',
//		'bbox',
//		'brightness',
//		'contrast',
//		'height',
//		'interpolation',
        'layers',
//		'quicklook',
        'request' //,
//		'sharpen_mode',
//		'srs',
//		'stretch_mode',
//		'stretch_mode_region',
//		'width'
    ] ) )
    {
      //println "INVALID: ${params}"
      cmd.errors.each { println it }
      log.error( cmd.createErrorString() )
      ogcExceptionService.writeResponse( response, ogcExceptionService.formatWmsException( cmd ) )
    }
    else
    {
      def wmsParams = [:]
      //wmsLogParams.request = "getkml"

      // Convert param names to lower case
      params?.each { wmsParams?.put( it.key.toLowerCase(), it.value ) }

      def rasterEntries
      if ( cmd.layers )
      {
        def layerNames = cmd.layers?.split( "," ) as String[]
        rasterEntries = rasterEntrySearchService.getWmsImageLayers( layerNames )
      }
      else if ( cmd.filter )
      {
        rasterEntries = rasterEntrySearchService.getWmsImageLayers( cmd.filter )
      }

      //  def serviceAddress = createLink(controller: "ogc", action: "wms", base: "${grailsApplication.config.omar.serverURL}", absolute: true)
      //  def kml = webMappingService.getKML(wmsRequest, serviceAddress)

      def filename = "image.kml"

      def kml = null;
      if ( rasterEntries?.size > 0 )
      {
        def tempMap = new CaseInsensitiveMap( params )
        def file = ( rasterEntries[0].mainFile.name as File ).name

        filename = "${ file }.kml"
        kml = rasterKmlService.createImagesKml( rasterEntries, cmd.toMap(), tempMap )
      }
      else
      {
        kml = ""
        filename = "empty.kml"
      }
      //internaltime = System.currentTimeMillis();
      response.setHeader( "Content-disposition", "attachment; filename=${ filename }" )
      render( contentType: "application/vnd.google-earth.kml+xml", text: kml, encoding: "UTF-8" )
    }

    null
  }

  def getFeatureInfo_()
  {
    WmsCommand cmd = new WmsCommand()

    def paramsClone = new CaseInsensitiveMap( params )

    def format = paramsClone.query_format ?: "csv"
    def queryParams = new WMSQuery()
    paramsClone.max = cmd.feature_count ?: 1
    queryParams.layers = cmd.query_layers

    bindData( cmd, paramsClone )

    bindData( queryParams, paramsClone )
    if ( !queryParams.time )
    {
      if ( !queryParams.startDate )
      {
        queryParams.startDate = DateUtil.initializeDate( "startDate", paramsClone )
      }
      if ( !queryParams.endDate )
      {
        queryParams.endDate = DateUtil.initializeDate( "endDate", paramsClone )
      }
    }

    def objects = rasterEntrySearchService.runQuery( queryParams, paramsClone )
    def fields = grailsApplication.config.export.rasterEntry.fields.clone()
    def labels = grailsApplication.config.export.rasterEntry.labels.clone()
    def formatters = grailsApplication.config.export.rasterEntry.formatters
    fields << "groundGeom"
    labels << "groundGeom"
    def (file, mimeType) = exportService.export(
        format,
        objects,
        fields,
        labels,
        formatters,
        [featureClass: RasterEntry.class]
    )

    response.setHeader( "Content-disposition", "attachment; filename=${ file?.name }" );
    response.contentType = mimeType
    response.outputStream << file?.newInputStream()
    response.outputStream.flush()
  }

  def getMap_()
  {
    WmsCommand cmd = new WmsCommand()

    bindData( cmd, new CaseInsensitiveMap( params ) )

//	println cmd

//    cmd.clearErrors()  // because validation happens on entry so clear errors and re-bind

    if ( !cmd.validate() )// ['reqeust', 'layers', 'bbox', 'srs', 'width', 'height', 'format'] ) )
    {
      cmd.errors.each { println it }
      log.error( cmd.createErrorString() )
      ogcExceptionService.writeResponse( response, ogcExceptionService.formatWmsException( cmd ) )
    }
    else
    {
      def wmsLogParams = cmd.toMap()

      wmsLogParams.startDate = new Date()
      def starttime = System.currentTimeMillis()
      def internaltime = starttime
      def endtime = starttime

      //wmsLogParams.request = "getmap"
      switch ( cmd?.format?.toLowerCase() )
      {
      case "jpeg":
      case "jpg":
      case "image/jpeg":
      case "image/jpg":
        if ( cmd?.transparent?.equalsIgnoreCase( "true" ) )
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

      def layers = rasterEntrySearchService.findRasterEntries( cmd?.layers?.split( ',' ) )
      def mapResult = webMappingService.getMap( cmd, layers )

      internaltime = System.currentTimeMillis()

      if ( mapResult.errorMessage )
      {
        def message = "WMS server Error: ${ mapResult.errorMessage }"
        // no data to process
        log.error( message )

        def ogcFormattedException = ogcExceptionService.formatOgcException( cmd.toMap(), message )
        ogcExceptionService.writeResponse( response, ogcFormattedException )
      }
      else
      {
        try
        {
          def writerType = response.contentType?.split( "/" )[-1]
          def ostream = new ByteArrayOutputStream()

          ImageIO.write( mapResult.image, writerType, ostream )

          def bytes = ostream.toByteArray()

          response.contentLength = bytes.size()
          response.outputStream << bytes
        }
        catch ( Exception e )
        { }
      }
      endtime = System.currentTimeMillis()

      def principal = springSecurityService?.principal
      def hasUserInformation = !( springSecurityService?.principal instanceof String )
      def secUser = hasUserInformation ? SecUser.findByUsername( principal.username ) : null
      wmsLogParams.userName = secUser ? secUser.username : principal
      wmsLogParams.domain = ""
      def domain = null
      def clientIp = request.getHeader( 'Client-ip' )
      def XForwarded = request.getHeader( 'X-Forwarded-For' )
      wmsLogParams.ip = XForwarded
      if ( clientIp )
      {
        if ( wmsLogParams.ip )
        {
          wmsLogParams.ip += ", ${ clientIp }"
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

      def urlTemp = createLink( [controller: 'ogc', action: 'wms', base: "${ grailsApplication.config.omar.serverURL }", absolute: true, params: params] )
      wmsLogParams.with {
        endDate = new Date()
        internalTime = ( internaltime - starttime ) / 1000.0
        renderTime = ( endtime - internaltime ) / 1000.0
        totalTime = ( endtime - starttime ) / 1000.0
        url = urlTemp
      }

      wmsLogService.logParams( wmsLogParams )

    }

    return null
  }


  private def createKMZ(def cmd)
  {
    def kmlbuilder = new StreamingMarkupBuilder()
    kmlbuilder.encoding = "UTF-8"

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
    def wmsQuery = webMappingService.setupQuery( cmd );
    def rasterEntryList = rasterEntrySearchService.findRasterEntries( cmd?.layers?.split( ',' ) )

    def image = webMappingService.getMap( cmd, rasterEntryList ).image
    def tempDescription = rasterEntryList ? rasterKmlService.createImageKmlDescription( rasterEntryList[0] ) : "No images found for the kmz query"
    if ( image && ( rasterEntryList.size() > 0 ) )
    {
      def nameString = rasterEntryList[0].title
      nameString = nameString ?: rasterEntryList[0].indexId
      def bounds = cmd.bounds
      def kmlnode = {
        mkp.xmlDeclaration()
        kml( "xmlns": "http://earth.google.com/kml/2.1" ) {
          Document() {
            GroundOverlay() {
              name( "${ nameString }" )
              Snippet()
              description { mkp.yieldUnescaped( "<![CDATA[${ tempDescription }]]>" ) }
              open( "1" )
              visibility( "1" )
              Icon() {
                href { mkp.yieldUnescaped( "images/image${ ext }" ) }
              }
              LatLonBox() {
                north( bounds.maxy )
                south( bounds.miny )
                east( bounds.maxx )
                west( bounds.minx )
              }
            }
          }
        }
      }

      def ostream = new ByteArrayOutputStream()
      def zos = new ZipOutputStream( ostream )

      //create a new zip entry
      def anEntry = null

      anEntry = new ZipEntry( "doc.kml" );
      //place the zip entry in the ZipOutputStream object
      zos.putNextEntry( anEntry );

      zos << kmlbuilder.bind( kmlnode ).toString()
      anEntry = new ZipEntry( "images/image${ ext }" );
      //place the zip entry in the ZipOutputStream object
      zos.putNextEntry( anEntry );
      if ( image )
      {
        def imstream = new ByteArrayOutputStream()
        ImageIO.write( image, format.split( "/" )[-1], imstream );
        zos << imstream.toByteArray()
      }

      zos.close();
      return ostream.toByteArray()
    }
    else
    {
//        render(contentType: "text/plain", text: "Unable to chip image for KMZ given parameters ${params}".toBytes())
      "ERROR: Shouldn't see this"
    }
  }


  public void afterPropertiesSet()
  {
    scratchDir = grailsApplication.config.export.workDir ?: "/tmp";
  }
}
