<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="rasterViewsStatic"/>
        <title>OMAR <g:meta name="app.version"/>: Ground Space Viewer</title>

        <openlayers:loadMapToolBar/>
        <openlayers:loadTheme theme="default"/>

        <meta name="apple-mobile-web-app-capable" content="yes"/>
        <meta name="apple-mobile-web-app-status-bar-style" content="black"/>
        <meta name="viewport" content="minimum-scale=1.0, width=device-width, maximum-scale=1.6, user-scalable=no">
        <openlayers:loadJavascript/>

        <style type="text/css">
        body
        {
            background: #2F2F2F;
        }
        #controls
        {
            margin-left: 0;
            padding-left: 2em;
            width: 12em;
        }
        #controls li
        {
            list-style: none;
            padding-top: 0.5em;
        }
        #compassMap
        {
            height: 75;
            width: 75;
        }
        div.olControlScale
        {
            background-color: #ffffff;
            font-size: 1.0em;
            font-weight: bold;
        }
        #exportMenu, #viewMenu
        {
            z-index: 99999;
        }
        #homeMenu
        {
            background: url(../images/skin/house.png) left no-repeat;
            z-index: 99999;
        }
        .message
        {
            border: 0px solid #b2d1ff;
            color: #006dba;
            margin: 0px 0 0px 0;
            padding: 0px 0px 0px 35px
        }
        #slider-brightness-bg, #slider-contrast-bg
        {
            background: url(${resource(plugin: 'yui', dir:'js/yui/slider/assets', file:'bg-fader.gif')}) 5px 0 no-repeat;
            width: 120px;
        }
    </style>
</head>

<body class="yui-skin-sam" onload="init();">
    <omar:bundle contentType="javascript" files="${[
        [plugin: 'omar-core', dir:'js', file: 'coordinateConversion.js'],
        [plugin: 'omar-core', dir:'js', file: 'mapwidget.js']
    ]}"/>

    <content tag="top">
        <g:form name="wcsForm" method="POST"/>
        <g:form name="wmsFormId" method="POST"/>

    <div id="rasterMenu" class="yuimenubar yuimenubarnav">
        <div class="bd">
            <ul class="first-of-type">
                <li class="yuimenubaritem first-of-type"><a class="yuimenubaritemlabel" id="homeMenu" href="${createLink(controller: 'home', action: 'index')}" title="OMAR™ Home">&nbsp;&nbsp;&nbsp;&nbsp;OMAR™ Home</a></li>
                <li class="yuimenubaritem first-of-type"><a class="yuimenubaritemlabel" href="#exportMenu">Export</a>
                    <div id="exportMenu" class="yuimenu">
                        <div class="bd">
                            <ul>
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="${createLink(controller: "ogc", action: "wms", params: [request: "GetCapabilities", layers: (rasterEntries*.indexId).join(',')])}" title="Show OGC WMS Capabilities">OGC WMS Capabilities</a></li>
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="javascript:getKML('${(rasterEntries*.indexId).join(',')}')" title="Export KML">KML</a></li>
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="javascript:getKmlSuperOverlay()" title="Export Image as Super Overlay">KML Super Overlay</a></li>
                            </ul>
                            <ul>
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="javascript: chipImage('jpeg')" title="Export JPEG">JPEG</a></li>
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="javascript: chipImage('png')" title="Export PNG">PNG</a></li>
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="javascript:getProjectedImage({'format':'png_uint8', 'crs':'EPSG:4326', 'coverage':'${(rasterEntries*.indexId).join(',')}'})" title="Export PNG">PNG 8-Bit</a></li>
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="javascript:getProjectedImage({'format':'geotiff', 'crs':'EPSG:4326', 'coverage':'${(rasterEntries*.indexId).join(',')}'})" title="Export GeoTIFF">GeoTIFF</a></li>
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="javascript:getProjectedImage({'format':'geotiff_uint8', 'crs':'EPSG:4326', 'coverage':'${(rasterEntries*.indexId).join(',')}'})" title="Export GeoTIFF 8-Bit">GeoTIFF 8-Bit</a></li>
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="javascript:getProjectedImage({'format':'geojp2', 'crs':'EPSG:4326', 'coverage':'${(rasterEntries*.indexId).join(',')}'})" title="Export Geo JPEG 2000">Geo JPEG 2000</a></li>
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="javascript:getProjectedImage({'format':'geojp2_uint8', 'crs':'EPSG:4326', 'coverage':'${(rasterEntries*.indexId).join(',')}'})" title="Export Geo JPEG 2000 8-Bit">Geo JPEG 2000 8-Bit</a></li>
                            </ul>
                            <ul>
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="javascript:getLocalKmz({'format':'image/png', 'transparent':'false','layers':'${(rasterEntries*.indexId).join(',')}'})" title="Export to a local KMZ with PNG chip">KMZ PNG</a></li>
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="javascript:getLocalKmz({'format':'image/png', 'transparent':'true','layers':'${(rasterEntries*.indexId).join(',')}'})" title="Export to a local KMZ with PNG chip and transparent">KMZ PNG Transparent</a></li>
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="javascript:getLocalKmz({'format':'image/jpeg', 'transparent':'false','layers':'${(rasterEntries*.indexId).join(',')}'})" title="Export to a local KMZ with JPEG chip">KMZ JPEG</a></li>
                            </ul>
                        </div>
                    </div>
                </li>

                <li class="yuimenubaritem first-of-type"><a class="yuimenubaritemlabel" href="#viewMenu">View</a>
                    <div id="viewMenu" class="yuimenu">
                        <div class="bd">
                            <ul>
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="javascript:changeToImageSpace();" title="Image Space Viewer (Rotate)">Image Space (Rotate)</a></li>
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="${createLink(controller: 'mapView', action: 'multiLayer', params: [layers: (rasterEntries*.indexId).join(',')])}" title="Multi Layer Ground Space Viewer">Multi Layer Ground Space</a></li>
                            </ul>
                            <ul>
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="${createLink(action: "index", params: [layers: (rasterEntries*.indexId).join(',')])}" title="Reset the view">Reset</a></li>
                            </ul>
                        </div>
                    </div>
                </li>
                <li class="yuimenubaritem first-of-type"><a class="yuimenubaritemlabel" href="javascript:changeToImageSpace();">Image Space (Rotate)</a></li>
                <li class="yuimenubaritem first-of-type"><a class="yuimenubaritemlabel" href="javascript:buildOMARLink();">Share Image</a></li>
            </ul>
        </div>
    </div>

    <div style="color: white">${(rasterEntries?.filename).join(",")}</div>
    <g:if test='${flash.message}'><label class='message'>${flash.message}</label></g:if>
</content>

