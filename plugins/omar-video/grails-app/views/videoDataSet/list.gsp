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
  <title>Video List</title>

  <g:javascript plugin="omar-core" src="prototype/prototype.js"/>
  <g:javascript>
    var globalActiveIndex=${videoDataSetListCurrentTab}

    function updateSession(event)
    {
        var link = "${createLink(action: sessionAction, controller: sessionController)}";
        var activeIndex = tabView.get('activeIndex').toString();

        if(activeIndex != globalActiveIndex)
        {
            globalActiveIndex = activeIndex.toString();
            new Ajax.Request(link+"?"+"videoDataSetListCurrentTab="+activeIndex, {method: 'post'});
        }
    };
  </g:javascript>

  <resource:tabView/>
</head>

<body>
  <content tag="header">
    <div class="nav">
      <ul>
      <li><g:link class="home" uri="/">OMAR™ Home</g:link></li>
      <li><g:link action="search">Search</g:link></li>
      </ul>
    </div>
  </content>

  <content tag="body">
    <h1>Video List</h1>
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
    </g:if>

  <g:if test="${!videoDataSetList}">
    <div class="message">No results found.</div>
  </g:if>


    <richui:tabView id="tabView">
      <omar:observe element="tabView" event="mouseover" function="updateSession"/>
      <richui:tabLabels>
        <g:if test="${videoDataSetListCurrentTab == '0'}">
          <richui:tabLabel title="Video" selected="true"/>
        </g:if>
        <g:else>
          <richui:tabLabel title="Video"/>
        </g:else>
        <g:if test="${videoDataSetListCurrentTab == '1'}">
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
              <g:sortableColumn property="id" title="Id" params="${[repositoryId:params.repositoryId]}"/>
              <g:sortableColumn property="width" title="Width" params="${[repositoryId:params.repositoryId]}"/>
              <g:sortableColumn property="height" title="Height" params="${[repositoryId:params.repositoryId]}"/>
              <g:sortableColumn property="startDate" title="Start Date" params="${[repositoryId:params.repositoryId]}"/>
              <g:sortableColumn property="endDate" title="End Date" params="${[repositoryId:params.repositoryId]}"/>
              <th>Min Lon</th>
              <th>Min Lat</th>
              <th>Max Lon</th>
              <th>Max Lat</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${videoDataSetList}" status="i" var="videoDataSet">
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
              <g:sortableColumn property="id" title="Id" params="${[repositoryId:params.repositoryId]}"/>
              <th>Filename</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${videoDataSetList}" status="i" var="videoDataSet">
              <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                <td><a href="${createLink(controller: "videoStreaming", action: "show", params: [id: videoDataSet.indexId])}">
                <img src="${createLink(controller: "thumbnail", action: "frame", params: [id: videoDataSet.indexId, size: 128])}" alt="Show Frame"/></a></td>
                <td><g:link controller="videoDataSet" action="show" id="${videoDataSet.id}">${videoDataSet.id?.encodeAsHTML()}</g:link></td>
                <td>
                  <sec:ifAllGranted roles="ROLE_DOWNLOAD">
                    <a href=${grailsApplication.config.image.download.prefix}${videoDataSet.mainFile?.name?.encodeAsHTML()}>
                  </sec:ifAllGranted>
                  ${videoDataSet.mainFile?.name?.encodeAsHTML()}
                  <sec:ifAllGranted roles="ROLE_DOWNLOAD">
                    </a>
                  </sec:ifAllGranted>
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

  <content tag="footer">
    <g:form name="paginateForm">
      <g:hiddenField id="offset" name="offset" value="${params.offset}"/>
      <g:hiddenField id="max" name="max" value="${params.max}"/>
    </g:form>

    <div class="paginateButtons">
      <g:paginate total="${videoDataSetList.totalCount}" params="${[repositoryId:params.repositoryId]}"/>
      <g:if test="${videoDataSetList.totalCount == 0}">

      </g:if>
      <g:else>
        <input type="text" id="pageOffset" size="2"/> <input type="button" value="Go to Page" onclick="javascript:updateOffset();"/>
      </g:else>
    </div>
  </content>

 <g:javascript>
   var bottomHeight = 66;
   if(${videoDataSetList.totalCount} == 0)
   {
       bottomHeight = 46; 
   }

   function updateOffset()
   {
       var max = document.getElementById("max").value;
       var pages = Math.ceil(${videoDataSetList.totalCount ?: 0} / max);

       if(document.getElementById("pageOffset").value >= 1 && document.getElementById("pageOffset").value <= pages)
       {
           document.getElementById("offset").value = (document.getElementById("pageOffset").value - 1) * document.getElementById("max").value;
           document.paginateForm.submit();
       }
       else
       {
           alert("Input must be between 1 and " + pages + ".");
       }
   }
   </g:javascript>

</body>
</html>
