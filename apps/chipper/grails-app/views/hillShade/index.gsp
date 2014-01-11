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
    <title>HillShade</title>
    <meta name="layout" content="standard"/>
    <style type="text/css">
    #pg {
        width: 300px;
    }
    </style>
</head>

<body>

<content tag="north">
    <div style="padding:5px;">
        <g:link controller="twoColorMulti" class="easyui-linkbutton" data-options="">Two Color Multiview</g:link>
        <g:link controller="panSharpenMultiView" class="easyui-linkbutton" data-options="">Pan Sharpen Fusion</g:link>
        <g:link controller="hillShade" class="easyui-linkbutton" data-options="disabled:true">Hillshade</g:link>
    </div>
</content>
<content tag="south"></content>
<content tag="east">
    <div id="layerMgr"></div>
</content>
<content tag="west">
    <table id="pg" class="easyui-propertygrid"
           data-options="url:'${createLink( action: 'getOptions' )}',showGroup:true,scrollbarSize:0"></table>
    <br/>

    <div align='center'>
        <button id="refresh">Refresh</button>
    </div>

</content>
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

            new OpenLayers.Layer.WMS( "Chipper - getChip - Map",
                    "${createLink( controller: 'chipper', action: 'getChip' )}",
                    {layers: '${mapImage}', format: 'image/png', transparent: true},
                    {buffer: 0, singleTile: true, ratio: 1.0, isBaseLayer: false, visibility: true} ),

    <g:each var="x" in="${( 0..<demImages.size() )}">
        new OpenLayers.Layer.WMS( "Chipper - getChip - Elevation ${x}",
                            "${createLink( controller: 'chipper', action: 'getChip' )}",
                            {layers: '${demImages[x]}', format: 'image/png', transparent: true},
                            {buffer: 0, singleTile: true, ratio: 1.0, isBaseLayer: false, visibility: false} ),

    </g:each>


    new OpenLayers.Layer.WMS( "Chipper - HillShade - Product",
        "${createLink( controller: 'chipper', action: 'getHillShade' )}",
                    {layers: '${mapImage}', format: 'image/png', transparent: true},
                    {buffer: 0, singleTile: true, ratio: 1.0, isBaseLayer: false, visibility: false} )

    ];
    map.addLayers( layers );

    controls = [
        new OpenLayers.Control.LayerSwitcher({'div':OpenLayers.Util.getElement('layerMgr')})
    ];
    map.addControls( controls );

    map.zoomToExtent( bbox, true );

    $( 'body' ).layout( 'panel', 'center' ).panel( {
        onResize: function ()
        {
            map.updateSize();
        }
    } );

    $('#refresh').click(function(){
        var data = $('#pg').propertygrid('getData').rows;
        var obj = {};

        data.forEach(function(item){
            obj[item.name] = item.value;
        }) ;

        //console.log(obj);
        var layer = map.getLayersByName("Chipper - HillShade - Product")[0];

        layer.mergeNewParams(obj);

    })
} );
</r:script>
</body>

</html>