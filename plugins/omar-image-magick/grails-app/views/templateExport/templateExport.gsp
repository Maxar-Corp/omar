<html>
	<head>
		<meta content = "templateExportLayout" name = "layout">
	</head>
	<body onLoad = "init()">
		<content tag = "top"></content>
		<content tag = "center">
			<div id = "center">		
				<form id = "templateExport" action = "${createLink(action: 'export')}" method = "post">
					<table align = "center" cellpadding = "5" width = "100%">
						<tr align = "center">
							<td>
								<b>Logo</b>
							</td>
							<td width = "55%">
								<b>Header Text</b>
							</td>
							<td>
								<b>Country</b>
							</td>
							<td>
								<b>North Arrow Angle</b>
							</td>
						</tr>
						<tr align = "center">
							<td>
								<select id = "logo" name = "logo" onChange = "changeLogo(this.value)">
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
								<br><br>
								<img id = "logoImage" src = "${resource(dir: 'images/', plugin: 'omar-image-magick')}ciaLogo.png" width = "100"/>
							</td>
							<td>
								Line 1: <input id = "line1" name = "line1" value = "${imageId}" type = "text"/>
								<br>
								Line 2: <input id = "line2" name = "line2" value = "Acquisition Date: ${acquisitionDate}" type = "text">
								<br>
								Line 3: <input id = "line3" name = "line3" value = "GEO: ${centerGeo} Country: ${countryCode}" type = "text">
								<br>	
							</td>
							<td>
								<g:render template = "overviewMapSelectOptions"/>
								<br>
								Overview Map: <input id = "includeOverviewMap" name = "includeOverviewMap" onClick = "updateCountryMaps()" type = "checkbox">
								<br>
								<div id = "overviewMap"></div> 
							</td>
							<td>
								<input id = "northArrowAngle" name = "northArrowAngle" size = "2" type = "text" value = "${northArrowAngle}"> deg
							</td>
						</tr>
						<tr>
							<td colspan = "4">
								<b>Security Classification:</b>
								<input id = "securityClassification" name = "securityClassification" type = "text" value = "${securityClassification}">
							</td>
						</tr>
					</table>
					<div align = "center">
						<input onClick = "exportPreview()" type = "button" value = "Preview">
						<input onClick = "submitTemplate()" type = "submit" value = "Download">
					</div>
					<input id = "imageURL" name = "imageURL" style = "display: none", type = "text" value = "${imageURL}">
				</form>
				<div align = "center">
					<div id = "templateStatus">Your template is being generated. This may take several seconds...</div>
					<img id = "loading" width = "25"/>
					<img id = "preview" width = "65%"/>
				</div>
				<div id = "countryCode">${countryCode}</div>
				<div id = "exportPreviewUrl">${createLink(action: 'exportPreview')}</div>
				<div id = "imageResourceLocation">${resource(dir: 'images/', plugin: 'omar-image-magick')}</div>
			</div>
		</content>
		<content tag = "bottom"></content>
	</body>
</html>
