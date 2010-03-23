<%--
 Created by IntelliJ IDEA.
 User: sbortman
 Date: Sep 14, 2009
 Time: 9:24:47 AM
 To change this template use File | Settings | File Templates.
--%>

<%@ page import="org.ossim.omar.RasterEntryQuery; org.ossim.omar.RasterEntrySearchTag" contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>OMAR - Raster Search</title>
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

  <openlayers:loadMapToolBar/>
  <openlayers:loadTheme theme="default"/>
  <openlayers:loadJavascript/>
  <resource:include components="dateChooser"/>

  <g:javascript src="mapwidget.js"/>
  <g:javascript src="coordinateConversion.js"/>

  <g:javascript>
  var setupBaseLayers = function()
  {
      var baseLayer = null;

      <g:each var="foo" in="${baseWMS}">
    baseLayer = new OpenLayers.Layer.WMS("${foo.title}", "${foo.url}",
    {layers: "${foo.layers}", format: "${foo.format}"},
    {isBaseLayer: true, buffer: 0,transitionEffect: "resize"});

    map.addLayer(baseLayer);
    map.setBaseLayer(baseLayer);
  </g:each>
  };

var raster = new RasterVideo();

  function init()
  {   
      raster.setupMapWidget();
      setupBaseLayers();
      raster.setupDataLayer("${dataWMS.title}", "${dataWMS.url}", "${dataWMS.layers}", "${dataWMS.styles}", "${dataWMS.format}");
      raster.changeMapSize();
      raster.setupAoiLayer();
      raster.setupToolBar();
      raster.setupMapView("${queryParams?.viewMinLon ?: -180}", "${queryParams?.viewMinLat ?: -90}", "${queryParams?.viewMaxLon ?: 180}", "${queryParams?.viewMaxLat ?: 90}");
      raster.setupQueryFields("${queryParams.searchMethod}");
      var numberOfNames = parseInt("${queryParams?.searchTagNames.size()}");
      var numberOfValues = parseInt(${queryParams?.searchTagValues.size()});
      raster.updateOmarFilters($("startDate_day").value, $("startDate_month").value, $("startDate_year").value, $("startDate_hour").value, $("startDate_minute").value, $("endDate_day").value, $("endDate_month").value, $("endDate_year").value, $("endDate_hour").value, $("endDate_minute").value, numberOfNames, numberOfValues);
  }
  </g:javascript>

</head>
<body onload="init( );" onresize="raster.changeMapSize( );">
<content tag="banner">
  <img id="logo" src="${createLinkTo(dir: 'images', file: 'OMARLarge.png', absolute)}" alt="OMAR-2.0 Logo"/>
</content>
<content tag="main">
    <div id="nav" class="nav">
      <span class="menuButton">
        <a class="home" href="${createLinkTo(dir: '')}">Home</a>
      </span>
      <span class="menuButton">
        <a href="javascript:raster.search();">Search</a>
      </span>
      <span class="menuButton">
        <a href="javascript:raster.generateKML();">KML</a>
      </span>
      <span class="menuButton">
        <a href="javascript:raster.updateFootprints( );">Update Footprints</a>
      </span>

      <span class="menuButton">
        Units: <g:select id="unitsMode" name="unitsMode" from="${['DD', 'DMS']}" onChange="raster.setTextFields()"/>
      </span>
    </div>
    <div class="body">
      <h1 id="mapTitle">Search for Imagery:</h1>
      <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
      </g:if>
      <div id="map"></div>
      <div class="niceBox">
        <div class="niceBoxHd">Mouse Position:</div>
        <div class="niceBoxBody">
          <div id="ddCoordinates"></div>
          <div id="dmsCoordinates"></div>
          <div id="utmCoordinates"></div>
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
            <g:radio name="searchMethod" id="radiusSearchButton" value="${RasterEntryQuery.RADIUS_SEARCH}" checked="${queryParams?.searchMethod == RasterEntryQuery.RADIUS_SEARCH}" onclick="raster.toggleRadiusSearch()"/>
            <label>Use Radius Search</label>
          </li>
          <li><br/></li>
          <li>
            <label for='aoiRadius'>Radius in Meters:</label><br/>
          </li>
          <li>
            <g:textField name="aoiRadius" value="${fieldValue(bean: queryParams, field: 'aoiRadius')}" onChange="raster.updateOmarFilters()"/>
          </li>
          <li><br/></li>
          <li>
            <span class="formButton">
              <input type="button" onclick="raster.goto( )" value="Set Center">
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
            <g:radio name="searchMethod" id="bboxSearchButton" value="${RasterEntryQuery.BBOX_SEARCH}" checked="${queryParams?.searchMethod == RasterEntryQuery.BBOX_SEARCH}" onclick="raster.toggleBBoxSearch()"/>
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
            <input type="button" onclick="raster.clearAOI( )" value="Clear AOI">
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
            <richui:dateChooser name="startDate" format="MM/dd/yyyy" timezone="${TimeZone.getTimeZone('UTC')}" style="width:75px" time="true" hourStyle="width:25px" minuteStyle="width:25px" value="${queryParams.startDate}" onChange="raster.updateOmarFilters()"/>
            <g:hiddenField name="startDate_timezone" value="UTC"/>
          </li>
          <li>
            <label for='endDate'>End Date:</label>
          </li>
          <li>
            <richui:dateChooser name="endDate" format="MM/dd/yyyy" timezone="${TimeZone.getTimeZone('UTC')}" style="width:75px" time="true" hourStyle="width:25px" minuteStyle="width:25px" value="${queryParams.endDate}" onChange="raster.updateOmarFilters()"/>
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
                    from="${RasterEntrySearchTag.list()}"
                    optionKey="name" optionValue="description"/>
            </li>
            <li>
              <g:textField name="searchTagValues[${i}]" value="${searchTagValue}" onChange="raster.updateOmarFilters()"/>
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
  </g:form>
</content>
<content tag="contentinfo">
</content>
</body>
</html>