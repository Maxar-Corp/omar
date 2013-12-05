package chipper

import org.apache.commons.collections.map.CaseInsensitiveMap

class ViewerController
{
  def grailsApplication

  def index()
  {
    def orthoImage = grailsApplication.config.chipper.orthoImage
    def colorImage = grailsApplication.config.chipper.colorImage
    def panImage = grailsApplication.config.chipper.panImage
    def psmImage = [colorImage, panImage].join( ',' )

    [colorImage: colorImage, panImage: panImage, orthoImage: orthoImage, psmImage: psmImage]
  }


}
