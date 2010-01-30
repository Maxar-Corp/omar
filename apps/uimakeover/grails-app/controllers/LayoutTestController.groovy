class LayoutTestController
{
  //def defaultAction = "test1"

  def index = {

    //def layersAddress = "http://hypercube.telascience.org/cgi-bin/haiti?map=/geo/haiti/mapfiles/4326.map&"
    //def layersAddress = "http://hypercube.telascience.org/cgi-bin/mapserv?map=/home/racicot/haiti/mapfiles/basedata.map&"
    def baseAddress = "http://${InetAddress.localHost.hostAddress}/tilecache/tilecache.py?"
    def capabilitiesURL = "${baseAddress}service=WMS&version=1.1.1&request=GetCapabilities".toURL()
    def capabilities = new XmlSlurper().parseText(capabilitiesURL.text)

    def baseLayers = []
    def overlayLayers = []

    capabilities.Capability.Layer.Layer.each {
      switch ( it.Name as String )
      {
        case "omar":
        case "basic":
          baseLayers << [name: it.Name, title: it.Title, url: baseAddress]
          break
        default:
          overlayLayers << [name: it.Name, title: it.Title, url: baseAddress]
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
}
