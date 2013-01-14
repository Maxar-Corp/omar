OMAR.views.FederatedRasterSearch = Backbone.View.extend({
    el:"#rasterSearchPageId",
    bboxView:null,
    initialize:function(params){

        this.bboxView = new OMAR.views.BBOX();
        this.bboxModel = this.bboxView.model;

        this.pointView = new OMAR.views.PointView();
        this.pointModel = this.pointView.model;

        this.dateTimeRangeView = new OMAR.views.SimpleDateRangeView();
        this.dateTimeRangeModel = this.dateTimeRangeView.model;
        this.omarServerCollectionView = new OMAR.views.OmarServerCollectionView(
            {model:new OMAR.models.OmarServerCollection()}
        );
        this.mapView = new OMAR.views.Map(params.map);
        this.mapView.setBboxModel(this.bboxModel);
        this.mapView.setPointModel(this.pointModel);

        this.mapView.setServerCollection(this.omarServerCollectionView.model);
        this.setElement(this.el);

        this.dateTimeRangeModel.bind('change', this.updateFootprintCql, this)
        this.rasterEntryDataModelView = new OMAR.views.RasterEntryDataModelView();
        this.omarServerCollectionView.bind('onModelClicked', this.serverClicked, this);
    },
    events: {
        "click #SearchRasterId": "searchRaster"
    },
    showTab:function(event, ui){
        if(ui.index == 0)
        {
        }
        else if(ui.index == 1)
        {
            this.centerResize();
        }
        if(ui.index == 2)
        {
            this.rasterEntryDataModelView.resizeView();
        }
    },
    serverClicked:function(id){
        var cqlFilter = this.toCql();

        var model = this.omarServerCollectionView.getLastClickedModel();
        if(model)
        {
            this.rasterEntryDataModelView.wfsModel.set(
                {"url":model.get("url")+"/wfs",
                    "filter":cqlFilter}
            );
        }

        this.tabView.tabs("select", 2);
        $(this.tabView).find("#ResultsLabelId").text(model.get("nickname"));
    },
    render:function(){
        if(this.bboxView)
        {
            this.bboxView.render();
        }
        if(this.pointView)
        { 
            this.pointView.render();
        }
        if(this.dateTimeRangeView)
        {
            this.dateTimeRangeView.render();
        }

        if(this.mapView)
        {
            this.mapView.render();
        }

        if(this.rasterEntryDataModelView)
        {
            this.rasterEntryDataModelView.render();
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

        this.mapView.setCqlFilterToFootprintLayers(this.toFootprintCql());
        this.tabView = $( "#tabView" ).tabs(
            {   "active":1,
                "show": $.proxy(this.showTab, this)
            });

    },
    updateFootprintCql:function(){
        this.mapView.setCqlFilterToFootprintLayers(this.toFootprintCql());
    },
    updateServers:function(){
        var collection =  this.omarServerCollectionView;
        collection.model.fetch({success:function(){},
            update: true, remove: false,date:{cache:false}});
        window.setTimeout(this.updateServers.bind(this),5000);
    },
    toCql:function(){
        var result = "";
        var timeQueryCql = this.dateTimeRangeModel.toCql("acquisition_date");
      // var spatialQueryCql = this.bboxModel.toCql("ground_geom");
          var spatialQueryCql = this.pointModel.toCql("ground_geom");
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
        return result;
    },
    centerResize:function(){
        this.rasterEntryDataModelView.resizeView();
        this.mapView.resizeView();
    },
    toFootprintCql:function(){
        var result = "";
        var timeQueryCql = this.dateTimeRangeModel.toCql("acquisition_date");

        // add all criteria here later.   Fo now we will just do time
        //
        result = timeQueryCql;

        return result;
    },
    searchRaster:function(){
        var cqlFilter = this.toCql();

        this.omarServerCollectionView.wfsServerCount.attributes.filter = cqlFilter;
        this.omarServerCollectionView.wfsServerCount.trigger("change");
        var model = this.omarServerCollectionView.getLastClickedModel();
        if(model)
        {
            this.rasterEntryDataModelView.wfsModel.set(
                {"url":model.get("url")+"/wfs",
                 "filter":cqlFilter}
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
                ,   east__size:             75
                ,   spacing_open:           8  // ALL panes
                ,   spacing_closed:         8  // ALL panes
                ,   west__spacing_closed:   12
                ,   east__spacing_closed:   12
                , onresize_end:function(){if(OMAR.federatedRasterSearch) OMAR.federatedRasterSearch.centerResize();}
                }
            }
        });

    init();
    OMAR.federatedRasterSearch.centerResize();
    $( "#accordion" ).accordion();

    $("#jMenu").jMenu({
                  openClick : false,
                  ulWidth : 100,
                  effects : {
                    effectSpeedOpen : 200,
                    effectSpeedClose : 200,
                    effectTypeOpen : 'slide',
                    effectTypeClose : 'hide',
                    effectOpen : 'linear',
                    effectClose : 'linear'
                  },
                  TimeBeforeOpening : 100,
                  TimeBeforeClosing : 100,
                  animatedText : true,
                  paddingLeft: 10
                });
  


});



function generateKmlQuery() {
    alert("kml query code goes here.")
}

function refreshFootprints() {
    alert("refresh footprints code goes here.")
}

function search() {
    alert("search code goes here.")
}

function foo() {
    alert("search code goes here.")
}
