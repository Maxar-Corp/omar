<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: Sep 26, 2008
  Time: 11:04:28 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="grails.converters.JSON" contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="multiLayerLayout"/>

  <meta name="apple-mobile-web-app-capable" content="yes"/>
  <meta name="apple-mobile-web-app-status-bar-style" content="black"/>
  <meta name="viewport" content="minimum-scale=1.0, width=device-width, maximum-scale=1.6, user-scalable=no">

  <title>OMAR: Ground Space Multi-Viewer</title>

  <openlayers:loadMapToolBar/>
  <openlayers:loadTheme theme="default"/>

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

    /*
    #config {
      margin-top: 1em;
      width: 1024px;
      position: relative;
      height: 8em;
    }
    */

  #controls {
    padding-left: 2em;
    margin-left: 0;
    width: 12em;
  }

  #controls li {
    padding-top: 0.5em;
    list-style: none;
  }

  </style>

</head>
<body class="yui-skin-sam" onload="init();">
<openlayers:loadJavascript/>
<g:javascript plugin="omar-core" src="touch.js"/>

<g:javascript>
  var map;

  function changeMapSize(mapWidth, mapHeight)
  {
//      var mapTitle = document.getElementById( "mapTitle" );
//      var mapDiv = document.getElementById( "map" );
//
//      mapDiv.style.width = mapTitle.offsetWidth + "px";
//      mapDiv.style.height = Math.round( mapTitle.offsetWidth / 2 ) + "px";
  var Dom = YAHOO.util.Dom;
  if(mapWidth&&mapHeight)
  {
     Dom.get( "map" ).style.width  = mapWidth + "px";
     Dom.get( "map" ).style.height = mapHeight + "px";
  }

    if(map) map.updateSize( );
  }

  function setupBaseLayer()
  {
    var baseLayer = null;
    var baseWMS = ${baseWMS as JSON};

    for ( foo  in baseWMS ) {
      baseLayer = new OpenLayers.Layer.WMS( baseWMS[foo].name, baseWMS[foo].url,
              baseWMS[foo].params, baseWMS[foo].options );
      map.addLayer( baseLayer );
      map.setBaseLayer( baseLayer );
    }
  }

