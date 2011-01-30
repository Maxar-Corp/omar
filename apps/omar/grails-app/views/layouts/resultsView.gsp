<%--
  Created by IntelliJ IDEA.
  User: dlucas
  Date: Nov 16, 2010
  Time: 8:09:29 PM
  To change this template use File | Settings | File Templates.
--%>
<html>
<head>

  <omar:bundle contentType="css" files="${[
    [dir: 'css', file: 'main.css'],
    [dir: 'css', file: 'omar-2.0.css']
  ]}"/>


  <link rel="stylesheet" type="text/css" href="${resource(plugin: 'richui', dir: 'js/yui/reset-fonts-grids', file: 'reset-fonts-grids.css')}"/>
  <link rel="stylesheet" type="text/css" href="${resource(plugin: 'richui', dir: 'js/yui/assets/skins/sam', file: 'skin.css')}"/>

  <omar:bundle contentType="javascript" files="${[
      [dir:'js', file: 'application.js'],
      [plugin:'richui' , dir:'js/yui/yahoo-dom-event', file: 'yahoo-dom-event.js'],
      [plugin:'richui' , dir:'js/datechooser', file: 'datechooser.js'],
      [plugin:'richui' , dir:'js/yui/calendar', file: 'calendar-min.js'],
      [plugin:'richui' , dir:'js/yui/element', file: 'element-min.js'],
      [plugin:'richui' , dir:'js/yui/tabview/', file: 'tabview-min.js']
  ]}"/>

  <style>
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
  </style>
  <title><g:layoutTitle default="Grails"/></title>
  <g:layoutHead/>
</head>

<body class="yui-skin-sam" onresize="${pageProperty(name: 'body.onresize')}">
<div id="header">
  <omar:securityClassificationBanner/>
</div>
<div id="top">
  <g:pageProperty name="page.top"/>
</div>
<div id="center">
  <g:pageProperty name="page.body"/>
</div>
<g:layoutBody/>
<div id="footer">
  <g:pageProperty name="page.footer"/>
  <omar:securityClassificationBanner/>
</div>

<g:javascript>
  (function()
  {
    //YAHOO.util.Dom.setStyle(document.body, 'display', 'none');
    var Dom = YAHOO.util.Dom;
    var Event = YAHOO.util.Event;
    Event.onDOMReady( function()
    {
      bodyOnResize();
    });
  })();
  function bodyOnResize()
  {
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