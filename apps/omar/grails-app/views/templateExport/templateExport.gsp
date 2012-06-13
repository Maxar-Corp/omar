<html>
	<body>
		<div null align="center" style="background: green; color: black; font-size:20px; font-weight:bold; position:fixed; top:0; width: 100%; z-index:10">
       Unclassified
     </div>
	<div style="position:relative; margin-top:20; z-index:1">
		<form id = "templateExport" action = "${createLink( action: 'export')}" method = "post">
			Logo: 
			<select name = "logo">
				<option value = "ciaLogo">CIA</option>
				<option value = "diaLogo">DIA</option>
				<option value = "dhsLogo">DHS</option>
				<option value = "ngaLogo">NGA</option>
				<option value = "nroLogo">NRO</option>
				<option value = "nsaLogo">NSA</option>
			</select>
			<br>
			Line 1: <input type = "text" name = "line1" value = "${imageId}" size = "100">
			<br>
			Line 2: <input type = "text" name = "line2" value = "${acquisitionDate}" size = "100">
			<br>
			Line 3: <input type = "text" name = "line3" value = "${mgrs} Country: ${countryCode}" size = "100">
			<br>
			North Arrow Angle: <input type = "text" name = "northArrowAngle" value = "${northArrowAngle}">
			<br>
			Image URL: <input type = "text" name = "imageURL" value = "${imageURL}" size = "100">
			<br>
			<input type = "submit" value = "Submit"><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>Cheese
		</form>
</div>
              <div null align="center" style="background: green; color: black; font-size:20px; font-weight:bold; position:fixed; bottom:0; width: 100%">
       Unclassified
     </div>

	</body>
</html>
