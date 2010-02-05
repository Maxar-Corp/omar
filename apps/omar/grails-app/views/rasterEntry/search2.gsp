<%--
  Created by IntelliJ IDEA.
  User: dlucas
  Date: Nov 23, 2009
  Time: 8:33:02 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>OMAR - Raster Search</title>

  <openlayers:loadTheme theme="default"/>
  <openlayers:loadMapToolBar/>
  <openlayers:loadJavascript/>

  <g:javascript>
    var aoiLayer;
    var polygonControl;
    var map;
    var dataLayer;


    function changeMapSize()
    {
      var mapTitle = $("mapTitle");
      var mapDiv = $("map");

      mapDiv.style.width = mapTitle.offsetWidth + "px";
      mapDiv.style.height = Math.round( mapTitle.offsetWidth / 2) + "px";

      map.updateSize();
    }

    function setupDataLayer()
    {
      dataLayer = new OpenLayers.Layer.WMS(
        "${dataWMS.title}",
        "${dataWMS.url}",
        { layers: "${dataWMS.layers}", format: "${dataWMS.format}", IMAGEFILTER: "true=true", transparent: true },
        {'isBaseLayer': false},
        {buffer:1}
      );
      map.addLayer(dataLayer);
    }

    function setupBaseLayer()
    {
      var baseLayer = new OpenLayers.Layer.WMS(
        "${baseWMS.title}",
        "${baseWMS.url}",
        {layers: '${baseWMS.layers}', format: "${baseWMS.format}" },
        {buffer:1}
      );
      map.addLayer(baseLayer);
      map.setBaseLayer(baseLayer);
    }

    function setupAoiLayer()
    {

      aoiLayer = new OpenLayers.Layer.Vector("Area of Interest");
      aoiLayer.events.register("featureadded", aoiLayer, setAOI );

      var polyOptions = {sides: 4, irregular: true} ;

      polygonControl = new OpenLayers.Control.DrawFeature( aoiLayer, OpenLayers.Handler.RegularPolygon,
         {handlerOptions: polyOptions});

      map.addLayer(aoiLayer);
      map.addControl(polygonControl);

      var aoiMinLon = "${queryParams?.aoiMinLon ?: ''}";
      var aoiMinLat = "${queryParams?.aoiMinLat ?: ''}";
      var aoiMaxLon = "${queryParams?.aoiMaxLon ?: ''}";
      var aoiMaxLat = "${queryParams?.aoiMaxLat ?: ''}";

      if ( aoiMinLon && aoiMinLat && aoiMaxLon && aoiMaxLat )
      {
        var bounds = new OpenLayers.Bounds( aoiMinLon, aoiMinLat, aoiMaxLon, aoiMaxLat );
        var feature = new OpenLayers.Feature.Vector(bounds.toGeometry());

        aoiLayer.addFeatures(feature, {silent: true});
      }
    }

    function setupMapWidget()
    {
       map = new OpenLayers.Map("map", { controls: [] });
       map.addControl(new OpenLayers.Control.LayerSwitcher());
       //map.addControl(new OpenLayers.Control.PanZoom());
       //map.addControl(new OpenLayers.Control.NavToolbar());
       map.addControl(new OpenLayers.Control.MousePosition());
       map.addControl(new OpenLayers.Control.Scale());
       //map.addControl(new OpenLayers.Control.Permalink("permalink"));
       map.addControl(new OpenLayers.Control.ScaleLine());
       map.addControl(new OpenLayers.Control.Attribution());
       map.events.register("moveend", map, setCenterText);
       map.events.register("zoomend", map, setView );
    }

    function setupMapView()
    {
      var viewMinLon = "${queryParams?.viewMinLon ?: -180}";
      var viewMinLat = "${queryParams?.viewMinLat ?: -90}";
      var viewMaxLon = "${queryParams?.viewMaxLon ?: 180}";
      var viewMaxLat = "${queryParams?.viewMaxLat ?: 90}";

      var bounds = new OpenLayers.Bounds(viewMinLon, viewMinLat, viewMaxLon, viewMaxLat);
      var zoom = map.getZoomForExtent(bounds, true);


      map.setCenter(bounds.getCenterLonLat(), zoom);

    }

    function zoomIn()
    {
      map.zoomIn();
    }

    function zoomOut()
    {
      map.zoomOut();

    }
      function setupToolbar()
      {
        var zoomBoxButton = new OpenLayers.Control.ZoomBox(
        {title:"Zoom into an area by clicking and dragging"});

        var zoomInButton = new OpenLayers.Control.Button({title:'Zoom in',
          displayClass: "olControlZoomIn",
          trigger: zoomIn
        });

        var zoomOutButton = new OpenLayers.Control.Button({title:'Zoom out',
          displayClass: "olControlZoomOut",
          trigger: zoomOut
        });

        var polyOptions = {sides: 4, irregular: true};

        var polygonControl = new OpenLayers.Control.DrawFeature(aoiLayer, OpenLayers.Handler.RegularPolygon,
        {handlerOptions: polyOptions, title: "Specify Area of Interest"});

        var clearAoiButton = new OpenLayers.Control.Button({title:'Clear Area of Interest',
          displayClass: "olControlClearAreaOfInterest",
          trigger: clearAOI
        });

        var container = $("panel2");

        var panel = new OpenLayers.Control.Panel(
        { div: container,defaultControl: zoomBoxButton,'displayClass': 'olControlPanel'}
                );


        var navButton = new OpenLayers.Control.NavigationHistory({
          nextOptions: {title: "Next View" },
          previousOptions: {title: "Previous View"}
        });

        map.addControl(navButton);

        panel.addControls([
          new OpenLayers.Control.MouseDefaults({title:'Drag to recenter map'}),
          zoomBoxButton,
          zoomInButton,
          zoomOutButton,
          navButton.next, navButton.previous,
          new OpenLayers.Control.ZoomToMaxExtent({title:"Zoom to the max extent"}),
          polygonControl,
          clearAoiButton
        ]);

        map.addControl(panel);
      }

    function init()
    {
       setupMapWidget();
       setupDataLayer();
       setupBaseLayer();
       changeMapSize();
       setupAoiLayer();
       setupToolbar();
       setupMapView();
       setupQueryFields();
       updateOmarFilters();
    }

    function goto()
    {
      var centerLon = $("centerLon").value;
      var centerLat = $("centerLat").value;
      var zoom = map.getZoom();
      var center = new OpenLayers.LonLat(centerLon, centerLat);

      map.setCenter(center, zoom);
    }

     function clearAOI( e )
      {
        aoiLayer.destroyFeatures();


        // HACK - Need a better way to this
        $("aoiMinLon").value = "";
        $("aoiMaxLat").value = "";
        $("aoiMaxLon").value = "";
        $("aoiMinLat").value = "";
      }

     function setAOI( e )
      {
        var geom = e.feature.geometry;
        var bounds = geom.getBounds();
        var feature = new OpenLayers.Feature.Vector(geom);

        // HACK - Need a better way to this
        $("aoiMinLon").value = bounds.left;
        $("aoiMaxLat").value = bounds.top;
        $("aoiMaxLon").value = bounds.right;
        $("aoiMinLat").value = bounds.bottom;

        aoiLayer.destroyFeatures();
        aoiLayer.addFeatures(feature, {silent: true});

      }

    function setView( e )
    {
      var bounds = map.getExtent();

      $("viewMinLon").value = bounds.left;
      $("viewMaxLat").value = bounds.top;
      $("viewMaxLon").value = bounds.right;
      $("viewMinLat").value = bounds.bottom;
    }

    function setCenterText( e )
    {
      var center = map.getCenter();
      $("centerLon").value = center.lon;
      $("centerLat").value = center.lat;
    }

    function setupQueryFields()
    {
      var searchMethod = "${queryParams.searchMethod}";

      if ( searchMethod == "${RasterEntryQuery.RADIUS_SEARCH}")
      {
        toggleRadiusSearch();
      }
      else if ( searchMethod == "${RasterEntryQuery.BBOX_SEARCH}" )
      {
        toggleBBoxSearch();
      }
    }

    function toggleRadiusSearch()
    {
      enableRadiusSearch();
      disableBBoxSearch();
    }

    function toggleBBoxSearch()
    {
      enableBBoxSearch();
      disableRadiusSearch();
    }

    function enableRadiusSearch()
    {
      $("aoiRadius").disabled = false;
    }

    function enableBBoxSearch()
    {
      $("aoiMinLon").disabled = false;
      $("aoiMinLat").disabled = false;
      $("aoiMaxLon").disabled = false;
      $("aoiMaxLat").disabled = false;
    }

    function disableRadiusSearch()
    {
      $("aoiRadius").disabled = true;
    }

    function disableBBoxSearch()
    {
      $("aoiMinLon").disabled = true;
      $("aoiMinLat").disabled = true;
      $("aoiMaxLon").disabled = true;
      $("aoiMaxLat").disabled = true;
    }

    function updateOmarFilters()
    {
      var sday = $("startDate_day").value;
      var smonth = $("startDate_month").value;
      var syear = $("startDate_year").value;
      var eday = $("endDate_day").value;
      var emonth = $("endDate_month").value;
      var eyear = $("endDate_year").value;

      var hasStartDate = sday != "" && smonth != "" && syear != "";
      var startDate = "'" + smonth + "-" + sday + "-" + syear + "'";

      var hasEndDate = eday != "" && emonth != "" && eyear != "";
      var endDate = "'" + emonth + "-" + eday + "-" + eyear + "'";

      if ( hasStartDate )
      {
        var omarfilter = "acquisition_date>=" + startDate;

        if ( hasEndDate )
        {
          omarfilter += " and acquisition_date<=" + endDate;
        }

        //alert(omarfilter);
        dataLayer.mergeNewParams({IMAGEFILTER: omarfilter });
      }
      else
      {
        if ( hasEndDate )
        {
          var omarfilter = "acquisition_date<=" + endDate;

          //alert(omarfilter);
          dataLayer.mergeNewParams({IMAGEFILTER: omarfilter });
        }
        else
        {
          var omarfilter = "true=true";

          //alert(omarfilter);
          dataLayer.mergeNewParams({IMAGEFILTER: omarfilter });
        }
      }
    }

    function setCurrentViewport()
    {
      var bounds = map.getExtent();
      $("viewMinLon").value = bounds.left;
      $("viewMaxLat").value = bounds.top;
      $("viewMaxLon").value = bounds.right;
      $("viewMinLat").value = bounds.bottom;
    }

    function searchForRasters()
    {
      document.searchForm.action = "search";
      setCurrentViewport();
      document.searchForm.submit();
    }

    function generateKML()
    {
      document.searchForm.action = "kmlnetworklink";
      setCurrentViewport();
      document.searchForm.submit();
    }

  <%--
  var Dom = YAHOO.util.Dom;
  var Event = YAHOO.util.Event;

  Event.onDOMReady(function()
  {
     init();
  });
  --%>
  </g:javascript>

  <!-- RichUI Date Chooser -->
  <resource:dateChooser/>

  <style type="text/css">
  div.datechooser {
    display: none;
    position: relative;
    left: 10px;
    top: 10px;
    z-index: 2
  }

  div.datechooser table.yui-calendar {
    width: 135px;
  }
  </style>

  <!-- YUI Layout Manager -->

  <gui:resources
          components="['accordion']"
          css="['reset_fonts_grids','resize','layout','button', 'container', 'accordion']"
          javascript="['yahoo', 'event', 'element', 'dom', 'dragdrop', 'resize', 'animation', 'layout', 'utilities']"/>

  <!-- Bubbling Accordion -->
  <style type="text/css">
  .myAccordion {
    float: left;
    margin-right: 15px;
  }

  .myAccordion .yui-cms-accordion .yui-cms-item {
    width: 167px;
    font-size: 11px;
  }
  </style>

