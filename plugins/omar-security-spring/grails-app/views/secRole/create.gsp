<%@ page import="org.ossim.omar.security.SecRole" %>
<!doctype html>
<html>
<head>
  <meta name="layout" content="generatedViews">
  <g:set var="entityName" value="${message( code: 'secRole.label', default: 'SecRole' )}"/>
  <title><g:message code="default.create.label" args="[entityName]"/></title>
</head>

<body>
<content tag="content">
  <a href="#create-secRole" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                                  default="Skip to content&hellip;"/></a>

  <div class="nav" role="navigation">
    <ul>
      <li><a class="home" href="${createLink( uri: '/' )}"><g:message code="default.home.label"/></a></li>
      <li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]"/></g:link></li>
    </ul>
  </div>

  <div id="create-secRole" class="content scaffold-create" role="main">
    <h1><g:message code="default.create.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
      <div class="message" role="status">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${secRoleInstance}">
      <ul class="errors" role="alert">
      <g:eachError bean="${secRoleInstance}" var="error">
        <li<g:if
          test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message
          error="${error}"/></li>
      </g:eachError>
      </ul>
    </g:hasErrors>
    <g:form action="save">
      <fieldset class="form">
        <g:render template="form"/>
      </fieldset>
      <br/>
      <fieldset class="buttons">
        <g:submitButton name="create" class="save"
                        value="${message( code: 'default.button.create.label', default: 'Create' )}"/>
      </fieldset>
    </g:form>
  </div>
</content>
</body>
</html>
