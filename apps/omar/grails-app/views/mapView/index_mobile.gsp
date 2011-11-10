<html>
<head>
  <meta content="yes" name="apple-mobile-web-app-capable"/>
  <meta content="minimum-scale=1.0, width=device-width, user-scalable=no" name="viewport"/>
  <link rel="stylesheet" href="${resource(plugin: 'omar', dir: 'css', file: 'main.css')}"/>
  <title>OMAR: Ground Space Viewer</title>

  <style type="text/css">

  #map {
    width: 100%;
    height: 100%;
    border: 1px solid black;
  }
  </style>

  <link rel="stylesheet" type="text/css"
        href="${resource(plugin: 'yui', dir: 'js/yui/fonts/assets/skins/sam', file: 'fonts-min.css')}"/>
  <link rel="stylesheet" type="text/css"
        href="${resource(plugin: 'yui', dir: 'js/yui/button/assets/skins/sam', file: 'button.css')}"/>
  <link rel="stylesheet" type="text/css"
        href="${resource(plugin: 'yui', dir: 'js/yui/container/assets/skins/sam', file: 'container.css')}"/>

</head>

<body onload="init()" class="yui-skin-sam">

<div class="nav">
  <span class="menuButton"><g:link class="home" uri="/">OMAR™ Home</g:link></span>
  <span class="menuButton"><g:link url="javascript:zoomIn();">Zoom In</g:link></span>
  <span class="menuButton"><g:link url="javascript:zoomOut();">Zoom Out</g:link></span>
  <span class="menuButton"><g:link url="javascript:zoomMaxExtent();">Max Extent</g:link></span>
  <span class="menuButton"><g:link url="javascript:zoomFullRes();">Full Res</g:link></span>
  <span class="menuButton"><button id="show">Image Adjustment</button></span>
</div>

<div class="body">
  <div id="map"></div>
</div>

<div id="dialog1" class="yui-pe-content">
  <div class="hd">Image Adjustments:</div>

  <div class="bd">
    <g:form name="wmsParams" method="POST" url="[action:'wms',controller:'ogc']">
      <input type="hidden" name="request" value=""/>
      <input type="hidden" name="layers" value=""/>
      <input type="hidden" name="bbox" value=""/>

      <b>Image Sharpen:</b>

      <p>
        <g:select id="sharpen_mode" name="sharpen_mode" from="${['none', 'light', 'heavy']}"
                  onChange="changeSharpenOpts()"/></p>

      <p>&nbsp;</p>

      <b>Histogram Stretch:</b>

      <p><g:select id="stretch_mode" name="stretch_mode"
                   from="${['linear_auto_min_max', 'linear_1std_from_mean', 'linear_2std_from_mean', 'linear_3std_from_mean', 'none']}"
                   onChange="changeHistoOpts()"/></p>

      <p><b>Region Stretch:</b></p>

      <p><g:select id="stretch_mode_region" name="stretch_mode_region" from="${['global', 'viewport']}"
                   onChange="changeHistoOpts() "/></p>

      <p>&nbsp;</p>

      <g:if test="${rasterEntries?.numberOfBands.get(0) == 2}">
        <b>Band Combinations:</b>

        <p><g:select id="bands" name="bands" from="${['0,1','1,0','0','1']}" onChange="changeBandsOpts()"/></p>
      </g:if>

      <g:if test="${rasterEntries?.numberOfBands.get(0) >= 3}">
        <b>Band Combinations:</b>

        <p><g:select id="bands" name="bands" from="${['0,1,2','2,1,0','0','1','2']}" onChange="changeBandsOpts()"/></p>
      </g:if>

      <p>&nbsp;</p>

      <b>Terrain Correction:</b>

      <p>
        <g:select id="quicklook" name="quicklook"
                  from="${[[name: 'On', value: 'true'], [name: 'Off', value: 'false']]}"
                  optionValue="name" optionKey="value"
                  onChange="changeQuickLookOpts()"/>
      </p>
    </g:form>
  </div>
</div>

</body>

<g:javascript plugin='yui' src="yui/yahoo-dom-event/yahoo-dom-event.js"/>
<g:javascript plugin='yui' src="yui/element/element-min.js"/>
<g:javascript plugin='yui' src="yui/container/container-min.js"/>

