<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
	<head>
		<title>OMAR <g:meta name="app.version"/>: Time Lapse</title>
		<g:layoutHead/>
		<r:require modules = "timeLapse"/>
		<r:layoutResources/>
	</head>
	<body class = "yui-skin-sam" onLoad = "${pageProperty(name: 'body.onload')}">
	<div id = "top1">
		<omar:securityClassificationBanner/>
		<g:pageProperty name="page.top"/>
	</div>
	<div id = "center1">
		<g:pageProperty name = "page.center"/>
	</div>
	<div id = "bottom1">
		<omar:securityClassificationBanner/>
		<g:pageProperty name="page.bottom"/>
	</div>	
		<g:layoutBody/>
		<r:layoutResources/>
	</body>
</html> 
