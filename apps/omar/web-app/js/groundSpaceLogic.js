/**
 * Created with IntelliJ IDEA.
 * User: sbortman
 * Date: 9/18/13
 * Time: 1:51 PM
 * To change this template use File | Settings | File Templates.
 */

function BusyIndicator()
{
    this.spinControl = null;
    this.spinCount = 0;

    this.spinnerOpts = {
        lines: 13, // The number of lines to draw
        length: 8, // The length of each line
        width: 4, // The line thickness
        radius: 10, // The radius of the inner circle
        corners: 1, // Corner roundness (0..1)
        rotate: 0, // The rotation offset
        color: '#FFFFFF', // #rgb or #rrggbb
        speed: 1, // Rounds per second
        trail: 60, // Afterglow percentage
        shadow: true, // Whether to render a shadow
        hwaccel: false, // Whether to use hardware acceleration
        className: 'spinnerControl', // The CSS class to assign to the spinner
        zIndex: 2e9, // The z-index (defaults to 2000000000)
        top: 'auto', // Top position relative to parent in px
        left: 'auto' // Left position relative to parent in px
    };


    this.loadStart = function ()
    {
        this.spinCount += 1;

        if ( (this.spinCount > 0) )
        {
            this.spin();
        }
    };

    this.loadEnd = function ()
    {
        this.spinCount -= 1;
        if ( (this.spinCount < 1) && this.spinControl )
        {
            this.spinControl.stop();
            this.spinCount = 0;
        }
    };

    this.spin = function ()
    {
        var targetDiv = YAHOO.util.Dom.get( "mapContainerDivId" );
        if ( this.spinControl )
        {
            this.spinControl.spin( targetDiv );
        }
        else
        {
            this.spinControl = new Spinner( this.spinnerOpts ).spin( targetDiv );
        }
    };
}

function BrightnessContrastEditor( params )
{
    this.brightnessSlider = YAHOO.widget.Slider.getHorizSlider(
        "slider-brightness-bg", "slider-brightness-thumb", 0, 100, 1 );
    this.contrastSlider = YAHOO.widget.Slider.getHorizSlider(
        "slider-contrast-bg", "slider-contrast-thumb", 0, 100, 1 );

    this.resetBrightnessContrast = function ()
    {
        this.brightnessSlider.setRealValue( 0 );
        this.contrastSlider.setRealValue( 1.0 );
    };

    this.brightnessSlider.animate = false;

    this.brightnessSlider.getRealValue = function ()
    {
        return ((this.getValue() - 50) / 50.0);
    };

    this.brightnessSlider.setRealValue = function ( value )
    {
        this.setValue( (value + 1) * 50 );
    };

    this.brightnessSlider.subscribe( "slideEnd", function ()
    {
        for ( var layer in rasterLayers )
        {
            wcsParams.setProperties( {brightness: this.getRealValue()} );
            rasterLayers[layer].mergeNewParams( {brightness: this.getRealValue()} );
        }
    } );

    this.getBrightness = function ()
    {
        this.brightnessSlider.getRealValue()
    };

    this.brightnessSlider.subscribe( "change", function ( offsetFromStart )
    {
        wcsParams.setProperties( {brightness: this.getRealValue()} );
        $( "brightness" ).value = this.getRealValue();
        $( "brightnessTextField" ).value = this.getRealValue();
    } );


    this.brightnessSlider.setRealValue( params.brightness || 0 );


    this.contrastSlider.getRealValue = function ()
    {
        var value = (this.getValue() / 100.0) * 2.0;
        return value;
    };

    this.contrastSlider.setRealValue = function ( value )
    {
        this.setValue( value * 50 );
    };

    this.contrastSlider.subscribe( "slideEnd", function ()
    {
        for ( var layer in rasterLayers )
        {
            wcsParams.setProperties( {contrast: this.getRealValue()} );
            rasterLayers[layer].mergeNewParams( {contrast: this.getRealValue()} );
        }
    } );

    this.contrastSlider.subscribe( "change", function ( offsetFromStart )
    {
        $( "contrast" ).value = this.getRealValue();
        $( "contrastTextField" ).value = this.getRealValue();
    } );

    this.contrastSlider.setRealValue( params.contrast || 1 );

    this.getContrast = function ()
    {
        this.contrastSlider.getRealValue()
    };

}

