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
   #homeMenu{
   background: url( ../images/skin/house.png )  left no-repeat;
  	z-index: 99999;
  }
  #exportMenu, #viewMenu{
  	z-index: 99999;
  }
  #slider-brightness-bg, #slider-contrast-bg{
    width:120px;
    background:url(${resource(plugin: 'richui', dir:'js/yui/slider/assets', file:'bg-fader.gif')}) 5px 0 no-repeat;
  }
  </style>
</head>

<body class="yui-skin-sam" onload="init();">
<omar:bundle contentType="javascript" files="${[
     [plugin: 'omar-core', dir:'js', file: 'coordinateConversion.js'],
     [plugin: 'omar-core', dir:'js', file: 'mapwidget.js']
 ]}"/>

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
								<li class="yuimenuitem"><a class="yuimenuitemlabel" href="${createLink(controller: "ogc", action: "wms", params: [request: "GetCapabilities", layers: (rasterEntries*.indexId).join(',')])}" title="Show OGC WMS Capabilities">OGC WMS Capabilities</a></li>
								<li class="yuimenuitem"><a class="yuimenuitemlabel" href="javascript:getKML('${(rasterEntries*.indexId).join(',')}')" title="Export KML">KML</a></li>
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
								<li class="yuimenuitem"><a class="yuimenuitemlabel" href="${createLink(controller: 'mapView', action: 'imageSpace', params: [layers: (rasterEntries*.indexId).join(',')])}" title="Image Space Viewer">Image Space Viewer</a></li>
								<li class="yuimenuitem"><a class="yuimenuitemlabel" href="${createLink(controller: 'mapView', action: 'multiLayer', params: [layers: (rasterEntries*.indexId).join(',')])}" title="Multi Layer Viewer">Multi Layer Viewer</a></li>
							</ul>
						</div>
					</div>
				</li>
				
			</ul>
		</div>
	</div>

</content>

