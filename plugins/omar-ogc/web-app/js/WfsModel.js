OMAR.models.Wfs = Backbone.Model.extend({
    defaults:{
        "url":"/omar/wfs",
        "service":"WFS",
        "version":"1.1.0",
        "request":"getFeature",
        "typeName":"raster_entry",
        "filter":"",
        "outputFormat":"JSON",
        "maxFeatures":"",
        "offset":"",
        "resultType":"",
        "sort":""// json formatted array of arrays
    },
    initialize:function(params){
    },
    toUrlParams: function(includeNullPropertiesFlag){
        var result = ""

        if(!includeNullPropertiesFlag) includeNullPropertiesFlag = false
        for(var propertyName in this.attributes)
        {
            if(propertyName!="url")
            {
                var value = this.get(propertyName);
                if(value == null)
                {
                    value = "";
                }
                if(((value==="")&&includeNullPropertiesFlag) || !(value===""))
                {

                    if(result)
                    {
                        result = result +"&"+propertyName + "=" + escape(value);
                    }
                    else
                    {
                        result = propertyName + "=" + escape(value);
                    }
                }
            }
        }
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
    }
});