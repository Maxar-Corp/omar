<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="main"/>
  <title>MetadataTag List</title>
</head>
<body>
<div class="nav">
  <span class="menuButton"><a class="home" href="${createLinkTo(dir: '')}">Home</a></span>
  <g:ifAllGranted role="ROLE_ADMIN">
    <span class="menuButton"><g:link class="create" action="create">New MetadataTag</g:link></span>
  </g:ifAllGranted>
</div>
<div class="body">
  <h1>MetadataTag List
  <g:if test="${params.rasterEntryId}">
    For RasterEntry ${params.rasterEntryId}
  </g:if>
  </h1>
  <g:if test="${flash.message}">
    <div class="message">${flash.message}</div>
  </g:if>
  <div class="list">
    <table>
      <thead>
        <tr>

          <g:sortableColumn property="id" title="Id" params="${[rasterEntryId:params?.rasterEntryId ]}"/>

          <g:sortableColumn property="name" title="Name" params="${[rasterEntryId:params?.rasterEntryId ]}"/>

          <g:sortableColumn property="value" title="Value" params="${[rasterEntryId:params?.rasterEntryId ]}"/>

          <th>Raster Entry</th>

        </tr>
      </thead>
      <tbody>
        <g:each in="${metadataTagList}" status="i" var="metadataTag">
          <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

            <td><g:link action="show" id="${metadataTag.id}">${metadataTag.id?.encodeAsHTML()}</g:link></td>

            <td>${metadataTag.name?.encodeAsHTML()}</td>

            <td>${metadataTag.value?.encodeAsHTML()}</td>

            <td>${metadataTag.rasterEntry?.encodeAsHTML()}</td>

          </tr>
        </g:each>
      </tbody>
    </table>
  </div>
  <div class="paginateButtons">
    <g:paginate controller="metadataTag" action="list" total="${metadataTagList.totalCount}"
        max="${params?.max}" offset="${params?.offset}" params="${[rasterEntryId:params?.rasterEntryId ]}"/>
  </div>
</div>
</body>
</html>