</head>
<body class="yui-skin-sam" onload="init();">

<div id="top1">
  <omar:securityClassificationBanner/>
</div>

<div id="bottom1">
  <omar:securityClassificationBanner/>
</div>

<div id="left1">
  <g:form name="searchForm">
    <div class="myAccordion">
      <div class="yui-cms-accordion multiple fade fixIE">

        <div class="yui-cms-item yui-panel selected">
          <div class="hd">
            Map Center:
          </div>
          <div class="bd">
            <div class="fixed">
              Longitude:<br/>
              <g:textField name="centerLon" value="${queryParams?.centerLon}"/><br/>
              Latitude:<br/>
              <g:textField name="centerLat" value="${queryParams?.centerLat}"/><br/>

              &nbsp;<p/>

              <g:radio name="searchMethod" value="${RasterEntryQuery.RADIUS_SEARCH}" checked="${queryParams?.searchMethod == RasterEntryQuery.RADIUS_SEARCH}" onclick="toggleRadiusSearch()"/>
              Use Radius Search<br/>

              &nbsp;<p/>

              Radius in Meters:<br/>
              <g:textField name="aoiRadius" value="${fieldValue(bean: queryParams, field: 'aoiRadius')}"/><br/>

              &nbsp;<p/>

              <input type="button" onclick="goto()" value="Set Center">
            </div>
          </div>
          <div class="actions">
            <a href="#" class="accordionToggleItem">&nbsp;</a>
          </div>
        </div>

        <div class="yui-cms-item yui-panel selected">
          <div class="hd">
            Geographic Criteria:
          </div>
          <div class="bd">
            <div class="fixed">
              <input type="hidden" id="viewMinLon" name="viewMinLon" value="${fieldValue(bean: queryParams, field: 'viewMinLon')}"/>
              <input type="hidden" id="viewMinLat" name="viewMinLat" value="${fieldValue(bean: queryParams, field: 'viewMinLat')}"/>
              <input type="hidden" id="viewMaxLon" name="viewMaxLon" value="${fieldValue(bean: queryParams, field: 'viewMaxLon')}"/>
              <input type="hidden" id="viewMaxLat" name="viewMaxLat" value="${fieldValue(bean: queryParams, field: 'viewMaxLat')}"/>

              <g:radio name="searchMethod" value="${RasterEntryQuery.BBOX_SEARCH}" checked="${queryParams?.searchMethod == RasterEntryQuery.BBOX_SEARCH}" onclick="toggleBBoxSearch()"/>
              Use BBox Search<br/>

              &nbsp;<p/>

              West Longitude:<br/>
              <input type="text" id="aoiMinLon" name="aoiMinLon" value="${fieldValue(bean: queryParams, field: 'aoiMinLon')}"/><br/>

              North Latitude:<br/>
              <input type="text" id="aoiMaxLat" name="aoiMaxLat" value="${fieldValue(bean: queryParams, field: 'aoiMaxLat')}"/><br/>

              East Longitude:<br/>
              <input type="text" id="aoiMaxLon" name="aoiMaxLon" value="${fieldValue(bean: queryParams, field: 'aoiMaxLon')}"/><br/>

              South Latitude:<br/>
              <input type="text" id="aoiMinLat" name="aoiMinLat" value="${fieldValue(bean: queryParams, field: 'aoiMinLat')}"/><br/>

              &nbsp;<p/>

              <input type="button" onclick="clearAOI()" value="Clear AOI">
            </div>
          </div>
          <div class="actions">
            <a href="#" class="accordionToggleItem">&nbsp;</a>
          </div>
        </div>

        <div class="yui-cms-item yui-panel selected">
          <div class="hd">
            Temporal Criteria:
          </div>
          <div class="bd">
            <div class="fixed">
              Start Date/Time:<br/>
              <richui:dateChooser name="startDate" format="MM/dd/yyyy" time="true" hourStyle="width:30px;" minuteStyle="width:30px;" value="${queryParams.startDate}"></richui:dateChooser><br/>

              End Date/Time:<br/>
              <richui:dateChooser name="endDate" format="MM/dd/yyyy" time="true" hourStyle="width:30px;" minuteStyle="width:30px;" value="${queryParams.endDate}"></richui:dateChooser><br/>

              &nbsp;<p/>

              <input type="button" onclick="updateOmarFilters()" value="Update Footprints">
            </div>
          </div>
          <div class="actions">
            <a href="#" class="accordionToggleItem">&nbsp;</a>
          </div>
        </div>

        <div class="yui-cms-item yui-panel selected">
          <div class="hd">
            Metadata Criteria:
          </div>
          <div class="bd">
            <div class="fixed">
              <g:each in="${queryParams?.searchTagValues}" var="searchTagValue" status="i">
                <g:select
                        noSelection="${['null':'Select One...']}"
                        name="searchTagNames[${i}]"
                        value="${queryParams?.searchTagNames[i]}"
                        from="${RasterEntrySearchTag.list()}"
                        optionKey="name" optionValue="description"/>
                <g:textField name="searchTagValues[${i}]" value="${searchTagValue}"/>
              </g:each>

              <g:ifAllGranted role="ROLE_ADMIN">
                <a class="home" href="${createLinkTo(dir: 'searchTag/list')}">Create Search Tag</a>
              </g:ifAllGranted>
            </div>
          </div>
          <div class="actions">
            <a href="#" class="accordionToggleItem">&nbsp;</a>
          </div>
        </div>

        <div class="yui-cms-item yui-panel selected">
          <div class="hd">
            Options:
          </div>
          <div class="bd">
            <div class="fixed">
              Max Results:<br/>
              <input type="text" id="max" name="max" value="${params?.max}">
            </div>
          </div>
          <div class="actions">
            <a href="#" class="accordionToggleItem">&nbsp;</a>
          </div>
        </div>

      </div>
    </div>
  </g:form>
