<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="main"/>
  <title>RasterFile List</title>
</head>
<body>
<div class="nav">
  <span class="menuButton"><a class="home" href="${resource(dir: '')}">Home</a></span>
  <g:ifAllGranted role="ROLE_ADMIN">
    <span class="menuButton"><g:link class="create" action="create">New RasterFile</g:link></span>
  </g:ifAllGranted>
</div>
<div class="body">
  <h1>RasterFile List</h1>
  <g:if test="${flash.message}">
    <div class="message">${flash.message}</div>
  </g:if>
  <div class="list">
    <table>
      <thead>
        <tr>

          <g:sortableColumn property="id" title="Id" params="${[rasterDataSetId:params.rasterDataSetId]}"/>

          <g:sortableColumn property="name" title="Name" params="${[rasterDataSetId:params.rasterDataSetId]}"/>

          <g:sortableColumn property="type" title="Type" params="${[rasterDataSetId:params.rasterDataSetId]}"/>

          <th>Raster Data Set</th>

        </tr>
      </thead>
      <tbody>
        <g:each in="${rasterFileList}" status="i" var="rasterFile">
          <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

            <td><g:link action="show" id="${rasterFile.id}">${rasterFile.id?.encodeAsHTML()}</g:link></td>

            <td>${rasterFile.name?.encodeAsHTML()}</td>

            <td>${rasterFile.type?.encodeAsHTML()}</td>

            <td>${rasterFile.rasterDataSet?.encodeAsHTML()}</td>

          </tr>
        </g:each>
      </tbody>
    </table>
  </div>
  <div class="paginateButtons">
    <g:paginate total="${rasterFileList.totalCount}" params="${[rasterDataSetId:params.rasterDataSetId]}"/>
  </div>
</div>
</body>
</html>
