package chipper

import grails.converters.JSON

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
