<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: 2/7/12
  Time: 10:27 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>OMAR <g:meta name="app.version"/>: Image Space - ${(rasterEntry?.filename)}</title>
  <meta content="imageSpacePageLayout" name="layout">
  <r:require modules="imageSpacePageLayout"/>
  <style type="text/css">
  #slider-brightness-bg, #slider-contrast-bg {
    background: url(${resource(plugin: 'yui', dir:'js/yui/slider/assets', file:'bg-fader.gif')}) 5px 0 no-repeat;
    width: 120px;
  }

  #slider-rotate-bg {
    background: url(${resource(plugin: 'yui', dir:'js/yui/slider/assets', file:'bg-fader.gif')}) 5px 0 no-repeat;
    width: 180px;
  }

 .olControlPanel .olControlButtonPanItemActive { 
  width:  24px;  
  height: 22px;
  background-image: url(${resource(plugin: 'openlayers', dir:'js/theme/default/img/', file:'pan_on.png')});
  background-repeat: no-repeat;
}

.olControlPanel .olControlButtonPanItemInactive { 
  width:  24px;  
  height: 22px;
  background-color: orange;
  background-image: url(${resource(plugin: 'openlayers', dir:'js/theme/default/img/', file:'pan_off.png')});
  background-repeat: no-repeat;
}

.olControlPanel .olControlButtonSelectAOIItemInactive { 
  width:  24px;  
  height: 24px;
  background-image: url(${resource(plugin: 'openlayers', dir:'images/themes/gis/grass', file:'mActionSelect.png')});
  background-repeat: no-repeat;
}

.olControlPanel .olControlButtonSelectAOIItemActive { 
  width:  24px;  
  height: 24px;
  background-color: orange;
  background-image: url(${resource(plugin: 'openlayers', dir:'images/themes/gis/grass', file:'mActionSelect.png')});
  background-repeat: no-repeat;
}

.olControlPanel .olControlButtonDeleteAOIItemInactive { 
  width:  24px;  
  height: 24px;
  background-image: url(${resource(plugin: 'openlayers', dir:'images/themes/gis', file:'mActionDeleteSelected.png')});
  background-repeat: no-repeat;
}

.olControlPanel .olControlButtonDeleteAOIItemActive { 
  width:  24px;  
  height: 24px;
  background-color: orange;
  background-image: url(${resource(plugin: 'openlayers', dir:'images/themes/gis', file:'mActionDeleteSelected.png')});
  background-repeat: no-repeat;
}

.olControlPanel .olControlButtonMeasurePointItemActive { 
  width:  24px;  
  height: 24px;
  background-color: orange;
  background-image: url(${resource(plugin: 'openlayers', dir:'images/themes/gis', file:'bullseye.png')});
  background-repeat: no-repeat;
}

.olControlPanel .olControlButtonMeasurePointItemInactive { 
  width:  24px;  
  height: 24px;
  background-image: url(${resource(plugin: 'openlayers', dir:'images/themes/gis', file:'bullseye.png')});
  background-repeat: no-repeat;
}

.olControlPanel .olControlButtonMeasurePathItemActive { 
  width:  24px;  
  height: 24px;
  background-color: orange;
  background-image: url(${resource(plugin: 'openlayers', dir:'images/themes/gis', file:'mActionMeasure.png')});
  background-repeat: no-repeat;
}

.olControlPanel .olControlButtonMeasurePathItemInactive { 
  width:  24px;  
  height: 24px;
  background-image: url(${resource(plugin: 'openlayers', dir:'images/themes/gis', file:'mActionMeasure.png')});
  background-repeat: no-repeat;
}

.olControlPanel .olControlButtonMeasureAreaItemActive { 
  width:  24px;  
  height: 24px;
  background-color: orange;
  background-image: url(${resource(plugin: 'openlayers', dir:'images/themes/gis', file:'mActionMeasureArea.png')});
  background-repeat: no-repeat;
}
.olControlPanel .olControlButtonMeasureAreaItemInactive { 
  width:  24px;  
  height: 24px;
  background-image: url(${resource(plugin: 'openlayers', dir:'images/themes/gis', file:'mActionMeasureArea.png')});
  background-repeat: no-repeat;
}

</style>
</head>

<body class=" yui-skin-sam">
 <g:form name="wmsFormId" method="POST"></g:form>
<input type="hidden" name="request" value=""/>
<input type="hidden" name="layers" value=""/>
<input type="hidden" name="bbox" value=""/>
<input type="hidden" id="contrast" name="contrast" value="${params.contrast ?: 0}"/>
<input type="hidden" id="brightness" name="brightness" value="${params.brightness ?: 0}"/>

<content tag="top1">
  <g:render template="imageSpaceMenu" model="${[rasterEntry: rasterEntry]}"/>
</content>

<content tag="bottom1"></content>

<content tag="left1">
  <g:render template="imageSpaceAdjustments" model="${[rasterEntry: rasterEntry, params: params]}"/>
</content>

<%--
<content tag="right1"></content>
--%>

<content tag="top2">
  <div id="toolBar" class="olControlPanel"></div>
</content>

<content tag="bottom2">

      <div id="mouseDisplayId" align="left"></div>

<!--  <table><tr>
    <td width="33%"><div id="ddMousePosition">&nbsp;</div></td>
    <td width="33%"><div id="dmsMousePosition">&nbsp;</div></td>
    <td width="33%"><div id="mgrsMousePosition">&nbsp;</div></td>
  </tr></table>
-->
</content>

<content tag="center2">

<div id="eventDivId"></div>
<div id="compassDivId" align="left">
    <img src="${resource(plugin: 'omar', dir: 'images', file: 'north_arrow.png')}">
</div>
 <div id="hudDivId">
 </div>

<div id="popDivId">
</div>

    <div id="map"></div>
</content>

<r:script>
 
//var hudChildDivId = document.getElementById("hudChildDivId")

var brightnessSlider;
var contrastSlider;
var currentMapCenterX;
var currentMapCenterY;
var format;
var image;
var imageBounds;
var imageURL;
var initFlag;
var layer;
var map;
var omarImageSpaceOpenLayersParams;
var resLevels;
var rotateSlider;
var rotationAngle;
var zoomInButton;
var selectedFeature;

