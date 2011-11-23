package org.ossim.omar

import joms.oms.DataInfo

class DataInfoService
{

  static transactional = false

  String getInfo(String filename)
  {
    def dataInfo = new DataInfo()
    def canOpen = dataInfo.open(filename)
    def xml = null
    if ( canOpen )
    {
      xml = dataInfo.getInfo()?.trim()
    }
    dataInfo.close()
    //def xml = StagerUtil.getInfo(filename)

    return xml
  }
}
