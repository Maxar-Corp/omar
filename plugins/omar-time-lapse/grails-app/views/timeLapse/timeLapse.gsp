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
			<button id = "rewindButton">Rewind</button>
			<button id = "playReverseButton">Play Reverse</button>
			<button id = "stopButton">Stop</button>
			<button id = "playForwardButton">Play Forward</button>
			<button id = "fastForwardButton">Fast Forward</button>
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
