<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: Feb 9, 2009
  Time: 10:19:01 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>OMAR <g:meta name="app.version"/>: Image Space Viewer</title>

  <openlayers:loadMapToolBar/>
  <openlayers:loadTheme theme="default"/>

  <meta name="layout" content="rasterViewsStatic"/>

  <meta name="apple-mobile-web-app-capable" content="yes" />
  <meta name="apple-mobile-web-app-status-bar-style" content="black" />
  <meta name="viewport" content="minimum-scale=1.0, width=device-width, maximum-scale=1.6, user-scalable=no">

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

  #slider-brightness-bg, #slider-contrast-bg{
    width:120px;
    background:url(${resource(plugin: 'richui', dir:'js/yui/slider/assets', file:'bg-fader.gif')}) 5px 0 no-repeat;
  }

 #slider-rotate-bg{
    
    background:url(${resource(plugin: 'richui', dir:'js/yui/slider/assets', file:'bg-fader.gif')}) 5px 0 no-repeat;
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
  }  </style>

  <openlayers:loadJavascript/>
  <g:javascript plugin="omar-core" src="touch.js"/>


</head>

<body class="yui-skin-sam" onload="init();" >


<g:form name="wmsFormId" method="POST">
</g:form>
<input type="hidden" name="request" value=""/>
<input type="hidden" name="layers" value=""/>
<input type="hidden" name="bbox" value=""/>
<input type="hidden" id="contrast" name="contrast" value="${params.contrast?:0}"/>
<input type="hidden" id="brightness" name="brightness" value="${params.brightness?:0}"/>

<content tag="top">



<div id="rasterMenu" class="yuimenubar yuimenubarnav">
	<div class="bd">
		<ul class="first-of-type">
			
			<li class="yuimenubaritem first-of-type"><a class="yuimenubaritemlabel" id="homeMenu" href="${createLink(controller: 'home', action: 'index')}" title="OMAR™ Home">&nbsp;&nbsp;&nbsp;&nbsp;OMAR™ Home</a>
			</li>
	
	
	
	
			<li class="yuimenubaritem first-of-type"><a class="yuimenubaritemlabel" href="#viewMenu">View</a>
				<div id="viewMenu" class="yuimenu">
					<div class="bd">
						<ul>
							<li class="yuimenuitem"><a class="yuimenuitemlabel" href="javascript:changeToSingleLayer();" title="Ground Space Viewer">Ground Space</a></li>
						
							<li class="yuimenuitem"><a class="yuimenuitemlabel" href="${createLink(controller: "mapView", action: "multiLayer", params: [layers: rasterEntry?.indexId])}" title="Multi Layer Ground Space Viewer">Multi Layer Ground Space</a></li>
						
						</ul>
                        <ul>
                            <li class="yuimenuitem"><a class="yuimenuitemlabel" href="${createLink(action: "imageSpace", params: [layers: rasterEntry?.indexId])}" title="Reset Image space">Reset</a></li>

                        </ul>
					</div>
				</div>
			</li>
	
	
	</div>
</div>

    <label>${(rasterEntry?.filename)}</label>

</content>

