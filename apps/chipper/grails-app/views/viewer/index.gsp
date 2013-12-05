<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: 8/9/13
  Time: 1:36 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Viewer</title>
    <meta name="layout" content="main"/>
    <style type="text/css">
    #map {
        width: 1024px;
        height: 512px;
        border: #255b17 solid thin;
    }
    </style>
</head>

<body>
<div class="nav">
    <ul>
        <li><g:link class="home" uri="/">Home</g:link></li>
    </ul>
</div>

<div class="content">
    <h1>Chipper Viewer</h1>

    <div align='center'>
        <div id='map'></div>
    </div>
</div>
<r:external plugin='jquery' dir='js/jquery' file='jquery-1.8.3.min.js'/>
<r:external plugin='openlayers' dir='js' file='OpenLayers.js'/>

<r:script>
    $( document ).ready( function ()
    {
        var map = new OpenLayers.Map( 'map', {
            numZoomLevels: 32
        } );

        var layers = [
            new OpenLayers.Layer.WMS( "BMNG",
                    "http://omar.ngaiost.org/cgi-bin/mapserv.sh",
                    {map: '/data/omar/bmng.map', layers: 'Reference', format: 'image/jpeg'},
                    {buffer: 0} ),

            new OpenLayers.Layer.WMS( "Chipper - getChip",
                    "${createLink( controller: 'chipper', action: 'getChip' )}",
                    {layers: '${orthoImage}', format: 'image/png', transparent: true},
                    {buffer: 0, singleTile: true, ratio: 1.0, isBaseLayer: false} ),

            new OpenLayers.Layer.WMS( "Chipper - getChip - Color",
                    "${createLink(  controller: 'chipper',action: 'getChip' )}",
                    {layers: '${colorImage}', format: 'image/png', transparent: true, bands: '3,2,1'},
                    {buffer: 0, singleTile: true, ratio: 1.0, isBaseLayer: false, visibility: true} ),

            new OpenLayers.Layer.WMS( "Chipper - getChip - Pan",
                    "${createLink(  controller: 'chipper',action: 'getChip' )}",
                    {layers: '${panImage}', format: 'image/png', transparent: true},
                    {buffer: 0, singleTile: true, ratio: 1.0, isBaseLayer: false, visibility: true} ),

            new OpenLayers.Layer.WMS( "Chipper - getPSM - Product",
                    "${createLink(  controller: 'chipper',action: 'getPSM' )}",
                    {layers: '${psmImage}', format: 'image/png', transparent: true, bands: '3,2,1'},
                    {buffer: 0, singleTile: true, ratio: 1.0, isBaseLayer: false, visibility: false} )

        ];
        map.addLayers( layers );

        var controls = [
            new OpenLayers.Control.LayerSwitcher(),
            new OpenLayers.Control.MousePosition()
        ];
        map.addControls( controls );

        var bounds = new OpenLayers.Bounds( -180, -90, 180, 90 );
        map.zoomToExtent( bounds );
    } );
</r:script>
</body>
</html>