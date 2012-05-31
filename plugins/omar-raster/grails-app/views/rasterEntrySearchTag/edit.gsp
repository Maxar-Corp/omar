<%@ page import="org.ossim.omar.raster.RasterEntrySearchTag" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="generatedViews"/>
  <g:set var="entityName" value="${message(code: 'rasterEntrySearchTag.label', default: 'RasterEntrySearchTag')}"/>
  <title>OMAR: Edit Raster Search Tag ${fieldValue(bean: rasterEntrySearchTagInstance, field: "id")}</title>
</head>
<body>
<content tag="content">
  <div class="nav">
      <ul>
          <li class="menuButton"><a class="home" href="${createLink(uri: '/')}">OMAR™ Home</a></li>
          <li class="menuButton"><g:link class="list" action="list">Raster Search Tag List</g:link></li>
          <li class="menuButton"><g:link class="create" action="create">Create Raster Search Tag</g:link></li>
      </ul>
  </div>
  <div class="body">
    <h1>OMAR: Edit Raster Search Tag ${fieldValue(bean: rasterEntrySearchTagInstance, field: "id")}</h1>
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${rasterEntrySearchTagInstance}">
      <div class="errors">
        <g:renderErrors bean="${rasterEntrySearchTagInstance}" as="list"/>
      </div>
    </g:hasErrors>
    <g:form method="post">
      <g:hiddenField name="id" value="${rasterEntrySearchTagInstance?.id}"/>
      <g:hiddenField name="version" value="${rasterEntrySearchTagInstance?.version}"/>
      <div class="dialog">
        <table>
          <tbody>
          <tr class="prop">
            <td valign="top" class="name">
              <label for="name"><g:message code="rasterEntrySearchTag.name.label" default="Name"/></label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: rasterEntrySearchTagInstance, field: 'name', 'errors')}">
              <g:textField name="name" value="${rasterEntrySearchTagInstance?.name}" />
            </td>
          </tr>
          <tr class="prop">
            <td valign="top" class="name">
              <label for="description"><g:message code="rasterEntrySearchTag.description.label" default="Description"/></label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: rasterEntrySearchTagInstance, field: 'description', 'errors')}">
              <g:textField name="description" value="${rasterEntrySearchTagInstance?.description}"/>
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