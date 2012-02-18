package org.ossim.omar.ogc

import javax.imageio.ImageIO;


import geoscript.filter.Filter
import geoscript.geom.Bounds
import geoscript.layer.Layer
import geoscript.layer.Shapefile
import geoscript.render.Map as GeoscriptMap
import geoscript.render.Draw

import geoscript.proj.Projection
import geoscript.style.Fill
import geoscript.style.Stroke
import geoscript.workspace.PostGIS
import geoscript.workspace.Workspace
import geoscript.workspace.Database

import org.geotools.data.Query
import org.geotools.jdbc.JDBCFeatureSource
import org.geotools.data.postgis.PostgisNGDataStoreFactory
import org.geotools.data.store.ContentEntry
import org.geotools.map.FeatureLayer

import org.geotools.factory.CommonFactoryFinder

import org.apache.commons.collections.map.CaseInsensitiveMap

import grails.converters.JSON
import geoscript.workspace.Directory
import java.awt.image.BufferedImage
import java.awt.Color
import org.ossim.omar.core.ImageGenerator


class FootprintService
{

  static transactional = false

  def dataSource
  def grailsApplication

  def createFilter(def filterText, def bounds)
  {
    def filter = Filter.intersects("ground_geom", bounds.geometry)

    if ( filterText )
    {
      filter = filter.and(new Filter(filterText))
    }

//    println filter.cql

    return filter
  }


  def createWorkspace(def flag = true)
  {
    def workspace = null

    if ( flag )
    {
      def jdbcParams = grailsApplication.config.dataSource

      def dbParams = [
              dbtype: "postgis",           //must be postgis
              user: jdbcParams.username,   //the user to connect with
              passwd: jdbcParams.password, //the password of the user.
              schema: "public"
      ]


      def pattern1 = "jdbc:(.*)://(.*):(.*)/(.*)"
      def pattern2 = "jdbc:(.*)://(.*)/(.*)"
      def pattern3 = "jdbc:(.*):(.*)"


      switch ( jdbcParams.url )
      {
      case ~pattern1:
        def matcher = (jdbcParams.url) =~ pattern1
        dbParams['host'] = matcher[0][2]
        dbParams['port'] = matcher[0][3]
        dbParams['database'] = matcher[0][4]
        break
      case ~pattern2:
        def matcher = (jdbcParams.url) =~ pattern2
        dbParams['host'] = matcher[0][2]
        dbParams['port'] = "5432"
        dbParams['database'] = matcher[0][3]
        break
      case ~pattern3:
        def matcher = (jdbcParams.url) =~ pattern3
        dbParams['host'] = "localhost"
        dbParams['port'] = "5432"
        dbParams['database'] = matcher[0][2]
        break
      }

      workspace = new PostGIS(
              dbParams['database'],
              dbParams['host'],
              dbParams['port'],
              dbParams['schema'],
              dbParams['user'],
              dbParams['passwd']
      )
    }
    else
    {
      def getDbParams = [(PostgisNGDataStoreFactory.DATASOURCE.key): dataSource]
      def dataStore = new PostgisNGDataStoreFactory().createDataStore(getDbParams)

      workspace = new Database(dataStore)
    }

    return workspace
  }

  def createStyle(def styleJSON)
  {
    def jsonObject = JSON.parse(styleJSON)
    def styleMap = jsonObject.entrySet().inject([:]) { result, i -> result[i.key] = i.value; return result}
    def style = new Fill(styleMap['fill']) + new Stroke(styleMap['stroke'])

    return style
  }

  def renderToImageToStream(def mapContext, def ostream)
  {
    if ( mapContext == "gif" )
    {
      def image = mapContext.renderToImage()
      //result = TransparentFilter.fixTransparency(new TransparentFilter(), result)
      image = ImageGenerator.convertRGBAToIndexed(image)
      ImageIO.write(image, "gif", ostream)
    }
    else
    {
      mapContext.render(ostream)
    }
  }

  def parseGetMap(def params)
  {
    def wmsGetMap = new CaseInsensitiveMap(params).subMap([
            'width',
            'height',
            'srs',
            'bbox',
            'format',
            'layers',
            'filter',
            'styles',
            'transparent',
            'version',
            'request',
            'service',
            'bgcolor',
            'exceptions',
            'time',
            'elevation'
    ])

    return wmsGetMap
  }


