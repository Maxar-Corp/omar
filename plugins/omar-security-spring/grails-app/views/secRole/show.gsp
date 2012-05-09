<%@ page import="org.ossim.omar.security.SecRole" %>
<!doctype html>
<html>
<head>
  <meta name="layout" content="generatedViews">
  <g:set var="entityName" value="${message( code: 'secRole.label', default: 'SecRole' )}"/>
  <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>

<body>
<content tag="content">
  <a href="#show-secRole" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                                default="Skip to content&hellip;"/></a>

  <div class="nav" role="navigation">
    <ul>
      <li><a class="home" href="${createLink( uri: '/' )}"><g:message code="default.home.label"/></a></li>
      <li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]"/></g:link></li>
      <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]"/></g:link></li>
    </ul>
  </div>

  <div id="show-secRole" class="content scaffold-show" role="main">
    <h1><g:message code="default.show.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
      <div class="message" role="status">${flash.message}</div>
    </g:if>
    <ol class="property-list secRole">

      <g:if test="${secRoleInstance?.authority}">
        <li class="fieldcontain">
          <span id="authority-label" class="property-label"><g:message code="secRole.authority.label"
                                                                       default="Authority"/></span>

          <span class="property-value" aria-labelledby="authority-label"><g:fieldValue bean="${secRoleInstance}"
                                                                                       field="authority"/></span>

        </li>
      </g:if>

      <g:if test="${secRoleInstance?.description}">
        <li class="fieldcontain">
          <span id="description-label" class="property-label"><g:message code="secRole.description.label"
                                                                         default="Description"/></span>

          <span class="property-value" aria-labelledby="description-label"><g:fieldValue bean="${secRoleInstance}"
                                                                                         field="description"/></span>

        </li>
      </g:if>

    </ol>
    <g:form>
      <fieldset class="buttons">
        <g:hiddenField name="id" value="${secRoleInstance?.id}"/>
        <g:link class="edit" action="edit" id="${secRoleInstance?.id}"><g:message code="default.button.edit.label"
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
