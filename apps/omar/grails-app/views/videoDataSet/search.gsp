<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: Sep 14, 2009
  Time: 9:24:47 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="org.ossim.omar.VideoDataSetQuery; org.ossim.omar.VideoDataSetSearchTag" contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>OMAR - Video Search</title>
  <meta name="layout" content="main3"/>

  <style type="text/css">
  #map {
    border: 1px solid black;
    width: 100%;
    height: 100%;
  }

  #controls {
    padding-left: 2em;
    margin-left: 0;
    width: 12em;
  }

  #controls li {
    padding-top: 0.5em;
    list-style: none;
  }

  #panel {
    right: 0px;
    height: 30px;
    width: 200px;
  }

  #panel div {
    float: left;
    margin: 5px;
  }
  </style>


  <openlayers:loadTheme theme="default"/>
  <openlayers:loadMapToolBar/>
  <openlayers:loadJavascript/>
  <resource:include components="dateChooser"/>

  <g:javascript src="mapwidget.js"/>
  <g:javascript src="coordinateConversion.js"/>

  <g:javascript>

   function setupBaseLayer()
   {
     var baseLayer = null;

     <g:each var="foo" in="${baseWMS}">
     baseLayer = new OpenLayers.Layer.WMS(
       "${foo.title}",
       "${foo.url}",
       {layers: '${foo.layers}', format: "${foo.format}" },
        {isBaseLayer:true, buffer:0,transitionEffect: "resize"}
     );
     map.addLayer(baseLayer);
     map.setBaseLayer(baseLayer);
     </g:each>
   }



    var video = new RasterVideo();




    function init()
    {
       video.setupMapWidget();
      setupBaseLayer();
      video.setupDataLayer("${dataWMS.title}", "${dataWMS.url}", "${dataWMS.layers}", "${dataWMS.styles}", "${dataWMS.format}");
      video.changeMapSize();
      video.setupAoiLayer();
      video.setupToolBar();
      video.setupMapView("${queryParams?.viewMinLon ?: -180}", "${queryParams?.viewMinLat ?: -90}", "${queryParams?.viewMaxLon ?: 180}", "${queryParams?.viewMaxLat ?: 90}");
      video.setupQueryFields("${queryParams.searchMethod}");
      var numberOfNames = parseInt("${queryParams?.searchTagNames.size()}");
      var numberOfValues = parseInt(${queryParams?.searchTagValues.size()});
      video.updateOmarFilters($("startDate_day").value, $("startDate_month").value, $("startDate_year").value, $("startDate_hour").value, $("startDate_minute").value, $("endDate_day").value, $("endDate_month").value, $("endDate_year").value, $("endDate_hour").value, $("endDate_minute").value, numberOfNames, numberOfValues);
    }
  </g:javascript>

</head>
<body onload="init( )" class="yui-skin-sam" onresize="changeMapSize( );">
<content tag="banner">
  <img id="logo" src="${createLinkTo(dir: 'images', file: 'OMARLarge.png', absolute)}" alt="OMAR-2.0 Logo"/>
</content>
<content tag="main">
  <div id="nav" class="nav">
    <span class="menuButton">
      <a class="home" href="${createLinkTo(dir: '')}">Home</a>
    </span>
    <span class="menuButton">
      <a href="javascript:video.search();">Search</a>
    </span>
    <span class="menuButton">
      <a href="javascript:video.generateKML();">KML</a>
    </span>
    <span class="menuButton">
      <a href="javascript:video.updateFootprints();">Update Footprints</a>
    </span>
    <span class="menuButton">
      Units: <g:select id="unitsMode" name="unitsMode" from="${['DD', 'DMS']}" onChange="video.setTextFields()"/>
    </span>
  </div>
  <div class="body">
    <h1 id="mapTitle">Search for Video:</h1>
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
    </g:if>
    <div id="map"></div>
    <div class="niceBox">
      <div class="niceBoxHd">Mouse Position:</div>
      <div class="niceBoxBody">
        <table>
          <tr>
            <td width=200><div id="ddCoordinates"></div></td>
            <td width=200><div id="dmsCoordinates"></div></td>
            <td width=200><div id="utmCoordinates"></div></td>
          </tr>
        </table>
      </div>
    </div>
  </div>
