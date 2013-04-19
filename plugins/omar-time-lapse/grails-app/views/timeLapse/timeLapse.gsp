<%@ page contentType="text/html;charset=UTF-8" %>
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
			
			<button id = "timeLapseSummaryButton">Time Lapse Summary</button>
		
			<input id = "playReverseButton" name = "radio" type = "radio"/>
				<label for = "playReverseButton" id = "playReverseButtonLabel">Play Reverse</label>
			<button id = "stepBackButton">Step Back</button>
			<input checked = "checked" id = "stopButton" name = "radio" type = "radio"/>
				<label for = "stopButton" id = "stopButtonLabel">Stop</label>
			<button id = "stepForwardButton">Step Forward</button>
			<input id = "playForwardButton" name = "radio" type = "radio"/>
				<label for = "playForwardButton" id = "playForwardButtonLabel">Play Forward</label>

			<button id = "slowDownButton">Slow Down</button>
			<button id = "speedUpButton">Speed Up</button>

			<div id = "exportAnimationDialog" title = "Export Animation">
				<table>
					<tr>
						<td><b>View</b>&nbsp;&nbsp;&nbsp;</td>
						<td>
							<select id = "exportAnimationDialogViewTypeSpinner">
								<option value = "ortho">ORTHO</option>
								<option value = "up">UP</option>
							</select>
						</td>
					</tr>
					<tr>
						<td><b>File Type:</b>&nbsp;&nbsp;&nbsp;</td>
						<td>
							<select id = "exportAnimationDialogFileTypeSpinner">
								<option value = "gif">GIF</option>
								<option selected value = "pdf">PDF</option>      
							</select>
						</td>
					</tr>
				</table>
			</div>
		
			<div id = "exportImageDialog" title = "Export Image">
				<b>View:</b>&nbsp;&nbsp;&nbsp;
				<select id = "exportImageDialogViewTypeSpinner">
					<option value = "ortho">ORTHO</option>
					<option value = "up">UP</option>
				</select>
			</div>

			<div id = "exportLinkDialog" title = "Export Link">
				<div>Right-click the link below to copy:</div>
				<br>
				<div id = "exportLinkDialogLinkDiv"></div>
			</div>
			
			<div id = "timeLapseSummaryDialog" title = "Time Lapse Summary"></div>

			<form id = "exportForm" action = "${createLink(action: 'index', controller: 'templateExport')}" method = "post" target = "_blank">
				<input id = "countryCodeFormInput" name = "countryCode" type = "hidden"/>
				<input id = "footerAcquisitionDateTextFormInput" name = "footerAcquisitionDateText" type = "hidden"/>
				<input id = "footerLocationTextFormInput" name = "footerLocationText" type = "hidden"/>
				<input id = "footerSecurityClassificationTextFormInput" name = "footerSecurityClassificationText" type = "hidden"/>
				<input id = "formatFormInput" name = "format" type = "hidden"/>
				<input id = "headerDescriptionTextFormInput" name = "headerDescriptionText" type = "hidden"/>
				<input id = "headerSecurityClassificationTextFormInput" name = "headerSecurityClassificationText" type = "hidden"/>
				<input id = "headerTitleTextFormInput" name = "headerTitleText" type = "hidden"/>
				<input id = "imageUrlFormInput" name = "imageUrl" type = "hidden"/>
				<input id = "northAngleFormInput" name = "northAngle" type = "hidden"/>
			</form>
		</content>
		<content tag = "bottom"></content>
		<r:script>
			var coordConvert = new CoordinateConversion();
			var exportImageUrlBase = "${createLink(action: 'index', controller: 'templateExport')}";
			var exportLinkUrlBase = "${createLink(absolute: 'true', action: 'viewer', base: grailsApplication.config.omar.serverURL)}";
			var groundSpaceUrl = "${createLink(action: 'index', controller: 'mapView')}";
			var groundToImageUrl = "${createLink(action: 'groundToImage', controller: 'imageSpace')}";
			var icon = "${resource(dir: 'js/img/', file: 'marker-blue.png', plugin: 'openlayers')}";
			var imageUrlBase = "${createLink(absolute: true, action: "wms", base: grailsApplication.config.omar.serverURL, controller: "ogc" )}";
			var imageSpaceChipUrl = "${createLink(absolute: true, action: "getTile", base: grailsApplication.config.omar.serverURL, controller: "icp" )}";	
			var timeLapseObject = ${timeLapseObject};
		</r:script>
	</body>
</html>
