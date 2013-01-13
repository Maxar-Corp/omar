OMAR.models.RasterEntryDataModel = Backbone.Model.extend({
    idAttribute:"index_id",
    defaults:{
        "id":""
        ,"raster_data_set_id":""
        ,"entry_id":""
        ,"exclude_policy":""
        ,"width":""
        ,"height":""
        ,number_of_bands:""
        ,number_of_res_levels:""
        ,gsd_unit:""
        ,gsdx:""
        ,gsdy:""
        ,"bit_depth":""
        ,"data_type":""
        ,"tie_point_set":""
        ,"index_id":""
        ,"filename":""
        ,"image_id":""
        ,"target_id":""
        ,"product_id":""
        ,"sensor_id":""
        ,"mission_id":""
        ,"image_category":""
        ,"image_representation":""
        ,"azimuth_angle":""
        ,"grazing_angle":""
        ,"security_classification":""
        ,"security_code":""
        ,"title":""
        ,"isorce":""
        ,"organization":""
        ,"description":""
        ,"country_code":""
        ,"be_number":""
        ,"niirs":""
        ,"wac_code":""
        ,"sun_elevation":""
        ,"sun_azimuth" : ""
        ,"cloud_cover" : ""
        ,"style_id" : ""
        ,"keep_forever":""
        ,"ground_geom" :""
        ,"acquisition_date":""
        ,"valid_model":""
        ,"access_date":""
        ,"ingest_date":""
        ,"recieve_date":""
        ,"release_id":""
        ,"file_type":""
        ,"class_name":""
        ,other_tags_xml:""

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
                // alert(feature.id);
                var model = new OMAR.models.RasterEntryDataModel(feature.properties)
                model.set("ground_geom",JSON.stringify(feature.geometry));
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
                    ,{ "sTitle": "GRAZING",   "mDataProp": "grazing_angle" }
                    ,{ "sTitle": "CLASS",   "mDataProp": "security_classification" }
                    ,{ "sTitle": "GEOM",   "mDataProp": "ground_geom" }
                    ,{ "sTitle": "WIDTH",   "mDataProp": "width" }
                    ,{ "sTitle": "HEIGHT",   "mDataProp": "height" }
                    ,{ "sTitle": "ENTRY",   "mDataProp": "entry_id" }
                    ,{ "sTitle": "FILE",   "mDataProp": "filename" }
                ],
                "sScrollX": "100%",
                "bScrollCollapse": true,
                "bPaginate": true,
                "sPaginationType": "full_numbers",
                "bProcessing": true,
                "bDeferRender": true,
                "bJQueryUI": false,//,
                "bServerSide":true,
                "fnServerData": $.proxy(this.getServerData,this)
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
        var tabHeight     = ($(".inner-center").height() - wrapperHeight);
        var innerHeight =  wrapperHeight-(tabHeight*2);
        this.dataTable.fnSettings().oScroll.sY = innerHeight;
        this.dataTable.fnAdjustColumnSizing();
    },
    getServerData:function( sUrl, aoData, fnCallback, oSettings ) {
        var result = {
            "aaData":{},
            "iTotalRecords":0,
            "iTotalDisplayRecords":0
        }
        if(sUrl&&this.model)
        {
            var oColumn = oSettings.aoColumns[ oSettings.aaSorting[0][0] ];
            this.wfsModel.attributes.offset = oSettings._iDisplayStart;
            this.wfsModel.attributes.sort = "[['"+oColumn.mDataProp.toUpperCase()+"','"+oSettings.aaSorting[0][1].toUpperCase()+"']]";
            this.model.url = this.wfsModel.toUrl()+"&callback=?";
            //alert(this.model.url);
            var model = this.model;
            this.model.reset();
            //alert("sorting by " + oColumn.mDataProp + " "+oSettings.aaSorting[0][1]);
            model.fetch({dataType: "jsonp",
                update: false,
                remove: true,
                date:{cache:false},
                "success":function(){
                    result.aaData = model.toJSON();
                    if(model.size())
                    {
                        result.iTotalRecords = model.size();
                        result.iTotalDisplayRecords=100000;
                    }
                    fnCallback(result);
                }
            });
        }
        else
        {
            fnCallback(result);
        }
    },
    resetTable:function()
    {
        /*
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
         this.dataTable.fnReloadAjax();
         */
    },
    wfsUrlChanged :function(params){

        this.model.reset();
        this.wfsModel.attributes.maxFeatures = 10;
        this.wfsModel.attributes.offset = 0;
        //this.model.url = this.wfsModel.toUrl().toString() + "&callback=?";
        this.dataTable.fnReloadAjax(this.wfsModel.toUrl().toString() + "&callback=?");

        // alert(this.model.url);
        //this.model.fetch({dataType: "jsonp",
        //     update: false, remove: true,date:{cache:false}});
    },
    render:function(){
        if(this.dataTable)
        {
            this.dataTable.fnDraw();
        }
    }
});