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
  <meta content="generatedViews" name="layout"/>
  <style type="text/css">
  #centerid {
    text-align: center;
    background-position: center center;
  }
  </style>
  <title>OMAR Streaming Video</title>
</head>

<body>

<content tag="content">
  <div class="nav">
    <span class="menuButton"><g:link class="home" uri="/">OMAR™ Home</g:link></span>
    <span class="menuButton"><g:link
        url='${createLink(dir: "videoStreaming", action: "getKML", id: params.id)}'>Generate KML</g:link></span>
  </div>

  <div class="body">
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
    </g:if>
    <div id="centerid">
      <h1>${title}</h1>
      <br/>
      <div id="test">
        <p>You need <a href="http://www.adobe.com/go/getflashplayer">Flash Player</a>
          installed and JavaScript enabled to play this media.</p>
      </div>
    </div>
  </div>

  <g:javascript plugin="swfobject" src="swfobject/swfobject.js"/>
  <g:flashPlayer id="test" varFile="${flvUrl}" width="${videoDataSet.width}" height="${videoDataSet.height}"
                 varAutostart="true"
                 paramAllowFullscreen="true"/>

</content>

</body>
</html>