<script>
  YAHOO.namespace( "example.container" );

  YAHOO.util.Event.onDOMReady( function ()
  {

    var handleCancel = function()
    {
      this.cancel();
    };

    YAHOO.example.container.dialog1 = new YAHOO.widget.Dialog( "dialog1",
        { width : "30em",
          fixedcenter : true,
          visible : false,
          zIndex: 99999,
          constraintoviewport : true,
          buttons : [
            { text:"Close", handler:handleCancel, isDefault:true }
          ]
        } );

    YAHOO.example.container.dialog1.render();

    YAHOO.util.Event.addListener( "show", "click", YAHOO.example.container.dialog1.show, YAHOO.example.container.dialog1, true );
    YAHOO.util.Event.addListener( "hide", "click", YAHOO.example.container.dialog1.hide, YAHOO.example.container.dialog1, true );
  } );
</script>


<script type='text/javascript' src='${omar.bundle(contentType: "text/javascript", files: [
    resource(plugin: "openlayers", dir: "js", file: "OpenLayers.js"),
    resource(plugin: 'omar-core', dir: 'js', file: 'jquery.js'),
    resource(plugin: 'omar-core', dir: 'js', file: 'MultitouchHandler.js'),
    resource(plugin: 'omar-core', dir: 'js', file: 'MultitouchNavigation.js')
])}'></script>

<script type="text/javascript">
  var map = null;
  var rasterLayers;

  function init()
  {
    var left = parseFloat( "${left}" );
    var bottom = parseFloat( "${bottom}" );
    var right = parseFloat( "${right}" );
    var top = parseFloat( "${top}" );
    var fullResScale = parseFloat( "${fullResScale}" );
    var smallestScale = parseFloat( "${smallestScale}" );
    var largestScale = parseFloat( "${largestScale}" );

    var bounds = new OpenLayers.Bounds( left, bottom, right, top );

    map = new OpenLayers.Map( "map", {controls: [],
      maxExtent:bounds,
      maxResolution:largestScale,
      minResolution:smallestScale} );

    setupLayers();

    var zoom = map.getZoomForExtent( bounds, true );

    map.setCenter( bounds.getCenterLonLat(), zoom );

    var touchControl = new OpenLayers.Control.MultitouchNavigation();
    map.addControl( touchControl );
  }

  function setupLayers()
  {
    var format = "image/jpeg";
    var transparent = false;

    var stretch_mode = $( "stretch_mode" ).value;
    var stretch_mode_region = $( "stretch_mode_region" ).value;
    var sharpen_mode = $( "sharpen_mode" ).value;

    rasterLayers = [
      new OpenLayers.Layer.WMS( "Raster", "${createLink(controller: 'ogc', action: 'wms')}",
          { layers: "${(rasterEntries*.indexId).join(',')}", format: format, sharpen_mode:sharpen_mode, stretch_mode:stretch_mode, stretch_mode_region: stretch_mode_region, transparent:transparent  },
          {isBaseLayer: true, buffer:0, singleTile:false, ratio:1.0, quicklook:false,
            displayOutsideMaxExtent:false} )
    ];
    map.addLayers( rasterLayers );
  }

  function changeQuickLookOpts()
  {
    var quicklook = document.getElementById( 'quicklook' ).value;
    for ( var layer in rasterLayers )
    {
      rasterLayers[layer].mergeNewParams( {quicklook:quicklook} );
    }
  }

  function changeHistoOpts()
  {
    var stretch_mode = document.getElementById( 'stretch_mode' ).value;
    var stretch_mode_region = document.getElementById( 'stretch_mode_region' ).value;
    for ( var layer in rasterLayers )
    {
      rasterLayers[layer].mergeNewParams( {stretch_mode:stretch_mode, stretch_mode_region: stretch_mode_region} );
    }
  }

  function changeSharpenOpts()
  {
    var sharpen_mode = document.getElementById( 'sharpen_mode' ).value;
    for ( var layer in rasterLayers )
    {
      rasterLayers[layer].mergeNewParams( {sharpen_mode:sharpen_mode} );
    }
  }

  function changeBandsOpts()
  {
    var bands = document.getElementById( 'bands' ).value;
    for ( var layer in rasterLayers )
    {
      rasterLayers[layer].mergeNewParams( {bands:bands} );
    }
  }

  function zoomIn()
  {
    map.zoomIn();
  }

  function zoomOut()
  {
    map.zoomOut();
  }

  function zoomMaxExtent()
  {
    map.zoomToMaxExtent();
  }

  function zoomFullRes()
  {
    var zoom = map.getZoomForResolution( parseFloat( "${fullResScale}" ), true );
    map.zoomTo( zoom );
  }
</script>

</html>