OMAR.views.FederatedRasterSearch = Backbone.View.extend({
    el:"#rasterSearchPageId",
    bboxView:null,
    initialize:function(params){
        this.bboxView = new OMAR.views.BBOX();
        this.bboxModel = this.bboxView.model;
        this.dateTimeRangeView = new OMAR.views.SimpleDateRangeView();
        this.dateTimeRangeModel = this.dateTimeRangeView.model;
        this.omarServerCollectionView = new OMAR.views.OmarServerCollectionView(
            {model:new OMAR.models.OmarServerCollection({models:[
                    new OMAR.models.OmarServerModel({url:"http://localhost/omar"})
                ]}
            )}
        );
        this.mapView = new OMAR.views.Map(params.map);
        this.mapView.setBboxModel(this.bboxModel);
        this.setElement(this.el);

        //$( "#accordion" ).accordion();
    },
    events: {
        "click #SearchRasterId": "searchRaster"
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
        if(this.omarServerCollectionView)
        {
            var collection =  this.omarServerCollectionView;

            collection.model.fetch({success:function(){collection.render()},
                                    update: true, remove: false,date:{cache:false}});
            window.setTimeout(this.updateServers.bind(this),5000);
        }

        if(this.mapView)
        {
           this.mapView.render();
        }
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
        var bboxQueryCql = this.bboxModel.toCql("ground_geom");
        if(timeQueryCql&&bboxQueryCql)
        {
            result = "(("+bboxQueryCql+")AND(" +timeQueryCql+"))";
        }
        else if(bboxQueryCql)
        {
            result=bboxQueryCql;
        }
        else
        {
            result = timeQueryCql;
        }

        return result;
    },
    searchRaster:function(){
        var wfs = new OMAR.models.Wfs({"resultType":"hits"});
        var cqlFilter = this.toCql();
        wfs.set("filter",cqlFilter);

        for(var idx = 0; idx <this.omarServerCollectionView.model.size();++idx )
        {
             var model = this.omarServerCollectionView.model.at(idx);
             this.omarServerCollectionView.setBusy(model.id, true);
             wfs.set("url",model.get("url")+"/wfs");
             $.ajax({
                url: wfs.toUrl()+"&callback=?",
                cache:false,
                type: "GET",
                crossDomain:true,
                dataType: "json",
                timeout: 60000,
                modelId:model.id,
                scopePtr:this,
                success: function(response) {
                    if(response.numberOfFeatures!=null)
                    {
                        var numberOfFeatures = response.numberOfFeatures;
                        this.scopePtr.omarServerCollectionView.setBusy(this.modelId, false);
                        this.scopePtr.omarServerCollectionView.model.get(this.modelId).set({"count":numberOfFeatures});
                    }
                },
                error: function(x, t, m) {
                    var count = "Error";
                    if(t==="timeout") {
                        count = "Timeout"
                    } else {
                        //alert(JSON.stringify(x)+ " " +t + " " + m);
                    }
                    this.scopePtr.omarServerCollectionView.setBusy(this.modelId, false);
                    this.scopePtr.omarServerCollectionView.model.get(this.modelId).set({"count":count});
                }
            });
        }
    }
});

OMAR.pages.FederatedRasterSearch = (function($, params){
    var result = new OMAR.views.FederatedRasterSearch(params);
    return result;
});

$(document).ready(function () {

    // OUTER-LAYOUT

    $('body').layout({
        center__paneSelector:	".outer-center"
        ,	west__paneSelector:		".outer-west"
        ,	east__paneSelector:		".outer-east"
        ,	west__size:				125
        ,	east__size:				125
        ,	spacing_open:			8  // ALL panes
        ,	spacing_closed:			12 // ALL panes
        //,	north__spacing_open:	0
        //,	south__spacing_open:	0
        ,	north__maxSize:			50
        ,   north__minSize:         50
        ,	south__maxSize:			50
        ,   south__minSize:         50

        // MIDDLE-LAYOUT (child of outer-center-pane)
        ,	center__childOptions: {
            center__paneSelector:	".middle-center"
            ,	west__paneSelector:		".middle-west"
            ,	east__paneSelector:		".middle-east"
            ,	west__size:				100
            ,	east__size:				100
            ,	spacing_open:			8  // ALL panes
            ,	spacing_closed:			12 // ALL panes

            // INNER-LAYOUT (child of middle-center-pane)
            ,	center__childOptions: {
                center__paneSelector:	".inner-center"
                ,	west__paneSelector:		".inner-west"
                ,	east__paneSelector:		".inner-east"
                ,	west__size:				250
                ,   west__minSize:          250
                ,	east__size:				75
                ,	spacing_open:			8  // ALL panes
                ,	spacing_closed:			8  // ALL panes
                ,	west__spacing_closed:	12
                ,	east__spacing_closed:	12
            }
        }
    });



    init();

});
