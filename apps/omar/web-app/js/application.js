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
        alert(name);
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

function OmarParams(){
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
function OmarImageAdjustmentParams(){
	this.sharpen_mode = "";
	this.interpolation = "";
	this.brightness = "";
	this.contrast = "";
	this.stretch_mode = "";
	this.stretch_mode_region = "";
	this.quicklook = "";
	this.bands = "";
    this.rotate = "";
}
function OmarOgcParams(){
	this.request = "";
	this.service = "";
	this.version = "";
}

function OmarWmsParams(){
	this.width  = "";
	this.height = "";
	this.format = "";
	this.filter = "";
	this.srs    = "";
    this.bbox   = "";
    this.layers   = "";
    this.transparent   = "";
}

function OmarWcsParams(){
	this.width  = "";
	this.height = "";
	this.format = "";
	this.filter = "";
	this.crs    = "";
    this.bbox   = "";
    this.coverage   = "";
    this.transparent   = "";
}
function OmarImageSpaceOpenLayersParams(){
    this.res = "";
    this.x = "";
    this.y = "";
    this.z = "";
    this.tileWidth = "";
    this.tileHeight = "";
    this.id = "";
    this.view = "";
}
function OmarImageSpaceGetTileParams(){
    this.scale = "";
    this.width = "";
    this.height = "";
    this.x = "";
    this.y = "";
    this.id = "";
    this.format = "";
    this.filter = "";
    this.pivot = "";
}

function OmarSearchParams(){

    this.clearStartEndDateStructure = function()
    {
        this.startDate = "";
        this.startDate_timezone = "";
        this.startDate_hour = "";
        this.startDate_minute = "";
        this.startDate_day = "";
        this.startDate_month = "";
        this.startDate_year = "";
        this.endDate_timezone = "";
        this.endDate_hour = "";
        this.endDate_minute = "";
        this.endDate_day   = "";
        this.endDate_month = "";
        this.endDate_year  = "";
        this.endDate = "";
    }
    this.clearSpatialParams = function()
    {
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
    }
    this.initTime = function(clearDateStructureFlag)
    {
        hasStartDate = ((this.startDate)&&
                           (this.startDate_day && this.startDate_month && this.startDate_year&&
                            this.startDate_hour&&this.startDate_minute));
        hasEndDate = ((this.endDate)&&
                           (this.endDate_day && this.endDate_month && this.endDate_year&&
                            this.endDate_hour &&this.endDate_minute));

        endDateNoQuote = "";
        startDateNoQuote = "";
        if(hasStartDate)
        {
            startDateNoQuote = this.startDate_year + this.startDate_month.leftPad( 2 ) +
                               this.startDate_day.leftPad( 2 ) + 'T' + this.startDate_hour.leftPad( 2 ) +
                               ':' + this.startDate_minute.leftPad( 2 ) + ':' + '00Z';
        }
        else
        {
            this.startDate = "";
            this.startDate_timezone = "";
            this.startDate_hour = "";
            this.startDate_minute = "";
            this.startDate_day = "";
            this.startDate_month = "";
            this.startDate_year = "";
        }
        if(hasEndDate)
        {
            endDateNoQuote = this.endDate_year + this.endDate_month.leftPad( 2 ) +
                             this.endDate_day.leftPad( 2 ) + 'T' + this.endDate_hour.leftPad( 2 ) +
                             ':' + this.endDate_minute.leftPad( 2 ) + ':' + '00Z';
        }
        else
        {
            this.endDate_timezone = "";
            this.endDate_hour = "";
            this.endDate_minute = "";
            this.endDate_day   = "";
            this.endDate_month = "";
            this.endDate_year  = "";
            this.endDate = "";
        }
        this.time = ""
        if ( startDateNoQuote )
        {
           this.time = startDateNoQuote;
            if ( endDateNoQuote )
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
        if(clearDateStructureFlag)
        {
            this.clearStartEndDateStructure()
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
    this.startDate = "";
    this.startDate_timezone = "";
    this.startDate_hour = "";
    this.startDate_minute = "";
    this.startDate_day = "";
    this.startDate_month = "";
    this.startDate_year = "";
    this.endDate_timezone = "";
    this.endDate_hour = "";
    this.endDate_minute = "";
    this.endDate_day   = "";
    this.endDate_month = "";
    this.endDate_year  = "";
    this.endDate = "";
    this.filter        = "";
    this.max           = "";
    this.time          = "";
    this.spatialSearchFlag = "";
    this.searchTagNames = [];
    this.searchTagValues = [];
}

function OmarSearchResults(){
    this.totalCount = "";
    this.offset     = "";
    this.order      = "";
    this.sort       = "";
}
OmarImageAdjustmentParams.inherits(OmarParams);
OmarSearchParams.inherits(OmarParams);
OmarSearchResults.inherits(OmarSearchParams);
OmarOgcParams.inherits(OmarImageAdjustmentParams);
OmarImageSpaceOpenLayersParams.inherits(OmarImageAdjustmentParams);
OmarImageSpaceGetTileParams.inherits(OmarImageAdjustmentParams);
OmarWmsParams.inherits(OmarOgcParams);
OmarWcsParams.inherits(OmarOgcParams);

function OmarPoint(px,py){

    this.x = px;
    this.y = py;

    this.toString = function()
    {
        return ("x:"+this.x + ",y:" +this.y);
    }
    this.toJSON = function()
    {
        return ("{x:" + this.x + ",\ny:" + this.y + "}");
    }
}

function OmarMatrix3x3(){
     this.m00 = 1.0;
     this.m01 = 0.0;
     this.m02 = 0.0;
     this.m10 = 0.0;
     this.m11 = 1.0;
     this.m12 = 0.0;
     this.m20 = 0.0;
     this.m21 = 0.0;
     this.m22 = 1.0;

     this.create = function(v00, v01, v02,
                            v10, v11, v12,
                            v20, v21, v22)
     {
        result = new OmarMatrix3x3();

        result.m00 = v00;
        result.m01 = v01;
        result.m02 = v02;
        result.m10 = v10;
        result.m11 = v11;
        result.m12 = v12;
        result.m20 = v20;
        result.m21 = v21;
        result.m22 = v22;

        return result;
     }
     this.copy = function(m)
     {
        this.m00 = m.m00;
        this.m01 = m.m01;
        this.m02 = m.m02;
        this.m10 = m.m10;
        this.m11 = m.m11;
        this.m12 = m.m12;
        this.m20 = m.m20;
        this.m21 = m.m21;
        this.m22 = m.m22;

        return this;
     }
     this.assign = function(v00, v01, v02,
                            v10, v11, v12,
                            v20, v21, v22)
     {
        this.m00 = v00;
        this.m01 = v01;
        this.m02 = v02;
        this.m10 = v10;
        this.m11 = v11;
        this.m12 = v12;
        this.m20 = v20;
        this.m21 = v21;
        this.m22 = v22;

        return this;
     }
     this.makeRotateX = function(angle)
     {
        RAD_PER_DEG = Math.PI/180.0;
        Cosine = Math.cos(angle*RAD_PER_DEG);
        Sine   = Math.sin(angle*RAD_PER_DEG);

        this.assign(1.0, 0.0, 0.0,
                    0.0, Cosine, Sine,
                    0, -Sine, Cosine);

        return this;
     }
     this.makeRotateY = function(angle)
     {
        RAD_PER_DEG = Math.PI/180.0;
        Cosine = Math.cos(angle*RAD_PER_DEG);
        Sine   = Math.sin(angle*RAD_PER_DEG);

        this.assign(Cosine, 0.0, -Sine,
                    0.0, 1.0, 0.0,
                    Sine, 0.0, Cosine);
        return this;
     }

     this.makeRotateZ = function(angle)
     {
        RAD_PER_DEG = Math.PI/180.0;
        Cosine = Math.cos(angle*RAD_PER_DEG);
        Sine   = Math.sin(angle*RAD_PER_DEG);

        this.assign(Cosine, Sine, 0.0,
                    -Sine, Cosine, 0.0,
                    0.0, 0.0, 1.0);

        return this;
     }

     this.makeScale = function(x,y)
     {

        if(x) this.m00 = x;
        if(y) this.m11 = y;

        return this;
     }
     this.makeTranslate = function(dx, dy)
     {
        if(dx) this.m02 = dx;
        if(dy) this.m12 = dy;

        return this;
     }
     this.times = function(m)
     {
        result = new OmarMatrix3x3();

        result.m00 = this.m00*m.m00 + this.m01*m.m10 + this.m02*m.m20;
        result.m01 = this.m00*m.m01 + this.m01*m.m11 + this.m02*m.m21;
        result.m02 = this.m00*m.m02 + this.m01*m.m12 + this.m02*m.m22;

        result.m10 = this.m10*m.m00 + this.m11*m.m10 + this.m12*m.m20;
        result.m11 = this.m10*m.m01 + this.m11*m.m11 + this.m12*m.m21;
        result.m12 = this.m10*m.m02 + this.m11*m.m12 + this.m12*m.m22;

        result.m20 = this.m20*m.m00 + this.m21*m.m10 + this.m22*m.m20;
        result.m21 = this.m20*m.m01 + this.m21*m.m11 + this.m22*m.m21;
        result.m22 = this.m20*m.m02 + this.m21*m.m12 + this.m22*m.m22;

        return result;
     }
     this.transform = function(pt)
     {
        return new OmarPoint(pt.x*this.m00 + pt.y*this.m01 + this.m02,
                             pt.x*this.m10 + pt.y*this.m11 + this.m12);
     }
     this.inv = function()
     {
        divisor = this.det();
        if(Math.abs(divisor) < 0.000000001) divisor = 1.0;

        result     = new OmarMatrix3x3();

        result.m00 = this.m22*this.m11 - this.m21*this.m12;
        result.m01 = -(this.m22*this.m01-this.m21*this.m02);
        result.m02 = this.m12*this.m01-this.m11*this.m02;
        result.m10 = -(this.m22*this.m10-this.m20*this.m12);
        result.m11 = this.m22*this.m00-this.m20*this.m02;
        result.m12 = -(this.m12*this.m00-this.m10*this.m02);
        result.m20 = this.m21*this.m10-this.m20*this.m11;
        result.m21 = -(this.m21*this.m00-this.m20*this.m01);
        result.m22 = this.m11*this.m00-this.m10*this.m01;

        result.m00/=divisor;
        result.m01/=divisor;
        result.m02/=divisor;
        result.m10/=divisor;
        result.m11/=divisor;
        result.m12/=divisor;
        result.m20/=divisor;
        result.m21/=divisor;
        result.m22/=divisor;

        return result;
     }
     this.det = function()
     {
        return (this.m00*(this.m22*this.m11 -this.m21*this.m12)-
                this.m10*(this.m22*this.m01-this.m21*this.m02) -
                this.m20*(this.m12*this.m01-this.m11*this.m02));
     }
     this.toString = function()
     {
         return (String(this.m00) + ", " + this.m01 + ", " +this.m02 + "\n"+
                 this.m10 + ", " + this.m11 + ", " + this.m12 + "\n"+
                 this.m20 + ", " + this.m21 + ", " + this.m22);
     }

}


function OmarImageBounds(){
    this.left   = 0.0;
    this.right  = 0.0;
    this.top    = 0.0;
    this.bottom = 0.0;
}

function OmarAffineImageModel(){
    this.m_center   = new OmarPoint(0.0,0.0); // center x
    this.m_pivot    = new OmarPoint(0.0,0.0); // center x
    this.m_width    = 0;
    this.m_height   = 0;
    this.m_rotation = 0.0;
    this.m_scalex   = 1.0;
    this.m_scaley   = 1.0;
    this.m_matrix   = new OmarMatrix3x3();

    this.makeCopy =function(){
        result = new OmarAffineImageModel();
        result.m_center   = this.m_center;
        result.m_width    = this.m_width;
        result.m_height   = this.m_height;
        result.m_rotation = this.m_rotation;
        result.m_scalex   = this.m_scalex;
        result.m_scaley   = this.m_scaley;

        result.buildTransform();

        return result;
    }
    this.setScale = function(sx,sy){
        this.m_scalex = sx;
        if(sy) this.m_scaley = sy;
        else this.m_scaley = this.m_scalex;
        this.buildTransform();
    }
    this.setRotate = function(r){
        this.m_rotation = r;
        this.buildTransform();
    }
    this.setCenter = function(pt){
        this.m_center = pt;
        this.buildTransform();
    }
    this.setPivot  = function(pt){
        this.m_pivot = pt;
        this.buildTransform();
    }
    this.buildTransform = function(){
        transM              = new OmarMatrix3x3(); // translate to center first
        scaleM              = new OmarMatrix3x3(); // scale to center first
        transOriginM        = new OmarMatrix3x3();
        transOriginNegatedM = new OmarMatrix3x3();
        rotzM               = new OmarMatrix3x3(); // rotate to center first

        transM.makeTranslate(-this.m_center.x, -this.m_center.y);
        transOriginM.makeTranslate(this.m_pivot.x, this.m_pivot.y);
        transOriginNegatedM.makeTranslate(-this.m_pivot.x, -this.m_pivot.y);
        scaleM.makeScale(this.m_scalex, this.m_scaley);

        this.m_matrix = scaleM.times(
                                     transM.times(
                                                  transOriginM.times(
                                                        rotzM.times(transOriginNegatedM))));

        this.m_matrixInv = this.m_matrix.inv();
    }
    this.setImageInfo = function(x,y,w,h){
        this.m_center = new OmarPoint(x,y);
        this.m_width  = w;
        this.m_height = h;
        this.buildTransform();
    }

    this.setIdentity = function()
    {
        this.m_scalex = 1.0;
        this.m_scaley = 1.0;
        this.rotation = 0.0;
        this.m_matrix = new OmarMatrix3x3();
        this.m_matrixInv = new OmarMatrix3x3();
    }
    this.getPivot = function()
    {
        return this.m_pivot;
    }
    this.getCenter = function()
    {
        return this.m_center;
    }
    this.centerToView = function()
    {
        return this.m_matrix.transform(this.m_center);
    }
    this.imageToView = function(pt){
       return this.m_matrix.transform(pt);
    }
    this.viewToImage = function(pt){
        return this.m_matrixInv.transform(pt);
    }
    this.viewCorners = function(){
        result = [];
        result[0] = this.imageToView(new OmarPoint(0.0,0.0));
        result[1] = this.imageToView(new OmarPoint(this.m_width-1,0.0));
        result[2] = this.imageToView(new OmarPoint(this.m_width-1,this.m_height-1));
        result[3] = this.imageToView(new OmarPoint(0.0,this.m_height-1));

        return result;
    }
    this.viewBounds = function(){
        corners = this.viewCorners();
        idx = 0;
        minx = corners[0].x;
        miny = corners[0].y;
        maxx = corners[0].x;
        maxy = corners[0].y;

        for(idx =1; idx < corners.length; ++idx)
        {
            if(corners[idx].x < minx) minx = corners[idx].x;
            if(corners[idx].x > maxx) maxx = corners[idx].x;
            if(corners[idx].y < miny) miny = corners[idx].y;
            if(corners[idx].y > maxy) maxy = corners[idx].y;
        }

        return { minx: minx,
                 maxx: maxx,
                 miny: miny,
                 maxy: maxy};
    }

}