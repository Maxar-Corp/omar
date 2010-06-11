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
  <meta name="layout" content="main7_mobile"/>

  <meta name="apple-mobile-web-app-capable" content="yes" />
  <meta name="apple-mobile-web-app-status-bar-style" content="black" />
  <meta name="viewport" content="minimum-scale=1.0, width=device-width, maximum-scale=1.6, user-scalable=no">

  <title>OMAR Ground Space Viewer</title>
 
  <openlayers:loadMapToolBar/>
  <openlayers:loadTheme theme="default"/>

  <style type="text/css">
    #map {
      width: 100%;
      height: 100%;
      border: 1px solid black;
    }
  </style>

  <openlayers:loadJavascript/>

  <g:javascript plugin="omar-core" src="touch.js"/>
</head>

<body>

<content tag="north">
  <g:form name="wmsParams" method="POST" url="[action:'wms',controller:'ogc']">
    <input type="hidden" name="sharpen_mode" value="none"/>
    <input type="hidden" name="stretch_mode" value="linear_auto_min_max"/>
    <input type="hidden" name="stretch_mode_region" value="global"/>
    <input type="hidden" name="request" value=""/>
    <input type="hidden" name="layers" value=""/>
    <input type="hidden" name="bbox" value=""/>
    <input type="hidden" name="terrain_correction" value=""/>
  </g:form>

  <span class="menuButton">
    <g:link class="home" uri="/">OMAR&#0153;</g:link>
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

  <span class="menuButton">
    <a href=javascript:zoomInFullRes()>Zoom To Full Resolution</a>
  </span>
</content>

<content tag="center">
  <div id="map"></div>
</content>

<content tag="south">

  <g:javascript>
   var map;
   var rasterLayers;
   var kmlLayers;
   var select;

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

   setupLayers();

   map.addControl(new OpenLayers.Control.Scale());
   map.addControl(new OpenLayers.Control.ScaleLine());

   var zoom = map.getZoomForExtent(bounds, true);

   map.setCenter(bounds.getCenterLonLat(), zoom);

   this.touchhandler = new TouchHandler( map, 4 );
   }

   function getKML(layers)
   {
   var extent = map.getExtent()
   var wmsParamForm = document.getElementById('wmsParams')
   wmsParamForm.sharpen_mode.value = $("sharpen_mode").value
   wmsParamForm.stretch_mode_region.value = $("stretch_mode_region").value
   wmsParamForm.stretch_mode.value = $("stretch_mode").value
   wmsParamForm.terrain_correction.value = $("terrain_correction").value
   wmsParamForm.request.value = "GetKML"
   wmsParamForm.layers.value = layers
   wmsParamForm.bbox.value = extent.toBBOX()
   wmsParamForm.submit()
   }

   function changeMapSize( mapWidth, mapHeight )
   {
   var Dom = YAHOO.util.Dom;

   Dom.get( "map" ).style.width = mapWidth + "px";
   Dom.get( "map" ).style.height = mapHeight + "px";

   map.updateSize();
   }

   function setupLayers()
   {
   var format = "image/jpeg";
   // var format = "image/png";
   // var format = "image/gif";
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


     function zoomInFullRes()
     {
         var zoom = map.getZoomForResolution(parseFloat("${fullResScale}"), true)
         map.zoomTo(zoom)
     }
   </g:javascript>
             </content>
 </body>
 </html>
 