<%@ page import="grails.converters.JSON; org.ossim.omar.BaseQuery; org.ossim.omar.VideoDataSetQuery; org.ossim.omar.VideoDataSetSearchTag" contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>OMAR <g:meta name="app.version"/>: Video Search</title>
  <meta name="layout" content="searchStatic"/>
  <openlayers:loadMapToolBar/>
  <openlayers:loadTheme theme="default"/>
  <style>
  #homeMenu {
    background: url(../images/skin/house.png) left no-repeat;
    z-index: 99999;
  }

  #exportMenu, #viewMenu, .datechooser {
    z-index: 99999;
  }

  </style>
</head>

<body class="yui-skin-sam" onload="init();">

<content tag="top">
  <g:form name="searchForm">
  </g:form>
  <div id="searchMenu" class="yuimenubar yuimenubarnav">
    <div class="bd">
      <ul class="first-of-type">
        <li class="yuimenubaritem first-of-type"><a class="yuimenubaritemlabel" id="homeMenu"
                                                    href="${createLink(controller: 'home', action: 'index')}"
                                                    title="OMAR™ Home">&nbsp;&nbsp;&nbsp;&nbsp;OMAR™ Home</a>
        </li>
        <li class="yuimenubaritem first-of-type"><a class="yuimenubaritemlabel" href="#exportMenu">Export</a>

          <div id="exportMenu" class="yuimenu">
            <div class="bd">
              <ul>
                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="javascript:generateKml()"
                                           title="Export KML.  If no selection box is present the view bounds will float in google earth and if you hit refresh it will use the current google viewport to query the latest imagery.  If you specify a selection then it will be fixed to that location">KML Query</a>
                </li>
              </ul>
            </div>
          </div>
        </li>
        <li class="yuimenubaritem first-of-type"><a class="yuimenubaritemlabel" href="#viewMenu">View</a>

          <div id="viewMenu" class="yuimenu">
            <div class="bd">
              <ul>
                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="javascript:updateOmarFilters();"
                                           title="Refresh the footprints">Refresh Footprints</a></li>
                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="javascript:search();"
                                           title="Execute the search for the specified criteria">Search</a></li>
              </ul>
            </div>
          </div>
        </li>
      </ul>
    </div>
  </div>
</content>

<content tag="left">
<div id="spatialTab" class="yui-navset">
<ul class="yui-nav">
  <li class="selected"><a href="#spatialTab1" id="ddTab"><em>DD</em></a></li>
  <li><a href="#spatialTab2" id="dmsTab"><em>DMS</em></a></li>
  <li><a href="#spatialTab3" id="mgrsTab"><em>MGRS</em></a></li>
</ul>

