import grails.util.Environment
//import org.ossim.postgis.Geometry
//import org.ossim.postgis.GeometryType
import com.vividsolutions.jts.geom.Geometry

grails.gorm.default.mapping = {
  cache true
  id generator: 'identity'
//  'user-type'(type: GeometryType, class: Geometry)
}

// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if(System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.types = [html: ['text/html', 'application/xhtml+xml'],
    xml: ['text/xml', 'application/xml'],
    text: 'text-plain',
    js: 'text/javascript',
    rss: 'application/rss+xml',
    atom: 'application/atom+xml',
    css: 'text/css',
    csv: 'text/csv',
    all: '*/*',
    json: ['application/json', 'text/json'],
    form: 'application/x-www-form-urlencoded',
    multipartForm: 'multipart/form-data',
    kml: 'application/vnd.google+earth.kml+xml'
]
// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"

// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true

grails.serverIP="localhost"
//grails.serverIP = InetAddress.localHost.hostAddress
//grails.serverIP = "scott-6a8076558:8080"

//grails.serverIP = "172.16.90.130"

// set per-environment serverURL stem for creating absolute links
environments {
  development {
    grails.serverURL = "http://${grails.serverIP}:${System.properties['server.port'] ?: '8080'}/${appName}"
  }
  test {
    grails.serverURL = "http://${grails.serverIP}:${System.properties['server.port'] ?: '8080'}/${appName}"
  }
  production {
    grails.serverURL = "http://${grails.serverIP}:${System.properties['server.port'] ?: '8080'}/${appName}"

  }
}

// log4j configuration
log4j = {
  // Example of changing the log pattern for the default console
  // appender:
  //
  appenders {
    appender new org.apache.log4j.DailyRollingFileAppender(name: "omarAppender",
        datePattern: "'.'yyyy-MM-dd",
        file: "/tmp/logs/omar.log",
        layout: pattern(conversionPattern: '[%d{yyyy-MM-dd hh:mm:ss.SSS}] %p %c{5} %m%n'))
  }

  info omarAppender: 'grails.app',
      additivity: false

  error 'org.codehaus.groovy.grails.web.servlet',  //  controllers
      'org.codehaus.groovy.grails.web.pages', //  GSP
      'org.codehaus.groovy.grails.web.sitemesh', //  layouts
      'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
      'org.codehaus.groovy.grails.web.mapping', // URL mapping
      'org.codehaus.groovy.grails.commons', // core / classloading
      'org.codehaus.groovy.grails.plugins', // plugins
      'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
      'org.springframework',
      'org.hibernate',
      'net.sf.ehcache.hibernate'

  warn 'org.mortbay.log'
}

//log4j.logger.org.springframework.security='off,stdout'

/** *********************************************************************************************************/
wms.referenceDataDirectory = "/data/omar"

wms.mapServExt = (System.properties["os.name"].startsWith("Windows")) ? ".exe" : ""
//wms.serverAddress = InetAddress.localHost.hostAddress
wms.serverAddress = grails.serverIP


wms.base.useTileCache = true
wms.base.mapFile = "${wms.referenceDataDirectory}/bmng.map"

wms.base = [
    [
        url: (wms.base.useTileCache) ? "http://${wms.serverAddress}/tilecache/tilecache.py" : "http://${wms.serverAddress}/cgi-bin/mapserv${wms.mapServExt}?map=${wms.base.mapFile}",
        layers: (wms.base.useTileCache) ? "omar" : "Reference",
        title: "Reference Data",
        format: "image/jpeg"
    ]
]

wms.supportIE6 = true


wms.data.mapFile = null

switch ( Environment.current.name.toUpperCase() )
{
case "DEVELOPMENT":
  wms.data.mapFile = "${wms.referenceDataDirectory}/omar-2.0-dev.map"
  break
case "PRODUCTION":
  wms.data.mapFile = "${wms.referenceDataDirectory}/omar-2.0-prod.map"
  break
case "TEST":
  wms.data.mapFile = "${wms.referenceDataDirectory}/omar-2.0-test.map"
  break
}


wms.data.raster = [
    url: "${grails.serverURL}/ogc/footprints",
    layers: (wms.supportIE6) ? "Imagery" : "ImageData",
    footprintLayers: "Imagery",
    title: "OMAR Imagery Coverage",
    format: (wms.supportIE6) ? "image/gif" : "image/png",
    styles: "green"
]

wms.data.video = [
//    url: "http://${wms.serverAddress}/cgi-bin/mapserv${wms.mapServExt}?map=${wms.data.mapFile}",
    url: "${grails.serverURL}/ogc/footprints",
    layers: (wms.supportIE6) ? "Videos" : "VideoData",
    footprintLayers: "Videos",
    title: "OMAR Video Coverage",
    format: (wms.supportIE6) ? "image/gif" : "image/png",
    styles: "red"
]

// Note the colors are normalized floats
wms.styles = [
    default: [
        outlinecolor: [r: 0.0, g: 1.0, b: 0, a: 1.0],
        width: 1
    ],
    red: [
        outlinecolor: [r: 1.0, g: 0.0, b: 0.0, a: 1.0],
        width: 1
    ],
    green: [
        outlinecolor: [r: 0.0, g: 1.0, b: 0.0, a: 1.0],
        width: 1
    ],
    blue: [
        outlinecolor: [r: 0.0, g: 0.0, b: 1.0, a: 1.0],
        width: 1
    ]
]
wms.vector.maxcount = 10000

thumbnail.cacheDir = (System.properties["os.name"] == "Windows XP") ? "c:/temp" : "${wms.referenceDataDirectory}/omar-cache"
thumbnail.defaultSize = 512

security.level = 'UNCLASS'
//security.level = 'SECRET'
//security.level = 'TOPSECRET'

omar.release = '1.8.4'

image.download.prefix = "http://${grails.serverIP}"

/** ********************************* CONDITIONALS FOR VIEWS               **********************************************************************/
// flags for different views
// we can conditionally turn off browsing on the home page
//
views.home.browseEnabled = true

/** *********************************************************************************************************/

videoStreaming.flashDirRoot = "/Library/WebServer/Documents/videos"
//videoStreaming.flashDirRoot = "/var/www/html/videos"
videoStreaming.flashUrlRoot = "http://${grails.serverIP}/videos"

rasterEntry.tagHeaderList = [
    "File Type",
    "Class Name",
    "Mission",
    "Country",
    "Target Id",
    "Sensor",
    "Image Id"
]


rasterEntry.tagNameList = [
    "fileType",
    "className",
    "missionId",
    "countryCode",
    "targetId",
    "sensorId",
    "title"
]

rasterEntry.searchTagData = [
    [name: "fileType", description: "File Type"],
    [name: "className", description: "Class Name"],
    [name: "missionId", description: "Mission"],
    [name: "countryCode", description: "Country"],
    [name: "targetId", description: "BE Number"],
    [name: "sensorId", description: "Sensor"],
    [name: "title", description: "Image Id"],
    [name: "niirs", description: "niirs"]
]

videoDataSet.searchTagData = [
    [name: "otherTagsXml.filename=", description: "Feed"]
]


kml.maxImages = 100
kml.maxVideos = 100
kml.defaultImages = 10
kml.defaultVideos = 10
kml.daysCoverage = 30
kml.viewRefreshTime = 2
