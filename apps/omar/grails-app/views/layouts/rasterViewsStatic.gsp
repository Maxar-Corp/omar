<html>
<head>
  <omar:bundle contentType="css" files="${[
      [dir: 'css', file: 'main.css'],
      [dir: 'css', file: 'omar-2.0.css']
  ]}"/>

  <link rel="stylesheet" type="text/css" href="${resource(plugin: 'richui', dir: 'js/yui/reset-fonts-grids', file: 'reset-fonts-grids.css')}"/>
  <link rel="stylesheet" type="text/css" href="${resource(plugin: 'richui', dir: 'js/yui/assets/skins/sam', file: 'skin.css')}"/>
  <link rel="stylesheet" type="text/css" href="${resource(plugin: 'richui', dir: 'js/yui/assets/skins/sam', file: 'slider.css')}"/>


  <style>
  body{
    height:100%;
    width:100%;
    text-align:left;
    margin:0;
    padding:0;
    overflow-y:hidden;
    overflow-x:hidden;
  }
  #map{
    width:100%;
  }
  #content
  {
    height:100%;
    min-height:100%;
    margin-bottom:-20px;
  }
  #left
  {
    position:absolute;
    top: 0;
    width:200px;
    height:100%;
    overflow-x:hidden;
    overflow-y:auto;
  }
  #centerMap
  {
    position:absolute;
    top: 0px;
    left:200px;
    height:100%;
  }
  #middle
  {
    position:relative;
    top:0px;
    height:100%;
    width:100%;
    margin-top:0px;
    margin-bottom:0px;
  }
  #header{
    position:relative;
    font-size:12px;
    top:0;
    width:100%;
  }
  #footer{
    position:absolute;
    font-size:12px;
    width:100%;
    bottom:0px;
    border-top:0px;
  }
  .h1
  {
   width:100%;
    height:100%;
  }
  .nav{
      font-size:14px;
  }
  </style>
  <title><g:layoutTitle default="Grails"/></title>
  <g:layoutHead/>
</head>
<body class="${pageProperty(name: 'body.class')}" onresize="bodyOnResize();${pageProperty(name: 'body.onresize')}" onload="bodyOnResize(false);${pageProperty(name: 'body.onload')}bodyOnResize();">
<omar:bundle contentType="javascript" files="${[
    [dir:'js', file: 'application.js'],
    [plugin:'richui' , dir:'js/yui/yahoo-dom-event', file: 'yahoo-dom-event.js'],
    [plugin:'richui' , dir:'js/datechooser', file: 'datechooser.js'],
    [plugin:'richui' , dir:'js/yui/calendar', file: 'calendar-min.js'],
    [plugin:'richui' , dir:'js/yui/element', file: 'element-min.js'],
    [plugin:'richui' , dir:'js/yui/tabview/', file: 'tabview-min.js'],
    [plugin:'richui' , dir:'js/yui/container/', file: 'container_core.js'],
    [plugin:'richui' , dir:'js/yui/menu/', file: 'menu-min.js'],
    [plugin:'richui' , dir:'js/yui/dragdrop', file:'dragdrop-min.js'],
    [plugin:'richui' , dir:'js/yui/slider', file: 'slider-min.js'],
    
]}"/>

<div id="content">
  <div id="header">
    <omar:securityClassificationBanner/>
  </div>
  <div id="top">
    <g:pageProperty name="page.top"/>
  </div>

  <div id="middle">
    <div id="left">
      <g:pageProperty name="page.left"/>
    </div>
    <div id="centerMap">
      <table>
        <tr>
          <td id="toolbarRow" colspan=3>
            <div id="toolBar" class="olControlPanel"></div>
          </td>
        </tr>
        <tr id="mapRow">
          <td id="mapColumn" colspan=3>
            <div id="map"></div>
          </td>
        </tr>

		    <tr>
	          <td width=33.3%><div id="ddMouseMapCtr" style="font-size:14px">&nbsp;</div></td>
	<td width=33.3%><div id="dmsMouseMapCtr" style="font-size:14px">&nbsp;</div></td>
	<td width=33.3%><div id="mgrsMouseMapCtr" style="font-size:14px">&nbsp;</div></td>
	        </tr>


      </table>
      <g:pageProperty name="page.middle"/>
    </div>
  </div>
</div>

<div id="footer">
  <omar:securityClassificationBanner/>
</div>

<g:javascript>
    //YAHOO.util.Dom.setStyle(document.body, 'display', 'none');
  bodyOnResize = function(changeMapSizeFlag)
  {
    var Dom        = YAHOO.util.Dom;
    var width      = Dom.getViewportWidth();
    var leftDiv    = Dom.get("left");
    var mapDiv     = Dom.get("map");
    var topDiv     = Dom.get("top");
    var toolbarRow = Dom.get("toolbarRow");
    var footer     = Dom.get("footer");
    var header     = Dom.get("header");
    var middleDiv  = Dom.get("middle");
    var ddMouseMapCtrDiv  = Dom.get("ddMouseMapCtr");
    var top          = topDiv.offsetTop+topDiv.offsetHeight;
    var bottom       = footer.offsetTop;
    var middleHeight = Math.abs(bottom-top);
    var coordinateHeight = ddMouseMapCtrDiv.offsetHeight;
    middleDiv.style.height =  middleHeight + "px";
    mapDiv.style.width     = (width - (leftDiv.offsetLeft+leftDiv.offsetWidth )) +"px";
    mapDiv.style.height    = (middleHeight - toolbarRow.offsetHeight - coordinateHeight) - 14 +"px";

    if(changeMapSizeFlag)
    {
        changeMapSize();
    }
  }.defaults(true)
</g:javascript>

<g:layoutBody />

</body>
</html>
