import org.apache.ivy.plugins.latest.LatestTimeStrategy
import org.apache.ivy.plugins.resolver.FileSystemResolver

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

//    def localPlugins = new FileSystemResolver(name: 'my-local-repo')
//    localPlugins.with {
//      addArtifactPattern("${System.env['OMAR_HOME']}/plugins/grails-[artifact]-[revision].[ext]")
//      settings = ivySettings
//      latestStrategy = new LatestTimeStrategy()
//      changingPattern = ".*SNAPSHOT"
//      setCheckmodified(true)
//    }
//    resolver(localPlugins)

    // uncomment the below to enable remote dependency resolution
    // from public Maven repositories
    mavenLocal()
    mavenCentral()
    //mavenRepo "http://snapshots.repository.codehaus.org"
    //mavenRepo "http://repository.codehaus.org"
    //mavenRepo "http://download.java.net/maven/2/"
    //mavenRepo "http://repository.jboss.com/maven2/"
  }
  dependencies {
    // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

    // runtime 'mysql:mysql-connector-java:5.1.5'
    compile 'org.apache.ant:ant:1.8.2'
    compile 'org.apache.ant:ant-launcher:1.8.2'
  }

  plugins {
    runtime ":p6spy:0.5"
    runtime ":yui:2.8.2.1"
    runtime ":resources:1.1.6"
    //   runtime ":zipped-resources:1.0"
    runtime ":yui-minify-resources:0.1.5"
    compile ":filterpane:2.0.1.1"
  }
}


grails.plugin.location.postgis = "../../plugins/postgis"
grails.plugin.location.openlayers = "../../plugins/openlayers"
grails.plugin.location.geoscript = "../../plugins/geoscript"

grails.plugin.location.omarCommonUi="../../plugins/omar-common-ui"

grails.plugin.location.omarCore = "../../plugins/omar-core"
grails.plugin.location.omarOms = "../../plugins/omar-oms"
grails.plugin.location.omarOgc = "../../plugins/omar-ogc"
grails.plugin.location.omarStager = "../../plugins/omar-stager"
grails.plugin.location.omarRaster = "../../plugins/omar-raster"
grails.plugin.location.omarVideo = "../../plugins/omar-video"
grails.plugin.location.omarSecuritySpring = "../../plugins/omar-security-spring"
grails.plugin.location.omarRss = "../../plugins/omar-rss"
//grails.plugin.location.omarScheduler = "../../plugins/omar-scheduler"
grails.plugin.location.omarSuperoverlay = "../../plugins/omar-superoverlay"
//grails.plugin.location.omarMobile="../../plugins/omar-mobile"

//grails.plugin.location.geodata = "../../plugins/geodata"


//grails.plugin.location.omarOgcCore="../../plugins/omar-ogc-core"
grails.plugin.location.omarImageMagick = "../../plugins/omar-image-magick"
