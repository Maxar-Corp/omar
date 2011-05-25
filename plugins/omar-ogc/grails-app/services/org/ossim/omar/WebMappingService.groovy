package org.ossim.omar

import java.awt.image.RenderedImage
import java.awt.Graphics2D
import java.awt.Color
import java.awt.BasicStroke
import java.awt.Point
import java.awt.Composite
import java.awt.Polygon
import java.awt.RenderingHints
import java.awt.image.*;
import java.awt.geom.AffineTransform
import joms.oms.WmsView
import joms.oms.ossimDpt
import joms.oms.ossimGpt
import joms.oms.WmsMap
import joms.oms.ossimKeywordlist
import joms.oms.ossimGptVector
import joms.oms.ossimDptVector
import joms.oms.Util
import joms.oms.ossimImageSource

import joms.oms.ossimImageGeometryPtr
import joms.oms.ossimImageGeometry
import joms.oms.ossimUnitConversionTool

import org.ossim.oms.image.omsImageSource
import javax.imageio.ImageIO
import org.geotools.geometry.jts.LiteShape
import geoscript.geom.MultiPolygon
import java.awt.Rectangle
import joms.oms.Chain


class WebMappingService
{
  def grailsApplication
  def rasterEntrySearchService
  def videoDataSetSearchService
  def rasterChainService
  

  public static final String SYSCALL = "syscall"
  public static final String LIBCALL = "libcall"
  public static final String BLANK = "blank"
  def mode = LIBCALL

  static transactional = true

  def transparent = new TransparentFilter()

  void drawCoverage(Graphics2D g, def wmsRequest, def geometries, def styleName)
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

    def style = grailsApplication.config.wms.styles.get(styleName)

    if ( !style )
    {
      style = grailsApplication.config.wms.styles.get("default")
    }

