<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: 2/13/12
  Time: 11:21 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>Cities Map</title>
  <meta content="main" name="layout">

  <link rel="stylesheet" href="${resource(plugin: 'openlayers', dir: 'js/theme/default', file: 'style.css')}"
        type="text/css">

  <style type="text/css">
  #map {
    background: #7391ad;
    border: 0;
    height: 512px;
    width: 1024px;
  }
  </style>
  <g:javascript plugin="openlayers" src="OpenLayers.js"/>
  <g:javascript>
    var lon = 0;
    var lat = 0;
    var map, layers;

    function init()
    {
      map = new OpenLayers.Map( 'map' );

      layers = [

        new OpenLayers.Layer.WMS( "OpenLayers WMS",
          "http://vmap0.tiles.osgeo.org/wms/vmap0", {layers: 'basic'} ),

        new OpenLayers.Layer.WMS( "Reference",
          "http://${InetAddress.localHost.hostAddress}/cgi-bin/mapserv?map=/data/omar/bmng.map&",
          {layers:'Reference', format: 'image/jpeg'},
          {buffer: 0, transitionEffect: 'resize'}
          ),
        new OpenLayers.Layer.WMS( "Cities",
          "${createLink(controller: 'cityMapView', action: 'wms')}",
          {layers:'city', format: 'image/png', styles: '{shape: {color: "#FF0000", type: "circle", size: 5}, fill: {color: "#000000", opacity: 0}, label: {property: "name"}}'},
          {buffer: 0, transitionEffect: 'resize', isBaseLayer: false}
          )
      ];


      map.addLayers( layers );
      map.zoomToMaxExtent();
      map.addControl( new OpenLayers.Control.LayerSwitcher() );
    }
  </g:javascript>
</head>

<body onload="init()">
<div class="nav">
  <span class="menuButton">
    <a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a>
  </span>
</div>

<div class="body">
  <br/>

  <div id="map"></div>
</div>

</body>
</html>