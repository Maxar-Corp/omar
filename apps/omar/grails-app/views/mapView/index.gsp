<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: Sep 26, 2008
  Time: 11:04:28 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="main7"/>
  <title>OMAR Ground Space Viewer</title>
  <link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css')}"/>

  <openlayers:loadMapToolBar/>
  <openlayers:loadTheme theme="default"/>

  <style type="text/css">
  #map {
    width: 100%;
    height: 100%;
    border: 1px solid black;
  }

    /*
      div.olControlMousePosition {
        font-family: Verdana;
        font-size: 1.0em;
        background-color: white;
        color: black;
      }
    */
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

  <openlayers:loadJavascript/>
  <g:javascript plugin="omar-core" src="coordinateConversion.js"/>
</head>
<body>
<content tag="hd">
  <div class="nav">
    <span class="menuButton"><g:link class="home" uri="/">Home</g:link></span>
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
      <a href="${createLink(controller: "mapView", action: "multiLayer", params: [rasterEntryIds: (rasterEntries*.id).join(',')])}">
        Multi-Layer
      </a>
    </span>
    <g:if test="${rasterEntries?.size() == 1}">
      <span class="menuButton">
        <a href="${createLink(controller: "mapView", action: "imageSpace", id: (rasterEntries*.id).join(','))}">
          Image Space
        </a>
      </span>
    </g:if>
    <span class="menuButton">
      <label>Sharpen:</label>
      <g:select id="sharpen_mode" name="sharpen_mode" from="${['none', 'light', 'heavy']}" onChange="changeSharpenOpts()"/>
    </span>
    <span class="menuButton">
      <label>Stretch:</label>
      <g:select id="stretch_mode" name="stretch_mode" from="${['linear_auto_min_max', 'linear_1std_from_mean', 'linear_2std_from_mean', 'linear_3std_from_mean', 'none']}" onChange="changeHistoOpts()"/>
    </span>
    <span class="menuButton">
      <label>Region:</label>
      <g:select id="stretch_mode_region" name="stretch_mode_region" from="${['global', 'viewport']}" onChange="changeHistoOpts()"/>
    </span>
    <span class="menuButton">
      <label>Terrain Correction:</label>
      <g:select id="terrain_correction" name="terrain_correction" from="${['false', 'true']}" onChange="changeTerrainCorrectionOps()"/>
    </span>
    <%--
      <div id="panel2" class="olControlPanel"></div>
    --%>
  </div>
</content>
<content tag="bd">
  <%--
  <h1 id="mapTitle">${rasterEntries*.mainFile.name}</h1>
  <g:if test="${flash.message}">
    <div class="message">${flash.message}</div>
  </g:if>
  --%>
  <div id="map"></div>
</div>
</content>
<content tag="ft">
  <div class="niceBox">
    <div class="niceBoxHd">Mouse Position:</div>
    <div class="niceBoxBody">
      <table>
        <tr>
          <td width=200><div id="ddCoordinates"></div></td>
          <td width=200><div id="dmsCoordinates"></div></td>
          <td width=200><div id="mgrsCoordinates"></div></td>
        </tr>
      </table>
    </div>
  </div>>
  <g:javascript>
  var map;
  var rasterLayers;
  var kmlLayers;
  var select;
  var convert = new CoordinateConversion();
  
  function changeMapSize( mapWidth, mapHeight )
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
    map.updateSize();
  }

  function init( mapWidth, mapHeight )
  {
    var left   = parseFloat("${left}");
    var bottom = parseFloat("${bottom}");
    var right  = parseFloat("${right}");
    var top    = parseFloat("${top}");
    var fullResScale = parseFloat("${fullResScale}");
    var smallestScale = parseFloat("${smallestScale}");
    var largestScale = parseFloat("${largestScale}");

    var bounds    = new OpenLayers.Bounds(left, bottom, right, top);

   if(smallestScale != 0.0)
   {
    map = new OpenLayers.Map("map", { controls: [],
                                      maxExtent:bounds,
                                      maxResolution:largestScale,
                                      minResolution:smallestScale });
   }
   else
   {
    map = new OpenLayers.Map("map", { controls: [],
                                      maxExtent:bounds,
                                      minExtent:minBounds,
                                      minResolution:'auto', maxResolution:'auto' });
   }
    changeMapSize( mapWidth, mapHeight );


    setupToolbar();
    setupLayers();
    map.events.register('mousemove',map,handleMouseMove);
    map.addControl(new OpenLayers.Control.LayerSwitcher());
    //map.addControl(new OpenLayers.Control.PanZoom());
    //map.addControl(new OpenLayers.Control.NavToolbar());
    //map.addControl(new OpenLayers.Control.MousePosition());
    map.addControl(new OpenLayers.Control.Scale());
    map.addControl(new OpenLayers.Control.ScaleLine());


    var zoom = map.getZoomForExtent(bounds, true);

    map.setCenter(bounds.getCenterLonLat(), zoom);


  }
    function handleMouseMove(evt)
    {
    var lonLat = map.getLonLatFromViewPortPx(new OpenLayers.Pixel(evt.xy.x , evt.xy.y) );
    var dmsOutput = document.getElementById('dmsCoordinates');
    var mgrsOutput = document.getElementById('mgrsCoordinates');

    if(lonLat.lat > "90" || lonLat.lat < "-90" || lonLat.lon > "180" || lonLat.lon < "-180")
    {
        dmsOutput.innerHTML = "<b>DMS:</b> ";
        mgrsOutput.innerHTML = "<b>MGRS:</b> ";
    }
    else
    {
        dmsOutput.innerHTML = "<b>DMS:</b> " + convert.ddToDms(lonLat.lat, "latitude") + " " + convert.ddToDms(lonLat.lon, "longitude");
        mgrsOutput.innerHTML = "<b>MGRS:</b> " + convert.ddToMgrs(lonLat.lat, lonLat.lon);
    }

    var latHem;
    if(lonLat.lat < 0)
    {
        latHem = " S";
    }
    else
    {
        latHem = " N";
    }

    var lonHem;
    if(lonLat.lon < 0)
    {
        lonHem = " W";
    }
    else
    {
        lonHem = " E";
    }

    var ddOutput = document.getElementById('ddCoordinates');
    if(lonLat.lat > "90" || lonLat.lat < "-90" || lonLat.lon > "180" || lonLat.lon < "-180")
    {
        ddOutput.innerHTML = "<b>DD:</b> ";
    }
    else
    {
        ddOutput.innerHTML = "<b>DD:</b> " + lonLat.lat + " " + lonLat.lon;
    }
}
  function setupLayers()
  {

    var format = "image/jpeg";
    //      var format = "image/png";
    //      var format = "image/gif";
    var transparent = false;


    var stretch_mode = $("stretch_mode").value;
    var stretch_mode_region = $("stretch_mode_region").value;
    var sharpen_mode = $("sharpen_mode").value;

     rasterLayers = [
      new OpenLayers.Layer.WMS( "Raster", "${createLink(controller: 'ogc', action: 'wms')}",
      { layers: "${(rasterEntries*.id).join(',')}", format: format, sharpen_mode:sharpen_mode, stretch_mode:stretch_mode, stretch_mode_region: stretch_mode_region, transparent:transparent  },
      {isBaseLayer: true, buffer:0, singleTile:true, ratio:1.0, terrain_correction:false, transitionEffect: "resize",
       displayOutsideMaxExtent:false})
    ];
    map.addLayers(rasterLayers);

    <g:each in="${kmlOverlays}" var="kmlOverlay" status="i">

      if ( ! kmlLayers ) {
        kmlLayers = new Array();
     }

 var kmlLayer =  new OpenLayers.Layer.Vector("${kmlOverlay.name}", {
      projection: map.displayProjection,
      strategies: [new OpenLayers.Strategy.Fixed()],
      protocol: new OpenLayers.Protocol.HTTP({
        url: "${kmlOverlay.url}",
        format: new OpenLayers.Format.KML({
          extractStyles: true,
          extractAttributes: true
        })
      })
     });
     
    kmlLayers[${i}] = kmlLayer;
    kmlLayer.events.on({
        "featureselected": onFeatureSelect,
        "featureunselected": onFeatureUnselect
    });

    map.addLayers(kmlLayers);
   select = new OpenLayers.Control.SelectFeature(kmlLayers);
    map.addControl(select);
    select.activate();

    </g:each>




    }

