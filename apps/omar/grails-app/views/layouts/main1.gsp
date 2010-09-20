<html>

<head>
  <title><g:layoutTitle default="Grails" /></title>
  <link rel="stylesheet" href="${resource(dir:'css',file:'main.css')}" />
  <link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
  <link rel="stylesheet" type="text/css" href="${resource(plugin: 'richui', dir: 'js/yui/reset-fonts-grids', file: 'reset-fonts-grids.css')}"/>
  <link rel="stylesheet" type="text/css" href="${resource(plugin: 'richui', dir: 'js/yui/resize/assets/skins/sam', file: 'resize.css')}"/>
  <link rel="stylesheet" type="text/css" href="${resource(plugin: 'richui', dir: 'js/yui/layout/assets/skins/sam', file: 'layout.css')}"/>

  <link rel="stylesheet" type="text/css" href="${resource(plugin: 'richui', dir: 'js/yui/paginator/assets/skins/sam', file: 'paginator.css')}"/>
  <link rel="stylesheet" type="text/css" href="${resource(plugin: 'richui', dir: 'js/yui/datatable/assets/skins/sam', file: 'datatable.css')}"/>
  <link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.5.2/build/assets/skins/sam/skin.css">
  <g:layoutHead />
</head>

<body class="yui-skin-sam">

<div id="top1">
  <p><omar:securityClassificationBanner/></p>
  <div align="center">
    <img src="${resource(dir: 'images', file: 'omarLogo.png')}" alt="OMAR Logo" />
  </div>
  <p><g:pageProperty name="page.header"/></p>
</div>

<div id="bottom1">
  <p><g:pageProperty name="page.footer"/></p>
  <p><omar:securityClassificationBanner/></p>
</div>

<g:javascript plugin='richui' src="yui/utilities/utilities.js"/>
<g:javascript plugin='richui' src="yui/calendar/calendar-min.js"/>
<script type="text/javascript" src="http://yui.yahooapis.com/2.5.2/build/datasource/datasource-beta-min.js"></script>
<script type="text/javascript" src="http://yui.yahooapis.com/2.5.2/build/datatable/datatable-beta-min.js"></script>
<g:javascript plugin='richui' src="yui/container/container_core-min.js"/>
<g:javascript plugin='richui' src="yui/menu/menu-min.js"/>

<div id="center1">
  <p><g:pageProperty name="page.content"/></p>
</div>

<g:javascript library="application" />
<g:javascript plugin='richui' src="yui/yahoo-dom-event/yahoo-dom-event.js"/>
<g:javascript plugin='richui' src="yui/element/element-min.js"/>
<g:javascript plugin='richui' src="yui/dragdrop/dragdrop-min.js"/>
<g:javascript plugin='richui' src="yui/resize/resize-min.js"/>
<g:javascript plugin='richui' src="yui/animation/animation-min.js"/>
<g:javascript plugin='richui' src="yui/layout/layout-min.js"/>

<script>

(function() {
  var Dom = YAHOO.util.Dom,
          Event = YAHOO.util.Event;

  Event.onDOMReady(function() {
    var layout = new YAHOO.widget.Layout({
      units: [
        { position: 'top', height: 112, body: 'top1' },
        { position: 'bottom', height: 59, body: 'bottom1' },
        { position: 'center', body: 'center1', scroll: true }
      ]
    });
    layout.render();
  });
})();
</script>

</body>

</html>