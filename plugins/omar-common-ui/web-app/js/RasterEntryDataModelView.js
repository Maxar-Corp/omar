OMAR.models.RasterEntryDataModel = Backbone.Model.extend({
    idAttribute:"id",
    defaults:{
        "selected":"false"
        ,"id":""
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
        ,"other_tags_xml":""
        ,"links":""
        ,"min_lat_lon":""
        ,"max_lat_lon":""
        ,"center_lat_lon":""

    },
    getBbox:function(){

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
                var omarUrlRaw = omarUrl+"/mapView/imageSpace?layers="+modelId;
                var omarUrlOrtho = omarUrl+"/mapView/index?layers="+modelId;
                //var omarUrlRawButton ="<button onclick=\"javascript:window.open(\'"+omarUrlRaw+"\')\">Raw</button>";
                var omarUrlRawButton ="<button onclick=\"javascript:window.open(\'"+omarUrlRaw+"\')\">Raw</button>";
                var omarUrlOrthoButton ="<button onclick=\"javascript:window.open(\'"+omarUrlOrtho+"\')\">Ortho</button>";

                var chipWidth=model.get("width");
                var chipHeight = model.get("height");
                if(chipWidth > chipHeight)
                {
                    if(chipWidth > 1024)
                    {
                        var ratio = 1024.0/chipWidth;
                        chipWidth = 1024;
                        chipHeight=Math.round(chipHeight*ratio);
                    }
                }
                else
                {
                    if(chipHeight > 1024)
                    {
                        var ratio = 1024.0/chipHeight;
                        chipHeight = 1024;
                        chipWidth=Math.round(chipWidth*ratio);
                    }
                }
                var bboxModel = new OMAR.models.BBOX();
                bboxModel.setFromWfsFeatureGeom(feature.geometry);
                var bbox = bboxModel.toWmsString();
                var omarUrlCapabilities = omarUrl + '/ogc/wms?request=GetCapabilities&layers=' + model.id;
                var omarUrlCapabilitiesLink ="<li><label class='link_cursor' onclick='javascript:window.open(\""+omarUrlCapabilities+"\");'>WMS Capabilities</label></li>"
                var omarUrlGetMap = omarUrl + '/ogc/wms?request=GetMap&layers=' + model.id+"&bbox="+bbox+"&srs=EPSG:4326&width="+chipWidth+"&height="+chipHeight+"&format=image/jpeg";
                var omarUrlGetMapLink ="<li><label class='link_cursor' onclick='javascript:window.open(\""+omarUrlGetMap+"\");'>WMS GetMap</label></li>"
                var omarUrlGetKML = omarUrl + '/ogc/wms?request=GetKML&layers=' + model.id+"&format=image/png&transparent=true";

                if(model.get("bit_depth") > 8)
                {
                    // auto stretch
                    omarUrlGetKML+="&stretch_mode=linear_auto_min_max&stretch_mode_region=viewport&bands=default"
                }
                //http://localhost/omar/superOverlay/createKml/d8add0161e20d289909647c0b020216998a4323c8bab9d067a9206ec1d4f99fc?stretch_mode=linear_auto_min_max&stretch_mode_region=global

                var omarUrlGetKMLLink ="<li><label class='link_cursor' onclick='javascript:window.open(\""+omarUrlGetKML+"\");'>KML</label></li>"

                var omarUrlSuperOverlay = omarUrl + "/superOverlay/createKml/" + model.id+"?"
                if(model.bit_depth > 8)
                {
                    omarUrlSuperOverlay += "stretch_mode=linear_auto_min_max&";
                    if(model.number_of_bands > 8)
                    {
                        omarUrlSuperOverlay += "stretch_mode_region=viewport&bands=default";
                    }
                    else
                    {
                        omarUrlSuperOverlay += "stretch_mode_region=global";
                    }
                }

                var omarUrlSuperOverlayLink ="<li><label class='link_cursor' onclick='javascript:window.open(\""+omarUrlSuperOverlay+"\");'>Super Overlay</label></li>"
                var centerPoint = bboxModel.getCenter();
                model.set({
                    min_lat_lon:bboxModel.get("miny")+","+bboxModel.get("minx")
                    ,max_lat_lon:bboxModel.get("maxy")+","+bboxModel.get("maxx")
                    ,center_lat_lon:centerPoint.y+","+centerPoint.x
                    ,"ground_geom":JSON.stringify(feature.geometry)
                    ,"thumbnail":"<img src='"+omarUrl+"/thumbnail/show/"+modelId+"?size=128'></img>"
                    ,"view": "<ul>"+omarUrlRawButton + omarUrlOrthoButton+"</ul>"
                    ,"links": "<ul>"+omarUrlCapabilitiesLink+omarUrlGetMapLink+omarUrlGetKMLLink+omarUrlSuperOverlayLink+"</ul>"
                    /*"<ul><button onclick=\"javascript:window.open(\'"+omarUrlRaw +
                     +"\')>Raw</button><button>Ortho</button></ul>"  */
                });
                result.push(model);
            }
        }
        return result;
    }
});
OMAR.models.RasterEntryColumnDef=Backbone.Model.extend({
    defaults:{
        aTargets:[],
        sTitle:"",
        sType:"string",
        sClass:null,
        mDataProp:"",
        bSearchable:true,
        asSorting:["asc","desc"],
        sWidth:"50px",
        sSortDataType:null,
        sName:"",
        bVisible:true
    },
    initialize:function(params){
        if(!this.get("sClass"))
        {
            this.set("sClass", this.get("mDataProp"));
        }
    }
});

