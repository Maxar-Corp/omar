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

  <title>OMAR Ground Space Multi-Viewer</title>

  <openlayers:loadMapToolBar/>
  <openlayers:loadTheme theme="default"/>
  <openlayers:loadJavascript/>

  <style type="text/css">
    #map {
      width: 100%;
      height: 100%;
      border: 1px solid black;
    }
  </style>

  <g:javascript plugin="omar-core" src="touch.js"/>
</head>

<body>

<content tag="north">

  <span class="menuButton">
    <g:link class="home" uri="/">OMAR&#0153;</g:link>
  </span>

  <span class="menuButton">
    <a href="${createLink(controller: "mapView", action: "index", params: [layers: (rasterEntries*.indexId).join(',')])}">
      Single Layer
    </a>
  </span>

  <g:if test="${rasterEntries?.size() == 1}">
    <span class="menuButton">
      <a href="${createLink(controller: "mapView", action: "imageSpace", params: [layers: (rasterEntries*.id).join(',')])}">
        Image Space
      </a>
    </span>
  </g:if>

</content>

<content tag="center">
  <div id="map"></div>
</content>

<content tag="south">
  <g:javascript>
    var map;

    function changeMapSize(mapWidth, mapHeight)
    {
      var Dom = YAHOO.util.Dom;

      Dom.get( "map" ).style.width = mapWidth + "px";
      Dom.get( "map" ).style.height = mapHeight + "px";

      map.updateSize( );
    }

    function setupBaseLayer()
    {
      var baseLayer = null;

    <g:each var="foo" in="${baseWMS}">
    baseLayer = new OpenLayers.Layer.WMS(
    "${foo.title}",
              "${foo.url}",
      {layers: '${foo.layers}', format: "${foo.format}" },
      {isBaseLayer:true, buffer:0,transitionEffect: "resize"}
              );
      map.addLayer( baseLayer );
      map.setBaseLayer( baseLayer );
  </g:each>
    }

  function init(mapWidth, mapHeight)
  {
  var left = ${left};
      var bottom = ${bottom};
      var right = ${right};
      var top = ${top};

      map = new OpenLayers.Map( "map", { controls: [], numZoomLevels: 32 } );

      var format = "image/png";
      var transparent = true;

      setupBaseLayer( );

      var layers = [

    <g:each var="rasterEntry" in="${rasterEntries}" status="i">

      <g:if test="${i > 0}">,</g:if>

      new OpenLayers.Layer.WMS(
      "Raster ${rasterEntry.id}",
                "${createLink(controller: 'ogc', action: 'wms')}",
        { layers: "${rasterEntry.id}", format: format, stretch_mode:"linear_auto_min_max", transparent:transparent  },
        {isBaseLayer: false, buffer:0, singleTile:true, ratio:1.0, transitionEffect: "resize"}
                )
      <g:if test="${hasKML}">

        , new OpenLayers.Layer.Vector( "KML", {
     projection: map.displayProjection,
     strategies: [new OpenLayers.Strategy.Fixed( )],
     protocol: new OpenLayers.Protocol.HTTP( {
       url: "${createLink(controller: 'rasterEntry', action: 'getKML', params: [rasterEntryIds: rasterEntry.id])}",
            format: new OpenLayers.Format.KML( {
              extractStyles: true,
              extractAttributes: true
            } )
          } )
        } )
      </g:if>
    </g:each>
    ];

     map.addLayers( layers );
     map.addControl( new OpenLayers.Control.Scale( ) );
     map.addControl( new OpenLayers.Control.ScaleLine( ) );

     var bounds = new OpenLayers.Bounds( left, bottom, right, top );

     map.maxExtent = bounds;
     changeMapSize( mapWidth, mapHeight );

     var zoom = map.getZoomForExtent( bounds, true );

     map.setCenter( bounds.getCenterLonLat( ), zoom );

     this.touchhandler = new TouchHandler( map, 4 );
    }

  </g:javascript>

</content>
</body>
</html>