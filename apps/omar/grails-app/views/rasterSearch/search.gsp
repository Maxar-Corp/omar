<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: Aug 7, 2009
  Time: 8:54:13 AM
  To change this template use File | Settings | File Templates.
--%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"/>
  <title>OMAR Raster Search</title>
  <style type="text/css">
    /*
    margin and padding on body element
    can introduce errors in determining
    element position and are not recommended;
    we turn them off as a foundation for YUI
    CSS treatments.
    */
  body {
    margin: 0;
    padding: 0;
  }

  #map {
    width: 100%;
    height: 100%;
    border: 1px solid black;
  }

  .smallmap {
    border: 1px solid #ccc;
  }

  .olControlEditingToolbar {
    float: left;
    right: 0px;
    height: 30px;
    width: 150px;
    border: 1px solid #ccc;
  }

    /**
    * Map Examples Specific
    */
  .smallmap {
    width: 512px;
    height: 256px;
    border: 1px solid #ccc;
  }

  .olControlPanel div {
    float: left;
    display: block;
    height: 30px;
    width: 150px;
    margin: 5px;
    border: 1px solid #ccc;
  }

  .olControlPanel .olControlMouseDefaultsItemActive {
    width: 22px;
    height: 22px;
    background-color: orange;
    background-image: url("${createLinkTo( file: 'plugins/openlayers-0.4/js/theme/default/img/pan_on.png')}");
    background-repeat: no-repeat;
    background-size: 100%;
  }

  .olControlPanel .olControlMouseDefaultsItemInactive {
    width: 22px;
    height: 22px; /*background-color: blue;*/
    background-image: url("${createLinkTo( file: 'plugins/openlayers-0.4/js/theme/default/img/pan_off.png')}");
    background-repeat: no-repeat;
    background-size: 100%;
  }

  .olControlPanel .olControlDrawFeatureItemActive {
    width: 22px;
    height: 22px;
    background-color: orange;
    background-image: url("${createLinkTo( file: 'plugins/openlayers-0.4/js/theme/default/img/draw_point_on.png')}");
    background-repeat: no-repeat;
    background-size: 100%;
  }

  .olControlPanel .olControlDrawFeatureItemInactive {
    width: 22px;
    height: 22px; /*background-color: blue;*/
    background-image: url("${createLinkTo( file: 'plugins/openlayers-0.4/js/theme/default/img/draw_point_off.png')}");
    background-repeat: no-repeat;
    background-size: 100%;
  }

  .olControlPanel .olControlZoomBoxItemInactive {
    width: 22px;
    height: 22px; /*background-color: blue;*/
    background-image: url("${createLinkTo( file: 'plugins/openlayers-0.4/js/theme/default/img/drag-rectangle-off.png')}");
    background-repeat: no-repeat;
    background-size: 100%;
  }

  .olControlPanel .olControlZoomBoxItemActive {
    width: 22px;
    height: 22px;
    background-color: orange;
    background-image: url("${createLinkTo( file: 'plugins/openlayers-0.4/js/theme/default/img/drag-rectangle-on.png')}");
    background-repeat: no-repeat;
    background-size: 100%;
  }

  .olControlPanel .olControlZoomToMaxExtentItemInactive {
    width: 22px;
    height: 22px; /*background-color: blue;*/
    background-image: url("${createLinkTo( file: 'plugins/openlayers-0.4/js/img/zoom-world-mini.png')}");
    background-repeat: no-repeat;
    background-size: 100%;
  }

  .olControlPanel .olControlClearAreaOfInterestItemInactive {
    width: 22px;
    height: 22px; /*background-color: blue;*/
    background-image: url("${createLinkTo( file: 'plugins/openlayers-0.4/js/theme/default/img/remove_point_off.png')}");
    background-repeat: no-repeat;
    background-size: 100%;
  }

  .olControlPanel .olControlNavigationHistory {
  /*background-color: blue;*/
    background-image: url("${createLinkTo( file: 'plugins/openlayers-0.4/js/theme/default/img/navigation_history.png')}");
    background-repeat: no-repeat;
    width: 22px;
    height: 22px;
    background-size: 100%;

  }

  .olControlPanel .olControlNavigationHistoryPreviousItemActive {
    background-position: 0px 0px;
  }

  .olControlPanel .olControlNavigationHistoryPreviousItemInactive {
    background-position: 0px -22px;
  }

  .olControlPanel .olControlNavigationHistoryNextItemActive {
    background-position: -22px 0px;
  }

  .olControlPanel .olControlNavigationHistoryNextItemInactive {
    background-position: -22px -22px;
  }

  div.niceBox {
    margin-top: 3px;
    margin-bottom: 8px;
    background-color: #CCFFBF;
    border: 1px solid #2A8400;
  }

  div.niceBoxHd {
    font-size: 87%;
    font-weight: bold;
    padding: 2px;
    color: white;
    background-color: #2AD400;
  }

  div.niceBoxBody {
    font-size: 87%;
    padding: 3px;
  }
  </style>

  <gui:resources css="['reset_fonts_grids', 'resize', 'layout', 'button']"/>
  <gui:resources javascript="['yahoo', 'event', 'dom', 'element', 'dragdrop', 'resize', 'animation', 'layout']"/>
  <gui:resources components="['dialog', 'datePicker']"/>

  <%--
   <link rel="stylesheet" type="text/css" href="${createLinkTo(file: 'plugins/richui-0.7/js/yui/reset-fonts-grids/reset-fonts-grids.css')}"/>
  <link rel="stylesheet" type="text/css" href="${createLinkTo(file: 'plugins/richui-0.7/js/yui/resize/assets/skins/sam/resize.css')}"/>
  <link rel="stylesheet" type="text/css" href="${createLinkTo(file: 'plugins/richui-0.7/js/yui/layout/assets/skins/sam/layout.css')}"/>
  <link rel="stylesheet" type="text/css" href="${createLinkTo(file: 'plugins/richui-0.7/js/yui/button/assets/skins/sam/button.css')}"/>

  <script type="text/javascript" src="${createLinkTo(file: 'plugins/richui-0.7/js/yui/yahoo/yahoo-min.js')}"></script>
  <script type="text/javascript" src="${createLinkTo(file: 'plugins/richui-0.7/js/yui/event/event-min.js')}"></script>
  <script type="text/javascript" src="${createLinkTo(file: 'plugins/richui-0.7/js/yui/dom/dom-min.js')}"></script>
  <script type="text/javascript" src="${createLinkTo(file: 'plugins/richui-0.7/js/yui/element/element-beta-min.js')}"></script>
  <script type="text/javascript" src="${createLinkTo(file: 'plugins/richui-0.7/js/yui/dragdrop/dragdrop-min.js')}"></script>
  <script type="text/javascript" src="${createLinkTo(file: 'plugins/richui-0.7/js/yui/resize/resize-min.js')}"></script>
  <script type="text/javascript" src="${createLinkTo(file: 'plugins/richui-0.7/js/yui/animation/animation-min.js')}"></script>
  <script type="text/javascript" src="${createLinkTo(file: 'plugins/richui-0.7/js/yui/layout/layout-min.js')}"></script>
  --%>

  <openlayers:loadTheme theme="default"/>
  <openlayers:loadJavascript/>

  <%--
  <resource:dateChooser/>
  --%>

