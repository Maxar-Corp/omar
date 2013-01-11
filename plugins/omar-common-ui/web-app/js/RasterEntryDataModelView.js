OMAR.models.RasterEntryDataModel = Backbone.Model.extend({
    idAttribute:"raster_data_set_id",
    defaults:{
        "raster_data_set_id":"",
        "title":"",
        "organization":"",
         "security_classification":"",
        azimuth_angle:"",
        "filename":"",
        "width":""
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

        if(response.features)
        {   var size = response.features.size();
            for(var idx=0;idx<size;++idx)
            {
                var feature = response.features[idx];
               // alert(feature.id + ",  " + feature.width + ", " +feature.filename)
                var model = new OMAR.models.RasterEntryDataModel(feature.properties)

          /*          {"raster_data_set_id":feature.properties.raster_data_set_id?:""
                        ,"title":feature.properties.title?feature.properties.title:""
                        ,"security_classification":feature.properties.security_classification
                        ,"filename":feature.properties.filename
                    ,"width":feature.properties.width});
            */
                result.push(model);
            }
        }
        return result;
    }
});

OMAR.views.RasterEntryDataModelView = Backbone.View.extend({
    url: '/omar/federation/serverList',
    el:"#DataTable",
    initialize:function(params){
        if(this.el){
            this.dataTable = $(this.el).dataTable({
                "aoColumns": [
                    { "sTitle": "ID",   "mDataProp": "raster_data_set_id" }
                    ,{ "sTitle": "IMAGE_ID",   "mDataProp": "title" }
                    ,{ "sTitle": "ORGANIZATION",   "mDataProp": "organization" }
                    ,{ "sTitle": "AZIMUTH",   "mDataProp": "azimuth_angle" }
                    ,{ "sTitle": "CLASS",   "mDataProp": "security_classification" }
                    ,{ "sTitle": "WIDTH",   "mDataProp": "width" }
                    ,{ "sTitle": "FILENAME",   "mDataProp": "filename" }
                ],
                //"sScrollY": "200px",
                "sScrollX": "100%",
                "bScrollCollapse": true,
                "bPaginate": true,
                "bProcessing": true,
                "bDeferRender": true,
                "bJQueryUI": false//,
                //"aoColumnDefs": [
                //    { "sWidth": "10%", "aTargets": [ -1 ] }
                //]

            });
        }
        this.model = new OMAR.models.RasterEntryDataCollection();
        this.wfsModel = new OMAR.models.Wfs({"resultType":"json"});
        this.wfsModel.bind("change", this.wfsUrlChanged, this);
        this.model.bind("reset", this.resetTable, this);
    },
    resizeView:function()
    {
        var wrapperHeight = $("#Results").height();
        var tableHeight   = $(this.el).height();
        var tabHeight     = ($(".inner-center").height() - wrapperHeight);
        var innerHeight = tableHeight;
        //alert(wrapperHeight + ", "+$(".inner-center").height() +","+tableHeight+","+tabHeight);
        if(innerHeight > wrapperHeight)
        {
            innerHeight = (wrapperHeight + (wrapperHeight - (innerHeight + tabHeight)) );
        }
        if(innerHeight < 200) innerHeight = 200;
        this.dataTable.fnSettings().oScroll.sY = innerHeight;
       this.dataTable.fnAdjustColumnSizing();
    },
    resetTable:function()
    {
        if(this.dataTable)
        {
            this.dataTable.fnClearTable();
            var idx = 0;
            for(idx = 0; idx < this.model.size();++idx)
            {
                var row = this.model.at(idx);
                this.dataTable.fnAddData(row.toJSON());
            }
            this.dataTable.fnAdjustColumnSizing();
        }
    },
    wfsUrlChanged :function(params){
        this.model.reset();
       this.wfsModel.attributes.maxFeatures = 10;
       this.wfsModel.attributes.offset = 0;
       this.model.url = this.wfsModel.toUrl().toString() + "&callback=?";
      // alert(this.model.url);
       this.model.fetch({dataType: "jsonp",
            update: false, remove: true,date:{cache:false}});
    },
    render:function(){
        if(this.dataTable)
        {
            this.dataTable.fnDraw();
        }
    }
});