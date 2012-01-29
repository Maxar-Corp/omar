<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="rasterViewsStatic"/>
  <title>OMAR <g:meta name="app.version"/>: Ground Space Viewer</title>
  <meta name="apple-mobile-web-app-capable" content="yes"/>
  <meta name="apple-mobile-web-app-status-bar-style" content="black"/>
  <meta name="viewport" content="minimum-scale=1.0, width=device-width, maximum-scale=1.6, user-scalable=no">

  <style type="text/css">
  body {
    background: black;
    color: white;
  }

  div.niceBoxHd {
    background: #0b0b65;
  }

  div.niceBoxBody {
    background: #2f2f2f
  }

  #interpolation, #sharpen_mode, #stretch_mode, #stretch_mode_region, #bands, #quicklook {
    background: black;
    color: white;
  }

  #map {
    width: 100%;
    height: 100%;
  }

  .message {
    border: 0px solid #b2d1ff;
    color: #006dba;
    margin: 0px 0 0px 0;
    padding: 0px 0px 0px 35px
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

  #homeMenu {
    background: url(../images/skin/house.png) left no-repeat;
    z-index: 99999;
  }

  #exportMenu, #viewMenu {
    z-index: 99999;
  }

  #slider-brightness-bg, #slider-contrast-bg {
    width: 120px;
    background: url(${resource(plugin: 'yui', dir:'js/yui/slider/assets', file:'bg-fader.gif')}) 5px 0 no-repeat;
  }
  </style>
</head>

<body class="yui-skin-sam" onload="init();">

