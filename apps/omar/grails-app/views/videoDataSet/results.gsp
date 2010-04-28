<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="main"/>
  <title>Video Data Set Search Results</title>
  <resource:tabView/>
</head>
<body>
<div class="nav">
  <span class="menuButton"><a class="home" href="${resource(dir: '')}">Home</a></span>
  <span class="menuButton"><g:link action="search">New Search</g:link></span>
  <span class="menuButton">
    <a href="${createLink(action: "search", params: params)}">Edit Search</a>
  </span>
</div>
<div class="body">
  <h1>Video Data Set Search Results</h1>
  <g:if test="${flash.message}">
    <div class="message">${flash.message}</div>
  </g:if>
  <richui:tabView id="tabView">
    <richui:tabLabels>
      <richui:tabLabel title="Video" selected="true"/>
      <richui:tabLabel title="File"/>
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
              --%>

              <th>Width</th>
              <th>Height</th>

              <g:sortableColumn property="startDate" title="Start Date" params="${queryParams.toMap()}"/>
              <g:sortableColumn property="endDate" title="End Date" params="${queryParams.toMap()}"/>

              <%--
              <th>Ground Geom</th>
              --%>

              <th>Min Lon</th>
              <th>Min Lat</th>
              <th>Max Lon</th>
              <th>Max Lat</th>
              <th>Thumbnail</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${videoDataSets}" status="i" var="videoDataSet">
              <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

                <td><g:link controller="videoDataSet" action="show" id="${videoDataSet.id}">${videoDataSet.id?.encodeAsHTML()}</g:link></td>

                <td>${videoDataSet.width?.encodeAsHTML()}</td>

                <td>${videoDataSet.height?.encodeAsHTML()}</td>

                <td><g:formatDate format="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" timeZone="GMT" date="${videoDataSet?.metadata?.startDate}"/></td>

                <td><g:formatDate format="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" timeZone="GMT" date="${videoDataSet?.metadata?.endDate}"/></td>

                <g:set var="bounds" value="${videoDataSet?.metadata?.groundGeom?.bounds}"/>
                <td>${bounds?.minLon?.encodeAsHTML()}</td>
                <td>${bounds?.minLat?.encodeAsHTML()}</td>
                <td>${bounds?.maxLon?.encodeAsHTML()}</td>
                <td>${bounds?.maxLat?.encodeAsHTML()}</td>

                <%--
                <td>${videoDataSet.groundGeom?.encodeAsHTML()}</td>
                --%>

                <td>
                  <a href="${createLink(controller: 'videoStreaming', action: 'show', id: videoDataSet.id)}">
                    <img src="${createLink(controller: 'thumbnail', action: 'frame', id: videoDataSet.id, params: [size: 128])}" alt="Show Frame"/>
                  </a>

                </td>
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
              <th>Thumbnail</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${videoDataSets}" status="i" var="videoDataSet">
              <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                <td><g:link controller="videoDataSet" action="show" id="${videoDataSet.id}">${videoDataSet.id?.encodeAsHTML()}</g:link></td>
                <g:set var="videoFile" value="${videoFiles.find { it.videoDataSet == videoDataSet }}"/>
                <td>${videoFile?.name?.encodeAsHTML()}</td>
                <td>
                  <a href="${createLink(controller: 'videoStreaming', action: 'show', id: videoDataSet.id)}">
                    <img src="${createLink(controller: 'thumbnail', action: 'frame', id: videoDataSet.id, params: [size: 128])}" alt="Show Frame"/>
                  </a>
                </td>
              </tr>
            </g:each>
            </tbody>
          </table>
        </div>
      </richui:tabContent>
    </richui:tabContents>
  </richui:tabView>

  <div class="paginateButtons">
    <g:paginate controller="videoDataSet" action="results" total="${totalCount ?: 0}"
            max="${params.max}" offset="${params.offset}" params="${queryParams.toMap()}"/>
  </div>
</div>
</body>
</html>
