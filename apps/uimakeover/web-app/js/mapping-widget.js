function MappingWidget( centerLat, centerLon, zoomLevel, baseLayers, overlayLayers )
{
  this.lon = centerLon;
  this.lat = centerLat;
  this.zoom = zoomLevel;
  this.baseLayers = baseLayers;
  this.overlayLayers = overlayLayers;

  this.map = null;
  this.aoiLayer = null;

  this.setView = function( e )
  {
    //var bounds = this.map.getExtent( );

    /*
     $("viewMinLon").value = bounds.left;
     $("viewMaxLat").value = bounds.top;
     $("viewMaxLon").value = bounds.right;
     $("viewMinLat").value = bounds.bottom;
     */
  };

  this.setCenterText = function()
  {
    //var center = this.map.getCenter( );

    /*
     $("centerLon").value = center.lon;
     $("centerLat").value = center.lat;
     */
  };

  this.clearAOI = function()
  {
    this.aoiLayer.destroyFeatures( );

    // HACK - Need a better way to this
    /*
     $("aoiMinLon").value = ""
     $("aoiMaxLat").value = ""
     $("aoiMaxLon").value = ""
     $("aoiMinLat").value = ""
     */
  };

  this.setAOI = function( e )
  {
    var geom = e.feature.geometry;
    var bounds = geom.getBounds( );
    var feature = new OpenLayers.Feature.Vector( geom );

    // HACK - Need a better way to this
    /*
     $("aoiMinLon").value = bounds.left
     $("aoiMaxLat").value = bounds.top
     $("aoiMaxLon").value = bounds.right
     $("aoiMinLat").value = bounds.bottom
     */

    e.object.destroyFeatures( );
    e.object.addFeatures( feature, {silent: true} );
  };

  this.zoomIn = function()
  {
    this.map.zoomIn( );
  };

  this.zoomOut = function()
  {
    this.map.zoomOut( );
  };


  this.setupMapWidget = function()
  {
    this.map = new OpenLayers.Map( 'map', {controls: []} );
    this.map.addControl( new OpenLayers.Control.LayerSwitcher( ) );
    this.map.addControl( new OpenLayers.Control.PanZoom( ) );
    this.map.addControl( new OpenLayers.Control.MousePosition( ) );
    this.map.addControl( new OpenLayers.Control.Scale( ) );
    this.map.addControl( new OpenLayers.Control.ScaleLine( ) );

    this.map.events.register( "moveend", this.map, this.setCenterText );
    this.map.events.register( "zoomend", this.map, this.setView );
  };


  this.setupBaseLayers = function()
  {
    for ( var it in this.baseLayers )
    {
      this.map.addLayer( new OpenLayers.Layer.WMS(
          this.baseLayers[it].title, this.baseLayers[it].url,
          {layers:  this.baseLayers[it].name, format: 'image/jpg' },
          {'isBaseLayer': true}, {buffer:0} ) );
    }
  };

  this.setupOverlayLayers = function()
  {
    for ( var it in this.overlayLayers )
    {
      this.map.addLayer( new OpenLayers.Layer.WMS(
          this.overlayLayers[it].title, this.overlayLayers[it].url,
          {layers: this.overlayLayers[it].name, format: 'image/png', transparent: true },
          {'isBaseLayer': false}, {buffer:0} ) );
    }
  };

  this.setupAreaOfInterestLayer = function ()
  {
    this.aoiLayer = new OpenLayers.Layer.Vector( "Area of Interest" );
    this.aoiLayer.events.register( "featureadded", this.aoiLayer, this.setAOI );
    this.map.addLayer( this.aoiLayer );

    /*
     var aoiMinLon = "${queryParams?.aoiMinLon ?: 0}";
     var aoiMinLat = "${queryParams?.aoiMinLat ?: 0}";
     var aoiMaxLon = "${queryParams?.aoiMaxLon ?: 0}";
     var aoiMaxLat = "${queryParams?.aoiMaxLat ?: 0}";

     if ( aoiMinLon && aoiMinLat && aoiMaxLon && aoiMaxLat )
     {
     var bounds = new OpenLayers.Bounds( aoiMinLon, aoiMinLat, aoiMaxLon, aoiMaxLat );
     var feature = new OpenLayers.Feature.Vector( bounds.toGeometry( ) );

     aoiLayer.addFeatures( feature, {silent: true} );
     }
     */
  };

  this.setupToolbar = function ()
  {
    var controls = [];
    var defaultControl;

    // Drag Pan
    controls.push( new OpenLayers.Control.MouseDefaults( {title:'Drag to recenter map'} ) );

    // Zoom Box
    var zoomBox = new OpenLayers.Control.ZoomBox( {title:"Zoom into an area by clicking and dragging"} );

    controls.push( zoomBox );
    defaultControl = zoomBox;

    // Zoon In
    var zoomInButton = new OpenLayers.Control.Button( {title:'Zoom in', displayClass: "olControlZoomIn",
      trigger: this.zoomIn } );

    controls.push( zoomInButton );

    // Zoom Out
    var zoomOutButton = new OpenLayers.Control.Button( {title:'Zoom out', displayClass: "olControlZoomOut",
      trigger: this.zoomOut } );

    controls.push( zoomOutButton );

    // Navigation History
    var nav = new OpenLayers.Control.NavigationHistory( {
      nextOptions: {title: "Next View"},
      previousOptions: {title: "Previous View"}
    } );

    this.map.addControl( nav );
    controls.push( nav.next );
    controls.push( nav.previous );

    // Zoom to Max Extent
    controls.push( new OpenLayers.Control.ZoomToMaxExtent( {title:"Zoom to the max extent"} ) );

    // Area of Interest
    if ( this.aoiLayer )
    {
      var polyOptions = {sides: 4, irregular: true};

      var polygonControl = new OpenLayers.Control.DrawFeature( this.aoiLayer,
          OpenLayers.Handler.RegularPolygon, {handlerOptions: polyOptions, title: "Specify Area of Interest"} );

      var button1 = new OpenLayers.Control.Button( {title:'Clear Area of Interest',
        displayClass: "olControlClearAreaOfInterest",
        trigger:  this.clearAOI
      } );

      button1.aoiLayer = this.aoiLayer;
      controls.push( polygonControl );
      controls.push( button1 );
    }

    // Measure Distance Button
    var measureDistanceButton = new OpenLayers.Control.Measure( OpenLayers.Handler.Path, {
      title: "Measure Distance",
      displayClass: "olControlMeasureDistance",
      eventListeners:
      {
        measure: function( evt )
        {
          alert( "Distance: " + evt.measure.toFixed( 2 ) + evt.units );
        }
      }
    } );

    controls.push( measureDistanceButton );

    // Measure Area Button
    var measureAreaButton = new OpenLayers.Control.Measure( OpenLayers.Handler.Polygon, {
      title: "Measure Area",
      displayClass: "olControlMeasureArea",
      eventListeners:
      {
        measure: function( evt )
        {
          alert( "Area: " + evt.measure.toFixed( 2 ) + evt.units );
        }
      }
    } );

    controls.push( measureAreaButton );

    //
    // Setup the container for the toolbar
    var container = $( "panel2" );

    var panel = new OpenLayers.Control.Panel( {div: container, defaultControl: defaultControl,
      'displayClass': 'olControlPanel'} );

    panel.addControls( controls );
    this.map.addControl( panel );
  };

  this.setupView = function()
  {
    this.map.setCenter( new OpenLayers.LonLat( this.lon, this.lat ), this.zoom );
  };

  this.updateSize = function()
  {
    this.map.updateSize( );
  };

  this.setupMapWidget( );
  this.setupBaseLayers( );
  this.setupOverlayLayers( );
  this.setupAreaOfInterestLayer( );
  this.setupToolbar( );
  this.setupView( );
}