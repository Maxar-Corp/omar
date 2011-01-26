<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: 1/12/11
  Time: 1:29 PM
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
  <link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css')}"/>
  <link rel="stylesheet" href="${resource(dir: 'css', file: 'omar-2.0.css')}"/>

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
    margin:0;
    padding:0;
    overflow-y:hidden;
    overflow-x:hidden;
  }
  #content
  {
    height:100%;
    min-height:100%;
    margin-bottom:-20px
  }
  #header{
    position:relative;
    width:100%;
    height:20px;
    top:0px;
  }

  #top{
    position:relative;
    text-align:left;
    width:100%;
  }
   center{
     position:relative;
     width:100%;
     height:100%;
   }
  footer{
    position:absolute;
    width:100%;
    bottom:0px;
    height:20px;
  }
  </style>
  <title><g:layoutTitle default="Grails"/></title>
  <g:layoutHead/>
</head>

<body class="${pageProperty(name: 'body.class')} onresize="bodyOnResize();">
<div id="content">

  <div id="header">
    <omar:securityClassificationBanner/>
  </div>
  <div id="top">
    <g:pageProperty name="page.top"/>
  </div>
  <div id="center">
    <g:pageProperty name="page.center"/>
  </div>
</div>

<div id="footer">
  <omar:securityClassificationBanner/>
</div>

  <g:layoutBody/>
</table>
</body>
<g:javascript>
  (function()
  {
    //YAHOO.util.Dom.setStyle(document.body, 'display', 'none');
    var Dom = YAHOO.util.Dom;
    var Event = YAHOO.util.Event;
    Event.onDOMReady( function()
    {
    } );
  })();
</g:javascript>
</html>
