package org.ossim.omar.chipper

import org.ossim.omar.raster.RasterEntry

class PanSharpenController
{
  def grailsApplication

  def index()
  {
    if ( params.panImage == null || params.colorImage == null )
    {
      redirect( controller: 'imageList' )
    }
    else
    {
//      def colorImage = RasterEntry.findByFilename( '/data1/test/data/geoeye1/GE1_Hobart_GeoStereo_NITF-NCD/001508507_01000SP00332258/5V090205M0001912264B220000100072M_001508507/Volume1/5V090205M0001912264B220000100072M_001508507.ntf' )
//      def panImage = RasterEntry.findByFilename( '/data1/test/data/geoeye1/GE1_Hobart_GeoStereo_NITF-NCD/001508507_01000SP00332258/5V090205P0001912264B220000100282M_001508507/Volume1/5V090205P0001912264B220000100282M_001508507.ntf' )

      def colorImage = RasterEntry.read( params.colorImage as Long )
      def panImage = RasterEntry.read( params.panImage as Long )

      def bounds = colorImage?.groundGeom?.intersection( panImage?.groundGeom )?.bounds
      def (minX, minY, maxX, maxY) = [bounds?.minLon, bounds?.minLat, bounds?.maxLon, bounds?.maxLat]
      def baseWMS = grailsApplication.config.wms.base.layers[-1]

      def model = [
          baseWMS   : baseWMS,
          colorImage: colorImage.id,
          panImage  : panImage.id,
          minX      : minX,
          minY      : minY,
          maxX      : maxX,
          maxY      : maxY
      ]

      render view: 'index', model: [model: model]
    }
  }
}
