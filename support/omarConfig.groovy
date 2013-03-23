//omar.serverIP=
//omar.serverURL=
//wms.referenceDataDirectory=""
//wms.mapFile=

thumbnail.cacheDir="${wms.referenceDataDirectory}/omar-cache"
//videoStreaming.flashDirRoot=
image.download.prefix = "http://${omr.serverIP}"

wms.base.defaultOptions = [isBaseLayer: true, buffer: 0, transitionEffect: "resize"]
wms.data.mapFile = "${wms.referenceDataDirectory}/bmng.map"
wms.supportIE6=true

wms.base.layers = [
        [
                url: "http://${omar.serverIP}/cgi-bin/mapserv.exe?map=${wms.mapFile}",
                params: [layers: "Reference", format: "image/jpeg"],
                name: "Reference Data",
                options: wms.base.defaultOptions
        ]

]


wms.data.raster = [
        url: "${ omar.serverURL }/wms/footprints",
        params: [layers: ( wms.supportIE6) ? "Imagery" : "ImageData", format: ( wms.supportIE6) ? "image/gif" : "image/png"],
        name: "OMAR Imagery Coverage",
        options: [styles: "byFileType", footprintLayers: "Imagery"]
]

wms.data.video = [
        url: "${ omar.serverURL }/wms/footprints",
        params: [layers: ( wms.supportIE6) ? "Videos" : "VideoData", format: ( wms.supportIE6) ? "image/gif" : "image/png"],
        name: "OMAR Video Coverage",
        options: [styles: "red", footprintLayers: "Videos"]
]

login.registration.enabled=true
login.registration.userVerification="manual"
login.registration.createLdapUser=false
login.registration.useMail=(login.registration.userVerification== "email")

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

