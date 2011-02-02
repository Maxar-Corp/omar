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

  <omar:bundle contentType="css" files="${[
    [dir: 'css', file: 'main.css'],
    [dir: 'css', file: 'omar-2.0.css']
  ]}"/>

  <g:javascript plugin="richui" src="yui/yahoo-dom-event/yahoo-dom-event.js"/>
  <g:javascript plugin="richui" src="yui/element/element-min.js"/>

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

  body{
    height:100%;
    width:100%;
    text-align:left;
    margin:0;
    padding:0;
    overflow-y:hidden;
  }
  #header
  {
    width:100%;
  }
  #top
  {
    width:100%;
  }
  #center
  {
    height:100%;
    width:100%;
    overflow-x:auto;
    overflow-y:auto;
  }
  #footer
  {
    position:absolute;
    bottom:0;
    height:20px;
    width:100%;
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
<body class="${pageProperty(name: 'body.class')}" onresize="bodyOnResize();${pageProperty(name: 'body.onresize')}" onload="${pageProperty(name: 'body.onload')}bodyOnResize();">
<omar:bundle contentType="javascript" files="${[
    [dir:'js', file: 'application.js'],
    [plugin:'richui' , dir:'js/yui/yahoo-dom-event', file: 'yahoo-dom-event.js'],
    [plugin:'richui' , dir:'js/yui/element',         file: 'element-min.js']
]}"/>

<div id="header" class="header">
  <omar:securityClassificationBanner/>
</div>
<div id="top">
  <g:pageProperty name="page.top"/>
</div>
<div id="center">
  <g:pageProperty name="page.center"/>
</div>
<div id="footer" class="footer">
  <omar:securityClassificationBanner/>
</div>
<g:layoutBody/>

<g:javascript>
    var Event = YAHOO.util.Event;
    var Layout = YAHOO.widget.Layout;
    Event.onDOMReady( function()
    {
      bodyOnResize();
    } );

  function bodyOnResize()
  {
    /*
    var Dom = YAHOO.util.Dom;
    var centerDiv = Dom.get("center");
    var contentDiv = Dom.get("content");
    var topDiv = Dom.get("top");
    var headerDiv = Dom.get("header");
    var footerDiv = Dom.get("footer");
    var height = Dom.getViewportHeight();
    var centerHeight =  (contentDiv.offsetHeight- (topDiv.offsetHeight + headerDiv.offsetHeight + footerDiv.offsetHeight)) ;
    centerDiv.style.height = centerHeight + "px";//(height - (topDiv.offsetHeight + headerDiv.offsetHeight + footerDiv.offsetHeight)) + "px";
*/
    var Dom = YAHOO.util.Dom;
    var header     = Dom.get("header");
    var top        = Dom.get("top");
    var centerDiv  = Dom.get("center");
    var footer     = Dom.get("footer");
    // IE6 seems to do better to use the root content div and then adjust everyone from  that
    var top     = top.offsetTop+top.offsetHeight;
    var bottom  = footer.offsetTop;
    var centerHeight     = Math.abs(bottom-top);
    centerDiv.style.height = centerHeight + "px";
    centerDiv.style.width  = "100%";

  }
</g:javascript>
</body>



</html>