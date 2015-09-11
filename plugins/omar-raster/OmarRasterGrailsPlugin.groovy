import org.ossim.omar.raster.PropertyNameStyle
import org.ossim.omar.raster.RasterEntrySearchService
import org.ossim.omar.raster.RasterEntryQuery
import org.ossim.omar.raster.RasterInfoParser
import java.awt.Color

class OmarRasterGrailsPlugin {
    // the plugin version
    def version = "0.1"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.5 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    // TODO Fill in these fields
    def title = "Omar Raster Plugin" // Headline display name of the plugin
    def author = "Your name"
    def authorEmail = ""
    def description = '''\
Brief summary/description of the plugin.
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/omar-raster"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
//    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
//    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
//    def scm = [ url: "http://svn.codehaus.org/grails-plugins/" ]

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before
    }

    def doWithSpring = {
        rasterInfoParser( RasterInfoParser )

    //    imageDataQueryParam(org.ossim.omar.raster.RasterEntryQuery) { bean ->
        //      bean.singleton = false
        //    }


        imageryQueryParam( RasterEntryQuery ) { bean ->
          bean.singleton = false
        }        
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { ctx ->
        ctx.registerAlias( "imageryQueryParam", "imageDataQueryParam" )
        ctx.registerAlias( "rasterEntrySearchService", "imagerySearchService" )
        ctx.registerAlias( "rasterEntrySearchService", "imageDataSearchService" )

//        def styles = application.config?.rasterEntry?.styles
//
//        def beanNames = []
//        def beans = beans {
//          styles?.each { style ->
//            def beanName = "by${style?.propertyName.capitalize()}"
//            beanNames << beanName
//            "${beanName}"( PropertyNameStyle ) { bean ->
//              propertyName = style.propertyName
//              outlineLookupTable = style.outlineLookupTable
//              fillLookupTable = style.fillLookupTable ?: [:]
//              defaultFillColor = style.defaultFillColor ?: new Color( 0, 0, 0, 0 )
//              defaultOutlineColor = style.defaultOutlineColor ?: new Color( 255, 255, 255, 255 )
//            }
//          }
//        }
//        beanNames.each { beanName ->
//          ctx.registerBeanDefinition( beanName,
//              beans.getBeanDefinition( beanName ) )
//        }
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

    def onShutdown = { event ->
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}
