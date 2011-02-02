<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: 1/12/11
  Time: 1:29 PM
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

<body class="${pageProperty(name: 'body.class')} onresize="bodyOnResize();" onload="${pageProperty(name: 'body.onload')}bodyOnResize();">
<omar:bundle contentType="javascript" files="${[
    [dir:'js', file: 'application.js'],
    [plugin:'richui' , dir:'js/yui/yahoo-dom-event', file: 'yahoo-dom-event.js'],
    [plugin:'richui' , dir:'js/datechooser', file: 'datechooser.js'],
    [plugin:'richui' , dir:'js/yui/calendar', file: 'calendar-min.js'],
    [plugin:'richui' , dir:'js/yui/element', file: 'element-min.js'],
    [plugin:'richui' , dir:'js/yui/tabview/', file: 'tabview-min.js']
]}"/>
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
<g:javascript>
    //YAHOO.util.Dom.setStyle(document.body, 'display', 'none');
  function bodyOnResize()
  {
    var Dom = YAHOO.util.Dom;
    var top = Dom.get("top");
    var header = Dom.get("header");
    var footer = Dom.get("footer");
    var center = Dom.get("center");

    center.style.height = Math.abs(footer.offsetTop - (top.offsetTop+top.offsetHeight));

  }
</g:javascript>
</body>
</html>