<div class="yui-content">
<div id="spatialTab1">
  <div class="niceBox">
    <div class="niceBoxHd">Map Center:</div>

    <div class="niceBoxBody">
      <ol>
        <li>
          <g:checkBox title="If this is checked we will use the center radius to define the Area of Interest"
                      name="searchMethod" id="radiusSearchButton" value="${BaseQuery.RADIUS_SEARCH}"
                      checked="${queryParams?.searchMethod == BaseQuery.RADIUS_SEARCH}"
                      onclick="mapWidget.togglePointRadiusCheckBox()"/>
          <label
              title="If this is checked we will use the center radius to define the Area of Interest">Use Radius Search</label>
        </li>
        <li>
          <br/>
        </li>
        <li>
          <label for="centerLat">Latitude:</label>
        </li>
        <li>
          <g:textField title="Specify in decimal degrees the center latitude" name="centerLat"
                       value="${queryParams?.centerLat}" onChange="mapWidget.setCenterDd()"/>
        </li>
        <li>
          <label for="centerLon">Longitude:</label>
        </li>
        <li>
          <g:textField title="Specify in decimal degrees the center longitude" name="centerLon"
                       value="${queryParams?.centerLon}" onChange="mapWidget.setCenterDd()"/>
        </li>
        <li>
          <br/>
        </li>
        <li>
          <label for="aoiRadius">Radius in Meters:</label>
        </li>
        <li>
          <g:textField title="Specify the radial search in meters" id="aoiRadius" name="aoiRadius"
                       value="${fieldValue(bean: queryParams, field: 'aoiRadius')}"
                       onChange="updateOmarFilters();syncAoiRadius('aoiRadius')"/>
        </li>
        <li>
          <br/>
        </li>
        <li>
          <button type="button" onclick="javascript:mapWidget.setCenterDd();">Set Center</button>
        </li>
      </ol>
    </div>
  </div>

  <div class="niceBox">
    <div class="niceBoxHd">Geospatial Criteria:</div>

    <div class="niceBoxBody">
      <input type="hidden" id="viewMinLon" name="viewMinLon"
             value="${fieldValue(bean: queryParams, field: 'viewMinLon')}"/>
      <input type="hidden" id="viewMinLat" name="viewMinLat"
             value="${fieldValue(bean: queryParams, field: 'viewMinLat')}"/>
      <input type="hidden" id="viewMaxLon" name="viewMaxLon"
             value="${fieldValue(bean: queryParams, field: 'viewMaxLon')}"/>
      <input type="hidden" id="viewMaxLat" name="viewMaxLat"
             value="${fieldValue(bean: queryParams, field: 'viewMaxLat')}"/>
      <ol>
        <li>
          <g:checkBox title="If this is checked we will use the center radius to define the Area of Interest"
                      name="searchMethod" id="bboxSearchButton" value="${BaseQuery.BBOX_SEARCH}"
                      checked="${queryParams?.searchMethod == BaseQuery.BBOX_SEARCH}"
                      onclick="mapWidget.toggleBboxCheckBox()"/>
          <label
              title="If this is checked we will use the center radius to define the Area of Interest">Use Bound Box Search</label>
        </li>
        <li>
          <br/>
        </li>
        <li>
          <label for="aoiMaxLat">North Latitude:</label>
        </li>
        <li>
          <input title="Specify the northern latitude in decimal degrees" type="text" id="aoiMaxLat" name="aoiMaxLat"
                 value="${fieldValue(bean: queryParams, field: 'aoiMaxLat')}"/>
        </li>
        <li>
          <label for="aoiMinLon">West Longitude:</label>
        </li>
        <li>
          <input title="Specify the weastern longitude in decimal degrees" type="text" id="aoiMinLon" name="aoiMinLon"
                 value="${fieldValue(bean: queryParams, field: 'aoiMinLon')}"/>
        </li>
        <li>
          <label for="aoiMinLat">South Latitude:</label>
        </li>
        <li>
          <input title="Specify the southern latitude in decimal degrees" type="text" id="aoiMinLat" name="aoiMinLat"
                 value="${fieldValue(bean: queryParams, field: 'aoiMinLat')}"/>
        </li>
        <li>
          <label for="aoiMaxLon">East Longitude:</label>
        </li>
        <li>
          <input title="Specify the estern longitude in decimal degrees" type="text" id="aoiMaxLon" name="aoiMaxLon"
                 value="${fieldValue(bean: queryParams, field: 'aoiMaxLon')}"/>
        </li>
        <li>
          <br/>
        </li>
        <li>
          <button title="Clear the Bounding Box search options" type="button"
                  onclick="javascript:mapWidget.clearAOI();">Clear</button>
        </li>
      </ol>
    </div>
  </div>
</div>

