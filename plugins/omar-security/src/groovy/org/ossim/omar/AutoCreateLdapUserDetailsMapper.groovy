package org.ossim.omar

/**
 * Created by IntelliJ IDEA.
 * User: sbortman
 * Date: 12/20/10
 * Time: 2:02 PM
 * To change this template use File | Settings | File Templates.
 */
import javax.servlet.http.HttpServletRequest

import org.codehaus.groovy.grails.plugins.springsecurity.SecurityRequestHolder
import org.codehaus.groovy.grails.plugins.springsecurity.ldap.GrailsLdapUserDetailsMapper
import org.springframework.ldap.core.DirContextOperations
import org.springframework.security.GrantedAuthority
import org.springframework.security.userdetails.UserDetails
import org.springframework.security.userdetails.UsernameNotFoundException

class AutoCreateLdapUserDetailsMapper extends GrailsLdapUserDetailsMapper {

   static final String LDAP_AUTOCREATE_CURRENT_AUTHORITIES = 'LDAP_AUTOCREATE_CURRENT_AUTHORITIES'

   @Override
   UserDetails mapUserFromContext(DirContextOperations ctx, String username, GrantedAuthority... authorities) {
      try {
         return super.mapUserFromContext(ctx, username, authorities)
      }
      catch (UsernameNotFoundException e) {
         HttpServletRequest request = SecurityRequestHolder.request
         request.session[LDAP_AUTOCREATE_CURRENT_AUTHORITIES] = authorities
         throw e
      }
   }
}