</content>
<content tag="search">
  <g:form name="searchForm">
    <div class="niceBox">
      <div class="niceBoxHd">Map Center:</div>
      <div class="niceBoxBody">
        <ol>
          <li>
            <label for='centerLat'>Lat:</label>
          </li>
          <li>
            <g:textField name="centerLat" value="${queryParams?.centerLat}"/>
          </li>
          <li>
            <label for='centerLon'>Lon:</label><br/>
          </li>
          <li>
            <g:textField name="centerLon" value="${queryParams?.centerLon}"/>
          </li>
          <li><br/></li>
          <li>
            <g:radio name="searchMethod" value="${VideoDataSetQuery.RADIUS_SEARCH}" checked="${queryParams?.searchMethod == VideoDataSetQuery.RADIUS_SEARCH}" onclick="video.toggleRadiusSearch()"/>
            <label>Use Radius Search</label>
          </li>
          <li><br/></li>
          <li>
            <label for='aoiRadius'>Radius in Meters:</label><br/>
          </li>
          <li>
            <g:textField name="aoiRadius" value="${fieldValue(bean: queryParams, field: 'aoiRadius')}"/>
          </li>
          <li><br/></li>
          <li>
            <span class="formButton">
              <input type="button" onclick="video.goto( )" value="Set Center">
            </span>
          </li>
        </ol>
      </div>
    </div>

    <div class="niceBox">
      <div class="niceBoxHd">Geospatial Criteria:</div>
      <div class="niceBoxBody">
        <input type="hidden" id="viewMinLon" name="viewMinLon" value="${fieldValue(bean: queryParams, field: 'viewMinLon')}"/>
        <input type="hidden" id="viewMinLat" name="viewMinLat" value="${fieldValue(bean: queryParams, field: 'viewMinLat')}"/>
        <input type="hidden" id="viewMaxLon" name="viewMaxLon" value="${fieldValue(bean: queryParams, field: 'viewMaxLon')}"/>
        <input type="hidden" id="viewMaxLat" name="viewMaxLat" value="${fieldValue(bean: queryParams, field: 'viewMaxLat')}"/>
        <ol>
          <li>
            <g:radio name="searchMethod" value="${VideoDataSetQuery.BBOX_SEARCH}" checked="${queryParams?.searchMethod == VideoDataSetQuery.BBOX_SEARCH}" onclick="video.toggleBBoxSearch()"/>
            <label>Use BBox Search</label>
          </li>
          <li><br/></li>
          <li>
            <label for='aoiMaxLat'>North Lat:</label>
          </li>
          <li>
            <input type="text" id="aoiMaxLat" name="aoiMaxLat" value="${fieldValue(bean: queryParams, field: 'aoiMaxLat')}"/>
          </li>
          <li>
            <label for='aoiMinLon'>West Lon:</label>
          </li>
          <li>
            <input type="text" id="aoiMinLon" name="aoiMinLon" value="${fieldValue(bean: queryParams, field: 'aoiMinLon')}"/>
          </li>
          <li>
            <label for='aoiMinLat'>South Lat:</label>
          </li>
          <li>
            <input type="text" id="aoiMinLat" name="aoiMinLat" value="${fieldValue(bean: queryParams, field: 'aoiMinLat')}"/>
          </li>
          <li>
            <label for='aoiMaxLon'>East Lon:</label>
          </li>
          <li>
            <input type="text" id="aoiMaxLon" name="aoiMaxLon" value="${fieldValue(bean: queryParams, field: 'aoiMaxLon')}"/>
          </li>
          <li><br/></li>
          <li>
            <input type="button" onclick="video.clearAOI( )" value="Clear AOI">
          </li>
        </ol>
      </div>
    </div>

    <div class="niceBox">
      <div class="niceBoxHd">Temporal Criteria:</div>
      <div class="niceBoxBody">
        <ol>
          <li>
            <label for='startDate'>Start Date:</label>
          </li>
          <li>
            <richui:dateChooser name="startDate" format="MM/dd/yyyy" timezone="${TimeZone.getTimeZone('UTC')}" style="width:75px" time="true" hourStyle="width:25px" minuteStyle="width:25px" value="${queryParams.startDate}" onChange="video.updateOmarFilters()"/>
            <g:hiddenField name="startDate_timezone" value="UTC"/>
          </li>
          <li>
            <label for='endDate'>End Date:</label>
          </li>
          <li>
            <richui:dateChooser name="endDate" format="MM/dd/yyyy" timezone="${TimeZone.getTimeZone('UTC')}" style="width:75px" time="true" hourStyle="width:25px" minuteStyle="width:25px" value="${queryParams.endDate}" onChange="video.updateOmarFilters()"/>
            <g:hiddenField name="endDate_timezone" value="UTC"/>
          </li>
        </ol>
      </div>
    </div>
    
    <div class="niceBox">
      <div class="niceBoxHd">Metadata Criteria:</div>
      <div class="niceBoxMetadataBody">
        <ol>
          <g:each in="${queryParams?.searchTagValues}" var="searchTagValue" status="i">
            <g:select
                    noSelection="${['null':'Select One...']}"
                    name="searchTagNames[${i}]"
                    value="${queryParams?.searchTagNames[i]}"
                    from="${VideoDataSetSearchTag.list()}"
                    optionKey="name" optionValue="description"/>
            </li>
            <li>
              <g:textField name="searchTagValues[${i}]" value="${searchTagValue}" onChange="video.updateOmarFilters()"/>
            </li>
          </g:each>
        </ol>
      </div>
    </div>

    <div class="niceBox">
      <div class="niceBoxHd">Options:</div>
      <div class="niceBoxBody">
        <ol>
          <li><label for="max">Max Results:</label></li>
          <li><input type="text" id="max" name="max" value="${params?.max}"></li>
        </ol>
      </div>
    </div>
    <br/>
  <%--
  <g:actionSubmit value="Search"/>&nbsp;<g:actionSubmit action="kmlnetworklink" value="KML"/>
  --%>
  </g:form>
</content>
<content tag="contentinfo">
</content>
</body>
</html>
