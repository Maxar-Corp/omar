<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: Oct 28, 2009
  Time: 8:24:35 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>DataManager - addRaster</title>
  <meta name="layout" content="main"/>
</head>
<body>
<div class="nav">
  <span class="menuButton"><a class="home" href="${resource(dir: '')}">Home</a></span>
</div>

<div class="body">
  <g:form action="addRaster" method="POST">
    <div class="dialog">
      <label labelFor="">Filename:</label>
      <g:textField name="filename"/>
      <span class="buttons">
      </span>
    </div>
    <div class="buttons">
      <span class="button">
        <span class="button"><g:actionSubmit class="save" value="Add Raster"/></span>
      </span>
    </div>
  </g:form>
</div>
</body>
</html>