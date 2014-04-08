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
<div id="#tb"></div>
<r:script>
    function showThumbnail( val, row )
    {
        var markup = '';
/*
        var size = 128;
        var serviceAddress = "http://omar.ngaiost.org/omar";
        var thumbnailURL = serviceAddress + "/thumbnail/show/" + row.id + "?size=128";
        var viewerURL = serviceAddress + "/mapView/index?layers=" + row.id;
        var markup = "<a href='" + viewerURL + "'><img src='" + thumbnailURL + "' width='" + size + "'  height='" + size + "'/></a>";
//        var markup = "<a href='" + viewerURL + "'><img src='" + thumbnailURL + "'/></a>";
*/

        return markup;
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
                {field: 'thumbnail', title: 'Thumbnail', formmater: showThumbnail, styler: styleThumbnail}
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