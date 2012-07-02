import org.ossim.omar.ogc.KmlService
import org.ossim.omar.ogc.OgcController

class OmarSiteGrailsPlugin {
    // the plugin version
    def version = "0.1"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.3.5 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

    // TODO Fill in these fields
    def author = "Your name"
    def authorEmail = ""
    def title = "Plugin summary/headline"
    def description = '''\\
Brief description of the plugin.
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/omar-site"

  def oldCreateImagesKml = null
  def kmlSiteService = null
    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before 
    }

    def doWithSpring = {
        // TODO Implement runtime spring config (optional)
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
        /*
      kmlSiteService = ctx.kmlSiteService
      oldCreateImagesKml  = KmlService.metaClass.getMetaMethod("createImagesKml", [List, Map, Map] as Class[])//, [KmlService] as Class[])
       KmlService.metaClass.createImagesKml = { List<org.ossim.omar.raster.RasterEntry> rasterEntries,
                                                Map wmsParams,
                                                Map params ->
         kmlSiteService.myCreateImages(rasterEntries, wmsParams, params)
         //oldCreateImagesKml.invoke(delegate, rasterEntries, wmsParams, params)
        }
        */
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