<div id="spatialTab2">
  <div class="niceBox">
    <div class="niceBoxHd">Map Center:</div>

    <div class="niceBoxBody">
      <ol>
        <li>
          <g:checkBox title="If this is checked we will use the center radius to define the Area of Interest"
                      name="searchMethod2" id="radiusSearchButton2" value="${BaseQuery.RADIUS_SEARCH}"
                      checked="${queryParams?.searchMethod == BaseQuery.RADIUS_SEARCH}"
                      onclick="mapWidget.togglePointRadiusCheckBox()"/>
          <label
              title="If this is checked we will use the center radius to define the Area of Interest">Use Radius Search</label>
        </li>
        <li>
          <br/>
        </li>
        <li>
          <label for="centerLat">Latitude:</label>
        </li>
        <li>
          <g:textField title="enter a dms of the form ddmmss.ssssN|S or dd mm ss.sss N|S" name="centerLatDms" value=""
                       onChange="mapWidget.setCenterDms()"/>
        </li>
        <li>
          <label for="centerLon">Longitude:</label>
        </li>
        <li>
          <g:textField title="enter a dms of the form dddmmss.ssssE|W or ddd mm ss.sss E|W" name="centerLonDms" value=""
                       onChange="mapWidget.setCenterDms()"/>
        </li>
        <li>
          <br/>
        </li>
        <li>
          <label for="aoiRadius2">Radius in Meters:</label>
        </li>
        <li>
          <g:textField title="Specify the radial search in meters" id="aoiRadius2" name="aoiRadius2"
                       value="${fieldValue(bean: queryParams, field: 'aoiRadius')}"
                       onChange="updateOmarFilters();syncAoiRadius('aoiRadius2')"/>
        </li>
        <li>
          <br/>
        </li>
        <li>
          <button title="Sets the center of the map to the specified coordinate" type="button"
                  onclick="javascript:mapWidget.setCenterDms();">Set Center</button>
        </li>
      </ol>
    </div>
  </div>

  <div class="niceBox">
    <div class="niceBoxHd">Geospatial Criteria:</div>

    <div class="niceBoxBody">
      <ol>
        <li>
          <g:checkBox title="If this is checked we will use the Bounding Box search" name="searchMethod2"
                      id="bboxSearchButton2" value="${BaseQuery.BBOX_SEARCH}"
                      checked="${queryParams?.searchMethod == BaseQuery.BBOX_SEARCH}"
                      onclick="mapWidget.toggleBboxCheckBox()"/>
          <label title="If this is checked we will use the Bounding Box search">Use Bound Box Search</label>
        </li>
        <li>
          <br/>
        </li>
        <li>
          <label for="aoiMaxLat">North Latitude:</label>
        </li>
        <li>
          <input title="Enter a dms of the form ddmmss.ssssN|S or dd mm ss.sss N|S" type="text" id="aoiMaxLatDms"
                 name="aoiMaxLatDms"/>
        </li>
        <li>
          <label for="aoiMinLon">West Longitude:</label>
        </li>
        <li>
          <input title="Enter a dms of the form dddmmss.ssssE|W or ddd mm ss.sss E|W" title="" type="text"
                 id="aoiMinLonDms" name="aoiMinLonDms"/>
        </li>
        <li>
          <label for="aoiMinLat">South Latitude:</label>
        </li>
        <li>
          <input title="Enter a dms of the form ddmmss.ssssN|S or dd mm ss.sss N|S" type="text" id="aoiMinLatDms"
                 name="aoiMinLatDms"/>
        </li>
        <li>
          <label for="aoiMaxLon">East Longitude:</label>
        </li>
        <li>
          <input title="Enter a dms of the form dddmmss.ssssE|W or ddd mm ss.sss E|W" type="text" id="aoiMaxLonDms"
                 name="aoiMaxLonDms"/>
        </li>
        <li>
          <br/>
        </li>
        <li>
          <button title="Clear the Bounding Box search options" type="button"
                  onclick="javascript:mapWidget.clearAOI();">Clear</button>
        </li>
      </ol>
    </div>
  </div>
</div>

