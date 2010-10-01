<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: Apr 29, 2010
  Time: 8:46:05 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title><g:layoutTitle default="Grails"/></title>

  <%--
  <link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css')}"/>
  <link rel="stylesheet" href="${resource(dir: 'css', file: 'omar-2.0.css')}"/>
  --%>

  <link rel="stylesheet" type="text/css" href="${omar.bundle(contentType: 'text/css', files: [
      resource(dir: 'css', file: 'main.css'),
      resource(dir: 'css', file: 'omar-2.0.css')
  ])}"/>

  <link rel="stylesheet" type="text/css" href="${resource(plugin: 'richui', dir: 'js/yui/reset-fonts-grids', file: 'reset-fonts-grids.css')}"/>
  <link rel="stylesheet" type="text/css" href="${resource(plugin: 'richui', dir: 'js/yui/resize/assets/skins/sam', file: 'resize.css')}"/>
  <link rel="stylesheet" type="text/css" href="${resource(plugin: 'richui', dir: 'js/yui/layout/assets/skins/sam', file: 'layout.css')}"/>
  <%--
  <link rel="stylesheet" type="text/css" href="${resource(plugin: 'richui', dir: 'js/yui/button/assets/skins/sam', file: 'button.css')}"/>
  --%>


  <style>
    /*
    margin and padding on body element
    can introduce errors in determining
    element position and are not recommended;
    we turn them off as a foundation for YUI
    CSS treatments.
    */

  body {
    margin: 0;
    padding: 0;
  }

  /* Set the background color */
  .yui-skin-sam .yui-layout {
    background-color: #FFFFFF;
  }

  /* Style the body */
  .yui-skin-sam .yui-layout .yui-layout-unit div.yui-layout-bd {
    background-color: #FFFFFF;
  }

  </style>

  <g:layoutHead/>
</head>
<body class="yui-skin-sam">

<div id="header">
  <omar:securityClassificationBanner/>
</div>
<div id="footer">
  <omar:securityClassificationBanner/>
</div>
<div id="content">
  <div id="north">
    <div id="hd">
      
    </div>
    <g:pageProperty name="page.north"/>
  </div>
  <div id="south">
    <g:pageProperty name="page.south"/>
  </div>
  <div id="west">
    <g:pageProperty name="page.west"/>
  </div>
  <div id="center">
    <g:pageProperty name="page.center"/>
  </div>
</div>

<%--
<g:javascript library="application"/>
<g:javascript plugin='richui' src="yui/yahoo-dom-event/yahoo-dom-event.js"/>
<g:javascript plugin='richui' src="yui/element/element-min.js"/>
<g:javascript plugin='richui' src="yui/dragdrop/dragdrop-min.js"/>
<g:javascript plugin='richui' src="yui/resize/resize-min.js"/>
--%>
<%--
<g:javascript plugin='richui' src="yui/animation/animation-min.js"/>
--%>
<%--
<g:javascript plugin='richui' src="yui/layout/layout-min.js"/>
--%>


<script type="text/javascript" src="${omar.bundle(contentType: 'text/javascript', files: [
    resource(dir: 'js', file: 'application.js'),
    resource(plugin: 'richui', dir: 'js/yui/yahoo-dom-event', file: 'yahoo-dom-event.js'),
    resource(plugin: 'richui', dir: 'js/yui/element', file: 'element-min.js'),
    resource(plugin: 'richui', dir: 'js/yui/dragdrop', file: 'dragdrop-min.js'),
    resource(plugin: 'richui', dir: 'js/yui/resize', file: 'resize-min.js'),
    resource(plugin: 'richui', dir: 'js/yui/layout', file: 'layout-min.js')
])}"></script>

<g:javascript>
  (function()
  {
    var Dom = YAHOO.util.Dom;
    var Event = YAHOO.util.Event;
    var Layout = YAHOO.widget.Layout;

       YAHOO.util.Dom.setStyle(document.body, 'visibility', 'hidden');

    Event.onDOMReady( function()
    {
      var outerLayout = new Layout( {
        units: [
          {
            position: 'top',
            height: 25,
            body: 'header'
          },
          {
            position: 'bottom',
            height: 25,
            body: 'footer'
          },
          {
            position: 'center',
            body: 'content'
          }
        ]
      } );


      outerLayout.on( 'render', function()
      {
        var el = outerLayout.getUnitByPosition( 'center' ).get( 'wrap' );

        var innerLayout = new Layout( el, {
          parent: outerLayout,
          units: [
            {
              position: 'top',
              height: 43,
              resize: false,
              body: 'north'
            },
           
            { position: 'left', header: '', width: 200, resize: false, proxy: false, body: 'west', collapse: true, gutter: '0px 0px 0px 0px', scroll: true, maxWidth: 200 },
            {
              position: 'center',
              body: 'center'
            }
          ]
        } );

        innerLayout.on( 'render', function()
        {
          var c = innerLayout.getUnitByPosition( 'center' );
          var mapWidth = c.get( 'width' );
          var mapHeight = c.get( 'height' );

          //alert( mapWidth + ' ' + mapHeight );
          init( mapWidth, mapHeight );


          c.on( 'resize', function()
          {
            var c1 = innerLayout.getUnitByPosition( 'center' );
            var mapWidth = c1.get( 'width' );
            var mapHeight = c1.get( 'height' );

            alert( mapWidth + ' ' + mapHeight );
            changeMapSize( mapWidth, mapHeight );
          } );
        } );

        innerLayout.on( 'resize', function()
        {
          var center = innerLayout.getUnitByPosition( 'center' );
          var mapWidth = center.get( 'width' );
          var mapHeight = center.get( 'height' );
          changeMapSize( mapWidth, mapHeight );
        } );
          YAHOO.util.Dom.setStyle(document.body, 'visibility', 'visible');
        innerLayout.render();
      } );


      outerLayout.render();
    } );

  })();
</g:javascript>
<g:layoutBody/>
</body>

</html>