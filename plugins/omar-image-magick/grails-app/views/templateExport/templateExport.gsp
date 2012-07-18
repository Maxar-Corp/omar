<html>
	<head>
		<meta content = "templateExportLayout" name = "layout">
		<style type = "text/css">
			body
			{
				margin: 0;
				padding: 0;
			}
			#countryCode
			{
				display: none;
			}
			#exportPreviewUrl
			{
				display: none;
			}
			#imageResourceLocation
			{
				display: none;
			}
			#templateStatus
			{
				display: none;
			}
		</style>
	</head>
	<body onLoad = "init()">
		<form id = "templateExport" action = "${createLink(action: 'export')}" method = "post">
			<table align = "center" cellpadding = "8">
				<tr align = "center">
					<td>
						<b>Logo</b>
					</td>
					<td>
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
							<option value = "ngaLogo">NGA</option>
							<option value = "nroLogo">NRO</option>
							<option value = "nsaLogo">NSA</option>
						</select>
						<br><br>
						<img id = "logoImage" src = "${resource(dir: 'images/', plugin: 'omar-image-magick')}ciaLogo.png" width = "100"/>
					</td>
					<td>
						Line 1: <input id = "line1" name = "line1" value = "${imageId}" size = "100" type = "text">
						<br>
						Line 2: <input id = "line2" name = "line2" value = "${acquisitionDate}" size = "100" type = "text">
						<br>
						Line 3: <input id = "line3" name = "line3" value = "${mgrs} Country: ${countryCode}" size = "100" type = "text">
					</td>
					<td>
						<g:render template = "overviewMapSelectOptions"/>
						<br>
						Overview Map: <input checked = "true" id = "includeOverviewMap" name = "includeOverviewMap" onChange = "updateCountryMaps()" type = "checkbox">
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
	</body>
</html>
