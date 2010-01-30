class LayoutTestController
{
  //def defaultAction = "test1"

  def wmsUtilsService

  def index = {

    //def baseAddress = "http://hypercube.telascience.org/cgi-bin/haiti?map=/geo/haiti/mapfiles/4326.map&"
    //def baseAddress = "http://hypercube.telascience.org/cgi-bin/mapserv?map=/home/racicot/haiti/mapfiles/basedata.map&"
    //def baseAddress = "http://${InetAddress.localHost.hostAddress}/tilecache/tilecache.py?"
    //def baseAddress = "http://hypersphere.telascience.org/cgi-bin/bmng?"

    //def baseAddress = "http://${InetAddress.localHost.hostAddress}/cgi-bin/mapserv?map=/data/bmng.map&"
    def baseAddress = "http://hypersphere.telascience.org/geothumper/tilecache/tilecache.cgi?"

    def layers = wmsUtilsService.getLayerList(baseAddress)

    def baseLayers = []
    def overlayLayers = []

    layers.each {
      switch ( it.name )
      {
        case "omar":
        case "basic":
        case "world_topo_bathy":
        case "Reference":  
          baseLayers << it
          break
        default:
          overlayLayers << it
      }
    }

    return [
        overlayLayers: overlayLayers,
        baseLayers: baseLayers,
        centerLon: -72.2,
        centerLat: 19.0,
        zoomLevel: 8
    ]
  }

  def test1 = {}

  def index2 = {
    //def baseAddress = "http://hypercube.telascience.org/cgi-bin/haiti?map=/geo/haiti/mapfiles/4326.map&"
    //def baseAddress = "http://hypercube.telascience.org/cgi-bin/mapserv?map=/home/racicot/haiti/mapfiles/basedata.map&"
    def baseAddress = "http://${InetAddress.localHost.hostAddress}/tilecache/tilecache.py?"
    def layers = wmsUtilsService.getLayerList(baseAddress)

    def baseLayers = []
    def overlayLayers = []

    layers.each {
      switch ( it.name )
      {
        case "omar":
        case "basic":
          baseLayers << it
          break
        default:
          overlayLayers << it
      }
    }

    return [
        overlayLayers: overlayLayers,
        baseLayers: baseLayers,
        centerLon: -72.2,
        centerLat: 19.0,
        zoomLevel: 8
    ]
  }
}
