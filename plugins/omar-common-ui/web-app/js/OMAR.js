var OMAR={};

OMAR.models = {}
OMAR.views  = {}
OMAR.pages  = {}

//OMAR.ddRegExp=/^(\-?\d{1,2})(\.\d+)?\,?\s?(\-?\d{1,3})(\.\d+)?$/;
//OMAR.dmsRegExp=/^(\d{1,2})\°?\s?(\d{1,2})\'?\s?(\d{1,2})\s?(\.\d+)?\"?\s?([NnSs])?\,?\s?(\d{1,3})\°?\s?(\d{1,2})\'?\s?(\d{1,2})\s?(\.\d+)?\"?\s?([EeWw])?$/;
//OMAR.mgrsRegExp=/^(\d{1,2})\s?([C-X])\s?([A-Z])\s?([A-Z])\s?(\d{1,5})\s?(\d{1,5})?/;

OMAR.dRegExp = /^\s*(\-?\d{1,2})\s*\u00B0?\s*([NnSs])?\s*\,?\s*(\-?\d{1,3})\s*\u00B0?\s*([WwEe])?\s*$/;
OMAR.ddRegExp = /^\s*(\-?\d{1,2}\.\d*)\s*\u00B0?\s*([NnSs])?\s*\,?\s*(\-?\d{1,3}\.\d*)\s*\u00B0?\s*([WwEe])?\s*$/;
OMAR.dmsRegExp = /^\s*(\d{1,2})\s*\u00B0?\s*\:?\s?(\d{1,2})\s*\'?\s*\:?\s?(\d{1,2})(\.\d*)?\s*\"?\s*([NnSs])\s*(\d{1,3})\s*\u00B0?\s*\:?\s?(\d{1,2})\s*\'?\s*\:?\s?(\d{1,2})(\.\d*)?\s*\"?\s*([EeWw])\s*$/;
OMAR.mgrsRegExp = /^\s*(\d{1,2})\s*([A-Za-z])\s*([A-Za-z])\s*([A-Za-z])\s*(\d{1,5})\s*(\d{1,5})\s*$/;


OMAR.regexp = {}

OMAR.regexp.floatExpression= /[-+]?[0-9]*\.?[0-9]*/;
OMAR.regexp.integerExpression= /[-+]?[0-9]*/;
OMAR.regexp.isoDate= /^([\+-]?\d{4}(?!\d{2}\b))((-?)((0[1-9]|1[0-2])(\3([12]\d|0[1-9]|3[01]))?|W([0-4]\d|5[0-2])(-?[1-7])?|(00[1-9]|0[1-9]\d|[12]\d{2}|3([0-5]\d|6[1-6])))([T\s]((([01]\d|2[0-3])((:?)[0-5]\d)?|24\:?00)([\.,]\d+(?!:))?)?(\17[0-5]\d([\.,]\d+)?)?([zZ]|([\+-])([01]\d|2[0-3]):?([0-5]\d)?)?)?)?$/;
OMAR.regexp.isoPeriod=/^P(?:\d+Y|Y)?(?:\d+M|M)?(?:\d+D|D)?(?:T(?:\d+H|H)?(?:\d+M|M)?(?:\d+(?:\.\d{1,2})?S|S)?)?$/;
OMAR.regexp.dRegExp = /^\s*(\-?\d{1,2})\s*\u00B0?\s*([NnSs])?\s*\,?\s*(\-?\d{1,3})\s*\u00B0?\s*([WwEe])?\s*$/;
OMAR.regexp.ddRegExp = /^\s*(\-?\d{1,2}\.\d*)\s*\u00B0?\s*([NnSs])?\s*\,?\s*(\-?\d{1,3}\.\d*)\s*\u00B0?\s*([WwEe])?\s*$/;
OMAR.regexp.ddrRegExp = /^\s*(\-?\d{1,2}\.\d*)\s*\u00B0?\s*([NnSs])?\s*\,?\s*(\-?\d{1,3}\.\d*)\s*\u00B0?\s*([WwEe])?\s*\,?\s*(\s*[0-9]*\.?[0-9]*)?$/;
OMAR.regexp.dmsRegExp = /^\s*(\d{1,2})\s*\u00B0?\s*\:?\s?(\d{1,2})\s*\'?\s*\:?\s?(\d{1,2})(\.\d*)?\s*\"?\s*([NnSs])\s*(\d{1,3})\s*\u00B0?\s*\:?\s?(\d{1,2})\s*\'?\s*\:?\s?(\d{1,2})(\.\d*)?\s*\"?\s*([EeWw])\s*$/;
OMAR.regexp.dmsrRegExp = /^\s*(\d{1,2})\s*\u00B0?\s*\:?\s?(\d{1,2})\s*\'?\s*\:?\s?(\d{1,2})(\.\d*)?\s*\"?\s*([NnSs])\s*(\d{1,3})\s*\u00B0?\s*\:?\s?(\d{1,2})\s*\'?\s*\:?\s?(\d{1,2})(\.\d*)?\s*\"?\s*([EeWw])\s*\,?\s*(\s*[0-9]*\.?[0-9]*)?$/;
OMAR.regexp.mgrsRegExp = /^\s*(\d{1,2})\s*([A-Za-z])\s*([A-Za-z])\s*([A-Za-z])\s*(\d{1,5})\s*(\d{1,5})\s*$/;
OMAR.regexp.mgrsrRegExp = /^\s*(\d{1,2})\s*([A-Za-z])\s*([A-Za-z])\s*([A-Za-z])\s*(\d{1,5})\s*(\d{1,5})\s*\,\s*(\s*[0-9]*\.?[0-9]*)?$/;


