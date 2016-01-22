# OSSIM Mapping Archive (OMAR™) 

OMAR™ is a multi-INT data archive and retrieval service used for the exploitation of multi-INT data. Access to the data can be accomplished by either an AJAX-enabled web client or a web service request via SOAP. OMAR™ can be used to query multi-INT data sources based on geospatial bounds, sensor, collector, and temporal attributes. The imagery products and associated metadata available as a result of an OMAR™ query can be packaged in either its native format or a specified format. OMAR™ includes a data ingest stager that inserts metadata into a relational database model for subsequent retrieval.

For more information, visit the OMAR™ site at [ossim.org](https://ossim.org)

Cross-Browser testing generously provided by [BrowserStack](https://s3.amazonaws.com/ossim.org/img/browserstack.png)

# External Configuration File

OMAR allows one to override configuration options via an external config file.  We typically link to this config and is auto loaded by specifying it's location with an environment variable called *OMAR_CONFIG*. Edit the config file and modify for your environment.

***Set the ip of the OMAR™ Server***

```
omar.serverIP = "<ip>"
omar.serverURL = "http://${omar.serverIP}:${System.properties.'server.port' ?: '8080'}/omar"
grails.serverURL = omar.serverURL
```
If you are going through a proxy and not directly to a port then the omar.serverURL will have the following syntax

```
omar.serverURL = "http://${omar.serverIP}/omar"
```

***Setup Security Keywords***

There are three preset values UNCLASS, SECRET, TOPSECRET.  The description is what's displayed in the banners on all pages and the color defines the color of the banner.   Setting the level value to one of these three will define the banners used on the OMAR server

```
security {
 		UNCLASS = [description: "Unclassified", color: "green"]
 		
  		SECRET = [description: "Secret // NOFORN", color: "red"]
  		TOPSECRET = [description: "Top Secret", color: "yellow"]
  		sessionTimeout = 60
  		level = "UNCLASS"
}
```

***Setup Pre-defined Mosaick layers***

Within OMAR we have two specially named layers that are reserved to enable *Selected Mosaicking* and *Auto Mosaicking*.   The layers are added to the base map keywords used for openlayers

```
wms.base.defaultOptions = [isBaseLayer: true, numZoomLevels: 20, buffer: 0, transitionEffect: "resize"]
```
The first layer is for a base reference map.   You can modify the **url:** field to point to any WMS server you want as a base reference layer.  For the next two layers the names are reserved and are identified by the layers params: ***layers: "auto\_raster_entry"*** and ***layers: "selected\_raster_entry"***.   These will show up on the seearch page and the ***selected\_raster_entry*** layer is used to allow one to select images in the result and have them display as an overlay to the map.  The ***auto\_raster_entry*** is used as an auto mosaicking capability that when you roam the map the images are queried and mosaicked as an overlay.

```
wms.base.layers = [
       [
            url: ( useTileCache ) ? "http://${serverAddress}/tilecache/tilecache.py" : "http://${serverAddress}/cgi-bin/mapserv${wms.mapServExt}?map=${mapFile}",
            //url: ( useTileCache ) ? "http://${ serverAddress }/tilecache/tilecache.py" : "http://omar.ngaiost.org/cgi-bin/mapserv.sh?map=${ mapFile }",
            params: [layers: ( useTileCache ) ? "omar" : "Reference", format: "image/jpeg"],
            name: "Reference Data",
            options: defaultOptions
        ],
        [
            url: "${omar.serverURL}/ogc/wms",
            params: [resampler: bilinear, layers: "auto_raster_entry", filter: "", format: "image/png", transparent: true, EXCEPTIONS: "application/vnd.ogc.se_blank"],
            name: "OMAR Auto Mosaic",
            //options: [isBaseLayer: false, minScale:0.0000001, maxScale:0.0001, buffer: 0, transitionEffect: "resize"]
            options: [visibility: false, isBaseLayer: false, singleTile: false, tileSize: [w: 512, h: 512], buffer: 0, transitionEffect: "resize"]
        ],
        [
            url: "${omar.serverURL}/ogc/wms",
            params: [resampler: bilinear, layers: "selected_raster_entry", filter: "", format: "image/png", transparent: true, EXCEPTIONS: "application/vnd.ogc.se_blank"],
            name: "OMAR Selected Image Mosaic",
            //options: [isBaseLayer: false, minScale:0.0000001, maxScale:0.0001, buffer: 0, transitionEffect: "resize"]
            options: [visibility: false, isBaseLayer: false, singleTile: false, tileSize: [w: 512, h: 512], buffer: 0, transitionEffect: "resize"]
        ]

]
```

The ***auto\_raster_entry*** layer's behavior can be controlled by the **autoMosaic** definition.   You can controll the font rendering by the **annotation** section.  The resolution at which the images begin to display can be controlled either by using a GSD in meters per pixel ***minGsd***  and ***maxGSD*** or via a min and max magnification factor identified by the keywords ***minGsdScale*** and ***maxGsdScale***.  You can control the maximum images used in the mosaick with the variable ***maxResults**.

```
autoMosaic {
  annotation {
    // Comma separated list of fields to annotate each tile with.
    // For now we only annotate the tile with the top most field in the
    // mosaic. For now we will center the text in the tile and render on separate lines
    fields = [[name: "title", width: 24], [name: "acquisitionDate"]]
    font {
      // TimesRoman, Courier, SansSerif, Serif, Helvetica
      name = "Courier"

      // Can be BOLD, ITALIC, PLAIN
      style = "BOLD"

      size = 18
      antiAlias = true
      // color is a normalized RGBA component
      color = [1.0, 1.0, 1.0, 0.5]
    }
    // alignment type can be CENTER, TOP_CENTER, BOTTOM_CENTER
    align = "BOTTOM_CENTER"
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
  minGsdScale = 1.0 / 16
  maxGsdScale = 16
}

```

To enable PIWIK logging of viewed pages you must specify the following keywords and modify for your piwik installation

```
piwik.analytics.enabled = true
piwik.analytics.url = "http://example.com/piwik"
piwik.analytics.siteid = 1
```

To enable Feedback we use the mailto syntax and let your mail client email feedback.  Set the enabled flag to true and modify the mailto to be the admin or some email you wish feedback to go to

```
feedback {
  enabled = false
  mailto = ""
  subject = "OMAR Feedback"
}
```

[![BrowserStack Logo](https://s3.amazonaws.com/ossim.org/img/browserstack.png)](https://browserstack.com)


Can't code without:

[![IntelliJ Idea Logo](https://www.jetbrains.com/idea/docs/logo_intellij_idea.png)](https://www.jetbrains.com/idea)
