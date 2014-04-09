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
        <a href="#" class="easyui-linkbutton" iconCls="icon-add" plain="true">Filter</a>
        <a href="#" class="easyui-linkbutton" iconCls="icon-edit" plain="true">2CMV</a>
        <a href="#" class="easyui-linkbutton" iconCls="icon-save" plain="true">PSM</a>
        <a href="#" class="easyui-linkbutton" iconCls="icon-cut" plain="true">HillShade</a>
    </div>
</div>

<div id="#tb"></div>
<r:script>
    function showThumbnail( val, row )
    {
        var size = 128;

        var thumbnailURL = "${g.createLink(controller: 'chipper', action: 'getThumbnail')}?layers="
            + row.filename + "&size=" + size;

        var imgTag = "<img src='" + thumbnailURL + "' width='" + size + "'  height='" + size + "'/>";

//        return '';
        return imgTag;
    }

    function styleThumbnail( value, row, index )
    {
        return {style: 'width:128px; height:128px'};
    }

    $( document ).ready( function ()
    {
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
    } );

</r:script>
<r:layoutResources/>
</body>
</html>