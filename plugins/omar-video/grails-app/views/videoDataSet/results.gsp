<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="main8"/>
  <title>Video Data Set Search Results</title>
  <resource:tabView/>
  <g:javascript plugin="omar-core" src="prototype/prototype.js"/>

  <g:javascript>
   var globalActiveIndex=${videoDataSetResultCurrentTab};
   function updateSession(event){
    var link = "${createLink(action: sessionAction, controller: sessionController)}"
    var activeIndex = tabView.get('activeIndex').toString();
    // only send a message if we change state this way it's fast
    //
    if(activeIndex != globalActiveIndex)
    {
      globalActiveIndex = activeIndex.toString();
      new Ajax.Request(link+"?"+"videoDataSetResultCurrentTab="+activeIndex, {
        method: 'post'
      });
    }
  }
  </g:javascript>
</head>
<body>
<content tag="north">
  <div class="nav">
    <span class="menuButton"><g:link class="home" uri="/">Home</g:link></span>
    <span class="menuButton"><g:link action="search">New Search</g:link></span>
    <span class="menuButton">
      <a href="${createLink(action: "search", params: params)}">Edit Search</a>
    </span>
  </div>
</content>
<content tag="center">
  <h1>Video Data Set Search Results</h1>
  <g:if test="${flash.message}">
    <div class="message">${flash.message}</div>
  </g:if>
  <richui:tabView id="tabView">
    <omar:observe element="tabView" event="mouseover" function="updateSession"/>
    <richui:tabLabels>
      <g:if test="${videoDataSetResultCurrentTab == '0'}">
        <richui:tabLabel title="Video" selected="true"/>
      </g:if>
      <g:else>
        <richui:tabLabel title="Video"/>
      </g:else>
      <g:if test="${videoDataSetResultCurrentTab == '1'}">
        <richui:tabLabel title="File" selected="true"/>
      </g:if>
      <g:else>
        <richui:tabLabel title="File"/>
      </g:else>
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

                <td><g:formatDate format="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" timeZone="GMT" date="${videoDataSet?.startDate}"/></td>

                <td><g:formatDate format="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" timeZone="GMT" date="${videoDataSet?.endDate}"/></td>

                <g:set var="bounds" value="${videoDataSet?.groundGeom?.bounds}"/>
                <td>${bounds?.minLon?.encodeAsHTML()}</td>
                <td>${bounds?.minLat?.encodeAsHTML()}</td>
                <td>${bounds?.maxLon?.encodeAsHTML()}</td>
                <td>${bounds?.maxLat?.encodeAsHTML()}</td>

                <%--
                <td>${videoDataSet.groundGeom?.encodeAsHTML()}</td>
                --%>

                <td>
                  <a href="${createLink(controller: 'videoStreaming', action: 'show', params: [id:videoDataSet.indexId])}">
                    <img src="${createLink(controller: 'thumbnail', action: 'frame', params: [id: videoDataSet.indexId, size: 128])}" alt="Show Frame"/>
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

                <td>
                  <g:ifAllGranted role="ROLE_DOWNLOAD">
                    <a href=${grailsApplication.config.image.download.prefix}${videoFile?.name?.encodeAsHTML()}>
                  </g:ifAllGranted>

                  ${videoFile?.name?.encodeAsHTML()}

                  <g:ifAllGranted role="ROLE_DOWNLOAD">
                    </a>
                  </g:ifAllGranted>
                </td>

                <td>
                  <a href="${createLink(controller: 'videoStreaming', action: 'show', params: [id: videoDataSet.indexId])}">
                    <img src="${createLink(controller: 'thumbnail', action: 'frame', params: [id: videoDataSet.indexId, size: 128])}" alt="Show Frame"/>
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

</content>
<content tag="south">
  <div class="paginateButtons">
    <g:paginate controller="videoDataSet" action="results" total="${totalCount ?: 0}"
            max="${params.max}" offset="${params.offset}" params="${queryParams.toMap()}"/>
  </div>
</content>
</body>
</html>
