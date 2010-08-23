<%--
  Created by IntelliJ IDEA.
  User: dlucas
  Date: June 11, 2010
  Time: 11:04:28 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>

<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="main6_mobile"/>

  <meta name="apple-mobile-web-app-capable" content="yes" />
  <meta name="apple-mobile-web-app-status-bar-style" content="black" />
  <meta name="viewport" content="minimum-scale=1.0, width=device-width, maximum-scale=1.6, user-scalable=no">

  <title>OMAR Image Space Viewer</title>

  <openlayers:loadMapToolBar/>
  <openlayers:loadTheme theme="default"/>

  <style type="text/css">
    #map {
      width: 100%;
      height: 100%;
      border: 1px solid black;
    }
  </style>

  <openlayers:loadJavascript/>

  <g:javascript plugin="omar-core" src="touch.js"/>
</head>

<body>

<content tag="north">
  <span class="menuButton">
    <g:link class="home" uri="/">Home</g:link>
  </span>

  <span class="menuButton">
    <a href="${createLink(controller: "mapView", action: "index", params: [layers: rasterEntry?.indexId])}">
      Ground Space
      </a>
  </span>

  <span class="menuButton">
    <label>Stretch:</label>
    <g:select id="stretch_mode" name="stretch_mode" from="${['linear_auto_min_max', 'linear_1std_from_mean', 'linear_2std_from_mean', 'linear_3std_from_mean', 'none']}" onChange="changeHistoOpts()"/>
  </span>

  <span class="menuButton">
    <label>Region:</label>
    <g:select id="stretch_mode_region" name="stretch_mode_region" from="${['global', 'viewport']}" onChange="changeHistoOpts()"/>
  </span>
</content>

<content tag="center">
  <div id="map"></div>
</content>

<content tag="south">
  <g:javascript>
  var map;
  var layer;

  function changeMapSize(mapWidth, mapHeight)
  {
    var Dom = YAHOO.util.Dom;

    Dom.get( "map" ).style.width = mapWidth + "px";
    Dom.get( "map" ).style.height = mapHeight + "px";

    map.updateSize();
  }

 function changeHistoOpts()
{
  var stretch_mode = $("stretch_mode").value;
  var stretch_mode_region = $("stretch_mode_region").value;


  layer.mergeNewParams({stretch_mode:stretch_mode, stretch_mode_region: stretch_mode_region});
}

  function get_my_url (bounds)
  {
      var res = this.map.getResolution();
      var x = /*Math.round*/ ((bounds.left - this.maxExtent.left) / (res * this.tileSize.w));
      var y = /*Math.round*/ ((this.maxExtent.top - bounds.top) / (res * this.tileSize.h));
      var z = this.map.getZoom();
      var stretch_mode = $("stretch_mode").value;
      var stretch_mode_region = $("stretch_mode_region").value;

      var path = "?z=" + z + "&x=" + x + "&y=" + y + "&format=" + this.type
          + "&tileWidth=" + this.tileSize.w + "&tileHeight=" + this.tileSize.h
          + "&id=" + ${rasterEntry?.id} + "&stretch_mode=" + stretch_mode
          + "&stretch_mode_region=" + stretch_mode_region;

      var url = this.url;
      if (url instanceof Array) {
          url = this.selectUrl(path, url);
      }
      return url + path;
  }

  function init(mapWidth, mapHeight)
  {
    map = new OpenLayers.Map('map', { controls: [], numZoomLevels: 32 } );
    map.addControl(new OpenLayers.Control.Scale());
    map.addControl(new OpenLayers.Control.ScaleLine());

    var options = {
      maxExtent: new OpenLayers.Bounds(0,0,${width},${height}),
      maxResolution: ${width} / map.getTileSize().w,
        numZoomLevels: 30,
      type:'jpeg',
      getURL: get_my_url,
      isBaseLayer: true,
      buffer: 0,
      singleTile: true,
      ratio: 1.0,
      transitionEffect: "resize"
    };


    layer = new OpenLayers.Layer.TMS("Layer",
        "${createLink(controller: 'ogc', action: 'getTile')}",
        options
    );


    changeMapSize(mapWidth, mapHeight);

    map.addLayers([layer]);
    map.zoomToMaxExtent();
    //setupToolbar();

    this.touchhandler = new TouchHandler( map, 4 );
  }

  </g:javascript>
</content>
</body>
</html>
