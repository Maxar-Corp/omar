<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: 1/30/12
  Time: 3:29 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="grails.converters.JSON" contentType="text/html;charset=UTF-8" %>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>OMAR <g:meta name="app.version"/>: Orthorectified Multi-Layer View</title>
  <meta content="multiLayerLayout" name="layout">
  <r:require modules="multiLayerLayout"/>
</head>

<body class=" yui-skin-sam">
<g:form name="wcsForm" method="POST"/>

<content tag="top1">
  <omar:securityClassificationBanner/>
    <omar:logout/>
    <g:render template="multiLayerMenu" model="${[rasterEntries: rasterEntries]}"/>
</content>

<content tag="bottom1">
  <omar:securityClassificationBanner/>
</content>

<%--
<content tag="right1">
</content>

<content tag="left1">
</content>
--%>

<content tag="top2">
  <div id="toolBar" class="olControlPanel"></div>
</content>

<content tag="bottom2">
  <table><tr>
    <td width="33%"><div id="ddMousePosition">&nbsp;</div></td>
    <td width="33%"><div id="dmsMousePosition">&nbsp;</div></td>
    <td width="33%"><div id="mgrsMousePosition">&nbsp;</div></td>
  </tr></table>
</content>

<content tag="center2">
  <div id="map"></div>
</content>

<r:script>
    var mapWidget = new MapWidget();
    var minLon = parseFloat("${left}");
    var minLat = parseFloat("${bottom}");
    var maxLon = parseFloat("${right}");
    var maxLat = parseFloat("${top}");
    var largestScale = parseFloat("${largestScale}");
    var smallestScale = parseFloat("${smallestScale}");
    var wcsParams = new OmarWcsParams();

  function getCapabilities()
  {
    window.open("${createLink( controller: 'ogc', action: 'wms', params: [request: 'GetCapabilities', layers: ( rasterEntries*.indexId ).join( ',' )] )}");
  }
  function changeMapSize()
  {
    var Dom = YAHOO.util.Dom;

    if(mapWidget) mapWidget.changeMapSize( );
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

  function init()
  {
    OpenLayers.ImgPath = "${resource(plugin: 'openlayers', dir: 'js/img')}/";

	var oMenu = new YAHOO.widget.MenuBar("rasterMenu", {
                                               autosubmenudisplay: true,
                                               hidedelay: 750,
                                               showdelay: 0,
                                               lazyload: true,
                                               zIndex:9999});
	oMenu.render();
    wcsParams.setProperties({
        brightness:"0",
        contrast:"1",
        sharpen_mode:"none",
        stretch_mode:"linear_auto_min_max",
        stretch_mode_region: "global",
        interpolation: "bilinear",
        srs: "EPSG:4326",
        crs: "EPSG:4326",
        bands:"",
        quicklook: false
    });

    var format = "${format}";
    var transparent = true;
    // lets expand out twice
    var lonDeltaHalf = (maxLon-minLon)*0.5;
    var latDeltaHalf = (maxLat-minLat)*0.5;
    var minLatTemp = minLat-latDeltaHalf;
    var minLonTemp = minLon-lonDeltaHalf;
    var maxLatTemp = maxLat+latDeltaHalf;
    var maxLonTemp = maxLon+lonDeltaHalf;
    minLatTemp = minLatTemp < -90.0?-90.0:minLatTemp;
    maxLatTemp = maxLatTemp >90.0?90.0:maxLatTemp;
    minLonTemp = minLonTemp < -180.0?-180.0:minLonTemp;
    maxLonTemp = maxLonTemp >180.0?180.0:maxLonTemp;
    var bounds = new OpenLayers.Bounds(minLonTemp, minLatTemp, maxLonTemp, maxLatTemp);

	//mapWidget = new MapWidget();
	mapWidget.setupMapWidgetWithOptions("map", {controls: [], theme: null,  displayOutsideMaxExtent:true, maxExtent:bounds, maxResolution:largestScale, minResolution:smallestScale});
	mapWidget.setFullResScale(parseFloat("${fullResScale}"));
    mapWidget.changeMapSize();
    //map = new OpenLayers.Map( "map", {controls: [], maxExtent:bounds, maxResolution:largestScale, minResolution:smallestScale} );
    setupBaseLayers( );
    var layers = [
  <g:each var="rasterEntry" in="${rasterEntries}" status="i">

    <g:if test="${i > 0}">,</g:if>

    new OpenLayers.Layer.WMS(
    "Raster ${rasterEntry.id}",
                "${createLink(controller: 'ogc', action: 'wms')}",
        { layers: "${rasterEntry.indexId}", displayOutsideMaxExtent:true, format: format, stretch_mode_region: "global", stretch_mode:"linear_auto_min_max", transparent:transparent  },
        {isBaseLayer: false,buffer:0, singleTile:true, ratio:1.0, transitionEffect: "resize"}
                )
    <g:if test="${kmlOverlays}">

      , new OpenLayers.Layer.Vector( "KML", {
   projection: mapWidget.getMap().displayProjection,
   strategies: [new OpenLayers.Strategy.Fixed( )],
   protocol: new OpenLayers.Protocol.HTTP( {
     url: "${createLink(controller: 'rasterEntry', action: 'getKML', params: [rasterEntryIds: rasterEntry.indexId])}",
            format: new OpenLayers.Format.KML( {
              extractStyles: true,
              extractAttributes: true
            } )
          } )
        } )
    </g:if>
  </g:each>
  ];

    mapWidget.getMap().addLayers( layers );
    mapWidget.setupAoiLayer();
    mapWidget.setupToolBar();

    mapWidget.getMap().addControl(new OpenLayers.Control.LayerSwitcher());
    //var overview = new OpenLayers.Control.OverviewMap({maximized: true});
    //mapWidget.getMap().addControl(overview);
    mapWidget.getMap().addControl(new OpenLayers.Control.Scale());
    mapWidget.getMap().addControl(new OpenLayers.Control.ScaleLine());

    var zoom = mapWidget.getMap().getZoomForExtent(bounds, true);
    mapWidget.getMap().setCenter(bounds.getCenterLonLat(), zoom);
}

function getProjectedImage(params)
{
	 var link   = "${createLink(action: "wcs", controller: "ogc")}";
	 var extent = mapWidget.getSelectedOrViewportExtents();
	 var size   = mapWidget.getSizeInPixelsFromExtents(extent);
	 var wcsProperties = {"request":"GetCoverage",
	               	  "format":params.format,
	               	  "bbox":extent.toBBOX(),
	               	  "coverage":params.coverage,
	               	  "crs":"EPSG:4326",
	               	  "width":size.w,
	               	  "height":size.h}
    wcsParams.setProperties(wcsProperties);

    var form = $("wcsForm");
    var url = link + "?" + wcsParams.toUrlParams();

    if(form)
    {
        form.action = url;
        form.submit();
    }
}

</r:script>

</body>
</html>