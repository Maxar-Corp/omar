<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: 4/13/14
  Time: 10:14 AM
--%>

<%@ page import="grails.converters.JSON" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Image List</title>
    <%--
    <r:external plugin="omar-chipper" dir="js/jquery-easyui/themes" file="icon.css"/>
    <r:external plugin="omar-chipper" dir="js/jquery-easyui/themes/default" file="easyui.css"/>
    <r:external plugin="omar-chipper" dir="js/openlayers/theme/default" file="style.css"/>
    --%>
    <r:require modules="jeasyui,chipperBackbone,chipperOpenLayers"/>
    <r:layoutResources/>
</head>

<body class="easyui-layout">

<div region="north" style="height: 20px">
    <omar:securityClassificationBanner/>
</div>

<div region="south" style="height: 20px">
    <omar:securityClassificationBanner/>
</div>

<%--
<div region="east" style="width: 100px"></div>

<div region="west" style="width: 100px"></div>
--%>

<div region="center">

<div class="easyui-layout" fit="true">
    <div region="north" style="height:35px">
        <div class="easyui-panel" style="padding:5px;">
            <g:link class="easyui-linkbutton" plain="true" uri="/">Home</g:link>
        </div>
    </div>

    <%--
    <div region="south" style="height: 100px"></div>

    <div region="east" style="width: 100px"></div>
   --%>

    <div region="west" style="width: 325px" split="true" title="Query Parameters">
        <table id="pg" class="easyui-propertygrid"
               url="${createLink( action: 'getFilterParams' )}"
               showGroup="true" showHeader="false" scrollbarSize="0">
        </table>
        <br/>

        <div align='center'>
            <button id="applyFilter">Apply Filter</button>
            <button id="reset">Reset</button>
        </div>
    </div>

    <div region="center">
        <table id="tbl" class="easyui-datagrid" rownumbers="true" pagination="true" fit="true"
               striped="true" url="${createLink( action: 'getData' )}"></table>

        <div id="tb">
            <div style="margin-bottom:5px">
                <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-add" plain="true"
                   onclick="showFilter()">Filter</a>
                <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-edit" plain="true"
                   onclick="create2CMV()">2CMV</a>
                <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-save" plain="true"
                   onclick="createPSM()">PSM</a>
                <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cut" plain="true"
                   onclick="createHillShade()">HillShade</a>
            </div></div>
    </div>

</div>

<div id="dlg" class="easyui-dialog" title="Filter" closed="true" style="width:400px;height:200px;padding:10px"
     data-options="buttons: [{
    text:'Ok',
    plain: true,
    iconCls:'icon-ok',
    handler:function(){
    alert('ok');
    }
    },{
    text:'Cancel',
    plain: true,
    handler:function(){
    alert('cancel');;
    }
    }]">
</div>

<div id="sp" class="easyui-layout" fit="true">
    <div region="south" style="padding:5px;"> 
        <a href="javascript:void(0)" id="setBBOX" class="easyui-linkbutton" iconCls="icon-ok" plain="true">Ok</a>
        <a href="javascript:void(0)" id="unsetBBOX" class="easyui-linkbutton" iconCls="icon-cancel"
           plain="true">Cancel</a>
    </div>

    <div region="center">
        <div id="map" style="width: 512px;height: 256px"></div>
    </div>
</div>

