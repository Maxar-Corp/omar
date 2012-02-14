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
  <table><tr>
    <td width="33%"><div id="ddMousePosition">&nbsp;</div></td>
    <td width="33%"><div id="dmsMousePosition">&nbsp;</div></td>
    <td width="33%"><div id="mgrsMousePosition">&nbsp;</div></td>
  </tr></table>

</content>

<content tag="center2">
  <div id="map"><div id="compassMap" style="position: absolute; z-index: 1000"></div></div>
</content>

<r:script>
            var northAngle = parseFloat("${rasterEntry.azimuthAngle}");
            var upIsUpRotation   =  parseFloat("${upIsUpRotation}");
            var brightnessSlider = YAHOO.widget.Slider.getHorizSlider("slider-brightness-bg",  "slider-brightness-thumb", 0, 100, 1);
            var compassImage;
            var compassMap;
            var compassVectorLayer;
            var contrastSlider = YAHOO.widget.Slider.getHorizSlider("slider-contrast-bg",  "slider-contrast-thumb", 0, 100, 1);
            var currentMapCenterX;
            var currentMapCenterY;
            var format = "image/jpeg";
            var hyp;
            var image;
            var imageBounds;
            var imageURL;
            var initFlag = 1;
            var imageHypotenuse;
            var imageVectorLayer;
            var layer;
            var map;
            var newImageCenterX;
            var newImageCenterY;
            var oldImageCenterX;
            var oldImageCenterY;
            var oldMapCenterX;
            var oldMapCenterY;
            var oldZoomLevel;
            var omarImageSpaceOpenLayersParams = new  OmarImageSpaceOpenLayersParams();
            var resLevels = parseFloat("${rasterEntry.numberOfResLevels}");
            var rotateSlider = YAHOO.widget.Slider.getHorizSlider("slider-rotate-bg",  "slider-rotate-thumb", 0, 180, 1);
            var rotationAngle = ${"rotateAngle"}.value;
            var zoomInButton;
            function changeBandsOpts()
            {
                var bands = $("bands").value;
                layer.mergeNewParams({bands:bands});

                updateImage();
            }

            function changeHistoOpts()
            {
                var stretch_mode = $("stretch_mode").value;
                var stretch_mode_region = $("stretch_mode_region").value;
                layer.mergeNewParams({stretch_mode:stretch_mode, stretch_mode_region: stretch_mode_region});
                updateImage(); ////////////////////
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
                map.updateSize();
            }

            function changeSharpenOpts()
            {
                var sharpen_mode = $("sharpen_mode").value;
                layer.mergeNewParams({sharpen_mode:sharpen_mode});

                updateImage();
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

            function chgInterpolation()
            {
	            var interpolation = $("interpolation").value;
	            layer.mergeNewParams({interpolation:interpolation});

	            updateImage();
            }

            function chipImage(format)
            {
                var imageParams = "";
                var res = map.getResolution();
                imageParams += "res=" + res + "&";
                var tileHypotenuse = Math.round(Math.sqrt(Math.pow(map.getCurrentSize().w, 2) + Math.pow(map.getCurrentSize().h, 2)));
                var width  = parseFloat("${rasterEntry.width}");
                var scalex = 1.0/(res * tileHypotenuse);
                var x = (imageBounds.left - (-width*0.5))*scalex;
                imageParams += "x=" + x + "&";
                var height = parseFloat("${rasterEntry.height}");
                var scaley = 1.0/(res * tileHypotenuse);
                var y =  ((height*0.5) - imageBounds.top)*scaley;
                var tempFormat = format;
                if(!tempFormat) tempFormat = "jpeg";
                imageParams += "y=" + y + "&";
                imageParams += "z=" + map.getZoom() + "&";
                imageParams += "tileWidth=" + tileHypotenuse + "&";
                imageParams += "tileHeight=" + tileHypotenuse + "&";
                imageParams += "id=" + "${rasterEntry?.id}" + "&";
                imageParams += "interpolation=" + $("interpolation").value + "&";
                imageParams += "brightness=" + brightnessSlider.getRealValue() + "&";
                imageParams += "contrast=" + contrastSlider.getRealValue() + "&";
                imageParams += "sharpen_mode=" + $("sharpen_mode").value + "&";
                imageParams += "stretch_mode=" + $("stretch_mode").value + "&";
                imageParams += "stretch_mode_region=" + $("stretch_mode_region").value + "&";
                imageParams += "bands=" + $("bands").value + "&";
                var imageWidth = map.getCurrentSize().w;
                var imageHeight = map.getCurrentSize().h;

                document.location.href = "${createLink(controller: 'ogc', action: 'chip')}" + "?" + imageParams + "&type=tile" + "&angle=" + ${"rotateAngle"}.value + "&imageHeight=" + map.getCurrentSize().h + "&imageWidth=" + map.getCurrentSize().w + "&format=" + tempFormat;
            }

            function getImageURL()
            {
                var res = map.getResolution();
                r= map.getCurrentSize().w;
            if(r < map.getCurrentSize().h) r=map.getCurrentSize().h;
                var tileHypotenuse = r;//Math.round(Math.sqrt(Math.pow(map.getCurrentSize().w, 2) + Math.pow(map.getCurrentSize().h, 2)));
            //var x = map.getCenter().lon;
            //var y = map.getCenter().lat;
                var width  = parseFloat("${rasterEntry.width}");
                var scalex = 1.0/(res * tileHypotenuse);
                var x =  (imageBounds.left - (-width*0.5))*scalex;

                var height = parseFloat("${rasterEntry.height}");
                var scaley = 1.0/(res * tileHypotenuse);
                var y =  ((height*0.5) - imageBounds.top)*scaley;
                var z = map.getZoom();

                omarImageSpaceOpenLayersParams.setProperties(document);
                omarImageSpaceOpenLayersParams.setProperties(
                {
                    'res' : res,
                    'x' : x,
                    'y' : y,
                    'z' : z,
                    'id' : "${rasterEntry?.id}",
                    'tileWidth' : tileHypotenuse,
                    'tileHeight' : tileHypotenuse
                });

                var path = "?"+omarImageSpaceOpenLayersParams.toUrlParams();
                var url = "${createLink(controller: 'icp', action: 'getTileOpenLayers')}";

                return url + path;
            }

            function get_my_url (bounds)
            {
                var width  = parseFloat("${rasterEntry.width}");
                var height = parseFloat("${rasterEntry.height}");
                var res = this.map.getResolution();
                //var scalex = 1.0/(res * this.tileSize.w);
                //var scaley = 1.0/(res * this.tileSize.h);
                //var x =  (bounds.left - (-width*0.5))*scalex;
                //var y =  ((height*0.5) - bounds.top)*scaley;
                var x = map.getCenter().lon;
                var y = map.getCenter().lat;
                var z = this.map.getZoom();

                omarImageSpaceOpenLayersParams.setProperties(document);
                omarImageSpaceOpenLayersParams.setProperties(
                {
                    'res': res,
                    'x': x,
                    'y': y,
                    'z': z,
                    // define an id of 0 to set the background image base layer to black ////////////////////
                    'id': 0,
                    'tileWidth':2,//this.tileSize.w,
                    'tileHeight':2//this.tileSize.h
                });

                var path = "?"+omarImageSpaceOpenLayersParams.toUrlParams();
                var url = this.url;

                if (url instanceof Array) { url = this.selectUrl(path, url); }
                return url + path;
            }

            function init(mapWidth, mapHeight)
            {
                OpenLayers.ImgPath = "${resource(plugin: 'openlayers', dir: 'js/img')}/";

                var width  = parseFloat("${rasterEntry.width}");
                var height = parseFloat("${rasterEntry.height}");
                var r = width;
                if (r < height) r = height;
                hyp    = r;//Math.sqrt(width*width + height*height)*0.5;
                var left       =  -hyp;
                var bottom     =  -hyp;
                var top        =  hyp;
                var right      =  hyp;
                var url = "${createLink(controller: 'icp', action: 'getTileOpenLayers')}";
                var bounds = new OpenLayers.Bounds(Math.round(left), Math.round(bottom), Math.round(right), Math.round(top));

                map = new OpenLayers.Map("map", { controls:[], theme: null, maxExtent:bounds, numZoomLevels:(resLevels+2) });
                map.events.register('zoomend', null, theMapHasZoomed);
                map.events.register("moveend", null, theMapHasMoved);

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

	            var oMenu = new YAHOO.widget.MenuBar("rasterMenu",
	            {
	                autosubmenudisplay: true,
                    showdelay: 0,
                    hidedelay: 750,
                    lazyload: true
                });
	            oMenu.render();



                layer = new OpenLayers.Layer.TMS( "Image Space Viewer", url, options);
                //layer = new OpenLayers.Layer("Empty", options);
                map.addLayer(layer);
                map.addControl(new OpenLayers.Control.MouseDefaults());
                map.addControl(new OpenLayers.Control.KeyboardDefaults());
                map.setBaseLayer(layer);
                changeMapSize(mapWidth, mapHeight);
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
                        updateImage();
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
                        updateImage();
		            }
                });
	            contrastSlider.setRealValue(${params.contrast ?: 1});

	            rotateSlider.animate = false;
	            rotateSlider.getRealValue = function() { return this.getValue() * 2; }
                rotateSlider.setRealValue = function(value) { this.setValue(Math.ceil((value%360.0) / 2)); }
                rotateSlider.subscribe("change", function() { sliderRotate(this.getRealValue()); });
                rotateSlider.setRealValue(rotationAngle);

	            setupCompassMap();

	            // initialize old and new coordinate center variables ////////////////////
	            oldMapCenterX = map.getCenter().lon;
	            oldMapCenterY = map.getCenter().lat;
	            oldImageCenterX = map.getCenter().lon;
	            oldImageCenterY = map.getCenter().lat;
	            newImageCenterX = map.getCenter().lon;
	            newImageCenterY = map.getCenter().lat;

	            // set the initialization flag so the moveend and zoomend code can execute
	            initFlag = 0;
                resetImageVectorLayer();

                map.zoomIn();
                // initialize the zoom level variable used to determine zoom in and out in the MapHasZoomed ////////////////////
	            oldZoomLevel = map.getZoom();



            }

            function resetImageVectorLayer() ////////////////////
            {
                // remove the image vector layer and all previous images
                if (imageVectorLayer) { imageVectorLayer.destroy(); }
	            imageVectorLayer = new OpenLayers.Layer.Vector("Simple Geometry",
	            {
		            styleMap: new OpenLayers.StyleMap
		            ({
			            "default":
			            {
				            externalGraphic : <%=' "${urlPath}" '%>,
				            graphicWidth : <%=' "${imageWidth}" '%>,
      			            graphicHeight : <%=' "${imageHeight}" '%>,
				            rotation : <%=' "${angle}" '%>
}
                })
                });
                map.addLayer(imageVectorLayer);
            }

            function resetBrightnessContrast()
            {
              brightnessSlider.setRealValue(0);
              contrastSlider.setRealValue(1.0);

                updateImage();
            }

            function rotateTextFieldChange(angle)
            {
                sliderRotate(angle);
                rotateSlider.unsubscribe("change");
                rotateSlider.setRealValue(angle);
                rotateSlider.subscribe("change", function() { sliderRotate(this.getRealValue()); });
            }

            function setupCompassMap()
            {

                compassMap = new OpenLayers.Map('compassMap', {controls: new OpenLayers.Control.Navigation({autoActivate: false}), theme: null});

              var baseLayer = new OpenLayers.Layer("Empty", {isBaseLayer: true});
              compassMap.addLayer(baseLayer);


              compassMap.setCenter(new OpenLayers.LonLat(0,0), 0);


              var compassImageURL = "${resource(plugin: 'omar', dir: 'images', file: 'north_arrow.png')}";

                // define a vector layer to add markers to
	            compassVectorLayer = new OpenLayers.Layer.Vector("Compass Layer",
	            {
		            styleMap: new OpenLayers.StyleMap
		            ({
			            "default":
			            {
				            externalGraphic : compassImageURL,
				            graphicWidth : 40,
                            graphicHeight : 40,
			                rotation : <%=' "${angle}" '%>
}
})
});

compassMap.addLayer(compassVectorLayer);



// define the marker for the image to sit on
compassImage = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Point(0,0), {angle: -northAngle});
compassVectorLayer.addFeatures([compassImage]);
}

