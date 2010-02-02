import java.awt.image.RenderedImage

import java.awt.image.BufferedImage
import java.awt.image.ColorModel
import java.awt.image.WritableRaster
import java.awt.image.DataBuffer
import java.awt.image.DataBufferByte
import java.awt.Point
import java.awt.Rectangle
import java.awt.image.ComponentColorModel
import java.awt.color.ColorSpace
import java.awt.Transparency

import java.awt.image.ImageFilter
import java.awt.image.FilteredImageSource
import java.awt.Toolkit

import joms.oms.WmsMap
import joms.oms.ossimKeywordlist
import joms.oms.ossimKeywordlistVector
import joms.oms.Util
import joms.oms.ossimImageGeometryPtr
import joms.oms.ossimImageGeometry
import joms.oms.ossimGpt
import joms.oms.ossimDpt
import joms.oms.ossimGptVector
import joms.oms.ossimDptVector
import org.ossim.oms.image.omsImageSource
import javax.imageio.ImageIO

class WebMappingService
{

  public static final String SYSCALL = "syscall"
  public static final String LIBCALL = "libcall"
  public static final String BLANK = "blank"
  def mode = LIBCALL

  boolean transactional = true
  def transparent = new TransparentFilter()

  def createBilinearModel(RasterEntry rasterEntry)
  {
    def gptArray = new ossimGptVector();
    def dptArray = new ossimDptVector();
    def groundGeom = rasterEntry.groundGeom.geom
    if(groundGeom.numPoints() >=4)
    {
      def w =   rasterEntry?.width as double
      def h =   rasterEntry?.height as double
       (0..<4).each{
          def point = groundGeom.getPoint(it);
          gptArray.add(new ossimGpt(point.y, point.x));
       }
       dptArray.add(new ossimDpt(0.0,0.0))
       dptArray.add(new ossimDpt( w,0.0))
       dptArray.add(new ossimDpt(w ,h))
       dptArray.add(new ossimDpt(0.0,h))
    }
    return Util.createBilinearModel(dptArray, gptArray)
  }
  RenderedImage getMap(WMSRequest wmsRequest)
  {
    RenderedImage image = null
    def enableOMS = true
    def quickLookFlagString = wmsRequest?.quicklook_flag?:"true"
    def sharpenMode = wmsRequest?.sharpen_mode ?: ""
    def sharpenWidth = wmsRequest?.sharpen_width ?: ""
    def sharpenSigma = wmsRequest?.sharpen_sigma ?: ""
    def stretchMode = wmsRequest?.stretch_mode ?: "linear_auto_min_max"
    def stretchModeRegion = wmsRequest?.stretch_mode_region ?: "global"
    def entryId = 0
    def bounds = wmsRequest?.bbox?.split(',')
    switch ( mode )
    {

      case SYSCALL:
        def inputFilenames = []

        wmsRequest?.layers.split(',').each {
          def rasterEntry = RasterEntry.get(it)
          inputFilenames << "${rasterEntry?.mainFile.name}|${rasterEntry.entryId}"
        }

        def ext

        switch ( wmsRequest?.format?.toLowerCase() )
        {
          case "jpeg":
          case "image/jpeg":
            ext = ".jpg"
            break
          case "png":
          case "image/png":
            ext = ".png"
            break
          case "gif":
          case "image/gif":
            ext = ".gif"
            break
        }

        def imageFile = File.createTempFile("ogcoms", ext);
        def cmd = "orthoigen --geo --cut-bbox-ll ${bounds[1]} ${bounds[0]} ${bounds[3]} ${bounds[2]} -t ${wmsRequest?.width} --resample-type bilinear --scale-to-8-bit --hist-auto-minmax --enable-entry-decoding ${inputFilenames.join(' ')} --writer-prop 'create_external_geometry=false' ${imageFile}"

        log.info(cmd.replace("|", "\\|"))

        def process = cmd.execute()

        process.consumeProcessOutput()
        process.waitFor();
        image = ImageIO.read(imageFile)
        imageFile.delete()
        // delete the geom file
        new File(imageFile.absolutePath - ext + ".geom").delete()


        break

      case BLANK:

        image = new BufferedImage(
            wmsRequest?.width?.toInteger(),
            wmsRequest?.height?.toInteger(),
            BufferedImage.TYPE_INT_RGB
        )

        break

      case LIBCALL:

        /*
        * BIL: pixelStride = 1, lineStride = 3*width, bandOffsets = {0, width, 2*width}
        * BSQ: pixelStride = 1, lineStride = width, bandOffsets = {0, width*height, 2*width*height}
        * BIP: pixelStride = 3, lineStride = 3*width, bandOffsets = {0, 1, 2}
        */
        def WmsMap wmsMap = new WmsMap();

        int width = wmsRequest?.width?.toInteger()
        int height = wmsRequest?.height?.toInteger()
        int pixelStride = 3
        int lineStride = 3 * width
        int[] bandOffsets = [0, 1, 2] as int[]
        Point location = null
        byte[] data = new byte[width * height * 3]

//        def kwlVector = new ossimKeywordlistVector();
        if ( sharpenMode.equals("light") )
        {
          sharpenWidth = "3"
          sharpenSigma = ".5"
        }
        else if ( sharpenMode.equals("heavy") )
        {
          sharpenWidth = "5"
          sharpenSigma = "1"
        }
        def quickLookFlag = Boolean.valueOf(quickLookFlagString)
        wmsRequest?.layers.split(',').each {
          def geom = (ossimImageGeometry)null
          def geomPtr = (ossimImageGeometryPtr)null
          def rasterEntry = RasterEntry.get(it)
          if(rasterEntry != null)
          {
            if(quickLookFlag)
            {
              geomPtr = createBilinearModel(rasterEntry)
              if(geomPtr != null)
              {
                geom = geomPtr.get()
              }
            }
            wmsMap.addFile(rasterEntry?.mainFile.name,
                           rasterEntry?.entryId?.toInteger(),
                           geom)
            geom = (ossimImageGeometry)null
            geomPtr = (ossimImageGeometryPtr)null
          }
        }
         if ( enableOMS )
        {
            def kwl = new ossimKeywordlist();
            kwl.add("stretch_mode", "${stretchMode}")
            kwl.add("stretch_region", "${stretchModeRegion}")
            kwl.add("sharpen_width", "${sharpenWidth}")
            kwl.add("sharpen_sigma", "${sharpenSigma}")
            kwl.add("null_flip", wmsRequest?.null_flip)
            wmsMap.setChainParameters(kwl)
            wmsMap.getMap(
                wmsRequest.srs,
                bounds[0] as Double, bounds[1] as Double, bounds[2] as Double, bounds[3] as Double,
                Integer.parseInt(wmsRequest.width), Integer.parseInt(wmsRequest.height),
                data
            )
            
          wmsMap.cleanUp();
          wmsMap = null;

        }
        else
        {
          new java.util.Random().nextBytes(data)
        }

        DataBuffer dataBuffer = new DataBufferByte(data, data.size())
        WritableRaster raster = WritableRaster.createInterleavedRaster(
            dataBuffer,
            width,
            height,
            lineStride,
            pixelStride,
            bandOffsets,
            location)

        ColorModel colorModel = omsImageSource.createColorModel(raster.sampleModel)

        boolean isRasterPremultiplied = true
        Hashtable<?, ?> properties = null

        image = new BufferedImage(
            colorModel,
            raster,
            isRasterPremultiplied,
            properties
        )

        break
    }

    if ( image && wmsRequest?.transparent?.equalsIgnoreCase("true") )
    {
      image = TransparentFilter.fixTransparency(transparent, image)
    }


    return image;
  }

