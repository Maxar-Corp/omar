<%@ page import="org.ossim.omar.raster.RasterEntry; org.ossim.omar.raster.RasterEntry" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="generatedViews"/>
  <title>OMAR: Edit Raster File ${rasterEntryFile.id}</title>
</head>
<body>
<content tag="content">
  <div class="nav">
      <ul>
          <li class="menuButton"><g:link class="home" uri="/">OMAR™ Home</g:link></li>
          <li class="menuButton"><g:link class="list" action="list">Raster File List</g:link></li>
          <li class="menuButton"><g:link class="create" action="create">Create Raster File</g:link></li>
      </ul>
  </div>
  <div class="body">
    <h1>OMAR: Edit Raster File ${rasterEntryFile.id}</h1>
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${rasterEntryFile}">
      <div class="errors">
        <g:renderErrors bean="${rasterEntryFile}" as="list"/>
      </div>
    </g:hasErrors>
    <g:form method="post">
      <input type="hidden" name="id" value="${rasterEntryFile?.id}"/>
      <div class="dialog">
        <table>
          <tbody>
          <tr class="prop">
            <td valign="top" class="name">
              <label for="name">Name:</label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: rasterEntryFile, field: 'name', 'errors')}">
              <input type="text" id="name" name="name" value="${fieldValue(bean: rasterEntryFile, field: 'name')}"/>
            </td>
          </tr>
          <tr class="prop">
            <td valign="top" class="name">
              <label for="type">Type:</label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: rasterEntryFile, field: 'type', 'errors')}">
              <input type="text" id="type" name="type" value="${fieldValue(bean: rasterEntryFile, field: 'type')}"/>
            </td>
          </tr>
          <tr class="prop">
            <td valign="top" class="name">
              <label for="rasterEntry">Raster Entry:</label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: rasterEntryFile, field: 'rasterEntry', 'errors')}">
              <g:select optionKey="id" from="${org.ossim.omar.raster.RasterEntry.list()}" name="rasterEntry.id" value="${rasterEntryFile?.rasterEntry?.id}"></g:select>
            </td>
          </tr>
          </tbody>
        </table>
      </div>
      <div class="buttons">
        <span class="button"><g:actionSubmit class="save" value="Update"/></span>
        <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
      </div>
    </g:form>
  </div>
</content>
</body>
</html>