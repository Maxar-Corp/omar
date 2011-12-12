<%@ page import="org.ossim.omar.Requestmap" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="generatedViews"/>
  <g:set var="entityName" value="${message(code: 'requestmap.label', default: 'Requestmap')}"/>
  <title><g:message code="default.create.label" args="[entityName]"/></title>
</head>

<body>
<content tag="content">
  <div class="nav">
    <span class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a>
    </span>
    <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label"
                                                                           args="[entityName]"/></g:link></span>
  </div>

  <div class="body">
    <h1><g:message code="default.create.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${requestmapInstance}">
      <div class="errors">
        <g:renderErrors bean="${requestmapInstance}" as="list"/>
      </div>
    </g:hasErrors>
    <g:form action="save">
      <div class="dialog">
        <table>
          <tbody>

          <tr class="prop">
            <td valign="top" class="name">
              <label for="url"><g:message code="requestmap.url.label" default="Url"/></label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: requestmapInstance, field: 'url', 'errors')}">
              <g:textField name="url" value="${requestmapInstance?.url}"/>
            </td>
          </tr>

          <tr class="prop">
            <td valign="top" class="name">
              <label for="configAttribute"><g:message code="requestmap.configAttribute.label"
                                                      default="Config Attribute"/></label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: requestmapInstance, field: 'configAttribute', 'errors')}">
              <g:textField name="configAttribute" value="${requestmapInstance?.configAttribute}"/>
            </td>
          </tr>

          </tbody>
        </table>
      </div>

      <div class="buttons">
        <span class="button"><g:submitButton name="create" class="save"
                                             value="${message(code: 'default.button.create.label', default: 'Create')}"/></span>
      </div>
    </g:form>
  </div>
</content>
</body>
</html>
