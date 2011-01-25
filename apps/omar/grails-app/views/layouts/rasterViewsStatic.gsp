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
    overflow-x:hidden;
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
    height:80%;
    width:100%;
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

  <div id="middle">
    <div id="left">
      <g:pageProperty name="page.left"/>
    </div>
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
      <g:pageProperty name="page.middle"/>
    </div>
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
      var mapDiv = Dom.get("map");
      bodyOnResize(false);
      init();
      bodyOnResize(true);
    });
  })();
  bodyOnResize = function(changeMapSizeFlag)
  {
    var Dom = YAHOO.util.Dom;
    var width  = Dom.getViewportWidth();
    var height = Dom.getViewportHeight();
    var leftDiv = Dom.get("left");
    var mapDiv = Dom.get("map");
    var topDiv = Dom.get("top");
    var toolbarRow = Dom.get("toolbarRow");
    var footer = Dom.get("footer");
    var header = Dom.get("header");
    var middleDiv = Dom.get("middle");

    middleDiv.style.height = height - (header.offsetHeight + footer.offsetHeight + topDiv.offsetHeight) + "px";
    mapDiv.style.width     = (width - leftDiv.offsetWidth ) +"px";
    mapDiv.style.height    = (middle.offsetHeight    - (toolbarRow.offsetHeight))+ "px";

    if(changeMapSizeFlag)
    {
      if(map) changeMapSize(mapDiv.style.width, mapDiv.style.height);
    }
   // mapWidget.changeMapSize()
  }.defaults(true);
</g:javascript>
</html>
