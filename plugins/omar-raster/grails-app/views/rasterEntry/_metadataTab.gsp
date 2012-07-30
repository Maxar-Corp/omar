<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: 1/31/12
  Time: 1:10 PM
  To change this template use File | Settings | File Templates.
--%>

<div class="list">
  <table>
    <thead>
    <tr>

        <th>Thumbnail</th>
        <th>View</th>

        <g:sortableColumn property="id" title="${message(code: 'rasterEntry.id.label', default: 'Id')}"
                        params="${queryParams?.toMap()}"/>

      <g:sortableColumn property="acquisitionDate"
                        title="${message(code: 'rasterEntry.acquisitionDate.label', default: 'Acquisition Date')}"
                        params="${queryParams?.toMap()}"/>

      <g:each in="${(0..<tagHeaderList?.size())}" var="i">
        <g:sortableColumn property="${tagNameList[i]}" title="${tagHeaderList[i]}"
                          params="${queryParams?.toMap()}"/>
      </g:each>

    </tr>
    </thead>
    <tbody>
    <g:each in="${rasterEntries}" status="i" var="rasterEntry">
      <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

        <td height="${rasterEntry.height / (Math.max(rasterEntry.width, rasterEntry.height) / 128.0)}">
          <a href="${createLink(controller: 'mapView', action:'imageSpace', params: [layers: rasterEntry.indexId])}">
            <img src="${createLink(controller: 'thumbnail', action: 'show', id: rasterEntry.id, params:
                [size: 128, projectionType: 'imagespace'])}" alt="Show Thumbnail"/>
          </a>
        </td>
          <td>
              <a class='buttons' href="${createLink( controller: 'mapView', action:'imageSpace', params: [layers: rasterEntry.indexId])}" >Raw</a><br/>
              <br/>
              <a class='buttons' href="${createLink( controller: 'mapView', action:'index', params: [layers: rasterEntry.indexId])}" >Ortho</a>

          </td>

          <td>
          <g:link controller="rasterEntry" action="show"
                  id="${rasterEntry.id}">${rasterEntry.id?.encodeAsHTML()}</g:link>
        </td>

        <td>
          <g:formatDate ormat="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" timeZone="0" date="${rasterEntry.acquisitionDate}"/>
        </td>

        <g:each in="${tagNameList}" var="tagName">
          <g:set var="tag" value='${rasterEntry?.properties[tagName]}'/>
          <td>${tag?.encodeAsHTML()}</td>
        </g:each>

      </tr>
    </g:each>
    </tbody>
  </table>
</div>
