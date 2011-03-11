var Ajax;
if (Ajax && (Ajax != null)) {
	Ajax.Responders.register({
	  onCreate: function() {
        if($('spinner') && Ajax.activeRequestCount>0)
          Effect.Appear('spinner',{duration:0.5,queue:'end'});
	  },
	  onComplete: function() {
        if($('spinner') && Ajax.activeRequestCount==0)
          Effect.Fade('spinner',{duration:0.5,queue:'end'});
	  }
	});
}

Function.prototype.defaults = function()
{
  var _f = this;
  var _a = Array(_f.length-arguments.length).concat(
           Array.prototype.slice.apply(arguments));
  return function()
  {
    return _f.apply(_f, Array.prototype.slice.apply(arguments).concat(
      _a.slice(arguments.length, _a.length)));
  }
}


/**
 * The inheritance example here which includes the swiss and inherits and this function were taken
 * from an online example written by Douglas Crockford.
 * 
 * http://www.crockford.com/javascript/inheritance.html
 */
Function.prototype.method = function (name, func) {
    this.prototype[name] = func;
    return this;
};

Function.method('inherits', function (parent) {
    var d = {}, p = (this.prototype = new parent());
    this.method('uber', function uber(name) {
        if (!(name in d)) {
            d[name] = 0;
        }        
        var f, r, t = d[name], v = parent.prototype;
        if (t) {
            while (t) {
                v = v.constructor.prototype;
                t -= 1;
            }
            f = v[name];
        } else {
            f = p[name];
            if (f == this[name]) {
                f = v[name];
            }
        }
        d[name] += 1;
        r = f.apply(this, Array.prototype.slice.apply(arguments, [1]));
        d[name] -= 1;
        return r;
    });
    return this;
});

Function.method('swiss', function (parent) {
    for (var i = 1; i < arguments.length; i += 1) {
        var name = arguments[i];
        this.prototype[name] = parent.prototype[name];
    }
    return this;
});

