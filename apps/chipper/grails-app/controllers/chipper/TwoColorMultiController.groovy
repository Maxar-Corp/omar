package chipper

class TwoColorMultiController
{

  def index()
  {

    def redImage = grailsApplication.config.chipper.twoColorMulti.redImage
    def blueImage = grailsApplication.config.chipper.twoColorMulti.blueImage
//    def panImage = grailsApplication.config.chipper.panSharpen.panImage
//    def psmImage = [colorImage, panImage].join( ',' )

    def model =  [
        redImage: redImage, blueImage: blueImage,
        minX:  -80.7890731698967, minY: 28.5112219064134, maxX: -80.6612985456409, maxY: 8.5827095687647
    ]
  }
}
