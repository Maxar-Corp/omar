package org.ossim.omar

import groovy.util.slurpersupport.GPathResult
import org.ossim.postgis.Geometry

/**
 * Created by IntelliJ IDEA.
 * User: sbortman
 * Date: May 16, 2009
 * Time: 7:36:25 PM
 * To change this template use File | Settings | File Templates.
 */

public class OmsInfoParser
{
  def additionalTags
  //def tagFile = new File("tags.txt")

  public def processRasterDataSets(GPathResult oms, Repository repository = null)
  {
    def rasterDataSets = []

    oms?.dataSets?.RasterDataSet.each {rasterDataSetNode ->

      RasterDataSet rasterDataSet = RasterDataSet.initRasterDataSet(rasterDataSetNode)

      if ( rasterDataSet.rasterEntries )
      {
        rasterDataSet.repository = repository
        rasterDataSets << rasterDataSet
        //repository?.addToRasterDataSets(rasterDataSet)
      }
    }

    return rasterDataSets
  }

  public def processVideoDataSets(GPathResult oms, Repository repository = null)
  {
    def videoDataSets = []

    oms?.dataSets?.VideoDataSet.each {videoDataSetNode ->

      VideoDataSet videoDataSet = VideoDataSet.initVideoDataSet(videoDataSetNode)

      videoDataSet.repository = repository
      videoDataSets << videoDataSet
      //repository?.addToVideoDataSets(videoDataSet)
    }



    return videoDataSets
  }
}
