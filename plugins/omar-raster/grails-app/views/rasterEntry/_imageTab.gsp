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
            <th>View</th>
            <g:sortableColumn property="id" title="Id" params="${queryParams?.toMap()}"/>
            <g:sortableColumn property="entryId" title="Entry Id" params="${queryParams?.toMap()}"/>
            <g:sortableColumn property="width" title="Width" params="${queryParams?.toMap()}"/>
            <g:sortableColumn property="height" title="Height" params="${queryParams?.toMap()}"/>
            <g:sortableColumn property="numberOfBands" title="Bands" params="${queryParams?.toMap()}"/>
            <g:sortableColumn property="numberOfResLevels" title="R-Levels" params="${queryParams?.toMap()}"/>
            <g:sortableColumn property="bitDepth" title="Bit Depth" params="${queryParams?.toMap()}"/>
            <th>Meters Per Pixel</th>
            <th>Min Lon</th>
            <th>Min Lat</th>
            <th>Max Lon</th>
            <th>Max Lat</th>

        </tr>
        </thead>
        <tbody>
        <g:each in="${rasterEntries}" status="i" var="rasterEntry">
            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                <td height="${rasterEntry.height / (Math.max(rasterEntry.width, rasterEntry.height) / 128.0)}">
                    <a href="${createLink(controller: "mapView", action:'imageSpace', params: [layers: rasterEntry.indexId])}">
                    <img src="${createLink(controller: "thumbnail", action: "show", id: rasterEntry.id,
                    params: [size: 128, projectionType: "imagespace"])}" alt="Show Thumbnail"/>
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
                <td>${rasterEntry.entryId?.encodeAsHTML()}</td>
                <td>${rasterEntry.width?.encodeAsHTML()}</td>
                <td>${rasterEntry.height?.encodeAsHTML()}</td>
                <td>${rasterEntry.numberOfBands?.encodeAsHTML()}</td>
                <td>${rasterEntry.numberOfResLevels?.encodeAsHTML()}</td>
                <td>${rasterEntry.bitDepth?.encodeAsHTML()}</td>
                <td>${rasterEntry.metersPerPixel.encodeAsHTML()}</td>
                <g:set var="bounds" value="${rasterEntry?.groundGeom?.bounds}"/>
                <td>${bounds?.minLon?.encodeAsHTML()}</td>
                <td>${bounds?.minLat?.encodeAsHTML()}</td>
                <td>${bounds?.maxLon?.encodeAsHTML()}</td>
                <td>${bounds?.maxLat?.encodeAsHTML()}</td>
            </tr>
        </g:each>
        </tbody>
    </table>
</div>
