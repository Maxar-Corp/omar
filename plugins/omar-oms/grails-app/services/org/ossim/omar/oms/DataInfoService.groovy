package org.ossim.omar.oms

import joms.oms.DataInfo

class DataInfoService
{

  static transactional = false

  String getInfo(String filename)
  {
    def dataInfo = new DataInfo()

    try
    {
        def canOpen = dataInfo.open(filename)
        def xml = null
        if ( canOpen )
        {
            xml = dataInfo.getInfo()?.trim()
        }
    }
    catch(def e)
    {

    }
    dataInfo?.close()
    dataInfo?.delete()
    dataInfo = null

    return xml
  }
}
