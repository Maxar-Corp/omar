var cacheRead;
var cacheWrite;
var currentLayer; 
var dom = YAHOO.util.Dom;
var map;
var mapSpinner;
var movieAdvance;
var playDirection;
var playSpeed = 1000;

$(document).ready
(
	function () 
	{ 
		Event = YAHOO.util.Event;
		Event.onDOMReady
		( 
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

		setupTimeLapseButtons();
		positionElementsOnPage();
		setupDialogs();
		setupMap();
		currentLayer = timeLapseObject.layers.length - 1;
		fastForward();
	}
);

function deleteImageFromTimeLapse()
{
	rewind();
	if (currentLayer == 0) { timeLapseObject.layers.splice(0, 1); currentLayer--; }
	else { timeLapseObject.layers.splice(currentLayer + 1, 1); }
	updateMapSlider();
	for (var i = 0; i < timeLapseObject.layers.length; i++) { timeLapseObject.layers[i].id = i; }
	rewind();
	fastForward();
}

//function exportImage()
//{
//	var exportImageUrl = exportImageUrlBase;
//	exportImageUrl += "?acquisitionDate=" + acquisitionDates[currentLayer];
//	exportImageUrl += "&countryCode=" + countryCodes[currentLayer];
//	exportImageUrl += "&imageId=" + imageIds[currentLayer];

//	var imageUrl = mapLayers[currentLayer].getURL(map.getExtent());
//	imageUrl = imageUrl.replace(/&/g, "%26");
//	exportImageUrl += "&imageURL=" + imageUrl;
	
//	var centerGeo = coordConvert.ddToDms(map.getCenter().lat, map.getCenter().lon);
//	var centerMgrs = coordConvert.ddToMgrs(map.getCenter().lat, map.getCenter().lon);
//	exportImageUrl += "&centerGeo=GEO: " + centerGeo + " MGRS: " + centerMgrs;
//	exportImageUrl += "&northArrowAngle=0";
	
//	if (markerLayer.features.length > 0)
//	{
//		exportImageUrl += "&markers=";
//		var markerLocationArray = new Array();
//		for (var i = 0; i < markerLayer.features.length; i++)
//		{
//			var markerGeometry = markerLayer.features[i].geometry;
//			var markerPoint = map.getPixelFromLonLat(new OpenLayers.LonLat(markerGeometry.x, markerGeometry.y));
//			markerLocationArray[2 * i] = markerPoint.x - (markerSize.w/2);
//			markerLocationArray[2 * i +1] = markerPoint.y - markerSize.h;
//		}
//		exportImageUrl += markerLocationArray.join(",");
//	}
//	window.open(exportImageUrl);
//}

//function exportLink()
//{
//	var exportLinkUrl = exportLinkUrlBase;
//	exportLinkUrl += "?imageIds=" + indexIds.join(",");
//	exportLinkUrl += "&bbox=" + map.calculateBounds().toArray();
//
//	$("#exportLinkDialog").html("Right-click the link below to copy:<br><br><a href='" + exportLinkUrl + "' target = '_blank'><b>OMAR Time Lapse Link</b></a>");
//	$("#exportLinkDialog").dialog("open");
//}
//
//function exportTimeLapseGif()
//{
//	var imageUrlsForGif = new Array();
//	for (var i = 0; i < imageIds.length; i++)
//	{
//		imageUrlsForGif[i] = mapLayers[i].getURL(map.getExtent());
//		imageUrlsForGif[i] = imageUrlsForGif[i].replace(/&/g, "%26");
//	}
//	var exportTimeLapseUrl = exportTimeLapseGifUrlBase;
//	exportTimeLapseUrl += "?imageUrls=" + imageUrlsForGif.join(">");
//	$("#submitForm").get(0).action = exportTimeLapseUrl;
//	$("#submitForm").get(0).submit();
//}

//function exportTimeLapsePdf()
//{
//	var imageUrlsForPdf = new Array();
//	for (var i = 0; i < imageIds.length; i++) 
//	{ 
//		imageUrlsForPdf[i] = mapLayers[i].getURL(map.getExtent()); 
//		imageUrlsForPdf[i] = imageUrlsForPdf[i].replace(/&/g, "%26");
//	}
//	var exportTimeLapseUrl = exportTimeLapsePdfUrlBase;
//	exportTimeLapseUrl += "?imageUrls=" + imageUrlsForPdf.join(">");
//	$("#submitForm").get(0).action = exportTimeLapseUrl;
//	$("#submitForm").get(0).submit();
//}

function fastForward()
{
	timeLapseObject.layers[currentLayer].mapLayer.setVisibility(false);
	currentLayer++;
	if (currentLayer > timeLapseObject.layers.length - 1) { currentLayer = 0; }
	timeLapseObject.layers[currentLayer].mapLayer.setVisibility(true);
	if (timeLapseObject.layers[currentLayer].layerLoaded == 0) { generateMapSpinner(); }
	updateProgressSlider();
	updateText();
}

function generateMapSpinner()
{
	var target = document.getElementById("map");
	if (mapSpinner) { mapSpinner.spin(target); }
	else
	{
		var options = 
		{
			className: "spinner", color: "#000000", corners: 1, hwaccel: false, left: "auto", lines: 13,
			radius: 10, rotate: 0, shadow: false, speed: 1, top: "auto", trail: 60, width: 4, zIndex: 2e9
		};
		mapSpinner = new Spinner(options).spin(target);
	}
}

//function getUpIsUpImageChipUrl()
//{
//	var currentMapBounds = map.calculateBounds().toArray();
//	var mapCornersInOrder = new Object();
//	mapCornersInOrder.latitude = [currentMapBounds[3], currentMapBounds[3], currentMapBounds[1], currentMapBounds[1]];
//	mapCornersInOrder.longitude = [currentMapBounds[0], currentMapBounds[2], currentMapBounds[0], currentMapBounds[2]];

//	var mapBoundsInPixelPositions = new Object();
//	mapBoundsInPixelPositions.latitude = new Array();
//	mapBoundsInPixelPositions.longitude = new Array();
//	for (var i = 0; i < 3; i++)
//	{
//		var request = OpenLayers.Request.POST
//		({
//			async: false, 
//			url: groundToImageUrl,
//			data: '{"id":' + entryIds[currentLayer] + ',"groundPoints":[{"lat":' + mapCornersInOrder.latitude[i] + ',"lon":' + mapCornersInOrder.longitude[i] + '}]}',
//			callback: function (data)
//			{
//				var dataJson = $.parseJSON(data.responseText);
//				mapBoundsInPixelPositions.latitude[i] = dataJson[0].y;
//				mapBoundsInPixelPositions.longitude[i] = dataJson[0].x;
//			}
//		});	
//	}

//	var request = OpenLayers.Request.POST
//	({
//		async: false,
//		url: groundToImageUrl,
//		data: '{"id":' + entryIds[currentLayer] + ',"groundPoints":[{"lat":' + map.getCenter().lat + ', "lon":' + map.getCenter().lon + '}]}',
//		callback: function (data)
//		{
//			var dataJson = $.parseJSON(data.responseText);
//			mapBoundsInPixelPositions.latitude[4] = dataJson[0].y;
//			mapBoundsInPixelPositions.longitude[4] = dataJson[0].x;
//		}
//	});

//	var resolutionX = Math.sqrt(Math.pow((mapBoundsInPixelPositions.longitude[0] - mapBoundsInPixelPositions.longitude[1]),2) + Math.pow((mapBoundsInPixelPositions.latitude[0] - mapBoundsInPixelPositions.latitude[1]),2)) / map.getSize().w;
//	var resolutionY = Math.sqrt(Math.pow((mapBoundsInPixelPositions.longitude[0] - mapBoundsInPixelPositions.longitude[2]),2) + Math.pow((mapBoundsInPixelPositions.latitude[0] - mapBoundsInPixelPositions.latitude[2]),2)) / map.getSize().h;
//	var resolution = Math.max(resolutionX, resolutionY);
//	var scale = 1 / (resolution);
//	var xCenter = scale * mapBoundsInPixelPositions.longitude[4];
//	var x = xCenter - map.getSize().w / 2;
//	var yCenter = scale * mapBoundsInPixelPositions.latitude[4];
//	var y = yCenter - map.getSize().h / 2;
//	var upIsUpImageChipUrl = imageSpaceChipUrl + "?height=" + map.getSize().h + "&scale=" + scale + "&width=" + map.getSize().w + "&x=" + x + "&y=" + y + "&id=" + entryIds[currentLayer] + "&sharpen_mode=none&interpolation=bilinear&brightness=0&contrast=1&stretch_mode=linear_auto_min_max&stretch_mode_region=global&bands=default";
//	alert(upIsUpImageChipUrl);
//}

function highlightTableRow(row) { row.style.backgroundColor = "#add8e6"; }

function positionElementsOnPage()
{
	var mapHeight = 0.75 * $(window).height();
	var mapWidth = 0.90 * $(window).width();

	$("#map").css("height", mapHeight);
	$("#map").css("width", mapWidth);
	$("#map").position({ my: "center top", at: "center top", of: $("#timeLapseMenu"), offset: "0 50", collision: "none" });

	$("#imageIdHyperlinkDiv").css("width", 0.5 * mapWidth);
	$("#imageIdHyperlinkDiv").css("text-align", "left");
	$("#imageIdHyperlinkDiv").position({ my: "left bottom", at: "left top", of: $("#map"), offset: "0 0", collision: "none" });

	$("#acquisitionDateTextDiv").css("width", 0.5 * mapWidth);
	$("#acquisitionDateTextDiv").css("text-align", "right");
	$("#acquisitionDateTextDiv").position({ my: "right bottom", at: "right top", of: $("#map"), offset: "0 0", collision: "none" });
	
	$("#mapCoordinatesDiv").css("width", mapWidth);
	$("#mapCoordinatesDiv").position({ my: "center top", at: "center bottom", of: $("#map"), offset: "0 5", collision: "none" });

	$("#timeLapseSlider").css("width", mapWidth);
	$("#timeLapseSlider").position({ my: "center top", at: "center bottom", of: $("#mapCoordinatesDiv"), offset: "0 5", collision: "none" });
	updateMapSlider();

	$("#timeLapsePlayControlsSpan").position({ my: "center top", at: "center bottom", of: $("#timeLapseSlider"), offset: "0 20", collision: "none" });

	$("#timeLapseSummaryButton").position({ my: "right top", at: "left top", of: $("#playReverseButton"), offset: "-30 0", collision: "none" });

	$("#slowDownButton").position({ my: "left top", at: "right top", of: ("#playForwardButton"), offset: "30 0" });

	$("#speedUpButton").position({ my: "left top", at: "right top", of: ("#slowDownButton"), offset: "5 0" });
}

function playMovie()
{
	if (playDirection == "forward") { fastForward(); }
	else if (playDirection == "reverse") { rewind(); }
	movieAdvance = setTimeout("playMovie()", playSpeed);
}

function reverseTimeLapseOrder()
{
	timeLapseObject.layers.reverse();
	for (var i = 0; i < timeLapseObject.layers.length; i++) { timeLapseObject.layers[i].id = i; }
	rewind();
	fastForward();
}

function rewind()
{
	timeLapseObject.layers[currentLayer].mapLayer.setVisibility(false);
	currentLayer--;
	if (currentLayer < 0) { currentLayer = timeLapseObject.layers.length - 1; }
	timeLapseObject.layers[currentLayer].mapLayer.setVisibility(true);
	if (timeLapseObject.layers[currentLayer].layerLoaded == 0) { generateMapSpinner(); }
	updateProgressSlider();
	updateText();
}

function setupDialogs()
{	
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

	$("#exportLinkDialog").dialog({ autoOpen: false, width: "auto" });

	$("#timeLapseSummaryDialog").dialog({ autoOpen: false, width: "auto" });
}

function setupMap()
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

	var bbox = new OpenLayers.Bounds(timeLapseObject.bbox[0], timeLapseObject.bbox[1], timeLapseObject.bbox[2], timeLapseObject.bbox[3]);
	var center = bbox.getCenterLonLat();
	var zoom = map.getZoomForExtent(bbox);
	map.setCenter(center, zoom);
	
	$.each
	(
		timeLapseObject.layers,
		function(i, x)
		{			
			x.mapLayer = new OpenLayers.Layer.WMS
			(
				"Layer" + i,
				imageUrlBase,
				{	
					bands: "default",
					brightness: 0,
					contrast: 1,
					format: "image/jpeg",
					interpolation: "bilinear",
					layers: x.indexId,
					sharpen_mode: "none",
					stretch_mode: "linear_auto_min_max",
					stretch_mode_region: "viewport"
				},
				{
					isBaseLayer: false,
					ratio: 1,
					singleTile: true,
					transitionEffect: "resize"
				}
			);

			x.mapLayer.id = i;
			x.mapLayer.loadEnd = function()
			{
				console.dir(this);
				x.layerLoaded = 1;
				if (mapSpinner && this.id == currentLayer) { mapSpinner.stop(); }
			};
			x.mapLayer.loadStart = function()
			{
				x.layerLoaded = 0;
				generateMapSpinner();
			};
			x.mapLayer.events.register("loadend", x.mapLayer, function() { this.loadEnd(); });
			x.mapLayer.events.register("loadstart", x.mapLayer, function() { this.loadStart(); });
			map.addLayer(x.mapLayer);
			x.mapLayer.setVisibility(false);

		}
	);
		
	map.events.register("mousemove", map, function(event) { updateMapCoordinates(event); });
//	map.events.register("moveend", map, function() { theMapHasMoved(); });
//	map.events.register("zoomend", map, function() { theMapHasZoomed(); });

	cacheWrite = new OpenLayers.Control.CacheWrite
	({
		autoActivate: true,
		imageFormat: "image/jpeg"
	});
	cacheRead = new OpenLayers.Control.CacheRead();
	map.addControls([cacheWrite, cacheRead]);
}

