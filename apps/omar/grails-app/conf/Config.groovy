import grails.util.Environment
import java.awt.Color

// locations to search for config files that get merged into the main config;
// config files can be ConfigSlurper scripts, Java properties files, or classes
// in the classpath in ConfigSlurper format

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if (System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination

// The ACCEPT header will not be used for content negotiation for user agents containing the following strings (defaults to the 4 major rendering engines)
grails.mime.disable.accept.header.userAgents = ['Gecko', 'WebKit', 'Presto', 'Trident']
grails.mime.types = [ // the first one is the default format
    all:           '*/*', // 'all' maps to '*' or the first available format in withFormat
    atom:          'application/atom+xml',
    css:           'text/css',
    csv:           'text/csv',
    form:          'application/x-www-form-urlencoded',
    html:          ['text/html','application/xhtml+xml'],
    js:            'text/javascript',
    json:          ['application/json', 'text/json'],
    multipartForm: 'multipart/form-data',
    rss:           'application/rss+xml',
    text:          'text/plain',
    hal:           ['application/hal+json','application/hal+xml'],
    xml:           ['text/xml', 'application/xml']
]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// Legacy setting for codec used to encode data with ${}
grails.views.default.codec = "html"

// The default scope for controllers. May be prototype, session or singleton.
// If unspecified, controllers are prototype scoped.
grails.controllers.defaultScope = 'singleton'

// GSP settings
grails {
    views {
        gsp {
            encoding = 'UTF-8'
            htmlcodec = 'xml' // use xml escaping instead of HTML4 escaping
            codecs {
                expression = 'html' // escapes values inside ${}
                scriptlet = 'html' // escapes output from scriptlets in GSPs
                taglib = 'none' // escapes output from taglibs
                staticparts = 'none' // escapes output from static template parts
            }
        }
        // escapes all not-encoded output at final stage of outputting
        // filteringCodecForContentType.'text/html' = 'html'
    }
}


grails.converters.encoding = "UTF-8"
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart=false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// configure auto-caching of queries by default (if false you can cache individual queries with 'cache: true')
grails.hibernate.cache.queries = false

// configure passing transaction's read-only attribute to Hibernate session, queries and criterias
// set "singleSession = false" OSIV mode in hibernate configuration after enabling
grails.hibernate.pass.readonly = false
// configure passing read-only to OSIV session by default, requires "singleSession = false" OSIV mode
grails.hibernate.osiv.readonly = false

environments {
    development {
        grails.logging.jul.usebridge = true
    }
    production {
        grails.logging.jul.usebridge = false
        // TODO: grails.serverURL = "http://www.changeme.com"
    }
}

// log4j configuration
log4j.main = {
    // Example of changing the log pattern for the default console appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}

    error  'org.codehaus.groovy.grails.web.servlet',        // controllers
           'org.codehaus.groovy.grails.web.pages',          // GSP
           'org.codehaus.groovy.grails.web.sitemesh',       // layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping',        // URL mapping
           'org.codehaus.groovy.grails.commons',            // core / classloading
           'org.codehaus.groovy.grails.plugins',            // plugins
           'org.codehaus.groovy.grails.orm.hibernate',      // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'
}

grails.gorm.default.mapping = {
  cache true
  id generator: 'identity'
  "user-type" type: org.jadira.usertype.dateandtime.joda.PersistentDateTime, class: org.joda.time.DateTime
  "user-type" type: org.jadira.usertype.dateandtime.joda.PersistentLocalDate, class: org.joda.time.LocalDate
}

security {
  level = 'UNCLASS'
//level = 'SECRET'
//level = 'TOPSECRET'
  UNCLASS = [description: "Unclassified", color: "green"]
  SECRET = [description: "Secret // NOFORN", color: "red"]
  TOPSECRET = [description: "Top Secret", color: "yellow"]
  sessionTimeout = 60
}


// Added by the Spring Security Core plugin:
grails.plugins.springsecurity.userLookup.userDomainClassName = 'org.ossim.omar.security.SecUser'
grails.plugins.springsecurity.userLookup.authorityJoinClassName = 'org.ossim.omar.security.SecUserSecRole'
grails.plugins.springsecurity.authority.className = 'org.ossim.omar.security.SecRole'
grails.plugins.springsecurity.requestMap.className = 'org.ossim.omar.security.Requestmap'
grails.plugins.springsecurity.securityConfigType = 'Requestmap'
grails.plugins.springsecurity.errors.login.fail = 'Sorry, we were not able to find a user with that username and password.'
grails.plugins.springsecurity.errors.login.expired = 'Sorry, your login has expired.  Please contact an Administrator.'
grails.plugins.springsecurity.errors.login.passwordExpired = 'Sorry, your password has expired.  Please contact an Administrator.'
grails.plugins.springsecurity.errors.login.disabled = 'Sorry, your account has been disabled.  Please contact an Administrator.'
grails.plugins.springsecurity.errors.login.locked = 'Sorry, your account has been locked.  Please contact an Administrator.'

// Values can be
// MD2, MD5, SHA-1, SHA-256, SHA-384, SHA-512
//
grails.plugins.springsecurity.password.algorithm = 'MD5'
grails.plugins.springsecurity.password.encodeHashAsBase64 = true

grails.plugins.springsecurity.ldap.active = false

federation {
  enabled = true
}

kml {
  maxImages = 100
  maxVideos = 100
  defaultImages = 10
  defaultVideos = 10
  daysCoverage = 30
  viewRefreshTime = 2
}


rabbitmq {
  enabled=false
    //connectionfactory {
    //    username = 'guest'
    //    password = 'guest'
    //    hostname = 'localhost'
    //    consumers = 10
    //}
//    connection = {
        //connection name: "server1", host: "rabbitmq-server", username: "omar", password: "abc123!@#"
//        connection name: "server1", host: "10.0.10.93", username: "omar", password: "abc123!@#"
       // connection host: "rabbit2.example.com", username: "foo", password: "bar"
//    }
//   queues = {
//        queue name: "omar.job.product", durable: true
//        queue name: "omar.job.status", durable: true

    //    exchange name: "omar.exchange", type: "topic", durable:true, {
    //      queue name: "omarmq.job.status", binding: "omar.job.status"
    //    }
        //exchange name: "omar.exchange", type: "topic", durable: true, autoDelete: false
        //exchange name: "omar.job.exchange", type: "topic", durable: true, autoDelete: false
//    }
}

// on windows this seems to return the MAC Address
//omar.serverIP = org.ossim.omar.app.NetUtil.ipAddress
//omar.serverIP = InetAddress.localHost.hostAddress
omar.serverIP = "localhost"
omar.serverURL = "http://${omar.serverIP}:${System.properties['server.port'] ?: '8080'}/${appName}"

wms {
  referenceDataDirectory = "/data/omar"
  mapServExt = ( System.properties['os.name'].startsWith( 'Windows' ) ) ? ".exe" : ""
  serverAddress = omar.serverIP
  useTileCache = false
  mapFile = "${referenceDataDirectory}/bmng.map"

  base {
    defaultOptions = [isBaseLayer: true, numZoomLevels: 20, buffer: 0, transitionEffect: "resize"]
    layers = [
//        [
//            url: "http://hyperquad.ucsd.edu/cgi-bin/i-cubed",
//            params: [layers: "icubed", format: "image/png", transparent: true, bgcolor: '#99B3CC'],
//            name: "I-Cubed LandSat",
//            options: defaultOptions
//        ],
//        [
//            url: "http://hyperquad.ucsd.edu/cgi-bin/onearth",
//           params: [layers: "OnEarth", format: "image/png", transparent: true, bgcolor: '#99B3CC'],
//            name: "OnEarth LandSat",
//            options: defaultOptions
//        ],
[
    url    : ( useTileCache ) ? "http://${serverAddress}/tilecache/tilecache.py" : "http://${serverAddress}/cgi-bin/mapserv${wms.mapServExt}?map=${mapFile}",
    //url: ( useTileCache ) ? "http://${ serverAddress }/tilecache/tilecache.py" : "http://omar.ngaiost.org/cgi-bin/mapserv.sh?map=${ mapFile }",
    params : [layers: ( useTileCache ) ? "omar" : "Reference", format: "image/jpeg"],
    name   : "Reference Data",
    options: defaultOptions
],

/*
[
        url: "${omar.serverURL}/ogc/wms",
        params: [resampler:bilinear,layers: "raster_entry", filter:"", format: "image/png", transparent: true, EXCEPTIONS:"application/vnd.ogc.se_blank"],
        name: "OMAR Rasters",
        options: [isBaseLayer: false, minScale:0.0000001, maxScale:0.0001, buffer: 0, transitionEffect: "resize"]
]
*/
    ]
  }

  supportIE6 = true

  data {
    mapFile = null

    switch ( Environment.current.name.toUpperCase() )
    {
    case "DEVELOPMENT":
      mapFile = "${referenceDataDirectory}/omar-2.0-dev.map"
      break
    case "PRODUCTION":
      mapFile = "${referenceDataDirectory}/omar-2.0-prod.map"
      break
    case "TEST":
      mapFile = "${referenceDataDirectory}/omar-2.0-test.map"
      break
    }

    raster = [
        url    : "${omar.serverURL}/wms/footprints",
        params : [transparent: true, styles: "byFileType", layers: ( supportIE6 ) ? "Imagery" : "ImageData", format: ( supportIE6 ) ? "image/gif" : "image/png"],
        name   : "OMAR Imagery Coverage",
        options: [isBaseLayer: false, footprintLayers: "Imagery"]
    ]

    video = [
        url    : "${omar.serverURL}/wms/footprints",
        params : [transparent: true, styles: "byFileType", layers: ( supportIE6 ) ? "Videos" : "VideoData", format: ( supportIE6 ) ? "image/gif" : "image/png"],
        name   : "OMAR Video Coverage",
        options: [isBaseLayer: false, footprintLayers: "Videos"]
    ]
  }

  // Note the colors are normalized floats
//  styles = [
//      default: [
//          outlinecolor: [r: 0.0, g: 1.0, b: 0, a: 1.0],
//          width: 1
//      ],
//      red: [
//          outlinecolor: [r: 1.0, g: 0.0, b: 0.0, a: 1.0],
//          width: 1
//      ],
//      green: [
//          outlinecolor: [r: 0.0, g: 1.0, b: 0.0, a: 1.0],
//          width: 1
//      ],
//      blue: [
//          outlinecolor: [r: 0.0, g: 0.0, b: 1.0, a: 1.0],
//          width: 1
//      ]
//      byFileType: new PropertyStyle('fileType')
//  ]

  vector {
    maxcount = 10000
  }
}

rasterEntry {
  tagHeaderList = [
      "File Type",
      "Class Name",
      "Mission",
      "Country",
      "Target Id",
      "BE",
      "Sensor",
      "Image Id"
  ]


  tagNameList = [
      "fileType",
      "className",
      "missionId",
      "countryCode",
      "targetId",
      "beNumber",
      "sensorId",
      "title"
  ]

  searchTagData = [
      [name: "fileType", description: "File Type"],
      [name: "filename", description: "Filename"],
      [name: "className", description: "Class Name"],
      [name: "missionId", description: "Mission"],
      [name: "countryCode", description: "Country"],
      [name: "targetId", description: "Target Id"],
      [name: "beNumber", description: "BE Number"],
      [name: "sensorId", description: "Sensor"],
      [name: "title", description: "Image Id"],
      [name: "niirs", description: "niirs"]
  ]

  styles = [
      [
          propertyName      : "fileType",
          outlineLookupTable: [
              //aaigrid: 4,
              cadrg      : new Color( 0, 255, 255, 255 ),
              cib        : new Color( 0, 128, 128, 255 ),
              ccf        : new Color( 128, 100, 255, 255 ),
              adrg       : new Color( 50, 111, 111, 255 ),
              mrsid      : new Color( 0, 188, 0, 255 ),
              //doqq: 2,
              dted       : new Color( 0, 255, 0, 255 ), // green
              jpeg       : new Color( 255, 255, 0, 255 ), // yellow
              jpeg2000   : new Color( 255, 200, 0, 255 ), //
              landsat7   : new Color( 255, 0, 255, 255 ), // purple
              nitf       : new Color( 0, 0, 255, 255 ),  // blue
              tiff       : new Color( 255, 0, 0, 255 ),  // red
              mpeg       : new Color( 164, 254, 255, 255 ),
              unspecified: new Color( 255, 255, 255, 255 ) // white
          ],
      ],
      [
          propertyName       : "sensorId",
          defaultOutlineColor: new Color( 255, 255, 255, 255 ),
          defaultFillColor   : new Color( 0, 0, 0, 0 ),

          outlineLookupTable : [
              'ACES_YOGI-HRI1': new Color( 255, 0, 0, 255 ),
              'ACES_YOGI-HRI2': new Color( 255, 0, 0, 255 ),
              'ACES_YOGI-HRI3': new Color( 255, 0, 0, 255 ),
              'ACES_YOGI-HSI' : new Color( 255, 255, 0, 255 ),
              ALPHA           : new Color( 255, 0, 255, 255 ),
              BRAVO           : new Color( 0, 255, 0, 255 ),
              CHARLIE         : new Color( 0, 255, 255, 255 ),
              DELTA           : new Color( 0, 0, 255, 255 ),
              unspecified     : new Color( 255, 255, 255, 255 ) // white
          ]
      ]
  ]
}

job{
  maxInputs=10
}

//---
// Note: defaultSize of 512 was killing speed on search page when browser is forced to resample.
//---
thumbnail {
  cacheDir = ( System.properties["os.name"].contains( "Windows" ) ) ? "c:/temp" : "${wms.referenceDataDirectory}/omar-cache"
  defaultSize = 128
}

/** ********************************* CONDITIONALS FOR VIEWS                           ***********************************************/
// flags for different views
//
views {
  home {
    // we can conditionally turn off browsing on the home page
    browseEnabled = true
  }
  mapView {
    defaultOverlayVisiblity = false
  }
}
/** *********************************************************************************************************/

piwik.analytics.enabled = false
piwik.analytics.url = "http://example.com/piwik"
piwik.analytics.siteid = 1

videoStreaming {
  flashDirRoot = "/opt/local/apache2/htdocs/videos"
  //flashDirRoot = "/var/www/html/videos"
  flashUrlRoot = "http://${omar.serverIP}/videos"
}



rss {
  rasterEntry {
    properties = [
        imageId               : 'Image ID',
        missionId             : 'Mission ID',
        securityClassification: 'Security Class',
        niirs                 : 'NIIRS',
        countryCode           : 'Country Code',
        beNumber              : 'BE Number',
        acquisitionDate       : 'Acquisition Date',
        width                 : 'Width',
        height                : 'Height',
    ]
  }
}
