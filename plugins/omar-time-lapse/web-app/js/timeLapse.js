var currentLayer; 
var dom = YAHOO.util.Dom;
var mapLayers = new Array();
var map;
var movieAdvance;
var playDirection;

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
				layout.on( 'render', function ()
				{
					dom.setStyle( document.body, 'visibility', 'visible' );
				});
				layout.render();
			}
		);
		pageSetup();
		mapSetup();
		currentLayer = imageIds.length - 1;
		fastForward();
	}
);

function fastForward()
{
	mapLayers[currentLayer].setVisibility(false);
	currentLayer++;
	if (currentLayer > imageIds.length - 1)
	{
		currentLayer = 0;
	}
	mapLayers[currentLayer].setVisibility(true);
	updateProgressSlider();
	updateText();
}

function mapSetup()
{
	map = new OpenLayers.Map("map", 
	{
		numZoomLevels: 25
	});

	var baseLayer = new OpenLayers.Layer("Empty",
	{
		isBaseLayer: true
	});
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
		map.addLayer(mapLayers[i]);
		mapLayers[i].setVisibility(false);
	}
}

function pageSetup()
{
	var mapHeight = 0.8 * $(window).height();
	var mapWidth = 0.8 * $(window).width();

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
	$("#slider").slider
	({
		max: imageIds.length - 1,
		min: 0,
		slide: function(event, ui)
		{
			if (ui.value > currentLayer)
			{
				fastForward();
			}
			else 
			{
				rewind();
			}
		}
	});

	$("#rewindButton").button(
	{
		icons: {primary: "ui-icon-seek-prev"},
		text: false
		}).click(function()
		{
			rewind();
		}
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
		}).click(function() 
		{
			stopMovie();
		}
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
		}).click( function()
		{
			fastForward();
		}
	);

	$("#movieControlsDiv").position
	({
		my: "middle top",
		at: "middle bottom",
		of: $("#slider"),
		offset: "0 15"
	});
}

function playMovie()
{
	if (playDirection == "forward")
	{
		fastForward();
	}
	else
	{
		rewind();
	}
	movieAdvance = setTimeout("playMovie()", 1000);
}

function rewind()
{
	mapLayers[currentLayer].setVisibility(false);
	currentLayer--;
	if (currentLayer < 0)
	{
		currentLayer = imageIds.length - 1;;
	}
        mapLayers[currentLayer].setVisibility(true);
	updateProgressSlider();
        updateText();
}

function stopMovie()
{
	clearTimeout(movieAdvance);
}

function updateProgressSlider()
{
	$("#slider").slider("value", currentLayer);
}

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
