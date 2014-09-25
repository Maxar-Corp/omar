grails.work.dir = "${System.env.OMAR_DEV_HOME}/.grails"
grails.dependency.cache.dir = "${System.env.OMAR_DEV_HOME}/.grails/ivy-cache"

grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    legacyResolve false // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility
    repositories {
        mavenRepo "http://repo.grails.org/grails/plugins/"

        mavenRepo "http://repo.boundlessgeo.com/main"
        mavenRepo "http://download.osgeo.org/webdav/geotools"

        //grailsCentral()
        mavenCentral()
        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        mavenLocal()
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        mavenRepo "http://download.java.net/maven/2/"
        mavenRepo "http://logicaldoc.sourceforge.net/maven/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

        // runtime 'mysql:mysql-connector-java:5.1.21'
    compile(
        'antlr:antlr:2.7.7',
        'com.googlecode.json-simple:json-simple:1.1',
//        'com.h2database:h2:1.1.119',
        'com.lowagie:itext:2.1.7',
        'com.miglayout:miglayout:3.7.4',
        'com.vividsolutions:jts:1.13',
//        'commons-beanutils:commons-beanutils:1.7.0',
//        'commons-codec:commons-codec:1.2',
//        'commons-collections:commons-collections:3.1',
//        'commons-dbcp:commons-dbcp:1.3',
        'commons-httpclient:commons-httpclient:3.1',
        'commons-io:commons-io:2.1',
        'commons-jxpath:commons-jxpath:1.3',
        'commons-logging:commons-logging:1.0.4',
//        'commons-pool:commons-pool:1.5.4',
        'hsqldb:hsqldb:1.8.0.7',
        'it.geosolutions.imageio-ext:imageio-ext-arcgrid:1.1.7',
        'it.geosolutions.imageio-ext:imageio-ext-gdal-bindings:1.9.2',
        'it.geosolutions.imageio-ext:imageio-ext-gdalarcbinarygrid:1.1.7',
        'it.geosolutions.imageio-ext:imageio-ext-gdaldted:1.1.7',
        'it.geosolutions.imageio-ext:imageio-ext-gdalecw:1.1.7',
        'it.geosolutions.imageio-ext:imageio-ext-gdalecwjp2:1.1.7',
        'it.geosolutions.imageio-ext:imageio-ext-gdalehdr:1.1.7',
        'it.geosolutions.imageio-ext:imageio-ext-gdalenvihdr:1.1.7',
        'it.geosolutions.imageio-ext:imageio-ext-gdalerdasimg:1.1.7',
        'it.geosolutions.imageio-ext:imageio-ext-gdalframework:1.1.7',
        'it.geosolutions.imageio-ext:imageio-ext-gdalidrisi:1.1.7',
        'it.geosolutions.imageio-ext:imageio-ext-gdalkakadujp2:1.1.7',
        'it.geosolutions.imageio-ext:imageio-ext-gdalmrsid:1.1.7',
        'it.geosolutions.imageio-ext:imageio-ext-gdalmrsidjp2:1.1.7',
        'it.geosolutions.imageio-ext:imageio-ext-gdalnitf:1.1.7',
        'it.geosolutions.imageio-ext:imageio-ext-gdalrpftoc:1.1.7',
        'it.geosolutions.imageio-ext:imageio-ext-geocore:1.1.7',
        'it.geosolutions.imageio-ext:imageio-ext-imagereadmt:1.1.7',
        'it.geosolutions.imageio-ext:imageio-ext-streams:1.1.7',
        'it.geosolutions.imageio-ext:imageio-ext-tiff:1.1.7',
        'it.geosolutions.imageio-ext:imageio-ext-utilities:1.1.7',
        'java3d:vecmath:1.3.2',
       // 'javax.media.jai:jai_codec:1.2.1',
        'javax.media.jai:jai-core:1.2.1',
        'javax.media.jai:jai-codec:1.2.1',
        'javax.media:jai_imageio:1.1',
        'jdom:jdom:1.0',
        'jfree:eastwood:1.1.1-20090908',
        'jfree:jcommon:1.0.13',
        'jfree:jfreechart:1.0.10',
        'jgridshift:jgridshift:1.0',
//        'junit:junit:4.7',
        'mysql:mysql-connector-java:5.1.17',
        'net.java.dev.jsr-275:jsr-275:1.0-beta-2',
//        'net.sf.ehcache:ehcache:1.6.2',
        'net.sf.opencsv:opencsv:2.0',
        'net.sourceforge.hatbox:hatbox:1.0.b7',
        'org.antlr:antlr:3.3',
        'org.antlr:antlr-runtime:3.3',
        'org.antlr:stringtemplate:3.2.1',
        'org.apache.avalon.framework:avalon-framework-api:4.3.1',
        'org.apache.avalon.framework:avalon-framework-impl:4.3.1',
        'org.apache.xml:xml-commons-resolver:1.2',
        'org.apache.xmlgraphics:batik-anim:1.7',
        'org.apache.xmlgraphics:batik-awt-util:1.7',
        'org.apache.xmlgraphics:batik-bridge:1.7',
        'org.apache.xmlgraphics:batik-css:1.7',
        'org.apache.xmlgraphics:batik-dom:1.7',
        'org.apache.xmlgraphics:batik-ext:1.7',
        'org.apache.xmlgraphics:batik-gvt:1.7',
//        'org.apache.xmlgraphics:batik-js:1.7',
        'org.apache.xmlgraphics:batik-parser:1.7',
        'org.apache.xmlgraphics:batik-script:1.7',
        'org.apache.xmlgraphics:batik-svg-dom:1.7',
        'org.apache.xmlgraphics:batik-svggen:1.7',
        'org.apache.xmlgraphics:batik-transcoder:1.7',
        'org.apache.xmlgraphics:batik-util:1.7',
        'org.apache.xmlgraphics:batik-xml:1.7',
        'org.apache.xmlgraphics:fop:0.94',
        'org.apache.xmlgraphics:xmlgraphics-commons:1.2',
//        'org.codehaus.groovy:groovy-all:2.1.6',
        'org.codehaus.janino:janino:2.5.16',
        'org.eclipse.emf:common:2.6.0',
        'org.eclipse.emf:ecore:2.6.1',
        'org.eclipse.xsd:xsd:2.6.0',
        'org.geoscript:geocss_2.10:0.8.3',
        'org.geoscript:geoscript-groovy:1.2',
        'org.geotools:gt-api:10.3',
        'org.geotools:gt-arcgrid:10.3',
        'org.geotools:gt-brewer:10.3',
        'org.geotools:gt-charts:10.3',
        'org.geotools:gt-coverage:10.3',
        'org.geotools:gt-cql:10.3',
        'org.geotools:gt-data:10.3',
        'org.geotools:gt-epsg-hsql:10.3',
        'org.geotools:gt-geojson:10.3',
        'org.geotools:gt-geotiff:10.3',
        'org.geotools:gt-graph:10.3',
        'org.geotools:gt-grassraster:10.3',
        'org.geotools:gt-grid:10.3',
        'org.geotools:gt-gtopo30:10.3',
        'org.geotools:gt-image:10.3',
        'org.geotools:gt-imageio-ext-gdal:10.3',
        'org.geotools:gt-imagemosaic:10.3',
        'org.geotools:gt-imagepyramid:10.3',
        'org.geotools:gt-jdbc:10.3',
        'org.geotools:gt-main:10.3',
        'org.geotools:gt-metadata:10.3',
        'org.geotools:gt-opengis:10.3',
        'org.geotools:gt-process:10.3',
        'org.geotools:gt-process-feature:10.3',
        'org.geotools:gt-process-geometry:10.3',
        'org.geotools:gt-process-raster:10.3',
        'org.geotools:gt-property:10.3',
        'org.geotools:gt-referencing:10.3',
        'org.geotools:gt-render:10.3',
        'org.geotools:gt-shapefile:10.3',
        'org.geotools:gt-svg:10.3',
        'org.geotools:gt-swing:10.3',
        'org.geotools:gt-transform:10.3',
        'org.geotools:gt-wfs:10.3',
        'org.geotools:gt-wms:10.3',
        'org.geotools:gt-xml:10.3',
        'org.geotools.jdbc:gt-jdbc-h2:10.3',
        'org.geotools.jdbc:gt-jdbc-mysql:10.3',
        'org.geotools.jdbc:gt-jdbc-postgis:10.3',
        'org.geotools.jdbc:gt-jdbc-spatialite:10.3',
        'org.geotools.ogc:net.opengis.fes:10.3',
        'org.geotools.ogc:net.opengis.ows:10.3',
        'org.geotools.ogc:net.opengis.wfs:10.3',
        'org.geotools.ogc:org.w3.xlink:10.3',
        'org.geotools.xsd:gt-xsd-core:10.3',
        'org.geotools.xsd:gt-xsd-fes:10.3',
        'org.geotools.xsd:gt-xsd-filter:10.3',
        'org.geotools.xsd:gt-xsd-gml2:10.3',
        'org.geotools.xsd:gt-xsd-gml3:10.3',
        'org.geotools.xsd:gt-xsd-kml:10.3',
        'org.geotools.xsd:gt-xsd-ows:10.3',
        'org.geotools.xsd:gt-xsd-wfs:10.3',
        'org.jaitools:jt-attributeop:1.3.1',
        'org.jaitools:jt-contour:1.3.1',
        'org.jaitools:jt-jiffle-language:0.2.0',
        'org.jaitools:jt-jiffleop:0.2.0',
        'org.jaitools:jt-rangelookup:1.3.1',
        'org.jaitools:jt-utils:1.3.1',
        'org.jaitools:jt-vectorbinarize:1.3.1',
        'org.jaitools:jt-vectorize:1.3.1',
        'org.jaitools:jt-zonalstats:1.3.1',
        'org.json:json:20090211',
        'org.opengeo:geodb:0.7-RC2',
        'org.scala-lang:scala-library:2.10.0',
        'picocontainer:picocontainer:1.2',
        'postgresql:postgresql:8.4-701.jdbc3',
        'xalan:xalan:2.6.0',
//        'xml-apis:xml-apis:1.3.04',
//        'xml-apis:xml-apis-ext:1.3.04',
        'xpp3:xpp3_min:1.1.4c',
    ) {
      transitive = false
    }
    }

    plugins {
        build(":tomcat:$grailsVersion",
              ":release:2.2.1",
              ":rest-client-builder:1.0.3") {
            export = false
        }
    }
}
