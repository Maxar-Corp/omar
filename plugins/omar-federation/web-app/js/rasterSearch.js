OMAR.models.WfsTypeNameModel = Backbone.Model.extend({
    idAttribute:"typeName",
    defaults:{
        typeName:"raster_entry"
    },
    initialize:function(params)
    {
    }
});

OMAR.views.WfsTypeNameView = Backbone.View.extend({
    el:"#wfsTypeNameId",
    initialize:function(params){
        this.setElement(this.el);
        if(params)
        {
            if(params.model)
            {
                this.model = params.model;
            }
        }
        if(!this.model)
        {
            this.model = new OMAR.models.WfsTypeNameModel();
        }
        this.model.bind("change", this.modelChanged, this);
    },
    modelChanged:function(){
        this.render();
    },
    render:function(){

        this.wfsTypeNameVideoDataSetEl = $(this.el).find("#WfsTypeNameVideoDataSetId")[0];
        this.wfsTypeNameRasterEntryEl  = $(this.el).find("#WfsTypeNameRasterEntryId")[0];

        if(this.model)
        {
            if(this.model.get("typeName") == "raster_entry")
            {
                $(this.wfsTypeNameRasterEntryEl).click();
            }
            else
            {
                $(this.wfsTypeNameVideoDataSetEl).click();
            }
        }

        $(this.el).delegate("#WfsTypeNameVideoDataSetId", "click", this.videoDataSetClicked.bind(this));
        $(this.el).delegate("#WfsTypeNameRasterEntryId", "click", this.rasterEntryClicked.bind(this));

    },
    videoDataSetClicked:function(){
        this.model.unbind("change", this.modelChanged, this);
        this.model.set({typeName:"video_data_set"});
        this.model.bind("change", this.modelChanged, this);
    },
    rasterEntryClicked:function(){
        this.model.unbind("change", this.modelChanged, this);
        this.model.set({typeName:"raster_entry"});
        this.model.bind("change", this.modelChanged, this);
    }
})

