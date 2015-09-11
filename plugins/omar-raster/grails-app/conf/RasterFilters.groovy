import org.ossim.omar.raster.GetMapRequest
import org.ossim.omar.ogc.WmsCommand

class RasterFilters
{
  def filters = {
    wmsFootprints( controller: 'wms', action: 'footprints' ) {
      before = {
        //println params
        new GetMapRequest().fixParamNames( params )
      }
    }
    wmsGetMap( controller: 'wms', action: 'getMap_' ) {
      before = {
        //println params
        new WmsCommand().fixParamNames( params )
      }
    }

  }
}