package org.ossim.omar.federation

import grails.converters.JSON
import org.apache.commons.collections.map.CaseInsensitiveMap
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

import java.awt.Color

class FederationController
{
  def jabberFederatedServerService
  def grailsApplication
  def springSecurityService

  def index()
  {
    forward controller: "federation", action: "search"
  }

  def search()
  {
    def roles = []
    if ( springSecurityService.isLoggedIn() )
    {
      def authorities = springSecurityService.principal.authorities
      roles = authorities.collect() { it.authority }
    }
    else
    {
      roles = ["ROLE_USER"]
    }
    def styles = grailsApplication.config.rasterEntry.styles
    def jsonStyles = []
    styles.each { style ->
      def colorlookup = [:]
      style.outlineLookupTable.each { name, value ->
        value = value.encodeAsHexColor()
        colorlookup."${name}" = value
      }
      jsonStyles << ["styleName" : "by${style.propertyName.capitalize()}",
                     "colorTable": colorlookup]
    }
    def wmsBaseLayers = ( grailsApplication.config.wms as JSON ).toString()
    def footprintStyle = grailsApplication.config?.wms?.data?.raster?.params?.styles ? grailsApplication.mainContext.getBean( grailsApplication.config?.wms?.data?.raster?.params?.styles ) : null
    render view: 'search', model: [wmsBaseLayers : wmsBaseLayers,
                                   footprintStyle: footprintStyle,
                                   roles         : roles as JSON,
                                   styles        : jsonStyles as JSON
    ]
  }

  def serverList()
  {
    def tempParam = new CaseInsensitiveMap( params );
    if ( !jabberFederatedServerService.isConnected() &&
        jabberFederatedServerService.wasConnected &&
        jabberFederatedServerService.enabled
    )
    {
      jabberFederatedServerService.reconnect()
    }
    def result = jabberFederatedServerService.serverList as JSON
    def callback = ""
    if ( tempParam.callback )
    {
      callback = tempParam.callback
    }
    else if ( tempParam.jsonCallback )
    {
      callback = tempParam.jsonCallback
    }
    if ( callback )
    {
      result = "${callback}(${result})"// added for cross domain support
    }
    render contentType: 'application/json', text: result.toString()
  }

  def reconnect()
  {
    if ( SpringSecurityUtils.ifAllGranted( "ROLE_ADMIN" ) )
    {
      jabberFederatedServerService.reconnect();
    }

    def tempParam = new CaseInsensitiveMap( params );
    def userAndId = jabberFederatedServerService.makeFullUserNameAndId( jabberFederatedServerService.jabberUser );
    def result = [error: "", id: "${userAndId.id}", user: userAndId.user, connected: jabberFederatedServerService.isConnected()] as JSON
    def callback = ""
    if ( tempParam.callback )
    {
      callback = tempParam.callback
    }
    if ( callback )
    {
      result = "${callback}(${result})"// added for cross domain support
    }
    render contentType: 'application/json', text: result.toString()
  }

  def admin()
  {

  }
}
