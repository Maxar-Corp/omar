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
  <title>DataManager - ${opType}</title>
  <meta name="layout" content="singleColumn"/>
</head>
<body>
<content tag="top">
  <div id="hd">
    <img id="logo" src="${resource(contextPath: "/", dir: 'images', file: 'OMARLarge.png')}" alt="OMAR-2.0 Logo"/>
  </div>
</content>

<content tag="center">
  <div class="nav">
    <span class="menuButton"><g:link class="home" uri="/">Home</g:link></span>
  </div>

  <div class="body">
    <g:form action="${opType}" method="POST">
      <div class="dialog">
        <label labelFor="">Filename:</label>
        <g:textField name="filename"/>
        <span class="buttons">
        </span>
      </div>
      <div class="buttons">
        <span class="button">
          <span class="button">
            <g:if test="${op == 'add'}">
              <g:actionSubmit class="save" value="Add ${type}"/>
            </g:if>
            <g:elseif test="${op == 'remove'}">
              <g:actionSubmit class="delete" value="Remove ${type}"/>
            </g:elseif>
            <g:elseif test="development">
              <g:actionSubmit class="edit" value="Update ${type}"/>
            </g:elseif>
          </span>
      </div>
    </g:form>
  </div>
</content>
</body>
</html>
