<html>
	<head>
		<meta content = "templateExportLayout" name = "layout">
		<r:script>
			var countryMap;
			
			function changeLogo(logo)
			{
				${"logoImage"}.src = "${resource(dir: 'images/', plugin: 'omar-image-magick')}" + logo + ".png?_debugResources=y";
			}
			
			function changeCountry(country)
			{
				countryMap = country;
				updateCountryMaps();
			}

			function exportPreview()
			{
				exportPreviewSetup();
				var previewUrl = "${createLink(action: 'exportPreview')}";
					var countryCode = ${"country"}.options.selectedIndex;
				previewUrl += "?country=" + ${"country"}.options[countryCode].value;
				previewUrl += "&imageURL=" + (${"imageURL"}.value).replace(/&/g,"%26");
				//if (${"includeOutlineMap"}.checked)
				//{
				//	previewUrl += "&includeOutlineMap=on";
				//}
				//else 
				//{
					previewUrl += "&includeOutlineMap=null";
				//}
				if (${"includeOverviewMap"}.checked)
				{
					previewUrl += "&includeOverviewMap=on";
				}
				else
				{
					previewUrl += "&includeOverviewMap=null";
				}
					var logoId = ${"logo"}.options.selectedIndex;
				previewUrl += "&logo=" + ${"logo"}.options[logoId].value;
				previewUrl += "&line1=" + ${"line1"}.value;
				previewUrl += "&line2=" + ${"line2"}.value;
				previewUrl += "&line3=" + ${"line3"}.value;
				previewUrl += "&northArrowAngle=" + ${"northArrowAngle"}.value;
				previewUrl += "&securityClassification=" + ${"securityClassification"}.value;
				${"preview"}.onload = function() { exportPreviewCleanup(); }
				${"preview"}.src = previewUrl;
				
			}

			function exportPreviewCleanup()
			{
				${"templateStatus"}.style.display = "none";
				${"loading"}.style.display = "none";
				${"preview"}.style.display = "block";
			}

			function exportPreviewSetup()
			{
				${"templateStatus"}.style.display = "block";
				${"loading"}.src = "${resource(dir: 'images/', plugin: 'omar-image-magick')}imageLoading.gif?_debugResources=y";
				${"loading"}.style.display = "block";
				${"preview"}.style.display = "none";
			}

			function init()
			{
				${"templateStatus"}.style.display = "none";

				countryMap = "yy";
				var countryCode = "${countryCode}".toLowerCase();
				var countryElement = document.getElementById("country");
				for (var i = 0; i < countryElement.options.length; i++)
				{
					if (countryCode == countryElement.options[i].value)
					{
						countryElement.options.selectedIndex = i;
						countryMap = countryElement.options[i].value;
					}	
				}
				updateCountryMaps();
				changeLogo("ciaLogo");
			}

			function submitTemplate()
			{
				${"templateStatus"}.style.display = "block";
			}

			function updateCountryMaps()
			{
				if (${"includeOverviewMap"}.checked)
				{	
					${"overviewMap"}.style.visibility = "visible";
					var image = "${resource(dir: 'images/overviewMaps/', plugin: 'omar-image-magick')}" + countryMap + ".gif?_debugResources=y";
					${"overviewMap"}.innerHTML = "<img src = '" + image + "' width = '100'/>";
				}
				else 
				{
					${"overviewMap"}.style.visibility = "hidden";
				}
			}
		</r:script>
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
						<img id = "logoImage" width = "100"/>
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
						<!--Outline Map: <input checked = "true" id = "includeOutlineMap" name = "includeOutlineMap" onChange = "updateCountryMaps()" type = "checkbox">-->
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
	</body>
</html>
