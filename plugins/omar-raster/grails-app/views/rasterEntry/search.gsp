<%--
 Created by IntelliJ IDEA.
 User: sbortman
 Date: Sep 14, 2009
 Time: 9:24:47 AM
 To change this template use File | Settings | File Templates.
--%>

<%@ page import="org.ossim.omar.BaseQuery; org.ossim.omar.RasterEntryQuery; org.ossim.omar.RasterEntrySearchTag" contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>OMAR - Raster Search</title>
  <meta name="layout" content="main9"/>

  <meta name="apple-mobile-web-app-capable" content="yes"/>
  <meta name="apple-mobile-web-app-status-bar-style" content="black"/>
  <meta name="viewport" content="minimum-scale=1.0, width=device-width, maximum-scale=1.6, user-scalable=no">

  <openlayers:loadMapToolBar/>
  <openlayers:loadTheme theme="default"/>
  <openlayers:loadJavascript />
  
  <resource:include components="dateChooser, tabView"/>

  <style type="text/css">
    td {
      font-size: 10px;
    }  

    div.niceBox {
      background-color: #F0F8FF;
      border: 1px solid #000000;
    }

    div.niceBoxHd {
      background-color: #003366;
    }
  </style>
</head>

<body class="yui-skin-sam" onLoad="init();">

<content tag="top">

  <div id="linkbuttonsfromjavascript"></div>

</content>

