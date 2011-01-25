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

  <link rel="stylesheet" type="text/css" href="${omar.bundle(contentType: 'text/css', files: [
      resource(dir: 'css', file: 'main.css'),
      resource(dir: 'css', file: 'omar-2.0.css')
  ])}"/>

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
    overflow-x:hidden;
  }

  /* Set the background color */
  .yui-skin-sam .yui-layout {
    background-color: #FFFFFF;
  }

  /* Style the body */
  .yui-skin-sam .yui-layout .yui-layout-unit div.yui-layout-bd {
    background-color: #FFFFFF;
  }
  #top
  {
    top:0px;
    width:100%;
  }
  #centerMap
  {
    position:absolute;
    top: 0px;
    left:0px;
    height:100%;
    width:100%;
  }
  #middle
  {
    position:relative;
    top: 0px;
    height:100%;
    width:100%
  }
  #header{
    position:relative;
    top:0;
    width:100%;
  }
  #footer{
    position:relative;
    width:100%;
  }

  </style>

  <g:layoutHead/>
</head>
<body class="${pageProperty(name: 'body.class')}" onresize="${pageProperty(name: 'body.onresize')}">

  <div id="header">
    <omar:securityClassificationBanner/>
  </div>
  <div id="top">
    <g:pageProperty name="page.north"/>
  </div>
  <div id="middle">
    <div id="centerMap">
      <table>
        <tr>
          <td id="toolbarRow">
            <div id="toolBar" class="olControlPanel"></div>
          </td>
        </tr>
        <tr id="mapRow">
          <td id="mapColumn">
            <div id="map"></div>
          </td>
        </tr>
      </table>
      <g:pageProperty name="page.center"/>
    </div>
  </div>

<div id="footer">
  <omar:securityClassificationBanner/>
</div>


<script type='text/javascript' src='${omar.bundle(contentType: "text/javascript", files: [
    resource(dir: "js", file: "application.js"),
    resource(plugin: "richui", dir: "js/yui/yahoo-dom-event", file: "yahoo-dom-event.js"),
    resource(plugin: "richui", dir: "js/yui/element", file: "element-min.js"),
    resource(plugin: "richui", dir: "js/yui/layout", file: "layout-min.js"),
])}'></script>

<g:layoutBody/>

</body>
<g:javascript>
  (function()
  {
    //YAHOO.util.Dom.setStyle(document.body, 'display', 'none');
    var Dom = YAHOO.util.Dom;
    var Event = YAHOO.util.Event;
    Event.onDOMReady( function()
    {
      var Dom = YAHOO.util.Dom;
      var mapDiv = Dom.get("map");
      bodyOnResize();
      init(mapDiv.style.width, mapDiv.style.height);
      bodyOnResize();
    });
  })();
  bodyOnResize = function()
  {
    var Dom = YAHOO.util.Dom;
    var width  = Dom.getViewportWidth();
    var height = Dom.getViewportHeight();
    var mapDiv = Dom.get("map");
    var topDiv = Dom.get("top");
    var toolbarRow = Dom.get("toolbarRow");
    var footer = Dom.get("footer");
    var header = Dom.get("header");
    var middleDiv = Dom.get("middle");
    var middleMapHeight = height - (toolbarRow.offsetHeight +
                                    header.offsetHeight +
                                    footer.offsetHeight +
                                    topDiv.offsetHeight);
    middleDiv.style.height = (middleMapHeight+toolbarRow.offsetHeight) +"px";
    mapDiv.style.width     = width +"px";
    mapDiv.style.height    = middleMapHeight + "px";

    if(map)
    {
      changeMapSize(mapDiv.style.width, mapDiv.style.height);
    }
  }
</g:javascript>

</html>