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
		setupKeyboardShortcuts();

		$(window).resize(function() { positionElementsOnPage(); });
	}
);

function deleteImageFromTimeLapse()
{
	if (currentLayer == 0) 
	{
		rewind(); 
		timeLapseObject.layers.splice(0, 1); currentLayer--; 
	}
	else 
	{ 
		rewind();
		timeLapseObject.layers.splice(currentLayer + 1, 1); 
	}
	updateMapSlider();
	for (var i = 0; i < timeLapseObject.layers.length; i++) { timeLapseObject.layers[i].mapLayer.id = i; }
	rewind();
	fastForward();

	 if ($("#timeLapseSummaryDialog").dialog("isOpen")) { timeLapseSummary(); }
}

function exportAnimation()
{
	var fileType = $("#exportAnimationDialogFileTypeSpinner").val();
	var layerIndexArray = []; for (var i = 0; i < timeLapseObject.layers.length; i++) { layerIndexArray[i] = i; }
	var viewType = $("#exportAnimationDialogViewTypeSpinner").val();

	prepareExportArray(layerIndexArray, fileType, viewType);
}

function exportImage()
{
	var fileType = "png";
	var layerIndexArray = [currentLayer];
	var viewType = $("#exportImageDialogViewTypeSpinner").val();

	prepareExportArray(layerIndexArray, fileType, viewType);
}

function exportLink()
{
	var exportLinkUrl = exportLinkUrlBase;

	exportLinkUrl += "?layer=";
	var idArray = [];
	for (var i = 0; i < timeLapseObject.layers.length; i++) { idArray.push(timeLapseObject.layers[i].id); }
	exportLinkUrl += idArray.join(",");

	exportLinkUrl += "&bbox=" + map.calculateBounds().toArray();

	$("#exportLinkDialogLinkDiv").html
	(
		"<a " + 
			"href='" + exportLinkUrl + "' " + 
			"style = 'color: blue' " +
			"target = '_blank'><b>OMAR Time Lapse Link</b>" + 
		"</a>"
	);
	$("#exportLinkDialog").dialog("open");
}

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
			className: "spinner", color: "#ffffff", corners: 1, hwaccel: false, left: "auto", lines: 13,
			radius: 10, rotate: 0, shadow: true, speed: 1, top: "auto", trail: 60, width: 4, zIndex: 2e9
		};
		mapSpinner = new Spinner(options).spin(target);
	}
}

function getOrthoChipUrl(layerIndex)
{
	var imageChipUrl = timeLapseObject.layers[layerIndex].mapLayer.getURL(map.getExtent());
	imageChipUrl = imageChipUrl.replace(/&/g, "%26");

	return imageChipUrl;
}

function getUpChipUrl(layerIndex)
{
	var bbox = map.calculateBounds().toArray();
	var mapCoordinatesInOrder = {};
	mapCoordinatesInOrder.latitude = [bbox[3], bbox[3], bbox[1], bbox[1], map.getCenter().lat];
	mapCoordinatesInOrder.longitude = [bbox[0], bbox[2], bbox[0], bbox[2], map.getCenter().lon];

	var mapCoordinatesInPixelPositions = {};
	mapCoordinatesInPixelPositions.latitude = [];
	mapCoordinatesInPixelPositions.longitude = [];
	for (var i = 0; i < 5; i++)
	{
		var request = OpenLayers.Request.POST
		({
			async: false, 
			url: groundToImageUrl,
			data: '{"id":' + timeLapseObject.layers[layerIndex].id + 
				',"groundPoints":[{"lat":' + mapCoordinatesInOrder.latitude[i] + 
				',"lon":' + mapCoordinatesInOrder.longitude[i] + '}]}',
			callback: function (data)
			{
				var dataJson = $.parseJSON(data.responseText);
				mapCoordinatesInPixelPositions.latitude[i] = dataJson[0].y;
				mapCoordinatesInPixelPositions.longitude[i] = dataJson[0].x;
			}
		});	
	}

	var deltaX1 = mapCoordinatesInPixelPositions.longitude[0] - mapCoordinatesInPixelPositions.longitude[1];
	var deltaY1 = mapCoordinatesInPixelPositions.latitude[0] - mapCoordinatesInPixelPositions.latitude[1];
	var resolutionX = Math.sqrt(Math.pow(deltaX1,2) + Math.pow(deltaY1,2)) / map.getSize().w;

	var deltaX2 = mapCoordinatesInPixelPositions.longitude[0] - mapCoordinatesInPixelPositions.longitude[2];
	var deltaY2 = mapCoordinatesInPixelPositions.latitude[0] - mapCoordinatesInPixelPositions.latitude[2]
	var resolutionY = Math.sqrt(Math.pow(deltaX2,2) + Math.pow(deltaY2,2)) / map.getSize().h;
	
	var resolution = Math.max(resolutionX, resolutionY);
	var scale = 1 / (resolution);
	
	var xCenter = scale * mapCoordinatesInPixelPositions.longitude[4];
	var x = xCenter - map.getSize().w / 2;
	
	var yCenter = scale * mapCoordinatesInPixelPositions.latitude[4];
	var y = yCenter - map.getSize().h / 2;

	var upIsUpImageChipUrl = imageSpaceChipUrl + "?height=" + map.getSize().h + "&scale=" + scale + "&width=" + map.getSize().w + 
		"&x=" + x + "&y=" + y + "&rotate=-" + timeLapseObject.layers[layerIndex].upAngle + 
		"&pivot=" + mapCoordinatesInPixelPositions.longitude[4] + "," + mapCoordinatesInPixelPositions.latitude[4] +
		"&id=" + timeLapseObject.layers[layerIndex].id + "&sharpen_mode=none&interpolation=bilinear&brightness=0" + 
		"&contrast=1&stretch_mode=linear_auto_min_max&stretch_mode_region=viewport&bands=default";

	upIsUpImageChipUrl = upIsUpImageChipUrl.replace(/&/g, "%26");
	return upIsUpImageChipUrl;
}