function OmarUrlParams(){
	this.toUrlParams = function(includeNullPropertiesFlag){
		result = ""
		if(!includeNullPropertiesFlag) includeNullPropertiesFlag = false
		for(propertyName in this)
		{
			if(!(this[propertyName] instanceof Function))
			{
				value = this[propertyName];
				if(value == null)
				{
				   value = "";
				}
				if(((value==="")&&includeNullPropertiesFlag) || !(value===""))
				{
                   if(value instanceof Array)
                   {
                      if(value.length > 0)
                      {
                          idx = 0;
                          while(idx < value.length)
                          {
                              arrayValue = value[idx];
                              if((arrayValue == "null")||(arrayValue == null))
                              {
                                 arrayValue = "";
                              }
                              if(((arrayValue==="")&&includeNullPropertiesFlag) || !(arrayValue===""))
                              {
                                  if(result)
                                  {
                                     result = result +"&"+propertyName+"["+idx+"]" + "=" + escape(arrayValue);
                                  }
                                  else
                                  {
                                     result = propertyName+"["+idx+"]" + "=" + escape(arrayValue);
                                  }
                              }
                              ++idx;
                          }
                      }
                   }
                   else
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
		}
		return result;
	}
	this.toJson = function()
    {
        result = {}
        for(propertyName in this)
        {
            if(!(this[propertyName] instanceof Function))
            {
                result[propertyName] = this[propertyName];
            }
        }
        return result;
    }
    this.setPropertiesFromObject = function(params)
    {
        if(!params) return this;
        for(x in params)
        {
            if(x in this)
            {
                this[x] = params[x];
            }
        }
        return this; // allow chaining
    }
    this.setPropertiesFromDocument = function(params)
    {
        if(!params) return this;
        for(x in this)
        {
            if(this[x] instanceof Array)
            {
               idx = 0;
               done = false;
               arrayElement = this[x];
               while(!done)
               {
                   element = params.getElementById(x+"["+idx+"]");
                   if(element)
                   {
                     arrayElement[idx] = element.value;
                   }
                   else
                   {
                     done = true;
                   }
                   ++idx;
               }

            }
            else
            {
                element = params.getElementById(x)
                if(element)
                {
                    this[x] = element.value;
                }
            }
        }
        return this; // allow chaining
    }
	this.setProperties = function(params)
	{
		if((typeof params) === "object")
		{
            if(params instanceof String)
            {
                this.setPropertiesFromObject(eval('(' + params + ')'));
            }
            else if(params.getElementById != null)    // check for function getElementById
            {
                this.setPropertiesFromDocument(params);
            }
            else
            {
                this.setPropertiesFromObject(params);
            }
		}
		else if((typeof params) === "string") // we will assume a json formatted string
		{
			this.setPropertiesFromObject(eval('(' + params + ')'));
		}
        else
        {
            alert("Can't set properties with type = " + (typeof params));
        }
		return this;
	}
}
function OmarImageAdjustmentUrlParams(){
	this.sharpen_mode = "";
	this.interpolation = "";
	this.brightness = "";
	this.contrast = "";
	this.stretch_mode = "";
	this.stretch_mode_region = "";
	this.quicklook = "";
	this.bands = "";
}
function OmarOgcUrlParams(){
	this.request = "";
	this.service = "";
	this.version = "";
}

function OmarWmsUrlParams(){
	this.width  = "";
	this.height = "";
	this.format = "";
	this.filter = "";
	this.srs    = "";
	this.bbox   = "";
}

function OmarWcsUrlParams(){
	this.width  = "";
	this.height = "";
	this.format = "";
	this.filter = "";
	this.crs    = "";
    this.bbox   = "";
    this.coverage   = "";
}

function OmarSearchParams(){

    this.setTimeFromDate = function(startDate, endDate)
    {
        hasStartDate = ((startDate)&&
                           (startDate.day && startDate.month && startDate.year&& startDate.hour&&startDate.minute));
        hasEndDate = ((endDate)&&
                           (endDate.day && endDate.month && endDate.year&& endDate.hour &&endDate.minute));

        endDateNoQuote = "";
        startDateNoQuote = "";

        if(hasStartDate)
        {
            startDateNoQuote = startDate.year + startDate.month.leftPad( 2 ) +
                               startDate.day.leftPad( 2 ) + 'T' + startDate.hour.leftPad( 2 ) +
                               ':' + startDate.minute.leftPad( 2 ) + ':' + '00Z';
        }
        if(hasEndDate)
        {
            endDateNoQuote = endDate.year + endDate.month.leftPad( 2 ) +
                             endDate.day.leftPad( 2 ) + 'T' + endDate.hour.leftPad( 2 ) +
                             ':' + endDate.minute.leftPad( 2 ) + ':' + '00Z';
        }

        this.time = ""
        if ( startDateNoQuote )
        {
           this.time = startDateNoQuote;
            if ( hasEndDate )
            {
                this.time += "/" + endDateNoQuote;
            }
            else
            {
                this.time += "/"
            }
        }
        else
        {
            if ( endDateNoQuote )
            {
                this.time += "/" + endDateNoQuote;
            }
            else
            {
                this.time = "";
            }
        }
        return this;
    }
    this.searchMethod = "";
    this.centerLat = "";
    this.centerLon = "";
    this.aoiRadius = "";
    this.viewMinLon = "";
    this.viewMinLat = "";
    this.viewMaxLon = "";
    this.viewMaxLat = "";
    this.aoiMinLon = "";
    this.aoiMinLat = "";
    this.aoiMaxLon = "";
    this.aoiMaxLat = "";
//    this.startDate = "";
//    this.startDate_timezone = "";
//    this.startDate_hour = "";
//    this.startDate_minute = "";
//    this.startDate_day = "";
//    this.startDate_month = "";
//    this.startDate_year = "";
//    this.endDate_timezone = "";
//    this.endDate_hour = "";
//    this.endDate_minute = "";
//    this.endDate_day   = "";
//    this.endDate_month = "";
//    this.endDate_year  = "";
//    this.endDate = "";
    this.filter        = "";
    this.max           = "";
    this.time          = "";
    this.searchTagNames = [];
    this.searchTagValues = [];
}

OmarImageAdjustmentUrlParams.inherits(OmarUrlParams);
OmarSearchParams.inherits(OmarUrlParams);
OmarOgcUrlParams.inherits(OmarImageAdjustmentUrlParams);
OmarWmsUrlParams.inherits(OmarOgcUrlParams);
OmarWcsUrlParams.inherits(OmarOgcUrlParams);
