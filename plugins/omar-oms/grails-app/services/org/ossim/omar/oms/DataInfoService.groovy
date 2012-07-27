package org.ossim.omar.oms

class DataInfoService
{
  static transactional = false

  def infoGetterPool

  String getInfo( File file )
  {
    this.getInfo( file?.absolutePath )
  }

  String getInfo( String filename )
  {
    def infoGetter = infoGetterPool.borrowObject()
    def xml = infoGetter.runDataInfo(filename)

    infoGetterPool.returnObject(infoGetter)

    return xml
  }
}
