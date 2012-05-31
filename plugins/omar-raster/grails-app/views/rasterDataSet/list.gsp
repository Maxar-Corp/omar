<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="generatedViews"/>
  <title>OMAR: Raster Set List</title>
</head>
<body>
<content tag="content">
  <div class="nav">
      <ul>
          <li class="menuButton"><g:link class="home" uri="/">OMAR™ Home</g:link></li>
          <sec:ifAllGranted roles="ROLE_ADMIN">
              <li class="menuButton"><g:link class="create" action="create">Create Raster Set</g:link></li>
          </sec:ifAllGranted>
      </ul>
   </div>
  <div class="body">
    <h1>OMAR: Raster Set List</h1>
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
