<%@ page import="grails.converters.JSON; org.ossim.omar.BaseQuery; org.ossim.omar.RasterEntryQuery; org.ossim.omar.RasterEntrySearchTag" contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>OMAR™ <g:meta name="app.version"/>: Raster Search</title>
  <meta name="layout" content="yuiLayout"/>
</head>

<body class="yui-skin-sam">
<g:form name="searchForm">
  <content tag="top">
    <omar:securityClassificationBanner/>
    <g:render plugin="omar-core" template="/common/searchMenuTemplate"/>
  </content>

  <content tag="left">
    <g:render plugin="omar-core" template="/common/geospatialCriteriaTemplate"/>
    <g:render plugin="omar-core" template="/common/dateTimeCriteriaTemplate"/>
    <g:render plugin="omar-core" template="/common/rasterEntryMetadataCriteriaTemplate"/>
    <div align="center">
      <span id="linkbutton1" class="yui-button yui-link-button" title="Search Rasters"><span class="first-child"><a
          href="javascript:search();">Search Rasters</a></span></span>
    </div>
  </content>

  <content tag="center">
    <table>
      <tr>
        <td colspan=3 id="top2"><div id="toolBar" class="olControlPanel"></div><span id="linkbutton2"
                                                                                     class="yui-button yui-link-button"
                                                                                     title="Search Rasters"><span
              class="first-child"><a href="javascript:search();">Search Rasters</a></span></span></td>
      </tr>
      <tr>
        <td colspan=3 id="middle2"><div id="map"></div></td>
      </tr>
      <tr id="bottom2">
        <td width="33%"><div id="ddMousePosition">&nbsp;</div></td>
        <td width="33%"><div id="dmsMousePosition">&nbsp;</div></td>
        <td width="33%"><div id="mgrsMousePosition">&nbsp;</div></td>
      </tr>
    </table>
  </content>

  <content tag="right">
    <g:render plugin="omar-core" template="/common/mensurationTemplate"/>
    <g:render plugin="omar-core" template="/common/olLayerSwitcherTemplate"/>
  </content>

  <content tag="bottom">
    <omar:securityClassificationBanner/>
  </content>
</g:form>

<omar:bundle contentType="javascript" files="${[
    [plugin: 'omar-core', dir: 'js', file: 'omar.js'],
    [plugin: 'omar-core', dir: 'js', file: 'coord.js']
]}"/>

