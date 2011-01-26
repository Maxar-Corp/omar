<%--
  Created by IntelliJ IDEA.
  User: dlucas
  Date: Nov 16, 2010
  Time: 8:09:29 PM
  To change this template use File | Settings | File Templates.
--%>
<html>
<head>

  <%--
  <link rel="stylesheet" href="${omar.bundle(contentType: 'text/css', files: [
      resource(dir: 'css', file: 'main.css'),
      resource(dir: 'css', file: 'omar-2.0.css')
  ])}"/>
  --%>
  <link rel="stylesheet" type="text/css" href="${resource(dir: 'css', file: 'main.css')}"/>
  <link rel="stylesheet" type="text/css" href="${resource(dir: 'css', file: 'omar-2.0.css')}"/>

  <link rel="stylesheet" type="text/css" href="${resource(plugin: 'richui', dir: 'js/yui/reset-fonts-grids', file: 'reset-fonts-grids.css')}"/>
  <link rel="stylesheet" type="text/css" href="${resource(plugin: 'richui', dir: 'js/yui/assets/skins/sam', file: 'skin.css')}"/>

  <g:javascript plugin="richui" src="yui/yahoo-dom-event/yahoo-dom-event.js"/>
  <g:javascript plugin="richui" src="datechooser/datechooser.js"/>
  <g:javascript plugin="richui" src="yui/calendar/calendar-min.js"/>
  <g:javascript plugin="richui" src="yui/element/element-min.js"/>
  <g:javascript plugin="richui" src="yui/tabview/tabview-min.js"/>

  <%--
  <script type='text/javascript' src='${omar.bundle(contentType: "text/javascript", files: [
      resource(dir: "js", file: "application.js"),
      resource(plugin: "richui", dir: "js/yui/yahoo-dom-event", file: "yahoo-dom-event.js"),
      resource(plugin: "richui", dir: "js/datechooser", file: "datechooser.js"),
      resource(plugin: "richui", dir: "js/yui/calendar", file: "calendar-min.js"),
      resource(plugin: "richui", dir: "js/yui/element", file: "element-min.js"),
      resource(plugin: "richui", dir: "js/yui/tabview", file: "tabview-min.js")
  ])}'>
  </script>
  --%>

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
<div id="footer">
  <g:pageProperty name="page.footer"/>
  <omar:securityClassificationBanner/>
</div>

<script type="text/javascript" src="${omar.bundle(contentType: 'text/javascript', files: [
    resource(plugin: 'richui', dir: 'js/yui/yahoo-dom-event', file: 'yahoo-dom-event.js'),
    resource(plugin: 'richui', dir: 'js/yui/element', file: 'element-min.js'),
    resource(plugin: 'richui', dir: 'js/yui/layout', file: 'layout-min.js')
])}"></script>

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

<g:layoutBody/>

</body>
</html>