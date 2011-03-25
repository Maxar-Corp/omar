<html>
<head>
  <openlayers:loadTheme/>
  <openlayers:loadJavascript/>
  <style type="text/css">
  #map {
    width: 100%;
    height: 100%;
  }
  </style>
  <g:javascript>
var map, layer;
function init()
{
    map = new OpenLayers.Map( 'map' );
    map.addControl(new OpenLayers.Control.LayerSwitcher());
    
    var baseURL = "${createLink(controller: "footprint", action: 'footprints')}";
    var imageType = "image/gif"
    
        
    layers  = [
        new OpenLayers.Layer.WMS( "Reference",
            //"http://${InetAddress.localHost.hostAddress}/tilecache/tilecache.py",
            "${createLink(controller: 'wms', action: 'getMap')}",
            {layers: 'omar', format: "image/gif", bgColor: "#99B3CC", styles: "{fillOpacity: 1.0, fillColor: '#BDDE83', strokeColor: '#000000', strokeWidth: 1}"},
            {isBaseLayer: true, buffer: 0, transitionEffect: "resize" /*,singleTile: true, ration: 1.0*/}
            ),

        new OpenLayers.Layer.WMS( "DTED Footprints",
                baseURL,
                {layers: "raster_entry", styles: "{fillOpacity:0,strokeColor:'#FFFF00'}", format: imageType, filter: "file_type = 'dted'"},
                {isBaseLayer: false, buffer: 0, transitionEffect: "resize"}
                ),

        new OpenLayers.Layer.WMS( "NITF Footprints",
                baseURL,
                {layers: "raster_entry", styles: "{fillOpacity:0,strokeColor:'#FF0000'}", format: imageType, filter: "file_type = 'nitf'"},
                {isBaseLayer: false, buffer: 0, transitionEffect: "resize"}
                ),

        new OpenLayers.Layer.WMS( "GeoTIFF Footprints",
                baseURL,
                {layers: "raster_entry", styles: "{fillOpacity:0,strokeColor:'#0000FF'}", format: imageType, filter: "file_type = 'tiff'"},
                {isBaseLayer: false, buffer: 0, transitionEffect: "resize"}
                ),

        new OpenLayers.Layer.WMS( "JPEG Footprints",
                baseURL,
                {layers: "raster_entry", styles: "{fillOpacity:0,strokeColor:'#00FFFF'}", format: imageType, filter: "file_type = 'jpeg'"},
                {isBaseLayer: false, buffer: 0, transitionEffect: "resize"}
                ),

        new OpenLayers.Layer.WMS( "CADRG Footprints",
                baseURL, 
                {layers: "raster_entry", styles: "{fillOpacity:0,strokeColor:'#00FF00'}", format: imageType, filter: "file_type = 'cadrg'"},
                {isBaseLayer: false, buffer: 0, transitionEffect: "resize"}
//                {isBaseLayer: false, buffer: 0, singleTile: true, ration: 1.0, transitionEffect: "resize"}
                )
                                        
    ];        
    map.addLayers(layers);
    map.zoomToMaxExtent();
}
  </g:javascript>
</head>
<body onload="init()">
<div id="map"></div>
</body>
</html>
