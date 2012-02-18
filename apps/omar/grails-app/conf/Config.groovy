import grails.util.Environment

import org.joda.time.*
import org.joda.time.contrib.hibernate.*
import org.ossim.omar.core.DbAppender

grails.gorm.default.mapping = {
  cache true
  id generator: 'identity'
  "user-type" type: PersistentDateTime, class: DateTime
  "user-type" type: PersistentLocalDate, class: LocalDate
}

// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

grails.config.locations = [
//  "classpath:${appName}-config.properties",
//  "classpath:${appName}-config.groovy",
//  "file:${userHome}/.grails/${appName}-config.properties",
//     "file:${userHome}/.grails/${appName}-config.groovy"
]
if ( new File("${userHome}/.grails/${appName}-config.groovy").exists() )
{
  grails.config.locations << "file:${userHome}/.grails/${appName}-config.groovy"
}
if ( System.env.OMAR_CONFIG )
{
  grails.config.locations << "file:${System.env.OMAR_CONFIG}"
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
grails.serverIP = InetAddress.localHost.hostAddress

// set per-environment serverURL stem for creating absolute links
environments {
  development {
    databaseName = "omardb-${appVersion}-dev"
    grails.serverURL = "http://${grails.serverIP}:${System.properties['server.port'] ?: '8080'}/${appName}"
  }
  test {
    databaseName = "omardb-${appVersion}-test"
    grails.serverURL = "http://${grails.serverIP}:${System.properties['server.port'] ?: '8080'}/${appName}"
  }
  production {
    databaseName = "omardb-${appVersion}-prod"
    //grails.serverURL = "http://${grails.serverIP}:${System.properties['server.port'] ?: '8080'}/${appName}"
    grails.serverURL = "http://${grails.serverIP}/${appName}"

  }
}

// log4j configuration
log4j = {
  // Example of changing the log pattern for the default console
  // appender:
  //
  appenders {

    // uncomment for DB appending.  Do this after the first build of OMAR.
    // Then uncomment the wmsLoggingAppender redirection below.
    // add in the import org.ossim.omar.DbAppender at the top
    appender new DbAppender(name: "wmsLoggingAppender",
            threshold: org.apache.log4j.Level.INFO,
            tableMapping: [width: ":width", height: ":height", layers: ":layers", styles: ":styles",
                    format: ":format", request: ":request", bbox: ":bbox", internal_time: ":internalTime",
                    render_time: ":renderTime", total_time: ":totalTime", start_date: ":startDate",
                    end_date: ":endDate", user_name: ":userName", ip: ":ip", url: ":url", mean_gsd: ":meanGsd",
                    geometry: "ST_GeomFromText(:geometry, 4326)"],
            tableName: "wms_log"
    )
    appender new org.apache.log4j.DailyRollingFileAppender(name: "omarDataManagerAppender",
            datePattern: "'.'yyyy-MM-dd",
            file: "/tmp/logs/omarDataManagerAppender.log",
            layout: pattern(conversionPattern: '[%d{yyyy-MM-dd hh:mm:ss.SSS}] %p %c{5} %m%n'))
    appender new org.apache.log4j.DailyRollingFileAppender(name: "omarAppender",
            datePattern: "'.'yyyy-MM-dd",
            file: "/tmp/logs/omar.log",
            layout: pattern(conversionPattern: '[%d{yyyy-MM-dd hh:mm:ss.SSS}] %p %c{5}  %m%n'))
  }

  info wmsLoggingAppender: 'grails.app.service.org.ossim.omar.WmsLogService', additivity: false
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
}
//log4j.logger.org.springframework.security='off,stdout'

/** *********************************************************************************************************/
wms {
  referenceDataDirectory = "/data/omar"
  mapServExt = (System.properties['os.name'].startsWith('Windows')) ? ".exe" : ""
  serverAddress = grails.serverIP
  useTileCache = false
  mapFile = "${referenceDataDirectory}/bmng.map"

  base {
    defaultOptions = [isBaseLayer: true, buffer: 0, transitionEffect: "resize"]
    layers = [
            [
                    url: "http://hyperquad.ucsd.edu/cgi-bin/i-cubed",
                    params: [layers: "icubed", format: "image/png", transparent: true, bgcolor: '#99B3CC'],
                    name: "I-Cubed LandSat",
                    options: defaultOptions
            ],
            [
                    url: "http://hyperquad.ucsd.edu/cgi-bin/onearth",
                    params: [layers: "OnEarth", format: "image/png", transparent: true, bgcolor: '#99B3CC'],
                    name: "OnEarth LandSat",
                    options: defaultOptions
            ],
            [
                    url: (useTileCache) ? "http://${serverAddress}/tilecache/tilecache.py" : "http://${serverAddress}/cgi-bin/mapserv${wms.mapServExt}?map=${mapFile}",
                    params: [layers: (useTileCache) ? "omar" : "Reference", format: "image/jpeg"],
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
            url: "${grails.serverURL}/wms/footprints",
            params: [layers: (supportIE6) ? "Imagery" : "ImageData", format: (supportIE6) ? "image/gif" : "image/png"],
            name: "OMAR Imagery Coverage",
            options: [styles: "green", footprintLayers: "Imagery"]
    ]

    video = [
            url: "${grails.serverURL}/wms/footprints",
            params: [layers: (supportIE6) ? "Videos" : "VideoData", format: (supportIE6) ? "image/gif" : "image/png"],
            name: "OMAR Video Coverage",
            options: [styles: "red", footprintLayers: "Videos"]
    ]
  }

  // Note the colors are normalized floats
  styles = [
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

  vector {
    maxcount = 10000
  }
}


thumbnail {
  cacheDir = (System.properties["os.name"] == "Windows XP") ? "c:/temp" : "${wms.referenceDataDirectory}/omar-cache"
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

image.download.prefix = "http://${grails.serverIP}"

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
  flashDirRoot = "/Library/WebServer/Documents/videos"
  //flashDirRoot = "/var/www/html/videos"
  flashUrlRoot = "http://${grails.serverIP}/videos"
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
grails.doc.images = new File("web-app/images")

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
            groundGeom: { def bounds = it.envelopeInternal; [bounds.minX, bounds.minY, bounds.maxX, bounds.maxY].join(',') }
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
            groundGeom: { def bounds = it.envelopeInternal; [bounds.minX, bounds.minY, bounds.maxX, bounds.maxY].join(',') }
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

// LDAP Configuration
grails.plugins.springsecurity.ldap.active = false
//grails.plugins.springsecurity.ldap.context.server = 'ldap://sles11-ldap-server'
//grails.plugins.springsecurity.ldap.context.managerDn = 'cn=Administrator,dc=otd,dc=radiantblue,dc=com' //
//grails.plugins.springsecurity.ldap.context.userDn = 'dc=otd,dc=radiantblue,dc=com' //

//grails.plugins.springsecurity.ldap.context.managerPassword = 'omarldap'                                //
//grails.plugins.springsecurity.ldap.search.base = 'ou=people,dc=otd,dc=radiantblue,dc=com'
//grails.plugins.springsecurity.ldap.authorities.retrieveGroupRoles = false
//grails.plugins.springsecurity.ldap.authorities.retrieveDatabaseRoles = true
//grails.plugins.springsecurity.ldap.search.searchSubtree = true
//grails.plugins.springsecurity.ldap.authorities.groupSearchBase = 'ou=group,dc=otd,dc=radiantblue,dc=com'
//grails.plugins.springsecurity.ldap.authorities.groupSearchBase = 'ou=group,memberUid=demo,dc=otd,dc=radiantblue,dc=com'

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
            acquisitionDate: 'Acquistion Date',
            width: 'Width',
            height: 'Height',
    ]
  }
}

//bundle {
//  combine = true
//  compress = true
//}