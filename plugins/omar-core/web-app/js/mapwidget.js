function MapWidget()
{
    var map = null;

    this.setupMapWidget = function()
    {
        map = new OpenLayers.Map("map", {controls: []});

        map.addControl(new OpenLayers.Control.LayerSwitcher());
        map.addControl(new OpenLayers.Control.Scale());
        map.addControl(new OpenLayers.Control.ScaleLine());

        map.events.register("click", map, this.handleMouseClick);
        map.events.register("mousemove", map, this.handleMouseHover);
        map.events.register("moveend", map, this.setPointRadiusText);
        map.events.register("zoomend", map, this.setBoundLatLonText);
    };

    var convert = new CoordinateConversion();

    this.handleMouseClick = function(evt)
    {
        var lonLat = map.getLonLatFromViewPortPx(new OpenLayers.Pixel(evt.xy.x, evt.xy.y));

        var mouseClickDd = document.getElementById("mouseClickDdOutput");
        var mouseClickDms = document.getElementById("mouseClickDmsOutput");
        var mouseClickMgrs = document.getElementById("mouseClickMgrsOutput");

        var geoExtentError = "Outside Geographic Extents.";

        if (lonLat.lat > "90" || lonLat.lat < "-90" || lonLat.lon > "180" || lonLat.lon < "-180")
        {
            mouseClickDd.innerHTML = "<b>DD:</b> " + geoExtentError;
            mouseClickDms.innerHTML = "<b>DMS:</b> " + geoExtentError;
            mouseClickMgrs.innerHTML = "<b>MGRS:</b> " + geoExtentError;
        }

        else
        {
            mouseClickDd.innerHTML = "<b>DD:</b> " + lonLat.lat + " " + lonLat.lon;
            mouseClickDms.innerHTML = "<b>DMS:</b> " + convert.ddToDms(lonLat.lat, "lat") + " " + convert.ddToDms(lonLat.lon, "lon");
            mouseClickMgrs.innerHTML = "<b>MGRS:</b> " + convert.ddToMgrs(lonLat.lat, lonLat.lon);
        }
    };

    this.handleMouseHover = function(evt)
    {
        var lonLat = map.getLonLatFromViewPortPx(new OpenLayers.Pixel(evt.xy.x, evt.xy.y));

        var mouseHoverDd = document.getElementById("mouseHoverDdOutput");
        var mouseHoverDms = document.getElementById("mouseHoverDmsOutput");
        var mouseHoverMgrs = document.getElementById("mouseHoverMgrsOutput");

        var geoExtentError = "Outside Geographic Extents.";

        if (lonLat.lat > "90" || lonLat.lat < "-90" || lonLat.lon > "180" || lonLat.lon < "-180")
        {
            mouseHoverDd.innerHTML = "<b>DD:</b> " + geoExtentError;
            mouseHoverDms.innerHTML = "<b>DMS:</b> " + geoExtentError;
            mouseHoverMgrs.innerHTML = "<b>MGRS:</b> " + geoExtentError;
        }

        else
        {
            mouseHoverDd.innerHTML = "<b>DD:</b> " + lonLat.lat + " " + lonLat.lon;
            mouseHoverDms.innerHTML = "<b>DMS:</b> " + convert.ddToDms(lonLat.lat, "lat") + " " + convert.ddToDms(lonLat.lon, "lon");
            mouseHoverMgrs.innerHTML = "<b>MGRS:</b> " + convert.ddToMgrs(lonLat.lat, lonLat.lon);
        }
    };

    this.setPointRadiusText = function()
    {
        var center = map.getCenter();

        $("centerLat").value = center.lat;
        $("centerLon").value = center.lon;

        $("centerLatDms").value = convert.ddToDms(center.lat, "lat");
        $("centerLonDms").value = convert.ddToDms(center.lon, "lon");

        $("centerMgrs").value = convert.ddToMgrs(center.lat, center.lon);
    };

    this.setCenterDd = function()
    {
        var latRegExpDd = /^(\-?\d{1,2})(\.\d+)?$/
        var lonRegExpDd = /^(\-?\d{1,3})(\.\d+)?$/

        if ($("centerLat").value.match(latRegExpDd) && $("centerLon").value.match(lonRegExpDd))
        {
            this.setMapCenter($("centerLat").value, $("centerLon").value);
        }
                    
        $("aoiRadius2").value = $("aoiRadius").value;
        $("aoiRadius3").value = $("aoiRadius").value;
    };

    this.setCenterDms = function()
    {
        var latRegExpDms = /^(\d{1,2})\s?(\d{2})\s?(\d{2})(.\d+)?\s?([NnSs])?/
        var lonRegExpDms = /^(\d{1,3})\s?(\d{2})\s?(\d{2})(.\d+)?\s?([EeWw])?/

        if ($("centerLatDms").value.match(latRegExpDms) && $("centerLonDms").value.match(lonRegExpDms))
        {
            if ($("centerLatDms").value.match(latRegExpDms))
            {
                var latDeg = parseInt(RegExp.$1, 10);
                var latMin = parseInt(RegExp.$2, 10);
                var latSec = parseInt(RegExp.$3, 10) + RegExp.$4;
                var latHem = RegExp.$5;

                if (latHem == "S" || latHem == "s")
                {
                    latDeg = -latDeg;
                }
            }

            if ($("centerLonDms").value.match(lonRegExpDms))
            {
                var lonDeg = parseInt(RegExp.$1, 10);
                var lonMin = parseInt(RegExp.$2, 10);
                var lonSec = parseInt(RegExp.$3, 10) + RegExp.$4;
                var lonHem = RegExp.$5;

                if (lonHem == "W" || lonHem == "w")
                {
                    lonDeg = -lonDeg;
                }
            }

            this.setMapCenter(convert.dmsToDd(latDeg, latMin, latSec), convert.dmsToDd(lonDeg, lonMin, lonSec));
        }

        $("aoiRadius").value = $("aoiRadius2").value;
        $("aoiRadius3").value = $("aoiRadius2").value;
    };

    this.setCenterMgrs = function()
    {
        var mgrsRegExpUtm = /^(-?\d{1,2})(\.\d+)?\s?(-?\d{1,3})(\.\d+)?/

        var centerMgrsToUtm = convert.mgrsToUtm($("centerMgrs" ).value);

        if (centerMgrsToUtm.match(mgrsRegExpUtm))
        {
            var centerLat = parseInt(RegExp.$1, 10) + RegExp.$2;
            var centerLon = parseInt(RegExp.$3, 10) + RegExp.$4;

            this.setMapCenter(centerLat, centerLon);
        }

        $("aoiRadius").value = $("aoiRadius3").value;
        $("aoiRadius2").value = $("aoiRadius3").value;
    };

    var aoiLayer = null;

    this.setupAoiLayer = function()
    {
        aoiLayer = new OpenLayers.Layer.Vector("Bound Box AOI");
        aoiLayer.events.register("featureadded", aoiLayer, this.setAoi);

        var boundBox = new OpenLayers.Control.DrawFeature(aoiLayer, OpenLayers.Handler.RegularPolygon,
        {handlerOptions: {sides: 4, irregular: true}});

        map.addLayer(aoiLayer);
        map.addControl(boundBox);

        var aoiMinLon = "${queryParams?.aoiMinLon ?: ''}";
        var aoiMinLat = "${queryParams?.aoiMinLat ?: ''}";
        var aoiMaxLon = "${queryParams?.aoiMaxLon ?: ''}";
        var aoiMaxLat = "${queryParams?.aoiMaxLat ?: ''}";

        if (aoiMinLon && aoiMinLat && aoiMaxLon && aoiMaxLat)
        {
            var bounds = new OpenLayers.Bounds( aoiMinLon, aoiMinLat, aoiMaxLon, aoiMaxLat );
            var feature = new OpenLayers.Feature.Vector( bounds.toGeometry( ) );

            aoiLayer.addFeatures(feature, {silent: true});
        }
    };

    this.setAoi = function(e)
    {
        var geom = e.feature.geometry;
        var bounds = geom.getBounds();
        var feature = new OpenLayers.Feature.Vector(geom);

        $("aoiMinLon").value = bounds.left;
        $("aoiMaxLat").value = bounds.top;
        $("aoiMaxLon").value = bounds.right;
        $("aoiMinLat").value = bounds.bottom;

        $("aoiMinLonDms").value = convert.ddToDms(bounds.left, "lon");
        $("aoiMaxLatDms").value = convert.ddToDms(bounds.top, "lat");
        $("aoiMaxLonDms").value = convert.ddToDms(bounds.right, "lon");
        $("aoiMinLatDms").value = convert.ddToDms(bounds.bottom, "lat");

        $("aoiNeMgrs").value = convert.ddToMgrs(bounds.top, bounds.right);
        $("aoiSwMgrs").value = convert.ddToMgrs(bounds.bottom, bounds.left);

        aoiLayer.destroyFeatures();
        aoiLayer.addFeatures(feature, {silent: true});
    };

    this.initAOI = function(minLon, minLat, maxLon, maxLat)
    {
        var bounds = new OpenLayers.Bounds(minLon, minLat, maxLon, maxLat);
        var feature = new OpenLayers.Feature.Vector(bounds.toGeometry());

        aoiLayer.destroyFeatures();
        aoiLayer.addFeatures(feature, {silent: true});
    };

    this.clearAOI = function(e)
    {
        aoiLayer.destroyFeatures();

        $("aoiMinLon").value = "";
        $("aoiMaxLat").value = "";
        $("aoiMaxLon").value = "";
        $("aoiMinLat").value = "";

        $("aoiMinLonDms").value = "";
        $("aoiMaxLatDms").value = "";
        $("aoiMaxLonDms").value = "";
        $("aoiMinLatDms").value = "";

        $("aoiNeMgrs").value = "";
        $("aoiSwMgrs").value = "";
    };

    this.search = function()
    {
        if ($("units").value == "DD" || $("units").value == "")
        {

        }

        else if ($("units").value == "DMS")
        {
            var latRegExpDms = /^(\d{1,2})\s?(\d{2})\s?(\d{2})(.\d+)?\s?([NnSs])?/
            var lonRegExpDms = /^(\d{1,3})\s?(\d{2})\s?(\d{2})(.\d+)?\s?([EeWw])?/

            if ($("centerLatDms").value.match(latRegExpDms) && $("centerLonDms").value.match(lonRegExpDms))
            {
                if ($("centerLatDms").value.match(latRegExpDms))
                {
                    var latDeg = parseInt(RegExp.$1, 10);
                    var latMin = parseInt(RegExp.$2, 10);
                    var latSec = parseInt(RegExp.$3, 10) + RegExp.$4;
                    var latHem = RegExp.$5;

                    if (latHem == "S" || latHem == "s")
                    {
                        latDeg = -latDeg;
                    }
                }

                if ($("centerLonDms").value.match(lonRegExpDms))
                {
                    var lonDeg = parseInt(RegExp.$1, 10);
                    var lonMin = parseInt(RegExp.$2, 10);
                    var lonSec = parseInt(RegExp.$3, 10) + RegExp.$4;
                    var lonHem = RegExp.$5;

                    if (lonHem == "W" || lonHem == "w")
                    {
                        lonDeg = -lonDeg;
                    }
                }

                $("centerLat").value = convert.dmsToDd(latDeg, latMin, latSec);
                $("centerLon").value = convert.dmsToDd(lonDeg, lonMin, lonSec);
            }

            var maxLatRegExpDms = /^(\d{1,2})\s?(\d{2})\s?(\d{2})(.\d+)?\s?([NnSs])?/
            var minLonRegExpDms = /^(\d{1,3})\s?(\d{2})\s?(\d{2})(.\d+)?\s?([EeWw])?/
            var minLatRegExpDms = /^(\d{1,2})\s?(\d{2})\s?(\d{2})(.\d+)?\s?([NnSs])?/
            var maxLonRegExpDms = /^(\d{1,3})\s?(\d{2})\s?(\d{2})(.\d+)?\s?([EeWw])?/

            if ($("aoiMaxLatDms").value.match(maxLatRegExpDms))
            {
                var maxLatDeg = parseInt(RegExp.$1, 10);
                var maxLatMin = parseInt(RegExp.$2, 10);
                var maxLatSec = parseInt(RegExp.$3, 10) + RegExp.$4;
                var maxLatHem = RegExp.$5;

                if (maxLatHem == "S" || maxLatHem == "s")
                {
                    maxLatDeg = -maxLatDeg;
                }

                $("aoiMaxLat").value = convert.dmsToDd(maxLatDeg, maxLatMin, maxLatSec);
            }

            if ($("aoiMinLonDms").value.match(minLonRegExpDms))
            {
                var minLonDeg = parseInt(RegExp.$1, 10);
                var minLonMin = parseInt(RegExp.$2, 10);
                var minLonSec = parseInt(RegExp.$3, 10) + RegExp.$4;
                var minLonHem = RegExp.$5;

                if (minLonHem == "W" || minLonHem == "w" )
                {
                    minLonDeg = -minLonDeg;
                }

                $("aoiMinLon").value = convert.dmsToDd(minLonDeg, minLonMin, minLonSec);
            }

            if ($("aoiMinLatDms").value.match(minLatRegExpDms))
            {
                var minLatDeg = parseInt(RegExp.$1, 10);
                var minLatMin = parseInt(RegExp.$2, 10);
                var minLatSec = parseInt(RegExp.$3, 10) + RegExp.$4;
                var minLatHem = RegExp.$5;

                if (minLatHem == "S" || minLonHem == "s")
                {
                    minLatDeg = -minLatDeg;
                }

                $("aoiMinLat").value = convert.dmsToDd(minLatDeg, minLatMin, minLatSec);
            }

            if ($("aoiMaxLonDms").value.match(maxLonRegExpDms))
            {
                var maxLonDeg = parseInt(RegExp.$1, 10);
                var maxLonMin = parseInt(RegExp.$2, 10);
                var maxLonSec = parseInt(RegExp.$3, 10) + RegExp.$4;
                var maxLonHem = RegExp.$5;

                if (maxLonHem == "W" || maxLonHem == "w" )
                {
                    maxLonDeg = -maxLonDeg;
                }

                $("aoiMaxLon").value = convert.dmsToDd(maxLonDeg, maxLonMin, maxLonSec);
            }
        }

        else if ($("units").value == "MGRS")
        {
            var mgrsRegExpUtm = /^(-?\d{1,2})(\.\d+)?\s?(-?\d{1,3})(\.\d+)?/

            var centerMgrsToUtm = convert.mgrsToUtm($("centerMgrs" ).value);

            if (centerMgrsToUtm.match(mgrsRegExpUtm))
            {
                var centerLat = parseInt(RegExp.$1, 10) + RegExp.$2;
                var centerLon = parseInt(RegExp.$3, 10) + RegExp.$4;

                $("centerLat").value = centerLat;
                $("centerLon").value = centerLon;
            }

            var regExpMgrs = /^(\d{1,2})([a-zA-Z])([a-zA-Z])([a-zA-Z])(\d{10})?/

            if ($("aoiNeMgrs").value.match(regExpMgrs) && $("aoiSwMgrs").value.match(regExpMgrs))
            {
                var neUtm = convert.mgrsToUtm($("aoiNeMgrs").value);

                if (neUtm.match(mgrsRegExpUtm))
                {
                    var maxLat = parseInt(RegExp.$1, 10) + RegExp.$2;
                    var maxLon = parseInt(RegExp.$3, 10) + RegExp.$4;

                    $("aoiMaxLat").value = maxLat;
                    $("aoiMaxLon").value = maxLon;
                }

                var swUtm = convert.mgrsToUtm($("aoiSwMgrs").value );

                if (swUtm.match(mgrsRegExpUtm))
                {
                    var minLat = parseInt(RegExp.$1, 10) + RegExp.$2;
                    var minLon = parseInt(RegExp.$3, 10) + RegExp.$4;

                    $("aoiMinLat").value = minLat;
                    $("aoiMinLon").value = minLon;
                }
            }
        }

        document.searchForm.action = "search";
        this.setCurrentViewport();
        document.searchForm.submit();
    };

    this.baseLayer = null;

    this.setupBaseLayers = function(baseLayer)
    {
        map.addLayer(baseLayer);
        map.setBaseLayer(baseLayer);
    };

    this.dataLayer = null;

    this.setupDataLayer = function(dataWmsTitle, dataWmsUrl, dataWmsLayers, dataWmsStyles, dataWmsFormat)
    {
        dataLayer = new OpenLayers.Layer.WMS(dataWmsTitle, dataWmsUrl,
        {layers: dataWmsLayers, styles: dataWmsStyles, format: dataWmsFormat, transparent: true},
        {isBaseLayer: false, buffer: 0, visibility: true, transitionEffect: "resize"});

        map.addLayer(dataLayer);
    };

    this.updateFootprints = function()
    {
        dataLayer.redraw(true);
    };

    this.setMapCenter = function(lat, lon)
    {
        var zoom = map.getZoom();
        var center = new OpenLayers.LonLat(lon, lat);

        map.setCenter(center, zoom);
    };

    this.setCurrentViewport = function()
    {
        var bounds = map.getExtent();

        $("viewMinLon").value = bounds.left;
        $("viewMaxLat").value = bounds.top;
        $("viewMaxLon").value = bounds.right;
        $("viewMinLat").value = bounds.bottom;
    };

    this.generateKML = function()
    {
        document.searchForm.action = "kmlnetworklink";
        this.setCurrentViewport();
        document.searchForm.submit();
    };

    this.changeMapSize = function()
    {     
        var mapTitle = $("mapTitle");
        var mapDiv = $("map");

        mapDiv.style.width = mapTitle.offsetWidth + "px";
        mapDiv.style.height = Math.round(mapTitle.offsetWidth / 2) + "px";

        map.updateSize();
    };

    this.setupMapView = function(viewMinLon, viewMinLat, viewMaxLon, viewMaxLat)
    {
        var bounds = new OpenLayers.Bounds(viewMinLon, viewMinLat, viewMaxLon, viewMaxLat);
        var zoom = map.getZoomForExtent(bounds, true);

        map.setCenter(bounds.getCenterLonLat(), zoom);
    };

    this.setupToolBar = function()
    {
        var panButton = new OpenLayers.Control.MouseDefaults(
        {title: "Click and drag to pan map."});

        var zoomInButton = new OpenLayers.Control.Button(
        {title: "Click to zoom in.", displayClass: "olControlZoomIn", trigger: this.zoomIn});

        var zoomOutButton = new OpenLayers.Control.Button(
        {title: "Click to zoom out.", displayClass: "olControlZoomOut", trigger: this.zoomOut});

        var zoomMaxExtentButton = new OpenLayers.Control.ZoomToMaxExtent(
        {title: "Click to zoom to the max extent."});

        var zoomBoxButton = new OpenLayers.Control.ZoomBox(
        {title: "Click and drag to zoom into an area."});

        var boundBoxButton = new OpenLayers.Control.DrawFeature(aoiLayer, OpenLayers.Handler.RegularPolygon,
        {handlerOptions: {sides: 4, irregular: true}, title: "Click and drag to specify an area of interest."});

        var clearAoiButton = new OpenLayers.Control.Button(
        {title: "Click to clear area of interest", displayClass: "olControlClearAreaOfInterest", trigger: this.clearAOI});

        var measureDistanceButton = new OpenLayers.Control.Measure(OpenLayers.Handler.Path,
        {title: "Measure Distance", displayClass: "olControlMeasureDistance", persist: true,
            eventListeners: {measure: function( evt )
            {
                var pathMeasurement = document.getElementById("pathMeasurementOutput");

                if ($("pathUnits").value == "centimeter")
                {
                    if (evt.units == "km")
                    {
                        pathMeasurement.innerHTML = "<b>Path:</b> " + (evt.measure.toFixed(3) * 100000) + " cm<br/>" +
                                "[<a href=javascript:mapWidget.clearPathMeasurement();>Clear Path</a>]";
                    }

                    else if (evt.units == "m")
                    {
                        pathMeasurement.innerHTML = "<b>Path:</b> " + (evt.measure.toFixed(3) * 100) + " cm<br/>" +
                                "[<a href=javascript:mapWidget.clearPathMeasurement();>Clear Path</a>]";
                    }
                }

                else if ($("pathUnits").value == "feet")
                {
                    if (evt.units == "km")
                    {
                        pathMeasurement.innerHTML = "<b>Path:</b> " + (evt.measure.toFixed(3) * 3280.839895) + " ft<br/>" +
                                "[<a href=javascript:mapWidget.clearPathMeasurement();>Clear Path</a>]";
                    }

                    else if (evt.units == "m")
                    {
                        pathMeasurement.innerHTML = "<b>Path:</b> " + (evt.measure.toFixed(3) * 3.280839895) + " ft<br/>" +
                                "[<a href=javascript:mapWidget.clearPathMeasurement();>Clear Path</a>]";
                    }
                }

                else if ($("pathUnits").value == "inch")
                {
                    if (evt.units == "km")
                    {
                        pathMeasurement.innerHTML = "<b>Path:</b> " + (evt.measure.toFixed(3) * 39370.07874) + " in<br/>" +
                                "[<a href=javascript:mapWidget.clearPathMeasurement();>Clear Path</a>]";
                    }

                    else if (evt.units == "m")
                    {
                        pathMeasurement.innerHTML = "<b>Path:</b> " + (evt.measure.toFixed(3) * 39.37007874) + " in<br/>" +
                                "[<a href=javascript:mapWidget.clearPathMeasurement();>Clear Path</a>]";
                    }
                }

                else if ($("pathUnits").value == "kilometer")
                {
                    if (evt.units == "km")
                    {
                        pathMeasurement.innerHTML = "<b>Path:</b> " + (evt.measure.toFixed(3) * 1) + " km<br/>" +
                                "[<a href=javascript:mapWidget.clearPathMeasurement();>Clear Path</a>]";
                    }

                    else if (evt.units == "m")
                    {
                        pathMeasurement.innerHTML = "<b>Path:</b> " + (evt.measure.toFixed(3) * 0.001) + " km<br/>" +
                                "[<a href=javascript:mapWidget.clearPathMeasurement();>Clear Path</a>]";
                    }
                }

                else if ($("pathUnits").value == "league")
                {
                    if (evt.units == "km")
                    {
                        pathMeasurement.innerHTML = "<b>Path:</b> " + (evt.measure.toFixed(3) * 0.20712331461) + " l<br/>" +
                                "[<a href=javascript:mapWidget.clearPathMeasurement();>Clear Path</a>]";
                    }

                    else if (evt.units == "m")
                    {
                        pathMeasurement.innerHTML = "<b>Path:</b> " + (evt.measure.toFixed(3) * 0.00020712331461) + " l<br/>" +
                                "[<a href=javascript:mapWidget.clearPathMeasurement();>Clear Path</a>]";
                    }
                }

                else if ($("pathUnits").value == "nautical league")
                {
                    if (evt.units == "km")
                    {
                        pathMeasurement.innerHTML = "<b>Path:</b> " + (evt.measure.toFixed(3) * 0.17998560115) + " nl<br/>" +
                                "[<a href=javascript:mapWidget.clearPathMeasurement();>Clear Path</a>]";
                    }

                    else if (evt.units == "m")
                    {
                        pathMeasurement.innerHTML = "<b>Path:</b> " + (evt.measure.toFixed(3) * 0.00017998560115) + " nl<br/>" +
                                "[<a href=javascript:mapWidget.clearPathMeasurement();>Clear Path</a>]";
                    }
                }

                else if ($("pathUnits").value == "meter")
                {
                    if (evt.units == "km")
                    {
                        pathMeasurement.innerHTML = "<b>Path:</b> " + (evt.measure.toFixed(3) * 1000) + " m<br/>" +
                                "[<a href=javascript:mapWidget.clearPathMeasurement();>Clear Path</a>]";
                    }

                    else if (evt.units == "m")
                    {
                        pathMeasurement.innerHTML = "<b>Path:</b> " + (evt.measure.toFixed(3) * 1) + " m<br/>" +
                                "[<a href=javascript:mapWidget.clearPathMeasurement();>Clear Path</a>]";
                    }
                }

                else if ($("pathUnits").value == "microinch")
                {
                    if (evt.units == "km")
                    {
                        pathMeasurement.innerHTML = "<b>Path:</b> " + (evt.measure.toFixed(3) * 39370078740) + " micro in<br/>" +
                                "[<a href=javascript:mapWidget.clearPathMeasurement();>Clear Path</a>]";
                    }

                    else if (evt.units == "m")
                    {
                        pathMeasurement.innerHTML = "<b>Path:</b> " + (evt.measure.toFixed(3) * 39370078.74) + " micro in<br/>" +
                                "[<a href=javascript:mapWidget.clearPathMeasurement();>Clear Path</a>]";
                    }
                }

                else if ($("pathUnits").value == "mile")
                {
                    if (evt.units == "km")
                    {
                        pathMeasurement.innerHTML = "<b>Path:</b> " + (evt.measure.toFixed(3) * 0.62137119224) + " mi<br/>" +
                                "[<a href=javascript:mapWidget.clearPathMeasurement();>Clear Path</a>]";
                    }

                    else if (evt.units == "m")
                    {
                        pathMeasurement.innerHTML = "<b>Path:</b> " + (evt.measure.toFixed(3) * 0.00062137119224) + " mi<br/>" +
                                "[<a href=javascript:mapWidget.clearPathMeasurement();>Clear Path</a>]";
                    }
                }

                else if ($("pathUnits").value == "millimeter")
                {
                    if (evt.units == "km")
                    {
                        pathMeasurement.innerHTML = "<b>Path:</b> " + (evt.measure.toFixed(3) * 1000000) + " ml<br/>" +
                                "[<a href=javascript:mapWidget.clearPathMeasurement();>Clear Path</a>]";
                    }

                    else if (evt.units == "m")
                    {
                        pathMeasurement.innerHTML = "<b>Path:</b> " + (evt.measure.toFixed(3) * 1000) + " ml<br/>" +
                                "[<a href=javascript:mapWidget.clearPathMeasurement();>Clear Path</a>]";
                    }
                }

                else if ($("pathUnits").value == "yard")
                {
                    if (evt.units == "km")
                    {
                        pathMeasurement.innerHTML = "<b>Path:</b> " + (evt.measure.toFixed(3) * 1093.6132983) + " yd<br/>" +
                                "[<a href=javascript:mapWidget.clearPathMeasurement();>Clear Path</a>]";
                    }

                    else if (evt.units == "m")
                    {
                        pathMeasurement.innerHTML = "<b>Path:</b> " + (evt.measure.toFixed(3) * 1.0936132983) + " yd<br/>" +
                                "[<a href=javascript:mapWidget.clearPathMeasurement();>Clear Path</a>]";
                    }
                }
            }}});

        var measureAreaButton = new OpenLayers.Control.Measure( OpenLayers.Handler.Polygon,
        {title: "Measure Area", displayClass: "olControlMeasureArea", persist: true,
            eventListeners: {measure: function(evt)
            {
                var areaMeasurement = document.getElementById("areaMeasurementOutput");

                areaMeasurement.innerHTML = "<b>Area:</b> " + evt.measure.toFixed(3) + evt.units + "<br/>" +
                                "[<a href=javascript:mapWidget.clearAreaMeasurement();>Clear Area</a>]";
            }}});

        var container = $("toolBar");

        var toolBar = new OpenLayers.Control.Panel(
        {div: container,defaultControl: panButton, displayClass: "olControlPanel"});

        toolBar.addControls([
            panButton,

            zoomInButton,
            zoomOutButton,
            zoomMaxExtentButton,
            zoomBoxButton,

            boundBoxButton,
            clearAoiButton,

            measureDistanceButton,
            measureAreaButton
        ]);

        map.addControl(toolBar);
    };

    this.zoomIn = function()
    {
        map.zoomIn();
    };

    this.zoomOut = function()
    {
        map.zoomOut();
    };

    this.clearPathMeasurement = function()
    {
        var pathMeasurement = document.getElementById("pathMeasurementOutput");

        pathMeasurement.innerHTML = "";
    };

    this.clearAreaMeasurement = function()
    {
        var areaMeasurement = document.getElementById("areaMeasurementOutput");

        areaMeasurement.innerHTML = "";
    };

    this.togglePointRadiusCheckBox = function()
    {                 
        $("radiusSearchButton").checked = true;
        $("radiusSearchButton2").checked = true;
        $("radiusSearchButton3").checked = true;

        $("bboxSearchButton").checked = false;
        $("bboxSearchButton2").checked = false;
        $("bboxSearchButton3").checked = false;
    };

    this.toggleBboxCheckBox = function()
    {
        $("bboxSearchButton").checked = true;
        $("bboxSearchButton2").checked = true;
        $("bboxSearchButton3").checked = true;

        $("radiusSearchButton").checked = false;
        $("radiusSearchButton2").checked = false;
        $("radiusSearchButton3").checked = false;
    };

    ///

  this.setBoundLatLonText = function()
  {                               /*
    if ( $( "units" ).value == "DD" )
    {

      var latDmsRegExp = /^(\d{1,2})\s?(\d{2})\s?(\d{2})(.\d+)?\s?([NnSs])?/
      var lonDmsRegExp = /^(\d{1,3})\s?(\d{2})\s?(\d{2})(.\d+)?\s?([EeWw])?/

      if ( $( "aoiMaxLat" ).value.match( latDmsRegExp ) )
      {
        var aoiMaxLatDeg = parseInt( RegExp.$1, 10 );
        var aoiMaxLatMin = parseInt( RegExp.$2, 10 );
        var aoiMaxLatSec = parseInt( RegExp.$3, 10 ) + RegExp.$4;
        var aoiMaxLatHem = RegExp.$5;

        if ( aoiMaxLatHem == "S" || aoiMaxLatHem == "s" )
        {
          aoiMaxLatDeg = -aoiMaxLatDeg;
        }

        $( "aoiMaxLat" ).value = convert.dmsToDd( aoiMaxLatDeg, aoiMaxLatMin, aoiMaxLatSec );
      }

      if ( $( "aoiMinLon" ).value.match( lonDmsRegExp ) )
      {
        var aoiMinLonDeg = parseInt( RegExp.$1, 10 );
        var aoiMinLonMin = parseInt( RegExp.$2, 10 );
        var aoiMinLonSec = parseInt( RegExp.$3, 10 ) + RegExp.$4;
        var aoiMinLonHem = RegExp.$5;

        if ( aoiMinLonHem == "W" || aoiMinLonHem == "w" )
        {
          aoiMinLonDeg = -aoiMinLonDeg;
        }

        $( "aoiMinLon" ).value = convert.dmsToDd( aoiMinLonDeg, aoiMinLonMin, aoiMinLonSec );
      }

      if ( $( "aoiMinLat" ).value.match( latDmsRegExp ) )
      {
        var aoiMinLatDeg = parseInt( RegExp.$1, 10 );
        var aoiMinLatMin = parseInt( RegExp.$2, 10 );
        var aoiMinLatSec = parseInt( RegExp.$3, 10 ) + RegExp.$4;
        var aoiMinLatHem = RegExp.$5;

        if ( aoiMinLatHem == "S" || aoiMinLatHem == "s" )
        {
          aoiMinLatDeg = -aoiMinLatDeg;
        }

        $( "aoiMinLat" ).value = convert.dmsToDd( aoiMinLatDeg, aoiMinLatMin, aoiMinLatSec );
      }

      if ( $( "aoiMaxLon" ).value.match( lonDmsRegExp ) )
      {
        var aoiMaxLonDeg = parseInt( RegExp.$1, 10 );
        var aoiMaxLonMin = parseInt( RegExp.$2, 10 );
        var aoiMaxLonSec = parseInt( RegExp.$3, 10 ) + RegExp.$4;
        var aoiMaxLonHem = RegExp.$5;

        if ( aoiMaxLonHem == "W" || aoiMaxLonHem == "w" )
        {
          aoiMaxLonDeg = -aoiMaxLonDeg;
        }

        $( "aoiMaxLon" ).value = convert.dmsToDd( aoiMaxLonDeg, aoiMaxLonMin, aoiMaxLonSec );
      }
    }

    else if ( $( "units" ).value == "DMS" )
    {

      var latDdRegExp = /^(\-?\d{1,2})(\.\d+)?$/
      var lonDdRegExp = /^(\-?\d{1,3})(\.\d+)?$/

      if ( $( "aoiMaxLat" ).value.match( latDdRegExp ) )
      {
        $( "aoiMaxLat" ).value = convert.ddToDms( $( "aoiMaxLat" ).value, "lat" );
      }

      if ( $( "aoiMinLon" ).value.match( lonDdRegExp ) )
      {
        $( "aoiMinLon" ).value = convert.ddToDms( $( "aoiMinLon" ).value, "lon" );
      }

      if ( $( "aoiMinLat" ).value.match( latDdRegExp ) )
      {
        $( "aoiMinLat" ).value = convert.ddToDms( $( "aoiMinLat" ).value, "lat" );
      }

      if ( $( "aoiMaxLon" ).value.match( lonDdRegExp ) )
      {
        $( "aoiMaxLon" ).value = convert.ddToDms( $( "aoiMaxLon" ).value, "lon" );
      }
    }

    else if ( $( "units" ).value == "MGRS" )
    {
      
    }   */
  };

    ///
    
  String.prototype.leftPad = function ( l, c )
  {
    return new Array( l - this.length + 1 ).join( c || '0' ) + this;
  };

  this.updateOmarFilters = function( startDay, startMonth, startYear, startHour, startMinute, endDay, endMonth, endYear, endHour, endMinute, numberOfNames, numberOfValues )
  {
    var wmsParams = new Array( );

    var hasStartDate = startDay != "" && startMonth != "" && startYear != "" && startHour != "" && startMinute != "";
    var startDateNoQuote = startYear + startMonth.leftPad( 2 ) + startDay.leftPad( 2 ) + 'T' + startHour.leftPad( 2 ) + ':' + startMinute.leftPad( 2 ) + ':' + '00Z';

    var hasEndDate = endDay != "" && endMonth != "" && endYear != "";
    var endDateNoQuote = endYear + endMonth.leftPad( 2 ) + endDay.leftPad( 2 ) + 'T' + endHour.leftPad( 2 ) + ':' + endMinute.leftPad( 2 ) + ':' + '00Z';

    var wmsTime = "";

    if ( hasStartDate )
    {
      wmsTime = startDateNoQuote;
      if ( hasEndDate )
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
      if ( hasEndDate )
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

    for ( idx = 0; idx < numberOfNames; ++idx )
    {
      tempName = "searchTagNames[" + idx + "]";
      wmsParams["searchTagNames[" + idx + "]"] = $( tempName ).value;
    }

    for ( idx = 0; idx < numberOfValues; ++idx )
    {
      tempName = "searchTagValues[" + idx + "]";
      wmsParams["searchTagValues[" + idx + "]"] = $( tempName ).value;
    }

    dataLayer.mergeNewParams( wmsParams );
  };
}