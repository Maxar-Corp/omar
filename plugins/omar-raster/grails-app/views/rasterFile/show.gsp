<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="main5"/>
  <title>Show RasterFile</title>
</head>
<body>
<div class="nav">
  <span class="menuButton"><g:link class="home" uri="/">Home</g:link></span>
  <span class="menuButton"><g:link class="list" action="list">RasterFile List</g:link></span>
  <g:ifAllGranted role="ROLE_ADMIN">
    <span class="menuButton"><g:link class="create" action="create">New RasterFile</g:link></span>
  </g:ifAllGranted>
</div>
<div class="body">
  <h1>Show RasterFile</h1>
  <g:if test="${flash.message}">
    <div class="message">${flash.message}</div>
  </g:if>
  <div class="dialog">
    <table>
      <tbody>

        <tr class="prop">
          <td valign="top" class="name">Id:</td>

          <td valign="top" class="value">${rasterFile.id}</td>

        </tr>

        <tr class="prop">
          <td valign="top" class="name">Name:</td>

          <td valign="top" class="value">${rasterFile.name}</td>

        </tr>

        <tr class="prop">
          <td valign="top" class="name">Type:</td>

          <td valign="top" class="value">${rasterFile.type}</td>

        </tr>

        <tr class="prop">
          <td valign="top" class="name">Raster Data Set:</td>

          <td valign="top" class="value"><g:link controller="rasterDataSet" action="show" id="${rasterFile?.rasterDataSet?.id}">${rasterFile?.rasterDataSet}</g:link></td>

        </tr>

      </tbody>
    </table>
  </div>
  <div class="buttons">
    <g:form>
      <input type="hidden" name="id" value="${rasterFile?.id}"/>
      <g:ifAllGranted role="ROLE_ADMIN">
        <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
        <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
      </g:ifAllGranted>
    </g:form>
  </div>
</div>
</body>
</html>