/*********************************************************************************************************************/
function initBands()
{
    var colorModel = $( "colorModel" );
    if ( colorModel )
    {

        if ( $( "bands" ).value == "default" )
        {
            colorModel.value = "Default";
        }
        else
        {
            var bandList = $( "bands" ).value.split( "," );
            if ( bandList.length >= 3 )
            {
                colorModel.value = "Color";
                $( "redBand" ).value = bandList[0];
                $( "greenBand" ).value = bandList[1];
                $( "blueBand" ).value = bandList[2];
            }
            else
            {
                colorModel.value = "Gray";
                $( "redBand" ).value = bandList[0];
                $( "greenBand" ).value = bandList[0];
                $( "blueBand" ).value = bandList[0];
            }
        }
        bandsChanged();
    }
}

function bandsChanged()
{
    var colorModel = $( 'colorModel' ).value;

    if ( colorModel === 'Default' )
    {
        $( 'redRow' ).style.display = 'none';
        $( 'greenRow' ).style.display = 'none';
        $( 'blueRow' ).style.display = 'none';
        $( 'bands' ).value = 'default';
        $( 'bandSwitcherTable' ).style.display = 'none';
    }
    else if ( colorModel === 'Gray' )
    {
        $( 'bandSwitcherTable' ).style.display = 'block';
        $( 'redRow' ).style.display = 'table-row';
        $( 'greenRow' ).style.display = 'none';
        $( 'blueRow' ).style.display = 'none';
        $( 'band0' ).innerHTML = "Band:&nbsp;";
        $( 'bands' ).value = $( 'redBand' ).value;
    }
    else if ( colorModel === 'Color' )
    {
        $( 'bandSwitcherTable' ).style.display = 'block';
        $( 'redRow' ).style.display = 'table-row';
        $( 'greenRow' ).style.display = 'table-row';
        $( 'blueRow' ).style.display = 'table-row';
        $( 'band0' ).innerHTML = "Red:&nbsp;";

        $( 'bands' ).value = [
            $( 'redBand' ).value,
            $( 'greenBand' ).value,
            $( 'blueBand' ).value
        ].join( ',' );
    }

    changeBandsOpts();
}

//  Initialize these values?
//    $( 'redRow' ).style.display = 'none';
//    $( 'greenRow' ).style.display = 'none';
//    $( 'blueRow' ).style.display = 'none';
//    $( 'bandSwitcherTable' ).style.display = 'none';

/*********************************************************************************************************************/

var popup;
var mapWidget;
var select;

var busyIndicator = new BusyIndicator();
var brightnessContrastEditor;

var coordConvert = new CoordinateConversion();
var wcsParams = new OmarWcsParams();
var kmlLayers;
var rasterLayers;
var templateParams;
var minLon;
var minLat;
var maxLon;
var maxLat;

var largestScale;
var smallestScale;
var fullResScale;
var counter = 0;

var ddRegExp = /^(\-?\d{1,2})(\.\d+)?\,?\s?(\-?\d{1,3})(\.\d+)?$/;
var dmsRegExp = /^(\d{1,2})\°?\s?(\d{1,2})\'?\s?(\d{1,2})\s?(\.\d+)?\"?\s?([NnSs])?\,?\s?(\d{1,3})\°?\s?(\d{1,2})\'?\s?(\d{1,2})\s?(\.\d+)?\"?\s?([EeWw])?$/;
var mgrsRegExp = /^(\d{1,2})\s?([C-X])\s?([A-Z])\s?([A-Z])\s?(\d{1,5})\s?(\d{1,5})?/;


function rotateUpIsUp()
{
    changeToImageSpace( (upIsUpAngle - azimuthAngle) % 360.0 );
}

function rotateNorthUp()
{
    changeToImageSpace( 0.0 );
}

function resetMapCenter()
{
    bounds = new OpenLayers.Bounds( minLon, minLat, maxLon, maxLat );
    zoom = mapWidget.getMap().getZoom();
    mapWidget.getMap().setCenter( bounds.getCenterLonLat(), zoom );
}