function onPopupClose(evt)
{
 select.unselectAll();
}

function onFeatureSelect(event)
{
 var feature = event.feature;
 // Since KML is user-generated, do naive protection against
 // Javascript.
 var content = "<h2>"+feature.attributes.name + "</h2>" + feature.attributes.description;
      if (content.search("<script") != -1) {
          content = "Content contained Javascript! Escaped content below.<br />" + content.replace(/</g, "&lt;");
      }
      popup = new OpenLayers.Popup.FramedCloud("chicken",
                               feature.geometry.getBounds().getCenterLonLat(),
                               new OpenLayers.Size(100,100),
                               content,
                               null, true, onPopupClose);
      feature.popup = popup;
      map.addPopup(popup);
  }

  function onFeatureUnselect(event)
  {
      var feature = event.feature;
      if(feature.popup) {
          map.removePopup(feature.popup);
          feature.popup.destroy();
          delete feature.popup;
      }
  }

  function changeTerrainCorrectionOps()
  {
    for ( var layer in rasterLayers )
    {
      rasterLayers[layer].mergeNewParams({terrain_correction:$("terrain_correction").value});
    }
  }
  function changeHistoOpts()
  {
    var stretch_mode = $("stretch_mode").value;
    var stretch_mode_region = $("stretch_mode_region").value;

    for ( var layer in rasterLayers )
    {
      rasterLayers[layer].mergeNewParams({stretch_mode:stretch_mode, stretch_mode_region: stretch_mode_region});
    }
  }
  function changeSharpenOpts()
  {
    var sharpen_mode = $("sharpen_mode").value;

    for ( var layer in rasterLayers )
    {
      rasterLayers[layer].mergeNewParams({sharpen_mode:sharpen_mode});
    }
  }

    function zoomIn()
    {
      map.zoomIn();
    }

    function zoomInFullRes()
    {
        var zoom = map.getZoomForResolution(parseFloat("${fullResScale}"), true)
        map.zoomTo(zoom)
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
       var zoomInFullResButton = new OpenLayers.Control.Button({title:'Zoom in full res',
          displayClass: "olControlZoomToLayer",
          trigger: zoomInFullRes
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

                var measureDistanceButton = new OpenLayers.Control.Measure(OpenLayers.Handler.Path, {
          title: "Measure Distance",
          displayClass: "olControlMeasureDistance",
          eventListeners:
          {
            measure: function(evt)
            {
              alert("Distance: " + evt.measure.toFixed(2) + evt.units);
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
              alert("Area: " + evt.measure.toFixed(2) + evt.units);
            }
          }
        });

        panel.addControls([
          new OpenLayers.Control.MouseDefaults({title:'Drag to recenter map'}),
          zoomBoxButton,
          zoomInButton,
          zoomOutButton,
          navButton.next, navButton.previous,
          new OpenLayers.Control.ZoomToMaxExtent({title:"Zoom to the max extent"}),
                zoomInFullResButton,
          measureDistanceButton,
          measureAreaButton
        ]);

        map.addControl(panel);
      }

  </g:javascript>
</content>

</body>
</html>
