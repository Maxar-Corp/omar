<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="main8"/>
  <title>Raster Entry Search Results</title>
  <resource:tabView/>
</head>
<body>
<content tag="hd">
  <div class="nav">
    <span class="menuButton"><a class="home" href="${resource(dir: '')}">Home</a></span>
    <span class="menuButton"><g:link action="search">New Search</g:link></span>
    <span class="menuButton">
      <a href="${createLink(action: "search", params: params)}">Edit Search</a>
    </span>
  </div>
</content>
<content tag="bd">
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
              <%--
              <g:sortableColumn property="width" title="Width" params="${queryParams.toMap()}"/>
              <g:sortableColumn property="height" title="Height" params="${queryParams.toMap()}"/>
              <g:sortableColumn property="numberOfBands" title="Number of Bands" params="${queryParams.toMap()}"/>
              <g:sortableColumn property="bitDepth" title="Bit Depth" params="${queryParams.toMap()}"/>
              <g:sortableColumn property="dataType" title="Data Type" params="${queryParams.toMap()}"/>
              --%>

              <th>Width</th>
              <th>Height</th>
              <th>Num Bands</th>
              <th>Num Bits</th>
              <th>Data Type</th>

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

                <g:set var="bounds" value="${rasterEntry?.metadata?.groundGeom?.bounds}"/>
                <td>${bounds?.minLon?.encodeAsHTML()}</td>
                <td>${bounds?.minLat?.encodeAsHTML()}</td>
                <td>${bounds?.maxLon?.encodeAsHTML()}</td>
                <td>${bounds?.maxLat?.encodeAsHTML()}</td>

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

              <g:sortableColumn property="acquisitionDate" title="Acquisition Date" params="${queryParams.toMap()}"/>

              <g:each in="${(0..<tagHeaderList?.size())}" var="i">
                <g:sortableColumn property="${tagNameList[i]}" title="${tagHeaderList[i]}" params="${queryParams.toMap()}"/>
              </g:each>

              <th>Thumbnail</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${rasterEntries}" status="i" var="rasterEntry">
              <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                <td><g:link controller="rasterEntry" action="show" id="${rasterEntry.id}">${rasterEntry.id?.encodeAsHTML()}</g:link></td>


                <td><g:formatDate format="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" timeZone="0" date="${rasterEntry?.metadata?.acquisitionDate}"/></td>


                <g:each in="${tagNameList}" var="tagName">
                  <td><%="${rasterEntry?.metadata?."${tagName}"}"%></td>
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
                <g:set var="rasterFile" value="${rasterFiles.find { it.rasterDataSet == rasterEntry.rasterDataSet }}"/>
                <td>${rasterFile?.name?.encodeAsHTML()}</td>
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
                <td>
                  <%--
                  <a href="${createLink(controller: "ogc", action: "wms", params: [request: "GetMap", layers: rasterEntry.id, bbox: [rasterEntry?.groundGeom?.bounds?.minLon, rasterEntry?.groundGeom?.bounds?.minLat, rasterEntry?.groundGeom?.bounds?.maxLon, rasterEntry?.groundGeom?.bounds?.maxLat].join(","), srs: "epsg:4326", width: 1024, height: 512, format: "image/jpeg"])}">
                  --%>
                  <a href="${createLink(controller: "ogc", action: "wms", params: [request: "GetMap", layers: rasterEntry.id, bbox: [rasterEntry?.metadata?.groundGeom?.bounds?.minLon, rasterEntry?.metadata?.groundGeom?.bounds?.minLat, rasterEntry?.metadata?.groundGeom?.bounds?.maxLon, rasterEntry?.metadata?.groundGeom?.bounds?.maxLat].join(","), srs: "epsg:4326", width: 1024, height: 512, format: "image/jpeg"])}">
                    WMS GetMap</a>
                </td>
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

  <g:hiddenField name="totalCount" value="${totalCount ?: 0}"/>
  <g:hiddenField name="max" value="${params.max}"/>
  <g:hiddenField name="offset" value="${params.offset}"/>
  <g:hiddenField name="queryParams" value="${queryParams.toMap()}"/>

</content>
<content tag="ft">
  <div class="paginateButtons">
    <g:paginate controller="rasterEntry" action="results" total="${totalCount ?: 0}"
            max="${params.max}" offset="${params.offset}" params="${queryParams.toMap()}"/>
  </div>
</content>
</body>
</html>
