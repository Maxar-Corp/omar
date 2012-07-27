package org.ossim.omar.raster

import groovy.util.slurpersupport.GPathResult
import org.ossim.omar.core.Repository
import org.ossim.omar.stager.OmsInfoParser
import org.ossim.omar.raster.RasterDataSet

/**
 * Created by IntelliJ IDEA.
 * User: sbortman
 * Date: May 13, 2010
 * Time: 5:58:59 PM
 * To change this template use File | Settings | File Templates.
 */
class RasterInfoParser implements OmsInfoParser
{
  def additionalTags
  //def tagFile = new File("tags.txt")

  public def processDataSets( GPathResult oms, Repository repository = null )
  {
    def rasterDataSets = []

    for ( def rasterDataSetNode in oms?.dataSets?.RasterDataSet )
    {

      RasterDataSet rasterDataSet = RasterDataSet.initRasterDataSet( rasterDataSetNode )

      if ( rasterDataSet.rasterEntries )
      {
        rasterDataSet.repository = repository
        rasterDataSets << rasterDataSet
        //repository?.addToRasterDataSets(rasterDataSet)
      }
    }

    return rasterDataSets
  }
}
