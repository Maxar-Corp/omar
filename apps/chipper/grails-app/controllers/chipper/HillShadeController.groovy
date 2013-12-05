package chipper

class HillShadeController
{
  def grailsApplication

  def index()
  {
    [
        mapImage: grailsApplication.config.chipper.hillShade.mapImage,
        demImage1: grailsApplication.config.chipper.hillShade.demImage1,
        demImage2: grailsApplication.config.chipper.hillShade.demImage2,

        minY: 37.6654930872174,
        minX: -122.567038179736,
        maxY: 38.0233940932067,
        maxX: -122.109265804213
    ]
  }
}
