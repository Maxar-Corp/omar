package org.ossim.omar
import joms.oms.Chain
import joms.oms.ossimString
import joms.oms.WmsView
import joms.oms.Util
import joms.oms.ossimDpt
import joms.oms.ossimDrect

class WebCoverageService {

    static transactional = true

    def getCoverage(def wcsRequest) {

        def coverageList = wcsRequest?.coverage?.split(",")
        def srcChains    = []
        def wmsQuery  = new WMSQuery()
        def params    = wcsRequest.toMap();
        params.layers = params.coverage;

        def max    = params.max?params.max as Integer:10
        if(max > 10) max = 10
        Utility.simpleCaseInsensitiveBind(wmsQuery, params)
        wmsQuery.max = max

        // for now we will sort by the date field if no layers are given
        //
        if(!wmsQuery.layers)
        {
            wmsQuery.sort  = wmsQuery.sort?:"acquisitionDate"
            wmsQuery.order = wmsQuery.order?:"desc"
        }
        def quickLookFlagString = wcsRequest?.quicklook ?: "false"
        def interpolation = wcsRequest.interpolation?wcsRequest.interpolation:"nearest neighbor"
        def sharpenMode = wcsRequest?.sharpen_mode?: ""
        def crs = wcsRequest?.response_crs?wcsRequest?.response_crs:wcsRequest?.crs
        def nullFlip = wcsRequest?.null_flip ?: null
        def requestFormat = wcsRequest?.format?.toLowerCase()
        def sharpenWidth = wcsRequest?.sharpen_width ?: null
        def sharpenSigma = wcsRequest?.sharpen_sigma ?: null
        def stretchMode = wcsRequest?.stretch_mode ? wcsRequest?.stretch_mode.toLowerCase(): null
        def stretchModeRegion = wcsRequest?.stretch_mode_region ?:null
        def bands = wcsRequest?.bands ?: ""
        def bounds = wcsRequest.bounds
        def wmsView = new WmsView()
        def defaultOutputName = "coverage"
        if(!wmsView.setProjection(crs))
        {
            log.error("Unsupported projection ${crs}")
            return null
        }
        if(!bounds)
        {
            log.error("No BBOX found.  We currently require a BBOX")
            return null
        }
        if(!wcsRequest.width)
        {
            log.error("No width found.")
            return null
        }
        if(!wcsRequest.height)
        {
            log.error("No height found.")
            return null
        }
        if(!wmsView.setViewDimensionsAndImageSize(bounds.minx, bounds.miny, bounds.maxx, bounds.maxy, bounds.width, bounds.height))
        {
            log.error("Unable to set the dimensions for the view bounds")
            return null
        }
        def rasterEntries = wmsQuery.getRasterEntriesAsList();
        def objectPrefixIdx = 0
        if(wmsQuery.layers)
        {
            rasterEntries = rasterEntries?.reverse()
        }
        def kwlString = null
        def imageRect = wmsView.getViewImageRect()
        def midPoint  = imageRect.midPoint()
        def x         = (int)(midPoint.x+0.5)
        def y         = (int)(midPoint.y+0.5)
        x            -= (bounds.width*0.5);
        y            -= (bounds.height*0.5);
        def w         = bounds.width
        def h         = bounds.height
        rasterEntries.each{rasterEntry->
            def histogramFile = new File(rasterEntry.getFileFromObjects("histogram")?.name)
            def overviewFile  = new File(rasterEntry.getFileFromObjects("overview")?.name)
            objectPrefixIdx = 0
            kwlString       = "type: ossimImageChain\n"
            def chain           = new joms.oms.Chain();
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
                def bandArray = bands.split(",")
                def validBands = true
                def maxBands = rasterEntry.numberOfBands as Integer
                if(bandArray.size() < 1)
                {
                    validBands = false
                }
                else
                {
                    bandArray.each{
                        try
                        {
                            if(Integer.parseInt(it) >= maxBands)
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
                if(validBands)
                {
                    kwlString += "object${objectPrefixIdx}.type:ossimBandSelector\n"
                    kwlString += "object${objectPrefixIdx}.bands:(${bands})\n"
                    ++objectPrefixIdx
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
                if(stretchModeRegion == "global")
                {
                    kwlString += "object${objectPrefixIdx}.type:ossimHistogramRemapper\n"
                    kwlString += "object${objectPrefixIdx}.histogram_filename:${histogramFile}\n"
                    kwlString += "object${objectPrefixIdx}.stretch_mode:${stretchMode}\n"
                    ++objectPrefixIdx
                }
               //  kwlString          += "object${objectPrefixIdx}.type:ossimHistogramRemapper\n"
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
                }

            }
            if(sharpenSigma&&sharpenWidth)
            {
                kwlString += "object${objectPrefixIdx}.type:ossimImageSharpenFilter\n"
                kwlString += "object${objectPrefixIdx}.kernel_sigma:${sharpenSigma}\n"
                kwlString += "object${objectPrefixIdx}.kernel_width:${sharpenWidth}\n"
                ++objectPrefixIdx
            }
            //CONSTRUCT IMAGE CACHE
            //
            kwlString          += "object${objectPrefixIdx}.type:ossimCacheTileSource\n"

            ++objectPrefixIdx
            //CONSTRUCT RENDERER
            //
            kwlString          += "object${objectPrefixIdx}.type:ossimImageRenderer\n"
            kwlString          += "object${objectPrefixIdx}.max_levels_to_compute:0\n"
            kwlString          += "object${objectPrefixIdx}.resampler.magnify_type:  ${interpolation}\n"
            kwlString          += "object${objectPrefixIdx}.resampler.minify_type:  ${interpolation}\n"

            ++objectPrefixIdx
            //CONSTRUCT VIEW CACHE
            //
            kwlString          += "object${objectPrefixIdx}.type:ossimCacheTileSource\n"
            kwlString          += "object${objectPrefixIdx}.tile_size_xy:(64,64)\n"
            ++objectPrefixIdx


            chain.loadChainKwlString(kwlString)
            Util.setAllViewGeometries(chain.getChain(), wmsView.getImageGeometry().get(), false);
            //chain.print()
            if(chain.getChain()!=null)
            {
                srcChains.add(chain)
            }
        }


        // now establish mosaic and cut to match the output dimensions
        kwlString = "type:ossimImageChain\n"
        kwlString += "object0.type:ossimImageMosaic\n"

        kwlString += "object1.type:ossimRectangleCutFilter\n"
        kwlString += "object1.rect:(${x},${y},${w},${h},lh)\n"
        kwlString += "object1.cut_type:null_outside\n"
        kwlString += "object1.id:10001\n"

        def connectionId = 10001
        objectPrefixIdx = 2
        if(stretchModeRegion == "viewport")
        {
            kwlString += "object${objectPrefixIdx}.type:ossimImageHistogramSource\n"
            kwlString += "object${objectPrefixIdx}.id:10002\n"
            ++objectPrefixIdx
            kwlString += "object${objectPrefixIdx}.type:ossimHistogramRemapper\n"
            kwlString += "object${objectPrefixIdx}.id:10003\n"
            kwlString += "object${objectPrefixIdx}.stretch_mode:${stretchMode}\n"
            kwlString += "object${objectPrefixIdx}.input_connection1:10001\n"
            kwlString += "object${objectPrefixIdx}.input_connection2:10002\n"
            ++objectPrefixIdx
            connectionId = 10003
        }
        if(requestFormat.contains("geotiff8")||
           requestFormat.contains("jpeg"))
        {
            kwlString += "object${objectPrefixIdx}.type:ossimScalarRemapper\n"
        }
        def mosaic = new joms.oms.Chain();
        mosaic.loadChainKwlString(kwlString)
        srcChains.each{srcChain->
            mosaic.connectMyInputTo(srcChain)
        }
        def writerType = "ossimTiffWriter"
        def contentType = "image/tiff"
        def outputFileName = "${defaultOutputName}.tif"
        File tempFile = File.createTempFile("wcs", ".tif");
        // now establish a writer
        kwlString = "type:ossimTiffWriter\n"
        //kwlString = "type:gdal_PNG\n"
        //kwlString = "type:gdal_GTiff\n"
        //kwlString = "type:ossimJpegWriter\n"
        kwlString += "filename:${tempFile}\n"
        kwlString += "image_type:tiff_tiled\n"
        //kwlString += "image_type:tiff_tiled_band_separate\n"
        def writer = new joms.oms.Chain();
        writer.loadChainKwlString(kwlString)
        writer.connectMyInputTo(mosaic)
        writer.executeChain()
        writer.deleteChain()
        mosaic.deleteChain()
        writer = null
        mosaic = null
        srcChains.each{srcChain->
            srcChain.deleteChain()
        }
        srcChains = null
        [file:tempFile, outputName: "${outputFileName}", contentType: "${contentType}"]
    }
}
