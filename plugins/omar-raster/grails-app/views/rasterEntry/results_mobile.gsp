<html>
<head>
  <meta content="yes" name="apple-mobile-web-app-capable"/>
  <meta content="minimum-scale=1.0, width=device-width, user-scalable=no" name="viewport"/>
  <link rel="stylesheet" href="${resource(plugin: 'omar', dir: 'css', file: 'main.css')}"/>
  <title>OMAR: Raster Results</title>
</head>

<body>

<g:hiddenField name="totalCount" value="${totalCount ?: 0}"/>
<g:hiddenField name="max" value="${params.max}"/>
<g:hiddenField name="offset" value="${params.offset}"/>
<g:hiddenField name="queryParams" value="${queryParams.toMap()}"/>

<div class="nav">
  <span class="menuButton"><g:link class="home" uri="/">OMAR™ Home</g:link></span>
  <span class="menuButton"><g:link action="search_mobile">New Raster Search</g:link></span>
  <span class="menuButton"><g:link action="list_mobile">List All Rasters</g:link></span>
</div>

<div class="body">
  <div class="paginateButtons">
    <g:paginate controller="rasterEntry" action="results_mobile" total="${totalCount ?: 0}" max="${params.max}" offset="${params.offset}" params="${queryParams.toMap()}"/>
  </div>

  <div class="list">
    <table>
      <thead>
      <tr>
        <g:sortableColumn property="id" title="Id" params="${queryParams.toMap()}"/>

        <th width="256">Thumbnail (256x256)</th>

        <g:sortableColumn property="acquisitionDate" title="Acquisition Date" params="${queryParams.toMap()}"/>

        <th></th>

        <th></th>
      </tr>
      </thead>
      <tbody style="overflow:auto">
      <g:each in="${rasterEntries}" status="i" var="rasterEntry">
        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
          <td><g:link controller="rasterEntry" action="show" id="${rasterEntry.id}">${rasterEntry.id?.encodeAsHTML()}</g:link></td>

          <td>
            <a href="${createLink(controller: "mapView", params: [layers: rasterEntry.indexId])}">
              <img src="${createLink(controller: 'thumbnail', action: 'show', id: rasterEntry.id, params: [size: 256, projectionType: "imagespace"])}" alt="Show Thumbnail"/>
            </a>
          </td>

          <td><g:formatDate format="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" timeZone="0" date="${rasterEntry?.acquisitionDate}"/></td>

          <td><g:link controller="rasterEntry" action="show" id="${rasterEntry.id}">Raster Details</g:link></td>

          <td><a href="${createLink(controller: "mapView", params: [layers: rasterEntry.indexId])}">View Raster</a></td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>

  <div class="paginateButtons">
    <g:paginate controller="rasterEntry" action="results_mobile" total="${totalCount ?: 0}" max="${params.max}" offset="${params.offset}" params="${queryParams.toMap()}"/>
  </div>
</div>
</body>

</html>