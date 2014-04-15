package org.ossim.omar.chipper

import org.ossim.omar.raster.RasterEntry

class TwoColorMultiController
{
  def grailsApplication

  def index()
  {
    if ( params.redImage == null || params.blueImage == null )
    {
      redirect( controller: 'imageList' )
    }
    else
    {
//    def redImage = RasterEntry.findByFilename( '/data1/space_coast_metric_private/3V050726P0000820271A0100007003410_00574200.ntf' )
//    def blueImage = RasterEntry.findByFilename( '/data1/space_coast_metric_private/po_176062_pan_0000000.tif' )

      def redImage = RasterEntry.read( params.redImage as Long )
      def blueImage = RasterEntry.read( params.blueImage as Long )

      def bounds = redImage?.groundGeom?.intersection( blueImage?.groundGeom )?.bounds
      def (minX, minY, maxX, maxY) = [bounds?.minLon, bounds?.minLat, bounds?.maxLon, bounds?.maxLat]
      def baseWMS = grailsApplication.config.wms.base.layers[-1]

      def model = [
          baseWMS  : baseWMS,
          redImage : redImage.id,
          blueImage: blueImage.id,
          minX     : minX,
          minY     : minY,
          maxX     : maxX,
          maxY     : maxY
      ]

      render view: 'index', model: [model: model]
    }
  }
}
