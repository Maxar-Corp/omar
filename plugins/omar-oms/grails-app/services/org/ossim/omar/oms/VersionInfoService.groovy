package org.ossim.omar.oms

import joms.oms.Info
import grails.converters.JSON

class VersionInfoService
{
   static transactional = false

   /**
    * @brief Returns version info from the ossim library via oms bindings.
    * @return json object.
    */
   JSON getOssimVersionInfo()
   {
      joms.oms.Info info = new joms.oms.Info();
      
      String result    = new String();
      String version   = new String();
      String buildDate = new String();
      String revision  = new String();
      
      buildDate = info.getOssimBuildDate();
      version   = info.getOssimVersion();
      revision  = info.getOssimRevisionNumber();

      def results = [ ossim:[version: version,
                             build_date: buildDate,
                             revision: revision] ]
      return results as JSON
   }
}
