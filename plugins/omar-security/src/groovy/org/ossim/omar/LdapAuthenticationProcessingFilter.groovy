package org.ossim.omar

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.codehaus.groovy.grails.plugins.springsecurity.GrailsAuthenticationProcessingFilter
import org.springframework.security.Authentication
import org.springframework.security.AuthenticationException
import org.springframework.security.userdetails.UsernameNotFoundException
/**
 * Created by IntelliJ IDEA.
 * User: sbortman
 * Date: 12/20/10
 * Time: 2:00 PM
 * To change this template use File | Settings | File Templates.
 */
class LdapAuthenticationProcessingFilter extends GrailsAuthenticationProcessingFilter
{

  /** Session key for the most recent successful LDAP authentication.  */
  static final String LDAP_LAST_AUTH = 'LDAP_LAST_AUTH'

  @Override
  protected void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url)
  {
    println "sendRedirect"

    Authentication authentication = findAuthentication(request)
    if ( authentication )
    {
      request.session[LDAP_LAST_AUTH] = authentication
      url = '/login/autocreate'
    }

    super.sendRedirect request, response, url
  }

  private Authentication findAuthentication(HttpServletRequest request)
  {
    println "findAuthentication"

    AuthenticationException exception = request.session[SPRING_SECURITY_LAST_EXCEPTION_KEY]

    if ( !(exception instanceof UsernameNotFoundException) )
    {
      return null
    }

    return exception.authentication
  }
}