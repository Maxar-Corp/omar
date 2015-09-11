import org.ossim.omar.core.MapPropertyEditor
import org.ossim.omar.core.ISO8601DateParser
import org.ossim.omar.core.CustomEditorRegistrar
import org.ossim.omar.core.DbAppender
import org.ossim.omar.core.XmlParserPool

class OmarCoreGrailsPlugin {
    // the plugin version
    def version = "0.1"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.5 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    // TODO Fill in these fields
    def title = "Omar Core Plugin" // Headline display name of the plugin
    def author = "Your name"
    def authorEmail = ""
    def description = '''\
Brief summary/description of the plugin.
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/omar-core"

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
      coreUtility( org.ossim.omar.core.Utility )

      customEditorRegistrar( CustomEditorRegistrar )
      mapPropertyEditor( MapPropertyEditor )

      wmsLoggingAppender( DbAppender ) {
        tableMapping = [
            width: ":width",
            height: ":height",
            layers: ":layers",
            styles: ":styles",
            format: ":format",
            request: ":request",
            bbox: ":bbox",
            internal_time: ":internalTime",
            render_time: ":renderTime",
            total_time: ":totalTime",
            start_date: ":startDate",
            end_date: ":endDate",
            user_name: ":userName",
            ip: ":ip",
            url: ":url",
            mean_gsd: ":meanGsd",
            geometry: "ST_GeomFromText(:geometry, 4326)"
        ]
        tableName = "wms_log"
      }
      getTileLoggingAppender( DbAppender ) {
        tableMapping = [
            x:":x",
            y:":y",
            width:":width",
            height:":height",
            format:":format",
            id:":id",
            scale:":scale",
            internalTime:":internalTime",
            renderTime:":renderTime",
            totalTime:":totalTime",
            startDate:":startDate",
            endDate:":endDate",
            userName:":userName",
            ip:":ip",
            url:":url"
        ]
        tableName = "get_tile_log"
      }

      parserPool( XmlParserPool, 32 )
  }

    def doWithDynamicMethods = { ctx ->
        String.metaClass.toDateTime {->
            ISO8601DateParser.parseDateTime( delegate )
          }
          String.metaClass.toDate {->

            def date = null
            def dateTime = ISO8601DateParser.parseDateTime( delegate.trim() )
            if ( dateTime )
            {
              date = new Date( dateTime.millis )
            }

            date
          }    
    }

    def doWithApplicationContext = { ctx ->
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

    def onShutdown = { event ->
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}