<g:javascript>
		var omar;
		var omarSearchParams;
		
		var init = function() {

      omar = new Omar();
      omarSearchParams = new OmarSearchParams();

			useBoundBoxSearch();
			
			omar.setupMapWidget();
			
			var baseWMS=${baseWMS as JSON};
			for(layer in baseWMS) {
				omar.setupBaseLayers(baseWMS[layer].name, baseWMS[layer].url, baseWMS[layer].params, baseWMS[layer].options);
			};
			
			omar.setupDataLayer("${dataWMS.name}", "${dataWMS.url}", "${dataWMS.params.layers}", "${dataWMS.options.styles}", "${dataWMS.params.format}");
			
			omar.setupGraticuleLayers();
			
			omar.setupAoiLayer();
			
			omar.setupToolBar();
			
			omar.changeMapSize();
			
			// preserve view
			omar.setupMapView("${queryParams?.viewMinLon ?: -180}", "${queryParams?.viewMinLat ?: -90}", "${queryParams?.viewMaxLon ?: 180}", "${queryParams?.viewMaxLat ?: 90}");
			
			// presereve bound box
			var aoiMinLon = ${queryParams?.aoiMinLon ?: 'null'};
			var aoiMinLat = ${queryParams?.aoiMinLat ?: 'null'};
			var aoiMaxLon = ${queryParams?.aoiMaxLon ?: 'null'};
			var aoiMaxLat = ${queryParams?.aoiMaxLat ?: 'null'};
			if(aoiMinLon && aoiMinLat && aoiMaxLon && aoiMaxLat) {
				omar.setupAoi(aoiMinLon, aoiMinLat, aoiMaxLon, aoiMaxLat);
			}
			
			// preserve point radius
			if("${queryParams?.centerLat}" && "${queryParams?.centerLon}") {
				var centerLat = "${queryParams?.centerLat ?: null}";
				var centerLon = "${queryParams?.centerLon ?: null}";
				$("point").value = centerLat + ", " + centerLon;
				$("radius").value = "${queryParams?.aoiRadius ?: null}";
			}
			
			//omar.getCurrentDateTime();
			
			// preserve date / time
			//
			//
			
			var startDate_hour = document.getElementById("startDate_hour");
			var startDate_minute = document.getElementById("startDate_minute");
			var endDate_hour = document.getElementById("endDate_hour");
			var endDate_minute = document.getElementById("endDate_minute");
			
			YAHOO.util.Event.addListener(startDate_hour, "change", updateOmarFilters);
			YAHOO.util.Event.addListener(startDate_minute, "change", updateOmarFilters);
			YAHOO.util.Event.addListener(endDate_hour, "change", updateOmarFilters);
			YAHOO.util.Event.addListener(endDate_minute, "change", updateOmarFilters);
			
			updateOmarFilters();
		};
		
		var search = function() {
			omar.setupSearch();
			
			omarSearchParams.setProperties(document);
			omarSearchParams.initTime();
			omarSearchParams.time = "";
			
			omarSearchParams.searchMethod = "${BaseQuery.BBOX_SEARCH}"
			if($("baseQueryType").value == "RADIUS") {
				omarSearchParams.searchMethod = "${BaseQuery.RADIUS_SEARCH}";
			}
			
			var url = "${createLink(action: 'search', controller: 'rasterEntry')}";
			document.searchForm.action = url + "?" + omarSearchParams.toUrlParams();
			document.searchForm.submit();
		};
		
		var generateKml = function() {
			omar.setupSearch();
			
			omarSearchParams.setProperties(document);
			omarSearchParams.initTime();
			omarSearchParams.time = "";
			
			omarSearchParams.searchMethod = "${BaseQuery.BBOX_SEARCH}"
			if($("baseQueryType").value == "RADIUS") {
				omarSearchParams.searchMethod = "${BaseQuery.RADIUS_SEARCH}";
			}
			
			var url = "${createLink(action: 'kmlnetworklink', controller: 'rasterEntry')}";
			document.searchForm.action = url + "?" + omarSearchParams.toUrlParams();
			document.searchForm.submit();
		};
		
		this.clearDateTime = function() {
			$("startDateInput").value = "";
			$("startDate_day").value = "";
			$("startDate_month").value = "";
			$("startDate_year").value = "";
			$("startDate_hour").value = "00";
			$("startDate_minute").value = "00";
			
			$("endDateInput").value = "";
			$("endDate_day").value = "";
			$("endDate_month").value = "";
			$("endDate_year").value = "";
			$("endDate_hour").value = "00";
			$("endDate_minute").value = "00";
			
			updateOmarFilters();
		};
		
		var updateOmarFilters = function() {
			var numberOfNames = parseInt("${queryParams?.searchTagNames.size()}");
			var numberOfValues = parseInt("${queryParams?.searchTagValues.size()}");
			
			var ogcFilterInput = document.getElementById("filter");
			var additionalParams = new Array();
			
		    if(ogcFilterInput) {
				additionalParams['filter']=ogcFilterInput.value;
			}
			
			omar.updateOmarFilters($("startDate_day").value, $("startDate_month").value, $("startDate_year").value, $("startDate_hour").value, $("startDate_minute").value, $("endDate_day").value, $("endDate_month").value, $("endDate_year").value, $("endDate_hour").value, $("endDate_minute").value, numberOfNames, numberOfValues, additionalParams);
		};
</g:javascript>
</body>
</html>