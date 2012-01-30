<html>
<head>

  <style>
  body {
    height: 100%;
    width: 100%;
    text-align: left;
    margin: 0;
    padding: 0;
    overflow-y: hidden;
    overflow-x: hidden;
  }

  #map {
    width: 100%;
  }

  #content {
    height: 100%;
    min-height: 100%;
    margin-bottom: -20px;
      text-align: left;
  }

  #left {
    position: absolute;
    top: 0;
    width: 200px;
    height: 100%;
    overflow-x: hidden;
    overflow-y: auto;
  }

  #centerMap {
    position: absolute;
    top: 0px;
    left: 200px;
    height: 100%;
  }

  #middle {
    position: relative;
    top: 0px;
    height: 100%;
    width: 100%;
    margin-top: 0px;
    margin-bottom: 0px;
  }

  #header {
    position: relative;
    font-size: 12px;
    top: 0;
    width: 100%;
  }

  #footer {
    position: absolute;
    font-size: 12px;
    width: 100%;
    bottom: 0px;
    border-top: 0px;
  }

  .h1 {
    width: 100%;
    height: 100%;
  }

  .nav {
    font-size: 14px;
  }
  </style>
  <title><g:layoutTitle default="Grails"/></title>
  <g:layoutHead/>
  <r:require modules="rasterViewsStatic"/>
  <r:layoutResources/>
</head>

<body class="${pageProperty(name: 'body.class')}" onresize="bodyOnResize();${pageProperty(name: 'body.onresize')}"
      onload="bodyOnResize( false );
      ${pageProperty(name: 'body.onload')}bodyOnResize();">
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
            <div id="map"><div id="compassMap" style="position: absolute; z-index: 1000"></div></div>
          </td>
        </tr>

        <tr>
          <td width=33.3%><div id="ddMouseMapCtr" style="font-size:14px; color: #00CCFF"">&nbsp;</div></td>
    <td width=33.3%><div id="dmsMouseMapCtr" style="font-size:14px; color: #00CCFF"">&nbsp;</div></td>
  <td width=33.3%><div id="mgrsMouseMapCtr" style="font-size:14px; color: #00CCFF"">&nbsp;</div></td>
</tr>

</table>
<g:pageProperty name="page.middle"/>
</div>
</div>
</div>

<div id="footer">
  <omar:securityClassificationBanner/>
</div>

<r:script>
  //YAHOO.util.Dom.setStyle(document.body, 'display', 'none');
  bodyOnResize = function ( changeMapSizeFlag )
  {
    var Dom = YAHOO.util.Dom;
    var width = Dom.getViewportWidth();
    var leftDiv = Dom.get( "left" );
    var mapDiv = Dom.get( "map" );
    var topDiv = Dom.get( "top" );
    var toolbarRow = Dom.get( "toolbarRow" );
    var footer = Dom.get( "footer" );
    var header = Dom.get( "header" );
    var middleDiv = Dom.get( "middle" );
    var ddMouseMapCtrDiv = Dom.get( "ddMouseMapCtr" );
    var top = topDiv.offsetTop + topDiv.offsetHeight;
    var bottom = footer.offsetTop;
    var middleHeight = Math.abs( bottom - top );
    var coordinateHeight = ddMouseMapCtrDiv.offsetHeight;
    middleDiv.style.height = middleHeight + "px";
    mapDiv.style.width = (width - (leftDiv.offsetLeft + leftDiv.offsetWidth )) + "px";
    mapDiv.style.height = (middleHeight - toolbarRow.offsetHeight - coordinateHeight) - 14 + "px";

    if ( changeMapSizeFlag )
    {
      changeMapSize();
    }
  }.defaults( true )
</r:script>

<g:layoutBody/>
<r:layoutResources/>
</body>
</html>
