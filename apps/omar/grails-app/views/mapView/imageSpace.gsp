<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: 2/7/12
  Time: 10:27 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="org.ossim.omar.ChipFormat" contentType="text/html;charset=UTF-8" %>
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
    <div id='imageIdField'>No Image ID present</div>
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
    <div id="AOI_TEMPLATES_DIV_ID" style='display: none'>
        <small>AOI:
            <g:select name="selectAoiTemplateId"
                    id="selectAoiTemplateId"
                    noSelection="['Custom':'Custom']"
                    from="${ChipFormat.list()}"
                    onclick="genAOI(this.value)">
            </g:select>
        Output Scale:<g:select
                from="${['Screen']}"
                name="aoiScaleId"
                id="aoiScaleId"
                noSelection="['Image':'Image']"
                onclick="setOutScale(this.value);genAOI(document.getElementById('selectAoiTemplateId').value)"/>
        </small>
    </div>
    <div id="busyCursor" style="position:absolute;top:-9999px;right:-9999px">
        <label id="busyCursorLabel" onclick="" style="font-weight:bold;"></label>
        <img id="busyCursorImage"  src="../images/spinner.gif"/>
    </div>
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
    <img src="${resource(plugin: 'omar', dir: 'images', file: 'north_arrow.gif')}">
</div>
 <div id="hudDivId">
 </div>

<div id="popDivId">
</div>

    <div id="map"></div>
</content>

<r:script>
 
//var hudChildDivId = document.getElementById("hudChildDivId")
var numberOfResLevels=parseInt("${rasterEntry.numberOfResLevels}");
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
var customAoi;
var onDemand = ("${onDemand}" == "true");
var currentCenterLatLon = {lat:0.0,lon:0.0};
var pqePoint = {x:0.0,y:0.0,lat:0.0,lon:0.0,hgt:0.0,hgtMsl:0.0,type:"",sInfo:"", displayUnit:"DD"}
var finishedInit = false;
var spinControl;// = new SpinControl();
var spinnerOpts = {
                lines: 13, // The number of lines to draw
                length: 8, // The length of each line
                width: 4, // The line thickness
                radius: 10, // The radius of the inner circle
                corners: 1, // Corner roundness (0..1)
                rotate: 0, // The rotation offset
                color: '#FFFFFF', // #rgb or #rrggbb
                speed: 1, // Rounds per second
                trail: 60, // Afterglow percentage
                shadow: true, // Whether to render a shadow
                hwaccel: false, // Whether to use hardware acceleration
                className: 'spinnerControl', // The CSS class to assign to the spinner
                zIndex: 2e9, // The z-index (defaults to 2000000000)
                top: 'auto', // Top position relative to parent in px
                left: 'auto' // Left position relative to parent in px
            };
var spinCount = 0;

function setImageId()
{
    var imageIdFieldEl = YAHOO.util.Dom.get("imageIdField");
    imageIdFieldEl.innerHTML = "<b>Image Id:</b> ${imageIds}";
}

function getCapabilities()
{
    window.open("${createLink( controller: 'ogc', action: 'wms', params: [request: 'GetCapabilities', layers: rasterEntry.indexId] )}");
}

function getDetailedMetadata()
{
    window.open("${createLink( controller: 'rasterEntry', action: 'show', params: [id: rasterEntry?.id] )}");
}

function getTileLog()
{
    window.open('${createLink(controller: "GetTileLog", action: "list")}');
}

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

function changeSharpenOpts()
{
    var sharpen_mode = $("sharpen_mode").value;
    layer.mergeNewParams({sharpen_mode:sharpen_mode});

    //updateImage();
}

