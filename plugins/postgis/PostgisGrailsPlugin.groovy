import com.vividsolutions.jts.geom.Polygon
import com.vividsolutions.jts.geom.Coordinate
import com.vividsolutions.jts.geom.Geometry
import com.vividsolutions.jts.geom.GeometryFactory
import com.vividsolutions.jts.geom.PrecisionModel

import grails.orm.HibernateCriteriaBuilder
import org.hibernatespatial.criterion.SpatialRestrictions
import org.hibernatespatial.SpatialRelation

class PostgisGrailsPlugin
{
  def version = 0.14
  def dependsOn = [:]

  def author = "Scott Bortman"
  def authorEmail = "scott.bortman@gmail.com"
  def title = "PostGIS"
  def description = '''\
    Adding support for PostGIS datatypes in domain classes
    '''

  // URL to the plugin's documentation
  def documentation = "http://grails.org/Postgis+Plugin"

  def doWithSpring = {
    // TODO Implement runtime spring config (optional)
    customEditorRegistrar(org.ossim.postgis.CustomEditorRegistrar)
    pointEditor(com.vividsolutions.jts.geom.PointEditor)
  }

  def doWithApplicationContext = { applicationContext ->
    // TODO Implement post initialization spring config (optional)
  }

  def doWithWebDescriptor = { xml ->
    // TODO Implement additions to web.xml (optional)
  }

  def doWithDynamicMethods = { ctx ->
    // TODO Implement registering dynamic methods to classes (optional)
    Geometry.metaClass.getBounds {->
      def coords = delegate?.envelope?.coordinates

      def bounds = [
              minLon: coords[0].x,
              minLat: coords[0].y,
              maxLon: coords[2].x,
              maxLat: coords[2].y
      ]
      return bounds
    }

    HibernateCriteriaBuilder.metaClass.spatialRestriction = { int relation, String propertyName, Geometry value ->
      return addToCriteria(SpatialRestrictions.spatialRestriction(relation, propertyName, value))
    }

    HibernateCriteriaBuilder.metaClass.intersects = { String propertyName, Geometry value ->
      return spatialRestriction(SpatialRelation.INTERSECTS, propertyName, value)
    }

    /*

    Polygon.metaClass.'static'.createPolygon { minLon, minLat, maxLon, maxLat ->
      def geometryFactory = new GeometryFactory(new PrecisionModel(), 4326)

      minLon = Double.parseDouble(minLon)
      minLat = Double.parseDouble(minLat)
      maxLon = Double.parseDouble(maxLon)
      maxLat = Double.parseDouble(maxLat)

      def coords = [
        new Coordinate(minLon, minLat),
        new Coordinate(minLon, maxLat),
        new Coordinate(maxLon, maxLat),
        new Coordinate(maxLon, minLat),
        new Coordinate(minLon, minLat)
      ]

      def polygon = geometryFactory.createPolygon( geometryFactory.createLinearRing(coords), null)

      return polygon.toText()
    }
     */
  }

  def onChange = { event ->
    // TODO Implement code that is executed when any artefact that this plugin is
    // watching is modified and reloaded. The event contains: event.source,
    // event.application, event.manager, event.ctx, and event.plugin.
  }

  def onConfigChange = { event ->
    // TODO Implement code that is executed when the project configuration changes.
    // The event is the same as for 'onChange'.
  }
}