    if ( style )
    {
      g.setPaint(new Color(style.outlinecolor.r * 255 as int,
              style.outlinecolor.g * 255 as int,
              style.outlinecolor.b * 255 as int,
              style.outlinecolor.a * 255 as int))

      g.setStroke(new BasicStroke(style.width as int))
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
  WMSQuery setupQuery(def wmsRequest)
  {
      def wmsQuery  = new WMSQuery()
      def params    = wmsRequest.toMap();
      wmsQuery.caseInsensitiveBind(wmsRequest.toMap())
      def max = params.max?params.max as Integer:10
      if(max > 10) max = 10
      wmsQuery.max = max
      if(wmsQuery.layers?.toLowerCase() == "raster_entry")
      {
        wmsQuery.layers = null
      }
      // for now we will sort by the date field if no layers are given
      //
      if(!wmsQuery.layers)
      {
          wmsQuery.sort  = wmsQuery.sort?:"acquisitionDate"
          wmsQuery.order = wmsQuery.order?:"desc"
      }

      wmsQuery
  }
  def getMap(def wmsRequest, def layers=null)
  {
    def result = [image:null,errorMessage:null]
    def params    = wmsRequest.toMap();
    def bounds = wmsRequest.bounds//wmsRequest?.bbox?.split(',')
    def maxBands = 1
    def wmsQuery = layers?null:setupQuery(wmsRequest);
    def stretchMode       = wmsRequest?.stretch_mode ? wmsRequest?.stretch_mode.toLowerCase(): null
    def stretchModeRegion = wmsRequest?.stretch_mode_region ?:null
    def wmsView = new WmsView()
	def srs = wmsRequest?.srs
    if(!wmsView.setProjection(srs))
    {
        result.errorMessage = "Unsupported projection ${srs}"
        log.error(result)
        return result
    }
    if(!wmsView.setViewDimensionsAndImageSize(bounds.minx,
              bounds.miny,
              bounds.maxx,
              bounds.maxy,
              bounds.width,
              bounds.height))
    {
        result.errorMessage = "Unable to set the dimensions for the view bounds"
        log.error(result)
        return result
    }
	def rasterEntries = layers?:wmsQuery.getRasterEntriesAsList();
    //params.viewGeom = wmsView.getImageGeometry();
	params.wmsView  = wmsView
	params.keepWithinScales = true
    def kwlString = ""
    if(rasterEntries)
    {
        rasterEntries = rasterEntries?.reverse()
        def srcChains    = []
        rasterEntries.each{rasterEntry->
			def chainMap = rasterChainService.createRasterEntryChain(rasterEntry, params)
			//chain.print()
            if(chainMap&&chainMap.chain&&(chainMap.chain.getChain()!=null))
            {
 			   def outputBands = chainMap.chain?.getChainAsImageSource()?.getNumberOfOutputBands()
			   if(outputBands > maxBands) maxBands = outputBands
               srcChains.add(chainMap)
            }
			chainMap = null
        }
 		if(srcChains)
		{
            def connectionId = 10000
			kwlString = "type:ossimImageChain\n"
	        def objectPrefixIdx = 0
			if(srcChains.size() > 1)
			{
		        // now establish mosaic and cut to match the output dimensions
		        kwlString += "object${objectPrefixIdx}.type:ossimImageMosaic\n"
				++objectPrefixIdx
			}
			def imageRect = wmsView.getViewImageRect()
			def midPoint  = imageRect.midPoint()
			def x         = (int)(midPoint.x+0.5)
			def y         = (int)(midPoint.y+0.5)
			x            -= (bounds.width*0.5);
			y            -= (bounds.height*0.5);
			def w         = bounds.width
			def h         = bounds.height
			imageRect = null
			midPoint  = null

			// for now scale all WMS requests to 8-bit
            kwlString += "object${objectPrefixIdx}.type:ossimScalarRemapper\n"
            kwlString += "object${objectPrefixIdx}.id:${connectionId}\n"
            ++connectionId
            ++objectPrefixIdx
			// and make it either 1 band or 3 band output
			//
			if(maxBands == 2)
			{
                kwlString += "object${objectPrefixIdx}.type:ossimBandSelector\n"
                kwlString += "object${objectPrefixIdx}.bands:(0)\n"
                kwlString += "object${objectPrefixIdx}.id:${connectionId}\n"
                ++connectionId
                ++objectPrefixIdx
			}
			else if(maxBands > 3)
			{
                kwlString += "object${objectPrefixIdx}.type:ossimBandSelector\n"
                kwlString += "object${objectPrefixIdx}.bands:(0,1,2)\n"
                kwlString += "object${objectPrefixIdx}.id:${connectionId}\n"
                ++connectionId
                ++objectPrefixIdx
 			}
	        kwlString += "object${objectPrefixIdx}.type:ossimRectangleCutFilter\n"
	        kwlString += "object${objectPrefixIdx}.rect:(${x},${y},${w},${h},lh)\n"
	        kwlString += "object${objectPrefixIdx}.cut_type:null_outside\n"
	        kwlString += "object${objectPrefixIdx}.id:${connectionId}\n"
		    ++objectPrefixIdx
	        if((stretchModeRegion == "viewport")&&
               (stretchMode!="none"))
	        {
	            kwlString += "object${objectPrefixIdx}.type:ossimImageHistogramSource\n"
	            kwlString += "object${objectPrefixIdx}.id:${connectionId+1}\n"
	            ++objectPrefixIdx
	            kwlString += "object${objectPrefixIdx}.type:ossimHistogramRemapper\n"
	            kwlString += "object${objectPrefixIdx}.id:${connectionId+2}\n"
	            kwlString += "object${objectPrefixIdx}.stretch_mode:${stretchMode}\n"
	            kwlString += "object${objectPrefixIdx}.input_connection1:${connectionId}\n"
	            kwlString += "object${objectPrefixIdx}.input_connection2:${connectionId+1}\n"
	            ++objectPrefixIdx
                connectionId += 2
	        }
		}
		else
		{
		     kwlString = "type:ossimMemoryImageSource\n"
			 if(params.width&&params.height)
			 {
				 kwlString += "rect:(0,0,${bounds.width},${bonds.height},lh)\n"
				 kwlString += "scalar_type:ossim_uint8\n"
				 kwlString += "number_bands:1\n"
			 }
		}
 	    def mosaic = new joms.oms.Chain();
	    mosaic.loadChainKwlString(kwlString)
        srcChains.each{srcChain->
            mosaic.connectMyInputTo(srcChain.chain)
        }
		result.image = rasterChainService.grabOptimizedImageFromChain(mosaic, params)
		mosaic?.deleteChain()
		srcChains.each{
			it.chain.deleteChain()
			it.kwl = ""
		}
		params.clear()
		mosaic = null
		srcChains?.clear()
		srcChains = null
		wmsView?.delete()
		wmsView = null
    }
    else // setup an empty chain
    {
        result.image = null
        result.errorMessage="No image found for the specified request"
//        kwlString = "type:ossimMemoryImageSource\n"
//        if(params.width&&params.height)
 //       {
 //           kwlString += "rect:(0,0,${params.width},${params.height},lh)\n"
 //           kwlString += "scalar_type:ossim_uint8\n"
 //           kwlString += "number_bands:1\n"
  //      }
  //      def chain = new joms.oms.Chain();
  //      chain.loadChainKwlString(kwlString)
  //      result.image = rasterChainService.grabOptimizedImageFromChain(chain, params)
    }

	return result
  }
  RenderedImage getMapOld(def wmsRequest)
  {
    RenderedImage image = null
    def enableOMS = true
    def quickLookFlagString = wmsRequest?.quicklook ?: "false"
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
      def bandSelectorCount = bands ? bands.split(",").length : 0


      def quickLookFlag = false

      switch ( quickLookFlagString?.toLowerCase() )
      {
      case "true":
      case "on":
        quickLookFlag = true
        break
      }

      def rasterEntries = new WMSQuery().caseInsensitiveBind(wmsRequest.toMap()).rasterEntriesAsList
      rasterEntries.reverse().each { rasterEntry ->
        def geom = (ossimImageGeometry) null
        def geomPtr = (ossimImageGeometryPtr) null
        //def rasterEntry = RasterEntry.get(it)
        if ( rasterEntry != null )
        {
          //rasterEntry.adjustAccessTimeIfNeeded(24) // adjust every 24 hours
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
            if ( quickLookFlag )
            {
              geomPtr = createModelFromTiePointSet(rasterEntry);
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
              else
              {
              }
            }
            geom = (ossimImageGeometry) null
            geomPtr = (ossimImageGeometryPtr) null
          }
        }
      }
      if ( bandSelectorCount > 0 )
      {
        if ( bandSelectorCount >= 3 )
        {
          viewableBandCount = 3;
        }
        else
        {
          viewableBandCount = 1;
        }
      }
      int pixelStride = viewableBandCount
      int lineStride = viewableBandCount * width
      int[] bandOffsets = null;
      if ( viewableBandCount == 1 )
      {
        bandOffsets = [0] as int[]
      }
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
        kwl.add("rotate", "${rotate}")
        kwl.add("null_flip", wmsRequest?.null_flip)
        wmsMap.setChainParameters(kwl)
        wmsMap.getMap(
                wmsRequest.srs,
                bounds[0] as Double, bounds[1] as Double, bounds[2] as Double, bounds[3] as Double,
                Integer.parseInt(wmsRequest.width), Integer.parseInt(wmsRequest.height),
                data
        )


      }
      else
      {
        new java.util.Random().nextBytes(data)
      }
//      wmsMap.cleanUp();
//      wmsMap.delete()
//      wmsMap = null;

