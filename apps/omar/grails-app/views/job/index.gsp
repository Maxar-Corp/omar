<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ page import="grails.converters.JSON" contentType="text/html;charset=UTF-8" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<r:require modules = "job"/>
<title>OMAR <g:meta name="app.version"/>: Job Page</title>
<r:layoutResources/>
<style type="text/css">
.banner{
    overflow:hidden;
}
</style>
</head>
<body class="easyui-layout" id="JobPagId">

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

        <div data-options="region:'west'" collapsible="true" style="max-width:300px;">
            <table id="propertyGridId" class="easyui-propertygrid" title="Query Parameters"
                   showGroup="true" showHeader="false" scrollbarSize="0">


                <tr>
                    <td/>
                    <td>
                        Job Type:
                    </td>
                    <td>
                        <div id="jobStatusGroupId" class="jobStatusGroupClass">
                            <g:checkBox  id="readyCheckboxId"  name="status" checked="false" value="READY">READY</g:checkBox>
                            <label>READY</label><br/>
                            <g:checkBox  id="runningCheckboxId" name="status" checked="false" value="RUNNING">RUNNING</g:checkBox>
                            <label>RUNNING</label><br/>
                            <g:checkBox  id="finishedCheckboxId"  name="status" checked="false" value="FINISHED">FINISHED</g:checkBox>
                            <label>FINISHED</label><br/>
                            <g:checkBox  id="canceledCheckboxId" name="status" checked="false" value="CANCELED">CANCELED</g:checkBox>
                            <label>CANCELED</label><br/>
                            <g:checkBox  id="pausedCheckboxId" name="status" checked="false" value="PAUSED">PAUSED</g:checkBox>
                            <label>PAUSED</label><br/>
                            <g:checkBox  id="failedCheckboxId" name="status" checked="false" value="FAILED">FAILED</g:checkBox>
                            <label>FAILED</label><br/>
                        </div>
                    </td>
                </tr>
                <sec:ifAllGranted roles="ROLE_ADMIN">
                    <tr>
                        <td/>
                        <td>User name:</td>
                        <td>
                            <g:textField id="usernameId" name="username"/>      <br/>
                            <label>Comparator:</label>
                            <g:select id="usernameOpTypeId" name="opType" from="${['equals', 'contains', 'Starts With', 'Ends With']}"></g:select>
                        </td>
                    </tr>
                </sec:ifAllGranted>

            </table>
            <div align='center'>
                <button id="applyFilterButtonId">Apply Filter</button>
                <button id="resetButtonId">Reset</button>
            </div>

        </div>
        <div data-options="region:'center'" style="background:#eee;">
            <table id="jobTableId" class="easyui-datagrid"  class="easyui-datagrid"
                   rownumbers="true" toolbar="#toolbarId" pagination="true" fit="true" fitColumns="true"
                   striped="true" url="${createLink( action: 'getData' )}"></table>

            <!--        <table id="jobTableId" class="easyui-datagrid" class="easyui-datagrid"
               data-options="
				view:scrollview,rownumbers:true,singleSelect:true,url:'${createLink( action: 'getData' )}',
				autoRowHeight:false,pageSize:50" ></table>
    -->
        </div>
        <div id="toolbarId">
            <a id="downloadId" href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-save" plain="true">Download Job</a>
            <a id="removeId" href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-remove" plain="true">Remove Job</a>
            <a id="reloadId" href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-reload" plain="true">Reload</a>
        </div>
    </div>
</div>

</body>



</div>
<r:layoutResources/>

<script type="text/javascript">
    function init(){
        // alert("${createLink(controller:"Job", action: "ids")}");
        var tModel = ${tableModel as grails.converters.JSON};
        var params = {model:new OMAR.models.Job(),
            tableModel:tModel,
            url: "${createLink( action: 'getData' )}",
            urls:{"remove":"${createLink( controller:'job', action: 'remove' )}"
                ,"download":"${createLink( controller:'job', action: 'download' )}"
                ,"update":"${createLink( controller:'job', action: 'update' )}"
            },
            baseUrl:"${createLink( )}"
        };
        var jobPage = OMAR.pages.JobPage(jQuery, params);
        jobPage.render();
        $("body").css("visibility","visible");
    }
</script>

</body>
</html>
