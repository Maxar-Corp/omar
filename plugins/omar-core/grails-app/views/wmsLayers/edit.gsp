<%@ page import="org.ossim.omar.core.WmsLayers" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="generatedViews"/>
  <g:set var="entityName" value="${message(code: 'wmsLayers.label', default: 'WmsLayers')}"/>
  <title><g:message code="default.edit.label" args="[entityName]"/></title>
</head>
<body>
<content tag="content">

  <div class="nav">
      <ul>
          <li class="menuButton"><g:link class="home" uri="/">Home</g:link></li>
          <li class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]"/></g:link></li>
          <li class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]"/></g:link></li>
      </ul>
  </div>
  <div class="body">
    <h1><g:message code="default.edit.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${wmsLayersInstance}">
      <div class="errors">
        <g:renderErrors bean="${wmsLayersInstance}" as="list"/>
      </div>
    </g:hasErrors>
    <g:form method="post">
      <g:hiddenField name="id" value="${wmsLayersInstance?.id}"/>
      <g:hiddenField name="version" value="${wmsLayersInstance?.version}"/>
      <div class="dialog">
        <table>
          <tbody>

          <tr class="prop">
            <td valign="top" class="name">
              <label for="name"><g:message code="wmsLayers.name.label" default="Name"/></label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: wmsLayersInstance, field: 'name', 'errors')}">
              <g:textField name="name" value="${wmsLayersInstance?.name}"/>
            </td>
          </tr>

          <tr class="prop">
            <td valign="top" class="name">
              <label for="url"><g:message code="wmsLayers.url.label" default="Url"/></label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: wmsLayersInstance, field: 'url', 'errors')}">
              <g:textField name="url" value="${wmsLayersInstance?.url}"/>
            </td>
          </tr>

          <tr class="prop">
            <td valign="top" class="name">
              <label for="params"><g:message code="wmsLayers.params.label" default="Params"/></label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: wmsLayersInstance, field: 'params', 'errors')}">
              <g:textField name="params" value="${wmsLayersInstance?.params}"/>
            </td>
          </tr>

          <tr class="prop">
            <td valign="top" class="name">
              <label for="options"><g:message code="wmsLayers.options.label" default="Options"/></label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: wmsLayersInstance, field: 'options', 'errors')}">
              <g:textField name="options" value="${wmsLayersInstance?.options}"/> 
            </td>
          </tr>

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
