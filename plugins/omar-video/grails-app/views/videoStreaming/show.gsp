<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: Jul 17, 2009
  Time: 1:01:30 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <meta content="main5" name="layout"/>
  <link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css')}"/>
  <style type="text/css">
  #centerid {
    text-align: center;
    background-position: center center;
  }
  </style>
  <title>OMAR Streaming Video</title>
  <g:javascript src="swfobject.js"/>
</head>
<body>
<div class="nav">
  <span class="menuButton">
	<g:link class="home" controller="home">Home</g:link>
  </span>
  <span class="menuButton"><a href='${createLink(dir: "videoStreaming", action: "getKML", id: params.id)}'>Generate KML</a></span>
</div>
<div class="body">
  <h1>${title}</h1>
  <g:if test="${flash.message}">
    <div class="message">${flash.message}</div>
  </g:if>
  <div id="centerid">
    <div id="player">FLASH PLUGIN REQUIRED</div>
  </div>
</div>
<g:javascript>
  var so = new SWFObject(
      '${resource(dir: "js", file: "player.swf")}', 'player',
      '${videoDataSet.width}', '${videoDataSet.height}', '9'
      );

  so.addParam('allowfullscreen', 'true');
  so.addParam('allowscriptprocess', 'true');
  so.addVariable('file', '${flvUrl}');
  so.addVariable('autostart', 'true');
  so.write('player');
</g:javascript>
</body>
</html>
