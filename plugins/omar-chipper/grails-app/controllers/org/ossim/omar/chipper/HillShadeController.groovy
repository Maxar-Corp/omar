package org.ossim.omar.chipper

import geoscript.GeoScript
import grails.converters.JSON
import org.ossim.omar.raster.RasterEntry

class HillShadeController
{

  def index()
  {
    def mapImage = RasterEntry.findByFilename( '/data1/sanfran/sanfran_map.tif' )
    def bounds = ( GeoScript.wrap( mapImage?.groundGeom ) )?.bounds
    def (minX, minY, maxX, maxY) = [bounds?.minX, bounds?.minY, bounds?.maxX, bounds?.maxY]
    def baseWMS = grailsApplication.config.wms.base.layers[-1]

    def model = [
        baseWMS : baseWMS,
        mapImage: mapImage.id,
        minX    : minX,
        minY    : minY,
        maxX    : maxX,
        maxY    : maxY
    ]

    render view: 'index', model: [model: model]

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
        [name: "azimuth_angle", value: '270', group: "Hill Shade", editor: 'text'
//            editor: [
//                type: 'slider',
//                options: [
//                    min: '0',
//                    max: '360',
//                    value: '270'
//                ]
//            ]
        ],
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
