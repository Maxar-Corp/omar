grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir	= "target/test-reports"
//grails.project.war.file = "target/${appName}-${appVersion}.war"
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits( "global" ) {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {        
        grailsPlugins()
        grailsHome()
        grailsCentral()

        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        //mavenLocal()
        //mavenCentral()
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

        // runtime 'mysql:mysql-connector-java:5.1.5'
    }

}

grails.plugin.location.postgis="${System.getenv('OMAR_HOME')}/../../plugins/postgis"
grails.plugin.location.'omar-core'="${System.getenv('OMAR_HOME')}/../../plugins/omar-core"
grails.plugin.location.'omar-oms'="${System.getenv('OMAR_HOME')}/../../plugins/omar-oms"
grails.plugin.location.'omar-stager'="${System.getenv('OMAR_HOME')}/../../plugins/omar-stager"
grails.plugin.location.'omar-raster'="${System.getenv('OMAR_HOME')}/../../plugins/omar-raster"
grails.plugin.location.'omar-video'="${System.getenv('OMAR_HOME')}/../../plugins/omar-video"
grails.plugin.location.'omar-security'="${System.getenv('OMAR_HOME')}/../../plugins/omar-security"
grails.plugin.location.openlayers="${System.getenv('OMAR_HOME')}/../../plugins/openlayers"
grails.plugin.location.geoscript="${System.getenv('OMAR_HOME')}/../../plugins/geoscript"


