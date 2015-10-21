package org.ossim.omar.oms

import joms.oms.Info
import grails.converters.JSON

class VersionInfoService
{
   static transactional = false

   /**
    * @brief 
    * @return String representing json object.
    */
   String getOssimVersionInfo()
   {
      joms.oms.Info info = new joms.oms.Info();
      
      String result    = new String();
      String buildDate = new String();
      String version   = new String();
      String revision  = new String();
      
      buildDate = info.getOssimBuildDate();
      version   = info.getOssimVersion();
      revision  = info.getOssimRevisionNumber();

      def results = [ ossim:[build_date: buildDate,
                             version: version,
                             revision: revision] ]
      return results as JSON
   }
}
