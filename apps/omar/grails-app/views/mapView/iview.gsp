<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: Apr 20, 2010
  Time: 2:06:30 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>Simple GSP page</title>

  <link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css')}"/>
  <link rel="stylesheet" href="${resource(dir: 'css', file: 'omar-2.0.css')}"/>

  <link rel="stylesheet" type="text/css" href="${resource(plugin: 'richui', dir: 'js/yui/reset-fonts-grids', file: 'reset-fonts-grids.css')}"/>
  <link rel="stylesheet" type="text/css" href="${resource(plugin: 'richui', dir: 'js/yui/resize/assets/skins/sam', file: 'resize.css')}"/>
  <link rel="stylesheet" type="text/css" href="${resource(plugin: 'richui', dir: 'js/yui/layout/assets/skins/sam', file: 'layout.css')}"/>
  <link rel="stylesheet" type="text/css" href="${resource(plugin: 'richui', dir: 'js/yui/button/assets/skins/sam', file: 'button.css')}"/>

  <g:javascript plugin='richui' src="yui/yahoo/yahoo-min.js"/>
  <g:javascript plugin='richui' src="yui/event/event-min.js"/>
  <g:javascript plugin='richui' src="yui/dom/dom-min.js"/>
  <g:javascript plugin='richui' src="yui/element/element-min.js"/>
  <g:javascript plugin='richui' src="yui/dragdrop/dragdrop-min.js"/>
  <g:javascript plugin='richui' src="yui/resize/resize-min.js"/>
  <g:javascript plugin='richui' src="yui/animation/animation-min.js"/>
  <g:javascript plugin='richui' src="yui/layout/layout-min.js"/>


  <openlayers:loadMapToolBar/>
  <openlayers:loadTheme theme="default"/>
  <openlayers:loadJavascript/>

  <%--
  <g:javascript library="yui"/>
  --%>
  <style type="text/css">
    /*
    margin and padding on body element
    can introduce errors in determining
    element position and are not recommended;
    we turn them off as a foundation for YUI
    CSS treatments.
    */
  body {
    margin: 0;
    padding: 0;
  }

  div.olControlMousePosition {
    font-family: Verdana;
    font-size: 1.0em;
    background-color: white;
    color: black;
  }

  div.olControlScale {
    background-color: #ffffff;
    font-size: 1.0em;
    font-weight: bold;
  }

  </style>

</head>
<body class="yui-skin-sam">

<div id="header">
  <omar:securityClassificationBanner/>
</div>
<div id="footer">
  <omar:securityClassificationBanner/>
</div>
<div id="content">
  <div id="top">
    <div class="nav">
      <span class="menuButton"><a class="home" href="${resource(dir: '')}">Home</a></span>
      <span class="menuButton">
        <a href="${createLink(controller: "mapView", action: "index", params: [rasterEntryIds: rasterEntry?.id])}">
          Ground Space
        </a>
      </span>
      <span class="menuButton">
        <label>Stretch:</label>
        <g:select id="stretch_mode" name="stretch_mode" from="${['linear_auto_min_max', 'linear_1std_from_mean', 'linear_2std_from_mean', 'linear_3std_from_mean', 'none']}" onChange="changeHistoOpts()"/>
      </span>
      <span class="menuButton">
        <label>Region:</label>
        <g:select id="stretch_mode_region" name="stretch_mode_region" from="${['global', 'viewport']}" onChange="changeHistoOpts()"/>
      </span>
      <%--
      <div>
        <img id="logo" src="${resource(dir: 'images', file: 'OMARLarge.png')}" width="150" height="50" alt="OMAR-2.0 Logo"/>
      </div>
      --%>
    </div>
  </div>
  <div id="bottom">
    bottom
  </div>
  <div id="left">
    left
  </div>
  <div id="right">
    right
  </div>
  <div id="center">
    <%--
    <div class="body">
    --%>
    <div>
      <%--
      <h1 id="mapTitle">${rasterEntry?.mainFile?.name}</h1>

      <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
      </g: if>
      --%>
      <div id="map"></div>
    </div>
  </div>
</div>

<g:javascript>
  (function()
  {
    var Dom = YAHOO.util.Dom;
    var Event = YAHOO.util.Event;
    var Layout = YAHOO.widget.Layout;

    Event.onDOMReady( function()
    {
      var outerLayout = new Layout( {
        units: [
          {
            position: 'top',
            height: 25,
            body: 'header'
          },
          {
            position: 'bottom',
            height: 25,
            body: 'footer'
          },
          {
            position: 'center',
            body: 'content'
          }
        ]
      } );

      outerLayout.on( 'render', function()
      {
        var el = outerLayout.getUnitByPosition( 'center' ).get( 'wrap' );

        var innerLayout = new Layout( el, {
          parent: outerLayout,
          units: [
            {
              position: 'top',
              height: 50,
              body: 'top'
            },
            {
              position: 'bottom',
              height: 100,
              body: 'bottom'
            },
            {
              position: 'left',
              width: 100,
              body: 'left'
            },
            {
              position: 'right',
              width: 100,
              body: 'right'
            },
            {
              position: 'center',
              body: 'center'
            }
          ]
        } );

        //      layout.subscribe( "resize", function( ev )
        //      {
        //      } );


        innerLayout.on( 'render', function()
        {
          init( );
        } );

        outerLayout.on( 'resize', function()
        {
          var c = innerLayout.getUnitByPosition( 'center' );
          var mapWidth = c.get( 'width' );
          var mapHeight = c.get( 'height' );

          resized( mapWidth, mapHeight );
        } );

        innerLayout.render( );
      } );

      outerLayout.render( );

    } );

  })( );
