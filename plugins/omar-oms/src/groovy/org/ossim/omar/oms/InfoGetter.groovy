package org.ossim.omar.oms

import joms.oms.DataInfo

/**
 * Created with IntelliJ IDEA.
 * User: sbortman
 * Date: 7/27/12
 * Time: 12:30 PM
 * To change this template use File | Settings | File Templates.
 */
class InfoGetter
{
  def dataInfo = new DataInfo();

  def runDataInfo(def filename, def entryId=null)
  {
    def xml = null

    if ( dataInfo.open(filename) )
    {
      if(entryId)
      {
          xml = dataInfo.getImageInfo(entryId as Integer);
      }
      else
      {
          xml = dataInfo.info
      }
    }
    dataInfo.close()

    return xml
  }

  def cleanup()
  {
    dataInfo.delete()
    dataInfo = null
  }
}