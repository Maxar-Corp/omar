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
  <title>Raster List</title>

  <g:javascript plugin="omar-core" src="prototype/prototype.js"/>
  <g:javascript>
    var globalActiveIndex=${rasterEntryListCurrentTab}

    function updateSession(event)
    {
        var link = "${createLink(action: sessionAction, controller: sessionController)}";
        var activeIndex = tabView.get('activeIndex').toString();

        if(activeIndex != globalActiveIndex)
        {
            globalActiveIndex = activeIndex.toString();
            new Ajax.Request(link+"?"+"rasterEntryListCurrentTab="+activeIndex, {method: 'post'});
        }
    };
  </g:javascript>

  <resource:tabView/>
</head>

<body>
  <content tag="header">
    <div class="nav">
        <ul>
            <li class="menuButton"><g:link class="home" uri="/">OMAR™ Home</g:link></li>
            <li class="menuButton"><g:link action="search">Search</g:link></li>
        </ul>
    </div>
  </content>

  <content tag="body">
    <h1>Raster List</h1>
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
    </g:if>

  <g:if test="${!rasterEntryList}">
    <div class="message">No results found.</div>
  </g:if>

    <richui:tabView id="tabView">
      <omar:observe element="tabView" event="mouseover" function="updateSession"/>
      <richui:tabLabels>
        <g:if test="${rasterEntryListCurrentTab == '0'}">
          <richui:tabLabel selected="true" title="Image"/>
        </g:if>
        <g:else>
          <richui:tabLabel title="Image"/>
        </g:else>
        <g:if test="${rasterEntryListCurrentTab == '1'}">
          <richui:tabLabel selected="true" title="Metadata"/>
        </g:if>
        <g:else>
          <richui:tabLabel title="Metadata"/>
        </g:else>
        <g:if test="${rasterEntryListCurrentTab == '2'}">
          <richui:tabLabel selected="true" title="File"/>
        </g:if>
        <g:else>
          <richui:tabLabel title="File"/>
        </g:else>
        <g:if test="${rasterEntryListCurrentTab == '3'}">
          <richui:tabLabel selected="true" title="Links"/>
        </g:if>
        <g:else>
          <richui:tabLabel title="Links"/>
        </g:else>
      </richui:tabLabels>
      <richui:tabContents>
        <richui:tabContent>
          <div class="list">
            <table>
              <thead>
              <tr>
                <th>Thumbnail</th>
                <g:sortableColumn property="id" title="Id" params="${[rasterDataSetId:params.rasterDataSetId]}"/>
                <g:sortableColumn property="entryId" title="Entry Id" params="${[rasterDataSetId:params.rasterDataSetId]}"/>
                <g:sortableColumn property="width" title="Width" params="${[rasterDataSetId:params.rasterDataSetId]}"/>
                <g:sortableColumn property="height" title="Height" params="${[rasterDataSetId:params.rasterDataSetId]}"/>
                <g:sortableColumn property="numberOfBands" title="Bands" params="${[rasterDataSetId:params.rasterDataSetId]}"/>
                <g:sortableColumn property="numberOfResLevels" title="R-Levels" params="${[rasterDataSetId:params.rasterDataSetId]}"/>
                <g:sortableColumn property="bitDepth" title="Bit Depth" params="${[rasterDataSetId:params.rasterDataSetId]}"/>
                <th>Meters Per Pixel</th>
                <th>Min Lon</th>
                <th>Min Lat</th>
                <th>Max Lon</th>
                <th>Max Lat</th>
              </tr>
              </thead>
              <tbody style="overflow:auto">
              <g:each in="${rasterEntryList}" status="i" var="rasterEntry">
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
        </richui:tabContent>
        <richui:tabContent>
          <div class="list">
            <table>
              <thead>
              <tr>
                <th>Thumbnail</th>
                <g:sortableColumn property="id" title="Id" params="${[rasterDataSetId:params.rasterDataSetId]}"/>
                <g:sortableColumn property="acquisitionDate" title="Acquisition Date" params="${[rasterDataSetId:params.rasterDataSetId]}"/>
                <g:each in="${(0..<tagHeaderList?.size())}" var="i">
                  <g:sortableColumn property="${tagNameList[i]}" title="${tagHeaderList[i]}" params="${[rasterDataSetId:params.rasterDataSetId]}"/>
                </g:each>
              </tr>
              </thead>
              <tbody style="overflow:auto">
              <g:each in="${rasterEntryList}" status="i" var="rasterEntry">
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
        </richui:tabContent>
        <richui:tabContent>
          <div class="list">
            <table>
              <thead>
              <tr>
                <th>Thumbnail</th>
                <g:sortableColumn property="id" title="Id" params="${[rasterDataSetId:params.rasterDataSetId]}"/>
                <th>Filename</th>
              </tr>
              </thead>
              <tbody style="overflow:auto">
              <g:each in="${rasterEntryList}" status="i" var="rasterEntry">
                <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                  <td><a href="${createLink(controller: "mapView", params: [layers: rasterEntry.indexId])}">
                  <img src="${createLink(controller: "thumbnail", action: "show", id: rasterEntry.id, params: [size: 128, projectionType: "imagespace"])}" alt="Show Thumbnail"/></a></td>
                  <td><g:link controller="rasterEntry" action="show" id="${rasterEntry.id}">${rasterEntry.id?.encodeAsHTML()}</g:link></td>
                  <g:set var="rasterFile" value="${rasterEntry.mainFile}"/>
                  <td>
                    <sec:ifAllGranted roles="ROLE_DOWNLOAD">
                      <a href=${grailsApplication.config.image.download.prefix}${rasterFile?.name?.encodeAsHTML()}>
                    </sec:ifAllGranted>
                    ${rasterFile?.name?.encodeAsHTML()}
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
        <richui:tabContent>
          <div class="list">
            <table>
              <thead>
              <tr>
                <th>Thumbnail</th>
                <g:sortableColumn property="id" title="Id" params="${[rasterDataSetId:params.rasterDataSetId]}"/>
                <th>WMS GetCapabilities</th>
                <th>WMS GetMap</th>
                <th>Generate KML</th>
              </tr>
              </thead>
              <tbody style="overflow:auto">
                <g:each in="${rasterEntryList}" status="i" var="rasterEntry">
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
        </richui:tabContent>
      </richui:tabContents>
    </richui:tabView>
  </content>

  <content tag="footer">
    <g:form name="paginateForm">
      <g:hiddenField id="offset" name="offset" value="${params.offset}"/>
      <g:hiddenField id="max" name="max" value="${params.max}"/>
    </g:form>

    <div class="pagination">
      <g:paginate total="${rasterEntryList.totalCount}" params="${[rasterDataSet:params.rasterDataSetId]}"/>
      <g:if test="${rasterEntryList.totalCount == 0}">

      </g:if>
      <g:else>
        <input type="text" id="pageOffset" size="2"/> <input type="button" value="Go to Page" onclick="javascript:updateOffset();"/>
      </g:else>
    </div>
  </content>

 <g:javascript>
    var bottomHeight = 66;
    if(${rasterEntryList.totalCount} == 0)
    {
        bottomHeight = 46;
    }

    function updateOffset()
    {
        var max = document.getElementById("max").value;
        var pages = Math.ceil(${rasterEntryList.totalCount ?: 0} / max);

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