  String getCapabilities(WMSRequest wmsRequest, String serviceAddress)
  {
    def layers = wmsRequest?.layers?.split(',')
    def wmsCapabilites = new WMSCapabilities(layers, serviceAddress)

    return wmsCapabilites.getCapabilities()
  }

  String getKML(WMSRequest wmsRequest, String serviceAddress)
  {
    def layers = wmsRequest?.layers?.split(',')
    def wmsCapabilities = new WMSCapabilities(layers, serviceAddress)

    return wmsCapabilities.getKML()
  }

  BufferedImage getUnprojectedTile(
  Rectangle rect,
  String inputFile,
  int entry,
  String stretchMode,
  String viewportStretchMode,
  BigDecimal scale,
  int startSample, int endSample, int startLine, int endLine
  )
  {

//    println rect
//    println "${inputFile} ${entry}"
//    println stretchMode
//    println viewportStretchMode
//    println scale
//    println "${startSample} ${endSample} ${startLine} ${endLine}"

    byte[] data = new byte[rect.width * rect.height * 3]

    WmsMap.getUnprojectedMap(
        inputFile, entry,
        "",
        stretchMode,
        viewportStretchMode,
        "",
        scale,
        startSample, endSample, startLine, endLine,
        data
    )
    DataBuffer dataBuffer = new DataBufferByte(data, data.size())
    int pixelStride = 3
    int lineStride = 3 * rect.width
    int[] bandOffsets = [0, 1, 2] as int[]
    Point location = null
    WritableRaster raster = WritableRaster.createInterleavedRaster(
        dataBuffer,
        rect.width as Integer,
        rect.height as Integer,
        lineStride,
        pixelStride,
        bandOffsets,
        location)

    ColorModel colorModel = omsImageSource.createColorModel(raster.sampleModel)

    boolean isRasterPremultiplied = true
    Hashtable<?, ?> properties = null

    BufferedImage image = new BufferedImage(
        colorModel,
        raster,
        isRasterPremultiplied,
        properties
    )

    return image
  }
}



