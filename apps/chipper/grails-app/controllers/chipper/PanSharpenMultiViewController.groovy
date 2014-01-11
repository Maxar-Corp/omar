package chipper

class PanSharpenMultiViewController
{
  def grailsApplication

  def index()
  {
    def colorImage = GeospatialImage.findByFilename( grailsApplication?.config?.chipper?.panSharpen?.colorImage as String )
    def panImage = GeospatialImage.findByFilename( grailsApplication?.config?.chipper?.panSharpen?.panImage as String )
    def bounds = colorImage.geometry.intersection( panImage.geometry )?.bounds
    def (minX, minY, maxX, maxY) = [bounds?.minLon, bounds?.minLat, bounds?.maxLon, bounds?.maxLat]
    def baseWMS = grailsApplication.config.chipper.baseWMS

//    println bounds

    def model = [
        baseWMS: baseWMS,
        colorImage: colorImage.filename,
        panImage: panImage.filename,
        psmImage: [colorImage.filename, panImage.filename].join( ',' ),
        minX: minX, minY: minY, maxX: maxX, maxY: maxY
    ]

//    println model
    model
  }
}
