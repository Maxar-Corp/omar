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
  legacyResolve true // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility
  log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
  repositories {
    grailsPlugins()
    grailsHome()
    grailsCentral()

    // uncomment the below to enable remote dependency resolution
    // from public Maven repositories
    mavenLocal()
    mavenCentral()
    //mavenRepo "http://snapshots.repository.codehaus.org"
    //mavenRepo "http://repository.codehaus.org"
    //mavenRepo "http://download.java.net/maven/2/"
    //mavenRepo "http://repository.jboss.com/maven2/"
  }

  plugins {
    compile ':spring-security-core:1.2.7.3'
    compile ':spring-security-ldap:1.0.6'
    compile ':csv:0.3.1'
    //compile ':filterpane:2.0.1.1'
    runtime ':mail:1.0.1'
  }

  dependencies {
    // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

    // runtime 'mysql:mysql-connector-java:5.1.13'
//    runtime "hsqldb:hsqldb:1.8.0.10"
  }
}

//grails.plugin.location.omarCommonUi = "${System.getenv( 'OMAR_DEV_HOME' )}/plugins/omar-common-ui"
grails.plugin.location.filterpane = "${System.getenv( 'OMAR_DEV_HOME' )}/plugins/filterpane"
