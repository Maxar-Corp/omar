var cacheRead;
var cacheWrite;
var currentLayer; 
var dom = YAHOO.util.Dom;
var loadedLayers = new Array();
var mapLayers = new Array();
var map;
var markerLayer;
var markerSize = new OpenLayers.Size(21,25);
var movieAdvance;
var playDirection;
var playSpeed = 1000;
var spinner;

$(document).ready
(
	function () 
	{ 
		Event = YAHOO.util.Event;
		Event.onDOMReady( 
			function ()
			{
				var layout = new YAHOO.widget.Layout(
				{
					units:
					[
						{ position:'top', height:25, body:'top1' },
						{ position:'bottom', height:25, body:'bottom1' },
						{ position:'center', body:'center1', scroll:true }
					]
				});
				layout.on('render', function () { dom.setStyle(document.body, 'visibility', 'visible'); });
				layout.render();
			}
		);

		var oMenu = new YAHOO.widget.MenuBar
		(
			"timeLapseMenu", 
			{ autosubmenudisplay: true, hidedelay: 750, showdelay: 0, lazyload: true, zIndex:9999}
		); 
		oMenu.render();

		pageSetup();
		mapSetup();
		setupDialogs();
		initializeMarkerLayer();
		currentLayer = imageIds.length - 1;
		fastForward();
	}
);

function addMarkerDialog()
{
	$("#markerLatitudeInput").val(map.getCenter().lat);
	$("#markerLongitudeInput").val(map.getCenter().lon);
	$("#addMarkerDialog").dialog("open");
}

function deleteImageFromTimeLapse()
{
	if (currentLayer == 0)
	{
		rewind();
		acquisitionDates.splice(0,1);
		countryCodes.splice(0,1);
		imageIds.splice(0,1);
		indexIds.splice(0,1);
		loadedLayers.splice(0,1);
		mapLayers.splice(0,1);
		currentLayer--;
	}
	else
	{
		rewind();
		acquisitionDates.splice(currentLayer + 1, 1);
		countryCodes.splice(currentLayer + 1, 1);
		imageIds.splice(currentLayer + 1, 1);
		indexIds.splice(currentLayer + 1, 1);
		loadedLayers.splice(currentLayer + 1, 1);
		mapLayers.splice(currentLayer + 1, 1);
	}
	generateSlider();
	for (var i = 0; i < imageIds.length; i++) { mapLayers[i].id = i; }
	rewind();
	fastForward();
}

function dropMarker()
{
	var markerLatitude = $("#markerLatitudeInput").val();
	var markerLongitude = $("#markerLongitudeInput").val();
	var markerPoint = new OpenLayers.Geometry.Point(markerLongitude,markerLatitude);
	var marker = new OpenLayers.Feature.Vector(markerPoint);
	markerLayer.addFeatures(marker);
	marker.style.label = markerLatitude + ", " + markerLongitude;
	markerLayer.redraw();
} 

function exportImage()
{
	var exportImageUrl = exportImageUrlBase;
	exportImageUrl += "?acquisitionDate=" + acquisitionDates[currentLayer];
	exportImageUrl += "&countryCode=" + countryCodes[currentLayer];
	exportImageUrl += "&imageId=" + imageIds[currentLayer];

	var imageUrl = mapLayers[currentLayer].getURL(map.getExtent());
	imageUrl = imageUrl.replace(/&/g, "%26");
	exportImageUrl += "&imageURL=" + imageUrl;
	
	var centerGeo = coordConvert.ddToDms(map.getCenter().lat, map.getCenter().lon);
	var centerMgrs = coordConvert.ddToMgrs(map.getCenter().lat, map.getCenter().lon);
	exportImageUrl += "&centerGeo=GEO: " + centerGeo + " MGRS: " + centerMgrs;
	exportImageUrl += "&northArrowAngle=0";
	
	if (markerLayer.features.length > 0)
	{
		exportImageUrl += "&markers=";
		var markerLocationArray = new Array();
		for (var i = 0; i < markerLayer.features.length; i++)
		{
			var markerGeometry = markerLayer.features[i].geometry;
			var markerPoint = map.getPixelFromLonLat(new OpenLayers.LonLat(markerGeometry.x, markerGeometry.y));
			markerLocationArray[2 * i] = markerPoint.x - (markerSize.w/2);
			markerLocationArray[2 * i +1] = markerPoint.y - markerSize.h;
		}
		exportImageUrl += markerLocationArray.join(",");
	}
	window.open(exportImageUrl);
}