<content tag="top">
  <g:form name="wcsForm" method="POST"/>
  <g:form name="wmsFormId" method="POST"/>

  <div id="rasterMenu" class="yuimenubar yuimenubarnav">
    <div class="bd">
      <ul class="first-of-type">

        <li class="yuimenubaritem first-of-type"><a class="yuimenubaritemlabel" id="homeMenu"
                                                    href="${createLink(controller: 'home', action: 'index')}"
                                                    title="OMAR™ Home">&nbsp;&nbsp;&nbsp;&nbsp;OMAR™ Home</a>
        </li>

        <li class="yuimenubaritem first-of-type"><a class="yuimenubaritemlabel" href="#exportMenu">Export</a>

          <div id="exportMenu" class="yuimenu">
            <div class="bd">
              <ul>
                <li class="yuimenuitem"><a class="yuimenuitemlabel"
                                           href="${createLink(controller: "ogc", action: "wms", params: [request: "GetCapabilities", layers: (rasterEntries*.indexId).join(',')])}"
                                           title="Show OGC WMS Capabilities">OGC WMS Capabilities</a></li>
                <li class="yuimenuitem"><a class="yuimenuitemlabel"
                                           href="javascript:getKML('${(rasterEntries*.indexId).join(',')}')"
                                           title="Export KML">KML</a></li>
                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="javascript:getKmlSuperOverlay()"
                                           title="Export Image as Super Overlay">KML Super Overlay</a></li>
              </ul>
              <ul>
                <li class="yuimenuitem"><a class="yuimenuitemlabel"
                                           href="javascript:getProjectedImage({'format':'image/jpeg', 'crs':'EPSG:4326', 'coverage':'${(rasterEntries*.indexId).join(',')}'})"
                                           title="Export JPEG">JPEG</a></li>
                <li class="yuimenuitem"><a class="yuimenuitemlabel"
                                           href="javascript:getProjectedImage({'format':'image/png', 'crs':'EPSG:4326', 'coverage':'${(rasterEntries*.indexId).join(',')}'})"
                                           title="Export PNG">PNG</a></li>
                <li class="yuimenuitem"><a class="yuimenuitemlabel"
                                           href="javascript:getProjectedImage({'format':'png_uint8', 'crs':'EPSG:4326', 'coverage':'${(rasterEntries*.indexId).join(',')}'})"
                                           title="Export PNG">PNG 8-Bit</a></li>
                <li class="yuimenuitem"><a class="yuimenuitemlabel"
                                           href="javascript:getProjectedImage({'format':'geotiff', 'crs':'EPSG:4326', 'coverage':'${(rasterEntries*.indexId).join(',')}'})"
                                           title="Export GeoTIFF">GeoTIFF</a></li>
                <li class="yuimenuitem"><a class="yuimenuitemlabel"
                                           href="javascript:getProjectedImage({'format':'geotiff_uint8', 'crs':'EPSG:4326', 'coverage':'${(rasterEntries*.indexId).join(',')}'})"
                                           title="Export GeoTIFF 8-Bit">GeoTIFF 8-Bit</a></li>
                <li class="yuimenuitem"><a class="yuimenuitemlabel"
                                           href="javascript:getProjectedImage({'format':'geojp2', 'crs':'EPSG:4326', 'coverage':'${(rasterEntries*.indexId).join(',')}'})"
                                           title="Export Geo JPEG 2000">Geo JPEG 2000</a></li>
                <li class="yuimenuitem"><a class="yuimenuitemlabel"
                                           href="javascript:getProjectedImage({'format':'geojp2_uint8', 'crs':'EPSG:4326', 'coverage':'${(rasterEntries*.indexId).join(',')}'})"
                                           title="Export Geo JPEG 2000 8-Bit">Geo JPEG 2000 8-Bit</a></li>
              </ul>
              <ul>
                <li class="yuimenuitem"><a class="yuimenuitemlabel"
                                           href="javascript:getLocalKmz({'format':'image/png', 'transparent':'false','layers':'${(rasterEntries*.indexId).join(',')}'})"
                                           title="Export to a local KMZ with PNG chip">KMZ PNG</a></li>
                <li class="yuimenuitem"><a class="yuimenuitemlabel"
                                           href="javascript:getLocalKmz({'format':'image/png', 'transparent':'true','layers':'${(rasterEntries*.indexId).join(',')}'})"
                                           title="Export to a local KMZ with PNG chip and transparent">KMZ PNG Transparent</a>
                </li>
                <li class="yuimenuitem"><a class="yuimenuitemlabel"
                                           href="javascript:getLocalKmz({'format':'image/jpeg', 'transparent':'false','layers':'${(rasterEntries*.indexId).join(',')}'})"
                                           title="Export to a local KMZ with JPEG chip">KMZ JPEG</a></li>
              </ul>
            </div>
          </div>
        </li>

        <li class="yuimenubaritem first-of-type"><a class="yuimenubaritemlabel" href="#viewMenu">View</a>

          <div id="viewMenu" class="yuimenu">
            <div class="bd">
              <ul>
                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="javascript:changeToImageSpace();"
                                           title="Image Space Viewer (Rotate)">Image Space (Rotate)</a></li>
                <li class="yuimenuitem"><a class="yuimenuitemlabel"
                                           href="${createLink(controller: 'mapView', action: 'multiLayer', params: [layers: (rasterEntries*.indexId).join(',')])}"
                                           title="Multi Layer Ground Space Viewer">Multi Layer Ground Space</a></li>
              </ul>
              <ul>
                <li class="yuimenuitem"><a class="yuimenuitemlabel"
                                           href="${createLink(action: "index", params: [layers: (rasterEntries*.indexId).join(',')])}"
                                           title="Reset the view">Reset</a></li>
              </ul>
            </div>
          </div>
        </li>

        <li class="yuimenubaritem first-of-type"><a class="yuimenubaritemlabel"
                                                    href="javascript:changeToImageSpace();">Image Space (Rotate)</a>
        </li>

      </ul>
    </div>
  </div>
  <label>${(rasterEntries?.filename).join(",")}</label>
  <g:if test='${flash.message}'>
    <label class='message'>${flash.message}</label>
  </g:if>
</content>

