<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: 1/31/12
  Time: 2:02 PM
  To change this template use File | Settings | File Templates.
--%>

<div class="list">
  <table>
    <thead>
    <tr>

        <th>Thumbnail</th>
        <th>View</th>

        <g:sortableColumn property="id" title="${message( code: 'rasterEntry.id.label', default: 'Id' )}"
                        params="${queryParams?.toMap()}"/>

      <g:sortableColumn property="filename"
                        title="${message( code: 'rasterEntry.filename.label', default: 'Filename' )}"
                        params="${queryParams?.toMap()}"/>

    </tr>
    </thead>
    <tbody>
    <g:each in="${rasterEntries}" status="i" var="rasterEntry">
      <tr class="${( i % 2 ) == 0 ? 'odd' : 'even'}">
        <td height="${rasterEntry.height / ( Math.max( rasterEntry.width, rasterEntry.height ) / 128.0 )}">
            <a href="${createLink( controller: 'mapView', action:'imageSpace', params: [layers: rasterEntry.indexId])}">
               <img src="${createLink( controller: 'thumbnail', action: 'show', id: rasterEntry.id, params: [size: 128, projectionType: "imagespace"])}"
                 alt="Show Thumbnail"/>
            </a>
        </td>
        <td>
            <a class='buttons' href="${createLink( controller: 'mapView', action:'imageSpace', params: [layers: rasterEntry.indexId])}" >Raw</a><br/>
            <br/>
            <a class='buttons' href="${createLink( controller: 'mapView', action:'index', params: [layers: rasterEntry.indexId])}" >Ortho</a>

        </td>
        <td><g:link controller="rasterEntry" action="show"
                    id="${rasterEntry.id}">${rasterEntry.id?.encodeAsHTML()}</g:link></td>
        <td>
          <sec:ifAllGranted roles="ROLE_DOWNLOAD">
            <a href=${grailsApplication.config.image.download.prefix}${rasterEntry.mainFile?.name?.encodeAsHTML()}>
          </sec:ifAllGranted>
          ${rasterEntry.mainFile?.name?.encodeAsHTML()}
          <sec:ifAllGranted roles="ROLE_DOWNLOAD">
            </a>
          </sec:ifAllGranted>
        </td>
      </tr>
    </g:each>
    </tbody>
  </table>
</div>
