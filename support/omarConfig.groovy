// If you need access to the appName or app version you must use the following
// grails.util.Metadata.current.'app.name'
// grails.util.Metadata.current.'app.version'
//
//
import java.awt.Color

//omar.serverIP="10.0.10.207"
//omar.serverURL="http://${omar.serverIP}/omar"
//wms.referenceDataDirectory=
//wms.mapFile=
//thumbnail.cacheDir="${wms.referenceDataDirectory}/omar-cache"
//videoStreaming.flashDirRoot="/var/www/html/videos"
//videoStreaming.flashUrlRoot="http://${omar.serverIP}/videos"
//image.download.prefix = "http://${omar.serverIP}"

wms.base.defaultOptions = [isBaseLayer: true, numZoomLevels: 20, buffer: 0, transitionEffect: "resize"]
wms.supportIE6=true
wms.serverAddress = omar.serverIP

wms.base.layers = [
        [
                url: "http://${omar.serverIP}/cgi-bin/mapserv?map=${wms.mapFile}",
                params: [layers: "Reference", format: "image/jpeg"],
                name: "Reference Data",
                options: wms.base.defaultOptions
        ],
        [
                 url: "${omar.serverURL}/ogc/wms",
                 params: [resampler:bilinear,layers: "auto_raster_entry", filter:"", format: "image/png", transparent: true, EXCEPTIONS:"application/vnd.ogc.se_blank"],
                 name: "OMAR Auto Mosaic",
                 //options: [isBaseLayer: false, minScale:0.0000001, maxScale:0.0001, buffer: 0, transitionEffect: "resize"]
                 options: [visibility:false, isBaseLayer: false,  singleTile: false,  tileSize:[w:512,h:512], buffer: 0, transitionEffect: "resize"]
         ] ,
         // this is a hack until we can do it properly. Note,  this is tested in the GUI and then the layers is set to null and is
         // directly edited by the selected rows to put in the mosaic
         //
        [
                url: "${omar.serverURL}/ogc/wms",
                params: [resampler:bilinear,layers: "selected_raster_entry", filter:"", format: "image/png", transparent: true, EXCEPTIONS:"application/vnd.ogc.se_blank"],
                name: "OMAR Selected Image Mosaic",
                //options: [isBaseLayer: false, minScale:0.0000001, maxScale:0.0001, buffer: 0, transitionEffect: "resize"]
                options: [visibility:false, isBaseLayer: false,  singleTile: false,  tileSize:[w:512,h:512], buffer: 0, transitionEffect: "resize"]
                ]

]


wms.data.raster = [
        url: "${ omar.serverURL }/wms/footprints",
        params: [styles: "byFileType", transparent:true, layers: ( wms.supportIE6) ? "Imagery" : "ImageData", format: ( wms.supportIE6) ? "image/gif" : "image/png"],
        name: "OMAR Imagery Coverage",
        options: [isBaseLayer:false, footprintLayers: "Imagery"]
]

wms.data.video = [
        url: "${ omar.serverURL }/wms/footprints",
        params: [styles: "byFileType", transparent:true, layers: ( wms.supportIE6) ? "Videos" : "VideoData", format: ( wms.supportIE6) ? "image/gif" : "image/png"],
        name: "OMAR Video Coverage",
        options: [isBaseLayer:false, footprintLayers: "Videos"]
]

