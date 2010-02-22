class PostgisGrailsPlugin {
    def version = 0.9
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
