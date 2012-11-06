<html>
	<head>
		<meta content = "timeLapseLayout" name = "layout">
		<script src = "http://openlayers.org/dev/OpenLayers.js"></script>
	</head>
	<body>
		<content tag = "top"></content>
		<content tag = "center">
			<div id = "map"></div>
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
			<button id = "exportLinkButton">Export Link</button>
			<button id = "exportImageButton">Export Image</button>
			<button id = "exportTimeLapseButton">Export Time Lapse</button>
			<button id = "deleteImageFromTimeLapseButton">Delete Image From Time Lapse</button>
			<button id = "reverseTimeLapseOrderButton">Reverse Time Lapse Order</button>
			
			<div id = "exportLinkDialog" title = "Export Link"></div>
			<div id = "wmsUrlBase">${createLink(action: "wms", controller: "ogc")}</div>
		</content>
		<content tag = "bottom"></content>
		<r:script>
			var acquisitionDates = ["${(acquisitionDates).join("\",\"")}"];
			var bbox = new OpenLayers.Bounds(${bbox});
			var coordConvert = new CoordinateConversion();
			var countryCodes = ["${(countryCodes).join("\",\"")}"];
			var exportImageUrlBase = "${createLink(action: 'index', controller: 'templateExport')}";
			var exportLinkUrlBase = "${createLink(absolute: 'true', action: 'timeLapse', base: grailsApplication.config.omar.serverURL)}";
			var imageIds = ["${(imageIds).join("\",\"")}"];
			var indexIds = ["${(indexIds).join("\",\"")}"];
			var urlBase = "${grailsApplication.config.omar.serverBase}";
		</r:script>
	</body>
</html>
