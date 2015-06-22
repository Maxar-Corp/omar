<html>
<head>
    <title><g:layoutTitle default="Grails"/></title>
    <style>
    body {
        margin: 0;
        padding: 0;
        visibility: hidden;
    }
    </style>

    <meta content="yes" name="apple-mobile-web-app-capable"/>
    <meta content="minimum-scale=1.0, width=device-width, user-scalable=no" name="viewport"/>


    <g:layoutHead/>
</head>

<body class="yui-skin-sam">

<div id="top">
    <omar:securityClassificationBanner/>
</div>

<div id="center">
    <g:pageProperty name="page.content"/>
</div>

<div id="bottom">
    <omar:securityClassificationBanner/>
</div>

<g:layoutBody/>
</body>
</html>