      DataBuffer dataBuffer = new DataBufferByte(data, data.size())

      def transparentFlag = wmsRequest?.transparent?.equalsIgnoreCase("true")
      try
      {
        if ( viewableBandCount == 1 )
        {
          image = Utility.convertToColorIndexModel(dataBuffer,
                  width as Integer,
                  height as Integer,
                  transparentFlag)
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
          if ( image && transparentFlag )
          {
            image = TransparentFilter.fixTransparency(new TransparentFilter(), image)
          }
          if ( wmsRequest?.format?.equalsIgnoreCase("image/gif") )
          {
            image = ImageGenerator.convertRGBAToIndexed(image)
          }
        }
      }
      catch (Exception e)
      {
        e.printStackTrace()
      }
      break
    }



    return image;
  }

  def convertToIndexModel()
  {

  }

  BufferedImage getUnprojectedTile(Rectangle rect,
                                   String inputFile,
                                   int entry,
                                   def inputBandCount,
                                   BigDecimal scale,
                                   int startSample, int endSample, int startLine, int endLine,
                                   def params)
  {
    def sharpenMode = params.sharpen_mode ?: ""
    def bands = params?.bands ?: ""
    def rotate = params?.rotate ?: "0.0"
    int viewableBandCount = 1
    if ( sharpenMode.equals("light") )
    {
      params.sharpen_width = "3"
      params.sharpen_sigma = ".5"
    }
    else if ( sharpenMode.equals("heavy") )
    {
      params.sharpen_width = "5"
      params.sharpen_sigma = "1"
    }
    if ( inputBandCount > 3 )
    {
      viewableBandCount = 3
    }
    def bandSelectorCount = bands ? bands.split(",").length : 0
    if ( bandSelectorCount > 0 )
    {
      if ( bandSelectorCount >= 3 )
      {
        viewableBandCount = 3;
      }
      else
      {
        viewableBandCount = 1;
      }
    }
//    println params
//    println rect
//    println "${inputFile} ${entry}"
//    println stretchMode
//    println viewportStretchMode
//    println scale
//    println "${startSample} ${endSample} ${startLine} ${endLine}"

    byte[] data = new byte[rect.width * rect.height * 3]
    def kwl = new ossimKeywordlist();
    params.each {name, value ->
      kwl.add(name, value)
    }
    kwl.add("viewable_bands", "${viewableBandCount}")
    kwl.add("rotate", "${rotate}")
    WmsMap.getUnprojectedMap(
            inputFile,
            entry,
            scale,
            startSample, endSample, startLine, endLine,
            data,
            kwl
    )
    DataBuffer dataBuffer = new DataBufferByte(data, data.size())
    int pixelStride = viewableBandCount
    int lineStride = viewableBandCount * rect.width
    int[] bandOffsets = null;
    if ( viewableBandCount == 1 )
    {
      bandOffsets = [0] as int[]
    }
    else
    {
      bandOffsets = [0, 1, 2] as int[]
    }
    def image;
    if ( viewableBandCount == 1 )
    {
      image = Utility.convertToColorIndexModel(dataBuffer, rect.width as Integer, rect.height as Integer, false)
    }
    else
    {
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

      image = new BufferedImage(
              colorModel,
              raster,
              isRasterPremultiplied,
              properties)
    }

    return image
  }

  String getCapabilities(def wmsRequest, String serviceAddress)
  {
    def layers = wmsRequest?.layers?.split(',')
    def rasterEntries = rasterEntrySearchService.getWmsImageLayers(layers)
    def wmsCapabilites = new WMSCapabilities(rasterEntries, serviceAddress)

    return wmsCapabilites.getCapabilities()
  }

  String getKML(def wmsRequest, String serviceAddress)
  {
    def layers = wmsRequest?.layers?.split(',')
    def rasterEntries = rasterEntrySearchService.getWmsImageLayers(layers)
    def wmsCapabilities = new WMSCapabilities(rasterEntries, serviceAddress)

    return wmsCapabilities.getKML()
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
        def unionBounds = null
        rasterEntries.each { rasterEntry ->
          def groundGeom = rasterEntry?.groundGeom
          if(unionBounds)
          {
            unionBounds = unionBounds.union(groundGeom)
          }
          else
          {
            unionBounds = groundGeom
          }
        }
        def coords = unionBounds?.envelope?.coordinates
        def minx = 9999999999
        def maxx = -9999999999
        def miny = 9999999999
        def maxy = -9999999999
        coords.each{coord->
            if(coord.x < minx) minx = coord.x
            if(coord.x > maxx) maxx = coord.x
            if(coord.y < miny) miny = coord.y
            if(coord.y > maxy) maxy = coord.y
        }

      return [left: minx, right: maxx, top: maxy, bottom: miny]
    }

  def createModelFromTiePointSet(def rasterEntry)
  {
    def gptArray = new ossimGptVector();
    def dptArray = new ossimDptVector();
    if ( rasterEntry?.tiePointSet )
    {
      def tiepoints = new XmlSlurper().parseText(rasterEntry?.tiePointSet)
      def imageCoordinates = tiepoints.Image.toString().trim()
      def groundCoordinates = tiepoints.Ground.toString().trim()
      def splitImageCoordinates = imageCoordinates.split(" ");
      def splitGroundCoordinates = groundCoordinates.split(" ");
      splitImageCoordinates.each {
        def point = it.split(",")
        if ( point.size() >= 2 )
        {
          dptArray.add(new ossimDpt(Double.parseDouble(point.getAt(0)),
                  Double.parseDouble(point.getAt(1))))
        }
      }
      splitGroundCoordinates.each {
        def point = it.split(",")
        if ( point.size() >= 2 )
        {
          gptArray.add(new ossimGpt(Double.parseDouble(point.getAt(1)),
                  Double.parseDouble(point.getAt(0))))
        }
      }
    }
    else if ( rasterEntry?.groundGeom ) // lets do a fall back if the tiepoint set is not set.
    {
      def coordinates = rasterEntry?.groundGeom.getCoordinates();
      if ( coordinates.size() >= 4 )
      {
        def w = width as double
        def h = height as double
        (0..<4).each {
          def point = coordinates[it];
          gptArray.add(new ossimGpt(coordinates[it].y, coordinates[it].x));
        }
        dptArray.add(new ossimDpt(0.0, 0.0))
        dptArray.add(new ossimDpt(w - 1, 0.0))
        dptArray.add(new ossimDpt(w - 1, h - 1))
        dptArray.add(new ossimDpt(0.0, h - 1))
      }
    }
    if ( (gptArray.size() < 1) || (dptArray.size() < 1) )
    {
      return null
    }
    return Util.createBilinearModel(dptArray, gptArray)
  }


  def wmsToScreen(double minx, double miny, double maxx, double maxy, int imageWidth, int imageHeight)
  {
    // Extent width and height
    double extentWidth = maxx - minx
    double extentHeight = maxy - miny

    // Scale
    double scaleX = extentWidth > 0 ? imageWidth / extentWidth : java.lang.Double.MAX_VALUE
    double scaleY = extentHeight > 0 ? imageHeight / extentHeight : 1.0 as double


    double tx = -minx * scaleX
    double ty = (miny * scaleY) + (imageHeight)

    // AffineTransform
    return new AffineTransform(scaleX, 0.0d, 0.0d, -scaleY, tx, ty)

  }


  def drawToGraphics(Graphics2D g2d, AffineTransform atx, def geoms)
  {
    try
    {
/*
      println "Before"

      g2d.color = Color.BLACK
      Composite c = g2d.composite
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
      g2d.stroke = new BasicStroke(2)

      if ( !(geoms instanceof List) )
      {
        geoms = [geoms]
      }

      geoms.each {g ->
        LiteShape shp = new LiteShape(g.g, atx, false)
        if ( g instanceof Polygon || g instanceof MultiPolygon )
        {
          g2d.color = Color.WHITE
          g2d.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, new Float(0.5).floatValue())
          //g2d.fill(shp)
          g2d.draw(shp)
        }
        g2d.composite = c
        g2d.color = Color.BLACK
        g2d.draw(shp)
      }

      println "After"
*/
      print geoms
    }
    catch (Exception e)
    {
      e.printStackTrace()
    }

  }

  def drawLayer(def style, String layer, Map params, Date startDate, Date endDate, def wmsRequest, Graphics2D g2d)
  {
    def queryParams = null
    def searchService = null

    def width = wmsRequest.width.toInteger()
    def height = wmsRequest.height.toInteger()

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

    // SCOTTIE HACK - Need to make this pluggable
    if ( layer == "Imagery" || layer == "ImageData" )
    {
      //queryParams = new RasterEntryQuery()
      try
      {
        queryParams = Class.forName("org.ossim.omar.RasterEntryQuery", true,
                Thread.currentThread().getContextClassLoader()).newInstance()
      }
      catch (Exception e)
      { e.printStackTrace() }

      searchService = rasterEntrySearchService

    }
    else if ( layer == "Videos" || layer == "VideoData" )
    {
      //queryParams = new VideoDataSetQuery()
      try
      {
        queryParams = Class.forName("org.ossim.omar.VideoDataSetQuery", true,
                Thread.currentThread().getContextClassLoader()).newInstance()
      }
      catch (Exception e)
      { e.printStackTrace() }

      searchService = videoDataSetSearchService
    }
    else
    {
      log.info("Layer ${layer} is not understood for footprint drawing.  Only layers Imagery or Videos accepted")
    }

    queryParams.caseInsensitiveBind(params)

    queryParams.with {
      aoiMaxLat = maxy
      aoiMinLat = miny
      aoiMaxLon = maxx
      aoiMinLon = minx
    }

    if ( !startDate && !endDate )
    {
      startDate = DateUtil.initializeDate("startDate", params)
      endDate = DateUtil.initializeDate("endDate", params)
    }

    queryParams.startDate = startDate
    queryParams.endDate = endDate

    //println "HERE"


    def affine = wmsToScreen(minx, miny, maxx, maxy, width, height)


    g2d.color = new Color(
            style.outlinecolor.r as float,
            style.outlinecolor.g as float,
            style.outlinecolor.b as float,
            style.outlinecolor.a as float
    )

    Composite c = g2d.composite
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g2d.stroke = new BasicStroke(style.width)


    def closure = { geom ->
      LiteShape shp = new LiteShape(geom, affine, false)

      if ( style.fillcolor && (geom instanceof Polygon || geom instanceof MultiPolygon) )
      {
        g2d.color = new Color(style.fillcolor.r, style.fillcolor.g, style.fillcolor.b, style.fillcolor.a)
        g2d.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, new Float(0.5).floatValue())
        g2d.fill(shp)
      }

      g2d.composite = c
      g2d.color = new Color(style.outlinecolor.r, style.outlinecolor.g, style.outlinecolor.b, style.outlinecolor.a)
      g2d.draw(shp)

    }

    searchService?.scrollGeometries(queryParams, params, closure)
  }

  def getBaseLayers()
  {
    def baseWMS = grailsApplication.config.wms.base.layers


    def wmsLayers = WmsLayers.list()

    wmsLayers?.each { wmsLayer ->
      def newLayer = [
              name: wmsLayer.name,
              url: wmsLayer.url,
              params: wmsLayer.params ?: grailsApplication.config.wms.base.defaultParams,
              options: wmsLayer.options ?: [:]
      ]

      if ( !baseWMS.find { it.name == newLayer.name } )
      {
        baseWMS << newLayer
      }
    }

    return baseWMS

  }

}