<content tag="left">
  <div class="niceBox">
        <div class="niceBoxHd">Image Adjustments:</div>
        <div class="niceBoxBody">
          <ol>
			<li>Interpolation:</li>
			<li>
				<g:select id="interpolation" name="interpolation" value="${params.interpolation?:bilinear}" from="${['bilinear', 'nearest neighbor', 'cubic', 'sinc']}" onChange="chgInterpolation()"/>
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
              <div align="center">
                  <button id="brightnessContrastReset" type="button" onclick="javascript:resetBrightnessContrast()">Reset</button>

              </div>

			
				<hr/>

            <li>Sharpen:</li>
            <li>
              <g:select id="sharpen_mode" name="sharpen_mode" value="${params.sharpen_mode?:'none'}" from="${['none', 'light', 'heavy']}" onChange="changeSharpenOpts()"/>
            </li>
			<li>Dynamic Range Adjustment:</li>
            <li>
				<g:select id="stretch_mode" name="stretch_mode" value="${params.stretch_mode?:'linear_auto_min_max'}" 
				        from="${[[name: 'Automatic', value: 'linear_auto_min_max'],[name: '1st Std', value: 'linear_1std_from_mean'],[name: '2nd Std', value: 'linear_2std_from_mean'],[name: '3rd Std', value: 'linear_3std_from_mean'],[name: 'No Adjustment', value: 'none']]}"
				        optionValue="name" optionKey="value"
				        onChange="changeHistoOpts()"/>
				</li>

            <li>Region:</li>
            <li>
              <g:select id="stretch_mode_region" name="stretch_mode_region" from="${['global', 'viewport']}" onChange="changeHistoOpts()" />
            </li>

            <g:if test="${rasterEntry?.numberOfBands == 1}">
              <li>Band:</li>
              <li><g:select id="bands" name="bands" value="${params.bands?:'0'}" from="${['0']}" onChange="changeBandsOpts()" /> </li>
            </g:if>
            <g:if test="${rasterEntry?.numberOfBands == 2}">
              <li>Bands:</li>
              <li><g:select id="bands" name="bands" value="${params.bands?:'0,1'}" from="${['0,1','1,0','0','1']}" onChange="changeBandsOpts()" /></li>
            </g:if>
            <g:if test="${rasterEntry?.numberOfBands >= 3}">
              <li>Bands:</li>
              <li><g:select id="bands" name="bands" value="${params.bands?:'0,1,2'}" from="${['0,1,2','2,1,0','0','1','2']}" onChange="changeBandsOpts()" /></li>
            </g:if>
            <li>Image Rotate:</li>
            <li>
                <!-- This is hidden to prevent rotating the image unintentionally -->
                <g:textField name="rotate" value="${params.rotate?:0}" style="display:none" size="1"/> <!-------------------->
                <!-- A new text field to hold the rotation value -->
                <g:textField name="rotateAngle" value="0" onChange="rotateSlider.setRealValue(this.value)" size="1"/> <!-------------------->
                <button id="rotateApply" type="button" onclick="rotateSlider.setRealValue($(rotateAngle).value)">Apply</button>

                <br> <!-------------------->

					<li>
					
					
				
					
						<div id="slider-rotate-bg" class="yui-h-slider" tabindex="-1" hidefocus="false"> 
			    			<div id="slider-rotate-thumb" class="yui-slider-thumb"><img src="${resource(plugin: 'richui', dir:'js/yui/slider/assets', file:'thumb-n.gif')}"></div> 
						</div> 
					</li>

               
                <br> <!-------------------->

                <!-- Buttons for preset rotation values -->
                <button type="button" onclick="rotateSlider.setRealValue(${rasterEntry.azimuthAngle})">North is Up</button> <!-------------------->


                <button type="button" onclick="rotateSlider.setRealValue('0')">Sensor is Up</button> <!-------------------->



   <button type="button" onclick="foo()">Refresh Layer</button> 
            </li>
          </ol>
        </div>
      </div>

    <!-- A new box to hold the north arrow -->
    <div class="niceBox"> <!-------------------->
        <div class="niceBoxHd">North Arrow:</div> <!-------------------->
        <div class="niceBoxBody"> <!-------------------->
            <div id="compassMap" style="width:190; height:190"></div> <!-------------------->
        </div> <!-------------------->
    </div> <!-------------------->

</content>

<content tag="center">
  <%--
  <h1 id="mapTitle">${rasterEntry?.mainFile?.name}</h1>
  <g:if test="${flash.message}">
    <div class="message">${flash.message}</div>
  </g:if>
  --%>
</div>
</content>
<content tag="bottom">

</content>







<g:javascript>
var map;
var hyp;
var layer;
var format = "image/jpeg";

var rotateSlider = YAHOO.widget.Slider.getHorizSlider("slider-rotate-bg",  "slider-rotate-thumb", 0, 360, 1);


var brightnessSlider = YAHOO.widget.Slider.getHorizSlider("slider-brightness-bg",  "slider-brightness-thumb", 0, 100, 1);
var contrastSlider = YAHOO.widget.Slider.getHorizSlider("slider-contrast-bg",  "slider-contrast-thumb", 0, 100, 1);
var omarImageSpaceOpenLayersParams = new  OmarImageSpaceOpenLayersParams();

