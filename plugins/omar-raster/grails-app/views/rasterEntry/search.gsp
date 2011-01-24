<%@ page import="grails.converters.JSON; org.ossim.omar.BaseQuery; org.ossim.omar.RasterEntryQuery; org.ossim.omar.RasterEntrySearchTag" contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>OMAR: Raster Search</title>
  <meta name="layout" content="searchStatic"/>
  <openlayers:loadMapToolBar/>
  <openlayers:loadTheme theme="default"/>
  <openlayers:loadJavascript/>
  <script type='text/javascript' src='${omar.bundle(contentType: "text/javascript", files: [
          resource(plugin: "omar-core", dir: "js", file: "mapwidget.js"),
          resource(plugin: "omar-core", dir: "js", file: "coordinateConversion.js")
      ])}'></script>
</head>

<body class="yui-skin-sam" onresize="bodyOnResize();">
<content tag="top">
  <div class="nav">
    <span class="menuButton"><g:link class="home" uri="/">OMAR™ Home</g:link></span>
    <span class="menuButton"><g:link action="list" url="javascript:mapWidget.generateKML();">KML Query</g:link></span>
    <span class="menuButton"><g:link action="list" url="javascript:updateOmarFilters();">Update Footprints</g:link></span>
    <span class="menuButton"><g:link action="list" url="javascript:mapWidget.search();">Search Rasters</g:link></span>
  </div>
</content>

