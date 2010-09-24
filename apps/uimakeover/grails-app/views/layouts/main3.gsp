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

  <link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon"/>

  <g:layoutHead/>

  <%--
  <g:javascript library="scriptaculous"/>
  <g:javascript library="application"/>
  --%>
  <%--
  <g:javascript library="yui"/>
  --%>

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

  <%--
  <gui:resources css="['reset_fonts_grids', 'resize', 'layout' /*, 'button', 'yahoo', 'event', 'dom'*/]"/>
  --%>

  <%--
  <link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css')}"/>
  <link rel='stylesheet' type='text/css' href='${resource(dir: "js/yui/2.7.0/reset-fonts-grids", file: "reset-fonts-grids.css")}'/>
  <link rel='stylesheet' type='text/css' href='${resource(dir: "js/yui/2.7.0/resize/assets/skins/sam", file: "resize.css")}'/>
  <link rel='stylesheet' type='text/css' href='${resource(dir: "js/yui/2.7.0/layout/assets/skins/sam", file: "layout.css")}'/>
  --%>

  <link rel="stylesheet" href="${omar.bundle(contentType: 'text/css', files: [
      resource(dir: 'css', file: 'main.css'),
      resource(dir: "js/yui/2.7.0/reset-fonts-grids", file: "reset-fonts-grids.css"),
      resource(dir: "js/yui/2.7.0/resize/assets/skins/sam", file: "resize.css") //,
//      resource(dir: "js/yui/2.7.0/layout/assets/skins/sam", file: "layout.css")
  ])}"/>

  <link rel='stylesheet' type='text/css' href='${resource(dir: "js/yui/2.7.0/layout/assets/skins/sam", file: "layout.css")}'/>

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

<%--
<div id="spinner" class="spinner" style="display:none;">
  <img src="${createLinkTo(dir: 'images', file: 'spinner.gif')}" alt="Spinner"/>
</div>
--%>

<%--
<gui:resources javascript="['yahoo_dom_event', 'element', 'dragdrop', 'resize', 'layout']"/>
<g:javascript src="docking-widget.js"/>
--%>

<%--
<g:javascript plugin="grails-ui" src="yui/2.7.0/yahoo-dom-event/yahoo-dom-event.js"/>
<g:javascript plugin="grails-ui" src="yui/2.7.0/element/element-min.js"/>
<g:javascript plugin="grails-ui" src="yui/2.7.0/dragdrop/dragdrop-min.js"/>
<g:javascript plugin="grails-ui" src="yui/2.7.0/resize/resize-min.js"/>
<g:javascript plugin="grails-ui" src="yui/2.7.0/layout/layout-min.js"/>
<g:javascript src="docking-widget.js"/>
--%>

<script type="text/javascript" src="${omar.bundle(contentType: 'text/javascript', files: [
    resource(plugin: "grails-ui", dir: "js/yui/2.7.0/yahoo-dom-event", file: "yahoo-dom-event.js"),
    resource(plugin: "grails-ui", dir: "js/yui/2.7.0/element", file: "element-min.js"),
    resource(plugin: "grails-ui", dir: "js/yui/2.7.0/dragdrop", file: "dragdrop-min.js"),
    resource(plugin: "grails-ui", dir: "js/yui/2.7.0/resize", file: "resize-min.js"),
    resource(plugin: "grails-ui", dir: "js/yui/2.7.0/layout", file: "layout-min.js"),
    resource(dir: "js", file: "docking-widget.js")
])}"></script>


<g:layoutBody/>
</body>
</html>