function setupOverviewCheck()
{
    if ( !onDemand )
    {
        return;
    }
    if ( numberOfResLevels < 2 )
    {
        var label = YAHOO.util.Dom.get( "busyCursorLabel" );
        YAHOO.util.Dom.setStyle( "busyCursor", "top", "5px" );
        YAHOO.util.Dom.setStyle( "busyCursor", "right", "12px" );
        YAHOO.util.Dom.setStyle( "busyCursorImage", "display", "inline" );

        YAHOO.util.Dom.setStyle( "busyCursor", "display", "inline" );
        YAHOO.util.Dom.setStyle( "busyCursorLabel", "display", "inline" );
        YAHOO.util.Dom.setStyle( "busyCursorImage", "display", "inline" );
        label.innerHTML = "Generating overviews...";
        setTimeout( checkOverview, 5000 );
    }
}


function toUrlParamString( params )
{
    var urlParams = "";
    for ( var key in params )
    {
        if ( urlParams == "" )
        {
            urlParams = key + "=" + params[key];
        }
        else
        {
            urlParams = urlParams + "&" + key + "=" + params[key]
        }
    }
    return urlParams
}

function onFeatureSelect( event )
{
    var feature = event.feature;

    var content = "<h2>" + feature.attributes.name + "</h2>" + feature.attributes.description;
    if ( content.search( "<script" ) != -1 )
    {
        content = "Content contained Javascript! Escaped content below.<br />" + content.replace( /</g, "&lt;" );
    }
    popup = new OpenLayers.Popup.FramedCloud( "chicken",
        feature.geometry.getBounds().getCenterLonLat(),
        new OpenLayers.Size( 100, 100 ),
        content,
        null, true, onPopupClose );
    feature.popup = popup;
    mapWidget.getMap().addPopup( popup );
}

function onFeatureUnselect( event )
{
    var feature = event.feature;
    if ( feature.popup )
    {
        mapWidget.getMap().removePopup( feature.popup );
        feature.popup.destroy();
        delete feature.popup;
    }
}

function onPopupClose( evt )
{
    select.unselectAll();
}

function calculateMetersPerPixel()
{
    return (OpenLayers.INCHES_PER_UNIT[mapWidget.getMap().units] *
        mapWidget.getMap().getResolution() *
        OpenLayers.METERS_PER_INCH);
}

function setMapCtrTxt()
{

    var center = mapWidget.getMap().getCenter();
    $( "ddMapCtr" ).value = center.lat + ", " + center.lon;
    $( "dmsMapCtr" ).value = coordConvert.ddToDms( center.lat, center.lon );
    $( "point" ).value = coordConvert.ddToMgrs( center.lat, center.lon );
}

function setMapCtr( unit, value )
{
    if ( unit == "dd" )
    {

        if ( $( "ddMapCtr" ).value.match( ddRegExp ) )
        {
            var match = ddRegExp.exec( $( "ddMapCtr" ).value );
            var lat = match[1] + match[2];
            var lon = match[3] + match[4];
            var center = new OpenLayers.LonLat( lon, lat );

            mapWidget.getMap().setCenter( center, mapWidget.getMap().getZoom() );
        }
        else
        {
            alert( "Invalid DD Input." );
        }
    }
    else if ( unit == "dms" )
    {
        if ( $( "dmsMapCtr" ).value.match( dmsRegExp ) )
        {
            var match = dmsRegExp.exec( $( "dmsMapCtr" ).value );
            var lat = coordConvert.dmsToDd( match[1], match[2], match[3] + match[4], match[5] );
            var lon = coordConvert.dmsToDd( match[6], match[7], match[8] + match[9], match[10] );
            var center = new OpenLayers.LonLat( lon, lat );

            mapWidget.getMap().setCenter( center, mapWidget.getMap().getZoom() );
        }
        else
        {
            alert( "Invalid DMS Input." );
        }
    }
    else if ( unit == "mgrs" )
    {
        if ( $( "point" ).value.match( mgrsRegExp ) )
        {
            var match = mgrsRegExp.exec( $( "point" ).value );
            var mgrs = coordConvert.mgrsToDd( match[1], match[2], match[3], match[4], match[5], match[6] );
            var match2 = ddRegExp.exec( mgrs );
            var lat = match2[1] + match2[2];
            var lon = match2[3] + match2[4];
            var center = new OpenLayers.LonLat( lon, lat );

            mapWidget.getMap().setCenter( center, mapWidget.getMap().getZoom() );
        }
        else
        {
            alert( "Invalid MGRS Input." );
        }
    }

    // call this because the center is clamped so we need to reset the center on the
    // display just in case a user typed a number outside the bounds of the image
    setMapCtrTxt();
}

