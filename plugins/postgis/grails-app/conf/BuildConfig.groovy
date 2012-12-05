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
    mavenRepo "http://download.osgeo.org/webdav/geotools"
    mavenRepo "http://www.hibernatespatial.org/repository"
  }
  dependencies {
    // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

    // runtime 'mysql:mysql-connector-java:5.1.5'

    compile(
        'org.hibernatespatial:hibernate-spatial-postgis:1.1.1',
        'org.hibernatespatial:hibernate-spatial:1.1.1',
        'com.vividsolutions:jts:1.12',
        'postgresql:postgresql:9.1-901.jdbc4',
        'org.postgis:postgis-jdbc:1.5.2'

    ) {
      transitive = false
    }
  }

}

