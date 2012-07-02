import geoscript.render.Map as GeoscriptMap
import geoscript.geom.Bounds
import org.geotools.map.FeatureLayer

import org.geotools.factory.Hints

class GeoscriptGrailsPlugin
{
  // the plugin version
  def version = "0.7"
  // the version or versions of Grails the plugin is designed for
  def grailsVersion = "1.2.2 > *"
  // the other plugins this plugin depends on
  def dependsOn = [:]
  // resources that are excluded from plugin packaging
  def pluginExcludes = [
          "grails-app/views/error.gsp"
  ]

  // TODO Fill in these fields
  def author = "Scott Bortman"
  def authorEmail = "sbortman@radiantblue.com"
  def title = "Adds Groovy Geoscript 0.98 support + WMS/WFS and Raster support"
  def description = '''\\
Brief description of the plugin.
'''

  // URL to the plugin's documentation
  def documentation = "http://grails.org/plugin/geoscript"

  def doWithWebDescriptor = { xml ->
    // TODO Implement additions to web.xml (optional), this event occurs before
  }

  def doWithSpring = {
    // TODO Implement runtime spring config (optional)
  }

  def doWithDynamicMethods = { ctx ->
    // TODO Implement registering dynamic methods to classes (optional)
    Hints.putSystemDefault( Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE )

    GeoscriptMap.metaClass.setUpRendering = {->

      // Add Layers
      layers.each {layer ->
        switch ( layer )
        {
        case geoscript.wms.WMSLayer:
          def mapLayer = new org.geotools.map.WMSLayer(layer.wms, layer.layer)
          context.addLayer(mapLayer)
          break
        case geoscript.layer.Layer:
          def mapLayer = new FeatureLayer(layer.fs, layer.style.style)
          context.addLayer(mapLayer)
          break
        case org.geotools.map.FeatureLayer:
          context.addLayer(layer)
          break
        }
      }
      // Set Bounds and Projections
      def b = getBounds()
      // If bounds is not set build it from all layers
      if ( b == null || b.empty )
      {
        layers.each {lyr ->
          if ( b == null || b.empty )
          {
            b = lyr.bounds
          }
          else
          {
            b.expand(lyr.bounds)
          }
        }
      }
      // Make sure that the Bounds has non 0 width and height
      // This covers points and horizontal/vertical lines
      b = b.ensureWidthAndHeight()
      // Fix the aspect ratio (or not)
      if ( fixAspectRatio )
      {
        b = fixAspectRatio(width, height, b)
      }
      // If the Bounds doesn't have a Projection, assume it is the same
      // Projection as the Map.  If the Map doesn't have a Projection
      // get if from the first Layer that has a Projection
      if ( b.proj == null )
      {
        def p = getProj()
        if ( p == null || p.crs == null )
        {
          layers.each {layer ->
            if ( layer.proj != null )
            {
              p = layer.proj
              setProj(p)
              return
            }
          }
        }
        b = new Bounds(b.minX, b.minY, b.maxX, b.maxY, p)
      }
      setBounds(b)
    }

    GeoscriptMap.metaClass.addLayer = { org.geotools.map.FeatureLayer mapLayer ->
      layers.add(mapLayer)
    }

    GeoscriptMap.metaClass.addLayer = { geoscript.wms.WMSLayer wmsLayer ->
      layers.add(wmsLayer)
    }

  }

  def doWithApplicationContext = { applicationContext ->
    // TODO Implement post initialization spring config (optional)
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