OMAR.models.RasterEntryColumnGroup=Backbone.Model.extend({
    defaults:{
        name:""
        ,id:""
        ,mDataProperties:[]
        ,comparator:"in"
    },
    initialize:function(params){

    }
});

OMAR.models.RasterEntryColumnGroups=Backbone.Collection.extend({
    model:OMAR.models.RasterEntryColumnGroup,
    idAttribute:"id",
    initialize:function(params){
        this.add([
            {name:"All",
                id:"RasterEntryAllGroupId",
                mDataProperties:[],
                comparator:"out",
                selected:true
            },
            {name:"Image",
                id:"RasterEntryImageGroupId",
                mDataProperties:["thumbnail","id","view","entry_id","width"
                    ,"height","number_of_bands","number_of_res_levels"
                    ,"bit_depth", "gsdx", "gsdy", "min_lat_lon"
                    , "max_lat_lon", "center_lat_lon"
                ],
                selected:false
            }
            ,{name:"Metadata",
                id:"RasterEntryMetadataGroupId",
                mDataProperties:[
                    "thumbnail","id", ,"view", "acquisition_date","file_type"
                    ,"class_name","mission_id","country_code","target_id"
                    ,"be_number","sensor_id","title"
                ],
                selected:false
            }
            ,{name:"File",
                id:"RasterEntryFileGroupId",
                mDataProperties:["thumbnail","id","view","filename"],
                selected:false
            }
            ,{name:"Links",
                id:"RasterEntryLinksGroupId",
                mDataProperties:["thumbnail","id","view", "links"],
                selected:false
            }
        ]);
    },
    clearSelection:function(){
       this.each(function(obj){
            obj.set("selected",false);
        });
    }
});

OMAR.views.RasterEntryColumnGroupsView = Backbone.View.extend({
    el:"div.groupViewSelection",
    initialize:function(params){
        if(!this.model){
            this.model=new OMAR.models.RasterEntryColumnGroups();
        }
    },
    toHtml:function(){
        var result = "";
        this.model.each(function(obj){
            var radio = '<input type="radio" id="';
              radio += (obj.get("id")+'" name="RasterEntryColumnGroupsView" ');
            if(obj.get("selected")){
              radio+=  ('checked="'+ obj.get("selected")+'">'+obj.get("name") +'</input>');
            }
            else
            {
                radio += ('>'+obj.get("name") +'</input>');
            }
            result += radio;

        });
        return result;
    },

    render:function(){

        $(this.el).html(this.toHtml());
        var thisPtr = this;

        // setup click listeners
        this.model.each(function(obj){
            var selector = "#"+obj.get("id");
            $(selector).click(function(){
                thisPtr.model.clearSelection();
                obj.set("selected", true);
                thisPtr.trigger("groupClicked", obj);
            });
        });
    }
});

