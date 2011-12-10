<%@ page import="org.ossim.omar.SecUser" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="generatedViews"/>
  <g:set var="entityName" value="${message(code: 'secUser.label', default: 'SecUser')}"/>
  <title><g:message code="default.list.label" args="[entityName]"/></title>

  <g:javascript contextPath="" src="prototype/prototype.js"/>
  <g:javascript contextPath="" src="application.js"/>

</head>

<body>
<content tag="content">
  <div class="nav">
    <span class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a>
    </span>
    <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label"
                                                                               args="[entityName]"/></g:link></span>
  </div>

  <div class="body">
    <h1><g:message code="default.list.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
    </g:if>
    <filter:dynamic  bean="org.ossim.omar.SecUser" success="userList" params="${[plugin: 'omar-security-spring']}"/>
    <g:render template="list"/>
  </div>
</content>
</body>
</html>
