<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: Feb 9, 2009
  Time: 10:19:01 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>OMAR Image Space Viewer</title>
  <link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css')}"/>

  <openlayers:loadMapToolBar/>
  <openlayers:loadTheme theme="default"/>
  <openlayers:loadJavascript/>

  <meta name="layout" content="main6"/>
  <style type="text/css">
  #map {
    width: 100%;
    height: 100%;
    border: 1px solid black;
  }

  div.olControlMousePosition {
    font-family: Verdana;
    font-size: 1.0em;
    background-color: white;
    color: black;
  }

  div.olControlScale {
    background-color: #ffffff;
    font-size: 1.0em;
    font-weight: bold;
  }

  #controls {
    padding-left: 2em;
    margin-left: 0;
    width: 12em;
  }

  #controls li {
    padding-top: 0.5em;
    list-style: none;
  }  </style>

</head>

<body>
<content tag="north">
  <div class="nav">
    <span class="menuButton"><g:link class="home" uri="/">Home</g:link></span>
    <span class="menuButton">
      <a href="${createLink(controller: "mapView", action: "index", params: [rasterEntryIds: rasterEntry?.id])}">
        Ground Space
      </a>
    </span>
    <span class="menuButton">
      <label>Stretch:</label>
      <g:select id="stretch_mode" name="stretch_mode"
                from="${['linear_auto_min_max', 'linear_1std_from_mean', 'linear_2std_from_mean', 'linear_3std_from_mean', 'none']}"
                onChange="changeHistoOpts()"/>
    </span>
    <span class="menuButton">
      <label>Region:</label>
      <g:select id="stretch_mode_region" name="stretch_mode_region" from="${['global', 'viewport']}"
                onChange="changeHistoOpts()"/>
    </span>
  </div>
</content>
<content tag="center">
  <%--
  <h1 id="mapTitle">${rasterEntry?.mainFile?.name}</h1>
  <g:if test="${flash.message}">
    <div class="message">${flash.message}</div>
  </g:if>
  --%>
  <div id="map"></div>
  <g:javascript>
  var map;
  var layer;

  function changeMapSize(mapWidth, mapHeight)
  {
//    var mapTitle = document.getElementById("mapTitle");
//    var mapDiv = document.getElementById("map");
//
//    mapDiv.style.width = mapTitle.offsetWidth + "px";
//    mapDiv.style.height = Math.round(mapTitle.offsetWidth / 2) + "px";

    var Dom = YAHOO.util.Dom;

    Dom.get( "map" ).style.width = mapWidth + "px";
    Dom.get( "map" ).style.height = mapHeight + "px";

//        alert( mapWidth + ' ' + mapHeight );
    
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

//      var path = "?bbox=" + x + "," + y + "," + bounds.right + "," + bounds.top

      var url = this.url;
      if (url instanceof Array) {
          url = this.selectUrl(path, url);
      }
      return url + path;
  }

  function init(mapWidth, mapHeight)
  {
    //alert(mapWidth + ' ' + mapHeight);
 
    map = new OpenLayers.Map('map', { controls: [], numZoomLevels: 32 } );
    map.addControl(new OpenLayers.Control.LayerSwitcher())
    map.addControl(new OpenLayers.Control.MousePosition());
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
    setupToolbar();

  }

    function zoomIn()
    {
      map.zoomIn();
    }

    function zoomOut()
    {
      map.zoomOut();

    }
      function setupToolbar()
      {

        var zoomBoxButton = new OpenLayers.Control.ZoomBox(
        {title:"Zoom into an area by clicking and dragging"});

        var zoomInButton = new OpenLayers.Control.Button({title:'Zoom in',
          displayClass: "olControlZoomIn",
          trigger: zoomIn
        });

        var zoomOutButton = new OpenLayers.Control.Button({title:'Zoom out',
          displayClass: "olControlZoomOut",
          trigger: zoomOut
        });

        var container = $("panel2");

        var panel = new OpenLayers.Control.Panel(
        { div: container,defaultControl: zoomBoxButton,'displayClass': 'olControlPanel'}
                );


        var navButton = new OpenLayers.Control.NavigationHistory({
          nextOptions: {title: "Next View" },
          previousOptions: {title: "Previous View"}
        });


        map.addControl(navButton);

        panel.addControls([
          new OpenLayers.Control.MouseDefaults({title:'Drag to recenter map'}),
          zoomBoxButton,
          zoomInButton,
          zoomOutButton,
          navButton.next, navButton.previous,
          new OpenLayers.Control.ZoomToMaxExtent({title:"Zoom to the max extent"})
        ]);

        map.addControl(panel);
      }

  </g:javascript>
</content>

</body>
</html>
