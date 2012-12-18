import org.apache.ivy.plugins.latest.LatestTimeStrategy
import org.apache.ivy.plugins.resolver.FileSystemResolver

grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
//grails.project.war.file = "target/${appName}-${appVersion}.war"
grails.project.dependency.resolution = {
  checksums false

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
    mavenRepo "http://download.java.net/maven/2/"
    //mavenRepo "http://repository.jboss.com/maven2/"
    mavenRepo "http://repo.opengeo.org"
    mavenRepo "http://download.osgeo.org/webdav/geotools"
  }
  dependencies {
    // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

    // runtime 'mysql:mysql-connector-java:5.1.5'
    compile(
        'bouncycastle:bcmail-jdk14:138',
        'bouncycastle:bcprov-jdk14:138',
        'com.googlecode.json-simple:json-simple:1.1',
        'com.lowagie:itext:2.1.7',
        'com.miglayout:miglayout-swing:4.2',
        'com.vividsolutions:jts:1.13',
        'commons-httpclient:commons-httpclient:3.1',
        'commons-jxpath:commons-jxpath:1.3',
        'hsqldb:hsqldb:1.8.0.7',
        'it.geosolutions.imageio-ext:imageio-ext-gdal-bindings:1.7',
        'it.geosolutions.imageio-ext:imageio-ext-gdalarcbinarygrid:1.1.5',
        'it.geosolutions.imageio-ext:imageio-ext-gdaldted:1.1.5',
        'it.geosolutions.imageio-ext:imageio-ext-gdalecw:1.1.5',
        'it.geosolutions.imageio-ext:imageio-ext-gdalecwjp2:1.1.5',
        'it.geosolutions.imageio-ext:imageio-ext-gdalehdr:1.1.5',
        'it.geosolutions.imageio-ext:imageio-ext-gdalenvihdr:1.1.5',
        'it.geosolutions.imageio-ext:imageio-ext-gdalerdasimg:1.1.5',
        'it.geosolutions.imageio-ext:imageio-ext-gdalframework:1.1.5',
        'it.geosolutions.imageio-ext:imageio-ext-gdalidrisi:1.1.5',
        'it.geosolutions.imageio-ext:imageio-ext-gdalkakadujp2:1.1.5',
        'it.geosolutions.imageio-ext:imageio-ext-gdalmrsid:1.1.5',
        'it.geosolutions.imageio-ext:imageio-ext-gdalmrsidjp2:1.1.5',
        'it.geosolutions.imageio-ext:imageio-ext-gdalnitf:1.1.5',
        'it.geosolutions.imageio-ext:imageio-ext-gdalrpftoc:1.1.5',
        'it.geosolutions.imageio-ext:imageio-ext-geocore:1.1.5',
        'it.geosolutions.imageio-ext:imageio-ext-imagereadmt:1.1.5',
        'it.geosolutions.imageio-ext:imageio-ext-streams:1.1.5',
        'it.geosolutions.imageio-ext:imageio-ext-tiff:1.1.5',
        'it.geosolutions.imageio-ext:imageio-ext-utilities:1.1.5',
        'java3d:vecmath:1.3.2',
        'javax.media:jai_codec:1.1.3',
        'javax.media:jai_core:1.1.3',
        'javax.media:jai_imageio:1.1',
        'jdom:jdom:1.0',
        'jfree:eastwood:1.1.1-20090908',
        'jfree:jcommon:1.0.13',
        'jfree:jfreechart:1.0.10',
        'jgridshift:jgridshift:1.0',
        'mysql:mysql-connector-java:5.1.17',
        'net.java.dev.jsr-275:jsr-275:1.0-beta-2',
        'net.sf.opencsv:opencsv:2.0',
        'net.sourceforge.hatbox:hatbox:1.0.b7',
        'org.apache.avalon.framework:avalon-framework-api:4.3.1',
        'org.apache.avalon.framework:avalon-framework-impl:4.3.1',
        'org.apache.xmlgraphics:batik-anim:1.7',
        'org.apache.xmlgraphics:batik-awt-util:1.7',
        'org.apache.xmlgraphics:batik-bridge:1.7',
        'org.apache.xmlgraphics:batik-css:1.7',
        'org.apache.xmlgraphics:batik-dom:1.7',
        'org.apache.xmlgraphics:batik-ext:1.7',
        'org.apache.xmlgraphics:batik-gvt:1.7',
//        'org.apache.xmlgraphics:batik-js:1.7',   // causes problems w/minify.   Both included version of mozilla javascript
        'org.apache.xmlgraphics:batik-parser:1.7',
        'org.apache.xmlgraphics:batik-script:1.7',
        'org.apache.xmlgraphics:batik-svg-dom:1.7',
        'org.apache.xmlgraphics:batik-svggen:1.7',
        'org.apache.xmlgraphics:batik-transcoder:1.7',
        'org.apache.xmlgraphics:batik-util:1.7',
        'org.apache.xmlgraphics:batik-xml:1.7',
        'org.apache.xmlgraphics:fop:0.94',
        'org.apache.xmlgraphics:xmlgraphics-commons:1.2',
        'org.bouncycastle:bcmail-jdk14:1.38',
        'org.bouncycastle:bcprov-jdk14:1.38',
        'org.bouncycastle:bctsp-jdk14:1.38',
        'org.codehaus.janino:janino:2.5.16',
        'org.eclipse.emf:common:2.6.0',
        'org.eclipse.emf:ecore:2.6.1',
        'org.eclipse.xsd:xsd:2.6.0',
        'org.geoscript:geocss_2.9.1:0.7.4',
        'org.geoscript:geoscript-groovy:1.0-SNAPSHOT',
        'org.geotools:gt-api:9-SNAPSHOT',
        'org.geotools:gt-brewer:9-SNAPSHOT',
        'org.geotools:gt-charts:9-SNAPSHOT',
        'org.geotools:gt-coverage:9-SNAPSHOT',
        'org.geotools:gt-cql:9-SNAPSHOT',
        'org.geotools:gt-data:9-SNAPSHOT',
        'org.geotools:gt-epsg-hsql:9-SNAPSHOT',
        'org.geotools:gt-geojson:9-SNAPSHOT',
        'org.geotools:gt-geotiff:9-SNAPSHOT',
        'org.geotools:gt-graph:9-SNAPSHOT',
        'org.geotools:gt-grid:9-SNAPSHOT',
        'org.geotools:gt-image:9-SNAPSHOT',
        'org.geotools:gt-imageio-ext-gdal:9-SNAPSHOT',
        'org.geotools:gt-imagemosaic:9-SNAPSHOT',
        'org.geotools:gt-jdbc:9-SNAPSHOT',
        'org.geotools:gt-main:9-SNAPSHOT',
        'org.geotools:gt-metadata:9-SNAPSHOT',
        'org.geotools:gt-opengis:9-SNAPSHOT',
        'org.geotools:gt-process:9-SNAPSHOT',
        'org.geotools:gt-process-feature:9-SNAPSHOT',
        'org.geotools:gt-process-geometry:9-SNAPSHOT',
        'org.geotools:gt-process-raster:9-SNAPSHOT',
        'org.geotools:gt-property:9-SNAPSHOT',
        'org.geotools:gt-referencing:9-SNAPSHOT',
        'org.geotools:gt-render:9-SNAPSHOT',
        'org.geotools:gt-shapefile:9-SNAPSHOT',
        'org.geotools:gt-svg:9-SNAPSHOT',
        'org.geotools:gt-swing:9-SNAPSHOT',
        'org.geotools:gt-wfs:9-SNAPSHOT',
        'org.geotools:gt-wms:9-SNAPSHOT',
        'org.geotools:gt-xml:9-SNAPSHOT',
        'org.geotools.jdbc:gt-jdbc-h2:9-SNAPSHOT',
        'org.geotools.jdbc:gt-jdbc-mysql:9-SNAPSHOT',
        'org.geotools.jdbc:gt-jdbc-postgis:9-SNAPSHOT',
        'org.geotools.jdbc:gt-jdbc-spatialite:9-SNAPSHOT',
        'org.geotools.ogc:net.opengis.fes:9-SNAPSHOT',
        'org.geotools.ogc:net.opengis.ows:9-SNAPSHOT',
        'org.geotools.ogc:net.opengis.wfs:9-SNAPSHOT',
        'org.geotools.ogc:org.w3.xlink:9-SNAPSHOT',
        'org.geotools.xsd:gt-xsd-core:9-SNAPSHOT',
        'org.geotools.xsd:gt-xsd-fes:9-SNAPSHOT',
        'org.geotools.xsd:gt-xsd-filter:9-SNAPSHOT',
        'org.geotools.xsd:gt-xsd-gml2:9-SNAPSHOT',
        'org.geotools.xsd:gt-xsd-gml3:9-SNAPSHOT',
        'org.geotools.xsd:gt-xsd-ows:9-SNAPSHOT',
        'org.geotools.xsd:gt-xsd-wfs:9-SNAPSHOT',
        'org.jaitools:jt-attributeop:1.3.0',
        'org.jaitools:jt-contour:1.3.0',
        'org.jaitools:jt-jiffle-language:0.2.0',
        'org.jaitools:jt-jiffleop:0.2.0',
        'org.jaitools:jt-rangelookup:1.3.0',
        'org.jaitools:jt-utils:1.3.0',
        'org.jaitools:jt-vectorbinarize:1.3.0',
        'org.jaitools:jt-vectorize:1.3.0',
        'org.jaitools:jt-zonalstats:1.3.0',
        'org.json:json:20090211',
        'org.opengeo:geodb:0.7-RC2',
        'org.scala-lang:scala-library:2.9.1',
        'picocontainer:picocontainer:1.2',
        'postgresql:postgresql:8.4-701.jdbc3',
        'xalan:xalan:2.6.0',
        'xerces:xercesImpl:2.9.1'
    ) {
      transitive = false
    }
  }

}

//grails.plugin.location.postgis='../../plugins/postgis'
