<%@ page import="org.ossim.omar.Report" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <meta name="layout" content="main" />
  <g:set var="entityName" value="${message(code: 'report.label', default: 'Report')}" />
  <title>OMAR: Create Report</title>
</head>
<body>
<content tag="content">
  <div class="nav">
    <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">OMAR™ Home</a></span>
    <g:ifAllGranted role="ROLE_ADMIN">
      <span class="menuButton"><g:link class="list" action="list">Report List</g:link></span>
    </g:ifAllGranted>
  </div>
  <div class="body">
    <h1>Create Report</h1>
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${reportInstance}">
      <div class="errors">
        <g:renderErrors bean="${reportInstance}" as="list" />
      </div>
    </g:hasErrors>
    <g:form action="save" method="post">
      <div class="dialog">
        <table>
          <tbody>
          <tr class="prop">
            <td valign="top" class="name">
              <label for="name"><g:message code="report.name.label" default="Name" /></label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: reportInstance, field: 'name', 'errors')}">
              <g:textField name="name" value="${reportInstance?.name}" />
            </td>
          </tr>
          <tr class="prop">
            <td valign="top" class="name">
              <label for="email"><g:message code="report.email.label" default="Email" /></label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: reportInstance, field: 'email', 'errors')}">
              <g:textField name="email" value="${reportInstance?.email}" />
            </td>
          </tr>
          <tr class="prop">
            <td valign="top" class="name">
              <label for="createdDate"><g:message code="report.createdDate.label" default="Created Date" /></label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: reportInstance, field: 'createdDate', 'errors')}">
              <g:datePicker name="createdDate" precision="day" value="${reportInstance?.createdDate}"  />
            </td>
          </tr>
          <tr class="prop">
            <td valign="top" class="name">
              <label for="report"><g:message code="report.report.label" default="Report" /></label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: reportInstance, field: 'report', 'errors')}">
              <g:textArea name="report" cols="40" rows="5" value="${reportInstance?.report}" />
            </td>
          </tr>
          </tbody>
        </table>
      </div>
      <div class="buttons">
        <span class="button"><g:submitButton name="create" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}" /></span>
      </div>
    </g:form>
  </div>
</content>
</body>
</html>