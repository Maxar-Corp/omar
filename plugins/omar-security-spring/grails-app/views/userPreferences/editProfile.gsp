<%@ page import="org.ossim.omar.security.SecUser" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="generatedViews"/>
  <g:set var="entityName" value="${message(code: 'secUser.label', default: 'SecUser')}"/>
  <title><g:message code="default.edit.label" args="[entityName]"/></title>
</head>
<body>
<content tag="content">
    <omar:logout/>
  <div class="nav">
    <span class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></span>
  </div>
  <div class="body">
    <h1><g:message code="default.edit.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${secUserInstance}">
      <div class="errors">
        <g:renderErrors bean="${secUserInstance}" as="list"/>
      </div>
    </g:hasErrors>
    <g:form method="post">
      <g:hiddenField name="id" value="${secUserInstance?.id}"/>
      <g:hiddenField name="version" value="${secUserInstance?.version}"/>
      <div class="dialog">
        <table>
          <tbody>

          <tr class="prop">
            <td valign="top" class="name">
              <label for="userRealName"><g:message code="secUser.userRealName.label" default="User Real Name"/></label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: secUserInstance, field: 'userRealName', 'errors')}">
              <g:textField name="userRealName" value="${secUserInstance?.userRealName}"/>
            </td>
          </tr>

          <tr class="prop">
            <td valign="top" class="name">
              <label for="organization"><g:message code="secUser.organization.label" default="Organization"/></label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: secUserInstance, field: 'organization', 'errors')}">
              <g:textField name="organization" value="${secUserInstance?.organization}"/>
            </td>
          </tr>

          <tr class="prop">
            <td valign="top" class="name">
              <label for="phoneNumber"><g:message code="secUser.phoneNumber.label" default="Phone Number"/></label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: secUserInstance, field: 'phoneNumber', 'errors')}">
              <g:textField name="phoneNumber" value="${secUserInstance?.phoneNumber}"/>
            </td>
          </tr>

          <tr class="prop">
            <td valign="top" class="name">
              <label for="email"><g:message code="secUser.email.label" default="Email"/></label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: secUserInstance, field: 'email', 'errors')}">
              <g:textField name="email" value="${secUserInstance?.email}"/>
            </td>
          </tr>

          </tbody>
        </table>
      </div>
      <div class="buttons">
        <span class="button"><g:actionSubmit class="save" action="updateProfile" value="${message(code: 'default.button.update.label', default: 'Update')}"/></span>
      </div>
    </g:form>
  </div>
</content>
</body>
</html>
