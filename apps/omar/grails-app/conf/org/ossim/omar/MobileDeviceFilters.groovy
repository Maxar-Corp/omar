
/**
 * Created by IntelliJ IDEA.
 * User: dlucas
 * Date: Jun 11, 2010
 * Time: 3:12:22 PM
 * To change this template use File | Settings | File Templates.
 */

package org.ossim.omar

class MobileDeviceFilters
{
  def filters = {
       detectMobile (controller:'*', action:'*') {
         before = {
           if (request.getHeader('user-agent') =~ /(?i)ipad/) {
             request['isMobile'] = true
           }
           else if (request.getHeader('user-agent') =~ /(?i)iphone/) {
              request['isMobile'] = true
           }
           else {
             request['isMobile'] = false
           }
           return true // keep processing other filters and the action
         }
         after = { }
         afterView = { }
       }
  }
}