<content tag="left">
  <g:form name="searchForm">
    <div id="demo" class="yui-navset">
      <ul class="yui-nav">
        <li class="selected"><a href="#tab1"><em>DD</em></a></li>
        <li><a href="#tab2"><em>DMS</em></a></li>
        <li><a href="#tab3"><em>MGRS</em></a></li>
      </ul>
      <div class="yui-content">
        <div id="tab1">
          <div class="niceBox">
            <div class="niceBoxHd">Map Center:</div>
            <div class="niceBoxBody">
              <ol>
                <li>
                  <g:checkBox name="searchMethod" id="radiusSearchButton" value="${BaseQuery.RADIUS_SEARCH}" checked="${queryParams?.searchMethod == BaseQuery.RADIUS_SEARCH}" onclick="mapWidget.togglePointRadiusCheckBox()"/>
                  <label>Use Radius Search</label>
                </li>
                <li>
                  <br/>
                </li>
                <li>
                  <label for="centerLat">Latitude:</label>
                </li>
                <li>
                  <g:textField name="centerLat" value="${queryParams?.centerLat}"/>
                </li>
                <li>
                  <label for="centerLon">Longitude:</label>
                </li>
                <li>
                  <g:textField name="centerLon" value="${queryParams?.centerLon}"/>
                </li>
                <li>
                  <br/>
                </li>
                <li>
                  <label for="aoiRadius">Radius in Meters:</label>
                </li>
                <li>
                  <g:textField name="aoiRadius" value="${fieldValue(bean: queryParams, field: 'aoiRadius')}" onChange="updateOmarFilters()"/>
                </li>
                <li>
                  <br/>
                </li>
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
              <input type="hidden" id="viewMinLon" name="viewMinLon" value="${fieldValue(bean: queryParams, field: 'viewMinLon')}"/>
              <input type="hidden" id="viewMinLat" name="viewMinLat" value="${fieldValue(bean: queryParams, field: 'viewMinLat')}"/>
              <input type="hidden" id="viewMaxLon" name="viewMaxLon" value="${fieldValue(bean: queryParams, field: 'viewMaxLon')}"/>
              <input type="hidden" id="viewMaxLat" name="viewMaxLat" value="${fieldValue(bean: queryParams, field: 'viewMaxLat')}"/>
              <ol>
                <li>
                  <g:checkBox name="searchMethod" id="bboxSearchButton" value="${BaseQuery.BBOX_SEARCH}" checked="${queryParams?.searchMethod == BaseQuery.BBOX_SEARCH}" onclick="mapWidget.toggleBboxCheckBox()"/>
                  <label>Use BBox Search</label>
                </li>
                <li>
                  <br/>
                </li>
                <li>
                  <label for="aoiMaxLat">North Latitude:</label>
                </li>
                <li>
                  <input type="text" id="aoiMaxLat" name="aoiMaxLat" value="${fieldValue(bean: queryParams, field: 'aoiMaxLat')}"/>
                </li>
                <li>
                  <label for="aoiMinLon">West Longitude:</label>
                </li>
                <li>
                  <input type="text" id="aoiMinLon" name="aoiMinLon" value="${fieldValue(bean: queryParams, field: 'aoiMinLon')}"/>
                </li>
                <li>
                  <label for="aoiMinLat">South Latitude:</label>
                </li>
                <li>
                  <input type="text" id="aoiMinLat" name="aoiMinLat" value="${fieldValue(bean: queryParams, field: 'aoiMinLat')}"/>
                </li>
                <li>
                  <label for="aoiMaxLon">East Longitude:</label>
                </li>
                <li>
                  <input type="text" id="aoiMaxLon" name="aoiMaxLon" value="${fieldValue(bean: queryParams, field: 'aoiMaxLon')}"/>
                </li>
                <li>
                  <br/>
                </li>
                <li>
                  <span class="formButton">
                    <input type="button" onclick="mapWidget.clearAOI()" value="Clear AOI">
                  </span>
                </li>
              </ol>
            </div>
          </div>
        </div>
        <div id="tab2">
          <div class="niceBox">
            <div class="niceBoxHd">Map Center:</div>
            <div class="niceBoxBody">
              <ol>
                <li>
                  <g:checkBox name="searchMethod2" id="radiusSearchButton2" value="${BaseQuery.RADIUS_SEARCH}" checked="${queryParams?.searchMethod == BaseQuery.RADIUS_SEARCH}" onclick="mapWidget.togglePointRadiusCheckBox()"/>
                  <label>Use Radius Search</label>
                </li>
                <li>
                  <br/>
                </li>
                <li>
                  <label for="centerLat">Latitude:</label>
                </li>
                <li>
                  <g:textField name="centerLatDms" value=""/>
                </li>
                <li>
                  <label for="centerLon">Longitude:</label>
                </li>
                <li>
                  <g:textField name="centerLonDms" value=""/>
                </li>
                <li>
                  <br/>
                </li>
                <li>
                  <label for="aoiRadius">Radius in Meters:</label>
                </li>
                <li>
                  <g:textField name="aoiRadius2"/>
                </li>
                <li>
                  <br/>
                </li>
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
                  <g:checkBox name="searchMethod2" id="bboxSearchButton2" value="${BaseQuery.BBOX_SEARCH}" checked="${queryParams?.searchMethod == BaseQuery.BBOX_SEARCH}" onclick="mapWidget.toggleBboxCheckBox()"/>
                  <label>Use BBox Search</label>
                </li>
                <li>
                  <br/>
                </li>
                <li>
                  <label for="aoiMaxLat">North Latitude:</label>
                </li>
                <li>
                  <input type="text" id="aoiMaxLatDms" name="aoiMaxLatDms"/>
                </li>
                <li>
                  <label for="aoiMinLon">West Longitude:</label>
                </li>
                <li>
                  <input type="text" id="aoiMinLonDms" name="aoiMinLonDms"/>
                </li>
                <li>
                  <label for="aoiMinLat">South Latitude:</label>
                </li>
                <li>
                  <input type="text" id="aoiMinLatDms" name="aoiMinLatDms"/>
                </li>
                <li>
                  <label for="aoiMaxLon">East Longitude:</label>
                </li>
                <li>
                  <input type="text" id="aoiMaxLonDms" name="aoiMaxLonDms"/>
                </li>
                <li>
                  <br/>
                </li>
                <li>
                  <span class="formButton">
                    <input type="button" onclick="mapWidget.clearAOI()" value="Clear AOI">
                  </span>
                </li>
              </ol>
            </div>
          </div>
        </div>
        <div id="tab3">
          <div class="niceBox">
            <div class="niceBoxHd">Map Center:</div>
            <div class="niceBoxBody">
              <ol>
                <li>
                  <g:checkBox name="searchMethod3" id="radiusSearchButton3" value="${BaseQuery.RADIUS_SEARCH}" checked="${queryParams?.searchMethod == BaseQuery.RADIUS_SEARCH}" onclick="mapWidget.togglePointRadiusCheckBox()"/>
                  <label>Use Radius Search</label>
                </li>
                <li>
                  <br/>
                </li>
                <li>
                  <label for="centerLat">MGRS:</label>
                </li>
                <li>
                  <g:textField name="centerMgrs" value=""/>
                </li>
                <li>
                  <br/>
                </li>
                <li>
                  <label for="aoiRadius">Radius in Meters:</label>
                </li>
                <li>
                  <g:textField name="aoiRadius3"/>
                </li>
                <li>
                  <br/>
                </li>
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
                  <g:checkBox name="searchMethod3" id="bboxSearchButton3" value="${BaseQuery.BBOX_SEARCH}" checked="${queryParams?.searchMethod == BaseQuery.BBOX_SEARCH}" onclick="mapWidget.toggleBboxCheckBox()"/>
                  <label>Use BBox Search</label>
                </li>
                <li>
                  <br/>
                </li>
                <li>
                  <label for="aoiNeMgrs">MGRS NE:</label>
                </li>
                <li>
                  <input type="text" id="aoiNeMgrs" name="aoiNeMgrs" value=""/>
                </li>
                <li>
                  <label for="aoiSwMgrs">MGRS SW:</label>
                </li>
                <li>
                  <input type="text" id="aoiSwMgrs" name="aoiSwMgrs" value=""/>
                </li>
                <li>
                  <br/>
                </li>
                <li>
                  <span class="formButton">
                    <input type="button" onclick="mapWidget.clearAOI()" value="Clear AOI">
                  </span>
                </li>
              </ol>
            </div>
          </div>
        </div>
      </div>
    </div>
    <input type="hidden" id="units" name="units"/>
    <div class="niceBox">
      <div class="niceBoxHd">Temporal Criteria:</div>
      <div class="niceBoxBody">
        <ol>
          <li>
            <label for="startDate">Start Date:</label>
          </li>
          <li>
            <richui:dateChooser name="startDate" format="MM/dd/yyyy" timezone="${TimeZone.getTimeZone('UTC')}" style="width:75px" time="true" hourStyle="width:25px" minuteStyle="width:25px" value="${queryParams.startDate}" onChange="updateOmarFilters()"/>
            <g:hiddenField name="startDate_timezone" value="UTC"/>
          </li>
          <li>
            <label for='endDate'>End Date:</label>
          </li>
          <li>
            <richui:dateChooser name="endDate" format="MM/dd/yyyy" timezone="${TimeZone.getTimeZone('UTC')}" style="width:75px" time="true" hourStyle="width:25px" minuteStyle="width:25px" value="${queryParams.endDate}" onChange="updateOmarFilters()"/>
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
            <li>
              <g:textField name="searchTagValues[${i}]" value="${searchTagValue}" onChange="updateOmarFilters()"/>
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
            <input type="text" id="max" name="max" value="${params?.max}"/>
          </li>
        </ol>
      </div>
    </div>
  </g:form>
