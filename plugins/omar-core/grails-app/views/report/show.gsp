<%@ page import="org.ossim.omar.Report" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="generatedViews"/>
  <g:set var="entityName" value="${message(code: 'report.label', default: 'Report')}"/>
  <title>OMAR: Show Report ${fieldValue(bean: reportInstance, field: "id")}</title>
</head>
<body>
<content tag="content">
  <div class="nav">
    <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">OMAR™ Home</a></span>
    <g:ifAllGranted role="ROLE_ADMIN">
      <span class="menuButton"><g:link class="list" action="list">Report List</g:link></span>
    </g:ifAllGranted>
    <span class="menuButton"><g:link class="create" action="create">Create Report</g:link></span>
  </div>
  <div class="body">
    <h1>OMAR: Show Report ${fieldValue(bean: reportInstance, field: "id")}</h1>
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
    </g:if>
    <div class="dialog">
      <table>
        <tbody>
        <tr class="prop">
          <td valign="top" class="name"><g:message code="report.id.label" default="Id"/></td>
          <td valign="top" class="value">${fieldValue(bean: reportInstance, field: "id")}</td>
        </tr>
        <tr class="prop">
          <td valign="top" class="name"><g:message code="report.name.label" default="Name"/></td>
          <td valign="top" class="value">${fieldValue(bean: reportInstance, field: "name")}</td>
        </tr>
        <tr class="prop">
          <td valign="top" class="name"><g:message code="report.email.label" default="Email"/></td>
          <td valign="top" class="value">${fieldValue(bean: reportInstance, field: "email")}</td>
        </tr>
        <tr class="prop">
          <td valign="top" class="name"><g:message code="report.phone.label" default="Phone"/></td>
          <td valign="top" class="value">${fieldValue(bean: reportInstance, field: "phone")}</td>
        </tr>
        <tr class="prop">
          <td valign="top" class="name"><g:message code="report.createdDate.label" default="Created Date"/></td>
          <td valign="top" class="value"><g:formatDate date="${reportInstance?.createdDate}" /></td>
        </tr>
        <tr class="prop">
          <td valign="top" class="name"><g:message code="report.report.label" default="Report"/></td>
          <td valign="top" class="value">${fieldValue(bean: reportInstance, field: "report")}</td>
        </tr>
        </tbody>
      </table>
    </div>
    <div class="buttons">
      <g:form>
        <g:hiddenField name="id" value="${reportInstance?.id}" />
        <span class="button"><g:actionSubmit class="edit" action="edit" value="${message(code: 'default.button.edit.label', default: 'Edit')}"/></span>
        <span class="button"><g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/></span>
      </g:form>
    </div>
  </div>
</content>
</body>
</html>