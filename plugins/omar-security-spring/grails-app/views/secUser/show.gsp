<%@ page import="org.ossim.omar.security.SecUser" %>
<!doctype html>
<html>
<head>
  <meta name="layout" content="mainG2">
  <g:set var="entityName" value="${message( code: 'secUser.label', default: 'SecUser' )}"/>
  <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>

<body>

<content tag="content">
  <a href="#show-secUser" class="skip" tabindex="-1">
    <g:message code="default.link.skip.label" default="Skip to content&hellip;"/>
  </a>

  <div class="nav" role="navigation">
    <ul>
      <li><a class="home" href="${createLink( uri: '/' )}"><g:message code="default.home.label"/></a></li>
      <li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]"/></g:link></li>
      <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]"/></g:link></li>
    </ul>
  </div>

  <div id="show-secUser" class="content scaffold-show" role="main">
    <h1><g:message code="default.show.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
      <div class="message" role="status">${flash.message}</div>
    </g:if>
    <ol class="property-list secUser">

      <g:if test="${secUserInstance?.username}">
        <li class="fieldcontain">
          <span id="username-label" class="property-label"><g:message code="secUser.username.label"
                                                                      default="Username"/></span>

          <span class="property-value" aria-labelledby="username-label"><g:fieldValue bean="${secUserInstance}"
                                                                                      field="username"/></span>

        </li>
      </g:if>
<%--
      <g:if test="${secUserInstance?.password}">
        <li class="fieldcontain">
          <span id="password-label" class="property-label"><g:message code="secUser.password.label"
                                                                      default="Password"/></span>

          <span class="property-value" aria-labelledby="password-label"><g:fieldValue bean="${secUserInstance}"
                                                                                      field="password"/></span>

        </li>
      </g:if>
--%>
      <g:if test="${secUserInstance?.userRealName}">
        <li class="fieldcontain">
          <span id="userRealName-label" class="property-label"><g:message code="secUser.userRealName.label"
                                                                          default="User Real Name"/></span>

          <span class="property-value" aria-labelledby="userRealName-label"><g:fieldValue bean="${secUserInstance}"
                                                                                          field="userRealName"/></span>

        </li>
      </g:if>

      <g:if test="${secUserInstance?.email}">
        <li class="fieldcontain">
          <span id="email-label" class="property-label"><g:message code="secUser.email.label" default="Email"/></span>

          <span class="property-value" aria-labelledby="email-label"><g:fieldValue bean="${secUserInstance}"
                                                                                   field="email"/></span>

        </li>
      </g:if>

      <g:if test="${secUserInstance?.organization}">
        <li class="fieldcontain">
          <span id="organization-label" class="property-label"><g:message code="secUser.organization.label"
                                                                          default="Organization"/></span>

          <span class="property-value" aria-labelledby="organization-label"><g:fieldValue bean="${secUserInstance}"
                                                                                          field="organization"/></span>

        </li>
      </g:if>

      <g:if test="${secUserInstance?.phoneNumber}">
        <li class="fieldcontain">
          <span id="phoneNumber-label" class="property-label"><g:message code="secUser.phoneNumber.label"
                                                                         default="Phone Number"/></span>

          <span class="property-value" aria-labelledby="phoneNumber-label"><g:fieldValue bean="${secUserInstance}"
                                                                                         field="phoneNumber"/></span>

        </li>
      </g:if>

      <g:if test="${secUserInstance?.accountExpired}">
        <li class="fieldcontain">
          <span id="accountExpired-label" class="property-label"><g:message code="secUser.accountExpired.label"
                                                                            default="Account Expired"/></span>

          <span class="property-value" aria-labelledby="accountExpired-label"><g:formatBoolean
              boolean="${secUserInstance?.accountExpired}"/></span>

        </li>
      </g:if>

      <g:if test="${secUserInstance?.accountLocked}">
        <li class="fieldcontain">
          <span id="accountLocked-label" class="property-label"><g:message code="secUser.accountLocked.label"
                                                                           default="Account Locked"/></span>

          <span class="property-value" aria-labelledby="accountLocked-label"><g:formatBoolean
              boolean="${secUserInstance?.accountLocked}"/></span>

        </li>
      </g:if>

      <g:if test="${secUserInstance?.enabled}">
        <li class="fieldcontain">
          <span id="enabled-label" class="property-label"><g:message code="secUser.enabled.label"
                                                                     default="Enabled"/></span>

          <span class="property-value" aria-labelledby="enabled-label"><g:formatBoolean
              boolean="${secUserInstance?.enabled}"/></span>

        </li>
      </g:if>

      <g:if test="${secUserInstance?.passwordExpired}">
        <li class="fieldcontain">
          <span id="passwordExpired-label" class="property-label"><g:message code="secUser.passwordExpired.label"
                                                                             default="Password Expired"/></span>

          <span class="property-value" aria-labelledby="passwordExpired-label"><g:formatBoolean
              boolean="${secUserInstance?.passwordExpired}"/></span>

        </li>
      </g:if>

    </ol>
    <g:form>
      <fieldset class="buttons">
        <g:hiddenField name="id" value="${secUserInstance?.id}"/>
        <g:link class="edit" action="edit" id="${secUserInstance?.id}"><g:message code="default.button.edit.label"
                                                                                  default="Edit"/></g:link>
        <g:actionSubmit class="delete" action="delete"
                        value="${message( code: 'default.button.delete.label', default: 'Delete' )}"
                        onclick="return confirm('${message( code: 'default.button.delete.confirm.message', default: 'Are you sure?' )}');"/>
      </fieldset>
    </g:form>
  </div>
</content>
</body>
</html>
