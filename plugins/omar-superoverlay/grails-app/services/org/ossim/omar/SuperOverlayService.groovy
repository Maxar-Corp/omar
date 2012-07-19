package org.ossim.omar

import groovy.xml.StreamingMarkupBuilder

import com.vividsolutions.jts.geom.Coordinate
import com.vividsolutions.jts.geom.Geometry
import com.vividsolutions.jts.geom.GeometryFactory
import com.vividsolutions.jts.geom.PrecisionModel
import joms.oms.ossimGpt
import joms.oms.ossimDpt
import org.springframework.beans.factory.InitializingBean
import java.awt.image.BufferedImage
import org.ossim.omar.core.Utility
import org.ossim.omar.ogc.WmsCommand

class SuperOverlayService implements InitializingBean
{

  def metersPerDegree
  def grailsApplication
  def rasterKmlService
  def webMappingService
  def tileSize = [width: 256, height: 256]
  def lodValues = [min: 128, max: 2000]
  def appTagLib = new org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib()
  static transactional = true

  def geometryFactory = new GeometryFactory( new PrecisionModel( PrecisionModel.FLOATING ), 4326 )


  def createFullResBounds( def rasterEntry )
  {
    def bounds = rasterEntry.groundGeom.bounds
    def fullResBound = [minx: bounds.minLon, miny: bounds.minLat, maxx: bounds.maxLon, maxy: bounds.maxLat]

    def fullResMpp = rasterEntry.metersPerPixel;
    def deltax = fullResBound.maxx - fullResBound.minx
    def deltay = fullResBound.maxy - fullResBound.miny
    def degreesPerMeter = 1.0 / metersPerDegree
    def degreesPerPixel = degreesPerMeter * fullResMpp

    def pixelsWide = deltax / degreesPerPixel
    def pixelsHigh = deltay / degreesPerPixel
    def adjustedPixelsWide = Math.ceil( pixelsWide / tileSize.width ) * tileSize.width;
    def adjustedPixelsHigh = Math.ceil( pixelsHigh / tileSize.height ) * tileSize.height;

    fullResBound.maxx = fullResBound.minx + ( degreesPerPixel * adjustedPixelsWide )
    fullResBound.maxy = fullResBound.miny + ( degreesPerPixel * adjustedPixelsHigh )

    fullResBound
  }

  def createPolygonFromTileBounds( def bounds )
  {
    def coords = [
            new Coordinate( bounds.minx, bounds.miny ),
            new Coordinate( bounds.minx, bounds.maxy ),
            new Coordinate( bounds.maxx, bounds.maxy ),
            new Coordinate( bounds.maxx, bounds.miny ),
            new Coordinate( bounds.minx, bounds.miny )
    ] as Coordinate[]

    geometryFactory.createPolygon( geometryFactory.createLinearRing( coords ), null )
  }

  def createRootKml( def rasterEntry, def params )
  {
    def fullResBound = createFullResBounds( rasterEntry )
    def kmlbuilder = new StreamingMarkupBuilder()
    kmlbuilder.encoding = "UTF-8"
    def newParams = new HashMap( params )
    def tileBounds = tileBound( params, fullResBound )
    def rasterEntryDescription = rasterKmlService.createImageKmlDescription( rasterEntry )
    newParams.level = 0
    newParams.row = 0
    newParams.col = 0

    def kmlnode = {
      mkp.xmlDeclaration()
      kml( "xmlns": "http://earth.google.com/kml/2.1" ) {
        Document() {
          name( "${rasterKmlService.createName( rasterEntry )}" )
          Snippet()
          description {mkp.yieldUnescaped( "<![CDATA[${rasterEntryDescription}]]>" )}
          Style() {
            ListStyle( id: "hideChildren" ) {
              listItemType( "checkHideChildren" )
            }
          }
          Region() {
            LatLonAltBox() {
              north( tileBounds.maxy )
              south( tileBounds.miny )
              east( tileBounds.maxx )
              west( tileBounds.minx )
            }
          }
          NetworkLink() {
            open( "1" )
            Region() {
              Lod() {
                minLodPixels( lodValues.min )
                maxLodPixels( "-1" )
              }
              LatLonAltBox() {
                north( tileBounds.maxy )
                south( tileBounds.miny )
                east( tileBounds.maxx )
                west( tileBounds.minx )
              }
            }
            Link() {
              newParams.remove( "action" )
              newParams.remove( "controller" )

              href {
                mkp.yieldUnescaped(
                        """<![CDATA[${
                          appTagLib.createLink(
                                  absolute: true, base: "${grailsApplication.config.omar.serverURL}",
                                  action: params.action, params: newParams )
                        }]]>""" )
              }
              viewRefreshMode( "onExpire" )
            }
          }
        }
      }
    }

    kmlbuilder.bind( kmlnode ).toString()
  }

