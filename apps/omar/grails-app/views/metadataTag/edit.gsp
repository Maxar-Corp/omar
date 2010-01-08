<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="main"/>
  <title>Edit MetadataTag</title>
</head>
<body>
<div class="nav">
  <span class="menuButton"><a class="home" href="${createLinkTo(dir: '')}">Home</a></span>
  <span class="menuButton"><g:link class="list" action="list">MetadataTag List</g:link></span>
  <span class="menuButton"><g:link class="create" action="create">New MetadataTag</g:link></span>
</div>
<div class="body">
  <h1>Edit MetadataTag</h1>
  <g:if test="${flash.message}">
    <div class="message">${flash.message}</div>
  </g:if>
  <g:hasErrors bean="${metadataTag}">
    <div class="errors">
      <g:renderErrors bean="${metadataTag}" as="list"/>
    </div>
  </g:hasErrors>
  <g:form method="post">
    <input type="hidden" name="id" value="${metadataTag?.id}"/>
    <div class="dialog">
      <table>
        <tbody>

          <tr class="prop">
            <td valign="top" class="name">
              <label for="name">Name:</label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: metadataTag, field: 'name', 'errors')}">
              <input type="text" id="name" name="name" value="${fieldValue(bean: metadataTag, field: 'name')}"/>
            </td>
          </tr>

          <tr class="prop">
            <td valign="top" class="name">
              <label for="value">Value:</label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: metadataTag, field: 'value', 'errors')}">
              <input type="text" id="value" name="value" value="${fieldValue(bean: metadataTag, field: 'value')}"/>
            </td>
          </tr>

          <tr class="prop">
            <td valign="top" class="name">
              <label for="rasterEntry">Raster Entry:</label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: metadataTag, field: 'rasterEntry', 'errors')}">
              <g:select optionKey="id" from="${RasterEntry.list()}" name="rasterEntry.id" value="${metadataTag?.rasterEntry?.id}"></g:select>
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
</body>
</html>
