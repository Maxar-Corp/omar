import geodata.City
import geodata.CityData
import gov.spawar.icode.Ais
import gov.spawar.icode.DataLoader

class BootStrap
{
  def sessionFactory

  def init = { servletContext ->
    if ( City.count() == 0 )
    {
      CityData.load()
    }
    
    if (Ais.count() == 0)
    {
      def aisData = new DataLoader(sessionFactory: sessionFactory)

      aisData.loadAisCSV('SanDiego.csv')
      aisData.loadAisCSV('Chile2.csv')
      aisData.loadCountryData()
    }
  }

  def destroy = {
  }
}
