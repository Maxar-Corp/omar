package org.ossim.omar.raster

import com.vividsolutions.jts.geom.LineString
import com.vividsolutions.jts.linearref.LinearIterator
import geoscript.GeoScript
import geoscript.filter.Filter
import geoscript.geom.Bounds
import geoscript.render.Map as GeoScriptMap
import geoscript.style.Composite
import geoscript.workspace.Workspace
import org.geotools.data.FeatureSource
import org.geotools.feature.FeatureIterator
import org.geotools.map.FeatureLayer
import org.ossim.omar.core.DateUtil
import org.ossim.omar.core.ISO8601DateParser

import java.awt.Graphics
import java.awt.Graphics2D
import java.text.SimpleDateFormat

import static geoscript.style.Symbolizers.*

import java.awt.AlphaComposite
import java.awt.BasicStroke

import java.awt.RenderingHints
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

import org.springframework.beans.factory.InitializingBean
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationContext

import org.geotools.geometry.jts.LiteShape

import com.vividsolutions.jts.geom.Polygon
import com.vividsolutions.jts.geom.MultiPolygon

import org.ossim.omar.core.ImageGenerator
import java.awt.Color


class DrawService implements ApplicationContextAware, InitializingBean
{
  static transactional = false

  ApplicationContext applicationContext
  def grailsApplication
  //def styles

  def dataSourceUnproxied

  def wmsToScreen(double minx, double miny, double maxx, double maxy, int imageWidth, int imageHeight)
  {
    // Extent width and height
    double extentWidth = maxx - minx
    double extentHeight = maxy - miny

    // Scale
    double scaleX = extentWidth > 0 ? imageWidth / extentWidth : java.lang.Double.MAX_VALUE
    double scaleY = extentHeight > 0 ? imageHeight / extentHeight : 1.0 as double


    double tx = -minx * scaleX
    double ty = ( miny * scaleY ) + ( imageHeight )

    // AffineTransform
    return new AffineTransform( scaleX, 0.0d, 0.0d, -scaleY, tx, ty )
  }


