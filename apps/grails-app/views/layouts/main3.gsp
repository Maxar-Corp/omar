<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: Jan 21, 2010
  Time: 9:50:04 AM
  To change this template use File | Settings | File Templates.
--%>

<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"/>
  <title><g:layoutTitle default="Grails"/></title>
  <link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css')}"/>
  <link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon"/>
  <g:layoutHead/>
  <g:javascript library="yui"/>
  <style type="text/css">
    /*margin and padding on body element
  can introduce errors in determining
  element position and are not recommended;
  we turn them off as a foundation for YUI
  CSS treatments. */
  body {
    margin: 0;
    padding: 0;
  }

  #toggle {
    text-align: center;
    padding: 1em;
  }

  #toggle a {
    padding: 0 5px;
    border-left: 1px solid black;
  }

  #tRight {
    border-left: none !important;
  }
  </style>

  <gui:resources css="['reset_fonts_grids', 'resize', 'layout', 'button', 'yahoo', 'event', 'dom']"
          javascript="['yahoo', 'event', 'dom', 'element', 'dragdrop', 'resize', 'animation', 'layout']"/>

</head>

<body class="${pageProperty(name: 'body.class')}">
<div id="header">
  <div align="center" style="background: green; color: black; font-size:1.5em; font-weight:bold">Unclassified</div>
</div>
<div id="main">
  <div id="top1">
    <g:pageProperty name="page.layout1.top.content"/>
  </div>
  <div id="bottom1">
    <g:pageProperty name="page.layout1.top.content"/>
  </div>
  <div id="right1">
    <g:pageProperty name="page.layout1.right.content"/>
  </div>
  <div id="left1">
    <g:pageProperty name="page.layout1.left.content"/>
  </div>
  <div id="center1">
    <g:pageProperty name="page.layout1.center.content"/>
  </div>
</div>
<div id="footer">
  <div align="center" style="background: green; color: black; font-size:1.5em; font-weight:bold">Unclassified</div>
</div>
<g:javascript src="docking-widget.js"/>
</body>
</html>
