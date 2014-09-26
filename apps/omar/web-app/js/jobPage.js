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
    },
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

    },

    refresh:function(){
        $('#jobTableId').datagrid('reload');
        setTimeout(OMAR.JobPage.refresh, 15000); // schedule next refresh after 15sec
    }, // reload grid

    render:function(){
        $('#jobTableId').datagrid({
		    title:'Job Status',
		    width:"100%",
		    //height:"",
		    remoteSort:true,
            pagination:true,
		    singleSelect:true,
		    nowrap:false,
		    fitColumns:true,
            total:800,
		    url:'/omar/job/list',
		    columns:[[
		        {field:'jobId',title:'JOB_ID',width:80,sortable:true},
		        {field:'jobType',title:'JOB_TYPE',width:100,sortable:true},
		        {field:'status',title:'STATUS',width:80,align:'right',sortable:true},
		        {field:'statusMessage',title:'STATUS_MESSAGE',width:80,align:'right',sortable:true},
		        {field:'percentComplete',title:'PERCENT_COMPLETE',width:50,sortable:true},
                {field:'submitDate',title:'SUBMIT_DATE',width:60,align:'center',sortable:true},
                {field:'startDate',title:'START_DATE',width:60,align:'center',sortable:true},
                {field:'endDate',title:'END_DATE',width:60,align:'center',sortable:true}
		    ]]//,

        //   view: default,
//		    detailFormatter: function(rowIndex, rowData){
//		        return '<table><tr>' +
//		                '<td rowspan=2 style="border:0"><img src="images/' + rowData.itemid + '.png" style="height:50px;"></td>' +
//		                '<td style="border:0">' +
//		                '<p>Attribute: ' + rowData.attr1 + '</p>' +
//		                '<p>Status: ' + rowData.status + '</p>' +
//		                '</td>' +
//		                '</tr></table>';
//		    }
		});
   /*     var pager = $('#jobTableId').datagrid('getPager');	// get the pager of datagrid
        pager.pagination({
            showPageList:true,
            buttons:[{
                iconCls:'icon-search',
                handler:function(){
                    alert('search');
                }
            },{
                iconCls:'icon-add',
                handler:function(){
                    alert('add');
                }
            },{
                iconCls:'icon-edit',
                handler:function(){
                    alert('edit');
                }
            }],
            onBeforeRefresh:function(){
                alert('before refresh');
                return true;
            }
     */

         //   $('#jobTableId').datagrid("getPager").pagination({
         //   layout:['list','sep','first','prev','sep',$('#p-style').val(),'sep','next','last','sep','refresh']
        //});

        setTimeout(this.refresh, 5000); // schedule next refresh after 15sec
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