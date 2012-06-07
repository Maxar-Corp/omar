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

    // runtime 'mysql:mysql-connector-java:5.1.13'
  }
  plugins {
    compile ":filterpane:2.0.1.1"
    runtime ":yui:2.8.2.1"
  }
}


grails.plugin.location.postgis = '../../plugins/postgis'
grails.plugin.location.geoscript = '../../plugins/geoscript'
grails.plugin.location.openlayers = '../../plugins/openlayers'
grails.plugin.location.geodata = '../../plugins/geodata'
//grails.plugin.location.omarSecuritySpring = '../../plugins/omar-security-spring'
grails.plugin.location.icodeAis = "${System.env['HOME']}/projects/icode-mda/OMAR/icode-ais"