<content tag="left">
    <div class="niceBox" style = "background: black">
        <div class="niceBoxHd" style = "background: #0B0B65">Map Center:</div>
        <div class="niceBoxBody">
            <ol>
                <li style="color: #00CCFF">DD:</li>
                <li><g:textField name="ddMapCtr" id="ddMapCtr" value="" onChange="setMapCtr('dd', this.value)" size="28" title="Enter decimal degree coordinates and click off the text field to re-center the map. Example: 25.77, -80.18" style="background: #2F2F2F; color: #ffffff"/></li>
            </ol>
            <ol>
                <li style="color: #00CCFF">DMS:</li>
                <li><g:textField name="dmsMapCtr" id="dmsMapCtr" value="" onChange="setMapCtr('dms', this.value)" size="28" title="Enter degree minute seconds coordinates and click off the text field to re-center the map. Example: 25°46'20.66'' N, 80°11'23.64'' W" style="background: #2F2F2F; color: #ffffff"/></li>
            </ol>
            <ol>
                <li style="color: #00CCFF">MGRS:</li>
                <li><g:textField name="centerMgrs" id="centerMgrs" value="" onChange="setMapCtr('mgrs', this.value)" size="28" title="Enter mgrs coordinate and click off the text field to re-center the map. Example: 17RNJ8123050729 or 17 RNJ 81230 50729" style="background: #2F2F2F; color: #ffffff"/></li>
            </ol>
            <div align="center">
                <button id="applyCenterButton" type="button" onclick="">Apply</button>
                <button id="resetCenterButton" type="button" onclick="javascript:resetMapCenter()" title="Resets the view to the center of the image but keeps the current zoom level">Reset</button>
            </div>
        </div>
    </div>

    <input type="hidden" name="request" value=""/>
    <input type="hidden" name="layers" value=""/>
    <input type="hidden" name="bbox" value=""/>
    <input type="hidden" id="contrast" name="contrast" value="${params.contrast ?: ''}"/>
    <input type="hidden" id="brightness" name="brightness" value="${params.brightness ?: ''}"/>
              <br>
    <div class="niceBox" style = "background: black">
        <div class="niceBoxHd" style = "background: #0B0B65">Image Adjustments:</div>
        <div class="niceBoxBody">
            <ol>
                <li style="color: #00CCFF">Interpolation:</li>
                <li><g:select id="interpolation" name="interpolation" value="${params.interpolation?:bilinear}" from="${['bilinear', 'nearest neighbor', 'cubic', 'sinc']}" onChange="mergeNewParams()" style="background: #2F2F2F; color: #ffffff"/></li>
                <hr/>

                <label style="color: #00CCFF">Brightness: <input type="text" readonly="true" id="brightnessTextField" size="3" maxlength="5" value="" style="background: #2F2F2F; color: #ffffff"></label>
                <li>
                    <div id="slider-brightness-bg" class="yui-h-slider" tabindex="-1" hidefocus="false">
                        <div id="slider-brightness-thumb" class="yui-slider-thumb"><img src="${resource(plugin: 'yui', dir: 'js/yui/slider/assets', file: 'thumb-n.gif')}"></div>
                    </div>
                </li>

                <label style="color: #00CCFF">Contrast: <input type="text" readonly="true" id="contrastTextField" size="3" maxlength="5" value="" style="background: #2F2F2F; color: #ffffff"></label>
                <li>
                    <div id="slider-contrast-bg" class="yui-h-slider" tabindex="-1" hidefocus="false">
                        <div id="slider-contrast-thumb" class="yui-slider-thumb"><img src="${resource(plugin: 'yui', dir: 'js/yui/slider/assets', file: 'thumb-n.gif')}"></div>
                    </div>
                </li>

                <div align="center"><button id="brightnessContrastReset" type="button" onclick="javascript:resetBrightnessContrast()">Reset</button></div>
                <hr/>

                <li style="color: #00CCFF">Sharpen:</li>
                <li><g:select id="sharpen_mode" name="sharpen_mode" value="${params.sharpen_mode?:'none'}" from="${['none', 'light', 'heavy']}" onChange="mergeNewParams()" style="background: #2F2F2F; color: #FFFFFF"/></li>

                <li style="color: #00CCFF">Dynamic Range Adjustment:</li>
                <li>
                    <g:select
                            id="stretch_mode"
                            name="stretch_mode"
                            value="${params.stretch_mode?:'linear_auto_min_max'}"
                            from="${[[name: 'Automatic', value: 'linear_auto_min_max'],[name: '1st Std', value: 'linear_1std_from_mean'],[name: '2nd Std', value: 'linear_2std_from_mean'],[name: '3rd Std', value: 'linear_3std_from_mean'],[name: 'No Adjustment', value: 'none']]}"
                            optionValue="name"
                            optionKey="value"
                            onChange="mergeNewParams()"
                            style="background: #2F2F2F; color: #ffffff"
                    />
                </li>

                <li style="color: #00CCFF">Region:</li>
                <li><g:select id="stretch_mode_region" name="stretch_mode_region" value="${params.stretch_mode_region?:'viewport'}" from="${['global', 'viewport']}" onChange="mergeNewParams()" style="background: #2F2F2F; color: #ffffff"/></li>

                <g:if test="${rasterEntries[0]?.numberOfBands == 1}">
                    <li style="color: #00CCFF">Band:</li>
                    <li><g:select id="bands" name="bands" value="${params.bands?:'0'}" from="${['0']}" onChange="mergeNewParams()" style="background: #2F2F2F; color: #ffffff"/></li>
                </g:if>
                <g:if test="${rasterEntries[0]?.numberOfBands == 2}">
                    <li style="color: #00CCFF">Bands:</li>
                    <li><g:select id="bands" name="bands" value="${params.bands?:'0,1'}" from="${['0,1','1,0','0','1']}" onChange="mergeNewParams()" style="background: #2F2F2F; color: #ffffff"/></li>
                </g:if>
                <g:if test="${rasterEntries[0]?.numberOfBands >= 3}">
                    <li style="color: #00CCFF">Bands:</li>
                    <li><g:select id="bands" name="bands" value="${params.bands?:'0,1,2'}" from="${['0,1,2','2,1,0','0','1','2']}" onChange="mergeNewParams()" style="background: #2F2F2F; color: #ffffff"/></li>
                </g:if>

                <li style="color: #00CCFF">Orthorectification:</li>
                <li><g:select id="quicklook" name="quicklook" from="${[[name: 'Rigorous', value: 'false'],[name: 'Simple', value: 'true']]}" optionValue="name" optionKey="value" onChange="mergeNewParams()" style="background: #2F2F2F; color: #ffffff"/></li>
                <hr/>

                <label style="color: #00CCFF">Rotation: <input type="text" id="rotationTextField" size="3" maxlength="5" value="" onChange="rotationSlider.setRealValue(this.value)" style="background: #2F2F2F; color: #ffffff">deg</label>
                <li>
                    <div id="slider-rotation-bg" class="yui-h-slider" tabindex="-1" hidefocus="false">
                        <div id="slider-rotation-thumb" class="yui-slider-thumb"><img src="${resource(plugin: 'yui', dir: 'js/yui/slider/assets', file: 'thumb-n.gif')}"></div>
                    </div>
                </li>
            </ol>
        </div>

        <div id = "shareLink" style = "display: none"></div>
    </div>

    <br>
    <div class="niceBox" style = "background: black">
        <div class="niceBoxHd" style = "background: #0B0B65">Map Measurement Tool:</div>
        <div class="niceBoxBody" style = "color: #00CCFF">
            <ul>
                <li>Measurement Units:</li>
                <li>Not certified for targeting.</li>
                <li><g:select name="measurementUnits" from="${['kilometers', 'meters', 'feet', 'miles', 'yards']}" title="Select a unit of measuremen and use the path and polygon measurment tools in the map toolbar." onChange="measureUnitChanged(this.value)"/></li>
                <div id="pathMeasurement"></div>
                <div id="polygonMeasurement"></div>
            </ul>
        </div>
    </div>
