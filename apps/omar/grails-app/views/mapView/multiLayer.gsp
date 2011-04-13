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

  <title>OMAR <g:meta name="app.version"/>: Ground Space Multi-Viewer</title>

  <openlayers:loadMapToolBar/>
  <openlayers:loadTheme theme="default"/>

  <style type="text/css">
  #map {
    width: 100%;
    height: 100%;
    border: 1px solid black;
  }

   #homeMenu{
   background: url( ../images/skin/house.png )  left no-repeat;
  	z-index: 99999;
  }
  #exportMenu, #viewMenu{
  	z-index: 99999;
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
  <openlayers:loadJavascript/>
  <g:javascript plugin="omar-core" src="touch.js"/>

</head>
<body class="yui-skin-sam" onload="init();">
<omar:bundle contentType="javascript" files="${[
     [plugin: 'omar-core', dir:'js', file: 'coordinateConversion.js'],
     [plugin: 'omar-core', dir:'js', file: 'mapwidget.js']
 ]}"/>

<g:javascript>
  var mapWidget = new MapWidget();
    var left = parseFloat("${left}");
    var bottom = parseFloat("${bottom}");
    var right = parseFloat("${right}");
    var top = parseFloat("${top}");
    var largestScale = parseFloat("${largestScale}");
    var smallestScale = parseFloat("${smallestScale}");
    var wcsParams = new OmarWcsParams();

  function changeMapSize()
  {
    var Dom = YAHOO.util.Dom;

    if(mapWidget) mapWidget.changeMapSize( );
  }

  function setupBaseLayer()
  {
    var baseLayer = null;
    var baseWMS = ${baseWMS as JSON};

    for ( foo  in baseWMS )
    {
      baseLayer = new OpenLayers.Layer.WMS( baseWMS[foo].name, baseWMS[foo].url,
              baseWMS[foo].params, baseWMS[foo].options );
      baseLayer.addOptions({displayOutsideMaxExtent:true});
      mapWidget.getMap().addLayer( baseLayer );
      mapWidget.getMap().setBaseLayer( baseLayer );
    }
  }

  function init()
  {
	var oMenu = new YAHOO.widget.MenuBar("rasterMenu", {
                                               autosubmenudisplay: true,
                                               hidedelay: 750,
                                               showdelay: 0,
                                               lazyload: true,
                                               zIndex:9999});
	oMenu.render();
    wcsParams.setProperties({
        brightness:"0",
        contrast:"1",
        sharpen_mode:"none",
        stretch_mode:"linear_auto_min_max",
        stretch_mode_region: "global",
        interpolation: "bilinear",
        srs: "EPSG:4326",
        crs: "EPSG:4326",
        bands:"",
        quicklook: false
    });

    var format = "${format}";
    var transparent = true;

	var bounds = new OpenLayers.Bounds(left, bottom, right, top);

	mapWidget = new MapWidget();
	mapWidget.setupMapWidgetWithOptions("map", {controls: [],  displayOutsideMaxExtent:true, maxExtent:bounds, maxResolution:largestScale, minResolution:smallestScale});
	mapWidget.setFullResScale(parseFloat("${fullResScale}"));
    mapWidget.changeMapSize();
    //map = new OpenLayers.Map( "map", {controls: [], maxExtent:bounds, maxResolution:largestScale, minResolution:smallestScale} );
    setupBaseLayer( );
    var layers = [
    <g:each var="rasterEntry" in="${rasterEntries}" status="i">

      <g:if test="${i > 0}">,</g:if>

      new OpenLayers.Layer.WMS(
      "Raster ${rasterEntry.id}",
                "${createLink(controller: 'ogc', action: 'wms')}",
        { layers: "${rasterEntry.indexId}", displayOutsideMaxExtent:true, format: format, stretch_mode_region: "global", stretch_mode:"linear_auto_min_max", transparent:transparent  },
        {isBaseLayer: false,buffer:0, singleTile:true, ratio:1.0, transitionEffect: "resize"}
                )
      <g:if test="${kmlOverlays}">

        , new OpenLayers.Layer.Vector( "KML", {
     projection: mapWidget.getMap().displayProjection,
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
    mapWidget.getMap().addLayers( layers );
    mapWidget.setupAoiLayer();
	mapWidget.setupToolBar();

	mapWidget.getMap().addControl(new OpenLayers.Control.LayerSwitcher());
	//var overview = new OpenLayers.Control.OverviewMap({maximized: true});
    //mapWidget.getMap().addControl(overview);
	mapWidget.getMap().addControl(new OpenLayers.Control.Scale());
	mapWidget.getMap().addControl(new OpenLayers.Control.ScaleLine());

  	var zoom = mapWidget.getMap().getZoomForExtent(bounds, true);
	mapWidget.getMap().setCenter(bounds.getCenterLonLat(), zoom);
  }

function getProjectedImage(params)
{
	 var link   = "${createLink(action: "wcs", controller: "ogc")}";
	 var extent = mapWidget.getSelectedOrViewportExtents();
	 var size   = mapWidget.getSizeInPixelsFromExtents(extent);
	 var wcsProperties = {"request":"GetCoverage",
	               	  "format":params.format,
	               	  "bbox":extent.toBBOX(),
	               	  "coverage":params.coverage,
	               	  "crs":"EPSG:4326",
	               	  "width":size.w,
	               	  "height":size.h}
    wcsParams.setProperties(wcsProperties);

    var form = $("wcsForm");
    var url = link + "?" + wcsParams.toUrlParams();

    if(form)
    {
        form.action = url;
        form.submit();
    }
}


</g:javascript>
<content tag="top">
    <form id="wcsForm" method="POST">
    </form>
<div id="rasterMenu" class="yuimenubar yuimenubarnav">
	<div class="bd">
		<ul class="first-of-type">
			
			<li class="yuimenubaritem first-of-type"><a class="yuimenubaritemlabel" id="homeMenu" href="${createLink(controller: 'home', action: 'index')}" title="OMAR™ Home">&nbsp;&nbsp;&nbsp;&nbsp;OMAR™ Home</a>
			</li>
			
			<li class="yuimenubaritem first-of-type"><a class="yuimenubaritemlabel" href="#exportMenu">Export</a>
				<div id="exportMenu" class="yuimenu">
					<div class="bd">
						<ul>
						
					
							<li class="yuimenuitem"><a class="yuimenuitemlabel" href="${createLink(controller: "ogc", action: "wms", params: [request: "GetCapabilities", layers: (rasterEntries*.id).join(',')])}" title="Show OGC WMS Capabilities">OGC WMS Capabilities</a></li>
							<li class="yuimenuitem"><a class="yuimenuitemlabel" href="${createLink(controller: "ogc", action: "wms", params: [request: "GetKML", layers: (rasterEntries*.id).join(',')])}" title="Export KML">KML</a></li>
									
						</ul>
                        <ul>
                            <li class="yuimenuitem"><a class="yuimenuitemlabel" href="javascript:getProjectedImage({'format':'image/jpeg', 'crs':'EPSG:4326', 'coverage':'${(rasterEntries*.indexId).join(',')}'})" title="Export Jpeg">Jpeg</a></li>
                            <li class="yuimenuitem"><a class="yuimenuitemlabel" href="javascript:getProjectedImage({'format':'geotiff', 'crs':'EPSG:4326', 'coverage':'${(rasterEntries*.indexId).join(',')}'})" title="Export Geotiff">Geotiff</a></li>
                            <li class="yuimenuitem"><a class="yuimenuitemlabel" href="javascript:getProjectedImage({'format':'geotiff_uint8', 'crs':'EPSG:4326', 'coverage':'${(rasterEntries*.indexId).join(',')}'})" title="Export Geotiff 8-Bit">Geotiff 8-Bit</a></li>
                            <li class="yuimenuitem"><a class="yuimenuitemlabel" href="javascript:getProjectedImage({'format':'geojp2', 'crs':'EPSG:4326', 'coverage':'${(rasterEntries*.indexId).join(',')}'})" title="Export Geo Jpeg 2000">Geo Jpeg 2000</a></li>
                            <li class="yuimenuitem"><a class="yuimenuitemlabel" href="javascript:getProjectedImage({'format':'geojp2_uint8', 'crs':'EPSG:4326', 'coverage':'${(rasterEntries*.indexId).join(',')}'})" title="Export Geo Jpeg 2000 8-Bit">Geo Jpeg 2000 8-Bit</a></li>
                        </ul>
					</div>
				</div>
			</li>
			
			<li class="yuimenubaritem first-of-type"><a class="yuimenubaritemlabel" href="#viewMenu">View</a>
				<div id="viewMenu" class="yuimenu">
					<div class="bd">
						<ul>
							<li class="yuimenuitem"><a class="yuimenuitemlabel" href="${createLink(controller: 'mapView', action: 'index', params: [layers: (rasterEntries*.indexId).join(',')])}" title="Ground Space Viewer">Ground Space</a></li>
						<g:if test="${rasterEntries?.size() == 1}">
							<li class="yuimenuitem"><a class="yuimenuitemlabel" href="${createLink(controller: 'mapView', action: 'imageSpace', params: [layers: (rasterEntries*.indexId).join(',')])}" title="Image Space Viewer">Image Space</a></li>
							</g:if>
						
						</ul>
					</div>
				</div>
			</li>
		
		</ul>
	</div>
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
