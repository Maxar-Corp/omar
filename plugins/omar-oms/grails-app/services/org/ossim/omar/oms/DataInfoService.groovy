package org.ossim.omar.oms

import joms.oms.DataInfo

class DataInfoService
{

  static transactional = false

  String getInfo( File file )
  {
    this.getInfo( file?.absolutePath )
  }

  String getInfo( String filename )
  {
    def dataInfo = new DataInfo()
    def xml = null

    try
    {
      def canOpen = dataInfo.open( filename )
      if ( canOpen )
      {
        xml = dataInfo.getInfo()?.trim()
      }
    }
    catch ( def e )
    {

    }
    dataInfo?.close()
    dataInfo?.delete()
    dataInfo = null

    return xml
  }
}