<content tag="left">

  <div class="niceBox">
    <div class="niceBoxHd">Map Center:</div>

    <div class="niceBoxBody">
      <ol>
        <li style="color: #00ccff">DD:</li>
        <li><g:textField name="ddMapCtr" id="ddMapCtr" value="" onChange="setMapCtr('dd', this.value)" size="28"
                         title="Enter decimal degree coordinates and click off the text field to re-center the map. Example: 25.77, -80.18"
                         style="background:  black; color: white"/></li>
      </ol>
      <ol>
        <li style="color: #00ccff">DMS:</li>
        <li><g:textField name="dmsMapCtr" id="dmsMapCtr" value="" onChange="setMapCtr('dms', this.value)" size="28"
                         title="Enter degree minute seconds coordinates and click off the text field to re-center the map. Example: 25Â°46'20.66'' N, 80Â°11'23.64'' W"
                         style="background:  black; color: white"/></li>
      </ol>
      <ol>
        <li style="color: #00ccff">MGRS:</li>
        <li><g:textField name="centerMgrs" id="centerMgrs" value="" onChange="setMapCtr('mgrs', this.value)" size="28"
                         title="Enter mgrs coordinate and click off the text field to re-center the map. Example: 17RNJ8123050729 or 17 RNJ 81230 50729"
                         style="background:  black; color: white"/></li>
      </ol>

      <div align="center">
        <button id="applyCenterButton" type="button" onclick="">Apply</button>
        <button id="resetCenterButton" type="button" onclick="javascript:resetMapCenter()"
                title="Resets the view to the center of the image but keeps the current zoom level">Reset</button>
      </div>
    </div>
  </div>


  <input type="hidden" name="request" value=""/>
  <input type="hidden" name="layers" value=""/>
  <input type="hidden" name="bbox" value=""/>
  <input type="hidden" id="contrast" name="contrast" value="${params.contrast ?: ''}"/>
  <input type="hidden" id="brightness" name="brightness" value="${params.brightness ?: ''}"/>

  <div class="niceBox">
    <div class="niceBoxHd">Image Adjustments:</div>

    <div class="niceBoxBody">
      <ol>
        <li style="color: #00ccff">Interpolation:</li>
        <li>
          <g:select id="interpolation" name="interpolation" value="${params.interpolation ?: bilinear}"
                    from="${['bilinear', 'nearest neighbor', 'cubic', 'sinc']}" onChange="chgInterpolation()"/>
        </li>
        <hr/>
        <label style="color: #00ccff">Brightness: <input type="text" readonly="true" id="brightnessTextField" size="3"
                                                         maxlength="5" value=""
                                                         style="background:  black; color: white"></label>

        <li>
          <div id="slider-brightness-bg" class="yui-h-slider" tabindex="-1" hidefocus="false">
            <div id="slider-brightness-thumb" class="yui-slider-thumb"><img
                src="${resource(plugin: 'yui', dir: 'js/yui/slider/assets', file: 'thumb-n.gif')}"></div>
          </div>
        </li>
        <label style="color: #00ccff">Contrast: <input type="text" readonly="true" id="contrastTextField" size="3"
                                                       maxlength="5" value="" style="background:  black; color: white">
        </label>
        <li>
          <div id="slider-contrast-bg" class="yui-h-slider" tabindex="-1" hidefocus="false">
            <div id="slider-contrast-thumb" class="yui-slider-thumb"><img
                src="${resource(plugin: 'yui', dir: 'js/yui/slider/assets', file: 'thumb-n.gif')}"></div>
          </div>
        </li>

        <div align="center">
          <button id="brightnessContrastReset" type="button"
                  onclick="javascript:resetBrightnessContrast()">Reset</button>

        </div>
        <hr/>
        <li style="color: #00ccff">Sharpen:</li>
        <li>
          <g:select id="sharpen_mode" name="sharpen_mode" value="${params.sharpen_mode ?: 'none'}"
                    from="${['none', 'light', 'heavy']}" onChange="mergeNewParams()"/>
        </li>
        <li style="color: #00ccff">Dynamic Range Adjustment:</li>
        <li>
          <g:select id="stretch_mode" name="stretch_mode" value="${params.stretch_mode ?: 'linear_auto_min_max'}"
                    from="${[[name: 'Automatic', value: 'linear_auto_min_max'], [name: '1st Std', value: 'linear_1std_from_mean'], [name: '2nd Std', value: 'linear_2std_from_mean'], [name: '3rd Std', value: 'linear_3std_from_mean'], [name: 'No Adjustment', value: 'none']]}"
                    optionValue="name" optionKey="value"
                    onChange="mergeNewParams()"/>
        </li>
        <li style="color: #00ccff">Region:</li>
        <li>
          <g:select id="stretch_mode_region" name="stretch_mode_region"
                    value="${params.stretch_mode_region ?: 'global'}" from="${['global', 'viewport']}"
                    onChange="mergeNewParams()"/>
        </li>

        <g:if test="${rasterEntries[0]?.numberOfBands == 1}">
          <li style="color: #00ccff">Band:</li>
          <li><g:select id="bands" name="bands" value="${params.bands ?: '0'}" from="${['0']}"
                        onChange="mergeNewParams()"/></li>
        </g:if>
        <g:if test="${rasterEntries[0]?.numberOfBands == 2}">
          <li style="color: #00ccff">Bands:</li>
          <li><g:select id="bands" name="bands" value="${params.bands ?: '0,1'}" from="${['0,1', '1,0', '0', '1']}"
                        onChange="mergeNewParams()"/></li>
        </g:if>
        <g:if test="${rasterEntries[0]?.numberOfBands >= 3}">
          <li style="color: #00ccff">Bands:</li>
          <li><g:select id="bands" name="bands" value="${params.bands ?: '0,1,2'}"
                        from="${['0,1,2', '2,1,0', '0', '1', '2']}" onChange="mergeNewParams()"/></li>
        </g:if>

        <li style="color: #00ccff">Orthorectification:</li>
        <li>
          <g:select id="quicklook" name="quicklook"
                    from="${[[name: 'Rigorous', value: 'false'], [name: 'Simple', value: 'true']]}"
                    optionValue="name" optionKey="value"
                    onChange="mergeNewParams()"/>
        </li>
      </ol>
    </div>
  </div>


  <div class="niceBox">
    <div class="niceBoxHd">Map Measurement Tool:</div>

    <div class="niceBoxBody">
      <ul>
        <li style="color: #00ccff">Measurement Units:</li>
        <li style="color: #00ccff">Not certified for targeting.</li>
        <li><g:select name="measurementUnits" from="${['kilometers', 'meters', 'feet', 'miles', 'yards']}"
                      title="Select a unit of measuremen and use the path and polygon measurment tools in the map toolbar."
                      onChange="measureUnitChanged(this.value)" style="background:  black; color: white"/></li>

        <div id="pathMeasurement" style="color: #00ccff"></div>

        <div id="polygonMeasurement" style="color: #00ccff"></div>
      </ul>
    </div>
  </div>