function resetRotate()
{
rotateSlider.setRealValue(0.0);
}
function rotateUpIsUp()
{
rotateSlider.setRealValue(OMAR.imageManipulator.upIsUpAngle);
}
function rotateNorthUp()
{
rotateSlider.setRealValue(OMAR.imageManipulator.northAngle);
}
function changeBandsOpts()
{
    var bands = $("bands").value;
    layer.mergeNewParams({bands:bands});

   // updateImage();
}

function changeHistoOpts()
{
   var stretch_mode = $("stretch_mode").value;
    var stretch_mode_region = $("stretch_mode_region").value;
    layer.mergeNewParams({stretch_mode:stretch_mode, stretch_mode_region: stretch_mode_region});
    //updateImage(); ////////////////////
}

function changeMapSize( mapWidth, mapHeight )
{
   if(mapWidth && mapHeight)
   {
        var Dom = YAHOO.util.Dom;
        var mapDiv = Dom.get( "map" );
        if(mapDiv)
        {
            mapDiv.style.width  = mapWidth + "px";
            mapDiv.style.height = mapHeight + "px";
        }
    }
    if(map) map.updateSize();
}

function changeSharpenOpts()
{
    var sharpen_mode = $("sharpen_mode").value;
    layer.mergeNewParams({sharpen_mode:sharpen_mode});

    //updateImage();
}

function changeToSingleLayer()
{
	var bboxCoords = new Array();
	var bboxPixels = map.calculateBounds().toArray();
	var lowerLeft = OMAR.imageManipulator.pointToLocal({x:bboxPixels[0],y:bboxPixels[1]}); 
	var upperRight = OMAR.imageManipulator.pointToLocal({x:bboxPixels[2],y:bboxPixels[3]});

	var request = OpenLayers.Request.POST({
		url: "${createLink( controller: 'imageSpace', action: 'imageToGround' )}",
		data: YAHOO.lang.JSON.stringify({
			id:${rasterEntry.id},
			imagePoints:[{"x":lowerLeft.x, "y":lowerLeft.y}]
		}),
		callback: function (transport)
		{
			var temp = YAHOO.lang.JSON.parse(transport.responseText);
			bboxCoords[0] = temp[0].lon;
			bboxCoords[1] = temp[0].lat;

			var request = OpenLayers.Request.POST({
				url: "${createLink( controller: 'imageSpace', action: 'imageToGround' )}",
				data: YAHOO.lang.JSON.stringify({
					id:${rasterEntry.id},
					imagePoints:[{"x":upperRight.x, "y":upperRight.y}] 
				 }),
				callback: function (transport)
				{
					var temp = YAHOO.lang.JSON.parse(transport.responseText);
					bboxCoords[2] = temp[0].lon;
					bboxCoords[3] = temp[0].lat;

					var request = OpenLayers.Request.POST({
						url: "${createLink( controller: 'imageSpace', action: 'imageToGround' )}",
						data: YAHOO.lang.JSON.stringify({
							id:${rasterEntry.id},
							imagePoints:[{"x":OMAR.imageManipulator.getCenterLocal().x, "y":OMAR.imageManipulator.getCenterLocal().y}]
						}),
						callback: function (transport)
						{
							var temp = YAHOO.lang.JSON.parse(transport.responseText);
							var centerLatitude = temp[0].lat;
							var centerLongitude = temp[0].lon;

							var url = "${createLink(controller: 'mapView', action: 'index')}";
							var wmsFormElement = $("wmsFormId");
							if(wmsFormElement)
							{
								var imageAdjustmentParams = new OmarWmsParams();
								imageAdjustmentParams.setProperties(document);
								imageAdjustmentParams.layers = "${rasterEntry.indexId}";
								imageAdjustmentParams.latitude = centerLatitude;
								imageAdjustmentParams.longitude = centerLongitude;
								imageAdjustmentParams.bbox = bboxCoords[0] + "," + bboxCoords[1] + "," + bboxCoords[2] + "," + bboxCoords[3];
								wmsFormElement.action = url + "?"+imageAdjustmentParams.toUrlParams();
								wmsFormElement.method = "POST";
								wmsFormElement.submit();
							}
						}
					});
				}
        		});
		}
	});
}

function chgInterpolation()
{
  var interpolation = $("interpolation").value;
  layer.mergeNewParams({interpolation:interpolation});

  //updateImage();
}

