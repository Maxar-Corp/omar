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
        missionComparator:"iContains",
        sensorCheckbox:false,
        sensorValue:"",
        sensorComparator:"iContains",
        beNumberCheckbox:false,
        beNumberValue:"",
        beNumberComparator:"iContains",
        targetCheckbox:false,
        targetValue:"",
        targetComparator:"iContains",
        wacCheckbox:false,
        wacValue:"",
        wacComparator:"iContains"
    },
    initialize: function (params) {

        if (params)
        {
            if(params.niirsCheckbox) this.attributes.niirsCheckbox = params.niirsCheckbox;
            if(params.niirsMinValue) this.attributes.niirsMinValue = params.niirsMinValue;
            if(params.niirsMinValue) this.attributes.niirsMinValue = params.niirsMinValue;

            if(params.azimuthAngleCheckbox) this.attributes.azimuthAngleCheckbox = params.azimuthAngleCheckbox;
            if(params.azimuthAngleMinValue) this.attributes.azimuthAngleMinValue = params.azimuthAngleMinValue;
            if(params.azimuthAngleMaxValue) this.attributes.azimuthAngleMaxValue = params.azimuthAngleMaxValue;

            if(params.grazingAngleCheckbox) this.attributes.grazingAngleCheckbox = params.grazingAngleCheckbox;
            if(params.grazingAngleMinValue) this.attributes.grazingAngleMinValue = params.grazingAngleMinValue;
            if(params.grazingAngleMaxValue) this.attributes.grazingAngleMaxValue = params.grazingAngleMaxValue;

            if(params.sunAzimuthCheckbox) this.attributes.sunAzimuthCheckbox = params.sunAzimuthCheckbox;
            if(params.sunAzimuthMinValue) this.attributes.sunAzimuthMinValue = params.sunAzimuthMinValue;
            if(params.sunAzimuthMaxValue) this.attributes.sunAzimuthMaxValue = params.sunAzimuthMaxValue;

            if(params.sunElevationCheckbox) this.attributes.sunElevationCheckbox = params.sunElevationCheckbox;
            if(params.sunElevationMinValue) this.attributes.sunElevationMinValue = params.sunElevationMinValue;
            if(params.sunElevationMaxValue) this.attributes.sunElevationMaxValue = params.sunElevationMaxValue;

            if(params.cloudCoverageCheckbox) this.attributes.cloudCoverageCheckbox = params.cloudCoverageCheckbox;
            if(params.cloudCoverageMaxValue) this.attributes.cloudCoverageMaxValue = params.cloudCoverageMaxValue;

            if(params.missionCheckbox)   this.attributes.missionCheckbox   = params.missionCheckbox;
            if(params.missionValue)      this.attributes.missionValue      = params.missionValue;
            if(params.missionComparator) this.attributes.missionComparator = params.missionComparator;

            if(params.sensorCheckbox) this.attributes.sensorCheckbox = params.sensorCheckbox;
            if(params.sensorValue) this.attributes.sensorValue = params.sensorValue;
            if(params.sensorComparator) this.attributes.sensorComparator = params.sensorValue;

            if(params.beNumberCheckbox) this.attributes.beNumberCheckbox = params.beNumberCheckbox;
            if(params.beNumberValue) this.attributes.beNumberValue = params.beNumberValue;
            if(params.beNumberComparator) this.attributes.beNumberComparator = params.beNumberComparator;

            if(params.targetCheckbox) this.attributes.targetCheckbox = params.targetCheckbox;
            if(params.targetValue) this.attributes.targetValue = params.targetValue;
            if(params.targetComparator) this.attributes.targetComparator = params.targetComparator;

            if(params.wacCheckbox) this.attributes.wacCheckbox = params.wacCheckbox;
            if(params.wacValue) this.attributes.wacValue = params.wacValue;
            if(params.wacComparator) this.attributes.wacComparator = params.wacComparator;
        }
    },
    reset: function()
    {
        this.attributes = new OMAR.models.GeneralQueryModel().attributes;
        this.trigger("change");
    },
    toCql: function (errors)
    {
        var dbAttributes = [
            {name:"niirs",dbname:"niirs"},
            {name:"azimuthAngle", dbname:"azimuth_angle"},
            {name:"grazingAngle", dbname:"grazing_angle"},
            {name:"sunElevation", dbname:"sun_elevation"},
            {name:"sunAzimuth", dbname:"sun_azimuth"},
            {name:"cloudCoverage", dbname:"cloud_cover"},
            {name:"mission",dbname:"mission_id"},
            {name:"sensor",dbname:"sensor_id"},
            {name:"beNumber", dbname:"be_number"},
            {name:"target", dbname:"target_id"},
            {name:"wac", dbname:"wac_code"}
            ]
        var result = "";
        var conjunction = " AND ";
        var thisPtr = this;

        $(dbAttributes).each(function(idx, obj){
            var checked  = thisPtr.get(obj.name+"Checkbox");
            var dbname   = obj.dbname;
            var minValue = thisPtr.get(obj.name+"MinValue");
            var maxValue = thisPtr.get(obj.name+"MaxValue");
            var value    = thisPtr.get(obj.name+"Value");
            var comparator= thisPtr.get(obj.name+"Comparator");
            var temp = "";
            if(checked)
            {
                if(value != null)
                {
                    if(comparator)
                    {
                        switch(comparator.toLowerCase())
                        {
                            case "contains":
                                temp = "("+dbname+" LIKE '%" + value + "%')";
                                break;
                            case "icontains":
                                temp = "("+dbname+" ILIKE '%" + value + "%')";
                                break;
                            case "startswith":
                                temp = "("+dbname+" LIKE '" + value + "%')";
                                break;
                            case "endswith":
                                temp = "("+dbname+" LIKE '%" + value + "')";
                                break;
                            case "istartswith":
                                temp = "("+dbname+" ILIKE '" + value + "%')";
                                break;
                            case "iendswith":
                                temp = "("+dbname+" ILIKE '%" + value + "')";
                                break;
                            case "equals":
                                temp = "("+dbname+" = '" + value + "')";
                                break;
                            case "notequals":
                                temp = "("+dbname+" <> '" + value + "')";
                                break;
                        }
                    }
                    else
                    {
                        temp = "("+dbname+" ILIKE '%" + value + "%')";
                    }
                }
                else if(minValue == null)
                {
                    if(!isNaN(maxValue))
                    {
                        temp = "("+dbname+"<="+maxValue+")";
                    }
                }
                else if(maxValue == null)
                {
                    if(!isNaN(minValue))
                    {
                        temp = "("+dbname+">="+minValue+")";
                    }
                }
                else if( !isNaN(minValue) && !isNaN(maxValue) )
                {
                    temp = "(("+dbname+">="+minValue+") AND ("+dbname+"<="+maxValue+"))";
                }

                if(result=="")
                {
                    result = temp;
                }
                else
                {
                    result = result +conjunction+temp;
                }
            }

        });

        //alert(result);
       // console.log(result);

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

        this.missionCheckboxId   = $("#missionCheckboxId");
        this.missionId           = $("#missionId");
        this.missionComparatorId = $("#missionComparatorId");

        this.sensorCheckboxId   = $("#sensorCheckboxId");
        this.sensorId           = $("#sensorId");
        this.sensorComparatorId = $("#sensorComparatorId");

        this.beNumberCheckboxId   = $("#beNumberCheckboxId");
        this.beNumberId           = $("#beNumberId");
        this.beNumberComparatorId = $("#beNumberComparatorId");

        this.targetCheckboxId   = $("#targetCheckboxId");
        this.targetId           = $("#targetId");
        this.targetComparatorId = $("#targetComparatorId");

        this.wacCheckboxId    = $("#wacCheckboxId");
        this.wacId            = $("#wacId");
        this.wacComparatorId  = $("#wacComparatorId");

        this.resetButtonId = $("#GeneralQueryResetButtonId")
        if (params.generalQueryMode)
        {
            this.model = new OMAR.models.GeneralQueryModel(params.generalQueryModel.atrributes);
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

        $(this.missionCheckboxId).change(function(){
            thisPtr.model.set("missionCheckbox", $(thisPtr.missionCheckboxId).is(':checked'));
        });
        $(this.missionId).change(function(){
            thisPtr.model.set("missionValue", $(thisPtr.missionId).val());
        });
        $(this.missionComparatorId).change(function(){
            thisPtr.model.set("missionComparator", $(thisPtr.missionComparatorId).val());
        });

        $(this.beNumberCheckboxId).change(function(){
            thisPtr.model.set("beNumberCheckbox", $(thisPtr.beNumberCheckboxId).is(':checked'));
        });
        $(this.beNumberId).change(function(){
            thisPtr.model.set("beNumberValue", $(thisPtr.beNumberId).val());
        });
        $(this.beNumberComparatorId).change(function(){
            thisPtr.model.set("beNumberComparator", $(thisPtr.beNumberComparatorId).val());
        });

        $(this.targetCheckboxId).change(function(){
            thisPtr.model.set("targetCheckbox", $(thisPtr.targetCheckboxId).is(':checked'));
        });
        $(this.targetId).change(function(){
            thisPtr.model.set("targetValue", $(thisPtr.targetId).val());
        });
        $(this.targetComparatorId).change(function(){
            thisPtr.model.set("targetComparator", $(thisPtr.targetComparatorId).val());
        });

        $(this.sensorCheckboxId).change(function(){
            thisPtr.model.set("sensorCheckbox", $(thisPtr.sensorCheckboxId).is(':checked'));
        });
        $(this.sensorId).change(function(){
            thisPtr.model.set("sensorValue", $(thisPtr.sensorId).val());
        });
        $(this.sensorComparatorId).change(function(){
            thisPtr.model.set("sensorComparator", $(thisPtr.sensorComparatorId).val());
        });


        $(this.wacCheckboxId).change(function(){
            thisPtr.model.set("wacCheckbox", $(thisPtr.wacCheckboxId).is(':checked'));
        });
        $(this.wacId).change(function(){
            thisPtr.model.set("wacValue", $(thisPtr.wacId).val());
        });
        $(this.wacComparatorId).change(function(){
            thisPtr.model.set("wacComparator", $(thisPtr.wacComparatorId).val());
        });

        $(this.resetButtonId).click(function(){
            thisPtr.reset()
        })
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

        this.model.attributes.azimuthAngleCheckbox = $(this.azimuthAngleCheckboxId).is(':checked');
        this.model.attributes.azimuthAngleMinValue = parseFloat($(this.azimuthAngleMinId).val());
        this.model.attributes.azimuthAngleMaxValue = parseFloat($(this.azimuthAngleMaxId).val());

        this.model.attributes.grazingAngleCheckbox = $(this.grazingAngleCheckboxId).is(':checked');
        this.model.attributes.grazingAngleMinValue = parseFloat($(this.grazingAngleMinId).val());
        this.model.attributes.grazingAngleMaxValue = parseFloat($(this.grazingAngleMaxId).val());

        this.model.attributes.sunAzimuthCheckbox  = $(this.sunAzimuthCheckboxId).is(':checked');
        this.model.attributes.sunAzimuthMinValue  = parseFloat($(this.sunAzimuthMinId).val());
        this.model.attributes.sunAzimuthMinValue  = parseFloat($(this.sunAzimuthMaxId).val());

        this.model.attributes.sunElevationCheckbox  = $(this.sunElevationCheckboxId).is(':checked');
        this.model.attributes.sunElevationMinValue  = parseFloat($(this.sunElevationMinId).val());
        this.model.attributes.sunElevationMinValue  = parseFloat($(this.sunElevationMaxId).val());

        this.model.attributes.cloudCoverageCheckbox  = $(this.cloudCoverageCheckboxId).is(':checked');
        this.model.attributes.cloudCoverageMaxValue  = parseFloat($(this.cloudCoverageMaxId).val());

        this.model.attributes.missionCheckbox = $(this.missionCheckboxId).is(':checked');
        this.model.attributes.missionValue    = $(this.missionId).val();
        this.model.attributes.missionComparatorValue    = $(this.missionComparatorId).val();

        this.model.attributes.beNumberCheckbox = $(this.beNumberCheckboxId).is(':checked');
        this.model.attributes.beNumberValue    = $(this.beNumberId).val();
        this.model.attributes.beNumberComparator = $(this.beNumberComparatorId).val();

        this.model.attributes.targetCheckbox = $(this.targetCheckboxId).is(':checked');
        this.model.attributes.targetValue    = $(this.targetId).val();
        this.model.attributes.targetComparator    = $(this.targetComparatorId).val();

        this.model.attributes.wacCheckbox   = $(this.wacCheckboxId).is(':checked');
        this.model.attributes.wacValue      = $(this.wacId).val();
        this.model.attributes.wacComparator = $(this.wacComparatorId).val();
    },
    setModel:function(model)
    {
        this.model = model;

        this.render();
    },
    reset:function(){
        this.model.reset();
        this.render();
        //this.model.setAttributes(new OMAR.models.GeneralQueryModel().attributes)
      //this.setModel(new OMAR.models.GeneralQueryModel());
    },
    render:function()
    {
        $(this.niirsCheckboxId).prop("checked", this.model.get("niirsCheckbox"));
        $(this.niirsMinId).val(this.model.get("niirsMinValue").toString());
        $(this.niirsMaxId).val(this.model.get("niirsMaxValue").toString());

        $(this.azimuthAngleCheckboxId).prop("checked", this.model.get("azimuthAngleCheckbox"));
        $(this.azimuthAngleMinId).val(this.model.get("azimuthAngleMinValue").toString());
        $(this.azimuthAngleMaxId).val(this.model.get("azimuthAngleMaxValue").toString());

        $(this.grazingAngleCheckboxId).prop("checked", this.model.get("grazingAngleCheckbox"));
        $(this.grazingAngleMinId).val(this.model.get("grazingAngleMinValue").toString());
        $(this.grazingAngleMaxId).val(this.model.get("grazingAngleMaxValue").toString());

        $(this.sunAzimuthCheckboxId).prop("checked", this.model.get("sunAzimuthCheckbox"));
        $(this.sunAzimuthMinId).val(this.model.get("sunAzimuthMinValue").toString());
        $(this.sunAzimuthMaxId).val(this.model.get("sunAzimuthMaxValue").toString());

        $(this.sunElevationCheckboxId).prop("checked", this.model.get("sunElevationCheckbox"));
        $(this.sunElevationMinId).val(this.model.get("sunElevationMinValue").toString());
        $(this.sunElevationMaxId).val(this.model.get("sunElevationMaxValue").toString());

        $(this.cloudCoverageCheckboxId).prop("checked", this.model.get("cloudCoverageCheckbox"));
        $(this.cloudCoverageMaxId).val(this.model.get("cloudCoverageMaxValue").toString());

        $(this.missionCheckboxId).prop("checked", this.model.get("missionCheckbox"));
        $(this.missionId).val(this.model.get("missionValue").toString());
        $(this.missionComparatorId).val(this.model.get("missionComparator").toString());

        $(this.sensorCheckboxId).prop("checked", this.model.get("sensorCheckbox"));
        $(this.sensorId).val(this.model.get("sensorValue").toString());
        $(this.sensorComparatorId).val(this.model.get("sensorComparator").toString());

        $(this.beNumberCheckboxId).prop("checked", this.model.get("beNumberCheckbox"));
        $(this.beNumberId).val(this.model.get("beNumberValue").toString());
        $(this.beNumberComparatorId).val(this.model.get("beNumberComparator").toString());

        $(this.targetCheckboxId).prop("checked", this.model.get("targetCheckbox"));
        $(this.targetId).val(this.model.get("targetValue").toString());
        $(this.targetComparatorId).val(this.model.get("targetComparator").toString());

        $(this.wacCheckboxId).prop("checked", this.model.get("wacCheckbox"));
        $(this.wacId).val(this.model.get("wacValue").toString());
        $(this.wacComparatorId).val(this.model.get("wacComparator").toString());
    }


});