</content>

<content tag="middle">
</content>


<r:script>
var coordConvert = new CoordinateConversion();
var mapWidget;
var kmlLayers;
var rasterLayers;
var select;
var wcsParams = new OmarWcsParams();

//var fullResScale = parseFloat("${fullResScale}");
var minLon = parseFloat("${left}");
var minLat = parseFloat("${bottom}");
var maxLon = parseFloat("${right}");
var maxLat = parseFloat("${top}");

var largestScale = parseFloat("${largestScale}");
var smallestScale = parseFloat("${smallestScale}");

var brightnessSlider = YAHOO.widget.Slider.getHorizSlider("slider-brightness-bg",  "slider-brightness-thumb", 0, 100, 1);
var contrastSlider= YAHOO.widget.Slider.getHorizSlider("slider-contrast-bg",  "slider-contrast-thumb", 0, 100, 1);
function changeToImageSpace()
{
   var url = "${createLink(controller: 'mapView', action: 'imageSpace')}";
   var wmsFormElement = $("wmsFormId");
   if(wmsFormElement)
   {
     wmsParams = new OmarWmsParams();
     wmsParams.setProperties(wcsParams);
     wmsParams.layers = "${(rasterEntries*.indexId).join(',')}";
    wmsFormElement.action = url + "?"+wmsParams.toUrlParams();
    wmsFormElement.method = "POST";
    wmsFormElement.submit();
   }

}

