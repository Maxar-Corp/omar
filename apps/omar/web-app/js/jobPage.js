OMAR.models.Job = Backbone.Model.extend({
    urlRoot: "/omar/job",
    idAttribute:"jobId",
    defaults: {
        id:null,
    	jobId: null,
  		jobType: null,
        status: null,
        statusMessage: null,
        data: null,
		percentComplete: null,
        submitDate: null,
        startDate: null,
        endDate: null
    },
    initialize: function(){

    }
    /*
    url:function() {
        result = this.urlRoot + "?";
        params = "";
        idx = 0;

        for (param in this.attributes) {
            if (params != "") {
                params += "&";
            }
            params += param + "=" + this.attributes[param];
        }

        return result + params;
    }
    */
});

OMAR.views.JobPageView = Backbone.View.extend({
    el:"#JobPageId",
    initialize:function(params){
        var thisPtr = this;
        this.timeoutId = null;
        this.timeoutInterval = 5000; // refresh every 5 seconds
        this.tableModel = params.tableModel;
        this.filter="";
        this.propertyGridId="#propertyGridId"
        this.jobTableId = "#jobTableId";
        this.usernameId = "#usernameId";
        this.usernameOpTypeId = "#usernameOpTypeId";
        this.applyFilterButtonId = "#applyFilterButtonId";
        this.resetButtonId = "#resetButtonId";
        this.usernameOpTypeId = "#usernameOpTypeId";
        this.jobStatusGroupCheckedList = "#jobStatusGroupId :checkbox:checked";
        this.jobStatusGroupCheckboxList = "#jobStatusGroupId :checkbox";
        this.removeJobId = "#removeJobId";
        this.crudUrls=params.crudUrls;
        $(this.applyFilterButtonId).click($.proxy(this.refresh, this));
        $(this.resetButtonId).click($.proxy(this.resetFilter, this));

        $(this.removeJobId).click($.proxy(this.removeJobClicked, this));

        $.extend(true, this.tableModel,{
            url:params.url,
            idField:"jobId",
            singleSelect:false,
            loadFilter:function(param){
             //   alert(JSON.stringify(param));
                if(param)
                {
                    thisPtr.resetTimerIfNeeded(param.rows);
                }
                else
                {
                    thisPtr.resetTimerIfNeeded();
                }
                return param;
             }
        });
    },
    removeJobClicked:function()
    {
        var thisPtr = this;
        var row = $(this.jobTableId).datagrid('getSelections');
        if (row){
  /*          $.messager.confirm('Confirm','Are you sure you want to remove and unregister this location?',function(r){
                if(r)
                {
                    $.post(thisPtr.crudUrls.remove,{id:row.id},function(result){
                        if (result.success){
                            $('#diskCacheTableId').datagrid('reload');    // reload the user data
                        } else {
                            $.messager.show({    // show error message
                                title: 'Error',
                                msg: result.message
                            });
                        }
                    },'json');
                }
            });
*/
        }

    },
    resetFilter:function(){
        $(this.jobStatusGroupCheckedList).each(function() {
            $(this).attr('checked', false);
        });
        $(this.usernameId).val("");
        this.refresh();
    },
    buildFilter:function(){
        var filter = "";

        var jobStatusFilter = "";
        $(this.jobStatusGroupCheckedList).each(function() {
            var temp = "("+$(this).attr('name') + "='"+$(this).attr('value')+"')";
            if(jobStatusFilter === "")
            {
                jobStatusFilter=temp;
            }
            else
            {
                jobStatusFilter = jobStatusFilter + " OR "+temp;
            }
        });

        if(jobStatusFilter!="")
        {
            filter =  "("+jobStatusFilter+")";
        }


        var usernameFilter = "";
        var usernameValue  =  $(this.usernameId).val();
        if((usernameValue != null) &&(usernameValue != ""))
        {
            var usernameOpType = "" + $(this.usernameOpTypeId).val();
            usernameOpType = usernameOpType.toLowerCase();
            if(usernameOpType!="")
            {
                usernameFilter = "(username";
                switch(usernameOpType)
                {
                    case "equals":
                        usernameFilter+="='"+usernameValue+"')"
                        break;
                    case "starts with":
                        usernameFilter+=" like '"+usernameValue+"%')"
                        break;
                    case "ends with":
                        usernameFilter+=" like '%"+usernameValue+"')"
                        break;
                    case "contains":
                        usernameFilter+=" like '%"+usernameValue+"%')"
                        break;
                }
                if(filter === "")
                {
                    filter=usernameFilter;
                }
                else
                {
                    filter += " AND "+usernameFilter;
                }
            }
         }

        return filter;
    },
    needToAutoUpdate:function(paramOverride){
        var result = false;
        var rows = null;

        if(paramOverride != null)
        {
            rows = paramOverride;
        }
        else
        {
            rows = $(this.jobTableId).datagrid("getRows");
        }

        $(rows).each(function(idx, v) {
            if(v.status === "RUNNING" || v.status==="READY")
            {
                result = true;
                return false; // break the for each loop
            }
        });
        return result;
     },
    clearTimeout:function(){
        if(this.timeoutId)
        {
            clearTimeout(this.timeoutId);
            this.timeoutId = null;
        }
    },
    resetTimerIfNeeded:function(paramOverride){
        if(this.needToAutoUpdate(paramOverride))
        {
            this.clearTimeout();
            this.timeoutId = setTimeout($.proxy(this.refreshWatchList, this), this.timeoutInterval);
        }
    },
    refreshWatchList:function(){
        var thisPtr = this;
        var rows = $(this.jobTableId).datagrid("getRows");
        var queryList = [];
        $(rows).each(function(idx, v){
            var status = v.status.toUpperCase();
            if(status === "RUNNING" ||
               status === "READY" )
            {
                queryList.push(v.jobId);
            }
        });
        if(queryList.length > 0)
        {
            this.currentGetQuery = $.ajax({
                url: "/omar/job/ids?jobIds="+queryList.join(","),
                type: "GET",
                context: this,
                success:function(response)
                {
                    $(response.rows).each(function(idx, v){
                        var idx = $(thisPtr.jobTableId).datagrid("getRowIndex", v.jobId);
                        $(thisPtr.jobTableId).datagrid("updateRow",{
                            index:idx,
                            row:v});
                    });
                    thisPtr.currentGetQuery = null;
                    thisPtr.resetTimerIfNeeded();
                },
                error:function(){
                }
            });

        }
    },
    refresh:function(){
        var filter = this.buildFilter();

        $('#jobTableId').datagrid('reload', {"filter":filter});
    },
    render:function(){
        var thisPtr = this;
        $(this.jobTableId).datagrid(this.tableModel);
        $(this.propertyGridId).propertygrid();
        $(this.jobStatusGroupCheckboxList).click($.proxy(this.refresh, this));
        $(this.usernameId).change($.proxy(this.refresh, this));
        $(this.usernameOpTypeId).change($.proxy(this.refresh, this));
    }
});

OMAR.jobPage = null;
OMAR.pages.JobPage = (function($, params){
    OMAR.jobPage = new OMAR.views.JobPageView(params);
    return OMAR.jobPage;
});

$(document).ready(function () {
    $.ajaxSetup({ cache: false });
    if(!OMAR.jobPage)
    {
        init();
    }
});