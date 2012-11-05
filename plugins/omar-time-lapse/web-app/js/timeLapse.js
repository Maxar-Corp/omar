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

function deleteImageFromMovie()
{
	if (currentLayer == 0)
	{
		rewind();
		acquisitionDates.splice(0,1);
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
	map = new OpenLayers.Map("map", {numZoomLevels: 25} );

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
	
	$("#deleteButton").button(
	{
		icons: {primary: "ui-icon-trash"},
		text: false
		}).click(function() { deleteImageFromMovie(); }
	);
	$("#deleteButton").position
	({
		my: "left top",
		at: "right top",
		of: $("#fastForwardButton"),
		offset: "30 0"
	});
	
}

function playMovie()
{
	if (playDirection == "forward") { fastForward(); }
	else { rewind(); }
	movieAdvance = setTimeout("playMovie()", 1000);
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
