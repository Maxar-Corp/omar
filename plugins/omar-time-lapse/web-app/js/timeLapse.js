var cacheRead;
var cacheWrite;
var currentLayer; 
var dom = YAHOO.util.Dom;
var loadedLayers = new Array();
var mapLayers = new Array();
var map;
var movieAdvance;
var playDirection;
var spinner;

$(document).ready(
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
		pageSetup();
		mapSetup();
		currentLayer = imageIds.length - 1;
		fastForward();
	}
);

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

function exportImage()
{
	var exportImageUrl = exportImageUrlBase;
	exportImageUrl += "?acquisitionDate=" + acquisitionDates[currentLayer];
	exportImageUrl += "&countryCode=" + countryCodes[currentLayer];
	exportImageUrl += "&imageId=" + imageIds[currentLayer];

	var imageUrl = urlBase + mapLayers[currentLayer].getURL(map.getExtent());
	imageUrl = imageUrl.replace(/&/g, "%26");
	exportImageUrl += "&imageURL=" + imageUrl;
	
	var centerGeo = coordConvert.ddToDms(map.getCenter().lat, map.getCenter().lon);
	var centerMgrs = coordConvert.ddToMgrs(map.getCenter().lat, map.getCenter().lon);
	exportImageUrl += "&centerGeo=GEO: " + centerGeo + " MGRS: " + centerMgrs;
	exportImageUrl += "&northArrowAngle=0";
	window.open(exportImageUrl);
}

function exportLink()
{
	var exportLinkUrl = exportLinkUrlBase;
	exportLinkUrl += "?imageIds=" + indexIds.join(",");
	exportLinkUrl += "&bbox=" + map.calculateBounds().toArray();

	$("#exportLinkDialog").html("Right-click the link below to copy:<br><br><a href='" + exportLinkUrl + "' target = '_blank'><b>OMAR Time Lapse Link</b></a>");
	$("#exportLinkDialog").dialog({ width: "auto" });
}

function exportTimeLapse()
{
	var imageUrlsForPdf = new Array();
	for (var i = 0; i < imageIds.length; i++) 
	{ 
		imageUrlsForPdf[i] = urlBase + mapLayers[i].getURL(map.getExtent()); 
		imageUrlsForPdf[i] = imageUrlsForPdf[i].replace(/&/g, "%26");
	}
	var exportTimeLapseUrl = exportTimeLapseUrlBase;
	exportTimeLapseUrl += "?imageUrls=" + imageUrlsForPdf.join(">");
	$("#submitForm").get(0).action = exportTimeLapseUrl;
	$("#submitForm").get(0).submit();
}

function exportTimeLapseSummary()
{
	$("#exportTimeLapseSummaryDialog").html(imageIds.join("<br>"));
	$("#exportTimeLapseSummaryDialog").css("textAlign", "left");
	$("#exportTimeLapseSummaryDialog").dialog({ width: "auto" });
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
			$("#wmsUrlBase").html(),
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
	var mapHeight = 0.8 * $(window).height();
	var mapWidth = 0.90 * $(window).width();

	$("#map").css("height", mapHeight);
	$("#map").css("width", mapWidth);
	$("#map").position
	({
		my: "middle top",
		at: "middle top",
		of: $(window),
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
	
	$("#slider").css("width", $("#map").width());
	$("#slider").position
	({
		my: "middle top",
		at: "middle bottom",
		of: $("#map"),
		offset: "0 10"
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

	$("#exportLinkButton").button(
	{
		icons: {primary: "ui-icon-link"},
		text: false,
		}).click(function() { exportLink(); }
	);
	$("#exportLinkButton").position
	({
		my: "right top",
		at: "left top",
		of: $("#rewindButton"),
		offset: "-30 0"
	});
	
	$("#exportImageButton").button(
	{
		icons: {primary: "ui-icon-image"},
		text: false
		}).click(function () { exportImage(); }
	);
	$("#exportImageButton").position
	({
		my: "right top",
		at: "left top",
		of: $("#exportLinkButton"),
		offset: "-5 0"
	});

	$("#exportTimeLapseButton").button(
	{
		icons: {primary: "ui-icon-video"},
		text: false
		}).click(function() { exportTimeLapse(); }
	);
	$("#exportTimeLapseButton").position
	({
		my: "right top",
		at: "left top",
 		of: $("#exportImageButton"),
		offset: "-5 0"
	});

	$("#exportTimeLapseSummaryButton").button(
	{
		icons: {primary: "ui-icon-script"},
		text: false,
		}).click(function() { exportTimeLapseSummary(); }
	);
	$("#exportTimeLapseSummaryButton").position
	({
		my: "right top",
		at: "left top",
		of: $("#exportTimeLapseButton"),
		offset: "-5 0"
	});

	$("#deleteImageFromTimeLapseButton").button(
	{
		icons: {primary: "ui-icon-trash"},
		text: false
		}).click(function() { deleteImageFromTimeLapse(); }
	);
	$("#deleteImageFromTimeLapseButton").position
	({
		my: "left top",
		at: "right top",
		of: $("#fastForwardButton"),
		offset: "30 0"
	});

	$("#reverseTimeLapseOrderButton").button(
	{
		icons: {primary: "ui-icon-refresh"},
		text: false
		}).click(function() { reverseTimeLapseOrder(); }
	);
	$("#reverseTimeLapseOrderButton").position
	({
		my: "left top",
		at: "right top",
		of: $("#deleteImageFromTimeLapseButton"),
		offset: "5 0"
	});
}

function playMovie()
{
	if (playDirection == "forward") { fastForward(); }
	else { rewind(); }
	movieAdvance = setTimeout("playMovie()", 1000);
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

function stopMovie() { clearTimeout(movieAdvance); }

function theMapHasMoved()
{
	for (var i = 0; i < imageIds.length; i++) { fastForward(); }
}

function theMapHasZoomed()
{
	for (var i = 0; i < imageIds.length; i++) { fastForward(); }
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