  def createTileKml( def rasterEntry, def params )
  {
    def fullResBound = createFullResBounds( rasterEntry )
    def kmlbuilder = new StreamingMarkupBuilder()
    kmlbuilder.encoding = "UTF-8"
    def tileBounds = tileBound( params, fullResBound )
    def wmsRequest = new WmsCommand()
    Utility.simpleCaseInsensitiveBind( wmsRequest, params )

    def newParams = new HashMap( params )
    newParams.remove( "action" )
    newParams.remove( "controller" )

    def edgeTileFlag = isAnEdgeTile( rasterEntry, fullResBound, params.level as Integer, params.row as Integer, params.col as Integer )
    def format = "image/jpeg"
    def transparent = false
    def ext = "jpg"
    def level = params.level as Integer
    if ( edgeTileFlag )
    {
      format = "image/png"
      transparent = true
      ext = "png"
    }
    Utility.simpleCaseInsensitiveBind( wmsRequest, [request: 'GetMap',
            layers: params.id,
            srs: 'EPSG:4326',
            format: format,
            service: 'wms',
            version: '1.1.1',
            width: tileSize.width,
            height: tileSize.height,
            transparent: transparent,
            bbox: "${tileBounds.minx},${tileBounds.miny},${tileBounds.maxx},${tileBounds.maxy}"] )
    def wmsMap = wmsRequest.toMap()
    Utility.removeEmptyParams( wmsMap )

    //def minLod = Math.sqrt(tileSize.width*tileSize.height)
    //def maxLod = minLod

    def subtiles = []
    if ( canSplit( tileBounds, rasterEntry.metersPerPixel ) )
    {
      subtiles = generateSubTiles( params, fullResBound )
    }
    def defaultName = "${params.level}/${params.col}/${params.row}.kml"
    def kmlnode = {
      mkp.xmlDeclaration()
      kml( "xmlns": "http://earth.google.com/kml/2.1" ) {
        Document() {
          name( defaultName )
          description()
          Style() {
            ListStyle( id: "hideChildren" ) {
              listItemType( "checkHideChildren" )
            }
          }
          Region() {
            Lod() {
              minLodPixels( lodValues.min )
              if ( subtiles.size() > 0 )
              {
                maxLodPixels( lodValues.max )
              }
              else
              {
                maxLodPixels( -1 )
              }
            }
            LatLonAltBox() {
              north( tileBounds.maxy )
              south( tileBounds.miny )
              east( tileBounds.maxx )
              west( tileBounds.minx )
            }
          }
          GroundOverlay() {
            drawOrder( params.level )
            Icon() {
              href {
                mkp.yieldUnescaped( """<![CDATA[${
                  appTagLib.createLink(
                          absolute: true, base: grailsApplication.config.omar.serverURL, controller: 'ogc',
                          action: 'wms', params: wmsMap )
                }]]>""" )
              }
              viewRefreshMode( "onExpire" )
            }
            LatLonBox() {
              north( tileBounds.maxy )
              south( tileBounds.miny )
              east( tileBounds.maxx )
              west( tileBounds.minx )
            }
          }
          subtiles.each {tile ->
            newParams.level = tile.level
            newParams.row = tile.row
            newParams.col = tile.col
            NetworkLink {
              name( "${tile.level}/${tile.col}/${tile.row}.${ext}" )
              Region {
                Lod {
                  minLodPixels( lodValues.min )
                  maxLodPixels( "-1" )
                }
                LatLonAltBox {
                  north( "${tile.maxy}" )
                  south( "${tile.miny}" )
                  east( "${tile.maxx}" )
                  west( "${tile.minx}" )
                }
              }
              Link {
                href {
                  mkp.yieldUnescaped( """<![CDATA[${
                    appTagLib.createLink( absolute: true, base: grailsApplication.config.omar.serverURL,
                            action: params.action, params: newParams )
                  }]]>""" )
                }
                viewRefreshMode( "onExpire" )
              }
            }
          }
        }
      }
    }

    kmlbuilder.bind( kmlnode ).toString()
  }

