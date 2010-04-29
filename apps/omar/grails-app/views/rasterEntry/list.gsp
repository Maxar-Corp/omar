<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="main5"/>
  <title>RasterEntry List</title>
  <resource:tabView/>
</head>
<body>
<div class="nav">
  <span class="menuButton"><a class="home" href="${resource(dir: '')}">Home</a></span>
  <g:ifAllGranted role="ROLE_ADMIN">
    <span class="menuButton"><g:link class="create" action="create">New RasterEntry</g:link></span>
  </g:ifAllGranted>
  <span class="menuButton"><g:link action="search">Search</g:link></span></div>
<div class="body">
  <h1>RasterEntry List</h1>
  <g:if test="${flash.message}">
    <div class="message">${flash.message}</div>
  </g:if>
  <richui:tabView id="tabView">
    <richui:tabLabels>
      <richui:tabLabel title="Image" selected="true"/>
      <richui:tabLabel title="File"/>
    </richui:tabLabels>

    <richui:tabContents>
      <richui:tabContent>
        <div class="list">
          <table>
            <thead>
            <tr>

              <g:sortableColumn property="id" title="Id" params="${[rasterDataSetId:params.rasterDataSetId]}"/>

              <g:sortableColumn property="entryId" title="Entry Id" params="${[rasterDataSetId:params.rasterDataSetId]}"/>

              <g:sortableColumn property="width" title="Width" params="${[rasterDataSetId:params.rasterDataSetId]}"/>

              <g:sortableColumn property="height" title="Height" params="${[rasterDataSetId:params.rasterDataSetId]}"/>

              <g:sortableColumn property="numberOfBands" title="Number Of Bands" params="${[rasterDataSetId:params.rasterDataSetId]}"/>

              <g:sortableColumn property="numberOfResLevels" title="Number Of R-Levels" params="${[rasterDataSetId:params.rasterDataSetId]}"/>

              <g:sortableColumn property="bitDepth" title="Bit Depth" params="${[rasterDataSetId:params.rasterDataSetId]}"/>

              <th>Min Lon</th>
              <th>Min Lat</th>
              <th>Max Lon</th>
              <th>Max Lat</th>
              <th>Thumbnail</th>

            </tr>
            </thead>
            <tbody>
            <g:each in="${rasterEntryList}" status="i" var="rasterEntry">
              <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

                <td><g:link action="show" id="${rasterEntry.id}">${rasterEntry.id?.encodeAsHTML()}</g:link></td>

                <td>${rasterEntry.entryId?.encodeAsHTML()}</td>

                <td>${rasterEntry.width?.encodeAsHTML()}</td>

                <td>${rasterEntry.height?.encodeAsHTML()}</td>

                <td>${rasterEntry.numberOfBands?.encodeAsHTML()}</td>

                <td>${rasterEntry.numberOfResLevels?.encodeAsHTML()}</td>
                
                <td>${rasterEntry.bitDepth?.encodeAsHTML()}</td>

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

              <g:sortableColumn property="id" title="Id" params="${[rasterDataSetId:params.rasterDataSetId]}"/>

              <th>Filename</th>
              <th>Thumbnail</th>

            </tr>
            </thead>
            <tbody>
            <g:each in="${rasterEntryList}" status="i" var="rasterEntry">
              <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

                <td><g:link action="show" id="${rasterEntry.id}">${rasterEntry.id?.encodeAsHTML()}</g:link></td>


                <td>${rasterEntry.mainFile?.name?.encodeAsHTML()}</td>


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
    <g:paginate total="${rasterEntryList.totalCount}" params="${[rasterDataSet:params.rasterDataSetId]}"/>
  </div>
</div>
</body>
</html>
