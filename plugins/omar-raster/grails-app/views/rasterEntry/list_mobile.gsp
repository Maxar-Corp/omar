<html>
<head>
  <meta content="yes" name="apple-mobile-web-app-capable"/>
  <meta content="minimum-scale=1.0, width=device-width, user-scalable=no" name="viewport"/>
  <link rel="stylesheet" href="${resource(plugin: 'omar', dir: 'css', file: 'main.css')}"/>
  <title>OMAR: Raster List</title>
</head>

<body>

<div class="nav">
  <span class="menuButton"><g:link class="home" uri="/">OMAR™ Home</g:link></span>
  <span class="menuButton"><g:link action="search_mobile">Search Rasters</g:link></span>
</div>

<div class="body">
  <div class="paginateButtons">
    <g:paginate total="${rasterEntryList.totalCount}" params="${[rasterDataSet:params.rasterDataSetId]}"/>
  </div>

  <div class="list">
    <table>
      <thead>
      <tr>
        <g:sortableColumn property="id" title="Id" params="${[rasterDataSetId:params.rasterDataSetId]}"/>

        <th width="256">Thumbnail</th>

        <g:sortableColumn property="acquisitionDate" title="Acquisition Date" params="${[rasterDataSetId:params.rasterDataSetId]}"/>

        <th></th>

        <th></th>
      </tr>
      </thead>
      <tbody>
      <g:each in="${rasterEntryList}" status="i" var="rasterEntry">
        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

          <td><g:link action="show" id="${rasterEntry.id}">${rasterEntry.id?.encodeAsHTML()}</g:link></td>

          <td><a href="${createLink(controller: "mapView", params: [layers: rasterEntry.indexId])}">
            <img src="${createLink(controller: 'thumbnail', action: 'show', params: [id: rasterEntry.indexId, size: 256, projectionType: "imagespace"])}" alt="Show Thumbnail"/>
          </a></td>

          <td>${rasterEntry.acquisitionDate?.encodeAsHTML()}</td>

          <td><g:link action="show" id="${rasterEntry.id}">Raster Details</g:link></td>

          <td><a href="${createLink(controller: "mapView", params: [layers: rasterEntry.indexId])}">View Raster</a></td>

        </tr>
      </g:each>
      </tbody>
    </table>
  </div>

  <div class="paginateButtons">
    <g:paginate total="${rasterEntryList.totalCount}" params="${[rasterDataSet:params.rasterDataSetId]}"/>
  </div>

</div>
</body>
</html>