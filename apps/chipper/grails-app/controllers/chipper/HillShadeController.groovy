package chipper

import geoscript.geom.Bounds
import grails.converters.JSON

class HillShadeController
{
  def grailsApplication
  def chipperService

  def index()
  {
    def mapImage = GeospatialImage.findByFilename( grailsApplication?.config?.chipper?.hillShade?.mapImage as String )
    def bounds = mapImage?.geometry?.bounds

    def demImages = chipperService.findElevationCells(
        grailsApplication?.config?.chipper?.hillShade?.elevationPath as String,
        new Bounds(bounds.minLon as double, bounds.minLat as double,
            bounds.maxLon as double, bounds.maxLat as double)
    )

    [
        mapImage: mapImage.filename,
        demImages: demImages,

        minY: bounds?.minLat,
        minX: bounds?.minLon,
        maxY: bounds?.maxLat,
        maxX: bounds?.maxLon
    ]
  }

  def getOptions()
  {
/*
        azimuth_angle:  '270',
        color_blue:  '139',
        color_green:  '26',
        color_red:  '85',
        elevation_angle:  '45',
        gain:  '1.5',
        resampler_filter:  'cubic',
        writer:  'ossim_png'

*/
    def options = [
        [name: "azimuth_angle", value: "270", group: "Hill Shade", editor: "text"],
        [name: "color_blue", value: "139", group: "Hill Shade", editor: "text"],
        [name: "color_green", value: "26", group: "Hill Shade", editor: "text"],
        [name: "color_red", value: "85", group: "Hill Shade", editor: "text"],
        [name: "elevation_angle", value: "45", group: "Hill Shade", editor: "text"],
        [name: "gain", value: "1.5", group: "Hill Shade", editor: "text"],

        [name: "resampler_filter", value: "cubic", group: "Image", editor: "text"],
        [name: "writer", value: "ossim_png", group: "Image", editor: "text"]
    ]

    render contentType: 'application/json', text: [total: options.size(), rows: options] as JSON
  }
}
