OMAR.models.DiskCache = Backbone.Model.extend({
    urlRoot: "/omar/diskCache",
    defaults: {
        id:null,
        directory: null,
        directoryType: null,
        maxSize: null,
        currentSize:null,
        expirePeriod:null
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



OMAR.views.DiskCachePageView = Backbone.View.extend({
    el: "#diskCachePageId",
    initialize:function(params) {
        var thisPtr = this;
        this.baseUrl = params.baseUrl;
        this.tableModel=params.tableModel;
        this.diskCacheTableId = "#diskCacheTableId";
        this.newLocationId = "#newLocationId";
        this.editLocationId = "#editLocationId";
        this.removeLocationId = "#removeLocationId";
        this.diskCacheFormId = "#diskCacheFormId";
        this.diskCacheDlgId="#diskCacheDlgId";
        this.cancelButtonId="#cancelButtonId";
        this.saveButtonId = "#saveButtonId";
        this.crudUrls=params.crudUrls;
        $.extend(true, this.tableModel,{
            url:params.url,
            idField:"id",
            loadFilter:function(param){
                return param;
            }
        });
        $(this.newLocationId).click($.proxy(this.newLocationClicked, this));
        $(this.editLocationId).click($.proxy(this.editLocationClicked, this));
        $(this.removeLocationId).click($.proxy(this.removeLocationClicked, this));
        $(this.cancelButtonId).click($.proxy(this.cancelButtonClicked, this));
        $(this.saveButtonId).click($.proxy(this.saveButtonClicked, this));
    },
    cancelButtonClicked:function()
    {
        $(this.diskCacheDlgId).dialog('close');
    },
    saveButtonClicked:function() {
        var thisPtr = this;
        $(this.diskCacheFormId).form('submit',{
            onSubmit: function(){
                return $(this).form('validate');
            },
            success: function(result){
                var result = eval('('+result+')');
                if (!result.success){
                    $.messager.show({
                        title: 'Error',
                        msg: result.message
                    });
                } else {
                    $(thisPtr.diskCacheDlgId).dialog('close');        // close the dialog
                    $('#diskCacheTableId').datagrid('reload');    // reload the user data
                }
            }
        });
    },
    newLocationClicked:function(){
        $(this.diskCacheDlgId).show();
        $(this.diskCacheFormId).form('clear');
        $(this.diskCacheFormId).attr('action','create');
        $(this.diskCacheDlgId).dialog('open').dialog('setTitle','New Disk Cache Location');
    },
    editLocationClicked:function(){
        $(this.diskCacheFormId).attr('action','update');
        var thisPtr = this;
        var row = $('#diskCacheTableId').datagrid('getSelected');
       // alert(row);
        if (row){
            $(this.diskCacheFormId).form('clear');
            $(this.diskCacheFormId).form('load',row);//row);
            $(this.diskCacheDlgId).dialog('open').dialog('setTitle','Edit Disk Cache Location');
        }
        else
        {
            $.messager.alert("No Rows Selected", "Please select a row to edit.");
        }
    },
    removeLocationClicked:function(){
        var thisPtr = this;
        var row = $(this.diskCacheTableId).datagrid('getSelected');
        if (row){
            $.messager.confirm('Confirm','Are you sure you want to remove and unregister this location?',function(r){
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
        }
    },
    render:function(){
        var thisPtr = this;
        $(this.diskCacheTableId).datagrid(thisPtr.tableModel);
    }
});

OMAR.diskCachePage = null;
OMAR.pages.DiskCachePage = (function($, params){
    OMAR.diskCachePage = new OMAR.views.DiskCachePageView(params);
    return OMAR.diskCachePage;
});

$(document).ready(function () {
    $.ajaxSetup({ cache: false });
    init();
});