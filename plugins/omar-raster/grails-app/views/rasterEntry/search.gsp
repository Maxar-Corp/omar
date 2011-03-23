<%@ page import="grails.converters.JSON; org.ossim.omar.BaseQuery; org.ossim.omar.RasterEntryQuery; org.ossim.omar.RasterEntrySearchTag" contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>OMAR: Raster Search</title>
  <meta name="layout" content="searchStatic"/>
  <openlayers:loadMapToolBar/>
  <openlayers:loadTheme theme="default"/>
  <openlayers:loadJavascript/>
    <style>
    #homeMenu{
    background: url( ../images/skin/house.png )  left no-repeat;
       z-index: 99999;
       }
    #exportMenu, #viewMenu, .datechooser{
        z-index: 100;
    }

    </style>
</head>

<body class="yui-skin-sam" onload="init();">
<omar:bundle contentType="javascript" files="${[
  [dir:'js', file: 'application.js'],
  [plugin: 'omar-core', dir: 'js', file: 'mapwidget.js'],
  [plugin: 'omar-core', dir: 'js', file: 'coordinateConversion.js'],
  [plugin:'richui' , dir:'js/yui/element', file: 'element-min.js'],
  [plugin:'richui' , dir:'js/yui/tabview/', file: 'tabview-min.js'],
]}"/>

<content tag="top">
    <g:form name="searchForm">
    </g:form>
    <div id="searchMenu" class="yuimenubar yuimenubarnav">
        <div class="bd">
            <ul class="first-of-type">
                <li class="yuimenubaritem first-of-type"><a class="yuimenubaritemlabel" id="homeMenu" href="${createLink(controller: 'home', action: 'index')}" title="OMAR™ Home">&nbsp;&nbsp;&nbsp;&nbsp;OMAR™ Home</a>
                </li>
                <li class="yuimenubaritem first-of-type"><a class="yuimenubaritemlabel" href="#exportMenu">Export</a>
                    <div id="exportMenu" class="yuimenu">
                        <div class="bd">
                            <ul>
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="javascript:generateKml()" title="Export KML">KML Query</a></li>
                            </ul>
                          </div>
                    </div>
                </li>
                <li class="yuimenubaritem first-of-type"><a class="yuimenubaritemlabel" href="#viewMenu">View</a>
                    <div id="viewMenu" class="yuimenu">
                        <div class="bd">
                            <ul>
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="javascript:updateOmarFilters();" title="Refresh the footprints">Refresh Footprints</a></li>
                                <li class="yuimenuitem"><a class="yuimenuitemlabel" href="javascript:search();" title="Execute the search for the specified criteria">Search</a></li>
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
        <li class="selected"><a href="#spatialTab1"><em>DD</em></a></li>
        <li><a href="#spatialTab2"><em>DMS</em></a></li>
        <li><a href="#spatialTab3"><em>MGRS</em></a></li>
      </ul>
      <div class="yui-content">
        <div id="spatialTab1">
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
                    <g:textField id="aoiRadius1" name="aoiRadius" value="${fieldValue(bean: queryParams, field: 'aoiRadius')}" onChange="updateOmarFilters();syncAoiRadius('aoiRadius1')"/>
                </li>
                <li>
                  <br/>
                </li>
                <li>
                    <button type="button"  onclick="javascript:mapWidget.setCenterDd();">Set Center</button>
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
                  <label>Use Bound Box Search</label>
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
                    <button type="button"  onclick="javascript:mapWidget.clearAOI();">Clear</button>
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
                    <g:textField id="aoiRadius2" name="aoiRadius" value="${fieldValue(bean: queryParams, field: 'aoiRadius')}" onChange="updateOmarFilters();syncAoiRadius('aoiRadius2')"/>
                </li>
                <li>
                  <br/>
                </li>
                <li>
                    <button type="button"  onclick="javascript:mapWidget.setCenterDms();">Set Center</button>
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
                  <label>Use Bound Box Search</label>
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
                    <button type="button"  onclick="javascript:mapWidget.clearAOI();">Clear</button>
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
                    <g:textField id="aoiRadius3" name="aoiRadius" value="${fieldValue(bean: queryParams, field: 'aoiRadius')}" onChange="updateOmarFilters();syncAoiRadius('aoiRadius3')"/>
                </li>
                <li>
                  <br/>
                </li>
                <li>
                    <button type="button"  onclick="javascript:mapWidget.setCenterMgrs();">Set Center</button>
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
                  <label>Use Bound Box Search</label>
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
                    <button type="button"  onclick="javascript:mapWidget.clearAOI();">Clear</button>
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

	<div id="criteriaTab" class="yui-navset">
        <p/>
	    <ul class="yui-nav">
	        <li class="selected"><a href="#criteriaTab1"><em>Metadata</em></a></li>
	        <li><a href="#criteriaTab2"><em>CQL</em></a></li>
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
		                from="${RasterEntrySearchTag.list(sort:'description')}"
		                optionKey="name" optionValue="description"/>
		            <li>
		              <g:textField id="searchTagValues[${i}]" name="searchTagValues[${i}]" value="${searchTagValue}" onChange="updateOmarFilters()"/>
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
		             <g:textArea id="filter" name="filter" value="${queryParams.filter}" style='width: 100%; height: 200px;' onChange="updateOmarFilters()"/>
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
            <input type="text" id="max" name="max" value="${params?.max}"/>
          </li>
        </ol>
      </div>
    </div>
	<div align="center">
        <button type="button"  onclick="javascript:search();">Search</button>
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
                  title="Select a unit of measuremen and use the path and polygon measurment tools in the map toolbar." onChange="measureUnitChanged(this.value)"/></li>
              <div id="pathMeasurement"></div>
              <div id="polygonMeasurement"></div>
          </ul>
      </div>
    </div>
  <g:render plugin="omar-core" template="/common/olLayerSwitcherTemplate"/>
