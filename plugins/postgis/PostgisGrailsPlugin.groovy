import com.vividsolutions.jts.geom.Geometry

import grails.orm.HibernateCriteriaBuilder
import org.hibernatespatial.criterion.SpatialRestrictions
import org.hibernatespatial.SpatialRelation

class PostgisGrailsPlugin
{
  def version = 0.16
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

      def coords = delegate?.coordinates
      def bounds = [
              minLon: Double.MAX_VALUE,
              maxLon: -Double.MAX_VALUE,
              minLat: Double.MAX_VALUE,
              maxLat: -Double.MAX_VALUE
      ]

      bounds.with {
        coords.each {coord ->
          if ( coord.x < minLon ) minLon = coord.x
          if ( coord.x > maxLon ) maxLon = coord.x
          if ( coord.y < minLat ) minLat = coord.y
          if ( coord.y > maxLat ) maxLat = coord.y
        }
      }

      return bounds
    }

    HibernateCriteriaBuilder.metaClass.spatialRestriction = { int relation, String propertyName, Geometry value ->
      return addToCriteria(SpatialRestrictions.spatialRestriction(relation, propertyName, value))
    }

    HibernateCriteriaBuilder.metaClass.intersects = { String propertyName, Geometry value ->
      return spatialRestriction(SpatialRelation.INTERSECTS, propertyName, value)
    }
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
