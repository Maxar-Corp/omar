<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: 9/8/11
  Time: 9:24 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>OMAR Raster Search</title>
  <style type="text/css">
    /*
    margin and padding on body element
    can introduce errors in determining
    element position and are not recommended;
    we turn them off as a foundation for YUI
    CSS treatments.
    */
  body {
    margin: 0;
    padding: 0;
    visibility: hidden;
    overflow: hidden;
  }
  </style>

  <link rel="stylesheet" type="text/css"
        href="${resource(plugin: 'yui', dir: 'js/yui/reset-fonts-grids', file: 'reset-fonts-grids.css')}"/>
  <link rel="stylesheet" type="text/css"
        href="${resource(plugin: 'yui', dir: 'js/yui/assets/skins/sam', file: 'skin.css')}"/>

  <link rel="stylesheet" href="/omar/css/fieldsets.css" type="text/css">

  <openlayers:loadMapToolBar/>
  <openlayers:loadTheme theme="default"/>

  <style type="text/css">
  .datechooser {
    z-index: 99999;
  }
  </style>

</head>

<body class="yui-skin-sam">

<div id="header">
  <omar:securityClassificationBanner fontSize="20px"/>
</div>

<div id="mainContent">
  <div id="top1">
    <div id="panel2" class="olControlPanel"></div>
  </div>

  <div id="bottom1">
  </div>

  <div id="right1">
  </div>

  <div id="left1">

    <g:form class="h3sForm" action="search" method="post">
      <fieldset>
        <h3>Temporal Criteria:</h3>
        <label for="startDate">
          Start Date:
          <g:datePicker name="startDate" value="${startDate}" precision="minute" noSelection="['':'-Choose-']"/>
        </label>
        <label for="endDate">
          End Date:
          <g:datePicker name="endDate" value="${endDate}" precision="minute" noSelection="['':'-Choose-']"/>
        </label>
      </fieldset>

      <fieldset>
        <h3>Other:</h3>
        <label for="foo">
          Widget:
          <g:textField name="foo" value="${foo}"/>
        </label>
      </fieldset>
      <g:submitButton name="search" value="Search"/>
    </g:form>
  </div>

  <div id="center1">
    <div id="map"></div>
  </div>
</div>

<div id="footer">
  <omar:securityClassificationBanner fontSize="20px"/>
</div>

<yui:javascript dir="yahoo-dom-event" file="yahoo-dom-event.js"/>
<yui:javascript dir="element" file="element-min.js"/>
<yui:javascript dir="dragdrop" file="dragdrop-min.js"/>
<yui:javascript dir="resize" file="resize-min.js"/>
<yui:javascript dir="layout" file="layout-min.js"/>
<openlayers:loadJavascript/>