</content>

<content tag="middle"></content>
<script src="http://yui.yahooapis.com/3.4.1/build/yui/yui-min.js"></script>
<g:javascript contextPath="" library="prototype"/>
<g:javascript>
    var aoiLayer;
    var brightnessSlider = YAHOO.widget.Slider.getHorizSlider("slider-brightness-bg",  "slider-brightness-thumb", 0, 100, 1);
    var compassImage;
    var compassImageURL = "${resource(plugin: 'omar', dir:'images', file:'north_arrow.png')}";
    var compassMap;
    var compassVectorLayer;
    var contrastSlider = YAHOO.widget.Slider.getHorizSlider("slider-contrast-bg",  "slider-contrast-thumb", 0, 100, 1);
    var coordConvert = new CoordinateConversion();
    var currentMapCenterLatitude;
    var currentMapCenterLongitude;
    var dial;
    var image;
    var imageBounds;
    var imageHypotenuse;
    var imageVectorLayer;
    var imageURL;
    var kmlLayers;
    var largestScale = parseFloat("${largestScale}");
    var map;
    var maxLat = parseFloat("${top}");
    var maxLon = parseFloat("${right}");
    var minLat = parseFloat("${bottom}");
    var minLon = parseFloat("${left}");
    var newImageCenterLatitude;
    var newImageCenterLongitude;
    var oldImageCenterLatitude;
    var oldImageCenterLongitude;
    var oldMapCenterLatitude;
    var oldMapCenterLongitude;
    var pathUnit = new Array();
    var rasterLayers;
    var rotationAngle = ${params.rotation ?: 0};
    var rotationSlider = YAHOO.widget.Slider.getHorizSlider("slider-rotation-bg",  "slider-rotation-thumb", 0, 180, 1);
    var select;
    var smallestScale = parseFloat("${smallestScale}");
    var wcsParams = new OmarWcsParams();
    var zoomInButton;
    var zoomInFullResButton;
    var zoomFullResScale;

    function init()
    {
        var oMenu = new YAHOO.widget.MenuBar("rasterMenu",
        {
            autosubmenudisplay: true,
            hidedelay: 750,
            lazyload: true,
            showdelay: 0,
            zIndex:9999
        });
	    oMenu.render();

        wcsParams.setProperties(
        {
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

        if (${rasterEntries?.numberOfBands.get(0) >= 3}) { wcsParams.setProperties({bands:"0,1,2"}); }
        else { wcsParams.setProperties({bands:"0"}); }

	    brightnessSlider.animate = false;
        brightnessSlider.getRealValue = function() { return (this.getValue()-50)/50.0; }
        brightnessSlider.setRealValue = function(value) { this.setValue((value+1)*50); }
        brightnessSlider.subscribe("change", function(offsetFromStart)
        {
		    wcsParams.setProperties({brightness:this.getRealValue()});
    	    $("brightness").value = this.getRealValue();
    	    $("brightnessTextField").value = this.getRealValue();
        });
        brightnessSlider.subscribe("slideEnd", function()
        {
		    for (var layer in rasterLayers)
		    {
		        wcsParams.setProperties({brightness:this.getRealValue()});
			    rasterLayers[layer].mergeNewParams({brightness:this.getRealValue()});
		    }
        });
        brightnessSlider.setRealValue(${params.brightness ?: 0});

        contrastSlider.getRealValue = function() { return (this.getValue()/100.0)*2.0; }
        contrastSlider.setRealValue = function(value) { this.setValue(value*50); }
        contrastSlider.subscribe("change", function(offsetFromStart)
        {
    	    $("contrast").value = this.getRealValue();
    	    $("contrastTextField").value = this.getRealValue();
        });
        contrastSlider.subscribe("slideEnd", function()
        {
		    for (var layer in rasterLayers)
		    {
		        wcsParams.setProperties({contrast:this.getRealValue()});
			    rasterLayers[layer].mergeNewParams({contrast:this.getRealValue()});
		    }
        });
        contrastSlider.setRealValue(${params.contrast ?: 1});

        rotationSlider.getRealValue = function() { return (this.getValue()*2); }
        rotationSlider.setRealValue = function(value) { this.setValue(value); }
        rotationSlider.subscribe("change", function()
        {
            $("rotationTextField").value = this.getRealValue();
            sliderRotate(this.getRealValue());
        });
        rotationSlider.setRealValue(rotationAngle);

        map = new OpenLayers.Map("map",
        {
            controls: [],
            maxExtent: new OpenLayers.Bounds(minLon, minLat, maxLon, maxLat),
            maxResolution: largestScale,
            minResolution: smallestScale
        });
        map.addControl(new OpenLayers.Control.LayerSwitcher());
        map.addControl(new OpenLayers.Control.Scale());
        map.addControl(new OpenLayers.Control.ScaleLine());
        map.events.register("mousemove", map, setMouseMapCtrTxt);


	    zoomFullResScale = "${fullResScale}";

    	setupLayers();
        setupAoiLayer();
	    setupToolbar();

        var bounds =  new OpenLayers.Bounds(minLon, minLat, maxLon, maxLat);

        var bbox = "${params.bbox ?: bounds}";
        if (bbox.split(",").length == 4)
        {
            bounds = new OpenLayers.Bounds(
                bbox.split(",")[0],
                bbox.split(",")[1],
                bbox.split(",")[2],
                bbox.split(",")[3]
                );
        }

        var centerStart = bounds.getCenterLonLat();
        var center = "${params.center ?: 0}";
        if (center.split(",").length == 2)
        {
            centerStart = new OpenLayers.LonLat(center.split(",")[1], center.split(",")[0]);
        }
        map.setCenter(centerStart, map.getZoomForExtent(bounds, true));

        setupCompassMap();

        oldMapCenterLatitude = map.getCenter().lat;
	    oldMapCenterLongitude = map.getCenter().lon;
	    oldImageCenterLatitude = map.getCenter().lat;
	    oldImageCenterLongitude = map.getCenter().lon;
	    newImageCenterLatitude = map.getCenter().lat;
	    newImageCenterLongitude = map.getCenter().lon;
	    map.events.register("moveend", map, updateImage);
    }

    function setMouseMapCtrTxt(evt)
    {
  	    var actualMouseLocation = map.getLonLatFromViewPortPx(new OpenLayers.Pixel(evt.xy.x , evt.xy.y));
        var deltaLongitude = actualMouseLocation.lon - newImageCenterLongitude;
        var deltaLatitude = actualMouseLocation.lat - newImageCenterLatitude;
        var deltaMagnitude = Math.sqrt(Math.pow(deltaLatitude, 2) + Math.pow(deltaLongitude, 2));
        var alpha = 0;

        if (deltaLongitude >= 0 && deltaLatitude >= 0) { alpha = Math.atan(deltaLatitude / deltaLongitude) * 180/Math.PI; }
        else if (deltaLongitude < 0 && deltaLatitude >= 0) { alpha = Math.atan(deltaLatitude / deltaLongitude) * 180/Math.PI + 180; }
        else if (deltaLongitude < 0 && deltaLatitude < 0) { alpha = Math.atan(deltaLatitude / deltaLongitude) * 180/Math.PI + 180; }
        else if (deltaLongitude > 0 && deltaLatitude < 0) { alpha = Math.atan(deltaLatitude / deltaLongitude) * 180/Math.PI + 360; }

        alpha -= parseInt(rotationAngle);
        var relativeMouseLocationLongitude = newImageCenterLongitude + deltaMagnitude * Math.cos(alpha * Math.PI/180);
        var relativeMouseLocationLatitude = newImageCenterLatitude + deltaMagnitude * Math.sin(alpha * Math.PI/180);

        var fixed = 6;
        var ddMouseCtr = document.getElementById("ddMouseMapCtr");
        ddMouseMapCtr.innerHTML = "DD: " + relativeMouseLocationLatitude.toFixed(fixed) + ", " + relativeMouseLocationLongitude.toFixed(fixed);

        var dmsMouseCtr = document.getElementById("dmsMouseMapCtr");
        dmsMouseMapCtr.innerHTML = "DMS: " + coordConvert.ddToDms(relativeMouseLocationLatitude, "lat") + ", " + coordConvert.ddToDms(relativeMouseLocationLongitude, "lon");

        var mgrsMouseCtr = document.getElementById("mgrsMouseMapCtr");
        mgrsMouseMapCtr.innerHTML = "MGRS: " + coordConvert.ddToMgrs(relativeMouseLocationLatitude, relativeMouseLocationLongitude);
    }

    function setMapCtrTxt()
    {
        $("ddMapCtr").value = newImageCenterLatitude + ", " + newImageCenterLongitude;
	    $("dmsMapCtr").value = coordConvert.ddToDms(newImageCenterLatitude, "lat") + ", " + coordConvert.ddToDms(newImageCenterLongitude, "lon");
	    $("centerMgrs").value = coordConvert.ddToMgrs(newImageCenterLatitude, newImageCenterLongitude);
    }

    function setupLayers()
    {
        var baseLayer = new OpenLayers.Layer("Empty", {isBaseLayer: true});
        imageVectorLayer = new OpenLayers.Layer.Vector("Simple Geometry",
	    {
		    styleMap: new OpenLayers.StyleMap
		    ({
			    "default":
			    {
				    externalGraphic : <%=' "${urlPath}" '%>,
				    graphicWidth : <%=' "${imageWidth}" '%>,
      			    graphicHeight : <%=' "${imageHeight}" '%>,
      			    rotation: <%=' "${angle}" '%>
			    }
		    })
        });
        map.addLayers([baseLayer, imageVectorLayer]);

        //<g:each in="${kmlOverlays}" var="kmlOverlay" status="i">
            //if(!kmlLayers) { kmlLayers = new Array(); }
            //kmlLayer = new OpenLayers.Layer.Vector("${kmlOverlay.name}",
            //{
		        //visibility: ${grailsApplication.config.views.mapView.defaultOverlayVisiblity},
		        //projection: mapWidget.getMap().displayProjection,
		        //strategies: [new OpenLayers.Strategy.Fixed()],
		        //protocol: new OpenLayers.Protocol.HTTP(
		        //{
			        //url: "${kmlOverlay.url}",
			        //format: new OpenLayers.Format.KML(
			        //{
				        //extractStyles: true,
				        //extractAttributes: true
			        //})
			    //})
			//});
			//kmlLayers[${i}] = kmlLayer;
			//kmlLayer.events.on(
			//{
			    //"featureselected": onFeatureSelect,
				//"featureunselected": onFeatureUnselect
			//});
		    //mapWidget.getMap().addLayers(kmlLayers);
		    //select = new OpenLayers.Control.SelectFeature(kmlLayers);
  		    //mapWidget.getMap().addControl(select);
  		    //select.activate();
        //</g:each>
    }

    function setupAoiLayer()
    {
        aoiLayer = new OpenLayers.Layer.Vector("Bound Box AOI");
        aoiLayer.events.register("featureadded", aoiLayer, setAoi);

        var boundBox = new OpenLayers.Control.DrawFeature( aoiLayer, OpenLayers.Handler.RegularPolygon,
        {
            handlerOptions:
            {
                sides: 4,
                irregular: true
            }
        });

        map.addLayer(aoiLayer);
        map.addControl(boundBox);

        var aoiMinLon = "${queryParams?.aoiMinLon ?: ''}";
        var aoiMinLat = "${queryParams?.aoiMinLat ?: ''}";
        var aoiMaxLon = "${queryParams?.aoiMaxLon ?: ''}";
        var aoiMaxLat = "${queryParams?.aoiMaxLat ?: ''}";

        if (aoiMinLon && aoiMinLat && aoiMaxLon && aoiMaxLat)
        {
            var bounds = new OpenLayers.Bounds(aoiMinLon, aoiMinLat, aoiMaxLon, aoiMaxLat);
            var feature = new OpenLayers.Feature.Vector(bounds.toGeometry());

            aoiLayer.addFeatures( feature, {silent: true} );
        }
    }

    function setAoi(evt)
    {
        var geom = evt.feature.geometry;
        var bounds = geom.getBounds();
        var feature = new OpenLayers.Feature.Vector(geom);

        if ($("aoiMinLon")) { $("aoiMinLon").value = bounds.left; }
        if ($("aoiMaxLat")) { $("aoiMaxLat").value = bounds.top; }
        if ($("aoiMaxLon")) { $("aoiMaxLon").value = bounds.right; }
        if ($("aoiMinLat")) { $( "aoiMinLat").value = bounds.bottom; }

        if ($("aoiMinLonDms")) { $("aoiMinLonDms").value = convert.ddToDms(bounds.left, "lon"); }
        if ($("aoiMaxLatDms")) { $("aoiMaxLatDms").value = convert.ddToDms(bounds.top, "lat" ); }
        if ($("aoiMaxLonDms")) { $("aoiMaxLonDms").value = convert.ddToDms(bounds.right, "lon"); }
        if ($("aoiMinLatDms")) { $("aoiMinLatDms").value = convert.ddToDms(bounds.bottom, "lat"); }

        if ($("aoiNeMgrs")) { $("aoiNeMgrs").value = convert.ddToMgrs(bounds.top, bounds.right); }
        if ($("aoiSwMgrs")) { $("aoiSwMgrs").value = convert.ddToMgrs(bounds.bottom, bounds.left); }

        aoiLayer.destroyFeatures();
        aoiLayer.addFeatures(feature, {silent: true});
    }

    function setupToolbar()
    {
        var panButton = new OpenLayers.Control.MouseDefaults({title: "Click pan button to activate. Once activated click the map and drag the mouse to pan."});
        zoomInButton = new OpenLayers.Control.Button({title: "Click to zoom in.", displayClass: "olControlZoomIn", trigger: zoomIn});
        zoomOutButton = new OpenLayers.Control.Button({title: "Click to zoom out.", displayClass: "olControlZoomOut", trigger: zoomOut});
        var zoomMaxExtentButton = new OpenLayers.Control.ZoomToMaxExtent({title:"Click to zoom to the max extent."});
        var zoomBoxButton = new OpenLayers.Control.ZoomBox({title: "Click the zoom box button to activate. Once activated click and drag over an area of interest on the map to zoom into."});
        zoomInFullResButton = new OpenLayers.Control.Button({title: "Click to zoom into full resolution.", displayClass: "olControlZoomToLayer", trigger: zoomInFullRes});

        var boundBoxButton;
        var clearAoiButton;
        if (aoiLayer)
        {
            boundBoxButton = new OpenLayers.Control.DrawFeature(aoiLayer, OpenLayers.Handler.RegularPolygon, {handlerOptions: {sides: 4, irregular: true}, title: "Click and drag to specify an area of interest."});
            clearAoiButton = new OpenLayers.Control.Button({title: "Click to clear area of interest", displayClass: "olControlClearAreaOfInterest", trigger: clearAOI});
        }
        var pathMeasurement = document.getElementById("pathMeasurement");
        var pathMeasurementButton;

        getPathUnit = function() { return pathUnit; }

        if ($("measurementUnits") && pathMeasurement)
        {
            pathMeasurementButton = new OpenLayers.Control.Measure(OpenLayers.Handler.Path,
            {
                title: "Click path measurement button to activate. Once activated click points on the map to create a path that you wish to measure. When you are done creating your path double click to end.",
                displayClass: "olControlMeasureDistance",
                geodesic:true,
                persist: true,
                eventListeners:
                {
                    measure: function(evt)
                    {
                        if (evt.units == "km")
                        {
                            pathUnit[0] = evt.measure + "km";
                            pathUnit[1] = evt.measure * 1000 + "m";
                            pathUnit[2] = evt.measure * 3280.839895 + "ft";
                            pathUnit[3] = evt.measure * 0.62137119224 + "mi";
                            pathUnit[4] = evt.measure * 1093.6132983 + "yd";

                            if ($("measurementUnits").value == "kilometers") { pathMeasurement.innerHTML = pathUnit[0]; }
                            else if ($("measurementUnits").value == "meters") { pathMeasurement.innerHTML = pathUnit[1]; }
                            else if ($("measurementUnits").value == "feet") { pathMeasurement.innerHTML = pathUnit[2]; }
                            else if ($("measurementUnits").value == "miles") { pathMeasurement.innerHTML = pathUnit[3]; }
                            else if ($("measurementUnits").value == "yards") { pathMeasurement.innerHTML = pathUnit[4]; }
                        }
                        else if (evt.units == "m")
                        {
                            pathUnit[0] = evt.measure * 0.001 + "km";
                            pathUnit[1] = evt.measure + "m";
                            pathUnit[2] = evt.measure * 3.280839895 + "ft";
                            pathUnit[3] = evt.measure * 0.00062137119224 + "mi";
                            pathUnit[4] = evt.measure * 1.0936132983 + "yd";

                            if ($("measurementUnits").value == "kilometers") { pathMeasurement.innerHTML = pathUnit[0]; }
                            else if ($("measurementUnits").value == "meters") { pathMeasurement.innerHTML = pathUnit[1]; }
                            else if ($("measurementUnits").value == "feet") { pathMeasurement.innerHTML = pathUnit[2]; }
                            else if ($("measurementUnits").value == "miles") { pathMeasurement.innerHTML = pathUnit[3]; }
                            else if ($("measurementUnits").value == "yards") { pathMeasurement.innerHTML = pathUnit[4]; }
                        }
                    }
                }
            });
        }

        var polygonMeasurement = document.getElementById("polygonMeasurement");
        var polygonMeasurementButton;

        if (polygonMeasurement && $("measurementUnits"))
        {
            polygonMeasurementButton = new OpenLayers.Control.Measure( OpenLayers.Handler.Polygon,
            {
                title: "Click polygon measurement button to activate. Once activated click points on the map to create a polygon that you wish to measure. When you are done creating your polygon double click to end.",
                displayClass: "olControlMeasureArea",
                geodesic:true, displaySystem: "metric",
                persist: true,
                eventListeners:
                {
                    measure: function(evt)
                    {
                        if (evt.units == "km")
                        {
                            pathUnit[0] = evt.measure + "km^2";
                            pathUnit[1] = evt.measure * 1000000 + "m^2";
                            pathUnit[2] = evt.measure * 10763910.416623611025 + "ft^2";
                            pathUnit[3] = evt.measure * .38610215854575903621 + "mi^2";
                            pathUnit[4] = evt.measure * 1195990.04621860478289 + "yd^2";

                            if ($("measurementUnits").value == "kilometers") { pathMeasurement.innerHTML = pathUnit[0]; }
                            else if ($("measurementUnits").value == "meters") { pathMeasurement.innerHTML = pathUnit[1];}
                            else if ($("measurementUnits").value == "feet") { pathMeasurement.innerHTML = pathUnit[2]; }
                            else if ($("measurementUnits").value == "miles") { pathMeasurement.innerHTML = pathUnit[3]; }
                            else if ($("measurementUnits").value == "yards") { pathMeasurement.innerHTML = pathUnit[4]; }
                        }
                        else if (evt.units == "m")
                        {
                            pathUnit[0] = evt.measure * 0.000001 + "km^2";
                            pathUnit[1] = evt.measure + "m^2";
                            pathUnit[2] = evt.measure * 10.763910416623611025 + "ft^2";
                            pathUnit[3] = evt.measure * .00000038610215854575 + "mi^2";
                            pathUnit[4] = evt.measure * 1.19599004621860478289 + "yd^2";

                            if ($("measurementUnits" ).value == "kilometers") { pathMeasurement.innerHTML = pathUnit[0]; }
                            else if ($("measurementUnits").value == "meters") { pathMeasurement.innerHTML = pathUnit[1]; }
                            else if ($("measurementUnits").value == "feet") { pathMeasurement.innerHTML = pathUnit[2]; }
                            else if ($("measurementUnits").value == "miles") { pathMeasurement.innerHTML = pathUnit[3]; }
                            else if ($("measurementUnits").value == "yards") { pathMeasurement.innerHTML = pathUnit[4]; }
                        }
                    }
                }
            });
        }

        var container = $("toolBar");
        var panel = new OpenLayers.Control.Panel(
        {
            div: container,
            defaultControl: panButton,
            displayClass: "olControlPanel"
        });

        panel.addControls([panButton, zoomInButton, zoomOutButton, zoomMaxExtentButton, zoomBoxButton, zoomInFullResButton]);
        if (boundBoxButton && clearAoiButton) { panel.addControls([boundBoxButton, clearAoiButton]); }
        if (pathMeasurementButton && polygonMeasurementButton) { panel.addControls([pathMeasurementButton, polygonMeasurementButton]); }
        map.addControl(panel);
    }

    function zoomIn()
    {
        map.zoomIn();
        var fullRes = map.getZoomForResolution(parseFloat(zoomFullResScale, true));
        if (map.getZoom() >= fullRes) { zoomInButton.displayClass = "olControlFoo"; }
    }

    function zoomOut()
    {
        map.zoomOut();
        var fullRes = map.getZoomForResolution( parseFloat(zoomFullResScale, true));
        if (map.getZoom() < fullRes) { zoomInButton.displayClass = "olControlZoomIn"; }
    }

    function zoomInFullRes()
    {
        var zoom = map.getZoomForResolution(zoomFullResScale, true);
        map.zoomTo( zoom );
        if (zoomInButton) { zoomInButton.displayClass = "olControlFoo"; }
    }

    function clearAOI(evt)
    {
        aoiLayer.destroyFeatures();
        if ($("aoiMinLon")) { $("aoiMinLon").value = ""; }
        if ($("aoiMaxLat")) { $("aoiMaxLat").value = ""; }
        if ($("aoiMaxLon")) { $("aoiMaxLon").value = ""; }
        if ($("aoiMinLat")) { $("aoiMinLat").value = ""; }

        if ($("aoiMinLonDms")) { $("aoiMinLonDms").value = ""; }
        if ($("aoiMaxLatDms")) { $("aoiMaxLatDms").value = ""; }
        if ($("aoiMaxLonDms")) { $("aoiMaxLonDms").value = ""; }
        if ($("aoiMinLatDms")) { $("aoiMinLatDms").value = ""; }

        if ($("aoiNeMgrs")) { $("aoiNeMgrs").value = ""; }
        if ($("aoiSwMgrs")) { $("aoiSwMgrs").value = ""; }
    }

    function updateImage()
    {
        var deltaMovementLatitude = map.getCenter().lat - oldMapCenterLatitude;
        var deltaMovementLongitude = map.getCenter().lon - oldMapCenterLongitude;

	    var deltaMovementMagnitude = Math.sqrt(Math.pow(deltaMovementLongitude,2) + Math.pow(deltaMovementLatitude,2));
        var beta = 0;

	    if (deltaMovementLatitude >= 0 && deltaMovementLongitude > 0)
	    {
            beta = Math.atan(deltaMovementLatitude / deltaMovementLongitude) * 180/Math.PI;
	    }
	    else if (deltaMovementLatitude >= 0 && deltaMovementLongitude < 0)
	    {
            beta = Math.atan(deltaMovementLatitude / deltaMovementLongitude) * 180/Math.PI + 180;
	    }
	    else if (deltaMovementLatitude < 0 && deltaMovementLongitude < 0)
	    {
		    beta = Math.atan(deltaMovementLatitude / deltaMovementLongitude) * 180/Math.PI + 180;
	    }
	    else if (deltaMovementLatitude < 0 && deltaMovementLongitude > 0)
	    {
		    beta = Math.atan(deltaMovementLatitude / deltaMovementLongitude) * 180/Math.PI + 360;
	    }

	    var theta = beta - rotationAngle;
	    var relativeLatitudeDelta = Math.sin(theta * Math.PI/180) * deltaMovementMagnitude;
	    var relativeLongitudeDelta = Math.cos(theta * Math.PI/180) * deltaMovementMagnitude;

	    newImageCenterLatitude = oldImageCenterLatitude + relativeLatitudeDelta;
	    newImageCenterLongitude = oldImageCenterLongitude + relativeLongitudeDelta;

        var mapWidthHalf = (map.calculateBounds().right - map.calculateBounds().left) / 2;
        var mapHeightHalf = (map.calculateBounds().top - map.calculateBounds().bottom) / 2;
        var mapHypotenuse = Math.sqrt(Math.pow(mapWidthHalf,2) + Math.pow(mapHeightHalf,2));
        imageBounds = new OpenLayers.Bounds(
            newImageCenterLongitude - mapHypotenuse,
            newImageCenterLatitude - mapHypotenuse,
            newImageCenterLongitude + mapHypotenuse,
            newImageCenterLatitude + mapHypotenuse
        );

        imageHypotenuse = Math.round(Math.sqrt(Math.pow(map.getCurrentSize().w, 2) + Math.pow(map.getCurrentSize().h, 2)));
        imageURL = getImageURL();

	    currentMapCenterLatitude = map.getCenter().lat;
	    currentMapCenterLongitude = map.getCenter().lon;

        image = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Point(currentMapCenterLongitude, currentMapCenterLatitude), {urlPath: imageURL, imageWidth: imageHypotenuse, imageHeight: imageHypotenuse, angle: -rotationAngle}); ////////////////////
	    imageVectorLayer.addFeatures([image]);

	    oldMapCenterLatitude = map.getCenter().lat;
	    oldMapCenterLongitude = map.getCenter().lon;
	    oldImageCenterLatitude = newImageCenterLatitude;
	    oldImageCenterLongitude = newImageCenterLongitude;

	    setMapCtrTxt();
    }

    function getImageURL()
    {
        var url = "";
        url += "${createLink(controller: "ogc", action: "wms")}" + "?";
        url += "request=GetMap&";
        url += "layers=" + "${(rasterEntries*.indexId).join(',')}" + "&";
        url += "bbox=" + imageBounds.toArray() + "&";
        url += "interpolation=" + ${"interpolation"}.value + "&";

        if (${"brightnessTextField"}.value != "") { url += "brightness=" + brightnessSlider.getRealValue() + "&"; }
        else { url += "brightness=0&"; }

        if (${"contrastTextField"}.value != "") { url += "contrast=" + contrastSlider.getRealValue() + "&"; }
        else { url += "contrast=1&"; }

        url += "sharpen_mode=" + $("sharpen_mode").value + "&";
        url += "stretch_mode=" + $("stretch_mode").value + "&";
        url += "stretch_mode_region=" + $("stretch_mode_region").value +"&";
        url += "bands=" + $("bands").value + "&";
        url += "transparent=false&";
        url += "srs=epsg:4326&";
        url += "width=" + imageHypotenuse + "&";
        url += "height=" + imageHypotenuse + "&";
        url += "format=image/jpeg";

        return url;
    }

    function setupCompassMap()
    {
        var baseLayer = new OpenLayers.Layer("Empty", {isBaseLayer: true});

        compassMap = new OpenLayers.Map("compassMap", {controls: new OpenLayers.Control.Navigation({autoActivate: false})});
        compassMap.addLayer(baseLayer);
        compassMap.setCenter(new OpenLayers.LonLat(0,0),0);

        compassVectorLayer = new OpenLayers.Layer.Vector("Compass Layer",
	    {
		    styleMap: new OpenLayers.StyleMap
		    ({
			    "default":
			    {
				    externalGraphic : compassImageURL,
				    graphicWidth : 40,
                    graphicHeight : 40,
                    rotation: <%=' "${angle}" '%>
                }
		    })
        });
        compassMap.addLayer(compassVectorLayer);

        compassImage = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Point(0, 0), {angle: -rotationAngle});
	    compassVectorLayer.addFeatures([compassImage]);
    }

    function getKML(layers)
    {
	    var wmsParamForm = document.getElementById("wmsFormId");
	    var wmsUrlParams  = new OmarWmsParams();

	    var link   = "${createLink(action: "wms", controller: "ogc")}";
	    var extent = getSelectedOrViewportExtents();
	    var size   = getSizeInPixelsFromExtents(extent);
	    var wmsProperties =
	    {
	        "request":"GetKML",
	        "bbox":extent.toBBOX(),
	        "layers":layers,
	        "srs":"EPSG:4326",
	        "width":size.w,
	        "height":size.h
	    }
        wmsUrlParams.setProperties(document);
        wmsUrlParams.setProperties(wmsProperties);

        var url = link + "?" + wmsUrlParams.toUrlParams();
        if(wmsParamForm)
        {
            wmsParamForm.action = url;
            wmsParamForm.submit();
        }
    }

    function getSelectedOrViewportExtents()
    {
        extent = getSelectedExtents();
        if (!extent || !extent.left) { extent = getViewportExtents(); }
        return extent;
    }

    function getSelectedExtents()
    {
        extent = null;
        if (aoiLayer) { extent = aoiLayer.getDataExtent(); }
        return extent;
    }

    function getViewportExtents()
    {
        extent = null;
        if (map) { extent = map.getExtent(); }
        return extent;
    }

    function getSizeInPixelsFromExtents(extents)
    {
        if (!extents) { return null; }
        cornerPt1 = map.getViewPortPxFromLonLat(new OpenLayers.LonLat(extents.left, extents.top));
        cornerPt2 = map.getViewPortPxFromLonLat(new OpenLayers.LonLat(extents.right, extents.bottom));

        return new OpenLayers.Size(Math.round(Math.abs(cornerPt2.x - cornerPt1.x) + 1), Math.round(Math.abs(cornerPt2.y - cornerPt1.y) + 1));
    }

    function getKmlSuperOverlay()
    {
	    var wmsParamForm = document.getElementById('wmsFormId');
	    var imageAdjustementParams  = new OmarImageAdjustmentParams();

	    var link   = "${createLink(action: "createKml", controller: "superOverlay")}";
        imageAdjustementParams.setProperties(document);

        var url = link + "?" + "id=${(rasterEntries*.indexId).join(',')}" + "&" + imageAdjustementParams.toUrlParams();
        if(wmsParamForm)
        {
            wmsParamForm.action = url;
            wmsParamForm.submit();
        }
    }

    function chipImage(format)
    {
        var layers = "${(rasterEntries*.indexId).join(',')}";
        var bbox = imageBounds.toArray();
        var interpolation = $("interpolation").value;
        var brightness = brightnessSlider.getRealValue();
        var contrast = contrastSlider.getRealValue();
        var sharpen_mode = $("sharpen_mode").value;
        var stretch_mode = $("stretch_mode").value;
        var stretch_mode_region = $("stretch_mode_region").value;
        var bands = $("bands").value;
        var width = imageHypotenuse;
        var height = imageHypotenuse;

        var angle = rotationAngle;
        var imageHeight = map.getCurrentSize().h;
        var imageWidth = map.getCurrentSize().w;
        var format = format;

        document.location.href = "http://localhost" + "${createLink(controller: 'ogc', action: 'chip')}" + "?layers=" + layers + "&bbox=" + bbox + "&interpolation=" + interpolation + "&brightness=" + brightness + "&contrast=" + contrast + "&sharpen_mode=" + sharpen_mode + "&stretch_mode=" + stretch_mode + "&stretch_mode_region=" + stretch_mode_region + "&bands=" + bands + "&width=" + width + "&height=" + height + "&angle=" + rotationAngle + "&imageHeight=" + imageHeight + "&imageWidth=" + imageWidth + "&format=" + format;

    }

    function getProjectedImage(params)
    {
        var link = "${createLink(action: "wcs", controller: "ogc")}";
        var extent = getSelectedExtents();

	    if (extent && extent.left)
	    {
	        viewportExtents = getViewportExtents();
	        if (!viewportExtents.containsBounds(extent))
	        {
	            alert("Selected extents exceeds the viewport extents.  The AOI will be cleared, please re-select the region to save.");
	            clearAOI();
	            return;
	        }
	    }
	    else { extent = getViewportExtents(); }

	    var size = getSizeInPixelsFromExtents(extent);
	    var wcsProperties = {"request":"GetCoverage", "format":params.format, "bbox":extent.toBBOX(), "coverage":params.coverage, "crs":"EPSG:4326", "width":size.w, "height":size.h}
        wcsParams.setProperties(wcsProperties);

        var form = $("wcsForm");
        var url = link + "?" + wcsParams.toUrlParams();alert(url);

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
	    var extent = getSelectedOrViewportExtents();
	    var size   = getSizeInPixelsFromExtents(extent);
	    var wmsProperties =
	    {
	        "request":"GetKMZ",
	        "bbox":extent.toBBOX(),
	        "srs":"EPSG:4326",
	        "width":size.w,
	        "height":size.h
	    }
	    wmsUrlParams.request = ""
        wmsUrlParams.setProperties(document);
        wmsUrlParams.setProperties(params);
        wmsUrlParams.setProperties(wmsProperties);

        var url = link + "?" + wmsUrlParams.toUrlParams();
        if(wmsParamForm)
        {
            wmsParamForm.action = url;
            wmsParamForm.submit();
        }
    }

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

    function setMapCtr(unit, value)
    {
        var requestedMapLocation;

        if (unit == "dd")
	    {
		    var ddRegExp = /^(\-?\d{1,2}\.?\d+)\,?\s?(\-?\d{1,3}\.?\d+)$/
		    if ($("ddMapCtr").value.match(ddRegExp))
		    {
			    var ddMapCtr = new OpenLayers.LonLat(RegExp.$2, RegExp.$1);
			    requestedMapLocation = ddMapCtr;
		    }
		    else { alert("Invalid Input."); }
	    }
	    else if (unit == "dms")
	    {
		    var dmsRegExp = /^(\d{1,2})\°?\s?(\d{2})\'?\s?(\d{2}\.?\d+)\"?\s?([NnSs])\,?\s?(\d{1,3})\°?\s?(\d{2})\'?\s?(\d{2}\.?\d+)\"?\s?([EeWw])$/
		    if ($("dmsMapCtr").value.match(dmsRegExp))
		    {
			    var dmsMapCtr = new OpenLayers.LonLat(coordConvert.dmsToDd(RegExp.$5, RegExp.$6, RegExp.$7, RegExp.$8), coordConvert.dmsToDd(RegExp.$1, RegExp.$2, RegExp.$3, RegExp.$4));
                requestedMapLocation = dmsMapCtr;
		    }
		    else { alert("Invalid Input."); }
	    }
	    else if (unit == "mgrs")
	    {
		    var foo = coordConvert.mgrsToUtm($("centerMgrs").value);
		    var mgrsRegExpUtm = /^(-?\d{1,2})(\.\d+)?\s?(-?\d{1,3})(\.\d+)?/
            if (foo.match(mgrsRegExpUtm))
            {
                var centerLat = parseInt(RegExp.$1, 10) + RegExp.$2;
                var centerLon = parseInt(RegExp.$3, 10) + RegExp.$4;
                var center = new OpenLayers.LonLat( centerLon, centerLat );
                requestedMapLocation = center;
            }
	    }

	    var deltaLongitude = requestedMapLocation.lon - newImageCenterLongitude;
        var deltaLatitude = requestedMapLocation.lat - newImageCenterLatitude;
        var deltaMagnitude = Math.sqrt(Math.pow(deltaLatitude, 2) + Math.pow(deltaLongitude, 2));

        if (deltaMagnitude != 0){
        var alpha = 0;

        if (deltaLongitude >= 0 && deltaLatitude >= 0)
        {
            alpha = Math.atan(deltaLatitude / deltaLongitude) * 180/Math.PI;

        }
        else if (deltaLongitude < 0 && deltaLatitude >= 0)
        {
            alpha = Math.atan(deltaLatitude / deltaLongitude) * 180/Math.PI + 180;

        }
        else if (deltaLongitude < 0 && deltaLatitude < 0)
        {
            alpha = Math.atan(deltaLatitude / deltaLongitude) * 180/Math.PI + 180;

        }
        else if (deltaLongitude > 0 && deltaLatitude < 0)
        {
            alpha = Math.atan(deltaLatitude / deltaLongitude) * 180/Math.PI + 360;

        }

        alpha += parseInt(rotationAngle);
        var relativeMouseLocationLongitude = map.getCenter().lon + deltaMagnitude * Math.cos(alpha * Math.PI/180);
        var relativeMouseLocationLatitude = map.getCenter().lat + deltaMagnitude * Math.sin(alpha * Math.PI/180);

        map.setCenter(new OpenLayers.LonLat(relativeMouseLocationLongitude, relativeMouseLocationLatitude), map.getZoom());
        }
    }

    function resetMapCenter()
    {
	    var bounds = new OpenLayers.Bounds(minLon, minLat, maxLon, maxLat);
	    map.setCenter(bounds.getCenterLonLat(), map.getZoom());
    }

    function mergeNewParams()
    {
        obj =
        {
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
	    for(var layer in rasterLayers) { rasterLayers[layer].mergeNewParams(obj); }

	    updateImage();
    }

    function resetBrightnessContrast()
    {
	    brightnessSlider.setRealValue(0);
	    contrastSlider.setRealValue(1.0);
    }

    function measureUnitChanged(unit)
    {
	    if(unit == 'kilometers') { pathMeasurement.innerHTML = pathUnit[0]; }
	    else if(unit == 'meters') { pathMeasurement.innerHTML = pathUnit[1]; }
	    else if(unit == 'feet') { pathMeasurement.innerHTML = pathUnit[2]; }
	    else if(unit == 'miles') { pathMeasurement.innerHTML = pathUnit[3]; }
	    else if(unit == 'yards') { pathMeasurement.innerHTML = pathUnit[4]; }
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
	
	map.updateSize();
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

    function sliderRotate(angle)
    {
        rotationAngle = parseInt(angle);

	    compassVectorLayer.removeFeatures([compassImage]);
	    imageVectorLayer.removeFeatures([image]);

        compassImage = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Point(0, 0), {angle: -rotationAngle});
        compassVectorLayer.addFeatures([compassImage]);

	    image = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Point(currentMapCenterLongitude, currentMapCenterLatitude), {urlPath: imageURL, imageWidth: imageHypotenuse, imageHeight: imageHypotenuse, angle: -rotationAngle}); ////////////////////
	    imageVectorLayer.addFeatures([image]);
    }

    function buildOMARLink()
    {
        var linkText = "";
        linkText += "${createLink(action: 'index')}" + "?";
        linkText += "layers=" + "${(rasterEntries*.indexId).join(',')}" + "&";
        linkText += "interpolation=" + $("interpolation").value + "&";
        linkText += "brightness=" + brightnessSlider.getRealValue() + "&";
        linkText += "contrast=" + contrastSlider.getRealValue() + "&";
        linkText += "sharpen_mode=" + $("sharpen_mode").value + "&";
        linkText += "stretch_mode=" + $("stretch_mode").value + "&";
        linkText += "stretch_mode_region=" + $("stretch_mode_region").value +"&";
        linkText += "bands=" + $("bands").value + "&";
        linkText += "transparent=false&";
        linkText += "srs=epsg:4326&";
        linkText += "format=image/jpeg&";
        linkText += "center=" + newImageCenterLatitude + "," + newImageCenterLatitude +"&";
        linkText += "bbox=" + (newImageCenterLongitude - (map.calculateBounds().right - map.calculateBounds().left)/2) + ",";
        linkText += newImageCenterLatitude - (map.calculateBounds().top - map.calculateBounds().bottom)/2 + ",";
        linkText += newImageCenterLongitude + (map.calculateBounds().right - map.calculateBounds().left)/2 + ",";
        linkText += newImageCenterLatitude + (map.calculateBounds().top - map.calculateBounds().bottom)/2 + "&";
        linkText += "rotation=" + rotationAngle;

        document.getElementById("shareLink").innerHTML = linkText;

        window.open("http://localhost/omar/mapView/shareLink", "", "height=200, width=450");
    }


</g:javascript>

</body>
</html>