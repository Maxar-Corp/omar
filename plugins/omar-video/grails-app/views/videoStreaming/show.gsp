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
  <meta content="showVideoPageLayout" name="layout"/>
  <r:require modules="showVideoPageLayout"/>
  <swfobject:resources/>
  <title>OMAR Streaming Video</title>
</head>

<body class="yui-skin-sam">

<content tag="top">
  <div class="nav">
    <ul>
    <li>
      <g:link class="home" uri="/">OMAR™ Home</g:link>
    </li>
    <li>
      <g:link url='${createLink(dir: "videoStreaming", action: "getKML", id: params.id)}'>Generate KML</g:link>
    </li>
    </ul>
  </div>
</content>
<content tag="center">

  <div align="center">
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

  <g:flashPlayer id="test" varFile="${flvUrl}" width="${videoDataSet.width}" height="${videoDataSet.height}"
                 varAutostart="true"
                 paramAllowFullscreen="true"/>

</content>

</body>
</html>
