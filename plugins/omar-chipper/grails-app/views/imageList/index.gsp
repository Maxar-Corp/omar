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
    <piwik:trackPageview/>
    <style type="text/css">
    .customEditingToolbar {
        float: right;
        right: 0px;
        height: 30px;
        background-color: lightgray;
    }

    .customEditingToolbar div {
        float: right;
        margin: 5px;
        width: 24px;
        height: 24px;
    }

    .okItemInactive {
        background-image: url(${resource(plugin: 'omar-chipper', dir: 'js/jquery-easyui/themes/icons', file: 'ok.png')});
        background-repeat: no-repeat;
        /*background-position: 0 1px;*/

        /*background: #30d5c8;
        border: 1px solid #000000;
        */
        width: 18px;
        height: 18px;
    }

    .cancelItemInactive {
        background-image: url(${resource(plugin: 'omar-chipper', dir: 'js/jquery-easyui/themes/icons', file: 'cancel.png')});
        background-repeat: no-repeat;
        background-position: 0 1px;

        /*background: #ffffdd;
        border: 1px solid #000000;
        */
        width: 18px;
        height: 18px;
    }
    </style>

    <%--
    <r:external plugin="omar-chipper" dir="js/jquery-easyui/themes" file="icon.css"/>
    <r:external plugin="omar-chipper" dir="js/jquery-easyui/themes/default" file="easyui.css"/>
    <r:external plugin="omar-chipper" dir="js/openlayers/theme/default" file="style.css"/>
    --%>
    <asset:stylesheet src="imageList.css"/>
</head>

<body class="easyui-layout">

<div region="north" class="security-banner" style="overflow:hidden;">
    <omar:securityClassificationBanner/>
</div>

<div region="south" class="security-banner" style="overflow:hidden">
    <omar:securityClassificationBanner/>
</div>

<%--
<div region="east" style="width: 100px"></div>

<div region="west" style="width: 100px"></div>
--%>

<div region="center">

    <div class="easyui-layout" fit="true">
        <div region="north" style="overflow:hidden;">
            <div class="easyui-panel" style="overflow:hidden;padding:5px;">
                <g:link class="easyui-linkbutton" plain="true" uri="/">Home</g:link>
            </div>
        </div>

        <%--
        <div region="south" style="height: 100px"></div>

        <div region="east" style="width: 100px"></div>
       --%>

        <div region="west" style="max-width: 325px" split="true" title="Query Parameters">
            <table id="pg" class="easyui-propertygrid"
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

    %{--<div id="dlg" class="easyui-dialog" title="Filter" closed="true" style="width:400px;height:200px;padding:10px"--}%
    %{--data-options="buttons: [{--}%
    %{--text:'Ok',--}%
    %{--plain: true,--}%
    %{--iconCls:'icon-ok',--}%
    %{--handler:function(){--}%
    %{--alert('ok');--}%
    %{--}--}%
    %{--},{--}%
    %{--text:'Cancel',--}%
    %{--plain: true,--}%
    %{--handler:function(){--}%
    %{--alert('cancel');;--}%
    %{--}--}%
    %{--}]">--}%
    %{--</div>--}%
</div>

