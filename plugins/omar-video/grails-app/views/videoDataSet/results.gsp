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
  <title>OMAR <g:meta name="app.version"/>: Video Search Results</title>

 <style>
 .yui-skin-sam .yui-navset .yui-content {
     background: none repeat scroll 0 0 #FFFFFF;
 }
 #homeMenu{
 background: url( ../images/skin/house.png )  left no-repeat;
    z-index: 99999;
    }
 #exportMenu, #searchMenu{
     z-index: 100;
 }

 </style>
</head>

<body class="yui-skin-sam" onload="init();">
<g:javascript plugin="omar-core" src="prototype/prototype.js"/>
<g:javascript>
</g:javascript>
  <content tag="top">
    <g:form name="paginateForm" method="post">
    </g:form>
    <g:form name="exportForm" method="post">
    </g:form>
    <div id="resultsMenu" class="yuimenubar yuimenubarnav">
        <div class="bd">
            <ul class="first-of-type">
                <li class="yuimenubaritem first-of-type"><a class="yuimenubaritemlabel" id="homeMenu" href="${createLink(controller: 'home', action: 'index')}" title="OMAR™ Home">&nbsp;&nbsp;&nbsp;&nbsp;OMAR™ Home</a>
                </li>
                <li class="yuimenubaritem first-of-type"><a class="yuimenubaritemlabel" id="Search" href="#searchMenu" title="Search">Search</a>
                    <div id="searchMenu" class="yuimenu">
                         <div class="bd">
                             <ul>
                                 <li class="yuimenuitem"><a class="yuimenuitemlabel" href="${createLink(action: 'search')}" title="New Search">New</a></li>
                                 <li class="yuimenuitem"><a class="yuimenuitemlabel" href="${createLink(action: "search", params: params)}" title="Edit Search">Edit</a></li>
                             </ul>
                           </div>
                     </div>
                </li>
                <li class="yuimenubaritem first-of-type"><a class="yuimenubaritemlabel" href="#exportMenu">Export</a>
                    <div id="exportMenu" class="yuimenu">
                        <div class="bd">
                            <ul>
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="javascript:exportAs('csv')" title="Export Csv">Csv File</a></li>
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="javascript:exportAs('shp')" title="Export Shape">Shape File</a></li>
                            </ul>
                          </div>
                    </div>
                </li>
            </ul>
        </div>
    </div>
      <g:hiddenField id="totalCount" name="totalCount" value="${totalCount ?: 0}"/>
      <g:hiddenField id="offset" name="offset" value="${params.offset}"/>
      <g:hiddenField name="queryParams" value="${queryParams.toMap()}"/>
      <g:hiddenField name="order" value="${params.order}"/>
      <g:hiddenField name="sort" value="${params.sort}"/>

    <div class="paginateButtons">
      <g:paginate controller="videoDataSet" action="results" total="${totalCount ?: 0}" max="${params.max}" offset="${params.offset}" params="${queryParams.toMap()}"/>
      <g:if test="${totalCount == 0}">

      </g:if>
      <g:else>
          <input type="text" id="pageOffset" size="3" onchange="updateOffset();"/> <button type="button"  onclick="javascript:updateOffset();">Go to Page</button>
          <label for="max">Max:</label>
          <input type="text" id="max" name="max"  value="${params.max}" onChange="updateMaxCount()"/>
          <button type="button"  onclick="javascript:updateMaxCount();">Set</button>
      </g:else>
    </div>
  </content>

  <content tag="body">
    <h1>Video Search Results</h1>
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
    </g:if>

  <g:if test="${!videoDataSets}">
    <div class="message">No results found.</div>
  </g:if>

    <div id="resultTab" class="yui-navset">
      <ul class="yui-nav">
        <g:if test="${videoDataSetResultCurrentTab == '0'}">
          <li class="selected"><a href="#resultTab1"><em>Video</em></a></li>
        </g:if>
        <g:else>
          <li><a href="#resultTab1"><em>Video</em></a></li>
        </g:else>
          <g:if test="${videoDataSetResultCurrentTab == '1'}">
            <li class="selected"><a href="#resultTab2"><em>File</em></a></li>
          </g:if>
          <g:else>
            <li><a href="#resultTab2"><em>File</em></a></li>
          </g:else>
          <g:if test="${videoDataSetResultCurrentTab == '2'}">
            <li class="selected"><a href="#resultTab3"><em>Links</em></a></li>
          </g:if>
          <g:else>
            <li><a href="#resultTab3"><em>Links</em></a></li>
          </g:else>
      </ul>

      <div class="yui-content">
       <g:if test="${videoDataSetResultCurrentTab == '0'}">
         <div id="resultTab1" style="visibility:visible">
       </g:if>
        <g:else>
          <div id="resultTab1" style="visibility:hidden">
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
                   <td height="${videoDataSet.height/(Math.max(videoDataSet.width, videoDataSet.height)/128.0)}"><a href="${createLink(controller: "videoStreaming", action: "show", params: [id: videoDataSet.indexId])}">
                   <img src="${createLink(controller: "thumbnail", action: "frame", params: [id: videoDataSet.id, size: 128])}" alt="Show Frame"/></a></td>
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
          <div id="resultTab2" style="visibility:visible">
        </g:if>
         <g:else>
           <div id="resultTab2" style="visibility:hidden">
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
                       <td height="${videoDataSet.height/(Math.max(videoDataSet.width, videoDataSet.height)/128.0)}"><a href="${createLink(controller: "videoStreaming", action: "show", params: [id: videoDataSet.indexId])}">
                     <img src="${createLink(controller: "thumbnail", action: "frame", params: [id: videoDataSet.id, size: 128])}" alt="Show Frame"/></a></td>
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
          <g:if test="${videoDataSetResultCurrentTab == '2'}">
            <div id="resultTab3" style="visibility:visible">
          </g:if>
           <g:else>
             <div id="resultTab3" style="visibility:hidden">
           </g:else>
           <div class="list">
             <table>
               <thead>
               <tr>
                   <th>Thumbnail</th>
                   <g:sortableColumn property="id" title="Id" params="${queryParams.toMap()}"/>
                   <th>Generate KML</th>
               </tr>
               </thead>
               <tbody>
               <g:each in="${videoDataSets}" status="i" var="videoDataSet">
                  <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                      <td height="${videoDataSet.height/(Math.max(videoDataSet.width, videoDataSet.height)/128.0)}"><a href="${createLink(controller: "videoStreaming", action: "show", params: [id: videoDataSet.indexId])}">
                    <img src="${createLink(controller: "thumbnail", action: "frame", params: [id: videoDataSet.id, size: 128])}" alt="Show Frame"/></a></td>
                    <td><g:link controller="videoDataSet" action="show" id="${videoDataSet.id}">${videoDataSet.id?.encodeAsHTML()}</g:link></td>
                    <td>
                        <a href='${createLink(controller: 'videoStreaming', action: "getKML", id: videoDataSet.id)}'>Generate KML</a>
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
  var tab1Div = Dom.get("resultTab1");
  var tab2Div = Dom.get("resultTab2");
  var tab3Div = Dom.get("resultTab3");
  var tabView = new YAHOO.widget.TabView('resultTab');
  var tab0 = tabView.getTab(0);
  var tab1 = tabView.getTab(1);
  var tab2 = tabView.getTab(2);

    function exportAs(format)
    {
      form = document.getElementById("exportForm");
      if ( format&&form )
      {
        var exportURL = "${createLink(controller: 'videoDataSetExport', action: 'export', params: params)}";

        exportURL += "&format=" + format;

        //alert(exportURL);

        form.action = exportURL;
        form.submit();
      }
    }
   function updateCurrentTab(variable, tabIndex)
  {
      var link = "${createLink(action: sessionAction, controller: sessionController)}";
      new Ajax.Request(link+"?"+variable+"="+tabIndex, {method: 'post'});
  }

  function handleClickTab0(e) {
    if(globalActiveIndex != 0)
    {
        globalActiveIndex = 0;
        updateCurrentTab("videoDataSetResultCurrentTab", 0);
    }
  }
  function handleClickTab1(e) {
    if(globalActiveIndex != 1)
    {
        globalActiveIndex = 1;
        updateCurrentTab("videoDataSetResultCurrentTab", 1);
    }
  }
  function handleClickTab2(e) {
    if(globalActiveIndex != 2)
    {
        globalActiveIndex = 2;
        updateCurrentTab("videoDataSetResultCurrentTab", 2);
    }
  }

  function init()
  {
      var oMenu = new YAHOO.widget.MenuBar("resultsMenu", {
                                                    autosubmenudisplay: true,
                                                    hidedelay: 750,
                                                    showdelay: 0,
                                                    lazyload: true,
                                                    zIndex:9999});
      oMenu.render();
      tab0.addListener('click', handleClickTab0);
      tab1.addListener('click', handleClickTab1);
      tab2.addListener('click', handleClickTab2);
      tab1Div.style.visibility = "visible"
      tab2Div.style.visibility = "visible"
      tab3Div.style.visibility = "visible"


      omarSearchResults.setProperties(${params.encodeAsJSON()});
      omarSearchResults.setProperties(document);

      updatePageOffset();

  }
</g:javascript>

</body>
</html>
