/**
 * Created with IntelliJ IDEA.
 * User: gpotts
 * Date: 1/14/13
 * Time: 10:48 AM
 * To change this template use File | Settings | File Templates.
 */
OMAR.models.EllipsoidModel = Backbone.Model.extend({
    defaults:{
        A:6378137.0 //major axis
        ,B:6356752.314245 //minor axis
    },
    initialize:function(params){
        this.A2 = this.attributes.A*this.attributes.A;
        this.B2 = this.attributes.B*this.attributes.B;
        this.flattening = (this.attributes.A - this.attributes.B)/this.attributes.A;
        this.e2 = 2*this.flattening - this.flattening*this.flattening;

        this.bind("change", this.paramsChanged, this);
    },
    paramsChanged:function(params){
        this.A2 = this.attributes.A*this.attributes.A;
        this.B2 = this.attributes.B*this.attributes.B;
        this.flattening = (this.attributes.A - this.attributes.B)/this.attributes.A;
        this.e2 = 2*this.flattening - this.flattening*this.flattening;
    },
    getGeodeticRadius : function(lat)
    {
        var PI180 = Math.PI/180.0;
        var latPi180 = lat*PI180;
        var cos_lat = Math.cos(latPi180);
        var sin_lat = Math.sin(latPi180);
        var cos2_lat = cos_lat*cos_lat;
        var sin2_lat = sin_lat*sin_lat;
        var a2_cos = this.A2*cos_lat;
        var b2_sin = this.B2*sin_lat;

        return Math.sqrt( ( (a2_cos*a2_cos) + (b2_sin*b2_sin) )/
                          (this.A2*cos2_lat + this.B2*sin2_lat));
    },
    getMetersPerDegree:function(lat){
        var result = {x:0.0,y:0.0}
        var PI180 = Math.PI/180.0;
        var radius = this.getGeodeticRadius(lat);

        result.y =  PI180 * radius;
        result.x =  result.y * Math.cos(lat*PI180);

        return result;
    },
    getDegreesPerMeter:function(lat){
        var result = this.getMetersPerDegree(lat);

        result.x = 1.0/result.x;
        result.y = 1.0/result.y;

        return result;
    }
})