function setupTimeLapseButtons()
{
	$("#playForwardButton").button({ icons: {primary: "ui-icon-triangle-1-e"}, text: false}).click
	(
		function()
		{
			playDirection = "forward";
			stopMovie();
			playMovie();
		}
	); 

	$("#playReverseButton").button({ icons: {primary: "ui-icon-triangle-1-w"}, text: false }).click
        (
                function()
                {
                        playDirection = "reverse";
                        stopMovie();
                        playMovie();
                }
        );

	$("#slowDownButton").button({ icons: {primary: "ui-icon-circle-minus"}, text: false }).click(function() { slowDown(); });

	$("#speedUpButton").button({ icons: {primary: "ui-icon-circle-plus"}, text: false }).click(function() { speedUp(); });

        $("#stepBackButton").button({ icons: {primary: "ui-icon-seek-prev"}, text: false }).click(function() { rewind(); });

        $("#stepForwardButton").button({ icons: {primary: "ui-icon-seek-next"}, text: false }).click(function() { fastForward(); });

	$("#stopButton").button({ icons: {primary: "ui-icon-stop"}, text: false }).click(function() { stopMovie(); });

       $("#timeLapseSummaryButton").button({ icons: {primary: "ui-icon-script"}, text: false }).click(function() { timeLapseSummary(); });
}