var azimuthAngle; /////////////////////
var compassImage; /////////////////////
var compassMap; ////////////////////
var compassVectorLayer; ////////////////////
var currentMapCenterX; ////////////////////
var currentMapCenterY; ////////////////////
var image; ////////////////////
var imageURL; ////////////////////
var initFlag = 1; ////////////////////
var imageHypotenuse; ////////////////////
var imageVectorLayer; ////////////////////
var newImageCenterX; ////////////////////
var newImageCenterY; ////////////////////
var oldImageCenterX; ////////////////////
var oldImageCenterY; ////////////////////
var oldMapCenterX; ////////////////////
var oldMapCenterY; ////////////////////
var oldZoomLevel; ////////////////////
var rotationAngle = 0; ////////////////////

function changeMapSize( mapWidth, mapHeight )
{
   if(mapWidth&&mapHeight)
   {
      var Dom = YAHOO.util.Dom;
      var mapDiv = Dom.get( "map" );
      if(mapDiv)
      {
        mapDiv.style.width  = mapWidth + "px";
        mapDiv.style.height = mapHeight + "px";
      }
   }
 //  else
//   {
//     var mapCenter = document.getElementById("mapCenter");
//     var mapDiv   = document.getElementById("map");
//     mapDiv.style.width  = mapCenter.width + "px";
//     mapDiv.style.height = mapCenter.height + "px";
//   }


//        alert( mapWidth + ' ' + mapHeight );

  map.updateSize();
}


function changeToSingleLayer()
{
   var url = "${createLink(controller: 'mapView', action: 'index')}";
   var wmsFormElement = $("wmsFormId");
   if(wmsFormElement)
   {
      var imageAdjustmentParams = new OmarWmsParams();
      imageAdjustmentParams.setProperties(document);
      imageAdjustmentParams.layers = "${rasterEntry.indexId}";
      wmsFormElement.action = url + "?"+imageAdjustmentParams.toUrlParams();
      wmsFormElement.method = "POST";
      wmsFormElement.submit();
   }
}

function changeHistoOpts()
{
var stretch_mode = $("stretch_mode").value;
var stretch_mode_region = $("stretch_mode_region").value;

layer.mergeNewParams({stretch_mode:stretch_mode, stretch_mode_region: stretch_mode_region});

// update the image after the setting has changed ////////////////////
updateImage(); ////////////////////
}

 function rotateImage()
{
var rotate = $("rotate").value;
//alert(rotate);
layer.mergeNewParams({rotate:rotate});
}

function chgInterpolation()
{
	var interpolation = $("interpolation").value;
	layer.mergeNewParams({interpolation:interpolation});

    // update the image after the setting has changed ////////////////////
	updateImage(); ////////////////////
}

function changeSharpenOpts()
{
  var sharpen_mode = $("sharpen_mode").value;
  layer.mergeNewParams({sharpen_mode:sharpen_mode});

  // update the image after the setting has changed ////////////////////
  updateImage(); ////////////////////
}

function changeBandsOpts()
{
    var bands = $("bands").value;
    layer.mergeNewParams({bands:bands});

    // update the image after the setting has changed ////////////////////
    updateImage(); ////////////////////
}

function get_my_url (bounds)
{
  var width  = parseFloat("${rasterEntry.width}");
  var height = parseFloat("${rasterEntry.height}");
    var res = this.map.getResolution();
    var scalex = 1.0/(res * this.tileSize.w);
    var scaley = 1.0/(res * this.tileSize.h);
//    var x =  (((bounds.left - this.maxExtent.left)+xres)*scalex);
    var x =  (bounds.left - (-width*0.5))*scalex;
//    var y =  (((this.maxExtent.top - bounds.top)+yres)*scaley);
    var y =  ((height*0.5) - bounds.top)*scaley;
    var z = this.map.getZoom();

   // alert(x + ", " + y);
    omarImageSpaceOpenLayersParams.setProperties(document);
    omarImageSpaceOpenLayersParams.setProperties({'res':res,
                                                  'x':x,
                                                  'y':y,
                                                  'z':z,
                                                  // define an id of 0 to set the background image base layer to black ////////////////////
                                                  'id': 0, ////////////////////
                                                  'tileWidth':this.tileSize.w,
                                                  'tileHeight':this.tileSize.h});
    var path = "?"+omarImageSpaceOpenLayersParams.toUrlParams();

    var url = this.url;
    if (url instanceof Array) {
        url = this.selectUrl(path, url);
    }
    return url + path;
}

