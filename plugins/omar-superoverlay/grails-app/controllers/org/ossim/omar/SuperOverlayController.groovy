package org.ossim.omar

import java.util.Date;
import org.ossim.omar.SuperOverlayQueueItem
import org.apache.commons.collections.map.CaseInsensitiveMap
import joms.oms.WmsView
import joms.oms.ossimUnitType
import joms.oms.ossimGpt
import joms.oms.ossimDpt
import joms.oms.Chain
import org.ossim.omar.WMSRequest
import org.springframework.beans.factory.InitializingBean
import groovy.xml.StreamingMarkupBuilder

class SuperOverlayController implements InitializingBean{
	def rasterChainService
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
       // println params
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
            def rasterEntryName      = rasterEntry.title?:rasterEntry.filename
            def newParams = new HashMap(params)
            def kmlbuilder = new StreamingMarkupBuilder()
            kmlbuilder.encoding = "UTF-8"
            def bounds = rasterEntry.groundGeom.bounds
            def fullResBound = [minx:bounds.minLon, miny:bounds.minLat, maxx:bounds.maxLon, maxy:bounds.maxLat]
            def tileBounds = superOverlayService.tileBound(params, fullResBound)
            def wmsRequest = new WMSRequest()
            Utility.simpleCaseInsensitiveBind(wmsRequest, params)
            if(params.level&&params.row&&params.col)
            {
                def edgeTileFlag = superOverlayService.isAnEdgeTile(params.level as Integer, params.row as Integer, params.col as Integer)
                def format = "image/jpeg"
                def transparent = false
                def ext = "jpg"
                if(edgeTileFlag)
                {
                    format = "image/png"
                    transparent = true
                    ext = "png"
                }
                Utility.simpleCaseInsensitiveBind(wmsRequest, [request:'GetMap',
                        layers:params.id,
                        srs:'EPSG:4326',
                        format:format,
                        service:'wms',
                        version:'1.1.1',
                        width:tileSize.width,
                        height:tileSize.height,
                        transparent:transparent,
                        bbox:"${tileBounds.minx},${tileBounds.miny},${tileBounds.maxx},${tileBounds.maxy}"])
                def wmsMap = wmsRequest.toMap()
                Utility.removeEmptyParams(wmsMap)

                def subtiles = []
                if(superOverlayService.canSplit(tileBounds, tileSize, metersPerDegree, rasterEntry.metersPerPixel))
                {
                    subtiles = superOverlayService.generateSubTiles(params, fullResBound)
                }
                def kmlnode = {
                  mkp.xmlDeclaration()
                  kml("xmlns": "http://earth.google.com/kml/2.1") {
                    Document() {
                      name("${params.level}/${params.row}/${params.col}.kml")
                      description()
                      Style(){
                          ListStyle(id:"hideChildren"){
                             listItemType("checkHideChildren")
                          }
                      }
                      Region(){
                          Lod(){
                              minLodPixels("${tileSize.width}")
                              if(subtiles.size() > 0)
                              {
                                  maxLodPixels("${tileSize.width*8}")
                              }
                              else
                              {
                                  maxLodPixels(-1)
                              }
                          }
                          LatLonAltBox(){
                              north(tileBounds.maxy)
                              south(tileBounds.miny)
                              east(tileBounds.maxx)
                              west(tileBounds.minx)
                          }
                      }
                      GroundOverlay(){
                          drawOrder(params.level)
                          Icon(){
                              href{mkp.yieldUnescaped("<![CDATA[${createLink(absolute: true, controller: 'ogc', action: 'wms',params:wmsMap)}]]>")}
                          }
                          LatLonBox(){
                              north(tileBounds.maxy)
                              south(tileBounds.miny)
                              east(tileBounds.maxx)
                              west(tileBounds.minx)
                          }
                      }
                      subtiles.each{tile->
                          newParams.level = tile.level
                          newParams.row   = tile.row
                          newParams.col   = tile.col
                        NetworkLink{
                            name("${tile.level}/${tile.row}/${tile.col}.${ext}")
                            Region{
                                Lod{
                                    minLodPixels("${tileSize.width}")
                                    maxLodPixels("-1")
                                }
                                LatLonAltBox{
                                    north("${tile.maxy}")
                                    south("${tile.miny}")
                                    east("${tile.maxx}")
                                    west("${tile.minx}")
                                }
                            }
                            Link{
                                href { mkp.yieldUnescaped("<![CDATA[${createLink(absolute: true, action:params.action, params: newParams)}]]>") }
                                viewRefreshMode("onRegion")
                            }
                        }
                      }
                    }
                  }
                }
                def kmlString = kmlbuilder.bind(kmlnode).toString()

                render(contentType: "application/vnd.google-earth.kml+xml", text:kmlString,
                        encoding: "UTF-8")
            }
            else
            {
                def rasterEntryDescription = kmlService.createImageKmlDescription(rasterEntry)
                newParams.level = 0
                newParams.row   = 0
                newParams.col   = 0
                def kmlnode = {
                  mkp.xmlDeclaration()
                  kml("xmlns": "http://earth.google.com/kml/2.1") {
                    Document() {
                      name("${rasterEntryName}")
                      Snippet()
                      description{mkp.yieldUnescaped("<![CDATA[${rasterEntryDescription}]]>")}
                      Style(){
                          ListStyle(id:"hideChildren"){
                             listItemType("checkHideChildren")
                          }
                      }
                      Region(){
                          LatLonAltBox(){
                                north(tileBounds.maxy)
                                south(tileBounds.miny)
                                east(tileBounds.maxx)
                                west(tileBounds.minx)
                          }
                      }
                      NetworkLink(){
                        open("1")
                        Region(){
                            Lod(){
                                minLodPixels("256")
                                maxLodPixels("-1")
                            }
                            LatLonAltBox(){
                                  north(tileBounds.maxy)
                                  south(tileBounds.miny)
                                  east(tileBounds.maxx)
                                  west(tileBounds.minx)
                            }
                        }
                          Link(){
                              href { mkp.yieldUnescaped("<![CDATA[${createLink(absolute: true, action:params.action, params: newParams)}]]>") }
                              viewRefreshMode("onRegion")
                          }
                      }
                    }
                  }
                }

                def kmlString = kmlbuilder.bind(kmlnode).toString()
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
