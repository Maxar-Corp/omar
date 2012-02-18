<%@ page import="org.ossim.omar.security.SecRole" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="generatedViews"/>
  <g:set var="entityName" value="${message(code: 'secRole.label', default: 'SecRole')}"/>
  <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>
<body>
<content tag="content">

  <div class="nav">
    <span class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></span>
    <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]"/></g:link></span>
    <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]"/></g:link></span>
  </div>
  <div class="body">
    <h1><g:message code="default.show.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
    </g:if>
    <div class="dialog">
      <table>
        <tbody>

        <tr class="prop">
          <td valign="top" class="name"><g:message code="secRole.id.label" default="Id"/></td>

          <td valign="top" class="value">${fieldValue(bean: secRoleInstance, field: "id")}</td>

        </tr>

        <tr class="prop">
          <td valign="top" class="name"><g:message code="secRole.authority.label" default="Authority"/></td>

          <td valign="top" class="value">${fieldValue(bean: secRoleInstance, field: "authority")}</td>

        </tr>

        <tr class="prop">
          <td valign="top" class="name"><g:message code="secRole.description.label" default="Description"/></td>

          <td valign="top" class="value">${fieldValue(bean: secRoleInstance, field: "description")}</td>

        </tr>

        </tbody>
      </table>
    </div>
    <div class="buttons">
      <g:form>
        <g:hiddenField name="id" value="${secRoleInstance?.id}"/>
        <span class="button"><g:actionSubmit class="edit" action="edit" value="${message(code: 'default.button.edit.label', default: 'Edit')}"/></span>
        <span class="button"><g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/></span>
      </g:form>
    </div>
  </div>
</content>
</body>
</html>
