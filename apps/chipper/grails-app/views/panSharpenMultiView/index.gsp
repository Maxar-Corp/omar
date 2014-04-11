<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: 12/4/13
  Time: 3:43 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="grails.converters.JSON" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>PanSharpenMultiView</title>

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
    <r:require modules="standard"/>
    <r:layoutResources/>
</head>

<body class="easyui-layout">

<div data-options="region:'north'" style="height:50px">
    <div style="padding:5px;">
        <g:link controller="geospatialImage" class="easyui-linkbutton">Home</g:link>
    </div>
</div>

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
        var productUrl = "${createLink( controller: 'chipper', action: 'getPSM' )}";

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

            new OpenLayers.Layer.WMS( "Chipper - getChip - Color",
                    chipUrl,
                    {layers: model.colorImage, format: 'image/png', transparent: true, bands: '3,2,1'},
                    {buffer: 0, singleTile: true, ratio: 1.0, isBaseLayer: false, visibility: true} ),

            new OpenLayers.Layer.WMS( "Chipper - getChip - Pan",
                    chipUrl,
                    {layers: model.panImage, format: 'image/png', transparent: true},
                    {buffer: 0, singleTile: true, ratio: 1.0, isBaseLayer: false, visibility: false} ),

            new OpenLayers.Layer.WMS( "Chipper - getPSM - Product",
                    productUrl,
                    {layers: model.psmImage, format: 'image/png', transparent: true, bands: '3,2,1'},
                    {buffer: 0, singleTile: true, ratio: 1.0, isBaseLayer: false, visibility: false} )

        ];
        map.addLayers( layers );

        var controls = [
            new OpenLayers.Control.LayerSwitcher({'div':OpenLayers.Util.getElement('layerMgr')})
        ];
        map.addControls( controls );

        map.zoomToExtent( bbox, true );

       var body =  $( 'body' );

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