<content tag="left">
	
	<div class="niceBox">
		<div class="niceBoxHd">Map Center:</div>
		<div class="niceBoxBody">
			<ol>
				<li>DD:</li>
				<li><g:textField name="ddMapCtr" value="" onChange="setMapCtr('dd', this.value)" size="28"
					title="Enter decimal degree coordinates and click off the text field to re-center the map. Example: 25.77, -80.18" /></li>
			</ol>
			<ol>
				<li>DMS:</li>
				<li><g:textField name="dmsMapCtr" value="" onChange="setMapCtr('dms', this.value)" size="28"
					title="Enter degree minute seconds coordinates and click off the text field to re-center the map. Example: 25°46'20.66'' N, 80°11'23.64'' W" /></li>
			</ol>      
			<ol>
				<li>MGRS:</li>
				<li><g:textField name="mgrsMapCtr" value="" onChange="setMapCtr('mgrs', this.value)" size="28" 
					title="Enter mgrs coordinate and click off the text field to re-center the map. Example: 17RNJ8123050729 or 17 RNJ 81230 50729" /></li>
			</ol>
		</div>
	</div>
	
	<g:form name="wmsParams" method="POST" url="[action:'wms', controller:'ogc']">
	<input type="hidden" name="request" value=""/>
	<input type="hidden" name="layers" value=""/>
	<input type="hidden" name="bbox" value=""/>
	<input type="hidden" id="contrast" name="contrast" value=""/>
	<input type="hidden" id="brightness" name="brightness" value=""/>

	<div class="niceBox">
		<div class="niceBoxHd">Image Adjustments:</div>
		<div class="niceBoxBody">
			<ol>
				<li>Interpolation:</li>
				<li>
					<g:select id="interpolation" name="interpolation" from="${['bilinear', 'nearest neighbor', 'cubic', 'sinc']}" onChange="chgInterpolation()"/>
				</li>
				<hr/>
				<label>Brightness: <input type="text"  readonly="true" id="brightnessTextField" size="3" maxlength="5" value=""></label>

				<li>
					<div id="slider-brightness-bg" class="yui-h-slider" tabindex="-1" hidefocus="false"> 
		    			<div id="slider-brightness-thumb" class="yui-slider-thumb"><img src="${resource(plugin: 'richui', dir:'js/yui/slider/assets', file:'thumb-n.gif')}"></div> 
					</div> 
				</li>
				<label>Contrast: <input type="text"  readonly="true"  id="contrastTextField" size="3" maxlength="5" value=""></label>
				<li>
					<div id="slider-contrast-bg" class="yui-h-slider" tabindex="-1" hidefocus="false"> 
		    			<div id="slider-contrast-thumb" class="yui-slider-thumb"><img src="${resource(plugin: 'richui', dir:'js/yui/slider/assets', file:'thumb-n.gif')}"></div> 
					</div> 
				</li>
				<button id="brightnessContrastReset" type="button" onclick="javascript:resetBrightnessContrast()">Reset</button>
				<hr/>
				<li>Sharpen:</li>
				<li>
					<g:select id="sharpen_mode" name="sharpen_mode" from="${['none', 'light', 'heavy']}" onChange="chgSharpenMode()"/>
				</li>
				<li>Stretch:</li>
				<li>
					<g:select id="stretch_mode" name="stretch_mode" from="${['linear_auto_min_max', 'linear_1std_from_mean', 'linear_2std_from_mean', 'linear_3std_from_mean', 'none']}" onChange="chgStretchMode()"/>
				</li>
          		<li>Region:</li>
          		<li>
            		<g:select id="stretch_mode_region" name="stretch_mode_region" from="${['global', 'viewport']}" onChange="chgStretchMode() "/>
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

          		<li>Orthorectification:</li>
          		<li>
            <g:select id="quicklook" name="quicklook"
                from="${[[name: 'Rigorous', value: 'false'],[name: 'Simple', value: 'true']]}"
                optionValue="name" optionKey="value"
                onChange="chgQuickLookMode()"/>
          </li>
        </ol>
      </div>
    </div>




  <div class="niceBox">
	<div class="niceBoxHd">Map Measurement Tool:</div>
	<div class="niceBoxBody">
		<ul>
			<li>Measurement Units:</li>
			<li>Not certified for targeting.</li>
			<li><g:select name="measurementUnits" from="${['kilometers', 'meters', 'feet', 'miles', 'yards']}"
				title="Select a unit of measuremen and use the path and polygon measurment tools in the map toolbar." /></li>
			<div id="pathMeasurement"></div>
			<div id="polygonMeasurement"></div>
		</ul>
	</div>
  </div>

   
  </g:form>
</content>

<content tag="middle">
</content>

<g:javascript>
var coordConvert = new CoordinateConversion();
var mapWidget;
var zoomInButton;
var kmlLayers;
var rasterLayers;
var select;
var wcsParams = new OmarWcsParams();

//var fullResScale = parseFloat("${fullResScale}");
var left = parseFloat("${left}");
var bottom = parseFloat("${bottom}");
var right = parseFloat("${right}");
var top = parseFloat("${top}");
var largestScale = parseFloat("${largestScale}");
var smallestScale = parseFloat("${smallestScale}");

var brightnessSlider = YAHOO.widget.Slider.getHorizSlider("slider-brightness-bg",  "slider-brightness-thumb", 0, 100, 1);
var contrastSlider= YAHOO.widget.Slider.getHorizSlider("slider-contrast-bg",  "slider-contrast-thumb", 0, 100, 1);

function resetBrightnessContrast()
{
	brightnessSlider.setRealValue(0);  
	contrastSlider.setRealValue(1.0);  
}

