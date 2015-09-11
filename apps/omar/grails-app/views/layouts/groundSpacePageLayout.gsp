<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: 1/30/12
  Time: 10:16 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
  <title><g:layoutTitle default="Grails"/></title>
  <g:layoutHead/>
  <piwik:trackPageview />

</head>

<body class="${pageProperty(name: 'body.class')}">

<div id="top1">
  <omar:securityClassificationBanner/>
  <omar:logout/>
  <g:pageProperty name="page.top1"/>
</div>

<div id="bottom1">
  <g:pageProperty name="page.bottom1"/>
  <omar:securityClassificationBanner/>
</div>

<div id="right1">
  <g:pageProperty name="page.right1"/>
</div>

<div id="left1">
  <g:pageProperty name="page.left1"/>
</div>

<div id="top2">
  <g:pageProperty name="page.top2"/>
</div>

<div id="bottom2">
  <g:pageProperty name="page.bottom2"/>
</div>

<div id="center2">
  <g:pageProperty name="page.center2"/>
</div>

<g:layoutBody/>
</body>
</html>
