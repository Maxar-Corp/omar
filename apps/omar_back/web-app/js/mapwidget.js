OpenLayers.ProxyHost = "/omar-2.0/thumbnail/proxy?url=";

var aoiLayer;
var polygonControl;
var map;

function goto()
{
  var centerLon = $("centerLon").value;
  var centerLat = $("centerLat").value;
  var zoom = map.getZoom();
  var center = new OpenLayers.LonLat(centerLon, centerLat);

  map.setCenter(center, zoom);
}

function clearAOI( e )
{
  aoiLayer.destroyFeatures();
  $("aoiMinLon").value = "";
  $("aoiMaxLat").value = "";
  $("aoiMaxLon").value = "";
  $("aoiMinLat").value = "";
}

function setAOI( e )
{
  var geom = e.feature.geometry;
  var bounds = geom.getBounds();
  var feature = new OpenLayers.Feature.Vector(geom);

  $("aoiMinLon").value = bounds.left;
  $("aoiMaxLat").value = bounds.top;
  $("aoiMaxLon").value = bounds.right;
  $("aoiMinLat").value = bounds.bottom;

  aoiLayer.destroyFeatures();
  aoiLayer.addFeatures(feature, {silent: true});
}


function setView( e )
{
  var bounds = map.getExtent();

  $("viewMinLon").value = bounds.left;
  $("viewMaxLat").value = bounds.top ;
  $("viewMaxLon").value = bounds.right;
  $("viewMinLat").value = bounds.bottom;
}


function setCenterText( e )
{
  var center = map.getCenter();

  $("centerLon").value = center.lon;
  $("centerLat").value = center.lat;
}

function setupMapWidget()
{
}