function changeToSingleLayer()
{
	var affineM = OMAR.imageManipulator.generateOssimFullImageTransform();
	var w = Math.abs(OMAR.imageManipulator.containerDivRegion.right - OMAR.imageManipulator.containerDivRegion.left) + 1;
	var h = Math.abs(OMAR.imageManipulator.containerDivRegion.top - OMAR.imageManipulator.containerDivRegion.bottom) + 1;
	var center =  OMAR.imageManipulator.getCenterLocal();
	var pivot = Math.round(center.x) + "," + Math.round(center.y);
	var centerView = affineM.transform(center);
	var x = Math.round(centerView.x - w/2);
	var y = Math.round(centerView.y - h/2);
    var match = OMAR.ddRegExp.exec( $( "ddMapCtr" ).value );
    var lat = match[1] + match[2];
    var lon = match[3] + match[4];

    var tempWmsParams = new OmarWmsParams();

    tempWmsParams.setProperties(document);
    tempWmsParams.layers = "${rasterEntry.indexId}";
    tempWmsParams.latitude = lat;
    tempWmsParams.longitude = lon;
    tempWmsParams.view = YAHOO.lang.JSON.stringify({"lat":lat,
                                                    "lon":lon,
                                                    "mpp":OMAR.imageManipulator.calculateMetersPerPixel(),
                                                    "azimuth":OMAR.imageManipulator.calculateAzimuth()
                                                    });
    var wmsFormElement = $("wmsFormId");

    if(wmsFormElement)
    {
        var url = "${createLink(controller: 'mapView', action: 'index')}";
        wmsFormElement.action = url + "?"+tempWmsParams.toUrlParams();
        wmsFormElement.method = "POST";
        wmsFormElement.submit();
    }
    else
    {
        alert("Unable to change to single layer. Can't find wmsFormElement");
    }
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

        // AOI applies to full res image
        if (OMAR.imageManipulator.outScaleAOI == 'Image')
        {
            w = w/scale;
            h = h/scale;
            x = Math.round(centerView.x/scale-w/2);
            y = Math.round(centerView.y/scale-h/2);
            scale = 1;
        }

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

function unitsChanged(value)
{
   OMAR.measure.units.active = value;
    displayMeasurements();

}
function loadUnitSelection()
{
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


function toolModeChanged()
{
    var el = document.getElementById("AOI_TEMPLATES_DIV_ID");
    if(OMAR.imageManipulator.toolMode == OMAR.ToolModeType.BOX_AOI)
    {
        el.style.display = "block";
    }
    else
    {
        el.style.display = "none";
    }
}

function aoiFinished()
{

    customAoi.w = parseInt(OMAR.imageManipulator.selectionBox.style.width);
    customAoi.h = parseInt(OMAR.imageManipulator.selectionBox.style.height);

    var selectAoiTemplateEl = document.getElementById("selectAoiTemplateId");
    var aoiScaleEl = document.getElementById("aoiScaleId");

    if( selectAoiTemplateEl&&aoiScaleEl)
    {
        selectAoiTemplateEl.value = "Custom";
        aoiScaleEl.value = "Screen";
        OMAR.imageManipulator.outScaleAOI =  aoiScaleEl.value
    }
    else
    {
        alert("Element id's are not found: selectAoiTemplateId and aoiScaleId");
    }
}
function setCurrentPqeDisplayUnitSelection()
{
    var pqeSelectionEl = YAHOO.util.Dom.get("pqeDisplayUnit");
    if(pqeSelectionEl)
    {
        pqeSelectionEl.value = pqePoint.displayUnit;
    }
}

function allocateControls()
{
}

function loadStart()
{
    spinCount = spinCount+1;
    if(spinCount==1)
    {
        spin();
    }
}

function loadEnd()
{
    spinCount=spinCount-1;
    if((spinCount < 1)&&spinControl)
    {
        spinControl.stop();
        spinCount = 0;
    }
}

function spin(){
    var targetDiv = YAHOO.util.Dom.get("hudDivId");
    if(spinControl)
    {
        spinControl.spin(targetDiv);
    }
    else
    {
        spinControl = new Spinner(spinnerOpts).spin(targetDiv);
    }
}
function init(mapWidth, mapHeight)
{
   //mapDiv.style.display = "block";
    setImageId();
    OMAR.coordConvert = new CoordinateConversion();
    pqePoint.displayUnit = "${pqeDisplayUnit?:"DMS"}"
    setCurrentPqeDisplayUnitSelection();

    customAoi = {w:256, h:256}
    loadUnitSelection();
    OMAR.imageManipulator = new OMAR.OpenLayersImageManipulator({metersPerPixelFullRes:${rasterEntry.gsdY}});
    //OMAR.imageManipulator.click = function(evt)
    //{
    //   getCoordinates(OMAR.imageManipulator.pointToLocal(this.mouseToPoint(evt)));
   // }

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
    var defaultResolutions = [1.0,.5,.25,.125,.0625 ];
    var resolutions = [];
    var width  = parseFloat("${rasterEntry.width}");
    var height = parseFloat("${rasterEntry.height}");
    //var url = "${createLink(controller: 'imageSpace', action: 'getTileOpenLayers')}";
    var url = "${createLink(controller: 'imageSpace', action: 'getTile')}";
    var bounds = new OpenLayers.Bounds(0, 0, width, height);
    var idx = 0;
    var currentLevel = resLevels;

    for(idx = resLevels-1; idx > -8; --idx)
    {
        resolutions.push(Math.pow(2, idx));
    }
    map = new OpenLayers.Map("map", { controls:[],
                             theme: null,
                             buffer:0,
                             maxExtent:bounds,
                             resolutions:resolutions
                             //maxResolution: 16,
                             //numZoomLevels:numZoomLevels
                             });

map.events.manipulator = OMAR.imageManipulator;
    var options = {
         controls: [],
         buffer:0,
         maxExtent: bounds,
         getURL: getTileUrl,
         isBaseLayer: true,
         maxResolution: (width) / map.getTileSize().w,
         transitionEffect: "resize",
         units:'pixel',
         singleTile:true,
         format: format
      }

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

    layer = new OpenLayers.Layer.TMS( "Image Space Viewer",
                                       url,
                                       options);
    layer.events.on({
        loadstart:loadStart,
        loadend:loadEnd
    });

map.addLayer(layer);
    map.setBaseLayer(layer);
// set the initialization flag so the moveend and zoomend code can execute
    initFlag = 0;

    var offsetX = width/2.0;
    var offsetY = height/2.0;

//map.setCenter(new  OpenLayers.LonLat(0.0,0.0), 0);

<%--
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
    --%>
<%--
	<g:if test="${params.bbox != null}">
		bboxToPixel();
	</g:if>
--%>

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
   //OMAR.imageManipulator.setToolMode(OMAR.ToolModeType.PAN_ZOOM);
   //alert(map.getMaxExtents());
OMAR.imageManipulator.events.on({
            "onFeatureDone": measureFinished,
            "onFeatureRemoved" : measureRemoved,
            "onToolModeChanged" : toolModeChanged,
            "onAoiFinished"     : aoiFinished,
            "onScaleChanged"    : scaleChanged,
            "onDragFinished"     : dragFinished,
            "click" : mouseClick
    });


    // Key press check for error ellipse popup
    document.onkeyup = keyCheck;

    // AOI chip scale
    OMAR.imageManipulator.outScaleAOI = "Image";

    // Offset for error ellipse popup positioning
    var region = YAHOO.util.Region.getRegion(OMAR.imageManipulator.containerDiv);
    regionOffX = region.left - 1;
    regionOffY = region.top - 1;
    regionRight = region.right - regionOffX;
    regionBottom = region.bottom - regionOffY;

    // Probability level for PQE
    currProbLevel = 0.9;

    rotateSlider.setRealValue(rotationAngle);
//    updateCenter();
    var lat = ${params.latitude?:"null"};
    var lon = ${params.longitude?:"null"};
    var view = ${params.view?:"null"};
    var url = "/omar/imageSpace/groundToImage";

    if(view)
    {
        lat = view.lat;
        lon = view.lon;

        if(view.azimuth != null)
        {
            rotateSlider.setRealValue(view.azimuth + OMAR.imageManipulator.northAngle);
        }
    }
    if(lat&&lon)
    {
        var request = OpenLayers.Request.POST({
            url:url,
            data: YAHOO.lang.JSON.stringify({
            id:${rasterEntry.id},
            groundPoints:[{"lat":lat, "lon":lon}]
            }),
            callback: function (transport)
            {
                var view = ${params.view?:"null"};
                var temp = YAHOO.lang.JSON.parse(transport.responseText);
                var zoom = OMAR.imageManipulator.findZoomForMetersPerPixel(view.mpp);
                OMAR.imageManipulator.setCenterGivenImagePoint(temp[0], zoom);
		        updateCenter();
            }
           });
    }
    setupOverviewCheck();
   // mapDiv.style.display = "inline";
    if(numberOfResLevels <2)
    {
        map.zoomTo(.5);
    }
    else
    {
        map.zoomToExtent(bounds,{closest:true});
    }
    finishedInit = true;
    updateCenter();


}

function setupOverviewCheck(){
    if(!onDemand) return;
    if(numberOfResLevels <2)
    {
        var label = YAHOO.util.Dom.get("busyCursorLabel");
        YAHOO.util.Dom.setStyle("busyCursor", "top", "5px");
        YAHOO.util.Dom.setStyle("busyCursor", "right", "12px");
        YAHOO.util.Dom.setStyle("busyCursorImage", "display", "inline");

        YAHOO.util.Dom.setStyle("busyCursor", "display", "inline");
        YAHOO.util.Dom.setStyle("busyCursorLabel", "display", "inline");
        YAHOO.util.Dom.setStyle("busyCursorImage", "display", "inline");
        label.innerHTML = "Generating overviews...";
        setTimeout(checkOverview, 5000);
    }
}

function checkOverview(){
    var url = "/omar/rasterEntryExport/export";

    var request = OpenLayers.Request.POST({
                        url: url+"?id=${rasterEntry.id}&max=1&format=json&fields=numberOfResLevels&labels=rlevels",
                        callback: function (transport){
                            var temp = YAHOO.lang.JSON.parse(transport.responseText);
                            if(temp&&temp.length>0)
                            {
                                var ok = true;
                                var idx = 0;
                                for(idx=0; idx < temp.length;++idx)
                                {
                                    if(parseInt(temp[idx].rlevels) < 2)
                                    {
                                        ok = false
                                    }
                                }
                                if(ok)
                                {
                                    var label = YAHOO.util.Dom.get("busyCursorLabel");
                                    YAHOO.util.Dom.setStyle("busyCursorImage", "display", "none");
                                    YAHOO.util.Dom.setStyle("busyCursorLabel", "text-decoration", "underline");
                                    label.innerHTML = "Click now to refresh";
                                    label.onclick=function(){window.location = window.location};
                                }
                                else
                                {
                                    setTimeout(checkOverview, 5000);
                                }
                            } // endif
                        } // callback
                  });
}

function mouseClick(evt){

    // point drop with error propagation (PQE)
    if(OMAR.imageManipulator.toolMode == OMAR.ToolModeType.POINT)
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

    var precision = OMAR.measure.units.precisionMapping[targetUnit];
    var gdist = Math.round(targetLen*inchesSourceG*precision)/precision;
    var dist = Math.round(targetLen*inchesSource*precision)/precision;
    var area = Math.round(targetArea*inchesSourceArea*precision)/precision;

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
      
        switch(OMAR.imageManipulator.toolMode)
        {
            case OMAR.ToolModeType.LINE:
            {
                if(OMAR.imageManipulator.measureLength)
                {
                    div.innerHTML  =
                    "<table>" +
                    "<tr><td style='padding-left:2px'>Geodetic Dist: </td><td>" + convertedValues.gdistance + " " + OMAR.measure.units.extensionMapping[OMAR.measure.units.active] + "</td></tr>" +
                    "<tr><td style='padding-left:2px'>Rect Dist: </td><td>" + convertedValues.distance + " " + OMAR.measure.units.extensionMapping[OMAR.measure.units.active] + "</td></tr>" +
                    "<tr><td style='padding-left:2px'><hr></td><td><hr></td></tr>" +
                    "<tr><td style='padding-left:2px'>Azimuth: </td><td>" + OMAR.imageManipulator.measureAzimuth.toFixed(1) + " deg" + "</td></tr>" +
                    "</table>";
                }
                break;
            }
            case OMAR.ToolModeType.POLYGON:
            {
                if(OMAR.imageManipulator.measureArea)
                {
                    div.innerHTML  =
                    "<table>" +
                    "<tr><td style='padding-left:2px'>Geodetic Dist: </td><td>" + convertedValues.gdistance + " " + OMAR.measure.units.extensionMapping[OMAR.measure.units.active] + "</td></tr>" +
                    "<tr><td style='padding-left:2px'>Rect Dist: </td><td>" + convertedValues.distance + " " + OMAR.measure.units.extensionMapping[OMAR.measure.units.active] + "</td></tr>" +
                    "<tr><td style='padding-left:2px'>Area: </td><td>" + convertedValues.area + " " + OMAR.measure.units.extensionMapping[OMAR.measure.units.active] + "^2</td></tr>" +
                    "</table>";
                }
                break;
            }
            default:
                div.innerHTML = "";
        }
    }
}

function measureRemoved(){
    OMAR.imageManipulator.measureLength  = null;
    OMAR.imageManipulator.measureLengthG = null;
    OMAR.imageManipulator.measureArea    = null;
    OMAR.imageManipulator.measureAzimuth = null;
    displayMeasurements();
}

function measureFinished(evt){
   if(evt && evt.feature)
    {
        var url = "/omar/imageSpace/measure";

        // Convert WKT lineString to true image coordinates
        var pathString = evt.feature.geometry.toString();
        var path = new OpenLayers.Format.WKT().read(pathString);
        var nodes = path.geometry.getVertices();
        var pts = [];
        for (var i=0; i<nodes.length; i++) {
            var pt = OMAR.imageManipulator.pointReflect(nodes[i]);
            pts[i] = new OpenLayers.Geometry.Point(pt.x, pt.y);
        }
        var feat;
        if (pathString.indexOf("POLYGON") >= 0)
        {
            var ring = new OpenLayers.Geometry.LinearRing(pts);
            feat = new OpenLayers.Geometry.Polygon(ring);
        }
        else
            feat = new OpenLayers.Geometry.LineString(pts);

        var request = OpenLayers.Request.POST({
             url: url,
             data: YAHOO.lang.JSON.stringify({id:${rasterEntry.id}, feature:{wkt:feat.toString()}}),
             callback: function (transport){
                var temp = YAHOO.lang.JSON.parse(transport.responseText);
                OMAR.imageManipulator.measureLengthG = temp.gdist;
                OMAR.imageManipulator.measureLength  = temp.distance;
                OMAR.imageManipulator.measureArea    = temp.area;
                OMAR.imageManipulator.measureAzimuth = temp.azimuth * 180/Math.PI;

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
                                                displayClass: "olControlDragPan"});

   var selectAoi = new OpenLayers.Control.Button({title: "Click to drag/select an AOI with an upright rectangle.",
                                                   id: "SELECT_AOI",
                                                trigger: selectAoiClicked,
                                                type:OpenLayers.Control.TYPE_TOOL,
                                                displayClass: "olControlDrawFeature"});

  var deleteAoi = new OpenLayers.Control.Button({title: "Click to clear AOI.",
                                                   id: "DELETE_AOI",
                                                trigger: deleteAoiClicked,
                                                type:OpenLayers.Control.TYPE_BUTTON,
                                                displayClass: "olControlClearAreaOfInterest"});

    var pointButton = new OpenLayers.Control.Button({title: "Click to drop a point", 
                                                   id: "POINT_BUTTON",
                                                trigger: pointModeClicked,
                                                type:OpenLayers.Control.TYPE_TOOL,
                                                displayClass: "olControlButtonMeasurePoint"});
  
    var measurePathButton = new OpenLayers.Control.Button({title: "Click to measure a path", 
                                                   id: "MEASURE_PATH",
                                                trigger: measurePathModeClicked,
                                                type:OpenLayers.Control.TYPE_TOOL,
                                                displayClass: "olControlMeasureDistance"});

    var measureAreaButton = new OpenLayers.Control.Button({title: "Click to measure an area",
                                                    id: "MEASURE_AREA",
                                               trigger: measureAreaModeClicked,
                                                type:OpenLayers.Control.TYPE_TOOL,
                                                displayClass: "olControlMeasureArea"});

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

    var zoomToMaxExtentButton = new OpenLayers.Control.ZoomToMaxExtent({title:"Click to zoom to the max extent.",id:"ZOOM_MAX_EXTENT", trigger:zoomMaxExtentClicked});

panel.addControls(
[
panButton,
zoomBoxButton,
zoomInButton,
zoomOutButton,
zoomInFullResButton,
zoomToMaxExtentButton,
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
// $("dmsMapCtr").value = OMAR.coordConvert.ddToDms(center.lat, center.lon);
// $("point").value = OMAR.coordConvert.ddToMgrs(center.lat, center.lon);
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
	            updateAOI();
            }
            function zoomBoxClicked()
            {
                OMAR.imageManipulator.setToolMode(OMAR.ToolModeType.ZOOM_BOX);
            }
            function zoomMaxExtentClicked()
            {
                map.zoomToMaxExtent();
                if (OMAR.imageManipulator.outScaleAOI == 'Image')
                {
                    //OMAR.imageManipulator.removeSelectionBox();
                }
                genAOI(document.getElementById("selectAoiTemplateId").value);

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
                var zoom = map.getZoomForResolution(1.0, true);
                map.zoomTo(zoom);
	            zoomInButton.displayClass = "olControlFoo";
	            updateAOI();
            }

            function zoomOut()
            {
                map.zoomOut();
	              if(map.getZoom() < map.getZoomForResolution(1.0, true)) { zoomInButton.displayClass = "olControlZoomIn"; }
	            updateAOI();
            }

          function formatPqePoint()
          {
                switch(pqePoint.displayUnit)
                {
                    case "MGRS":
                    {
                        return OMAR.coordConvert.ddToMgrs(pqePoint.lat, pqePoint.lon);
                    }
                    case "DMS":
                    {
                        return OMAR.coordConvert.ddToDms(pqePoint.lat, pqePoint.lon);
                    }
                    case "DD":
                    {
                        return (pqePoint.lat + ", "+ pqePoint.lon);
                    }

                }
          }
          function updateStatusLine()
          {
            var tmpout = document.getElementById("mouseDisplayId");


            if(tmpout&&pqePoint&&pqePoint.x&&pqePoint.lon)
            {
tmpout.innerHTML = "<table><tr>" +
    "<td width='20%'>Img: (" + Math.round(pqePoint.x*1000.0)/1000.0 + ", "+ Math.round(pqePoint.y*1000.0)/1000.0+")</td>" +
    "<td width='40%'>Gnd: (" + formatPqePoint() +") "+  pqePoint.displayUnit + "</td>" +
    "<td width='10%'>HAE: " + Math.round(pqePoint.hgt*10.0)/10.0 + " m </td>" +
    "<td width='10%'>MSL: " + Math.round(pqePoint.hgtMsl*10.0)/10.0 + " m </td>" +
    "<td width='10%'>" + "<i>" + pqePoint.sInfo + "</i>" + "</td>" +
    "<td width='10%'>" + "<i>" + pqePoint.type + "</i>" + "</td>"
    "</tr></table>";
              }
          }
          function getProjectedGround(ipt, xyPop)
          {

              var url = "/omar/imageSpace/imageToGroundFull"

              var request = OpenLayers.Request.POST({
                  url: url,
                  data: YAHOO.lang.JSON.stringify({id:${rasterEntry.id},
                    imagePoints:[{"x":ipt.x, "y":ipt.y}],
                    pLevel:currProbLevel
                                                  }),
                  callback: function (transport){
                     var tmp = YAHOO.lang.JSON.parse(transport.responseText);
                    pqePoint.x = tmp.ellpar.x;
                    pqePoint.y = tmp.ellpar.y;
                    pqePoint.lat = tmp.ellpar.lat;
                    pqePoint.lon = tmp.ellpar.lon;
                    pqePoint.hgt = tmp.ellpar.hgt;
                    pqePoint.hgtMsl = tmp.ellpar.hgtMsl;
                    pqePoint.sInfo = tmp.ellpar.sInfo;
                    pqePoint.type = tmp.ellpar.type;
                    updateStatusLine();
                     if(tmp&&tmp.ellpar&&tmp.ellpar.lon)
                     {
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

                 // Left side PQE div
                 var pqeBox = document.getElementById("pqeDivId");
                 pqeBox.innerHTML = createPointInfoForm();
             }

             function createPointInfoForm(){
               var theHTML = '';
               //theHTML += "<h3 style='font-weight:bold;color:#BBCCFF;background-color:#003366;'>" + "Summary" + "</h3><hr>";
               theHTML += "<table style='background-color:#F0F8FF;border:0px solid black'>";
               theHTML += "<tr><td>CE/LE</td>"  + "<td style='text-align:right;'>"+CE.toFixed(1)+"</td>"  + "<td style='text-align:right;'>/"+LE.toFixed(1)+"</td>"  + "<td style='text-align:right;'>"+"m"+"</td>";
               theHTML += "<tr><td>SMA/SMI</td>"+ "<td style='text-align:right;'>"+SMA.toFixed(1)+"</td>" + "<td style='text-align:right;'>/"+SMI.toFixed(1)+"</td>" + "<td style='text-align:right;'>"+"m"+"</td>";
               theHTML += "<tr><td>SMA AZ</td>" + "<td style='text-align:right;'>"+AZ.toFixed(1)+"</td>"  + "<td style='text-align:right;'>"+""+"</td>"             + "<td style='text-align:right;'>"+"deg"+"</td>";
               theHTML += "</table>";
               theHTML += "Probability Level: " + pLvl +"P";
               theHTML += "<hr>";
               return theHTML;
             }


          }//end drawProjectedGround

    function setPropLevel(pLev)
    {
        currProbLevel = pLev.replace("P","");



var feats = OMAR.imageManipulator.vectorLayer.features
        var pt = feats[0].geometry;
        var xd = pt.x;
        var yd = height - pt.y;

        var xyPop = new OmarPoint(xd, yd);
        var point = new OmarPoint(xd, yd);


        getProjectedGround(point, xyPop);
    }


    // Toggle PQE popup
    //   check for "P" key
    function keyCheck(e) {
        var keyID = (window.event) ? event.keyCode : e.keyCode;

        if(OMAR.imageManipulator.toolMode == 'point')
        {
            if (keyID==80)  //key = P
            {
// Popup disabled
//                var overlay = document.getElementById("popDivId");
//                if (overlay.style.visibility == "visible")
//                   overlay.style.visibility = "hidden";
//                else
//                   overlay.style.visibility = "visible";
            }
        }
    }

    function getSelectionBoxDimensions()
    {
        if(OMAR.imageManipulator.selectionBox)
        {
            var x = parseFloat(OMAR.imageManipulator.selectionBox.style.left);
            var y = parseFloat(OMAR.imageManipulator.selectionBox.style.top);
            var w = parseFloat(OMAR.imageManipulator.selectionBox.style.width);
            var h = parseFloat(OMAR.imageManipulator.selectionBox.style.height);
            return {x:x, y:y, w:w, h:h, centerX:(x+w/2.0), centerY: (y+h/2.0)}
        }
        return null;
    }
    function scaleChanged()
    {
        if(OMAR.imageManipulator.toolMode == OMAR.ToolModeType.BOX_AOI)
        {
            OMAR.imageManipulator.removeSelectionBox();

            genAOI(document.getElementById("selectAoiTemplateId").value);
            //if(getSelectionBoxDimensions())
            //{
                //genAOI(document.getElementById("selectAoiTemplateId").value);
            //}
        }
        updateCenter();
    }
    function dragFinished()
    {
       updateCenter();
    }

    function setMapCtrTxt(center)
    {
        $("ddMapCtr").value = center.lat + ", " + center.lon;
        $("dmsMapCtr").value = OMAR.coordConvert.ddToDms(center.lat, center.lon);
        $("point").value = OMAR.coordConvert.ddToMgrs(center.lat, center.lon);
    }
 /*
    function setMapCtr(unit, value) {
        if(unit == "dd")
        {

        if($("ddMapCtr").value.match(ddRegExp)) {
        var match = OMAR.ddRegExp.exec( $( "ddMapCtr" ).value );
        var lat = match[1] + match[2];
        var lon = match[3] + match[4];
        var center = new OpenLayers.LonLat( lon, lat );

        mapWidget.getMap().setCenter(center, mapWidget.getMap().getZoom());
        }
        else {
        alert("Invalid DD Input.");
        }
        }
        else if(unit == "dms") {
        if($("dmsMapCtr").value.match(dmsRegExp)) {
        var match = OMAR.dmsRegExp.exec( $( "dmsMapCtr" ).value );
        var lat = OMAR.coordConvert.dmsToDd( match[1], match[2], match[3] + match[4], match[5] );
        var lon = OMAR.coordConvert.dmsToDd( match[6], match[7], match[8] + match[9], match[10] );
        var center = new OpenLayers.LonLat( lon, lat );

        mapWidget.getMap().setCenter(center, mapWidget.getMap().getZoom());
    }
    else {
        alert("Invalid DMS Input.");
        }
    }
    else if(unit == "mgrs")
    {
    if($("point").value.match(mgrsRegExp)) {
    var match = OMAR.mgrsRegExp.exec( $( "point" ).value );
    var mgrs = OMAR.coordConvert.mgrsToDd( match[1], match[2], match[3], match[4], match[5], match[6] );
    var match2 = OMAR.ddRegExp.exec( mgrs );
    var lat = match2[1] + match2[2];
    var lon = match2[3] + match2[4];
    var center = new OpenLayers.LonLat( lon, lat );

    mapWidget.getMap().setCenter(center, mapWidget.getMap().getZoom());
    }
    else {
    alert("Invalid MGRS Input.");
    }
    }

    // call this because the center is clamped so we need to reset the center on the
    // display just in case a user typed a number outside the bounds of the image
    setMapCtrTxt();
    }
 */

function updateCenter()
    {
       imageToGround(OMAR.imageManipulator.getCenterLocal(),
                     function(transport){
                        var tmp = YAHOO.lang.JSON.parse(transport.responseText);
                        currentCenterLatLon = tmp[0];
                        setMapCtrTxt(tmp[0]);
                     }
                    );

    }
    function genAOI(dimensionsAOI)
    {
        var currentDimensions = getSelectionBoxDimensions();
//OMAR.imageManipulator.setToolMode(OMAR.ToolModeType.BOX_AOI);
        if (OMAR.imageManipulator.toolMode == OMAR.ToolModeType.BOX_AOI)
        {
            //OMAR.imageManipulator.removeSelectionBox();
            // Handle "Custom" selection
            if (dimensionsAOI == "Custom")
            {
                dimensionsAOI = prompt("Enter AOI dimensions (wxh)",customAoi.w+"x"+customAoi.h);
                dimensionsAOI = " :" + dimensionsAOI;
            }

            // Parse dimensions
            var labDim = dimensionsAOI.split(":");
            var dimxy = labDim[1].split("x");

            if (dimxy.length == 2)
            {
                var region = YAHOO.util.Region.getRegion(OMAR.imageManipulator.annotationDiv);
                var centerX = (region.right - region.left)/2;
                var centerY = (region.bottom - region.top)/2;

                var factor = 1.0;

                // AOI applies to full res image
                if (OMAR.imageManipulator.outScaleAOI == 'Image')
                {
                    factor = OMAR.imageManipulator.map.getResolution();
                }

                var dimx = dimxy[0]/factor;
                var dimy = dimxy[1]/factor;
                var offx = centerX - dimx/2;
                var offy = centerY - dimy/2;

                if(!currentDimensions)
                {
                    OMAR.imageManipulator.selectionBox = OpenLayers.Util.createDiv('selectionBox',
                                                        {x:offx,y:offy}, {w:dimx,h:dimy}, null, "absolute", null, null, .5);
                    OMAR.imageManipulator.selectionBox.style.backgroundColor = "orange";
                    OMAR.imageManipulator.selectionBox.style.fontSize = "1px";
                    OMAR.imageManipulator.selectionBox.style.zIndex = OMAR.imageManipulator.map.Z_INDEX_BASE["Popup"] - 1;
                    if(OMAR.imageManipulator.annotationDiv!=null)
                    {
                        OMAR.imageManipulator.annotationDiv.appendChild(OMAR.imageManipulator.selectionBox);
                    }
                }
                else
                {
                    OpenLayers.Util.modifyDOMElement(OMAR.imageManipulator.selectionBox,
                    null,    // id
                    {x:currentDimensions.centerX-dimx/2.0, y:currentDimensions.centerY-dimy/2.0}, // x,y
                    {w:dimx, h:dimy}, // width, height
                    null,            //position
                    null,
                    null,
                    null);
                }
            }
        }
    }

    function updateAOI()
    {
    }

    function setOutScale(scaleAOI)
    {
        OMAR.imageManipulator.outScaleAOI = scaleAOI;
    }

    function imageToGround(ipt, callbackRoutine)
    {
        var isArray =  (ipt instanceof Array);
        var imagePointArray = []
        if(isArray)
        {
            imagePointArray = ipt;
        }
        else
        {
            imagePointArray = [{"x":ipt.x, "y":ipt.y}];
        }
        var request = OpenLayers.Request.POST({
                url: "${createLink( controller: 'imageSpace', action: 'imageToGround' )}",
                data: YAHOO.lang.JSON.stringify({
                    id:${rasterEntry.id},
                    imagePoints: imagePointArray
                }),
                callback: callbackRoutine
            });


    }
    function shareImage()
    {
        var center =  OMAR.imageManipulator.getCenterLocal();
        var baseURL = "${createLink(absolute: 'true', action: 'imageSpace', base: grailsApplication.config.omar.serverURL)}";
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

        var affineM = OMAR.imageManipulator.generateOssimFullImageTransform();
        var w = Math.abs(OMAR.imageManipulator.containerDivRegion.right - OMAR.imageManipulator.containerDivRegion.left) + 1;
        var h = Math.abs(OMAR.imageManipulator.containerDivRegion.top - OMAR.imageManipulator.containerDivRegion.bottom) + 1;
        var pivot = Math.round(center.x) + "," + Math.round(center.y);
        var centerView = affineM.transform(center);
        var x = Math.round(centerView.x - w/2);
        var y = Math.round(centerView.y - h/2);


        var tempWmsParams = new OmarWmsParams();
        tempWmsParams.setProperties(document);
        tempWmsParams.layers = "${rasterEntry.indexId}";
        tempWmsParams.view = YAHOO.lang.JSON.stringify({"lat":currentCenterLatLon.lat,
                                                        "lon":currentCenterLatLon.lon,
                                                        "mpp":OMAR.imageManipulator.calculateMetersPerPixel(),
                                                        "azimuth":OMAR.imageManipulator.calculateAzimuth()
                                                        });
        var shareLink = baseURL + "?" + tempWmsParams.toUrlParams() + "&rotate=" + rotate;
        window.prompt ("Copy to clipboard: Ctrl+C, Enter", shareLink);

//var popUpWindow = window.open("", "OMARImageShare", "width=400, height=50");
//popUpWindow.document.write("Copy and paste this <a href='" + shareLink + "' target='_new'>link</a> to share the image!");
//popUpWindow.document.close();


    }
    function setCurrentPqeDisplayUnit(value)
    {
        pqePoint.displayUnit = value;
        updateStatusLine();
    }
    function exportTemplate()
    {		
	var centerLatitude = currentCenterLatLon.lat;
	var centerLongitude = currentCenterLatLon.lon;
	var dms = OMAR.coordConvert.ddToDms(centerLatitude, centerLongitude);
	var mgrs = OMAR.coordConvert.ddToMgrs(centerLatitude, centerLongitude);
	var centerGeo = "GEO: " + dms + " MGRS: " + mgrs;

	var acquisitionDate = "${rasterEntry.acquisitionDate}";
	var countryCode = "${rasterEntry.countryCode}";
	var imageId = "${rasterEntry.title}";
	var northArrowAngle = parseFloat(${"rotateAngle"}.value) - parseFloat("${rasterEntry.azimuthAngle}");

	var res = OMAR.imageManipulator.map.getResolution();
	var scale = 1.0/res;
	var affineM = OMAR.imageManipulator.generateOssimFullImageTransform();

	var w = Math.abs(OMAR.imageManipulator.containerDivRegion.right - OMAR.imageManipulator.containerDivRegion.left) + 1;
	var h = Math.abs(OMAR.imageManipulator.containerDivRegion.top - OMAR.imageManipulator.containerDivRegion.bottom) + 1;
	var center =  OMAR.imageManipulator.getCenterLocal();
	var pivot = Math.round(center.x) + "," + Math.round(center.y);
	var centerView = affineM.transform(center);
	var x = Math.round(centerView.x-w/2);
	var y = Math.round(centerView.y-h/2);
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
		'format':"image/png",
		'id' : "${rasterEntry?.id}",
		'pivot' : pivot
	});

	var imageURL  = "${createLink(absolute: 'true', controller: 'imageSpace', action: 'getTile')}" + "?" + params.toUrlParams();
	imageURL = imageURL.replace(/&/g,"%26");
	var templateURL = "${createLink( controller: 'templateExport', action: 'index', plugin: 'omar-image-magick')}" + "?acquisitionDate=" + acquisitionDate + "&countryCode=" + countryCode + "&imageId=" + imageId + "&imageURL=" + imageURL + "&centerGeo=" + centerGeo + "&northArrowAngle=" + northArrowAngle;
	window.open(templateURL);		
    }

</r:script>
</body>
</html>