<g:javascript>
  var map, baseLayer, footprintLayer, aoiLayer;

  function setAOI(e)
  {
      var geom = e.feature.geometry;
      var bounds = geom.getBounds();
      var feature = new OpenLayers.Feature.Vector(geom);

//      if($("unitsMode").value == "DD")
//      {
//          $("aoiMinLon").value = bounds.left;
//          $("aoiMaxLat").value = bounds.top;
//          $("aoiMaxLon").value = bounds.right;
//          $("aoiMinLat").value = bounds.bottom;
//      }
//
//      if($("unitsMode").value == "DMS")
//      {
//          $("aoiMinLon").value = convert.ddToDms(bounds.left, "longitude");
//          $("aoiMaxLat").value = convert.ddToDms(bounds.top, "latitude");
//          $("aoiMaxLon").value = convert.ddToDms(bounds.right, "longitude");
//          $("aoiMinLat").value = convert.ddToDms(bounds.bottom, "latitude");
//      }
//
//      if($("unitsMode").value == "MGRS")
//      {
//
//      }

      aoiLayer.destroyFeatures();
      aoiLayer.addFeatures(feature, {silent: true});
  }


  function setupAoiLayer()
  {
      aoiLayer = new OpenLayers.Layer.Vector("Area of Interest");
      aoiLayer.events.register("featureadded", aoiLayer, setAOI);

      var polyOptions = {sides: 4, irregular: true};

      var polygonControl = new OpenLayers.Control.DrawFeature(aoiLayer, OpenLayers.Handler.RegularPolygon,
      {handlerOptions: polyOptions});

      map.addLayer(aoiLayer);
      map.addControl(polygonControl);

//      var aoiMinLon = "${queryParams?.aoiMinLon ?: ''}";
//      var aoiMinLat = "${queryParams?.aoiMinLat ?: ''}";
//      var aoiMaxLon = "${queryParams?.aoiMaxLon ?: ''}";
//      var aoiMaxLat = "${queryParams?.aoiMaxLat ?: ''}";

//      if (aoiMinLon && aoiMinLat && aoiMaxLon && aoiMaxLat )
//      {
//          var bounds = new OpenLayers.Bounds(aoiMinLon, aoiMinLat, aoiMaxLon, aoiMaxLat);
//          var feature = new OpenLayers.Feature.Vector(bounds.toGeometry());
//
//          aoiLayer.addFeatures(feature, {silent: true});
//      }
  }

  function setupToolbar()
  {
        var recenterButton = new OpenLayers.Control.MouseDefaults(
        {title: "Click and drag to recenter map."});

        var zoomBoxButton = new OpenLayers.Control.ZoomBox(
        {title: "Click and drag to zoom into an area."});

        var zoomInButton = new OpenLayers.Control.Button(
        {title: "Click to zoom in.", displayClass: "olControlZoomIn", trigger: function() { map.zoomIn() } });

        var zoomOutButton = new OpenLayers.Control.Button(
        {title: "Click to zoom out.", displayClass: "olControlZoomOut", trigger: function() { map.zoomOut() } });

/*
        var navButton = new OpenLayers.Control.NavigationHistory(
        {nextOptions:{title: "Click to go to next view."}, previousOptions:{title: "Click to go to previous view."}});

        map.addControl(navButton);
*/

        var zoomMaxExtentButton = new OpenLayers.Control.ZoomToMaxExtent(
        {title: "Click to zoom to the max extent."});

        var polyOptions = {sides: 4, irregular: true};

        var polygonControl = new OpenLayers.Control.DrawFeature(aoiLayer, OpenLayers.Handler.RegularPolygon,
        {handlerOptions: polyOptions, title: "Click and drag to specify an area of interest."});

        var clearAoiButton = new OpenLayers.Control.Button(
        {title: "Click to clear area of interest", displayClass: "olControlClearAreaOfInterest", trigger: function() { aoiLayer.destroyFeatures(); } });

        var measureDistanceButton = new OpenLayers.Control.Measure( OpenLayers.Handler.Path,
        {title: "Measure Distance", displayClass: "olControlMeasureDistance",
        eventListeners: {measure: function(evt){alert("Distance: " + evt.measure.toFixed(3) + evt.units);}}});

        var measureAreaButton = new OpenLayers.Control.Measure(OpenLayers.Handler.Polygon,
        {title: "Measure Area", displayClass: "olControlMeasureArea",
        eventListeners: {measure: function(evt){alert("Area: " + evt.measure.toFixed(3) + evt.units);}}});

        var container = YAHOO.util.Dom.get("panel2");

        var panel = new OpenLayers.Control.Panel({
            div: container,
            defaultControl: zoomBoxButton,
            displayClass: "olControlPanel"
        });

        panel.addControls([
            recenterButton,
            zoomBoxButton,
            zoomInButton,
            zoomOutButton,
//            navButton.next, navButton.previous,
            zoomMaxExtentButton,
            polygonControl,
            clearAoiButton,
            measureDistanceButton,
            measureAreaButton
        ]);

        map.addControl(panel);
  }

  function init()
  {
    map = new OpenLayers.Map("map");

    baseLayer = new OpenLayers.Layer.WMS("Reference",
        "http://${InetAddress.localHost.hostAddress}/cgi-bin/mapserv?map=/data/omar/bmng.map",
        {layers: 'Reference', format: 'jpeg'},
        {buffer: 0});
    map.addLayer(baseLayer);

    footprintLayer = new OpenLayers.Layer.WMS("Reference",
        "http://${InetAddress.localHost.hostAddress}/cgi-bin/mapserv?map=/data/omar/omar-1.8.12-prod.map",
        {layers: 'Imagery', format: 'gif', transparent: true},
        {buffer: 0});
    map.addLayer(footprintLayer);

    setupAoiLayer();
    setupToolbar();


    if ( ! map.getCenter() )
    {
        map.zoomToMaxExtent();
    }
  }

  (function()
  {
    var Dom = YAHOO.util.Dom;
    var Event = YAHOO.util.Event;

    Event.onDOMReady( function()
    {
      var outerLayout =  new YAHOO.widget.Layout( {
        units: [
          { position: 'top', height: 35, body: 'header', gutter: '5px' },
          { position: 'bottom', height: 35, body: 'footer', gutter: '5px' },
          { position: 'center', body: 'mainContent' }
        ]
      } );

      outerLayout.on('render', function(){

        var layout = new YAHOO.widget.Layout( outerLayout.getUnitByPosition('center').get('wrap'), {
          parent: outerLayout,
          units: [
            { position: 'top', height: 65, body: 'top1', header: 'Top', gutter: '5px', collapse: true, resize: true, animate: false },
            { position: 'right', header: 'Right', width: 200, resize: true, gutter: '5px', collapse: true, scroll: true, body: 'right1', animate: false },
            { position: 'bottom', header: 'Bottom', height: 100, resize: true, body: 'bottom1', gutter: '5px', collapse: true, animate: false },
            { position: 'left', header: 'Left', width: 250, resize: true, body: 'left1', gutter: '5px', collapse: true, scroll: true, animate: false },
            { position: 'center', body: 'center1' }
          ]
        } );

        layout.on( 'render', function()
        {
          init();

          layout.getUnitByPosition('center').on('resize', function() {
            map.updateSize();
          });
        } );

        layout.render();

        Dom.setStyle( document.body, 'visibility', 'visible' );

      });

      outerLayout.render();
    } );
  })();
</g:javascript>
</body>
</html>
</html>