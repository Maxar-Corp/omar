<html>
	<head>
		<meta content = "timeLapseLayout" name = "layout">
	
	</head>
	<body class = "yui-skin-sam">
		<content tag = "top"></content>
		<content tag = "center">
			<g:render template="timeLapseMenu"/>
			<div id = "map"></div>
			<div id = "mapCoordinatesDiv">Map Coordinates</div>
			<div id = "imageIdHyperlink">Image Id Hyperlink</div>
			<div id = "acquisitionDateText">Acquisition Date Text</div>
			
			<div id = "slider"></div>
			<span id = "movieControlsDiv">
				<button id = "rewindButton">Rewind</button>
				<span id = "playControls">
					<input id = "playReverseButton" name = "radio" type = "radio"/>
						<label for = "playReverseButton">Play Reverse</label>
					<input checked = "checked" id = "stopButton" name = "radio" type = "radio"/>
						<label for = "stopButton">Stop</label>
					<input id = "playForwardButton" name = "radio" type = "radio"/>
						<label for = "playForwardButton">Play Forward</label>
				</span>
				<button id = "fastForwardButton">Fast Forward</button>
			</span>
			<button id = "slowDownButton">Slow Down</button>
			<button id = "speedUpButton">Speed Up</button>
			
			<div id = "exportTimeLapseSummaryDialog" title = "Time Lapse Summary"></div>
			<div id = "exportLinkDialog" title = "Export Link"></div>

			<div id = "addMarkerDialog" title = "Add Marker">
				Latitude: <input id = "markerLatitudeInput" type = "text"/>
				<br>
				Longitude: <input id = "markerLongitudeInput" type = "text"/>
			</div>

			<form id = "submitForm" method = "post"></form>
		</content>
		<content tag = "bottom"></content>
		<r:script>
			var acquisitionDates = ["${(acquisitionDates).join("\",\"")}"];
			var bbox = new OpenLayers.Bounds(${bbox});
			var coordConvert = new CoordinateConversion();
			var countryCodes = ["${(countryCodes).join("\",\"")}"];
			var exportImageUrlBase = "${createLink(action: 'index', controller: 'templateExport')}";
			var exportLinkUrlBase = "${createLink(absolute: 'true', action: 'timeLapse', base: grailsApplication.config.omar.serverURL)}";
			var exportTimeLapseGifUrlBase = "${createLink(action: 'exportTimeLapseGif', controller: 'timeLapse')}";
			var exportTimeLapsePdfUrlBase = "${createLink(action: 'exportTimeLapsePdf', controller: 'timeLapse')}";
			var icon = "${resource(dir: 'js/img/', file: 'marker-blue.png', plugin: 'openlayers')}";
			var imageIds = ["${(imageIds).join("\",\"")}"];
			var imageUrlBase = "${createLink(absolute: true, action: "wms", base: grailsApplication.config.omar.serverURL, controller: "ogc" )}";
			var indexIds = ["${(indexIds).join("\",\"")}"];
			var niirsValues = ["${(niirsValues).join("\",\"")}"];
			var markers = [${(markers).join(",")}];
		</r:script>
	</body>
</html>
