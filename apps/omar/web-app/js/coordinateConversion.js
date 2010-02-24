function setCenterText()
{
    var center = map.getCenter();

    if(($("unitsMode").value) == "DD")
    {
        $("centerLat").value = center.lat;
        $("centerLon").value = center.lon;

        setView();
    }

    if(($("unitsMode").value) == "DMS")
    {
        $("centerLat").value = ddToDms(center.lat, "latitude");
        $("centerLon").value = ddToDms(center.lon, "longitude");

        setView();
    }

    if(($("unitsMode").value) == "MGRS")
    {
        setView();
    }
}

function ddToDms(decimalDegrees, hemisphere)
{
    var degreesAbs = Math.abs(decimalDegrees);
    var degrees = Math.floor(degreesAbs);

    var minutesAbs = Math.abs((degreesAbs - degrees) * 60);
    var minutes = Math.floor(minutesAbs);

    var seconds = Math.abs((minutesAbs - minutes) * 60);
    seconds = seconds.toFixed(3);

    var dms;

    if(minutes < 10 && seconds < 10)
    {
        dms = degrees + " 0" + minutes + " 0" + seconds;
    }

    else if(minutes < 10)
    {
        dms = degrees + " 0" + minutes + " " + seconds;
    }

    else if(seconds < 10)
    {
        dms = degrees + " " + minutes + " 0" + seconds;
    }

    else
    {
        dms = degrees + " " + minutes + " " + seconds;
    }

    if(hemisphere == "latitude")
    {
        if(decimalDegrees < 0)
        {
            dms = dms + " S";
        }
        else
        {
            dms = dms + " N";
        }
    }

    if(hemisphere == "longitude")
    {
        if(decimalDegrees < 0)
        {
            dms = dms + " W";
        }
        else
        {
            dms = dms + " E";
        }
    }

    return dms;
}

function dmsToDd(degrees, minutes, seconds)
{
    var dd = Math.abs(degrees) + Math.abs((minutes / 60)) + Math.abs((seconds / 3600));

    if(degrees < 0)
    {
        dd = -dd;
    }

    return dd;
}

function setMapCenter(latitude, longitude)
{
    var zoom = map.getZoom();
    var center = new OpenLayers.LonLat(longitude, latitude);

    map.setCenter(center, zoom);
}

function goto()
{
    var latDdRegExp = /^(\-?\d{1,2})(\.\d+)?$/
    var lonDdRegExp = /^(\-?\d{1,3})(\.\d+)?$/

    var latDmsRegExp = /^(\d{1,2})\s?(\d{2})\s?(\d{2})(.\d+)?\s?([NnSs])?/
    var lonDmsRegExp = /^(\d{1,3})\s?(\d{2})\s?(\d{2})(.\d+)?\s?([EeWw])?/

    if(($("centerLat").value).match(latDdRegExp) && ($("centerLon").value).match(lonDdRegExp))
    {
        setMapCenter(($("centerLat").value), ($("centerLon").value));
    }

    else if(($("centerLat").value).match(latDmsRegExp) && ($("centerLon").value).match(lonDmsRegExp))
    {
        if(($("centerLat").value).match(latDmsRegExp)) // There has to be a better way to do this.
        {
            var latDeg = parseInt(RegExp.$1, 10);
            var latMin = parseInt(RegExp.$2, 10);
            var latSec = parseInt(RegExp.$3, 10) + RegExp.$4;
            var latHem = RegExp.$5;

            if(latHem == "S" || latHem == "s")
            {
                latDeg = -latDeg;
            }
        }

        if(($("centerLon").value).match(lonDmsRegExp)) // There has to be a better way to do this.
        {
            var lonDeg = parseInt(RegExp.$1, 10);
            var lonMin = parseInt(RegExp.$2, 10);
            var lonSec = parseInt(RegExp.$3, 10) + RegExp.$4;
            var lonHem = RegExp.$5;

            if(lonHem == "W" || lonHem == "w")
            {
                lonDeg = -lonDeg;
            }
        }

        setMapCenter(dmsToDd(latDeg, latMin, latSec), dmsToDd(lonDeg, lonMin, lonSec));
    }

    else
    {
        alert("Invalid DMS Format.\n\n" +
              "Valid Examples: \n" +
              "DDDMMSS.SSS[NnSsEeWw]\n\n" +
              "0 00 00.000 N\n" +
              "00000.000N\n" +
              "12 34 56.123 N\n" +
              "123456.123N\n" +
              "90 00 00 N\n" +
              "900000N\n" +
              "90 00 00 S\n" +
              "900000S\n" +
              "123 46 07.891 E\n" +
              "1234607.891E\n" +
              "180 00 00 E\n" +
              "1800000E\n" +
              "180 00 00 W\n" +
              "1800000W");
    }
}

