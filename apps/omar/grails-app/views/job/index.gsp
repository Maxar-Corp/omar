<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <r:require modules = "job"/>
    <title>OMAR <g:meta name="app.version"/>: Job Page</title>
    <r:layoutResources/>
</head>
<body>
<div class="outer-center" id="JobPageId">
    <omar:securityClassificationBanner/>


    <table id="jobTableId" class="easyui-datagrid" rownumbers="true" pagination="true"
           striped="true" url="${createLink( action: 'getData' )}"></table>




</div>
<r:layoutResources/>

<script type="text/javascript">
    function init(){
        var tModel = ${tableModel as grails.converters.JSON};
        var params = {model:new OMAR.models.Job(),
                      tableModel:tModel};
        var jobPage = OMAR.pages.JobPage(jQuery, params);
        jobPage.render();
    }
</script>

</body>
</html>