function init()
{
// we need to pass a json string or object and save to the session
// and reload here
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
    if(${rasterEntries?.numberOfBands.get(0) >= 3})
    {
        wcsParams.setProperties({bands:"0,1,2"});
    }
    else
    {
        wcsParams.setProperties({bands:"0"});
    }
	brightnessSlider.animate = false;
	 
    brightnessSlider.getRealValue = function() { 
	    return ((this.getValue()-50)/50.0); 
    } 
    contrastSlider.getRealValue = function() { 
        var value = (this.getValue()/100.0)*2.0;
        return value;
    } 
    brightnessSlider.setRealValue = function(value) { 
	    this.setValue((value+1)*50);  
    } 
    contrastSlider.setRealValue = function(value) {
    	this.setValue(value*50); 
    } 
	 
    brightnessSlider.subscribe("slideEnd", function() { 
		for(var layer in rasterLayers)
		{
		    wcsParams.setProperties({brightness:this.getRealValue()});
			rasterLayers[layer].mergeNewParams({brightness:this.getRealValue()});
		}
    }); 	
    contrastSlider.subscribe("slideEnd", function() { 
		for(var layer in rasterLayers)
		{
		    wcsParams.setProperties({contrast:this.getRealValue()});
			rasterLayers[layer].mergeNewParams({contrast:this.getRealValue()});
		}
    }); 
     contrastSlider.subscribe("change", function(offsetFromStart) 
    {
    	$("contrast").value = this.getRealValue();
    	$("contrastTextField").value = this.getRealValue();
    });
    brightnessSlider.subscribe("change", function(offsetFromStart) 
    {
		wcsParams.setProperties({brightness:this.getRealValue()});
    	$("brightness").value = this.getRealValue();
    	$("brightnessTextField").value = this.getRealValue();
    });
    	
	brightnessSlider.setRealValue(0);  
	contrastSlider.setRealValue(1);  
	var bounds = new OpenLayers.Bounds(left, bottom, right, top);
	
	mapWidget = new MapWidget();
	mapWidget.setupMapWidgetWithOptions("map", {controls: [], maxExtent:bounds, maxResolution:largestScale, minResolution:smallestScale});
	mapWidget.setFullResScale(parseFloat("${fullResScale}"));
    mapWidget.changeMapSize();
	
	
	setupLayers();
    mapWidget.setupAoiLayer();
	mapWidget.setupToolBar();
  	
	mapWidget.getMap().addControl(new OpenLayers.Control.LayerSwitcher());
	var overview = new OpenLayers.Control.OverviewMap({maximized: true});
    mapWidget.getMap().addControl(overview);
	mapWidget.getMap().addControl(new OpenLayers.Control.Scale());
	mapWidget.getMap().addControl(new OpenLayers.Control.ScaleLine());
	
	mapWidget.getMap().events.register('mousemove',map,setMouseMapCtrTxt);
	mapWidget.getMap().events.register("moveend", map, this.setMapCtrTxt);
	
  	var zoom = mapWidget.getMap().getZoomForExtent(bounds, true);
	mapWidget.getMap().setCenter(bounds.getCenterLonLat(), zoom);
    var oMenu = new YAHOO.widget.MenuBar("rasterMenu", { 
                                                autosubmenudisplay: true, 
                                                hidedelay: 750, 
                                                lazyload: true,
                                                zIndex:9999}); 
	oMenu.render();
}

function setMapCtrTxt()
{
    var center = mapWidget.getMap().getCenter();
    $("ddMapCtr").value = center.lat + ", " + center.lon;
	$("dmsMapCtr").value = coordConvert.ddToDms(center.lat, "lat") + ", " + coordConvert.ddToDms(center.lon, "lon");
	$("mgrsMapCtr").value = coordConvert.ddToMgrs(center.lat, center.lon);
}

function setMapCtr(unit, value)
{
	if(unit == "dd")
	{
		var ddRegExp = /^(\-?\d{1,2}\.?\d+)\,?\s?(\-?\d{1,3}\.?\d+)$/
		if($("ddMapCtr").value.match(ddRegExp))
		{
			var ddMapCtr = new OpenLayers.LonLat(RegExp.$2, RegExp.$1);
			mapWidget.getMap().setCenter(ddMapCtr, mapWidget.getMap().getZoom());
		}
		else
		{
			alert("Invalid Input.");
		}
	}
	else if(unit == "dms")
	{
		var dmsRegExp = /^(\d{1,2})\°?\s?(\d{2})\'?\s?(\d{2}\.?\d+)\"?\s?([NnSs])\,?\s?(\d{1,3})\°?\s?(\d{2})\'?\s?(\d{2}\.?\d+)\"?\s?([EeWw])$/
		if($("dmsMapCtr").value.match(dmsRegExp))
		{
			var dmsMapCtr = new OpenLayers.LonLat(coordConvert.dmsToDd(RegExp.$5, RegExp.$6, RegExp.$7, RegExp.$8), 
													coordConvert.dmsToDd(RegExp.$1, RegExp.$2, RegExp.$3, RegExp.$4));
			mapWidget.getMap().setCenter(dmsMapCtr, mapWidget.getMap().getZoom());
		}
		else
		{
			alert("Invalid Input.");
		}
	}
	else if(unit == "mgrs")
	{	
		
		
		
		var foo = coordConvert.mgrsToUtm($("mgrsMapCtr").value);
		
		
		
		var mgrsRegExpUtm = /^(-?\d{1,2})(\.\d+)?\s?(-?\d{1,3})(\.\d+)?/

       
        if ( foo.match( mgrsRegExpUtm ) )
        {
            var centerLat = parseInt( RegExp.$1, 10 ) + RegExp.$2;
            var centerLon = parseInt( RegExp.$3, 10 ) + RegExp.$4;

			  var zoom = mapWidget.getMap().getZoom();
		        var center = new OpenLayers.LonLat( centerLon, centerLat );

		        mapWidget.getMap().setCenter( center, zoom );

            
        }		
	}
}