OMAR.defaultSpinnerOptions = {
    lines: 13,
    length: 7,
    width: 4,
    radius: 10,
    corners: 1,
    rotate: 0,
    color: '#000',
    speed: 1,
    trail: 60,
    shadow: false,
    hwaccel: false,
    className: 'spinner',
    zIndex: 2e9,
    top: 'auto',
    left: 'auto'
}

/****
 * If you want a higher precision in OpenLayers inches per unit for meters then
 * add this line in your code that uses open layers.
 *
 * OpenLayers.INCHES_PER_UNIT['m'] = 39.3700787;
 *
 * The OMAR.measure.units are OpenLayers units that we interface into
 */
OMAR.measure = {}
OMAR.measure.units = { labels:["kilometers", "meters", "feet", "yards", "miles", "nautical miles"],
    openlayersMapping:{"kilometers":"Kilometer", "meters":"Meter", "feet":"Foot", "yards":"Yard", "miles": "Mile", "nautical miles":"NautM"},
    extensionMapping:{"kilometers":"km", "meters":"m", "feet":"ft", "yards":"yd", "miles": "mi", "nautical miles":"nmi" },
    unitMapping:{"km":"kilometers", "m":"meters", "ft":"feet", "yd":"yards",  "mi":"miles", "nmi":"nautical miles" },
    precisionMapping:{"kilometers":10000,"meters":1000,"feet":100,"yards":100, "miles":10000, "nautical miles":10000},
    active:"meters"
};



OMAR.matchesCompletely = function(value, expression)
{
    var matches = value.match(expression)
    if(matches&&matches.length>0){
        if(matches[0].length == value.length)
        {
            return true;
        }
    }

    return false;
}

OMAR.testArray = function(values, comparator)
{
    var idx = 0;
    for(idx = 0;idx < values.size();++idx)
    {
        if(!comparator(values[idx]))
        {
            return false;
        }
    }

    return true;
}

OMAR.isFloat = function(value)
{
    return OMAR.matchesCompletely(value, OMAR.regexp.floatExpression);
}

OMAR.isInteger = function(value)
{
    return OMAR.matchesCompletely(value, OMAR.regexp.integerExpression);
}


OMAR.isIntegerArray = function(values)
{
    return OMAR.testArray(values, OMAR.isInteger);
}

OMAR.isFloatArray = function(values)
{
    return OMAR.testArray(values, OMAR.isFloat);
}

OMAR.isIsoPeriod = function(value)
{
    return OMAR.matchesCompletely(value, OMAR.regexp.isoPeriod);
}

OMAR.isIsoDate = function(value)
{
    return OMAR.matchesCompletely(value, OMAR.regexp.isoDate);
}

OMAR.isIsoInterval = function(value)
{
    var typeEncoding = "";
    if(value.length < 1) return false;
    if(OMAR.isIsoDate(value)) return true;
    if(OMAR.isIsoPeriod(value)) return true;

    var splitValue = value.split("/");

    if(splitValue.length>0)
    {
        if(splitValue[0] != "")
        {
            if(OMAR.isIsoDate(splitValue[0])) typeEncoding += "D";
            else if(OMAR.isIsoPeriod(splitValue[0])) typeEncoding += "P";
            else return false;
        }
        if(splitValue[1] != "")
        {
            if(OMAR.isIsoDate(splitValue[1])) typeEncoding += "D";
            else if(OMAR.isIsoPeriod(splitValue[1])) typeEncoding += "P";
            else return false;
        }

        return ((typeEncoding == "PD")||
            (typeEncoding == "DP")||
            (typeEncoding == "DD")||
            (typeEncoding == "D")||
            (typeEncoding == "P"));
    }

    return false;
}

OMAR.isIso8601 = function(value)
{
    var result = true;
    if(value&&value.length>0)
    {
        var intervals = value.split(",");
        var idx = 0;
        for(idx = 0; idx < intervals.length;++idx)
        {
            if(!OMAR.isIsoInterval(intervals[idx]))
            {
                result = false;
                break;
            }
        }
    }
    else
    {
        result = false;
    }

    return result;
}


OMAR.parseXml = null;

/*
if (typeof window.DOMParser != "undefined") {
    OMAR.parseXml = function(xmlStr) {
        return ( new window.DOMParser() ).parseFromString(xmlStr, "text/xml");
    };
} else if (typeof window.ActiveXObject != "undefined" &&
    new window.ActiveXObject("Microsoft.XMLDOM")) {
    OMAR.parseXml = function(xmlStr) {
        var xmlDoc = new window.ActiveXObject("Microsoft.XMLDOM");
        xmlDoc.async = "false";
        xmlDoc.loadXML(xmlStr);
        return xmlDoc;
    };
} else {
    throw new Error("No XML parser found");
}
  */


//Browser JSON seems to mess things up with export.  I seem to get
// quoted arrays.  We will delete the toJSON prototypes so it doesn't
// get overriden by stringify
//
if(window.Prototype) {
    delete Object.prototype.toJSON;
    delete Array.prototype.toJSON;
    delete Hash.prototype.toJSON;
    delete String.prototype.toJSON;
}
