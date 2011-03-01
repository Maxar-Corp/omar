package org.ossim.omar
import joms.oms.Chain
import joms.oms.WmsMap
import joms.oms.ossimKeywordlist
import joms.oms.ossimGptVector
import joms.oms.ossimDptVector
import joms.oms.Util
import joms.oms.ossimDpt
import joms.oms.ossimGpt

class RasterChainService {

    static transactional = true

	/**
	 * @param numberOfInputBands
	 * @param bandSelection
	 * @return true if the bandSelection list is valid or false otherwise
	 */
	static def validBandSelection(def numberOfInputBands, def bandSelection)
	{
		def bandArray = []
		def validBands = true
		if(bandSelection instanceof String)
		{
		   bandArray = bandSelection.split(",")
		}
		// validate that the ban list is within the desired ranges
		// the http request is 0 based.
		if(bandArray.size() < 1)
		{
			validBands = false
		}
		else
		{
			bandArray.each{
				try
				{
					if(Integer.parseInt(it) >= numberOfInputBands)
					{
						validBands = false;
					}
				}
				catch(Exception e)
				{
				   validBands = false
				}
			}
		}
		validBands
	}
	/**
	 * @param rasterEntry
	 * @param params
	 * @return A new chain
	 */
	def createRasterEntryChain(def rasterEntry, def params)
	{
        def quickLookFlagString = params?.quicklook ?: "false"
        def interpolation = params.interpolation?params.interpolation:"bilinear"
        def sharpenMode = params?.sharpen_mode?: ""
        def crs = params.srs?params.srs:params.crs?:null
        def nullFlip = params?.null_flip ?: null
        def requestFormat = params?.format?.toLowerCase()
        def sharpenWidth = params?.sharpen_width ?: null
        def sharpenSigma = params?.sharpen_sigma ?: null
        def stretchMode = params?.stretch_mode ? params?.stretch_mode.toLowerCase(): null
        def stretchModeRegion = params?.stretch_mode_region ?:null
        def bands = params?.bands ?: ""
		
		def histogramFile = new File(rasterEntry.getFileFromObjects("histogram")?.name)
		def overviewFile  = new File(rasterEntry.getFileFromObjects("overview")?.name)
		def objectPrefixIdx = 0
		def kwlString       = "type: ossimImageChain\n"
		def chain           = new joms.oms.Chain()
	    def quickLookFlag = false
	
	    switch ( quickLookFlagString?.toLowerCase() )
	    {
	      case "true":
	      case "on":
	        quickLookFlag = true
	        break
	    }
		// CONSTRUCT HANDLER
		//
		kwlString          += "object${objectPrefixIdx}.type:${rasterEntry.className?rasterEntry.className:'ossimImageHandler'}\n"
		kwlString          += "object${objectPrefixIdx}.entry:${rasterEntry.entryId}\n"
		kwlString          += "object${objectPrefixIdx}.filename:${rasterEntry.rasterDataSet.getFileFromObjects("main").name}\n"
		kwlString          += "object${objectPrefixIdx}.width:${rasterEntry.width}\n"
		kwlString          += "object${objectPrefixIdx}.height:${rasterEntry.height}\n"
		if(overviewFile.exists())
		{
			kwlString          += "object${objectPrefixIdx}.overview_file:${overviewFile}\n"
		}
		++objectPrefixIdx

		// CONSTRUCT BAND SELECTION IF NEEDED
		//
		if(bands)
		{
			if( validBandSelection( rasterEntry.numberOfBands, bands ) )
			{
				// the keywordlist in ossim takes a list of integers surrounded
				// by parenthesis
				//
				kwlString += "object${objectPrefixIdx}.type:ossimBandSelector\n"
				kwlString += "object${objectPrefixIdx}.bands:(${bands})\n"
				++objectPrefixIdx
			}
			else
			{
				log.error("Invalid band selection (${bands}) for image ${rasterEntry.id}")
			}
		}
		if(nullFlip)
		{
			kwlString += "object${objectPrefixIdx}.type:ossimNullPixelFlip\n"
			++objectPrefixIdx
		}
		// CONSTRUCT HISTOGRAM STRETCHING IF NEEDED
		//
		if(stretchMode&&stretchModeRegion)
		{
			if(stretchModeRegion == "global"  )
			{
				if(histogramFile.exists())
				{
					kwlString += "object${objectPrefixIdx}.type:ossimHistogramRemapper\n"
					kwlString += "object${objectPrefixIdx}.histogram_filename:${histogramFile}\n"
					kwlString += "object${objectPrefixIdx}.stretch_mode:${stretchMode}\n"
					++objectPrefixIdx
				}
				else
				{
					log.error("Histogram file does not exist and will ignore the stretch: ${histogramFile}")
				}
			}
		}
		// CONSTRUCT SHARPENING IF NEEDED
		//
		if(sharpenMode)
		{
			switch(sharpenMode)
			{
				case "light":
					sharpenSigma = 0.5
					sharpenWidth = 3
					break
				case "heavy":
					sharpenSigma = 1.0
					sharpenWidth = 5.0
					break
				default:
					break
			}

		}
		if(sharpenSigma&&sharpenWidth)
		{
			kwlString += "object${objectPrefixIdx}.type:ossimImageSharpenFilter\n"
			kwlString += "object${objectPrefixIdx}.kernel_sigma:${sharpenSigma}\n"
			kwlString += "object${objectPrefixIdx}.kernel_width:${sharpenWidth}\n"
			++objectPrefixIdx
		}
		
		if(crs)
		{
			//CONSTRUCT IMAGE CACHE
			//
			kwlString          += "object${objectPrefixIdx}.type:ossimCacheTileSource\n"
			kwlString          += "object${objectPrefixIdx}.tile_size_xy:(64,64)\n"
			
			++objectPrefixIdx
			//CONSTRUCT RENDERER
			//
			kwlString          += "object${objectPrefixIdx}.type:ossimImageRenderer\n"
			kwlString          += "object${objectPrefixIdx}.max_levels_to_compute:0\n"
			kwlString          += "object${objectPrefixIdx}.resampler.magnify_type:  ${interpolation}\n"
			kwlString          += "object${objectPrefixIdx}.resampler.minify_type:  ${interpolation}\n"
	        def kwl = new ossimKeywordlist()
	        kwl.add("object${objectPrefixIdx}.image_view_trans.type", "ossimImageViewProjectionTransform")
			if(params.viewGeom?.get())
			{
				params.viewGeom.get().saveState(kwl, "object${objectPrefixIdx}.image_view_trans.view_geometry.")
			}
	        if ( quickLookFlag )
	        {
	            def geomPtr = createModelFromTiePointSet(rasterEntry);
	            if ( geomPtr != null )
	            {
	              geomPtr.get().saveState(kwl, "object${objectPrefixIdx}.image_view_trans.image_geometry.")
				  
	            }
				geomPtr.delete()
	        }
	        kwlString += "${kwl.toString()}\n"
		    kwl.delete()
			kwl = null
			++objectPrefixIdx
			//CONSTRUCT VIEW CACHE
			//
			kwlString          += "object${objectPrefixIdx}.type:ossimCacheTileSource\n"
			kwlString          += "object${objectPrefixIdx}.tile_size_xy:(64,64)\n"
			++objectPrefixIdx
		}
		else
		{
			//CONSTRUCT VIEW CACHE or an image cache depending on if parameters were supplied
			//
			if(params)
			{
				kwlString          += "object${objectPrefixIdx}.type:ossimCacheTileSource\n"
				kwlString          += "object${objectPrefixIdx}.tile_size_xy:(64,64)\n"
			}
			else
			{
				// because this is straight to an image let's just use the default
				// tile size
				kwlString          += "object${objectPrefixIdx}.type:ossimCacheTileSource\n"
			}
		}
		//println kwlString
		//println "*"*40
		chain.loadChainKwlString(kwlString)
		chain
	}
	
