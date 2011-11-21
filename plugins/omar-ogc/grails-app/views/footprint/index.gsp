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
    <g:each in="${layerData}" var="currentLayer">
      new OpenLayers.Layer.WMS( "${currentLayer.name} Footprints",
              baseURL,
              {layers: "raster_entry", styles: "{fill: {opacity: 0}, stroke: {color:'${currentLayer.color}', width: 0.5}}", format: imageType, transparent: true, filter: "${currentLayer.filter}"},
              {isBaseLayer: false, buffer: 0, transitionEffect: "resize"}
              ),
    </g:each>

        new OpenLayers.Layer.WMS( "Reference",
          "${baseLayer}",
          {layers: 'omar', format: "image/jpeg"},
          {isBaseLayer: true, buffer: 0, transitionEffect: "resize"}
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