<%--
<r:external plugin="omar-chipper" dir="js/jquery-easyui" file="jquery.min.js"/>
<r:external plugin="omar-chipper" dir="js/jquery-easyui" file="jquery.easyui.min.js"/>
<r:external plugin="omar-chipper" dir="js/openlayers" file="OpenLayers.light.js"/>
--%>
<asset:javascript src="imageList.js"/>
<g:javascript>


    function showFilter()
    {
        $( '#dlg' ).dialog( 'center' ).dialog( 'open' );
    }

    function getSelectedImages()
    {
        var rows = $('#tbl').datagrid('getSelections');

        return rows;
    }

    function createPolygon(image)
    {
        var points = _.collect( image.groundGeom.coordinates[0], function(point) {
                return new OpenLayers.Geometry.Point(point[0], point[1]);
            } );
        var ring = new OpenLayers.Geometry.LinearRing(points)
        return new OpenLayers.Geometry.Polygon(ring);
    }

    function checkForIntersect(images)
    {
        var polygons = _.collect( images, function(image){
            return createPolygon(image);
        });

        return polygons[0].intersects(polygons[1]);
    }

    function create2CMV()
    {
        var images = getSelectedImages();

        if ( images.length === 2 )
        {
            if ( ! checkForIntersect(images))
            {
              $.messager.alert('2CMV', 'Images do not intersect.', 'error');
              return;
            }

            var redImage = images[0].id;
            var blueImage =  images[1].id;

            window.location = '${raw( createLink( controller: "twoColorMulti" ).toString() )}?redImage=' + redImage + '&blueImage=' + blueImage;
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
            if ( ! checkForIntersect(images))
            {
              $.messager.alert('PSM', 'Images do not intersect.', 'error');
              return;
            }

            if ( ! ( images[0].numberOfBands === 1 || images[1].numberOfBands === 1 ) )
            {
                $.messager.alert('PSM', 'At least one image must be a Pan Chromatic.');
                return;
            }
            else if ( images[0].numberOfBands === 1 && images[1].numberOfBands === 1)
            {
                $.messager.alert('PSM', 'At least one image must have 3 or more bands.');
                return;
            }

            var panImage = (images[0].numberOfBands === 1) ? images[0] : images[1];
            var colorImage = (images[0].numberOfBands > 1) ? images[0] : images[1];

            console.log(panImage);
            console.log(colorImage);

            if ( panImage.gsdY > colorImage.gsdY )
            {
                $.messager.alert('PSM', 'Pan Image is lower resolution than Color Image.');
                return;
            }

            window.location = '${raw( createLink( controller: "panSharpen" ).toString() )}?panImage='
                + panImage.id + '&colorImage=' + colorImage.id;
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

            window.location = '${raw( createLink( controller: "hillShade" ).toString() )}?mapImage=' + mapImage;
        }
        else
        {
            $.messager.alert('HillShade', 'Must pick exactly 1 image', 'error');
        }
    }

    $(document).ready(function(){
    {
        var tableModel = ${raw( ( tableModel as JSON ).toString() )};

        var geomCol = _.find( tableModel.columns[0], function(it) {
            return (it.field === 'groundGeom');
        } );

        if ( geomCol )
        {
            geomCol.formatter =  showBBOX;
        }

        OpenLayers.ImgPath = "${raw( resource( plugin: 'omar-chipper', dir: 'js/openlayers/img' ).toString() )}/";

        $.extend($.fn.propertygrid.defaults.editors, {
            mapbox: {
                init: function ( container, options )
                {
                    //console.log( 'init' );
                    var input = $( '<select>' ).appendTo( container );
                    input.combo( options );

                    $( "<div id='map' style='width: 512px; height: 256px;'></div>" ).appendTo( input.combo( 'panel' ) );

                    this.map = new OpenLayers.Map('map', {theme: null});

                    var baseWMS = ${raw( ( baseWMS as JSON ).toString() )};
                    var layers = [
                        new OpenLayers.Layer.WMS(
                            baseWMS.name,
                            baseWMS.url,
                            baseWMS.params,
                            baseWMS.options
                        )
                    ];
                    this.map.addLayers( layers );

                    var panel = new OpenLayers.Control.Panel( {
                        type: OpenLayers.Control.TYPE_BUTTON,
                        displayClass: 'customEditingToolbar',
                        allowDepress: true
                    } )

                    var buttons = [
                        new OpenLayers.Control.Button( {
                            title: 'Cancel',
                            trigger: function ()
                            {
                                //console.log(this);

                                this.map.zoomToMaxExtent();
                                var bbox = this.map.getExtent().toString();

                                //console.log(bbox);
                                input.combo( 'setValue', null ).combo( 'setText', bbox ).combo( 'hidePanel' );
                            },
                            displayClass: "cancel"
                        } ),
                        new OpenLayers.Control.Button( {
                            title: 'Ok',
                            trigger: function ()
                            {
                                //console.log(this);
                                var bbox = this.map.getExtent().toString();

                                //console.log(bbox);
                                input.combo( 'setValue', bbox ).combo( 'setText', bbox ).combo( 'hidePanel' );
                            },
                            displayClass: "ok"
                        } )
                    ]
                    panel.addControls( buttons );

                    var controls = [
                        panel
                    ];
                    this.map.addControls( controls );
                    //map.extent = new OpenLayers.Bounds(-180, -90, 180, 90);
                    this.map.updateSize();
                    this.map.zoomToMaxExtent();
                    return input
                },
                destroy: function ( target )
                {
                    //console.log( 'destroy' );
                    this.map.destroy();
                    $( target ).combo( 'destroy' );
                },
                getValue: function ( target )
                {
                    //console.log( 'getValue' );
                    var bbox = this.map.getExtent().toString();

                    //console.log( bbox );
                    $( target ).combo( 'setValue', bbox );

                    return $( target ).combo( 'getValue' );
                },
                setValue: function ( target, value )
                {
                    //console.log( 'setValue' + value );

                    if ( typeof value === 'undefined' || value === "" )
                    {
                        this.map.zoomToMaxExtent();
                    }
                    else
                    {
                        this.map.zoomToExtent( new OpenLayers.Bounds( value.split( ',' ) ), true );
                    }

                    $( target ).combo( 'setValue', value );
                },
                resize: function ( target, width )
                {
                    //console.log( 'resize' );
                    this.map.updateSize();
                    $( target ).combo( 'resize', width );
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

            var thumbnailURL = "${raw( g.createLink( controller: 'chipper', action: 'getThumbnail' ).toString() )}?id="
                + row.id + "&size=" + size + '&type=jpeg';

            var imgTag = "<img src='" + thumbnailURL + "' width='" + size + "' height='" + size + "'/>";

            return imgTag;
        }


        function showBBOX( val, row )
        {
            return createPolygon(row).getBounds();
        }

        var filterParams = ${raw( ( filterParams as JSON ).toString() )};

        $('#pg').propertygrid({
            data: filterParams
        });

        $('#reset').click(function(){
            _.each( filterParams, function(row) {
                row.value = null;
            } );

            $('#pg').propertygrid({
                data: filterParams
            });
            //console.log(filterParams);
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
                        filter += "acquisition_date >= TIMESTAMP WITH TIME ZONE '" + item.value + "Z'";
                    }
                    else if ( item.name === "End Date")
                    {
                        filter += "acquisition_date <= TIMESTAMP WITH TIME ZONE '" + item.value + "Z'";
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


        OpenLayers.ImgPath = "${raw( resource( plugin: 'openlayers', dir: 'js/img' ).toString() )}/";

        function zoomChanged(e)
        {
            //console.log(e);
        }


        function initMap()
        {
            var baseWMS = ${raw( ( baseWMS as JSON ).toString() )};
            var map = new OpenLayers.Map('map', {theme: null});
            var layers = [
                new OpenLayers.Layer.WMS(
                    baseWMS.name,
                    baseWMS.url,
                    baseWMS.params,
                    baseWMS.options
                )
            ];
            map.addLayers(layers);
            //map.extent = new OpenLayers.Bounds(-180, -90, 180, 90);
            map.updateSize();
            map.zoomToMaxExtent();
            return map
        }
    }
});
</g:javascript>
</body>
</html>
