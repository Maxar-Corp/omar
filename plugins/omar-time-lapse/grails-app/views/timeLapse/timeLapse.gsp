<html>
	<head>
		<meta content = "timeLapseLayout" name = "layout">
	</head>
	<body>
		<content tag = "top"></content>
		<content tag = "center">
			<div id = "pageContainer">
				<div id = "imageIdHyperlink"></div>
				<div id = "map"></div>
				<button id = "rewindButton">Rewind</button>
				<button id = "playReverseButton">Play Reverse</button>
				<button id = "stopButton">Stop</button>
				<button id = "playForwardButton">Play Forward</button>
				<button id = "fastForwardButton">Fast Forward</button>
			</div>
		</content>
		<content tag = "bottom"></content>
		<r:script>
			var bbox = "";//${bbox};
			var imageId = "";//${imageId};
			OpenLayers.ImgPath = "${resource(plugin: 'openlayers', dir: 'js/img')}/";
		</r:script>
	</body>
</html>
