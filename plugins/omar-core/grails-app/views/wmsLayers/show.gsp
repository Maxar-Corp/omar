<%@ page import="org.ossim.omar.core.WmsLayers" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="generatedViews"/>
  <g:set var="entityName" value="${message(code: 'wmsLayers.label', default: 'WmsLayers')}"/>
  <title><g:message code="default.show.label" args="[entityName]"/></title>
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
    <h1><g:message code="default.show.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
    </g:if>
    <div class="dialog">
      <table>
        <tbody>

        <tr class="prop">
          <td valign="top" class="name"><g:message code="wmsLayers.id.label" default="Id"/></td>

          <td valign="top" class="value">${fieldValue(bean: wmsLayersInstance, field: "id")}</td>

        </tr>

        <tr class="prop">
          <td valign="top" class="name"><g:message code="wmsLayers.name.label" default="Name"/></td>

          <td valign="top" class="value">${fieldValue(bean: wmsLayersInstance, field: "name")}</td>

        </tr>

        <tr class="prop">
          <td valign="top" class="name"><g:message code="wmsLayers.url.label" default="Url"/></td>

          <td valign="top" class="value">${fieldValue(bean: wmsLayersInstance, field: "url")}</td>

        </tr>

        <tr class="prop">
          <td valign="top" class="name"><g:message code="wmsLayers.params.label" default="Params"/></td>

          <td valign="top" class="value">${fieldValue(bean: wmsLayersInstance, field: "params")}</td>

        </tr>

        <tr class="prop">
          <td valign="top" class="name"><g:message code="wmsLayers.options.label" default="Options"/></td>

          <td valign="top" class="value">${fieldValue(bean: wmsLayersInstance, field: "options")}</td>

        </tr>

        </tbody>
      </table>
    </div>
    <div class="buttons">
      <g:form>
        <g:hiddenField name="id" value="${wmsLayersInstance?.id}"/>
        <span class="button"><g:actionSubmit class="edit" action="edit" value="${message(code: 'default.button.edit.label', default: 'Edit')}"/></span>
        <span class="button"><g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/></span>
      </g:form>
    </div>
  </div>
</content>
</body>
</html>
