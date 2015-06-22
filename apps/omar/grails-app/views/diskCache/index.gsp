<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ page import="grails.converters.JSON" contentType="text/html;charset=UTF-8" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<asset:stylesheet src="diskCachePage.css"/>
<title>OMAR <g:meta name="app.version"/>: Disk Cache Locations</title>
<style type="text/css">
.banner {
    overflow: hidden;
}
</style>
</head>
<body class="easyui-layout" id="DiskCachePagId">

<div region="north" class="banner">
    <omar:securityClassificationBanner/>
</div>

<div region="south" class="banner">
    <omar:securityClassificationBanner/>
</div>

<div region="center" split="true">
    <div class="easyui-layout" fit="true">
        <div region="north" style="overflow:hidden;">
            <div class="easyui-panel" style="overflow:hidden;padding:5px;">
                <g:link class="easyui-linkbutton" plain="true" uri="/">Home</g:link>
            </div>
        </div>

        <!--        <div data-options="region:'center'" style="background:#eee;">
            <table id="diskCacheTableId" class="easyui-datagrid"  class="easyui-datagrid"
                   rownumbers="true" pagination="true" fit="true" fitColumns="true"
                   striped="true" url="${createLink( action: 'getData' )}"></table>

        </div>
        -->
        <div data-options="region:'center'" style="background:#eee;">
            <table id="diskCacheTableId" class="easyui-datagrid" class="easyui-datagrid"
                   rownumbers="true" toolbar="#toolbarId" pagination="true" url="${createLink( action: 'list' )}"
                   fit="true" fitColumns="true"
                   striped="true">
            </table>

        </div>

        <div id="toolbarId">
            <a id="newLocationId" href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-add"
               plain="true">New Location</a>
            <a id="editLocationId" href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-edit"
               plain="true">Edit Location</a>
            <a id="removeLocationId" href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-remove"
               plain="true">Remove Location</a>
        </div>

    </div>
</div>

<div id="diskCacheDlgId" class="easyui-dialog" closed="true" style="width:400px;height:280px;padding:10px 20px"
     closed="true" buttons="#diskCacheDlgButtonsId">
    <form id="diskCacheFormId" method="post" novalidate>
        <table>
            <tr>
                <td>
                    <label>Id:</label>
                </td>
                <td>
                    <input id="diskCacheRecordId" name="id" class="easyui-textbox" readonly/>
                </td>
            </tr>
            <tr>
                <td>
                    <label>Directory:</label>
                </td>
                <td>
                    <input id="directoryId" name="directory" class="easyui-textbox"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label>Directory Type:</label>
                </td>
                <td>
                    <select id="directoryTypeId" name="directoryType" class="easyui-combobox">
                        <option value="SUB_DIRECTORY">Sub Directory</option>
                        <option value="DEDICATED">Dedicated</option>
                    </select>
                </td>
            </tr>
            <tr>
                <td>
                    <label>Maximum Size:</label>
                </td>
                <td>
                    <input id="maxSizeId" name="maxSize" class="easyui-numberspinner" style="width:80px;"
                           data-options="min:20,editable:true"/>
                    <label>gigabytes</label>
                </td>
            </tr>
            <tr>
                <td>
                    <label>Expire Period:</label>
                </td>
                <td>
                    <select id="expirePeriodId" name="expirePeriod" class="easyui-combobox">
                        <option value="P24H">P24H (24 hours)</option>
                        <option value="P2D">P2D (2 days)</option>
                        <option value="P1W">P1W (1 week)</option>
                        <option value="P2W">P2W (2 weeks)</option>
                        <option value="P1M">P1M (1 month)</option>
                        <option value="P1Y">P1Y (1 year)</option>
                    </select>
                </td>
            </tr>
        </table>
    </form>
</div>

<div id="diskCacheDlgButtonsId">
    <a id="saveButtonId" href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok"
       style="width:90px">Save</a>
    <a id="cancelButtonId" href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel"
       style="width:90px">Cancel</a>
    -->
</div>

<asset:javascript src="diskCachePage.js"/>

<g:javascript>
    function init()
    {
        $( document.body ).show();
        var tModel = ${raw( ( tableModel as grails.converters.JSON ).toString() )};
        var params = {
            model: new OMAR.models.DiskCache(),
            tableModel: tModel,
            url: "${createLink( action: 'list' )}",
            crudUrls: {
                "remove": "${raw( createLink( action: 'remove' ) )}",
                "update": "${raw( createLink( action: 'update' ) )}",
                "create": "${raw( createLink( action: 'create' ) )}"
            }
        };
        var diskCachePage = OMAR.pages.DiskCachePage( jQuery, params );
        diskCachePage.render();
        $( "body" ).css( "visibility", "visible" );
    }
</g:javascript>

</body>
</html>
