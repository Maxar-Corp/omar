import geodata.City
import geodata.CityData
import gov.spawar.icode.Ais
import gov.spawar.icode.AisData

class BootStrap
{
  def init = { servletContext ->
    if ( City.count() == 0 )
    {
      CityData.load()
    }
    
    if (Ais.count() == 0)
    {
      AisData.load('SanDiego.csv')
      AisData.load('Chile2.csv')
    }
  }

  def destroy = {
  }
}
