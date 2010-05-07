<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="main5"/>
  <title>RasterEntryFile List</title>
</head>
<body>
<div class="nav">
  <span class="menuButton">
	<g:link class="home" controller="home">Home</g:link>
  </span>
  <g:ifAllGranted role="ROLE_ADMIN">
    <span class="menuButton"><g:link class="create" action="create">New RasterEntryFile</g:link></span>
  </g:ifAllGranted>
</div>
<div class="body">
  <h1>RasterEntryFile List</h1>
  <g:if test="${flash.message}">
    <div class="message">${flash.message}</div>
  </g:if>
  <div class="list">
    <table>
      <thead>
        <tr>

          <g:sortableColumn property="id" title="Id" params="${[rasterEntryId:params.rasterEntryId]}"/>

          <g:sortableColumn property="name" title="Name" params="${[rasterEntryId:params.rasterEntryId]}"/>

          <g:sortableColumn property="type" title="Type" params="${[rasterEntryId:params.rasterEntryId]}"/>

          <th>Raster Entry</th>

        </tr>
      </thead>
      <tbody>
        <g:each in="${rasterEntryFileList}" status="i" var="rasterEntryFile">
          <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

            <td><g:link action="show" id="${rasterEntryFile.id}">${rasterEntryFile.id?.encodeAsHTML()}</g:link></td>

            <td>${rasterEntryFile.name?.encodeAsHTML()}</td>

            <td>${rasterEntryFile.type?.encodeAsHTML()}</td>

            <td>${rasterEntryFile.rasterEntry?.encodeAsHTML()}</td>

          </tr>
        </g:each>
      </tbody>
    </table>
  </div>
  <div class="paginateButtons">
    <g:paginate total="${rasterEntryFileList.totalCount}" params="${[rasterEntryId:params.rasterEntryId]}"/>
  </div>
</div>
</body>
</html>
