import java.awt.image.RenderedImage

import java.awt.image.*;
import java.awt.*;
import joms.oms.WmsView
import joms.oms.ossimDpt
import joms.oms.ossimGpt
import joms.oms.WmsMap
import joms.oms.ossimKeywordlist
import joms.oms.Util
import joms.oms.ossimImageGeometryPtr
import joms.oms.ossimImageGeometry
import joms.oms.ossimGptVector
import joms.oms.ossimDptVector
import org.ossim.oms.image.omsImageSource
import javax.imageio.ImageIO

class WebMappingService
{
  def grailsApplication
  def rasterEntrySearchService

  public static final String SYSCALL = "syscall"
  public static final String LIBCALL = "libcall"
  public static final String BLANK = "blank"
  def mode = LIBCALL

  boolean transactional = true
  def transparent = new TransparentFilter()
/*
  def createBilinearModel(RasterEntry rasterEntry)
  {
    def gptArray = new ossimGptVector();
    def dptArray = new ossimDptVector();
    def groundGeom = rasterEntry?.metadata?.groundGeom?.geom

    if(rasterEntry.tiePointSet)
    {

    }
    else
    {
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
    }
    return Util.createBilinearModel(dptArray, gptArray)
  }
  */
  void drawCoverage(Graphics2D g, WMSRequest wmsRequest, def geometries, def styleName)
  {
    def minx = -180.0
    def maxx = 180.0
    def miny = -90.0
    def maxy = 90.0
    if ( wmsRequest.bbox )
    {
      def bounds = wmsRequest.bbox.split(',')
      minx = bounds[0] as double
      miny = bounds[1] as double
      maxx = bounds[2] as double
      maxy = bounds[3] as double
    }
    def w  = wmsRequest.width as int
    def h  = wmsRequest.height as int
    
    def wmsView = new WmsView()
    def projPoint = new ossimGpt(maxy, minx)
    def origin = new ossimDpt(0.0, 0.0)
    def ls = new ossimDpt(0.0, 0.0)
    wmsView.setProjection(wmsRequest.srs)
    wmsView.setViewDimensionsAndImageSize(minx, miny, maxx, maxy, w, h)
    def proj    = wmsView.getProjection()
    proj.worldToLineSample(projPoint, origin)

    def style = (HashMap)grailsApplication.config.wms.styles.get(styleName)
    if(!style)
    {
      style = (HashMap)grailsApplication.config.wms.styles.get("default")
    }
    if(style)
    {
      g.setPaint(new Color(style.outlinecolor.r*255 as int,
                           style.outlinecolor.g*255 as int,
                           style.outlinecolor.b*255 as int,
                           style.outlinecolor.a*255 as int))
      g.setStroke(new BasicStroke(style.width as int));
    }
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                       RenderingHints.VALUE_ANTIALIAS_ON);
    geometries.each {geom ->
      def pointListx = new int[geom.numPoints()]
      def pointListy = new int[geom.numPoints()]

      def numPoints = geom.numPoints()
      (0..<numPoints).each{
        def point = geom.getPoint(it);
        projPoint.setLatd(point.y);
        projPoint.setLond(point.x);
        proj.worldToLineSample(projPoint, ls);
        ls.x -= origin.x;
        ls.y -= origin.y;
        pointListx[it] = (ls.x as int)
        pointListy[it] = (ls.y as int)
      }
      g.drawPolyline(pointListx, pointListy, pointListx.size())
    }
  }
  
  RenderedImage getMap(WMSRequest wmsRequest)
  {
    RenderedImage image = null
    def enableOMS = true
    def quickLookFlagString = wmsRequest?.quicklook?:"true"
    def sharpenMode = wmsRequest?.sharpen_mode ?: ""
    def sharpenWidth = wmsRequest?.sharpen_width ?: ""
    def sharpenSigma = wmsRequest?.sharpen_sigma ?: ""
    def stretchMode = wmsRequest?.stretch_mode ?: "linear_auto_min_max"
    def stretchModeRegion = wmsRequest?.stretch_mode_region ?: "global"
    def bands = wmsRequest?.bands?:""
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

        WmsView view = new WmsView()
        view.setProjection(wmsRequest.srs)
        view.setViewDimensionsAndImageSize(bounds[0] as Double,
                                           bounds[1] as Double,
                                           bounds[2] as Double,
                                           bounds[3] as Double,
                                           Integer.parseInt(wmsRequest.width),
                                           Integer.parseInt(wmsRequest.height))
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
            def file = new File(rasterEntry?.mainFile.name)

            if(!file.exists())
            {

            }
            else if(!file.canRead())
            {
            }
            else
            {
              double scaleCheck = 1.0
               if(quickLookFlag)
               {
                 geomPtr = rasterEntry.createModelFromTiePointSet();
                 if(geomPtr != null)
                 {
                   geom = geomPtr.get()
                 }
               }
               // we will use a crude bilinear to test scale change to
               // verify we have enough overviews to reproject the image
               if(geomPtr != null)
               {
                 scaleCheck = view.getScaleChangeFromInputToView(geomPtr.get())
               }
               // if we are near zooming to full res just add the image
               if(scaleCheck >= 0.9)
               {
                 wmsMap.addFile(rasterEntry?.mainFile.name,
                                rasterEntry?.entryId?.toInteger(),
                                geom)
               }
               // make sure we are within resolution level before adding an image
               else if(scaleCheck > 0.0)
               {
                 // check to see if the decimation puts us smaller than the bounding rect of the smallest
                 // res level scale
                 //
                 long maxSize = (rasterEntry.width > rasterEntry.height)?rasterEntry.width:rasterEntry.height
                 if((maxSize*scaleCheck) >= (maxSize/(2**rasterEntry.numberOfResLevels)))
                 {
                   wmsMap.addFile(rasterEntry?.mainFile.name,
                                  rasterEntry?.entryId?.toInteger(),
                                  geom)
                 }
               }
               geom = (ossimImageGeometry)null
               geomPtr = (ossimImageGeometryPtr)null
            }
          }
        }
        if ( enableOMS )
        {
          //println bounds
            def kwl = new ossimKeywordlist();
            kwl.add("stretch_mode", "${stretchMode}")
            kwl.add("stretch_region", "${stretchModeRegion}")
            kwl.add("sharpen_width", "${sharpenWidth}")
            kwl.add("sharpen_sigma", "${sharpenSigma}")
            kwl.add("bands", "${bands}")
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



