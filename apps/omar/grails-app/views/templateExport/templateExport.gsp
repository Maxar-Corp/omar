<html>
	<body>
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
			Image URL: <input type = "text" name = "imageURL" value = "${imageURL}" size = "100">
			<br>
			<input type = "submit" value = "Submit">
		</form>
	</body>
</html>
