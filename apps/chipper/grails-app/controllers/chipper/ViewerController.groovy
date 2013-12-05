package chipper

import org.apache.commons.collections.map.CaseInsensitiveMap

class ViewerController
{
  def grailsApplication

  def index()
  {
    def orthoImage = grailsApplication.config.chipper.chipImage.orthoImage
    def colorImage = grailsApplication.config.chipper.panSharpen.colorImage
    def panImage = grailsApplication.config.chipper.panSharpen.panImage
    def psmImage = [colorImage, panImage].join( ',' )

    [colorImage: colorImage, panImage: panImage, orthoImage: orthoImage, psmImage: psmImage]
  }


}
