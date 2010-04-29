<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: Oct 28, 2009
  Time: 8:25:02 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>DataManager - removeRaster</title>
  <meta name="layout" content="main5"/>
</head>
<body>
<div class="nav">
  <span class="menuButton"><a class="home" href="${resource(dir: '')}">Home</a></span>
</div>

<div class="body">
  <g:form action="removeRaster" method="POST">
    <div class="dialog">
      <label labelFor="">Filename:</label>
      <g:textField name="filename"/>
    </div>
    <div class="buttons">
      <span class="button">
        <span class="button"><g:submitButton class="delete" name="Remove Raster"/></span>
      </span>
    </div>
  </g:form>
</div>
</body>
</html>