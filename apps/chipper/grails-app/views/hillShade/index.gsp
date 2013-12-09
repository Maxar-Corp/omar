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
    <style type="text/css">
    #north {
        height: 100px;
    }

    #south {
        height: 100px;
    }

    #east {
        width: 100px;
    }

    #west {
        width: 200px;
    }

    #center {
        padding: 5px;
        background: #eee;
    }

    #pg {
        width: 300px;
    }
    </style>
    <r:require modules="easyui_scaffold"/>
    <r:layoutResources/>
</head>

<body class="easyui-layout">

<div id="north" data-options="region:'north',title:'North Title',split:true"></div>

<div id="south" data-options="region:'south',title:'South Title',split:true,collapsed:true"></div>

<div id="east" data-options="region:'east',title:'East',split:true,collapsed:true"></div>

<div id="west" data-options="region:'west',title:'West',split:true">
    <table id="pg" class="easyui-propertygrid"
           data-options="url:'${createLink( action: 'getOptions' )}',showGroup:true,scrollbarSize:0"></table>
    <br/>

    <div align='center'>
        <button id="refresh">Refresh</button>
    </div>
</div>

<div id="center" data-options="region:'center',title:'center title'">
    <div id="map"></div>
</div>

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
                    "http://omar.ngaiost.org/cgi-bin/mapserv.sh",
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
        new OpenLayers.Control.LayerSwitcher()
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