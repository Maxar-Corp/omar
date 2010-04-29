<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: Oct 28, 2009
  Time: 7:19:09 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>DataManager - index</title>
  <meta name="layout" content="main5"/>
</head>
<body>
<div class="nav">
  <span class="menuButton"><a class="home" href="${resource(dir: '')}">Home</a></span>
</div>
<div class="body">
  <g:if test="${flash.message}">
    <div class="message">${flash.message}</div>
  </g:if>
  <ul>
    <li><a href="${createLink(action: "addRaster")}">Add Raster</a></li>
    <li><a href="${createLink(action: "removeRaster")}">Remove Raster</a></li>
    <li><a href="${createLink(action: "addVideo")}">Add Video</a></li>
    <li><a href="${createLink(action: "removeVideo")}">Remove Video</a></li>
  </ul>
</div>
</body>
</html>