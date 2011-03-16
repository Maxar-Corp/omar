<%@ page import="org.ossim.omar.Report" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="generatedViews"/>
  <g:set var="entityName" value="${message(code: 'report.label', default: 'Report')}"/>
  <title>OMAR: Edit Report ${fieldValue(bean: reportInstance, field: "id")}</title>
</head>
<body>
<content tag="content">
  <div class="nav">
    <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">OMAR™ Home</a></span>
<%--    <sec:ifAllGranted roles="ROLE_ADMIN">   --%>
      <span class="menuButton"><g:link class="list" action="list">Report List</g:link></span>
<%--    </sec:ifAllGranted>   --%>
    <span class="menuButton"><g:link class="create" action="create">Create Report</g:link></span>
  </div>
  <div class="body">
    <h1>OMAR: Edit Report ${fieldValue(bean: reportInstance, field: "id")}</h1>
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${reportInstance}">
      <div class="errors">
        <g:renderErrors bean="${reportInstance}" as="list"/>
      </div>
    </g:hasErrors>
    <g:form method="post">
      <g:hiddenField name="id" value="${reportInstance?.id}"/>
      <g:hiddenField name="version" value="${reportInstance?.version}"/>
      <div class="dialog">
        <table>
          <tbody>
          <tr class="prop">
            <td valign="top" class="name">
              <label for="name"><g:message code="report.name.label" default="Name"/></label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: reportInstance, field: 'name', 'errors')}">
              <g:textField name="name" value="${reportInstance?.name}"/>
            </td>
          </tr>
          <tr class="prop">
            <td valign="top" class="name">
              <label for="email"><g:message code="report.email.label" default="Email"/></label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: reportInstance, field: 'email', 'errors')}">
              <g:textField name="email" value="${reportInstance?.email}"/>
            </td>
          </tr>
          <tr class="prop">
            <td valign="top" class="name">
              <label for="phone"><g:message code="report.phone.label" default="Phone"/></label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: reportInstance, field: 'phone', 'errors')}">
              <g:textField name="phone" value="${reportInstance?.phone}"/>
            </td>
          </tr>
          <tr class="prop">
            <td valign="top" class="name">
              <label for="createdDate"><g:message code="report.createdDate.label" default="Created Date"/></label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: reportInstance, field: 'createdDate', 'errors')}">
              <g:datePicker name="createdDate" precision="day" value="${reportInstance?.createdDate}"/>
            </td>
          </tr>
          <tr class="prop">
            <td valign="top" class="name">
              <label for="report"><g:message code="report.report.label" default="Report"/></label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: reportInstance, field: 'report', 'errors')}">
              <g:textArea name="report" cols="40" rows="5" value="${reportInstance?.report}"/>
            </td>
          </tr>
          <sec:ifAllGranted roles="ROLE_ADMIN">
              <tr class="prop">
                <td valign="top" class="name">
                  <label for="status"><g:message code="report.status.label" default="Status"/></label>
                </td>
                <td valign="top" class="value ${hasErrors(bean: reportInstance, field: 'status', 'errors')}">
                  <g:textField name="status" value="${reportInstance?.status}"/>
                </td>
              </tr>
              <tr class="prop">
                 <td valign="top" class="name">
                   <label for="comment"><g:message code="report.comments.label" default="Comment"/></label>
                 </td>
                 <td valign="top" class="value ${hasErrors(bean: reportInstance, field: 'comment', 'errors')}">
                   <g:textArea name="comment" cols="40" rows="5" value="${reportInstance?.comment}"/>
                 </td>
               </tr>
          </sec:ifAllGranted>
          <sec:ifNotGranted roles="ROLE_ADMIN">
              <tr class="prop">
                  <td valign="top" class="name"><g:message code="report.id.label" default="Status"/></td>
                  <td valign="top" class="value">${fieldValue(bean: reportInstance, field: "status")}</td>
              </tr>
              <tr class="prop">
                  <td valign="top" class="name"><g:message code="report.id.label" default="Comment"/></td>
                  <td valign="top" class="value">${fieldValue(bean: reportInstance, field: "comment")}</td>
              </tr>
          </sec:ifNotGranted>
    </tbody>
        </table>
      </div>
      <div class="buttons">
        <span class="button"><g:actionSubmit class="save" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}"/></span>
        <span class="button"><g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/></span>
      </div>
    </g:form>
  </div>
</content>
</body>
</html>