function init(mapWidth, mapHeight)
{
    var left = ${left};
    var bottom = ${bottom};
    var right = ${right};
    var top = ${top};

    map = new OpenLayers.Map( "map", { controls: [], numZoomLevels: 32 } );

    var format = "${format}";
    var transparent = true;

    setupBaseLayer( );

    var layers = [



  <g:each var="rasterEntry" in="${rasterEntries}" status="i">

    <g:if test="${i > 0}">,</g:if>

    new OpenLayers.Layer.WMS(
    "Raster ${rasterEntry.id}",
              "${createLink(controller: 'ogc', action: 'wms')}",
      { layers: "${rasterEntry.indexId}", format: format, stretch_mode:"linear_auto_min_max", transparent:transparent  },
      {isBaseLayer: false, buffer:0, singleTile:true, ratio:1.0, transitionEffect: "resize"}
              )
    <g:if test="${kmlOverlays}">

      , new OpenLayers.Layer.Vector( "KML", {
   projection: map.displayProjection,
   strategies: [new OpenLayers.Strategy.Fixed( )],
   protocol: new OpenLayers.Protocol.HTTP( {
     url: "${createLink(controller: 'rasterEntry', action: 'getKML', params: [rasterEntryIds: rasterEntry.indexId])}",
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
   map.addControl( new OpenLayers.Control.LayerSwitcher( ) )
   //map.addControl(new OpenLayers.Control.PanZoom())
   //map.addControl(new OpenLayers.Control.NavToolbar())
   map.addControl( new OpenLayers.Control.MousePosition( ) );
   map.addControl( new OpenLayers.Control.Scale( ) );
   map.addControl( new OpenLayers.Control.Permalink( "permalink" ) );
   map.addControl( new OpenLayers.Control.ScaleLine( ) );
   map.addControl( new OpenLayers.Control.Attribution( ) );


   var bounds = new OpenLayers.Bounds( left, bottom, right, top );

   map.maxExtent = bounds;
   changeMapSize( mapWidth, mapHeight );
   setupToolbar( );

   var zoom = map.getZoomForExtent( bounds, true );

   map.setCenter( bounds.getCenterLonLat( ), zoom );

  var isiPad = navigator.userAgent.match( /iPad/i ) != null;

   if ( isiPad )
   {
      this.touchhandler = new TouchHandler( map, 4 );
   }
  }

  function zoomIn()
  {
   map.zoomIn( );
  }

  function zoomOut()
  {
   map.zoomOut( );

  }
  function setupToolbar()
  {

   var zoomBoxButton = new OpenLayers.Control.ZoomBox(
   {title:"Zoom into an area by clicking and dragging"} );

   var zoomInButton = new OpenLayers.Control.Button( {title:'Zoom in',
     displayClass: "olControlZoomIn",
     trigger: zoomIn
   } );

   var zoomOutButton = new OpenLayers.Control.Button( {title:'Zoom out',
     displayClass: "olControlZoomOut",
     trigger: zoomOut
   } );

   var container = $( "toolBar" );

   var panel = new OpenLayers.Control.Panel(
   { div: container,defaultControl: zoomBoxButton,'displayClass': 'olControlPanel'}
           );


   var navButton = new OpenLayers.Control.NavigationHistory( {
     nextOptions: {title: "Next View" },
     previousOptions: {title: "Previous View"}
   } );


   map.addControl( navButton );

     var message = "Alert: Not certified for targeting.\n";

              var measureDistanceButton = new OpenLayers.Control.Measure(OpenLayers.Handler.Path, {
        title: "Measure Distance",
        displayClass: "olControlMeasureDistance",
        eventListeners:
        {
          measure: function(evt)
          {
            alert(message + "Path: " + evt.measure.toFixed(3) + " " + evt.units);
          }
        }
      });

      var measureAreaButton = new OpenLayers.Control.Measure(OpenLayers.Handler.Polygon, {
        title: "Measure Area",
        displayClass: "olControlMeasureArea",
        eventListeners:
        {
          measure: function(evt)
          {
            alert(message + "Area: " + evt.measure.toFixed(3) + " " + evt.units);
          }
        }
      });

   panel.addControls( [
     new OpenLayers.Control.MouseDefaults( {title:'Drag to recenter map'} ),
     zoomBoxButton,
     zoomInButton,
     zoomOutButton,
     navButton.next, navButton.previous,
     new OpenLayers.Control.ZoomToMaxExtent( {title:"Zoom to the max extent"} ),
     measureDistanceButton,
     measureAreaButton
   ] );

   map.addControl( panel );
  }

</g:javascript>
<content tag="top">
  <div class="nav">
    <span class="menuButton"><g:link class="home" uri="/">OMAR™ Home</g:link></span>
    <span class="menuButton">
      <a href="${createLink(controller: "ogc", action: "wms", params: [request: "GetCapabilities", layers: (rasterEntries*.id).join(',')])}">
        WMS GetCapabilities
      </a>
    </span>
    <span class="menuButton">
      <a href="${createLink(controller: "ogc", action: "wms", params: [request: "GetKML", layers: (rasterEntries*.id).join(',')])}">
        Generate KML
      </a>
    </span>
    <span class="menuButton">
      <a href="${createLink(controller: "mapView", action: "index", params: [layers: (rasterEntries*.indexId).join(',')])}">
        Single Layer Viewer
      </a>
    </span>
    <g:if test="${rasterEntries?.size() == 1}">
      <span class="menuButton">
        <a href="${createLink(controller: "mapView", action: "imageSpace", params: [layers: (rasterEntries*.indexId).join(',')])}">
          Image Space Viewer
        </a>
      </span>
    </g:if>

  </div>
</content>
<content tag="center">
  <%--
  <h1 id="mapTitle">${rasterEntries*.mainFile.name}</h1>
  <g:if test="${flash.message}">
    <div class="message">${flash.message}</div>
  </g:if>
  --%>
</content>


</body>
</html>