function setupToolbar()
{
var panButton = new OpenLayers.Control.MouseDefaults({title:'Click pan button to activate. Once activated click the map and drag the mouse to pan.'});
var zoomBoxButton = new OpenLayers.Control.ZoomBox({title:"Click the zoom box button to activate. Once activated click and drag over an area of interest on the map to zoom into."});
zoomInButton = new OpenLayers.Control.Button({title: "Click to zoom in.", displayClass: "olControlZoomIn", trigger: zoomIn});
var zoomInFullResButton = new OpenLayers.Control.Button({title: "Click to zoom into full resolution.", displayClass: "olControlZoomToLayer", trigger: zoomInFullRes});
var zoomOutButton = new OpenLayers.Control.Button({title: "Click to zoom out.", displayClass: "olControlZoomOut", trigger: zoomOut});
var container = $("toolBar");
var panel = new OpenLayers.Control.Panel(
{
div: container,defaultControl: panButton,
'displayClass': 'olControlPanel'
});
var navButton = new OpenLayers.Control.NavigationHistory(
{
nextOptions: {title: "Next View" },
previousOptions: {title: "Previous View"}
});

map.addControl(navButton);
panel.addControls(
[
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

function sliderRotate(sliderValue)
{
// remove all previous images
resetImageVectorLayer();
            rotationAngle = 360 - parseInt(sliderValue)
${"rotateAngle"}.value = sliderValue;
                // remove the image from the map so it can be rotated
	            compassVectorLayer.removeFeatures([compassImage]);
	            imageVectorLayer.removeFeatures([image]);

	            // redefine the marker with the new rotation angle
	            compassImage = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Point(0, 0), {angle: (-northAngle)+sliderValue});
	            compassVectorLayer.addFeatures([compassImage]);

                 image = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Point(currentMapCenterX, currentMapCenterY), {urlPath: imageURL, imageWidth: imageHypotenuse, imageHeight: imageHypotenuse, angle: sliderValue});
	            imageVectorLayer.addFeatures([image]);
            }

            function theMapHasMoved()
            {
                if (initFlag == 0) { updateImage(); }
            }

            function theMapHasZoomed()
            {
                if (initFlag == 0)
                {
                    // remove the image vector layer and erase all previous images

                    // reset the image vector layer an make a new one
                    resetImageVectorLayer();

                    //if ((map.getZoom() - oldZoomLevel) > 0) { imageHypotenuse = imageHypotenuse * 2; }
                    //else if((map.getZoom() - oldZoomLevel) < 0) { imageHypotenuse = imageHypotenuse * 0.5; }
                    //image = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Point(currentMapCenterX, currentMapCenterY), {urlPath: imageURL, imageWidth: imageHypotenuse, imageHeight: imageHypotenuse, angle: -rotationAngle});
	                //imageVectorLayer.addFeatures([image]);

                    // reset the zoom variable to be used next time
                    oldZoomLevel = map.getZoom();
	            }
            }

            function updateImage()
            {
                // calculate the change in actual map movement
                var deltaMovementX = map.getCenter().lon - oldMapCenterX;
	            var deltaMovementY = map.getCenter().lat - oldMapCenterY;
	            var deltaMovementMagnitude = Math.sqrt(Math.pow(deltaMovementX,2) + Math.pow(deltaMovementY,2));
                var beta = 0;
                // calculate the angle of the actual map movement
	            if (deltaMovementY >= 0 && deltaMovementX > 0) { beta = Math.atan(deltaMovementY / deltaMovementX) * 180/Math.PI; }
	            else if (deltaMovementY >= 0 && deltaMovementX < 0) { beta = Math.atan(deltaMovementY / deltaMovementX) * 180/Math.PI + 180; }
	            else if (deltaMovementY < 0 && deltaMovementX < 0) { beta = Math.atan(deltaMovementY / deltaMovementX) * 180/Math.PI + 180; }
	            else if (deltaMovementY < 0 && deltaMovementX > 0) { beta = Math.atan(deltaMovementY / deltaMovementX) * 180/Math.PI + 360; }

	            // relate the actual map movement to relative image movement
	            var theta = beta - rotationAngle;
	            var relativeXDelta = Math.cos(theta * Math.PI/180) * deltaMovementMagnitude;
	            var relativeYDelta = Math.sin(theta * Math.PI/180) * deltaMovementMagnitude;

                // calculate the new map center
	            newImageCenterX = oldImageCenterX + relativeXDelta;
	            newImageCenterY = oldImageCenterY + relativeYDelta;

                // calculate the map bounds for the TMS call
                var mapWidthHalf = (map.calculateBounds().right - map.calculateBounds().left) / 2;
                var mapHeightHalf = (map.calculateBounds().top - map.calculateBounds().bottom) / 2;
                r =  mapWidthHalf;
                if(r < mapHeightHalf) r = mapHeightHalf;
                var mapHypotenuse = r;//Math.sqrt(Math.pow(mapWidthHalf,2) + Math.pow(mapHeightHalf,2));
                imageBounds = new OpenLayers.Bounds(
                    newImageCenterX - mapHypotenuse,
                    newImageCenterY - mapHypotenuse,
                    newImageCenterX + mapHypotenuse,
                    newImageCenterY + mapHypotenuse
                );

                //calculateImageBounds();

                // get the URL for the image
                imageURL = getImageURL();

            r = map.getCurrentSize().w;
            if(r < map.getCurrentSize().h) r = map.getCurrentSize().h;
                // calculate the image size so there are no gaps while rotating
                imageHypotenuse = r;//Math.round(Math.sqrt(Math.pow(map.getCurrentSize().w, 2) +
                                      //                 Math.pow(map.getCurrentSize().h, 2)));

                // get the current map center which is where the image will go
	            currentMapCenterX = map.getCenter().lon;
	            currentMapCenterY = map.getCenter().lat;

                // define the marker for the image to sit on
                image = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Point(currentMapCenterX, currentMapCenterY),
                                                     {urlPath: imageURL,
                                                      imageWidth: imageHypotenuse,
                                                      imageHeight: imageHypotenuse,
                                                      angle: $("rotateAngle").value});
	            imageVectorLayer.addFeatures([image]);

                // set the map center coordinate variables to be used the next time the map is moved
	            oldMapCenterX = map.getCenter().lon;
	            oldMapCenterY = map.getCenter().lat;

                // set the image center coordinate variables to be used the next time the map is moved
	            oldImageCenterX = newImageCenterX;
	            oldImageCenterY = newImageCenterY;
            }

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
	            if(map.getZoom() < map.getZoomForResolution(1.0, true)) { zoomInButton.displayClass = "olControlZoomIn"; }
            }


            function calculateImageBounds()
            {
                //alert("Map Width / 2 =" + map.getCurrentSize().w/2);
                //alert("Map Height / 2 =" + map.getCurrentSize().h/2);
                var alpha = Math.atan((map.getCurrentSize().h/2) / (map.getCurrentSize().w/2)) * 180/Math.PI;
                var beta = rotationAngle - alpha;
                var hypotenuse1 = (map.getCurrentSize().w/2) / Math.cos(beta * Math.PI/180);
                var hypotenuse2 = Math.sqrt(Math.pow(map.getCurrentSize().w/2,2) + Math.pow(map.getCurrentSize().h/2,2)) - hypotenuse1;
                var extensionLength = Math.sin((90 - beta) * Math.PI/180) * hypotenuse2;
                //alert(extensionLength);

                var mapWidth = map.calculateBounds().right - map.calculateBounds().left;
                var mapHeight = map.calculateBounds().top - map.calculateBounds().bottom;
                alpha = Math.atan((mapHeight / 2) / (mapWidth / 2)) * 180/Math.PI;
                hypotenuse1 = (mapWidth / 2) / Math.cos(beta * Math.PI/180);
                hypotenuse2 = Math.sqrt(Math.pow(mapWidth / 2,2) + Math.pow(mapHeight / 2,2)) - hypotenuse1;
                extensionLength = Math.sin((90 - beta) * Math.PI/180) * hypotenuse2;

                var imageBounds1 = new OpenLayers.Bounds(
                    newImageCenterX - mapWidth / 2 + extensionLength,
                    newImageCenterY - mapHeight / 2,
                    newImageCenterX + mapWidth / 2 + extensionLength,
                    newImageCenterY + mapHeight / 2
                );
            }
</r:script>
</body>
</html>