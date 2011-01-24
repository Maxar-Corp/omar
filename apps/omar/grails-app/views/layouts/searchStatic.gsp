<html>
<head>
  <link rel="stylesheet" href="${omar.bundle(contentType: 'text/css', files: [
      resource(dir: 'css', file: 'main.css'),
      resource(dir: 'css', file: 'omar-2.0.css')
  ])}"/>

  <link rel="stylesheet" type="text/css" href="${resource(plugin: 'richui', dir: 'js/yui/reset-fonts-grids', file: 'reset-fonts-grids.css')}"/>
  <link rel="stylesheet" type="text/css" href="${resource(plugin: 'richui', dir: 'js/yui/assets/skins/sam', file: 'skin.css')}"/>

  <script type='text/javascript' src='${omar.bundle(contentType: "text/javascript", files: [
      resource(dir: "js", file: "application.js"),
      resource(plugin: "richui", dir: "js/yui/yahoo-dom-event", file: "yahoo-dom-event.js"),
      resource(plugin: "richui", dir: "js/datechooser", file: "datechooser.js"),
      resource(plugin: "richui", dir: "js/yui/calendar", file: "calendar-min.js"),
      resource(plugin: "richui", dir: "js/yui/element", file: "element-min.js"),
      resource(plugin: "richui", dir: "js/yui/tabview", file: "tabview-min.js")
  ])}'>

  </script>

  <style>
  body{
    height:100%;
    width:100%;
    text-align:left;
    margin:0;
    padding:0;
    overflow-y:hidden;
  }
  #content
  {
    height:100%;
    min-height:100%;
    margin-bottom:-20px
  }
  #left
  {
    position:absolute;
    top: 60px;
    width:200px;
    height:80%;
    overflow-x:hidden;
    overflow-y:auto;
  }
  #right
  {
    position:absolute;
    height:80%;
    top: 60px;
    right: 0px;
    min-height:100%;
    width:200px;
    overflow-x:hidden;
    overflow-y:auto;
  }
  #center
  {
    position:absolute;
    top: 60px;
    left:200px;
    right:200px;
    height:80%;
  }
  #bottom
  {
    height:20px;
  }
  #top
  {
    height:20px;
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
<body class="${pageProperty(name: 'body.class')}" onresize="${pageProperty(name: 'body.onresize')}">
<div id="content">

<div id="header">
  <omar:securityClassificationBanner/>
</div>
<div id="top">
  <g:pageProperty name="page.top"/>
</div>
<div id="left">
  <g:pageProperty name="page.left"/>
</div>
<div id="center">
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
  <table>
    <tr>
      <td width="200px"><div id="mouseHoverDdOutput">&nbsp;</div></td>
      <td width="200px"><div id="mouseHoverDmsOutput">&nbsp;</div></td>
      <td width="200px"><div id="mouseHoverMgrsOutput">&nbsp;</div></td>
    </tr>
  </table>
  <g:pageProperty name="page.center"/>
</div>
<div id="right">
  <g:pageProperty name="page.right"/>
</div>
</div>
<div id="footer">
  <omar:securityClassificationBanner/>
</div>


<g:layoutBody />

</body>

<g:javascript>
  (function()
  {
    //YAHOO.util.Dom.setStyle(document.body, 'display', 'none');
    var Dom = YAHOO.util.Dom;
    var Event = YAHOO.util.Event;
    Event.onDOMReady( function()
    {
      bodyOnResize();
      init();
      bodyOnResize();
    });
  })();
  function bodyOnResize()
  {
    var Dom = YAHOO.util.Dom;
    var contentDiv = Dom.get("content");
    var leftDiv = Dom.get("left");
    var rightDiv = Dom.get("right");
    var mapDiv = Dom.get("map");
    var centerDiv = Dom.get("center");
    var toolbarRow = Dom.get("toolbarRow");
    var footer = Dom.get("footer");

    // IE6 seems to do better to use the root content div and then adjust everyone from  that
    var centerHeight = contentDiv.offsetHeight*.8;
    centerDiv.style.left  = leftDiv.offsetWidth + "px";
    mapDiv.style.width  = (contentDiv.offsetWidth - (leftDiv.offsetWidth + rightDiv.offsetWidth)) +"px";
    mapDiv.style.height = centerHeight - toolbarRow.offsetHeight-footer.offsetHeight  + "px";
    mapWidget.changeMapSize()
  }
</g:javascript>
</html>
