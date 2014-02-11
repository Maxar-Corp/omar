package org.ossim.omar

import org.apache.commons.io.FileUtils
import org.ossim.omar.raster.RasterEntry
import org.ossim.omar.raster.RasterFile

import static groovyx.gpars.GParsPool.withPool

class DownloadController
{

  def raster(Long rasterEntryId)
  {
    def fileList = []
    def rasterEntry = RasterEntry.get( rasterEntryId )

    if ( rasterEntry )
    {
      def rasterFiles = RasterFile.findAllByRasterDataSet( rasterEntry.rasterDataSet )

      withPool {
        fileList = rasterFiles?.collectParallel {
          def file = it.name as File
          [
              id: it.id,
              name: file.absolutePath,
              size: FileUtils.byteCountToDisplaySize( file.size() ),
              date: new Date( file.lastModified() )
          ]
        }
      }
    }
    else
    {
      flash.message = "Cannot find RasterEntry with id: ${rasterEntryId}"
      return
    }

    [type: 'raster', fileList: fileList]
  }

  def file(String type, Long id)
  {
    switch ( type )
    {
    case 'raster':
      def file = RasterFile.read( id )
      response.setHeader( 'Content-disposition', "attachment; filename=${file.name}" )
      response.sendRedirect( "http://${grailsApplication.config.omar.serverIP}/${file.name}" )
      break
    }
  }
}