function resetBrightnessContrast()
{
	brightnessSlider.setRealValue(0);
	contrastSlider.setRealValue(1.0);

    // update the image after the setting has changed ////////////////////
	updateImage(); ////////////////////
}

function formatOutput(value)
{
    return value.lon + ", " + value.lat;
}
var resLevels = parseFloat("${rasterEntry.numberOfResLevels}");

function init(mapWidth, mapHeight)
{
  var width  = parseFloat("${rasterEntry.width}");
  var height = parseFloat("${rasterEntry.height}");
  var r = width;
  if(r < height) r = height;
  hyp    = Math.sqrt(width*width + height*height)*0.5;
  var left       =  -hyp;
  var bottom     =  -hyp;
  var top        =  hyp;
  var right      =  hyp;
  var url = "${createLink(controller: 'icp', action: 'getTileOpenLayers')}";
  var bounds = new OpenLayers.Bounds(Math.round(left), Math.round(bottom), Math.round(right), Math.round(top));
  // full res is included in resLevels so we need to add 2 more to give us
  // an 8x zoom
  map = new OpenLayers.Map("map", { controls:[], maxExtent:bounds, numZoomLevels:(resLevels+2) });
 map.events.register("moveend", map, theMapHasMoved); ////////////////////
  map.events.register('zoomend', map, theMapHasZoomed); ////////////////////


  var options = {
  controls: [],
  maxExtent: bounds,
    getURL: get_my_url,
    isBaseLayer: true,
    maxResolution: (width) / map.getTileSize().w,
    ratio: 1.0,
    transitionEffect: "resize",
    units:'pixel',
    singleTile:true,
    format: format
  };

	var oMenu = new YAHOO.widget.MenuBar("rasterMenu", {
                                              autosubmenudisplay: true,
                                              showdelay: 0,
                                              hidedelay: 750,
                                              lazyload: true});
	oMenu.render();

  layer = new OpenLayers.Layer.TMS( "Image Space Viewer",
                                    url, options);

//  var mousePositionControl = new OpenLayers.Control.MousePosition();
//  mousePositionControl.formatOutput =  formatOutput;
  map.addLayer(layer);

//  map.addControl(mousePositionControl);
  map.addControl(new OpenLayers.Control.MouseDefaults());
  map.addControl(new OpenLayers.Control.KeyboardDefaults());


  map.setBaseLayer(layer);
  changeMapSize(mapWidth, mapHeight);
  map.zoomToMaxExtent();
  setupToolbar();
  var isiPad = navigator.userAgent.match( /iPad/i ) != null;

   if ( isiPad )
   {
      this.touchhandler = new TouchHandler( map, 4 );
   }


	omarImageSpaceOpenLayersParams.setProperties(document);
	
	
	
	
	
	
	
	
	
	rotateSlider.animate = false;
	
	rotateSlider.getRealValue = function() { 
	    return this.getValue(); 
    }
	
	rotateSlider.setRealValue = function(value) { 
	    this.setValue(value%360);  
    }
	
	rotateSlider.subscribe("slideEnd", function() {
			sliderRotate(this.getRealValue());
    });









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
		if(layer)
		{
			layer.mergeNewParams({brightness:this.getRealValue()});

			// update the image after the setting has changed ////////////////////
			updateImage(); ////////////////////
		}
    });
    contrastSlider.subscribe("slideEnd", function() {
		if(layer)
		{
			layer.mergeNewParams({contrast:this.getRealValue()});

			// update the image after the setting has changed ////////////////////
			updateImage(); ////////////////////
		}
    });
     contrastSlider.subscribe("change", function(offsetFromStart){
    	$("contrast").value = this.getRealValue();
    	$("contrastTextField").value = this.getRealValue();
    });
    brightnessSlider.subscribe("change", function(offsetFromStart)
    {
    	$("brightness").value = this.getRealValue();
    	$("brightnessTextField").value = this.getRealValue();
    });

	brightnessSlider.setRealValue(${params.brightness?:0});
	contrastSlider.setRealValue(${params.contrast?:1});

	// setup the north arrow compass ////////////////////
	setupCompassMap(); ////////////////////

	// initialize old and new coordinate center variables ////////////////////
	oldMapCenterX = map.getCenter().lon; ////////////////////
	oldMapCenterY = map.getCenter().lat; ////////////////////
	oldImageCenterX = map.getCenter().lon; ////////////////////
	oldImageCenterY = map.getCenter().lat; ////////////////////
	newImageCenterX = map.getCenter().lon; ////////////////////
	newImageCenterY = map.getCenter().lat; ////////////////////

	// set the initialization flag so the moveend and zoomend code can execute
	initFlag = 0; ////////////////////

	// setup an the initial image vector layer ////////////////////
	imageVectorLayer = new OpenLayers.Layer.Vector("Simple Geometry", ////////////////////
	{ ////////////////////
		styleMap: new OpenLayers.StyleMap ////////////////////
		({ ////////////////////
			"default": ////////////////////
			{ ////////////////////
				externalGraphic : <%=' "${urlPath}" '%>, ////////////////////
				graphicWidth : <%=' "${imageWidth}" '%>, ////////////////////
      			graphicHeight : <%=' "${imageHeight}" '%>, ////////////////////
				rotation : <%=' "${angle}" '%> ////////////////////
			} ////////////////////
		}) ////////////////////
    }); ////////////////////
	map.addLayer(imageVectorLayer); ////////////////////

    // default the rotation to North is Up
    sliderRotate(0); ////////////////////

    // initialize the zoom level variable used to determine zoom in and out in the MapHasZoomed ////////////////////
	oldZoomLevel = map.getZoom();
}



