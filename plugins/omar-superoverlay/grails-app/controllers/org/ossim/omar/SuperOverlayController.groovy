package org.ossim.omar

import java.util.Date;
import org.ossim.omar.SuperOverlayQueueItem
import org.apache.commons.collections.map.CaseInsensitiveMap
import joms.oms.WmsView
import joms.oms.ossimUnitType
import joms.oms.Chain
import org.springframework.beans.factory.InitializingBean
import groovy.xml.StreamingMarkupBuilder
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.imageio.ImageIO

class SuperOverlayController implements InitializingBean{
	def baseDir
	def serverUrl
    def kmlService
    def superOverlayService
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
        def rasterEntry = null
        // Currently I am having troubles with KMZ and bands=.  For some reason
        // it doesn't work when adding a band selection.  The kmz is good but the
        // image is invalid for some reason.
        //
        def outputAsKmlFlag = params.outputKml?:true
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

        if(rasterEntry)
        {
            // we will return the root document if any level of detail param is null
            //
            def isRoot = ( (params.level==null) || (params.row==null) || (params.col==null))

           // response.setHeader("Pragma", "no-cache");
           // response.setDateHeader("Expires", 1L);
           // response.setHeader("Cache-Control", "no-cache");
           // response.addHeader("Cache-Control", "no-store");

            if(outputAsKmlFlag)
            {
                if(!isRoot)
                {
                    def kmlString =  superOverlayService.createTileKml(rasterEntry, params)
                    //response.setDateHeader("Expires", System.currentTimeMillis()+(24*24*60*60*1000));
                   // response.addHeader("Cache-Control", "max-age=120")
                   //   response.setHeader("max-age", "120");
                    render(contentType: "application/vnd.google-earth.kml+xml", text:kmlString,
                            encoding: "UTF-8")
                }
                else
                {
                    def kmlString = superOverlayService.createRootKml(rasterEntry, params)
                    response.setHeader("Content-disposition", "attachment; filename=doc.kml")
                    render(contentType: "application/vnd.google-earth.kml+xml",
                            text:kmlString,
                            encoding: "UTF-8")
                }
            }
            else
            {
                if(!isRoot)
                {
                    def kmlInfoMap =  superOverlayService.createTileKmzInfo(rasterEntry, params)
                    response.contentType = "application/vnd.google-earth.kmz"
                    response.setHeader("Content-disposition", "attachment; filename=output.kmz")

                    def zos =  new ZipOutputStream(response.outputStream)
                    //create a new zip entry
                    def anEntry = null

                    anEntry = new ZipEntry("doc.kml");
                    //place the zip entry in the ZipOutputStream object
                    zos.putNextEntry(anEntry);

                    zos << kmlInfoMap.kml
                    if(kmlInfoMap.imagePath)
                    {
                        anEntry = new ZipEntry("${kmlInfoMap.imagePath}");
                        //place the zip entry in the ZipOutputStream object
                        zos.putNextEntry(anEntry);
                        if(kmlInfoMap.image)
                        {
                            ImageIO.write(kmlInfoMap.image, kmlInfoMap.format, zos);
                        }
                    }
                    zos.close();
                    response.outputStream.close();
                }
                else
                {
                    def kmlString = superOverlayService.createRootKml(rasterEntry, params)
                    response.setHeader("Content-disposition", "attachment; filename=doc.kml")
                    render(contentType: "application/vnd.google-earth.kml+xml",
                           text:kmlString,
                           encoding: "UTF-8")
                 }
            }
        }
        null
    }
    public void afterPropertiesSet()
    {
		baseDir = grailsApplication.config.export?.superoverlay?.baseDir
		//serverUrl = grailsApplication.config.export?.superoverlay?.serverUrl
    }
}
