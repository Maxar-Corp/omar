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
  <title>Raster Search Results</title>
  <style>
  .yui-skin-sam .yui-navset .yui-content {
      background: none repeat scroll 0 0 #FFFFFF;
  }
  #homeMenu{
  background: url( ../images/skin/house.png )  left no-repeat;
     z-index: 99999;
     }
  #exportMenu, #viewMenu, .datechooser{
      z-index: 100;
  }

  </style>

<%--  <resource:tabView/> --%>

</head>

<body class="yui-skin-sam" onload="init();">
<g:javascript plugin="omar-core" src="prototype/prototype.js"/>
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
    <g:paginate event="testing('tabView');" controller="rasterEntry" action="results" total="${totalCount ?: 0}" max="${params.max}" offset="${params.offset}" params="${params}"/>
    <g:if test="${totalCount == 0}">
    </g:if>
    <g:else>
      <input type="text" id="pageOffset" size="3" onchange="updateOffset();"/> <button type="button"  onclick="javascript:updateOffset();">Go to Page</button>
        <label for="max">Max:</label>
        <input type="text" id="max" name="max" value="${params.max}" onChange="updateMaxCount()"/>
        <button type="button"  onclick="javascript:updateMaxCount();">Set</button>
    </g:else>
  </div>

</content>

