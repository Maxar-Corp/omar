import grails.util.Environment

import org.joda.time.*
import org.joda.time.contrib.hibernate.*

// on windows this seems to return the MAC Address
//omar.serverIP = org.ossim.omar.app.NetUtil.ipAddress
omar.serverIP = InetAddress.localHost.hostAddress
omar.serverURL = "http://${ omar.serverIP }:${ System.properties['server.port'] ?: '8080' }/${ appName }"



//import org.ossim.omar.core.DbAppender

grails.gorm.default.mapping = {
  cache true
  id generator: 'identity'
  "user-type" type: PersistentDateTime, class: DateTime
  "user-type" type: PersistentLocalDate, class: LocalDate
}

// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts
if (!grails.config.locations || !(grails.config.locations instanceof List)) {
    grails.config.locations = []
}

if ( new File( "${ userHome }/.grails/${ appName }-config.groovy" ).exists() )
{
  grails.config.locations << "file:${ userHome }/.grails/${ appName }-config.groovy"
}
//if ( System.env.OMAR_CONFIG )
//{
  grails.config.locations << "file:${ System.env.OMAR_CONFIG }"
//}
if ( System.env.QUARTZ_CONFIG )
{
  grails.config.locations << "file:${ System.env.QUARTZ_CONFIG }"
}
// if(System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.types = [html: ['text/html', 'application/xhtml+xml'],
    xml: ['text/xml', 'application/xml'],
    text: 'text-plain',
    jpeg: 'image/jpeg',
    jpg: 'image/jpeg',
    png: 'image/png',
    js: 'text/javascript',
    rss: 'application/rss+xml',
    atom: 'application/atom+xml',
    css: 'text/css',
    csv: 'text/csv',
    all: '*/*',
    json: ['application/json', 'text/json'],
    form: 'application/x-www-form-urlencoded',
    multipartForm: 'multipart/form-data',
    kml: 'application/vnd.google+earth.kml+xml',
    kmz: 'application/vnd.google-earth.kmz'
]

// The default codec used to encode data with ${}
grails.views.default.codec = "none" // noxne, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"

// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true

//
//// set per-environment serverURL stem for creating absolute links
//environments {
//  development {
//    databaseName = "omardb-${appVersion}-dev"
//    serverURL = "http://${serverIP}:${System.properties['server.port'] ?: '8080'}/${appName}"
//  }
//  test {
//    databaseName = "omardb-${appVersion}-test"
//    serverURL = "http://${serverIP}:${System.properties['server.port'] ?: '8080'}/${appName}"
//  }
//  production {
//    databaseName = "omardb-${appVersion}-prod"
//    serverURL = "http://${serverIP}/${appName}"
//
//  }
//}

// log4j configuration
log4j = {
  // Example of changing the log pattern for the default console
  // appender:
  //
  appenders {

    // uncomment for DB appending.  Do this after the first build of OMAR.
    // Then uncomment the wmsLoggingAppender redirection below.
    // add in the import org.ossim.omar.DbAppender at the top
//    appender new DbAppender(name: "wmsLoggingAppender",
//            threshold: org.apache.log4j.Level.INFO,
//            tableMapping: [width: ":width", height: ":height", layers: ":layers", styles: ":styles",
//                    format: ":format", request: ":request", bbox: ":bbox", internal_time: ":internalTime",
//                    render_time: ":renderTime", total_time: ":totalTime", start_date: ":startDate",
//                    end_date: ":endDate", user_name: ":userName", ip: ":ip", url: ":url", mean_gsd: ":meanGsd",
//                    geometry: "ST_GeomFromText(:geometry, 4326)"],
//            tableName: "wms_log"
//    )
    appender new org.apache.log4j.DailyRollingFileAppender( name: "omarDataManagerAppender",
        datePattern: "'.'yyyy-MM-dd",
        file: "/tmp/logs/omarDataManagerAppender.log",
        layout: pattern( conversionPattern: '[%d{yyyy-MM-dd hh:mm:ss.SSS}] %p %c{5} %m%n' ) )
    appender new org.apache.log4j.DailyRollingFileAppender( name: "omarAppender",
        datePattern: "'.'yyyy-MM-dd",
        file: "/tmp/logs/omar.log",
        layout: pattern( conversionPattern: '[%d{yyyy-MM-dd hh:mm:ss.SSS}] %p %c{5}  %m%n' ) )
  }

//  info wmsLoggingAppender: 'grails.app.service.org.ossim.omar.WmsLogService', additivity: false
  info 'omarDataManagerAppender': '*DataManagerService', additivity: false
  info omarAppender: 'grails.app', additivity: false
  info omarAppender: 'omar', additivity: false

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

  fatal 'org.grails.plugin.resource'

//   debug 'org.springframework.security',
//         'com.sun.jndi.ldap',

//  root {
//        debug 'stdout'
//    }

}

