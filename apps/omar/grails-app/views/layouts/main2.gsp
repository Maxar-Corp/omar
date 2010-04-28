<html>
<head>
  <title><g:layoutTitle default="Grails"/></title>
  <link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css')}"/>
  <link rel="stylesheet" href="${resource(dir: 'css', file: 'omar-2.0.css')}"/>

  <link rel="stylesheet" type="text/css"
          href="${resource(plugin: 'richui', dir: 'js/yui/reset-fonts-grids',
                  file: 'reset-fonts-grids.css')}"/>
  <link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon"/>
  <g:layoutHead/>
  <g:javascript library="application"/>
</head>
<body onload="${pageProperty(name: 'body.onload')}" onresize="${pageProperty(name: 'body.onresize')}">
<div id="doc3" class="yui-t2">
  <div id="hd">
    <omar:securityClassificationBanner/>
    <img id="logo" src="${resource(dir: 'images', file: 'OMARLarge.png')}" alt="OMAR-2.0 Logo"/>
  </div>
  <div id="bd">
    <div id="yui-main">
      <div class="yui-b"><div class="yui-g">
        <!-- YOUR DATA GOES HERE -->
        <g:layoutBody/>
      </div>
      </div>
    </div>
    <div class="yui-b">
      <!-- YOUR NAVIGATION GOES HERE -->
      <g:render template="${sidebar}"/>
    </div>
  </div>
  <div id="ft">
    <omar:securityClassificationBanner/>
  </div>
</div>
</body>
</html>
