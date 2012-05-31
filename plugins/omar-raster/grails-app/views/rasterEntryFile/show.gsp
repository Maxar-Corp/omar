<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="generatedViews"/>
  <title>OMAR: Show Raster File ${rasterEntryFile.id}</title>
</head>
<body>
<content tag="content">
  <div class="nav">
      <ul>
          <li class="menuButton"><g:link class="home" uri="/">OMAR™ Home</g:link></li>
          <li class="menuButton"><g:link class="list" action="list">Raster File List</g:link></li>
          <sec:ifAllGranted roles="ROLE_ADMIN">
              <li class="menuButton"><g:link class="create" action="create">Create Raster File</g:link></li>
          </sec:ifAllGranted>
      </ul>
  </div>
  <div class="body">
    <h1>OMAR: Show Raster File ${rasterEntryFile.id}</h1>
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
    </g:if>
    <div class="dialog">
      <table>
        <tbody>
        <tr class="prop">
          <td valign="top" class="name">Id:</td>
          <td valign="top" class="value">${rasterEntryFile.id}</td>
        </tr>
        <tr class="prop">
          <td valign="top" class="name">Name:</td>
          <td valign="top" class="value">${rasterEntryFile.name}</td>
        </tr>
        <tr class="prop">
          <td valign="top" class="name">Type:</td>
          <td valign="top" class="value">${rasterEntryFile.type}</td>
        </tr>
        <tr class="prop">
          <td valign="top" class="name">Raster Entry:</td>
          <td valign="top" class="value"><g:link controller="rasterEntry" action="show" id="${rasterEntryFile?.rasterEntry?.id}">${rasterEntryFile?.rasterEntry}</g:link></td>
        </tr>
        </tbody>
      </table>
    </div>
    <div class="buttons">
      <g:form>
        <input type="hidden" name="id" value="${rasterEntryFile?.id}"/>
        <sec:ifAllGranted roles="ROLE_ADMIN">
          <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
          <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </sec:ifAllGranted>
      </g:form>
    </div>
  </div>
</content>
</body>
</html>