OMAR.views.FederatedRasterSearch = Backbone.View.extend({
    el:"#rasterSearchPageId",
    bboxView:null,
    initialize:function(params){
        var thisPtr = this;
        this.cqlModel = new OMAR.models.CqlModel();
        this.wfsTypeNameModel = new OMAR.models.WfsTypeNameModel();
        this.wfsTypeNameView = new OMAR.views.WfsTypeNameView({model:this.wfsTypeNameModel});

        this.bboxView = new OMAR.views.BBOX();
        this.bboxModel = this.bboxView.model;

        this.pointView = new OMAR.views.PointView();
        this.pointModel = this.pointView.model;

        this.menuView = new OMAR.views.MenuView();
        this.menuModel = this.menuView.model;

        this.menuView.bind("onKmlQueryClicked", this.kmlQueryClicked, this);
        this.menuView.bind("onKmlClicked", this.kmlClicked, this);
        this.menuView.bind("onGeoJsonClicked", this.geoJsonClicked, this);
        this.menuView.bind("onGml2Clicked", this.gml2Clicked, this);
        this.menuView.bind("onCsvClicked", this.csvClicked, this);

        this.dateTimeRangeView = new OMAR.views.SimpleDateRangeView();
        this.dateTimeRangeModel = this.dateTimeRangeView.model;
        this.wfsServerCountModel = new OMAR.models.WfsModel({"resultType":"hits"});

        this.omarServerCollectionView = new OMAR.views.OmarServerCollectionView(
            {"model":new OMAR.models.OmarServerCollection(),
             "wfsServerCountModel":this.wfsServerCountModel,
             "wfsTypeNameModel":this.wfsTypeNameModel
            }
        );
        this.measurementUnitView = new OMAR.views.UnitModelView({el:"#measurementUnitViewId"});
        this.measurementUnitModel = this.measurementUnitView.model;

        var mapParams = params.map;
        mapParams.unitModelView = this.measurementUnitView;
        this.mapView = new OMAR.views.Map(mapParams);
        this.mapView.setSearchType(this.wfsTypeNameModel);
        this.mapView.setBboxModel(this.bboxModel);
        this.mapView.setPointModel(this.pointModel);
        this.measurementUnitModel.set("unit", "meters");
        this.mapView.setUnitModelView(this.measurementUnitView);

        this.mapView.setServerCollection(this.omarServerCollectionView.model);
        this.setElement(this.el);

        // construct with a shared wfsTypeName model
        this.dataModelView = new OMAR.views.DataModelView({wfsTypeNameModel:this.wfsTypeNameModel});

        var cqlViewParams = params.cql;
        cqlViewParams.wfsTypeNameModel = this.wfsTypeNameModel;
        this.cqlView = new OMAR.views.CqlView(cqlViewParams);
        this.cqlView.bind("onCqlChanged",this.cqlCustomQueryChanged, this);
        this.currentCqlModel = this.cqlView.model;
        this.dateTimeRangeModel.bind('change', this.updateFootprintCql, this);
        this.bboxModel.bind('change', this.updateFootprintCql, this);
        this.currentCqlModel.bind('change', this.updateFootprintCql, this);

        this.omarServerCollectionView.bind("onModelClicked", this.serverClicked, this);
        this.omarServerCollectionView.activeServerModel.bind("change", this.serverClicked, this);

        this.viewSelector = new OMAR.views.ViewSelector({el:"#tabView",
                                                         views:["#CustomQueryView",
                                                                "#MapView",
                                                                "#ResultsView"]});
        this.viewSelector.bind("show", this.showTab, this);
        this.wfsTypeNameModel.bind("change", this.wfsTypeNameChanged, this);
        this.cqlModel.bind("change", this.cqlModelChanged, this);
        this.useSpatialFlag = $('#spatialSearchFlag').is(":checked");
        $('#spatialSearchFlag').click(function() {
            thisPtr.useSpatialFlag = $(this).is(':checked');
            thisPtr.updateFootprintCql();
        });
    },
    events: {
        "click #SearchRasterId": "searchRaster"
    },
    cqlCustomQueryChanged:function(){
        this.updateFootprintCql();
        this.searchRaster();
    },
    showTab:function(idx){
        if(idx == 0)
        {
        }
        else if(idx == 1)
        {
            this.centerResize();
        }
        if(idx == 2)
        {
            this.dataModelView.resizeView();
        }
    },
    serverClicked:function(){
        var cqlFilter = this.toCql();
        var model =  this.omarServerCollectionView.model.get(this.omarServerCollectionView.activeServerModel.get("id"));
        this.omarServerCollectionView.wfsServerCountModel.set({"filter":cqlFilter,
                                                          "typeName":this.wfsTypeNameModel.get("typeName")});
        if(model)
        {
            var settings = {"url":model.get("url")+"/wfs",
                "filter":cqlFilter,
                "typeName":this.wfsTypeNameModel.get("typeName")};
            this.dataModelView.wfsModel.set(settings);
            this.viewSelector.click(2);
            this.viewSelector.setText(2, model.get("nickname"));
        }
    },
    kmlQueryClicked:function(){

        // alert(this.mapView.hasBBOXSelection());
        var model = this.omarServerCollectionView.model.get(this.omarServerCollectionView.activeServerModel.get("id"));
        var wfsModel = this.dataModelView.wfsModel.clone();
        var cqlFilter = wfsModel.get("filter");

        var currentSelection = this.dataModelView.getCurrentSelection();

        wfsModel.set({
            outputFormat:"kmlquery"
            ,resultType:""
        });
        //var saveSpatial = this.useSpatialFlag;
        //this.useSpatialFlag = this.mapView.hasBBOXSelection();
        //cqlFilter = this.toCql();
        //this.useSpatialFlag = saveSpatial;

        if(currentSelection.size() > 0) {
            var idCql = "(id in (" + currentSelection.toStringOfIds() + "))";

            if(!cqlFilter) {
                cqlFilter = idCql;
            }
            else {
                cqlFilter = idCql + " AND " + cqlFilter;
            }
            // clear out offset if there is a selection
            wfsModel.set({
                maxFeatures:""
                ,offset:""
                ,filter:cqlFilter
            });
        }
        else
        {
            wfsModel.set({
                filter:cqlFilter
            });

        }
        window.open(wfsModel.toUrl(),"myWindow");
    },
    kmlClicked:function(){
        var model = this.omarServerCollectionView.model.get(this.omarServerCollectionView.activeServerModel.get("id"));
        var wfsModel = this.dataModelView.wfsModel.clone();
        var cqlFilter = wfsModel.get("filter");
        var currentSelection = this.dataModelView.getCurrentSelection();
        wfsModel.set({
            outputFormat:"kml"
            ,resultType:""
        });
        if(currentSelection.size() > 0) {
            var idCql = "(id in (" + currentSelection.toStringOfIds() + "))";

            if(!cqlFilter) {
                cqlFilter = idCql;
            }
            else {
                cqlFilter = idCql + " AND " + cqlFilter;
            }
            // clear out offset if there is a selection
            wfsModel.set({
                maxFeatures:""
                ,offset:""
                ,filter:cqlFilter
            });
        }

        window.open(wfsModel.toUrl(),"myWindow");
    },

    geoJsonClicked:function(){
        var model = this.omarServerCollectionView.model.get(this.omarServerCollectionView.activeServerModel.get("id"));
        var wfsModel = this.dataModelView.wfsModel.clone();
        var cqlFilter = wfsModel.get("filter");
        
        var currentSelection = this.dataModelView.getCurrentSelection();
        wfsModel.set({
            outputFormat:"json"
            ,resultType:""
        });
        if(currentSelection.size() > 0) {
            var idCql = "(id in (" + currentSelection.toStringOfIds() + "))";

            if(!cqlFilter) {
                cqlFilter = idCql;
            }
            else {
                cqlFilter = idCql + " AND " + cqlFilter;
            }
            // clear out offset if there is a selection
            wfsModel.set({
                maxFeatures:""
                ,offset:""
                ,filter:cqlFilter
            });

        }

        window.open(wfsModel.toUrl(),"myWindow");
    },
    gml2Clicked:function(){
        var model = this.omarServerCollectionView.model.get(this.omarServerCollectionView.activeServerModel.get("id"));
        var wfsModel = this.dataModelView.wfsModel.clone();
        var cqlFilter = wfsModel.get("filter");
        
        var currentSelection = this.dataModelView.getCurrentSelection();
        wfsModel.set({
            outputFormat:"gml2"
            ,resultType:""
        });
        if(currentSelection.size() > 0) {
            var idCql = "(id in (" + currentSelection.toStringOfIds() + "))";

            if(!cqlFilter) {
                cqlFilter = idCql;
            }
            else {
                cqlFilter = idCql + " AND " + cqlFilter;
            }
            // clear out offset if there is a selection
            wfsModel.set({
                maxFeatures:""
                ,offset:""
                ,filter:cqlFilter
            });

        }

        window.open(wfsModel.toUrl(),"myWindow");
    },
    csvClicked:function(){
        var model = this.omarServerCollectionView.model.get(this.omarServerCollectionView.activeServerModel.get("id"));
        var wfsModel = this.dataModelView.wfsModel.clone();
        var cqlFilter = wfsModel.get("filter");
        
        var currentSelection = this.dataModelView.getCurrentSelection();
        wfsModel.set({
            outputFormat:"csv"
            ,resultType:""
        });
        if(currentSelection.size() > 0) {
            var idCql = "(id in (" + currentSelection.toStringOfIds() + "))";

            if(!cqlFilter) {
                cqlFilter = idCql;
            }
            else {
                cqlFilter = idCql + " AND " + cqlFilter;
            }
            // clear out offset if there is a selection
            wfsModel.set({
                maxFeatures:""
                ,offset:""
                ,filter:cqlFilter
            });

        }

        window.open(wfsModel.toUrl(),"myWindow");
    },
    wfsTypeNameChanged:function()
    {
        this.searchRaster();
    },
    cqlModelChanged:function()
    {
        this.searchRaster();
    },
    render:function(){
        if(this.wfsTypeNameView)
        {
            this.wfsTypeNameView.render();
        }
        if(this.bboxView)
        {
            this.bboxView.render();
        }
        if(this.pointView)
        { 
            this.pointView.render();
        }

        if(this.menuView)
        { 
            this.menuView.render();
        }

        if(this.dateTimeRangeView)
        {
            this.dateTimeRangeView.render();
        }

        if(this.mapView)
        {
            this.mapView.render();
        }
        if(this.cqlView)
        {
            this.cqlView.render();
        }
        if(this.dataModelView)
        {
            this.dataModelView.render();
        }
        if(this.measurementUnitView)
        {
            this.measurementUnitView.render();
        }
        // lets make sure that that the map object exists
        // before we start the AJAX calls for fetching the server lists
        //
        if(this.omarServerCollectionView)
        {
            var collection =  this.omarServerCollectionView;

            collection.model.fetch({success:function(){collection.render()},
                update: true, remove: false,date:{cache:false}});
            window.setTimeout(this.updateServers.bind(this),5000);
        }

        if(this.mapView) this.mapView.setCqlFilterToFootprintLayers(this.toCql());//this.toFootprintCql());

        // we must render everything first and fully initialize before we set a selected view
        //
        this.viewSelector.click(1);
    },
    updateFootprintCql:function(){
        if(this.mapView) this.mapView.setCqlFilterToFootprintLayers(this.toCql());//this.toFootprintCql());
    },
    updateServers:function(){
        var collection =  this.omarServerCollectionView;
        collection.model.fetch({success:function(){},
            update: true, remove: false,date:{cache:false}});
        window.setTimeout(this.updateServers.bind(this),5000);
    },
    toCql:function(){
        var result = "";
        var timeQueryCql = null;
        var wfsTypeName = this.wfsTypeNameModel.get("typeName");
        var customQueryFilter = this.currentCqlModel.toCql();

        if(wfsTypeName == "raster_entry")
        {
            timeQueryCql = this.dateTimeRangeModel.toCql("acquisition_date");
        }
        else if(wfsTypeName == "video_data_set")
        {
            timeQueryCql = this.dateTimeRangeModel.toCql("start_date", "end_date");
        }
        var spatialQueryCql;

        //if ($('#spatialSearchFlag').is(':checked')) {
        if (this.useSpatialFlag) {
            if( $('input[name=spatialSearchType]:checked').val() == "bbox" )
            {
                spatialQueryCql = this.bboxModel.toCql("ground_geom");
            }
            else if( $('input[name=spatialSearchType]:checked').val() == "point" )
            { 
                spatialQueryCql = this.pointModel.toCql("ground_geom");
            }
        }

        if(timeQueryCql&&spatialQueryCql)
        {
            result = "(("+spatialQueryCql+")AND(" +timeQueryCql+"))";
        }
        else if(spatialQueryCql)
        {
            result=spatialQueryCql;
        }
        else
        {
            result = timeQueryCql;
        }
        if(customQueryFilter!="")
        {
            if(result) result += "AND";
            result += customQueryFilter;
        }
        return result;
    },
    centerResize:function(){
        //alert($("#tabView").height() + ", " + $(".inner-center").height()+","+$(".tabViewContainer").height());
        //var h = $(".inner-center").height();
        //$("#tabView").height(h-110);

        if(this.dataModelView) this.dataModelView.resizeView();
        if(this.mapView)       this.mapView.resizeView();
    },
    searchRaster:function(){
        var cqlFilter = this.toCql();
       // alert(cqlFilter);
        this.wfsServerCountModel.set({
            filter:cqlFilter
        });
        this.wfsServerCountModel.trigger("change");
        var model = this.omarServerCollectionView.model.get(this.omarServerCollectionView.activeServerModel.get("id"));
        if(model)
        {
            this.dataModelView.wfsModel.set(
                {"url":model.get("url")+"/wfs",
                 "filter":cqlFilter,
                 "typeName":this.wfsTypeNameModel.get("typeName")}
            );
        }
     }
});

