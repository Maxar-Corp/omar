package geodata

import org.apache.commons.collections.map.CaseInsensitiveMap

import java.awt.image.BufferedImage
import javax.imageio.ImageIO

import geoscript.geom.Bounds
import geoscript.proj.Projection
import geoscript.proj.Projection
import geoscript.render.Draw
import geoscript.style.Shape
import geoscript.style.Stroke
import geoscript.style.Fill
import geoscript.style.Label
import geoscript.workspace.Database
import geoscript.workspace.PostGIS


import org.geotools.factory.Hints
import org.geotools.data.postgis.PostgisNGDataStoreFactory

import grails.converters.JSON

class CityMapService
{

  static transactional = false

  def grailsApplication
  def dataSource

  def getMap(def params, def response)
  {
    def wmsParams = new CaseInsensitiveMap(params)
    def mode = 'geoscript'
    def image

    def width = wmsParams['width']?.toInteger()
    def height = wmsParams['height']?.toInteger()
    def format = wmsParams['format']
    def bbox = wmsParams['bbox']?.split(',').collect { it?.toDouble() }
    def layers = wmsParams['layers']
    def srs = wmsParams['srs']
    def imageType = format?.split('/')[-1]
    def styles = wmsParams['styles']

    Hints.putSystemDefault(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE);

    switch ( mode )
    {
    case 'geoscript':
      def workspace = createWorkspace()
      def layer = workspace[layers]
      def proj = new Projection(srs)
      def bounds = new Bounds(bbox[0], bbox[1], bbox[2], bbox[3], proj)

      layer.style = createStyle(styles)
      image = Draw.drawToImage(layer, bounds, [width, height],)
      workspace?.close()
      break
    default:
      image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    }

    def ostream = response.outputStream

    response.contentType = format
    ImageIO.write(image, imageType, ostream)
    ostream.close()
  }

  def createStyle(def styleJSON)
  {
    def jsonObject = JSON.parse(styleJSON)
    def styleMap = jsonObject.entrySet().inject([:]) { result, i -> result[i.key] = i.value; return result}
    def style

    styleMap.each { k, v ->
      def tmp
      switch ( k )
      {
      case "fill":
        tmp = new Fill(v)
        break
      case "shape":
        tmp = new Shape(v)
        break
      case "stroke":
        tmp = new Stroke(v)
        break
      case "label":
        tmp = new Label(v)
        break
      }

      style = (!style) ? tmp : style + tmp
    }

    return style
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
}
