/**
 * Created by gpotts on 8/3/15.
 */
OMAR.models.GeneralQueryModel = Backbone.Model.extend({
    defaults: {
        niirsCheckbox:false,
        niirsMinValue:0.0,
        niirsMaxValue:9.0,
        azimuthAngleCheckbox:false,
        azimuthAngleMinValue:0.0,
        azimuthAngleMaxValue:360.0,
        grazingAngleCheckbox:false,
        grazingAngleMinValue:0.0,
        grazingAngleMaxValue:90.0,
        sunAzimuthCheckbox:false,
        sunAzimuthMinValue:0.0,
        sunAzimuthMaxValue:360.0,
        sunElevationCheckbox:false,
        sunElevationMinValue:-90.0,
        sunElevationMaxValue:90.0,
        cloudCoverageCheckbox:false,
        cloudCoverageMaxValue:100.0,
        missionCheckbox:false,
        missionValue:"",
        beNumberCheckbox:false,
        beNumberValue:"",
        targetIdCheckbox:false,
        targetIdValue:"",
        wacCheckbox:false,
        wacValue:""
    },
    initialize: function (params) {

        if (params)
        {
        }
    },
    toCql: function (errors)
    {
        var rangeAttributes = [
            {name:"niirs",dbname:"niirs"},
            {name:"azimuthAngle", dbname:"azimuth_angle"},
            {name:"grazingAngle", dbname:"grazing_angle"},
            {name:"sunElevation", dbname:"sun_elevation"},
            {name:"sunAzimuth", dbname:"sun_azimuth"},
            {name:"cloudCoverage", dbname:"cloud_cover"},
                              ]
        var result = "";
        var conjunction = " AND ";
        var thisPtr = this;

        $(rangeAttributes).each(function(idx, obj){
            var checked  = thisPtr.get(obj.name+"Checkbox");
            var dbname   = obj.dbname;
            var minValue = thisPtr.get(obj.name+"MinValue");
            var maxValue = thisPtr.get(obj.name+"MaxValue");
            if(checked)
            {
                if(minValue == null)
                {
                    if(!isNaN(maxValue))
                    {
                        var value = "("+dbname+"<="+maxValue+")";
                    }
                }
                else if(maxValue == null)
                {
                    if(!isNaN(minValue))
                    {
                        var value = "("+dbname+">="+minValue+")";
                    }
                }
                else if( !isNaN(minValue) && !isNaN(maxValue) )
                {
                    var value = "(("+dbname+">="+minValue+") AND ("+dbname+"<="+maxValue+"))";
                }
                if(result=="")
                {
                    result = value;
                }
                else
                {
                    result = result +conjunction+value;
                }
            }

        });

        //console.log(result);

        return result;
    }
});



