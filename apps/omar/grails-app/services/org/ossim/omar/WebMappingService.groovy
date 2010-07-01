package org.ossim.omar

import java.awt.image.RenderedImage

import java.awt.image.*;
import java.awt.*;
import joms.oms.WmsView
import joms.oms.ossimDpt
import joms.oms.ossimGpt
import joms.oms.WmsMap
import joms.oms.ossimKeywordlist

import joms.oms.ossimImageGeometryPtr
import joms.oms.ossimImageGeometry
import joms.oms.ossimUnitConversionTool

import org.ossim.oms.image.omsImageSource
import javax.imageio.ImageIO
import org.ossim.omar.RasterEntry
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
import javax.imageio.ImageTypeSpecifier
import java.awt.image.SampleModel
import java.awt.image.IndexColorModel

import java.awt.image.ImageFilter
import java.awt.image.FilteredImageSource
import java.awt.Toolkit

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

  static def getWmsImageLayers(def params)
  {
    def names = []
    params?.layers.split(',').each {
      names.add(it)
    }
    RasterEntryQuery rasterQuery = null
    if ( params.bbox )
    {
      rasterQuery = new RasterEntryQuery()
      def bounds = params.bbox.split(',')
      rasterQuery.aoiMinLon = bounds[0]
      rasterQuery.aoiMinLat = bounds[1]
      rasterQuery.aoiMaxLon = bounds[2]
      rasterQuery.aoiMaxLat = bounds[3]
    }

    return RasterEntry.createCriteria().list() {
      or {
        names.each() {name ->
          try
          {
            eq('id', java.lang.Long.valueOf(name))
          }
          catch (java.lang.Exception e)
          {
            eq('title', name)
            eq('imageId', name)
          }
        }
      }
      if ( rasterQuery )
      {
        addToCriteria(rasterQuery.createIntersection())
      }
    }
  }

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
    def w = wmsRequest.width as int
    def h = wmsRequest.height as int

    def wmsView = new WmsView()
    def projPoint = new ossimGpt(maxy, minx)
    def origin = new ossimDpt(0.0, 0.0)
    def ls = new ossimDpt(0.0, 0.0)
    wmsView.setProjection(wmsRequest.srs)
    wmsView.setViewDimensionsAndImageSize(minx, miny, maxx, maxy, w, h)
    def proj = wmsView.getProjection()
    proj.worldToLineSample(projPoint, origin)

    def style = (HashMap) grailsApplication.config.wms.styles.get(styleName)
    if ( !style )
    {
      style = (HashMap) grailsApplication.config.wms.styles.get("default")
    }
    if ( style )
    {
      g.setPaint(new Color(style.outlinecolor.r * 255 as int,
              style.outlinecolor.g * 255 as int,
              style.outlinecolor.b * 255 as int,
              style.outlinecolor.a * 255 as int))
      g.setStroke(new BasicStroke(style.width as int));
    }
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

    geometries.each {geom ->
      //def pointListx = new int[geom.numPoints()]
      //def pointListy = new int[geom.numPoints()]

      def coordinates = geom.coordinates
      def pointListx = new int[coordinates.size()]
      def pointListy = new int[coordinates.size()]

      //def numPoints = geom.numPoints()
      def numPoints = coordinates.size()
      (0..<numPoints).each {
        //def point = geom.getPoint(it);
        def point = coordinates[it];
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
    def terrainCorrectionFlagString = wmsRequest?.terrain_correction ?: "false"
    def sharpenMode = wmsRequest?.sharpen_mode ?: ""
    def sharpenWidth = wmsRequest?.sharpen_width ?: ""
    def sharpenSigma = wmsRequest?.sharpen_sigma ?: ""
    def stretchMode = wmsRequest?.stretch_mode ?: "linear_auto_min_max"
    def stretchModeRegion = wmsRequest?.stretch_mode_region ?: "global"
    def bands = wmsRequest?.bands ?: ""
    def entryId = 0
    def bounds = wmsRequest?.bbox?.split(',')
    int viewableBandCount = 1
    Point location = null
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
      def terrainCorrectionFlag = Boolean.valueOf(terrainCorrectionFlagString)
      def rasterEntries = getWmsImageLayers(wmsRequest)
      rasterEntries.each { rasterEntry ->
        def geom = (ossimImageGeometry) null
        def geomPtr = (ossimImageGeometryPtr) null
        //def rasterEntry = RasterEntry.get(it)
        if ( rasterEntry != null )
        {
          rasterEntry.adjustAccessTimeIfNeeded(24) // adjust every 24 hours
          def file = new File(rasterEntry?.mainFile.name)

          if ( !file.exists() )
          {
          }
          else if ( !file.canRead() )
          {
          }
          else
          {
            double scaleCheck = 1.0
            if ( !terrainCorrectionFlag )
            {
              geomPtr = rasterEntry.createModelFromTiePointSet();
              if ( geomPtr != null )
              {
                geom = geomPtr.get()
              }
            }
            // we will use a crude bilinear to test scale change to
            // verify we have enough overviews to reproject the image
            if ( geomPtr != null )
            {
              scaleCheck = view.getScaleChangeFromInputToView(geomPtr.get())
            }
            if ( rasterEntry?.numberOfBands > viewableBandCount )
            {
              viewableBandCount = 3
            }
            // if we are near zooming to full res just add the image
            if ( scaleCheck >= 0.9 )
            {
              wmsMap.addFile(rasterEntry?.mainFile.name,
                      rasterEntry?.entryId?.toInteger(),
                      geom)
            }
            // make sure we are within resolution level before adding an image
            else if ( scaleCheck > 0.0 )
            {
              // check to see if the decimation puts us smaller than the bounding rect of the smallest
              // res level scale
              //
              long maxSize = (rasterEntry.width > rasterEntry.height) ? rasterEntry.width : rasterEntry.height
              if ( (maxSize * scaleCheck) >= (maxSize / (2 ** rasterEntry.numberOfResLevels)) )
              {
                wmsMap.addFile(rasterEntry?.mainFile.name,
                        rasterEntry?.entryId?.toInteger(),
                        geom)
              }
            }
            geom = (ossimImageGeometry) null
            geomPtr = (ossimImageGeometryPtr) null
          }
        }
      }
      int pixelStride = viewableBandCount
      int lineStride = viewableBandCount * width
      int[] bandOffsets = null;
      if ( viewableBandCount == 1 ) bandOffsets = [0] as int[]
      else bandOffsets = [0, 1, 2] as int[]
      byte[] data = new byte[width * height * viewableBandCount]
      if ( enableOMS )
      {
        //println bounds
        def kwl = new ossimKeywordlist();
        kwl.add("viewable_bands", "${viewableBandCount}")
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


      try
      {
        if ( viewableBandCount == 1 )
        {
          ImageTypeSpecifier isp = ImageTypeSpecifier.createGrayscale(8, DataBuffer.TYPE_BYTE, false);
          ColorModel colorModel
          SampleModel sampleModel = isp.getSampleModel(width as Integer, height as Integer)
          if ( !wmsRequest?.transparent?.equalsIgnoreCase("true") )
          {
            colorModel = isp.getColorModel();
          }
          else
          {
            int[] lut = new int[256]
            (0..<lut.length).each {i ->
              lut[i] = ((0xff << 24) | (i << 16) | (i << 8) | (i));
            }
            lut[0] = 0xff000000
            colorModel = new IndexColorModel(8, lut.length, lut, 0, true, 0, DataBuffer.TYPE_BYTE)
          }
          WritableRaster raster = WritableRaster.createWritableRaster(sampleModel, dataBuffer, null)
          image = new BufferedImage(colorModel, raster, false, null);
        }
        else
        {
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
        }
      }
      catch (Exception e)
      {
        e.printStackTrace()
      }
      break
    }

    if ( image && wmsRequest?.transparent?.equalsIgnoreCase("true") && (viewableBandCount == 3) )
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

  def computeScales(def rasterEntries)
  {
    def unitConversion = new ossimUnitConversionTool(1.0)
    def fullResScale = 0.0 // default to 1 unit per pixel
//    def minResLevels  = 0 // default to 1 unit per pixel
    def smallestScale = 0.0
    def largestScale = 0.0
    def testScale = 0.0

    rasterEntries.each {rasterEntry ->
      if ( rasterEntry.gsdY )
      {
        unitConversion.setValue(rasterEntry.gsdY);
        def testValue = unitConversion.getDegrees();
        if ( (fullResScale == 0.0) || (testValue < fullResScale) )
        {
          fullResScale = testValue
        }
        if ( smallestScale == 0.0 )
        {
          smallestScale = fullResScale
          largestScale = fullResScale
        }
      }
      if ( rasterEntry.numberOfResLevels )
      {
        testScale = 2 ** rasterEntry.numberOfResLevels * fullResScale;
        if ( testScale > largestScale )
        {
          largestScale = testScale
        }
      }
      // now allow at least 8x zoom in
      testScale = 0.125 * fullResScale
      if ( testScale < smallestScale )
      {
        smallestScale = testScale;
      }
    }

    return [fullResScale: fullResScale, smallestScale: smallestScale, largestScale: largestScale]
  }

  def computeBounds(def rasterEntries)
  {
    def left = null
    def right = null
    def top = null
    def bottom = null

    rasterEntries.each { rasterEntry ->
      def bounds = rasterEntry?.groundGeom?.bounds


      if ( left == null || bounds?.minLon < left )
      {
        left = bounds?.minLon
      }

      if ( bottom == null || bounds?.minLat < bottom )
      {
        bottom = bounds?.minLat
      }

      if ( right == null || bounds?.maxLon > right )
      {
        right = bounds?.maxLon
      }

      if ( top == null || bounds?.maxLat > top )
      {
        top = bounds?.maxLat
      }
    }

    return [left: left, right: right, top: top, bottom: bottom]
  }
}