/** *********************************************************************************************************/
wms {
  referenceDataDirectory = "/data/omar"
  mapServExt = ( System.properties['os.name'].startsWith( 'Windows' ) ) ? ".exe" : ""
  serverAddress = omar.serverIP
  useTileCache = false
  mapFile = "${ referenceDataDirectory }/bmng.map"

  base {
    defaultOptions = [isBaseLayer: true, numZoomLevels:17, buffer: 0, transitionEffect: "resize"]
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
            url: ( useTileCache ) ? "http://${ serverAddress }/tilecache/tilecache.py" : "http://${ serverAddress }/cgi-bin/mapserv${ wms.mapServExt }?map=${ mapFile }",
            params: [layers: ( useTileCache ) ? "omar" : "Reference", format: "image/jpeg"],
            name: "Reference Data",
            options: defaultOptions
        ]

    ]
  }

  supportIE6 = true

  data {
    mapFile = null

    switch ( Environment.current.name.toUpperCase() )
    {
    case "DEVELOPMENT":
      mapFile = "${ referenceDataDirectory }/omar-2.0-dev.map"
      break
    case "PRODUCTION":
      mapFile = "${ referenceDataDirectory }/omar-2.0-prod.map"
      break
    case "TEST":
      mapFile = "${ referenceDataDirectory }/omar-2.0-test.map"
      break
    }

    raster = [
        url: "${ omar.serverURL }/wms/footprints",
        params: [layers: ( supportIE6 ) ? "Imagery" : "ImageData", format: ( supportIE6 ) ? "image/gif" : "image/png"],
        name: "OMAR Imagery Coverage",
        options: [styles: "byFileType", footprintLayers: "Imagery"]
    ]

    video = [
        url: "${ omar.serverURL }/wms/footprints",
        params: [layers: ( supportIE6 ) ? "Videos" : "VideoData", format: ( supportIE6 ) ? "image/gif" : "image/png"],
        name: "OMAR Video Coverage",
        options: [styles: "red", footprintLayers: "Videos"]
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


thumbnail {
    cacheDir = ( System.properties["os.name"].contains("Windows") ) ? "c:/temp" : "${ wms.referenceDataDirectory }/omar-cache"
    defaultSize = 512
}

security {
//  level = 'UNCLASS'
//level = 'SECRET'
//level = 'TOPSECRET'
  UNCLASS = [description: "Unclassified", color: "green"]
  SECRET = [description: "Secret // NOFORN", color: "red"]
  TOPSECRET = [description: "Top Secret", color: "yellow"]
  sessionTimeout = 60
  level = "UNCLASS"
}

image.download.prefix = "http://${ omar.serverIP }"

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

videoStreaming {
  flashDirRoot = "/opt/local/apache2/htdocs/videos"
  //flashDirRoot = "/var/www/html/videos"
  flashUrlRoot = "http://${ omar.serverIP }/videos"
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
}

videoDataSet {
  searchTagData = [
      [name: "filename", description: "Feed"]
  ]
}

login {
  registration {
    /**
     * registration has the following values:
     *  true: Allows users to register a new account by following the register link on the OMAR login page.
     * false: Prevents user registration and removes the register link from the OMAR login page. We recommend
     *        setting enabled to false if you are using LDAP for user authentication.
     */
    enabled = true

    /**
     * userVerification has the following values:
     *   none: Enables a new user account upon registration.
     * manual: Requires an administrator to enable new user accounts.
     *  email: Requires email verification before enabling the account, but also requires the modification
     *         of SecurityConfig.groovy in the omar-security plugin to specify your mail host settings.
     */
    userVerification = "none"

    if ( userVerification == "email" )
    {
      useMail = "true"
    }

    createLdapUser = false
  }
}

kml {
  maxImages = 100
  maxVideos = 100
  defaultImages = 10
  defaultVideos = 10
  daysCoverage = 30
  viewRefreshTime = 2
}


grails.doc.authors = "Garrett Potts"
grails.doc.license = "LGPL"
grails.doc.copyright = "RadiantBlue Technologies"
grails.doc.footer = ""
grails.doc.title = "OMAR"
grails.doc.subtitle = ""
grails.doc.logo = """<a href="http://www.ossim.org" ><img src="../img/OMAR.png" border="0"/></a>"""
grails.doc.sponsorLogo = """<a href="http://www.radiantblue.com" ><img src="../img/RBT.png" border="0"/></a>"""
grails.doc.images = new File( "web-app/images" )

tomcat {
  servers = [
      localhost: [url: "http://localhost:8080/manager", username: "tomcat", password: "s3cret"]
  ]
}

bundle {
  combine = false
  compress = false
}

export {
  prefix = "omar-export-"
  workDir = "/tmp"
  superoverlay {
    baseDir = "/data/omar/superoverlay"
    outputKmz = true
  }
  rasterEntry {
    fields = [
        'acquisitionDate',
        'azimuthAngle',
        'beNumber',
        'cloudCover',
        'countryCode',
        'description',
        'entryId',
        'fileType',
        'filename',
        'grazingAngle',
        'groundGeom',
        'gsdUnit',
        'gsdX',
        'gsdY',
        'height',
        'id',
        'imageCategory',
        'imageId',
        'imageRepresentation',
        'indexId',
        'isorce',
        'missionId',
        'niirs',
        'numberOfBands',
        'numberOfResLevels',
        'organization',
        'productId',
        'releaseId',
        'securityClassification',
        'securityCode',
        'sensorId',
        'sunAzimuth',
        'sunElevation',
        'targetId',
        'title',
        'validModel',
        'wacCode',
        'width'
    ]

    labels = [
        'Acquisition Date',
        'Azimuth Angle',
        'Be Number',
        'Cloud Cover',
        'Country Code',
        'Description',
        'Entry Id',
        'File Type',
        'Filename',
        'Grazing Angle',
        'Ground Geom',
        'Gsd Unit',
        'Gsd X',
        'Gsd Y',
        'Height',
        'Id',
        'Image Category',
        'Image Id',
        'Image Representation',
        'Index Id',
        'Isorce',
        'Mission Id',
        'Niirs',
        'Number Of Bands',
        'Number Of Res Levels',
        'Organization',
        'Product Id',
        'Release Id',
        'Security Classification',
        'Security Code',
        'Sensor Id',
        'Sun Azimuth',
        'Sun Elevation',
        'Target Id',
        'Title',
        'Valid Model',
        'Wac Code',
        'Width'
    ]


    formatters = [
        groundGeom: {
            def convertValue = it;
           // if(it instanceof String)
           // {
           //     convertValue = geoscript.geom.Geometry.fromWKT(it);
           // }
            def bounds = convertValue.envelopeInternal; [bounds.minX, bounds.minY, bounds.maxX, bounds.maxY].join( ',' )
        }
    ]
  }

  videoDataSet {
    fields = [
        'endDate',
        'filename',
        'groundGeom',
        'height',
        'id',
        'indexId',
        'startDate',
        'width'
    ]
    labels = [
        'End Date',
        'Filename',
        'Ground Geom',
        'Height',
        'Id',
        'Index Id',
        'Start Date',
        'Width'
    ]
    formatters = [
        groundGeom: { def bounds = it.envelopeInternal; [bounds.minX, bounds.minY, bounds.maxX, bounds.maxY].join( ',' ) }
    ]

  }
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

/*********** LDAP config for Active Directory    **********/
/*
//grails.plugins.springsecurity.ldap.context.managerDn = 'CN=Administrator,OU=users,DC=otd,DC=radiantblue,DC=com'
grails.plugins.springsecurity.ldap.context.managerDn = 'cn=administrator,cn=users,dc=otd,dc=radiantblue,dc=com'
grails.plugins.springsecurity.ldap.context.managerPassword = 'abc123!@#'
grails.plugins.springsecurity.ldap.context.server = 'ldap://otd-dc.otd.radiantblue.com:389/'
grails.plugins.springsecurity.ldap.authorities.ignorePartialResultException = true // typically needed for Active Directory
//grails.plugins.springsecurity.ldap.search.base = 'cn=users,dc=otd,dc=radiantblue,dc=com'
grails.plugins.springsecurity.ldap.search.base = 'dc=otd,dc=radiantblue,dc=com'
grails.plugins.springsecurity.ldap.search.filter="sAMAccountName={0}" // for Active Directory you need this
grails.plugins.springsecurity.ldap.search.searchSubtree = true
grails.plugins.springsecurity.ldap.auth.hideUserNotFoundExceptions = false
*/
/***********   END config for active directory    ***********/

/***********      LDAP config for OpenLdap        **********/

/*
    grails.plugins.springsecurity.ldap.context.managerDn = 'cn=Administrator,dc=otd,dc=radiantblue,dc=com'
    grails.plugins.springsecurity.ldap.context.managerPassword = 'omarldap'
    grails.plugins.springsecurity.ldap.context.server = 'ldap://sles11-ldap-server.otd.radiantblue.com:389'
    grails.plugins.springsecurity.ldap.authorities.groupSearchBase = 'dc=otd,dc=radiantblue,dc=com'
    grails.plugins.springsecurity.ldap.search.base = 'dc=otd,dc=radiantblue,dc=com'

    // If you want to also assign application-specific roles to users in
    // the database, then uncomment the line below
    grails.plugins.springsecurity.ldap.authorities.retrieveDatabaseRoles = true
*/

/***********   END config for OpenLdap    ***********/

/*********** Optional ldap settings (group, search attributes, etc.)  **********/

//grails.plugins.springsecurity.ldap.search.attributesToReturn = ['mail', 'displayName'] // extra attributes you want returned; see below for custom classes that access this data
//grails.plugins.springsecurity.providerNames = ['ldapAuthProvider', 'anonymousAuthenticationProvider']
//grails.plugins.springsecurity.providerNames= ['adAuthenticationProvider']

//grails.plugins.springsecurity.ldap.context.server = 'ldap://sles11-ldap-server'

// LDAP user:
//  username: demo
//  password: d3m0m@p5

grails {
  plugins {
    springsecurity {
      ui {
        register {
          emailBody = '''\
Hi $user.username,<br/>
<br/>
You (or someone pretending to be you) created an account with this email address.<br/>
<br/>
If you made the request, please click <a href="$url">here</a> to finish the registration.
'''
          emailFrom = 'do.not.reply@localhost'
          emailSubject = 'New Account'
          defaultRoleNames = ['ROLE_USER']
          postRegisterUrl = null // use defaultTargetUrl if not set
        }

        forgotPassword {
          emailBody = '''\
Hi $user.username,<br/>
<br/>
You (or someone pretending to be you) requested that your password be reset.<br/>
<br/>
If you didn't make this request then ignore the email; no changes have been made.<br/>
<br/>
If you did make the request, then click <a href="$url">here</a> to reset your password.
'''
          emailFrom = 'do.not.reply@localhost'
          emailSubject = 'Password Reset'
          postResetUrl = null // use defaultTargetUrl if not set
        }
      }
    }
  }
}
//grails {
//   mail {
//     host = "smtp.gmail.com"
//     port = 465
//     username = "youracount@gmail.com"
//     password = "yourpassword"
//     props = ["mail.smtp.auth":"true",
//              "mail.smtp.socketFactory.port":"465",
//              "mail.smtp.socketFactory.class":"javax.net.ssl.SSLSocketFactory",
//              "mail.smtp.socketFactory.fallback":"false"]
//   }
//}

rss {
  rasterEntry {
    properties = [
        imageId: 'Image ID',
        missionId: 'Mission ID',
        securityClassification: 'Security Class',
        niirs: 'NIIRS',
        countryCode: 'Country Code',
        beNumber: 'BE Number',
        acquisitionDate: 'Acquisition Date',
        width: 'Width',
        height: 'Height',
    ]
  }
}

thumbnail {
  cacheDir = ( System.properties["os.name"] == "Windows XP" ) ? "c:/temp" : "${ wms.referenceDataDirectory }/omar-cache"
  defaultSize = 512
}

/**
 * This is the settings for the stager threads and jobs in the OMAR system
 */
stager {
    /**
     * Worker threads are currently for building on demand overviews and histograms
     * This will specify how many simultaneous threads that can be active
     *
     */
  worker{
      threads = 3
      maxQueueSize=1000
  }

    /**
     * When files are being indexed into the system this specifies how
     * many threads can be used to pop off the table queue in the database
     *
     * Currently this is used for indexing new data into the system
     */
  queue {
    threads = 4
  }

    /**
     * This is passed to external scripts that support threading.  You can specify
     * the default value and the max thread count that is used in the UI for
     * user input to the executing thread
     */
  scripts{
      defaultThreadCount = 4
      maxThreadCount = 8

      /**
       * This will force the scripts page on reload to always use the formatters
       * listed.
       */
      forceUseFormatterOnReload = true
      formatter = [
              /**
               * This formats the arguments to the indexFilesArgs on the scripts page
               */
              indexFilesArgs:{
                  def date = new org.joda.time.DateTime()
                  "/data"
//                  "/data/${date.toString('YYYY-MM-dd')}"
                    },
              /**
               * This formats the arguments to the stageFilesArgs on the scripts page
               */
              stageFilesArgs:{
                  def date = new org.joda.time.DateTime()
                  "/data"
//                  "/data/${date.toString('YYYY-MM-dd')}"
              },
              /**
               * This formats the arguments to the removeFilesArgs on the scripts page
               */
              removeFilesArgs:{
                  def date = new org.joda.time.DateTime()
                  date = date.plusDays(-30);
                  "/data"
//                  "/data/${date.toString('YYYY-MM-dd')}"
              }
              ]
  }
  histogramOptions = ""
  overview {
    compressionType = "JPEG"
    compressionQuality = 75
  }
  onDemand = true
}

grails.resources.mappers.yuicssminify.includes = ['**/*.css']
grails.resources.mappers.yuijsminify.includes = ['**/*.js']
grails.resources.mappers.yuicssminify.excludes = ['**/*.min.css']
grails.resources.mappers.yuijsminify.excludes = ['**/*.min.js']
