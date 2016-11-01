grails.servlet.version = "3.0" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.work.dir = "target/work"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
grails.enable.native2ascii = false
//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.project.fork = [
    // configure settings for compilation JVM, note that if you alter the Groovy version forked compilation is required
    //  compile: [maxMemory: 256, minMemory: 64, debug: false, maxPerm: 256, daemon:true],

    // configure settings for the test-app JVM, uses the daemon by default
    test: [maxMemory: 1024, minMemory: 256, debug: false, maxPerm: 256, daemon: true],
    // configure settings for the run-app JVM
    run: [maxMemory: 1024, minMemory: 256, debug: false, maxPerm: 256, forkReserve: false, jvmArgs: [
	"-Djava.library.path=${System.getenv('OSSIM_INSTALL_PREFIX')}/lib64" as String
	]],
    // configure settings for the run-war JVM
    war: [maxMemory: 1024, minMemory: 256, debug: false, maxPerm: 256, forkReserve: false],
    // configure settings for the Console UI JVM
    console: [maxMemory: 1024, minMemory: 256, debug: false, maxPerm: 256]
]

grails.project.dependency.resolver = "maven" // or ivy
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

    mavenLocal( System.getenv( 'MAVEN_REPO' ) )
    if(System.env.OSSIM_MAVEN_PROXY) mavenRepo ( System.env.OSSIM_MAVEN_PROXY)
    grailsPlugins()
    grailsHome()
    grailsCentral()
    mavenCentral()      

    // uncomment these (or add new ones) to enable remote dependency resolution from public Maven repositories
    //mavenRepo "http://repository.codehaus.org"
    //mavenRepo "http://download.java.net/maven/2/"
    //mavenRepo "http://repository.jboss.com/maven2/"
  }

  dependencies {
    // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes e.g.
    // runtime 'mysql:mysql-connector-java:5.1.29'
    // runtime 'org.postgresql:postgresql:9.3-1101-jdbc41'
    runtime 'jline:jline:2.12'
    test "org.grails:grails-datastore-test-support:1.0.2-grails-2.4"
  }

  plugins {
    // plugins for the build system only
    build ":tomcat:7.0.55.2" // or ":tomcat:8.0.20"

    // plugins for the compile step
    compile ":scaffolding:2.1.2"
    compile ':cache:1.1.8'
    compile ":asset-pipeline:2.6.1"
    compile ":rabbitmq-native:2.0.10"

    // plugins needed at runtime but not for compilation
    // runtime ":hibernate4:4.3.8.1" // or ":hibernate:3.6.10.18"
    runtime ":hibernate4:4.3.5.5" //  <---- This version needed for SpringSecurity RequestMap to work
    runtime ":database-migration:1.4.0"
    runtime ":jquery:1.11.1"
    compile ":piwik:0.1"

    runtime ":cors:1.1.8"

    compile "org.grails.plugins:standalone:1.3"

    // Uncomment these to enable additional asset-pipeline capabilities
    //compile ":sass-asset-pipeline:1.9.0"
    //compile ":less-asset-pipeline:1.10.0"
    //compile ":coffee-asset-pipeline:1.8.0"
    //compile ":handlebars-asset-pipeline:1.3.0.3"
  }
}

grails.plugin.location.postgis = "${System.getenv( 'OMAR_DEV_HOME' )}/plugins/postgis"
grails.plugin.location.geoscript = "${System.getenv( 'OMAR_DEV_HOME' )}/plugins/geoscript"
grails.plugin.location.omarChipper = "${System.getenv( 'OMAR_DEV_HOME' )}/plugins/omar-chipper"
grails.plugin.location.omarCommonUI = "${System.getenv( 'OMAR_DEV_HOME' )}/plugins/omar-common-ui"
grails.plugin.location.omarCore = "${System.getenv( 'OMAR_DEV_HOME' )}/plugins/omar-core"
grails.plugin.location.omarFederation = "${System.getenv( 'OMAR_DEV_HOME' )}/plugins/omar-federation"
grails.plugin.location.omarImageMagick = "${System.getenv( 'OMAR_DEV_HOME' )}/plugins/omar-image-magick"
grails.plugin.location.omarOGC = "${System.getenv( 'OMAR_DEV_HOME' )}/plugins/omar-ogc"
grails.plugin.location.omarOMS = "${System.getenv( 'OMAR_DEV_HOME' )}/plugins/omar-oms"
grails.plugin.location.omarRaster = "${System.getenv( 'OMAR_DEV_HOME' )}/plugins/omar-raster"
grails.plugin.location.omarRSS = "${System.getenv( 'OMAR_DEV_HOME' )}/plugins/omar-rss"
grails.plugin.location.omarSecuritySpring = "${System.getenv( 'OMAR_DEV_HOME' )}/plugins/omar-security-spring"
grails.plugin.location.omarStager = "${System.getenv( 'OMAR_DEV_HOME' )}/plugins/omar-stager"
//grails.plugin.location.omarSuperOverlay = "${System.getenv( 'OMAR_DEV_HOME' )}/plugins/omar-superoverlay"
//grails.plugin.location.omarTimeLapse = "${System.getenv( 'OMAR_DEV_HOME' )}/plugins/omar-timelapse"
grails.plugin.location.omarVideo = "${System.getenv( 'OMAR_DEV_HOME' )}/plugins/omar-video"
grails.plugin.location.openlayers = "${System.getenv( 'OMAR_DEV_HOME' )}/plugins/openlayers"
grails.plugin.location.omarSuperOverlay = "${System.getenv( 'OMAR_DEV_HOME' )}/plugins/omar-superoverlay"
grails.plugin.location.omarOpensearch = "${System.getenv( 'OMAR_DEV_HOME' )}/plugins/omar-opensearch"