<content tag="left">
  <g:form name="searchForm">

    <richui:tabView id="demo">
      <richui:tabLabels>
        <richui:tabLabel title="DD" selected="true" />
        <richui:tabLabel title="DMS" />
        <richui:tabLabel title="MGRS" />
      </richui:tabLabels>

      <richui:tabContents>

        <richui:tabContent>
          <div class="niceBox">
            <div class="niceBoxHd">Map Center:</div>
              <div class="niceBoxBody">
                <ol>
                  <li>
                    <g:checkBox name="searchMethod" id="radiusSearchButton" value="${BaseQuery.RADIUS_SEARCH}" checked="${queryParams?.searchMethod == BaseQuery.RADIUS_SEARCH}" onclick="mapWidget.togglePointRadiusCheckBox()" />
                    <label>Use Radius Search</label>
                  </li>
                  <li><br/></li>
                  <li>
                    <label for="centerLat">Latitude:</label>
                  </li>
                  <li>
                    <g:textField name="centerLat" value="${queryParams?.centerLat}" />
                  </li>
                  <li>
                    <label for="centerLon">Longitude:</label>
                  </li>
                  <li>
                    <g:textField name="centerLon" value="${queryParams?.centerLon}" />
                  </li>
                  <li><br/></li>
                  <li>
                    <label for="aoiRadius">Radius in Meters:</label>
                  </li>
                  <li>
                    <g:textField name="aoiRadius" value="${fieldValue(bean: queryParams, field: 'aoiRadius')}" onChange="updateOmarFilters()" />
                  </li>
                  <li><br/></li>
                  <li>
                    <span class="formButton">                                        
                      <input type="button" onclick="mapWidget.setCenterDd()" value="Set Center">
                    </span>
                  </li>
                </ol>
              </div>
          </div>
          <div class="niceBox">
            <div class="niceBoxHd">Geospatial Criteria:</div>
              <div class="niceBoxBody">
                <input type="hidden" id="viewMinLon" name="viewMinLon" value="${fieldValue(bean: queryParams, field: 'viewMinLon')}" />
                <input type="hidden" id="viewMinLat" name="viewMinLat" value="${fieldValue(bean: queryParams, field: 'viewMinLat')}" />
                <input type="hidden" id="viewMaxLon" name="viewMaxLon" value="${fieldValue(bean: queryParams, field: 'viewMaxLon')}" />
                <input type="hidden" id="viewMaxLat" name="viewMaxLat" value="${fieldValue(bean: queryParams, field: 'viewMaxLat')}" />
                <ol>
                  <li>
                    <g:checkBox name="searchMethod" id="bboxSearchButton" value="${BaseQuery.BBOX_SEARCH}" checked="${queryParams?.searchMethod == BaseQuery.BBOX_SEARCH}" onclick="mapWidget.toggleBboxCheckBox()" />
                    <label>Use BBox Search</label>
                  </li>
                  <li><br/></li>
                  <li>
                    <label for="aoiMaxLat">North Latitude:</label>
                  </li>
                  <li>
                    <input type="text" id="aoiMaxLat" name="aoiMaxLat" value="${fieldValue(bean: queryParams, field: 'aoiMaxLat')}" />
                  </li>
                  <li>
                    <label for="aoiMinLon">West Longitude:</label>
                  </li>
                  <li>
                    <input type="text" id="aoiMinLon" name="aoiMinLon" value="${fieldValue(bean: queryParams, field: 'aoiMinLon')}" />
                  </li>
                  <li>
                    <label for="aoiMinLat">South Latitude:</label>
                  </li>
                  <li>
                    <input type="text" id="aoiMinLat" name="aoiMinLat" value="${fieldValue(bean: queryParams, field: 'aoiMinLat')}" />
                  </li>
                  <li>
                    <label for="aoiMaxLon">East Longitude:</label>
                  </li>
                  <li>
                    <input type="text" id="aoiMaxLon" name="aoiMaxLon" value="${fieldValue(bean: queryParams, field: 'aoiMaxLon')}" />
                  </li>
                  <li><br/></li>
                  <li>
                    <span class="formButton">
                      <input type="button" onclick="mapWidget.clearAOI()" value="Clear AOI">
                    </span>
                  </li>
                </ol>
              </div>
          </div>
        </richui:tabContent>

        <richui:tabContent>
          <div class="niceBox">
            <div class="niceBoxHd">Map Center:</div>
            <div class="niceBoxBody">
              <ol>
                <li>
                  <g:checkBox name="searchMethod2" id="radiusSearchButton2" value="${BaseQuery.RADIUS_SEARCH}" checked="${queryParams?.searchMethod == BaseQuery.RADIUS_SEARCH}" onclick="mapWidget.togglePointRadiusCheckBox()" />
                  <label>Use Radius Search</label>
                </li>
                <li><br/></li>
                <li>
                  <label for="centerLat">Latitude:</label>
                </li>
                <li>
                  <g:textField name="centerLatDms" value="" />
                </li>
                <li>
                  <label for="centerLon">Longitude:</label>
                </li>
                <li>
                  <g:textField name="centerLonDms" value="" />
                </li>
                <li><br/></li>
                <li>
                  <label for="aoiRadius">Radius in Meters:</label>
                </li>
                <li>
                  <g:textField name="aoiRadius2" />
                </li>
                <li><br/></li>
                <li>
                  <span class="formButton">
                    <input type="button" onclick="mapWidget.setCenterDms()" value="Set Center">
                  </span>
                </li>
              </ol>
            </div>
          </div>
          <div class="niceBox">
            <div class="niceBoxHd">Geospatial Criteria:</div>
            <div class="niceBoxBody">
              <ol>
                <li>
                  <g:checkBox name="searchMethod2" id="bboxSearchButton2" value="${BaseQuery.BBOX_SEARCH}" checked="${queryParams?.searchMethod == BaseQuery.BBOX_SEARCH}" onclick="mapWidget.toggleBboxCheckBox()" />
                  <label>Use BBox Search</label>
                </li>
                <li><br/></li>
                <li>
                  <label for="aoiMaxLat">North Latitude:</label>
                </li>
                <li>
                  <input type="text" id="aoiMaxLatDms" name="aoiMaxLatDms" />
                </li>
                <li>
                  <label for="aoiMinLon">West Longitude:</label>
                </li>
                <li>
                  <input type="text" id="aoiMinLonDms" name="aoiMinLonDms" />
                </li>
                <li>
                  <label for="aoiMinLat">South Latitude:</label>
                </li>
                <li>
                  <input type="text" id="aoiMinLatDms" name="aoiMinLatDms" />
                </li>
                <li>
                  <label for="aoiMaxLon">East Longitude:</label>
                </li>
                <li>
                  <input type="text" id="aoiMaxLonDms" name="aoiMaxLonDms" />
                </li>
                <li><br/></li>
                <li>
                  <span class="formButton">
                    <input type="button" onclick="mapWidget.clearAOI()" value="Clear AOI">
                  </span>
                </li>
              </ol>
            </div>
          </div>
        </richui:tabContent>

        <richui:tabContent>
          <div class="niceBox">
            <div class="niceBoxHd">Map Center:</div>
            <div class="niceBoxBody">
              <ol>
                <li>
                  <g:checkBox name="searchMethod3" id="radiusSearchButton3" value="${BaseQuery.RADIUS_SEARCH}" checked="${queryParams?.searchMethod == BaseQuery.RADIUS_SEARCH}" onclick="mapWidget.togglePointRadiusCheckBox()" />
                  <label>Use Radius Search</label>
                </li>
                <li><br/></li>
                <li>
                  <label for="centerLat">MGRS:</label>
                </li>
                <li>
                  <g:textField name="centerMgrs" value="" />
                </li>
                <li><br/></li>
                <li>
                  <label for="aoiRadius">Radius in Meters:</label>
                </li>
                <li>
                  <g:textField name="aoiRadius3"/>
                </li>
                <li><br/></li>
                <li>
                  <span class="formButton">
                    <input type="button" onclick="mapWidget.setCenterMgrs()" value="Set Center">
                  </span>
                </li>
              </ol>
            </div>
          </div>
          <div class="niceBox">
            <div class="niceBoxHd">Geospatial Criteria:</div>
              <div class="niceBoxBody">
                <ol>
                  <li>
                    <g:checkBox name="searchMethod3" id="bboxSearchButton3" value="${BaseQuery.BBOX_SEARCH}" checked="${queryParams?.searchMethod == BaseQuery.BBOX_SEARCH}" onclick="mapWidget.toggleBboxCheckBox()" />
                    <label>Use BBox Search</label>
                  </li>
                  <li><br/></li>
                  <li>
                    <label for="aoiNeMgrs">MGRS NE:</label>
                  </li>
                  <li>
                    <input type="text" id="aoiNeMgrs" name="aoiNeMgrs" value="" />
                  </li>
                  <li>
                    <label for="aoiSwMgrs">MGRS SW:</label>
                  </li>
                  <li>
                    <input type="text" id="aoiSwMgrs" name="aoiSwMgrs" value="" />
                  </li>
                  <li><br/></li>
                  <li>
                    <span class="formButton">
                      <input type="button" onclick="mapWidget.clearAOI()" value="Clear AOI">
                    </span>
                  </li>
                </ol>
              </div>
          </div>
        </richui:tabContent>

      </richui:tabContents>
    </richui:tabView>

    <input type="hidden" id="units" name="units"/>

    <style type="text/css">
    div.datechooser {
      display: none;
      position: absolute;
      left: 10px;
      top: 10px;
      z-index: 2
    }

    div.datechooser table.yui-calendar {
      width: 150px;
    }
    </style>

    <div class="niceBox">
      <div class="niceBoxHd">Temporal Criteria:</div>
      <div class="niceBoxBody">
        <ol>
          <li>
            <label for="startDate">Start Date:</label>
          </li>
          <li>
            <richui:dateChooser name="startDate" format="MM/dd/yyyy" timezone="${TimeZone.getTimeZone('UTC')}" style="width:75px" time="true" hourStyle="width:25px" minuteStyle="width:25px" value="${queryParams.startDate}" onChange="updateOmarFilters()" />
            <g:hiddenField name="startDate_timezone" value="UTC" />
          </li>
          <li>
            <label for='endDate'>End Date:</label>
          </li>
          <li>
            <richui:dateChooser name="endDate" format="MM/dd/yyyy" timezone="${TimeZone.getTimeZone('UTC')}" style="width:75px" time="true" hourStyle="width:25px" minuteStyle="width:25px" value="${queryParams.endDate}" onChange="updateOmarFilters()" />
            <g:hiddenField name="endDate_timezone" value="UTC" />
          </li>
        </ol>
      </div>
    </div>

    <div class="niceBox">
      <div class="niceBoxHd">Metadata Criteria:</div>
      <div class="niceBoxBody">
        <ol>
          <g:each in="${queryParams?.searchTagValues}" var="searchTagValue" status="i">
            <g:select
                    noSelection="${['null':'Select One...']}"
                    name="searchTagNames[${i}]"
                    value="${queryParams?.searchTagNames[i]}"
                    from="${RasterEntrySearchTag.list()}"
                    optionKey="name" optionValue="description" />
            <li>
              <g:textField name="searchTagValues[${i}]" value="${searchTagValue}" onChange="updateOmarFilters()" />
            </li>
          </g:each>
        </ol>
      </div>
    </div>

    <div class="niceBox">
      <div class="niceBoxHd">Options:</div>
      <div class="niceBoxBody">
        <ol>
          <li>
            <label for="max">Max Results:</label>
          </li>
          <li>
            <input type="text" id="max" name="max" value="${params?.max}" />
          </li>
        </ol>
      </div>
    </div>
  </g:form>
