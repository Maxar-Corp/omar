<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: 1/31/12
  Time: 2:33 PM
  To change this template use File | Settings | File Templates.
--%>

<div class="list">
  <table>
    <thead>
    <tr>
      <th>Thumbnail</th>
      <g:sortableColumn property="id" title="Id" params="${queryParams.toMap()}"/>
      <g:sortableColumn property="width" title="Width" params="${queryParams.toMap()}"/>
      <g:sortableColumn property="height" title="Height" params="${queryParams.toMap()}"/>
      <g:sortableColumn property="startDate" title="Start Date" params="${queryParams.toMap()}"/>
      <g:sortableColumn property="endDate" title="End Date" params="${queryParams.toMap()}"/>
      <th>Min Lon</th>
      <th>Min Lat</th>
      <th>Max Lon</th>
      <th>Max Lat</th>
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
        <td>${videoDataSet.width?.encodeAsHTML()}</td>
        <td>${videoDataSet.height?.encodeAsHTML()}</td>
        <td>
          <g:formatDate format="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" timeZone="GMT"
                        date="${videoDataSet?.startDate}"/>
        </td>
        <td>
          <g:formatDate format="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" timeZone="GMT"
                        date="${videoDataSet?.endDate}"/>
        </td>
        <g:set var="bounds" value="${videoDataSet?.groundGeom?.bounds}"/>
        <td>${bounds?.minLon?.encodeAsHTML()}</td>
        <td>${bounds?.minLat?.encodeAsHTML()}</td>
        <td>${bounds?.maxLon?.encodeAsHTML()}</td>
        <td>${bounds?.maxLat?.encodeAsHTML()}</td>
      </tr>
    </g:each>
    </tbody>
  </table>
</div>
