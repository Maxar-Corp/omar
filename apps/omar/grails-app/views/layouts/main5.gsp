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

  <link rel="stylesheet" type="text/css" href="${omar.bundle(contentType: "text/css", files: [
      resource(dir: 'css', file: 'main.css'),
      resource(dir: 'css', file: 'omar-2.0.css'),
  ])}"/>

  <link rel="stylesheet" type="text/css" href="${resource(plugin: 'richui', dir: 'js/yui/reset-fonts-grids', file: 'reset-fonts-grids.css')}"/>
  <link rel="stylesheet" type="text/css" href="${resource(plugin: 'richui', dir: 'js/yui/assets/skins/sam', file: 'skin.css')}"/>

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
    visibility: hidden
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
  <div id="doc3" class="yui-t7">
    <div id="hd">
      <img id="logo" src="${resource(dir: 'images', file: 'OMARLarge.png')}" alt="OMAR-2.0 Logo"/>
    </div>
    <div id="bd">
      <div class="yui-g">
        <!-- YOUR DATA GOES HERE -->
        <g:layoutBody/>
      </div>
    </div>
    <div id="ft">
    </div>
  </div>
</div>

</body>

<script type='text/javascript' src='${omar.bundle(contentType: "text/javascript", files: [
    resource(dir: "js", file: "application.js"),
    resource(plugin: "richui", dir: "js/yui/yahoo-dom-event", file: "yahoo-dom-event.js"),
    resource(plugin: "richui", dir: "js/yui/element", file: "element-min.js"),
    resource(plugin: "richui", dir: "js/yui/layout", file: "layout-min.js")
])}'></script>

<g:javascript>
  (function()
  {
    var Event = YAHOO.util.Event;
    var Layout = YAHOO.widget.Layout;

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
            body: 'content',
            scroll: true
          }
        ]
      } );

      YAHOO.util.Dom.setStyle( document.body, 'visibility', 'visible' );        
      outerLayout.render();
    } );

  })();
</g:javascript>

</html>