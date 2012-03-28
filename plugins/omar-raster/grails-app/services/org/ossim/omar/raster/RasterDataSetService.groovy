package org.ossim.omar.raster

import org.ossim.omar.core.Repository

class RasterDataSetService
{

  static transactional = true

  def deleteFromRepository(Repository repository)
  {
    //RasterDataSet.executeUpdate("delete from RasterDataSet r  where r.repository = ?", [repository])

    def rasterDataSets = RasterDataSet.findAllByRepository(repository)

    rasterDataSets?.each { it.delete() }
  }
}
