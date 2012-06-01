<%@ page import="org.ossim.omar.core.Report" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="generatedViews"/>
  <g:set var="entityName" value="${message(code: 'report.label', default: 'Report')}"/>
  <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>
<body>
<content tag="content">
    <omar:logout/>
    <div class="nav">
      <ul>
          <li class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
          <li class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]"/></g:link></li>
          <li class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]"/></g:link></li>

      </ul>
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
          <td valign="top" class="name"><g:message code="report.dateCreated.label" default="Date Created"/></td>

          <td valign="top" class="value"><g:formatDate date="${reportInstance?.dateCreated}"/></td>

        </tr>

        <tr class="prop">
          <td valign="top" class="name"><g:message code="report.lastUpdated.label" default="Last Updated"/></td>

          <td valign="top" class="value"><g:formatDate date="${reportInstance?.lastUpdated}"/></td>

        </tr>


        <tr class="prop">
          <td valign="top" class="name"><g:message code="report.report.label" default="Report"/></td>

          <td valign="top" class="value">${fieldValue(bean: reportInstance, field: "report")}</td>

        </tr>

        <tr class="prop">
          <td valign="top" class="name"><g:message code="report.status.label" default="Status"/></td>

          <td valign="top" class="value">${fieldValue(bean: reportInstance, field: "status")}</td>

        </tr>

        <tr class="prop">
          <td valign="top" class="name"><g:message code="report.comment.label" default="Comment"/></td>

          <td valign="top" class="value">${fieldValue(bean: reportInstance, field: "comment")}</td>

        </tr>

        </tbody>
      </table>
    </div>
    <div class="buttons">
      <g:form>
        <g:hiddenField name="id" value="${reportInstance?.id}"/>
        <span class="button"><g:actionSubmit class="edit" action="edit" value="${message(code: 'default.button.edit.label', default: 'Edit')}"/></span>
        <sec:ifAllGranted roles="ROLE_ADMIN">
          <span class="button"><g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/></span>
        </sec:ifAllGranted>
      </g:form>
    </div>
  </div>
</content>
</body>
</html>