function exportLink()
{
	var exportLinkUrl = exportLinkUrlBase;
	exportLinkUrl += "?imageIds=" + indexIds.join(",");
	exportLinkUrl += "&bbox=" + map.calculateBounds().toArray();

	$("#exportLinkDialog").html("Right-click the link below to copy:<br><br><a href='" + exportLinkUrl + "' target = '_blank'><b>OMAR Time Lapse Link</b></a>");
	$("#exportLinkDialog").dialog("open");
}

function exportTimeLapseGif()
{
	var imageUrlsForGif = new Array();
	for (var i = 0; i < imageIds.length; i++)
	{
		imageUrlsForGif[i] = mapLayers[i].getURL(map.getExtent());
		imageUrlsForGif[i] = imageUrlsForGif[i].replace(/&/g, "%26");
	}
	var exportTimeLapseUrl = exportTimeLapseGifUrlBase;
	exportTimeLapseUrl += "?imageUrls=" + imageUrlsForGif.join(">");
	$("#submitForm").get(0).action = exportTimeLapseUrl;
	$("#submitForm").get(0).submit();
}

function exportTimeLapsePdf()
{
	var imageUrlsForPdf = new Array();
	for (var i = 0; i < imageIds.length; i++) 
	{ 
		imageUrlsForPdf[i] = mapLayers[i].getURL(map.getExtent()); 
		imageUrlsForPdf[i] = imageUrlsForPdf[i].replace(/&/g, "%26");
	}
	var exportTimeLapseUrl = exportTimeLapsePdfUrlBase;
	exportTimeLapseUrl += "?imageUrls=" + imageUrlsForPdf.join(">");
	$("#submitForm").get(0).action = exportTimeLapseUrl;
	$("#submitForm").get(0).submit();
}

function exportTimeLapseSummary()
{
	var timeLapseSummaryTable = "";
	
	// list all images and metadata
	timeLapseSummaryTable += "<table>" +
		"<tr>" +
			"<td><b>No.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</b></td>" + 
			"<td><b>Image Id&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</b></td>" +
			"<td><b>Acquisition Date&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</b></td>" +
			"<td><b>NIIRS&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</b></td>" +
			"<td><b>CC&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</b></td>" +
		"</tr>";
	for (var i = 0; i < imageIds.length; i++)
	{
		timeLapseSummaryTable += 
			"<tr>" + 
				"<td>" + (i + 1) + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>" +
				"<td>" + imageIds[i] + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>" +
				"<td>" + acquisitionDates[i] + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>" +
				"<td>" + niirsValues[i] + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>" +
				"<td>" + countryCodes[i] + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>" +
			"</tr>";
	} 
	timeLapseSummaryTable += "</table><br>";

	// list the current map center
	timeLapseSummaryTable += "<b>Map Center:&nbsp;&nbsp;</b>" + 
		map.getCenter().lat + ", " + map.getCenter().lon + " // " +
		coordConvert.ddToDms(map.getCenter().lat, map.getCenter().lon) + " // " +
		coordConvert.ddToMgrs(map.getCenter().lat, map.getCenter().lon);

	timeLapseSummaryTable += "<br>";

	// list the current map dimensions in meters
	var leftCenterPoint = new OpenLayers.Geometry.Point(map.getCenter().lon - map.calculateBounds().getWidth()/2, map.getCenter().lat);
	var rightCenterPoint = new OpenLayers.Geometry.Point(map.getCenter().lon + map.calculateBounds().getWidth()/2, map.getCenter().lat);
	var longitudeLine = new OpenLayers.Geometry.LineString([leftCenterPoint, rightCenterPoint]);
	var longitudeDistance = parseInt(longitudeLine.getGeodesicLength(new OpenLayers.Projection("EPSG:4326")));

	var topMiddlePoint = new OpenLayers.Geometry.Point(map.getCenter().lon, map.getCenter().lat + map.calculateBounds().getHeight()/2);
	var bottomMiddlePoint = new OpenLayers.Geometry.Point(map.getCenter().lon, map.getCenter().lat - map.calculateBounds().getHeight()/2);
	var latitudeLine = new OpenLayers.Geometry.LineString([topMiddlePoint, bottomMiddlePoint]);
	var latitudeDistance = parseInt(latitudeLine.getGeodesicLength(new OpenLayers.Projection("EPSG:4326")));
	timeLapseSummaryTable += "<b>Map Dimensions:&nbsp;&nbsp;</b>" + longitudeDistance + "m x " + latitudeDistance + "m (approx.)";

	$("#exportTimeLapseSummaryDialog").html(timeLapseSummaryTable);
	$("#exportTimeLapseSummaryDialog").css("textAlign", "left");
	$("#exportTimeLapseSummaryDialog").dialog("open");
}