</head>

<body class=" yui-skin-sam" onresize="changeMapSize()">
<div id="top1">
  <div id="panel2" class="olControlPanel"></div>
</div>
<div id="bottom1">
</div>
<div id="right1">
</div>
<div id="left1">
  <g:form action="search">
    <input type="hidden" id="viewMinLon" name="viewMinLon" value="${fieldValue(bean: queryParams, field: 'viewMinLon')}"/>
    <input type="hidden" id="viewMinLat" name="viewMinLat" value="${fieldValue(bean: queryParams, field: 'viewMinLat')}"/>
    <input type="hidden" id="viewMaxLon" name="viewMaxLon" value="${fieldValue(bean: queryParams, field: 'viewMaxLon')}"/>
    <input type="hidden" id="viewMaxLat" name="viewMaxLat" value="${fieldValue(bean: queryParams, field: 'viewMaxLat')}"/>

  <%--
      <gui:accordion bounce="false" slow="false" multiple="true">
        <gui:accordionElement title="Geospatial Criteria" selected="true">
  --%>
    <div class="niceBox">
      <div class="niceBoxHd">Map Center:</div>
      <div class="niceBoxBody">
        <ol>
          <li>
            <label for='centerLon'>Lon:</label><br/>
          </li>
          <li>
            <input type="text" id="centerLon" name="center"/>
          </li>
          <li>
            <label for='centerLat'>Lat:</label>
          </li>
          <li>
            <input type="text" id="centerLat" name="center"/>
          </li>
          <li><br/></li>
          <li>
            <span class="formButton">
              <input type="button" onclick="goto()" value="Set Center">
            </span>
          </li>
        </ol>
      </div>
    </div>
    <div class="niceBox">
      <div class="niceBoxHd">Geospatial Criteria</div>
      <div class="niceBoxBody">
        <fieldset>
          <%--                                --%>
          <legend></legend>
          <ol>
            <li><label for="aoiMinLon">Min Lon:</label></li>
            <li><input type="text" id="aoiMinLon" name="aoiMinLon" value="${queryParams?.aoiMinLon}"/></li>
            <li><label for="aoiMinLat">Min Lat:</label></li>
            <li><input type="text" id="aoiMinLat" name="aoiMinLat" value="${queryParams?.aoiMinLat}"/></li>
            <li><label for="aoiMaxLon">Max Lon:</label></li>
            <li><input type="text" id="aoiMaxLon" name="aoiMaxLon" value="${queryParams?.aoiMaxLon}"/></li>
            <li><label for="aoiMaxLat">Max Lat:</label></li>
            <li><input type="text" id="aoiMaxLat" name="aoiMaxLat" value="${queryParams?.aoiMaxLat}"/></li>
            <li><br/></li>
            <li><button onclick="">Set</button><input type="button" onclick="clearAOI()" value="Clear"></li>
          </ol>
        </fieldset>
      </div>
    </div>
  <%--
        </gui:accordionElement>
        <gui:accordionElement title="Temporal Criteria" selected="true">
  --%>
    <div class="niceBox">
      <div class="niceBoxHd">Temporal Criteria</div>
      <div class="niceBoxBody">
        <fieldset>
          <%--                              --%>
          <legend></legend>
          <ol>
            <li><label for="startDate">Start Date:</label></li>
            <%--
           <li><richui:dateChooser name="startDate" format="MM.dd.yyyy"/></li>
            --%>
            <%--
            <li><input type="text" id="startDate" value=""/><button onclick="return false;">Set</button></li>
            --%>
            <li><gui:datePicker id="startDate" close="true" includeTime="true"/></li>
            <li><label for="endDate">End Date:</label></li>
            <%--
            <li><richui:dateChooser name="endDate" format="MM.dd.yyyy"/></li>
            --%>
            <li><input type="text" id="endDate" value=""/><button id="showEndate" onclick="return false;">Set</button></li>
            <gui:dialog
                    title="Set the Ending Date"
                    triggers="[show:[id:'showEndate', on:'click']]"
                    modal="true">
              <gui:datePicker includeTime="true"/>
            </gui:dialog>

            <li><br/></li>
            <li><button onclick="">Set</button><button onclick="">Clear</button></li>
          </ol>
        </fieldset>
      </div>
    </div>
  <%--
        </gui:accordionElement>
        <gui:accordionElement title="Metadata Criteria" selected="true">
  --%>
    <%--
    <div class="niceBox">
      <div class="niceBoxHd">Metadata Criteria:</div>
      <div class="niceBoxBody">
        <ol>
          <li>
            <g:select id="searchTag.id" name='searchTag.id' value="${queryParams?.searchTag?.id}"
                    noSelection="${['null':'Select One...']}"
                    from='${SearchTag.list()}'
                    optionKey="id" optionValue="description"></g:select>
          </li>
          <li>
            <input type="text" id="searchTagValue" name="searchTagValue" value="${fieldValue(bean: queryParams, field: 'searchTagValue')}"/>
          </li>
        </ol>
      </div>
    </div>
    <br/>
    --%>
    <fieldset>
      <ol><li><g:submitButton name="Search"/></li></ol>
    </fieldset>

  </g:form>
