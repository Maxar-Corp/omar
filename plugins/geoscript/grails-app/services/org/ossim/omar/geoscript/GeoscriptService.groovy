package org.ossim.omar.geoscript

import javax.imageio.ImageIO
import geoscript.proj.Projection
import geoscript.geom.Bounds
import geoscript.filter.Filter
import geoscript.layer.Layer
import geoscript.render.Draw
import geoscript.workspace.PostGIS
import grails.converters.JSON
import geoscript.style.Fill
import geoscript.style.Shape
import geoscript.style.Stroke
import geoscript.style.Label
import geoscript.style.Halo
import geoscript.style.Font

class GeoscriptService
{
  def imageUtilService
  def grailsApplication

  def getMap( def params )
  {
    def format = params.get( 'format', 'image/png' )
    def width = params.get( 'width', 256 ) as int
    def height = params.get( 'height', 256 ) as int
    def imageType = format.split( '/' )[-1]
    def image

    def contentType
    def buffer = new ByteArrayOutputStream()

    def mode = 'GEOSCRIPT'

    //println params

    switch ( mode )
    {
    case 'BLANK':
      image = imageUtilService.createIndexedImage( width, height )
      contentType = format
      ImageIO.write( image, imageType, buffer )
      break
    case 'GEOSCRIPT':
      def (workspaceName, layerName) = params.get( 'layers' ).split( ':' )
      def workspace = createWorkspace( workspaceName )
      try
      {
        def layer = workspace[layerName]
        def (minX, minY, maxX, maxY) = params.get( 'bbox', '-180,-90,180,90' ).split( ',' ).collect {it as double}
        def proj = new Projection( params.get( 'srs', 'epsg:4326' ) )
        def bounds = new Bounds( minX, minY, maxX, maxY, proj )

        def styleJSON = params.get( 'styles' )
        def style
        if ( styleJSON )
        {
          style = createStyle( styleJSON )
        }

        contentType = format

        def geomField = layer.schema.geom
        def filter = Filter.intersects( geomField.name, bounds.geometry )
        def filterText = params.get( 'filter' )

        if ( filterText )
        {
          filter = filter.and( new Filter( filterText ) )

          def cursor = layer.getCursor( filter )
          def queryLayer = new Layer( cursor.col )

          queryLayer.style = style
          image = Draw.drawToImage( [bounds: bounds, size: [width, height], proj: proj, format: imageType], queryLayer )
          cursor.close()
        }
        else
        {
          layer.style = style
          image = Draw.drawToImage( [bounds: bounds, size: [width, height], proj: proj, format: imageType], layer )
        }

        image = imageUtilService.convertToIndexImage( image )
        ImageIO.write( image, imageType, buffer )
      }
      finally
      {
        workspace?.close()
      }

      break
    }

    [contentType, buffer.toByteArray()]
  }

  def createWorkspace( workspaceName )
  {
    def jdbcURL = grailsApplication.config.dataSource.url
    def matcher = jdbcURL =~ /jdbc:(\S+):(\/\/(\S+)(:(\d+))?\/)?(\S+)/
    def workspace

    if ( matcher.matches() )
    {
      def params = [
              host: matcher[0][3] ?: 'localhost',
              port: matcher[0][5] ?: '5432',
              user: grailsApplication.config.dataSource.username,
              password: grailsApplication.config.dataSource.password
      ]

      def database = matcher[0][6]

      workspace = new PostGIS( params, database )

    }

    return workspace
  }


  def createStyle( def styleJSON )
  {
    def jsonObject = JSON.parse( styleJSON )
    def styleMap = jsonObject.entrySet().inject( [:] ) { result, i -> result[i.key] = i.value; return result}
    def style

    styleMap.each { k, v ->
      def tmp
      switch ( k )
      {
      case "fill":
        tmp = new Fill( v )
        break
      case "shape":
        tmp = new Shape( v )
        break
      case "stroke":
        tmp = new Stroke( v )
        break
      case "label":
        tmp = new Label( v )
        break
      case "halo":
        tmp = new Halo( v )
        break
      case "font":
        tmp = new Font( v )
        break
      }

      style = ( !style ) ? tmp : style + tmp
    }

    return style
  }

}
