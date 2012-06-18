<html>
	<head>
		<script type = "text/javascript">
			function submitTemplate()
			{
				document.getElementById("submitStatus").style.display = "block";
			}
		</script>
	</head>
	<body>
		<form id = "templateExport" action = "${createLink(action: 'export')}" method = "post">
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
			North Arrow Angle: <input type = "text" name = "northArrowAngle" size = "2" value = "${northArrowAngle}"> deg
			<br>
			<input type = "submit" onClick = "submitTemplate()" value = "Submit">
			<div id = "submitStatus" style = "display: none">
				Your template is being generated. This may take several seconds... 
			</div>
		</form>
	</body>
</html>
