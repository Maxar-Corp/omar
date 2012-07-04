<html>
	<head>
		<script type = "text/javascript">
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
				var previewUrl = "${createLink(action: 'exportPreview')}";
					var countryCode = ${"country"}.options.selectedIndex;
				previewUrl += "?country=" + ${"country"}.options[countryCode].value;
				previewUrl += "&imageURL=" + (${"imageURL"}.value).replace(/&/g,"%26");
				previewUrl += "&includeOutlineMap=" + ${"includeOutlineMap"}.value;
				previewUrl += "&includeOverviewMap=" + ${"includeOverviewMap"}.value;
					var logoId = ${"logo"}.options.selectedIndex;
				previewUrl += "&logo=" + ${"logo"}.options[logoId].value;
				previewUrl += "&line1=" + ${"line1"}.value;
				previewUrl += "&line2=" + ${"line2"}.value;
				previewUrl += "&line3=" + ${"line3"}.value;
				previewUrl += "&northArrowAngle=" + ${"northArrowAngle"}.value;
				${"preview"}.innerHTML = "<img src = '" + previewUrl + "'/>";
			}

			function init()
			{
				countryMap = "yy";
				var countryCode = "${countryCode}".toLowerCase();
				for (var i = 0; i < ${"country"}.options.length; i++)
				{
					if (countryCode == ${"country"}.options[i].value)
					{
						${"country"}.options.selectedIndex = i;
						countryMap = ${"country"}.options[i].value;
					}	
				}
				updateCountryMaps();
				changeLogo("ciaLogo");
			}

			function submitTemplate()
			{
				${"submitStatus"}.style.display = "block";
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
		</script>
	</head>
	<body onLoad = "init()">
		<form id = "templateExport" action = "${createLink(action: 'export')}" method = "post">
			<table align = "center" border = "1">
				<tr>
					<td>
						Logo: 
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
						Country: <br>
						<g:render template = "overviewMapSelectOptions"/>
						<br>
						Outline Map: <input checked = "true" id = "includeOutlineMap" name = "includeOutlineMap" onChange = "updateCountryMaps()" type = "checkbox">
						Overview Map: <input checked = "true" id = "includeOverviewMap" name = "includeOverviewMap" onChange = "updateCountryMaps()" type = "checkbox">
						<br>
						<div id = "overviewMap"></div> 
					</td>
					<td>
						North Arrow Angle: <input id = "northArrowAngle" name = "northArrowAngle" size = "2" type = "text" value = "${northArrowAngle}"> deg
					</td>
				</tr>
			</table>
			<input onClick = "exportPreview()" type = "button" value = "Preview">
			<input onClick = "submitTemplate()" type = "submit" value = "Download">
			<div id = "submitStatus" style = "display: none">
				Your template is being generated. This may take several seconds... 
			</div>
			<input id = "imageURL" name = "imageURL" style = "display: none", type = "text" value = "${imageURL}">
		</form>
		<div align = "center" id = "preview"></div>
	</body>
</html>