<div id="spatialTab3">
  <div class="niceBox">
    <div class="niceBoxHd">Map Center:</div>

    <div class="niceBoxBody">
      <ol>
        <li>
          <g:checkBox title="If this is checked we will use the Bounding Box search" name="searchMethod3"
                      id="radiusSearchButton3" value="${BaseQuery.RADIUS_SEARCH}"
                      checked="${queryParams?.searchMethod == BaseQuery.RADIUS_SEARCH}"
                      onclick="mapWidget.togglePointRadiusCheckBox()"/>
          <label title="If this is checked we will use the Bounding Box search">Use Radius Search</label>
        </li>
        <li>
          <br/>
        </li>
        <li>
          <label for="centerLat">MGRS:</label>
        </li>
        <li>
          <g:textField
              title="MGRS formatted input of the form [grid zone designator][100,000-meter square identifier][easting][northing]"
              name="centerMgrs" value="" onChange="mapWidget.setCenterMgrs()"/>
        </li>
        <li>
          <br/>
        </li>
        <li>
          <label for="aoiRadius3">Radius in Meters:</label>
        </li>
        <li>
          <g:textField title="Specify the radial search in meters" id="aoiRadius3" name="aoiRadius3"
                       value="${fieldValue(bean: queryParams, field: 'aoiRadius')}"
                       onChange="updateOmarFilters();syncAoiRadius('aoiRadius3')"/>
        </li>
        <li>
          <br/>
        </li>
        <li>
          <button title="Sets the center of the map to the specified input field" type="button"
                  onclick="javascript:mapWidget.setCenterMgrs();">Set Center</button>
        </li>
      </ol>
    </div>
  </div>

  <div class="niceBox">
    <div class="niceBoxHd">Geospatial Criteria:</div>

    <div class="niceBoxBody">
      <ol>
        <li>
          <g:checkBox title="If this is checked we will use the Bounding Box search" name="searchMethod3"
                      id="bboxSearchButton3" value="${BaseQuery.BBOX_SEARCH}"
                      checked="${queryParams?.searchMethod == BaseQuery.BBOX_SEARCH}"
                      onclick="mapWidget.toggleBboxCheckBox()"/>
          <label title="If this is checked we will use the Bounding Box search">Use Bound Box Search</label>
        </li>
        <li>
          <br/>
        </li>
        <li>
          <label for="aoiNeMgrs">MGRS NE:</label>
        </li>
        <li>
          <input
              title="MGRS formatted input of the form [grid zone designator][100,000-meter square identifier][easting][northing]"
              type="text" id="aoiNeMgrs" name="aoiNeMgrs" value=""/>
        </li>
        <li>
          <label for="aoiSwMgrs">MGRS SW:</label>
        </li>
        <li>
          <input
              title="MGRS formatted input of the form [grid zone designator][100,000-meter square identifier][easting][northing]"
              type="text" id="aoiSwMgrs" name="aoiSwMgrs" value=""/>
        </li>
        <li>
          <br/>
        </li>
        <li>
          <button title="Clear the Bounding Box search options" type="button"
                  onclick="javascript:mapWidget.clearAOI();">Clear</button>
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
        <!--- HACK --->
        <input class='' style='width:75px' type='text' name='startDateInput' id='startDateInput' value=''/>

        <div id='startDateContainer' class='datechooser yui-skin-sam'></div>
        <input type='hidden' name='startDate' id='startDate' value='date.struct'/>
        <input class='null' style='width:25px' type='text' name='startDate_hour' id='startDate_hour' value='00'/>:
        <input class='null' style='width:25px' type='text' name='startDate_minute' id='startDate_minute' value='00'/>
        <input type='hidden' name='startDate_day' id='startDate_day' value=''/>
        <input type='hidden' name='startDate_month' id='startDate_month' value=''/>
        <!--- HACK --->
        <input type='hidden' name='startDate_year' id='startDate_year' value=''/>
        <g:hiddenField name="startDate_timezone" value="UTC"/>
      </li>
      <li>
        <label for='endDate'>End Date:</label>
      </li>
      <li>
        <!-- HACK -->
        <input class='' style='width:75px' type='text' name='endDateInput' id='endDateInput' value=''/>

        <div id='endDateContainer' class='datechooser yui-skin-sam'></div>
        <input type='hidden' name='endDate' id='endDate' value='date.struct'/>
        <input class='null' style='width:25px' type='text' name='endDate_hour' id='endDate_hour' value='00'/>:
        <input class='null' style='width:25px' type='text' name='endDate_minute' id='endDate_minute' value='00'/>
        <input type='hidden' name='endDate_day' id='endDate_day' value=''/>
        <input type='hidden' name='endDate_month' id='endDate_month' value=''/>
        <input type='hidden' name='endDate_year' id='endDate_year' value=''/>
        <!-- HACK -->
        <g:hiddenField name="endDate_timezone" value="UTC"/>
      </li>
    </ol>
  </div>
</div>

