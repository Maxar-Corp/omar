<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: 4/7/14
  Time: 3:52 PM
--%>

<%@ page import="grails.converters.JSON" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title></title>
    <r:require module="standard"/>
    <r:layoutResources/>
</head>

<body>
<table id="tbl"></table>

<div id="tb">
    <div style="margin-bottom:5px">
        <a href="#" class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="showFilter()">Filter</a>
        <a href="#" class="easyui-linkbutton" iconCls="icon-edit" plain="true" onclick="create2CMV()">2CMV</a>
        <a href="#" class="easyui-linkbutton" iconCls="icon-save" plain="true" onclick="createPSM()">PSM</a>
        <a href="#" class="easyui-linkbutton" iconCls="icon-cut" plain="true" onclick="createHillShade()">HillShade</a>
    </div>
</div>

<div id="dlg" class="easyui-dialog" style="width:600px; height:480px; margin: 0px auto;"
     closed="true" title="Add Filter" resizable="true" modal="true" buttons="#dlg-buttons">
    <div class="query"></div>
    <button id="btnCondition">Get Condition</button>
    <button id="btnQuery">Get Query</button>
</div>

<div id="dlg-buttons">
    <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-ok" onclick="javascript:alert( 'Ok' )"
       plain="true">Ok</a>
    <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon"
       onclick="javascript:$( '#dlg' ).dialog( 'close' )" plain="true">Cancel</a>
</div>

<r:script>


    function showFilter()
    {
        $( '#dlg' ).dialog( 'center' );
        $( '#dlg' ).dialog( 'open' );
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
            var redImage = images[0].filename;
            var blueImage =  images[1].filename;

            window.location = '../twoColorMulti?redImage=' + redImage + '&blueImage=' + blueImage;
        }
        else
        {
            alert('must pick exactly 2 images');
        }
    }

    function createPSM()
    {
        var images = getSelectedImages();

        if ( images.length === 2 )
        {
            var panImage = (images[0].numBands === 1) ? images[0].filename : images[1].filename;
            var colorImage = (images[0].numBands > 1) ? images[0].filename : images[1].filename;

            window.location = '../panSharpenMultiView?panImage=' + panImage + '&colorImage=' + colorImage;
        }
        else
        {
            alert('must pick exactly 2 images');
        }
    }

    function createHillShade()
    {
        var images = getSelectedImages();

        if ( images.length === 1 )
        {
            var mapImage = images[0].filename;

            window.location = '../hillShade?mapImage=' + mapImage;
        }
        else
        {
            alert('must pick exactly 1 image');
        }
    }

    $( document ).ready( function ()
    {
        function showThumbnail( val, row )
        {
            var size = 128;

            var thumbnailURL = "${g.createLink( controller: 'chipper', action: 'getThumbnail' )}?layers="
                + row.filename + "&size=" + size;

            var imgTag = "<img src='" + thumbnailURL + "' width='" + size + "' height='" + size + "'/>";

            return imgTag;
        }

        function styleThumbnail( value, row, index )
        {
            return {style: 'width:128px; height:128px'};
        }

        var tableModel = ${tableModel as JSON};

        $.extend(true, tableModel, {
            toolbar: '#tb',
            frozenColumns: [[
                {field: 'ck', checkbox: true},
                {field: 'thumbnail', title: 'Thumbnail', formatter: showThumbnail, styler: styleThumbnail}
            ]],
            striped: true,
            rownumbers: true,
            pagination:  true,
            fit: true
        });

        $('#tbl' ).datagrid(tableModel);

        var conditionBuilderView = new ConditionBuilderView( {
            el: $( '.query' ),
            model: new ConditionBuilderModel( {
                columns: tableModel.columns[0],
                operators: tableModel.operators,
                editors: [

                ]
            } )
        } );

        $( '#btnCondition' ).click( function ()
        {
            var query = conditionBuilderView.getCondition( '.query > table' );
            //var l = JSON.stringify(query,null,4);
            var l = JSON.stringify( query );
            alert( l );
        } );

        $( '#btnQuery' ).click( function ()
        {
            var con = conditionBuilderView.getCondition( '.query > table' );
            var k = conditionBuilderView.getQuery( con );
            alert( k );
        } );
    } );

</r:script>
<r:layoutResources/>
</body>
</html>