</div>
<div id="center1">
  <div id="map" class="smallmap"></div>
</div>


<g:javascript>

    var Dom = YAHOO.util.Dom;
    var Event = YAHOO.util.Event;
    var map;
    var aoiLayer;
    var layout;

      function setupBaseLayer()
      {
        var baseLayer = new OpenLayers.Layer.WMS(
                "${baseWMS.title}",
                "${baseWMS.url}",
        {layers: '${baseWMS.layers}', format: 'image/jpeg' },
        {buffer:1}
                );

        map.addLayer(baseLayer);
        map.setBaseLayer(baseLayer);
      }

      function setupDataLayer()
      {
        var dataLayer = new OpenLayers.Layer.WMS(
                "${dataWMS.title}",
                "${dataWMS.url}",
        { layers: "${dataWMS.layers}", format: 'image/png', transparent: true },
        {'isBaseLayer': false},
        {buffer:1}
                );

        map.addLayer(dataLayer);
      }


      function clearAOI( e )
      {
        aoiLayer.destroyFeatures();


        // HACK - Need a better way to this
        $("aoiMinLon").value = ""
        $("aoiMaxLat").value = ""
        $("aoiMaxLon").value = ""
        $("aoiMinLat").value = ""
      }

      function setAOI( e )
      {
        var geom = e.feature.geometry;
        var bounds = geom.getBounds();
        var feature = new OpenLayers.Feature.Vector(geom);

        // HACK - Need a better way to this
        $("aoiMinLon").value = bounds.left
        $("aoiMaxLat").value = bounds.top
        $("aoiMaxLon").value = bounds.right
        $("aoiMinLat").value = bounds.bottom

        aoiLayer.destroyFeatures();
        aoiLayer.addFeatures(feature, {silent: true});

      }

      function setupAreaOfInterestLayer()
      {
        aoiLayer = new OpenLayers.Layer.Vector("Area of Interest");
        aoiLayer.events.register("featureadded", aoiLayer, setAOI);
        map.addLayer(aoiLayer);

        var aoiMinLon = "${queryParams?.aoiMinLon ?: 0}";
        var aoiMinLat = "${queryParams?.aoiMinLat ?: 0}";
        var aoiMaxLon = "${queryParams?.aoiMaxLon ?: 0}";
        var aoiMaxLat = "${queryParams?.aoiMaxLat ?: 0}";

        if ( aoiMinLon && aoiMinLat && aoiMaxLon && aoiMaxLat )
        {
          var bounds = new OpenLayers.Bounds( aoiMinLon, aoiMinLat, aoiMaxLon, aoiMaxLat );
          var feature = new OpenLayers.Feature.Vector(bounds.toGeometry());

          aoiLayer.addFeatures(feature, {silent: true});
        }
      }

      function setupToolbar()
      {
        var zoomBox = new OpenLayers.Control.ZoomBox(
        {title:"Zoom into an area by clicking and dragging"});


        var polyOptions = {sides: 4, irregular: true};

        var polygonControl = new OpenLayers.Control.DrawFeature(aoiLayer, OpenLayers.Handler.RegularPolygon,
        {handlerOptions: polyOptions, title: "Specify Area of Interest"});

        var button1 = new OpenLayers.Control.Button({title:'Clear Area of Interest',
          displayClass: "olControlClearAreaOfInterest",
          trigger: clearAOI

        });

        var container = $("panel2");

        var panel = new OpenLayers.Control.Panel(
        {div: container,defaultControl: zoomBox,'displayClass': 'olControlPanel'}
                );


        var nav = new OpenLayers.Control.NavigationHistory({
          nextOptions: {title: "Next View"},
          previousOptions: {title: "Previous View"}
        });

        map.addControl(nav);

        panel.addControls([
          new OpenLayers.Control.MouseDefaults({title:'Drag to recenter map'}),
          zoomBox,
          nav.next, nav.previous,
          new OpenLayers.Control.ZoomToMaxExtent({title:"Zoom to the max extent"}),
          polygonControl,
          button1,
        ]);

        map.addControl(panel);
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

      function setupMap()
      {

        map = new OpenLayers.Map('map', {controls: []});
        map.addControl(new OpenLayers.Control.LayerSwitcher());
        map.addControl(new OpenLayers.Control.PanZoom());
        //map.addControl(new OpenLayers.Control.NavToolbar());
        map.addControl(new OpenLayers.Control.MousePosition());
        map.addControl(new OpenLayers.Control.Scale());
        map.addControl(new OpenLayers.Control.ScaleLine());

        map.events.register("moveend", map, setCenterText);
        map.events.register("zoomend", map, setView );


        setupBaseLayer();
        setupDataLayer();
        setupAreaOfInterestLayer();
        setupToolbar();
        setupMapView();

        //map.zoomToMaxExtent();
      }

    function goto()
    {
      var centerLon = $("centerLon").value;
      var centerLat = $("centerLat").value;
      var zoom = map.getZoom();
      var center = new OpenLayers.LonLat(centerLon, centerLat);

      map.setCenter(center, zoom);

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


    Event.onDOMReady(function()
    {
     layout = new YAHOO.widget.Layout({
        units: [
          {
            position: 'top',
            height: 70,
            body: 'top1',
            header: 'Top',
            gutter: '5px',
            collapse: true,
            resize: true
          },
          {
            position: 'right',
            header: 'Right',
            width: 300,
            resize: true,
            gutter: '5px',
            collapse: true,
            scroll: true,
            body: 'right1',
            animate: true
          },
          {
            position: 'bottom',
            header: 'Bottom',
            height: 300,
            resize: true,
            body: 'bottom1',
            gutter: '5px',
            collapse: true
          },
          {
            position: 'left',
            header: 'Left',
            width: 450,
            resize: true,
            body: 'left1',
            gutter: '5px',
            collapse: true,
            scroll: true,
            animate: true
          },
          {
            position: 'center',
            body: 'center1'
          }
        ]
      });

      layout.on('render', function()
      {
        setupMap();
      });

      layout.subscribe("resize", function( ev )
      {
        var c = this.getUnitByPosition('center');
        var mapWidth = c.get('width');
        var mapHeight = c.get('height');

        Dom.get("map").style.width = mapWidth + "px";
        Dom.get("map").style.height = mapHeight + "px";

        map.updateSize();


      });


      layout.render();
      layout.getUnitByPosition('right').collapse();
      layout.getUnitByPosition('bottom').collapse();



      changeSize();

    });

</g:javascript>
</body>
</html>