<div id="criteriaTab" class="yui-navset">
  <p/>
  <ul class="yui-nav">
    <li class="selected" id="metadataCriteriaTab"><a href="#criteriaTab1"><em>Metadata</em></a></li>
    <li id="cqlCriteriaTab"><a href="#criteriaTab2"><em>CQL</em></a></li>
  </ul>

  <div class="yui-content">
    <div id="criteriaTab1"><p>


      <div class="niceBox">
        <div class="niceBoxHd">Metadata Criteria:</div>

        <div class="niceBoxBody">
          <ol>
            <g:each in="${queryParams?.searchTagValues}" var="searchTagValue" status="i">
              <g:select
                  noSelection="${['null':'Select One...']}"
                  id="searchTagNames[${i}]"
                  name="searchTagNames[${i}]"
                  value="${queryParams?.searchTagNames[i]}"
                  from="${VideoDataSetSearchTag.list(sort:'description')}"
                  optionKey="name" optionValue="description"/>
              <li>
                <g:textField id="searchTagValues[${i}]" name="searchTagValues[${i}]" value="${searchTagValue}"
                             onChange="updateOmarFilters()"/>
              </li>
            </g:each>
          </ol>
        </div>
      </div>
    </p></div>

    <div id="criteriaTab2"><p>

      <div class="niceBox">
        <div class="niceBoxHd">Common Query Language:</div>

        <div class="niceBoxBody">
          <ol>

            <li>
              <g:textArea id="filter" name="filter" value="${queryParams.filter}" style='width: 100%; height: 200px;'
                          onChange="updateOmarFilters()"/>
            </li>

          </ol>
        </div>
      </div>



    </p></div>
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
        <input type="text" id="max" name="max" value="${params?.max}" onChange="validateMaxResults()"/>
      </li>
    </ol>
  </div>
</div>

<div align="center">
  <button type="button" onclick="javascript:search();">Search</button>
</div>
</content>
<content tag="center">
</content>
<content tag="right">
  <div class="niceBox">
    <div class="niceBoxHd">Map Measurement Tool:</div>

    <div class="niceBoxBody">
      <ul>
        <li>Measurement Units:</li>
        <li>Not certified for targeting.</li>
        <li><g:select name="measurementUnits" from="${['kilometers', 'meters', 'feet', 'miles', 'yards']}"
                      title="Select a unit of measuremen and use the path and polygon measurment tools in the map toolbar."
                      onChange="measureUnitChanged(this.value)"/></li>

        <div id="pathMeasurement"></div>

        <div id="polygonMeasurement"></div>
      </ul>
    </div>
  </div>
  <g:render plugin="omar-core" template="/common/olLayerSwitcherTemplate"/>
</content>
<omar:bundle contentType="javascript" files="${[
  [plugin: 'omar-core', dir: 'js', file: 'mapwidget.js'],
  [plugin: 'omar-core', dir: 'js', file: 'coordinateConversion.js'],
  [plugin:'yui' , dir:'js/yui/element', file: 'element-min.js'],
  [plugin:'yui' , dir:'js/yui/tabview/', file: 'tabview-min.js'],
]}"/>

<openlayers:loadJavascript/>

<!-- HACK -->
<g:javascript>
	var dateChooser = new DateChooser();
	dateChooser.setDisplayContainer("startDateContainer");
	dateChooser.setInputId("startDateInput");
	dateChooser.setStructId("startDate");
	dateChooser.setFormat("MM/dd/yyyy");
	dateChooser.setLocale("en");
  dateChooser.setChangeCallback("updateOmarFilters()");
	dateChooser.init();
</g:javascript>
<g:javascript>
	var dateChooser = new DateChooser();
	dateChooser.setDisplayContainer("endDateContainer");
	dateChooser.setInputId("endDateInput");
	dateChooser.setStructId("endDate");
	dateChooser.setFormat("MM/dd/yyyy");
	dateChooser.setLocale("en");
  dateChooser.setChangeCallback("updateOmarFilters()");
	dateChooser.init();
</g:javascript>
<!-- HACK -->

