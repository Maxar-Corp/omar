package org.ossim.omar

import groovy.util.slurpersupport.GPathResult

/**
 * Created by IntelliJ IDEA.
 * User: sbortman
 * Date: May 13, 2010
 * Time: 6:01:33 PM
 * To change this template use File | Settings | File Templates.
 */
class VideoInfoParser implements OmsInfoParser
{
  def additionalTags
  //def tagFile = new File("tags.txt")

  public def processDataSets(GPathResult oms, Repository repository = null)
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
