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
							<td width = "45%">
								<b>Text</b>
							</td>
							<td>
								<b>Colors</b>
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
								<img id = "logoImage" src = "${resource(dir: 'images/', plugin: 'omar-image-magick')}ciaLogoForWeb.png"/>
							</td>
							<td>
								<table width = "100%">
									<tr>
										<td align = "right" width = "25%">Title:&nbsp;</td>
										<td>
											<input id = "title" name = "title" value = "${imageId}" type = "text"/>
										</td>
									</tr>
									<tr>
										<td align = "right">Description:&nbsp;</td>
										<td>
											<input id = "description" name = "description" value = "Country: ${countryCode}" type = "text"/>
										</td>
									</tr>
									<tr>
										<td align = "right">Location:&nbsp;</td>
										<td>
											<input id = "location" name = "location" value = "${centerGeo}" type = "text"/>
										</td>
									</tr>
									<tr>
										<td align = "right">Acquisition Date:&nbsp;</td>
										<td>
											<input id = "acquisitionDate" name = "acquisitionDate" type = "text" value = "${acquisitionDate}"/>
										</td>
									</tr>
									<tr>
										<td align = "right">Security Classification:&nbsp;</td>
										<td>
											<input id = "securityClassification" name = "securityClassification" type = "text" value = "${securityClassification}"/>
										</td>
									</tr>
									<tr>
										<td align = "right">Header/Footer Gradient:&nbsp;</td>
										<td align = "right">
											<div id = "headerFooterGradient"><div\>
											<input class = "color" id = "gradientColorTop" name = "gradientColorTop" onChange = "gradientGenerator()" size = "5" value = "595454"/>
										</td>
									</tr>
								</table>
							</td>
							<td>
								<table>
									<tr>
										<td><input class = "color" id = "titleTextColor" name = "titleTextColor" size = "5" value = "FFFF00"/></td>
									</tr>
									<tr>
										<td><input class = "color" id = "descriptionTextColor" name = "descriptionTextColor" size = "5"/></td>
									</tr>
									<tr>
										<td><input class = "color" id = "locationTextColor" name = "locationTextColor" size = "5" value = "ADD8E6"/></td>
									</tr>
									<tr>
										<td><input class = "color" id = "acquisitionDateTextColor" name = "acquisitionDateTextColor" size = "5" value = "ADD8E6"/></td>
									</tr>
									<tr>
										<td><input class = "color" id = "securityClassificationTextColor" name = "securityClassificationTextColor" size = "5" value = "ADD8E6"/></td>
									</tr>
									<tr>
										<td><input class = "color" id = "gradientColorBottom" name = "gradientColorBottom" onChange = "gradientGenerator()" size = "5" value = "000000"/></td>
									</tr>
								</table>
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
					</table>
					<div align = "center">
						<input onClick = "exportPreview()" type = "button" value = "Preview"/>
						<input onClick = "resetColors()" type = "button" value = "Reset"/>
						<input onClick = "submitTemplate()" type = "submit" value = "Download"/>
						<br><br>
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
			<div id = "gradientGeneratorUrl">${createLink(action: 'gradientGenerator')}</div>
			<div id = "jsColorImagesDirectory">${resource(dir: 'images/jsColor/', plugin: 'omar-image-magick')}</div>
		</content>
		<content tag = "bottom"></content>
	</body>
</html>
