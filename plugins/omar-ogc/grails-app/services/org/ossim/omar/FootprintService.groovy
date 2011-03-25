package org.ossim.omar

import javax.imageio.ImageIO;

import geoscript.filter.Filter
import geoscript.geom.Bounds
import geoscript.layer.Layer
import geoscript.layer.Shapefile
import geoscript.map.Map as MapContext
import geoscript.proj.Projection
import geoscript.style.PolygonSymbolizer
import geoscript.style.Style
import geoscript.workspace.PostGIS
import geoscript.workspace.Workspace
import geoscript.workspace.Database

import org.geotools.data.Query
import org.geotools.jdbc.JDBCFeatureSource
import org.geotools.data.postgis.PostgisNGDataStoreFactory
import org.geotools.data.store.ContentEntry

import org.geotools.factory.CommonFactoryFinder

import org.apache.commons.collections.map.CaseInsensitiveMap

import grails.converters.JSON
import geoscript.workspace.Directory


class FootprintService
{

  static transactional = true

  def dataSource
  def grailsApplication

  def render(def params, def ostream)
  {
    def wmsGetMap = parseGetMap(params)
    def mapContext = null
    def layer = null
    def filter = null

    try
    {
      mapContext = createMapContext(wmsGetMap)
      filter = createFilter(wmsGetMap['filter'], mapContext.bounds)
      layer = createQueryLayer(wmsGetMap['layers'], filter)
//    layer = new Shapefile("/data/omar/world_adm0.shp")
      layer.style = createStyle(wmsGetMap['styles'])
      mapContext.addLayer(layer)
      renderToImageToStream(mapContext, ostream)
    }
    finally
    {
      mapContext?.close()
      layer?.workspace?.close()

    }
  }

  def createFilter(def filterText, def bounds)
  {
    def x = Filter.intersects("ground_geom", bounds.geometry)
    def y = new Filter(filterText)
    def filterFactory = CommonFactoryFinder.getFilterFactory(null)
    def filter = filterFactory.and(x.filter, y.filter)

    return new Filter(filter)
  }

  def createQueryLayer(def typeName, def filterText, def flag = true)
  {
    def workspace = createWorkspace(flag)

    return new QueryLayer(workspace, typeName, filterText)
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

  def createMapContext(def wmsGetMap)
  {
    def coords = wmsGetMap['bbox'].split(',').collect { it.toString().toDouble() }
    def proj = new Projection(wmsGetMap['srs'])
    def bounds = new Bounds(coords[0], coords[1], coords[2], coords[3], proj)

    def mapContext = new QueryMapContext(
            width: wmsGetMap['width']?.toInteger(),
            height: wmsGetMap['height']?.toInteger(),
            bounds: bounds,
            imageType: wmsGetMap['format'] - "image/"
    )

    if ( wmsGetMap['bgcolor'] )
    {
      mapContext.backgroundColor = wmsGetMap['bgcolor']
    }

    return mapContext
  }

  def createStyle(def styleJSON)
  {
    def jsonObject = JSON.parse(styleJSON)
    def styleMap = jsonObject.entrySet().inject([:]) { result, i -> result[i.key] = i.value; return result}

    return new Style(new PolygonSymbolizer(styleMap))
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
}