  def drawLayer(
      def layerName, def styleName,
      def params,
      def startDate, def endDate,
      def minx, def miny, def maxx, def maxy,
      def width, def height,
      def g2d)
  {
    layerName = layerName.replaceFirst( layerName[0], layerName[0].toLowerCase() )

    def style = null

    try
    {
      style = applicationContext.getBean( styleName )
    }
    catch ( Exception e )
    {
      style = new PropertyNameStyle( propertyName: 'id' )
    }

//    try
//    {
//      style = styles[styleName]
//    }
//    catch ( Exception e )
//    {
//      styleName = "default"
//      style = styles[styleName]
//    }

    def queryParams = applicationContext.getBean( "${layerName}QueryParam" )
    def searchService = applicationContext.getBean( "${layerName}SearchService" )

    queryParams.caseInsensitiveBind( params )

    queryParams.with {
      aoiMaxLat = maxy
      aoiMinLat = miny
      aoiMaxLon = maxx
      aoiMinLon = minx
    }

    queryParams.startDate = startDate
    queryParams.endDate = endDate

    def affine = wmsToScreen( minx, miny, maxx, maxy, width, height )

    def drawCount = 0;
    def closure = { feature ->
    //  println feature.class.name
      LiteShape shp = new LiteShape( feature.groundGeom, affine, false )

      def outlineColor = style?.getOutlineColor( feature[style?.propertyName] )
      /*
      def fillColor = style?.getFillColor( feature[style?.propertyName] )

      if ( fillColor && ( feature.groundGeom instanceof Polygon || feature.groundGeom instanceof MultiPolygon ) )
      {
        def isTransparent = (fillColor.transparency == Color.TRANSLUCENT)||
                            (fillColor.transparency == Color.BITMASK)
        g2d.color = fillColor
        g2d.composite = AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 1 )
        if(!(isTransparent&&
            (fillColor.alpha==0)))
        {
          g2d.fill( shp )
        }
      }
      */
      g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF )
      g2d.setRenderingHint( RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED )
      g2d.setRenderingHint( RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED )
      g2d.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR )
      g2d.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED )
      g2d.stroke = new BasicStroke( 1, /*style.width*/ )
      g2d.color = outlineColor
      g2d.draw( shp )
      ++drawCount;
    }


    def options = [
        max: grailsApplication.config.wms.vector.maxcount,
        fieldName: style.propertyName
    ]
    //searchService?.scrollGeometries( queryParams, pageParams, closure )
    //println "about to scroll fetures and draw"
    searchService?.scrollFeatures( queryParams, options, closure )
    //println "DONE fetures and draw"
  }

  byte[] drawLayers(def wmsRequest, def startDate, def endDate, def params)
  {
    def bytes = null
    def g2d = null

    try
    {
      def image = null
      def bounds = wmsRequest.bounds

      if ( wmsRequest.transparent )
      {
        image = new BufferedImage( bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB )
      }
      else
      {
        image = new BufferedImage( bounds.width, bounds.height, BufferedImage.TYPE_INT_RGB )
      }

      g2d = image.createGraphics()

      if ( wmsRequest.bgcolor )
      {
        g2d.setPaint( wmsRequest.backgroundColor )
        g2d.fillRect( 0, 0, bounds.width, bounds.height )
      }

      String[] layerNames = wmsRequest.layers?.split( "," )
      String[] styleNames = wmsRequest.styles?.split( "," )

      for ( def index in 0..<layerNames.size() )
      {
        drawLayer(
            layerNames[index], styleNames[index],

            params,

            startDate, endDate,

            bounds.minx, bounds.miny, bounds.maxx, bounds.maxy,
            bounds.width, bounds.height,

            g2d )
      }

      if ( ( wmsRequest.format == "image/gif" ) && wmsRequest.transparent )
      {
        image = ImageGenerator.convertRGBAToIndexed( image )
      }

      def formatName = wmsRequest.format?.split( "/" )?.last() ?: 'png'
      def ostream = new ByteArrayOutputStream()

      ImageIO.write( image, formatName, ostream )

      bytes = ostream.toByteArray()
    }
    catch ( Exception e )
    {
      log.error( "Exception OGC:FOOTPRINTS: ${e.message}" )
      e.printStackTrace()
    }

    if ( g2d )
    {
      g2d.dispose()
    }

    return bytes
  }


  void afterPropertiesSet()
  {
//    styles = grailsApplication.config.wms.styles
  }

