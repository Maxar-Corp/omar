package org.ossim.omar.raster

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

    def queryParams = applicationContext.getBean( "${ layerName }QueryParam" )
    def searchService = applicationContext.getBean( "${ layerName }SearchService" )

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

//    def closure = { geom ->
//      LiteShape shp = new LiteShape( geom, affine, false )
//
//      def outlineColor = new Color( style.outlinecolor.r, style.outlinecolor.g, style.outlinecolor.b, style.outlinecolor.a )
//      def fillColor = ( style.fillcolor ) ? new Color( style.fillcolor.r, style.fillcolor.g, style.fillcolor.b, style.fillcolor.a ) : null
//
//      if ( fillColor && ( geom instanceof Polygon || geom instanceof MultiPolygon ) )
//      {
//        g2d.color = fillColor
//        g2d.composite = AlphaComposite.getInstance( AlphaComposite.SRC_OVER, new Float( 0.5 ).floatValue() )
//        g2d.fill( shp )
//      }
//
//      g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON )
//      g2d.stroke = new BasicStroke( style.width )
//      g2d.color = outlineColor
//      g2d.draw( shp )
//
//    }

    def closure = { feature ->
      LiteShape shp = new LiteShape( feature.groundGeom, affine, false )

      def outlineColor = style?.getOutlineColor( feature[style?.propertyName] )
      def fillColor = style?.getFillColor( feature[style?.propertyName] )

      if ( fillColor && ( feature.groundGeom instanceof Polygon || feature.groundGeom instanceof MultiPolygon ) )
      {
        g2d.color = fillColor
        g2d.composite = AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 1 )
        g2d.fill( shp )
      }

      g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON )
      g2d.stroke = new BasicStroke( 1, /*style.width*/ )
      g2d.color = outlineColor
      g2d.draw( shp )

    }


    def options = [
        max: grailsApplication.config.wms.vector.maxcount,
        fieldName: style.propertyName
    ]

    //searchService?.scrollGeometries( queryParams, pageParams, closure )
    searchService?.scrollFeatures( queryParams, options, closure )

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

      def formatName = wmsRequest.format?.split( "/" )[-1]
      def ostream = new ByteArrayOutputStream()

      ImageIO.write( image, formatName, ostream )

      bytes = ostream.toByteArray()
    }
    catch ( Exception e )
    {
      log.error( "Exception OGC:FOOTPRINTS: ${ e.message }" )
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
}