  def createTileKmzInfo( def rasterEntry, def params )
  {
    def fullResBound = createFullResBounds( rasterEntry )
    def kmlbuilder = new StreamingMarkupBuilder()
    kmlbuilder.encoding = "UTF-8"
    def tileBounds = tileBound( params, fullResBound )
    def wmsRequest = new WmsCommand()
    Utility.simpleCaseInsensitiveBind( wmsRequest, params )

    def newParams = new HashMap( params )
    newParams.remove( "action" )
    newParams.remove( "controller" )

    def edgeTileFlag = isAnEdgeTile( rasterEntry, fullResBound, params.level as Integer, params.row as Integer, params.col as Integer )
    def format = "image/jpeg"
    def transparent = false
    def ext = "jpg"
    if ( edgeTileFlag )
    {
      format = "image/png"
      transparent = true
      ext = "png"
    }
    Utility.simpleCaseInsensitiveBind( wmsRequest, [request: 'GetMap',
            layers: params.id,
            srs: 'EPSG:4326',
            format: format,
            request: "GetMap",
            version: "1.1.1",
            service: 'wms',
            width: tileSize.width,
            height: tileSize.height,
            transparent: transparent,
            bbox: "${tileBounds.minx},${tileBounds.miny},${tileBounds.maxx},${tileBounds.maxy}"] )
    def wmsMap = wmsRequest.toMap()
    Utility.removeEmptyParams( wmsMap )

    //def minLod = Math.sqrt(tileSize.width*tileSize.height)
    //def maxLod = minLod

    def subtiles = []
    if ( canSplit( tileBounds, rasterEntry.metersPerPixel ) )
    {
      subtiles = generateSubTiles( params, fullResBound )
    }
    def defaultName = "${params.level}/${params.col}/${params.row}.kml"
    def kmlnode = {
      mkp.xmlDeclaration()
      kml( "xmlns": "http://earth.google.com/kml/2.1" ) {
        Document() {
          name( defaultName )
          description()
          Style() {
            ListStyle( id: "hideChildren" ) {
              listItemType( "checkHideChildren" )
            }
          }
          Region() {
            Lod() {
              minLodPixels( lodValues.min )
              if ( subtiles.size() > 0 )
              {
                maxLodPixels( lodValues.max )
              }
              else
              {
                maxLodPixels( -1 )
              }
            }
            LatLonAltBox() {
              north( tileBounds.maxy )
              south( tileBounds.miny )
              east( tileBounds.maxx )
              west( tileBounds.minx )
            }
          }
          GroundOverlay() {
            drawOrder( params.level )
            Icon() {
              href { mkp.yieldUnescaped( "images/image.${ext}" ) }
//                      href{mkp.yieldUnescaped("<![CDATA[${appTagLib.createLink(absolute: true, controller: 'ogc', action: 'wms',params:wmsMap)}]]>")}
              viewRefreshMode( "onExpire" )
            }
            LatLonBox() {
              north( tileBounds.maxy )
              south( tileBounds.miny )
              east( tileBounds.maxx )
              west( tileBounds.minx )
            }
          }
          subtiles.each {tile ->
            newParams.level = tile.level
            newParams.row = tile.row
            newParams.col = tile.col
            NetworkLink {
              name( "${tile.level}/${tile.col}/${tile.row}.${ext}" )
              Region {
                Lod {
                  minLodPixels( lodValues.min )
                  maxLodPixels( "-1" )
                }
                LatLonAltBox {
                  north( "${tile.maxy}" )
                  south( "${tile.miny}" )
                  east( "${tile.maxx}" )
                  west( "${tile.minx}" )
                }
              }
              Link {
                href {
                  mkp.yieldUnescaped( """<![CDATA[${
                    appTagLib.createLink( absolute: true, base: grailsApplication.config.omar.serverURL,
                            action: params.action, params: newParams )
                  }]]>""" )
                }
                viewRefreshMode( "onExpire" )
              }
            }
          }
        }
      }
    }
    kmlbuilder.bind( kmlnode ).toString()
    def mapResult = [image: null, errorMessage: null]
    if ( !rasterEntry )
    {
      mapResult.image = new BufferedImage( tileSize.width, tileSize.height, BufferedImage.TYPE_INT_RGB )
    }
    else
    {
      mapResult = webMappingService.getMap( wmsRequest, [rasterEntry] )
    }

    [kml: kmlbuilder.bind( kmlnode ).toString(), image: mapResult.image, format: "${ext}", imagePath: "images/image.${ext}"]
  }