function resetBrightnessContrast()
{
	brightnessSlider.setRealValue(0);  
	contrastSlider.setRealValue(1.0);
}
function resetMapCenter()
{
	bounds = new OpenLayers.Bounds(minLon, minLat, maxLon, maxLat);
	zoom = mapWidget.getMap().getZoom();
	mapWidget.getMap().setCenter(bounds.getCenterLonLat(), zoom);

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
// we need to pass a json string or object and save to the session
// and reload here
    wcsParams.setProperties({
        brightness:"${params.brightness ?: 0}",
        contrast:"${params.contrast ?: 1.0}",
        sharpen_mode:"${params.sharpen_mode ?: 'none'}",
        stretch_mode:"${params.stretch_mode ?: 'linear_auto_min_max'}",
        stretch_mode_region: "${params.stretch_mode_region ?: 'global'}",
        interpolation: "${params.interpolation ?: 'bilinear'}",
        srs: "${params.srs ?: 'EPSG:4326'}",
        crs: "${params.crs ?: 'EPSG:4326'}",
        bands:$("bands").value,
        quicklook: "${params.quicklook ?: 'false'}"
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


	brightnessSlider.setRealValue(${params.brightness ?: 0});
	contrastSlider.setRealValue(${params.contrast ?: 1});
	var bounds = new OpenLayers.Bounds(minLon, minLat, maxLon, maxLat);
	
	mapWidget = new MapWidget();
	mapWidget.setupMapWidgetWithOptions("map", {controls: [], maxExtent:bounds, maxResolution:largestScale, minResolution:smallestScale});
	mapWidget.setFullResScale(parseFloat("${fullResScale}"));
  mapWidget.changeMapSize();
	
	
	setupLayers();
  mapWidget.setupAoiLayer();
	mapWidget.setupToolBar();
  	
	mapWidget.getMap().addControl(new OpenLayers.Control.LayerSwitcher());
	var overview = new OpenLayers.Control.OverviewMap({maximized: true});
    //mapWidget.getMap().addControl(overview);
	mapWidget.getMap().addControl(new OpenLayers.Control.Scale());
	mapWidget.getMap().addControl(new OpenLayers.Control.ScaleLine());
	
	mapWidget.getMap().events.register('mousemove',map,setMouseMapCtrTxt);
	mapWidget.getMap().events.register("moveend", map, this.setMapCtrTxt);

  var zoom = mapWidget.getMap().getZoomForExtent(bounds, true);
	mapWidget.getMap().setCenter(bounds.getCenterLonLat(), zoom);
}

function setMapCtrTxt()
{
    var center = mapWidget.getMap().getCenter();
    $("ddMapCtr").value = center.lat + ", " + center.lon;
	$("dmsMapCtr").value = coordConvert.ddToDms(center.lat, "lat") + ", " + coordConvert.ddToDms(center.lon, "lon");
	$("centerMgrs").value = coordConvert.ddToMgrs(center.lat, center.lon);
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
		var dmsRegExp = /^(\d{1,2})\Â°?\s?(\d{2})\'?\s?(\d{2}\.?\d+)\"?\s?([NnSs])\,?\s?(\d{1,3})\Â°?\s?(\d{2})\'?\s?(\d{2}\.?\d+)\"?\s?([EeWw])$/
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
		
		
		
		var foo = coordConvert.mgrsToUtm($("centerMgrs").value);
		
		
		
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

	// call this because the center is clamped so we need to reset the center on the
	// display just in case a user typed a number outside the bounds of the image
	setMapCtrTxt();
}

function mergeNewParams()
{
    obj = {
         interpolation:$("interpolation").value,
         sharpen_mode:$("sharpen_mode").value,
         stretch_mode:$("stretch_mode").value,
         stretch_mode_region: $("stretch_mode_region").value,
         quicklook:$("quicklook").value,
         quicklook:$("quicklook").value,
         bands:$("bands").value,
         brightness:brightnessSlider.getRealValue(),
         contrast:contrastSlider.getRealValue()
    };
    wcsParams.setProperties(obj);
	for(var layer in rasterLayers)
	{
		rasterLayers[layer].mergeNewParams(obj);
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

	if ( center )
	{
    var ddMouseCtr = document.getElementById("ddMouseMapCtr");
    ddMouseMapCtr.innerHTML = "DD: " + center.lat.toFixed(fixed) + ", " + center.lon.toFixed(fixed);

    var dmsMouseCtr = document.getElementById("dmsMouseMapCtr");
    dmsMouseMapCtr.innerHTML = "DMS: " + coordConvert.ddToDms(center.lat, "lat") + ", " + coordConvert.ddToDms(center.lon, "lon");

    var mgrsMouseCtr = document.getElementById("mgrsMouseMapCtr");
    mgrsMouseMapCtr.innerHTML = "MGRS: " + coordConvert.ddToMgrs(center.lat, center.lon);
	}
}

function measureUnitChanged(unit)
{
	if(unit == 'kilometers')
	{
		pathMeasurement.innerHTML = mapWidget.getPathUnit()[0];
	}
	else if(unit == 'meters')
	{
		pathMeasurement.innerHTML = mapWidget.getPathUnit()[1];
	}
	else if(unit == 'feet')
	{
		pathMeasurement.innerHTML = mapWidget.getPathUnit()[2];
	}
	else if(unit == 'miles')
	{
		pathMeasurement.innerHTML = mapWidget.getPathUnit()[3];
	}
	else if(unit == 'yards')
	{
		pathMeasurement.innerHTML = mapWidget.getPathUnit()[4];
	}

}

function clearPathMeasurement()
{
	pathMeasurement.innerHTML = "";
}

function clearPolygonMeasurement()
{
	polygonMeasurement.innerHTML = "";
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
	var wmsParamForm = document.getElementById('wmsFormId');
	var wmsUrlParams  = new OmarWmsParams();

	 var link   = "${createLink(action: "wms", controller: "ogc")}";
	 var extent = mapWidget.getSelectedOrViewportExtents();
	 var size   = mapWidget.getSizeInPixelsFromExtents(extent);
	 var wmsProperties = {"request":"GetKML",
	               	  "bbox":extent.toBBOX(),
	               	  "layers":layers,
	               	  "srs":"EPSG:4326",
	               	  "width":size.w,
	               	  "height":size.h}
    wmsUrlParams.setProperties(document);
    wmsUrlParams.setProperties(wmsProperties);

    var url = link + "?" + wmsUrlParams.toUrlParams();
    if(wmsParamForm)
    {
        wmsParamForm.action = url;
//        alert(wmsParamForm.action);
        wmsParamForm.submit();
    }

/*
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
	*/
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
               {layers: "${(rasterEntries*.indexId).join(',')}",
                format: format, sharpen_mode:sharpen_mode,
                stretch_mode:stretch_mode, stretch_mode_region: stretch_mode_region, transparent:transparent,
         brightness:brightnessSlider.getRealValue(),
         contrast:contrastSlider.getRealValue(),
         bands:$("bands").value,
         interpolation:$("interpolation").value
                },
	           {isBaseLayer: true, buffer: 0,
	            singleTile: true, ratio: 1.0,
	            quicklook: true, transitionEffect: "resize", displayOutsideMaxExtent:false})];
	
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


function getKmlSuperOverlay()
{
	var wmsParamForm = document.getElementById('wmsFormId');
	var imageAdjustementParams  = new OmarImageAdjustmentParams();

	var link   = "${createLink(action: "createKml", controller: "superOverlay")}";
    imageAdjustementParams.setProperties(document);

    var url = link + "?" + "id=${(rasterEntries*.indexId).join(',')}" + "&"+imageAdjustementParams.toUrlParams();
    if(wmsParamForm)
    {
        wmsParamForm.action = url;
        wmsParamForm.submit();
    }
}

function getProjectedImage(params)
{
	 var link   = "${createLink(action: "wcs", controller: "ogc")}";
	 var extent = mapWidget.getSelectedExtents();

	 if(extent&&extent.left)
	 {
	    viewportExtents = mapWidget.getViewportExtents();

	    if(!viewportExtents.containsBounds(extent))
	    {
	        alert("Selected extents exceeds the viewport extents.  The AOI will be cleared, please re-select the region to save.");
	        mapWidget.clearAOI();
	        return;
	    }
	 }
	 else
	 {
	    extent = mapWidget.getViewportExtents();
	 }
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

function getLocalKmz(params)
{
	var wmsParamForm = document.getElementById('wmsFormId');
	var wmsUrlParams  = new OmarWmsParams();

	 var link   = "${createLink(action: "wms", controller: "ogc")}";
	 var extent = mapWidget.getSelectedOrViewportExtents();
	 var size   = mapWidget.getSizeInPixelsFromExtents(extent);
	 var wmsProperties = {"request":"GetKMZ",
	               	  "bbox":extent.toBBOX(),
	               	  "srs":"EPSG:4326",
	               	  "width":size.w,
	               	  "height":size.h}
	wmsUrlParams.request = ""
    wmsUrlParams.setProperties(document);
    wmsUrlParams.setProperties(params);
    wmsUrlParams.setProperties(wmsProperties);

    var url = link + "?" + wmsUrlParams.toUrlParams();
    if(wmsParamForm)
    {
        wmsParamForm.action = url;
        //alert(url);
        wmsParamForm.submit();
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
</r:script>

</body>
</html>