function mergeNewParams()
{
    obj = {
        interpolation: $( "interpolation" ).value,
        sharpen_mode: $( "sharpen_mode" ).value,
        stretch_mode: $( "stretch_mode" ).value,
        stretch_mode_region: $( "stretch_mode_region" ).value,
        quicklook: $( "quicklook" ).value,
        bands: $( "bands" ).value,
        brightness: brightnessContrastEditor.getBrightness(),
        contrast: brightnessContrastEditor.getContrast()
    };
    wcsParams.setProperties( obj );
    for ( var layer in rasterLayers )
    {
        rasterLayers[layer].mergeNewParams( obj );
    }
}

function chgInterpolation()
{
    var interpolation = $( "interpolation" ).value;
    obj = {interpolation: interpolation};
    wcsParams.setProperties( obj );

    for ( var layer in rasterLayers )
    {
        rasterLayers[layer].mergeNewParams( obj );
    }
}

function chgSharpenMode()
{
    var sharpen_mode = $( "sharpen_mode" ).value;
    obj = {sharpen_mode: sharpen_mode};
    wcsParams.setProperties( obj );

    for ( var layer in rasterLayers )
    {
        rasterLayers[layer].mergeNewParams( obj );
    }
}

function chgStretchMode()
{
    var stretch_mode = $( "stretch_mode" ).value;
    var stretch_mode_region = $( "stretch_mode_region" ).value;
    obj = {stretch_mode: stretch_mode, stretch_mode_region: stretch_mode_region};
    wcsParams.setProperties( obj );
    for ( var layer in rasterLayers )
    {
        rasterLayers[layer].mergeNewParams( obj );
    }
}

function chgQuickLookMode()
{
    obj = {quicklook: $( "quicklook" ).value};
    wcsParams.setProperties( obj );
    for ( var layer in rasterLayers )
    {
        rasterLayers[layer].mergeNewParams( obj );
    }
}

function changeBandsOpts()
{
    var bands = $( "bands" ).value;
    obj = {bands: bands};
    wcsParams.setProperties( obj );

    for ( var layer in rasterLayers )
    {
        rasterLayers[layer].mergeNewParams( obj );
    }
}

function setMouseMapCtrTxt( evt )
{
    var center = mapWidget.getMap().getLonLatFromViewPortPx( new OpenLayers.Pixel( evt.xy.x, evt.xy.y ) );
    var fixed = 6;

    if ( center )
    {
        var ddMouseCtr = document.getElementById( "ddMouseMapCtr" );
        ddMouseMapCtr.innerHTML = "DD: " + center.lat.toFixed( fixed ) + ", " + center.lon.toFixed( fixed );

        var dmsMouseCtr = document.getElementById( "dmsMouseMapCtr" );
        dmsMouseMapCtr.innerHTML = "DMS: " + coordConvert.ddToDms( center.lat, center.lon );

        var mgrsMouseCtr = document.getElementById( "mgrsMouseMapCtr" );
        mgrsMouseMapCtr.innerHTML = "MGRS: " + coordConvert.ddToMgrs( center.lat, center.lon );
    }
}

function measureUnitChanged( unit )
{
    if ( unit == 'kilometers' )
    {
        pathMeasurement.innerHTML = mapWidget.getPathUnit()[0];
    }
    else if ( unit == 'meters' )
    {
        pathMeasurement.innerHTML = mapWidget.getPathUnit()[1];
    }
    else if ( unit == 'feet' )
    {
        pathMeasurement.innerHTML = mapWidget.getPathUnit()[2];
    }
    else if ( unit == 'miles' )
    {
        pathMeasurement.innerHTML = mapWidget.getPathUnit()[3];
    }
    else if ( unit == 'yards' )
    {
        pathMeasurement.innerHTML = mapWidget.getPathUnit()[4];
    }
    else if ( unit == 'nautical miles' )
    {
        pathMeasurement.innerHTML = mapWidget.getPathUnit()[5];
    }

}


function clearPathMeasurement()
{
    pathMeasurement.innerHTML = "";
}

function clearPolygonMeasurement()
{
    polygonMeasurement.innerHTML = "";
}


function changeMapSize( mapWidth, mapHeight )
{
    if ( mapWidth && mapHeight )
    {
        var Dom = YAHOO.util.Dom;

        Dom.setStyle( "map", 'width', mapWidth );
        Dom.setStyle( "map", 'height', mapHeight );

        mapWidget.changeMapSize();
    }
}