<content tag="body">
  <h1>Raster Search Results</h1>
  <g:if test="${flash.message}">
    <div class="message">${flash.message}</div>
  </g:if>

  <g:if test="${!rasterEntries}">
    <div class="message">No results found.</div>
  </g:if>

  <div id="demo" class="yui-navset">
    <ul class="yui-nav">
      <g:if test="${rasterEntryResultCurrentTab=='0'}">
        <li class="selected"><a href="#tab1"><em>Image</em></a></li>
      </g:if>
      <g:else>
        <li><a href="#tab1"><em>Image</em></a></li>
      </g:else>

      <g:if test="${rasterEntryResultCurrentTab=='1'}">
        <li class="selected"><a href="#tab2"><em>Metadata</em></a></li>
      </g:if>
      <g:else>
        <li><a href="#tab1"><em>Metadata</em></a></li>
      </g:else>

      <g:if test="${rasterEntryResultCurrentTab=='2'}">
        <li class="selected"><a href="#tab3"><em>File</em></a></li>
      </g:if>
      <g:else>
        <li><a href="#tab1"><em>File</em></a></li>
      </g:else>

      <g:if test="${rasterEntryResultCurrentTab=='3'}">
        <li class="selected"><a href="#tab4"><em>Links</em></a></li>
      </g:if>
      <g:else>
        <li><a href="#tab1"><em>Links</em></a></li>
      </g:else>
    </ul>
    <div class="yui-content">
      <g:if test="${rasterEntryResultCurrentTab=='0'}">
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
              <g:sortableColumn property="entryId" title="Entry Id" params="${queryParams.toMap()}"/>
              <g:sortableColumn property="width" title="Width" params="${queryParams.toMap()}"/>
              <g:sortableColumn property="height" title="Height" params="${queryParams.toMap()}"/>
              <g:sortableColumn property="numberOfBands" title="Bands" params="${queryParams.toMap()}"/>
              <g:sortableColumn property="numberOfResLevels" title="R-Levels" params="${queryParams.toMap()}"/>
              <g:sortableColumn property="bitDepth" title="Bit Depth" params="${queryParams.toMap()}"/>
              <th>Meters Per Pixel</th>
              <th>Min Lon</th>
              <th>Min Lat</th>
              <th>Max Lon</th>
              <th>Max Lat</th>
            </tr>
            </thead>
            <tbody style="overflow:auto">
            <g:each in="${rasterEntries}" status="i" var="rasterEntry">
              <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                <td><a href="${createLink(controller: "mapView", params: [layers: rasterEntry.indexId])}">
                  <img src="${createLink(controller: "thumbnail", action: "show", id: rasterEntry.id, params: [size: 128, projectionType: "imagespace"])}" alt="Show Thumbnail"/></a></td>
                <td><g:link controller="rasterEntry" action="show" id="${rasterEntry.id}">${rasterEntry.id?.encodeAsHTML()}</g:link></td>
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
      </div>
      <g:if test="${rasterEntryResultCurrentTab=='1'}">
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
              <g:sortableColumn property="acquisitionDate" title="Acquisition Date" params="${queryParams.toMap()}"/>
              <g:each in="${(0..<tagHeaderList?.size())}" var="i">
                <g:sortableColumn property="${tagNameList[i]}" title="${tagHeaderList[i]}" params="${queryParams.toMap()}"/>
              </g:each>
            </tr>
            </thead>
            <tbody style="overflow:auto">
            <g:each in="${rasterEntries}" status="i" var="rasterEntry">
              <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                <td><a href="${createLink(controller: "mapView", params: [layers: rasterEntry.indexId])}">
                  <img src="${createLink(controller: "thumbnail", action: "show", id: rasterEntry.id, params: [size: 128, projectionType: "imagespace"])}" alt="Show Thumbnail"/></a></td>
                <td><g:link controller="rasterEntry" action="show" id="${rasterEntry.id}">${rasterEntry.id?.encodeAsHTML()}</g:link></td>
                <td><g:formatDate format="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" timeZone="0" date="${rasterEntry?.acquisitionDate}"/></td>
                <g:each in="${tagNameList}" var="tagName">
                  <g:set var="tag" value='${rasterEntry?.properties[tagName]}'/>
                  <td>${tag?.encodeAsHTML()}</td>
                </g:each>
              </tr>
            </g:each>
            </tbody>
          </table>
        </div>
      </div>
      <g:if test="${rasterEntryResultCurrentTab=='2'}">
        <div id="tab3" style="visibility:visible">
      </g:if>
      <g:else>
        <div id="tab3" style="visibility:hidden">
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
            <tbody style="overflow:auto">
            <g:each in="${rasterEntries}" status="i" var="rasterEntry">
              <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                <td><a href="${createLink(controller: "mapView", params: [layers: rasterEntry.indexId])}">
                  <img src="${createLink(controller: "thumbnail", action: "show", id: rasterEntry.id, params: [size: 128, projectionType: "imagespace"])}" alt="Show Thumbnail"/></a></td>
                <td><g:link controller="rasterEntry" action="show" id="${rasterEntry.id}">${rasterEntry.id?.encodeAsHTML()}</g:link></td>
                <td>
                  <sec:ifAllGranted roles="ROLE_DOWNLOAD">
                    <a href=${grailsApplication.config.image.download.prefix}${rasterEntry.mainFile?.name?.encodeAsHTML()}>
                  </sec:ifAllGranted>
                  ${rasterEntry.mainFile?.name?.encodeAsHTML()}
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
      <g:if test="${rasterEntryResultCurrentTab=='3'}">
        <div id="tab4" style="visibility:visible">
      </g:if>
      <g:else>
        <div id="tab4" style="visibility:hidden">
      </g:else>
        <div class="list">
          <table>
            <thead>
            <tr>
              <th>Thumbnail</th>
              <g:sortableColumn property="id" title="Id" params="${queryParams.toMap()}"/>
              <th>WMS GetCapabilities</th>
              <th>WMS GetMap</th>
              <th>Generate KML</th>
            </tr>
            </thead>
            <tbody style="overflow:auto">
            <g:each in="${rasterEntries}" status="i" var="rasterEntry">
              <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                <td><a href="${createLink(controller: "mapView", params: [layers: rasterEntry.indexId])}">
                  <img src="${createLink(controller: "thumbnail", action: "show", params: [id: rasterEntry.indexId, size: 128, projectionType: "imagespace"])}" alt="Show Thumbnail"/></a></td>
                <td><g:link controller="rasterEntry" action="show" id="${rasterEntry.id}">${rasterEntry.id?.encodeAsHTML()}</g:link></td>
                <td><a href="${createLink(controller: "ogc", action: "wms", params: [request: "GetCapabilities", layers: rasterEntry.indexId])}">WMS GetCapabilities</a></td>
                <td><a href="${createLink(controller: "ogc", action: "wms", params: [request: "GetMap", layers: rasterEntry.indexId, bbox: [rasterEntry?.groundGeom?.bounds?.minLon, rasterEntry?.groundGeom?.bounds?.minLat, rasterEntry?.groundGeom?.bounds?.maxLon, rasterEntry?.groundGeom?.bounds?.maxLat].join(","), srs: "epsg:4326", width: 1024, height: 512, format: "image/jpeg"])}">WMS GetMap</a></td>
                <td><a href="${createLink(controller: "ogc", action: "wms", params: [request: "GetKML", layers: rasterEntry.indexId, format: "image/png", transparent: "true"])}">Generate KML</a></td>
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
  var globalActiveIndex=${rasterEntryResultCurrentTab};
  var Dom = YAHOO.util.Dom;
  var tab1Div = Dom.get("tab1");
  var tab2Div = Dom.get("tab2");
  var tab3Div = Dom.get("tab3");
  var tab4Div = Dom.get("tab4");
  var tabView = new YAHOO.widget.TabView('demo');
  var tab0 = tabView.getTab(0);
  var tab1 = tabView.getTab(1);
  var tab2 = tabView.getTab(2);
  var tab3 = tabView.getTab(3);

    function exportAs(format)
    {
      form = document.getElementById("exportForm");
      if ( format&&form )
      {
        var exportURL = "${createLink(controller: 'rasterEntryExport', action: 'export', params: params)}";

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
        updateCurrentTab("rasterEntryResultCurrentTab", 0);
    }
  }
  function handleClickTab1(e) {
    if(globalActiveIndex != 1)
    {
        updateCurrentTab("rasterEntryResultCurrentTab", 1);
    }
  }
  function handleClickTab2(e) {
    if(globalActiveIndex != 2)
    {
        updateCurrentTab("rasterEntryResultCurrentTab", 2);
    }
  }
  function handleClickTab3(e) {
    if(globalActiveIndex != 3)
    {
        updateCurrentTab("rasterEntryResultCurrentTab", 3);
    }
  }

  function init()
  {
      tab0.addListener('click', handleClickTab0);
      tab1.addListener('click', handleClickTab1);
      tab2.addListener('click', handleClickTab2);
      tab3.addListener('click', handleClickTab3);
      tab1Div.style.visibility = "visible"
      tab2Div.style.visibility = "visible"
      tab3Div.style.visibility = "visible"
      tab4Div.style.visibility = "visible"

      var oMenu = new YAHOO.widget.MenuBar("resultsMenu", {
                                                    autosubmenudisplay: true,
                                                    hidedelay: 750,
                                                    lazyload: true,
                                                    zIndex:9999});
      oMenu.render();

      omarSearchResults.setProperties(${params.encodeAsJSON()});
      omarSearchResults.setProperties(document);

    updatePageOffset();
      //alert(omarSearchResults.toUrlParams());
  }
</g:javascript>
</body>
</html>