  def isAnEdgeTile( def rasterEntry, def fullResBbox, def level, def row, def col )//def level, def row, def col)
  {
    // we will consider edge tiles as all tiles overlapping the bounds of the raster entry
    //
    def rasterGeom = rasterEntry.groundGeom
    def tileGeometry = createPolygonFromTileBounds( tileBound( level, row, col, fullResBbox ) )

    def result = !rasterGeom.contains( tileGeometry )
    result
  }

  def getMetersPerPixel( def tileBounds, def fullResMetersPerPixel )
  {
    def deltax = ( tileBounds.maxx - tileBounds.minx )
    def deltay = ( tileBounds.maxy - tileBounds.miny )
    //def maxDelta = deltax>deltay?deltay:deltax
    // def maxTileSize = tileSize.width>tileSize.height?tileSize.width:tileSize.height
    //def metersPerPixel = (maxDelta*metersPerDegree)/maxTileSize
    def metersPerPixel = ( ( ( deltax * metersPerDegree ) / tileSize.width ) +
            ( ( deltay * metersPerDegree ) / tileSize.height ) ) * 0.5

    metersPerPixel
  }

  def canSplit( def tileBounds, def fullResMetersPerPixel )
  {
    def metersPerPixel = getMetersPerPixel( tileBounds, fullResMetersPerPixel )

    // keep splitting if we can zoom further
    metersPerPixel > fullResMetersPerPixel
  }

  def tileBound( def params, def fullResBbox )
  {
    tileBound( params.level ? params.level as Integer : 0,
            params.row ? params.row as Integer : 0,
            params.col ? params.col as Integer : 0,
            fullResBbox )
  }

  def tileBound( def level, def row, def col, def fullResBbox )
  {
    def minx = fullResBbox.minx
    def maxx = fullResBbox.maxx
    def miny = fullResBbox.miny
    def maxy = fullResBbox.maxy
    def deltax = ( maxx - minx ) / ( 2 ** level )
    def deltay = ( maxy - miny ) / ( 2 ** level )

    def llx = minx + deltax * col
    def lly = miny + deltay * row

    [minx: llx, miny: lly, maxx: ( llx + deltax ), maxy: ( lly + deltay )]
  }

  def generateSubTiles( def params, def fullResBbox )
  {
    def level = ( params.level as Integer ) + 1
    def row = params.row as Integer
    def col = params.col as Integer
    def nrow = row * 2
    def ncol = col * 2
    def minx = fullResBbox.minx
    def maxx = fullResBbox.maxx
    def miny = fullResBbox.miny
    def maxy = fullResBbox.maxy
    def deltax = ( maxx - minx ) / ( 2 ** level )
    def deltay = ( maxy - miny ) / ( 2 ** level )

    def llx = minx + deltax * ncol
    def lly = miny + deltay * nrow

    [[minx: llx, miny: lly, maxx: ( llx + deltax ), maxy: ( lly + deltay ), level: level, col: ncol, row: nrow],
            [minx: llx + deltax, miny: lly, maxx: ( llx + 2.0 * deltax ), maxy: ( lly + deltay ), level: level, col: ( ncol + 1 ), row: nrow],
            [minx: llx + deltax, miny: ( lly + deltay ), maxx: ( llx + 2.0 * deltax ), maxy: ( lly + 2.0 * deltay ), level: level, col: ( ncol + 1 ), row: ( nrow + 1 )],
            [minx: llx, miny: lly + deltay, maxx: ( llx + deltax ), maxy: ( lly + 2.0 * deltay ), level: level, col: ncol, row: ( nrow + 1 )]
    ]
  }

  void afterPropertiesSet( )
  {
    def gpt = new ossimGpt()
    def dpt = gpt.metersPerDegree()
    metersPerDegree = dpt.y
    dpt.delete()
    gpt.delete()
    dpt = null
    gpt = null
    tileSize = grailsApplication.config.superOverlay?.tileSize
    lodValues = grailsApplication.config.superOverlay?.lodPixel
    tileSize = tileSize ?: [width: 256, height: 256]
    def square = Math.sqrt( tileSize.width * tileSize.height )
    lodValues = lodValues ?: [min: square * 0.5, max: square * 8.0]
  }
}
