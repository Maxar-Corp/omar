<!doctype html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
  <title><g:layoutTitle default="Grails"/></title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="shortcut icon" href="${resource( dir: 'images', file: 'favicon.ico' )}" type="image/x-icon">
  <link rel="apple-touch-icon" href="${resource( dir: 'images', file: 'apple-touch-icon.png' )}">
  <link rel="apple-touch-icon" sizes="114x114" href="${resource( dir: 'images', file: 'apple-touch-icon-retina.png' )}">
  <%--
  <link rel="stylesheet" href="${resource( dir: 'css', file: 'mainG2.css' )}" type="text/css">
  <link rel="stylesheet" href="${resource( dir: 'css', file: 'mobile.css' )}" type="text/css">
  --%>
  <g:layoutHead/>
  <r:require modules="mainG2"/>
  <r:layoutResources/>
</head>

<body class="yui-skin-sam">


<%--
<g:javascript library="application"/>
--%>


<div id="top">
  <omar:securityClassificationBanner/>
  <div id="grailsLogo" role="banner">
    <a href="http://grails.org"><img src="${resource( dir: 'images', file: 'grails_logo.png' )}" alt="Grails"/></a>
  </div>
</div>

<div id="center">
  <g:pageProperty name="page.content"/>
</div>

<div id="bottom">
  <%--
  <div class="footer" role="contentinfo"></div>
  --%>
  <div id="spinner" class="spinner" style="display:none;">
    <g:message code="spinner.alt" default="Loading&hellip;"/>
  </div>
  <omar:securityClassificationBanner/>
</div>

<g:layoutBody/>

<r:script>
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
    } );
  })();
</r:script>

<r:layoutResources/>
</body>
</html>