OMAR.models.RasterEntryColumnDefs=Backbone.Collection.extend({
    model:OMAR.models.RasterEntryColumnDef
    ,idAttribute:"mDataProp"
    ,initialize:function(params)
    {
        if(this.size()<1){
            this.add([
                { "aTargets":[],
                    "sTitle": "ID","sClass":"id", "sType":"string", "sName":"id", "mDataProp": "id", bSearchable:false}
                ,{ "aTargets":[], "sTitle": "THUMBNAIL", "sClass":"thumbnail", sType:"html", "mDataProp": "thumbnail", "bSearchable": false, "asSorting": [] }
                ,{ "aTargets":[], "sTitle": "VIEW", "sClass":"view", sType:"html", "mDataProp": "view", "bSearchable": false, "asSorting": [] }
                ,{ "aTargets":[], "sTitle": "ACQUISITION DATE", "sType":"date", "sName":"acquisition_date", "mDataProp": "acquisition_date" }
                ,{ "aTargets":[], "sTitle": "MISSION",  "sName":"mission_id", "mDataProp": "mission_id" }
                ,{ "aTargets":[], "sTitle": "COUNTRY",  "sName":"country_code", "mDataProp": "country_code" }
                ,{ "aTargets":[], "sTitle": "TARGET ID",  "sName":"target_id", "mDataProp": "target_id" }
                ,{ "aTargets":[], "sTitle": "BE",  "sName":"be_number", "mDataProp": "be_number" }
                ,{ "aTargets":[], "sTitle": "SENSOR ID",  "sName":"sensor_id", "mDataProp": "sensor_id" }
                ,{ "aTargets":[], "sTitle": "IID",  "sClass":"title", "sType":"string","sName":"title", "mDataProp": "title" }
                ,{ "aTargets":[], "sTitle": "IID2", "sClass":"image_id",  "sType":"string","sName":"image_id", "mDataProp": "image_id" }
                ,{ "aTargets":[], "sTitle": "PRODUCT ID", "sName":"product_id",  "mDataProp": "product_id" }
                ,{ "aTargets":[], "sTitle": "NIIRS",  "sClass":"niirs", "sType":"numeric", "sName":"niirs", "mDataProp": "niirs" }
                ,{ "aTargets":[], "sTitle": "ORGANIZATION",  "sClass":"organization", "sType":"string","sName":"organization","mDataProp": "organization" }
                ,{ "aTargets":[], "sTitle": "AZIMUTH","sType":"numeric", "sClass":"azimuth_angle", "sName":"azimuth_angle", "mDataProp": "azimuth_angle" }
                ,{ "aTargets":[], "sTitle": "GRAZING","sType":"numeric",  "sName":"grazing_angle", "mDataProp": "grazing_angle" }
                ,{ "aTargets":[], "sTitle": "SECURITY CLASS",  "sName":"security_classification", "mDataProp": "security_classification" }
                ,{ "aTargets":[], "sTitle": "SECURITY_CODE",  "sName":"security_code", "mDataProp": "security_code" }
                ,{ "aTargets":[], "sTitle": "GEOM",  "sName":"ground_geom", "mDataProp": "ground_geom", "bSearchable": false, "asSorting": [] }
                ,{ "aTargets":[], "sTitle": "WIDTH",  "sName":"width", "sType":"numeric", "mDataProp": "width" }
                ,{ "aTargets":[], "sTitle": "HEIGHT", "sName":"height", "sType":"numeric", "mDataProp": "height" }
                ,{ "aTargets":[], "sTitle": "BANDS",  "sName":"number_of_bands", "sType":"numeric", "mDataProp": "number_of_bands" }
                ,{ "aTargets":[], "sTitle": "RLEVELS", "sName":"number_of_res_levels", "sType":"numeric", "mDataProp": "number_of_res_levels" }
                ,{ "aTargets":[], "sTitle": "GSD UNIT",  "sName":"gsd_unit", "mDataProp": "gsd_unit" }
                ,{ "aTargets":[], "sTitle": "GSD X", "sType":"numeric", "sName":"gsdx", "mDataProp": "gsdx" }
                ,{ "aTargets":[], "sTitle": "GSD Y", "sType":"numeric", "sName":"gsdy", "mDataProp": "gsdy" }
                ,{ "aTargets":[], "sTitle": "BIT DEPTH", "sType":"numeric", "sName":"bit_depth", "mDataProp": "bit_depth" }
                ,{ "aTargets":[], "sTitle": "DATA TYPE",  "sName":"data_type", "mDataProp": "data_type" }
                ,{ "aTargets":[], "sTitle": "INDEX_ID",  "sName":"index_id", "mDataProp": "index_id" }
                ,{ "aTargets":[], "sTitle": "FILE",  "sName":"filename", "mDataProp": "filename" }
                ,{ "aTargets":[], "sTitle": "ICAT",  "sName":"image_category", "mDataProp": "image_category" }
                ,{ "aTargets":[], "sTitle": "IREP",  "sName":"image_representation", "mDataProp": "image_representation" }
                ,{ "aTargets":[], "sTitle": "ISORCE", "sName":"isorce",  "mDataProp": "isorce" }
                ,{ "aTargets":[], "sTitle": "DESCRIPTION", "sName":"description",  "mDataProp": "description" }
                ,{ "aTargets":[], "sTitle": "WAC",  "sName":"wac_code", "mDataProp": "wac_code" }
                ,{ "aTargets":[], "sTitle": "SUN ELEVATION", "sType":"numeric", "sName":"sun_elevation", "mDataProp": "sun_elevation" }
                ,{ "aTargets":[], "sTitle": "SUN AZIMUTH", "sType":"numeric", "sName":"sun_azimuth", "mDataProp": "sun_azimuth" }
                ,{ "aTargets":[], "sTitle": "CLOUD COVER",  "sName":"cloud_cover", "mDataProp": "cloud_cover" }
                ,{ "aTargets":[], "sTitle": "KEEP FOREVER",  "sName":"keep_forever", "mDataProp": "keep_forever" }
                ,{ "aTargets":[], "sTitle": "VALID MODEL",  "sName":"valid_model", "mDataProp": "valid_model" }
                ,{ "aTargets":[], "sTitle": "ENTRY", "sType":"numeric", "sName":"entry_id", "mDataProp": "entry_id" }
                ,{ "aTargets":[], "sTitle": "ACCESS", "sType":"date", "sName":"access_date", "mDataProp": "access_date" }
                ,{ "aTargets":[], "sTitle": "INGEST", "sType":"date", "sName":"ingest_date", "mDataProp": "ingest_date" }
                ,{ "aTargets":[], "sTitle": "RECEIVE",  "sType":"date", "sName":"receive_date", "mDataProp": "receive_date" }
                ,{ "aTargets":[], "sTitle": "RELEASE ID",  "sName":"release_id", "mDataProp": "release_id" }
                ,{ "aTargets":[], "sTitle": "FILE TYPE",  "sName":"file_type", "mDataProp": "file_type" }
                ,{ "aTargets":[], "sTitle": "CLASS NAME",  "sName":"class_name", "mDataProp": "class_name" }
                ,{ "aTargets":[], "sTitle": "LINKS",  "sType":"html","mDataProp": "links","asSorting": [] }
                ,{ "aTargets":[], "sTitle": "MIN LAT LON",  "sType":"string","mDataProp": "min_lat_lon","asSorting": [] }
                ,{ "aTargets":[], "sTitle": "MAX LAT LON",  "sType":"string","mDataProp": "max_lat_lon","asSorting": [] }
                ,{ "aTargets":[], "sTitle": "CENTER LAT LON",  "sType":"string","mDataProp": "center_lat_lon","asSorting": [] }
            ]);

        }
        // add loop to verify aTarget and default to index

        var thisPtr = this;
        this.forEach(function(obj,idx){

            var obj2 = thisPtr.at(idx);
            var aTargets = obj2.get("aTargets");
            if(aTargets){
                if(aTargets.size() < 1)
                {
                    obj2.set("aTargets", [idx]);
                }
            }
            else
            {
                obj2.set("aTargets", [idx]);
            }
       });
    }
});