function chipImage(format)
{
   var res = OMAR.imageManipulator.map.getResolution();
    var scale = 1.0/res;
    var url = "${createLink(controller: 'imageSpace', action: 'getTile')}";
    var x = null;
    var y = null;
    var w = null;
    var h = null;
    var pivot = null;
    var affineM      = OMAR.imageManipulator.generateOssimFullImageTransform();


    if(OMAR.imageManipulator.selectionBox)
    {
        x = parseInt(OMAR.imageManipulator.selectionBox.style.left);
        y = parseInt(OMAR.imageManipulator.selectionBox.style.top);
        w = parseInt(OMAR.imageManipulator.selectionBox.style.width);
        h = parseInt(OMAR.imageManipulator.selectionBox.style.height);
        var centerChip = OMAR.imageManipulator.pointToLocal(OMAR.imageManipulator.annotationPointToPoint({x:x+w/2,y:y+h/2}));
        var center =  OMAR.imageManipulator.getCenterLocal();
        pivot = Math.round(center.x) + "," + Math.round(center.y);

        var centerView = affineM.transform(centerChip);

        x = Math.round(centerView.x-w/2);
        y = Math.round(centerView.y-h/2);
    }
    else
    {
        // we will chip the viewport only
        //
        var w = Math.abs(OMAR.imageManipulator.containerDivRegion.right - OMAR.imageManipulator.containerDivRegion.left) + 1;
        var h = Math.abs(OMAR.imageManipulator.containerDivRegion.top - OMAR.imageManipulator.containerDivRegion.bottom) + 1;
        var center =  OMAR.imageManipulator.getCenterLocal();
        pivot = Math.round(center.x) + "," + Math.round(center.y);
        var centerView = affineM.transform(center);
        x = Math.round(centerView.x-w/2);
        y = Math.round(centerView.y-h/2);
   }

    //var size = bounds.getSize();

    var z = this.map.getZoom();
    var params = new OmarImageSpaceGetTileParams();

    params.setProperties(document);
    params.setProperties(
    {
                        'x': x,
                        'y': y,
                        'scale':scale,
                        'rotate': -parseFloat(${"rotateAngle"}.value),
                        'width':w,
                        'height':h,
                        'format':"image/"+format,
                        'id' : "${rasterEntry?.id}",
                        'pivot' : pivot
    });

     document.location.href = "${createLink(controller: 'imageSpace', action: 'getTile')}" + "?" + params.toUrlParams();
}
function initUnit()
{
    OpenLayers.INCHES_PER_UNIT['m'] = 39.3700787;
    OMAR.measure = {}
    OMAR.measure.units = { labels:["kilometers", "meters", "feet", "yards", "miles", "nautical miles"],
                           openlayersMapping:{"kilometers":"Kilometer", "meters":"Meter", "feet":"Foot", "yards":"Yard", "miles": "Mile", "nautical miles":"NautM"},
                           extensionMapping:{"kilometers":"km", "meters":"m", "feet":"ft", "yards":"yd", "miles": "mi", "nautical miles":"nmi" },
                           precisionMapping:{"kilometers":10000,"meters":1000,"feet":100,"yards":100, "miles":10000, "nautical miles":10000},
                           active:"meters"
                         };
}
function unitsChanged(value)
{
   OMAR.measure.units.active = value;
    displayMeasurements();

}
function loadUnitSelection()
{
    if(!OMAR.measure)
    {
        initUnit();
    }
    var selectionUnit = $("unitSelectionId");
    if(selectionUnit)
    {
        selectionUnit.from = OMAR.measure.units.labels;
    }
}
function moveToCenter()
{
 if(OMAR.imageManipulator) OMAR.imageManipulator.moveToCenter();
}
function getTileUrl (bounds)
{
    var width  = parseFloat("${rasterEntry.width}");
    var height = parseFloat("${rasterEntry.height}");
    var res = this.map.getResolution();
    var scale = 1.0/res;
    var size = bounds.getSize();


    var x = bounds.left*scale;
    var y = (height-bounds.top)*scale;

    var z = this.map.getZoom();
    var params = new OmarImageSpaceGetTileParams();

    params.setProperties(document);
    params.setProperties(
    {
                        'x': x,
                        'y': y,
                        'scale':scale,
                        'width':this.tileSize.w,
                        'height':this.tileSize.h,
                        'id' : "${rasterEntry?.id}"
    });

    var path = "?"+params.toUrlParams();
    var url = this.url;

    if (url instanceof Array) { url = this.selectUrl(path, url); }

    return url + path;
}