OMAR.views.GeneralQueryView = Backbone.View.extend({
    el: "#generalQueryId",
    initialize: function (params) {
        this.niirsCheckboxId = $("#niirsCheckboxId");
        this.niirsMinId      = $("#niirsMinId");
        this.niirsMaxId      = $("#niirsMaxId");

        this.azimuthAngleCheckboxId = $("#azimuthAngleCheckboxId");
        this.azimuthAngleMinId      = $("#azimuthAngleMinId");
        this.azimuthAngleMaxId      = $("#azimuthAngleMaxId");

        this.grazingAngleCheckboxId = $("#grazingAngleCheckboxId");
        this.grazingAngleMinId      = $("#grazingAngleMinId");
        this.grazingAngleMaxId      = $("#grazingAngleMaxId");

        this.sunAzimuthCheckboxId = $("#sunAzimuthCheckboxId");
        this.sunAzimuthMinId      = $("#sunAzimuthMinId");
        this.sunAzimuthMaxId      = $("#sunAzimuthMaxId");

        this.sunElevationCheckboxId  = $("#sunElevationCheckboxId");
        this.sunElevationMinId       = $("#sunElevationMinId");
        this.sunElevationMaxId       = $("#sunElevationMaxId");

        this.cloudCoverageCheckboxId = $("#cloudCoverageCheckboxId");
        this.cloudCoverageMaxId      = $("#cloudCoverageMaxId");

        if (params.generalQueryMode)
        {
            this.model = new OMAR.models.GeneralQueryModel(params.generalQueryModel);
        }
        else
        {
            this.model = new OMAR.models.GeneralQueryModel();
        }


        var thisPtr = this;
        $(this.niirsCheckboxId).change(function(){
            thisPtr.model.set("niirsCheckbox", $(thisPtr.niirsCheckboxId).is(':checked'));
        });
        $(this.niirsMinId).change(function(){
            thisPtr.model.set("niirsMinValue", parseFloat($(thisPtr.niirsMinId).val()));
        });
        $(this.niirsMaxId).change(function(){
            thisPtr.model.set("niirsMaxValue", parseFloat($(thisPtr.niirsMaxId).val()));
        });

        $(this.azimuthAngleCheckboxId).change(function(){
            thisPtr.model.set("azimuthAngleCheckbox", $(thisPtr.azimuthAngleCheckboxId).is(':checked'));
        });
        $(this.azimuthAngleMinId).change(function(){
            thisPtr.model.set("azimuthAngleMinValue", parseFloat($(thisPtr.azimuthAngleMinId).val()));
        });
        $(this.azimuthAngleMaxId).change(function(){
            thisPtr.model.set("azimuthAngleMaxValue", parseFloat($(thisPtr.azimuthAngleMaxId).val()));
        });

        $(this.grazingAngleCheckboxId).change(function(){
            thisPtr.model.set("grazingAngleCheckbox", $(thisPtr.grazingAngleCheckboxId).is(':checked'));
        });
        $(this.grazingAngleMinId).change(function(){
            thisPtr.model.set("grazingAngleMinValue", parseFloat($(thisPtr.grazingAngleMinId).val()));
        });
        $(this.grazingAngleMaxId).change(function(){
            thisPtr.model.set("grazingAngleMaxValue", parseFloat($(thisPtr.grazingAngleMaxId).val()));
        });


        $(this.sunAzimuthCheckboxId).change(function(){
            thisPtr.model.set("sunAzimuthCheckbox", $(thisPtr.sunAzimuthCheckboxId).is(':checked'));
        });
        $(this.sunAzimuthMinId).change(function(){
            thisPtr.model.set("sunAzimuthMinValue", parseFloat($(thisPtr.sunAzimuthMinId).val()));
        });
        $(this.sunAzimuthMaxId).change(function(){
            thisPtr.model.set("sunAzimuthMaxValue", parseFloat($(thisPtr.sunAzimuthMaxId).val()));
        });


        $(this.sunElevationCheckboxId).change(function(){
            thisPtr.model.set("sunElevationCheckbox", $(thisPtr.sunElevationCheckboxId).is(':checked'));
        });
        $(this.sunElevationMinId).change(function(){
            thisPtr.model.set("sunElevationMinValue", parseFloat($(thisPtr.sunElevationMinId).val()));
        });
        $(this.sunElevationMaxId).change(function(){
            thisPtr.model.set("sunElevationMaxValue", parseFloat($(thisPtr.sunElevationMaxId).val()));
        });


        $(this.cloudCoverageCheckboxId).change(function(){
            thisPtr.model.set("cloudCoverageCheckbox", $(thisPtr.cloudCoverageCheckboxId).is(':checked'));
        });
        $(this.cloudCoverageMaxId).change(function(){
            thisPtr.model.set("cloudCoverageMaxValue", parseFloat($(thisPtr.cloudCoverageMaxId).val()));
        });
    },
    toCql:function(){
        this.viewToModel();

        return this.model.toCql()
    },
    viewToModel:function()
    {
        this.model.attributes.niirsCheckbox = $(this.niirsCheckbox).is(':checked');
        this.model.attributes.niirsMinValue = parseFloat($(this.niirsMinId).val());
        this.model.attributes.niirsMaxValue = parseFloat($(this.niirsMaxId).val());

        this.model.attributes.azimuthAngleCheckbox = $(this.azimuthCheckboxId).is(':checked');
        this.model.attributes.azimuthAngleMinValue = parseFloat($(this.azimuthMinId).val());
        this.model.attributes.azimuthAngleMaxValue = parseFloat($(this.azimuthMaxId).val());

        this.model.attributes.grazingAngleCheckbox = $(this.grazingAngleCheckboxId).is(':checked');
        this.model.attributes.grazingAngleMinValue = parseFloat($(this.grazingAngleMinId).val());
        this.model.attributes.grazingAngleMaxValue = parseFloat($(this.grazingAngleMaxId).val());

        this.model.attributes.sunAzimuthCheckbox  = $(this.sunZimuthCheckboxId).is(':checked');
        this.model.attributes.sunAzimuthMinValue  = parseFloat($(this.sunAzimuthMinId).val());
        this.model.attributes.sunAzimuthMinValue  = parseFloat($(this.sunAzimuthMaxId).val());

        this.model.attributes.sunElevationCheckbox  = $(this.sunElevationCheckboxId).is(':checked');
        this.model.attributes.sunElevationMinValue  = parseFloat($(this.sunElevationMinId).val());
        this.model.attributes.sunElevationMinValue  = parseFloat($(this.sunElevationMaxId).val());

        this.model.attributes.cloudCoverageCheckbox  = $(this.cloudCoverageCheckboxId).is(':checked');
        this.model.attributes.cloudCoverageMaxValue  = parseFloat($(this.cloudCoverageMaxId).val());

    }

});

