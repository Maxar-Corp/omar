<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="generatedViews"/>
  <title>OMAR: Show Raster Set ${rasterDataSet.id}</title>
</head>
<body>
<content tag="content">
  <div class="nav">
      <ul>
          <li class="menuButton"><g:link class="home" uri="/">OMAR™ Home</g:link></li>
          <li class="menuButton"><g:link class="list" action="list">Raster Set List</g:link></li>
          <sec:ifAllGranted roles="ROLE_ADMIN">
              <li class="menuButton"><g:link class="create" action="create">Create Raster Set</g:link></li>
          </sec:ifAllGranted>
      </ul>
  </div>
  <div class="body">
    <h1>OMAR: Show Raster Set ${rasterDataSet.id}</h1>
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
    </g:if>
    <div class="dialog">
      <table>
        <tbody>
        <tr class="prop">
          <td valign="top" class="name">Id:</td>
          <td valign="top" class="value">${rasterDataSet.id}</td>
        </tr>
        <tr class="prop">
          <td valign="top" class="name">File Objects:</td>
          <td valign="top" style="text-align:left;" class="value">
            <g:if test="${rasterDataSet.fileObjects}">
              <g:link controller="rasterFile" action="list" params="${[rasterDataSetId: rasterDataSet.id]}">Show Raster Files</g:link>
            </g:if>
          </td>
        </tr>
        <tr class="prop">
          <td valign="top" class="name">Raster Entries:</td>
          <td valign="top" style="text-align:left;" class="value">
            <g:if test="${rasterDataSet.rasterEntries}">
              <g:link controller="rasterEntry" action="list" params="${[rasterDataSetId: rasterDataSet.id]}">Show Raster Entries</g:link>
            </g:if>
          </td>
        </tr>
        <tr class="prop">
          <td valign="top" class="name">Repository:</td>
          <td valign="top" class="value"><g:link controller="repository" action="show" id="${rasterDataSet?.repository?.id}">${rasterDataSet?.repository}</g:link></td>
        </tr>
        </tbody>
      </table>
    </div>
    <div class="buttons">
      <g:form>
        <input type="hidden" name="id" value="${rasterDataSet?.id}"/>
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