function init(mapWidth, mapHeight)
{

    loadUnitSelection();
    OMAR.imageManipulator = new OMAR.OpenLayersImageManipulator();
    //OMAR.imageManipulator.click = function(evt)
    //{
    //   getCoordinates(OMAR.imageManipulator.pointToLocal(this.mouseToPoint(evt)));
   // }
    OMAR.coordConvert = new CoordinateConversion();

    OMAR.imageManipulator.northAngle = parseFloat("${rasterEntry.azimuthAngle}");
    OMAR.imageManipulator.upIsUpAngle   =  parseFloat("${upIsUpRotation}");
    brightnessSlider = YAHOO.widget.Slider.getHorizSlider("slider-brightness-bg",  "slider-brightness-thumb", 0, 100, 1);
    contrastSlider = YAHOO.widget.Slider.getHorizSlider("slider-contrast-bg",  "slider-contrast-thumb", 0, 100, 1);
    omarImageSpaceOpenLayersParams = new  OmarImageSpaceOpenLayersParams();
    format = "image/jpeg";
    resLevels = parseFloat("${rasterEntry.numberOfResLevels}");
    initFlag = 1;
    rotateSlider = YAHOO.widget.Slider.getHorizSlider("slider-rotate-bg",  "slider-rotate-thumb", 0, 180, 1);
    rotationAngle = "${params.rotate ?: 0}";///${"rotateAngle"}.value;
    OpenLayers.ImgPath = "${resource(plugin: 'openlayers', dir: 'js/img')}/";

    var width  = parseFloat("${rasterEntry.width}");
    var height = parseFloat("${rasterEntry.height}");
    //var url = "${createLink(controller: 'imageSpace', action: 'getTileOpenLayers')}";
    var url = "${createLink(controller: 'imageSpace', action: 'getTile')}";
    var bounds = new OpenLayers.Bounds(0, 0, width, height);
    map = new OpenLayers.Map("map", { controls:[], theme: null, maxExtent:bounds, maxResolution: 16, numZoomLevels:(resLevels+5) });
    map.events.manipulator = OMAR.imageManipulator;
    var options = {
         controls: [],
         maxExtent: bounds,
         getURL: getTileUrl,
         isBaseLayer: true,
         maxResolution: (width) / map.getTileSize().w,
         transitionEffect: "resize",
         units:'pixel',
         singleTile:true,
         format: format
     };

    var oMenu = new YAHOO.widget.MenuBar("rasterMenu",
    {
        autosubmenudisplay: true,
          showdelay: 0,
          hidedelay: 750,
          lazyload: true
      });
    oMenu.render();


    //map.events.register('zoomend', null, theMapHasZoomed);
    //map.events.register("moveend", null, theMapHasMoved);

    layer = new OpenLayers.Layer.TMS( "Image Space Viewer", url, options);
    map.addLayer(layer);
    //map.addControl(new OpenLayers.Control.MouseDefaults());
    //map.addControl(new OpenLayers.Control.KeyboardDefaults());
    map.setBaseLayer(layer);
    //changeMapSize(mapWidth, mapHeight);
    map.zoomToMaxExtent();


    setupToolbar();
    /*
    var isiPad = navigator.userAgent.match( /iPad/i ) != null;
    if ( isiPad )
    {
        this.touchhandler = new TouchHandler( map, 4 );
    }
    */

    omarImageSpaceOpenLayersParams.setProperties(document);

    brightnessSlider.animate = false;
    brightnessSlider.getRealValue = function() { return ((this.getValue() - 50)/50.0); }
    brightnessSlider.setRealValue = function(value) { this.setValue((value + 1) * 50); }
    brightnessSlider.subscribe("change", function(offsetFromStart)
    {
      $("brightness").value = this.getRealValue();
      $("brightnessTextField").value = this.getRealValue();
    });
    brightnessSlider.subscribe("slideEnd", function()
    {
      if(layer)
      {
        layer.mergeNewParams({brightness:this.getRealValue()});
      }
      });
    brightnessSlider.setRealValue(${params.brightness ?: 0});

    contrastSlider.getRealValue = function()
    {
        var value = (this.getValue()/100.0)*2.0;
        return value;
    }
    contrastSlider.setRealValue = function(value) { this.setValue(value*50); }
    contrastSlider.subscribe("change", function(offsetFromStart)
    {
      $("contrast").value = this.getRealValue();
      $("contrastTextField").value = this.getRealValue();

    });
   contrastSlider.subscribe("slideEnd", function()
   {
      if(layer)
      {
        layer.mergeNewParams({contrast:this.getRealValue()});
      }
  });
  contrastSlider.setRealValue(${params.contrast ?: 1});

  rotateSlider.animate = false;
  rotateSlider.getRealValue = function() { return this.getValue() * 2; }
  rotateSlider.setRealValue = function(value) { this.setValue(Math.ceil((value%360.0) / 2)); }
  rotateSlider.subscribe("change", function() { sliderRotate(this.getRealValue()); });
  //rotateSlider.subscribe("slideEnd", function() { OMAR.imageManipulator.containerResized() });

  // set the initialization flag so the moveend and zoomend code can execute
  initFlag = 0;

    var offsetX = width/2.0;
    var offsetY = height/2.0;

    map.setCenter(new  OpenLayers.LonLat(0.0,0.0), 0);

	<g:if test="${(params.latitude != null) && (params.longitude != null)}">
		var url = "/omar/imageSpace/groundToImage";
		var request = OpenLayers.Request.POST({
			url:url,
			data: YAHOO.lang.JSON.stringify({
				id:${rasterEntry.id},
				groundPoints:[{"lat":${params.latitude}, "lon":${params.longitude}}]
				}),
			callback: function (transport)
			{
				var temp = YAHOO.lang.JSON.parse(transport.responseText);
				 OMAR.imageManipulator.setCenterGivenImagePoint(temp[0]);
			}
		});
	</g:if>

	<g:if test="${params.bbox != null}">
		bboxToPixel();
	</g:if>

    map.zoomToMaxExtent();
    // map.zoomIn();
    // initialize the zoom level variable used to determine zoom in and out in the MapHasZoomed ////////////////////
   //OMAR.imageManipulator.affineParams.rotate = parseFloat(${"rotateAngle"}.value);
   OMAR.imageManipulator.localImageBounds = new OpenLayers.Bounds(0, 0, width, height);
   OMAR.imageManipulator.setup("center2", map, "hudDivId", "eventDivId", "compassDivId");
   // add these just in case there were settings passed to the GSP 
   // but we only want to apply them once the page is finished with setup
   
   //OMAR.imageManipulator.updateTransform(); 
   //OMAR.imageManipulator.transformDiv();
   //OMAR.imageManipulator.applyRotate(0.0);
   //OMAR.imageManipulator.applyRotate(${"rotateAngle"}.value);
   // OMAR.iamgeManipulator.setToolMode(OMAR.ToolModeType.PAN_ZOOM);
   //alert(map.getMaxExtents());

   OMAR.imageManipulator.events.on({
            "featureDone": measureFinished,
            "featureRemoved" : measureRemoved,
            "click" : mouseClick
    });


    // Key press check for error ellipse popup
    document.onkeyup = KeyCheck;

    // Offset for popup positioning
    var region = YAHOO.util.Region.getRegion(OMAR.imageManipulator.containerDiv);
    regionOffX = region.left - 1;
    regionOffY = region.top - 1;
    regionRight = region.right - regionOffX;
    regionBottom = region.bottom - regionOffY;
    rotateSlider.setRealValue(rotationAngle);
    //OMAR.imageManipulator.applyRotate(${"rotateAngle"}.value);
    //setTimeout("applyRotationAfterInit()", 100);
}
<g:if test="${params.bbox != null}">
function bboxToPixel(bbox)
{
	var bbox = "${params.bbox}";
	var mapBBOX = bbox.split(",");
	if (mapBBOX.length == 4)
	{
		var cornerPoint = new Array();
		cornerPoint[0] = mapBBOX[0];
		cornerPoint[1] = mapBBOX[1];
		cornerPoint[2] = mapBBOX[2];
		cornerPoint[3] = mapBBOX[3];
                
		var url = "/omar/imageSpace/groundToImage";
                var request = OpenLayers.Request.POST({
                	url:url,
                	data: YAHOO.lang.JSON.stringify({
				id:${rasterEntry.id},
				groundPoints:[{"lat":cornerPoint[1], "lon":cornerPoint[0]}]
			}),
			callback: function (transport){
				var temp = YAHOO.lang.JSON.parse(transport.responseText);
				bboxToPixelFinish(0,temp[0].x,temp[0].y);
				}
		});
		var request = OpenLayers.Request.POST({
			url:url,
			data: YAHOO.lang.JSON.stringify({
				id:${rasterEntry.id},
				groundPoints:[{"lat":cornerPoint[3], "lon":cornerPoint[2]}]
			}),
			callback: function (transport){
				var temp = YAHOO.lang.JSON.parse(transport.responseText);
				bboxToPixelFinish(1,temp[0].x,temp[0].y);
			}
		});			
	}
	else { map.zoomToMaxExtent(); }
}

var lowerLeftCoordinate;
var upperRightCoordinate;
function bboxToPixelFinish(i,longitude,latitude)
{
	if (i == 0)
	{
		lowerLeftCoordinate = [longitude,latitude];
	}
	else if (i == 1)
	{
		upperRightCoordinate = [longitude,latitude];
	}

	if ((lowerLeftCoordinate != null) && (upperRightCoordinate != null))
	{
		var zoom = map.getZoomForExtent(new OpenLayers.Bounds(lowerLeftCoordinate[0],lowerLeftCoordinate[1],upperRightCoordinate[0],upperRightCoordinate[1]), true);
		map.zoomTo(zoom);
	}
}
</g:if>