var setView = function(e)
{
    if(($("aoiMinLon").value == "") && ($("aoiMaxLat").value == "") && ($("aoiMaxLon").value == "") && ($("aoiMinLat").value == ""))
    {
        //
    }

    else
    {
        var unitsMode = $("unitsMode").value;

        if(unitsMode == "DD")
        {
            var latDmsRegExp = /^(\d{1,2})\s?(\d{2})\s?(\d{2})(.\d+)?\s?([NnSs])?/
            var lonDmsRegExp = /^(\d{1,3})\s?(\d{2})\s?(\d{2})(.\d+)?\s?([EeWw])?/

            if(($("aoiMaxLat").value).match(latDmsRegExp))
            {
                var aoiMaxlatDeg = parseInt(RegExp.$1, 10);
                var aoiMaxlatMin = parseInt(RegExp.$2, 10);
                var aoiMaxlatSec = parseInt(RegExp.$3, 10) + RegExp.$4;
                var aoiMaxlatHem = RegExp.$5;

                if(aoiMaxlatHem == "S" || aoiMaxlatHem == "s")
                {
                    aoiMaxlatDeg = -aoiMaxlatDeg;
                }

                $("aoiMaxLat").value = dmsToDd(aoiMaxlatDeg, aoiMaxlatMin, aoiMaxlatSec);
            }

            if(($("aoiMinLon").value).match(lonDmsRegExp))
            {
                var aoiMinlonDeg = parseInt(RegExp.$1, 10);
                var aoiMinlonMin = parseInt(RegExp.$2, 10);
                var aoiMinlonSec = parseInt(RegExp.$3, 10) + RegExp.$4;
                var aoiMinlonHem = RegExp.$5;

                if(aoiMinlonHem == "W" || aoiMinlonHem == "w")
                {
                    aoiMinlonDeg = -aoiMinlonDeg;
                }

                $("aoiMinLon").value = dmsToDd(aoiMinlonDeg, aoiMinlonMin, aoiMinlonSec);
            }

            if(($("aoiMinLat").value).match(latDmsRegExp))
            {
                var aoiMinlatDeg = parseInt(RegExp.$1, 10);
                var aoiMinlatMin = parseInt(RegExp.$2, 10);
                var aoiMinlatSec = parseInt(RegExp.$3, 10) + RegExp.$4;
                var aoiMinlatHem = RegExp.$5;

                if(aoiMinlatHem == "S" || aoiMinlatHem == "s")
                {
                    aoiMinlatDeg = -aoiMinlatDeg;
                }

                $("aoiMinLat").value = dmsToDd(aoiMinlatDeg, aoiMinlatMin, aoiMinlatSec);
            }

            if(($("aoiMaxLon").value).match(lonDmsRegExp))
            {
                var aoiMaxlonDeg = parseInt(RegExp.$1, 10);
                var aoiMaxlonMin = parseInt(RegExp.$2, 10);
                var aoiMaxlonSec = parseInt(RegExp.$3, 10) + RegExp.$4;
                var aoiMaxlonHem = RegExp.$5;

                if(aoiMaxlonHem == "W" || aoiMaxlonHem == "w")
                {
                    aoiMaxlonDeg = -aoiMaxlonDeg;
                }

                $("aoiMaxLon").value = dmsToDd(aoiMaxlonDeg, aoiMaxlonMin, aoiMaxlonSec);
            }
        }

        if(unitsMode == "DMS")
        {
            var latDdRegExp = /^(\-?\d{1,2})(\.\d+)?$/
            var lonDdRegExp = /^(\-?\d{1,3})(\.\d+)?$/

            if(($("aoiMaxLat").value).match(latDdRegExp))
            {
                $("aoiMaxLat").value = ddToDms(($("aoiMaxLat").value),"latitude");
            }

            if(($("aoiMinLon").value).match(lonDdRegExp))
            {
                $("aoiMinLon").value = ddToDms(($("aoiMinLon").value),"longitude");
            }

            if(($("aoiMinLat").value).match(latDdRegExp))
            {
                $("aoiMinLat").value = ddToDms(($("aoiMinLat").value),"latitude");
            }

            if(($("aoiMaxLon").value).match(lonDdRegExp))
            {
                $("aoiMaxLon").value = ddToDms(($("aoiMaxLon").value),"longitude");
            }
        }

        if(unitsMode == "MGRS")
        {
        }
    }
}

