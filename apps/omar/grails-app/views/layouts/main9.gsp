<%--
  Created by IntelliJ IDEA.
  User: dlucas
  Date: May 5, 2010
  Time: 8:06:08 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title><g:layoutTitle default="Grails"/></title>

  <link rel="stylesheet" type="text/css" href="${resource(plugin: 'richui', dir: 'js/yui/reset-fonts-grids', file: 'reset-fonts-grids.css')}" />
  <link rel="stylesheet" type="text/css" href="${resource(plugin: 'richui', dir: 'js/yui/resize/assets/skins/sam', file: 'resize.css')}" />
  <link rel="stylesheet" type="text/css" href="${resource(plugin: 'richui', dir: 'js/yui/layout/assets/skins/sam', file: 'layout.css')}" />
  <link rel="stylesheet" type="text/css" href="${resource(plugin: 'richui', dir: 'js/yui/button/assets/skins/sam', file: 'button.css')}" />

  <link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css')}"/>
  <link rel="stylesheet" href="${resource(dir: 'css', file: 'omar-2.0.css')}"/>

  <g:layoutHead/>

</head>
<body class="${pageProperty(name: 'body.class')}" onload="${pageProperty(name: 'body.onload')}">

<div id="header1">
  <omar:securityClassificationBanner/>
</div>

<div id="top1">
  <g:pageProperty name="page.top"/>
</div>

<div id="left1">
  <g:pageProperty name="page.left"/>
</div>

<div id="center1">
  <g:pageProperty name="page.center"/>
</div>

<div id="right1">
  <g:pageProperty name="page.right"/>
</div>

<div id="footer1">
  <omar:securityClassificationBanner/>
</div>

<g:javascript>

  (function()
  {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event;

    Event.onDOMReady( function()
    {
      var layout = new YAHOO.widget.Layout( {
        minWidth: 1000,
        minHeight: 500,
        units: [
          { position: 'top', height: 25, body: 'header1' },
          { position: 'bottom', height: 25, body: 'footer1' },
          { position: 'center', body: 'main' }
        ]
      } );
      layout.on( 'render', function()
      {
        var el = layout.getUnitByPosition( 'center' ).get( 'wrap' );
        var layout2 = new YAHOO.widget.Layout( el, {
          parent: layout,
          minWidth: 400,
          minHeight: 200,
          units: [
            { position: 'top', height: 26, body: 'top1', gutter: '0px', maxHeight: 80 },
            { position: 'left', header: '', width: 200, resize: false, proxy: false, body: 'left1', collapse: true, gutter: '0px 0px 0px 0px', scroll: true, maxWidth: 200 },
            { position: 'center', body: 'center1', gutter: '0px 0px 0px 0px', scroll: false },
            { position: 'right', header: '', width: 200, resize: false, proxy: false, body: 'right1', collapse: true, gutter: '0px 0px 0px 0px', maxWidth: 200 }
          ]
        } );

        layout2.on( 'render', function()
        {
          var c = layout2.getUnitByPosition( 'center' );

          c.on( 'resize', function()
          {
            var c1 = layout2.getUnitByPosition( 'center' );

            var mapWidth1 = c1.get( 'width' );
            var mapHeight1 = c1.get( 'height' );

            mapWidget.changeMapSize( mapWidth1, mapHeight1 );
          } );
        } );

        //Listen for the render event
        layout2.on( 'render', function()
        {
          //Now give the top unit a zindex to make it and it's menus go above the other units
          //layout2.getUnitByPosition( 'top' ).setStyle( 'zIndex', 10000 );
        } );

        layout2.render();
      } );

      layout.render();
    } );
  })();

</g:javascript>

<g:javascript library="application"/>

<g:javascript plugin='richui' src="yui/yahoo-dom-event/yahoo-dom-event.js"/>

<g:javascript plugin='richui' src="yui/animation/animation-min.js"/>
<g:javascript plugin='richui' src="yui/layout/layout-min.js"/>

<g:pageProperty name="page.javascript"/>

</body>
</html>