</g:javascript>

<g:javascript>
  var map;
  var layer;

  function changeMapSize()
  {
    var mapTitle = document.getElementById("mapTitle");
    var mapDiv = document.getElementById("map");

    mapDiv.style.width = mapTitle.offsetWidth + "px";
    mapDiv.style.height = Math.round(mapTitle.offsetWidth / 2) + "px";
    map.updateSize();
  }

 function changeHistoOpts()
{
  var stretch_mode = $("stretch_mode").value;
  var stretch_mode_region = $("stretch_mode_region").value;


  layer.mergeNewParams({stretch_mode:stretch_mode, stretch_mode_region: stretch_mode_region});
}

  function get_my_url (bounds)
  {
      var res = this.map.getResolution();
      var x = /*Math.round*/ ((bounds.left - this.maxExtent.left) / (res * this.tileSize.w));
      var y = /*Math.round*/ ((this.maxExtent.top - bounds.top) / (res * this.tileSize.h));
      var z = this.map.getZoom();
      var stretch_mode = $("stretch_mode").value;
      var stretch_mode_region = $("stretch_mode_region").value;

      var path = "?z=" + z + "&x=" + x + "&y=" + y + "&format=" + this.type
          + "&tileWidth=" + this.tileSize.w + "&tileHeight=" + this.tileSize.h
          + "&id=" + ${rasterEntry?.id} + "&stretch_mode=" + stretch_mode
          + "&stretch_mode_region=" + stretch_mode_region;

//      var path = "?bbox=" + x + "," + y + "," + bounds.right + "," + bounds.top

      var url = this.url;
      if (url instanceof Array) {
          url = this.selectUrl(path, url);
      }
      return url + path;
  }

  function init()
  {
    map = new OpenLayers.Map('map', { controls: [], numZoomLevels: 32 } );
    map.addControl(new OpenLayers.Control.LayerSwitcher())
    //map.addControl(new OpenLayers.Control.PanZoom())
    //map.addControl(new OpenLayers.Control.NavToolbar())
    map.addControl(new OpenLayers.Control.MousePosition());
    map.addControl(new OpenLayers.Control.Scale());
    map.addControl(new OpenLayers.Control.ScaleLine());

    var options = {
      maxExtent: new OpenLayers.Bounds(0,0,${width},${height}),
      maxResolution: ${width} / map.getTileSize().w,
        numZoomLevels: 30,
//        numZoomLevels: ${numRLevels},
      type:'jpeg',
      getURL: get_my_url,
      isBaseLayer: true,
      buffer: 0,
      singleTile: true,
      ratio: 1.0,
      transitionEffect: "resize"
    };


    layer = new OpenLayers.Layer.TMS("Layer",
        "${createLink(controller: 'ogc', action: 'getTile')}",
        options
    );


    //changeMapSize();

    map.addLayers([layer]);
    map.zoomToMaxExtent();
    setupToolbar();

  }

    function zoomIn()
    {
      map.zoomIn();
    }

    function zoomOut()
    {
      map.zoomOut();

    }
      function setupToolbar()
      {

        var zoomBoxButton = new OpenLayers.Control.ZoomBox(
        {title:"Zoom into an area by clicking and dragging"});

        var zoomInButton = new OpenLayers.Control.Button({title:'Zoom in',
          displayClass: "olControlZoomIn",
          trigger: zoomIn
        });

        var zoomOutButton = new OpenLayers.Control.Button({title:'Zoom out',
          displayClass: "olControlZoomOut",
          trigger: zoomOut
        });

        var container = $("panel2");

        var panel = new OpenLayers.Control.Panel(
        { div: container,defaultControl: zoomBoxButton,'displayClass': 'olControlPanel'}
                );


        var navButton = new OpenLayers.Control.NavigationHistory({
          nextOptions: {title: "Next View" },
          previousOptions: {title: "Previous View"}
        });


        map.addControl(navButton);

        panel.addControls([
          new OpenLayers.Control.MouseDefaults({title:'Drag to recenter map'}),
          zoomBoxButton,
          zoomInButton,
          zoomOutButton,
          navButton.next, navButton.previous,
          new OpenLayers.Control.ZoomToMaxExtent({title:"Zoom to the max extent"})
        ]);

        map.addControl(panel);
      }


  function resized( mapWidth, mapHeight )
  {
    //alert( "B: " + mapWidth + " " + mapHeight );

    Dom.get( "map" ).style.width = mapWidth + "px";
    Dom.get( "map" ).style.height = mapHeight + "px";

    map.updateSize( );
    alert( mapWidth + " " + mapHeight);

  }

</g:javascript>

</body>
</html>