function setImageId()
{
    var imageIdFieldEl = YAHOO.util.Dom.get( "imageIdField" );
    imageIdFieldEl.innerHTML = "<b>Image Id:</b> " + imageIds;

}

function getCapabilities()
{
    window.open( links.getCapabilities );
}

function getDetailedMetadata()
{
    window.open( links.detailedMetadata );
}

function getKML( layers )
{
    var wmsParamForm = $( 'wmsFormId' );
    var wmsUrlParams = new OmarWmsParams();

    var link = links.getMap;
    var extent = mapWidget.getSelectedOrViewportExtents();
    var size = mapWidget.getSizeInPixelsFromExtents( extent );

    var wmsProperties = {
        "request": "GetKML",
        "bbox": extent.toBBOX(),
        "layers": layers,
        "srs": "EPSG:4326",
        "width": size.w,
        "height": size.h
    };

    wmsUrlParams.setProperties( document );
    wmsUrlParams.setProperties( wmsProperties );

    var url = link + "?" + wmsUrlParams.toUrlParams();
    if ( wmsParamForm )
    {
        wmsParamForm.action = url;
//        alert(wmsParamForm.action);
        wmsParamForm.submit();
    }

    /*
     wmsParamForm.request.value = "GetKML";
     wmsParamForm.layers.value = layers;
     var extent = mapWidget.getMap().getExtent();
     wmsParamForm.bbox.value = extent.toBBOX();

     wmsParamForm.sharpen_mode.value = $("sharpen_mode").value;
     wmsParamForm.stretch_mode.value = $("stretch_mode").value;
     wmsParamForm.stretch_mode_region.value = $("stretch_mode_region").value;
     var numberOfBands = parseInt("${rasterEntries?.numberOfBands.get( 0 )}");
     if(numberOfBands > 1)
     {
     wmsParamForm.bands.value = $("bands").value;              /////// bands doesn't appear to working on kml output ///////
     }
     wmsParamForm.quicklook.value = $("quicklook").value;

     wmsParamForm.submit();
     */
}


function getWmsLog()
{
    window.open( links.wmsLog );
}


function getProjectedImage( params )
{
    var link = wcsGetCoverage;
    var extent = mapWidget.getSelectedExtents();

    if ( extent && extent.left )
    {
        viewportExtents = mapWidget.getViewportExtents();

        if ( !viewportExtents.containsBounds( extent ) )
        {
            alert( "Selected extents exceeds the viewport extents.  The AOI will be cleared, please re-select the region to save." );
            mapWidget.clearAOI();
            return;
        }
    }
    else
    {
        extent = mapWidget.getViewportExtents();
    }
    var size = mapWidget.getSizeInPixelsFromExtents( extent );
    var wcsProperties = {
        "request": "GetCoverage",
        "format": params.format,
        "bbox": extent.toBBOX(),
        "coverage": params.coverage,
        "crs": "EPSG:4326",
        "width": size.w,
        "height": size.h
    };
    wcsParams.setProperties( wcsProperties );

    var form = $( "wcsForm" );
    var url = link + "?" + wcsParams.toUrlParams();
    if ( form )
    {
        form.action = url;
        form.submit();
    }
}

function getLocalKmz( params )
{
    var wmsParamForm = $( 'wmsFormId' );
    var wmsUrlParams = new OmarWmsParams();

    var link = links.getMap;
    var extent = mapWidget.getSelectedOrViewportExtents();
    var size = mapWidget.getSizeInPixelsFromExtents( extent );

    var wmsProperties = {
        "request": "GetKMZ",
        "bbox": extent.toBBOX(),
        "srs": "EPSG:4326",
        "width": size.w,
        "height": size.h
    };

    wmsUrlParams.request = "";
    wmsUrlParams.setProperties( document );
    wmsUrlParams.setProperties( params );
    wmsUrlParams.setProperties( wmsProperties );

    var url = link + "?" + wmsUrlParams.toUrlParams();
    if ( wmsParamForm )
    {
        wmsParamForm.action = url;
        //alert(url);
        wmsParamForm.submit();
    }
}

