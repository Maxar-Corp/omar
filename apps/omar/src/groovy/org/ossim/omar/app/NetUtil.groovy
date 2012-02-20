package org.ossim.omar.app

/**
 * Created by IntelliJ IDEA.
 * User: sbortman
 * Date: 9/23/11
 * Time: 3:15 PM
 * To change this template use File | Settings | File Templates.
 */
class NetUtil
{
  def static getIpAddress()
  {
    def ipAddress = null

    NetworkInterface.networkInterfaces.each { x ->
      if ( !x.isLoopback() )
      {
        x.inetAddresses.each { y ->
          if ( !y.isLinkLocalAddress() )
          {
            ipAddress = y.hostAddress
          }
        }
      }
    }

    return ipAddress
  }
}
