<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: 1/30/12
  Time: 3:29 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="grails.converters.JSON; org.ossim.omar.core.BaseQuery; org.ossim.omar.raster.RasterEntryQuery; org.ossim.omar.raster.RasterEntrySearchTag" contentType="text/html;charset=UTF-8" %>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>OMAR <g:meta name="app.version"/>: Raster Search</title>
  <meta content="searchPageLayout" name="layout">
  <r:require modules="searchPageLayout"/>
</head>

<body class=" yui-skin-sam">
<g:form name="searchForm">

  <content tag="top1">
    <omar:securityClassificationBanner/>
    <omar:logout/>
    <g:render plugin="omar-core" template="/common/searchMenuTemplate"/>
  </content>

  <content tag="bottom1">
    <omar:securityClassificationBanner/>
  </content>

  <content tag="right1">
    <g:render plugin="omar-core" template="/common/mensurationTemplate"/>
    <g:render plugin="omar-core" template="/common/olLayerSwitcherTemplate"/>
    <g:render plugin="omar-core" template="/common/footprintLegendTemplate" model="${[style: footprintStyle]}"/>

  </content>

  <content tag="left1">
    <g:render plugin="omar-core" template="/common/geospatialCriteriaTemplate"/>
    <g:render plugin="omar-core" template="/common/dateTimeCriteriaTemplate"/>
    <g:render template="rasterEntryMetadataCriteriaTemplate"/>
    <div align="center">
      <span id="linkbutton1" class="yui-button yui-link-button" title="Search Rasters">
        <span class="first-child">
          <a href="javascript:search();">Search</a>
        </span>
      </span>
    </div>
  </content>


  <content tag="top2">
    <div id="toolBar" class="olControlPanel"></div>
    <span id="linkbutton2" class="yui-button yui-link-button" title="Search Rasters">
      <span class="first-child">
        <a href="javascript:search();">Search</a>
      </span>
    </span>
  </content>

  <content tag="bottom2">
    <table><tr>
      <td width="33%"><div id="ddMousePosition">&nbsp;</div></td>
      <td width="33%"><div id="dmsMousePosition">&nbsp;</div></td>
      <td width="33%"><div id="mgrsMousePosition">&nbsp;</div></td>
    </tr></table>
  </content>
</g:form>

<content tag="center2">
  <div id="map"></div>
</content>

<r:script>
		var omar;
		var omarSearchParams;

		var init = function() {

      omarInit();

      omar = new Omar();
      omarSearchParams = new OmarSearchParams();
			
			updateGeoTab();
			
			omar.setupMapWidget();

			var baseWMS=${baseWMS as JSON};
			for(var i = 0; i < baseWMS.length; i++ ) {
				omar.setupBaseLayers(baseWMS[i].name, baseWMS[i].url, baseWMS[i].params, baseWMS[i].options);
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
			if ("${params?.startDate_month}" || "${params?.startDate_day}" || "${params?.startDate_year}")
			{
				$("startDateInput").value = "${params?.startDate_month ?: null}/${params?.startDate_day ?: null}/${params?.startDate_year ?: null}";
				
				$("startDate_month").value = "${params?.startDate_month ?: null}";
				$("startDate_day").value = "${params?.startDate_day ?: null}";
				$("startDate_year").value = "${params?.startDate_year ?: null}";
				
				$("startDate_hour").value = "${params?.startDate_hour ?: '00'}";
				$("startDate_minute").value = "${params?.startDate_minute ?: '00'}";
			}
			
			if ("${params?.endDate_month}" || "${params?.endDate_day}" || "${params?.endDate_year}")
			{
				$("endDateInput").value = "${params?.endDate_month ?: null}/${params?.endDate_day ?: null}/${params?.endDate_year ?: null}";
				
				$("endDate_month").value = "${params?.endDate_month ?: null}";
				$("endDate_day").value = "${params?.endDate_day ?: null}";
				$("endDate_year").value = "${params?.endDate_year ?: null}";
				
				$("endDate_hour").value = "${params?.endDate_hour ?: '00'}";
				$("endDate_minute").value = "${params?.endDate_minute ?: '00'}";
			}
			
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

			omarSearchParams.searchMethod = "${org.ossim.omar.core.BaseQuery.BBOX_SEARCH}"
			if($("baseQueryType").value == "RADIUS") {
				omarSearchParams.searchMethod = "${org.ossim.omar.core.BaseQuery.RADIUS_SEARCH}";
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

			omarSearchParams.searchMethod = "${org.ossim.omar.core.BaseQuery.BBOX_SEARCH}"
			if($("baseQueryType").value == "RADIUS") {
				omarSearchParams.searchMethod = "${org.ossim.omar.core.BaseQuery.RADIUS_SEARCH}";
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
		
		var tabView = new YAHOO.widget.TabView( 'tabview1', { activeIndex: ${rasterEntrySearchCurrentTab1} } );
		tabView.selectTab(${rasterEntrySearchCurrentTab1});
		var tab0 = tabView.getTab( 0 );
	    var tab1 = tabView.getTab( 1 );
	    tab0.addListener( 'click', updateGeoTab );
	    tab1.addListener( 'click', updateGeoTab );
		
		var tabView2 = new YAHOO.widget.TabView( 'tabview2', { activeIndex: ${rasterEntrySearchCurrentTab2} } );
		tabView2.selectTab(${rasterEntrySearchCurrentTab2});
		var tab2 = tabView2.getTab( 0 );
	    var tab3 = tabView2.getTab( 1 );
	    tab2.addListener( 'click', updateMetaTab );
	    tab3.addListener( 'click', updateMetaTab );
		
		function updateMetaTab() {
			updateCurrentTab("rasterEntrySearchCurrentTab2", tabView2.get('activeIndex'));
		}
		
		function updateGeoTab() {
			updateCurrentTab("rasterEntrySearchCurrentTab1", tabView.get('activeIndex'));
			
			if (tabView.get('activeIndex') == "0") {
				//alert('radius');
			 	$( "baseQueryType" ).value = "RADIUS";
			}
		 	else if (tabView.get('activeIndex') == "1") {
				//alert('bbox');
				$( "baseQueryType" ).value = "BBOX";
			}
			
		}
		
		function updateCurrentTab(variable, tabIndex)
	  	{
			var link = "${createLink(action: sessionAction, controller: sessionController)}";
	      	new Ajax.Request(link+"?"+variable+"="+tabIndex, {method: 'post'});
	  	}
		
	</r:script>
</body>
</html>