function shareImage()
{
    var baseURL = links.shareImage;

    var layer = mapWidget.getMap().layers[0];
    var params = layer.params;
    var layers = params["LAYERS"];
    var interpolation = params["INTERPOLATION"];
    var brightness = params["BRIGHTNESS"];
    var contrast = params["CONTRAST"];
    var sharpen_mode = params["SHARPEN_MODE"];
    var stretch_mode = params["STRETCH_MODE"];
    var stretch_mode_region = params["STRETCH_MODE_REGION"];
    var bands = params["BANDS"];
    var centerLatitude = mapWidget.getMap().getCenter().lat;
    var centerLongitude = mapWidget.getMap().getCenter().lon;

    var shareLink = baseURL + "?" +
        "layers=" + layers +
        "&interpolation=" + interpolation +
        "&brightness=" + brightness +
        "&contrast=" + contrast +
        "&sharpen_mode=" + sharpen_mode +
        "&stretch_mode=" + stretch_mode +
        "&stretch_mode_region=" + stretch_mode_region +
        "&bands=" + bands +
        "&latitude=" + mapWidget.getMap().getCenter().lat +
        "&longitude=" + mapWidget.getMap().getCenter().lon +
        "&bbox=" + mapWidget.getMap().getExtent();
    window.prompt( "Copy to clipboard: Ctrl+C, Enter", shareLink );

//var popUpWindow = window.open("", "OMARImageShare", "width=400, height=50");
//	popUpWindow.document.write("Copy and paste this <a href='" + shareLink + "' target='_new'>link</a> to share the image!");
//    popUpWindow.document.close();
}

function init( mapWidth, mapHeight )
{

    brightnessContrastEditor = new BrightnessContrastEditor( params );


    setImageId();
    OpenLayers.ImgPath = links.openLayersImgPath;


    var oMenu = new YAHOO.widget.MenuBar( "rasterMenu", {
        autosubmenudisplay: true,
        hidedelay: 750,
        showdelay: 0,
        lazyload: true,
        zIndex: 9999} );
    oMenu.render();
// we need to pass a json string or object and save to the session
// and reload here
    wcsParams.setProperties( {
        brightness: params.brightness || 0,
        contrast: params.contrast || 1.0,
        sharpen_mode: params.sharpen_mode || 'none',
        stretch_mode: params.stretch_mode || 'linear_auto_min_max',
        stretch_mode_region: params.stretch_mode_region || 'global',
        interpolation: params.interpolation || 'bilinear',
        srs: params.srs || 'EPSG:4326',
        crs: params.crs || 'EPSG:4326',
        bands: $( "bands" ).value,
        quicklook: params.quicklook || 'false'
    } );


    //console.log(wcsParams);

    wcsParams.setProperties( {bands: "default"} );
    // if(${rasterEntries?.numberOfBands.get( 0 ) >= 3})
    // {
    //     wcsParams.setProperties({bands:"0,1,2"});
    // }
    // else
    // {
    //     wcsParams.setProperties({bands:"0"});
    // }
    var bounds = new OpenLayers.Bounds( minLon, minLat, maxLon, maxLat );
    mapWidget = new MapWidget();
    mapWidget.setupMapWidgetWithOptions( "map", {controls: [], maxExtent: bounds, theme: null, maxResolution: largestScale, minResolution: smallestScale} );
    mapWidget.setFullResScale( fullResScale );

    changeMapSize( mapWidth, mapHeight );
    setupLayers();

    mapWidget.setupAoiLayer();
    mapWidget.setupToolBar();

    mapWidget.getMap().addControl( new OpenLayers.Control.LayerSwitcher() );
    var overview = new OpenLayers.Control.OverviewMap( {maximized: true} );
    //mapWidget.getMap().addControl(overview);
    mapWidget.getMap().addControl( new OpenLayers.Control.Scale() );
    mapWidget.getMap().addControl( new OpenLayers.Control.ScaleLine() );

    mapWidget.getMap().events.register( 'mousemove', map, setMouseMapCtrTxt );
    mapWidget.getMap().events.register( "mouseup", map, this.setMapCtrTxt );

    var viewParam = params.view || null;
    if ( viewParam )
    {
        viewParam = YAHOO.lang.JSON.parse( viewParam );
        console.log( viewParam );
        var mapCenter = new OpenLayers.LonLat( viewParam.lon, viewParam.lat );
        var normScale = OpenLayers.Util.normalizeScale( viewParam.mpp );
        resolution = (1.0 / OpenLayers.INCHES_PER_UNIT["degrees"]) *
            (OpenLayers.INCHES_PER_UNIT["m"] * viewParam.mpp);
        mapWidget.getMap().setCenter( mapCenter, mapWidget.getMap().getZoomForResolution( resolution ) );
    }
    else
    {
        var mapBbox = new OpenLayers.Bounds( bounds.left, bounds.bottom, bounds.right, bounds.top );
        var zoomBbox = mapWidget.getMap().getZoomForExtent( mapBbox, true );
        var zoomMax = mapWidget.getMap().getZoomForExtent( bounds, true );

        var zoom;
        if ( zoomBbox < zoomMax )
        {
            zoom = zoomMax;
        }
        else
        {
            zoom = zoomBbox
        }

        zoom--;

        var mapCenterLatitude = params.latitude || bounds.getCenterLonLat().lat;
        var mapCenterLongitude = params.longitude || bounds.getCenterLonLat().lon;
        var mapCenter = new OpenLayers.LonLat( mapCenterLongitude, mapCenterLatitude );

        mapWidget.getMap().setCenter( mapCenter, zoom );
    }

    setMapCtrTxt();
    setupOverviewCheck();
    var target = $( 'map' );
    initBands();


}

