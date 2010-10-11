class OmarVideoGrailsPlugin {
    // the plugin version
    def version = "0.3"
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
    def title = "OMAR Video"
    def description = '''\\
OMAR plugin for video support
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/omar-video"

    def doWithWebDescriptor = { xml ->
        def filters = xml.filter
        def lastFilter = filters[-1]

        lastFilter + {
          'filter-mapping' {
            'filter-name'('gzipFilter')
            'url-pattern'('/videoDataSet/search')
          }
        }
        lastFilter + {
          'filter-mapping' {
            'filter-name'('gzipFilter')
            'url-pattern'('/videoDataSet/results')
          }
        }
    }

    def doWithSpring = {
        // TODO Implement runtime spring config (optional)
        videoInfoParser(org.ossim.omar.VideoInfoParser)
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
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
