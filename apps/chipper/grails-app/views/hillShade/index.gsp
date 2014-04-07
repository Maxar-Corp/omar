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
    <title>HillShade</title>
    <style type="text/css">
    #pg {
        width: 300px;
    }
    </style>
    <r:require module="standard"/>
    <r:layoutResources/>
</head>

<body class="easyui-layout">

<div data-options="region:'north'" style="height:50px">
    <div style="padding:5px;">
        <g:link controller="twoColorMulti" class="easyui-linkbutton"
                data-options="toggle:true,group:'g1'">Two Color Multiview</g:link>
        <g:link controller="panSharpenMultiView" class="easyui-linkbutton"
                data-options="toggle:true,group:'g1'">Pan Sharpen Fusion</g:link>
        <g:link controller="hillShade" class="easyui-linkbutton"
                data-options="toggle:true,group:'g1',selected:true">Hillshade</g:link>
    </div>
</div>

<div data-options="region:'east',split:true" title="East" style="width:200px;">
    <div id="layerMgr"></div>
</div>

<div id="west" data-options="region:'west',title:'West',split:true" style="width:200px;">
    <table id="pg" class="easyui-propertygrid"
           data-options="url:'${createLink( action: 'getOptions' )}',showGroup:true,scrollbarSize:0"></table>
    <br/>

    <div align='center'>
        <button id="refresh">Refresh</button>
    </div>

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
        var productUrl =  "${createLink( controller: 'chipper', action: 'getHillShade' )}";
        var bbox = new OpenLayers.Bounds(model.minX, model.minY, model.maxX, model.maxY);

        var map = new OpenLayers.Map( 'map', {numZoomLevels: 32} );

        var layers = [
            new OpenLayers.Layer.WMS("NASA BMNG", model.baseWMS.server, model.baseWMS.params, {buffer: 0}),

            new OpenLayers.Layer.WMS( "Chipper - getChip - Map",
                    chipUrl,
                    {layers: model.mapImage, format: 'image/png', transparent: true},
                    {buffer: 0, singleTile: true, ratio: 1.0, isBaseLayer: false, visibility: true} ),
        ];



        for (  var x = 0; x < model.demImages.length; x++ )
        {
            layers.push( new OpenLayers.Layer.WMS( "Chipper - getChip - Elevation " + x,
                            chipUrl,
                            {layers: model.demImages[x], format: 'image/png', transparent: true},
                            {buffer: 0, singleTile: true, ratio: 1.0, isBaseLayer: false, visibility: false} )
            );
        }


    layers.push( new OpenLayers.Layer.WMS( "Chipper - HillShade - Product",
        productUrl,
                    {layers: model.mapImage, format: 'image/png', transparent: true},
                    {buffer: 0, singleTile: true, ratio: 1.0, isBaseLayer: false, visibility: false} )

    );

    map.addLayers( layers );

    var controls = [
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
<r:layoutResources/>
</body>

</html>