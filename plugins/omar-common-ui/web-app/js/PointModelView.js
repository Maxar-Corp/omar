OMAR.models.PointModel = Backbone.Model.extend(
{
    defaults:{
        "y": 0.0,
        "x": 0.0,
        "radius": 0
    },
    initialize:function(options)
    {
        this.ellipsoidModel = new OMAR.models.EllipsoidModel();
    },

    validate:function(attrs)
    {
        /*if(attrs.y && (!OMAR.isFloat(attrs.y.toString())))
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
        return null;*/
    },

    toCql:function(columnName)
    {
        var result = "";
        var bad = this.validate(this.attributes);
        if(!bad)
        {
            var radius = this.get("radius");

            if(radius == 0.0)
            {
                result = "CONTAINS("+columnName+","+"POINT("+this.get("x") +" "+this.get("y")+"))";
            }
            else
            {
                var newRadius = this.ellipsoidModel.getDegreesPerMeter(0.0).y * this.get("radius");
                result = "DWITHIN("+ columnName + ",POINT(" + this.get("x") + " "
                    + this.get("y") +")," + newRadius + ",meters)";
            }
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
        this.displayUnitEl = $("#displayUnit");
        this.model.on("error",
            function(model,err) {
                alert("Point has errors: " + err);
            });
        if(params)
        {
            if(params.displayUnitModel)
            {
                this.displayUnitModel = params.displayUnitModel;
            }
        }
        if(this.displayUnitModel)
        {
            this.displayUnitModel.on('change', this.displayUnitModelChanged, this);
        }
        this.model.on('change', this.pointModelChange, this);
    },
    events:{
        "change #center": "centerOnChange",
        "change #radius": "radiusOnChange"

    },
    displayUnitModelChanged: function(){
      this.render();
    },
    pointModelChange: function() {
        this.render();
    },
    centerOnChange: function(e){
        var v = this.centerPointEl.val();

        if(v.match(OMAR.ddRegExp)) {
            var values = v.split(",");

            this.model.off("change", this.pointModelChange, this);
            this.model.set({y:values[0],x:values[1]});
            this.model.on("change", this.pointModelChange, this);
            //alert("DD Match");
        }
        else if(v.match(OMAR.dmsRegExp)) {
            var match = OMAR.dmsRegExp.exec(v);
            var lat = convert.dmsToDd(match[1], match[2], match[3] + match[4], match[5]);
            var lon = convert.dmsToDd(match[6], match[7], match[8] + match[9], match[10]);
            
            this.model.off("change", this.pointModelChange, this);
            this.model.set({y:lat,x:lon});
            this.model.on("change", this.pointModelChange, this);
            //alert("DMS Match");
        }
        else if(v.match(OMAR.mgrsRegExp)) {
            var match = OMAR.mgrsRegExp.exec(v);
            var mgrs = convert.mgrsToDd(match[1], match[2], match[3], match[4], match[5], match[6]);
            var match2 = OMAR.ddRegExp.exec(mgrs);
            var lat = match2[1] + match2[2];
            var lon = match2[3] + match2[4];

            this.model.off("change", this.pointModelChange, this);
            this.model.set({y:lat,x:lon});
            this.model.on("change", this.pointModelChange, this);
            //alert("MGRS Match");
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
        if(this.displayUnitModel)
        {
            switch(this.displayUnitModel.get("unit"))
            {
                case "DMS":
                    this.centerPointEl.val(convert.ddToDms(this.model.get("y"), this.model.get("x")));

                    this.radiusEl.val(this.model.get("radius"));
                   break;
                case "MGRS":
                    this.centerPointEl.val(convert.ddToMgrs(this.model.get("y") , this.model.get("x")));

                    this.radiusEl.val(this.model.get("radius"));
                    break;
                default:
                    this.centerPointEl.val(this.model.get("y") + ", "
                        + this.model.get("x"));

                    this.radiusEl.val(this.model.get("radius"));
                     break;
            }
        }
        /*
        if(this.displayUnitEl.val() == "DMS") {
            this.centerPointEl.val(convert.ddToDms(this.model.get("y"), this.model.get("x")));

            this.radiusEl.val(this.model.get("radius"));
        }

        else if(this.displayUnitEl.val() == "MGRS") {
            this.centerPointEl.val(convert.ddToMgrs(this.model.get("y") , this.model.get("x")));
            
            this.radiusEl.val(this.model.get("radius"));
        }

        else {
            this.centerPointEl.val(this.model.get("y") + ", "
            + this.model.get("x"));
        
            this.radiusEl.val(this.model.get("radius"));
        }
        */
    }

});