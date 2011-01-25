<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: Apr 29, 2010
  Time: 8:46:05 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title><g:layoutTitle default="Grails"/></title>

  <link rel="stylesheet" type="text/css" href="${omar.bundle(contentType: "text/css", files: [
      resource(dir: 'css', file: 'main.css'),
      resource(dir: 'css', file: 'omar-2.0.css'),
  ])}"/>
  <script type="text/javascript" src="${omar.bundle(contentType: "text/javascript", files: [
      resource(plugin: "richui", dir: "js/yui/yahoo-dom-event", file: "yahoo-dom-event.js"),
      resource(plugin: "richui", dir: "js/yui/element", file: "element-min.js"),
      resource(plugin: "richui", dir: "js/yui/layout", file: "layout-min.js"),
  ])}"></script>

  <link rel="stylesheet" type="text/css" href="${resource(plugin: 'richui', dir: 'js/yui/reset-fonts-grids', file: 'reset-fonts-grids.css')}"/>
  <link rel="stylesheet" type="text/css" href="${resource(plugin: 'richui', dir: 'js/yui/assets/skins/sam', file: 'skin.css')}"/>

  <style>
    /*
    margin and padding on body element
    can introduce errors in determining
    element position and are not recommended;
    we turn them off as a foundation for YUI
    CSS treatments.
    */

  body {
    margin: 0;
    padding: 0;
    text-align:left;
    overflow-y:hidden;
    overflow-x:hidden;
  }
  #center{
    overflow-y:auto;
    height:100%;
  }
  /* Set the background color */
  .yui-skin-sam .yui-layout {
    background-color: #FFFFFF;
  }

  /* Style the body */
  .yui-skin-sam .yui-layout .yui-layout-unit div.yui-layout-bd {
    background-color: #FFFFFF;
  }

  </style>

  <g:layoutHead/>
  <g:javascript library="application"/>
</head>
<body class="yui-skin-sam" onresize="bodyOnResize();">

<div id="header">
  <omar:securityClassificationBanner/>
</div>
<div id="top">
  <g:pageProperty name="page.top"/>
</div>
<div id="center">
  <g:pageProperty name="page.center"/>
</div>
<g:layoutBody/>

<div id="footer">
  <omar:securityClassificationBanner/>
</div>

</body>


<g:javascript>
  (function()
  {
    var Event = YAHOO.util.Event;
    var Layout = YAHOO.widget.Layout;
    Event.onDOMReady( function()
    {
      bodyOnResize();
    } );
  })();

  function bodyOnResize()
  {
    var Dom = YAHOO.util.Dom;
    var centerDiv = Dom.get("center");
    var topDiv = Dom.get("top");
    var headerDiv = Dom.get("header");
    var footerDiv = Dom.get("footer");
    var height = Dom.getViewportHeight();
    centerDiv.style.height = (height - (topDiv.offsetHeight + headerDiv.offsetHeight + footerDiv.offsetHeight)) + "px";
  }
</g:javascript>

</html>