<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: Jan 21, 2010
  Time: 9:54:28 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>Layout Test</title>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="main3"/>

  <openlayers:loadMapToolBar/>
  <openlayers:loadTheme theme="default"/>
  <openlayers:loadJavascript/>

  <g:javascript src="mapping-widget.js"/>
  <g:javascript>
  // your own js code is here
  var lon = ${centerLon};
  var lat = ${centerLat};
  var zoom = ${zoomLevel};

  var baseLayers = new Array();
  var overlayLayers = new Array();

  var mapWidget;

  // define your init function
  var init = function()
  {
    <g:each in="${baseLayers}">
      baseLayers.push({title: '${it.title}', url: '${it.url}', name: '${it.name}'});
    </g:each>

    <g:each in="${overlayLayers}">
      overlayLayers.push( {title: '${it.title}', url: '${it.url}', name: '${it.name}'} );
    </g:each>

    mapWidget = new MappingWidget(lat, lon, zoom, baseLayers, overlayLayers);
  };


  var setView = function( e )
  {
  /*
  var bounds = map.getExtent();

  $("viewMinLon").value = bounds.left;
  $("viewMaxLat").value = bounds.top;
  $("viewMaxLon").value = bounds.right;
  $("viewMinLat").value = bounds.bottom;
  */
  };

  var setCenterText = function( e )
  {
  /*
  var center = map.getCenter();

  $("centerLon").value = center.lon;
  $("centerLat").value = center.lat;
  */
  };

  var resized = function( mapWidth, mapHeight )
  {
    //alert( "B: " + mapWidth + " " + mapHeight );

    //Dom.get( "map" ).style.width = mapWidth + "px";
    //Dom.get( "map" ).style.height = mapHeight + "px";

    //mapWidget.updateSize();  
  };


  var clearAOI = function ( e )
  {
  //    aoiLayer.destroyFeatures();

  // HACK - Need a better way to this
  /*
    $("aoiMinLon").value = ""
    $("aoiMaxLat").value = ""
    $("aoiMaxLon").value = ""
    $("aoiMinLat").value = ""
  */
  };

  var setAOI = function ( e )
  {
  /*
  var geom = e.feature.geometry;
  var bounds = geom.getBounds();
  var feature = new OpenLayers.Feature.Vector(geom);

  // HACK - Need a better way to this
  $("aoiMinLon").value = bounds.left
  $("aoiMaxLat").value = bounds.top
  $("aoiMaxLon").value = bounds.right
  $("aoiMinLat").value = bounds.bottom

  aoiLayer.destroyFeatures();
  aoiLayer.addFeatures(feature, {silent: true});
  */
  };
  </g:javascript>
</head>
<!-- define your page content -->
<body class="yui-skin-sam">

<content tag="layout1.top.content">
  <div id="panel2" class="olControlPanel"></div>
</content>

<content tag="layout1.bottom.content">
</content>

<content tag="layout1.right.content">
</content>

<content tag="layout1.left.content">
</content>

<content tag="layout1.center.content">
  <div id="map"></div>
</content>

</body>
</html>