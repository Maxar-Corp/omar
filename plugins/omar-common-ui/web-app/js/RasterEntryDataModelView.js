OMAR.models.RasterEntryDataModel = Backbone.Model.extend({
    idAttribute:"id",
    defaults:{
        "id":""
        ,"thumbnail":""
        ,"view":""
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
        ,"receive_date":""
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
                var model = new OMAR.models.RasterEntryDataModel(feature.properties)
                var modelId = model.id;
                var omarUrl = this.url.substr(0,this.url.indexOf("omar")+4);
                var rawUrl = "<a href='"+omarUrl+"/mapView/imageSpace?layers="+modelId+"'>Raw</a>";
                var orthoUrl = "<a href='"+omarUrl+"/mapView/index?layers="+modelId+"'>Ortho</a>";
                model.set({
                    "ground_geom":JSON.stringify(feature.geometry)
                    ,"thumbnail":"<img src='"+omarUrl+"/thumbnail/show/"+modelId+"?size=128'></img>"
                    ,"view":"<ul><li>"+rawUrl+"</li><li>"+orthoUrl+"</li></ul>"
                });
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
                "aoColumnDefs": [
                    { "aTargets":[0], "sTitle": "ID",   "mDataProp": "id" }
                    ,{ "aTargets":[1], "sTitle": "thumbnail", "mDataProp": "thumbnail","sWidth":"150", "bSearchable": false, "asSorting": [] }
                    ,{ "aTargets":[2], "sTitle": "View", "mDataProp": "view", "bSearchable": false, "asSorting": [] }
                    ,{ "aTargets":[3], "sTitle": "IID",   "mDataProp": "title" }
                    ,{ "aTargets":[4], "sTitle": "IID2",   "mDataProp": "image_id" }
                    ,{ "aTargets":[5], "sTitle": "NIIRS",   "mDataProp": "niirs" }
                    ,{ "aTargets":[6], "sTitle": "ORGANIZATION",   "mDataProp": "organization" }
                    ,{ "aTargets":[7], "sTitle": "AZIMUTH",   "mDataProp": "azimuth_angle" }
                    ,{ "aTargets":[8], "sTitle": "GRAZING",   "mDataProp": "grazing_angle" }
                    ,{ "aTargets":[9], "sTitle": "SECURITY CLASS",   "mDataProp": "security_classification" }
                    ,{ "aTargets":[10], "sTitle": "SECURITY_CODE",   "mDataProp": "security_code" }
                    ,{ "aTargets":[11], "sTitle": "GEOM",   "mDataProp": "ground_geom", "bSearchable": false, "asSorting": [] }
                    ,{ "aTargets":[12], "sTitle": "WIDTH",   "mDataProp": "width" }
                    ,{ "aTargets":[13], "sTitle": "HEIGHT",   "mDataProp": "height" }
                    ,{ "aTargets":[14], "sTitle": "BANDS",   "mDataProp": "number_of_bands" }
                    ,{ "aTargets":[15], "sTitle": "RLEVELS",   "mDataProp": "number_of_res_levels" }
                    ,{ "aTargets":[16], "sTitle": "GSD UNIT",   "mDataProp": "gsd_unit" }
                    ,{ "aTargets":[17], "sTitle": "GSD X",   "mDataProp": "gsdx" }
                    ,{ "aTargets":[18], "sTitle": "GSD Y",   "mDataProp": "gsdy" }
                    ,{ "aTargets":[19], "sTitle": "BIT DEPTH",   "mDataProp": "bit_depth" }
                    ,{ "aTargets":[20], "sTitle": "DATA TYPE",   "mDataProp": "data_type" }
                    ,{ "aTargets":[21], "sTitle": "INDEX_ID",   "mDataProp": "index_id" }
                    ,{ "aTargets":[22], "sTitle": "FILE",   "mDataProp": "filename" }
                    ,{ "aTargets":[23], "sTitle": "TARGET ID",   "mDataProp": "target_id" }
                    ,{ "aTargets":[24], "sTitle": "PRODUCT ID",   "mDataProp": "product_id" }
                    ,{ "aTargets":[25], "sTitle": "SENSOR ID",   "mDataProp": "sensor_id" }
                    ,{ "aTargets":[26], "sTitle": "MISSION",   "mDataProp": "mission_id" }
                    ,{ "aTargets":[27], "sTitle": "ICAT",   "mDataProp": "image_category" }
                    ,{ "aTargets":[28], "sTitle": "IREP",   "mDataProp": "image_representation" }
                    ,{ "aTargets":[29], "sTitle": "ISORCE",   "mDataProp": "isorce" }
                    ,{ "aTargets":[30], "sTitle": "DESCRIPTION",   "mDataProp": "description" }
                    ,{ "aTargets":[31], "sTitle": "COUNTRY",   "mDataProp": "country_code" }
                    ,{ "aTargets":[32], "sTitle": "BE",   "mDataProp": "be_number" }
                    ,{ "aTargets":[33], "sTitle": "WAC",   "mDataProp": "wac_code" }
                    ,{ "aTargets":[34], "sTitle": "SUN ELEVATION",   "mDataProp": "sun_elevation" }
                    ,{ "aTargets":[35], "sTitle": "SUN AZIMUTH",   "mDataProp": "sun_azimuth" }
                    ,{ "aTargets":[36], "sTitle": "CLOUD COVER",   "mDataProp": "cloud_cover" }
                    ,{ "aTargets":[37], "sTitle": "KEEP FOREVER",   "mDataProp": "keep_forever" }
                    ,{ "aTargets":[38], "sTitle": "VALID MODEL",   "mDataProp": "valid_model" }
                    ,{ "aTargets":[39], "sTitle": "ENTRY",   "mDataProp": "entry_id" }
                    ,{ "aTargets":[40], "sTitle": "ACQUISITION",   "mDataProp": "acquisition_date" }
                    ,{ "aTargets":[41], "sTitle": "ACCESS",   "mDataProp": "access_date" }
                    ,{ "aTargets":[42], "sTitle": "INGEST",   "mDataProp": "ingest_date" }
                    ,{ "aTargets":[43], "sTitle": "RECEIVE",   "mDataProp": "receive_date" }
                    ,{ "aTargets":[44], "sTitle": "RELEASE ID",   "mDataProp": "release_id" }
                    ,{ "aTargets":[45], "sTitle": "FILE TYPE",   "mDataProp": "file_type" }
                    ,{ "aTargets":[46], "sTitle": "CLASS NAME",   "mDataProp": "class_name" }
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
            });


        }
        this.model = new OMAR.models.RasterEntryDataCollection();
        this.wfsModel = new OMAR.models.Wfs({"resultType":"json"});
        this.wfsModel.dirty = true;
        this.wfsModel.countDirty = true;
        this.wfsModel.count = -1;
        this.wfsModel.bind("change", this.wfsUrlChanged, this);
        this.wfsModel.bind("onNumberOfFeaturesChange",
                           this.onNumberOfFeaturesChange, this);
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
        var wfsModel = this.wfsModel;
        if(sUrl&&this.model&&wfsModel)
        {
            result.iTotalRecords = wfsModel.get("numberOfFeatures");
            var searchable = oSettings.aoColumns[oSettings.aaSorting[0][0]].bSearchable;
            var oColumn = oSettings.aoColumns[ oSettings.aaSorting[0][0] ];
            var sort = searchable?"[['"+oColumn.mDataProp.toLowerCase()+"','"+oSettings.aaSorting[0][1].toUpperCase()+"']]":"";
            if((wfsModel.attributes.maxFeatures != oSettings._iDisplayLength)||
                (wfsModel.attributes.offset != oSettings._iDisplayStart)||
                (wfsModel.attributes.sort != sort)
                )
            {
                wfsModel.dirty = true;
            }
            wfsModel.attributes.maxFeatures = oSettings._iDisplayLength
            wfsModel.attributes.offset = oSettings._iDisplayStart;
            wfsModel.attributes.sort = sort;
            var model = this.model;
            model.url = this.wfsModel.toUrl()+"&callback=?";
            //alert(this.model.url);
            //alert("sorting by " + oColumn.mDataProp + " "+oSettings.aaSorting[0][1]);
            if(wfsModel.dirty&&searchable)
            {
                this.model.reset();
                var thisPtr = this;
                model.fetch({dataType: "jsonp",
                    update: false,
                    remove: true,
                    date:{cache:false},
                    "success":function(){
                        wfsModel.dirty = false;
                        result.aaData = model.toJSON();
                        result.iTotalRecords =   wfsModel.get("numberOfFeatures");
                        result.iTotalDisplayRecords =   wfsModel.get("numberOfFeatures");
                       // if(model.size())
                       // {
                       //     result.iTotalRecords = model.size();
                       // }
                        fnCallback(result);
                        if((wfsModel.get("numberOfFeatures") < 1)&&(model.size()>0))
                        {
                            result.iTotalRecords =        model.size();
                            result.iTotalDisplayRecords = model.size();
                            wfsModel.fetchCount();
                        }
                        thisPtr.dataTable.fnAdjustColumnSizing();
                     }
                });
            }
            else
            {
                if(model.size())
                {
                    result.iTotalRecords =   wfsModel.get("numberOfFeatures");
                    result.iTotalDisplayRecords =   wfsModel.get("numberOfFeatures");
                    result.aaData = model.toJSON();
                }
                fnCallback(result);
                //this.dataTable.fnAdjustColumnSizing();
            }
        }
        else
        {
            fnCallback(result);
        }
 //       setTimeout( function () {
//            dataTable.fnAdjustColumnSizing();
//        }, 10 );

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
        this.wfsModel.dirty = true;
        this.wfsModel.attributes.numberOfFeatures = 0;
        //this.model.url = this.wfsModel.toUrl().toString() + "&callback=?";
        this.dataTable.fnReloadAjax(this.wfsModel.toUrl().toString() + "&callback=?");

        //this.model.fetch({dataType: "jsonp",
        //     update: false, remove: true,date:{cache:false}});
    },
    onNumberOfFeaturesChange:function(){
        this.dataTable.fnReloadAjax(this.wfsModel.toUrl().toString() + "&callback=?");
    },
    render:function(){
        if(this.dataTable)
        {
            this.dataTable.fnDraw();
        }
    }
});


jQuery.fn.dataTableExt.aTypes.push(
    function ( sData ) {
        return 'html';
    }
);

