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

 <style>
 .yui-skin-sam .yui-navset .yui-content {
     background: none repeat scroll 0 0 #FFFFFF;
 }

 </style>
</head>

<body class="yui-skin-sam">
<g:javascript plugin="omar-core" src="prototype/prototype.js"/>
<g:javascript>
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

function exportAs()
{
  var formatSelect = document.getElementById("format")
  var format = formatSelect.value;

  if ( format != "null" )
  {
    var exportURL = "${createLink(controller: 'videoDataSetExport', action: 'export', params: params)}";

    exportURL += "&format=" + format;

    //alert(exportURL);

    formatSelect.selectedIndex = 0;
    window.location = exportURL;
  }
}
</g:javascript>
  <content tag="top">
    <div class="nav">
      <span class="menuButton"><g:link class="home" uri="/">OMAR™ Home</g:link></span>
      <span class="menuButton"><g:link action="search">New Search</g:link></span>
      <span class="menuButton"><a href="${createLink(action: "search", params: params)}">Edit Search</a></span>
      <span>
        <g:select name='format' from="['csv', 'shp']"
            noSelection="${['null':'Export As...']}"
            onchange="javascript:exportAs();"></g:select>
      </span>
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

    <div id="demo" class="yui-navset">
      <ul class="yui-nav">
        <g:if test="${videoDataSetResultCurrentTab == '0'}">
          <li class="selected"><a href="#tab1"><em>Video</em></a></li>
        </g:if>
        <g:else>
          <li><a href="#tab1"><em>Video</em></a></li>
        </g:else>
        <g:if test="${videoDataSetResultCurrentTab == '1'}">
          <li class="selected"><a href="#tab2"><em>File</em></a></li>
        </g:if>
        <g:else>
          <li><a href="#tab2"><em>File</em></a></li>
        </g:else>
      </ul>

      <div class="yui-content">
       <g:if test="${videoDataSetResultCurrentTab == '0'}">
         <div id="tab1" style="visibility:visible">
       </g:if>
        <g:else>
          <div id="tab1" style="visibility:hidden">
        </g:else>
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
           </div>
        <g:if test="${videoDataSetResultCurrentTab == '1'}">
          <div id="tab2" style="visibility:visible">
        </g:if>
         <g:else>
           <div id="tab2" style="visibility:hidden">
         </g:else>
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
           </div>
         </div>
    </div>
  </content>
<g:javascript>
  var globalActiveIndex=${videoDataSetResultCurrentTab};
  var Dom = YAHOO.util.Dom;
  var tab1Div = Dom.get("tab1");
  var tab2Div = Dom.get("tab2");
  var tabView = new YAHOO.widget.TabView('demo');
  var tab0 = tabView.getTab(0);
  var tab1 = tabView.getTab(1);

   function updateCurrentTab(tabIndex)
    {
      var link = "${createLink(action: sessionAction, controller: sessionController)}";
      if(tabIndex != globalActiveIndex)
      {
        globalActiveIndex = tabIndex;
        new Ajax.Request(link+"?"+"videoDataSetResultCurrentTab="+globalActiveIndex, {method: 'post'});
      }
    }
  function handleClickTab0(e) {
    updateCurrentTab(0);
  }
  function handleClickTab1(e) {
    updateCurrentTab(1);
  }

  tab0.addListener('click', handleClickTab0);
  tab1.addListener('click', handleClickTab1);
  tab1Div.style.visibility = "visible"
  tab2Div.style.visibility = "visible"
</g:javascript>

</body>
</html>
