var convert = new CoordinateConversion();

OMAR.models.BBOX = Backbone.Model.extend(
    {
        defaults:{
            "minx": -180.0,
            "miny": -90.0,
            "maxx": 180.0,
            "maxy": 90.0
        },
        initialize:function(options)
        {
        },
        intersects:function(bbox)
        {
            var maxOfMinx = Math.max(this.attributes.minx, bbox.attributes.minx);
            var minOfMaxx = Math.min(this.attributes.maxx, bbox.attributes.maxx);
            var minOfMaxy = Math.min(this.attributes.maxy, bbox.attributes.maxy);
            var maxOfMiny = Math.max(this.attributes.miny, bbox.attributes.miny);
            return ((maxOfMinx <= minOfMaxx)&&
                    (minOfMaxy >= maxOfMiny));
        },
        intersect:function(bbox)
        {
            var result = null;

            var maxOfMinx = Math.max(this.attributes.minx, bbox.attributes.minx);
            var minOfMaxy = Math.min(this.attributes.maxy, bbox.attributes.maxy);
            var minOfMaxx = Math.min(this.attributes.maxx, bbox.attributes.maxx);
            var maxOfMiny = Math.max(this.attributes.miny, bbox.attributes.miny);

            if( minOfMaxx < maxOfMinx || maxOfMiny > minOfMaxy )
            {
                return null;
            }

            return new OMAR.models.BBOX({minx: maxOfMinx
                                         ,miny: maxOfMiny
                                         ,maxx: minOfMaxx
                                         ,maxy: minOfMaxy
                                         });
        },
        toWmsString:function()
        {
            return (this.get("minx") + "," + this.get("miny") + "," +
                this.get("maxx") + "," + this.get("maxy"));
        },
        setFromWfsFeatureGeom:function(tempGeom)
        {
            var geom = null
            var minx = 9e20;
            var maxx = -9e20;
            var miny = 9e20;
            var maxy = -9e20;

            if(typeof tempGeom == "string")
            {
                geom = JSON.parse(tempGeom);
            }
            else
            {
                geom = tempGeom;
            }
            switch(geom.type.toLowerCase())
            {
                case "polygon":
                    $(geom.coordinates).each(function(idx,v){
                        $(v).each(function(idx, v){
                            if(v[0] < minx) minx = v[0];
                            if(v[0] > maxx) maxx = v[0];
                            if(v[1] < miny) miny = v[1];
                            if(v[1] > maxy) maxy = v[1];
                            });
                    });
                    break;
                case "multipolygon":
                    $(geom.coordinates).each(function(idx,v){
                           $(v).each(function(idx, v)
                           {
                                $(v).each(function(idx, v){
                                    if(v[0] < minx) minx = v[0];
                                    if(v[0] > maxx) maxx = v[0];
                                    if(v[1] < miny) miny = v[1];
                                    if(v[1] > maxy) maxy = v[1];
                                })
                            })
                        }
                    );
                    break;
            }

            this.set({"minx":minx
                ,"maxx":maxx
                ,"miny":miny
                ,"maxy":maxy
            });
        },
        getCenter: function(){
            var result = {
                x:(this.get("minx")+this.get("maxx"))*0.5,
                y:(this.get("miny")+this.get("maxy"))*0.5
            };
            return result;
        },
        validate:function(attrs)
        {
            if(attrs.minx && (!OMAR.isFloat(attrs.minx.toString())))
            {
                return ("Minx value is invalid: " + attrs.minx);
            }
            if(attrs.miny && (!OMAR.isFloat(attrs.miny.toString())))
            {
                return ("Miny value is invalid: " + attrs.miny);
            }
            if(attrs.maxx && (!OMAR.isFloat(attrs.maxx.toString())))
            {
                return ("Maxx value is invalid: " + attrs.maxx);
            }
            if(attrs.maxy && (!OMAR.isFloat(attrs.maxy.toString())))
            {
                return ("Maxy value is invalid: " + attrs.maxy);
            }

            if(typeof attrs.minx == "string")
            {
                attrs.minx = parseFloat(attrs.minx);
            }
            if(typeof attrs.miny == "string")
            {
                attrs.miny = parseFloat(attrs.miny);
            }
            if(typeof attrs.maxx  == "string")
            {
                attrs.maxx = parseFloat(attrs.maxx);
            }
            if(typeof attrs.maxy  == "string")
            {
                attrs.maxy = parseFloat(attrs.maxy);
            }

            if(attrs.minx < -180.0) attrs.minx = -180.0;
            if(attrs.maxx > 180.0) attrs.maxx = 180.0;
            if(attrs.miny < -90.0) attrs.miny = -90.0;
            if(attrs.maxy > 90.0) attrs.maxy = 90.0;

            return null;
        },
        setFromWmsString:function(s)
        {
            var splitBounds = s.split(",");
            if(splitBounds.length == 4)
            {
                this.set({minx:parseFloat(splitBounds[0]),
                          miny:parseFloat(splitBounds[1]),
                          maxx:parseFloat(splitBounds[2]),
                          maxy:parseFloat(splitBounds[3])});
 //               this.minx = parseFloat(splitBounds[0]);
 //               this.miny = parseFloat(splitBounds[1]);
 //               this.maxx = parseFloat(splitBounds[2]);
  //              this.maxy = parseFloat(splitBounds[3]);
            }
            return this;
        },
        toCql:function(columnName)
        {
            var result = "";
            var bad = this.validate(this.attributes);
            if(!bad)
            {
                result = "BBOX(" + columnName + "," + this.toWmsString() + ")";
            }
            return result;
        }
    }
);