function chgInterpolation()
{
	var interpolation = $("interpolation").value;
	obj = {interpolation:interpolation};
    wcsParams.setProperties(obj);

	for(var layer in rasterLayers)
	{
		rasterLayers[layer].mergeNewParams(obj);
	}
}

function chgSharpenMode()
{
	var sharpen_mode = $("sharpen_mode").value;
	obj = {sharpen_mode:sharpen_mode};
    wcsParams.setProperties(obj);

	for(var layer in rasterLayers)
	{
		rasterLayers[layer].mergeNewParams(obj);
	}
}

function chgStretchMode()
{
	var stretch_mode = $("stretch_mode").value;
	var stretch_mode_region = $("stretch_mode_region").value;
	obj = {stretch_mode:stretch_mode, stretch_mode_region: stretch_mode_region};
    wcsParams.setProperties(obj);
	for(var layer in rasterLayers)
	{
		rasterLayers[layer].mergeNewParams(obj);
	}
}

function chgQuickLookMode()
{
	obj = {quicklook:$("quicklook").value};
    wcsParams.setProperties(obj);
	for(var layer in rasterLayers)
  	{
		rasterLayers[layer].mergeNewParams(obj);
	}
}

function changeBandsOpts()
{
	var bands = $("bands").value;
	obj = {bands:bands};
    wcsParams.setProperties(obj);

	for(var layer in rasterLayers)
	{
		rasterLayers[layer].mergeNewParams(obj);
	}
}

function setMouseMapCtrTxt(evt)
{
	var center = mapWidget.getMap().getLonLatFromViewPortPx(new OpenLayers.Pixel(evt.xy.x , evt.xy.y));
	var fixed = 6;
	
	var ddMouseCtr = document.getElementById("ddMouseMapCtr");
	ddMouseMapCtr.innerHTML = "DD: " + center.lat.toFixed(fixed) + ", " + center.lon.toFixed(fixed);

	var dmsMouseCtr = document.getElementById("dmsMouseMapCtr");
	dmsMouseMapCtr.innerHTML = "DMS: " + coordConvert.ddToDms(center.lat, "lat") + ", " + coordConvert.ddToDms(center.lon, "lon");

	var mgrsMouseCtr = document.getElementById("mgrsMouseMapCtr");
	mgrsMouseMapCtr.innerHTML = "MGRS: " + coordConvert.ddToMgrs(center.lat, center.lon);
}