function setupCompassMap() ////////////////////
{ ////////////////////
    // define the map for the compass and disable zoom and pan ////////////////////
    compassMap = new OpenLayers.Map('compassMap', {controls: new OpenLayers.Control.Navigation({autoActivate: false})}); ////////////////////

	// add some base layer so the image can be added on top ////////////////////
	var baseLayer = new OpenLayers.Layer("Empty", {isBaseLayer: true}); ////////////////////
	compassMap.addLayer(baseLayer); ////////////////////
	compassMap.setCenter(new OpenLayers.LonLat(0,0), 0); ////////////////////

    // define the image to be used for the compass ////////////////////
	var compassImageURL = "${resource(plugin: 'omar', dir:'images', file:'north_arrow.png')}"; ////////////////////

	// define a vector layer to add markers to ////////////////////
	compassVectorLayer = new OpenLayers.Layer.Vector("Compass Layer", ////////////////////
	{ ////////////////////
		styleMap: new OpenLayers.StyleMap ////////////////////
		({ ////////////////////
			"default": ////////////////////
			{ ////////////////////
				externalGraphic : compassImageURL, ////////////////////
				graphicWidth : 45, ////////////////////
                graphicHeight : 190, ////////////////////
			    rotation : <%=' "${angle}" '%> ////////////////////
			} ////////////////////
		}) ////////////////////
    }); ////////////////////
    compassMap.addLayer(compassVectorLayer); ////////////////////

    azimuthAngle = parseInt(${rasterEntry.azimuthAngle}); ////////////////////

	// define the marker for the image to sit on ////////////////////
	compassImage = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Point(0,0), {angle: (azimuthAngle)}); ////////////////////
	compassVectorLayer.addFeatures([compassImage]); ////////////////////
} ////////////////////

