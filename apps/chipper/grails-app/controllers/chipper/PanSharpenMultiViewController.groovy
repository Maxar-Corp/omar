package chipper

class PanSharpenMultiViewController
{

  def index()
  {
    def orthoImage = grailsApplication.config.chipper.chipImage.orthoImage
    def colorImage = grailsApplication.config.chipper.panSharpen.colorImage
    def panImage = grailsApplication.config.chipper.panSharpen.panImage
    def psmImage = [colorImage, panImage].join( ',' )

    def minX = 147.164803569264
    def minY = -42.9392433157082
    def maxX = 147.259377599723
    def maxY = -42.8613244680972


    [
        colorImage: colorImage,
        panImage: panImage,
        orthoImage: orthoImage,
        psmImage: psmImage,
        minX: minX, minY: minY, maxX: maxX, maxY: maxY
    ]

  }
}
