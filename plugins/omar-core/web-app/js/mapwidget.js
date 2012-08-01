function MapWidget()
{
    var pathUnit = new Array();
    pathUnit = [" "," "," "," "," "];
    var wheelListener = null;
    var aoiLayer = null;
    var openlayersMap = null;
    var convert = new CoordinateConversion();
    var zoomInButton = null;
    var zoomOutButton = null;
    var zoomInFullResButton = null;
    var zoomFullResScale = null;
    var lastTick = (new Date()).getTime();
    var panButton = null;
    this.getZoomInButton = function()
    {
        return zoomInButton;
    }
    this.allocateMap = function( divId, params )
    {
        openlayersMap = new OpenLayers.Map( divId, params );
    }
    this.setFullResScale = function( value )
    {
        zoomFullResScale = value;
    }
    this.setupMapWidgetWithOptions = function( divId, params )
    {
        this.allocateMap( divId, params );
        openlayersMap.addControl( new OpenLayers.Control.LayerSwitcher( {'div':OpenLayers.Util.getElement( 'layerswitcher' ), roundedCorner: false} ) );

        //openlayersMap.addControl( new OpenLayers.Control.Scale() );
        //openlayersMap.addControl( new OpenLayers.Control.ScaleLine() );

        openlayersMap.events.register( "click", map, this.handleMouseClick );
        openlayersMap.events.register( "mousemove", map, this.handleMouseHover );
        openlayersMap.events.register( "moveend", map, this.setPointRadiusText );
        openlayersMap.events.register( "zoomend", map, this.setBoundLatLonText );
        openlayersMap.events.register("mouseup", map, this.setCenterForLayers)
        var isiPad = navigator.userAgent.match( /iPad/i ) != null;

        if ( isiPad )
        {
            this.touchhandler = new TouchHandler( map, 4 );
        }
    }
    this.setCenterForLayers = function()
    {
        var extent = openlayersMap.getExtent();
        var idx = 0;
        for(idx = 0; idx < openlayersMap.layers.length; ++idx)
        {
            openlayersMap.layers[idx].moveTo(extent);
        }
    }
    this.getMap = function()
    {
        return openlayersMap;
    }
    this.setupMapWidget = function()
    {
        this.allocateMap( "map", {controls: []} );
        openlayersMap.addControl( new OpenLayers.Control.LayerSwitcher( {'div':OpenLayers.Util.getElement( 'layerswitcher' ), roundedCorner: false} ) );

        openlayersMap.addControl( new OpenLayers.Control.Scale() );
        openlayersMap.addControl( new OpenLayers.Control.ScaleLine() );

        openlayersMap.events.register( "click", map, this.handleMouseClick );
        openlayersMap.events.register( "mousemove", map, this.handleMouseHover );
        openlayersMap.events.register( "moveend", map, this.setPointRadiusText );
        openlayersMap.events.register( "zoomend", map, this.setBoundLatLonText );

        var isiPad = navigator.userAgent.match( /iPad/i ) != null;

        if ( isiPad )
        {
            this.touchhandler = new TouchHandler( map, 4 );
        }
    };


    this.handleMouseClick = function( evt )
    {
        var lonLat = openlayersMap.getLonLatFromViewPortPx( new OpenLayers.Pixel( evt.xy.x, evt.xy.y ) );

        var mouseClickDd = document.getElementById( "mouseClickDdOutput" );
        var mouseClickDms = document.getElementById( "mouseClickDmsOutput" );
        var mouseClickMgrs = document.getElementById( "mouseClickMgrsOutput" );

        var geoExtentError = "Outside Geographic Extents.";
        if ( lonLat.lat > "90" || lonLat.lat < "-90" || lonLat.lon > "180" || lonLat.lon < "-180" )
        {
            if ( mouseClickDd )   mouseClickDd.innerHTML = "<b>DD:</b> " + geoExtentError;
            if ( mouseClickDms )  mouseClickDms.innerHTML = "<b>DMS:</b> " + geoExtentError;
            if ( mouseClickMgrs ) mouseClickMgrs.innerHTML = "<b>MGRS:</b> " + geoExtentError;
        }

        else
        {
            if ( mouseClickDd )   mouseClickDd.innerHTML = "<b>DD:</b> " + lonLat.lat + " " + lonLat.lon;
            if ( mouseClickDms )  mouseClickDms.innerHTML = "<b>DMS:</b> " + convert.ddToDms( lonLat.lat, "lat" ) + " " + convert.ddToDms( lonLat.lon, "lon" );
            if ( mouseClickMgrs ) mouseClickMgrs.innerHTML = "<b>MGRS:</b> " + convert.ddToMgrs( lonLat.lat, lonLat.lon );
        }
    };

    this.handleMouseHover = function( evt )
    {
        var lonLat = openlayersMap.getLonLatFromViewPortPx( new OpenLayers.Pixel( evt.xy.x, evt.xy.y ) );

        var mouseHoverDd = document.getElementById( "mouseHoverDdOutput" );
        var mouseHoverDms = document.getElementById( "mouseHoverDmsOutput" );
        var mouseHoverMgrs = document.getElementById( "mouseHoverMgrsOutput" );

        var geoExtentError = "Outside Geographic Extents.";

        if ( lonLat )
        {
            if ( lonLat.lat > "90" || lonLat.lat < "-90" || lonLat.lon > "180" || lonLat.lon < "-180" )
            {
                if ( mouseHoverDd )   mouseHoverDd.innerHTML = "<b>DD:</b> " + geoExtentError;
                if ( mouseHoverDms )  mouseHoverDms.innerHTML = "<b>DMS:</b> " + geoExtentError;
                if ( mouseHoverMgrs ) mouseHoverMgrs.innerHTML = "<b>MGRS:</b> " + geoExtentError;
            }

            else
            {
                if ( mouseHoverDd )   mouseHoverDd.innerHTML = "<b>DD:</b> " + lonLat.lat + " " + lonLat.lon;
                if ( mouseHoverDms )  mouseHoverDms.innerHTML = "<b>DMS:</b> " + convert.ddToDms( lonLat.lat, "lat" ) + " " + convert.ddToDms( lonLat.lon, "lon" );
                if ( mouseHoverMgrs ) mouseHoverMgrs.innerHTML = "<b>MGRS:</b> " + convert.ddToMgrs( lonLat.lat, lonLat.lon );
            }
        }
    };

    this.setPointRadiusText = function()
    {
        if ( openlayersMap )
        {
            var center = openlayersMap.getCenter();

            if ( $( "centerLat" ) ) $( "centerLat" ).value = center.lat;
            if ( $( "centerLon" ) ) $( "centerLon" ).value = center.lon;

            if ( $( "centerLatDms" ) ) $( "centerLatDms" ).value = convert.ddToDms( center.lat, "lat" );
            if ( $( "centerLonDms" ) ) $( "centerLonDms" ).value = convert.ddToDms( center.lon, "lon" );

            if ( $( "centerMgrs" ) ) $( "centerMgrs" ).value = convert.ddToMgrs( center.lat, center.lon );
        }
    };

    this.setCenterDd = function()
    {
        var latRegExpDd = /^(\-?\d{1,2})(\.\d+)?$/
        var lonRegExpDd = /^(\-?\d{1,3})(\.\d+)?$/

        if ( $( "centerLat" ) && $( "centerLon" ) )
        {
            if ( $( "centerLat" ).value.match( latRegExpDd ) && $( "centerLon" ).value.match( lonRegExpDd ) )
            {
                this.setMapCenter( $( "centerLat" ).value, $( "centerLon" ).value );
            }
        }

        if ( $( "aoiRadius" ) )
        {
            if ( $( "aoiRadius2" ) ) $( "aoiRadius2" ).value = $( "aoiRadius" ).value;
            if ( $( "aoiRadius3" ) ) $( "aoiRadius3" ).value = $( "aoiRadius" ).value;
        }
    };

    this.setCenterDms = function()
    {
        var latRegExpDms = /^(\d{1,2})\°?\s?(\d{2})\'?\s?(\d{2}\.?\d+)\"?\s?([NnSs])?/
        var lonRegExpDms = /^(\d{1,3})\°?\s?(\d{2})\'?\s?(\d{2}\.?\d+)\"?\s?([EeWw])?/

        if ( $( "centerLatDms" ) )
        {
            var newLat, newLon;

            if ( $( "centerLatDms" ).value.match( latRegExpDms ) && $( "centerLonDms" ).value.match( lonRegExpDms ) )
            {
                if ( $( "centerLatDms" ).value.match( latRegExpDms ) )
                {
                    newLat = convert.dmsToDd( RegExp.$1, RegExp.$2, RegExp.$3, RegExp.$4 );
                }
                if ( $( "centerLonDms" ).value.match( lonRegExpDms ) )
                {
                    newLon = convert.dmsToDd( RegExp.$1, RegExp.$2, RegExp.$3, RegExp.$4 );
                }

                this.setMapCenter( newLat, newLon );
            }

            $( "aoiRadius" ).value = $( "aoiRadius2" ).value;
            $( "aoiRadius3" ).value = $( "aoiRadius2" ).value;
        }
    };

    this.setCenterMgrs = function()
    {
        var mgrsRegExpUtm = /^(-?\d{1,2})(\.\d+)?\s?(-?\d{1,3})(\.\d+)?/

        var centerMgrsToUtm = convert.mgrsToUtm( $( "centerMgrs" ).value );

        if ( centerMgrsToUtm.match( mgrsRegExpUtm ) )
        {
            var centerLat = parseInt( RegExp.$1, 10 ) + RegExp.$2;
            var centerLon = parseInt( RegExp.$3, 10 ) + RegExp.$4;

            this.setMapCenter( centerLat, centerLon );
        }

        $( "aoiRadius" ).value = $( "aoiRadius3" ).value;
        $( "aoiRadius2" ).value = $( "aoiRadius3" ).value;
    };


    this.setupAoiLayer = function()
    {
        aoiLayer = new OpenLayers.Layer.Vector( "Bound Box AOI" );
        aoiLayer.events.register( "featureadded", aoiLayer, this.setAoi );

        var boundBox = new OpenLayers.Control.DrawFeature( aoiLayer, OpenLayers.Handler.RegularPolygon,
        {handlerOptions: {sides: 4, irregular: true}} );

        openlayersMap.addLayer( aoiLayer );
        openlayersMap.addControl( boundBox );

        var aoiMinLon = "${queryParams?.aoiMinLon ?: ''}";
        var aoiMinLat = "${queryParams?.aoiMinLat ?: ''}";
        var aoiMaxLon = "${queryParams?.aoiMaxLon ?: ''}";
        var aoiMaxLat = "${queryParams?.aoiMaxLat ?: ''}";

        if ( aoiMinLon && aoiMinLat && aoiMaxLon && aoiMaxLat )
        {
            var bounds = new OpenLayers.Bounds( aoiMinLon, aoiMinLat, aoiMaxLon, aoiMaxLat );
            var feature = new OpenLayers.Feature.Vector( bounds.toGeometry() );

            aoiLayer.addFeatures( feature, {silent: true} );
        }
    };

    this.setAoi = function( e )
    {
        var geom = e.feature.geometry;
        var bounds = geom.getBounds();
        var feature = new OpenLayers.Feature.Vector( geom );

        if ( $( "aoiMinLon" ) ) $( "aoiMinLon" ).value = bounds.left;
        if ( $( "aoiMaxLat" ) ) $( "aoiMaxLat" ).value = bounds.top;
        if ( $( "aoiMaxLon" ) ) $( "aoiMaxLon" ).value = bounds.right;
        if ( $( "aoiMinLat" ) ) $( "aoiMinLat" ).value = bounds.bottom;

        if ( $( "aoiMinLonDms" ) ) $( "aoiMinLonDms" ).value = convert.ddToDms( bounds.left, "lon" );
        if ( $( "aoiMaxLatDms" ) ) $( "aoiMaxLatDms" ).value = convert.ddToDms( bounds.top, "lat" );
        if ( $( "aoiMaxLonDms" ) ) $( "aoiMaxLonDms" ).value = convert.ddToDms( bounds.right, "lon" );
        if ( $( "aoiMinLatDms" ) ) $( "aoiMinLatDms" ).value = convert.ddToDms( bounds.bottom, "lat" );

        if ( $( "aoiNeMgrs" ) ) $( "aoiNeMgrs" ).value = convert.ddToMgrs( bounds.top, bounds.right );
        if ( $( "aoiSwMgrs" ) ) $( "aoiSwMgrs" ).value = convert.ddToMgrs( bounds.bottom, bounds.left );

        aoiLayer.destroyFeatures();
        aoiLayer.addFeatures( feature, {silent: true} );
    };

    this.initAOI = function( minLon, minLat, maxLon, maxLat )
    {
        var bounds = new OpenLayers.Bounds( minLon, minLat, maxLon, maxLat );
        var feature = new OpenLayers.Feature.Vector( bounds.toGeometry() );

        if ( $( "aoiMinLon" ) ) $( "aoiMinLon" ).value = bounds.left;
        if ( $( "aoiMaxLat" ) ) $( "aoiMaxLat" ).value = bounds.top;
        if ( $( "aoiMaxLon" ) ) $( "aoiMaxLon" ).value = bounds.right;
        if ( $( "aoiMinLat" ) ) $( "aoiMinLat" ).value = bounds.bottom;

        if ( $( "aoiMinLonDms" ) ) $( "aoiMinLonDms" ).value = convert.ddToDms( bounds.left, "lon" );
        if ( $( "aoiMaxLatDms" ) ) $( "aoiMaxLatDms" ).value = convert.ddToDms( bounds.top, "lat" );
        if ( $( "aoiMaxLonDms" ) ) $( "aoiMaxLonDms" ).value = convert.ddToDms( bounds.right, "lon" );
        if ( $( "aoiMinLatDms" ) ) $( "aoiMinLatDms" ).value = convert.ddToDms( bounds.bottom, "lat" );

        if ( $( "aoiNeMgrs" ) ) $( "aoiNeMgrs" ).value = convert.ddToMgrs( bounds.top, bounds.right );
        if ( $( "aoiSwMgrs" ) ) $( "aoiSwMgrs" ).value = convert.ddToMgrs( bounds.bottom, bounds.left );

        aoiLayer.destroyFeatures();
        aoiLayer.addFeatures( feature, {silent: true} );
    };

    this.clearAOI = function( e )
    {
        aoiLayer.destroyFeatures();
        if ( $( "aoiMinLon" ) ) $( "aoiMinLon" ).value = "";
        if ( $( "aoiMaxLat" ) ) $( "aoiMaxLat" ).value = "";
        if ( $( "aoiMaxLon" ) ) $( "aoiMaxLon" ).value = "";
        if ( $( "aoiMinLat" ) ) $( "aoiMinLat" ).value = "";

        if ( $( "aoiMinLonDms" ) ) $( "aoiMinLonDms" ).value = "";
        if ( $( "aoiMaxLatDms" ) ) $( "aoiMaxLatDms" ).value = "";
        if ( $( "aoiMaxLonDms" ) ) $( "aoiMaxLonDms" ).value = "";
        if ( $( "aoiMinLatDms" ) ) $( "aoiMinLatDms" ).value = "";

        if ( $( "aoiNeMgrs" ) ) $( "aoiNeMgrs" ).value = "";
        if ( $( "aoiSwMgrs" ) ) $( "aoiSwMgrs" ).value = "";
    };
    this.getSizeInPixelsFromExtents = function( extents )
    {
        if ( !extents ) return null;
        cornerPt1 = openlayersMap.getViewPortPxFromLonLat( new OpenLayers.LonLat( extents.left, extents.top ) );
        cornerPt2 = openlayersMap.getViewPortPxFromLonLat( new OpenLayers.LonLat( extents.right, extents.bottom ) );

        return new OpenLayers.Size( Math.round( Math.abs( cornerPt2.x - cornerPt1.x ) + 1 ),
                Math.round( Math.abs( cornerPt2.y - cornerPt1.y ) + 1 ) );
    }
    this.getSelectedExtents = function()
    {
        extent = null;
        if ( aoiLayer )
        {
            extent = aoiLayer.getDataExtent();
        }
        return extent;
    }
    this.getViewportExtents = function()
    {
        extent = null;
        if ( openlayersMap )
        {
            extent = openlayersMap.getExtent();
        }
        return extent;
    }
    this.getSelectedOrViewportExtents = function ()
    {
        extent = this.getSelectedExtents();
        if ( !extent || !extent.left )
        {
            extent = this.getViewportExtents();
        }
        return extent;
    }
    this.setupSearch = function()
    {
        if ( $( "units" ).value == "DD" || $( "units" ).value == "" )
        {

        }

        else if ( $( "units" ).value == "DMS" )
        {
            var latRegExpDms = /^(\d{1,2})\s?(\d{2})\s?(\d{2})(.\d+)?\s?([NnSs])?/
            var lonRegExpDms = /^(\d{1,3})\s?(\d{2})\s?(\d{2})(.\d+)?\s?([EeWw])?/

            if ( $( "centerLatDms" ).value.match( latRegExpDms ) && $( "centerLonDms" ).value.match( lonRegExpDms ) )
            {
                if ( $( "centerLatDms" ).value.match( latRegExpDms ) )
                {
                    var latDeg = parseInt( RegExp.$1, 10 );
                    var latMin = parseInt( RegExp.$2, 10 );
                    var latSec = parseInt( RegExp.$3, 10 ) + RegExp.$4;
                    var latHem = RegExp.$5;

                    if ( latHem == "S" || latHem == "s" )
                    {
                        latDeg = -latDeg;
                    }
                }

                if ( $( "centerLonDms" ).value.match( lonRegExpDms ) )
                {
                    var lonDeg = parseInt( RegExp.$1, 10 );
                    var lonMin = parseInt( RegExp.$2, 10 );
                    var lonSec = parseInt( RegExp.$3, 10 ) + RegExp.$4;
                    var lonHem = RegExp.$5;

                    if ( lonHem == "W" || lonHem == "w" )
                    {
                        lonDeg = -lonDeg;
                    }
                }

                $( "centerLat" ).value = convert.dmsToDd( latDeg, latMin, latSec );
                $( "centerLon" ).value = convert.dmsToDd( lonDeg, lonMin, lonSec );
            }

            var maxLatRegExpDms = /^(\d{1,2})\s?(\d{2})\s?(\d{2})(.\d+)?\s?([NnSs])?/
            var minLonRegExpDms = /^(\d{1,3})\s?(\d{2})\s?(\d{2})(.\d+)?\s?([EeWw])?/
            var minLatRegExpDms = /^(\d{1,2})\s?(\d{2})\s?(\d{2})(.\d+)?\s?([NnSs])?/
            var maxLonRegExpDms = /^(\d{1,3})\s?(\d{2})\s?(\d{2})(.\d+)?\s?([EeWw])?/

            if ( $( "aoiMaxLatDms" ).value.match( maxLatRegExpDms ) )
            {
                var maxLatDeg = parseInt( RegExp.$1, 10 );
                var maxLatMin = parseInt( RegExp.$2, 10 );
                var maxLatSec = parseInt( RegExp.$3, 10 ) + RegExp.$4;
                var maxLatHem = RegExp.$5;

                if ( maxLatHem == "S" || maxLatHem == "s" )
                {
                    maxLatDeg = -maxLatDeg;
                }

                $( "aoiMaxLat" ).value = convert.dmsToDd( maxLatDeg, maxLatMin, maxLatSec );
            }

            if ( $( "aoiMinLonDms" ).value.match( minLonRegExpDms ) )
            {
                var minLonDeg = parseInt( RegExp.$1, 10 );
                var minLonMin = parseInt( RegExp.$2, 10 );
                var minLonSec = parseInt( RegExp.$3, 10 ) + RegExp.$4;
                var minLonHem = RegExp.$5;

                if ( minLonHem == "W" || minLonHem == "w" )
                {
                    minLonDeg = -minLonDeg;
                }

                $( "aoiMinLon" ).value = convert.dmsToDd( minLonDeg, minLonMin, minLonSec );
            }

            if ( $( "aoiMinLatDms" ).value.match( minLatRegExpDms ) )
            {
                var minLatDeg = parseInt( RegExp.$1, 10 );
                var minLatMin = parseInt( RegExp.$2, 10 );
                var minLatSec = parseInt( RegExp.$3, 10 ) + RegExp.$4;
                var minLatHem = RegExp.$5;

                if ( minLatHem == "S" || minLonHem == "s" )
                {
                    minLatDeg = -minLatDeg;
                }

                $( "aoiMinLat" ).value = convert.dmsToDd( minLatDeg, minLatMin, minLatSec );
            }

            if ( $( "aoiMaxLonDms" ).value.match( maxLonRegExpDms ) )
            {
                var maxLonDeg = parseInt( RegExp.$1, 10 );
                var maxLonMin = parseInt( RegExp.$2, 10 );
                var maxLonSec = parseInt( RegExp.$3, 10 ) + RegExp.$4;
                var maxLonHem = RegExp.$5;

                if ( maxLonHem == "W" || maxLonHem == "w" )
                {
                    maxLonDeg = -maxLonDeg;
                }

                $( "aoiMaxLon" ).value = convert.dmsToDd( maxLonDeg, maxLonMin, maxLonSec );
            }
        }

        else if ( $( "units" ).value == "MGRS" )
        {
            var mgrsRegExpUtm = /^(-?\d{1,2})(\.\d+)?\s?(-?\d{1,3})(\.\d+)?/

            var centerMgrsToUtm = convert.mgrsToUtm( $( "centerMgrs" ).value );

            if ( centerMgrsToUtm.match( mgrsRegExpUtm ) )
            {
                var centerLat = parseInt( RegExp.$1, 10 ) + RegExp.$2;
                var centerLon = parseInt( RegExp.$3, 10 ) + RegExp.$4;

                $( "centerLat" ).value = centerLat;
                $( "centerLon" ).value = centerLon;
            }

            var regExpMgrs = /^(\d{1,2})([a-zA-Z])([a-zA-Z])([a-zA-Z])(\d{10})?/

            if ( $( "aoiNeMgrs" ).value.match( regExpMgrs ) && $( "aoiSwMgrs" ).value.match( regExpMgrs ) )
            {
                var neUtm = convert.mgrsToUtm( $( "aoiNeMgrs" ).value );

                if ( neUtm.match( mgrsRegExpUtm ) )
                {
                    var maxLat = parseInt( RegExp.$1, 10 ) + RegExp.$2;
                    var maxLon = parseInt( RegExp.$3, 10 ) + RegExp.$4;

                    $( "aoiMaxLat" ).value = maxLat;
                    $( "aoiMaxLon" ).value = maxLon;
                }

                var swUtm = convert.mgrsToUtm( $( "aoiSwMgrs" ).value );

                if ( swUtm.match( mgrsRegExpUtm ) )
                {
                    var minLat = parseInt( RegExp.$1, 10 ) + RegExp.$2;
                    var minLon = parseInt( RegExp.$3, 10 ) + RegExp.$4;

                    $( "aoiMinLat" ).value = minLat;
                    $( "aoiMinLon" ).value = minLon;
                }
            }
        }
        this.setCurrentViewport();
    }
    this.search = function( actionOverride )
    {
        this.setupSearch();
        if ( actionOverride )
        {
            document.searchForm.action = actionOverride;
        }
        else
        {
            document.searchForm.action = "search";
        }
        document.searchForm.submit();
    };

    this.baseLayer = null;

    this.setupBaseLayers = function( baseLayer )
    {
        openlayersMap.addLayer( baseLayer );
        openlayersMap.setBaseLayer( baseLayer );
    };

    this.dataLayer = null;

    this.setupDataLayer = function( dataWmsTitle, dataWmsUrl, dataWmsLayers, dataWmsStyles, dataWmsFormat )
    {
        dataLayer = new OpenLayers.Layer.WMS( dataWmsTitle, dataWmsUrl,
        {layers: dataWmsLayers, styles: dataWmsStyles, format: dataWmsFormat, transparent: true},
        {isBaseLayer: false, buffer: 0, visibility: true, transitionEffect: "resize"} );

        openlayersMap.addLayer( dataLayer );
    };

    this.updateFootprints = function()
    {
        dataLayer.redraw( true );
    };

    this.setMapCenter = function( lat, lon )
    {
        var zoom = openlayersMap.getZoom();
        var center = new OpenLayers.LonLat( lon, lat );

        openlayersMap.setCenter( center, zoom );
    };

    this.setCurrentViewport = function()
    {
        var bounds = openlayersMap.getExtent();

        $( "viewMinLon" ).value = bounds.left;
        $( "viewMaxLat" ).value = bounds.top;
        $( "viewMaxLon" ).value = bounds.right;
        $( "viewMinLat" ).value = bounds.bottom;
    };

    this.generateKML = function()
    {
        document.searchForm.action = "kmlnetworklink";
        this.setCurrentViewport();
        document.searchForm.submit();
    };

    this.changeMapSize = function()
    {

        //var mapTitle = $( "mapTitle" );
        //var mapDiv = $( "map" );
        //mapDiv.style.width = mapTitle.offsetWidth + "px";
        //mapDiv.style.height = Math.round( mapTitle.offsetWidth / 2 ) + "px";

        if ( openlayersMap ) openlayersMap.updateSize();
    };

    this.setupMapView = function( viewMinLon, viewMinLat, viewMaxLon, viewMaxLat )
    {
        var bounds = new OpenLayers.Bounds( viewMinLon, viewMinLat, viewMaxLon, viewMaxLat );
        var zoom = openlayersMap.getZoomForExtent( bounds, true );

        openlayersMap.setCenter( bounds.getCenterLonLat(), zoom );
    };

    var pathUnit;

    this.setPathUnits = function( pathUnit )
    {
        $( "pathUnits" ).value = pathUnit;
    };

    this.wheel = function(e){

        var currentTick = (new Date()).getTime();
        if((currentTick - this.lastTick) < 500)
        {
            YAHOO.util.Event.stopEvent(e);
            return;
        }
         this.lastTick = currentTick;
        this.panButton.onWheelEvent(e);
     }
    //////////////////////////////////

    /////////////////////
    //
    //

    var message = "Alert: Not certified for targeting.\n";
    this.setupToolBar = function()
    {
        this.panButton = new OpenLayers.Control.MouseDefaults( {title: "Click pan button to activate. Once activated click the map and drag the mouse to pan."} );

        var zoomBoxButton = new OpenLayers.Control.ZoomBox( {title: "Click the zoom box button to activate. Once activated click and drag over an area of interest on the map to zoom into."} );

        zoomInButton = new OpenLayers.Control.Button( {title: "Click to zoom in.",
            displayClass: "olControlZoomIn",
            trigger: this.zoomIn} );

        zoomOutButton = new OpenLayers.Control.Button( {title: "Click to zoom out.",
            displayClass: "olControlZoomOut",
            trigger: this.zoomOut} );

        zoomInFullResButton = new OpenLayers.Control.Button( {title: "Click to zoom into full resolution.",
            displayClass: "olControlZoomToLayer",
            trigger: this.zoomInFullRes} );

        var zoomMaxExtentButton = new OpenLayers.Control.ZoomToMaxExtent( {title:"Click to zoom to the max extent.", trigger: this.zoomMaxExtent} );

        var navButton = new OpenLayers.Control.NavigationHistory( {nextOptions:{title:"Click to go to next view."},
            previousOptions:{title:"Click to go to previous view."}} );
        var boundBoxButton;
        var clearAoiButton;
        if ( aoiLayer )
        {
            boundBoxButton = new OpenLayers.Control.DrawFeature( aoiLayer, OpenLayers.Handler.RegularPolygon,
            {handlerOptions: {sides: 4, irregular: true}, displayClass: "olControlDrawFeature", title: "Click and drag to specify an area of interest."} );

            clearAoiButton = new OpenLayers.Control.Button(
            {title: "Click to clear area of interest", displayClass: "olControlClearAreaOfInterest", trigger: this.clearAOI} );
        }

        var pathMeasurement = document.getElementById( "pathMeasurement" );
        var pathMeasurementButton;

        this.getPathUnit = function()
        {
            return pathUnit;
        }

        if ( $( "measurementUnits" ) && pathMeasurement )
        {
            pathMeasurementButton = new OpenLayers.Control.Measure( OpenLayers.Handler.Path, {
                title:"Click path measurement button to activate. Once activated click points on the map to create a path that you wish to measure. When you are done creating your path double click to end.",
                displayClass: "olControlMeasureDistance", geodesic:true, persist: true,
                eventListeners:
                {
                    measure: function( evt )
                    {
                        if ( evt.units == "km" )
                        {
                            pathUnit[0] = evt.measure.toFixed(4) + " km";
                            pathUnit[1] = evt.measure.toFixed(1) * 1000 + " m";
                            pathUnit[2] = (evt.measure * 3280.839895).toFixed(1) + " ft";
                            pathUnit[3] = (evt.measure * 0.62137119224).toFixed(4) + " mi";
                            pathUnit[4] = (evt.measure * 1093.6132983).toFixed(1) + " yd";
                            pathUnit[5] = (evt.measure * 0.539956803).toFixed(4) + " nmi";

                            if ( $( "measurementUnits" ).value == "kilometers" )
                            {
                                pathMeasurement.innerHTML = pathUnit[0];
                            }
                            else if ( $( "measurementUnits" ).value == "meters" )
                            {
                                pathMeasurement.innerHTML = pathUnit[1];
                            }
                            else if ( $( "measurementUnits" ).value == "feet" )
                            {
                                pathMeasurement.innerHTML = pathUnit[2];
                            }
                            else if ( $( "measurementUnits" ).value == "miles" )
                            {
                                pathMeasurement.innerHTML = pathUnit[3];
                            }
                            else if ( $( "measurementUnits" ).value == "yards" )
                            {
                                pathMeasurement.innerHTML = pathUnit[4];
                            }
                            else if ( $( "measurementUnits" ).value == "nautical miles" )
                            {
                                pathMeasurement.innerHTML = pathUnit[5];
                            }
                        }
                        else if ( evt.units == "m" )
                        {
                            pathUnit[0] = (evt.measure * 0.001).toFixed(4) + " km";
                            pathUnit[1] = evt.measure.toFixed(1) + " m";
                            pathUnit[2] = (evt.measure * 3.280839895).toFixed(1) + " ft";
                            pathUnit[3] = (evt.measure * 0.00062137119224).toFixed(4) + " mi";
                            pathUnit[4] = (evt.measure * 1.0936132983).toFixed(1) + " yd";
                            pathUnit[5] = (evt.measure * 0.000539957).toFixed(4) + " nmi";

                            if ( $( "measurementUnits" ).value == "kilometers" )
                            {
                                pathMeasurement.innerHTML = pathUnit[0];
                            }
                            else if ( $( "measurementUnits" ).value == "meters" )
                            {
                                pathMeasurement.innerHTML = pathUnit[1];
                            }
                            else if ( $( "measurementUnits" ).value == "feet" )
                            {
                                pathMeasurement.innerHTML = pathUnit[2];
                            }
                            else if ( $( "measurementUnits" ).value == "miles" )
                            {
                                pathMeasurement.innerHTML = pathUnit[3];
                            }
                            else if ( $( "measurementUnits" ).value == "yards" )
                            {
                                pathMeasurement.innerHTML = pathUnit[4];
                            }
                            else if ( $( "measurementUnits" ).value == "nautical miles" )
                            {
                                pathMeasurement.innerHTML = pathUnit[5];
                            }
                        }
                    }
                }
            } );



        }

        var polygonMeasurement = document.getElementById( "polygonMeasurement" );
        var polygonMeasurementButton;

        if ( polygonMeasurement && $( "measurementUnits" ) )
        {
            polygonMeasurementButton = new OpenLayers.Control.Measure( OpenLayers.Handler.Polygon, {
                title:"Click polygon measurement button to activate. Once activated click points on the map to create a polygon that you wish to measure. When you are done creating your polygon double click to end.",
                displayClass: "olControlMeasureArea", geodesic:true, displaySystem: "metric", persist: true,
                eventListeners:
                {
                    measure: function( evt )
                    {
                        if ( evt.units == "km" )
                        {
                            pathUnit[0] = evt.measure.toFixed(4) + " km^2";
                            pathUnit[1] = (evt.measure * 1000000).toFixed(1) + " m^2";
                            pathUnit[2] = (evt.measure * 10763910.416623611025).toFixed(1) + " ft^2";
                            pathUnit[3] = (evt.measure * .38610215854575903621).toFixed(4) + " mi^2";
                            pathUnit[4] = (evt.measure * 1195990.04621860478289).toFixed(1) + " yd^2";
                            pathUnit[5] = (evt.measure * 0.2915533496).toFixed(4) + " nmi^2";

                            if ( $( "measurementUnits" ).value == "kilometers" )
                            {
                                pathMeasurement.innerHTML = pathUnit[0];
                            }
                            else if ( $( "measurementUnits" ).value == "meters" )
                            {
                                pathMeasurement.innerHTML = pathUnit[1];
                            }
                            else if ( $( "measurementUnits" ).value == "feet" )
                            {
                                pathMeasurement.innerHTML = pathUnit[2];
                            }
                            else if ( $( "measurementUnits" ).value == "miles" )
                            {
                                pathMeasurement.innerHTML = pathUnit[3];
                            }
                            else if ( $( "measurementUnits" ).value == "yards" )
                            {
                                pathMeasurement.innerHTML = pathUnit[4];
                            }
                            else if ( $( "measurementUnits" ).value == "nautical miles" )
                            {
                                pathMeasurement.innerHTML = pathUnit[5];
                            }
                        }
                        else if ( evt.units == "m" )
                        {
                            pathUnit[0] = (evt.measure * 0.000001).toFixed(4) + " km^2";
                            pathUnit[1] = evt.measure.toFixed(1) + " m^2";
                            pathUnit[2] = (evt.measure * 10.763910416623611025).toFixed(1) + " ft^2";
                            pathUnit[3] = (evt.measure * .00000038610215854575).toFixed(4) + " mi^2";
                            pathUnit[4] = (evt.measure * 1.19599004621860478289).toFixed(1) + " yd^2";
                            pathUnit[5] = (evt.measure * 0.0000002915533496).toFixed(4) + " nmi^2";

                            if ( $( "measurementUnits" ).value == "kilometers" )
                            {
                                pathMeasurement.innerHTML = pathUnit[0];
                            }
                            else if ( $( "measurementUnits" ).value == "meters" )
                            {
                                pathMeasurement.innerHTML = pathUnit[1];
                            }
                            else if ( $( "measurementUnits" ).value == "feet" )
                            {
                                pathMeasurement.innerHTML = pathUnit[2];
                            }
                            else if ( $( "measurementUnits" ).value == "miles" )
                            {
                                pathMeasurement.innerHTML = pathUnit[3];
                            }
                            else if ( $( "measurementUnits" ).value == "yards" )
                            {
                                pathMeasurement.innerHTML = pathUnit[4];
                            }
                            else if ( $( "measurementUnits" ).value == "nautical miles" )
                            {
                                pathMeasurement.innerHTML = pathUnit[5];
                            }
                        }
                    }
                }
            } );

        }

        var container = $( "toolBar" );
        var panel = new OpenLayers.Control.Panel(
        {
            div: container,
            defaultControl: this.panButton,
            displayClass: "olControlPanel"
        } );

        openlayersMap.addControl( navButton );

        panel.addControls( [
            this.panButton,
            zoomInButton,
            zoomOutButton,
            zoomMaxExtentButton,
            zoomBoxButton,
            zoomInFullResButton
            //   	boundBoxButton,
            //   	clearAoiButton
            //   	navButton.next,
            //   	navButton.previous
        ] );
        if ( boundBoxButton && clearAoiButton )
        {
            panel.addControls( [
                boundBoxButton,
                clearAoiButton
            ] );
        }
        if ( pathMeasurementButton && polygonMeasurementButton )
        {
            panel.addControls( [
                pathMeasurementButton,
                polygonMeasurementButton
            ] );
        }


        openlayersMap.addControl( panel );

        OpenLayers.Event.stopObserving(window, "DOMMouseScroll", this.panButton.wheelObserver);
        OpenLayers.Event.stopObserving(window, "mousewheel", this.panButton.wheelObserver);
        OpenLayers.Event.stopObserving(document, "mousewheel", this.panButton.wheelObserver);

        this.wheelListener = OpenLayers.Function.bindAsEventListener(this.wheel, this);

        OpenLayers.Event.observe(window,   "DOMMouseScroll", this.wheelListener);
        OpenLayers.Event.observe(window,   "mousewheel",     this.wheelListener);
        OpenLayers.Event.observe(document, "mousewheel",     this.wheelListener);

        this.panButton.wheelObserver = null;
    };

    this.zoomInFullRes = function()
    {
        var zoom = openlayersMap.getZoomForResolution( zoomFullResScale, true );
        openlayersMap.zoomTo( zoom );

        if ( zoomInButton ) zoomInButton.displayClass = "olControlFoo";
    }

    this.zoomMaxExtent = function()
    {
        openlayersMap.zoomToMaxExtent();
        if ( zoomInButton ) zoomInButton.displayClass = "olControlZoomIn";
    }

    this.zoomIn = function()
    {
        openlayersMap.zoomIn();

        var fullRes = openlayersMap.getZoomForResolution( parseFloat( zoomFullResScale, true ) );

        if ( openlayersMap.getZoom() >= fullRes )
        {
            zoomInButton.displayClass = "olControlFoo";
        }
    };

    this.zoomOut = function()
    {
        openlayersMap.zoomOut();

        var fullRes = openlayersMap.getZoomForResolution( parseFloat( zoomFullResScale, true ) );

        if ( openlayersMap.getZoom() < fullRes )
        {
            zoomInButton.displayClass = "olControlZoomIn";
        }
    };

    this.clearPathMeasurement = function()
    {
        var pathMeasurement = document.getElementById( "pathMeasurementOutput" );

        if ( pathMeasurement ) pathMeasurement.innerHTML = "";
    };

    this.clearAreaMeasurement = function()
    {
        var areaMeasurement = document.getElementById( "areaMeasurementOutput" );

        if ( areaMeasurement ) areaMeasurement.innerHTML = "";
    };

    this.togglePointRadiusCheckBox = function()
    {
        $( "radiusSearchButton" ).checked = true;
        $( "radiusSearchButton2" ).checked = true;
        $( "radiusSearchButton3" ).checked = true;

        $( "bboxSearchButton" ).checked = false;
        $( "bboxSearchButton2" ).checked = false;
        $( "bboxSearchButton3" ).checked = false;
    };

    this.toggleBboxCheckBox = function()
    {
        $( "bboxSearchButton" ).checked = true;
        $( "bboxSearchButton2" ).checked = true;
        $( "bboxSearchButton3" ).checked = true;

        $( "radiusSearchButton" ).checked = false;
        $( "radiusSearchButton2" ).checked = false;
        $( "radiusSearchButton3" ).checked = false;
    };
    ////check this code

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

    this.updateOmarFiltersGivenHash = function( params )
    {
        var wmsParams = {};
        for ( x in params )
        {
            wmsParams[x] = params[x]
        }
        dataLayer.mergeNewParams( wmsParams );
    }
    this.updateOmarFilters = function( startDay, startMonth, startYear, startHour, startMinute, endDay, endMonth, endYear, endHour, endMinute, numberOfNames, numberOfValues, additionalParams )
    {
        var wmsParamsTemp = {};

        var hasStartDate = startDay != "" && startMonth != "" && startYear != "" && startHour != "" && startMinute != "";
        var startDateNoQuote = startYear + startMonth.leftPad( 2 ) + startDay.leftPad( 2 ) + 'T' + startHour.leftPad( 2 ) + startMinute.leftPad( 2 ) + '00Z';

        var hasEndDate = endDay != "" && endMonth != "" && endYear != "";
        var endDateNoQuote = endYear + endMonth.leftPad( 2 ) + endDay.leftPad( 2 ) + 'T' + endHour.leftPad( 2 ) + endMinute.leftPad( 2 ) + '00Z';

        var wmsTime = "";

        //alert("HAS END? " + hasEndDate + "==>" + endDay +"," + endMonth +","+ endYear + "," + endHour + "," + endMinute);
        //alert(endDateNoQuote);
        if ( hasStartDate )
        {
            wmsTime = startDateNoQuote;
            if ( hasEndDate )
            {
                wmsTime += "/" + endDateNoQuote;
            }
            else
            {
                wmsTime += "/";
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

        wmsParamsTemp = {"time":wmsTime};

        var tempName = "";

        if ( numberOfNames )
        {

            for ( idx = 0; idx < numberOfNames; ++idx )
            {
                tempName = "searchTagNames[" + idx + "]";
                tempValue = $( tempName ).value;
                if ( tempValue && !(tempValue === "null") )
                {
                    wmsParamsTemp["searchTagNames[" + idx + "]"] = $( tempName ).value;
                }
                else
                {
                    wmsParamsTemp["searchTagNames[" + idx + "]"] = "";
                }
            }
        }

        if ( numberOfNames )
        {
            for ( idx = 0; idx < numberOfValues; ++idx )
            {
                tempName = "searchTagValues[" + idx + "]";
                tempValue = $( tempName ).value;
                if ( tempValue && !(tempValue === "null") )
                {
                    wmsParamsTemp["searchTagValues[" + idx + "]"] = $( tempName ).value;
                }
                else
                {
                    wmsParamsTemp["searchTagValues[" + idx + "]"] = "";
                }
            }
        }

        if ( additionalParams )
        {

            for ( attr in additionalParams )
            {
                wmsParamsTemp[attr] = additionalParams[attr];
            }
        }
        //alert(JSON.stringify(wmsParamsTemp));
        dataLayer.mergeNewParams( wmsParamsTemp );
    };
}