function highlightTableRow(row) { row.style.backgroundColor = "yellow"; }

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

	$("#stopButtonLabel").position({ my: "center top", at: "center bottom", of: $("#timeLapseSlider"), offset: "0 10", collision: "none" });

	$("#stepBackButton").position({ my: "right top", at: "left top", of: $("#stopButtonLabel"), offset: "-5 0", collision: "none" });

	$("#playReverseButtonLabel").position({ my: "right top", at: "left top", of: $("#stepBackButton"), offset: "-5 0", collision: "none" });

	$("#timeLapseSummaryButton").position({ my: "right top", at: "left top", of: $("#playReverseButtonLabel"), offset: "-30 0", collision: "none" });

	$("#stepForwardButton").position({ my: "left top", at: "right top", of: $("#stopButtonLabel"), offset: "5 0", collision: "none" });

	$("#playForwardButtonLabel").position({ my: "left top", at: "right top", of: $("#stepForwardButton"), offset: "5 0", collision: "none" });

	$("#slowDownButton").position({ my: "left top", at: "right top", of: $("#playForwardButtonLabel"), offset: "30 0" });

	$("#speedUpButton").position({ my: "left top", at: "right top", of: $("#slowDownButton"), offset: "5 0" });
}

function playMovie()
{
	if (playDirection == "forward") { fastForward(); }
	else if (playDirection == "reverse") { rewind(); }
	movieAdvance = setTimeout("playMovie()", playSpeed);
}

function prepareExportArray(layerIndexArray, format, view)
{
	var footerAcquisitionDateTextArray = []; 
	var footerLocationTextArray = [];
	var footerSecurityClassificationTextArray = [];
	var headerDescriptionTextArray = [];
	var headerSecurityClassificationTextArray = [];
	var headerTitleTextArray = [];
	var imageUrlArray = [];
	var northAngleArray = [];

	$.each
	(
		layerIndexArray,
		function(i, x) 
		{
			footerAcquisitionDateTextArray[i] = timeLapseObject.layers[x].acquisitionDate;
			footerLocationTextArray[i] = "GEO: " + coordConvert.ddToDms(map.getCenter().lat, map.getCenter().lon) +
				" MGRS: " + coordConvert.ddToMgrs(map.getCenter().lat, map.getCenter().lon);
			footerSecurityClassificationTextArray[i] = "UNCLASS";
			headerDescriptionTextArray[i] = "Country: " + timeLapseObject.layers[i].countryCode;
			headerSecurityClassificationTextArray[i] = "UNCLASS";
			headerTitleTextArray[i] = timeLapseObject.layers[i].imageId;
			
			if (view == "ortho") 
			{ 
				imageUrlArray[i] = getOrthoChipUrl(x);
				northAngleArray[i] = 0; 
			}
			else if (view == "up") 
			{ 
				imageUrlArray[i] = getUpChipUrl(x); 
				northAngleArray[i] = timeLapseObject.layers[x].upAngle;
			}	
		}
	);

	$("#countryCodeFormInput").val(timeLapseObject.layers[0].countryCode);
	$("#footerAcquisitionDateTextFormInput").val(footerAcquisitionDateTextArray.join(","));
	$("#footerLocationTextFormInput").val(footerLocationTextArray.join(","));
	$("#footerSecurityClassificationTextFormInput").val(footerSecurityClassificationTextArray.join(","));
	$("#formatFormInput").val(format);
	$("#headerDescriptionTextFormInput").val(headerDescriptionTextArray.join(","));
	$("#headerSecurityClassificationTextFormInput").val(headerSecurityClassificationTextArray.join(","));
	$("#headerTitleTextFormInput").val(headerTitleTextArray.join(","));
	$("#imageUrlFormInput").val(imageUrlArray.join(">"));
	$("#northAngleFormInput").val(northAngleArray.join(","));

	$("#exportForm")[0].submit();	
}	

