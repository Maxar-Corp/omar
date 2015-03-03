if ( System.getenv( 'GRAILS_CACHE_DIR' ) )
{
  grails.work.dir = "${System.env.GRAILS_CACHE_DIR}"
  grails.dependency.cache.dir = "${System.env.GRAILS_CACHE_DIR}/ivy-cache"
}
else
{
  grails.work.dir = "${System.env.OMAR_DEV_HOME}/.grails"
  grails.dependency.cache.dir = "${System.env.OMAR_DEV_HOME}/.grails/ivy-cache"
}

grails.servlet.version = "3.0" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

// uncomment (and adjust settings) to fork the JVM to isolate classpaths
//grails.project.fork = [
//   run: [maxMemory:1024, minMemory:64, debug:false, maxPerm:256]
//]

grails.project.dependency.resolution = {
  // inherit Grails' default dependencies
  inherits( "global" ) {
    // specify dependency exclusions here; for example, uncomment this to disable ehcache:
    // excludes 'ehcache'
  }
  log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
  checksums true // Whether to verify checksums on resolve
  legacyResolve false
  // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility

  repositories {
    inherits true // Whether to inherit repository definitions from plugins

    mavenRepo "http://repo.grails.org/grails/plugins/"

    grailsPlugins()
    grailsHome()
    //grailsCentral()

    mavenLocal()
    mavenCentral()

    // uncomment these (or add new ones) to enable remote dependency resolution from public Maven repositories
    //mavenRepo "http://snapshots.repository.codehaus.org"
    //mavenRepo "http://repository.codehaus.org"
    //mavenRepo "http://download.java.net/maven/2/"
    //mavenRepo "http://repository.jboss.com/maven2/"
  }

  dependencies {
    // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes e.g.

    // runtime 'mysql:mysql-connector-java:5.1.22'
  }

  plugins {
    runtime ":hibernate:$grailsVersion"
    runtime ":jquery:1.11.0.1"
    runtime ":resources:1.2.8"

    // Uncomment these (or add new ones) to enable additional resources capabilities
    //runtime ":zipped-resources:1.0"
    //runtime ":cached-resources:1.0"
    runtime ":yui-minify-resources:0.1.5"

    build ":tomcat:$grailsVersion"

    runtime ":database-migration:1.3.8"
    runtime ":p6spy:0.5"

    compile ':cache:1.1.1'
    compile ":piwik:0.1"
    compile ":rabbitmq-native:2.0.10"
  }
}

grails.plugin.location.omarRSS = "${System.env['OMAR_DEV_HOME']}/plugins/omar-rss"
grails.plugin.location.omarFederation = "${System.env['OMAR_DEV_HOME']}/plugins/omar-federation"
grails.plugin.location.omarChipper = "${System.env['OMAR_DEV_HOME']}/plugins/omar-chipper"
grails.plugin.location.omarOms = "${System.env['OMAR_DEV_HOME']}/plugins/omar-oms"



