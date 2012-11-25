<html>
	<head>
		<meta content = "templateExportLayout" name = "layout">
	</head>
	<body>
		<content tag = "top"></content>
		<content tag = "center">
			<g:render template="templateExportMenu"/>
			<div id = "header" onClick = "changeColorGradient()"></div>
			<img id = "previewImage" onLoad = "init()" src = "${imageURL}"/>	
			<img id = "logoImage" onClick = "changeLogo()"/>
			<div id = "headerSecurityClassificationTextContainer" onClick = "changeHeaderSecurityClassificationText()"></div>
			<div id = "headerTitleTextContainer" onClick = "changeHeaderTitleText()"></div>
			<div id = "headerDescriptionTextContainer" onClick = "changeHeaderDescriptionText()"></div>
			<img id = "overviewMapImage" onClick = "changeOverviewMap()" onLoad = "positionOverviewMapImage(); positionNorthArrow()"/>
			<img id = "northArrowImage" onClick = "changeNorthArrow()" onLoad = "removeNorthArrowSpinner()"/>
			<div id = "northArrowSpinnerDiv"></div>

			<div id = "footer"></div>
			<div id = "footerSecurityClassificationTextContainer" onClick = "changeFooterSecurityClassificationText()"></div>
			<div id = "footerLocationTextContainer" onClick = "changeFooterLocationText()"></div>
			<div id = "footerAcquisitionDateTextContainer" onClick = "changeFooterAcquisitionDateText()"></div>
			<div id = "markerDiv"></div>
			
			<form id = "downloadForm" method = "post"></form>

			<div id = "changeColorGradientPopup" title = "Header/Footer Color Gradient">
				<table>
					<tr>
						<td><b>Top Color:</b></td>
						<td><input class = "color" id = "gradientColorTop" size = "6" value = "595454"/></td>
					</tr>
					<tr>
						<td><b>Bottom Color:</b></td>
						<td><input class = "color" id = "gradientColorBottom" size = "6" value = "000000"/></td>
					</tr>
				</table>
			</div>

			<div id = "changeLogoPopup" title = "Change Logo">
				<b>Logo:&nbsp;</b> <select id = "logo">
					<option value = "ciaLogo">CIA</option>
					<option value = "diaLogo">DIA</option>
					<option value = "dhsLogo">DHS</option>
					<option value = "dodLogo">DOD</option>
					<option value = "dosLogo">DOS</option>
					<option value = "ngaLogo">NGA</option>
					<option value = "nroLogo">NRO</option>
					<option value = "nsaLogo">NSA</option>
					<option value = "airForceLogo">Air Force</option>
					<option value = "armyLogo">Army</option>
					<option value = "coastGaurdLogo">Coast Gaurd</option>
					<option value = "marineCorpsLogo">Marine Corps</option>
					<option value = "navyLogo">Navy</option>
				</select>
			</div>

			<div id = "changeHeaderSecurityClassificationTextPopup" title = "Header Security Classification">
				<table>
					<tr>
						<td><b>Text:&nbsp;</b></td>
						<td><input id = "headerSecurityClassificationTextInput" type = "text" value = "${securityClassification}"/></td>
					</tr>
					<tr>	
						<td><b>Color:&nbsp;</b></td>
						<td><input class = "color" id = "headerSecurityClassificationTextColor" size = "6" value = "ADD8E6"/></td>
					</tr>
				</table>
			</div>

			<div id = "changeHeaderTitleTextPopup" title = "Title">
				<table>
					<tr>
						<td><b>Text:&nbsp;</b></td>
						<td><input id = "headerTitleTextInput" type = "text" value = "${imageId}"/></td>
					</tr>
					<tr>
						<td><b>Color:&nbsp;</b></td>
						<td><input class = "color" id = "headerTitleTextColor" size = "6" value = "FFFF00"/></td>
					</tr>
				</table>
			</div>

			<div id = "changeHeaderDescriptionTextPopup" title = "Description">
				<table>
					<tr>
						<td><b>Text:&nbsp;</b></td>
						<td><input id = "headerDescriptionTextInput" type = "text" value = "Country: ${countryCode}"/></td>
					</tr>
					<tr>
						<td><b>Color:&nbsp;</b></td>
						<td><input class = "color" id = "headerDescriptionTextColor" size = "6" value = "FFFFFF"/></td>
					</tr>
				</table>
			</div>

			<div id = "changeOverviewMapPopup" title = "Overview Map">
				<table>
					<tr>
						<td><b>Include:&nbsp;</b></td>
						<td><input id = "includeOverviewMapCheckbox" type = "checkbox"/></td>
					</tr>
					<tr>
						<td><b>Country:&nbsp;</b></td>
						<td><g:render template = "overviewMapSelectOptions"/></td>
					</tr>
				</table>
			</div>

			<div id = "changeNorthArrowPopup" title = "North Arrow">
				<table style = "white-space: nowrap">
					<tr>
						<td><b>Angle From North:&nbsp;</b></td>
						<td><input id = "northAngleInput" size = "4" type = "text" value = "${northArrowAngle}"/>deg</td>
					</tr>
					<tr>
						<td><b>Arrow Color:&nbsp;&nbsp;</b></td>
						<td><input class = "color" id = "northArrowColor" size = "6" type = "text" value = "FFFFFF"/></td>
					</tr>
					<tr>
						<td><b>Background Color:&nbsp;</b></td>
						<td><input class = "color" id = "northArrowBackgroundColor" size = "6" type = "text" value = "000000"/></td>
					</tr>
				</table>
			</div>	

			<div id = "changeFooterSecurityClassificationTextPopup" title = "Footer Security Classification">
				<table>
					<tr>
						<td><b>Text:&nbsp;</b></td>
						<td><input id = "footerSecurityClassificationTextInput" type = "text" value = "${securityClassification}"/></td>
					</tr>
					<tr>
						<td><b>Color:&nbsp;</b></td>
						<td><input class = "color" id = "footerSecurityClassificationTextColor" size = "6" value = "ADD8E6"/></td>
					</tr>
				</table>
			</div>

			<div id = "changeFooterLocationTextPopup" title = "Location">
				<table>
					<tr>
						<td><b>Text:&nbsp;</b></td>
						<td><input id = "footerLocationTextInput" type = "text" value = "${centerGeo}"/></td>
					</tr>
					<tr>
						<td><b>Color:&nbsp;</b></td>
						<td><input class = "color" id = "footerLocationTextColor" size = "6" value = "ADD8E6"/></td>
					</tr>
				</table>
			</div>

			<div id = "changeFooterAcquisitionDateTextPopup" title = "Acquisition Date">
				<table>
					<tr>
						<td><b>Text:&nbsp;</b></td>
						<td><input id = "footerAcquisitionDateTextInput" type = "text" value = "${acquisitionDate}"/></td>
					</tr>
					<tr>
						<td><b>Color:&nbsp;</b></td>
						<td><input class = "color" id = "footerAcquisitionDateTextColor" size = "6" value = "ADD8E6"/></td>
					</tr>
				</table>
			</div>

			<div id = "loadingDialogPopup" title = "Generating Template...">
				<br><br><br><div align = "center" id = "spinner" valign = "bottom"></div>
			</div>
				
			<div id = "downloadDialogPopup" title = "Downloading...">The download will start automatically once the template is complete.</div>
			<div id = "fontSize"></div>
			<div id = "jsColorImagesDirectory">${resource(dir: 'images/jsColor/', plugin: 'omar-image-magick')}</div>
		</content>
		<content tag = "bottom"></content>
		<r:script>
			var countryCode = "${countryCode.toLowerCase()}";
                        var formActionUrl = "${createLink(action: 'export')}";
                        var footerGradientGeneratorUrl = "${createLink(action: 'footerGradientGenerator')}";
                        var headerGradientGeneratorUrl = "${createLink(action: 'headerGradientGenerator')}";
                        var logoImagesDirectory = "${resource(dir: 'images/', plugin: 'omar-image-magick')}";
			var markerIcon = "${resource(dir: 'js/img/', file: 'marker-blue.png', plugin: 'openlayers')}";
			var markers = ["${markers.join("\",\"")}"];
                        var northArrowGeneratorUrl = "${createLink(action: 'northArrowGenerator')}";
                        var overviewMapImagesDirectory = "${resource(dir: 'images/overviewMaps/', plugin: 'omar-image-magick')}";
		</r:script>
	</body>
</html>