function changeToImageSpace( azimuth )
{
    var rotation = azimuth;
    var url = links.imageSpace;
    var wmsFormElement = $( "wmsFormId" );
    if ( wmsFormElement )
    {
        if ( !azimuth )
        {
            rotation = 0.0;
        }
        var mpp = calculateMetersPerPixel();
        wmsParams = new OmarWmsParams();
        wmsParams.setProperties( wcsParams );
        wmsParams.layers = rasterEntries.indexIds;
        // need to calculate an azimuth if we go to other projectors
        // for now just hard code to 0.0
        //  if we ever to UTM grids we need to modify this
        wmsParams.view = YAHOO.lang.JSON.stringify( {lat: mapWidget.getMap().getCenter().lat,
            lon: mapWidget.getMap().getCenter().lon,
            mpp: mpp,
            azimuth: rotation} );
        wmsFormElement.action = url + "?" + wmsParams.toUrlParams();
        wmsFormElement.method = "POST";
        wmsFormElement.submit();
    }


}

function checkOverview()
{
    var url = "/omar/rasterEntryExport/export";

    var request = OpenLayers.Request.POST( {
        url: url + "?id=" + rasterEntries.ids + "&max=10&format=json&fields=numberOfResLevels&labels=rlevels&_=" + new Date().getTime(),
        callback: function ( transport )
        {
            var temp = YAHOO.lang.JSON.parse( transport.responseText );
            if ( temp && temp.length > 0 )
            {
                var ok = true;
                var idx = 0;
                for ( idx = 0; idx < temp.length; ++idx )
                {
                    if ( parseInt( temp[idx].rlevels ) < 2 )
                    {
                        ok = false
                    }
                }
                if ( ok )
                {
                    var label = YAHOO.util.Dom.get( "busyCursorLabel" );
                    if ( label )
                    {
                        YAHOO.util.Dom.setStyle( "busyCursorImage", "display", "none" );
                        YAHOO.util.Dom.setStyle( "busyCursorLabel", "text-decoration", "underline" );
                        label.innerHTML = "Click now to refresh";
                        label.onclick = function ()
                        {
                            window.location = window.location
                        };
                    }
                }
                else
                {
                    setTimeout( checkOverview, 5000 );
                }
            } // endif
        } // callback
    } );
}

