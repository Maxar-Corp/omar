//grails.serverIP=
//grails.serverURL=
//wms.referenceDataDirectory=
//wms.mapFile=

thumbnail.cacheDir="${wms.referenceDataDirectory}/omar-cache"
//videoStreaming.flashDirRoot=
image.download.prefix = "http://${grails.serverIP}"

wms.base.defaultOptions = [isBaseLayer: true, buffer: 0, transitionEffect: "resize"]
wms.data.mapFile = "${wms.referenceDataDirectory}/bmng.map"
wms.supportIE6=true

wms.base.layers = [
            [
                    url: "http://${grails.serverIP}/cgi-bin/mapserv.sh?map=${wms.mapFile}",
                    params: [layers: "Reference", format: "image/jpeg"],
                    name: "Reference Data",
                    options: defaultOptions
            ]

    ]

wms.data.raster = [
        url: "${ omar.serverURL }/wms/footprints",
        params: [layers: ( supportIE6 ) ? "Imagery" : "ImageData", format: ( supportIE6 ) ? "image/gif" : "image/png"],
        name: "OMAR Imagery Coverage",
        options: [styles: "byFileType", footprintLayers: "Imagery"]
]

wms.data.video = [
        url: "${ omar.serverURL }/wms/footprints",
        params: [layers: ( supportIE6 ) ? "Videos" : "VideoData", format: ( supportIE6 ) ? "image/gif" : "image/png"],
        name: "OMAR Video Coverage",
        options: [styles: "red", footprintLayers: "Videos"]
]

login.registration.enabled=true
login.registration.userVerification="manual"
login.registration.createLdapUser=false
login.registration.useMail=(userVerification == "email")
