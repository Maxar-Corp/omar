<%@ page import="org.ossim.omar.raster.RasterEntrySearchTag" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="generatedViews"/>
  <g:set var="entityName" value="${message(code: 'rasterEntrySearchTag.label', default: 'RasterEntrySearchTag')}"/>
  <title>OMAR: Show Raster Search Tag ${fieldValue(bean: rasterEntrySearchTagInstance, field: "id")}</title>
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
    <h1>OMAR: Show Raster Search Tag ${fieldValue(bean: rasterEntrySearchTagInstance, field: "id")}</h1>
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
    </g:if>
    <div class="dialog">
      <table>
        <tbody>
        <tr class="prop">
          <td valign="top" class="name"><g:message code="rasterEntrySearchTag.id.label" default="Id"/></td>
          <td valign="top" class="value">${fieldValue(bean: rasterEntrySearchTagInstance, field: "id")}</td>
        </tr>
        <tr class="prop">
          <td valign="top" class="name"><g:message code="rasterEntrySearchTag.name.label" default="Name"/></td>
          <td valign="top" class="value">${fieldValue(bean: rasterEntrySearchTagInstance, field: "name")}</td>
        </tr>
        <tr class="prop">
          <td valign="top" class="name"><g:message code="rasterEntrySearchTag.description.label" default="Description"/></td>
          <td valign="top" class="value">${fieldValue(bean: rasterEntrySearchTagInstance, field: "description")}</td>
        </tr>
        </tbody>
      </table>
    </div>
    <div class="buttons">
      <g:form>
        <g:hiddenField name="id" value="${rasterEntrySearchTagInstance?.id}"/>
        <span class="button"><g:actionSubmit class="edit" action="edit" value="${message(code: 'default.button.edit.label', default: 'Edit')}"/></span>
        <span class="button"><g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/></span>
      </g:form>
    </div>
  </div>
</content>
</body>
</html>