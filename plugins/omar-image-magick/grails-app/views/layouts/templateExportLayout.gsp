<!DOCTYPE html>
<html>
<head>
    <title>OMAR <g:meta name="app.version"/>: Template Export</title>
    <g:layoutHead/>
    <asset:stylesheet src="templateExportPage.css"/>
</head>

<body class="yui-skin-sam" onLoad="${pageProperty( name: 'body.onload' )}">
<div id="top1">
    <omar:securityClassificationBanner/>
    <g:pageProperty name="page.top"/>
</div>

<div id="center1">
    <g:pageProperty name="page.center"/>
</div>

<div id="bottom1">
    <omar:securityClassificationBanner/>
    <g:pageProperty name="page.bottom"/>
</div>
<asset:javascript src="templateExportPage.js"/>
<g:layoutBody/>
</body>
</html> 
