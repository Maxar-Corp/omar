function RasterVideo()
{
    
    this.setupMapWidget = function()
    {
        map = new OpenLayers.Map("map", {controls: []});

        map.addControl(new OpenLayers.Control.LayerSwitcher());
        map.addControl(new OpenLayers.Control.Scale());
        map.addControl(new OpenLayers.Control.ScaleLine());
        map.addControl(new OpenLayers.Control.Attribution());

        map.events.register("moveend", map, this.setCenterLatLonText);
        map.events.register("zoomend", map, this.setBoundLatLonText);
        map.events.register('mousemove', map, this.setMousePosition);
    };

    this.setTextFields = function()
    {
        this.setCenterLatLonText();
        this.setBoundLatLonText();
    };

    convert = new CoordinateConversion();

    this.setCenterLatLonText = function()
    {
        this.center = map.getCenter();

        // Decimal Degrees
        if($("unitsMode").value == "DD")
        {
            $("centerLat").value = this.center.lat;
            $("centerLon").value = this.center.lon;
        }

        // Degrees Minutes Seconds
        if($("unitsMode").value == "DMS")
        {
            $("centerLat").value = convert.ddToDms(this.center.lat, "latitude");
            $("centerLon").value = convert.ddToDms(this.center.lon, "longitude");
        }

        // Military Grid Reference System
        if($("unitsMode" ).value == "MGRS")
        {

        }
    };

    this.setBoundLatLonText = function()
    {
        // Decimal Degrees
        if($("unitsMode").value == "DD")
        {
            this.latDmsRegExp = /^(\d{1,2})\s?(\d{2})\s?(\d{2})(.\d+)?\s?([NnSs])?/
            this.lonDmsRegExp = /^(\d{1,3})\s?(\d{2})\s?(\d{2})(.\d+)?\s?([EeWw])?/

            if($("aoiMaxLat").value.match(this.latDmsRegExp))
            {
                this.aoiMaxLatDeg = parseInt(RegExp.$1, 10);
                this.aoiMaxLatMin = parseInt(RegExp.$2, 10);
                this.aoiMaxLatSec = parseInt(RegExp.$3, 10) + RegExp.$4;
                this.aoiMaxLatHem = RegExp.$5;

                if(this.aoiMaxLatHem == "S" || this.aoiMaxLatHem == "s")
                {
                    this.aoiMaxLatDeg = -this.aoiMaxLatDeg;
                }

                $("aoiMaxLat").value = convert.dmsToDd(this.aoiMaxLatDeg, this.aoiMaxLatMin, this.aoiMaxLatSec);
            }

            if($("aoiMinLon").value.match(this.lonDmsRegExp))
            {
                this.aoiMinLonDeg = parseInt(RegExp.$1, 10);
                this.aoiMinLonMin = parseInt(RegExp.$2, 10);
                this.aoiMinLonSec = parseInt(RegExp.$3, 10) + RegExp.$4;
                this.aoiMinLonHem = RegExp.$5;

                if(this.aoiMinLonHem == "W" || this.aoiMinLonHem == "w")
                {
                    this.aoiMinLonDeg = -this.aoiMinLonDeg;
                }

                $("aoiMinLon").value = convert.dmsToDd(this.aoiMinLonDeg, this.aoiMinLonMin, this.aoiMinLonSec);
            }

            if($("aoiMinLat").value.match(this.latDmsRegExp))
            {
                this.aoiMinLatDeg = parseInt(RegExp.$1, 10);
                this.aoiMinLatMin = parseInt(RegExp.$2, 10);
                this.aoiMinLatSec = parseInt(RegExp.$3, 10) + RegExp.$4;
                this.aoiMinLatHem = RegExp.$5;

                if(this.aoiMinLatHem == "S" || this.aoiMinLatHem == "s")
                {
                    this.aoiMinLatDeg = -this.aoiMinLatDeg;
                }

                $("aoiMinLat").value = convert.dmsToDd(this.aoiMinLatDeg, this.aoiMinLatMin, this.aoiMinLatSec );
            }

            if($("aoiMaxLon").value.match(this.lonDmsRegExp))
            {
                this.aoiMaxLonDeg = parseInt(RegExp.$1, 10);
                this.aoiMaxLonMin = parseInt(RegExp.$2, 10);
                this.aoiMaxLonSec = parseInt(RegExp.$3, 10) + RegExp.$4;
                this.aoiMaxLonHem = RegExp.$5;

                if(this.aoiMaxLonHem == "W" || this.aoiMaxLonHem == "w")
                {
                    this.aoiMaxLonDeg = -this.aoiMaxLonDeg;
                }

                $("aoiMaxLon").value = convert.dmsToDd(this.aoiMaxLonDeg, this.aoiMaxLonMin, this.aoiMaxLonSec);
            }
        }

        // Degrees Minutes Seconds
        if($("unitsMode").value == "DMS")
        {
            this.latDdRegExp = /^(\-?\d{1,2})(\.\d+)?$/
            this.lonDdRegExp = /^(\-?\d{1,3})(\.\d+)?$/

            if($("aoiMaxLat").value.match(this.latDdRegExp))
            {
                $("aoiMaxLat").value = convert.ddToDms($("aoiMaxLat").value, "latitude");
            }

            if($("aoiMinLon").value.match(this.lonDdRegExp))
            {
                $("aoiMinLon").value = convert.ddToDms($("aoiMinLon").value, "longitude");
            }

            if($("aoiMinLat").value.match(this.latDdRegExp))
            {
                $("aoiMinLat").value = convert.ddToDms($("aoiMinLat").value, "latitude");
            }

            if($("aoiMaxLon").value.match(this.lonDdRegExp))
            {
                $("aoiMaxLon").value = convert.ddToDms($("aoiMaxLon").value, "longitude");
            }
        }

        // Military Grid Reference System
        if($("unitsMode").value == "MGRS")
        {

        }
    };

    this.setMousePosition = function(evt)
    {
        this.mouseLonLat = map.getLonLatFromViewPortPx(new OpenLayers.Pixel(evt.xy.x, evt.xy.y));

        // Decimal Degrees
        this.ddOutput = document.getElementById("ddCoordinates");

        if(this.mouseLonLat.lat > "90" || this.mouseLonLat.lat < "-90" || this.mouseLonLat.lon > "180" || this.mouseLonLat.lon < "-180")
        {
            this.ddOutput.innerHTML = "<b>DD:</b> Out of geospatial bounds.";
        }
        else
        {
            this.ddOutput.innerHTML = "<b>DD:</b> " + this.mouseLonLat.lat + " " + this.mouseLonLat.lon;
        }

        // Degrees Minutes Seconds
        this.dmsOutput = document.getElementById("dmsCoordinates");

        if(this.mouseLonLat.lat > "90" || this.mouseLonLat.lat < "-90" || this.mouseLonLat.lon > "180" || this.mouseLonLat.lon < "-180")
        {
            this.dmsOutput.innerHTML = "<b>DMS:</b> Out of geospatial bounds.";
        }
        else
        {
            this.dmsOutput.innerHTML = "<b>DMS:</b> " + convert.ddToDms(this.mouseLonLat.lat, "latitude") + " " + convert.ddToDms(this.mouseLonLat.lon, "longitude");
        }

        // Military Grid Reference System
    };

    this.changeMapSize = function()
        {
            this.mapTitle = $("mapTitle");
            this.mapDiv = $("map");

            this.mapDiv.style.width = this.mapTitle.offsetWidth + "px";
            this.mapDiv.style.height = Math.round(this.mapTitle.offsetWidth / 2) + "px";

            map.updateSize();
        };





       this.setupBaseLayer = function(baseLayers)
  {   this.baseLayers = baseLayers;

      alert(this.baseLayers);
    for ( var it in this.baseLayers )
    {         alert("tes");
      this.map.addLayer( new OpenLayers.Layer.WMS(
          this.baseLayers[it].title, this.baseLayers[it].url,
          {layers:  this.baseLayers[it].name, format: 'image/jpg' },
          {'isBaseLayer': true}, {buffer:0} ) );
    }
  };
    













    this.setupDataLayer = function(dataWmsTitle, dataWmsUrl, dataWmsLayers, dataWmsStyles, dataWmsFormat)
    {
        this.dataLayer = new OpenLayers.Layer.WMS(dataWmsTitle, dataWmsUrl,
        {layers: dataWmsLayers, styles: dataWmsStyles, format: dataWmsFormat, transparent: true},
        {isBaseLayer: false, buffer: 0, visibility: false, transitionEffect: "resize"});

        map.addLayer(this.dataLayer);
    };

    this.setupAoiLayer = function()
    {
        aoiLayer = new OpenLayers.Layer.Vector("Area of Interest");
        aoiLayer.events.register("featureadded", aoiLayer, this.setAOI);

        this.polyOptions = {sides: 4, irregular: true};

        this.polygonControl = new OpenLayers.Control.DrawFeature(aoiLayer, OpenLayers.Handler.RegularPolygon,
        {handlerOptions: this.polyOptions});

        map.addLayer(aoiLayer);
        map.addControl(this.polygonControl);

        this.aoiMinLon = "${queryParams?.aoiMinLon ?: ''}";
        this.aoiMinLat = "${queryParams?.aoiMinLat ?: ''}";
        this.aoiMaxLon = "${queryParams?.aoiMaxLon ?: ''}";
        this.aoiMaxLat = "${queryParams?.aoiMaxLat ?: ''}";

        if (this.aoiMinLon && this.aoiMinLat && this.aoiMaxLon && this.aoiMaxLat)
        {
            this.bounds = new OpenLayers.Bounds(this.aoiMinLon, this.aoiMinLat, this.aoiMaxLon, this.aoiMaxLat);
            this.feature = new OpenLayers.Feature.Vector(this.bounds.toGeometry());

            aoiLayer.addFeatures(this.feature, {silent: true});
        }
    };

    this.setAOI = function(e)
    {
        this.geom = e.feature.geometry;
        this.bounds = this.geom.getBounds();
        this.feature = new OpenLayers.Feature.Vector(this.geom);

        // Decimal Degrees
        if($("unitsMode").value == "DD")
        {
            $("aoiMinLon").value = this.bounds.left;
            $("aoiMaxLat").value = this.bounds.top;
            $("aoiMaxLon").value = this.bounds.right;
            $("aoiMinLat").value = this.bounds.bottom;
        }

        // Degrees Minutes Seconds
        if($("unitsMode").value == "DMS")
        {
            $("aoiMinLon").value = convert.ddToDms(this.bounds.left, "longitude");
            $("aoiMaxLat").value = convert.ddToDms(this.bounds.top, "latitude");
            $("aoiMaxLon").value = convert.ddToDms(this.bounds.right, "longitude");
            $("aoiMinLat").value = convert.ddToDms(this.bounds.bottom, "latitude");
        }

        // Military Grid Reference System
        if($("unitsMode").value == "MGRS")
        {

        }

        aoiLayer.destroyFeatures();
        aoiLayer.addFeatures(this.feature, {silent: true});
    };

    this.setupToolBar = function()
    {
        this.recenterButton = new OpenLayers.Control.MouseDefaults(
        {title: "Click and drag to recenter map."});

        this.zoomBoxButton = new OpenLayers.Control.ZoomBox(
        {title: "Click and drag to zoom into an area."});

        this.zoomInButton = new OpenLayers.Control.Button(
        {title: "Click to zoom in.", displayClass: "olControlZoomIn", trigger: this.zoomIn});

        this.zoomOutButton = new OpenLayers.Control.Button(
        {title: "Click to zoom out.", displayClass: "olControlZoomOut", trigger: this.zoomOut});

        this.navButton = new OpenLayers.Control.NavigationHistory(
        {nextOptions:{title: "Click to go to next view."}, previousOptions:{title: "Click to go to previous view."}});

        map.addControl(this.navButton);

        this.zoomMaxExtentButton = new OpenLayers.Control.ZoomToMaxExtent(
        {title: "Click to zoom to the max extent."});

        this.polyOptions = {sides: 4, irregular: true};

        this.polygonControl = new OpenLayers.Control.DrawFeature(aoiLayer, OpenLayers.Handler.RegularPolygon,
        {handlerOptions: this.polyOptions, title: "Click and drag to specify an area of interest."});

        this.clearAoiButton = new OpenLayers.Control.Button(
        {title: "Click to clear area of interest", displayClass: "olControlClearAreaOfInterest", trigger: this.clearAOI});

        this.measureDistanceButton = new OpenLayers.Control.Measure(OpenLayers.Handler.Path,
        {title: "Measure Distance", displayClass: "olControlMeasureDistance",
            eventListeners:
            {
                measure: function(evt)
                {
                    alert("Distance: " + evt.measure.toFixed(3) + evt.units);
                }
            }
        });

        this.measureAreaButton = new OpenLayers.Control.Measure(OpenLayers.Handler.Polygon,
        {title: "Measure Area", displayClass: "olControlMeasureArea",
            eventListeners:
            {
                measure: function(evt)
                {
                    alert("Area: " + evt.measure.toFixed(3) + evt.units);
                }
            }
        });

        this.container = $("panel2");

        this.panel = new OpenLayers.Control.Panel(
        {div: this.container,defaultControl: this.zoomBoxButton, displayClass: "olControlPanel"});

        this.panel.addControls([
            this.recenterButton,
            this.zoomBoxButton,
            this.zoomInButton,
            this.zoomOutButton,
            this.navButton.next, this.navButton.previous,
            this.zoomMaxExtentButton,
            this.polygonControl,
            this.clearAoiButton,
            this.measureDistanceButton,
            this.measureAreaButton
        ]);

        map.addControl(this.panel);
    };

    this.zoomIn = function()
    {
        map.zoomIn();
    };

    this.zoomOut = function()
    {
        map.zoomOut();
    };

    this.clearAOI = function( e )
    {
        aoiLayer.destroyFeatures();

        $("aoiMinLon").value = "";
        $("aoiMaxLat").value = "";
        $("aoiMaxLon").value = "";
        $("aoiMinLat").value = "";
    };

    this.setupMapView = function(viewMinLon, viewMinLat, viewMaxLon, viewMaxLat)
    {
        this.bounds = new OpenLayers.Bounds(viewMinLon, viewMinLat, viewMaxLon, viewMaxLat);
        this.zoom = map.getZoomForExtent(this.bounds, true);

        map.setCenter(this.bounds.getCenterLonLat(), this.zoom);
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

    String.prototype.leftPad = function (l, c) {return new Array(l - this.length + 1).join(c || '0') + this;};
    this.updateOmarFilters = function(startDay, startMonth, startYear, startHour, startMinute, endDay, endMonth, endYear, endHour, eMinute, numberOfNames, numberOfValues)
    {
        this.wmsParams = new Array();

        this.hasStartDate = startDay != "" && startMonth != "" && startYear != "" && startHour != "" && startMinute != "";
        this.startDateNoQuote = startYear + startMonth.leftPad(2) + startDay.leftPad(2) + 'T' + startHour.leftPad(2) +':' + startMinute.leftPad(2) + ':' + '00Z';

        this.hasEndDate = endDay != "" && endMonth != "" && endYear != "";
        this.endDateNoQuote = endYear + endMonth.leftPad(2) + endDay.leftPad(2) + 'T' + endHour.leftPad(2) + ':' + eminute.leftPad(2) +':'+'00Z';

        this.wmsTime = "";

        if(this.hasStartDate)
        {
            this.wmsTime = this.startDateNoQuote;
            if(this.hasEndDate)
            {
                this.wmsTime += "/" + this.endDateNoQuote;
            }
            else
            {
                this.wmsTime += "/"
            }
        }
        else
        {
            if(this.hasEndDate)
            {
                this.wmsTime += "/" + this.endDateNoQuote;
            }
            else
            {
                this.wmsTime = "";
            }
        }

        this.idx = 0;

        this.wmsParams["time"] = this.wmsTime;

        this.tempName = "";

        for(this.idx=0; this.idx<numberOfNames; ++this.idx)
        {
            this.tempName = "searchTagNames[" + this.idx + "]";
            this.wmsParams["searchTagNames["+this.idx+"]"] =$(this.tempName).value;
        }

        for(this.idx=0; this.idx<numberOfValues; ++this.idx)
        {
            this.tempName = "searchTagValues[" + this.idx + "]";
            this.wmsParams["searchTagValues["+this.idx+"]"] =$(this.tempName).value;
        }

        this.dataLayer.mergeNewParams(this.wmsParams);
    };

    this.generateKML = function()
    {
        document.searchForm.action = "kmlnetworklink";
        this.setCurrentViewport();
        document.searchForm.submit();
    };

    this.setCurrentViewport = function()
    {
        this.bounds = map.getExtent();
        $("viewMinLon").value = this.bounds.left;
        $("viewMaxLat").value = this.bounds.top;
        $("viewMaxLon").value = this.bounds.right;
        $("viewMinLat").value = this.bounds.bottom;
    };

    this.updateFootprints = function()
    {
        this.dataLayer.redraw(true);
    };

    this.searchForRasters = function()
    {
        this.latDmsRegExp = /^(\d{1,2})\s?(\d{2})\s?(\d{2})(.\d+)?\s?([NnSs])?/
        this.lonDmsRegExp = /^(\d{1,3})\s?(\d{2})\s?(\d{2})(.\d+)?\s?([EeWw])?/

        // Convert DMS Center to DD
        if($("centerLat").value.match(this.latDmsRegExp) && $("centerLon").value.match(this.lonDmsRegExp))
        {
            if($("centerLat").value.match(this.latDmsRegExp))
            {
                this.ctrLatDeg = parseInt(RegExp.$1, 10);
                this.ctrLatMin = parseInt(RegExp.$2, 10);
                this.ctrLatSec = parseInt(RegExp.$3, 10) + RegExp.$4;
                this.ctrLatHem = RegExp.$5;

                if(this.ctrLatHem == "S" || this.ctrLatHem == "s" )
                {
                    this.ctrLatDeg = -this.ctrLatDeg;
                }

                $("centerLat").value = convert.dmsToDd(this.ctrLatDeg, this.ctrLatMin, this.ctrLatSec);
            }

            if ($("centerLon").value.match(this.lonDmsRegExp))
            {
                this.ctrLonDeg = parseInt(RegExp.$1, 10);
                this.ctrLonMin = parseInt(RegExp.$2, 10);
                this.ctrLonSec = parseInt(RegExp.$3, 10) + RegExp.$4;
                this.ctrLonHem = RegExp.$5;

                if(this.ctrLonHem == "W" || this.ctrLonHem == "w")
                {
                    this.ctrlonDeg = -this.ctrlonDeg;
                }

                $("centerLon").value = convert.dmsToDd(this.ctrLonDeg, this.ctrLonMin, this.ctrLonSec);
            }
        }

        // Convert DMS Bounds to DD
        this.aoiMaxLatRegExp = /^(\d{1,2})\s?(\d{2})\s?(\d{2})(.\d+)?\s?([NnSs])?/
        this.aoiMinLonRegExp = /^(\d{1,3})\s?(\d{2})\s?(\d{2})(.\d+)?\s?([EeWw])?/
        this.aoiMinLatRegExp = /^(\d{1,2})\s?(\d{2})\s?(\d{2})(.\d+)?\s?([NnSs])?/
        this.aoiMaxLonRegExp = /^(\d{1,3})\s?(\d{2})\s?(\d{2})(.\d+)?\s?([EeWw])?/

        if($("aoiMaxLat").value.match(this.aoiMaxLatRegExp))
        {
            this.aoiMaxLatDeg = parseInt(RegExp.$1, 10);
            this.aoiMaxLatMin = parseInt(RegExp.$2, 10);
            this.aoiMaxLatSec = parseInt(RegExp.$3, 10) + RegExp.$4;
            this.aoiMaxLatHem = RegExp.$5;

            if(this.aoiMaxLatHem == "S" || this.aoiMaxLatHem == "s")
            {
                this.aoiMaxLatDeg = -this.aoiMaxLatDeg;
            }

            $("aoiMaxLat").value = convert.dmsToDd(this.aoiMaxLatDeg, this.aoiMaxLatMin, this.aoiMaxLatSec);
        }

        if($("aoiMinLon").value.match(this.aoiMinLonRegExp))
        {
            this.aoiMinLonDeg = parseInt(RegExp.$1, 10);
            this.aoiMinLonMin = parseInt(RegExp.$2, 10);
            this.aoiMinLonSec = parseInt(RegExp.$3, 10) + RegExp.$4;
            this.aoiMinLonHem = RegExp.$5;

            if(this.aoiMinLonHem == "W" || this.aoiMinLonHem == "w")
            {
                this.aoiMinlonDeg = -this.aoiMinlonDeg;
            }

            $("aoiMinLon").value = convert.dmsToDd(this.aoiMinLonDeg, this.aoiMinLonMin, this.aoiMinLonSec);
        }

        if($("aoiMinLat").value.match(this.aoiMinLatRegExp))
        {
            this.aoiMinLatDeg = parseInt(RegExp.$1, 10);
            this.aoiMinLatMin = parseInt(RegExp.$2, 10);
            this.aoiMinLatSec = parseInt(RegExp.$3, 10) + RegExp.$4;
            this.aoiMinLatHem = RegExp.$5;

            if(this.aoiMinLatHem == "S" || this.aoiMinLonHem == "s")
            {
                this.aoiMinlatDeg = -this.aoiMinlatDeg;
            }

            $("aoiMinLat").value = convert.dmsToDd(this.aoiMinLatDeg, this.aoiMinLatMin, this.aoiMinLatSec);
        }

        if($("aoiMaxLon").value.match(this.aoiMaxLonRegExp))
        {
            this.aoiMaxLonDeg = parseInt(RegExp.$1, 10);
            this.aoiMaxLonMin = parseInt(RegExp.$2, 10);
            this.aoiMaxLonSec = parseInt(RegExp.$3, 10) + RegExp.$4;
            this.aoiMaxLonHem = RegExp.$5;

            if(this.aoiMaxLonHem == "W" || this.aoiMaxLonHem == "w")
            {
                this.aoiMaxLonDeg = -this.aoiMaxLonDeg;
            }

            $("aoiMaxLon").value = convert.dmsToDd(this.aoiMaxLonDeg, this.aoiMaxLonMin, this.aoiMaxLonSec);
        }

        document.searchForm.action = "search";
        this.setCurrentViewport();
        document.searchForm.submit();
    };

    this.goto = function()
    {
        this.latDdRegExp = /^(\-?\d{1,2})(\.\d+)?$/
        this.lonDdRegExp = /^(\-?\d{1,3})(\.\d+)?$/

        this.latDmsRegExp = /^(\d{1,2})\s?(\d{2})\s?(\d{2})(.\d+)?\s?([NnSs])?/
        this.lonDmsRegExp = /^(\d{1,3})\s?(\d{2})\s?(\d{2})(.\d+)?\s?([EeWw])?/

        if($("centerLat").value.match(this.latDdRegExp) && $("centerLon").value.match(this.lonDdRegExp))
        {
            this.setMapCenter($("centerLat").value, $("centerLon").value);
        }
        else if($("centerLat").value.match(this.latDmsRegExp) && $("centerLon").value.match(this.lonDmsRegExp))
        {
            if($("centerLat").value.match(this.latDmsRegExp))
            {
                this.latDeg = parseInt(RegExp.$1, 10);
                this.latMin = parseInt(RegExp.$2, 10);
                this.latSec = parseInt(RegExp.$3, 10) + RegExp.$4;
                this.latHem = RegExp.$5;

                if(this.latHem == "S" || this.latHem == "s")
                {
                    this.latDeg = -this.latDeg;
                }
            }

            if($("centerLon").value.match(this.lonDmsRegExp))
            {
                this.lonDeg = parseInt(RegExp.$1, 10);
                this.lonMin = parseInt(RegExp.$2, 10);
                this.lonSec = parseInt(RegExp.$3, 10) + RegExp.$4;
                this.lonHem = RegExp.$5;

                if(this.lonHem == "W" || this.lonHem == "w")
                {
                    this.lonDeg = -this.lonDeg;
                }
            }

            this.setMapCenter(convert.dmsToDd(this.latDeg, this.latMin, this.latSec ), convert.dmsToDd(this.lonDeg, this.lonMin, this.lonSec));
        }
        else
        {
            alert( "Invalid DMS Format.\n\n" +
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
                   "1800000W" );
        }
    };

    this.setMapCenter = function(latitude, longitude)
    {  alert("test");
        this.zoom = map.getZoom( );
        this.center = new OpenLayers.LonLat(longitude, latitude);

        map.setCenter(this.center, this.zoom);
    };

}