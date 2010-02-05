<html>
<head>
  <title><g:layoutTitle default="Grails"/></title>
  <link rel="stylesheet" href="${createLinkTo(dir: 'css', file: 'main.css')}"/>
  <link rel="stylesheet" href="${createLinkTo(dir: 'css', file: 'omar-2.0.css')}"/>

  <link rel="stylesheet" type="text/css"
          href="${createLinkTo(dir: 'plugins/richui-0.7/js/yui/reset-fonts-grids',
                  file: 'reset-fonts-grids.css')}"/>
  <link rel="shortcut icon" href="${createLinkTo(dir: 'images', file: 'favicon.ico')}" type="image/x-icon"/>
  <g:layoutHead/>
  <g:javascript library="application"/>
</head>
<body onload="${pageProperty(name: 'body.onload')}" onresize="${pageProperty(name: 'body.onresize')}">
<div id="doc3" class="yui-t7">
  <div id="hd">
    <omar:securityClassificationBanner/>
    <img id="logo" src="${createLinkTo(dir: 'images', file: 'OMARLarge.png', absolute)}" alt="OMAR-2.0 Logo"/>
  </div>
  <div id="bd">
    <div class="yui-g">
      <!-- YOUR DATA GOES HERE -->
      <g:layoutBody/>
    </div>
  </div>
  <div id="ft">
    <omar:securityClassificationBanner/>
  </div>
</div>
</body>
</html>
