package org.ossim.omar.chipper

import geoscript.GeoScript
import geoscript.geom.Bounds
import grails.converters.JSON
import org.ossim.omar.raster.RasterEntry

class HillShadeController
{
  def chipperService

  def index()
  {
    if ( params.mapImage == null )
    {
      redirect( controller: 'imageList' )
    }
    else
    {
      //def mapImage = RasterEntry.findByFilename( '/data1/sanfran/sanfran_map.tif' )
      def mapImage = RasterEntry.read( params?.mapImage as Long )
      def bounds = ( GeoScript.wrap( mapImage?.groundGeom ) )?.bounds
      def (minX, minY, maxX, maxY) = [bounds?.minX, bounds?.minY, bounds?.maxX, bounds?.maxY]
      def baseWMS = grailsApplication.config.wms.base.layers[-1]

      def demImages = chipperService.findElevationCells(
          grailsApplication?.config?.chipper?.hillShade?.elevationPath as String,
          bounds
      )


      def model = [
          baseWMS  : baseWMS,
          mapImage : mapImage?.id,
          demImages: demImages,
          minX     : minX,
          minY     : minY,
          maxX     : maxX,
          maxY     : maxY
      ]

      render view: 'index', model: [model: model]
    }
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

    def options = chipperService.defaultHillShadeOpts.collect {
      def group = ( it.key in ['writer', 'resamplerFilter'] ) ? 'Image' : 'Hill Shade'

      [name: it.key, value: it.value, group: group, editor: "text"]
    }
    render contentType: 'application/json', text: [total: options.size(), rows: options] as JSON
  }

}