function fastForward()
{
	mapLayers[currentLayer].setVisibility(false);
	currentLayer++;
	if (currentLayer > imageIds.length - 1) { currentLayer = 0; }
	mapLayers[currentLayer].setVisibility(true);
	if (loadedLayers[currentLayer] == 0) { generateSpinner(); }
	updateProgressSlider();
	updateText();
}

function generateSlider()
{
	$("#slider").slider
	({
		max: imageIds.length - 1,
		min: 0,
		range: "min",
		slide: function(event, ui)
		{
			if (ui.value > currentLayer) { fastForward(); }
			else { rewind(); }
		}
	});
}

function generateSpinner()
{
	var target = document.getElementById("map");
	if (spinner) { spinner.spin(target); }
	else
	{
		var options = 
		{
			className: "spinner", color: "#000000", corners: 1, hwaccel: false, left: "auto", lines: 13,
			radius: 10, rotate: 0, shadow: false, speed: 1, top: "auto", trail: 60, width: 4, zIndex: 2e9
		};
		spinner = new Spinner(options).spin(target);
	}
}

function initializeMarkerLayer()
{
	markerLayer = new OpenLayers.Layer.Vector
	(
		"Vector Layer",
		{
			style:
			{
				externalGraphic: icon,
				fontColor: "white",
				fontFamily: "Courrier New, monospace",
				fontSize: "14px",
				fontWeight: "bold",
				graphicHeight: markerSize.h,
				graphicHWidth: markerSize.w,
				graphicYOffset: -markerSize.h,
				labelOutlineColor: "black",
				labelOutlineWidth: 3,
				labelYOffset: 1.25 * markerSize.h
			}
		}
	);
	map.addLayer(markerLayer);

	var dragFeatureControl = new OpenLayers.Control.DragFeature
	(
		markerLayer, 
		{
			onComplete: function(marker) { updateMarkerPosition(marker); }
		}
	);
	map.addControl(dragFeatureControl);
	dragFeatureControl.activate();

	// drop any marker locations passed through the url
	if (markers.length > 1)
	{
		var numberOfMarkers = parseInt(markers.length / 2);
		for (var i = 0; i < numberOfMarkers; i++)
		{
			$("#markerLatitudeInput").val(markers[2 * i]);
			$("#markerLongitudeInput").val(markers[2 * i + 1]);
			dropMarker();
		}
	}
}

