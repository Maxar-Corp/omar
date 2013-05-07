grails.work.dir="${System.env.OMAR_DEV_HOME}/.grails"
grails.dependency.cache.dir = "${System.env.OMAR_DEV_HOME}/.grails/ivy-cache"

grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.project.dependency.resolution = {
  // inherit Grails' default dependencies
  inherits( "global" ) {
    // uncomment to disable ehcache
    // excludes 'ehcache'
  }
  log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
  repositories {
    grailsCentral()
    // uncomment the below to enable remote dependency resolution
    // from public Maven repositories
    mavenCentral()
    mavenLocal()
    //mavenRepo "http://snapshots.repository.codehaus.org"
    //mavenRepo "http://repository.codehaus.org"
    //mavenRepo "http://download.java.net/maven/2/"
    //mavenRepo "http://repository.jboss.com/maven2/"
  }
  dependencies {
    // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

    // runtime 'mysql:mysql-connector-java:5.1.5'
    compile 'org.igniterealtime.smack:smack:3.2.1'
    compile 'org.igniterealtime.smack:smackx:3.2.1'
  }

  plugins {
    runtime ":hibernate:$grailsVersion"
    runtime ":resources:1.1.6"
  }
}

grails.plugin.location.openlayers = "${System.getenv( 'OMAR_DEV_HOME' )}/plugins/openlayers"
grails.plugin.location.omarCore = "${System.getenv( 'OMAR_DEV_HOME' )}/plugins/omar-core"
grails.plugin.location.omarCommonUi = "${System.getenv( 'OMAR_DEV_HOME' )}/plugins/omar-common-ui"
