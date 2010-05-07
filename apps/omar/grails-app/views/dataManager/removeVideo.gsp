<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: Oct 28, 2009
  Time: 8:25:16 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>DataManager - removeVideo</title>
  <meta name="layout" content="main5"/>
</head>
<body>
<div class="nav">
  <span class="menuButton"><g:link class="home" uri="/">Home</g:link></span>
</div>

<div class="body">
  <g:form action="removeVideo" method="POST">
    <div class="dialog">
      <label labelFor="">Filename:</label>
      <g:textField name="filename"/>
    </div>
    <div class="buttons">
      <span class="button">
        <span class="button"><g:submitButton class="delete" name="Remove Video"/></span>
      </span>
    </div>
  </g:form>
</div>
</body>
</html>
