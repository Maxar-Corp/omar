<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: 3/26/14
  Time: 10:41 AM
--%>

<%@ page import="grails.converters.JSON" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Example Table</title>
    <r:external dir="js/red-query-builder/RedQueryBuilder/gwt/dark" file="dark.css"/>

    <r:external dir="js/jquery-easyui/themes/default" file="easyui.css"/>
    <r:external dir="js/jquery-easyui/themes" file="icon.css"/>
    <r:external dir="js/jquery-easyui/demo" file="demo.css"/>

    <r:require module="jquery"/>
    <r:layoutResources/>

    <style type="text/css">
    .panel-body {
        background-color: transparent;
    }
    </style>
</head>

<body>
<table id="dg" class="easyui-datagrid" title="Raster Entry"
       url="${tableModel.url}"
       method="get"
       striped="true"
       pagination="true"
       rownumbers="true"
       toolbar="#tb"
       fit="true">
    <thead>
    <tr>
        <th field="ck" checkbox="true"></th>
        <th field="thumbnail" formatter="showThumbnail" styler="styleThumbnail">Thumbnail</th>
        <g:each in="${tableModel.columns}">
            <th field="${it.field}" align="${it.align ?: 'left'}"
                sortable="true" resizable="true">${( it.field.split( '_' )*.capitalize() ).join( ' ' )}</th>
        </g:each>
    </tr>
    </thead>
</table>

<div id="tb">
    <div>
        <a href="#" class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="javascript:addHandler();"></a>
        <a href="#" class="easyui-linkbutton" iconCls="icon-edit" plain="true" onclick="javascript:addHandler();"></a>
        <a href="#" class="easyui-linkbutton" iconCls="icon-save" plain="true" onclick="javascript:addHandler();"></a>
        <a href="#" class="easyui-linkbutton" iconCls="icon-cut" plain="true" onclick="javascript:addHandler();"></a>
        <a href="#" class="easyui-linkbutton" iconCls="icon-remove" plain="true" onclick="javascript:addHandler();"></a>
    </div>
</div>

<div id="dlg" closed="true" class="easyui-dialog" title="Basic Dialog" data-options="iconCls:'icon-save'"
     style="width:600px;height:400px;padding:10px" resizable="true">
     <div id="rqb">&nbsp;</div>
</div>

<r:external dir="js/red-query-builder/RedQueryBuilder" file="RedQueryBuilder.nocache.js"/>
<r:external dir="js/red-query-builder/RedQueryBuilder" file="RedQueryBuilderFactory.nocache.js"/>

<r:external dir="js/jquery-easyui" file="jquery.easyui.min.js"/>
<r:script>
    $( document ).ready( function ()
    {
        RedQueryBuilderFactory.create( ${( tableMetadata as JSON ).toString()} ) ;

        $('#dlg').dialog({
            buttons: [{
                text:'Ok',
                iconCls:'icon-ok',
                handler:function(){
                    alert('ok');
                }
            },{
                text:'Cancel',
                handler:function(){
                    alert('cancel');
                }
            }]
          });
        $( '#dg' ).datagrid( {
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
                // $( '#dg' ).datagrid( 'autoSizeColumn' );
                // $(this).datagrid('getPanel').find('a.easyui-linkbutton').linkbutton();
            }
        } );
    } );


    function addHandler()
    {
        var dlg = $( '#dlg' );

        dlg.dialog( 'open' );
    }

    function showThumbnail( val, row )
    {
        var size = 128;
        var serviceAddress = "http://omar.ngaiost.org/omar";
        var thumbnailURL = serviceAddress + "/thumbnail/show/" + row.id + "?size=128";
        var viewerURL = serviceAddress + "/mapView/index?layers=" + row.id;
        var markup = "<a href='" + viewerURL + "'><img src='" + thumbnailURL + "' width='" + size + "'  height='" + size + "'/></a>";
//        var markup = "<a href='" + viewerURL + "'><img src='" + thumbnailURL + "'/></a>";

        return markup;
    }

    function styleThumbnail( value, row, index )
    {
        return {style: 'width:128px; height:128px'};
    }
</r:script>
<r:layoutResources/>
</body>
</html>