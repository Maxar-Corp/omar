package org.ossim.omar.oms

class DataInfoService
{
  static transactional = false

  def infoGetterPool

  String getInfo( File file, Integer entryId=null )
  {
    this.getInfo( file?.absolutePath )
  }

  String getInfo( String filename, Integer entryId = null )
  {
    def infoGetter = infoGetterPool.borrowObject()
    def xml
    try{
        xml = infoGetter.runDataInfo(filename, entryId)
    }
    catch(def e)
    {
        xml = null
    }
    infoGetterPool.returnObject(infoGetter)

    return xml
  }
}
