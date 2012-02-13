import geodata.City
import geodata.CityData

class BootStrap
{
  def init = { servletContext ->
    if ( City.count() == 0 )
    {
      CityData.load()
    }
  }

  def destroy = {
  }
}
