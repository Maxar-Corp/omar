OMAR.models.WfsModel = Backbone.Model.extend({
    defaults:{
        "url":"/omar/wfs",
        "service":"WFS",
        "version":"1.0.0",
        "request":"getFeature",
        "typeName":"omar:raster_entry",
        "filter":"",
        "outputFormat":"JSON",
        "maxFeatures":"",
        "offset":"",
        "resultType":"",
        "sortBy":"",// json formatted array of arrays

        /* These attributes will be set after the fetch */
        "numberOfFeatures":0,
        "getFeatureResult":{}
    },
    initialize:function(params){
       // if(params&&params.filter)
       // {
       //     filter = params.filter;
       // }

    },
    toUrlParams: function(includeNullPropertiesFlag){
        var result = ""

        if(!includeNullPropertiesFlag) includeNullPropertiesFlag = false
        for(var propertyName in this.attributes)
        {
            switch(propertyName)
            {
                case "url":
                case "numberOfFeatures":
                case "getFeatureResult":
                    break;
                default:
                    var value = this.get(propertyName);
                    if(value == null)
                    {
                        value = "";
                    }
                    if(((value==="")&&includeNullPropertiesFlag) || !(value===""))
                    {

                        if(result)
                        {
                            result = result +"&"+propertyName + "=" + encodeURIComponent(value);//escape(value);
                        }
                        else
                        {
                            result = propertyName + "=" + encodeURIComponent(value);//escape(value);
                        }
                    }
                    break;
            }
        }
        return result;
    },
    parse:function(response){
        //alert("response = " + JSON.stringify(response) );

        var result = this.attributes;

        if(response.numberOfFeatures)
        {
            result.numberOfFeatures = response.numberOfFeatures;
        }
        else{
            result.getFeatureResult = response;
        }
        //alert(result);
        return result;
    },
    toUrl:function(includeNullPropertiesFlag){
        var result =this.toUrlParams(includeNullPropertiesFlag);
        var url = this.get("url");
        if(url&&(url!=""))
        {
            result = (url + "?" + result);
        }

        return result;
    },
    fetchCount:function(){
        if(this.fetchCountAjax&&(this.fetchCountAjax.readState !=4))
        {
            this.fetchCountAjax.abort();
        }
        var thisPtr = this;

        var countClone = this.clone();
        countClone.attributes.numberOfFeatures = 0;
        countClone.attributes.getFeatureResult = "";
        countClone.attributes.resultType = "hits";
        countClone.url = countClone.toUrl()+"&callback=?";
        this.fetchCountAjax = countClone.fetch(
            {cache:false,
                "success":function(){
                    thisPtr.attributes.numberOfFeatures = countClone.attributes.numberOfFeatures;
                    thisPtr.trigger("onNumberOfFeaturesChange", thisPtr);
                    thisPtr.fetchCounAjax = null;
                }
            });
        return this.fetchCountAjax;
    },
    fetchGetFeatureResult:function(){
        if(this.fetchGetFeatureAjax&&this.fetchGetFeatureAjax.abort)
        {
            this.fetchGetFeatureAjax.abort();
        }
        var thisPtr = this;
        var countClone = this.clone();
        countClone.attributes.numberOfFeatures = -1;
        countClone.attributes.getFeatureResult = ""
        countClone.attributes.resultType = "hits";
        countClone.url = countClone.toUrl()+"&callback=?";
        this.fetchGetFeatureAjax = countClone.fetch(
            {cache:false,
                "success":function(){
                    thisPtr.attributes.getFeatureResult = countClone.attributes.getFeatureResult;
                    thisPtr.trigger("onGetFeatureResultChange");
                    thisPtr.fetchGetFeatureAjax = null;
                }
            });
    }
});