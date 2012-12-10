OMAR.pages.RasterSearch = (function($){
    var self = {
    };

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
   

    self.start = function (){
        //self.mapView  = (new MapView);
        self.bboxView = (new OMAR.views.BBOX());

// 

        self.bboxView.render();
        //self.mapView.render();
    }
    return self;
});