</content>

<content tag="center">

  <div id="toolBar" class="olControlPanel" style="position: relative;"></div><br/>

  <h1 id="mapTitle"></h1>

  <g:if test="${flash.message}">
    <div class="message">${flash.message}</div>
  </g:if>

  <div id="map"></div>

  <table>
    <tr>
      <td width="200px"><div id="mouseHoverDdOutput">&nbsp;</div></td>
      <td width="200px"><div id="mouseHoverDmsOutput">&nbsp;</div></td>
      <td width="200px"><div id="mouseHoverMgrsOutput">&nbsp;</div></td>
    </tr>
  </table>
</content>

<content tag="right">
  <div class="niceBox">
    <div class="niceBoxHd">Map Mensuration:</div>
    <div class="niceBoxBody">
      Path Units: <g:select id="pathUnits" name="pathUnits" from="${['kilometer', 'meter', 'centimeter','feet','inch','league','nautical league','microinch','mile','millimeter','yard']}" />
      <div id="pathMeasurementOutput"></div><br />
      <div id="areaMeasurementOutput"></div>
    </div>
  </div>

  <div id="layerswitcher" class="olControlLayerSwitcher" style="position: relative;"></div>
</content>

<content tag="javascript">
  <g:javascript plugin='richui' src="yui/button/button-min.js"/>

  

  <g:javascript plugin="omar-core" src="mapwidget.js" />
  <g:javascript plugin="omar-core" src="coordinateConversion.js" />
  <g:javascript plugin="omar-core" src="touch.js" />

  <g:javascript>



    var mapWidget = new MapWidget();

    function init()
    {
      var setupBaseLayers = function()
      {
        var baseLayer = null;

        <g:each var="foo" in="${baseWMS}">
    baseLayer = new OpenLayers.Layer.WMS("${foo.title}", "${foo.url}",
            {layers: "${foo.layers}", format: "${foo.format}"},
            {isBaseLayer: true, buffer: 0,transitionEffect: "resize"});

            mapWidget.setupBaseLayers(baseLayer);
         </g:each>
    };

    mapWidget.setupMapWidget();
    setupBaseLayers();
    mapWidget.setupDataLayer("${dataWMS.title}", "${dataWMS.url}", "${dataWMS.layers}", "${dataWMS.styles}", "${dataWMS.format}");
    mapWidget.changeMapSize();
    mapWidget.setupAoiLayer();
    mapWidget.setupToolBar();
    mapWidget.setupMapView("${queryParams?.viewMinLon ?: -180}", "${queryParams?.viewMinLat ?: -90}", "${queryParams?.viewMaxLon ?: 180}", "${queryParams?.viewMaxLat ?: 90}");

// SCOTTIE - Will have to revisit this...        
//    mapWidget.setupQueryFields("${queryParams.searchMethod}");

    var minLon = ${queryParams?.aoiMinLon ?: 'null'};
    var minLat = ${queryParams?.aoiMinLat ?: 'null'};
    var maxLon = ${queryParams?.aoiMaxLon ?: 'null'};
    var maxLat = ${queryParams?.aoiMaxLat ?: 'null'};
    if ( minLon && minLat && maxLon && maxLat)
    {
      mapWidget.initAOI(minLon, minLat, maxLon, maxLat);
    }
    if("${queryParams.searchMethod}" == "BBOX")
    {
       mapWidget.toggleBboxCheckBox()
    }
    else if("${queryParams.searchMethod}" == "RADIUS")
    {
       mapWidget.togglePointRadiusCheckBox()
    }
    else
    {
       mapWidget.toggleBboxCheckBox()
    }

    updateOmarFilters();
  }

  function updateOmarFilters()
  {
    var numberOfNames = parseInt("${queryParams?.searchTagNames.size()}");
    var numberOfValues = parseInt(${queryParams?.searchTagValues.size()});

    mapWidget.updateOmarFilters(
        $("startDate_day").value, $("startDate_month").value, $("startDate_year").value, $("startDate_hour").value, $("startDate_minute").value,
        $("endDate_day").value, $("endDate_month").value, $("endDate_year").value, $("endDate_hour").value, $("endDate_minute").value,
        numberOfNames, numberOfValues
        );
  }
  </g:javascript>

  <g:javascript>
    YAHOO.example.init = function () {
      var oLinkButton1 = new YAHOO.widget.Button({ type: "link", id: "linkbutton4", label: "OMAR Home", href: "${createLink(controller: "home")}", container: "linkbuttonsfromjavascript" });
      var oLinkButton2 = new YAHOO.widget.Button({ type: "link", id: "linkbutton5", label: "Static KML Bookmark", href: "javascript:mapWidget.generateKML();", container: "linkbuttonsfromjavascript" });
      var oLinkButton3 = new YAHOO.widget.Button({ type: "link", id: "linkbutton6", label: "Update Footprints", href: "javascript:updateOmarFilters();", container: "linkbuttonsfromjavascript" });
      var oLinkButton4 = new YAHOO.widget.Button({ type: "link", id: "linkbutton6", label: "Search Rasters", href: "javascript:mapWidget.search();", container: "linkbuttonsfromjavascript" });
    } ();
  </g:javascript>
  
</content>

</body>
</html>