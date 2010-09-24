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


</head>
<!-- define your page content -->
<body class="yui-skin-sam">

<content tag="layout1.top.content">
  <div id="panel2" class="olControlPanel"></div>
</content>

<content tag="layout1.bottom.content">
</content>

<content tag="layout1.right.content">
  <b>Units:</b>
  <form action="#" name='units'>
    <select name="mode" onchange="document.getElementById( 'units' ).innerHTML = units.mode.value">
      <option value="DD">Decimal Degrees (DD)</option>
      <option value="DMS">Degrees Minutes Seconds (DMS)</option>
      <option value="MGRS">Military Grid Reference System (MGRS)</option>
    </select>
  </form>

  <div id="distanceMeasurement"></div>
  <div id="areaMeasurement"></div>
</content>

<content tag="layout1.left.content">
  <div class="myAccordion">
    <div class="yui-cms-accordion multiple fade fixIE">

      <div class="yui-cms-item yui-panel selected">
        <div class="hd">
          Map Center:
        </div>
        <div class="bd">
          <div class="fixed">

            <div id="units">
              Center Latitude (DD):<br/>
              <g:textField name="centerLat" value="${queryParams?.centerLat}"/><br/>
              Center Longitude (DD):<br/>
              <g:textField name="centerLon" value="${queryParams?.centerLon}"/><br/>
            </div>

          </div>
        </div>
        <div class="actions">
          <a href="#" class="accordionToggleItem">&nbsp;</a>
        </div>
      </div>

    </div>
  </div>
</content>

<content tag="layout1.center.content">
  <div id="map"></div>
</content>
<openlayers:loadJavascript/>
<g:javascript>
// your own js code is here
var lon = ${centerLon};
var lat = ${centerLat};
var zoom = ${zoomLevel};
var map;
var layer;
var aoiLayer;

// define your init function
var init = function()
{
  setupMapWidget();
  setupBaseLayers();
  //setupOverlayLayers();
  setupAreaOfInterestLayer();
  setupToolbar();
  setupView();
};


var setView = function( e )
{
  var bounds = map.getExtent();

%{--$("viewMinLon").value = bounds.left;--}%
%{--$("viewMaxLat").value = bounds.top;--}%
%{--$("viewMaxLon").value = bounds.right;--}%
%{--$("viewMinLat").value = bounds.bottom;--}%
  }


var setCenterText = function( e )
{
var center = map.getCenter();
$("centerLon").value = center.lon;
$("centerLat").value = center.lat;
}

var resized = function( mapWidth, mapHeight )
{
//alert( "B: " + mapWidth + " " + mapHeight );

Dom.get( "map" ).style.width = mapWidth + "px";
Dom.get( "map" ).style.height = mapHeight + "px";

map.updateSize( );
}


var clearAOI = function ( e )
{
aoiLayer.destroyFeatures();


// HACK - Need a better way to this
%{--$("aoiMinLon").value = ""--}%
%{--$("aoiMaxLat").value = ""--}%
%{--$("aoiMaxLon").value = ""--}%
%{--$("aoiMinLat").value = ""--}%
  }

var setAOI = function ( e )
{
var geom = e.feature.geometry;
var bounds = geom.getBounds();
var feature = new OpenLayers.Feature.Vector(geom);

// HACK - Need a better way to this
%{--$("aoiMinLon").value = bounds.left--}%
%{--$("aoiMaxLat").value = bounds.top--}%
%{--$("aoiMaxLon").value = bounds.right--}%
%{--$("aoiMinLat").value = bounds.bottom--}%

  aoiLayer.destroyFeatures();
  aoiLayer.addFeatures(feature, {silent: true});
}

var zoomIn = function ()
{
  map.zoomIn();
}

var zoomOut = function ()
{
  map.zoomOut();
}


var setupMapWidget = function()
{
  map = new OpenLayers.Map('map', {controls: []});
  map.addControl(new OpenLayers.Control.LayerSwitcher());
  map.addControl(new OpenLayers.Control.PanZoom());
  map.addControl(new OpenLayers.Control.MousePosition());
  map.addControl(new OpenLayers.Control.Scale());
  map.addControl(new OpenLayers.Control.ScaleLine());

  map.events.register("moveend", map, setCenterText);
  map.events.register("zoomend", map, setView );
}


var setupBaseLayers = function()
{
  <g:each in="${baseLayers}">
    map.addLayer( new OpenLayers.Layer.WMS('${it.title}', '${it.url}',
    {layers: '${it.name}', format: 'image/jpeg' },
    {'isBaseLayer': true}, {buffer:0}));
  </g:each>
  }

  var setupOverlayLayers = function ()
  {
  <g:each in="${overlayLayers}">
    map.addLayer( new OpenLayers.Layer.WMS('${it.title}', '${it.url}',
    {layers: '${it.name}', format: 'image/png', transparent: true },
    {'isBaseLayer': false}, {buffer:0}));
  </g:each>
  }

