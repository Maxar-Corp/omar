class LayoutTestController
{
  //def defaultAction = "test1"

  def wmsUtilsService

  def index = {

    def baseAddresses = [
        //"http://hypercube.telascience.org/cgi-bin/haiti?map=/geo/haiti/mapfiles/4326.map&",
        //"http://hypercube.telascience.org/cgi-bin/mapserv?map=/home/racicot/haiti/mapfiles/basedata.map&",
        //"http://${InetAddress.localHost.hostAddress}/tilecache/tilecache.py?"
        //"http://hypersphere.telascience.org/cgi-bin/bmng?",
        //"http://${InetAddress.localHost.hostAddress}/cgi-bin/mapserv?map=/data/bmng.map&",
        //"http://hypersphere.telascience.org/geothumper/tilecache/tilecache.cgi?"
            "http://localhost/tilecache/tilecache.py?"
    ]

    def baseLayers = []
    def overlayLayers = []

    baseAddresses.each {baseAddress ->
      def layers = wmsUtilsService.getLayerList(baseAddress)

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
    }

    render view: "lite", model: [
        overlayLayers: overlayLayers,
        baseLayers: baseLayers,
        centerLon: -72.2,
        centerLat: 19.0,
        zoomLevel: 8
    ]
  }

  def test1 = {}

  def index2 = {
    def baseAddresses = [
        //"http://hypercube.telascience.org/cgi-bin/haiti?map=/geo/haiti/mapfiles/4326.map&",
        //"http://hypercube.telascience.org/cgi-bin/mapserv?map=/home/racicot/haiti/mapfiles/basedata.map&",
        "http://${InetAddress.localHost.hostAddress}/tilecache/tilecache.py?"
        //"http://hypersphere.telascience.org/cgi-bin/bmng?",
        //"http://${InetAddress.localHost.hostAddress}/cgi-bin/mapserv?map=/data/bmng.map&",
        //"http://hypersphere.telascience.org/geothumper/tilecache/tilecache.cgi?"
    ]

    def baseLayers = []
    def overlayLayers = []

    baseAddresses.each {baseAddress ->
      def layers = wmsUtilsService.getLayerList(baseAddress)

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
