<html>
	<head>
		<meta content = "timeLapseLayout" name = "layout">
	</head>
	<body class = "yui-skin-sam">
		<content tag = "top"></content>
		<content tag = "center">
			<g:render template="timeLapseMenu"/>
			<div id = "map"></div>
			<div id = "imageIdHyperlinkDiv">Image Id Hyperlink</div>
			<div id = "acquisitionDateTextDiv">Acquisition Date Text</div>
			
			<div id = "mapCoordinatesDiv">Map Coordinates</div>
			<div id = "timeLapseSlider"></div>
			
			<span id = "timeLapsePlayControlsSpan">
				<input id = "playReverseButton" name = "radio" type = "radio"/>
					<label for = "playReverseButton">Play Reverse</label>
				<button id = "stepBackButton">Step Back</button>
				<input checked = "checked" id = "stopButton" name = "radio" type = "radio"/>
					<label for = "stopButton">Stop</label>
				<button id = "stepForwardButton">Step Forward</button>
				<input id = "playForwardButton" name = "radio" type = "radio"/>
					<label for = "playForwardButton">Play Forward</label>
			</span>

			<button id = "timeLapseSummaryButton">Time Lapse Summary</button>
			<button id = "slowDownButton">Slow Down</button>
			<button id = "speedUpButton">Speed Up</button>
		
			<div id = "exportLinkDialog" title = "Export Link"></div>
			
			<div id = "timeLapseSummaryDialog" title = "Time Lapse Summary"></div>

			<form id = "submitForm" method = "post"></form>
		</content>
		<content tag = "bottom"></content>
		<r:script>
			var coordConvert = new CoordinateConversion();
			var exportImageUrlBase = "${createLink(action: 'index', controller: 'templateExport')}";
			var exportLinkUrlBase = "${createLink(absolute: 'true', action: 'timeLapse', base: grailsApplication.config.omar.serverURL)}";
			var exportTimeLapseGifUrlBase = "${createLink(action: 'exportTimeLapseGif', controller: 'timeLapse')}";
			var exportTimeLapsePdfUrlBase = "${createLink(action: 'exportTimeLapsePdf', controller: 'timeLapse')}";
			var groundSpaceUrl = "${createLink(action: 'index', controller: 'mapView')}";
			var groundToImageUrl = "${createLink(action: 'groundToImage', controller: 'imageSpace')}";
			var icon = "${resource(dir: 'js/img/', file: 'marker-blue.png', plugin: 'openlayers')}";
			var imageUrlBase = "${createLink(absolute: true, action: "wms", base: grailsApplication.config.omar.serverURL, controller: "ogc" )}";
			var imageSpaceChipUrl = "${createLink(absolute: true, action: "getTile", base: grailsApplication.config.omar.serverURL, controller: "icp" )}";	
			var timeLapseObject = ${timeLapseObject};
		</r:script>
	</body>
</html>