  def referenceMap(def params, def response)
  {
    synchronized ( this )
    {

      def wmsGetMap = parseGetMap(params)
      def mapContext = createMapContext(wmsGetMap)

      def workspace = new Directory(grailsApplication.config.wms.referenceDataDirectory)

      try
      {
        def style = createStyle(wmsGetMap['styles'])

        ["world_adm0", "statesp020"].each { layerName ->
          def layer = workspace[layerName]

          layer.style = style
          mapContext.addLayer(layer)
        }

        def ostream = response.outputStream

        response.contentType = wmsGetMap['format']
        //renderToImageToStream(mapContext, ostream)
        mapContext.render(ostream)
        ostream.flush()
        ostream.close()
      }
      finally
      {
        mapContext.close()
        workspace.close()
      }
    }
  }


  def render(def params, def ostream)
  {
    def wmsGetMap = parseGetMap(params)
    def writer = wmsGetMap['format']?.split('/')[-1] ?: 'png'
    def flag = "geoscript"

    switch ( flag )
    {
    case "foo":
      def start = System.currentTimeMillis()
      def workspace = new PostGIS([user: 'postgres', password: 'postgres'], 'omardb-1.8.12-prod')
      def srs = new Projection(wmsGetMap['srs'])
      def coords = wmsGetMap['bbox'].split(',').collect { it as double }
      def bbox = new Bounds(coords[0], coords[1], coords[2], coords[3], srs)
      def inputLayer = workspace[wmsGetMap['layers']]
      def schema = inputLayer.schema
      def filter = new Filter(wmsGetMap['filter']).and(Filter.intersects(schema.geom.name, bbox.geometry))
      def cursor = inputLayer.getCursor(filter)
      def outputLayer = new Layer(Layer.newname(), schema)
      def style = createStyle(wmsGetMap['styles'])
      def count = 0

      while ( cursor.hasNext() )
      {
        def feature = cursor.next()

        outputLayer.add(feature)
        count++
      }

      cursor.close()
      outputLayer.style = style
      Draw.draw(outputLayer, bbox, [wmsGetMap['width'] as int, wmsGetMap['height'] as int], ostream, writer)
      workspace.close()

      def stop = System.currentTimeMillis()

//      println "count: ${count}"
//      println "elapsed: ${stop - start}ms"
      break
    case "geoscript":
      def coords = wmsGetMap['bbox']?.split(',').collect { it as double }
      def bbox = new Bounds(coords[0], coords[1], coords[2], coords[3])
      def width = wmsGetMap['width'] as int
      def height = wmsGetMap['height'] as int
      def srs = new Projection(wmsGetMap['srs'])

      def mapParams = [
              width: width,
              height: height,
              type: writer,
              proj: srs,
              bounds: bbox
      ]

      def mapContext = new GeoscriptMap(mapParams)
      def workspace = createWorkspace(true)
      def layer = workspace[wmsGetMap['layers']]
      def style = createStyle(wmsGetMap['styles'])
      def filter = createFilter(wmsGetMap['filter'], bbox)
      def query = new Query(wmsGetMap['layers'], filter.filter)
      def mapLayer = new FeatureLayer(layer.fs, style.style)

      query.maxFeatures = 10000
      mapLayer.query = query
      mapContext.addLayer(mapLayer)

      def image = mapContext.renderToImage()

      if ( wmsGetMap['format'] == "image/gif" && wmsGetMap['transparent']?.toLowerCase() == "true" )
      {
        image = ImageGenerator.convertRGBAToIndexed(image)
      }

      ImageIO.write(image, writer, ostream)

      //mapContext.render(ostream)
      mapContext.close()
      workspace.close()
      break
    default:
      def image = new BufferedImage(wmsGetMap['width'] as int, wmsGetMap['height'] as int, BufferedImage.TYPE_INT_ARGB)
      def g2d = image.graphics

      g2d.color = Color.yellow
      g2d.drawRect(0, 0, image.width, image.height)
      g2d.dispose()
      ImageIO.write(image, writer, ostream)
    }
  }

}
