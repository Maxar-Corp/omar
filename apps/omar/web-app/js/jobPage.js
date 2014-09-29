OMAR.models.Job = Backbone.Model.extend({
    urlRoot: "/omar/job",
    defaults: {
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

        // alert("Welcome to this world");
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
        this.tableModel = params.tableModel;
        var thisPtr=this;
        $.extend(true, this.tableModel,{
            loadFilter:function(param){
                var rowIdx = 0;
                var needTimeoutSet = false;
                for(rowIdx=0;rowIdx<param.rows.length;++rowIdx)
                {
                    if(param.rows[rowIdx].status == "RUNNING" ||
                       param.rows[rowIdx].status == "READY" )
                    {
                        needTimeoutSet = true;
                    }
                }
                if(needTimeoutSet)
                {
                    if(!thisPtr.timeOut)
                    {
                        thisPtr.timeOut = setInterval(thisPtr.refresh, 5000);
                    }
                }
                else if(thisPtr.timeOut)
                {
                    clearTimeout(thisPtr.timeOut);
                    thisPtr.timeOut = null;
                }

                 //alert(JSON.stringify(param.rows.length));
                return param;
             }
        })
    },

    refresh:function(){
        $('#jobTableId').datagrid('reload');
       // setTimeout(OMAR.JobPage.refresh, 15000); // schedule next refresh after 15sec
    }, // reload grid

    render:function(){
        $('#jobTableId').datagrid(this.tableModel);
      //  setTimeout(this.refresh, 5000); // schedule next refresh after 15sec
    }
});

OMAR.JobPage = null;
OMAR.pages.JobPage = (function($, params){
    OMAR.JobPage = new OMAR.views.JobPageView(params);
    return OMAR.JobPage;
});

$(document).ready(function () {
    //$.ajaxSetup({ cache: false });
    init();


});