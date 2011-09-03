import org.apache.ivy.plugins.latest.LatestTimeStrategy
import org.apache.ivy.plugins.resolver.FileSystemResolver

grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
//grails.project.war.file = "target/${appName}-${appVersion}.war"
grails.project.dependency.resolution = {
  // inherit Grails' default dependencies
  inherits("global") {
    // uncomment to disable ehcache
    // excludes 'ehcache'
  }
  log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
  repositories {
    grailsPlugins()
    grailsHome()
    grailsCentral()

    def localPlugins = new FileSystemResolver(name: 'my-local-repo')
    localPlugins.with {
      addArtifactPattern("${System.env['OMAR_HOME']}/plugins/grails-[artifact]-[revision].[ext]")
      settings = ivySettings
      latestStrategy = new LatestTimeStrategy()
      changingPattern = ".*SNAPSHOT"
      setCheckmodified(true)
    }
    resolver(localPlugins)

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
  }

  plugins {
    compile ":richui:0.8"
    compile ":joda-time:1.2"
    compile ":omar-core:0.1"
  }
}


grails.plugin.location."postgis" = "../../plugins/postgis"
grails.plugin.location."openlayers" = "../../plugins/openlayers"
grails.plugin.location."geoscript" = "../../plugins/geoscript"
grails.plugin.location.'omar-core' = "../../plugins/omar-core"
grails.plugin.location.'omar-oms' = "../../plugins/omar-oms"
grails.plugin.location.'omar-ogc' = "../../plugins/omar-ogc"
grails.plugin.location.'omar-stager' = "../../plugins/omar-stager"
grails.plugin.location.'omar-raster' = "../../plugins/omar-raster"
grails.plugin.location.'omar-video' = "../../plugins/omar-video"
grails.plugin.location.'omar-security-spring' = "../../plugins/omar-security-spring"
grails.plugin.location.'omar-rss' = "../../plugins/omar-rss"
//grails.plugin.location.'omar-scheduler' = "../../plugins/omar-scheduler"
grails.plugin.location.'omar-superoverlay' = "../../plugins/omar-superoverlay"
grails.plugin.location.'omar-rss' = "../../plugins/omar-rss"
//grails.plugin.location.'omar-mobile'="../../plugins/omar-mobile"

