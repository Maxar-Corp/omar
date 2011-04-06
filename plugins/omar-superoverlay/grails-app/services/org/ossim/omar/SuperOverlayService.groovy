package org.ossim.omar
import groovy.xml.StreamingMarkupBuilder
import org.ossim.omar.WMSRequest

class SuperOverlayService {

    def kmlService
    def appTagLib = new org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib()
    static transactional = true

    def createRootKml(def rasterEntry, def fullResBound, def tileSize, def params)
    {
        def rasterEntryName      = rasterEntry.title?:rasterEntry.filename
        def kmlbuilder = new StreamingMarkupBuilder()
        kmlbuilder.encoding = "UTF-8"
        def newParams = new HashMap(params)
        def tileBounds = tileBound(params, fullResBound)
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
                        minLodPixels("${tileSize.width}")
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
                      newParams.remove("action")
                      newParams.remove("controller")

                      href { mkp.yieldUnescaped("<![CDATA[${appTagLib.createLink(absolute: true, action:params.action, params: newParams)}]]>") }
                      viewRefreshMode("onRegion")
                  }
              }
            }
          }
        }

        kmlbuilder.bind(kmlnode).toString()
    }
    def createTileKml(def rasterEntry, def fullResBound, def tileSize, def metersPerDegree, def params)
    {
        def kmlbuilder = new StreamingMarkupBuilder()
        kmlbuilder.encoding = "UTF-8"
        def tileBounds = tileBound(params, fullResBound)
        def wmsRequest = new WMSRequest()
        Utility.simpleCaseInsensitiveBind(wmsRequest, params)

        def newParams = new HashMap(params)
        newParams.remove("action")
        newParams.remove("controller")

        def edgeTileFlag = isAnEdgeTile(params.level as Integer, params.row as Integer, params.col as Integer)
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
        if(canSplit(tileBounds, tileSize, metersPerDegree, rasterEntry.metersPerPixel))
        {
            subtiles = generateSubTiles(params, fullResBound)
        }
        def defaultName = "${params.level}/${params.row}/${params.col}.kml"
        def kmlnode = {
          mkp.xmlDeclaration()
          kml("xmlns": "http://earth.google.com/kml/2.1") {
            Document() {
              name(defaultName)
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
                      href{mkp.yieldUnescaped("<![CDATA[${appTagLib.createLink(absolute: true, controller: 'ogc', action: 'wms',params:wmsMap)}]]>")}
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
                        href { mkp.yieldUnescaped("<![CDATA[${appTagLib.createLink(absolute: true, action:params.action, params: newParams)}]]>") }
                        viewRefreshMode("onRegion")
                    }
                }
              }
            }
          }
        }

        kmlbuilder.bind(kmlnode).toString()
    }
    def isAnEdgeTile(def level, def row, def col)
    {
        def result = true

        if(row&&col)
        {
          def maxValue = (2**level) - 1
          if((row != maxValue)&&
             (col != maxValue))
          {
            // must be interior tile
            result = false
          }
        }

        result
    }
    def canSplit(def tileBounds, def tileSize, def metersPerDegree, def fullResMetersPerPixel)
    {
        def deltax = (tileBounds.maxx-tileBounds.minx)
        def deltay = (tileBounds.maxy-tileBounds.miny)
        def maxDelta = deltax>deltay?deltay:deltax
        def maxTileSize = tileSize.width>tileSize.height?tileSize.width:tileSize.height
        def metersPerPixel = (maxDelta*metersPerDegree)/maxTileSize

        // keep splitting if we can zoom further
        metersPerPixel > fullResMetersPerPixel
    }
    def tileBound(def params, def fullResBbox)
    {
        def level = params.level?params.level as Integer:0
        def row   = params.row?params.row as Integer:0
        def col   = params.col?params.col as Integer:0
        def minx  = fullResBbox.minx
        def maxx  = fullResBbox.maxx
        def miny  = fullResBbox.miny
        def maxy  = fullResBbox.maxy
        def deltax = (maxx-minx)/(2**level)
        def deltay = (maxy-miny)/(2**level)

        def llx = minx + deltax*col
        def lly = miny + deltay*row

        [minx:llx, miny:lly, maxx:(llx+deltax), maxy:(lly+deltay)]
    }
    def generateSubTiles(def params, def fullResBbox)
    {
        def level = params.level as Integer
        def row   = params.row   as Integer
        def col   = params.col   as Integer
        def nrow = row*2
        def ncol = col*2
        def minx  = fullResBbox.minx
        def maxx  = fullResBbox.maxx
        def miny  = fullResBbox.miny
        def maxy  = fullResBbox.maxy
        ++level
        def deltax = (maxx-minx)/(2**level)
        def deltay = (maxy-miny)/(2**level)

        def llx = minx + deltax*ncol
        def lly = miny + deltay*nrow

        [[minx:llx, miny:lly, maxx:(llx+deltax), maxy:(lly+deltay), level:level, col:ncol, row:nrow],
         [minx:llx+deltax, miny:lly, maxx:(llx+2.0*deltax), maxy:(lly+deltay), level:level, col:(ncol+1), row:nrow],
         [minx:llx+deltax, miny:(lly+deltay), maxx:(llx+2.0*deltax), maxy:(lly+2.0*deltay), level:level, col:(ncol+1), row:(nrow+1)],
         [minx:llx, miny:lly+deltay, maxx:(llx+deltax), maxy:(lly+2.0*deltay), level:level, col:ncol, row:(nrow+1)]
        ]
    }
}
