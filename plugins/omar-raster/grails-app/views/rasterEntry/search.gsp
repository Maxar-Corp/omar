<%@ page import="grails.converters.JSON; org.ossim.omar.BaseQuery; org.ossim.omar.RasterEntryQuery; org.ossim.omar.RasterEntrySearchTag" contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>OMAR: Raster Search</title>
  <meta name="layout" content="searchStatic"/>
  <openlayers:loadMapToolBar/>
  <openlayers:loadTheme theme="default"/>
  <openlayers:loadJavascript/>
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
    <div id="demo" class="yui-navset">
      <ul class="yui-nav">
        <li class="selected"><a href="#demoTab1"><em>DD</em></a></li>
        <li><a href="#demoTab2"><em>DMS</em></a></li>
        <li><a href="#demoTab3"><em>MGRS</em></a></li>
      </ul>
      <div class="yui-content">
        <div id="demoTab1">
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
                    <span class="yui-button yui-link-button"><span class="first-child"><g:link url="javascript:mapWidget.setCenterDd()">Set Center</g:link></span></span>
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
                    <span class="yui-button yui-link-button"><span class="first-child"><g:link url="javascript:mapWidget.clearAOI();">Clear AOI</g:link></span></span>
                </li>
              </ol>
            </div>
          </div>
        </div>
        <div id="demoTab2">
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
                    <span class="yui-button yui-link-button"><span class="first-child"><g:link url="javascript:mapWidget.setCenterDms()">Set Center</g:link></span></span>
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
                    <span class="yui-button yui-link-button"><span class="first-child"><g:link url="javascript:mapWidget.clearAOI();">Clear AOI</g:link></span></span>
                </li>
              </ol>
            </div>
          </div>
        </div>
        <div id="demoTab3">
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
                    <span class="yui-button yui-link-button"><span class="first-child"><g:link url="javascript:mapWidget.setCenterMgrs()">Set Center</g:link></span></span>
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
                    <%--
                  <span class="formButton">
                      <input type="button" onclick="mapWidget.clearAOI()" value="Clear AOI">
                  </span>
                  --%>
                    <span class="yui-button yui-link-button"><span class="first-child"><g:link url="javascript:mapWidget.clearAOI();">Clear AOI</g:link></span></span>
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
        <span class="yui-button yui-link-button"><span class="first-child"><g:link url="javascript:search();">Search </g:link></span></span>
</div>


<%--  </g:form>  --%>
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
  var mapWidget = null;
  var oElement  = null;
  var oElement1 = null;
  var oElement2 = null;
  var oElement3 = null;
  var tabView   = null;
  var criteriaTabView = null;
  var rasterSearchCriteriaIndex=${session.rasterSearchCriteriaTab?:0};
  var omarSearchParams = new OmarSearchParams();
  function search()
  {
    var url = "${createLink(action: 'search', controller: 'rasterEntry')}";
    mapWidget.setupSearch();
    omarSearchParams.setProperties(document);

    omarSearchParams.setTimeFromDate({day:$("startDate_day").value,
                             month:$("startDate_month").value,
                             year:$("startDate_year").value,
                             hour:$("startDate_hour").value,
                             minute:$("startDate_minute").value,
                             sec:""
                             },
                              {day:$("endDate_day").value,
                             month:$("endDate_month").value,
                             year:$("endDate_year").value,
                             hour:$("endDate_hour").value,
                             minute:$("endDate_minute").value,
                             sec:""
                             });
    document.searchForm.action = url + "?" + omarSearchParams.toUrlParams();
    document.searchForm.submit();
  }
  function generateKml()
  {
    var url = "${createLink(action: 'kmlnetworklink', controller: 'rasterEntry')}";
    mapWidget.setupSearch();
    omarSearchParams.setProperties(document);

    omarSearchParams.setTimeFromDate({day:$("startDate_day").value,
                             month:$("startDate_month").value,
                             year:$("startDate_year").value,
                             hour:$("startDate_hour").value,
                             minute:$("startDate_minute").value,
                             sec:""
                             },
                              {day:$("endDate_day").value,
                             month:$("endDate_month").value,
                             year:$("endDate_year").value,
                             hour:$("endDate_hour").value,
                             minute:$("endDate_minute").value,
                             sec:""
                             });
    document.searchForm.action = url + "?" + omarSearchParams.toUrlParams();
    document.searchForm.submit();
  }
  function init()
  {

    tabView = new YAHOO.widget.TabView('demo');
    criteriaTabView = new YAHOO.widget.TabView('criteriaTab');

    var tab0 = criteriaTabView.getTab(0);
    var tab1 = criteriaTabView.getTab(1);
    tab0.addListener('click', handleClickCriteriaTab0);
    tab1.addListener('click', handleClickCriteriaTab1);
    criteriaTabView.selectTab(rasterSearchCriteriaIndex);

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
    var oMenu = new YAHOO.widget.MenuBar("searchMenu", {
                                                autosubmenudisplay: true,
                                                hidedelay: 750,
                                                lazyload: true,
                                                zIndex:9999});
	oMenu.render();
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

      mapWidget.setupBaseLayers(baseLayer);
    }
  }
 </g:javascript>

</body>
</html>