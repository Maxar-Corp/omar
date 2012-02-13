package geodata.test

class CityMapViewController
{
  def cityMapService

  def index = {
  }

  def wms = {
    cityMapService.getMap(params, response)
  }
}