</div>

<div id="center1">
  <a class="home" href="${createLinkTo(dir: '')}">OMAR Home</a> -

  <a href="javascript:searchForRasters();">Search Rasters</a> -

  <a href="javascript:generateKML();">Generate KML</a> -

  <a href="#" id="tAll">Toggle Panels</a>

  <h1 id="mapTitle">Search for Imagery:</h1>
  <g:if test="${flash.message}">
    <div class="message">${flash.message}</div>
  </g:if>

  <div id="map"></div>
</div>

<script>

  (function()
  {
    var Dom = YAHOO.util.Dom,
            Event = YAHOO.util.Event;

    Event.onDOMReady(function()
    {
      var layout = new YAHOO.widget.Layout({
        units: [
          {
            position: 'top',
            height: 58,
            body: 'top1',
            header: 'OMAR Release ${grailsApplication.config.omar.release}',
            gutter: '5px',
            collapse: true,
            collapseSize: 18,
            resize: true
          },
          {
            position: 'bottom',
            header: '&nbsp;',
            height: 58,
            resize: true,
            body: 'bottom1',
            gutter: '5px',
            collapse: true,
            collapseSize: 20
          },
          {
            position: 'left',
            header: 'Search Criteria:',
            width: 193,
            resize: true,
            body: 'left1',
            gutter: '5px',
            collapse: true,
            collapseSize: 25,
            scroll: true,
            animate: true
          },
          {
            position: 'center',
            header: 'OMAR Map:',
            body: 'center1',
            gutter: '5px'
          }
        ]
      });
      layout.on('render', function()
      {
        layout.getUnitByPosition('left').on('close', function()
        {
          closeLeft();
        });
      });
      layout.render();
      Event.on('tAll', 'click', function( ev )
      {
        Event.stopEvent(ev);
        layout.getUnitByPosition('left').toggle();
        layout.getUnitByPosition('bottom').toggle();
        layout.getUnitByPosition('top').toggle();
      });

    });

  })();
</script>

</body>
</html>