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

  <omar:bundle contentType="css" files="${[
      [dir: 'css', file: 'main.css'],
      [dir: 'css', file: 'omar-2.0.css'],
  ]}"/>

  <link rel="stylesheet" type="text/css"
        href="${resource(plugin: 'yui', dir: 'js/yui/reset-fonts-grids', file: 'reset-fonts-grids.css')}"/>
  <link rel="stylesheet" type="text/css"
        href="${resource(plugin: 'yui', dir: 'js/yui/assets/skins/sam', file: 'skin.css')}"/>

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
    visibility: hidden;
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
  <g:javascript library="application"/>
</head>

<body class="yui-skin-sam">

<div id="header">
  <omar:securityClassificationBanner/>
</div>

<div id="footer">
  <omar:securityClassificationBanner/>
</div>

<div id="content">
  <g:layoutBody/>
</div>

</body>

<omar:bundle contentType="javascript" files="${[
    [plugin: 'yui', dir: 'js/yui/yahoo-dom-event', file: 'yahoo-dom-event.js'],
    [plugin: 'yui', dir: 'js/yui/element', file: 'element-min.js'],
    [plugin: 'yui', dir: 'js/yui/layout', file: 'layout-min.js']
]}"/>

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