</content>
<content tag="center">
</content>
<content tag="right">
  <div class="niceBox">
    <div class="niceBoxHd">Map Mensuration:</div>
    <div class="niceBoxBody">
      Path Units: <g:select id="pathUnits" name="pathUnits" from="${['kilometer', 'meter','feet','mile','yard']}"/>
      <div id="pathMeasurementOutput"></div>
      <div id="areaMeasurementOutput"></div>
    </div>
  </div>
  <g:render plugin="omar-core" template="/common/olLayerSwitcherTemplate"/>
</content>
<g:javascript>
  var mapWidget = new MapWidget();
(function() {
    var tabView = new YAHOO.widget.TabView('demo');
})();



function init()
{
    var setupBaseLayers = function()
    {
        var baseLayer = null;
        var baseWMS=${baseWMS as JSON};

        for ( layer in baseWMS ) {
          baseLayer = new OpenLayers.Layer.WMS(baseWMS[layer].name, baseWMS[layer].url,
                  baseWMS[layer].params, baseWMS[layer].options);

          mapWidget.setupBaseLayers(baseLayer);
        }
  };

  mapWidget.setupMapWidget();
  setupBaseLayers();
  mapWidget.setupDataLayer("${dataWMS.name}", "${dataWMS.url}", "${dataWMS.params.layers}", "${dataWMS.options.styles}", "${dataWMS.params.format}");
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

var oElement = document.getElementById("startDate_hour");
var oElement1 = document.getElementById("startDate_minute");
var oElement2 = document.getElementById("endDate_hour");
var oElement3 = document.getElementById("endDate_minute");

YAHOO.util.Event.addListener(oElement, "change", updateOmarFilters);
YAHOO.util.Event.addListener(oElement1, "change", updateOmarFilters);
YAHOO.util.Event.addListener(oElement2, "change", updateOmarFilters);
YAHOO.util.Event.addListener(oElement3, "change", updateOmarFilters);

</g:javascript>

</body>
</html>