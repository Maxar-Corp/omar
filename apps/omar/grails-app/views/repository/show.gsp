<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="main5"/>
  <title>Show Repository</title>
</head>
<body>
<div class="nav">
  <span class="menuButton"><a class="home" href="${resource(dir: '')}">Home</a></span>
  <span class="menuButton"><g:link class="list" action="list">Repository List</g:link></span>
  <g:ifAllGranted role="ROLE_ADMIN">
    <span class="menuButton"><g:link class="create" action="create">New Repository</g:link></span>
  </g:ifAllGranted>
</div>
<div class="body">
  <h1>Show Repository</h1>
  <g:if test="${flash.message}">
    <div class="message">${flash.message}</div>
  </g:if>
  <div class="dialog">
    <table>
      <tbody>

      <tr class="prop">
        <td valign="top" class="name">Id:</td>

        <td valign="top" class="value">${fieldValue(bean: repository, field: 'id')}</td>

      </tr>

      <tr class="prop">
        <td valign="top" class="name">Base Dir:</td>

        <td valign="top" class="value">${fieldValue(bean: repository, field: 'baseDir')}</td>

      </tr>

      <tr class="prop">
        <td valign="top" class="name">Scan Start Date:</td>

        <td valign="top" class="value">${fieldValue(bean: repository, field: 'scanStartDate')}</td>

      </tr>

      <tr class="prop">
        <td valign="top" class="name">Scan End Date:</td>

        <td valign="top" class="value">${fieldValue(bean: repository, field: 'scanEndDate')}</td>

      </tr>

      <%--
      <tr class="prop">
        <td valign="top" class="name">Raster Data Sets:</td>

        <td valign="top" style="text-align:left;" class="value">
          <g:if test="${repository.rasterDataSets}">
            <g:link controller="rasterDataSet" action="list" params="${[repositoryId: repository.id]}">Show Raster Datasets</g:link>
          </g:if>
        </td>

      </tr>

      <tr class="prop">
        <td valign="top" class="name">Video Data Sets:</td>

        <td valign="top" style="text-align:left;" class="value">
          <g:if test="${repository.videoDataSets}">
            <g:link controller="videoDataSet" action="list" params="${[repositoryId: repository.id]}">Show Video Datasets</g:link>
          </g:if>
        </td>

      </tr>
     --%>

      </tbody>
    </table>
  </div>
  <div class="buttons">
    <g:form>
      <input type="hidden" name="id" value="${repository?.id}"/>
      <g:ifAllGranted role="ROLE_ADMIN">
        <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
        <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        <span class="menuButton"><g:link action="runStager" id="${repository?.id}">Run Stager</g:link></span>
      </g:ifAllGranted>
    </g:form>
  </div>
</div>
</body>
</html>
