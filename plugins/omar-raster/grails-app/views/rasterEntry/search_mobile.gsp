<%@ page import="org.ossim.omar.core.BaseQuery; org.ossim.omar.raster.RasterEntryQuery; org.ossim.omar.raster.RasterEntrySearchTag" contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <meta content="yes" name="apple-mobile-web-app-capable"/>
  <meta content="minimum-scale=1.0, width=device-width, user-scalable=no" name="viewport"/>
  <link rel="stylesheet" href="${resource(plugin: 'omar', dir: 'css', file: 'main.css')}"/>
  <title>OMAR: Raster Search</title>
</head>

<body onload="init()">

<div class="nav">
  <span class="menuButton"><g:link class="home" uri="/">OMAR™ Home</g:link></span>
  <span class="menuButton"><g:link class="search" url="javascript:search();">Search Rasters</g:link></span>
  <span class="menuButton"><g:link url="javascript:zoomIn();">Zoom In</g:link></span>
  <span class="menuButton"><g:link url="javascript:zoomOut();">Zoom Out</g:link></span>
  <span class="menuButton"><g:link url="javascript:zoomMaxExtent();">Max Extent</g:link></span>
</div>

<div class="body">
  <div id="map"></div>
</div>

<g:form name="searchForm">
  <input type="hidden" id="viewMinLon" name="viewMinLon" value=""/>
  <input type="hidden" id="viewMaxLat" name="viewMaxLat" value=""/>
  <input type="hidden" id="viewMaxLon" name="viewMaxLon" value=""/>
  <input type="hidden" id="viewMinLat" name="viewMinLat" value=""/>
</g:form>

</body>

<omar:bundle contentType="text/javascript" files="${[
        [plugin: 'openlayers', dir: 'js', file: 'OpenLayers.js'],
        [plugin: 'omar-core', dir: 'js', file: 'jquery.js'],
        [plugin: 'omar-core', dir: 'js', file: 'MultitouchHandler.js'],
        [plugin: 'omar-core', dir: 'js', file: 'MultitouchNavigation.js']
]}"/>

<script type="text/javascript">
  var map = null;

  function init()
  {
    map = new OpenLayers.Map( 'map', {controls: []} );

  <g:each var="base" in="${baseWMS}">
    var baseLayer = new OpenLayers.Layer.WMS( "${base.title}", "${base.url}",
    {layers: "${base.layers}", format: "${base.format}"},
    {isBaseLayer: true, buffer: 0, transitionEffect: "resize"} );
    map.addLayer( baseLayer );
    map.setBaseLayer( baseLayer );
  </g:each>

    var dataLayer = new OpenLayers.Layer.WMS( "${dataWMS.title}", "${dataWMS.url}",
    {layers: "${dataWMS.layers}", styles: "${dataWMS.styles}", format: "${dataWMS.format}", transparent: true},
    {isBaseLayer: false, buffer: 0, visibility: true, transitionEffect: "resize"} );
    map.addLayer( dataLayer );

    map.zoomToMaxExtent();
    map.zoomIn(); // hack for demo purposes...

    var touchControl = new OpenLayers.Control.MultitouchNavigation();
    map.addControl( touchControl );
  }

  function zoomIn()
  {
    map.zoomIn();
  }

  function zoomOut()
  {
    map.zoomOut();
  }

  function zoomMaxExtent()
  {
    map.zoomToMaxExtent();
  }

  function search()
  {
    document.searchForm.action = "search_mobile";

    var bounds = map.getExtent();
    document.getElementById( 'viewMinLon' ).value = bounds.left;
    document.getElementById( 'viewMaxLat' ).value = bounds.top;
    document.getElementById( 'viewMaxLon' ).value = bounds.right;
    document.getElementById( 'viewMinLat' ).value = bounds.bottom;

    document.searchForm.submit();
  }
</script>

</html>
