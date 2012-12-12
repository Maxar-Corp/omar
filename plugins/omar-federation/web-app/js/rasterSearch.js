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
    el:"#rasterSearchPage",
    bboxView:null,
    initialize:function(params){
        this.bboxView = new OMAR.views.BBOX();
        this.bboxModel = this.bboxView.model;
        this.setElement(this.el);
    },
    events: {
        "click #SearchRasterId": "searchRaster"
    },
    render:function(){
        if(this.bboxView)
        {
            this.bboxView.render();
        }
    },
    searchRaster:function(){
        var wfs = new OMAR.models.Wfs({"resultType":"hits"});
        wfs.set("filter","BBOX(ground_geom,"+this.bboxModel.toWmsString()+")");
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