function updateImage() ////////////////////
{ ////////////////////

    // calculate the change in actual map movement ////////////////////
    var deltaMovementX = map.getCenter().lon - oldMapCenterX; ////////////////////
	var deltaMovementY = map.getCenter().lat - oldMapCenterY; ////////////////////
	var deltaMovementMagnitude = Math.sqrt(Math.pow(deltaMovementX,2) + Math.pow(deltaMovementY,2)); ////////////////////

    // calculate the angle of the actual map movement ////////////////////
	if (deltaMovementY >= 0 && deltaMovementX > 0) ////////////////////
	{ ////////////////////
		var beta = Math.atan(deltaMovementY / deltaMovementX) * 180/Math.PI; ////////////////////
	} ////////////////////
	else if (deltaMovementY >= 0 && deltaMovementX < 0) ////////////////////
	{ ////////////////////
		 var beta = Math.atan(deltaMovementY / deltaMovementX) * 180/Math.PI + 180; ////////////////////
	} ////////////////////
	else if (deltaMovementY < 0 && deltaMovementX < 0) ////////////////////
	{ ////////////////////
		var beta = Math.atan(deltaMovementY / deltaMovementX) * 180/Math.PI + 180; ////////////////////
	} ////////////////////
	else if (deltaMovementY < 0 && deltaMovementX > 0) ////////////////////
	{ ////////////////////
		var beta = Math.atan(deltaMovementY / deltaMovementX) * 180/Math.PI + 360; ////////////////////
	} ////////////////////
	else { beta = 0; } ////////////////////

	// relate the actual map movement to relative image movement ////////////////////
	var theta = beta - rotationAngle; ////////////////////
	var relativeXDelta = Math.cos(theta * Math.PI/180) * deltaMovementMagnitude; ////////////////////
	var relativeYDelta = Math.sin(theta * Math.PI/180) * deltaMovementMagnitude; ////////////////////

	// calculate the new map center ////////////////////
	newImageCenterX = oldImageCenterX + relativeXDelta; ////////////////////
	newImageCenterY = oldImageCenterY + relativeYDelta; ////////////////////

    // calculate the map bounds for the TMS call ////////////////////
    var mapWidthHalf = (map.calculateBounds().right - map.calculateBounds().left) / 2; ////////////////////
    var mapHeightHalf = (map.calculateBounds().top - map.calculateBounds().bottom) / 2; ////////////////////
    var mapHypotenuse = Math.sqrt(Math.pow(mapWidthHalf,2) + Math.pow(mapHeightHalf,2)); ////////////////////
    var imageBounds = new OpenLayers.Bounds( ////////////////////
        newImageCenterX - mapHypotenuse, ////////////////////
        newImageCenterY - mapHypotenuse, ////////////////////
        newImageCenterX + mapHypotenuse, ////////////////////
        newImageCenterY + mapHypotenuse ////////////////////
    ); ////////////////////

    // get the URL for the image ////////////////////
    imageURL = getImageURL(imageBounds); ////////////////////

    // calculate the image size so there are no gaps while rotating ////////////////////
    imageHypotenuse = Math.round(Math.sqrt(Math.pow(map.getCurrentSize().w, 2) + Math.pow(map.getCurrentSize().h, 2))); ////////////////////

    // get the current map center which is where the image will go ////////////////////
	currentMapCenterX = map.getCenter().lon; ////////////////////
	currentMapCenterY = map.getCenter().lat; ////////////////////

    // define the marker for the image to sit on ////////////////////
    image = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Point(currentMapCenterX, currentMapCenterY), {urlPath: imageURL, imageWidth: imageHypotenuse, imageHeight: imageHypotenuse, angle: -rotationAngle}); ////////////////////
	imageVectorLayer.addFeatures([image]); ////////////////////

    // set the map center coordinate variables to be used the next time the map is moved ////////////////////
	oldMapCenterX = map.getCenter().lon; ////////////////////
	oldMapCenterY = map.getCenter().lat; ////////////////////

    // set the image center coordinate variables to be used the next time the map is moved ////////////////////
	oldImageCenterX = newImageCenterX; ////////////////////
	oldImageCenterY = newImageCenterY; ////////////////////
} ////////////////////

