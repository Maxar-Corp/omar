package org.ossim.omar

import java.util.Date;
import org.ossim.omar.SuperOverlayQueueItem
import org.apache.commons.collections.map.CaseInsensitiveMap
import joms.oms.WmsView
import joms.oms.ossimUnitType
import joms.oms.ossimGpt
import joms.oms.ossimDpt
import joms.oms.Chain
import org.springframework.beans.factory.InitializingBean
import groovy.xml.StreamingMarkupBuilder

class SuperOverlayController implements InitializingBean{
	def baseDir
	def serverUrl
    def kmlService
    def superOverlayService
    def metersPerDegree
    def tileSize = [width:256, height:256]
    def index = { render ""}
/*
	def create =
	{
		def result = null
        def paramsIgnoreCase    = new CaseInsensitiveMap(params)
		def rasterEntry = null
		try
		{
			rasterEntry = RasterEntry.findByIndexId(paramsIgnoreCase.layers)?:RasterEntry.findByTitle(paramsIgnoreCase.layers)?:RasterEntry.findById(paramsIgnoreCase.layers)
		}
		catch(Exception e)
		{
			log.error(e)
			rasterEntry = null
		}
		if(rasterEntry&&baseDir)
		{
			def outDir    = new File(baseDir) 
			if(!outDir.exists())
			{
				outDir.mkdirs()
			}
			paramsIgnoreCase.crs    = "EPSG:4326"
			paramsIgnoreCase.srs    = "EPSG:4326"
			def wmsView = new WmsView();
			wmsView.setProjection("EPSG:4326");
			def geometry = wmsView.getImageGeometry()
			if(geometry.valid() && geometry.get().projection)
			{
				geometry.get().projection.changeGsd(rasterEntry.metersPerPixel, ossimUnitType.OSSIM_METERS)
			}
			paramsIgnoreCase.wmsView = wmsView
			def chainMap = rasterChainService.createRasterEntryChain(rasterEntry, paramsIgnoreCase, false)
			def kwlString = chainMap.kwl
			def objectPrefixIdx = chainMap.prefixIdx
			kwlString  += "object${objectPrefixIdx}.type:ossimScalarRemapper\n"
			++objectPrefixIdx
			
			def chain = "";
			if(rasterEntry)
			{
				 SuperOverlayQueueItem.addItem([
					indexId:rasterEntry.indexId,
					dateCreated: new Date(),
					startTime: null,
					endTime: null,
					action:"create",
					priority: 0,
					status: "ready",
					baseDir: "${outDir}",
					kwl: kwlString
					], false)
			}
			result = "Added job for raster ${rasterEntry.indexId}"
		}
		else
		{
			if(!baseDir)
			{
				result = "No base superoverlay output directory specified in OMAR configuration"
			}
		}
		result;
	}
	*/
    def createKml = {
       //println params
        def rasterEntry = null
        try
        {
            if(params.id)
            {
                rasterEntry = RasterEntry.findByIndexId(params.id)?:
                              RasterEntry.findByTitle(params.id)?:
                              RasterEntry.findById(params.id)
            }
        }
        catch(Exception e)
        {
            rasterEntry = null
        }

      //  println rasterEntry
/*
        withFormat{
            kml { render (contentType: "text/plain", text:"KML ${params.id}") }
            jpg { render (contentType: "text/plain", text:"JPEG ${params.id}") }
            png { render (contentType: "text/plain", text:"PNG ${params.id}") }
        }
*/
        if(rasterEntry)
        {
            def bounds = rasterEntry.groundGeom.bounds
            def fullResBound = [minx:bounds.minLon, miny:bounds.minLat, maxx:bounds.maxLon, maxy:bounds.maxLat]
            if(params.level&&params.row&&params.col)
            {
                def kmlString =  superOverlayService.createTileKml(rasterEntry, fullResBound, tileSize, metersPerDegree, params)
                render(contentType: "application/vnd.google-earth.kml+xml", text:kmlString,
                        encoding: "UTF-8")
            }
            else
            {
                def kmlString =  superOverlayService.createRootKml(rasterEntry, fullResBound, tileSize, params)
                response.setHeader("Content-disposition", "attachment; filename=doc.kml")
                render(contentType: "application/vnd.google-earth.kml+xml", text:kmlString,
                        encoding: "UTF-8")
            }
        }
        null
    }
    public void afterPropertiesSet()
    {
		baseDir = grailsApplication.config.export?.superoverlay?.baseDir
        def gpt = new ossimGpt()
        def dpt = gpt.metersPerDegree()
        metersPerDegree = dpt.y
        dpt.delete()
        gpt.delete()
        dpt = null
        gpt = null
		//serverUrl = grailsApplication.config.export?.superoverlay?.serverUrl
    }
}
