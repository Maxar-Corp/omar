<html>
<head>
  <title><g:layoutTitle default="Grails"/></title>
  <piwik:trackPageview />
  <style>
  body {
    margin: 0;
    padding: 0;
    visibility: hidden;
  }
  </style>

  <meta content="yes" name="apple-mobile-web-app-capable"/>
  <meta content="minimum-scale=1.0, width=device-width, user-scalable=no" name="viewport"/>


  <g:layoutHead/>
  <asset:stylesheet src="generatedViews.css"/>
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
  <asset:javascript src="generatedViews.js"/>
<g:javascript>
  (function ()
  {
    var Dom = YAHOO.util.Dom;
    var Event = YAHOO.util.Event;

    Event.onDOMReady( function ()
    {
      var layout = new YAHOO.widget.Layout( {
        units:[
          { position:'top', height:25, body:'top' },
          { position:'center', body:'center', scroll:true },
          { position:'bottom', height:25, body:'bottom' }
        ]
      } );

      YAHOO.util.Dom.setStyle( document.body, 'visibility', 'visible' );

      layout.render();

        if(init)
        {
            init();
        }
    } );
  })();
</g:javascript>
<g:layoutBody/>
</body>
</html>
