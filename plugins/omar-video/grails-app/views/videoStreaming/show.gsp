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
  <meta content="singleColumn" name="layout"/>
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

<content tag="top">
  <div class="nav">
    <span class="menuButton"><g:link class="home" uri="/">OMAR™ Home</g:link></span>
    <span class="menuButton"><a href='${createLink(dir: "videoStreaming", action: "getKML", id: params.id)}'>Generate KML</a></span>
  </div>
  <%--
  <div id="hd">
    <img id="logo" src="${resource(contextPath: "/", dir: 'images', file: 'OMARLarge.png')}" alt="OMAR-2.0 Logo"/>
  </div>
  --%>
</content>

<content tag="center">
  <div class="body">
    <h1>${title}</h1>
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
    </g:if>
    <div id="centerid">
      <div id="player">FLASH PLUGIN REQUIRED</div>
    </div>
  </div>
</content>
<g:javascript>

var flashvars = {
	file: "${flvUrl}",
	autostart:"true"
	//shuffle: ‘false’,
	// repeat: ‘list’
};

var videoparams = {
	allowfullscreen:"true"
	//allowscriptaccess:”always”
};

var videoattributes = {
	id:"player1",
	name:"player1"
};

swfobject.embedSWF("${createLinkTo(dir: 'js', file: 'player.swf')}", "player", "720", "520", "9", true,flashvars,videoparams,videoattributes);

</g:javascript>
</body>
</html>
