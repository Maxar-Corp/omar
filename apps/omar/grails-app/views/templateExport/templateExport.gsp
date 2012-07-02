<html>
	<head>
		<script type = "text/javascript">
			var countryMap;
			
			function changeLogo(logo)
			{
				document.getElementById("logoImage").src = "/omar/images/" + logo + ".png?_debugResources=y";
			}
			
			function changeCountry(country)
			{
				countryMap = country;
				updateCountryMaps();
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
				document.getElementById("submitStatus").style.display = "block";
			}

			function updateCountryMaps()
			{
				if (${"includeOverviewMap"}.checked)
				{
					${"overviewMap"}.style.visibility = "visible";
					${"overviewMap"}.innerHTML = "<img src = '/omar/images/overviewMaps/" + countryMap + ".gif?_debugResources=y' width = '100'/>";
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
			<table border = "1">
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
						Line 1: <input type = "text" name = "line1" value = "${imageId}" size = "100">
						<br>
						Line 2: <input type = "text" name = "line2" value = "${acquisitionDate}" size = "100">
						<br>
						Line 3: <input type = "text" name = "line3" value = "${mgrs} Country: ${countryCode}" size = "100">
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
						North Arrow Angle: <input type = "text" name = "northArrowAngle" size = "2" value = "${northArrowAngle}"> deg
					</td>
				</tr>
			</table>
			<input type = "submit" onClick = "submitTemplate()" value = "Submit">
			<div id = "submitStatus" style = "display: none">
				Your template is being generated. This may take several seconds... 
			</div>
		</form>
	</body>
</html>