function setupToolbar()
{
	var panButton = new OpenLayers.Control.MouseDefaults({title: "Click pan button to activate. Once activated click the map and drag the mouse to pan."});
	
	var zoomBoxButton = new OpenLayers.Control.ZoomBox({title: "Click the zoom box button to activate. Once activated click and drag over an area of interest on the map to zoom into."});
	
	zoomInButton = new OpenLayers.Control.Button({title: "Click to zoom in.",
	displayClass: "olControlZoomIn",
	trigger: zoomIn});
	
	var zoomOutButton = new OpenLayers.Control.Button({title: "Click to zoom out.",
    displayClass: "olControlZoomOut",
    trigger: zoomOut});
	
	var zoomInFullResButton = new OpenLayers.Control.Button({title: "Click to zoom into full resolution.",
    displayClass: "olControlZoomToLayer",
    trigger: zoomInFullRes});
	
	var zoomMaxExtentButton = new OpenLayers.Control.ZoomToMaxExtent({title:"Click to zoom to the max extent."});
	
	var navButton = new OpenLayers.Control.NavigationHistory({nextOptions:{title:"Click to go to next view."}, 
	previousOptions:{title:"Click to go to previous view."}});

	var pathMeasurement = document.getElementById("pathMeasurement");
	var pathMeasurementButton = new OpenLayers.Control.Measure(OpenLayers.Handler.Path, {
		title:"Click path measurement button to activate. Once activated click points on the map to create a path that you wish to measure. When you are done creating your path double click to end. Measurement results will appear in the left column.",
		displayClass: "olControlMeasureDistance", persist: true,
		eventListeners:
		{
			measure: function(evt)
			{
				if($("measurementUnits").value == "kilometers")
				{
					if(evt.units == "km")
					{
						pathMeasurement.innerHTML = "Path: " + evt.measure + " km [<a href='javascript:clearPathMeasurement()'>X</a>]";
					}
					else if(evt.units == "m")
					{
						pathMeasurement.innerHTML = "Path: " + (evt.measure * 0.001) + " km [<a href='javascript:clearPathMeasurement()'>X</a>]"; 
					}
				}
				else if($("measurementUnits").value == "meters")
				{
					if(evt.units == "km" )
					{
						pathMeasurement.innerHTML = "Path: " + (evt.measure * 1000) + " m [<a href='javascript:clearPathMeasurement()'>X</a>]";
	                }
					else if(evt.units == "m")
					{
						pathMeasurement.innerHTML = "Path: " + evt.measure + " m [<a href='javascript:clearPathMeasurement()'>X</a>]";
					}
				}
				else if($("measurementUnits").value == "feet")
				{
					if(evt.units == "km")
					{
						pathMeasurement.innerHTML = "Path: " + (evt.measure * 3280.839895) + " ft [<a href='javascript:clearPathMeasurement()'>X</a>]";
					}
					else if(evt.units == "m")
					{
						pathMeasurement.innerHTML = "Path: " + (evt.measure * 3.280839895) + " ft [<a href='javascript:clearPathMeasurement()'>X</a>]";
					}
				}
				else if($("measurementUnits").value == "miles")
				{
					if(evt.units == "km")
					{
						pathMeasurement.innerHTML = "Path: " + (evt.measure * 0.62137119224) + " mi [<a href='javascript:clearPathMeasurement()'>X</a>]";
					}
					else if(evt.units == "m")
					{
						pathMeasurement.innerHTML = "Path: " + (evt.measure * 0.00062137119224) + " mi [<a href='javascript:clearPathMeasurement()'>X</a>]";
					}
				}
				else if($("measurementUnits").value == "yards")
				{
					if(evt.units == "km")
					{
						pathMeasurement.innerHTML = "Path: " + (evt.measure * 1093.6132983) + " yd [<a href='javascript:clearPathMeasurement()'>X</a>]";
					}
					else if(evt.units == "m")
					{
						pathMeasurement.innerHTML = "Path: " + (evt.measure * 1.0936132983) + " yd [<a href='javascript:clearPathMeasurement()'>X</a>]";
					}
				}
			}
		}
	});

	var polygonMeasurement = document.getElementById("polygonMeasurement");
	var polygonMeasurementButton = new OpenLayers.Control.Measure(OpenLayers.Handler.Polygon, {
		title:"Click polygon measurement button to activate. Once activated click points on the map to create a polygon that you wish to measure. When you are done creating your polygon double click to end. Measurement results will appear in the left column.",
		displayClass: "olControlMeasureArea", persist: true,
		eventListeners:
		{
			measure: function(evt)
			{
				if($("measurementUnits").value == "kilometers")
				{
					if(evt.units == "km")
					{
						polygonMeasurement.innerHTML = "Polygon: " + evt.measure + " km² [<a href='javascript:clearPolygonMeasurement()'>X</a>]";
					}
					else if(evt.units == "m")
					{
						polygonMeasurement.innerHTML = "Polygon: " + (evt.measure * 0.001) + " km² [<a href='javascript:clearPolygonMeasurement()'>X</a>]"; 
					}
				}
				else if($("measurementUnits").value == "meters")
				{
					if(evt.units == "km" )
					{
						polygonMeasurement.innerHTML = "Polygon: " + (evt.measure * 1000) + " m² [<a href='javascript:clearPolygonMeasurement()'>X</a>]";
	                }
					else if(evt.units == "m")
					{
						polygonMeasurement.innerHTML = "Polygon: " + evt.measure + " m² [<a href='javascript:clearPolygonMeasurement()'>X</a>]";
					}
				}
				else if($("measurementUnits").value == "feet")
				{
					if(evt.units == "km")
					{
						polygonMeasurement.innerHTML = "Polygon: " + (evt.measure * 3280.839895) + " ft² [<a href='javascript:clearPolygonMeasurement()'>X</a>]";
					}
					else if(evt.units == "m")
					{
						polygonMeasurement.innerHTML = "Polygon: " + (evt.measure * 3.280839895) + " ft² [<a href='javascript:clearPolygonMeasurement()'>X</a>]";
					}
				}
				else if($("measurementUnits").value == "miles")
				{
					if(evt.units == "km")
					{
						polygonMeasurement.innerHTML = "Polygon: " + (evt.measure * 0.62137119224) + " mi² [<a href='javascript:clearPolygonMeasurement()'>X</a>]";
					}
					else if(evt.units == "m")
					{
						polygonMeasurement.innerHTML = "Polygon: " + (evt.measure * 0.00062137119224) + " mi² [<a href='javascript:clearPolygonMeasurement()'>X</a>]";
					}
				}
				else if($("measurementUnits").value == "yards")
				{
					if(evt.units == "km")
					{
						polygonMeasurement.innerHTML = "Polygon: " + (evt.measure * 1093.6132983) + " yd² [<a href='javascript:clearPolygonMeasurement()'>X</a>]";
					}
					else if(evt.units == "m")
					{
						polygonMeasurement.innerHTML = "Polygon: " + (evt.measure * 1.0936132983) + " yd² [<a href='javascript:clearPolygonMeasurement()'>X</a>]";
					}
				}
			}
		}
	});
	
	var container = $("toolBar");
	
	var panel = new OpenLayers.Control.Panel(
	{
		div: container,
		defaultControl: zoomBoxButton,
		displayClass: "olControlPanel"
	});
	
	mapWidget.getMap().addControl(navButton);
	
	panel.addControls([
	panButton,
	zoomBoxButton,
	zoomInButton,
	zoomOutButton,
	zoomInFullResButton,
	zoomMaxExtentButton,
	navButton.next,
	navButton.previous,
	pathMeasurementButton,
	polygonMeasurementButton
	]);
	
	mapWidget.getMap().addControl(panel);
}