function reverseTimeLapseOrder()
{
	timeLapseObject.layers.reverse();
	for (var i = 0; i < timeLapseObject.layers.length; i++) { timeLapseObject.layers[i].id = i; }
	rewind();
	fastForward();

	 if ($("#timeLapseSummaryDialog").dialog("isOpen")) { timeLapseSummary(); }
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
	$("#exportAnimationDialog").dialog
	({
		autoOpen: false,
		buttons:
		{
			"Submit" : function() { $(this).dialog("close"); exportAnimation(); },
			"Cancel" : function() { $(this).dialog("close"); }
		},
		height: "auto",
		width: "auto"
	});
	
	$("#exportImageDialog").dialog
	({ 
		autoOpen: false, 
		buttons:
		{
			"Submit" : function() { $(this).dialog("close"); exportImage(); },
			"Cancel" : function() { $(this).dialog("close"); }
		},
		width: "auto" 
	});

	$("#exportLinkDialog").dialog({ autoOpen: false, width: "auto" });

	$("#timeLapseSummaryDialog").dialog({ autoOpen: false, width: "auto" });
}

function setupKeyboardShortcuts()
{
	$(document).keydown
	(
		function(event)
		{
			if (event.keyCode == 37) { rewind(); }
			else if (event.keyCode == 39) { fastForward(); }
		}	
	);
	return false;
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
					brightness: "0",
					contrast: "1",
					format: "image/jpeg",
					interpolation: "bilinear",
					layers: x.indexId,
					sharpen_mode: "none",
					stretch_mode: "linear_auto_min_max",
					stretch_mode_region: "viewport"
				},
				{
					isBaseLayer: false,
					ratio: "1",
					singleTile: true,
					transitionEffect: "resize"
				}
			);

			x.mapLayer.id = i;
			x.mapLayer.loadEnd = function()
			{
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

	//cacheWrite = new OpenLayers.Control.CacheWrite
	//({
	//	autoActivate: true,
	//	imageFormat: "image/jpeg"
	//});
	//cacheRead = new OpenLayers.Control.CacheRead();
	//map.addControls([cacheWrite, cacheRead]);
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

        $("#stepBackButton").button({ icons: {primary: "ui-icon-arrowthickstop-1-w"}, text: false }).click(function() { rewind(); });

        $("#stepForwardButton").button({ icons: {primary: "ui-icon-arrowthickstop-1-e"}, text: false }).click(function() { fastForward(); });

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

function sortLayers(sortingIndex)
{
	switch(sortingIndex)
	{
		case "Azimuth" : timeLapseObject.layers.sort(function(a, b)  { return a.azimuth - b.azimuth; });
	}
	
	if ($("#timeLapseSummaryDialog").dialog("isOpen")) { timeLapseSummary(); }
	for (var i = 0; i < timeLapseObject.layers.length; i++) { timeLapseObject.layers[i].mapLayer.id = i; }
	rewind();
	fastForward();
}

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

	cell = row.insertCell(5);
	$(cell).append("<b>Azimuth</b>&nbsp;&nbsp;&nbsp;");
	$(cell).css("cursor", "pointer");
	$(cell).click(function() { sortLayers("Azimuth"); });

	cell = row.insertCell(6);
	$(cell).append("<b>Graze</b>&nbsp;&nbsp;&nbsp;");

	var bbox = map.calculateBounds().toArray();
	var center = map.getCenter();
	$.each
	(
		timeLapseObject.layers,
		function(i, x)
		{
			row = table.insertRow(i + 1);
			if (i == currentLayer) { row.style.backgroundColor = "#add8e6"; }
			else 
			{
				row.onclick = function() { skipToImage(i); };
				row.onmouseover = function() { highlightTableRow(this); };
				row.onmouseout = function() { unhighlightTableRow(this); };
			}

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

			cell = row.insertCell(5);
			$(cell).append(parseFloat(x.azimuth).toFixed(2) + "&nbsp;&nbsp;&nbsp;");			

			cell = row.insertCell(6);
			$(cell).append(x.graze + "&nbsp;&nbsp;&nbsp;");
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
	$("#timeLapseSummaryDialog").find("a").first().blur();
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

	if ($("#timeLapseSummaryDialog").dialog("isOpen")) { updateTimeLapseSummary(); }
}

function updateTimeLapseSummary()
{
	var row;
	$.each
	(
		timeLapseObject.layers,
		function(i, x)
		{
			row = $("#timeLapseSummaryTable")[0].rows[i + 1];
			row.onclick = function() { skipToImage(i); };
			row.onmouseover = function() { highlightTableRow(this); };
			row.onmouseout = function() { unhighlightTableRow(this); };
			row.style.backgroundColor = "transparent";
		}
	);
	
	row = $("#timeLapseSummaryTable")[0].rows[currentLayer + 1];
	row.onclick = function() {};
	row.onmouseover = function() {};
	row.onmouseout = function() {};
	row.style.backgroundColor = "#add8e6";
}
