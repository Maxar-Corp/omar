function MapWidget()
{
    var map;
    this.baseLayer = null;
    this.dataLayer = null;
    var aoiLayer = null;

    var convert = new CoordinateConversion();

    this.setupMapWidget = function()
    {
        map = new OpenLayers.Map("map", {controls: []});

        map.addControl(new OpenLayers.Control.Attribution());
        map.addControl(new OpenLayers.Control.LayerSwitcher());
        map.addControl(new OpenLayers.Control.Scale());
        map.addControl(new OpenLayers.Control.ScaleLine());

        map.events.register("mousemove", map, this.setMousePosition);
        map.events.register("moveend", map, this.setCenterLatLonText);
        map.events.register("zoomend", map, this.setBoundLatLonText);
    };

    this.setMousePosition = function(evt)
    {
        var mouseLonLat = map.getLonLatFromViewPortPx(new OpenLayers.Pixel(evt.xy.x, evt.xy.y));

        var ddOutput = document.getElementById("ddCoordinates");
        var dmsOutput = document.getElementById("dmsCoordinates");
        var utmOutput = document.getElementById("utmCoordinates");

        if(mouseLonLat.lat > "90" || mouseLonLat.lat < "-90" || mouseLonLat.lon > "180" || mouseLonLat.lon < "-180")
        {
            ddOutput.innerHTML = "<b>DD:</b> Out of geospatial bounds.";
            dmsOutput.innerHTML = "<b>DMS:</b> Out of geospatial bounds.";
            utmOutput.innerHTML = "<b>UTM:</b> Out of geospatial bounds.";
        }

        else
        {
            ddOutput.innerHTML = "<b>DD:</b> " + mouseLonLat.lat + " " + mouseLonLat.lon;
            dmsOutput.innerHTML = "<b>DMS:</b> " + convert.ddToDms(mouseLonLat.lat, "latitude") + " " + convert.ddToDms(mouseLonLat.lon, "longitude");
            utmOutput.innerHTML = "<b>UTM:</b> " + convert.getUtm(mouseLonLat.lat, mouseLonLat.lon);
        }
    };

    this.setTextFields = function()
    {
        this.setCenterLatLonText();
        this.setBoundLatLonText();
    };

    this.setCenterLatLonText = function()
    {
        var center = map.getCenter( );

        if ($("unitsMode").value == "DD")
        {
            $("centerLat").value = center.lat;
            $("centerLon").value = center.lon;
        }

        if ($("unitsMode").value == "DMS")
        {
            $("centerLat").value = convert.ddToDms(center.lat, "latitude");
            $("centerLon").value = convert.ddToDms(center.lon, "longitude");
        }

        if($("unitsMode").value == "MGRS")
        {

        }
    };

    this.setBoundLatLonText = function()
    {
        if($("unitsMode").value == "DD")
        {
            var latDmsRegExp = /^(\d{1,2})\s?(\d{2})\s?(\d{2})(.\d+)?\s?([NnSs])?/
            var lonDmsRegExp = /^(\d{1,3})\s?(\d{2})\s?(\d{2})(.\d+)?\s?([EeWw])?/

            if($("aoiMaxLat").value.match(latDmsRegExp))
            {
                var aoiMaxLatDeg = parseInt(RegExp.$1, 10);
                var aoiMaxLatMin = parseInt(RegExp.$2, 10);
                var aoiMaxLatSec = parseInt(RegExp.$3, 10) + RegExp.$4;
                var aoiMaxLatHem = RegExp.$5;

                if(aoiMaxLatHem == "S" || aoiMaxLatHem == "s")
                {
                    aoiMaxLatDeg = -aoiMaxLatDeg;
                }

                $("aoiMaxLat").value = convert.dmsToDd(aoiMaxLatDeg, aoiMaxLatMin, aoiMaxLatSec);
            }

            if($("aoiMinLon").value.match(lonDmsRegExp))
            {
                var aoiMinLonDeg = parseInt(RegExp.$1, 10);
                var aoiMinLonMin = parseInt(RegExp.$2, 10);
                var aoiMinLonSec = parseInt(RegExp.$3, 10) + RegExp.$4;
                var aoiMinLonHem = RegExp.$5;

                if(aoiMinLonHem == "W" || aoiMinLonHem == "w")
                {
                    aoiMinLonDeg = -aoiMinLonDeg;
                }

                $("aoiMinLon").value = convert.dmsToDd(aoiMinLonDeg, aoiMinLonMin, aoiMinLonSec);
            }

            if($("aoiMinLat").value.match(latDmsRegExp))
            {
                var aoiMinLatDeg = parseInt(RegExp.$1, 10);
                var aoiMinLatMin = parseInt(RegExp.$2, 10);
                var aoiMinLatSec = parseInt(RegExp.$3, 10) + RegExp.$4;
                var aoiMinLatHem = RegExp.$5;

                if(aoiMinLatHem == "S" || aoiMinLatHem == "s")
                {
                    aoiMinLatDeg = -aoiMinLatDeg;
                }

                $("aoiMinLat").value = convert.dmsToDd(aoiMinLatDeg, aoiMinLatMin, aoiMinLatSec);
            }

            if($("aoiMaxLon").value.match(lonDmsRegExp))
            {
                var aoiMaxLonDeg = parseInt(RegExp.$1, 10);
                var aoiMaxLonMin = parseInt(RegExp.$2, 10);
                var aoiMaxLonSec = parseInt(RegExp.$3, 10) + RegExp.$4;
                var aoiMaxLonHem = RegExp.$5;

                if(aoiMaxLonHem == "W" || aoiMaxLonHem == "w")
                {
                    aoiMaxLonDeg = -aoiMaxLonDeg;
                }

                $("aoiMaxLon").value = convert.dmsToDd(aoiMaxLonDeg, aoiMaxLonMin, aoiMaxLonSec);
            }
        }

        if($("unitsMode").value == "DMS")
        {
            var latDdRegExp = /^(\-?\d{1,2})(\.\d+)?$/
            var lonDdRegExp = /^(\-?\d{1,3})(\.\d+)?$/

            if($("aoiMaxLat").value.match(latDdRegExp))
            {
                $("aoiMaxLat").value = convert.ddToDms($("aoiMaxLat").value, "latitude");
            }

            if($("aoiMinLon").value.match(lonDdRegExp))
            {
                $("aoiMinLon").value = convert.ddToDms($("aoiMinLon").value, "longitude");
            }

            if($("aoiMinLat").value.match(latDdRegExp))
            {
                $("aoiMinLat").value = convert.ddToDms($("aoiMinLat").value, "latitude");
            }

            if($("aoiMaxLon").value.match(lonDdRegExp))
            {
                $("aoiMaxLon").value = convert.ddToDms($("aoiMaxLon").value, "longitude");
            }
        }

        if($("unitsMode").value == "MGRS")
        {

        }  
    };

    this.setupBaseLayers = function(baseLayer)
    {
        map.addLayer(baseLayer);
        map.setBaseLayer(baseLayer);
    };

    this.setupDataLayer = function(dataWmsTitle, dataWmsUrl, dataWmsLayers, dataWmsStyles, dataWmsFormat)
    {
        dataLayer = new OpenLayers.Layer.WMS(dataWmsTitle, dataWmsUrl,
        {layers: dataWmsLayers, styles: dataWmsStyles, format: dataWmsFormat, transparent: true},
        {isBaseLayer: false, buffer: 0, visibility: true, transitionEffect: "resize"});

        map.addLayer(dataLayer);
    };

    this.changeMapSize = function()
    {
        var mapTitle = $("mapTitle");
        var mapDiv = $("map");

        mapDiv.style.width = mapTitle.offsetWidth + "px";
        mapDiv.style.height = Math.round(mapTitle.offsetWidth / 2) + "px";

        map.updateSize();
    };

    this.setupAoiLayer = function()
    {
        aoiLayer = new OpenLayers.Layer.Vector("Area of Interest");
        aoiLayer.events.register("featureadded", aoiLayer, this.setAOI);

        var polyOptions = {sides: 4, irregular: true};

        var polygonControl = new OpenLayers.Control.DrawFeature(aoiLayer, OpenLayers.Handler.RegularPolygon,
        {handlerOptions: polyOptions});

        map.addLayer(aoiLayer);
        map.addControl(polygonControl);

        var aoiMinLon = "${queryParams?.aoiMinLon ?: ''}";
        var aoiMinLat = "${queryParams?.aoiMinLat ?: ''}";
        var aoiMaxLon = "${queryParams?.aoiMaxLon ?: ''}";
        var aoiMaxLat = "${queryParams?.aoiMaxLat ?: ''}";

        if (aoiMinLon && aoiMinLat && aoiMaxLon && aoiMaxLat )
        {
            var bounds = new OpenLayers.Bounds(aoiMinLon, aoiMinLat, aoiMaxLon, aoiMaxLat);
            var feature = new OpenLayers.Feature.Vector(bounds.toGeometry());

            aoiLayer.addFeatures(feature, {silent: true});
        }
    };

    this.setAOI = function(e)
    {
        var geom = e.feature.geometry;
        var bounds = geom.getBounds();
        var feature = new OpenLayers.Feature.Vector(geom);

        if($("unitsMode").value == "DD")
        {
            $("aoiMinLon").value = bounds.left;
            $("aoiMaxLat").value = bounds.top;
            $("aoiMaxLon").value = bounds.right;
            $("aoiMinLat").value = bounds.bottom;
        }

        if($("unitsMode").value == "DMS")
        {
            $("aoiMinLon").value = convert.ddToDms(bounds.left, "longitude");
            $("aoiMaxLat").value = convert.ddToDms(bounds.top, "latitude");
            $("aoiMaxLon").value = convert.ddToDms(bounds.right, "longitude");
            $("aoiMinLat").value = convert.ddToDms(bounds.bottom, "latitude");
        }

        if($("unitsMode").value == "MGRS")
        {

        }

        aoiLayer.destroyFeatures();
        aoiLayer.addFeatures(feature, {silent: true});
    };

    this.setupToolBar = function()
    {
        var recenterButton = new OpenLayers.Control.MouseDefaults(
        {title: "Click and drag to recenter map."});

        var zoomBoxButton = new OpenLayers.Control.ZoomBox(
        {title: "Click and drag to zoom into an area."});

        var zoomInButton = new OpenLayers.Control.Button(
        {title: "Click to zoom in.", displayClass: "olControlZoomIn", trigger: this.zoomIn});

        var zoomOutButton = new OpenLayers.Control.Button(
        {title: "Click to zoom out.", displayClass: "olControlZoomOut", trigger: this.zoomOut});

        var navButton = new OpenLayers.Control.NavigationHistory(
        {nextOptions:{title: "Click to go to next view."}, previousOptions:{title: "Click to go to previous view."}});

        map.addControl(navButton);

        var zoomMaxExtentButton = new OpenLayers.Control.ZoomToMaxExtent(
        {title: "Click to zoom to the max extent."});

        var polyOptions = {sides: 4, irregular: true};

        var polygonControl = new OpenLayers.Control.DrawFeature(aoiLayer, OpenLayers.Handler.RegularPolygon,
        {handlerOptions: polyOptions, title: "Click and drag to specify an area of interest."});

        var clearAoiButton = new OpenLayers.Control.Button(
        {title: "Click to clear area of interest", displayClass: "olControlClearAreaOfInterest", trigger: this.clearAOI});

        var measureDistanceButton = new OpenLayers.Control.Measure( OpenLayers.Handler.Path,
        {title: "Measure Distance", displayClass: "olControlMeasureDistance",
        eventListeners: {measure: function(evt){alert("Distance: " + evt.measure.toFixed(3) + evt.units);}}});

        var measureAreaButton = new OpenLayers.Control.Measure(OpenLayers.Handler.Polygon,
        {title: "Measure Area", displayClass: "olControlMeasureArea",
        eventListeners: {measure: function(evt){alert("Area: " + evt.measure.toFixed(3) + evt.units);}}});

        var container = $("panel2");

        var panel = new OpenLayers.Control.Panel(
        {div: container,defaultControl: zoomBoxButton, displayClass: "olControlPanel"});

        panel.addControls([
            recenterButton,
            zoomBoxButton,
            zoomInButton,
            zoomOutButton,
            navButton.next, navButton.previous,
            zoomMaxExtentButton,
            polygonControl,
            clearAoiButton,
            measureDistanceButton,
            measureAreaButton
        ]);

        map.addControl(panel);
    };

    this.zoomIn = function()
    {
        map.zoomIn( );
    };

    this.zoomOut = function()
    {
        map.zoomOut( );
    };

    this.clearAOI = function( e )
    {
        aoiLayer.destroyFeatures( );

        $("aoiMinLon").value = "";
        $("aoiMaxLat").value = "";
        $("aoiMaxLon").value = "";
        $("aoiMinLat").value = "";
    };

    this.setupMapView = function(viewMinLon, viewMinLat, viewMaxLon, viewMaxLat)
    {
        var bounds = new OpenLayers.Bounds(viewMinLon, viewMinLat, viewMaxLon, viewMaxLat);
        var zoom = map.getZoomForExtent(bounds, true);

        map.setCenter(bounds.getCenterLonLat(), zoom);
    };

    this.setupQueryFields = function(searchMethod)
    {
        if(searchMethod == "RADIUS")
        {
            this.toggleRadiusSearch();
        }
        else if(searchMethod == "BBOX")
        {
            this.toggleBBoxSearch();
        }
    };

    this.toggleRadiusSearch = function()
    {
        this.enableRadiusSearch();
        this.disableBBoxSearch();
    };

    this.enableRadiusSearch = function()
    {
        $("aoiRadius").disabled = false;
    };

    this.disableBBoxSearch = function()
    {
        $("aoiMinLon").disabled = true;
        $("aoiMinLat").disabled = true;
        $("aoiMaxLon").disabled = true;
        $("aoiMaxLat").disabled = true;
    };

    this.toggleBBoxSearch = function()
    {
        this.enableBBoxSearch();
        this.disableRadiusSearch();
    };

    this.enableBBoxSearch = function()
    {
        $("aoiMinLon").disabled = false;
        $("aoiMinLat").disabled = false;
        $("aoiMaxLon").disabled = false;
        $("aoiMaxLat").disabled = false;
    };

    this.disableRadiusSearch = function()
    {
        $("aoiRadius").disabled = true;
    };

    String.prototype.leftPad = function ( l, c )
    {
        return new Array(l - this.length + 1).join(c || '0') + this;
    };

    this.updateOmarFilters = function(startDay, startMonth, startYear, startHour, startMinute, endDay, endMonth, endYear, endHour, endMinute, numberOfNames, numberOfValues)
    {
        var wmsParams = new Array( );

        var hasStartDate = startDay != "" && startMonth != "" && startYear != "" && startHour != "" && startMinute != "";
        var startDateNoQuote = startYear + startMonth.leftPad(2) + startDay.leftPad(2) + 'T' + startHour.leftPad(2) + ':' + startMinute.leftPad(2) + ':' + '00Z';

        var hasEndDate = endDay != "" && endMonth != "" && endYear != "";
        var endDateNoQuote = endYear + endMonth.leftPad(2) + endDay.leftPad(2) + 'T' + endHour.leftPad(2) + ':' + endMinute.leftPad(2) + ':' + '00Z';

        var wmsTime = "";

        if(hasStartDate)
        {
            wmsTime = startDateNoQuote;
            if(hasEndDate)
            {
                wmsTime += "/" + endDateNoQuote;
            }
            else
            {
                wmsTime += "/"
            }
        }
        else
        {
            if(hasEndDate)
            {
                wmsTime += "/" + endDateNoQuote;
            }
            else
            {
                wmsTime = "";
            }
        }

        var idx = 0;

        wmsParams["time"] = wmsTime;

        var tempName = "";

        for(idx = 0; idx < numberOfNames; ++idx )
        {
            tempName = "searchTagNames[" + idx + "]";
            wmsParams["searchTagNames[" + idx + "]"] = $(tempName).value;
        }

        for(idx = 0; idx < numberOfValues; ++idx)
        {
            tempName = "searchTagValues[" + idx + "]";
            wmsParams["searchTagValues[" + idx + "]"] = $(tempName).value;
        }

        dataLayer.mergeNewParams(wmsParams);
    };

    this.generateKML = function()
    {
        document.searchForm.action = "kmlnetworklink";
        this.setCurrentViewport();
        document.searchForm.submit();
    };

    this.setCurrentViewport = function()
    {
        var bounds = map.getExtent();
        $("viewMinLon").value = bounds.left;
        $("viewMaxLat").value = bounds.top;
        $("viewMaxLon").value = bounds.right;
        $("viewMinLat").value = bounds.bottom;
    };

    this.updateFootprints = function()
    {
        dataLayer.redraw(true);
    };

    this.search = function()
    {
        var latDmsRegExp = /^(\d{1,2})\s?(\d{2})\s?(\d{2})(.\d+)?\s?([NnSs])?/
        var lonDmsRegExp = /^(\d{1,3})\s?(\d{2})\s?(\d{2})(.\d+)?\s?([EeWw])?/

        // Convert DMS Center to DD
        if($("centerLat").value.match(latDmsRegExp) && $("centerLon").value.match(lonDmsRegExp))
        {
            if($("centerLat").value.match(latDmsRegExp))
            {
                var ctrLatDeg = parseInt(RegExp.$1, 10);
                var ctrLatMin = parseInt(RegExp.$2, 10);
                var ctrLatSec = parseInt(RegExp.$3, 10) + RegExp.$4;
                var ctrLatHem = RegExp.$5;

                if(ctrLatHem == "S" || ctrLatHem == "s")
                {
                    ctrLatDeg = -ctrLatDeg;
                }

                $("centerLat").value = convert.dmsToDd(ctrLatDeg, ctrLatMin, ctrLatSec);
            }

            if($("centerLon").value.match(lonDmsRegExp))
            {
                var ctrLonDeg = parseInt(RegExp.$1, 10);
                var ctrLonMin = parseInt(RegExp.$2, 10);
                var ctrLonSec = parseInt(RegExp.$3, 10) + RegExp.$4;
                var ctrLonHem = RegExp.$5;

                if(ctrLonHem == "W" || ctrLonHem == "w")
                {
                    ctrLonDeg = -ctrLonDeg;
                }

                $("centerLon").value = convert.dmsToDd(ctrLonDeg, ctrLonMin, ctrLonSec);
            }
        }

        // Convert DMS Bounds to DD
        var aoiMaxLatRegExp = /^(\d{1,2})\s?(\d{2})\s?(\d{2})(.\d+)?\s?([NnSs])?/
        var aoiMinLonRegExp = /^(\d{1,3})\s?(\d{2})\s?(\d{2})(.\d+)?\s?([EeWw])?/
        var aoiMinLatRegExp = /^(\d{1,2})\s?(\d{2})\s?(\d{2})(.\d+)?\s?([NnSs])?/
        var aoiMaxLonRegExp = /^(\d{1,3})\s?(\d{2})\s?(\d{2})(.\d+)?\s?([EeWw])?/

        if($("aoiMaxLat").value.match(aoiMaxLatRegExp))
        {
            var aoiMaxLatDeg = parseInt(RegExp.$1, 10);
            var aoiMaxLatMin = parseInt(RegExp.$2, 10);
            var aoiMaxLatSec = parseInt(RegExp.$3, 10) + RegExp.$4;
            var aoiMaxLatHem = RegExp.$5;

            if(aoiMaxLatHem == "S" || aoiMaxLatHem == "s")
            {
                aoiMaxLatDeg = -aoiMaxLatDeg;
            }

            $("aoiMaxLat").value = convert.dmsToDd(aoiMaxLatDeg, aoiMaxLatMin, aoiMaxLatSec);
        }

        if($("aoiMinLon").value.match(aoiMinLonRegExp))
        {
            var aoiMinLonDeg = parseInt(RegExp.$1, 10);
            var aoiMinLonMin = parseInt(RegExp.$2, 10);
            var aoiMinLonSec = parseInt(RegExp.$3, 10) + RegExp.$4;
            var aoiMinLonHem = RegExp.$5;

            if(aoiMinLonHem == "W" || aoiMinLonHem == "w")
            {
                aoiMinLonDeg = -aoiMinLonDeg;
            }

            $("aoiMinLon").value = convert.dmsToDd(aoiMinLonDeg, aoiMinLonMin, aoiMinLonSec);
        }

        if($("aoiMinLat").value.match(aoiMinLatRegExp))
        {
            var aoiMinLatDeg = parseInt(RegExp.$1, 10);
            var aoiMinLatMin = parseInt(RegExp.$2, 10);
            var aoiMinLatSec = parseInt(RegExp.$3, 10) + RegExp.$4;
            var aoiMinLatHem = RegExp.$5;

            if(aoiMinLatHem == "S" || aoiMinLonHem == "s")
            {
                aoiMinLatDeg = -aoiMinLatDeg;
            }

            $("aoiMinLat").value = convert.dmsToDd(aoiMinLatDeg, aoiMinLatMin, aoiMinLatSec);
        }

        if($("aoiMaxLon").value.match(aoiMaxLonRegExp))
        {
            var aoiMaxLonDeg = parseInt(RegExp.$1, 10);
            var aoiMaxLonMin = parseInt(RegExp.$2, 10);
            var aoiMaxLonSec = parseInt(RegExp.$3, 10) + RegExp.$4;
            var aoiMaxLonHem = RegExp.$5;

            if(aoiMaxLonHem == "W" || aoiMaxLonHem == "w")
            {
                aoiMaxLonDeg = -aoiMaxLonDeg;
            }

            $("aoiMaxLon").value = convert.dmsToDd(aoiMaxLonDeg, aoiMaxLonMin, aoiMaxLonSec);
        }

        document.searchForm.action = "search";
        this.setCurrentViewport();
        document.searchForm.submit();
    };

//    this.goto = function()
//    {
//        var latDdRegExp = /^(\-?\d{1,2})(\.\d+)?$/
//        var lonDdRegExp = /^(\-?\d{1,3})(\.\d+)?$/
//
//        var latDmsRegExp = /^(\d{1,2})\s?(\d{2})\s?(\d{2})(.\d+)?\s?([NnSs])?/
//        var lonDmsRegExp = /^(\d{1,3})\s?(\d{2})\s?(\d{2})(.\d+)?\s?([EeWw])?/
//
//        if($("centerLat").value.match(latDdRegExp) && $("centerLon").value.match(lonDdRegExp))
//        {
//            this.setMapCenter($("centerLat").value, $("centerLon").value );
//        }
//        else if($("centerLat" ).value.match(latDmsRegExp ) && $("centerLon").value.match(lonDmsRegExp))
//        {
//            if($("centerLat").value.match(latDmsRegExp))
//            {
//                var latDeg = parseInt(RegExp.$1, 10);
//                var latMin = parseInt(RegExp.$2, 10);
//                var latSec = parseInt(RegExp.$3, 10) + RegExp.$4;
//                var latHem = RegExp.$5;
//
//                if(latHem == "S" || latHem == "s")
//                {
//                    latDeg = -latDeg;
//                }
//            }
//
//            if($("centerLon").value.match(lonDmsRegExp))
//            {
//                var lonDeg = parseInt(RegExp.$1, 10);
//                var lonMin = parseInt(RegExp.$2, 10);
//                var lonSec = parseInt(RegExp.$3, 10) + RegExp.$4;
//                var lonHem = RegExp.$5;
//
//                if(lonHem == "W" || lonHem == "w")
//                {
//                    lonDeg = -lonDeg;
//                }
//            }
//
//            this.setMapCenter(convert.dmsToDd(latDeg, latMin, latSec), convert.dmsToDd(lonDeg, lonMin, lonSec));
//        }
//        else
//        {
//            alert("Invalid DMS Format.\n\n" +
//                  "Valid Examples: \n" +
//                  "DDDMMSS.SSS[NnSsEeWw]\n\n" +
//                  "0 00 00.000 N\n" +
//                  "00000.000N\n" +
//                  "12 34 56.123 N\n" +
//                  "123456.123N\n" +
//                  "90 00 00 N\n" +
//                  "900000N\n" +
//                  "90 00 00 S\n" +
//                  "900000S\n" +
//                  "123 46 07.891 E\n" +
//                  "1234607.891E\n" +
//                  "180 00 00 E\n" +
//                  "1800000E\n" +
//                  "180 00 00 W\n" +
//                  "1800000W");
//        }
//    };

    this.setMapCenter = function( latitude, longitude )
    {
        var zoom = map.getZoom( );
        var center = new OpenLayers.LonLat(longitude, latitude);

        map.setCenter(center, zoom);
    };
}