</content>
<g:javascript>
  var mapWidget = null;
  var oElement  = null;
  var oElement1 = null;
  var oElement2 = null;
  var oElement3 = null;
  var tabView   = null;
  var criteriaTabView = null;
  var rasterSearchSpatialIndex=${session.rasterSearchSpatialTab?:0};
  var rasterSearchCriteriaIndex=${session.rasterSearchCriteriaTab?:0};
  var omarSearchParams = new OmarSearchParams();
  function syncAoiRadius(synchTo)
  {
    value = $(synchTo).value;
    $("aoiRadius1").value = value;
    $("aoiRadius2").value = value;
    $("aoiRadius3").value = value;

  }
  function search()
  {
    var url = "${createLink(action: 'search', controller: 'rasterEntry')}";
    mapWidget.setupSearch();
    omarSearchParams.setProperties(document);
    omarSearchParams.initTime();
    omarSearchParams.time = "";
    if($( "bboxSearchButton" ).checked)
    {
        omarSearchParams.searchMethod = "${BaseQuery.BBOX_SEARCH}"
    }
    else
    {
        omarSearchParams.searchMethod = "${BaseQuery.RADIUS_SEARCH}"
    }
    document.searchForm.action = url + "?" + omarSearchParams.toUrlParams();
    document.searchForm.submit();
  }
  function generateKml()
  {
    var url = "${createLink(action: 'kmlnetworklink', controller: 'rasterEntry')}";
    mapWidget.setupSearch();
    omarSearchParams.initTime();
    omarSearchParams.setProperties(document);
    document.searchForm.action = url + "?" + omarSearchParams.toUrlParams();
    //alert(document.searchForm.action );
    document.searchForm.submit();
  }
  function init()
  {
    var oMenu = new YAHOO.widget.MenuBar("searchMenu", {
                                                autosubmenudisplay: true,
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
    criteriaTabView.selectTab(rasterSearchCriteriaIndex);
    var spatialTab0 = spatialTabView.getTab(0);
    var spatialTab1 = spatialTabView.getTab(1);
    var spatialTab2 = spatialTabView.getTab(2);
    spatialTab0.addListener('click', handleClickSpatialTab0);
    spatialTab1.addListener('click', handleClickSpatialTab1);
    spatialTab2.addListener('click', handleClickSpatialTab2);
    spatialTabView.selectTab(rasterSearchSpatialIndex);

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
    if(tabIndex != rasterSearchCriteriaIndex)
    {
      rasterSearchCriteriaIndex = tabIndex;

      new OpenLayers.Ajax.Request(link+"?"+"rasterSearchCriteriaTab="+rasterSearchCriteriaIndex, {method: 'post',
            onCreate: function(transport) {
             }

      });
    }
  }
  function updateCurrentSpatialTab(tabIndex)
  {
    var link = "${createLink(action: 'updateSession', controller: 'session')}";
    if(tabIndex != rasterSearchSpatialIndex)
    {
      rasterSearchSpatialIndex = tabIndex;

      new OpenLayers.Ajax.Request(link+"?"+"rasterSearchSpatialTab="+rasterSearchSpatialIndex, {method: 'post',
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