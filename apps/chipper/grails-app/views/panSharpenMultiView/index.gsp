<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: 12/4/13
  Time: 3:43 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>PanSharpenMultiView</title>
    <meta name="layout" content="standard"/>
</head>

<body>

<content tag="north">
    <div style="padding:5px;">
        <g:link controller="twoColorMulti" class="easyui-linkbutton" data-options="">Two Color Multiview</g:link>
        <g:link controller="panSharpenMultiView" class="easyui-linkbutton"
                data-options="disabled:true">Pan Sharpen Fusion</g:link>
        <g:link controller="hillShade" class="easyui-linkbutton" data-options="">Hillshade</g:link>
    </div>
</content>
<content tag="south"></content>

<content tag="east">
    <div id="layerMgr"></div>
</content>
<content tag="west"></content>

<content tag="center">
    <div id="map"></div>
</content>

<r:external plugin='openlayers' file='OpenLayers.js' dir='js'/>
<r:script>
    $( document ).ready( function ()
    {

        var bbox = new OpenLayers.Bounds(${minX}, ${minY}, ${maxX}, ${maxY});
        var map, layers, controls;

        map = new OpenLayers.Map( 'map', {
            numZoomLevels: 32
        } );

        layers = [
            new OpenLayers.Layer.WMS(
                    "NASA BMNG",
                    "${baseWMS}",
                    {layers: 'Reference', map: '/data/omar/bmng.map'},
                    {buffer: 0}
            ),

            new OpenLayers.Layer.WMS( "Chipper - getChip - Color",
                    "${createLink( controller: 'chipper', action: 'getChip' )}",
                    {layers: '${colorImage}', format: 'image/png', transparent: true, bands: '3,2,1'},
                    {buffer: 0, singleTile: true, ratio: 1.0, isBaseLayer: false, visibility: true} ),

            new OpenLayers.Layer.WMS( "Chipper - getChip - Pan",
                    "${createLink( controller: 'chipper', action: 'getChip' )}",
                    {layers: '${panImage}', format: 'image/png', transparent: true},
                    {buffer: 0, singleTile: true, ratio: 1.0, isBaseLayer: false, visibility: false} ),

            new OpenLayers.Layer.WMS( "Chipper - getPSM - Product",
                    "${createLink( controller: 'chipper', action: 'getPSM' )}",
                    {layers: '${psmImage}', format: 'image/png', transparent: true, bands: '3,2,1'},
                    {buffer: 0, singleTile: true, ratio: 1.0, isBaseLayer: false, visibility: false} )

        ];
        map.addLayers( layers );

        controls = [
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

        body.layout('collapse','west');

    } );
</r:script>
</body>

</html>