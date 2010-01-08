<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="main"/>
  <title>Show MetadataTag</title>
</head>
<body>
<div class="nav">
  <span class="menuButton"><a class="home" href="${createLinkTo(dir: '')}">Home</a></span>
  <span class="menuButton"><g:link class="list" action="list">MetadataTag List</g:link></span>
  <g:ifAllGranted role="ROLE_ADMIN">
    <span class="menuButton"><g:link class="create" action="create">New MetadataTag</g:link></span>
  </g:ifAllGranted>
</div>
<div class="body">
  <h1>Show MetadataTag</h1>
  <g:if test="${flash.message}">
    <div class="message">${flash.message}</div>
  </g:if>
  <div class="dialog">
    <table>
      <tbody>

        <tr class="prop">
          <td valign="top" class="name">Id:</td>

          <td valign="top" class="value">${metadataTag.id}</td>

        </tr>

        <tr class="prop">
          <td valign="top" class="name">Name:</td>

          <td valign="top" class="value">${metadataTag.name}</td>

        </tr>

        <tr class="prop">
          <td valign="top" class="name">Value:</td>

          <td valign="top" class="value">${metadataTag.value}</td>

        </tr>

        <tr class="prop">
          <td valign="top" class="name">Raster Entry:</td>

          <td valign="top" class="value"><g:link controller="rasterEntry" action="show" id="${metadataTag?.rasterEntry?.id}">${metadataTag?.rasterEntry}</g:link></td>

        </tr>

      </tbody>
    </table>
  </div>
  <div class="buttons">
    <g:form>
      <input type="hidden" name="id" value="${metadataTag?.id}"/>
      <g:ifAllGranted role="ROLE_ADMIN">
        <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
        <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
      </g:ifAllGranted>
    </g:form>
  </div>
</div>
</body>
</html>
