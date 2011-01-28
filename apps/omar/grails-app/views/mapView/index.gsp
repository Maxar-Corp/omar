<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="rasterViewsStatic"/>
  <title>OMAR: Ground Space Viewer</title>

  <openlayers:loadMapToolBar/>
  <openlayers:loadTheme theme="default"/>

  <meta name="apple-mobile-web-app-capable" content="yes"/>
  <meta name="apple-mobile-web-app-status-bar-style" content="black"/>
  <meta name="viewport" content="minimum-scale=1.0, width=device-width, maximum-scale=1.6, user-scalable=no">
  <openlayers:loadJavascript/>
  <omar:bundle contentType="javascript" files="${[
       [plugin: 'omar-core', dir:'js', file: 'coordinateConversion.js'],
       [plugin: 'omar-core', dir:'js', file: 'touch.js']
   ]}"/>


  <style type="text/css">

  #map {
    width: 100%;
    height: 100%;
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
  <g:javascript>
  var map;
  var rasterLayers;
  var kmlLayers;
  var select;
  var convert = new CoordinateConversion();
  function getKML(layers)
  {
      var numberOfBands = parseInt("${rasterEntries?.numberOfBands.get(0)}")
     var extent = map.getExtent()
     var wmsParamForm = document.getElementById('wmsParams')
     wmsParamForm.sharpen_mode.value = $("sharpen_mode").value
     wmsParamForm.stretch_mode_region.value = $("stretch_mode_region").value
     wmsParamForm.stretch_mode.value = $("stretch_mode").value
     if(numberOfBands > 1)
      {
        wmsParamForm.bands.value = $("bands").value
      }
     wmsParamForm.quicklook.value = $("quicklook").value
     wmsParamForm.request.value = "GetKML"
     wmsParamForm.layers.value = layers
     wmsParamForm.bbox.value = extent.toBBOX()
     wmsParamForm.submit()
  }

  function changeMapSize( mapWidth, mapHeight )
  {
     if(mapWidth&&mapHeight)
     {
        var Dom = YAHOO.util.Dom;

        Dom.get( "map" ).style.width  = mapWidth + "px";
        Dom.get( "map" ).style.height = mapHeight + "px";
     }
  //   else
  //   {
  //     var mapCenter = document.getElementById("mapCenter");
   //    var mapDiv   = document.getElementById("map");
   //    mapDiv.style.width  = mapCenter.width + "px";
   //    mapDiv.style.height = mapCenter.height + "px";
   //  }


  //        alert( mapWidth + ' ' + mapHeight );

    //map.updateSize();
    map.updateSize();
  }

  //function init( mapWidth, mapHeight )
  function init()
  {
    var left   = parseFloat("${left}");
    var bottom = parseFloat("${bottom}");
    var right  = parseFloat("${right}");
    var top    = parseFloat("${top}");
    var fullResScale = parseFloat("${fullResScale}");
    var smallestScale = parseFloat("${smallestScale}");
    var largestScale = parseFloat("${largestScale}");

    var bounds    = new OpenLayers.Bounds(left, bottom, right, top);

    map = new OpenLayers.Map("map", { controls: [],
                                      maxExtent:bounds,
                                      maxResolution:largestScale,
                                      minResolution:smallestScale });
    changeMapSize( null, null);
    setupToolbar();
    setupLayers();
    map.events.register('mousemove',map,handleMouseMove);
    map.addControl(new OpenLayers.Control.LayerSwitcher());
    //map.addControl(new OpenLayers.Control.PanZoom());
    //map.addControl(new OpenLayers.Control.NavToolbar());
    //map.addControl(new OpenLayers.Control.MousePosition());
    map.addControl(new OpenLayers.Control.Scale());
    map.addControl(new OpenLayers.Control.ScaleLine());
    map.events.register("moveend", map, this.setCenterText);

    var zoom = map.getZoomForExtent(bounds, true);

    map.setCenter(bounds.getCenterLonLat(), zoom);

    var isiPad = navigator.userAgent.match( /iPad/i ) != null;

     if ( isiPad )
     {
        this.touchhandler = new TouchHandler( map, 4 );
     }
   }
    function handleMouseMove(evt)
    {
    var lonLat = map.getLonLatFromViewPortPx(new OpenLayers.Pixel(evt.xy.x , evt.xy.y) );
    var dmsOutput = document.getElementById('dmsMousePosition');
    var mgrsOutput = document.getElementById('mgrsMousePosition');

    if(lonLat.lat > "90" || lonLat.lat < "-90" || lonLat.lon > "180" || lonLat.lon < "-180")
    {
        if(dmsOutput) dmsOutput.innerHTML = "<b>DMS:</b> ";
        if(mgrsOutput) mgrsOutput.innerHTML = "<b>MGRS:</b> ";
    }
    else
    {
        if(dmsOutput) dmsOutput.innerHTML = "<b>DMS:</b> " + convert.ddToDms(lonLat.lat, "latitude") + " " + convert.ddToDms(lonLat.lon, "longitude");
        if(mgrsOutput) mgrsOutput.innerHTML = "<b>MGRS:</b> " + convert.ddToMgrs(lonLat.lat, lonLat.lon);
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

    var ddOutput = document.getElementById('ddMousePosition');
    if(lonLat.lat > "90" || lonLat.lat < "-90" || lonLat.lon > "180" || lonLat.lon < "-180")
    {
        if(ddOutput) ddOutput.innerHTML = "<b>DD:</b> ";
    }
    else
    {
        if(ddOutput) ddOutput.innerHTML = "<b>DD:</b> " + lonLat.lat + " " + lonLat.lon;
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
      { layers: "${(rasterEntries*.indexId).join(',')}", format: format, sharpen_mode:sharpen_mode, stretch_mode:stretch_mode, stretch_mode_region: stretch_mode_region, transparent:transparent  },
      {isBaseLayer: true, buffer:0, singleTile:true, ratio:1.0, quicklook:true, transitionEffect: "resize",
       displayOutsideMaxExtent:false})
    ];
    map.addLayers(rasterLayers);

    <g:each in="${kmlOverlays}" var="kmlOverlay" status="i">

      if ( ! kmlLayers ) {
        kmlLayers = new Array();
     }

   var kmlLayer =  new OpenLayers.Layer.Vector("${kmlOverlay.name}", {
      visibility: ${grailsApplication.config.views.mapView.defaultOverlayVisiblity},
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

  function changeQuickLookOpts()
  {
    for ( var layer in rasterLayers )
    {
      rasterLayers[layer].mergeNewParams({quicklook:$("quicklook").value});
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
  function changeBandsOpts()
  {
      var bands = $("bands").value;

      for ( var layer in rasterLayers )
      {
        rasterLayers[layer].mergeNewParams({bands:bands});
      }
  }

  function setCenterText()
  {
      var center = map.getCenter();
      $("center").value = center.lat + ", " + center.lon;
  }

  function setCenter()
  {
      var centerRegExp = /^(\-?\d{1,2}\.?\d+)(\,\s)(\-?\d{1,3}\.?\d+)$/

      if ($("center").value.match(centerRegExp))
      {
          var centerLat = RegExp.$1;
          var centerLon = RegExp.$3;

          if (centerLat >= parseFloat("${top}") || centerLat <= parseFloat("${bottom}") ||
                  centerLon >= parseFloat("${right}") || centerLon <= parseFloat("${left}"))
          {
              alert("Error: Latitude/Longitude input is outside the image extent.\n\n" +
                      "Latitude input must be between " + parseFloat("${bottom}") + " and " + parseFloat("${top}") + ".\n\n" +
                      "Longitude input must be between " + parseFloat("${left}") + " and " + parseFloat("${right}") + ".");
          }

          else
          {
              var zoom = map.getZoom();
              var center = new OpenLayers.LonLat(centerLon, centerLat);

              map.setCenter(center, zoom);
          }
      }

      else
      {
          var ctrEx = map.getCenter();
          alert("Error: Invalid Latitude/Longitude input.\n\nEx. " + ctrEx.lat + ", " + ctrEx.lon);
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


        var container = $("toolBar");

        var panel = new OpenLayers.Control.Panel(
        { div: container,defaultControl: zoomBoxButton,'displayClass': 'olControlPanel'}
                );


        var navButton = new OpenLayers.Control.NavigationHistory({
          nextOptions: {title: "Next View" },
          previousOptions: {title: "Previous View"}
        });

        map.addControl(navButton);

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
</head>

<body class="yui-skin-sam" onresize="bodyOnResize();">

<content tag="top">

  <div class="nav">

    <span class="menuButton">
      <g:link class="home" uri="/">
        OMAR™ Home
      </g:link>
    </span>

    <span class="menuButton">
      <a href="${createLink(controller: "ogc", action: "wms", params: [request: "GetCapabilities", layers: (rasterEntries*.indexId).join(',')])}">
        WMS GetCapabilities
      </a>
    </span>

    <span class="menuButton">
      <a href="javascript:getKML('${(rasterEntries*.indexId).join(',')}')">
        Generate KML
      </a>
    </span>

    <span class="menuButton">
      <a href="${createLink(controller: "mapView", action: "multiLayer", params: [layers: (rasterEntries*.indexId).join(',')])}">
        Multi Layer Viewer
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

<content tag="left">

  <div class="niceBox">
    <div class="niceBoxHd">Map Center:</div>
    <div class="niceBoxBody">
      <g:textField name="center" value="${queryParams?.center}" onChange="setCenter()" size="30"/>
    </div>
  </div>

  <g:form name="wmsParams" method="POST" url="[action:'wms',controller:'ogc']">
    <input type="hidden" name="request" value=""/>
    <input type="hidden" name="layers" value=""/>
    <input type="hidden" name="bbox" value=""/>

    <div class="niceBox">
      <div class="niceBoxHd">Image Adjustments:</div>
      <div class="niceBoxBody">
        <ol>
          <li>Sharpen:</li>
          <li>
            <g:select id="sharpen_mode" name="sharpen_mode" from="${['none', 'light', 'heavy']}" onChange="changeSharpenOpts()"/>
          </li>
          <li>Stretch:</li>
          <li>
            <g:select id="stretch_mode" name="stretch_mode" from="${['linear_auto_min_max', 'linear_1std_from_mean', 'linear_2std_from_mean', 'linear_3std_from_mean', 'none']}" onChange="changeHistoOpts()"/>
          </li>
          <li>Region:</li>
          <li>
            <g:select id="stretch_mode_region" name="stretch_mode_region" from="${['global', 'viewport']}" onChange="changeHistoOpts() "/>
          </li>

          <g:if test="${rasterEntries?.numberOfBands.get(0) == 2}">
            <li>Bands:</li>
            <li>
              <g:select id="bands" name="bands" from="${['0,1','1,0','0','1']}" onChange="changeBandsOpts()"/>
            </li>
          </g:if>

          <g:if test="${rasterEntries?.numberOfBands.get(0) >= 3}">
            <li>Bands:</li>
            <li>
              <g:select id="bands" name="bands" from="${['0,1,2','2,1,0','0','1','2']}" onChange="changeBandsOpts()"/>
            </li>
          </g:if>

          <li>Quick Look:</li>
          <li>
            <g:select id="quicklook" name="quicklook"
                from="${[[name: 'On', value: 'true'], [name: 'Off', value: 'false']]}"
                optionValue="name" optionKey="value"
                onChange="changeQuickLookOpts()"/>
          </li>
        </ol>
      </div>
    </div>

    <div class="niceBox">
      <div class="niceBoxHd">Mouse Position:</div>
      <div class="niceBoxBody">
        <div id="ddMousePosition">&nbsp;</div>
        <div id="dmsMousePosition">&nbsp;</div>
        <div id="mgrsMousePosition">&nbsp;</div>
      </div>
    </div>
  </g:form>
</content>

<content tag="middle">
</content>
</body>
</html>