OMAR.federatedRasterSearch = null;
OMAR.pages.FederatedRasterSearch = (function($, params){
    OMAR.federatedRasterSearch = new OMAR.views.FederatedRasterSearch(params);
    return OMAR.federatedRasterSearch;
});

$(document).ready(function () {

    // OUTER-LAYOUT

    $('body').layout({
            center__paneSelector:   ".outer-center"
        ,   west__paneSelector:     ".outer-west"
        ,   east__paneSelector:     ".outer-east"
        ,   west__size:             125
        ,   east__size:             125
        ,   spacing_open:           8  // ALL panes
        ,   spacing_closed:         12 // ALL panes
        //, north__spacing_open:    0
        //, south__spacing_open:    0
        ,   north__maxSize:         200
        ,   south__maxSize:         200

            // MIDDLE-LAYOUT (child of outer-center-pane)
        ,   center__childOptions: {
                center__paneSelector:   ".middle-center"
            ,   west__paneSelector:     ".middle-west"
            ,   east__paneSelector:     ".middle-east"
            ,   west__size:             100
            ,   east__size:             100
            ,   spacing_open:           0  // ALL panes
            ,   spacing_closed:         0 // ALL panes

                // INNER-LAYOUT (child of middle-center-pane)
            ,   center__childOptions: {
                    center__paneSelector:   ".inner-center"
                ,   west__paneSelector:     ".inner-west"
                ,   east__paneSelector:     ".inner-east"
                ,   west__size:             225
                ,   west__minSize:          225
                ,   east__size:             175
                ,   spacing_open:           8  // ALL panes
                ,   spacing_closed:         8  // ALL panes
                ,   west__spacing_closed:   12
                ,   east__spacing_closed:   12
                , onresize_end:function(){if(OMAR.federatedRasterSearch) OMAR.federatedRasterSearch.centerResize();}
                }
            }
        });
    // initialize one time the html parsing for datatable

    init();
    OMAR.federatedRasterSearch.centerResize();
    //$( "#accordion" ).accordion();

  
  


});






