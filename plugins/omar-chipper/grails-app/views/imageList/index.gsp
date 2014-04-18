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
    <r:external plugin="omar-chipper" dir="js/jquery-easyui" file="jquery.min.js"/>
    <r:external plugin="omar-chipper" dir="js/jquery-easyui/themes" file="icon.css"/>
    <r:external plugin="omar-chipper" dir="js/jquery-easyui/themes/default" file="easyui.css"/>
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

        <div region="west" style="width: 200px">
            <table id="pg" class="easyui-propertygrid"
                   url="${createLink( action: 'getFilterParams' )}"
                   showGroup="true" showHeader="false" scrollbarSize="0">
            </table>
            <br/>

            <div align='center'>
                <button id="applyFilter">Apply Filter</button>
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

    <r:external plugin="omar-chipper" dir="js/jquery-easyui" file="jquery.easyui.min.js"/>
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
            var redImage = images[0].id;
            var blueImage =  images[1].id;

            window.location = '${createLink( controller: "twoColorMulti" )}?redImage=' + redImage + '&blueImage=' + blueImage;
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
            var panImage = (images[0].numberOfBands === 1) ? images[0].id : images[1].id;
            var colorImage = (images[0].numberOfBands > 1) ? images[0].id : images[1].id;

            window.location = '../panSharpen?panImage=' + panImage + '&colorImage=' + colorImage;
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
            var mapImage = images[0].id;

            window.location = '../hillShade?mapImage=' + mapImage;
        }
        else
        {
            alert('must pick exactly 1 image');
        }
    }

    $( document ).ready( function ()
    {
        var tableModel = ${tableModel as JSON};

        function showThumbnail( val, row )
        {
            var size = 128;

            var thumbnailURL = "${g.createLink( controller: 'chipper', action: 'getThumbnail' )}?id="
                + row.id + "&size=" + size;

            var imgTag = "<img src='" + thumbnailURL + "' width='" + size + "' height='" + size + "'/>";

            return imgTag;
        }

         $('#applyFilter').click(function(){
            var data = $('#pg').propertygrid('getData').rows;
            var obj = {};

            data.forEach(function(item){
                obj[item.name] = item.value;
            });

            console.log(obj);
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


// Need to figure out how add date/time editor

/*
        $.extend($.fn.propertygrid.defaults.editors, {
            datetimebox: {
                init: function(container, options){
                    var input = $('<input class="easyui-datebox">').appendTo(container);
                    return input;
                },
                destroy: function(target){
                    $(target).remove();
                },
                getValue: function(target){
                    return $(target).val();
                },
                setValue: function(target, value){
                    $(target).val(value);
                },
                resize: function(target, width){
                    $(target)._outerWidth(width);
                }
            }
        });
*/
    } );
    </r:script>
    <r:layoutResources/>
</body>
</html>