<html>
<head>
  <title><g:layoutTitle default="Grails"/></title>
  <style>
  body {
    margin: 0;
    padding: 0;
  visibility: visible;
  }
  </style>
  <link rel="stylesheet" href="${omar.bundle(contentType: 'text/css', files: [
      resource(dir: 'css', file: 'main.css'),
      resource(dir: 'css', file: 'omar-2.0.css')
  ])}"/>

  <link rel="stylesheet" type="text/css" href="${resource(plugin: 'richui', dir: 'js/yui/reset-fonts-grids', file: 'reset-fonts-grids.css')}"/>
  <link rel="stylesheet" type="text/css" href="${resource(plugin: 'richui', dir: 'js/yui/assets/skins/sam', file: 'skin.css')}"/>

  <script type='text/javascript' src='${omar.bundle(contentType: "text/javascript", files: [
      resource(plugin: "richui", dir: "js/yui/yahoo-dom-event", file: "yahoo-dom-event.js"),
      resource(plugin: "richui", dir: "js/datechooser", file: "datechooser.js"),
      resource(plugin: "richui", dir: "js/yui/calendar", file: "calendar-min.js")
  ])}'></script>

  <g:layoutHead/>
</head>
<body class="${pageProperty(name: 'body.class')}" onload="${pageProperty(name: 'body.onload')}">
<div id="left">
  <g:pageProperty name="page.left"/>
</div>

<div id="top">
  <omar:securityClassificationBanner/>
  <p><g:pageProperty name="page.top2"/></p>
</div>

<div id="center">
  <g:pageProperty name="page.center"/>
</div>

<div id="right">
  <g:pageProperty name="page.right"/>
</div>

<div id="bottom">
  <omar:securityClassificationBanner/>
</div>

<g:javascript>
  (function()
  {
    var Dom = YAHOO.util.Dom;
    var Event = YAHOO.util.Event;

    //////////////////
    /*
    YAHOO.namespace("example.container");

    YAHOO.example.container.wait =
    new YAHOO.widget.Panel("wait",
    { width: "240px",
    fixedcenter: true,
    close: false,
    draggable: false,
    zindex:4,
    modal: true,
    visible: false
    }
    );

    YAHOO.example.container.wait.setHeader("Loading, please wait...");
    YAHOO.example.container.wait.setBody("<img src='${createLinkTo(dir: 'images', file: 'spinner.gif')}' alt='Spinner' />");
    YAHOO.example.container.wait.render(document.body);

    YAHOO.example.container.wait.show();
   */

    //////////////

    Event.onDOMReady( function()
    {
      var layout = new YAHOO.widget.Layout( {
        units: [
          { position: 'top', height: 67, body: 'top' },
          { position: 'bottom', height: 25, body: 'bottom' },
          { position: 'left', header: '', width: 200, resize: false, proxy: false, body: 'left', collapse: true, gutter: '0px 0px 0px 0px', scroll: true, maxWidth: 200 },
          { position: 'center', body: 'center' },
          { position: 'right', header: '', width: 200, resize: false, proxy: false, body: 'right', collapse: true, gutter: '0px 0px 0px 0px', maxWidth: 200 }
        ]
      } );

      layout.on( 'render', function()
      {
        var center = layout.getUnitByPosition( 'center' );

        var mapWidth = center.get( 'width' );
        var mapHeight = center.get( 'height' );
        mapWidget.changeMapSize( mapWidth, mapHeight );
      } );

      layout.on( 'resize', function()
      {
        var center = layout.getUnitByPosition( 'center' );
        var mapWidth = center.get( 'width' );
        var mapHeight = center.get( 'height' );
        mapWidget.changeMapSize( mapWidth, mapHeight );
      } );

      YAHOO.util.Dom.setStyle( document.body, 'visibility', 'visible' );
      layout.render();
    } );
  })();

</g:javascript>

<script type='text/javascript' src='${omar.bundle(contentType: "text/javascript", files: [
    resource(dir: "js", file: "application.js"),
    resource(plugin: "richui", dir: "js/yui/element", file: "element-min.js"),
    resource(plugin: "richui", dir: "js/yui/layout", file: "layout-min.js"),
    resource(plugin: "richui", dir: "js/yui/tabview", file: "tabview-min.js")
])}'></script>

<g:layoutBody/>

</body>
</html>