wms.styles = [
      byFileType: [
          adrg: [filter: "file_type='adrg'", color: new Color( 50, 111, 111, 255 )],
          aaigrid: [filter: "file_type='aaigrid'", color: 'pink'],
          cadrg: [filter: "file_type='cadrg'", color: new Color( 0, 255, 255, 255 )],
          ccf: [filter: "file_type='ccf'", color: new Color( 128, 100, 255, 255 )],
          cib: [filter: "file_type='cib'", color: new Color( 0, 128, 128, 255 )],
          doqq: [filter: "file_type='doqq'", color: 'purple'],
          dted: [filter: "file_type='dted'", color: new Color( 0, 255, 0, 255 )],
          imagine_hfa: [filter: "file_type='imagine_hfa'", color: 'lightGray'],
          jpeg: [filter: "file_type='jpeg'", color: new Color( 255, 255, 0, 255 )],
          jpeg2000: [filter: "file_type='jpeg2000'", color: new Color( 255, 200, 0, 255 )],
          landsat7: [filter: "file_type='landsat7'", color: new Color( 255, 0, 255, 255 )],
          mrsid: [filter: "file_type='mrsid'", color: new Color( 0, 188, 0, 255 )],
          nitf: [filter: "file_type='nitf'", color: new Color( 0, 0, 255, 255 )],
          tiff: [filter: "file_type='tiff'", color: new Color( 255, 0, 0, 255 )],
          mpeg: [filter: "file_type='mpeg'", color: new Color( 164, 254, 255, 255 )],
          unspecified: [filter: "file_type='unspecified'", color: 'white']
      ],
      bySensorType: [
          'ACES_YOGI-HRI1': [filter: "mission_id='ACES_YOGI-HRI1'", color: new Color( 255, 0, 0, 255 )],
          'ACES_YOGI-HRI2': [filter: "mission_id='ACES_YOGI-HRI2'", color: new Color( 255, 0, 0, 255 )],
          'ACES_YOGI-HRI3': [filter: "mission_id='ACES_YOGI-HRI3'", color: new Color( 255, 0, 0, 255 )],
          'ACES_YOGI-HSI': [filter: "mission_id='ACES_YOGI-HSI'", color: new Color( 255, 255, 0, 255 )],
          ALPHA: [filter: "mission_id='ALPHA'", color: new Color( 255, 0, 255, 255 )],
          BRAVO: [filter: "mission_id='BRAVO'", color: new Color( 0, 255, 0, 255 )],
          CHARLIE: [filter: "mission_id='CHARLIE'", color: new Color( 0, 255, 255, 255 )],
          DELTA: [filter: "mission_id='DELTA'", color: new Color( 0, 0, 255, 255 )],
          unspecified: [filter: "mission_id='unspecified'", color: new Color( 255, 255, 255, 255 )] // white
      ],
      byVideoType: [
          mpeg: [filter: "filename like '%mpg'", color: new Color( 255, 0, 0, 255 )]
      ]

  ]



login.registration.enabled=true
login.registration.userVerification="manual"
login.registration.createLdapUser=false
login.registration.useMail=(login.registration.userVerification== "email")

jabber.securityMode = "disabled"

piwik.analytics.enabled = false
piwik.analytics.url = "http://example.com/piwik"
piwik.analytics.siteid = 1

federation {
  enabled = true
}

feedback {
  enabled = false
  mailto = ""
  subject = "OMAR Feedback"
}

//security {
//  level = 'UNCLASS'
//level = 'SECRET'
//level = 'TOPSECRET'
//    UNCLASS = [description: "Unclassified", color: "green"]
//    SECRET = [description: "Secret // NOFORN", color: "red"]
//    TOPSECRET = [description: "Top Secret", color: "yellow"]
//    sessionTimeout = 60
//    level = "UNCLASS"
//}

//dataSource.pooled = true
//dataSource.driverClassName = ( useP6Spy ) ? "com.p6spy.engine.spy.P6SpyDriver" : "org.postgis.DriverWrapper"
//dataSource.username = "postgres"
//dataSource.password = "postgres"
//dataSource.dialect = org.ossim.omar.postgis.PostGISDialect
//dataSource.logSql = true
//dataSource.url="jdbc:postgresql_postGIS://localhost/omardb-${grails.util.Metadata.current.'app.version'}-prod"

job{
  maxInputs=10
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
//    }
}

// the WMS layer is called "auto_raster_entry" for wms chipping.  If this layer exists, then
// how the layer is rendered is defined here in the autoMosaic settings.
//
autoMosaic{
   annotation{
      // Comma separated list of fields to annotate each tile with.
      // For now we only annotate the tile with the top most field in the
      // mosaic. For now we will center the text in the tile and render on separate lines
      fields = [[name:"title", width:24],[name:"acquisitionDate"]]
      font{
         // TimesRoman, Courier, SansSerif, Serif, Helvetica
         name      = "SansSerif"

         // Can be BOLD, ITALIC, PLAIN
         style     = "BOLD"

         size      = 12
         antiAlias = true
         // color is a normalized RGBA component
         color     = [1.0,1.0,1.0,1.0]
      }
      // alignment type can be CENTER, TOP_CENTER, BOTTOM_CENTER
      align = "CENTER"
   }

   // defines the maxCount in the mosaic process
   maxResults = 10

   // GSD is in meters per pixel
  // minGsd = 0.001
  // maxGsd = 10

   // If a fixed minGsd and maxGsd range is not set then the gsd range will floa
   // based on the current zoom location gsd on the requested tile
   //  if a tile request gives you an average gsd of 1 meter and the minGsd and maxGsd is not set
   // but the minGsdScale and maxGsdScale is set then the range of imagery used for the mosaic
   // is
   //    requestGsd*minGsdScale >= requestGsd <= requestGsd*maxGsdScale
   //
   // This is a floating window for the scale range where the center is the calculated request
   // gsd of the tile being rendered
   minGsdScale = 1.0/16
   maxGsdScale = 16

}

views {
  home {
    rssEnabled = true
  }
}


