var OMAR={};

OMAR.ddRegExp=/^(\-?\d{1,2})(\.\d+)?\,?\s?(\-?\d{1,3})(\.\d+)?$/;
OMAR.dmsRegExp=/^(\d{1,2})\°?\s?(\d{1,2})\'?\s?(\d{1,2})\s?(\.\d+)?\"?\s?([NnSs])?\,?\s?(\d{1,3})\°?\s?(\d{1,2})\'?\s?(\d{1,2})\s?(\.\d+)?\"?\s?([EeWw])?$/;
OMAR.mgrsRegExp=/^(\d{1,2})\s?([C-X])\s?([A-Z])\s?([A-Z])\s?(\d{1,5})\s?(\d{1,5})?/;


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
