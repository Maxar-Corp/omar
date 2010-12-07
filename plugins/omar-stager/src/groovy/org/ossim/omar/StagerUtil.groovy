package org.ossim.omar
/**
 * Created by IntelliJ IDEA.
 * User: sbortman
 * Date: Dec 18, 2009
 * Time: 9:19:25 PM
 * To change this template use File | Settings | File Templates.
 */

import joms.oms.ImageStager
import joms.oms.DataInfo

class StagerUtil
{
  static DataInfo sharedDataInfo = new DataInfo()
  static ImageStager sharedImageStager = new ImageStager()

  public static def getInfo(File file)
  {
    DataInfo dataInfo = new DataInfo()

    def canOpen = dataInfo.open(file.absolutePath)
    def xml = null

    if ( canOpen )
    {
      ImageStager imageStager = new ImageStager()

      if(imageStager.open(file.absolutePath))
      {
        imageStager.setUseFastHistogramStagingFlag(true)
        def generated = imageStager.stageAll()

        imageStager.delete()

        if ( generated )
        {
          dataInfo.close()
          dataInfo.open(file.absolutePath)
        }

        xml = dataInfo.getInfo()?.trim()
      }
      else
      {
        imageStager.delete();
        imageStager = null
      }

//      if ( xml )
//      {
//        def oms = new XmlSlurper().parseText(xml)
//      }
    }

    dataInfo.close()
    dataInfo.delete()

    return xml
  }


  public static synchronized def getInfoSynchronized(File file)
  {
    def canOpen = sharedDataInfo.open(file.absolutePath)
    def xml = null

    if ( canOpen )
    {
      sharedImageStager.filename = file.absolutePath

      def generated = sharedImageStager.stageAll()

      if ( generated )
      {
        sharedDataInfo.close()
        sharedDataInfo.open(file.absolutePath)
      }

      xml = sharedDataInfo.getInfo()?.trim()

//      if ( xml )
//      {
//        def oms = new XmlSlurper().parseText(xml)
//      }
    }

    sharedDataInfo.close()

    return xml
  }
}
