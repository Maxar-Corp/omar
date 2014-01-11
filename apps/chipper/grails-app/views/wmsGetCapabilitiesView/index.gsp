<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: 10/16/13
  Time: 2:37 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title></title>
    <meta name="layout" content="main"/>
</head>

<body>
<div class="nav">
    <ul>
        <li>
            <g:link uri='/' class="home">Home</g:link>
        </li>
    </ul>
</div>

<div class="content">
    <h1>WMS GetCapabilities</h1>
    <div
    <g:render template="layerNames" model="${wmsGetCaps}"/>
</div>
</body>
</html>