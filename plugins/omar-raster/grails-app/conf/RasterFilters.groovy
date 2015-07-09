import org.ossim.omar.raster.GetMapRequest

class RasterFilters
{
  def filters = {
    wmsFootprints( controller: 'wms', action: 'footprints' ) {
      before = {
        //println params
        new GetMapRequest().fixParamNames( params )
      }
    }
  }
}