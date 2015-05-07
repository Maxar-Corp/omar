
function omarInit()
{
    var oMenu = new YAHOO.widget.MenuBar( "menu1", {
        autosubmenudisplay:true,
        showdelay:0} );

    oMenu.render();

    var startDateChooser = new DateChooser();
    startDateChooser.setDisplayContainer( "startDateContainer" );
    startDateChooser.setInputId( "startDateInput" );
    startDateChooser.setStructId( "startDate" );
    startDateChooser.setFormat( "MM/dd/yyyy" );
    startDateChooser.setLocale( "en" );
    startDateChooser.setChangeCallback( "updateOmarFilters()" );
    startDateChooser.init();

    var endDateChooser = new DateChooser();
    endDateChooser.setDisplayContainer( "endDateContainer" );
    endDateChooser.setInputId( "endDateInput" );
    endDateChooser.setStructId( "endDate" );
    endDateChooser.setFormat( "MM/dd/yyyy" );
    endDateChooser.setLocale( "en" );
    endDateChooser.setChangeCallback( "updateOmarFilters()" );
    endDateChooser.init();

    pointRadiusTab.title = "Define a point radius by specifying a Center coordinate in either DD, DMS, or MGRS format and a Radius in meters.";
    boundBoxTab.title = "Define a bound box by specifing Lower Left and Upper Right coordinates in either DD, DMS, or MGRS format.";

    metadataTab.title = "Metadata";
    cqlTab.title = "CQL";

}

