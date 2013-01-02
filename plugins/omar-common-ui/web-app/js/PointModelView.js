OMAR.models.POINT = Backbone.Model.extend(
{
    defaults:{
        "lat":0.0,
        "lon":0.0,
        "radius":0
    },
    initialize:function(options)
    {
    },
    toWmsString:function()
    {
        return (this.get("lat")+","+this.get("lon"));
    },
    validate:function(attrs)
    {
        if(attrs.lat&&(!OMAR.isFloat(attrs.lat.toString())))
        {
            return ("Lat value is invalid: " + attrs.lat);
        }
        if(attrs.lon&&(!OMAR.isFloat(attrs.lon.toString())))
        {
            return ("Lon value is invalid: " + attrs.lon);
        }
        return null;
    },
    setFromWmsString:function(s)
    {
        var splitBounds = s.split(",");
        if(splitBounds.length == 2)
        {
            this.lat =parseFloat(splitBounds[0]);
            this.lon =parseFloat(splitBounds[1]);

        }

        return this;
    },
    toCql:function(columnName)
    {
        var result = "";
        var bad = this.validate(this.attributes);
        if(!bad)
        {
            result = "BBOX("+columnName+","+this.toWmsString()+")";
        }

        return result;
    }
}
);

OMAR.views.POINT = Backbone.View.extend({
    el:"#pointRadiusId",
    initialize: function(params)
    {
        // this should create a variable for us called this.$el
        this.setElement(this.el);
        this.model = new OMAR.models.POINT();
        this.mapCenterEl = $("#mapCenter");
        this.centerRadiusEl = $("#centerRadius");
        this.model.on("error",
            function(model,err) {
                alert("POINT Has errors: " + err);
            });
        this.model.on('change', this.pointModelChange, this);

    },
    events:{
        "change #mapCenter" : "mcOnChange",
        "change #centerRadius": "crOnChange"
    },

    pointModelChange: function() {
        this.render();
    },

    mcOnChange: function(e){
        var v = this.mapCenterEl.val();
        var values = v.split(",");
        if(values.length ==2)
        {
            this.model.off("change", this.pointModelChange, this);
            this.model.set({lat:values[1],lon:values[0]});
            this.model.on("change", this.pointModelChange, this);
        }
        this.render();
    },
    crOnChange: function(e){
        var v = this.centerRadiusEl.val();
        var values = v.split(",");
        if(values.length ==1)
        {
            this.model.off("change", this.pointModelChange, this);
            this.model.set({radius:values[0]});
            this.model.on("change", this.pointModelChange, this);
        }
        this.render();
    },
    pointError:function(model, err){
    },
    render:function()
    {
        this.mapCenterEl.val(this.model.get("lat")+","
            +this.model.get("lon"));
        this.centerRadiusEl.val(this.model.get("radius"));
    }
});

