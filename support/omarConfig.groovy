//grails.serverIP=org.ossim.omar.app.NetUtil.ipAddress
//grails.serverURL= "http://${grails.serverIP}/omar"
wms.referenceDataDirectory="/data/omar"
wms.mapFile= "${wms.referenceDataDirectory}/bmng.map"
thumbnail.cacheDir="${wms.referenceDataDirectory}/omar-cache"
videoStreaming.flashDirRoot="/var/www/html/videos"
image.download.prefix = "http://${grails.serverIP}"



wms.base.defaultOptions = [isBaseLayer: true, buffer: 0, transitionEffect: "resize"]
wms.data.mapFile = "${wms.referenceDataDirectory}/omar-2.0-prod.map"
wms.supportIE6=true
wms.base.layers = [
            [
                    url: "http://${grails.serverIP}/cgi-bin/mapserv.sh?map=${wms.mapFile}",
                    params: [layers: "Reference", format: "image/jpeg"],
                    name: "Reference Data",
                    options: defaultOptions
            ],
            [
                    url: "http://hyperquad.ucsd.edu/cgi-bin/onearth",
                    params: [layers: "OnEarth", format: "image/png", BGCOLOR: "#99B3CC"],
                    name: "Landsat",
                    options: defaultOptions
            ],
            [
                    url: "http://hyperquad.ucsd.edu/cgi-bin/i-cubed",
                    params: [layers: "icubed", format: "image/png", BGCOLOR:"#99B3CC"],
                    name: "ICubed Landsat",
                    options: defaultOptions
            ],

    ]

wms.data.raster = [
            url: "${grails.serverURL}/wms/footprints",
            params: [layers: (wms.supportIE6) ? "Imagery" : "ImageData", format: (wms.supportIE6) ? "image/gif" : "image/png"],
            name: "OMAR Imagery Coverage",
            options: [styles: "green", footprintLayers: "Imagery"]
    ]

wms.data.video = [
            url: "${grails.serverURL}/wms/footprints",
            params: [layers: (wms.supportIE6) ? "Videos" : "VideoData", format: (wms.supportIE6) ? "image/gif" : "image/png"],
            name: "OMAR Video Coverage",
            options: [styles: "red", footprintLayers: "Videos"]

    ]

// Values can be
// MD2, MD5, SHA-1, SHA-256, SHA-384, SHA-512
//
grails.plugins.springsecurity.password.algorithm = 'MD5'
grails.plugins.springsecurity.password.encodeHashAsBase64 = true

// LDAP Configuration
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

login.registration.enabled=true
login.registration.userVerification="manual"
login.registration.createLdapUser=false
login.registration.useMail=(userVerification == "email")

rasterEntry.tagHeaderList = [
          "File Type",
          "Class Name",
          "Mission",
          "Country",
          "Target Id",
          "BE",
          "Sensor",
          "Image Id"
  ]


rasterEntry.tagNameList = [
          "fileType",
          "className",
          "missionId",
          "countryCode",
          "targetId",
          "beNumber",
          "sensorId",
          "title"
  ]

rasterEntry.searchTagData = [
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

videoDataSet.searchTagData= [
          [name: "filename", description: "Feed"]
  ]

