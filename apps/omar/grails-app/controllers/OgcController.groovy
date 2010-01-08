import javax.imageio.ImageIO

import java.awt.Point
import java.awt.Rectangle
import java.awt.image.BufferedImage
import java.awt.image.AffineTransformOp
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D

import java.awt.geom.AffineTransform

import javax.media.jai.JAI

import java.awt.image.ColorModel
import java.awt.image.DataBuffer
import java.awt.image.DataBufferByte
import java.awt.image.WritableRaster

import org.grails.plugins.springsecurity.service.AuthenticateService

class OgcController
{
  def webMappingService
  def grailsApplication
  AuthenticateService authenticateService
  KmlService kmlService

  def wms = {

    def tempMap = [:]

    // Convert param names to lower case
    params.each { tempMap.put(it.key.toLowerCase(), it.value)}

    // Populate WMSCapabilities Request object
    def wmsRequest = new WMSRequest()
    def debugFlag = false;
    if ( tempMap.containsKey("debug") )
    {
      debugFlag = tempMap.debug.toString().equals("true");
    }

    bindData(wmsRequest, tempMap)

    if ( debugFlag )
    {
      println "Starting ${wmsRequest.layers}"
    }


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
    catch (java.lang.Exception e)
    {
      // println "OGC::WMS Error: ${e.message}"
    }
    if ( debugFlag )
    {
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
