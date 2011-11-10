<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: Dec 9, 2008
  Time: 1:25:21 PM
  To change this template use File | Settings | File Templates.
--%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"/>
  <title>Full Page Layout - Example</title>
  <style type="text/css">
    /*margin and padding on body element
  can introduce errors in determining
  element position and are not recommended;
  we turn them off as a foundation for YUI
  CSS treatments. */
  body {
    margin: 0;
    padding: 0;
  }

  #toggle {
    text-align: center;
    padding: 1em;
  }

  #toggle a {
    padding: 0 5px;
    border-left: 1px solid black;
  }

  #tRight {
    border-left: none !important;
  }
  </style>

  <link rel="stylesheet" type="text/css"
        href="${resource(plugin: 'yui', dir: 'js/yui/reset-fonts-grids', file: 'reset-fonts-grids.css')}"/>
  <link rel="stylesheet" type="text/css"
        href="${resource(plugin: 'yui', dir: 'js/yui/resize/assets/skins/sam', file: 'resize.css')}"/>
  <link rel="stylesheet" type="text/css"
        href="${resource(plugin: 'yui', dir: 'js/yui/layout/assets/skins/sam', file: 'layout.css')}"/>
  <link rel="stylesheet" type="text/css"
        href="${resource(plugin: 'yui', dir: 'js/yui/button/assets/skins/sam', file: 'button.css')}"/>

  <g:javascript plugin="yui" src="yui/yahoo/yahoo-min.js"/>
  <g:javascript plugin="yui" src="yui/event/event-min.js"/>
  <g:javascript plugin="yui" src="yui/dom/dom-min.js"/>
  <g:javascript plugin="yui" src="yui/element/element-min.js"/>
  <g:javascript plugin="yui" src="yui/dragdrop/dragdrop-min.js"/>
  <g:javascript plugin="yui" src="yui/resize/resize-min.js"/>
  <g:javascript plugin="yui" src="yui/animation/animation-min.js"/>
  <g:javascript plugin="yui" src="yui/layout/layout-min.js"/>


  <openlayers:loadTheme theme="default"/>
  <openlayers:loadJavascript/>
  <style type="text/css">
    /**
     * Map Examples Specific
     */
  .smallmap {
    border: 1px solid #ccc;
  }

  </style>

</head>

<body class="yui-skin-sam">
<div id="top1">
  <div id="panel2" class="olControlPanel"></div>
</div>

<div id="bottom1">
</div>

<div id="right1">
</div>

<div id="left1">
</div>

<div id="center1">
  <div id="map" class="smallmap"></div>
</div>


<script>

  (function()
  {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event;

    Event.onDOMReady( function()
    {
      var map = new OpenLayers.Map( 'map' );

      //map.addLayer(aoiLayer);
      map.setBaseLayer( baseLayer );
      map.addControl( new OpenLayers.Control.LayerSwitcher() );
      map.addControl( new OpenLayers.Control.PanZoom() );
      map.addControl( new OpenLayers.Control.NavToolbar() );
      map.addControl( new OpenLayers.Control.MousePosition() );
      map.addControl( new OpenLayers.Control.Scale() );
      map.addControl( new OpenLayers.Control.Permalink( "permalink" ) );
      map.addControl( new OpenLayers.Control.ScaleLine() );
      map.addControl( new OpenLayers.Control.Attribution() );

      var dataLayer = new OpenLayers.Layer.WMS(
          "${dataWMS.title}",
          "${dataWMS.url}",
          { layers: "${dataWMS.layers}", format: 'image/png', transparent: true },
          {'isBaseLayer': false},
          {buffer:1}
      );

      map.addLayer( dataLayer );

      var baseLayer = new OpenLayers.Layer.WMS(
          "${baseWMS.title}",
          "${baseWMS.url}",
          {layers: '${baseWMS.layers}', format: 'image/jpeg' },
          {buffer:1}
      );

      map.addLayer( baseLayer );

      var layout = new YAHOO.widget.Layout( {
        units: [
          {
            position: 'top',
            height: 50,
            body: 'top1',
            header: 'Top',
            gutter: '5px',
            collapse: true,
            resize: true
          },
          {
            position: 'right',
            header: 'Right',
            width: 300,
            resize: true,
            gutter: '5px',
            collapse: true,
            scroll: true,
            body: 'right1',
            animate: true
          },
          {
            position: 'bottom',
            header: 'Bottom',
            height: 100,
            resize: true,
            body: 'bottom1',
            gutter: '5px',
            collapse: true
          },
          {
            position: 'left',
            header: 'Left',
            width: 200,
            resize: true,
            body: 'left1',
            gutter: '5px',
            collapse: true,
            collapseSize: 50,
            scroll: true,
            animate: true
          },
          {
            position: 'center',
            body: 'center1'
          }
        ]
      } );

      layout.subscribe( "resize", function( ev )
      {
        var c = this.getUnitByPosition( 'center' );
        var mapWidth = c.get( 'width' );
        var mapHeight = Math.round( mapWidth / 2 );

        Dom.get( "map" ).style.width = mapWidth + "px";
        Dom.get( "map" ).style.height = mapHeight + "px";

        map.updateSize();


      } );


      layout.render();
      map.zoomToMaxExtent();

    } );
  })();

  function foo()
  {
    var centerDiv = this.getUnitByPosition( 'center' );

    alert( centerDiv.get( 'width' ) );
  }
</script>
</body>
</html>