function clearPathMeasurement()
{
	pathMeasurement.innerHTML = "";
}

function clearPolygonMeasurement()
{
	polygonMeasurement.innerHTML = "";
}

function zoomIn()
{
	mapWidget.getMap().zoomIn();

	var fullRes = mapWidget.getMap().getZoomForResolution(parseFloat(fullResScale), true);
	if(mapWidget.getMap().getZoom() >= fullRes)
	{
		zoomInButton.displayClass = "olControlZoomOut";
	}
}

function zoomOut()
{
	mapWidget.getMap().zoomOut();
	
	var fullRes = mapWidget.getMap().getZoomForResolution(parseFloat(fullResScale), true);
	if(mapWidget.getMap().getZoom() < fullRes)
	{
		zoomInButton.displayClass = "olControlZoomIn";
	}
}

function zoomInFullRes()
{
	var zoom = mapWidget.getMap().getZoomForResolution(parseFloat("${fullResScale}"), true);
    mapWidget.getMap().zoomTo(zoom);

	zoomInButton.displayClass = "olControlZoomOut";
}

function changeMapSize(mapWidth, mapHeight)
{
	if(mapWidth&&mapHeight)
	{
		var Dom = YAHOO.util.Dom;

		Dom.get("map").style.width  = mapWidth + "px";
		Dom.get("map").style.height = mapHeight + "px";
	}
	
	mapWidget.changeMapSize()
}

