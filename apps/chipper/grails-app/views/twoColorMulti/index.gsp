<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: 4/7/14
  Time: 11:09 AM
--%>

<%@ page import="grails.converters.JSON" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Two Color MultiView</title>
    <style type="text/css">
    #layerMgr {
        width: 100%;
        height: 100%;
    }

    #center {
        padding: 5px;
        background: #eee;
    }
    </style>
    <r:require module="standard"/>
    <r:layoutResources/>
</head>

<body class="easyui-layout">

<div data-options="region:'north'" style="height:50px">
    <div style="padding:5px;">
        <g:link controller="twoColorMulti" class="easyui-linkbutton"
                data-options="toggle:true,group:'g1',selected:true">Two Color Multiview</g:link>
        <g:link controller="panSharpenMultiView" class="easyui-linkbutton"
                data-options="toggle:true,group:'g1'">Pan Sharpen Fusion</g:link>
        <g:link controller="hillShade" class="easyui-linkbutton"
                data-options="toggle:true,group:'g1'">Hillshade</g:link>
    </div>
</div>

<%--
<div data-options="region:'south',split:true" style="height:50px;"></div>
<div data-options="region:'west',split:true" title="West" style="width:100px;"></div>
--%>

<div data-options="region:'east',split:true" title="East" style="width:200px;">
    <div id="layerMgr"></div>
</div>


<div id="center" data-options="region:'center'">
    <div id="map"></div>
</div>

<r:external plugin='openlayers' file='OpenLayers.js' dir='js'/>
<r:script>
    $( document ).ready( function ()
    {
        var model = ${model as JSON};
        var chipUrl = "${createLink( controller: 'chipper', action: 'getChip' )}";
        var productUrl = "${createLink( controller: 'chipper', action: 'get2CMV' )}";

        var bbox = new OpenLayers.Bounds(model.minX, model.minY, model.maxX, model.maxY);

        var map = new OpenLayers.Map( 'map', {
            numZoomLevels: 32
        } );

        var layers = [
            new OpenLayers.Layer.WMS(
                    "NASA BMNG",
                    model.baseWMS.server, model.baseWMS.params,
                    {buffer: 0}
            ),

            new OpenLayers.Layer.WMS( "Chipper - 2CMV - Red",
                    chipUrl,
                    {layers: model.redImage, format: 'image/png', transparent: true},
                    {buffer: 0, singleTile: true, ratio: 1.0, isBaseLayer: false, visibility: true} ),

            new OpenLayers.Layer.WMS( "Chipper - 2CMV - Blue",
                    chipUrl,
                    {layers: model.blueImage, format: 'image/png', transparent: true},
                    {buffer: 0, singleTile: true, ratio: 1.0, isBaseLayer: false, visibility: true} ),

            new OpenLayers.Layer.WMS( "Chipper - 2CMV - Product",
                    productUrl,
                    {layers: model.redImage + "," + model.blueImage, format: 'image/png', transparent: true},
                    {buffer: 0, singleTile: true, ratio: 1.0, isBaseLayer: false, visibility: false} )

        ];
        map.addLayers( layers );

        var controls = [
            new OpenLayers.Control.LayerSwitcher({'div':OpenLayers.Util.getElement('layerMgr')})
        ];
        map.addControls( controls );

        map.zoomToExtent( bbox, true );

        var body = $( 'body' );

        body.layout( 'panel', 'center' ).panel( {
            onResize: function ()
            {
                map.updateSize();
            }
        } );
    } );
</r:script>
<r:layoutResources/>
</body>
</html>