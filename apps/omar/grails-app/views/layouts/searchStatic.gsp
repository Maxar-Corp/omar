<html>
<head>
  <omar:bundle contentType="css" files="${[
    [dir: 'css', file: 'main.css'],
    [dir: 'css', file: 'omar-2.0.css']
  ]}"/>

  <link rel="stylesheet" type="text/css"
        href="${resource(plugin: 'yui', dir: 'js/yui/reset-fonts-grids', file: 'reset-fonts-grids.css')}"/>
  <link rel="stylesheet" type="text/css"
        href="${resource(plugin: 'yui', dir: 'js/yui/assets/skins/sam', file: 'skin.css')}"/>


  <style>
  html, body {
    height: 100%;
    width: 100%;
    text-align: left;
    margin: 0;
    padding: 0;
    overflow-y: hidden;
    overflow-x: hidden;
  }

  #content {
    height: 100%;
    min-height: 100%;
    margin-bottom: -20px;
  }

  #middle {
    position: relative;
    top: 0px;
    height: 100%;
    width: 100%;
    margin-top: 0px;
    margin-bottom: 0px;
  }

  #left {
    position: absolute;
    top: 0;
    width: 200px;
    height: 100%;
    overflow-x: hidden;
    overflow-y: auto;
  }

  #right {
    position: absolute;
    height: 100%;
    top: 0;
    right: 0px;
    min-height: 100%;
    width: 200px;
    overflow-x: hidden;
    overflow-y: auto;
  }

  #center {
    position: absolute;
    top: 0;
    left: 200px;
    right: 200px;
    height: 100%;
  }

  #header {
    position: relative;
    font-size: 20px;
    top: 0;
    width: 100%;
  }

  #footer {
    position: absolute;
    font-size: 20px;
    width: 100%;
    bottom: 0px;
    border-top: 0px;
  }

  #mouseRow {
    font-size: 12px;
  }

  #map {
    width: 100%;
    height: 100%;
  }
  </style>
  <title><g:layoutTitle default="Grails"/></title>
  <g:layoutHead/>
</head>

<body class="${pageProperty(name: 'body.class')}" onresize="bodyOnResize();${pageProperty(name: 'body.onresize')}"
      onload="bodyOnResize( false );
      ${pageProperty(name: 'body.onload')}bodyOnResize();">

<div id="content" class="content">

  <div id="header" class="header">
    <omar:securityClassificationBanner fontSize="20px"/>
  </div>

  <div id="top" class="top">
    <g:pageProperty name="page.top"/>
  </div>

  <div id="middle" class="middle">
    <div id="left">
      <g:pageProperty name="page.left"/>
    </div>

    <div id="center" class="center">
      <table>
        <tr id="toolbarRow">
          <td>
            <div id="toolBar" class="olControlPanel"></div>
            <button type="button" onclick="javascript:search();">Search</button>
            <g:checkBox id="spatialSearchFlag" value="true" checked="true"
                        onclick="javascript:this.value = this.checked"/>
            <label>Include spatial</label>
          </td>
        </tr>
        <tr id="mapRow">
          <td id="mapColumn">
            <div id="map"></div>
          </td>
        </tr>
      </table>
      <table>
        <tr id="mouseRow" class="mouseRow">
          <td width="33%"><div id="mouseHoverDdOutput">&nbsp;</div></td>
          <td width="33%"><div id="mouseHoverDmsOutput">&nbsp;</div></td>
          <td width="33%"><div id="mouseHoverMgrsOutput">&nbsp;</div></td>
        </tr>
      </table>
      <g:pageProperty name="page.center"/>
    </div>

    <div id="right" class="right">
      <g:pageProperty name="page.right"/>
    </div>
  </div>

  <div id="footer">
    <g:pageProperty name="page.footer"/>
    <omar:securityClassificationBanner fontSize="20px"/>
  </div>

  <omar:bundle contentType="javascript" files="${[
    [dir:'js', file: 'application.js'],
    [plugin:'yui' , dir:'js/yui/yahoo-dom-event', file: 'yahoo-dom-event.js'],
    [plugin:'yui' , dir:'js/yui/calendar', file: 'calendar-min.js'],
    [plugin:'yui' , dir:'js/yui/element', file: 'element-min.js'],
    [plugin:'yui' , dir:'js/yui/tabview/', file: 'tabview-min.js'],
    [plugin:'yui' , dir:'js/yui/container/', file: 'container_core-min.js'],
    [plugin:'yui' , dir:'js/yui/menu/', file: 'menu-min.js'],
    [plugin:'yui' , dir:'js/yui/dragdrop', file:'dragdrop-min.js'],
    [dir:'js', file: 'datechooser.js']
  ]}"/>

  <g:javascript>
    bodyOnResize = function( changeMapSizeFlag )
    {
      var Dom = YAHOO.util.Dom;
      var contentDiv = Dom.get( "content" );
      var leftDiv = Dom.get( "left" );
      var rightDiv = Dom.get( "right" );
      var mapDiv = Dom.get( "map" );
      var topDiv = Dom.get( "top" );
      var toolbarRow = Dom.get( "toolbarRow" );
      var mouseRow = Dom.get( "mouseRow" );
      var footer = Dom.get( "footer" );
      var header = Dom.get( "header" );
      var middleDiv = Dom.get( "middle" );

      //middleDiv.style.top = (topDiv.offsetHeight+header.offsetHeight) + "px";
      // IE6 seems to do better to use the root content div and then adjust everyone from  that
      var middleHeight = Math.abs( footer.offsetTop - (topDiv.offsetTop + topDiv.offsetHeight) );
      middleDiv.style.height = middleHeight + "px";
      var mapWidth = Math.abs( rightDiv.offsetLeft - (leftDiv.offsetLeft + leftDiv.offsetWidth) );
      var mapHeight = middleHeight - (toolbarRow.offsetHeight + mouseRow.offsetHeight);

      mapDiv.style.width = mapWidth + "px";
      mapDiv.style.height = mapHeight + "px";


      if ( changeMapSizeFlag && mapWidget )
      {
        mapWidget.changeMapSize();
      }
    }.defaults( true );
  </g:javascript>

  <g:layoutBody/>

</body>

</html>
