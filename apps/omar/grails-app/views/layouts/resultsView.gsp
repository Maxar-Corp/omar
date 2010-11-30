<%--
  Created by IntelliJ IDEA.
  User: dlucas
  Date: Nov 16, 2010
  Time: 8:09:29 PM
  To change this template use File | Settings | File Templates.
--%>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <title><g:layoutTitle default="Grails"/></title>
  <style>
  body {
    margin: 0;
    padding: 0;
    visibility: hidden;
  }
  </style>
  <link rel="stylesheet" href="${omar.bundle(contentType: 'text/css', files: [
      resource(dir: 'css', file: 'main.css'),
      resource(dir: 'css', file: 'omar-2.0.css')
  ])}"/>

  <link rel="stylesheet" type="text/css" href="${resource(plugin: 'richui', dir: 'js/yui/reset-fonts-grids', file: 'reset-fonts-grids.css')}"/>
  <link rel="stylesheet" type="text/css" href="${resource(plugin: 'richui', dir: 'js/yui/assets/skins/sam', file: 'skin.css')}"/>

  <g:layoutHead/>
</head>

<body class="yui-skin-sam">

<div id="header">
  <omar:securityClassificationBanner/>
  <g:pageProperty name="page.header"/>
</div>
<div id="body">
  <g:pageProperty name="page.body"/>
</div>
<div id="footer">
  <g:pageProperty name="page.footer"/>
  <omar:securityClassificationBanner/>
</div>

<script type="text/javascript" src="${omar.bundle(contentType: 'text/javascript', files: [
    resource(plugin: 'richui', dir: 'js/yui/yahoo-dom-event', file: 'yahoo-dom-event.js'),
    resource(plugin: 'richui', dir: 'js/yui/element', file: 'element-min.js'),
    resource(plugin: 'richui', dir: 'js/yui/layout', file: 'layout-min.js')
])}"></script>

<g:javascript>
  (function()
  {
    var Dom = YAHOO.util.Dom;
    var Event = YAHOO.util.Event;

    Event.onDOMReady( function()
    {
      var layout = new YAHOO.widget.Layout( {
        units: [
          { position: 'top', height: 67, body: 'header' },
          { position: 'center', body: 'body', scroll: true },
          { position: 'bottom', height: 66, body: 'footer' }
        ]
      } );
      YAHOO.util.Dom.setStyle( document.body, 'visibility', 'visible' );
      layout.render();
    } );
  })();
</g:javascript>

<g:layoutBody/>

</body>
</html>