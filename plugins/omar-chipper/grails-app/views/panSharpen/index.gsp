<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: 4/14/14
  Time: 8:52 AM
--%>

<%@ page import="grails.converters.JSON" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Pan Sharpen MultiView</title>
    <%--
    <r:external plugin="omar-chipper" dir="js/jquery-easyui/themes" file="icon.css"/>
    <r:external plugin="omar-chipper" dir="js/jquery-easyui/themes/default" file="easyui.css"/>
    <r:external plugin="omar-chipper" dir="js/openlayers/theme/default" file="style.css"/>
    --%>
    <r:require modules="jeasyui,chipperOpenLayers,spinner"/>
    <r:layoutResources/>
</head>

<body class="easyui-layout">
<div region="north" style="height: 20px">
    <omar:securityClassificationBanner/>
</div>

<div region="south" style="height: 20px">
    <omar:securityClassificationBanner/>
</div>

<div region="center">
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
<r:script>
    $( document ).ready( function ()
    {
        var model = ${model as JSON};
        var chipUrl = "${createLink( controller: 'chipper', action: 'getChip' )}";
        var productUrl = "${createLink( controller: 'chipper', action: 'getPSM' )}";
        var bbox = new OpenLayers.Bounds(model.minX, model.minY, model.maxX, model.maxY);

        var map = new OpenLayers.Map( 'map', {
            theme: null
        } );

        OpenLayers.ImgPath = "${resource( plugin: 'openlayers', dir: 'js/img' )}/";

         var layerEvents = {
            spinner: null,
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
                    top: 'auto', // Top position relative to parent in px
                    left: 'auto' // Left position relative to parent in px
                };
                this.spinner = new Spinner( opts ).spin($('#map')[0]);
            },
            loadend: function ()
            {
                this.spinner.stop();
                this.spinner = null;
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
            new OpenLayers.Layer.WMS( "Chipper - PSM - Color",
                    chipUrl,
                    {layers: model.colorImage, format: 'image/png', transparent: true},
                    {buffer: 0, singleTile: true, ratio: 1.0, isBaseLayer: false, visibility: false,
                        eventListeners: layerEvents
                    } ),

            new OpenLayers.Layer.WMS( "Chipper - PSM - Pan",
                    chipUrl,
                    {layers: model.panImage, format: 'image/png', transparent: true},
                    {buffer: 0, singleTile: true, ratio: 1.0, isBaseLayer: false, visibility: false,
                        eventListeners: layerEvents
                    } ),

            new OpenLayers.Layer.WMS( "Chipper - PSM - Product",
                    productUrl,
                    {layers: '', colorImage: model.colorImage, panImage: model.panImage, format: 'image/png', transparent: true},
                    {buffer: 0, singleTile: true, ratio: 1.0, isBaseLayer: false, visibility: true,
                        eventListeners: layerEvents
                    } )
        ];
        map.addLayers( layers );

        var controls = [
            new OpenLayers.Control.LayerSwitcher({'div':OpenLayers.Util.getElement('layerMgr')})
        ];
        map.addControls( controls );

        map.zoomToExtent( bbox, true );

        var body = $( '#content' );

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