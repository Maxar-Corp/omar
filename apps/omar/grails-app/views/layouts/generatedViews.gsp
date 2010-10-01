<html>
<head>
  <title><g:layoutTitle default="Grails" /></title>
  <link rel="stylesheet" href="${resource(dir:'css',file:'main.css')}" />
  <link rel="stylesheet" type="text/css" href="${resource(plugin: 'richui', dir: 'js/yui/reset-fonts-grids', file: 'reset-fonts-grids.css')}"/>
  <link rel="stylesheet" type="text/css" href="${resource(plugin: 'richui', dir: 'js/yui/layout/assets/skins/sam', file: 'layout.css')}"/>
  <g:layoutHead/>
</head>

<body class="yui-skin-sam">

<div id="top">
  <omar:securityClassificationBanner/>
</div>

<div id="center">
  <g:pageProperty name="page.content"/>
</div>

<div id="bottom">
  <omar:securityClassificationBanner/>
</div>

<g:javascript library="application"/>
<g:javascript plugin='richui' src="yui/yahoo-dom-event/yahoo-dom-event.js"/>
<g:javascript plugin='richui' src="yui/element/element-min.js"/>
<g:javascript plugin='richui' src="yui/layout/layout-min.js"/>

<script>
  (function() {
    var Dom = YAHOO.util.Dom,
            Event = YAHOO.util.Event;

    YAHOO.util.Dom.setStyle(document.body, 'visibility', 'hidden');

    Event.onDOMReady(function() {
      var layout = new YAHOO.widget.Layout({
        units: [
          { position: 'top', height: 25, body: 'top' },
          { position: 'center', body: 'center', scroll: true },
          { position: 'bottom', height: 25, body: 'bottom' }
        ]
      });

      YAHOO.util.Dom.setStyle(document.body, 'visibility', 'visible');

      layout.render();
    });
  })();
</script>

</body>
</html>