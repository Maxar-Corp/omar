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
  <g:layoutHead/>
  <r:layoutResources/>
</head>

<body class="${pageProperty(name: 'body.class')}">

<div id="top1">
  <g:pageProperty name="page.top1"/>
</div>

<div id="bottom1">
  <g:pageProperty name="page.bottom1"/>
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
<r:layoutResources/>
</body>
</html>