function setAOI( e )
{
    var geom = e.feature.geometry;
    var bounds = geom.getBounds();
    var feature = new OpenLayers.Feature.Vector(geom);

    var unitsMode = $("unitsMode").value;

    if(unitsMode == "DD")
    {
        // HACK - Need a better way to this
        $("aoiMinLon").value = bounds.left;
        $("aoiMaxLat").value = bounds.top;
        $("aoiMaxLon").value = bounds.right;
        $("aoiMinLat").value = bounds.bottom;
    }

    if(unitsMode == "DMS")
    {
        // HACK - Need a better way to this
        $("aoiMinLon").value = ddToDms(bounds.left, "longitude");
        $("aoiMaxLat").value = ddToDms(bounds.top, "latitude");
        $("aoiMaxLon").value = ddToDms(bounds.right, "longitude");
        $("aoiMinLat").value = ddToDms(bounds.bottom, "latitude");
    }

    if(unitsMode == "MGRS")
    {
    }
      
    aoiLayer.destroyFeatures();
    aoiLayer.addFeatures(feature, {silent: true});
}

function searchForRasters()
{
    var centerLat = $("centerLat").value;
    var centerLon = $("centerLon").value;

    var latDmsRegExp = /^(\d{1,2})\s?(\d{2})\s?(\d{2})(.\d+)?\s?([NnSs])?/
    var lonDmsRegExp = /^(\d{1,3})\s?(\d{2})\s?(\d{2})(.\d+)?\s?([EeWw])?/

    if(centerLat.match(latDmsRegExp) && centerLon.match(lonDmsRegExp))
    {
        if(centerLat.match(latDmsRegExp))
        {
            var ctrlatDeg = parseInt(RegExp.$1, 10);
            var ctrlatMin = parseInt(RegExp.$2, 10);
            var ctrlatSec = parseInt(RegExp.$3, 10) + RegExp.$4;
            var ctrlatHem = RegExp.$5;

            if(ctrlatHem == "S" || ctrlatHem == "s")
            {
                ctrlatDeg = -ctrlatDeg;
            }

            $("centerLat").value = dmsToDd(ctrlatDeg, ctrlatMin, ctrlatSec);
        }

        if(centerLon.match(lonDmsRegExp))
        {
            var ctrlonDeg = parseInt(RegExp.$1, 10);
            var ctrlonMin = parseInt(RegExp.$2, 10);
            var ctrlonSec = parseInt(RegExp.$3, 10) + RegExp.$4;
            var ctrlonHem = RegExp.$5;

            if(ctrlonHem == "W" || ctrlonHem == "w")
            {
                ctrlonDeg = -ctrlonDeg;
            }

            $("centerLon").value = dmsToDd(ctrlonDeg, ctrlonMin, ctrlonSec);
        }
    }

    var aoiMaxLatRegExp = /^(\d{1,2})\s?(\d{2})\s?(\d{2})(.\d+)?\s?([NnSs])?/
    var aoiMinLonRegExp = /^(\d{1,3})\s?(\d{2})\s?(\d{2})(.\d+)?\s?([EeWw])?/
    var aoiMinLatRegExp = /^(\d{1,2})\s?(\d{2})\s?(\d{2})(.\d+)?\s?([NnSs])?/
    var aoiMaxLonRegExp = /^(\d{1,3})\s?(\d{2})\s?(\d{2})(.\d+)?\s?([EeWw])?/

    var aoiMaxLat = $("aoiMaxLat").value;

    if(aoiMaxLat.match(aoiMaxLatRegExp))
    {
        var aoiMaxlatDeg = parseInt(RegExp.$1, 10);
        var aoiMaxlatMin = parseInt(RegExp.$2, 10);
        var aoiMaxlatSec = parseInt(RegExp.$3, 10) + RegExp.$4;
        var aoiMaxlatHem = RegExp.$5;

        if(aoiMaxlatHem == "S" || aoiMaxlonHem == "s")
        {
            aoiMaxlatDeg = -aoiMaxlatDeg;
        }

        $("aoiMaxLat").value = dmsToDd(aoiMaxlatDeg, aoiMaxlatMin, aoiMaxlatSec);
    }


    var aoiMinLon = $("aoiMinLon").value;

    if(aoiMinLon.match(aoiMinLonRegExp))
    {
        var aoiMinlonDeg = parseInt(RegExp.$1, 10);
        var aoiMinlonMin = parseInt(RegExp.$2, 10);
        var aoiMinlonSec = parseInt(RegExp.$3, 10) + RegExp.$4;
        var aoiMinlonHem = RegExp.$5;

        if(aoiMinlonHem == "W" || aoiMinlonHem == "w")
        {
            aoiMinlonDeg = -aoiMinlonDeg;
        }

        $("aoiMinLon").value = dmsToDd(aoiMinlonDeg, aoiMinlonMin, aoiMinlonSec);
    }

    var aoiMinLat = $("aoiMinLat").value;

    if(aoiMinLat.match(aoiMinLatRegExp))
    {
        var aoiMinlatDeg = parseInt(RegExp.$1, 10);
        var aoiMinlatMin = parseInt(RegExp.$2, 10);
        var aoiMinlatSec = parseInt(RegExp.$3, 10) + RegExp.$4;
        var aoiMinlatHem = RegExp.$5;

        if(aoiMinlatHem == "S" || aoiMinlonHem == "s")
        {
            aoiMinlatDeg = -aoiMinlatDeg;
        }

        $("aoiMinLat").value = dmsToDd(aoiMinlatDeg, aoiMinlatMin, aoiMinlatSec);
    }

    var aoiMaxLon = $("aoiMaxLon").value;

    if(aoiMaxLon.match(aoiMaxLonRegExp))
    {
        var aoiMaxlonDeg = parseInt(RegExp.$1, 10);
        var aoiMaxlonMin = parseInt(RegExp.$2, 10);
        var aoiMaxlonSec = parseInt(RegExp.$3, 10) + RegExp.$4;
        var aoiMaxlonHem = RegExp.$5;

        if(aoiMaxlonHem == "W" || aoiMaxlonHem == "w")
        {
            aoiMaxlonDeg = -aoiMaxlonDeg;
        }

        $("aoiMaxLon").value = dmsToDd(aoiMaxlonDeg, aoiMaxlonMin, aoiMaxlonSec);
    }

    document.searchForm.action = "search";
    setCurrentViewport();
    document.searchForm.submit();
}