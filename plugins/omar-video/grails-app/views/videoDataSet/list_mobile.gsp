<html>
<head>
  <meta content="yes" name="apple-mobile-web-app-capable"/>
  <meta content="minimum-scale=1.0, width=device-width, user-scalable=no" name="viewport"/>
  <link rel="stylesheet" href="${resource(plugin: 'omar', dir: 'css', file: 'main.css')}"/>
  <title>OMAR: Video List</title>
</head>

<body>

<div class="nav">
  <ul>
  <li><g:link class="home" uri="/">OMAR™ Home</g:link></li>
  <li><g:link action="search_mobile">Search Videos</g:link></li>
  </ul>
</div>

<div class="body">
  <div class="paginateButtons">
    <g:paginate total="${videoDataSetList.totalCount}" params="${[repositoryId:params.repositoryId]}"/>
  </div>

  <div class="list">
    <table>
      <thead>
      <tr>
        <g:sortableColumn property="id" title="Id" params="${[repositoryId:params.repositoryId]}"/>

        <th>Thumbnail (256x256)</th>

        <g:sortableColumn property="startDate" title="Start Date" params="${[repositoryId:params.startDate]}"/>

        <g:sortableColumn property="endDate" title="End Date" params="${[repositoryId:params.endDate]}"/>

        <th></th>

        <th></th>
      </tr>
      </thead>
      <tbody>
      <g:each in="${videoDataSetList}" status="i" var="videoDataSet">
        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
          <td><g:link action="show" id="${videoDataSet.id}">${fieldValue(bean: videoDataSet, field: 'id')}</g:link></td>

          <td>
            <a href="${createLink(controller: 'videoStreaming', action: 'show_mobile', id: videoDataSet.indexId)}">
              <img src="${createLink(controller: 'thumbnail', action: 'frame', id: videoDataSet.indexId, params: [size: 256])}" alt="Show Frame"/>
            </a>
          </td>

          <td>${fieldValue(bean: videoDataSet, field: 'startDate')}</td>

          <td>${fieldValue(bean: videoDataSet, field: 'endDate')}</td>

          <td><g:link action="show" id="${videoDataSet.id}">Video Details</g:link></td>

          <td><a href="${createLink(controller: 'videoStreaming', action: 'show_mobile', id: videoDataSet.indexId)}">View Video</a></td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>

  <div class="paginateButtons">
    <g:paginate total="${videoDataSetList.totalCount}" params="${[repositoryId:params.repositoryId]}"/>
  </div>
</div>
</body>

</html>
