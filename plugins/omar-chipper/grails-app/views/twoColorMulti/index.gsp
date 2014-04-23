<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: 4/14/14
  Time: 8:52 AM
--%>

<%@ page import="grails.converters.JSON" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Two Color MultiView</title>
    <%--
    <r:external plugin="omar-chipper" dir="js/jquery-easyui/themes" file="icon.css"/>
    <r:external plugin="omar-chipper" dir="js/jquery-easyui/themes/default" file="easyui.css"/>
    <r:external plugin="omar-chipper" dir="js/openlayers/theme/default" file="style.css"/>
    --%>
    <r:require modules="jeasyui,chipperOpenLayers"/>
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
        var productUrl = "${createLink( controller: 'chipper', action: 'get2CMV' )}";
        var bbox = new OpenLayers.Bounds(model.minX, model.minY, model.maxX, model.maxY);

        var map = new OpenLayers.Map( 'map', {
            themes: null
        } );

        OpenLayers.ImgPath = "${resource( plugin: 'openlayers', dir: 'js/img' )}/";

        var layers = [
            new OpenLayers.Layer.WMS(
                    model.baseWMS.name,
                    model.baseWMS.url,
                    model.baseWMS.params,
                    model.baseWMS.options
            ),

            new OpenLayers.Layer.WMS( "Chipper - 2CMV - Red",
                    chipUrl,
                    {layers: model.redImage, format: 'image/png', transparent: true},
                    {buffer: 0, singleTile: true, ratio: 1.0, isBaseLayer: false, visibility: false} ),

            new OpenLayers.Layer.WMS( "Chipper - 2CMV - Blue",
                    chipUrl,
                    {layers: model.blueImage, format: 'image/png', transparent: true},
                    {buffer: 0, singleTile: true, ratio: 1.0, isBaseLayer: false, visibility: false} ),

            new OpenLayers.Layer.WMS( "Chipper - 2CMV - Product",
                    productUrl,
                    {layers: '', redImage: model.redImage, blueImage: model.blueImage, format: 'image/png', transparent: true},
                    {buffer: 0, singleTile: true, ratio: 1.0, isBaseLayer: false, visibility: true} )
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