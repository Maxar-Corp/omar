<html>

<head>
  <meta name="apple-mobile-web-app-capable" content="yes" />
  <!--meta name="viewport" content="minimum-scale=1.0, width='device-width', maximum-scale=1.6, user-scalable=yes" /-->
  <title><g:layoutTitle default="Grails" /></title>
  <link rel="stylesheet" href="${resource(dir:'css',file:'main.css')}" />
  <link rel="stylesheet" type="text/css" href="${resource(plugin: 'yui', dir: 'js/yui/reset-fonts-grids', file: 'reset-fonts-grids.css')}"/>
  <%--
  <link rel="stylesheet" type="text/css" href="${resource(plugin: 'yui', dir: 'js/yui/resize/assets/skins/sam', file: 'resize.css')}"/>
  --%>
  <link rel="stylesheet" type="text/css" href="${resource(plugin: 'yui', dir: 'js/yui/layout/assets/skins/sam', file: 'layout.css')}"/>
  <g:layoutHead />
</head>

<body class="yui-skin-sam">

<div id="top1">
  <p><omar:securityClassificationBanner/></p>
</div>

<div id="bottom1">
  <p><omar:securityClassificationBanner/></p>
</div>

<div id="center1">
  <div align="center">
    <g:link class="home" uri="/"><img src="${resource(dir: 'images', file: 'omarLogo.png')}" alt="OMAR Logo" /></g:link>
  </div>
  <p><g:pageProperty name="page.content"/></p>
</div>

<g:javascript library="application" />
<g:javascript plugin='yui' src="yui/yahoo-dom-event/yahoo-dom-event.js"/>
<g:javascript plugin='yui' src="yui/element/element-min.js"/>
<%--
<g:javascript plugin='yui' src="yui/dragdrop/dragdrop-min.js"/>
<g:javascript plugin='yui' src="yui/resize/resize-min.js"/>
<g:javascript plugin='yui' src="yui/animation/animation-min.js"/>
--%>
<g:javascript plugin='yui' src="yui/layout/layout-min.js"/>

<script>

(function() {
  var Dom = YAHOO.util.Dom,
          Event = YAHOO.util.Event;

  Event.onDOMReady(function() {
    var layout = new YAHOO.widget.Layout({
      units: [
        { position: 'top', height: 25, body: 'top1' },
        { position: 'bottom', height: 25, body: 'bottom1' },
        { position: 'center', body: 'center1', scroll: true }
      ]
    });
    layout.render();
  });
})();
</script>

</body>

</html>
