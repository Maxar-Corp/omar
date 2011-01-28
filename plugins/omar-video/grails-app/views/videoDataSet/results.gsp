<%--
  Created by IntelliJ IDEA.
  User: dlucas
  Date: Nov 16, 2010
  Time: 8:09:29 PM
  To change this template use File | Settings | File Templates.
--%>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="resultsView"/>
  <title>Video Search Results</title>

  <g:javascript plugin="omar-core" src="prototype/prototype.js"/>
  <g:javascript>
    var globalActiveIndex=${videoDataSetResultCurrentTab}

    function updateSession(event)
    {
       var link = "${createLink(action: sessionAction, controller: sessionController)}";
       var activeIndex = tabView.get('activeIndex')
       var activeIndexString = null;
       if(activeIndex)
       {
         activeIndexString = activeIndex.toString();
         if(activeIndex != globalActiveIndex)
         {
              globalActiveIndex = activeIndexString;
              new Ajax.Request(link+"?"+"videoDataSetResultCurrentTab="+globalActiveIndex, {method: 'post'});
         }
       }
    };
    var bottomHeight = 66;
    if(${totalCount} == 0)
    {
        bottomHeight = 46;
    }

    function updateOffset()
    {
        var max = document.getElementById("max").value;
        var pages = Math.ceil(${totalCount ?: 0} / max);

        if(document.getElementById("pageOffset").value >= 1 && document.getElementById("pageOffset").value <= pages)
        {
            document.getElementById("offset").value = (document.getElementById("pageOffset").value - 1) * document.getElementById("max").value;
	        document.paginateForm.action = "results";
            document.paginateForm.submit();
        }
        else
        {
            alert("Input must be between 1 and " + pages + ".");
        }
    }
  </g:javascript>

  <resource:tabView/>
</head>

<body>
  <content tag="top">
    <div class="nav">
      <span class="menuButton"><g:link class="home" uri="/">OMAR™ Home</g:link></span>
      <span class="menuButton"><g:link action="search">New Search</g:link></span>
      <span class="menuButton"><a href="${createLink(action: "search", params: params)}">Edit Search</a></span>
    </div>
    <g:form name="paginateForm">
      <g:hiddenField id="totalCount" name="totalCount" value="${totalCount ?: 0}"/>
      <g:hiddenField id="max" name="max" value="${params.max}"/>
      <g:hiddenField id="offset" name="offset" value="${params.offset}"/>
      <g:hiddenField name="queryParams" value="${queryParams.toMap()}"/>
      <g:hiddenField name="order" value="${params.order}"/>
      <g:hiddenField name="sort" value="${params.sort}"/>
    </g:form>

    <div class="paginateButtons">
      <g:paginate controller="videoDataSet" action="results" total="${totalCount ?: 0}" max="${params.max}" offset="${params.offset}" params="${queryParams.toMap()}"/>
      <g:if test="${totalCount == 0}">

      </g:if>
      <g:else>
        <input type="text" id="pageOffset" size="2"/> <input type="button" value="Go to Page" onclick="javascript:updateOffset();"/>
      </g:else>
    </div>
  </content>

  <content tag="body">
    <h1>Video Search Results</h1>
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
                  <td><a href="${createLink(controller: "videoStreaming", action: "show", params: [id: videoDataSet.indexId])}">
                  <img src="${createLink(controller: "thumbnail", action: "frame", params: [id: videoDataSet.indexId, size: 128])}" alt="Show Frame"/></a></td>
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
                <th>Thumbnail</th>
                <g:sortableColumn property="id" title="Id" params="${queryParams.toMap()}"/>
                <th>Filename</th>
              </tr>
              </thead>
              <tbody>
              <g:each in="${videoDataSets}" status="i" var="videoDataSet">
                <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                  <td><a href="${createLink(controller: "videoStreaming", action: "show", params: [id: videoDataSet.indexId])}">
                  <img src="${createLink(controller: "thumbnail", action: "frame", params: [id: videoDataSet.indexId, size: 128])}" alt="Show Frame"/></a></td>
                  <td><g:link controller="videoDataSet" action="show" id="${videoDataSet.id}">${videoDataSet.id?.encodeAsHTML()}</g:link></td>
                  <td>
                    <g:ifAllGranted role="ROLE_DOWNLOAD">
                      <a href=${grailsApplication.config.image.download.prefix}${videoDataSet.mainFile?.name?.encodeAsHTML()}>
                    </g:ifAllGranted>
                    ${videoDataSet.mainFile?.name?.encodeAsHTML()}
                    <g:ifAllGranted role="ROLE_DOWNLOAD">
                      </a>
                    </g:ifAllGranted>
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

</body>
</html>