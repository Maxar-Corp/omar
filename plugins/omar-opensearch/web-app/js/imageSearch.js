/**
 * Created by gpotts on 8/11/15.
 */
var WfsTypeNameModel = Backbone.Model.extend({
    idAttribute:"typeName",
    defaults:{
        typeName:"omar:raster_entry"
    },
    initialize:function(params)
    {
    }
});

OMAR.views.imageSearch = Backbone.View.extend({
    el:"#ResultsView",
    initialize:function(params)
    {

        this.wfsModel = null;

        if(params&&params.cqlFilter)
        {
            this.wfsModel = new OMAR.models.WfsModel({sortBy:"acquisition_date+D", "filter":params.cqlFilter});
        }
        else
        {
            this.wfsModel = new OMAR.models.WfsModel();
        }
        this.converter = new  CoordinateConversion();

        this.wfsTypeName = new WfsTypeNameModel();
        this.dataModelView = new OMAR.views.DataModelView({
            wfsTypeNameModel:this.wfsTypeName,
            el:"#ResultsView",
            containerEl:"#ResultsView",
            wfsModel:this.wfsModel
        });
        console.log(params);
        if(params&&params.pointRadius)
        {
            this.setPointRadiusSearch(params.pointRadius);
        }
    },


    setPointRadiusSearch:function(value)
    {
        var matchedArray = null;
        var pointModel   = null;
        var lat
        var lon
        var radius = "0.0";
        if(value)
        {
            matchedArray = value.match(OMAR.regexp.ddrRegExp);
            if(!matchedArray) matchedArray = value.match(OMAR.regexp.ddRegExp);
            if(matchedArray)
            {
                console.log("matched DD");

                var deg1    = matchedArray[1];
                var deg1Dir = matchedArray[2];
                var deg2    = matchedArray[3];
                var deg2Dir = matchedArray[4];

                if(deg1&&deg2)
                {
                    deg1 = parseFloat(deg1);
                    deg2 = parseFloat(deg2);

                    if(deg1Dir&&deg2Dir)
                    {
                        if(deg1Dir == "N" || deg1Dir == "n")
                        {
                            lat = deg1;
                        }
                        else if(deg1Dir == "S"||deg1Dir == "s")
                        {
                            lat = -deg1;
                        }
                        if(deg2Dir == "E" || deg2Dir == "e")
                        {
                            lon = deg2;
                        }
                        else if(deg2Dir == "W" || deg2Dir == "w")
                        {
                            lon = -deg2;
                        }
                    }
                    else
                    {
                        lat = deg1;
                        lon = deg2;
                    }

                }

                if(matchedArray[5]) radius = matchedArray[5];
                console.log("SETTING TO DECIMAL DEGREES RADIUS");
            }
            else
            {
                matchedArray = value.match(OMAR.regexp.dmsrRegExp);
                if(!matchedArray) matchedArray = value.match(OMAR.regexp.dmsrRegExp);

                if(matchedArray)
                {
                    var lat;
                    var lon;
                    var radius = "0.0";
                    console.log("SETTING TO DMS RADIUS\n"+matchedArray);
                    if(matchedArray)
                    {
                        lat = convert.dmsToDd(matchedArray[1], matchedArray[2], matchedArray[3] + matchedArray[4], matchedArray[5]);
                        lon = convert.dmsToDd(matchedArray[6], matchedArray[7], matchedArray[8] + matchedArray[9], matchedArray[10]);
                    }
                    if(matchedArray[11])
                    {
                        radius = matchedArray[11];
                    }

                    lat    = parseFloat(lat);
                    lon    = parseFloat(lon);
                    radius = parseFloat(radius);
                }
                else
                {

                    matchedArray = value.match(OMAR.regexp.mgrsrRegExp);
                    if(!matchedArray) matchedArray =value.match(OMAR.regexp.mgrsRegExp);
                    var mgrs;
                    var radius = 0;
                    if(matchedArray)
                    {
                        console.log("MGRS RADIUS\n"+matchedArray);
                        mgrs = convert.mgrsToDd(matchedArray[1], matchedArray[2], matchedArray[3], matchedArray[4], matchedArray[5], matchedArray[6]);
                        if(matchedArray[7]) radius = matchedArray[7];
                    }
                    if(mgrs)
                    {
                        var match2 = OMAR.regexp.ddRegExp.exec(mgrs);
                        lat = match2[1] + match2[2];
                        lon = match2[3] + match2[4];
                    }
                }
            }
            if((lat!=null)&&(lon!=null))
            {
                lat = parseFloat(lat);
                lon = parseFloat(lon);
                if(radius) radius = parseFloat(radius);
                pointModel = new OMAR.models.PointModel({x:lon,y:lat,radius:radius});
            }
            if(pointModel)
            {
                console.log(pointModel.toCql("ground_geom"));
                this.wfsModel.set("filter", pointModel.toCql("ground_geom"));
            }
        }

    },
    render:function()
    {
       // this.dataModelView.render();
        this.dataModelView.wfsUrlChanged();

    }
});

OMAR.imageSearch = null;
OMAR.pages.imageSearch = (function($, params){
    if(!OMAR.imageSearch)
    {
        OMAR.imageSearch = new OMAR.views.imageSearch(params);
    }
    else
    {

    }
    return OMAR.imageSearch;
});

$(document).ready(function () {
    $.ajaxSetup({cache: false}); // turn cache off for ajax

    if(!OMAR.imageSearch) {
        init();
    }
    $('body').layout({
        north__resizable: false
        ,north__closable:  false
        ,north__pane_spacing: 0
        ,south__resizable: false
        ,south__closable:  false
        ,south__pane_spacing: 0
    });
    $("body").css({"visibility":"visible"});
    $( window ).resize(function() {
        OMAR.imageSearch.dataModelView.resizeView();
    });
    OMAR.imageSearch.dataModelView.resizeView();
});