function Omar()
{
    var map;
    var convert = new CoordinateConversion();
    var ddGraticuleLayer;
    var dmsGraticuleLayer;
    this.setupMapWidget = function ()
    {
        map = new OpenLayers.Map( "map", {controls:[], theme:null} );

        //map.addControl(new OpenLayers.Control.KeyboardDefaults());
        map.addControl( new OpenLayers.Control.LayerSwitcher( {div:OpenLayers.Util.getElement( "layerSwitcher" ), roundedCorner:false} ) );
        map.addControl( new OpenLayers.Control.Scale() );
        map.addControl( new OpenLayers.Control.ScaleLine() );

        map.events.register("mousemove", map, this.setMousePositionDiv );
        map.events.register("moveend", this, this.setCenterForLayers );
        map.events.register("mouseup", this, this.setCenterForLayers)
    };
    this.setCenterForLayers = function(evt)
    {
        var extent = map.getExtent();
        var idx = 0;
        for(idx = 0; idx < map.layers.length; ++idx)
        {
            map.layers[idx].moveTo(extent);
        }
        this.setMapCenterTextField();
    };

    this.changeMapSize = function ()
    {
        map.updateSize();
    };

    var graticuleColor = "#4169E1";
    var graticuleOpacity = "0.7";
    this.setupGraticuleLayers = function ()
    {
        ddGraticuleLayer = new OpenLayers.Control.Graticule( {
            visible:false,
            numPoints:2,
            layerName:"DD Grid",
            labelled:true,
            labelFormat:"dd",
            lineSymbolizer:{strokeColor:graticuleColor, strokeOpacity:graticuleOpacity, strokeWidth:"1"},
            labelSymbolizer:{fontColor:graticuleColor, fontOpacity:graticuleOpacity}
        } );

        dmsGraticuleLayer = new OpenLayers.Control.Graticule( {
            visible:false,
            numPoints:2,
            layerName:"DMS Grid",
            labelled:true,
            labelFormat:"dms",
            lineSymbolizer:{strokeColor:graticuleColor, strokeOpacity:graticuleOpacity, strokeWidth:"1"},
            labelSymbolizer:{fontColor:graticuleColor, fontOpacity:graticuleOpacity}
        } );


        map.addControl(ddGraticuleLayer);
        map.addControl(dmsGraticuleLayer);
        //map.addControl( new OpenLayers.Control( ddGraticuleLayer ) );
        //map.addControl( new OpenLayers.Control( dmsGraticuleLayer ) );
    };

    var baseLayer;
    this.setupBaseLayers = function ( name, url, params, options )
    {
        baseLayer = new OpenLayers.Layer.WMS( name, url,
                params, options );

        if ( options.isBaseLayer )
        {
            map.addLayer( baseLayer );
            map.setBaseLayer( baseLayer );
        }
        else
        {
            map.addLayer( baseLayer );
        }
    };

    var dataLayer;
    this.setupDataLayer = function ( name, url, layers, styles, format )
    {
        dataLayer = new OpenLayers.Layer.WMS( name, url,
                {layers:layers, styles:styles, format:format, transparent:true},
                {isBaseLayer:false} );

        map.addLayer( dataLayer );
    };

    this.updateFootprints = function ()
    {
        dataLayer.redraw( true );
    };

    this.setMousePositionDiv = function ( evt )
    {
        var mousePosition = map.getLonLatFromViewPortPx( new OpenLayers.Pixel( evt.xy.x, evt.xy.y ) );

        var ddMousePosition = document.getElementById( "ddMousePosition" );
        var dmsMousePosition = document.getElementById( "dmsMousePosition" );
        var mgrsMousePosition = document.getElementById( "mgrsMousePosition" );

        if ( mousePosition.lat < "90" && mousePosition.lat > "-90" && mousePosition.lon < "180" && mousePosition.lon > "-180" )
        {
            ddMousePosition.innerHTML = "<b>DD:</b> " + mousePosition.lat + ", " + mousePosition.lon;
            dmsMousePosition.innerHTML = "<b>DMS:</b> " + convert.ddToDms( mousePosition.lat, mousePosition.lon );
            mgrsMousePosition.innerHTML = "<b>MGRS:</b> " + convert.ddToMgrs( mousePosition.lat, mousePosition.lon );
        }
        else
        {
            ddMousePosition.innerHTML = "<b>DD:</b> Outside of geographic extent.";
            dmsMousePosition.innerHTML = "<b>DMS:</b> Outside of geographic extent.";
            mgrsMousePosition.innerHTML = "<b>MGRS:</b> Outside of geographic extent.";
        }
    };

    this.setMapCenterTextField = function ()
    {
        var center = map.getCenter();

        if ( $( "displayUnit" ).value == "DD" )
        {
            $( "point" ).value = center.lat + ", " + center.lon;
        }
        else if ( $( "displayUnit" ).value == "DMS" )
        {
            $( "point" ).value = convert.ddToDms( center.lat, center.lon );
        }
        else if ( $( "displayUnit" ).value == "MGRS" )
        {
            $( "point" ).value = convert.ddToMgrs( center.lat, center.lon );
        }
        if(ddGraticuleLayer.active)
        {
            ddGraticuleLayer.deactivate();
            ddGraticuleLayer.activate();

        }
        if(dmsGraticuleLayer.active)
        {
            dmsGraticuleLayer.deactivate();
            dmsGraticuleLayer.activate();
        }
    };

    var ddRegExp = /^(\-?\d{1,2})(\.\d+)?\,?\s?(\-?\d{1,3})(\.\d+)?$/
    var dmsRegExp = /^(\d{1,2})\°?\s?(\d{1,2})\'?\s?(\d{1,2})\s?(\.\d+)?\"?\s?([NnSs])?\,?\s?(\d{1,3})\°?\s?(\d{1,2})\'?\s?(\d{1,2})\s?(\.\d+)?\"?\s?([EeWw])?$/
    var mgrsRegExp = /^(\d{1,2})\s?([C-X])\s?([A-Z])\s?([A-Z])\s?(\d{1,5})\s?(\d{1,5})?/
    this.setMapCenter = function ()
    {
        if ( $( "point" ).value.match( ddRegExp ) )
        {
            $( "displayUnit" ).value = "DD";

            var match = ddRegExp.exec( $( "point" ).value );
            var lat = match[1] + match[2];
            var lon = match[3] + match[4];
            var center = new OpenLayers.LonLat( lon, lat );
            var zoom = map.getZoom();

            map.setCenter( center, zoom );
        }
        else if ( $( "point" ).value.match( dmsRegExp ) )
        {
            $( "displayUnit" ).value = "DMS";

            var match = dmsRegExp.exec( $( "point" ).value );
            var lat = convert.dmsToDd( match[1], match[2], match[3] + match[4], match[5] );
            var lon = convert.dmsToDd( match[6], match[7], match[8] + match[9], match[10] );
            var center = new OpenLayers.LonLat( lon, lat );
            var zoom = map.getZoom();

            map.setCenter( center, zoom );
        }
        else if ( $( "point" ).value.match( mgrsRegExp ) )
        {
            $( "displayUnit" ).value = "MGRS";

            var match = mgrsRegExp.exec( $( "point" ).value );
            var mgrs = convert.mgrsToDd( match[1], match[2], match[3], match[4], match[5], match[6] );
            var match2 = ddRegExp.exec( mgrs );
            var lat = match2[1] + match2[2];
            var lon = match2[3] + match2[4];
            var center = new OpenLayers.LonLat( lon, lat );
            var zoom = map.getZoom();

            map.setCenter( center, zoom );
        }
        else
        {
            alert( "Invalid Map Center.\n" + $( "point" ).value );
            this.setMapCenterTextField();
        }
    };

    var aoiLayer;
    this.setupAoiLayer = function ()
    {
        aoiLayer = new OpenLayers.Layer.Vector( "Bound Box" );
        aoiLayer.events.register( "featureadded", aoiLayer, this.setAoiLayer );

        var boundBox = new OpenLayers.Control.DrawFeature( aoiLayer, OpenLayers.Handler.RegularPolygon,
                {handlerOptions:{sides:4, irregular:true}} );

        map.addLayer( aoiLayer );
        map.addControl( boundBox );
    };

    var bounds, bounds2;
    this.setAoiLayer = function ( e )
    {
        var geom = e.feature.geometry;
        bounds = geom.getBounds();
        var feature = new OpenLayers.Feature.Vector( geom );

        if ( $( "displayUnit" ).value == "DD" )
        {
            $( "lowerLeft" ).value = bounds.bottom + ", " + bounds.left;
            $( "upperRight" ).value = bounds.top + ", " + bounds.right;
        }
        else if ( $( "displayUnit" ).value == "DMS" )
        {
            $( "lowerLeft" ).value = convert.ddToDms( bounds.bottom, bounds.left );
            $( "upperRight" ).value = convert.ddToDms( bounds.top, bounds.right );
        }
        else if ( $( "displayUnit" ).value == "MGRS" )
        {
            $( "lowerLeft" ).value = convert.ddToMgrs( bounds.bottom, bounds.left );
            $( "upperRight" ).value = convert.ddToMgrs( bounds.top, bounds.right );
        }

        aoiLayer.destroyFeatures();
        aoiLayer.addFeatures( feature, {silent:true} );
    };

    this.setupAoi = function ( minLon, minLat, maxLon, maxLat )
    {
        bounds2 = new OpenLayers.Bounds( minLon, minLat, maxLon, maxLat );
        var feature = new OpenLayers.Feature.Vector( bounds2.toGeometry() );

        $( "aoiMinLon" ).value = bounds2.left;
        $( "aoiMinLat" ).value = bounds2.bottom;
        $( "aoiMaxLon" ).value = bounds2.right;
        $( "aoiMaxLat" ).value = bounds2.top;

        if ( $( "displayUnit" ).value == "DD" )
        {
            $( "lowerLeft" ).value = bounds2.bottom + ", " + bounds2.left;
            $( "upperRight" ).value = bounds2.top + ", " + bounds2.right;
        }
        else if ( $( "displayUnit" ).value == "DMS" )
        {
            $( "lowerLeft" ).value = convert.ddToDms( bounds2.bottom, bounds2.left );
            $( "upperRight" ).value = convert.ddToDms( bounds2.top, bounds2.right );
        }
        else if ( $( "displayUnit" ).value == "MGRS" )
        {
            $( "lowerLeft" ).value = convert.ddToMgrs( bounds2.bottom, bounds2.left );
            $( "upperRight" ).value = convert.ddToMgrs( bounds2.top, bounds2.right );
        }

        aoiLayer.destroyFeatures();
        aoiLayer.addFeatures( feature, {silent:true} );
    };

    this.setBoundBoxTextField = function ()
    {
        if ( bounds )
        {
            if ( $( "displayUnit" ).value == "DD" )
            {
                $( "lowerLeft" ).value = bounds.bottom + ", " + bounds.left;
                $( "upperRight" ).value = bounds.top + ", " + bounds.right;
            }
            else if ( $( "displayUnit" ).value == "DMS" )
            {
                $( "lowerLeft" ).value = convert.ddToDms( bounds.bottom, bounds.left );
                $( "upperRight" ).value = convert.ddToDms( bounds.top, bounds.right );
            }
            else if ( $( "displayUnit" ).value == "MGRS" )
            {
                $( "lowerLeft" ).value = convert.ddToMgrs( bounds.bottom, bounds.left );
                $( "upperRight" ).value = convert.ddToMgrs( bounds.top, bounds.right );
            }
        }
        else if ( bounds2 )
        {
            if ( $( "displayUnit" ).value == "DD" )
            {
                $( "lowerLeft" ).value = bounds2.bottom + ", " + bounds2.left;
                $( "upperRight" ).value = bounds2.top + ", " + bounds2.right;
            }
            else if ( $( "displayUnit" ).value == "DMS" )
            {
                $( "lowerLeft" ).value = convert.ddToDms( bounds2.bottom, bounds2.left );
                $( "upperRight" ).value = convert.ddToDms( bounds2.top, bounds2.right );
            }
            else if ( $( "displayUnit" ).value == "MGRS" )
            {
                $( "lowerLeft" ).value = convert.ddToMgrs( bounds2.bottom, bounds2.left );
                $( "upperRight" ).value = convert.ddToMgrs( bounds2.top, bounds2.right );
            }
        }
    };

    this.clearBoundBox = function ()
    {
        aoiLayer.destroyFeatures();

        $( "lowerLeft" ).value = "";
        $( "upperRight" ).value = "";
        $( "aoiMinLat" ).value = "";
        $( "aoiMinLon" ).value = "";
        $( "aoiMaxLat" ).value = "";
        $( "aoiMaxLon" ).value = "";
    };

    this.setupSearch = function ()
    {
        if ( $( "baseQueryType" ).value == "RADIUS" )
        {
            if ( $( "point" ).value.match( ddRegExp ) )
            {
                var match = ddRegExp.exec( $( "point" ).value );
                if ( match[2] && match[4] )
                {
                    $( "centerLat" ).value = match[1] + match[2];
                    $( "centerLon" ).value = match[3] + match[4];
                }
                else
                {
                    $( "centerLat" ).value = match[1];
                    $( "centerLon" ).value = match[3];
                }

                $( "aoiRadius" ).value = $( "radius" ).value;
            }
            else if ( $( "point" ).value.match( dmsRegExp ) )
            {
                var match = dmsRegExp.exec( $( "point" ).value );
                $( "centerLat" ).value = convert.dmsToDd( match[1], match[2], match[3] + match[4], match[5] );
                $( "centerLon" ).value = convert.dmsToDd( match[6], match[7], match[8] + match[9], match[10] );

                $( "aoiRadius" ).value = $( "radius" ).value;
            }
            else if ( $( "point" ).value.match( mgrsRegExp ) )
            {
                var match = mgrsRegExp.exec( $( "point" ).value );
                var mgrs = convert.mgrsToDd( match[1], match[2], match[3], match[4], match[5], match[6] );
                var match2 = ddRegExp.exec( mgrs );
                if ( match2[2] && match2[4] )
                {
                    $( "centerLat" ).value = match2[1] + match2[2];
                    $( "centerLon" ).value = match2[3] + match2[4];
                }
                else
                {
                    $( "centerLat" ).value = match2[1];
                    $( "centerLon" ).value = match2[3];
                }

                $( "aoiRadius" ).value = $( "radius" ).value;
            }
        }
        else if ( $( "baseQueryType" ).value == "BBOX" )
        {
            if ( $( "lowerLeft" ).value.match( ddRegExp ) )
            {
                var match = ddRegExp.exec( $( "lowerLeft" ).value );
                if ( match[2] && match[4] )
                {
                    $( "aoiMinLat" ).value = match[1] + match[2];
                    $( "aoiMinLon" ).value = match[3] + match[4];
                }
                else
                {
                    $( "aoiMinLat" ).value = match[1];
                    $( "aoiMinLon" ).value = match[3];
                }
                if ( $( "upperRight" ).value.match( ddRegExp ) )
                {
                    var match2 = ddRegExp.exec( $( "upperRight" ).value );
                    if ( match2[2] && match2[4] )
                    {
                        $( "aoiMaxLat" ).value = match2[1] + match2[2];
                        $( "aoiMaxLon" ).value = match2[3] + match2[4];
                    }
                    else
                    {
                        $( "aoiMaxLat" ).value = match2[1];
                        $( "aoiMaxLon" ).value = match2[3];
                    }
                }
            }
            else if ( $( "lowerLeft" ).value.match( dmsRegExp ) )
            {
                var match = dmsRegExp.exec( $( "lowerLeft" ).value );
                $( "aoiMinLat" ).value = convert.dmsToDd( match[1], match[2], match[3] + match[4], match[5] );
                $( "aoiMinLon" ).value = convert.dmsToDd( match[6], match[7], match[8] + match[9], match[10] );

                if ( $( "upperRight" ).value.match( dmsRegExp ) )
                {
                    var match2 = dmsRegExp.exec( $( "upperRight" ).value );
                    $( "aoiMaxLat" ).value = convert.dmsToDd( match2[1], match2[2], match2[3] + match2[4], match2[5] );
                    $( "aoiMaxLon" ).value = convert.dmsToDd( match2[6], match2[7], match2[8] + match2[9], match2[10] );
                }
            }
            else if ( $( "lowerLeft" ).value.match( mgrsRegExp ) )
            {
                var match = mgrsRegExp.exec( $( "lowerLeft" ).value );
                var mgrs = convert.mgrsToDd( match[1], match[2], match[3], match[4], match[5], match[6] );
                var match2 = ddRegExp.exec( mgrs );
                if ( match2[2] && match2[4] )
                {
                    $( "aoiMinLat" ).value = match2[1] + match2[2];
                    $( "aoiMinLon" ).value = match2[3] + match2[4];
                }
                else
                {
                    $( "aoiMinLat" ).value = match2[1];
                    $( "aoiMinLon" ).value = match2[3];
                }
                if ( $( "upperRight" ).value.match( mgrsRegExp ) )
                {
                    var match2 = mgrsRegExp.exec( $( "upperRight" ).value );
                    var mgrs2 = convert.mgrsToDd( match2[1], match2[2], match2[3], match2[4], match2[5], match2[6] );
                    var match3 = ddRegExp.exec( mgrs2 );
                    if ( match3[2] && match3[4] )
                    {
                        $( "aoiMaxLat" ).value = match3[1] + match3[2];
                        $( "aoiMaxLon" ).value = match3[3] + match3[4];
                    }
                    else
                    {
                        $( "aoiMaxLat" ).value = match3[1];
                        $( "aoiMaxLon" ).value = match3[3];
                    }
                }
            }
        }
        this.setCurrentViewport();
    };

    var zoomInButton;
    var measureUnit = new Array();
    measureUnit = ["", "", "", "", "", ""];
    this.setupToolBar = function ()
    {
        var panButton = new OpenLayers.Control.MouseDefaults( {title:"Click button to activate. Once activated, drag the mouse to pan."} );

		zoomInButton = new OpenLayers.Control.Button( {title:"Click button to zoom in.",
            displayClass:"olControlZoomIn",
            trigger:this.zoomIn} );

        var zoomOutButton = new OpenLayers.Control.Button( {title:"Click button to zoom out.",
            displayClass:"olControlZoomOut",
            trigger:this.zoomOut} );

        var zoomMaxExtentButton = new OpenLayers.Control.ZoomToMaxExtent( {title:"Click button to zoom to the max extent of the map.",
            trigger:this.zoomMaxExtent} );
		
		//var zoomBoxButton = new OpenLayers.Control.ZoomBox( {title:"Click button to activate. Once activated, drag the mouse to define a zoom box."} );
		
		var zoomBoxButton =  new OpenLayers.Control.ZoomBox({alwaysZoom:true});
		
		
        var zoomInFullResButton = new OpenLayers.Control.Button( {title:"Click button to zoom to full resolution.",
            displayClass:"olControlZoomToLayer",
            trigger:this.zoomInFullRes} );

        var boundBoxButton = new OpenLayers.Control.DrawFeature( aoiLayer, OpenLayers.Handler.RegularPolygon,
                {handlerOptions:{sides:4, irregular:true},
                    title:"Click button to activate. Once activated, drag the mouse to define a bound box."} );

        var clearAoiButton = new OpenLayers.Control.Button( {title:"Click button to clear the bound box.",
            displayClass:"olControlClearAreaOfInterest",
            trigger:this.clearBoundBox} );

        var pathMeasurement = document.getElementById( "pathMeasurement" );
        var pathMeasurementButton = new OpenLayers.Control.Measure( OpenLayers.Handler.Path, {title:"Click button to activate. Once acitivated, click points on the map to create a path that you wish to measure. When you are done creating your path, double click to end.",
            displayClass:"olControlMeasureDistance",
            geodesic:true,
            persist:true,
            eventListeners:{
                measure:function ( evt )
                {
                    if ( evt.units == "km" )
                    {
                        measureUnit[0] = evt.measure + " km";
                        measureUnit[1] = evt.measure * 1000 + " m";
                        measureUnit[2] = evt.measure * 3280.839895 + " ft";
                        measureUnit[3] = evt.measure * 0.62137119224 + " mi";
                        measureUnit[4] = evt.measure * 1093.6132983 + " yd";
						measureUnit[5] = evt.measure * 0.539956803 + " nmi";
                        if ( $( "measurementUnits" ).value == "kilometers" )
                        {
                            pathMeasurement.innerHTML = measureUnit[0];
                        }
                        else if ( $( "measurementUnits" ).value == "meters" )
                        {
                            pathMeasurement.innerHTML = measureUnit[1];
                        }
                        else if ( $( "measurementUnits" ).value == "feet" )
                        {
                            pathMeasurement.innerHTML = measureUnit[2];
                        }
                        else if ( $( "measurementUnits" ).value == "miles" )
                        {
                            pathMeasurement.innerHTML = measureUnit[3];
                        }
                        else if ( $( "measurementUnits" ).value == "yards" )
                        {
                            pathMeasurement.innerHTML = measureUnit[4];
                        }
                        else if ( $( "measurementUnits" ).value == "nautical miles" )
                        {
                            pathMeasurement.innerHTML = measureUnit[5];
                        }
                    }
                    else if ( evt.units == "m" )
                    {
                        measureUnit[0] = evt.measure * 0.001 + " km";
                        measureUnit[1] = evt.measure + " m";
                        measureUnit[2] = evt.measure * 3.280839895 + " ft";
                        measureUnit[3] = evt.measure * 0.00062137119224 + " mi";
                        measureUnit[4] = evt.measure * 1.0936132983 + " yd";
						measureUnit[5] = evt.measure * 0.000539956803 + " nmi";
                        if ( $( "measurementUnits" ).value == "kilometers" )
                        {
                            pathMeasurement.innerHTML = measureUnit[0];
                        }
                        else if ( $( "measurementUnits" ).value == "meters" )
                        {
                            pathMeasurement.innerHTML = measureUnit[1];
                        }
                        else if ( $( "measurementUnits" ).value == "feet" )
                        {
                            pathMeasurement.innerHTML = measureUnit[2];
                        }
                        else if ( $( "measurementUnits" ).value == "miles" )
                        {
                            pathMeasurement.innerHTML = measureUnit[3];
                        }
                        else if ( $( "measurementUnits" ).value == "yards" )
                        {
                            pathMeasurement.innerHTML = measureUnit[4];
                        }
                        else if ( $( "measurementUnits" ).value == "nautical miles" )
                        {
                            pathMeasurement.innerHTML = measureUnit[5];
                        }
                    }
                }
            }} );

        var polygonMeasurement = document.getElementById( "polygonMeasurement" );
        var polygonMeasurementButton = new OpenLayers.Control.Measure( OpenLayers.Handler.Polygon, {title:"Click button to activate. Once acitivated, click points on the map to create a polygon that you wish to measure. When you are done creating your polygon, double click to end.",
            displayClass:"olControlMeasureArea",
            displaySystem:"metric",
            geodesic:true,
            persist:true,
            eventListeners:{
                measure:function ( evt )
                {
                    if ( evt.units == "km" )
                    {
                        measureUnit[0] = evt.measure + " km^2";
                        measureUnit[1] = evt.measure * 1000000 + " m^2";
                        measureUnit[2] = evt.measure * 10763910.417 + " ft^2";
                        measureUnit[3] = evt.measure * 0.38610215855  + " mi^2";
                        measureUnit[4] = evt.measure * 1195990.0463 + " yd^2";
						measureUnit[5] = evt.measure * 0.2915533496  + " nmi^2";
                        if ( $( "measurementUnits" ).value == "kilometers" )
                        {
                            pathMeasurement.innerHTML = measureUnit[0];
                        }
                        else if ( $( "measurementUnits" ).value == "meters" )
                        {
                            pathMeasurement.innerHTML = measureUnit[1];
                        }
                        else if ( $( "measurementUnits" ).value == "feet" )
                        {
                            pathMeasurement.innerHTML = measureUnit[2];
                        }
                        else if ( $( "measurementUnits" ).value == "miles" )
                        {
                            pathMeasurement.innerHTML = measureUnit[3];
                        }
                        else if ( $( "measurementUnits" ).value == "yards" )
                        {
                            pathMeasurement.innerHTML = measureUnit[4];
                        }
                        else if ( $( "measurementUnits" ).value == "nautical miles" )
                        {
                            pathMeasurement.innerHTML = measureUnit[5];
                        }
                    }
                    else if ( evt.units == "m" )
                    {
                        measureUnit[0] = evt.measure * 0.000001 + " km^2";
                        measureUnit[1] = evt.measure + " m^2";
                        measureUnit[2] = evt.measure * 10.763910417 + " ft^2";
                        measureUnit[3] = evt.measure * 3.8610215855 + " mi^2";
                        measureUnit[4] = evt.measure * 1.1959900463 + " yd^2";
						measureUnit[5] = evt.measure * 2.915533496 + " nmi^2";
                        if ( $( "measurementUnits" ).value == "kilometers" )
                        {
                            pathMeasurement.innerHTML = measureUnit[0];
                        }
                        else if ( $( "measurementUnits" ).value == "meters" )
                        {
                            pathMeasurement.innerHTML = measureUnit[1];
                        }
                        else if ( $( "measurementUnits" ).value == "feet" )
                        {
                            pathMeasurement.innerHTML = measureUnit[2];
                        }
                        else if ( $( "measurementUnits" ).value == "miles" )
                        {
                            pathMeasurement.innerHTML = measureUnit[3];
                        }
                        else if ( $( "measurementUnits" ).value == "yards" )
                        {
                            pathMeasurement.innerHTML = measureUnit[4];
                        }
                        else if ( $( "measurementUnits" ).value == "nautical miles" )
                        {
                            pathMeasurement.innerHTML = measureUnit[5];
                        }
                    }
                }
            }} );

        var panel = new OpenLayers.Control.Panel( {
            div:$( "toolBar" ),
            defaultControl:panButton,
            displayClass:"olControlPanel"
        } );

        panel.addControls( [
            panButton,
            zoomBoxButton,
            zoomInButton,
            zoomOutButton,
            zoomInFullResButton,
            zoomMaxExtentButton,
            boundBoxButton,
            clearAoiButton,
            pathMeasurementButton,
            polygonMeasurementButton
        ] );

        map.addControl( panel );
    };

    var zoomFullResScale;
    this.zoomIn = function ()
    {
        map.zoomIn();

        var fullRes = map.getZoomForResolution( zoomFullResScale, true );

        if ( map.getZoom() >= fullRes )
        {
            zoomInButton.displayClass = "olControlFoo";
        }
    };

    this.zoomOut = function ()
    {
        map.zoomOut();

        var fullRes = map.getZoomForResolution( zoomFullResScale, true );

        if ( map.getZoom() < fullRes )
        {
            zoomInButton.displayClass = "olControlZoomIn";
        }
    };

    this.zoomMaxExtent = function ()
    {
        map.zoomToMaxExtent();
        zoomInButton.displayClass = "olControlZoomIn";
    };

    this.zoomInFullRes = function ()
    {
        var fullRes = map.getZoomForResolution( zoomFullResScale, true );

        map.zoomTo( fullRes );
        zoomInButton.displayClass = "olControlFoo";
    };

    this.changeMeasureUnit = function ( measureUnit )
    {
        if ( measureUnit == "kilometers" )
        {
            pathMeasurement.innerHTML = this.getMeasureUnit()[0];
        }
        else if ( measureUnit == "meters" )
        {
            pathMeasurement.innerHTML = this.getMeasureUnit()[1];
        }
        else if ( measureUnit == "feet" )
        {
            pathMeasurement.innerHTML = this.getMeasureUnit()[2];
        }
        else if ( measureUnit == "miles" )
        {
            pathMeasurement.innerHTML = this.getMeasureUnit()[3];
        }
        else if ( measureUnit == "yards" )
        {
            pathMeasurement.innerHTML = this.getMeasureUnit()[4];
        }
        else if ( measureUnit == "nautical miles" )
        {
            pathMeasurement.innerHTML = this.getMeasureUnit()[5];
        }
    };

    this.getMeasureUnit = function ()
    {
        return measureUnit;
    };

    var date, month, day, year, hour, minute, second, currentDateTime;
    this.getCurrentDateTime = function ()
    {
        date = new Date();

        month = date.getMonth() + 1;
        if ( month < 10 )
        {
            month = "0" + month;
        }

        day = date.getDate();
        if ( day < 10 )
        {
            day = "0" + day;
        }

        year = date.getFullYear();

        hour = date.getUTCHours();
        if ( hour < 10 )
        {
            hour = "0" + hour;
        }

        minute = date.getUTCMinutes();
        if ( minute < 10 )
        {
            minute = "0" + minute;
        }

        second = date.getUTCSeconds();
        if ( second < 10 )
        {
            second = "0" + second;
        }

        currentDateTime = document.getElementById( "currentDateTimeDiv" );
        currentDateTime.innerHTML = month + "/" + day + "/" + year + " " + hour + ":" + minute + ":" + second + " Zulu";
    };

    this.setupMapView = function ( viewMinLon, viewMinLat, viewMaxLon, viewMaxLat )
    {
        var bounds = new OpenLayers.Bounds( viewMinLon, viewMinLat, viewMaxLon, viewMaxLat );
        var zoom = map.getZoomForExtent( bounds, true );

        map.setCenter( bounds.getCenterLonLat(), zoom );
    };

    this.setCurrentViewport = function ()
    {
        var bounds = map.getExtent();

        $( "viewMinLon" ).value = bounds.left;
        $( "viewMaxLat" ).value = bounds.top;
        $( "viewMaxLon" ).value = bounds.right;
        $( "viewMinLat" ).value = bounds.bottom;
    };

    String.prototype.leftPad = function ( l, c )
    {
        return new Array( l - this.length + 1 ).join( c || '0' ) + this;
    };

    this.updateOmarFilters = function ( startDay, startMonth, startYear, startHour, startMinute, endDay, endMonth, endYear, endHour, endMinute, numberOfNames, numberOfValues, additionalParams )
    {
        var wmsParamsTemp = {};

        var hasStartDate = startDay != "" && startMonth != "" && startYear != "" && startHour != "" && startMinute != "";
        var startDateNoQuote = startYear + startMonth.leftPad( 2 ) + startDay.leftPad( 2 ) + 'T' + startHour.leftPad( 2 ) + startMinute.leftPad( 2 ) + '00Z';

        var hasEndDate = endDay != "" && endMonth != "" && endYear != "";
        var endDateNoQuote = endYear + endMonth.leftPad( 2 ) + endDay.leftPad( 2 ) + 'T' + endHour.leftPad( 2 ) + endMinute.leftPad( 2 ) + '00Z';

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