var setupAreaOfInterestLayer = function ()
{
  aoiLayer = new OpenLayers.Layer.Vector("Area of Interest");
  aoiLayer.events.register("featureadded", aoiLayer, setAOI);
  map.addLayer(aoiLayer);

%{--var aoiMinLon = "${queryParams?.aoiMinLon ?: 0}";--}%
%{--var aoiMinLat = "${queryParams?.aoiMinLat ?: 0}";--}%
%{--var aoiMaxLon = "${queryParams?.aoiMaxLon ?: 0}";--}%
%{--var aoiMaxLat = "${queryParams?.aoiMaxLat ?: 0}";--}%

%{--if ( aoiMinLon && aoiMinLat && aoiMaxLon && aoiMaxLat )--}%
%{--{--}%
%{--var bounds = new OpenLayers.Bounds( aoiMinLon, aoiMinLat, aoiMaxLon, aoiMaxLat );--}%
%{--var feature = new OpenLayers.Feature.Vector(bounds.toGeometry());--}%

%{--aoiLayer.addFeatures(feature, {silent: true});--}%
%{--}--}%
  }

var setupToolbar = function ()
{
var controls = [];
var defaultControl;

// Drag Pan
controls.push(new OpenLayers.Control.MouseDefaults({title:'Drag to recenter map'}));

// Zoom Box
var zoomBox = new OpenLayers.Control.ZoomBox(
{title:"Zoom into an area by clicking and dragging"});

controls.push(zoomBox);
defaultControl = zoomBox;

// Zoon In
var zoomInButton = new OpenLayers.Control.Button({title:'Zoom in',
displayClass: "olControlZoomIn",
trigger: zoomIn
});

controls.push(zoomInButton);

// Zoom Out
var zoomOutButton = new OpenLayers.Control.Button({title:'Zoom out',
displayClass: "olControlZoomOut",
trigger: zoomOut
});

controls.push(zoomOutButton);

// Navigation History
var nav = new OpenLayers.Control.NavigationHistory({
nextOptions: {title: "Next View"},
previousOptions: {title: "Previous View"}
});

map.addControl(nav);
controls.push(nav.next);
controls.push(nav.previous);

// Zoom to Max Extent
controls.push(new OpenLayers.Control.ZoomToMaxExtent({title:"Zoom to the max extent"}));

// Area of Interest
if ( aoiLayer )
{
var polyOptions = {sides: 4, irregular: true};

var polygonControl = new OpenLayers.Control.DrawFeature(aoiLayer, OpenLayers.Handler.RegularPolygon,
{handlerOptions: polyOptions, title: "Specify Area of Interest"});

var button1 = new OpenLayers.Control.Button({title:'Clear Area of Interest',
displayClass: "olControlClearAreaOfInterest",
trigger: clearAOI
});

controls.push(polygonControl);
controls.push(button1);
}

// Measure Distance Button
var measureDistanceButton = new OpenLayers.Control.Measure(OpenLayers.Handler.Path, {
title: "Measure Distance",
displayClass: "olControlMeasureDistance",
eventListeners:
{
measure: function(evt)
{
//alert("Distance: " + evt.measure.toFixed(2) + evt.units);
document.getElementById('distanceMeasurement').innerHTML = "<b>Distance:</b> " + (evt.measure.toFixed(2) + evt.units) + " <a href='#' onclick=''>Clear Measurement</a>";
      }
    }
  });

  controls.push(measureDistanceButton);

  // Measure Area Button
  var measureAreaButton = new OpenLayers.Control.Measure(OpenLayers.Handler.Polygon, {
    title: "Measure Area",
    displayClass: "olControlMeasureArea",
    eventListeners:
    {
      measure: function(evt)
      {
        //alert("Area: " + evt.measure.toFixed(2) + evt.units);
        document.getElementById('areaMeasurement').innerHTML = "<b>Area:</b> " + (evt.measure.toFixed(2) + evt.units) + " <a href='#' onclick=''>Clear Measurement</a>";
      }
    }
  });

  controls.push(measureAreaButton);

  //
  // Setup the container for the toolbar
  var container = $("panel2");

  var panel = new OpenLayers.Control.Panel(
    {div: container,defaultControl: defaultControl,'displayClass': 'olControlPanel'}
    );

  panel.addControls(controls);
  map.addControl(panel);
}
var setupView = function()
{
  map.setCenter( new OpenLayers.LonLat( lon, lat ), zoom );
}

</g:javascript>

</body>
</html>