function skipToImage(layerIndex)
{
	timeLapseObject.layers[layerIndex].mapLayer.setVisibility(false);
	currentLayer = layerIndex;
	timeLapseObject.layers[currentLayer].mapLayer.setVisibility(true);
	rewind();
	fastForward();
}

function slowDown() { if (playSpeed < 4000) { playSpeed *= 2; } }

function speedUp() { if (playSpeed > 500) { playSpeed /= 2; } }

function stopMovie() { clearTimeout(movieAdvance); }

//function theMapHasMoved()
//{
//	for (var i = 0; i < imageIds.length; i++) { fastForward(); }
//}

//function theMapHasZoomed()
//{
//	for (var i = 0; i < imageIds.length; i++) { fastForward(); }
//}

function timeLapseSummary()
{
	var cell, row, table;
	$("#timeLapseSummaryDialog").html("<table id = 'timeLapseSummaryTable'></table>");
	table = document.getElementById("timeLapseSummaryTable");

	row = table.insertRow(0);
	cell = row.insertCell(0); 
	$(cell).append("<b>No.</b>&nbsp;&nbsp;&nbsp;");

	cell = row.insertCell(1);
	$(cell).append("<b>Image Id</b>&nbsp;&nbsp;&nbsp;");

	cell = row.insertCell(2);
	$(cell).append("<b>Acquisition Date</b>&nbsp;&nbsp;&nbsp;");

	cell = row.insertCell(3);
	$(cell).append("<b>NIIRS</b>&nbsp;&nbsp;&nbsp;");

	cell = row.insertCell(4);
	$(cell).append("<b>CC</b>&nbsp;&nbsp;&nbsp;");

	var bbox = map.calculateBounds().toArray();
	var center = map.getCenter();
	$.each
	(
		timeLapseObject.layers,
		function(i, x)
		{
			row = table.insertRow(i + 1);
			row.onclick = function() { skipToImage(i); };
			row.onmouseover = function() { highlightTableRow(this); };
			row.onmouseout = function() { unhighlightTableRow(this); };

			cell = row.insertCell(0);
			$(cell).append((i + 1) + "&nbsp;&nbsp;&nbsp;");

			cell = row.insertCell(1);
			$(cell).append
			(
				"<a " + 
					"href = '" + groundSpaceUrl + "?layers=" + x.indexId + "&latitude=" + center.lat + "&longitude=" + center.lon + "&bbox=" + bbox + 
					"' target = '_blank'" +
				">" + 
						x.imageId + 
				"</a>&nbsp;&nbsp;&nbsp;"
			);

			cell = row.insertCell(2);
			$(cell).append(x.acquisitionDate + "&nbsp;&nbsp;&nbsp;");

			cell = row.insertCell(3);
			$(cell).append(x.niirs + "&nbsp;&nbsp;&nbsp;");

			cell = row.insertCell(4);
			$(cell).append(x.countryCode + "&nbsp;&nbsp;&nbsp;");
		}
	);


	$("#timeLapseSummaryDialog").append("<br>");
	$("#timeLapseSummaryDialog").append
	(
		"<b>Map Center:</b>&nbsp;&nbsp;" +
			map.getCenter().lat + ", " + map.getCenter().lon + " // " +
			coordConvert.ddToDms(map.getCenter().lat, map.getCenter().lon) + " // " +
			coordConvert.ddToMgrs(map.getCenter().lat, map.getCenter().lon)
	);

	var leftCenterPoint = new OpenLayers.Geometry.Point(map.getCenter().lon - map.calculateBounds().getWidth()/2, map.getCenter().lat);
	var rightCenterPoint = new OpenLayers.Geometry.Point(map.getCenter().lon + map.calculateBounds().getWidth()/2, map.getCenter().lat);
	var longitudeLine = new OpenLayers.Geometry.LineString([leftCenterPoint, rightCenterPoint]);
	var longitudeDistance = parseInt(longitudeLine.getGeodesicLength(new OpenLayers.Projection("EPSG:4326")));

	var topMiddlePoint = new OpenLayers.Geometry.Point(map.getCenter().lon, map.getCenter().lat + map.calculateBounds().getHeight()/2);
	var bottomMiddlePoint = new OpenLayers.Geometry.Point(map.getCenter().lon, map.getCenter().lat - map.calculateBounds().getHeight()/2);
	var latitudeLine = new OpenLayers.Geometry.LineString([topMiddlePoint, bottomMiddlePoint]);
	var latitudeDistance = parseInt(latitudeLine.getGeodesicLength(new OpenLayers.Projection("EPSG:4326")));
	
	$("#timeLapseSummaryDialog").append("<br>");
	$("#timeLapseSummaryDialog").append("<b>Map Dimensions:</b>&nbsp;&nbsp;" + longitudeDistance + "m x " + latitudeDistance + "m (approx.)");

	$("#timeLapseSummaryDialog").css("textAlign", "left");
	$("#timeLapseSummaryDialog").dialog("open");
}

function unhighlightTableRow(row) { row.style.backgroundColor = "#ffffff"; }

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

function updateProgressSlider() { $("#timeLapseSlider").slider("value", currentLayer); }

function updateMapSlider()
{
	$("#timeLapseSlider").slider
	({
		max: timeLapseObject.layers.length - 1,
		min: 0,
		range: "min",
		slide: function(event, ui)
		{
			if (ui.value > currentLayer) { fastForward(); }
			else { rewind(); }
		}
	});
}


function updateText()
{
	var bbox = map.calculateBounds().toArray();
	var center = map.getCenter();

	$("#imageIdHyperlinkDiv").html("<a href = '" + groundSpaceUrl + "?layers=" + timeLapseObject.layers[currentLayer].indexId + 
		"&latitude=" + center.lat + "&longitude=" + center.lon +
		"&bbox=" + bbox + "' target = '_blank'>" + timeLapseObject.layers[currentLayer].imageId + "</a>");

	$("#acquisitionDateTextDiv").html(timeLapseObject.layers[currentLayer].acquisitionDate);
}
