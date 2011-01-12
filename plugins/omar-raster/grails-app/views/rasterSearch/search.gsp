<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: Aug 4, 2010
  Time: 2:39:26 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <meta name="layout" content="search"/>
  <title>Raster Search</title>
  <openlayers:loadTheme/>
</head>
<body>

<content tag="top">
</content>

<content tag="bottom">
</content>

<content tag="left">
</content>

<content tag="right">
</content>

<content tag="center">
  <div id="map"></div>
</content>
<content tag="scripts">
  <openlayers:loadJavascript/>
  <g:javascript>
    var bounds = new OpenLayers.Bounds( -180, -90, 180, 90 );
    var map, layer;

    function init()
    {
      map = new OpenLayers.Map( 'map' );
      layer = new OpenLayers.Layer.WMS( "Reference", "http://localhost/tilecache/tilecache.py", {layers: 'omar'} );

      map.addLayer( layer );
      map.addControl( new OpenLayers.Control.LayerSwitcher() );

      var center = layout.getUnitByPosition( 'center' );

      changeMapSize( center.get( 'width' ), center.get( 'height' ) );
      map.zoomToExtent( bounds );
    }

    function changeMapSize( width, height )
    {
      Dom.setStyle( "map", "width", width + "px" );
      Dom.setStyle( "map", "height", height + "px" );

      map.updateSize();
    }

    Event.onDOMReady( function()
    {
      init();
      layout.getUnitByPosition( 'center' ).on( 'resize', function()
      {
        var width = this.get( 'width' );
        var height = this.get( 'height' );

        changeMapSize( width, height );
      } );
    } );
  </g:javascript>
</content>

</body>
</html>