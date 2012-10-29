var currentLayer; 
var dom = YAHOO.util.Dom;
var mapLayers = new Array();
var map;

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
	}
}

function pageSetup()
{
	var mapHeight = 0.8 * $(window).height();
	var mapWidth = 0.8 * $(window).width();

	$("#imageIdHyperlink").css("width", mapWidth);
	$("#imageIdHyperlink").css("text-align", "center");
	$("#imageIdHyperlink").position
	({
		my: "middle top",
		at: "middle top",
		of: $(window),
		offset: "0 25"
	});

	$("#acquisitionDateText").css("width", mapWidth);
	$("#acquisitionDateText").css("text-align", "center");
	$("#acquisitionDateText").position
	({
		my: "middle top",
		at: "middle bottom",
		of: $("#imageIdHyperlink"),
		offset: "0 0"
	});

	$("#map").css("height", mapHeight);
	$("#map").css("width", mapWidth);
	$("#map").position
	({
		my: "middle top",
		at: "middle bottom",
		of: $("#acquisitionDateText"),
		offset: "0 0"
	});
	
	$("#slider").css("width", $("#map").width());
	$("#slider").position
	({
		my: "middle top",
		at: "middle bottom",
		of: $("#map"),
		offset: "0 0"
	});
	$("#slider").slider();

	$("#fastForwardButton").button(
	{
		icons: {primary: "ui-icon-seek-next"},
		text: false
		}).click( function()
		{
			fastForward();
		}
	);
	$("#playForwardButton").button(
	{
		icons: {primary: "ui-icon-triangle-1-e"},
		text: false
	});
	$("#playReverseButton").button(
	{
		icons: {primary: "ui-icon-triangle-1-w"},
		text: false
	});
	$("#rewindButton").button(
	{
		icons: {primary: "ui-icon-seek-prev"},
		text: false
	});
	$("#stopButton").button(
	{
		icons: {primary: "ui-icon-stop"},
		text: false
	});
}

function updateText()
{
	$("#imageIdHyperlink").html(imageIds[currentLayer]);
	$("#acquisitionDateText").html(acquisitionDates[currentLayer]);
}