function mouseClick(evt){

    // point drop with error propagation (PQE)
    if(OMAR.imageManipulator.toolMode == 'point')
    {
        var isIE=document.all;
        var xyPop = new OmarPoint();
        if (isIE)
        {
            xyPop.x = event.clientX;
            xyPop.y = event.clientY;
        }
        else
        {
            xyPop.x = evt.clientX;
            xyPop.y = evt.clientY;
        }

        var point = OMAR.imageManipulator.pointToLocal(this.mouseToPoint(evt));

        getProjectedGround(point, xyPop);
    }
}


function convertPathAreaMetersToTargetUnit(geodLength, pathLength, area, targetUnit)
{
    var openLayersMapping = OMAR.measure.units.openlayersMapping[targetUnit];
    var inchesSourceMultiplier = 1.0/OpenLayers.METERS_PER_INCH;

    var targetMultiplier  = 1.0/OpenLayers.INCHES_PER_UNIT[openLayersMapping];
    var targetLen  = targetMultiplier;
    var targetArea = targetMultiplier*targetMultiplier;

    var inchesSourceG = inchesSourceMultiplier*geodLength;
    var inchesSource = inchesSourceMultiplier*pathLength;
    var inchesSourceArea = area*(inchesSourceMultiplier*inchesSourceMultiplier);

    var gdist = Math.round(targetLen*inchesSourceG*OMAR.measure.units.precisionMapping[targetUnit])/OMAR.measure.units.precisionMapping[targetUnit];
    var dist = Math.round(targetLen*inchesSource*OMAR.measure.units.precisionMapping[targetUnit])/OMAR.measure.units.precisionMapping[targetUnit];
    var area = Math.round(targetArea*inchesSourceArea*OMAR.measure.units.precisionMapping[targetUnit])/OMAR.measure.units.precisionMapping[targetUnit];

    return {gdistance: gdist, distance:dist, area:area};
}

function displayMeasurements()
{
   var div = $("mensurationDivId");
    if(div)
    {
       var convertedValues = convertPathAreaMetersToTargetUnit(OMAR.imageManipulator.measureLengthG,
                                                               OMAR.imageManipulator.measureLength,
                                                               OMAR.imageManipulator.measureArea, 
                                                               OMAR.measure.units.active);
      
       if(OMAR.imageManipulator.measureLength)
       {
            div.innerHTML  =
            "<table>" +
            "<tr><td style='padding-left:2px'>Geodetic Dist: </td><td>" + convertedValues.gdistance + " " + OMAR.measure.units.extensionMapping[OMAR.measure.units.active] + "</td></tr>" +
            "<tr><td style='padding-left:2px'>Rect Dist: </td><td>" + convertedValues.distance + " " + OMAR.measure.units.extensionMapping[OMAR.measure.units.active] + "</td></tr>" +
            "<tr><td style='padding-left:2px'>Area: </td><td>" + convertedValues.area + " " + OMAR.measure.units.extensionMapping[OMAR.measure.units.active] + "^2</td></tr>" +
            "</table>";
       }
       else
       {
            div.innerHTML = "";
       }
    }
}
function measureRemoved(){
    OMAR.imageManipulator.measureLength = null;
    OMAR.imageManipulator.measureLengthG = null;
    OMAR.imageManipulator.measureArea    = null;
    displayMeasurements();
}
function measureFinished(evt){
   if(evt&&evt.feature)
    {
        var url = "/omar/imageSpace/measure"; 
        var request = OpenLayers.Request.POST({
             url: url,
             //params: {id:${rasterEntry.id}, feature:jsonText.toString()},
             data: YAHOO.lang.JSON.stringify({id:${rasterEntry.id}, 
                                              feature:{wkt:evt.feature.geometry.toString()}
                                             }),
             callback: function (transport){
                var temp = YAHOO.lang.JSON.parse(transport.responseText);
                OMAR.imageManipulator.measureLengthG = temp.gdist;
                OMAR.imageManipulator.measureLength  = temp.distance;
                OMAR.imageManipulator.measureArea    = temp.area;

               displayMeasurements();
            }
        });

    }
}
function resetBrightnessContrast()
{
  brightnessSlider.setRealValue(0);
  contrastSlider.setRealValue(1.0);

    //updateImage();
}

function rotateTextFieldChange(angle)
{
  if(rotateSlider)
  {
    sliderRotate(angle);
    rotateSlider.unsubscribe("change");
    rotateSlider.setRealValue(angle);
    rotateSlider.subscribe("change", function() { sliderRotate(this.getRealValue()); });

    if(OMAR.imageManipulator) OMAR.imageManipulator.applyRotate(angle);
  }
}


