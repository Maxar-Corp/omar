/*
var DataUtilities = (function() {
    //Privileged variables and methods
    var self = {
        "processXML": function(xml) {
            if (!jQuery.support.htmlSerialize) {
                //If IE 6+
                var xmlDoc = new ActiveXObject("Microsoft.XMLDOM");
                xmlDoc.loadXML(xml);
                xml = xmlDoc;
            }
            return xml;
        }
    };
    return self;
})();
  */


OMAR.views.FederatedRasterSearch = Backbone.View.extend({
    el:"#rasterSearchPageId",
    bboxView:null,
    initialize:function(params){
        this.bboxView = new OMAR.views.BBOX();
        this.bboxModel = this.bboxView.model;
        this.dateTimeRangeView = new OMAR.views.SimpleDateRangeView();
        this.dateTimeRangeModel = this.dateTimeRangeView.model;
        this.setElement(this.el);
    },
    events: {
        "click #SearchRasterId": "searchRaster"
    },
    render:function(){
        if(this.bboxView)
        {
            this.bboxView.render();
            this.dateTimeRangeView.render();
        }
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
        //this.bboxModel.toCqlString("ground_geom");
        var cqlFilter = this.toCql();
       // alert(cqlFilter);
        wfs.set("filter",cqlFilter);
        //alert(wfs.toUrl());
        $.ajax({
            url: wfs.toUrl(),
            type: "GET",
            dataType: "text",
            timeout: 20000,
            success: function(response) {
                var xml = OMAR.parseXml(response);
                if(xml.documentElement)
                {
                    alert(xml.documentElement.getAttribute("numberOfFeatures"));
                }
            },
            error: function(x, t, m) {
                if(t==="timeout") {
                    alert("got timeout");
                } else {
                    alert(t);
                }
            }
        });
    }
});



OMAR.pages.FederatedRasterSearch = (function($){

   // var self = {
   // };

    /*
        BaseView = Backbone.View.extend({
            initialize: function(){
                this.el = $(this.el);
            }
        });

        self.TestView = self.BaseView.extend({
            el:"#container",
            initialize: function(params)
            {
                this.el = $(this.el);
                self.BaseView.prototype.initialize.call(this);
            },
            events: {
                "click #buttonId": "doStuff"
            },
            doStuff: function(e) {
               // alert("CLICKED");
                $("#loadMeUp").html("CLICKED IT MAN!");
                //this.el.html("CLICK: HEY MAN THIS IS COOLER THAN COOL"); // prevent default behavior
                // How can I access the element (i.e. a <a>) here?
            },
            render:function()
            {
                alert(this.model);
                //this.el.html("HEY MAN THIS IS COOLER THAN COOL");
            }
        });
    */
   // self.ajax = $;

   // $.get(wfs.toUrl(), function(data) {
   //     alert(data);
   // });

    /*
    self.start = function (){
        //self.mapView  = (new MapView);
        self.bboxView = (new OMAR.views.BBOX());
        self.bboxView.render();
        self.federatedSearchView = new OMAR.views.FederatedRasterSearch();
        self.federatedSearchView.bboxModel = self.bboxView.model;
        federatedSearchView.render();
    }

    return self;
    */
    var result = new OMAR.views.FederatedRasterSearch();

    //result.bboxView = new OMAR.views.BBOX();
    //result.bboxModel = result.bboxView.model;


   // var tempXml = "<?xml version='1.0' encoding='UTF-8'?><temp id='12'/>";

  //  $(tempXml).find('temp').each(function(){
  //     alert("HERE");
  //  });
    //alert($(temp).find('id'));
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
                ,	west__size:				225
                ,   west__minSize:          225
                ,	east__size:				75
                ,	spacing_open:			8  // ALL panes
                ,	spacing_closed:			8  // ALL panes
                ,	west__spacing_closed:	12
                ,	east__spacing_closed:	12
            }
        }
    });

    var searchPageController = new OMAR.pages.FederatedRasterSearch(jQuery);
    searchPageController.render();

});
