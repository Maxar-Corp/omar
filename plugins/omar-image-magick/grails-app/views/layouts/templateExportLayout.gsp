<html>
	<head>
		<title>OMAR <g:meta name="app.version"/>: Template Export</title>
		<g:layoutHead/>
		<r:require modules = "templateExport"/>
		<r:layoutResources/>
	</head>
	<body class = "yui-skin-sam" onLoad = "${pageProperty(name: 'body.onload')}">
	<div id = "header">
		<omar:securityClassificationBanner/>
	</div>
	<div id = "center">
		<g:pageProperty name = "page.center"/>
	</div>
	<div id = "footer">
		<omar:securityClassificationBanner/>
	</div>	
		<g:layoutBody/>
		<r:layoutResources/>
	</body>
</html> 