function mapSetup()
{
	map = new OpenLayers.Map
	(
		"map", 
		{
			numZoomLevels: 25,
			theme: null
		} 
	);

	var baseLayer = new OpenLayers.Layer("Empty", { isBaseLayer: true} );
	map.addLayer(baseLayer);	

	var center = bbox.getCenterLonLat();
	var zoom = map.getZoomForExtent(bbox);
	map.setCenter(center, zoom);
	
	for (var i = 0; i < imageIds.length; i++)
	{
		mapLayers[i] = new OpenLayers.Layer.WMS
		(
			"Layer" + i,
			imageUrlBase,
			{	
				bands: "default",
				brightness: 0,
				contrast: 1,
				format: "image/jpeg",
				interpolation: "bilinear",
				layers: indexIds[i],
				sharpen_mode: "none",
				stretch_mode: "linear_auto_min_max",
				stretch_mode_region: "viewport"
			},
			{
				isBaseLayer: false,
				ratio: 1,
				singleTile: true
			}
		);
		mapLayers[i].id = i;
		mapLayers[i].loadEnd = function()
		{
			loadedLayers[this.id] = 1;
			if (spinner && this.id == currentLayer) { spinner.stop(); }
		};
		mapLayers[i].loadStart = function()
		{
			loadedLayers[this.id] = 0;
			generateSpinner();
		};
		mapLayers[i].events.register("loadend", mapLayers[i], function() { this.loadEnd(); });
		mapLayers[i].events.register("loadstart", mapLayers[i], function() { this.loadStart(); });
		map.addLayer(mapLayers[i]);
		mapLayers[i].setVisibility(false);
	}

	map.events.register("mousemove", map, function(event) { updateMapCoordinates(event); });
	map.events.register("moveend", map, function() { theMapHasMoved(); });
	map.events.register("zoomend", map, function() { theMapHasZoomed(); });

	cacheWrite = new OpenLayers.Control.CacheWrite
	({
		autoActivate: true,
		imageFormat: "image/jpeg"
	});
	cacheRead = new OpenLayers.Control.CacheRead();
	map.addControls([cacheWrite, cacheRead]);
}

function pageSetup()
{
	var mapHeight = 0.75 * $(window).height();
	var mapWidth = 0.90 * $(window).width();

	$("#map").css("height", mapHeight);
	$("#map").css("width", mapWidth);
	$("#map").position
	({
		my: "middle top",
		at: "middle top",
		of: $("#timeLapseMenu"),
		offset: "0 50"
	});

	$("#imageIdHyperlink").css("width", 0.5 * mapWidth);
	$("#imageIdHyperlink").css("text-align", "left");
	$("#imageIdHyperlink").position
	({
		my: "left bottom",
		at: "left top",
		of: $("#map"),
		offset: "0 0"
	});

	$("#acquisitionDateText").css("width", 0.5 * mapWidth);
	$("#acquisitionDateText").css("text-align", "right");
	$("#acquisitionDateText").position
	({
		my: "right bottom",
		at: "right top",
		of: $("#map"),
		offset: "0 0"
	});
	
	$("#mapCoordinatesDiv").css("width", $("#map").width());
	$("#mapCoordinatesDiv").position
	({
		my: "middle top",
		at: "middle bottom",
		of: $("#map"),
		offset: "0 5"
	});

	$("#slider").css("width", $("#map").width());
	$("#slider").position
	({
		my: "middle top",
		at: "middle bottom",
		of: $("#mapCoordinatesDiv"),
		offset: "0 5"
	});
	generateSlider();

	$("#rewindButton").button(
	{
		icons: {primary: "ui-icon-seek-prev"},
		text: false
		}).click(function() { rewind(); }
	);

	$("#playControls").buttonset();
	$("#playReverseButton").button(
	{
		icons: {primary: "ui-icon-triangle-1-w"},
		text: false
		}).click(function()
		{
			playDirection = "reverse";
			stopMovie();
			playMovie();
		}
	);

	$("#stopButton").button(
	{
		icons: {primary: "ui-icon-stop"},
		text: false
		}).click(function() { stopMovie(); }
	);

	$("#playForwardButton").button(
	{
		icons: {primary: "ui-icon-triangle-1-e"},
		text: false
		}).click(function()
		{
			playDirection = "forward";
			stopMovie();
			playMovie();
		}
	);

	$("#fastForwardButton").button(
	{
		icons: {primary: "ui-icon-seek-next"},
		text: false
		}).click( function() { fastForward(); }
	);

	$("#movieControlsDiv").position
	({
		my: "middle top",
		at: "middle bottom",
		of: $("#slider"),
		offset: "0 15"
	});

	$("#slowDownButton").button(
	{
		icons: {primary: "ui-icon-circle-minus"},
		text: false
		}).click(function() { slowDown(); }
	);
	$("#slowDownButton").position
	({
		my: "left top",
		at: "right top",
		of: ("#fastForwardButton"),
		offset: "30 0"
	});

	$("#speedUpButton").button(
	{
		icons: {primary: "ui-icon-circle-plus"},
		text: false
		}).click(function() { speedUp(); }

	);
	$("#speedUpButton").position
	({
		my: "left top",
		at: "right top",
		of: ("#slowDownButton"),
		offset: "5 0"
	});
}