function setupToolbar()
{

 //  var panButton = new OpenLayers.Control.MouseDefaults({title:'Click pan button to activate. Once activated click the map and drag the mouse to pan.'});
   
    panButton = new OpenLayers.Control.Button({title: "Click to zoom in.", 
                                                id: "PAN",
                                                trigger: panMode,
                                                type:OpenLayers.Control.TYPE_TOOL,
                                                displayClass: "olControlButtonPan"});

   var selectAoi = new OpenLayers.Control.Button({title: "Click to select an AOI with an upright rectangle.", 
                                                   id: "SELECT_AOI",
                                                trigger: selectAoiClicked,
                                                type:OpenLayers.Control.TYPE_TOOL,
                                                displayClass: "olControlButtonSelectAOI"});

  var deleteAoi = new OpenLayers.Control.Button({title: "Click to zoom in.", 
                                                   id: "DELETE_AOI",
                                                trigger: deleteAoiClicked,
                                                type:OpenLayers.Control.TYPE_BUTTON,
                                                displayClass: "olControlButtonDeleteAOI"});

    var pointButton = new OpenLayers.Control.Button({title: "Click to drop a point", 
                                                   id: "POINT_BUTTON",
                                                trigger: pointModeClicked,
                                                type:OpenLayers.Control.TYPE_TOOL,
                                                displayClass: "olControlButtonMeasurePoint"});
  
    var measurePathButton = new OpenLayers.Control.Button({title: "Click to measure a path", 
                                                   id: "MEASURE_PATH",
                                                trigger: measurePathModeClicked,
                                                type:OpenLayers.Control.TYPE_TOOL,
                                                displayClass: "olControlButtonMeasurePath"});

    var measureAreaButton = new OpenLayers.Control.Button({title: "Click to measure an area",
                                                    id: "MEASURE_AREA",
                                               trigger: measureAreaModeClicked,
                                                type:OpenLayers.Control.TYPE_TOOL,
                                                displayClass: "olControlButtonMeasureArea"});

    var zoomBoxButton = new OpenLayers.Control.ZoomBox({title:"Click the zoom box button to activate. Once activated click and drag over an area of interest on the map to zoom into.",id:"ZOOM_BOX", trigger:zoomBoxClicked});
    zoomInButton = new OpenLayers.Control.Button({title: "Click to zoom in.", 
                                                 id:"ZOOM_IN",
                                                 displayClass: "olControlZoomIn", 
                                                 trigger: zoomIn});
    var zoomInFullResButton = new OpenLayers.Control.Button({title: "Click to zoom into full resolution.", displayClass: "olControlZoomToLayer", id:"ZOOM_IN_FULL", trigger: zoomInFullRes});
    var zoomOutButton = new OpenLayers.Control.Button({title: "Click to zoom out.", displayClass: "olControlZoomOut", trigger: zoomOut,
                                                      id:"ZOOM_OUT"});
    var container = $("toolBar");

    var panel = new OpenLayers.Control.Panel(
    {
       div: container,
       defaultControl: panButton,
      'displayClass': 'olControlPanel',
      activateControl: controlActivated
      });

    panel.addControls(
    [
      panButton,
      zoomBoxButton,
      zoomInButton,
      zoomOutButton,
      zoomInFullResButton,
      new OpenLayers.Control.ZoomToMaxExtent({title:"Click to zoom to the max extent."}),
      selectAoi,
      deleteAoi,
      pointButton,
      measurePathButton,
      measureAreaButton
    ]);
    map.addControl(panel);
}

function setMapCtrTxt()
{
    //var center = mapWidget.getMap().getCenter();
    
   // $("ddMapCtr").value = center.lat + ", " + center.lon;
   // $("dmsMapCtr").value = coordConvert.ddToDms(center.lat, center.lon);
   // $("point").value = coordConvert.ddToMgrs(center.lat, center.lon);
}


