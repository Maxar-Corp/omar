package org.ossim.omar.oms

class VersionInfoController
{
   def versionInfoService

   def getVersion(){ render contentType:'application/json', text:versionInfoService.getOssimVersionInfo() }
}
