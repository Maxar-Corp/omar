package chipper

import geoscript.layer.WMS

class WmsGetCapabilitiesViewController
{
  def index()
  {
    def wms = new WMS( "http://localhost:8080/geoserver/ows?service=wms&version=1.1.1&request=GetCapabilities" )

    [wmsGetCaps: wms]
  }
}
