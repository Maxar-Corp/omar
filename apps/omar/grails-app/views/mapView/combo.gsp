<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: Mar 24, 2009
  Time: 4:49:57 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<meta name="layout" content="main"/>
<title>OMAR Map Viewer</title>
<link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css')}"/>

<openlayers:loadTheme theme="default"/>
<openlayers:loadJavascript/>
<resource:tabView/>
<style type="text/css">
.map {
  width: 1024px;
  height: 512px;
<%--
  width: 100%;
  height: 100%;
--%> border: 1px solid black;
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

  /*
  #config {
    margin-top: 1em;
    width: 1024px;
    position: relative;
    height: 8em;
  }
  */

#controls {
  padding-left: 2em;
  margin-left: 0;
  width: 12em;
}

#controls li {
  padding-top: 0.5em;
  list-style: none;
}

</style>
<g:javascript>

    var maps = {};
    var title = "OMAR WMS";

    var layers = "${params.id}";
    var format = "image/jpeg";
    var transparent = false;



function init()
{
  maps = {
    map1: setupUpIsUpMap("map1",${width}, ${height}, ${numRLevels}),
    map2: setupNorthIsUpMap("map2")

  };
}

    function setupUpIsUpMap(mapId, width, height, numRLevels)
    {
      var map = new OpenLayers.Map(mapId);
      map.addControl(new OpenLayers.Control.LayerSwitcher())
      map.addControl(new OpenLayers.Control.PanZoom())
      map.addControl(new OpenLayers.Control.NavToolbar())
      map.addControl(new OpenLayers.Control.MousePosition());
      map.addControl(new OpenLayers.Control.Scale());
      map.addControl(new OpenLayers.Control.ScaleLine());
      map.addControl(new OpenLayers.Control.Attribution());

      var options = {
        maxExtent: new OpenLayers.Bounds(0,0,width,height),
        maxResolution: width / map.getTileSize().w,
        numZoomLevels: numRLevels,
        type:'jpeg',
        getURL: getTileURL,
        isBaseLayer: true,
        buffer: 1,
        singleTile: false,
        transitionEffect: "resize"
      };


      var layer = new OpenLayers.Layer.TMS("Layer",
          "${createLink(controller: 'ogc', action: 'getTile')}",
          options
      );

      map.addLayers([layer]);
      map.zoomToMaxExtent();

      return map;
    }

    function setupNorthIsUpMap(mapId)
    {
      var left = ${rasterEntry.groundGeom.bounds.minLon};
      var bottom = ${rasterEntry.groundGeom.bounds.minLat};
      var right = ${rasterEntry.groundGeom.bounds.maxLon};
      var top = ${rasterEntry.groundGeom.bounds.maxLat};

      map = new OpenLayers.Map(mapId, { controls: [], numZoomLevels: 32 });

      var bounds = new OpenLayers.Bounds(left, bottom, right, top);

      map.maxExtent = bounds;
      //changeMapSize();

      var format = "image/jpeg";
//      var format = "image/png";
//      var format = "image/gif";
      var transparent = false;

      var layer = new OpenLayers.Layer.WMS(
              "Layer",
              "${createLink(controller: 'ogc', action: 'wms')}",
      { layers: "${params.id}", format: format, stretch_mode:"linear_auto_min_max", transparent:transparent  },
      {isBaseLayer: true, buffer:1, singleTile:true, transitionEffect: "resize"}
              );


      map.addLayer(layer);
      map.addControl(new OpenLayers.Control.LayerSwitcher())
      map.addControl(new OpenLayers.Control.PanZoom())
      map.addControl(new OpenLayers.Control.NavToolbar())
      map.addControl(new OpenLayers.Control.MousePosition());
      map.addControl(new OpenLayers.Control.Scale());
      map.addControl(new OpenLayers.Control.Permalink("permalink"));
      map.addControl(new OpenLayers.Control.ScaleLine());
      map.addControl(new OpenLayers.Control.Attribution());


      var zoom = map.getZoomForExtent(bounds, true);

      map.setCenter(bounds.getCenterLonLat(), zoom);
      
      return map;
    }

    function getTileURL (bounds)
    {
        var res = this.map.getResolution();
        var x = Math.round ((bounds.left - this.maxExtent.left) / (res * this.tileSize.w));
        var y = Math.round ((this.maxExtent.top - bounds.top) / (res * this.tileSize.h));
        var z = this.map.getZoom();

        var path = "?z=" + z + "&x=" + x + "&y=" + y + "&format=" + this.type
            + "&tileWidth=" + this.tileSize.w + "&tileHeight=" + this.tileSize.h
            + "&id=" + ${params.id};

//      var path = "?bbox=" + x + "," + y + "," + bounds.right + "," + bounds.top

        var url = this.url;
        if (url instanceof Array) {
            url = this.selectUrl(path, url);
        }
        return url + path;
    }

    function tabChanged(e)
    {

      var label = e.newValue.get('label');
      var map = null;
      var mapDiv = null;

      if ( label == "Image Space")
      {
        map = maps['map1'];
      }
      else if ( "Ground Space")
      {
        map = maps['map2'];
      }

      if ( map )
      {
        var size = map.getSize();


        if ( isNaN(size.w) || isNaN(size.h) )
        {
          alert(size);
          map.updateSize();
          map.zoomToMaxExtent();
        }
      }
    }

    function imageToWorld()
    {
      var map = maps.map1;


      alert(map.getCenter());
    }

    function worldToImage()
    {
      var map = maps.map2;


      alert(map.getCenter());    }

</g:javascript>
</head>

<body onload="init()" >
<div class="nav">
  <span class="menuButton">
    <g:link class="home" uri="/">Home</g:link>
    <button name="" onclick="imageToWorld()">Image to World</button>
    <button onclick="worldToImage()">World to Image</button>
  </span>
</div>

<div class="body">
  <h1 id="mapTitle">Map Widget</h1>
  <g:if test="${flash.message}">
    <div class="message">${flash.message}</div>
  </g:if>

  <div id="map1" class="map"></div>

  <div id="map2" class="map"></div>
</div>
</body>
</html>