OMAR.views.RasterEntryDataModelView = Backbone.View.extend({
    url: '/omar/federation/serverList',
    el:"#ResultsView",
    initialize:function(params){
        this.dataTableEl = $(this.el).find("#DataTable")[0];
        if(params&&params.columnDefs)
        {
            this.columnDefs = params.columnDefs;
        }
        else
        {
            this.columnDefs = new OMAR.models.RasterEntryColumnDefs();
        }
        if(params&&params.wfsModel)
        {
            this.wfsModel = params.wfsModel;
        }
        else
        {
            this.wfsModel = new OMAR.models.Wfs({"resultType":"json"});
        }
        this.wfsModel.dirty = true;
        this.wfsModel.countDirty = true;
        this.wfsModel.count = -1;
        this.wfsModel.bind("change", this.wfsUrlChanged, this);
        this.wfsModel.bind("onNumberOfFeaturesChange",
            this.onNumberOfFeaturesChange, this);
        if(params&&params.model)
        {
            this.model = params.model;
        }
        else
        {
            this.model = new OMAR.models.RasterEntryDataCollection();
        }
        this.model.bind("reset", this.resetTable, this);

        // now setup table
        if(this.dataTableEl){
            this.reinitializeTable();
        }
        //$("div.toolbar").html('<input type="radio" id="ImageViewSelectionId" name="viewselection">Image</input><input type="radio" id="MetadataViewSelectionId" name="viewselection">Metadata</input><input type="radio" name="FileViewSelectionId">File</input><input type="radio" id="LinksViewSelectionId" name="viewselection">Links</input>');
    },
    reinitializeTable:function(){
        if(this.dataTable)
        {
            this.dataTable.fnDestroy();
            this.dataTable = null;
        }
        else
        {
            this.groupedViews = new OMAR.views.RasterEntryColumnGroupsView({el:"div.groupViewSelection"});
            this.groupedViews.bind("groupClicked", this.groupedViewClicked, this);
        }
        //var omarUrl = this.wfsModel.get("url");
        //alert(omarUrl);
                           // alert(JSON.stringify(this.columnDefs));

        this.columnDefs.at(0).set("mRender",$.proxy(this.renderColumn,this,0));// this.renderColumn.bind(this));

            //function ( data, type, full ) {
            //if(data!=null)
            //    return "<a href='"+ omarUrl+"'>"+data +"</a";//moment(oObj.aData[1], "YYYY-MM-DD HH:mm:ss").format("DD/MM/YYYY hh:mm:ss a");
            //else
            //    return null;
        //});
        this.dataTable = $(this.dataTableEl).dataTable({
            "aoColumns": this.columnDefs.toJSON(),
            "sDom": '<"top"l><"groupViewSelection">frtip', //'<"top"flp>rt<"bottom"i><"clear">',
            "sScrollX": "100%",
            "sScrollY": "100%",
            "bScrollCollapse": true,
            "bPaginate": true,
            "sPaginationType": "full_numbers",
            "bProcessing": true,
            "bAutoWidth" : true,
            "bDeferRender": true,
            "bJQueryUI": false,//,
            "bServerSide": true,
            'bFilter': false,
            "aLengthMenu": [1,10,25,50,100],
            "fnServerData": $.proxy(this.getServerData,this)
        });

        $('.sorting_disabled').unbind('click');
        this.dataTable.fnSort( [ [0,'asc'] ] );

        this.groupedViews.setElement($("div.groupViewSelection"));
        this.groupedViews.render();

        this.resizeView();

    },
    renderColumn:function(column,data, type, full){
        //alert(JSON.stringify(column));
        var url = this.wfsModel.toUrl();
        var omarUrl = url.substr(0,url.indexOf("omar")+4);
        var urlLink = omarUrl + "/rasterEntry/show/" +data;
      //  return "<label class='link_cursor' onclick='window.open(\'"+urlLink+"\')>" + data + "</label>";
        return "<label class='link_cursor' onclick='javascript:window.open(\""+urlLink+"\");'>" + data + "</label>";
    },
    resetTable:function(){
    },
    groupedViewClicked:function(model){
       // $("td.thumbnail").hide();
       // $("th.thumbnail").hide();

        var thisPtr = this;
        var propertiesList = model.get("mDataProperties");
        var comparator = model.get("comparator");
        var modified = false;
        var oSettings = this.dataTable.fnSettings();
        thisPtr.columnDefs.each(function(columnObj){
            var targets = columnObj.get("aTargets");
            found = $.inArray(columnObj.get("mDataProp"), propertiesList) > -1;
            if(comparator == "out"){

                if(columnObj.get("bVisible")!=!found)
                {
                    modified = true;
                    columnObj.set("bVisible", !found);
                    thisPtr.dataTable.fnSetColumnVis(columnObj.get("aTargets")[0], !found, false);
                }
            }
            else{
                if(columnObj.get("bVisible")!=found)
                {
                    modified = true;
                    columnObj.set("bVisible", found);
                    thisPtr.dataTable.fnSetColumnVis(columnObj.get("aTargets")[0], found, false);
                }
            }
        });

        if(modified)
        {
            this.resizeView();
        }
    },
    resizeView:function()
    {
        var innerHeight =  $(".inner-center").height();
        var innerWidth =  $(".inner-center").width();
        var innerHeightAdjusted = innerHeight - 90
        $(".dataTables_wrapper").css("min-height", innerHeightAdjusted+"px");
        $(".dataTables_wrapper").css("height", innerHeightAdjusted+"px");
        $(".dataTables_scrollBody").css("height", innerHeightAdjusted+"px");
        $(".dataTables_scrollBody").css("min-height", innerHeightAdjusted+"px");
      //  $(".dataTables_scrollBody").height(innerHeight-110);
        this.dataTable.fnSettings().oScroll.sY =innerHeightAdjusted;
        this.dataTable.fnAdjustColumnSizing();
    },
    getServerData:function( sUrl, aoData, fnCallback, oSettings ) {
        var result = {
            "aaData":{},
            "iTotalRecords":0,
            "iTotalDisplayRecords":0
        }
        var wfsModel = this.wfsModel;
        var thisPtr = this;
        if(!this.blockGetServerData&&sUrl&&this.model&&wfsModel)
        {
            result.iTotalRecords = wfsModel.get("numberOfFeatures");
            //var searchable = oSettings.aoColumns[oSettings.aaSorting[0][0]].bSearchable;
            var oColumn = oSettings.aoColumns[ oSettings.aaSorting[0][0] ];
            var sortDirection = oSettings.aaSorting[0][1];
            var sort = sortDirection?"[['"+oColumn.mDataProp.toLowerCase()+"','"+oSettings.aaSorting[0][1].toUpperCase()+"']]":"";
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
            if(wfsModel.dirty&&sort)
            {
                this.model.reset();
                model.fetch({dataType: "jsonp",
                    update: false,
                    remove: true,
                    date:{cache:false},
                    "success":function(){
                        wfsModel.dirty = false;
                        result.aaData = model.toJSON();
                        result.iTotalRecords =   wfsModel.get("numberOfFeatures");
                        result.iTotalDisplayRecords =   wfsModel.get("numberOfFeatures");
                        if((wfsModel.get("numberOfFeatures") < 1)&&(model.size()>0))
                        {
                            result.iTotalRecords =        model.size();
                            result.iTotalDisplayRecords = model.size();
                            wfsModel.fetchCount();
                        }
                        if(result.iTotalRecords > 100000)  result.iTotalRecords = 100000;
                        if(result.iTotalDisplayRecords > 100000)  result.iTotalDisplayRecords = 100000;
                        fnCallback(result);
                        thisPtr.dataTable.fnAdjustColumnSizing();

                        //thisPtr.blockGetServerData = true;
                        //thisPtr.dataTable.fnAdjustColumnSizing();
                        //thisPtr.blockGetServerData = false;
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
                if(result.iTotalRecords > 100000)  result.iTotalRecords = 100000;
                if(result.iTotalDisplayRecords > 100000)  result.iTotalDisplayRecords = 100000;
                fnCallback(result);
                //thisPtr.blockGetServerData = true;
                //thisPtr.dataTable.fnAdjustColumnSizing();
                //thisPtr.blockGetServerData = false;
            }
        }
        else if(this.model&&wfsModel&&this.model.size())
        {
            result.iTotalRecords =   wfsModel.get("numberOfFeatures");
            result.iTotalDisplayRecords =   wfsModel.get("numberOfFeatures");
            result.aaData = this.model.toJSON();
            fnCallback(result);
        }
        else
        {
            fnCallback(result);
        }
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

/*
jQuery.fn.dataTableExt.aTypes.push(
    function ( sData ) {
        return 'html';
    }
);
*/
jQuery.fn.dataTableExt.oSort['html-undefined']  = function(a,b) {
    return false;
};