/*
  def drawFootprints(GetMapRequest getMapRequest)
  {
    //println getMapRequest
    def ostream = new ByteArrayOutputStream()

//    def image = new BufferedImage( 256, 256, BufferedImage.TYPE_INT_ARGB )
//
//    ImageIO.write( image, 'png', ostream )

    def layerName = ( getMapRequest.layers == 'Imagery' ) ? 'raster_entry' : 'video_data_set'
    def dataSourceConfig = grailsApplication.config.dataSource
    def pattern = "jdbc:postgresql:(//(.*)/)?(.*)"
    def matcher = dataSourceConfig.url =~ pattern

    def dbParams = [
        dbtype: 'postgis',
        host: matcher[0][-2] ?: 'localhost',
        port: '5432',
        database: matcher[0][-1],
        user: dataSourceConfig.username,
        password: dataSourceConfig.password,
//        'Data Source': dataSourceUnproxied,
        'Expose primary keys': true
    ]

    //println dbParams

    def workspace = Workspace.getWorkspace( dbParams )
    def layer = workspace[layerName]

    def styleMap = grailsApplication.config.rasterEntry.styles.groupBy {
      "by${it.propertyName.capitalize()}".toString()
    }
    def outlineLookupTable = styleMap[getMapRequest.styles].first().outlineLookupTable

    def styles = outlineLookupTable.collect { k, v ->
      ( stroke( color: v ) + fill( opacity: 0.0 ) ).where( "file_type='${k}'" )
    }

    def x = outlineLookupTable.keySet().collect { "'${it}'" }.join( ',' )

    styles << ( stroke( color: '#000000' ) + fill( opacity: 0.0 ) ).where( "file_type not in (${x})" )

    def queryLayer = new QueryLayer( layer, styles as Composite )
    def bbox = new Bounds( *( getMapRequest.bbox.split( ',' )*.toDouble() ), getMapRequest.srs )
    def filter = Filter.bbox( 'ground_geom', bbox )

    if ( getMapRequest.filter )
    {
      filter = filter.and( getMapRequest.filter )
    }

    queryLayer.filter = filter

    def map = new GeoScriptMap(
        width: getMapRequest.width,
        height: getMapRequest.height,
        proj: bbox.proj,
        bounds: bbox,
        layers: [queryLayer]
    )

    map.render( ostream )
    map.close()
//    workspace.ds.dispose()
    workspace.close()

    [contentType: 'image/png', buffer: ostream.toByteArray()]
  }
  */

  def drawFootprints(GetMapRequest getMapRequest)
  {
   // println getMapRequest

    def ostream = new ByteArrayOutputStream()
    def dataSourceConfig = grailsApplication.config.dataSource
    def pattern = "jdbc:postgresql:(//([^:]+)(:(\\d+))?/)?(.*)"
    def matcher = dataSourceConfig.url =~ pattern

    def workspaceParams = [
        dbtype: 'postgis',
        user: dataSourceConfig.username,
        password: dataSourceConfig.password,
        host: matcher[0][2] ?: 'localhost',
        port: matcher[0][4] ?: '5432',
        database: matcher[0][5],
//        'Data Source': dataSourceUnproxied,
        'Expose primary keys': true
    ]
     String intervalFilter
     SimpleDateFormat sdf = DateUtil.findDateFormatter("yyyyMMdd'T'hh:mm:ss'Z'")
     def intervals = ISO8601DateParser.parseOgcTimeIntervals(getMapRequest.time)
     if(intervals)
     {
        intervals.each{interval->
           def startDate = new Date(interval.getStart().getMillis());
           def endDate   = new Date(interval.getEnd().getMillis());
           String intervalValue = "((acquisition_date >= '${sdf.format(startDate)}') AND (acquisition_date <= '${sdf.format(endDate)}'))"
           //startDate = DateUtil.setTimeZoneForDate(startDate, TimeZone.getTimeZone("UTC"))
           //endDate   = DateUtil.setTimeZoneForDate(endDate, TimeZone.getTimeZone("UTC"))

            if(intervalFilter) intervalFilter += "AND ${intervalValue}"
            else intervalFilter = intervalValue
        }
     }


     Workspace.withWorkspace( workspaceParams ) { workspace ->
      def layerName = ( getMapRequest.layers == 'Imagery' ) ? 'raster_entry' : 'video_data_set'
      def layer = workspace[layerName]
      def styleMap = grailsApplication.config.wms.styles[getMapRequest.styles]

      def style = styleMap.collect { k, v ->
        ( stroke( color: v.color ) + fill( opacity: 0.0 ) ).where( v.filter )
      } as Composite

      Bounds bounds = new Bounds( *( getMapRequest?.bbox?.split( ',' )*.toDouble() ), getMapRequest.srs )
      QueryLayer queryLayer = new QueryLayer( layer, style )
     // def filter = Filter.bbox( layer.schema.geom.name, bounds )
      //def filter = new Filter("INTERSECTS(${layer.schema.geom.name},POLYGON((${bounds.minX} ${bounds.minY}, ${bounds.minX bounds.maxY}, ${bounds.maxX bounds.maxY}, ${bounds.maxX bounds.minY}, ${bounds.minX bounds.minY})))")//.intersects( layer.schema.geom.name, bounds.geometry )
     def filter = Filter.intersects(layer.schema.geom.name, bounds.geometry)
      if ( getMapRequest.filter )
      {
         filter = filter.and( getMapRequest.filter )
      }

      filter = filter.and(intervalFilter)

     queryLayer.filter = filter

      def map = new GeoScriptMap(
          width: getMapRequest.width,
          height: getMapRequest.height,
          proj: bounds.proj,
          bounds: bounds,
          type: getMapRequest?.format?.split( '/' )?.last() ?: 'png',
          layers: [queryLayer]
      )

      map.render( ostream )
      map.close()

      workspace.close()
    }

    [contentType: 'image/png', buffer: ostream.toByteArray()]
  }
}