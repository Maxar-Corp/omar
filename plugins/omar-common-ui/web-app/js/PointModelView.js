OMAR.models.PointModel = Backbone.Model.extend(
{
    defaults:{
        "y": 0.0,
        "x": 0.0,
        "radius": 0
    },
    initialize:function(options)
    {
    },

    validate:function(attrs)
    {
        if(attrs.y && (!OMAR.isFloat(attrs.y.toString())))
        {
            return ("Y value is invalid: " + attrs.y);
        }
        if(attrs.x && (!OMAR.isFloat(attrs.x.toString())))
        {
            return ("X value is invalid: " + attrs.x);
        }
        if(attrs.radius && (!OMAR.isFloat(attrs.radius.toString())))
        {
            return ("Radius value is invalid: " + attrs.radius);
        }
        return null;
    },

    toCql:function(columnName)
    {
        var result = "";
        var bad = this.validate(this.attributes);
        if(!bad)
        {
            result = "DWITHIN(geom, POINT(" + y + " " + x +"), " + radius + ", meters)";
        }
        return result;
    }
}
);

OMAR.views.PointView = Backbone.View.extend({
    el:"#pointRadiusId",
    initialize: function(params)
    {
        this.setElement(this.el);
        this.model = new OMAR.models.PointModel();
        this.centerPointEl = $("#center");//$(this.el).find(#center)[0]
        this.radiusEl = $("#radius");
        this.model.on("error",
            function(model,err) {
                alert("Point has errors: " + err);
            });
        this.model.on('change', this.pointModelChange, this);
    },
    events:{
        "change #center": "centerOnChange",
        "change #radius": "radiusOnChange"
    },
    pointModelChange: function() {
        this.render();
    },
    centerOnChange: function(e){
        var v = this.centerPointEl.val();
        var values = v.split(",");
        if(values.length ==2)
        {
            this.model.off("change", this.pointModelChange, this);
            this.model.set({y:values[0],x:values[1]});
            this.model.on("change", this.pointModelChange, this);
        }
        this.render();
    },
    radiusOnChange: function(e){
        var v = this.radiusEl.val();
        {
            this.model.off("change", this.pointModelChange, this);
            this.model.set({radius:this.radiusEl.val()});
            this.model.on("change", this.pointModelChange, this);
        }
        this.render();
    },
    pointError:function(model, err){
    },
    render:function()
    {
        this.centerPointEl.val(this.model.get("y") + ","
            + this.model.get("x"));
        this.radiusEl.val(this.model.get("radius"));
    }
});