<%--
<r:external plugin="omar-chipper" dir="js/jquery-easyui" file="jquery.min.js"/>
<r:external plugin="omar-chipper" dir="js/jquery-easyui" file="jquery.easyui.min.js"/>
<r:external plugin="omar-chipper" dir="js/openlayers" file="OpenLayers.light.js"/>
--%>
<r:script>

    function showFilter()
    {
        $( '#dlg' ).dialog( 'center' ).dialog( 'open' );
    }

    function getSelectedImages()
    {
        var rows = $('#tbl').datagrid('getSelections');

        return rows;
    }

    function create2CMV()
    {
        var images = getSelectedImages();

        if ( images.length === 2 )
        {
            var redImage = images[0].id;
            var blueImage =  images[1].id;

            window.location = '${createLink( controller: "twoColorMulti" )}?redImage=' + redImage + '&blueImage=' + blueImage;
        }
        else
        {
            $.messager.alert('2CMV', 'Must pick exactly 2 images', 'error');
        }
    }

    function createPSM()
    {
        var images = getSelectedImages();

        if ( images.length === 2 )
        {
            var panImage = (images[0].numberOfBands === 1) ? images[0].id : images[1].id;
            var colorImage = (images[0].numberOfBands > 1) ? images[0].id : images[1].id;

            window.location = '${createLink( controller: "panSharpen" )}?panImage=' + panImage + '&colorImage=' + colorImage;
        }
        else
        {
            $.messager.alert('PSM', 'Must pick exactly 2 images', 'error');
        }
    }

    function createHillShade()
    {
        var images = getSelectedImages();

        if ( images.length === 1 )
        {
            var mapImage = images[0].id;

            window.location = '${createLink( controller: "hillShade" )}?mapImage=' + mapImage;
        }
        else
        {
            $.messager.alert('HillShade', 'Must pick exactly 1 image', 'error');
        }
    }

    $( document ).ready( function ()
    {
        var tableModel = ${tableModel as JSON};
        var map = initMap();

        $('#sp #setBBOX').click(function(e){
            var bbox = map.getExtent().toString();

            $('#cc').combo('setText', bbox).combo('hidePanel');
            console.log(bbox);

        });
        $('#sp #unsetBBOX').click(function(e){
            map.zoomToMaxExtent();
            var bbox = map.getExtent().toString();

            console.log(bbox);
            $('#cc').combo('setText', null).combo('hidePanel');
        });

        $.extend($.fn.propertygrid.defaults.editors, {
            mapbox: {
                init: function(container, options){
                    console.log('init');
                    var input = $('<input id="cc">').appendTo(container);
                    input.combo(options);
                    $( '#sp' ).appendTo( input.combo( 'panel' ) );

                    return input
                },
                destroy: function(target){
                    console.log('destroy');
                    $(target).combo('destroy');
                },
                getValue: function(target){
                    console.log('getValue');
                    return $(target).combo('getValue');
                },
                setValue: function(target, value){
                    console.log('setValue');
                    $(target).combo('setValue', value);
                },
                resize: function(target, width){
                    console.log('resize');
                    $(target).combo('resize', width);
                }
            },
            datetimebox: {
                init: function(container, options){
                    var input = $('<input>').appendTo(container);
                    input.datetimebox(options);
                    return input
                },
                destroy: function(target){
                    $(target).datetimebox('destroy');
                },
                getValue: function(target){
                    return $(target).datetimebox('getValue');
                },
                setValue: function(target, value){
                    $(target).datetimebox('setValue', value);
                },
                resize: function(target, width){
                    $(target).datetimebox('resize', width);
                }
            }
    	});

        function showThumbnail( val, row )
        {
            var size = 128;

            var thumbnailURL = "${g.createLink( controller: 'chipper', action: 'getThumbnail' )}?id="
                + row.id + "&size=" + size;

            var imgTag = "<img src='" + thumbnailURL + "' width='" + size + "' height='" + size + "'/>";

            return imgTag;
        }

        $('#reset').click(function(){
          $('#pg').propertygrid('reload');
        });

         $('#applyFilter').click(function(){
            var data = $('#pg').propertygrid('getData').rows;
            var filter = "";

            _.each( data, function(item){
                if ( item.value )
                {
                    if ( ! ( filter === "" ) )
                    {
                        filter += " AND ";
                    }

                    if ( item.name === "Format")
                    {
                        filter += "file_type='" + item.value + "'";
                    }
                    else if ( item.name === "Start Date")
                    {
                        filter += "acquisition_date >='" + item.value + "'";
                    }
                    else if ( item.name === "End Date")
                    {
                        filter += "acquisition_date <='" + item.value + "'";
                    }
                    else if ( item.name === "Mission")
                    {
                        filter += "mission_id='" + item.value + "'";
                    }
                    else if ( item.name === "Sensor")
                    {
                        filter += "sensor_id='" + item.value + "'";
                    }
                    else if ( item.name === "Filename")
                    {
                        filter += "filename ilike '%" + item.value + "%'";
                    }
                    else if ( item.name === "Image Id")
                    {
                        filter += "image_id ilike '%" + item.value + "%'";
                    }
                    else if ( item.name === "Intersects")
                    {
                        filter += "ground_geom && ST_MakeEnvelope(" + item.value + ", 4326)";
                    }
                }
            });
            console.log(filter);

            $('#tbl').datagrid('load', {filter: filter});
        });


        function styleThumbnail( value, row, index )
        {
            return {style: 'width:128px; height:128px'};
        }


        $.extend(true, tableModel, {
            toolbar: '#tb',
            frozenColumns: [[
                {field: 'ck', checkbox: true},
                {field: 'thumbnail', title: 'Thumbnail', formatter: showThumbnail, styler: styleThumbnail}
            ]]
        });
        var dg  = $('#tbl').datagrid(tableModel);


        OpenLayers.ImgPath = "${resource( plugin: 'openlayers', dir: 'js/img' )}/";

        function zoomChanged(e)
        {
            //console.log(e);
        }


        function initMap()
        {
            var map = new OpenLayers.Map('map', {theme: null});
            var layers = [
            new OpenLayers.Layer.WMS( "OpenLayers WMS",
                "http://vmap0.tiles.osgeo.org/wms/vmap0",
                {layers: 'basic'}    )
            ];
            map.addLayers(layers);
            //map.extent = new OpenLayers.Bounds(-180, -90, 180, 90);
            map.updateSize();
            map.zoomToMaxExtent();
            return map
        }
    } );
</r:script>
<r:layoutResources/>
</body>
</html>