function getImageURL(imageBounds) ////////////////////
{ ////////////////////

    var res = map.getResolution(); ////////////////////
    var tileHypotenuse = Math.round(Math.sqrt(Math.pow(map.getCurrentSize().w, 2) + Math.pow(map.getCurrentSize().h, 2))); ////////////////////

    var width  = parseFloat("${rasterEntry.width}"); ////////////////////
    var scalex = 1.0/(res * tileHypotenuse); ////////////////////
    var x =  (imageBounds.left - (-width*0.5))*scalex; ////////////////////

    var height = parseFloat("${rasterEntry.height}"); ////////////////////
    var scaley = 1.0/(res * tileHypotenuse); ////////////////////
    var y =  ((height*0.5) - imageBounds.top)*scaley; ////////////////////

    var z = map.getZoom(); ////////////////////

    omarImageSpaceOpenLayersParams.setProperties(document); ////////////////////
    omarImageSpaceOpenLayersParams.setProperties( ////////////////////
    { ////////////////////
        'res' : res, ////////////////////
        'x' : x, ////////////////////
        'y' : y, ////////////////////
        'z' : z, ////////////////////
        'id' : "${rasterEntry?.id}", ////////////////////
        'tileWidth' : tileHypotenuse, ////////////////////
        'tileHeight' : tileHypotenuse ////////////////////
    }); ////////////////////

    var path = "?"+omarImageSpaceOpenLayersParams.toUrlParams(); ////////////////////
    var url = "${createLink(controller: 'icp', action: 'getTileOpenLayers')}"; ////////////////////

    return url + path; ////////////////////
} ////////////////////

function theMapHasMoved() ////////////////////
{


 ////////////////////
    if (initFlag == 0) ////////////////////
    { ////////////////////
        updateImage(); ////////////////////

    } ////////////////////



} ////////////////////





/////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////




function theMapHasZoomed() ////////////////////
{ ////////////////////
    if (initFlag == 0) ////////////////////
    { ////////////////////
        // remove the image vector layer and erase all previous images ////////////////////
        imageVectorLayer.destroy(); ////////////////////

        // reset the image vector layer an make a new one ////////////////////
        resetImageVectorLayer(); ////////////////////

        // check to see if the user zoomed in ////////////////////
        if ((map.getZoom() - oldZoomLevel) > 0) ////////////////////
        { ////////////////////
            // scale the image size up by a factor of 2 ////////////////////
            imageHypotenuse = imageHypotenuse * 2; ////////////////////
        } ////////////////////
        // check to see if the user zoomed out ////////////////////
        else if((map.getZoom() - oldZoomLevel) < 0) ////////////////////
        { ////////////////////
            imageHypotenuse = imageHypotenuse * 0.5; ////////////////////
        } ////////////////////
        // define the new image marker and add it to the map ////////////////////
        image = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Point(currentMapCenterX, currentMapCenterY), {urlPath: imageURL, imageWidth: imageHypotenuse, imageHeight: imageHypotenuse, angle: -rotationAngle}); ////////////////////
	    imageVectorLayer.addFeatures([image]); ////////////////////

        // reset the zoom variable to be used next time ////////////////////
        oldZoomLevel = map.getZoom();
	} ////////////////////
} ////////////////////

function resetImageVectorLayer() ////////////////////
{ ////////////////////
    // remove the image vector layer and all previous images ////////////////////
    imageVectorLayer.destroy(); ////////////////////

    // define a vector layer to add markers to ////////////////////
	imageVectorLayer = new OpenLayers.Layer.Vector("Simple Geometry", ////////////////////
	{ ////////////////////
		styleMap: new OpenLayers.StyleMap ////////////////////
		({ ////////////////////
			"default": ////////////////////
			{ ////////////////////
				externalGraphic : <%=' "${urlPath}" '%>, ////////////////////
				graphicWidth : <%=' "${imageWidth}" '%>, ////////////////////
      			graphicHeight : <%=' "${imageHeight}" '%>, ////////////////////
				rotation : <%=' "${angle}" '%> ////////////////////
			} ////////////////////
		}) ////////////////////
    }); ////////////////////
	map.addLayer(imageVectorLayer); ////////////////////
} ////////////////////

