<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="main"/>
  <title>Raster Entry Search Results</title>
  <resource:tabView/>
</head>
<body>
<div class="nav">
  <span class="menuButton"><a class="home" href="${resource(dir: '')}">Home</a></span>
  <span class="menuButton"><g:link action="search">New Search</g:link></span>
  <span class="menuButton">
    <g:link action="search" params="${queryParams.toMap()}">Edit Search</g:link>
  </span>
</div>
<div class="body">
  <h1>Raster Entry Search Results</h1>
  <g:if test="${flash.message}">
    <div class="message">${flash.message}</div>
  </g:if>

  <richui:tabView id="tabView">
    <richui:tabLabels>
      <richui:tabLabel title="Image"/>
      <richui:tabLabel selected="true" title="Metadata"/>
      <richui:tabLabel title="File"/>
      <richui:tabLabel title="Links"/>
    </richui:tabLabels>

    <richui:tabContents>
      <richui:tabContent>
        <div class="list">
          <table>
            <thead>
            <tr>
              <g:sortableColumn property="id" title="Id" params="${queryParams.toMap()}"/>
              <g:sortableColumn property="width" title="Width" params="${queryParams.toMap()}"/>
              <g:sortableColumn property="height" title="Height" params="${queryParams.toMap()}"/>
              <g:sortableColumn property="numberOfBands" title="Number of Bands" params="${queryParams.toMap()}"/>
              <g:sortableColumn property="bitDepth" title="Bit Depth" params="${queryParams.toMap()}"/>
              <g:sortableColumn property="dataType" title="Data Type" params="${queryParams.toMap()}"/>
              <th>Min Lon</th>
              <th>Min Lat</th>
              <th>Max Lon</th>
              <th>Max Lat</th>
              <th>Thumbnail</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${rasterEntries}" status="i" var="rasterEntry">
              <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                <td><g:link controller="rasterEntry" action="show" id="${rasterEntry.id}">${rasterEntry.id?.encodeAsHTML()}</g:link></td>
                <td>${rasterEntry.width?.encodeAsHTML()}</td>
                <td>${rasterEntry.height?.encodeAsHTML()}</td>
                <td>${rasterEntry.numberOfBands?.encodeAsHTML()}</td>
                <td>${rasterEntry.bitDepth?.encodeAsHTML()}</td>
                <td>${rasterEntry.dataType?.encodeAsHTML()}</td>
                <td>${rasterEntry.groundGeom?.bounds?.minLon?.encodeAsHTML()}</td>
                <td>${rasterEntry.groundGeom?.bounds?.minLat?.encodeAsHTML()}</td>
                <td>${rasterEntry.groundGeom?.bounds?.maxLon?.encodeAsHTML()}</td>
                <td>${rasterEntry.groundGeom?.bounds?.maxLat?.encodeAsHTML()}</td>
                <td><a href="${createLink(controller: "mapView", params: [rasterEntryIds: rasterEntry.id])}">
                  <img src="${createLink(controller: 'thumbnail', action: 'show', id: rasterEntry.id, params: [size: 128, projectionType: "imagespace"])}" alt="Show Thumbnail"/>
                </a></td>
              </tr>
            </g:each>
            </tbody>
          </table>
        </div>
      </richui:tabContent>

      <richui:tabContent>
        <div class="list">
          <table>
            <thead>
            <tr>
              <g:sortableColumn property="id" title="Id" params="${queryParams.toMap()}"/>
              <g:sortableColumn property="acquisitionDate" title="Aquisition Date" params="${queryParams.toMap()}"/>

              <g:each in="${tagHeaderList}" var="tagHeader">
                <th>${tagHeader}</th>
              </g:each>

              <th>Thumbnail</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${rasterEntries}" status="i" var="rasterEntry">
              <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                <td><g:link controller="rasterEntry" action="show" id="${rasterEntry.id}">${rasterEntry.id?.encodeAsHTML()}</g:link></td>
                <td>${rasterEntry.acquisitionDate?.encodeAsHTML()}</td>

                <g:each in="${tagNameList}" var="tagName">
                <%--
                <td><%=tags[rasterEntry]?.find { it.name == tagName }?.value?.encodeAsHTML()%></td>
                --%>

                  <td><%=rasterEntry?.metadataTags?.find { it.name == tagName }?.value?.encodeAsHTML()%></td>

                  <%--
                  <td>FOO</td>
                  --%>
                </g:each>

                <td><a href="${createLink(controller: "mapView", params: [rasterEntryIds: rasterEntry.id])}">
                  <img src="${createLink(controller: 'thumbnail', action: 'show', id: rasterEntry.id, params: [size: 128, projectionType: "imagespace"])}" alt="Show Thumbnail"/>
                </a></td>
              </tr>
            </g:each>
            </tbody>
          </table>
        </div>
      </richui:tabContent>

      <richui:tabContent>
        <div class="list">
          <table>
            <thead>
            <tr>
              <g:sortableColumn property="id" title="Id" params="${queryParams.toMap()}"/>
              <th>Filename</th>
              <g:sortableColumn property="entryId" title="Entry Id" params="${queryParams.toMap()}"/>
              <th>Thumbnail</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${rasterEntries}" status="i" var="rasterEntry">
              <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                <td><g:link controller="rasterEntry" action="show" id="${rasterEntry.id}">${rasterEntry.id?.encodeAsHTML()}</g:link></td>
                <td>${rasterEntry.mainFile?.name?.encodeAsHTML()}</td>
                <td>${rasterEntry.entryId?.encodeAsHTML()}</td>
                <td><a href="${createLink(controller: "mapView", params: [rasterEntryIds: rasterEntry.id])}">
                  <img src="${createLink(controller: 'thumbnail', action: 'show', id: rasterEntry.id, params: [size: 128, projectionType: "imagespace"])}" alt="Show Thumbnail"/>
                </a></td>
              </tr>
            </g:each>
            </tbody>
          </table>
        </div>
      </richui:tabContent>

      <richui:tabContent>
        <div class="list">
          <table>
            <thead>
            <tr>
              <g:sortableColumn property="id" title="Id" params="${queryParams.toMap()}"/>
              <th>WMS GetCapabilities</th>
              <th>WMS GetMap</th>
              <th>Generate KML</th>
              <th>Thumbnail</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${rasterEntries}" status="i" var="rasterEntry">
              <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                <td><g:link controller="rasterEntry" action="show" id="${rasterEntry.id}">${rasterEntry.id?.encodeAsHTML()}</g:link></td>
                <td><a href="${createLink(controller: "ogc", action: "wms", params: [request: "GetCapabilities", layers: rasterEntry.id])}">
                  WMS GetCapabilities
                </a></td>
                <td></td>
                <td><a href="${createLink(controller: "ogc", action: "wms", params: [request: "GetKML", layers: rasterEntry.id, format: "image/png", transparent: "true"])}">
                  Generate KML
                </a></td>
                <td><a href="${createLink(controller: "mapView", params: [rasterEntryIds: rasterEntry.id])}">
                  <img src="${createLink(controller: 'thumbnail', action: 'show', id: rasterEntry.id, params: [size: 128, projectionType: "imagespace"])}" alt="Show Thumbnail"/>
                </a></td>
              </tr>
            </g:each>
            </tbody>
          </table>
        </div>
      </richui:tabContent>
    </richui:tabContents>
  </richui:tabView>


  <div class="paginateButtons">
    <g:paginate controller="rasterEntry" action="results" total="${rasterEntries?.totalCount ?: 0}"
            max="${params.max}" offset="${params.offset}" params="${queryParams.toMap()}"/>
  </div>
</div>
</body>
</html>
