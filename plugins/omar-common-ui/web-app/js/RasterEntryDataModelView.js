OMAR.models.RasterEntryDataModel = Backbone.Model.extend({
    idAttribute:"id",
    defaults:{
        id:""
    },
    initialize:function(params){
    }
});

OMAR.models.RasterEntryDataCollection=Backbone.Collection.extend({
    url:"",
    initialize:function(params){

    },
    parse:function(response){

        var result = new Array();

        var size = response.size();
        for(var idx=0;idx<size;++idx)
        {
            var model = new OMAR.models.RasterEntryDataModel(response[idx]);
            var tempM = this.get(model.id);

            // make sure we copy any existing user defined data or counts to
            // the copy of the model.
            //
            if(tempM)
            {
                model.userDefinedData = tempM.userDefinedData;
            }
            result.push(model);
        }
        return result;
    }
});

OMAR.views.RasterEntryDataModelView = Backbone.View.extend({
    url: '/omar/federation/serverList',
    el:"#DataTable",
    initialize:function(params){
        if(this.el){
            this.dataTable = $(this.el).dataTable();
        }
        this.model = new OMAR.models.RasterEntryDataCollection();
    },
    render:function(){
        if(this.dataTable)
        {
            this.dataTable.fnDraw();
        }
    }
});