function playMovie()
{
	if (playDirection == "forward") { fastForward(); }
	else { rewind(); }
	movieAdvance = setTimeout("playMovie()", playSpeed);
}

function reverseTimeLapseOrder()
{
	acquisitionDates.reverse();
	countryCodes.reverse();
	imageIds.reverse();
	indexIds.reverse();
	loadedLayers.reverse();
	mapLayers.reverse();
	for (var i = 0; i < imageIds.length; i++) { mapLayers[i].id = i; }
	rewind();
	fastForward();
}

function rewind()
{
	mapLayers[currentLayer].setVisibility(false);
	currentLayer--;
	if (currentLayer < 0) { currentLayer = imageIds.length - 1; }
        mapLayers[currentLayer].setVisibility(true);
	if (loadedLayers[currentLayer] == 0) { generateSpinner(); }
	updateProgressSlider();
        updateText();
}

function setupDialogs()
{
	$("#exportTimeLapseSummaryDialog").dialog
	({
		autoOpen: false, 
		width: "auto" 
	});
	
	$("#exportLinkDialog").dialog
	({ 
		autoOpen: false,
		width: "auto" 
	});

	$("#addMarkerDialog").dialog
	({
		autoOpen: false,
		buttons:
		{
			"Drop": function()
			{
				$(this).dialog("close");
				dropMarker();
			},
			Cancel: function() { $(this).dialog("close"); }
		},
		width: "auto"
	});
}

function slowDown()
{
	if (playSpeed < 2000) { playSpeed += 75; }
}

function speedUp()
{
	if (playSpeed > 250) { playSpeed -= 75; }
}

function stopMovie() { clearTimeout(movieAdvance); }

function theMapHasMoved()
{
	for (var i = 0; i < imageIds.length; i++) { fastForward(); }
}

function theMapHasZoomed()
{
	for (var i = 0; i < imageIds.length; i++) { fastForward(); }
}

function updateMapCoordinates(event)
{
	var mouseCoordinate = map.getLonLatFromPixel(event.xy);
	$("#mapCoordinatesDiv").html
	(
		mouseCoordinate.lat.toFixed(7) + ", " + mouseCoordinate.lon.toFixed(7) + " // " +
		coordConvert.ddToDms(mouseCoordinate.lat, mouseCoordinate.lon) + " // " +
		coordConvert.ddToMgrs(mouseCoordinate.lat, mouseCoordinate.lon)
	);
}

function updateMarkerPosition(marker)
{
	marker.style.label = marker.geometry.y + ", " + marker.geometry.x;
	markerLayer.redraw();
}

function updateProgressSlider() { $("#slider").slider("value", currentLayer); }

function updateText()
{
	var mapBounds = map.calculateBounds().toArray();
	var mapCenterLatitude = map.center.lat;
	var mapCenterLongitude = map.center.lon;
	$("#imageIdHyperlink").html("<a href = '/omar/mapView/index?layers=" + indexIds[currentLayer] + 
		"&latitude=" + mapCenterLatitude + "&longitude=" + mapCenterLongitude +
		"&bbox=" + mapBounds + "' target = '_blank'>" + imageIds[currentLayer] + "</a>");
	$("#acquisitionDateText").html(acquisitionDates[currentLayer]);
}
