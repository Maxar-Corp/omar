<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: Sep 11, 2009
  Time: 9:34:43 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
  <title><g:layoutTitle default="Grails"/></title>
  <link rel="stylesheet" href="${createLinkTo(dir: 'css', file: 'main.css')}"/>
  <link rel="stylesheet" href="${createLinkTo(dir: 'css', file: 'omar-2.0.css')}"/>

  <link rel="stylesheet" type="text/css"
          href="${createLinkTo(dir: 'plugins/richui-0.6/js/yui/reset-fonts-grids',
                  file: 'reset-fonts-grids.css')}"/>
  <link rel="shortcut icon" href="${createLinkTo(dir: 'images', file: 'favicon.ico')}" type="image/x-icon"/>
  <g:layoutHead/>
  <g:javascript library="application"/>
</head>
<body class="${pageProperty(name: 'body.class')}" onload="${pageProperty(name: 'body.onload')}" onresize="${pageProperty(name: 'body.onresize')}">
<div id="doc3" class="yui-t2">
  <div id="hd" role="banner">
    <omar:securityClassificationBanner/>
    <g:pageProperty name="page.banner"/>
  </div>
  <div id="bd" role="main">
    <div id="yui-main">
      <div class="yui-b"><div role="main" class="yui-g">
        <g:pageProperty name="page.main"/>
      </div>
      </div>
    </div>
    <div role="search" class="yui-b">
      <g:pageProperty name="page.search"/>
    </div>
  </div>
  <div id="ft" role="page.contentinfo">
    <g:pageProperty name="contentinfo"/>
    <omar:securityClassificationBanner/>    
  </div>
</div>
</body>
</html>