function getKML(layers)
{	
	var wmsParamForm = document.getElementById('wmsParams')
	
	wmsParamForm.request.value = "GetKML";
	wmsParamForm.layers.value = layers;
	var extent = mapWidget.getMap().getExtent();
	wmsParamForm.bbox.value = extent.toBBOX();
	
	wmsParamForm.sharpen_mode.value = $("sharpen_mode").value;
	wmsParamForm.stretch_mode.value = $("stretch_mode").value;
	wmsParamForm.stretch_mode_region.value = $("stretch_mode_region").value;
	var numberOfBands = parseInt("${rasterEntries?.numberOfBands.get(0)}");
	if(numberOfBands > 1)
	{
		wmsParamForm.bands.value = $("bands").value;              /////// bands doesn't appear to working on kml output ///////
	}
	wmsParamForm.quicklook.value = $("quicklook").value;

	wmsParamForm.submit();
}

function setupLayers()
{
	var format = "image/jpeg";
	//var format = "image/png";
	//var format = "image/gif";
	var transparent = false;
	
	var sharpen_mode = $("sharpen_mode").value;
	var stretch_mode = $("stretch_mode").value;
	var stretch_mode_region = $("stretch_mode_region").value;
	
	rasterLayers = [
	new OpenLayers.Layer.WMS("Raster", "${createLink(controller: "ogc", action: "wms")}",
    {layers: "${(rasterEntries*.indexId).join(',')}", format: format, sharpen_mode:sharpen_mode, stretch_mode:stretch_mode, stretch_mode_region: stretch_mode_region, transparent:transparent},
	{isBaseLayer: true, buffer: 0, singleTile: true, ratio: 1.0, quicklook: true, transitionEffect: "resize", displayOutsideMaxExtent:false})];
	
	mapWidget.getMap().addLayers(rasterLayers);
	
	<g:each in="${kmlOverlays}" var="kmlOverlay" status="i">
	if(!kmlLayers)
	{
		kmlLayers = new Array();
   	}
	
	kmlLayer = new OpenLayers.Layer.Vector("${kmlOverlay.name}", {
		visibility: ${grailsApplication.config.views.mapView.defaultOverlayVisiblity},
		projection: mapWidget.getMap().displayProjection,
		strategies: [new OpenLayers.Strategy.Fixed()],
		protocol: new OpenLayers.Protocol.HTTP({
			url: "${kmlOverlay.url}",
			format: new OpenLayers.Format.KML({
				extractStyles: true,
				extractAttributes: true})})
			});
			
			kmlLayers[${i}] = kmlLayer;
			kmlLayer.events.on({
				"featureselected": onFeatureSelect,
				"featureunselected": onFeatureUnselect
			});
			
		mapWidget.getMap().addLayers(kmlLayers);
		select = new OpenLayers.Control.SelectFeature(kmlLayers);
  		mapWidget.getMap().addControl(select);
  		select.activate();
	</g:each>
}

function onPopupClose(evt)
{
	select.unselectAll();
}


function toUrlParamString(params)
{

	 var urlParams = "";
	 for (var key in params) 
	 { 
	    if(urlParams == "")
	    {
	   		urlParams = key + "=" + params[key];
	    }
	    else
	    {
	    	urlParams = urlParams + "&" + key + "=" + params[key]
	    }
	 }
	 return urlParams
}

function onFeatureSelect(event)
{
	var feature = event.feature;
	
	var content = "<h2>"+feature.attributes.name + "</h2>" + feature.attributes.description;
	if (content.search("<script") != -1)
	{
		content = "Content contained Javascript! Escaped content below.<br />" + content.replace(/</g, "&lt;");
	}
	popup = new OpenLayers.Popup.FramedCloud("chicken",
	feature.geometry.getBounds().getCenterLonLat(),
	new OpenLayers.Size(100,100),
	content,
	null, true, onPopupClose);
	feature.popup = popup;
	mapWidget.getMap().addPopup(popup);
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

function onFeatureUnselect(event)
{
	var feature = event.feature;
	if(feature.popup)
	{
		mapWidget.getMap().removePopup(feature.popup);
		feature.popup.destroy();
		delete feature.popup;
	}
}
</g:javascript>

</body>
</html>