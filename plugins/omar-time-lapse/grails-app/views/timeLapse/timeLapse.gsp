<html>
	<head>
		<meta content = "timeLapseLayout" name = "layout">
	</head>
	<body>
		<content tag = "top"></content>
		<content tag = "center">
			<div id = "imageIdHyperlink">Image Id Hyperlink</div>
			<div id = "acquisitionDateText">Acquisition Date Text</div>
			<div id = "map"></div>
			<div id = "slider"></div>
			<%--<div id = "movieControlsDiv">--%>
				<button id = "rewindButton">Rewind</button>
				<div id = "playControls">
					<input id = "playReverseButton" name = "radio" type = "radio"/>
						<label for = "playReverseButton">Play Reverse</label>
					<input checked = "checked" id = "stopButton" name = "radio" type = "radio"/>
						<label for = "stopButton">Stop</label>
					<input id = "playForwardButton" name = "radio" type = "radio"/>
						<label for = "playForwardButton">Play Forward</label>
				</div>
				<button id = "fastForwardButton">Fast Forward</button>
			<%--</div>--%>
			<div id = "wmsUrlBase">${createLink(action: "wms", controller: "ogc")}</div>
		</content>
		<content tag = "bottom"></content>
		<r:script>
			var acquisitionDates = ["${(acquisitionDates).join("\",\"")}"];
			var bbox = new OpenLayers.Bounds(${bbox});
			var imageIds = ["${(imageIds).join("\",\"")}"];
			var indexIds = ["${(indexIds).join("\",\"")}"];
			OpenLayers.ImgPath = "${resource(plugin: 'openlayers', dir: 'js/img')}/";
		</r:script>
	</body>
</html>
