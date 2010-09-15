<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="main"/>
  <title>OMAR: Raster Set List</title>
</head>
<body>
<content tag="content">
  <div class="nav">
    <span class="menuButton"><g:link class="home" uri="/">OMAR™ Home</g:link></span>
    <g:ifAllGranted role="ROLE_ADMIN">
      <span class="menuButton"><g:link class="create" action="create">Create Raster Set</g:link></span>
    </g:ifAllGranted>
  </div>
  <div class="body">
    <h1>Raster Set List</h1>
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
    </g:if>
    <div class="list">
      <table>
        <thead>
        <tr>
          <g:sortableColumn property="id" title="Id" params="${[repositoryId:params.repositoryId]}"/>
          <th>Repository</th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${rasterDataSetList}" status="i" var="rasterDataSet">
          <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
            <td><g:link action="show" id="${rasterDataSet.id}">${rasterDataSet.id?.encodeAsHTML()}</g:link></td>
            <td>${rasterDataSet.repository?.encodeAsHTML()}</td>
          </tr>
        </g:each>
        </tbody>
      </table>
    </div>
    <div class="paginateButtons">
      <g:paginate total="${rasterDataSetList.totalCount}" params="${[repositoryId:params.repositoryId]}"/>
    </div>
  </div>
</content>
</body>
</html>