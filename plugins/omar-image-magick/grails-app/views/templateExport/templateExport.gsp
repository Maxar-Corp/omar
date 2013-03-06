<html>
	<head>
		<meta content = "templateExportLayout" name = "layout">
	</head>
	<body>
		<content tag = "top"></content>
		<content tag = "center">
			<g:render template="templateExportMenu"/>
			<div id = "headerDiv" onclick = "changeColorGradient()"></div>
			<img id = "previewImage" onLoad = "init()"/>	
			<img id = "logoImage" onClick = "changeLogo()"/>
			<div id = "headerSecurityClassificationTextDiv" onClick = "changeHeaderSecurityClassificationText()"></div>
			<div id = "headerTitleTextDiv" onClick = "changeHeaderTitleText()"></div>
			<div id = "headerDescriptionTextDiv" onClick = "changeHeaderDescriptionText()"></div>
			<img id = "overviewMapImage" onClick = "changeOverviewMap()" onLoad = "positionOverviewMapImage(); positionNorthArrow()"/>
			<img id = "northArrowImage" onClick = "changeNorthArrow()" onLoad = "removeNorthArrowSpinner()"/>
			<div id = "northArrowSpinnerDiv"></div>

			<div id = "footerDiv"></div>
			<div id = "footerSecurityClassificationTextDiv" onClick = "changeFooterSecurityClassificationText()"></div>
			<div id = "footerLocationTextDiv" onClick = "changeFooterLocationText()"></div>
			<div id = "footerAcquisitionDateTextDiv" onClick = "changeFooterAcquisitionDateText()"></div>
			<div id = "markerDiv"></div>
			
			<form id = "downloadForm" method = "post"></form>

			<div id = "changeColorGradientDialog" title = "Header/Footer Color Gradient">
				<table>
					<tr>
						<td><b>Top Color:&nbsp;&nbsp;&nbsp;</b></td>
						<td><input class = "color" id = "gradientColorTopInput" size = "6" value = "595454"/></td>
					</tr>
					<tr>
						<td><b>Bottom Color:&nbsp;&nbsp;&nbsp;</b></td>
						<td><input class = "color" id = "gradientColorBottomInput" size = "6" value = "000000"/></td>
					</tr>
				</table>
			</div>

			<div id = "changeLogoDialog" title = "Change Logo">
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

			<div id = "changeHeaderSecurityClassificationTextDialog" title = "Header Security Classification">
				<table>
					<tr>
						<td><b>Text:&nbsp;&nbsp;&nbsp;</b></td>
						<td><input id = "headerSecurityClassificationTextInput" type = "text"/></td>
					</tr>
					<tr>	
						<td><b>Color:&nbsp;&nbsp;&nbsp;</b></td>
						<td><input class = "color" id = "headerSecurityClassificationTextColorInput" size = "6" value = "ADD8E6"/></td>
					</tr>
				</table>
			</div>

			<div id = "changeHeaderTitleTextDialog" title = "Title">
				<table>
					<tr>
						<td><b>Text:&nbsp;&nbsp;&nbsp;</b></td>
						<td><input id = "headerTitleTextInput" type = "text"/></td>
					</tr>
					<tr>
						<td><b>Color:&nbsp;&nbsp;&nbsp;</b></td>
						<td><input class = "color" id = "headerTitleTextColorInput" size = "6" value = "FFFF00"/></td>
					</tr>
				</table>
			</div>

			<div id = "changeHeaderDescriptionTextDialog" title = "Description">
				<table>
					<tr>
						<td><b>Text:&nbsp;&nbsp;&nbsp;</b></td>
						<td><input id = "headerDescriptionTextInput" type = "text"/></td>
					</tr>
					<tr>
						<td><b>Color:&nbsp;&nbsp;&nbsp;</b></td>
						<td><input class = "color" id = "headerDescriptionTextColorInput" size = "6" value = "FFFFFF"/></td>
					</tr>
				</table>
			</div>

			<div id = "changeOverviewMapDialog" title = "Overview Map">
				<table>
					<tr>
						<td><b>Include:&nbsp;&nbsp;&nbsp;</b></td>
						<td><input id = "includeOverviewMapCheckbox" type = "checkbox"/></td>
					</tr>
					<tr>
						<td><b>Country:&nbsp;&nbsp;&nbsp;</b></td>
						<td><g:render template = "overviewMapSelectOptions"/></td>
					</tr>
				</table>
			</div>

			<div id = "changeNorthArrowDialog" title = "North Arrow">
				<table style = "white-space: nowrap">
					<tr>
						<td><b>Angle From North:&nbsp;&nbsp;&nbsp;</b></td>
						<td><input id = "northAngleInput" size = "4" type = "text"/>deg</td>
					</tr>
					<tr>
						<td><b>Arrow Color:&nbsp;&nbsp;&nbsp;</b></td>
						<td><input class = "color" id = "northArrowColorInput" size = "6" type = "text" value = "FFFFFF"/></td>
					</tr>
					<tr>
						<td><b>Background Color:&nbsp;&nbsp;&nbsp;</b></td>
						<td><input class = "color" id = "northArrowBackgroundColorInput" size = "6" type = "text" value = "000000"/></td>
					</tr>
				</table>
			</div>	

			<div id = "changeFooterSecurityClassificationTextDialog" title = "Footer Security Classification">
				<table>
					<tr>
						<td><b>Text:&nbsp;&nbsp;&nbsp;</b></td>
						<td><input id = "footerSecurityClassificationTextInput" type = "text"/></td>
					</tr>
					<tr>
						<td><b>Color:&nbsp;&nbsp;&nbsp;</b></td>
						<td><input class = "color" id = "footerSecurityClassificationTextColorInput" size = "6" value = "ADD8E6"/></td>
					</tr>
				</table>
			</div>

			<div id = "changeFooterLocationTextDialog" title = "Location">
				<table>
					<tr>
						<td><b>Text:&nbsp;&nbsp;&nbsp;</b></td>
						<td><input id = "footerLocationTextInput" type = "text"/></td>
					</tr>
					<tr>
						<td><b>Color:&nbsp;&nbsp;&nbsp;</b></td>
						<td><input class = "color" id = "footerLocationTextColorInput" size = "6" value = "ADD8E6"/></td>
					</tr>
				</table>
			</div>

			<div id = "changeFooterAcquisitionDateTextDialog" title = "Acquisition Date">
				<table>
					<tr>
						<td><b>Text:&nbsp;&nbsp;&nbsp;</b></td>
						<td><input id = "footerAcquisitionDateTextInput" type = "text"/></td>
					</tr>
					<tr>
						<td><b>Color:&nbsp;&nbsp;&nbsp;</b></td>
						<td><input class = "color" id = "footerAcquisitionDateTextColorInput" size = "6" value = "ADD8E6"/></td>
					</tr>
				</table>
			</div>

			<div id = "loadingDialog" title = "Generating Template...">
				<br><br><br><div align = "center" id = "spinner" valign = "bottom"></div>
			</div>
			
			<div align = "left" id = "productGenerationProgressDialog" title = "Generating Product...">
				<table>
					<tr>
						<td><b>Current Progress:</b>&nbsp;&nbsp;&nbsp;</td>
						<td><div id = "currentProductProgressDiv">0%</div></td>
					</tr>
					<tr>
						<td><b>Product Link:</b>&nbsp;&nbsp;&nbsp;</td>
						<td><div id = "productLinkDiv">...</div></td>
					</tr>
				</table>
			</div>
	
			<div id = "downloadDialog" title = "Downloading...">The download will start automatically once the template is complete.</div>
			<div id = "fontSize"></div>
			<div id = "jsColorImagesDirectory">${resource(dir: 'images/jsColor/', plugin: 'omar-image-magick')}</div>
		</content>
		<content tag = "bottom"></content>
		<r:script>
			var countryCode = "${countryCode}";
			var footerAcquisitionDateTextArray = ["${footerAcquisitionDateTextArray.join("\",\"")}"];
			var footerLocationTextArray = ["${footerLocationTextArray.join("\",\"")}"];
			var footerSecurityClassificationTextArray = ["${footerSecurityClassificationTextArray.join("\",\"")}"];
			var headerDescriptionTextArray = ["${headerDescriptionTextArray.join("\",\"")}"];
			var headerSecurityClassificationTextArray = ["${headerSecurityClassificationTextArray.join("\",\"")}"];
			var headerTitleTextArray = ["${headerTitleTextArray.join("\",\"")}"];
			var imageUrlArray = ["${imageUrlArray.join("\",\"")}"];
			var northAngleArray = ["${northAngleArray.join("\",\"")}"];
			
                        var exportFormUrl = "${createLink(action: 'export')}";
                        var footerGradientGeneratorUrl = "${createLink(action: 'footerGradientGenerator')}";
                        var headerGradientGeneratorUrl = "${createLink(action: 'headerGradientGenerator')}";
                        var logoImagesDirectory = "${resource(dir: 'images/', plugin: 'omar-image-magick')}";
                        var northArrowGeneratorUrl = "${createLink(action: 'northArrowGenerator')}";
                        var overviewMapImagesDirectory = "${resource(dir: 'images/overviewMaps/', plugin: 'omar-image-magick')}";
			var viewProductUrl = "${createLink(action: 'viewProduct')}";
		</r:script>
	</body>
</html>
