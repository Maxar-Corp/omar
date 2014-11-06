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
        this.singleSelect=params.singleSelect;
        this.propertyGridId="#propertyGridId";
        this.jobTableId = "#jobTableId";
        this.usernameId = "#usernameId";
        this.usernameOpTypeId = "#usernameOpTypeId";
        this.applyFilterButtonId = "#applyFilterButtonId";
        this.resetButtonId = "#resetButtonId";
        this.usernameOpTypeId = "#usernameOpTypeId";
        this.jobStatusGroupCheckedList = "#jobStatusGroupId :checkbox:checked";
        this.jobStatusGroupCheckboxList = "#jobStatusGroupId :checkbox";
        this.removeJobId = "#removeId";
        this.donwnloadJobId = "#downloadId";
        this.reloadId = "#reloadId";
        this.cancelJobId = "#cancelJobId";
        this.urls=params.urls;
        $(this.applyFilterButtonId).click($.proxy(this.refresh, this));
        $(this.resetButtonId).click($.proxy(this.resetFilter, this));

        $(this.removeJobId).click($.proxy(this.removeJobClicked, this));
        $(this.donwnloadJobId).click($.proxy(this.downloadJobClicked, this));
        $(this.reloadId).click($.proxy(this.reload, this));
        $(this.cancelJobId).click($.proxy(this.cancelJobClicked, this));
        if(this.singleSelect == null) this.singleSelect = false;
        $.extend(true, this.tableModel,{
            url:params.url,
            idField:"jobId",
            singleSelect:thisPtr.singleSelect,
            loadFilter:function(param){
                if(param)
                {
                    thisPtr.resetTimerIfNeeded(param.rows);
                }
                else
                {
                    thisPtr.resetTimerIfNeeded();
                }
                return param;
             },
            frozenColumns: [[
                {field: 'ck', checkbox: true}
            ]]

        });
    },
    reload:function(){
        $(this.jobTableId).datagrid('clearSelections');
        $(this.jobTableId).datagrid('clearChecked');
        $(this.jobTableId).datagrid('reload');
    },
    downloadJobClicked:function(){
        var thisPtr = this;
        var rows = $(this.jobTableId).datagrid('getSelections');
        if(rows)
        {
            $(rows).each(function(idx, row){
                $.fileDownload(thisPtr.urls.download+"?jobId="+row.jobId)
                    .fail(function (message) { alert(JSON.stringify(message.responseText)); });
            })

        }
    },
    cancelJobClicked:function(){
        var thisPtr = this;
        var rows = $(this.jobTableId).datagrid('getSelections');
        if(rows)
        {
            $(rows).each(function(idx, row){
                $.post(thisPtr.urls.cancel+"?jobId="+row.jobId)
                    .fail(function (message) { alert(JSON.stringify(message.responseText)); });
            })
        }
    },
    removeSelectedRows:function()
    {
        var rows = $(this.jobTableId).datagrid('getSelections');
        var thisPtr = this;
        var nRows = rows.length;
        $(rows).each(function(idx,v){
            $.post(thisPtr.urls.remove,{id:v.id},function(result){
             })
                .complete(function(result){
                    $(thisPtr.jobTableId).datagrid('clearSelections');
                    thisPtr.refresh();
                })
        });
    },
    removeJobClicked:function()
    {
        var thisPtr = this;
        var rows = $(this.jobTableId).datagrid('getSelections');
        var canRemove =true
        var errorMessage = "";
        $(rows).each(function(idx,v){
            var testV = v.status.toUpperCase();
            if((testV == "RUNNING")||
                (testV == "READY"))
            {
                canRemove = false;
                errorMessage = "We can only remove jobs already processed!"
            }
            if(!canRemove) return;
        });
        if(canRemove)
        {
            var values = [];
            $.messager.confirm('Confirm','Are you sure you want to remove and unregister this location?',function(r){
                if(r)
                {

                    thisPtr.removeSelectedRows();
                }
            });
        }
        else
        {
            $.messager.alert('Warning',errorMessage);
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