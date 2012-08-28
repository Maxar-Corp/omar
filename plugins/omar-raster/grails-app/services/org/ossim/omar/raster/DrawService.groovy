package org.ossim.omar.raster

import java.awt.BasicStroke
import java.awt.Color
import java.awt.Composite
import java.awt.RenderingHints
import java.awt.geom.AffineTransform

import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationContext

import org.geotools.geometry.jts.LiteShape

import com.vividsolutions.jts.geom.Polygon
import com.vividsolutions.jts.geom.MultiPolygon


class DrawService implements ApplicationContextAware
{
  static transactional = false

  ApplicationContext applicationContext
  def grailsApplication


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
      def layer, def style,
      def params,
      def startDate, def endDate,
      def minx, def miny, def maxx, def maxy,
      def width, def height,
      def g2d)
  {
    layer = layer.replaceFirst( layer[0], layer[0].toLowerCase() )

    def queryParams = applicationContext.getBean( "${ layer }QueryParam" )
    def searchService = applicationContext.getBean( "${ layer }SearchService" )

    queryParams.caseInsensitiveBind( params )

    queryParams.with {
      aoiMaxLat = maxy
      aoiMinLat = miny
      aoiMaxLon = maxx
      aoiMinLon = minx
    }

    queryParams.startDate = startDate
    queryParams.endDate = endDate

    //println "HERE"


    def affine = wmsToScreen( minx, miny, maxx, maxy, width, height )


    g2d.color = new Color(
        style.outlinecolor.r as float,
        style.outlinecolor.g as float,
        style.outlinecolor.b as float,
        style.outlinecolor.a as float
    )

    Composite c = g2d.composite
    g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON )
    g2d.stroke = new BasicStroke( style.width )


    def closure = { geom ->
      LiteShape shp = new LiteShape( geom, affine, false )

      if ( style.fillcolor && ( geom instanceof Polygon || geom instanceof MultiPolygon ) )
      {
        g2d.color = new Color( style.fillcolor.r, style.fillcolor.g, style.fillcolor.b, style.fillcolor.a )
        g2d.composite = AlphaComposite.getInstance( AlphaComposite.SRC_OVER, new Float( 0.5 ).floatValue() )
        g2d.fill( shp )
      }

      g2d.composite = c
      g2d.color = new Color( style.outlinecolor.r, style.outlinecolor.g, style.outlinecolor.b, style.outlinecolor.a )
      g2d.draw( shp )

    }

    def pageParams = [
        max: grailsApplication.config.wms.vector.maxcount
    ]
    searchService?.scrollGeometries( queryParams, pageParams, closure )
  }

}
