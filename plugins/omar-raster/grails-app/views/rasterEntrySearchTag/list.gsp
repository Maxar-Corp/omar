<%@ page import="org.ossim.omar.raster.RasterEntrySearchTag" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="generatedViews"/>
  <g:set var="entityName" value="${message(code: 'rasterEntrySearchTag.label', default: 'RasterEntrySearchTag')}"/>
  <title>OMAR: Raster Search Tag List</title>
</head>
<body>
<content tag="content">
  <div class="nav">
      <ul>
          <li class="menuButton"><a class="home" href="${createLink(uri: '/')}">OMAR™ Home</a></li>
          <li class="menuButton"><g:link class="create" action="create">Create Raster Search Tag</g:link></li>
      </ul>
  </div>
  <div class="body">
    <h1>OMAR: Raster Search Tag List</h1>
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
    </g:if>
    <div class="list">
      <table>
        <thead>
        <tr>
          <g:sortableColumn property="id" title="${message(code: 'rasterEntrySearchTag.id.label', default: 'Id')}"/>
          <g:sortableColumn property="name" title="${message(code: 'rasterEntrySearchTag.name.label', default: 'Name')}"/>
          <g:sortableColumn property="description" title="${message(code: 'rasterEntrySearchTag.description.label', default: 'Description')}"/>
        </tr>
        </thead>
        <tbody>
        <g:each in="${rasterEntrySearchTagInstanceList}" status="i" var="rasterEntrySearchTagInstance">
          <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
            <td><g:link action="show" id="${rasterEntrySearchTagInstance.id}">${fieldValue(bean: rasterEntrySearchTagInstance, field: "id")}</g:link></td>
            <td>${fieldValue(bean: rasterEntrySearchTagInstance, field: "name")}</td>
            <td>${fieldValue(bean: rasterEntrySearchTagInstance, field: "description")}</td>
          </tr>
        </g:each>
        </tbody>
      </table>
    </div>
    <div class="pagination">
      <g:paginate total="${rasterEntrySearchTagInstanceTotal}"/>
    </div>
  </div>
</content>
</body>
</html>