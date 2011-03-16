<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: 3/14/11
  Time: 3:31 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>WFS Test Page</title></head>
<openlayers:loadTheme/>
<openlayers:loadJavascript/>
<style type="text/css">
#map {
  width: 100%;
  height: 100%;
}
</style>
<g:javascript>
  var map;
  OpenLayers.ProxyHost = "/cgi-bin/proxy.cgi?url=";
  function init()
  {
    map = new OpenLayers.Map( 'map', {
      controls: [
        new OpenLayers.Control.PanZoom(),
        new OpenLayers.Control.Permalink(),
        new OpenLayers.Control.Navigation()
      ]
    } );

    map.addLayer( new OpenLayers.Layer.WMS( "Reference",
            "http://${InetAddress.localHost.hostAddress}/tilecache/tilecache.py",
            {layers: 'omar', format: "image/jpeg"},
            {isBaseLayer: true, buffer: 0, transitionEffect: "resize"}
            )
    );

    layer = new OpenLayers.Layer.WMS(
    "Footprints",
    "http://${InetAddress.localHost.hostAddress}/cgi-bin/mapserv?map=/data/omar/omar-1.8.8-prod.map&",
    {layers: 'Imagery', format: 'image/gif', transparent: true},
    {isBaseLayer: false}
    );
    select = new OpenLayers.Layer.Vector( "Selection", {styleMap:
    new OpenLayers.Style( OpenLayers.Feature.Vector.style["select"] )
    } );
    hover = new OpenLayers.Layer.Vector( "Hover" );
    map.addLayers( [layer , hover, select] );

    control = new OpenLayers.Control.GetFeature( {
      protocol: OpenLayers.Protocol.WFS.fromWMSLayer( layer ),
      box: true,
      hover: true,
      multipleKey: "shiftKey",
      toggleKey: "ctrlKey"
    } );
    control.events.register( "featureselected", this, function( e )
    {
      select.addFeatures( [e.feature] );
    } );
    control.events.register( "featureunselected", this, function( e )
    {
      select.removeFeatures( [e.feature] );
    } );
    control.events.register( "hoverfeature", this, function( e )
    {
      hover.addFeatures( [e.feature] );
    } );
    control.events.register( "outfeature", this, function( e )
    {
      hover.removeFeatures( [e.feature] );
    } );
    map.addControl( control );
    control.activate();

    map.zoomToMaxExtent();
    map.addControl( new OpenLayers.Control.LayerSwitcher() );


  }
</g:javascript>
<body onload="init()">
<div id="map"></div>
</body>
</html>