function setMapCtr(unit, value)
{
    var lat, lon;

    if(unit == "dd") {
        
        if($("ddMapCtr").value.match(OMAR.ddRegExp)) {
            var match = OMAR.ddRegExp.exec( $( "ddMapCtr" ).value );
            lat = match[1] + match[2];
            lon = match[3] + match[4];



          //  var center = new OpenLayers.LonLat( lon, lat );
            
          //  mapWidget.getMap().setCenter(center, mapWidget.getMap().getZoom());
        }
        else {
            alert("Invalid DD Input.");
            return;
        }
    }
    else if(unit == "dms") {
        if($("dmsMapCtr").value.match(OMAR.dmsRegExp)) {
            var match = OMAR.dmsRegExp.exec( $( "dmsMapCtr" ).value );
            lat = OMAR.coordConvert.dmsToDd( match[1], match[2], match[3] + match[4], match[5] );
            lon = OMAR.coordConvert.dmsToDd( match[6], match[7], match[8] + match[9], match[10] );
            //var center = new OpenLayers.LonLat( lon, lat );
            
           // mapWidget.getMap().setCenter(center, mapWidget.getMap().getZoom());
        }
        else {
            alert("Invalid DMS Input.");
            return;
        }
    }
    else if(unit == "mgrs")
    {
        if($("point").value.match(OMAR.mgrsRegExp)) {
            var match = OMAR.mgrsRegExp.exec( $( "point" ).value );
            var mgrs = OMAR.coordConvert.mgrsToDd( match[1], match[2], match[3], match[4], match[5], match[6] );
            var match2 = OMAR.ddRegExp.exec( mgrs );
            lat = match2[1] + match2[2];
            lon = match2[3] + match2[4];
            //var center = new OpenLayers.LonLat( lon, lat );
            
           // mapWidget.getMap().setCenter(center, mapWidget.getMap().getZoom());
        }
        else {
            alert("Invalid MGRS Input.");
            return;
        }
    }
    if(lat&&lon)
    {
        var url = "/omar/imageSpace/groundToImage";
        var request = OpenLayers.Request.POST({
                      url:url,
                      data: YAHOO.lang.JSON.stringify({id:${rasterEntry.id}, 
                                                      groundPoints:[{"lat":lat, "lon":lon}]
                                                     }),
                     callback: function (transport){
                        var temp = YAHOO.lang.JSON.parse(transport.responseText);
                        var out = document.getElementById("mouseDisplayId");
                        OMAR.imageManipulator.setCenterGivenImagePoint(temp[0]);
                       // alert(temp);
                    }
                });
    }
     
    // call this because the center is clamped so we need to reset the center on the
    // display just in case a user typed a number outside the bounds of the image
    setMapCtrTxt();
    
}

            function sliderRotate(sliderValue)
            {
                        rotationAngle = 360 - parseInt(sliderValue)
                        document.getElementById("rotateAngle").value = sliderValue;
                        OMAR.imageManipulator.applyRotate(sliderValue);
            }

            function theMapHasMoved()
            {
                if (initFlag == 0) { 
                    //updateImage(); 
                }
            }

            function theMapHasZoomed()
            {
                if (initFlag == 0)
                {
	              }
            }

            function zoomIn()
            {
	            map.zoomIn();
	            if(map.getZoom() >= map.getZoomForResolution(1.0, true))
	            {
		            zoomInButton.displayClass = "olControlFoo";
	            }
            }
            function zoomBoxClicked()
            {
                OMAR.imageManipulator.setToolMode(OMAR.ToolModeType.ZOOM_BOX);
            }
            function panMode()
            {
                OMAR.imageManipulator.setToolMode(OMAR.ToolModeType.PAN_ZOOM);
            }
            function measurePathModeClicked()
            {
                OMAR.imageManipulator.setToolMode(OMAR.ToolModeType.LINE);
            }
            function measureAreaModeClicked()
            {
                OMAR.imageManipulator.setToolMode(OMAR.ToolModeType.POLYGON);
            }
            function pointModeClicked()
            {
                OMAR.imageManipulator.setToolMode(OMAR.ToolModeType.POINT);
            }

            function controlActivated(control)
            {
               if (!this.active) {
                     return false;
               }


               var overlay = document.getElementById("popDivId");
               overlay.style.visibility = "hidden";
               var pointDropInfo = document.getElementById("mouseDisplayId");
               pointDropInfo.innerHTML = "";

               if (control.type == OpenLayers.Control.TYPE_BUTTON) 
               {
                 if(control.trigger) control.trigger();
                 this.redraw();
                 return;
               }
               if (control.type == OpenLayers.Control.TYPE_TOGGLE) 
               {
                 if (control.active) {
                    control.deactivate();
                 } else {
                    control.activate();
                 }
                 this.redraw();
                 return;
               }
               for (var i = 0, len = this.controls.length; i < len; i++) {
                 if (this.controls[i] != control) 
                 {
                    if (this.controls[i].type != OpenLayers.Control.TYPE_TOGGLE) 
                    {
                       this.controls[i].deactivate();
                    }
                 }
              }
              control.activate();
              if(control.trigger) control.trigger();
            }

            function deleteAoiClicked()
            {
                OMAR.imageManipulator.removeSelectionBox();
            }
            function selectAoiClicked()
            {
                OMAR.imageManipulator.setToolMode(OMAR.ToolModeType.BOX_AOI);
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
	              if(map.getZoom() < map.getZoomForResolution(1.0, true)) { zoomInButton.displayClass = "olControlZoomIn"; }
            }



          function getProjectedGround(ipt, xyPop)
          {
              var url = "/omar/imageSpace/imageToGroundFull"

              var request = OpenLayers.Request.POST({
                  url: url,
                  data: YAHOO.lang.JSON.stringify({id:${rasterEntry.id},
                                                   imagePoints:[{"x":Math.round(ipt.x), "y":Math.round(ipt.y)}]
                                                  }),
                  callback: function (transport){
                     var tmp = YAHOO.lang.JSON.parse(transport.responseText);
                     var tmpout = document.getElementById("mouseDisplayId");
                     if(tmpout)
                     {
                        tmpout.innerHTML = "<table><tr>" +
                                           "<td width='20%'>Img: (" + tmp.ellpar.x + ", "+ tmp.ellpar.y+")</td>" +
                                           "<td width='40%'>Gnd: (" + tmp.ellpar.lat + ", "+ tmp.ellpar.lon+") DD</td>" +
                                           "<td width='10%'>HAE: " + tmp.ellpar.hgt + " m </td>" +
                                           "<td width='10%'>MSL: " + tmp.ellpar.hgtMsl + " m </td>" +
                                           "<td width='10%'>" + "<i>" + tmp.ellpar.sInfo + "</i>" + "</td>" +
                                           "<td width='10%'>" + "<i>" + tmp.ellpar.type + "</i>" + "</td>"
                                           "</tr></table>";
                        drawProjectedGround(tmp, xyPop);
                     }
                  }
              });
          }


          function drawProjectedGround(pointData, xyPop)
          {
             OMAR.imageManipulator.vectorLayer.removeAllFeatures()

             var style_green = {
                 strokeColor: "#00FF00",
                 strokeOpacity: 0.6,
                 strokeWidth: 1,
                 fillColor: "#00FF00",
                 fillOpacity: 0.3
             };

             // Point mark
             height = parseFloat("${rasterEntry.height}");
             var xd = pointData.ellpar.x;
             var yd = -(pointData.ellpar.y - height);
             xPoint = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Point(xd, yd), null, null);
             OMAR.imageManipulator.vectorLayer.addFeatures([xPoint]);

             // Ellipse
             var nEll = pointData.ellpar.nELL;

             if (nEll > 0)
             {
                 var CE   = pointData.ellpar.CE;
                 var LE   = pointData.ellpar.LE;
                 var SMA  = pointData.ellpar.SMA;
                 var SMI  = pointData.ellpar.SMI;
                 var AZ   = pointData.ellpar.AZ;
                 var pLvl = pointData.ellpar.lvl;

                 var ellPnts = [];
                 for(var i=0; i<nEll; i++){
                    var obj = pointData.ellpts[i];
                    var xell = obj.xe;
                    var yell = -(obj.ye - height);
                    ellPnts.push(new OpenLayers.Geometry.Point(xell, yell));
                 }
                 linearRing = new OpenLayers.Geometry.LinearRing(ellPnts);
                 ellipse = new OpenLayers.Feature.Vector(linearRing, null, style_green);

                 smaPts = new Array(ellPnts[0], ellPnts[18]);
                 smaLineStr = new OpenLayers.Geometry.LineString(smaPts);
                 smaAxis = new OpenLayers.Feature.Vector(smaLineStr, null, style_green);

                 smiPts = new Array(ellPnts[9], ellPnts[27]);
                 smiLineStr = new OpenLayers.Geometry.LineString(smiPts);
                 smiAxis = new OpenLayers.Feature.Vector(smiLineStr, null, style_green);

                 OMAR.imageManipulator.vectorLayer.addFeatures([ellipse]);
                 OMAR.imageManipulator.vectorLayer.addFeatures([smaAxis]);
                 OMAR.imageManipulator.vectorLayer.addFeatures([smiAxis]);

             }

             // Ellipse popup
             drawEllipsePopup(xyPop, nEll);


             function drawEllipsePopup(xyPop, nEll)
             {
                // Offset from ellipse center
                var space = 20;

                // Get popup dimensions
                var overlay = document.getElementById("popDivId");
                var owid = overlay.clientWidth;
                var ohgt = overlay.clientHeight;

                // Start assuming lower right display
                var offX = regionOffX - space;
                var offY = regionOffY - space;

                // Check for right side obscured
                var rightExtent = xyPop.x - offX + owid;
                if (rightExtent > regionRight)
                    offX = regionOffX + space + owid;

                // Check for bottom obscured
                var bottomExtent = xyPop.y - offY + ohgt;
                if (bottomExtent > regionBottom)
                    offY = regionOffY + space + ohgt;

                // Upper left popup corner
                overlay.style.left = xyPop.x - offX;
                overlay.style.top  = xyPop.y - offY;

                // Load content
                if (nEll > 0)
                    overlay.innerHTML = createPointInfoForm();
                else
                    overlay.innerHTML = createNoInfoForm();

                overlay.style.visibility = "hidden";
             }

             function createPointInfoForm(){
               var theHTML = '';
               theHTML += "<h3 style='font-weight:bold;color:#BBCCFF;background-color:#003366;'>" + "PQE Summary" + "</h3><hr>";
               theHTML += "<table style='background-color:#BBB;border:0px solid black;'>";
               theHTML += "<tr><td>CE/LE</td>"  + "<td style='text-align:right;'>"+CE.toFixed(1)+"</td>"  + "<td style='text-align:right;'>/"+LE.toFixed(1)+"</td>"  + "<td style='text-align:right;'>"+"m"+"</td>";
               theHTML += "<tr><td>SMA/SMI</td>"+ "<td style='text-align:right;'>"+SMA.toFixed(1)+"</td>" + "<td style='text-align:right;'>/"+SMI.toFixed(1)+"</td>" + "<td style='text-align:right;'>"+"m"+"</td>";
               theHTML += "<tr><td>SMA AZ</td>" + "<td style='text-align:right;'>"+AZ.toFixed(1)+"</td>"  + "<td style='text-align:right;'>"+""+"</td>"             + "<td style='text-align:right;'>"+"deg"+"</td>";
               theHTML += "</table>";
               theHTML += "Probability Level: " + pLvl +"P";
               theHTML += "<hr>";
               return theHTML;
             }

             function createNoInfoForm(){
               var theHTML = '';
               theHTML += "<h3 style='font-weight:bold;color:#BBCCFF;background-color:#003366;'>" + "PQE Summary" + "</h3><hr>";
               theHTML += "No Error Ellipse Available";
               theHTML += "<hr>";
               return theHTML;
             }

          }//end drawProjectedGround


    // Toggle popup
    //   check for "p" key
    function KeyCheck(e) {
        if(OMAR.imageManipulator.toolMode == 'point')
        {
            var KeyID = (window.event) ? event.keyCode : e.keyCode;
            if (KeyID==80)  //key = p
            {
                var overlay = document.getElementById("popDivId");
                if (overlay.style.visibility == "visible")
                   overlay.style.visibility = "hidden";
                else
                   overlay.style.visibility = "visible";
            }
        }
    }


    function shareImage()
    {
        var baseURL = "${createLink(absolute: 'true', action: 'imageSpace')}";
        var layers = "${rasterEntry?.indexId}";
        var interpolation = $("interpolation").value;
        var brightness = $("brightness").value;
        var contrast = $("contrast").value;
        var sharpen_mode = $("sharpen_mode").value;
        var stretch_mode = $("stretch_mode").value;
        var stretch_mode_region = $("stretch_mode_region").value;
        var bands = $("bands").value;
	var rotate = ${"rotateAngle"}.value;
	var mapCenter = OMAR.imageManipulator.getCenterLocal();
	
	var bboxCoords = new Array();
        var bboxPixels = map.calculateBounds().toArray();
        var lowerLeft = OMAR.imageManipulator.pointToLocal({x:bboxPixels[0],y:bboxPixels[1]});
        var upperRight = OMAR.imageManipulator.pointToLocal({x:bboxPixels[2],y:bboxPixels[3]});
        
	var request = OpenLayers.Request.POST({
    		url: "${createLink( controller: 'imageSpace', action: 'imageToGround' )}",
		data: YAHOO.lang.JSON.stringify({
				id:${rasterEntry.id},
				imagePoints:[{"x":lowerLeft.x, "y":lowerLeft.y}]
		}),     
		callback: function (transport)
		{
			var temp = YAHOO.lang.JSON.parse(transport.responseText);
			bboxCoords[0] = temp[0].lon;
			bboxCoords[1] = temp[0].lat;			

			var request = OpenLayers.Request.POST({
				url: "${createLink( controller: 'imageSpace', action: 'imageToGround' )}",
				data: YAHOO.lang.JSON.stringify({
					id:${rasterEntry.id},
					imagePoints:[{"x":upperRight.x, "y":upperRight.y}]
				}),
				callback: function (transport)
				{
					var temp = YAHOO.lang.JSON.parse(transport.responseText);
					bboxCoords[2] = temp[0].lon;
					bboxCoords[3] = temp[0].lat;

					var request = OpenLayers.Request.POST({
						url: "${createLink( controller: 'imageSpace', action: 'imageToGround' )}",
						data: YAHOO.lang.JSON.stringify({
							id:${rasterEntry.id},
							imagePoints:[{"x":mapCenter.x, "y":mapCenter.y}]
						}),
						callback: function (transport)
						{
							var temp = YAHOO.lang.JSON.parse(transport.responseText);                               
							var centerLatitude = temp[0].lat;
							var centerLongitude = temp[0].lon;

		        				var shareLink = baseURL + "?" +
							"layers=" + "${rasterEntry?.indexId}" +
							"&interpolation=" + interpolation +
							"&brightness=" + brightness +
							"&contrast=" + contrast +
							"&sharpen_mode=" + sharpen_mode +
							"&stretch_mode=" + stretch_mode +
							"&strech_mode_region=" + stretch_mode_region +
							"&bands=" + bands +
							"&latitude=" + centerLatitude +
							"&longitude=" + centerLongitude +
							"&bbox=" + bboxCoords[0] + "," + bboxCoords[1] + "," + bboxCoords[2] + "," + bboxCoords[3] +
							"&rotate=" + rotate;
       				
							var popUpWindow = window.open("", "OMARImageShare", "width=400, height=50");
							popUpWindow.document.write("Copy and paste this <a href='" + shareLink + "' target='_new'>link</a> to share the image!");
						}
					});
				}
			});
		}
	});
    }


</r:script>
</body>
</html>
