package chipper

class TwoColorMultiController
{
  def grailsApplication

  def index()
  {

    def redImage = GeospatialImage.findByFilename( grailsApplication?.config?.chipper?.twoColorMulti?.redImage as String )
    def blueImage = GeospatialImage.findByFilename( grailsApplication?.config?.chipper?.twoColorMulti?.blueImage as String )
    def bounds = redImage?.geometry?.intersection( blueImage?.geometry )?.bounds
    def (minX, minY, maxX, maxY) = [bounds?.minLon, bounds?.minLat, bounds?.maxLon, bounds?.maxLat]

    def model = [
        redImage: redImage.filename, blueImage: blueImage.filename,
        minX: minX, minY: minY, maxX: maxX, maxY: maxY
    ]

    model
  }
}
