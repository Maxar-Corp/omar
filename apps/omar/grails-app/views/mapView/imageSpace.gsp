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
  <title>OMAR: Image Space Viewer</title>

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


<g:form name="wmsParams" method="POST" url="[action:'wms', controller:'ogc']">
<input type="hidden" name="request" value=""/>
<input type="hidden" name="layers" value=""/>
<input type="hidden" name="bbox" value=""/>
<input type="hidden" id="contrast" name="contrast" value=""/>
<input type="hidden" id="brightness" name="brightness" value=""/>
</g:form>

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
							<li class="yuimenuitem"><a class="yuimenuitemlabel" href="${createLink(controller: "mapView", action: "index", params: [layers: rasterEntry?.indexId])}" title="Single Layer Viewer">Single Layer Viewer</a></li>
						
							<li class="yuimenuitem"><a class="yuimenuitemlabel" href="${createLink(controller: "mapView", action: "multiLayer", params: [layers: rasterEntry?.indexId])}" title="Multi Layer Viewer">Multi Layer Viewer</a></li>
						
						</ul>
					</div>
				</div>
			</li>
	
	
	</div>
</div>











</content>

<content tag="left">
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
              <g:select id="sharpen_mode" name="sharpen_mode" from="${['none', 'light', 'heavy']}" onChange="changeSharpenOpts()"/>
            </li>
            <li>Stretch:</li>
            <li>
              <g:select id="stretch_mode" name="stretch_mode" from="${['linear_auto_min_max', 'linear_1std_from_mean', 'linear_2std_from_mean', 'linear_3std_from_mean', 'none']}" onChange="changeHistoOpts()" />
            </li>
            <li>Region:</li>
            <li>
              <g:select id="stretch_mode_region" name="stretch_mode_region" from="${['global', 'viewport']}" onChange="changeHistoOpts()" />
            </li>

            <g:if test="${rasterEntry?.numberOfBands == 1}">
              <li>Band:</li>
              <li><g:select id="bands" name="bands" from="${['0']}" onChange="changeBandsOpts()" /> </li>
            </g:if>
            <g:if test="${rasterEntry?.numberOfBands == 2}">
              <li>Bands:</li>
              <li><g:select id="bands" name="bands" from="${['0,1','1,0','0','1']}" onChange="changeBandsOpts()" /></li>
            </g:if>
            <g:if test="${rasterEntry?.numberOfBands >= 3}">
              <li>Bands:</li>
              <li><g:select id="bands" name="bands" from="${['0,1,2','2,1,0','0','1','2']}" onChange="changeBandsOpts()" /></li>
            </g:if>
            <li>Image Rotate:</li>
            <li><g:textField name="rotate" onChange="rotateImage()" size="1"/></li>
          </ol>
        </div>
      </div>
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
var brightnessSlider = YAHOO.widget.Slider.getHorizSlider("slider-brightness-bg",  "slider-brightness-thumb", 0, 100, 1);
var contrastSlider = YAHOO.widget.Slider.getHorizSlider("slider-contrast-bg",  "slider-contrast-thumb", 0, 100, 1);
var imageSpaceBounds;
var omarImageSpaceOpenLayersParams = new  OmarImageSpaceOpenLayersParams();
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

  //map.updateSize();
  map.updateSize();
}

 function changeHistoOpts()
{
var stretch_mode = $("stretch_mode").value;
var stretch_mode_region = $("stretch_mode_region").value;


layer.mergeNewParams({stretch_mode:stretch_mode, stretch_mode_region: stretch_mode_region});
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
}

function changeSharpenOpts()
{
  var sharpen_mode = $("sharpen_mode").value;

  layer.mergeNewParams({sharpen_mode:sharpen_mode});
}

function changeBandsOpts()
{
    var bands = $("bands").value;

    layer.mergeNewParams({bands:bands});
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
                                                  'id': "${rasterEntry?.id}",
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
}



function init(mapWidth, mapHeight)
{
  var width  = parseFloat("${rasterEntry.width}");
  var height = parseFloat("${rasterEntry.height}");
  var r = width;
  if(r < height) r = height;
//  hyp    = Math.sqrt(2)*(r*0.5);//Math.sqrt(r*r + r*r);
  hyp    = Math.sqrt(width*width + height*height)*0.5;

  var left       =  -hyp;
  var bottom     =  -hyp;
  var top        =  hyp;
  var right      =  hyp;
  var url = "${createLink(controller: 'icp', action: 'getTileOpenLayers')}";
  var resLevels = parseFloat("${rasterEntry.numberOfResLevels}");
  var bounds = new OpenLayers.Bounds(Math.round(left), Math.round(bottom), Math.round(right), Math.round(top));
  // full res is included in resLevels so we need to add 2 more to give us
  // an 8x zoom
  map = new OpenLayers.Map("map", {controls:[], maxExtent:bounds, numZoomLevels:(resLevels+2)});
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

  layer = new OpenLayers.Layer.TMS( "Image Space Viewer",
                                    url, options);
  map.addLayer(layer);
  map.addControl(new OpenLayers.Control.MousePosition());
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

	var oMenu = new YAHOO.widget.MenuBar("rasterMenu", { 
                                              autosubmenudisplay: true, 
                                              hidedelay: 750, 
                                              lazyload: true});
	oMenu.render();

	omarImageSpaceOpenLayersParams.setProperties(document);
	

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
		}
    });
    contrastSlider.subscribe("slideEnd", function() {
		if(layer)
		{
			layer.mergeNewParams({contrast:this.getRealValue()});
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

	brightnessSlider.setRealValue(0);
	contrastSlider.setRealValue(1);
}

  function zoomIn()
  {
    map.zoomIn();
  }
  function zoomInFullRes()
  {
      // we are image space so set to a 1:1 scale
      var zoom = map.getZoomForResolution(1.0, true)
      map.zoomTo(zoom)
  }

  function zoomOut()
  {
    map.zoomOut();

  }
    function setupToolbar()
    {

var panButton = new OpenLayers.Control.MouseDefaults({title:'Drag to recenter map'});
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
      { div: container,defaultControl: panButton,'displayClass': 'olControlPanel'}
              );


      var navButton = new OpenLayers.Control.NavigationHistory({
        nextOptions: {title: "Next View" },
        previousOptions: {title: "Previous View"}
      });

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


      map.addControl(navButton);

      panel.addControls([
        
        panButton,
		zoomBoxButton,
        zoomInButton,
        zoomOutButton,
        navButton.next, navButton.previous,
        new OpenLayers.Control.ZoomToMaxExtent({title:"Zoom to the max extent"}),
        zoomInFullResButton
//          measureDistanceButton,
//          measureAreaButton
      ]);

      map.addControl(panel);
    }

</g:javascript>
</body>
</html>
