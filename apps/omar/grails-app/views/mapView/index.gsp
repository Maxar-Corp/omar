<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: 2/7/12
  Time: 2:52 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>OMAR <g:meta name="app.version"/>: Ground Space - ${(rasterEntries*.id)?.join(', ')}</title>
  <meta content="groundSpacePageLayout" name="layout">
  <r:require modules="groundSpacePageLayout"/>
  <style type="text/css">
  #slider-brightness-bg, #slider-contrast-bg {
    width: 120px;
    background: url(${resource(plugin: 'yui', dir:'js/yui/slider/assets', file:'bg-fader.gif')}) 5px 0 no-repeat;
  }
  </style>
</head>

<body class=" yui-skin-sam">

<content tag="top1">
  <g:render template="groundSpaceMenu" model="${[rasterEntries: rasterEntries]}"/>
</content>

<content tag="bottom1"></content>

<content tag="left1">
  <g:render template="groundSpaceAdjustments" model="${[rasterEntries: rasterEntries, params: params]}"/>
</content>

<%--
<content tag="right1"></content>
--%>

<content tag="top2">
  <div id="toolBar" class="olControlPanel"></div>
</content>

<content tag="bottom2">
  <table><tr>
    <td width="33%"><div id="ddMouseMapCtr">&nbsp;</div></td>
    <td width="33%"><div id="dmsMouseMapCtr">&nbsp;</div></td>
    <td width="33%"><div id="mgrsMouseMapCtr">&nbsp;</div></td>
  </tr></table>
</content>

<content tag="center2">
  <div id="map"></div>
</content>


<r:script>
var coordConvert;
var mapWidget;
var kmlLayers;
var rasterLayers;
var select;
var wcsParams;

//var fullResScale = parseFloat("${fullResScale}");
var minLon;
var minLat;
var maxLon;
var maxLat;

var largestScale;
var smallestScale;

var brightnessSlider;
var contrastSlider;

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

function init(mapWidth, mapHeight)
{
    OpenLayers.ImgPath = "${resource(plugin: 'openlayers', dir: 'js/img')}/";

    coordConvert = new CoordinateConversion();
    wcsParams = new OmarWcsParams();

  //var fullResScale = parseFloat("${fullResScale}");
  minLon = parseFloat("${left}");
  minLat = parseFloat("${bottom}");
  maxLon = parseFloat("${right}");
  maxLat = parseFloat("${top}");

  largestScale = parseFloat("${largestScale}");
  smallestScale = parseFloat("${smallestScale}");

  brightnessSlider = YAHOO.widget.Slider.getHorizSlider("slider-brightness-bg",  "slider-brightness-thumb", 0, 100, 1);
  contrastSlider= YAHOO.widget.Slider.getHorizSlider("slider-contrast-bg",  "slider-contrast-thumb", 0, 100, 1);


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
	mapWidget.setupMapWidgetWithOptions("map", {controls: [], maxExtent:bounds, theme: null, maxResolution:largestScale, minResolution:smallestScale});
	mapWidget.setFullResScale(parseFloat("${fullResScale}"));
  changeMapSize(mapWidth, mapHeight);


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
	else if(unit == 'nautical miles')
	{
		pathMeasurement.innerHTML = mapWidget.getPathUnit()[5];
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

		Dom.setStyle("map", 'width',  mapWidth);
		Dom.setStyle("map", 'height', mapHeight);

    mapWidget.changeMapSize();
	}
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