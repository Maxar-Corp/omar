grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
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
  legacyResolve true // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility

  repositories {
    inherits true // Whether to inherit repository definitions from plugins

    grailsPlugins()
    grailsHome()
    grailsCentral()

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

    // runtime 'mysql:mysql-connector-java:5.1.20'
  }

  plugins {
    runtime ":hibernate:$grailsVersion"
    //runtime ":jquery:1.8.3"
    runtime ":resources:1.1.6"
    runtime ":p6spy:0.5"
    // Uncomment these (or add new ones) to enable additional resources capabilities
    //runtime ":zipped-resources:1.0"
    //runtime ":cached-resources:1.0"
    runtime ":yui-minify-resources:0.1.5"

    build ":tomcat:$grailsVersion"

    runtime ":database-migration:1.3.2"

    compile ':cache:1.0.1'
  }
}
grails.plugin.location.postgis = "${System.env['OMAR_DEV_HOME']}/plugins/postgis"
grails.plugin.location.openlayers = "${System.env['OMAR_DEV_HOME']}/plugins/openlayers"
grails.plugin.location.geoscript = "${System.env['OMAR_DEV_HOME']}/plugins/geoscript"
grails.plugin.location.omarCommonUi = "${System.env['OMAR_DEV_HOME']}/plugins/omar-common-ui"
grails.plugin.location.omarCore = "${System.env['OMAR_DEV_HOME']}/plugins/omar-core"
grails.plugin.location.omarFederation = "${System.env['OMAR_DEV_HOME']}/plugins/omar-federation"
grails.plugin.location.omarOms = "${System.env['OMAR_DEV_HOME']}/plugins/omar-oms"
grails.plugin.location.omarOgc = "${System.env['OMAR_DEV_HOME']}/plugins/omar-ogc"
grails.plugin.location.omarStager = "${System.env['OMAR_DEV_HOME']}/plugins/omar-stager"
grails.plugin.location.omarRaster = "${System.env['OMAR_DEV_HOME']}/plugins/omar-raster"
grails.plugin.location.omarVideo = "${System.env['OMAR_DEV_HOME']}/plugins/omar-video"
grails.plugin.location.omarSecuritySpring = "${System.env['OMAR_DEV_HOME']}/plugins/omar-security-spring"
grails.plugin.location.omarRss = "${System.env['OMAR_DEV_HOME']}/plugins/omar-rss"
//grails.plugin.location.omarScheduler = "${System.env['OMAR_DEV_HOME']}/plugins/omar-scheduler"
grails.plugin.location.omarSuperoverlay = "${System.env['OMAR_DEV_HOME']}/plugins/omar-superoverlay"
//grails.plugin.location.omarMobile="${System.env['OMAR_DEV_HOME']}/plugins/omar-mobile"

grails.plugin.location.geodata = "${System.env['OMAR_DEV_HOME']}/plugins/geodata"

//grails.plugin.location.omarOgcCore="${System.env['OMAR_DEV_HOME']}/plugins/omar-ogc-core"
grails.plugin.location.omarImageMagick = "${System.env['OMAR_DEV_HOME']}/plugins/omar-image-magick"
grails.plugin.location.omarTimeLapse = "${System.env['OMAR_DEV_HOME']}/plugins/omar-time-lapse"

grails.plugin.location.filterpane = "${System.env['OMAR_DEV_HOME']}/plugins/filterpane"
grails.plugin.location.icodeAis = "${System.env['OMAR_DEV_HOME']}/plugins/icode-ais"
