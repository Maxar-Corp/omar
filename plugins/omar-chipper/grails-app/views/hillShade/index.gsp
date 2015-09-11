<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: 4/14/14
  Time: 8:51 AM
--%>

<%@ page import="grails.converters.JSON" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Hill Shade</title>
    <piwik:trackPageview/>

    <%--
    <r:external plugin="omar-chipper" dir="js/jquery-easyui/themes" file="icon.css"/>
    <r:external plugin="omar-chipper" dir="js/jquery-easyui/themes/default" file="easyui.css"/>
    <r:external plugin="omar-chipper" dir="js/openlayers/theme/default" file="style.css"/>
    --%>
    <asset:stylesheet src="hillShade.css"/>
</head>

<body class="easyui-layout">
<div region="north" style="height: 20px">
    <omar:securityClassificationBanner/>
</div>

<div region="south" style="height: 20px">
    <omar:securityClassificationBanner/>
</div>

<div region="center" class="mapContainerDiv">
    <div id="content" class="easyui-layout" fit="true">
        <div region="north" style="height:50px">
            <div class="easyui-panel" style="padding:5px;">
                <g:link class="easyui-linkbutton" plain="true" uri="/"><b>Home</b></g:link>
                <g:link class="easyui-linkbutton" plain="true" controller="imageList"><b>Images</b></g:link>
            </div>
        </div>

        <div data-options="region:'east',split:true" title="Layers" style="width:200px;">
            <div id="layerMgr"></div>
        </div>

        <div data-options="region:'west',split:true" title="Paramters" style="width:200px;">
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
    </div>
</div>

<%--
<r:external plugin="omar-chipper" dir="js/jquery-easyui" file="jquery.min.js"/>
<r:external plugin="omar-chipper" dir="js/jquery-easyui" file="jquery.easyui.min.js"/>
<r:external plugin="omar-chipper" dir="js/openlayers" file="OpenLayers.light.js"/>
--%>
<asset:javascript src="hillShade.js"/>
<g:javascript>
    $( document ).ready( function ()
    {
        var model = ${raw( ( model as JSON ).toString() )};
        var chipUrl = "${raw( createLink( controller: 'chipper', action: 'getChip' ) )}";
        var productUrl =  "${raw( createLink( controller: 'chipper', action: 'getHillShade' ) )}";
        var bbox = new OpenLayers.Bounds(model.minX, model.minY, model.maxX, model.maxY);

        var map = new OpenLayers.Map( 'map', {
            theme: null
        } );

        OpenLayers.ImgPath = "${resource( plugin: 'openlayers', dir: 'js/img' )}/";

        var  spinner = null;
        var  spinnerCount = 0;

       var layerEvents = {
            loadstart: function ()
            {
                //console.log( 'loadStart' );
                var opts = {
                    lines: 13, // The number of lines to draw
                    length: 8, // The length of each line
                    width: 4, // The line thickness
                    radius: 10, // The radius of the inner circle
                    corners: 1, // Corner roundness (0..1)
                    rotate: 0, // The rotation offset
                    color: '#FFFFFF', // #rgb or #rrggbb
                    speed: 1, // Rounds per second
                    trail: 60, // Afterglow percentage
                    shadow: true, // Whether to render a shadow
                    hwaccel: false, // Whether to use hardware acceleration
                    className: 'spinnerControl', // The CSS class to assign to the spinner
                    zIndex: 2e9, // The z-index (defaults to 2000000000)
                    top: '50%', // Top position relative to parent in px
                    left: '50%' // Left position relative to parent in px
                };
                if ( spinnerCount === 0 )
                {
                    spinner = new Spinner( opts ).spin($('#map')[0]);
                }
                spinnerCount++;
                //console.log("spinnerCount: " + spinnerCount);
            },
            loadend: function ()
            {
                spinnerCount--;
                //console.log("spinnerCount: " + spinnerCount);
                if ( spinnerCount === 0 )
                {
                    spinner.stop();
                    spinner = null;
                }
                //console.log( 'loadEnd' );
            },
            scope: this
        };

        var layers = [
            new OpenLayers.Layer.WMS(
                    model.baseWMS.name,
                    model.baseWMS.url,
                    model.baseWMS.params,
                    model.baseWMS.options
            ),

            new OpenLayers.Layer.WMS( "Chipper - getChip - Map",
                    chipUrl,
                    {layers: model.mapImage, format: 'image/png', transparent: true},
                    {buffer: 0, singleTile: true, ratio: 1.0, isBaseLayer: false, visibility: false, eventListeners: layerEvents} )
        ];

        for (  var x = 0; x < model.demImages.length; x++ )
        {
            layers.push( new OpenLayers.Layer.WMS( "Chipper - getChip - Elevation " + x,
                            chipUrl,
                            {layers: '', filename: model.demImages[x], format: 'image/png', transparent: true},
                            {buffer: 0, singleTile: true, ratio: 1.0, isBaseLayer: false, visibility: false, eventListeners: layerEvents} )
            );
        }

        layers.push( new OpenLayers.Layer.WMS( "Chipper - HillShade - Product",
            productUrl,
            {layers: model.mapImage, format: 'image/png', transparent: true},
            {buffer: 0, singleTile: true, ratio: 1.0, isBaseLayer: false, visibility: true, eventListeners: layerEvents} )

        );

        map.addLayers( layers );

        var controls = [
            new OpenLayers.Control.LayerSwitcher( {'div': OpenLayers.Util.getElement( 'layerMgr' )} )
        ];
        map.addControls( controls );

        map.zoomToExtent( bbox, true );

        $( '#content' ).layout( 'panel', 'center' ).panel( {
            onResize: function ()
            {
                console.log('resized');
                map.updateSize();
            }
        } );

        $('#refresh').click(function(){
            var data = $('#pg').propertygrid('getData').rows;
            var obj = {};

            data.forEach(function(item){
                obj[item.name] = item.value;
            });

            console.log(obj);
            var layer = map.getLayersByName("Chipper - HillShade - Product")[0];

            layer.mergeNewParams(obj);
        });
    } );
</g:javascript>
</body>
</html>
