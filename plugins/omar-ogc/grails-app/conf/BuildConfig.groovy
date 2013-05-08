grails.work.dir="${System.env.OMAR_DEV_HOME}/.grails"
grails.dependency.cache.dir = "${System.env.OMAR_DEV_HOME}/.grails/ivy-cache"

grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
//grails.project.war.file = "target/${appName}-${appVersion}.war"
grails.project.dependency.resolution = {
  // inherit Grails' default dependencies
  inherits( "global" ) {
    // uncomment to disable ehcache
    // excludes 'ehcache'
  }
  log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
  legacyResolve true // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility
  repositories {
    grailsPlugins()
    grailsHome()
    grailsCentral()

    // uncomment the below to enable remote dependency resolution
    // from public Maven repositories
    mavenLocal()
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
  plugins {
    //compile ":csv:0.3.1"
    //runtime ":yui-minify-resources:0.1.5"
  }
}

grails.plugin.location.geoscript = "${System.getenv( 'OMAR_DEV_HOME' )}/plugins/geoscript"
grails.plugin.location.omarCore = "${System.getenv( 'OMAR_DEV_HOME' )}/plugins/omar-core"
grails.plugin.location.postgis = "${System.getenv( 'OMAR_DEV_HOME' )}/plugins/postgis"

//grails.plugin.location.omarOms = '../../plugins/omar-oms'
//grails.plugin.location.omarSecuritySpring = '../../plugins/omar-security-spring'

//grails.plugin.location.omarRaster = '../../plugins/omar-raster'
//grails.plugin.location.omarVideo = '../../plugins/omar-video'
//grails.plugin.location.omarStager = '../../plugins/omar-stager'
