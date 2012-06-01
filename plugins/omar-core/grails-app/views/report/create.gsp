<%@ page import="org.ossim.omar.core.Report" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="generatedViews"/>
  <g:set var="entityName" value="${message(code: 'report.label', default: 'Report')}"/>
  <title><g:message code="default.create.label" args="[entityName]"/></title>
</head>
<body>
<content tag="content">
    <omar:logout/>
    <div class="nav">
      <ul>
          <li class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
          <li class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]"/></g:link></li>

      </ul>
  </div>
  <div class="body">
    <h1><g:message code="default.create.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${reportInstance}">
      <div class="errors">
        <g:renderErrors bean="${reportInstance}" as="list"/>
      </div>
    </g:hasErrors>
    <g:form action="save">
      <div class="dialog">
        <table>
          <tbody>

          <tr class="prop">
            <td valign="top" class="name">
              <label for="name"><g:message code="report.name.label" default="Name"/></label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: reportInstance, field: 'name', 'errors')}">
              <g:textField name="name" value="${reportInstance?.name}" readOnly="true"/>
            </td>
          </tr>

          <tr class="prop">
            <td valign="top" class="name">
              <label for="email"><g:message code="report.email.label" default="Email"/></label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: reportInstance, field: 'email', 'errors')}">
              <g:textField name="email" value="${reportInstance?.email}" readOnly="true"/>
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
              <label for="report"><g:message code="report.report.label" default="Report"/></label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: reportInstance, field: 'report', 'errors')}">
              <g:textArea name="report" cols="40" rows="5" value="${reportInstance?.report}"/>
            </td>
          </tr>

          <%--
          <tr class="prop">
            <td valign="top" class="name">
              <label for="status"><g:message code="report.status.label" default="Status"/></label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: reportInstance, field: 'status', 'errors')}">
              <g:textField name="status" value="${reportInstance?.status}"/>
            </td>
          </tr>
          --%>

          <g:hiddenField name="status" value="NEW"/>

          <sec:ifAllGranted roles="ROLE_ADMIN">

            <tr class="prop">
              <td valign="top" class="name">
                <label for="comment"><g:message code="report.comment.label" default="Comment"/></label>
              </td>
              <td valign="top" class="value ${hasErrors(bean: reportInstance, field: 'comment', 'errors')}">
                <g:textField name="comment" value="${reportInstance?.comment}"/>
              </td>
            </tr>
          </sec:ifAllGranted>
          </tbody>
        </table>
      </div>
      <div class="buttons">
        <span class="button"><g:submitButton name="create" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}"/></span>
      </div>
    </g:form>
  </div>
</content>
</body>
</html>
