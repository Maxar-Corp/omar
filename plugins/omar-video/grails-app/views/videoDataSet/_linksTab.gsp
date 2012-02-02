<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: 1/31/12
  Time: 2:14 PM
  To change this template use File | Settings | File Templates.
--%>
<div class="list">
  <table>
    <thead>
    <tr>

      <th>Thumbnail</th>
      <g:sortableColumn property="id" title="Id" params="${queryParams?.toMap()}"/>
      <th>Generate KML</th>

    </tr>
    </thead>
    <tbody>
    <g:each in="${videoDataSets}" status="i" var="videoDataSet">
      <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
        <td height="${videoDataSet.height / (Math.max(videoDataSet.width, videoDataSet.height) / 128.0)}">
          <a href="${createLink(controller: "videoStreaming", action: "show", params: [id: videoDataSet.indexId])}">
            <img src="${createLink(controller: "thumbnail", action: "frame", params: [id: videoDataSet.id, size: 128])}"
                 alt="Show Frame"/>
          </a>
        </td>
        <td>
          <g:link controller="videoDataSet" action="show"
                  id="${videoDataSet.id}">${videoDataSet.id?.encodeAsHTML()}</g:link>
        </td>
        <td>
          <a href='${createLink(controller: 'videoStreaming', action: "getKML", id: videoDataSet.id)}'>Generate KML</a>
        </td>
      </tr>
    </g:each>

    </tbody>
  </table>
</div>