OMAR.views.BBOX = Backbone.View.extend({
    el:"#boundBoxId",
    initialize: function(params)
    {
        this.setElement(this.el);
        this.model = new OMAR.models.BBOX();
        this.lowerLeftBboxEl = $("#lowerLeftBbox");
        this.upperRightBboxEl = $("#upperRightBbox");
        //this.displayUnitEl = $("#displayUnit");
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

        this.model.on("error",
            function(model,err) {
                alert("BBOX Has errors: " + err);
            });
        this.model.on('change', this.bboxModelChange, this);
    },
    events:{
        "change #lowerLeftBbox": "llOnChange",
        "change #upperRightBbox": "urOnChange"
    },
    displayUnitModelChanged:function(){
      this.render();
    },
    bboxModelChange: function() {
        this.render();
    },
    llOnChange: function(e){ //alert("CHANGE");
        var v = this.lowerLeftBboxEl.val();
        var values = v.split(",");
        if(values.length ==2)
        {
            this.model.off("change", this.bboxModelChange, this);
            this.model.set({minx:values[1].trim(),miny:values[0].trim()});
            this.model.on("change", this.bboxModelChange, this);
            this.trigger("onLlChanged", this.model);
        }
        this.render();
    },
    urOnChange: function(e){
        var v = this.upperRightBboxEl.val();
        var values = v.split(",");
        if(values.length ==2)
        {
            this.model.off("change", this.bboxModelChange, this);
            this.model.set({maxx:values[1].trim(),maxy:values[0].trim()});
            this.model.on("change", this.bboxModelChange, this);
            this.trigger("onUrChanged", this.model);

        }
        this.render();
    },
    bboxError:function(model, err){
    },
    render:function()
    {
        // lets validate the params before showing them so the get clamped
        //
        this.model.set(this.model.attributes);
        if(this.displayUnitModel)
        {
            switch(this.displayUnitModel.get("unit"))
            {
                case "DMS":
                    this.lowerLeftBboxEl.val(convert.ddToDms(this.model.get("miny"), this.model.get("minx")));
                    this.upperRightBboxEl.val(convert.ddToDms(this.model.get("maxy"), this.model.get("maxx")));
                    break;
                case "MGRS":
                    this.lowerLeftBboxEl.val(convert.ddToMgrs(this.model.get("miny") , this.model.get("minx")));
                    this.upperRightBboxEl.val(convert.ddToMgrs(this.model.get("maxy") , this.model.get("maxx")));
                    break;
                default:
                    this.lowerLeftBboxEl.val(this.model.get("miny") + ", "
                        + this.model.get("minx"));
                    this.upperRightBboxEl.val(this.model.get("maxy") + ", "
                        + this.model.get("maxx"));
                    break;
            }
        }
     }
});

