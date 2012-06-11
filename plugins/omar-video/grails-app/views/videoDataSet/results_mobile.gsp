<html>
<head>
  <meta content="yes" name="apple-mobile-web-app-capable"/>
  <meta content="minimum-scale=1.0, width=device-width, user-scalable=no" name="viewport"/>
  <link rel="stylesheet" href="${resource(plugin: 'omar', dir: 'css', file: 'main.css')}"/>
  <title>OMAR: Video Results</title>
</head>

<body>

<div class="nav">
  <ul>
  <li><g:link class="home" uri="/">OMAR™ Home</g:link></li>
  <li><g:link action="search_mobile">New Video Search</g:link></li>
  <li><g:link action="list_mobile">List All Videos</g:link></li>
  </ul>
</div>


<div class="body">
  <div class="paginateButtons">
    <g:paginate controller="videoDataSet" action="results_mobile" total="${totalCount ?: 0}" max="${params.max}" offset="${params.offset}" params="${queryParams.toMap()}"/>
  </div>

  <div class="list">
    <table>
      <thead>
      <tr>
        <g:sortableColumn property="id" title="Id" params="${queryParams.toMap()}"/>

        <th>Thumbnail (256x256)</th>

        <g:sortableColumn property="startDate" title="Start Date" params="${queryParams.toMap()}"/>

        <g:sortableColumn property="endDate" title="End Date" params="${queryParams.toMap()}"/>

        <th></th>

        <th></th>
      </tr>
      </thead>
      <tbody>
      <g:each in="${videoDataSets}" status="i" var="videoDataSet">
        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
          <td><g:link action="show" id="${videoDataSet.id}">${videoDataSet.id?.encodeAsHTML()}</g:link></td>

          <td>
            <a href="${createLink(controller: 'videoStreaming', action: 'show_mobile', params: [id: videoDataSet.indexId])}">
              <img src="${createLink(controller: 'thumbnail', action: 'frame', params: [id: videoDataSet.indexId, size: 256])}" alt="Show Frame"/>
            </a>
          </td>

          <td><g:formatDate format="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" timeZone="GMT" date="${videoDataSet?.startDate}"/></td>

          <td><g:formatDate format="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" timeZone="GMT" date="${videoDataSet?.endDate}"/></td>

          <td><g:link action="show" id="${videoDataSet.id}">Video Details</g:link></td>

          <td><a href="${createLink(controller: 'videoStreaming', action: 'show_mobile', params: [id: videoDataSet.indexId])}">View Video</a></td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>

  <div class="paginateButtons">
    <g:paginate controller="videoDataSet" action="results_mobile" total="${totalCount ?: 0}" max="${params.max}" offset="${params.offset}" params="${queryParams.toMap()}"/>
  </div>
</div>
</body>

</html>