function setupLayers()
{
    var format = "image/jpeg";
    //var format = "image/png";
    //var format = "image/gif";
    var transparent = false;

    var sharpen_mode = $( "sharpen_mode" ).value;
    var stretch_mode = $( "stretch_mode" ).value;
    var stretch_mode_region = $( "stretch_mode_region" ).value;

    console.log( 'before' );

    rasterLayers = [
        new OpenLayers.Layer.WMS( "Raster", links.getMap,
            {layers: rasterEntries.indexIds,
                format: format, sharpen_mode: sharpen_mode,
                stretch_mode: stretch_mode, stretch_mode_region: stretch_mode_region, transparent: transparent,
                brightness: brightnessContrastEditor.getBrightness(),
                contrast: brightnessContrastEditor.getContrast(),
                bands: $( "bands" ).value,
                interpolation: $( "interpolation" ).value
            },
            {
                isBaseLayer: true, buffer: 0,
                singleTile: true, ratio: 1.0,
                quicklook: true,
                transitionEffect: "resize",
                displayOutsideMaxExtent: false,
                eventListeners: {
                    loadstart: busyIndicator.loadStart,
                    loadend: busyIndicator.loadEnd,
                    scope: busyIndicator
                }
            } )];

    console.log( 'after' );

    mapWidget.getMap().addLayers( rasterLayers );

    if ( !kmlLayers )
    {
        kmlLayers = [];
    }

    for ( var i = 0; i < kmlOverlays.length; i++ )
    {

        kmlLayer = new OpenLayers.Layer.Vector( kmlOverlays[i].name, {
            visibility: defaultOverlayVisiblity,
            projection: mapWidget.getMap().displayProjection,
            strategies: [new OpenLayers.Strategy.Fixed()],
            protocol: new OpenLayers.Protocol.HTTP( {
                url: kmlOverlays[i].url,
                format: new OpenLayers.Format.KML( {
                    extractStyles: true,
                    extractAttributes: true} )} )
        } );

        kmlLayers.push( kmlLayer );
        kmlLayer.events.on( {
            "featureselected": onFeatureSelect,
            "featureunselected": onFeatureUnselect
        } );

        mapWidget.getMap().addLayers( kmlLayers );
        select = new OpenLayers.Control.SelectFeature( kmlLayers );
        mapWidget.getMap().addControl( select );
        select.activate();
    }
}

function getKmlSuperOverlay()
{
    var wmsParamForm = $( 'wmsFormId' );
    var imageAdjustementParams = new OmarImageAdjustmentParams();

    var link = links.superOverlay;
    imageAdjustementParams.setProperties( document );

    var url = link + "?id=" + rasterEntries.indexIds + "&" + imageAdjustementParams.toUrlParams();
    if ( wmsParamForm )
    {
        wmsParamForm.action = url;
        wmsParamForm.submit();
    }
}

function exportTemplate()
{
    var centerLatitude = mapWidget.getMap().getCenter().lat;
    var centerLongitude = mapWidget.getMap().getCenter().lon;
    var dms = coordConvert.ddToDms( centerLatitude, centerLongitude );
    var mgrs = coordConvert.ddToMgrs( centerLatitude, centerLongitude );
    var centerGeo = "GEO: " + dms + " MGRS: " + mgrs;

    var acquisitionDate = rasterEntries.acquisitionDates;
    var countryCode = rasterEntries.countryCodes;
    var imageId = rasterEntries.titles;

    var baseURL = links.getMapAbsolute;
    var extent = mapWidget.getViewportExtents();
    var size = mapWidget.getSizeInPixelsFromExtents( extent );

    var layer = mapWidget.getMap().layers[0];
    var params = layer.params;

    var wmsProperties =
    {
        "bands": params["BANDS"],
        "bbox": extent.toBBOX(),
        "brightness": params["BRIGHTNESS"],
        "contrast": params["CONTRAST"],
        "layers": rasterEntries.indexIds,
        "srs": "EPSG:4326",
        "format": "image/png",
        "height": size.h,
        "interpolation": params["INTERPOLATION"],
        "request": "GetMap",
        "sharpen_mode": params["SHARPEN_MODE"],
        "stretch_mode": params["STRETCH_MODE"],
        "stretch_mode_region": params["STRETCH_MODE_REGION"],
        "width": size.w
    };

    templateParams = new OmarWmsParams();
    templateParams.setProperties( wmsProperties );
    var imageUrl = baseURL + "?" + templateParams.toUrlParams();
    imageUrl = imageUrl.replace( /&/g, "%26" );

    $( "countryCodeFormInput" ).value = countryCode;
    $( "footerAcquisitionDateTextFormInput" ).value = acquisitionDate;
    $( "footerLocationTextFormInput" ).value = centerGeo;
    $( "footerSecurityClassificationTextFormInput" ).value = "UNCLASS";
    $( "headerDescriptionTextFormInput" ).value = "Country: " + countryCode;
    $( "headerSecurityClassificationTextFormInput" ).value = "";
    $( "headerTitleTextFormInput" ).value = imageId;
    $( "imageUrlFormInput" ).value = imageUrl;
    $( "northAngleFormInput" ).value = 0;

    $( "exportForm" ).submit();
}




