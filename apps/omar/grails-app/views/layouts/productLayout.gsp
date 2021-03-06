<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: 1/30/12
  Time: 10:16 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title><g:layoutTitle default="Grails"/></title>
  <piwik:trackPageview />
  <g:layoutHead/>
  <r:layoutResources/>
</head>

<body class="${pageProperty(name: 'body.class')}">

<div id="top1">
  <omar:securityClassificationBanner />
    <omar:logout/>
    <g:pageProperty name="page.top1"/>
</div>

<div id="bottom1">
  <g:pageProperty name="page.bottom1"/>
  <omar:securityClassificationBanner />
</div>

<div id="content">
  <g:pageProperty name="page.content"/>
</div>

<g:layoutBody/>
<r:layoutResources/>
</body>
</html>