<g:javascript>

  function validateMaxResults()
  {
  var maxResultsRegExp = /^([1-9][0-9]?[0-9]?)$/

  if (!$("max").value.match(maxResultsRegExp))
  {
    alert("Invalid input for max results.\nAcceptable Inputs: 1 - 100");
    $("max").value = "10";
  }
  }
  var mapWidget = null;
  var oElement  = null;
  var oElement1 = null;
  var oElement2 = null;
  var oElement3 = null;
  var tabView   = null;
  var criteriaTabView = null;
  var videoSearchSpatialIndex=${session.videoSearchSpatialTab ?: 0};
  var videoSearchCriteriaIndex=${session.videoSearchCriteriaTab ?: 0};
  var omarSearchParams = new OmarSearchParams();
  omarSearchParams.searchMethod = "${BaseQuery.BBOX_SEARCH}"
  function syncAoiRadius(synchTo)
  {
    value = $(synchTo).value;
    $("aoiRadius").value = value;
    $("aoiRadius2").value = value;
    $("aoiRadius3").value = value;

  }
  function search()
  {
   // if(!validateSearchParameters()) return

    var url = "${createLink(action: 'search', controller: 'videoDataSet')}";
    mapWidget.setupSearch();
    omarSearchParams.setProperties(document);
    omarSearchParams.initTime();
    omarSearchParams.time = "";
    omarSearchParams.searchMethod = "${BaseQuery.BBOX_SEARCH}"
    if(!$( "bboxSearchButton" ).checked)
    {
        omarSearchParams.searchMethod = "${BaseQuery.RADIUS_SEARCH}"
    }
    document.searchForm.action = url + "?" + omarSearchParams.toUrlParams();
    document.searchForm.submit();
  }
  function generateKml()
  {
    var url = "${createLink(action: 'kmlnetworklink', controller: 'videoDataSet')}";
    mapWidget.setupSearch();
    omarSearchParams.initTime();
    omarSearchParams.setProperties(document);
    omarSearchParams.searchMethod = "${BaseQuery.BBOX_SEARCH}"
    if(!$( "bboxSearchButton" ).checked)
    {
        omarSearchParams.searchMethod = "${BaseQuery.RADIUS_SEARCH}"
    }
    document.searchForm.action = url + "?" + omarSearchParams.toUrlParams();
    //alert(document.searchForm.action );
    document.searchForm.submit();
  }
  function init()
  {
    var oMenu = new YAHOO.widget.MenuBar("searchMenu", {
                                                autosubmenudisplay: true,
                        showdelay: 0,
                                                hidedelay: 750,
                                                lazyload: true,
                                                zIndex:9999});
  oMenu.render();
    var spatialSearchFlag = document.getElementById("spatialSearchFlag");
    if(spatialSearchFlag)
    {
       spatialSearchFlag.checked = ${queryParams?.spatialSearchFlag};
       spatialSearchFlag.value   = "${queryParams?.spatialSearchFlag}";
    }
    tabView = new YAHOO.widget.TabView('demo');
    criteriaTabView = new YAHOO.widget.TabView('criteriaTab');
    spatialTabView = new YAHOO.widget.TabView('spatialTab');

    var tab0 = criteriaTabView.getTab(0);
    var tab1 = criteriaTabView.getTab(1);
    tab0.addListener('click', handleClickCriteriaTab0);
    tab1.addListener('click', handleClickCriteriaTab1);
    criteriaTabView.selectTab(videoSearchCriteriaIndex);
    var spatialTab0 = spatialTabView.getTab(0);
    var spatialTab1 = spatialTabView.getTab(1);
    var spatialTab2 = spatialTabView.getTab(2);
    spatialTab0.addListener('click', handleClickSpatialTab0);
    spatialTab1.addListener('click', handleClickSpatialTab1);
    spatialTab2.addListener('click', handleClickSpatialTab2);
    spatialTabView.selectTab(videoSearchSpatialIndex);

    mapWidget = new MapWidget();
    mapWidget.setupMapWidget();
    setupBaseLayers();
    mapWidget.setupDataLayer("${dataWMS.name}", "${dataWMS.url}", "${dataWMS.params.layers}", "${dataWMS.options.styles}", "${dataWMS.params.format}");
    mapWidget.changeMapSize();
    mapWidget.setupAoiLayer();
    mapWidget.setupToolBar();
    mapWidget.setupMapView("${queryParams?.viewMinLon ?: -180}", "${queryParams?.viewMinLat ?: -90}", "${queryParams?.viewMaxLon ?: 180}", "${queryParams?.viewMaxLat ?: 90}");
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
    oElement = document.getElementById("startDate_hour");
    oElement1 = document.getElementById("startDate_minute");
    oElement2 = document.getElementById("endDate_hour");
    oElement3 = document.getElementById("endDate_minute");

    YAHOO.util.Event.addListener(oElement, "change", updateOmarFilters);
    YAHOO.util.Event.addListener(oElement1, "change", updateOmarFilters);
    YAHOO.util.Event.addListener(oElement2, "change", updateOmarFilters);
    YAHOO.util.Event.addListener(oElement3, "change", updateOmarFilters);

    ddTab.title               = "Please enter the search parameters in Decimal Degrees (DD) format";
    dmsTab.title              = "Please enter the search parameters in Degrees Minutes Seconds (DMS) format.  DMS is formatted as ddmmss.ssssN|S or dd mm ss.sss N|S for latitude and longitude can have up to 3 digits for the degree indicator and can be formatted as dddmmss.ssssN|S or ddd mm ss.sss N|S";
    mgrsTab.title             = "Please enter the search parameters in Military Grid Reference System (MGRS) format.  MGRS formatted input is of the form [grid zone designator][100,000-meter square identifier][easting][northing].";
    metadataCriteriaTab.title = "Please select from the list of meta data you would like to search";
    cqlCriteriaTab.title      = "Please enter the Common Query Language (CQL) statements";
  }
  function handleClickSpatialTab0(e) {
  updateCurrentSpatialTab(0);
  }
  function handleClickSpatialTab1(e) {
  updateCurrentSpatialTab(1);
  }
  function handleClickSpatialTab2(e) {
  updateCurrentSpatialTab(2);
  }
  function handleClickCriteriaTab0(e) {
  updateCurrentTab(0);
  }
  function handleClickCriteriaTab1(e) {
  updateCurrentTab(1);
  }
  function updateCurrentTab(tabIndex)
  {
    var link = "${createLink(action: 'updateSession', controller: 'session')}";
    if(tabIndex != videoSearchCriteriaIndex)
    {
      videoSearchCriteriaIndex = tabIndex;

      new OpenLayers.Ajax.Request(link+"?"+"videoSearchCriteriaTab="+videoSearchCriteriaIndex, {method: 'post',
            onCreate: function(transport) {
             }

      });
    }
  }
  function updateCurrentSpatialTab(tabIndex)
  {
    var link = "${createLink(action: 'updateSession', controller: 'session')}";
    if(tabIndex != videoSearchSpatialIndex)
    {
      videoSearchSpatialIndex = tabIndex;

      new OpenLayers.Ajax.Request(link+"?"+"videoSearchSpatialTab="+videoSearchSpatialIndex, {method: 'post',
            onCreate: function(transport) {
             }

      });
    }
  }
    function measureUnitChanged(unit)
    {
        if(unit == 'kilometers')
        {
            pathMeasurement.innerHTML = mapWidget.getPathUnit()[0];
        }
        else if(unit == 'meters')
        {
            pathMeasurement.innerHTML = mapWidget.getPathUnit()[1];
        }
        else if(unit == 'feet')
        {
            pathMeasurement.innerHTML = mapWidget.getPathUnit()[2];
        }
        else if(unit == 'miles')
        {
            pathMeasurement.innerHTML = mapWidget.getPathUnit()[3];
        }
        else if(unit == 'yards')
        {
            pathMeasurement.innerHTML = mapWidget.getPathUnit()[4];
        }

    }
  function updateOmarFilters()
  {
    if(!mapWidget) return;

    var numberOfNames = parseInt("${queryParams?.searchTagNames.size()}");
    var numberOfValues = parseInt(${queryParams?.searchTagValues.size()});

    var ogcFilterInput = document.getElementById('filter');
    var additionalParams = new Array();

    if(ogcFilterInput)
    {
        additionalParams['filter']=ogcFilterInput.value;
    }
    mapWidget.updateOmarFilters(
        $("startDate_day").value, $("startDate_month").value, $("startDate_year").value, $("startDate_hour").value, $("startDate_minute").value,
        $("endDate_day").value, $("endDate_month").value, $("endDate_year").value, $("endDate_hour").value, $("endDate_minute").value,
        numberOfNames, numberOfValues, additionalParams
        );
  }
  function setupBaseLayers()
  {
    if(!mapWidget) return;
        var baseLayer = null;
        var baseWMS=${baseWMS as JSON};

    for ( layer in baseWMS ) {
      baseLayer = new OpenLayers.Layer.WMS(baseWMS[layer].name, baseWMS[layer].url,
              baseWMS[layer].params, baseWMS[layer].options);

      if(baseWMS[layer].options.isBaseLayer)
      {
        mapWidget.setupBaseLayers(baseLayer);
      }
      else
      {
        mapWidget.getMap().addLayer(baseLayer);
      }
    }
  }
</g:javascript>

</body>
</html>