function foo()
{
imageVectorLayer.redraw(true);
//alert("refreshed");
}


function sliderRotate(sliderValue) ////////////////////
{ ////////////////////
    // remove all previous images ////////////////////
    resetImageVectorLayer(); ////////////////////

    // calculate the rotation angle taking into account North is Up ////////////////////
    rotationAngle = parseInt(sliderValue) + parseInt(${rasterEntry.azimuthAngle}); ////////////////////

    // ensure the value of the rotation is normalized such that North is Up is 0 ////////////////////
    ${"rotateAngle"}.value = rotationAngle - parseInt(${rasterEntry.azimuthAngle}); ////////////////////

    // remove the image from the map so it can be rotated ////////////////////
	compassVectorLayer.removeFeatures([compassImage]); ////////////////////
	imageVectorLayer.removeFeatures([image]); ////////////////////

	// redefine the marker with the new rotation angle ////////////////////
	compassImage = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Point(0, 0), {angle: (${rasterEntry.azimuthAngle} - parseInt(sliderValue))}); ////////////////////
	compassVectorLayer.addFeatures([compassImage]); ////////////////////

	image = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Point(currentMapCenterX, currentMapCenterY), {urlPath: imageURL, imageWidth: imageHypotenuse, imageHeight: imageHypotenuse, angle: -rotationAngle}); ////////////////////
	imageVectorLayer.addFeatures([image]); ////////////////////
} ////////////////////

function rotateTextField() ////////////////////
{ ////////////////////
	alert("Foo");
    ${"slider"}.value = ${"rotateAngle"}.value; ////////////////////
	
    sliderRotate(${"slider"}.value); ////////////////////
} ////////////////////

  var zoomInButton;
  function zoomIn()
  {
	map.zoomIn();
	if(map.getZoom() >= map.getZoomForResolution(1.0, true))
	{
		zoomInButton.displayClass = "olControlFoo";
	}
  }
  function zoomInFullRes()
  {
      // we are image space so set to a 1:1 scale
      var zoom = map.getZoomForResolution(1.0, true)
      map.zoomTo(zoom)
	  zoomInButton.displayClass = "olControlFoo";
  }

  function zoomOut()
  {
    map.zoomOut();
	if(map.getZoom() < map.getZoomForResolution(1.0, true))
	{
		zoomInButton.displayClass = "olControlZoomIn";
	}

  }
    function setupToolbar()
    {

var panButton = new OpenLayers.Control.MouseDefaults({title:'Click pan button to activate. Once activated click the map and drag the mouse to pan.'});
     
 var zoomBoxButton = new OpenLayers.Control.ZoomBox(
      {title:"Click the zoom box button to activate. Once activated click and drag over an area of interest on the map to zoom into."});

      zoomInButton = new OpenLayers.Control.Button({title: "Click to zoom in.",
        displayClass: "olControlZoomIn",
        trigger: zoomIn
      });

     var zoomInFullResButton = new OpenLayers.Control.Button({title: "Click to zoom into full resolution.",
        displayClass: "olControlZoomToLayer",
        trigger: zoomInFullRes
      });

      var zoomOutButton = new OpenLayers.Control.Button({title: "Click to zoom out.",
        displayClass: "olControlZoomOut",
        trigger: zoomOut
      });

      var container = $("toolBar");

      var panel = new OpenLayers.Control.Panel(
      { div: container,defaultControl: panButton,'displayClass': 'olControlPanel'}
              );


      var navButton = new OpenLayers.Control.NavigationHistory({
        nextOptions: {title: "Next View" },
        previousOptions: {title: "Previous View"}
      });

   


      map.addControl(navButton);

      panel.addControls([
        
        panButton,
		zoomBoxButton,
        zoomInButton,
        zoomOutButton,
        //navButton.next, navButton.previous,
        new OpenLayers.Control.ZoomToMaxExtent({title:"Click to zoom to the max extent."}),
        zoomInFullResButton
      ]);

      map.addControl(panel);
    }

</g:javascript>
</body>
</html>
