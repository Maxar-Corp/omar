OMAR.models.Bbox = Backbone.Model.extend(
{
    defaults:{
        "minx":-180.0,
        "miny":-90.0,
        "maxx":180.0,
        "maxy":90.0
    },
    initialize:function(options)
    {
    },
    toWmsString:function()
    {
        return (this.get("minx")+","+this.get("miny")+","+
                this.get("maxx")+","+this.get("maxy"));
    },
    validate:function(attrs)
    {
    },
    setFromWmsString:function(s)
    {
        var splitBounds = s.split(",");
        if(splitBounds.length == 4)
        {
            this.minx =parseFloat(splitBounds[0]);
            this.miny =parseFloat(splitBounds[1]);
            this.maxx =parseFloat(splitBounds[2]);
            this.maxy =parseFloat(splitBounds[3]);

        }

        return this;
    }
}
);

OMAR.views.BboxView = Backbone.Model.extend({
    el:"#BboxId",
    initialize: function(params)
    {
        // this should create a variable for us called this.$el
        this.setElement(this.el);
        this.model = new OMAR.BBOX();
        this.lowerLeftBboxEl = $("#lowerLeftBbox");
        this.upperRightBboxEl = $("#upperRightBbox");
        this.model.on("error",
            function(model,err) {
                alert("BBOX Has errors: " + err);
            });

    },
    events:{
        "change #lowerLeftBbox" : "llOnChange"
    },
    llOnChange: function(e){
        var v = this.lowerLeftBboxEl.val();
        var values = v.split(",");
        if(values.length ==2)
        {
            this.model.set({minx:values[0],miny:values[1]});
        }
    },
    bboxError:function(model, err){
    },
    render:function()
    {
        this.lowerLeftBboxEl.val(this.model.get("minx")+","
            +this.model.get("miny"));
        this.upperRightBboxEl.val(this.model.get("maxx")+","
            +this.model.get("maxy"));
    }
});

