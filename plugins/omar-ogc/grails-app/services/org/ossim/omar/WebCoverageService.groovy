package org.ossim.omar
import joms.oms.Chain
import joms.oms.ossimString
import joms.oms.WmsView
import joms.oms.Util
import joms.oms.ossimDpt
import joms.oms.ossimDrect
import org.springframework.beans.factory.InitializingBean
class WebCoverageService implements InitializingBean{

    static transactional = true
    def grailsApplication
    def temporaryDirectory
	def rasterChainService
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
        def crs = wcsRequest?.response_crs?wcsRequest?.response_crs:wcsRequest?.crs
        def requestFormat = wcsRequest?.format?.toLowerCase()
        def sharpenWidth = wcsRequest?.sharpen_width ?: null
        def sharpenSigma = wcsRequest?.sharpen_sigma ?: null
        def stretchMode = wcsRequest?.stretch_mode ? wcsRequest?.stretch_mode.toLowerCase(): null
        def stretchModeRegion = wcsRequest?.stretch_mode_region ?:null
        def bands = wcsRequest?.bands ?: ""
        def bounds = wcsRequest.bounds
        def wmsView = new WmsView()
        def defaultOutputName = "coverage"
		def wcsParams = wcsRequest.toMap()
        if(!wmsView.setProjection(crs))
        {
            log.error("Unsupported projection ${crs}")
            return null
        }
        if(!bounds)
        {
            log.error("Invalid Bounds found.  We currently require a BBOX along with either WIDTH HEIGHT or RESX RESY")
            return null
        }
        if(!wmsView.setViewDimensionsAndImageSize(bounds.minx, bounds.miny, bounds.maxx, bounds.maxy, bounds.width, bounds.height))
        {
            log.error("Unable to set the dimensions for the view bounds")
            return null
        }
        def rasterEntries = wmsQuery.getRasterEntriesAsList();
        def objectPrefixIdx = 0
        if(rasterEntries)
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
		wcsParams.viewGeom = wmsView.getImageGeometry()
        rasterEntries.each{rasterEntry->
			def chainMap = rasterChainService.createRasterEntryChain(rasterEntry, wcsParams)
           // Util.setAllViewGeometries(chain.getChain(), wmsView.getImageGeometry().get(), false);
            //chainMap.chain.print()
            if(chainMap.chain&&(chainMap.chain.getChain()!=null))
            {
                srcChains.add(chainMap.chain)
            }
        }
		wcsParams.viewGeom = null

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
        if(requestFormat.contains("uint8")||
           requestFormat.contains("jpeg"))
        {
            kwlString += "object${objectPrefixIdx}.type:ossimScalarRemapper\n"
        }

		
        def mosaic = new joms.oms.Chain();
        mosaic.loadChainKwlString(kwlString)
        srcChains.each{srcChain->
            mosaic.connectMyInputTo(srcChain)
        }
		
		def writer = rasterChainService.createWriterChain([format:wcsParams.format, 
			                                               temporaryDirectory:"${temporaryDirectory}",
														   filenamePrefix:"wcs"])
		def contentType = ""
        if(writer.chain&&(writer.chain.getChain() != null))
        {
            writer.chain.connectMyInputTo(mosaic)
            writer.chain.executeChain()
			contentType = writer.contentType
        }
        else
        {
            log.error("Unable to create a writer with the format ${wcsParams.format}")
        }
        writer.chain.deleteChain()
        mosaic.deleteChain()
        writer.chain = null
        mosaic = null
        srcChains.each{srcChain->
            srcChain.deleteChain()
        }
        srcChains = null
		
        [file:writer.file, outputName: "${defaultOutputName}${writer.ext}", contentType: "${contentType}"]
    }
    public void afterPropertiesSet()
    {
      temporaryDirectory = grailsApplication.config.export.workDir
    }
}