	/**
	 * @param params 
	 * @return A Map that contains the content-type and the chain object
	 */
	def createWriterChain(def params)
	{
		def requestFormat      = params?.format?.toLowerCase()
		def temporaryDirectory = params?.temporaryDirectory
		def tempFilenamePrefix = params?.filenamePrefix?:"rasterChainService"
		
        def ext = null
        def contentType = null
        def kwlString = ""
        switch(requestFormat)
        {
            case ~/.*jpeg.*/:
                kwlString  += "type:ossimJpegWriter\n"
                contentType = "image/jpeg"
                ext = ".jpg"
                break
            case ~/.*tiff.*/:
                kwlString    += "type:ossimTiffWriter\n"
                kwlString    += "image_type:tiff_tiled\n"
                contentType  =  "image/tiff"
                ext          =  ".tif"
                break
            case ~/.*jp2.*/:
                kwlString   += "type:ossimKakaduJp2Writer\n"
                contentType  = "image/jp2"
                ext = ".jp2"
                break
            case ~/.*png.*/:
                kwlString   += "type:ossimPngWriter\n"
                contentType  = "image/png"
                ext = ".png"
                break
            default:
                log.error("Unsupported FORMAT=${requestFormat}")
                break
        }
		def writer = null
		def outputFileName = null
		def tempFile = null
		if(ext != null)
		{
	        tempFile = File.createTempFile(tempFilenamePrefix, ext, temporaryDirectory?new File(temporaryDirectory):null);
	        // now establish a writer
	        //
	        kwlString += "filename:${tempFile}\n"
	        writer = new joms.oms.Chain();
	        writer.loadChainKwlString(kwlString)
		}
		
		return [chain:writer, contentType:contentType, file:tempFile]
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
  
}
