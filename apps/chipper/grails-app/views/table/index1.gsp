<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: 3/26/14
  Time: 10:39 PM
--%>

<%@ page import="grails.converters.JSON" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Table 1</title>
    <r:external dir="js/jquery-easyui/themes/default" file="easyui.css"/>
    <r:external dir="js/jquery-easyui/themes" file="icon.css"/>
    <r:external dir="js/jquery-easyui/demo" file="demo.css"/>
    <r:require module="jquery"/>
    <r:layoutResources/>

</head>

<body>
<table id="dg" title="Raster Entry"></table>

<r:external dir="js/jquery-easyui" file="jquery.easyui.min.js"/>
<r:script>
    $( document ).ready( function ()
    {
        var serviceAddress = "http://omar.ngaiost.org/omar";
        var thumbnailSize = 128;
        var frozenColumns = [
            {field: 'chk', checkbox: true},
            {field: 'thumbnail', title: 'Thumbnail', resizable: true,  formatter: function(value,row,index) {
                var thumbnailUrl = serviceAddress + "/thumbnail/show/" + row.id + "?size=" + thumbnailSize;
                var viewerUrl =  serviceAddress + "/mapView/index?layers=" + row.id;
                var imgTag = "<img src='" + thumbnailUrl + "' width='" + thumbnailSize + "' height='" + thumbnailSize + "'/>";
                var anchorTag = "<a href='" + viewerUrl + "'>" + imgTag + "</a>";
                return anchorTag;
                }, styler: function(value,row,index) {
                        return {style: 'width:' + thumbnailSize + 'px;height:' + thumbnailSize + 'px'};
                }
            }
          ];

//        var columns  = frozenColumns.concat(${tableModel.columns as JSON});
         var columns  = ${tableModel.columns as JSON};

        console.log( columns) ;

    $( '#dg' ).datagrid( {
        url: '${tableModel.url}',
        method: 'get',
        frozenColumns: [frozenColumns],
        columns: [ columns ],
        striped: true,
        pagination: true,
        rownumbers:true,
        fit: true,
        onSelect: function ( rowIndex, rowData )
        {
            console.log( 'onSelect: ' + rowIndex + ' ' + rowData );
        },
        onUnselect: function ( rowIndex, rowData )
        {
            console.log( 'onUnselect: ' + rowIndex + ' ' + rowData );
        },
        onLoadSuccess: function ( data )
        {
//            $( this ).datagrid( 'fixRowHeight' );
//            $( this ).datagrid( 'fixColumnSize' );
        },
        toolbar: [{
            text:'Add',
            iconCls:'icon-add',
            handler:function(){alert('add')}
        },{
            text:'Cut',
            iconCls:'icon-cut',
            handler:function(){alert('cut')}
        },'-',{
            text:'Save',
            iconCls:'icon-save',
            handler:function(){alert('save')}
        }]
    } );
    } );
</r:script>
<r:layoutResources/>
</body>
</html>