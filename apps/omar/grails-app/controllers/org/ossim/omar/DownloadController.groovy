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
      def rasterFiles = RasterFile.findByRasterDataSet( rasterEntry.rasterDataSet )

      withPool {
        fileList = rasterFiles?.collectParallel {
          def file = it.name as File
          [
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

    [fileList: fileList]
  }
}
