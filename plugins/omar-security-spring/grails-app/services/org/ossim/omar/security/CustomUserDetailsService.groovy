package org.ossim.omar.security

import org.codehaus.groovy.grails.plugins.springsecurity.GrailsUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.core.authority.GrantedAuthorityImpl
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.springframework.security.core.GrantedAuthority
import org.ossim.omar.security.SecUser
import org.ossim.omar.CustomUserDetails

class CustomUserDetailsService implements GrailsUserDetailsService
{

  static transactional = true
  private static final List NO_ROLES = [new GrantedAuthorityImpl(SpringSecurityUtils.NO_ROLE)]
  private static final List USER_ROLE = [new GrantedAuthorityImpl("ROLE_USER")] as GrantedAuthority[]

  UserDetails loadUserByUsername(String username, boolean loadRoles)
  {
    return loadUserByUsername(username)
  }

  UserDetails loadUserByUsername(String username)
  {
    SecUser.withTransaction { status ->

      SecUser user = SecUser.findByUsername(username)

      if ( !user ) throw new UsernameNotFoundException('User not found', username)

      def authorities = user.authorities.collect {new GrantedAuthorityImpl(it.authority)}

      return new CustomUserDetails(
              user.username,
              user.password,
              user.enabled,
              !user.accountExpired,
              !user.passwordExpired,
              !user.accountLocked,
              authorities ?: USER_ROLE, user.id,
              user.userRealName,
              user.organization,
              user.phoneNumber,
              user.email
      )
    }
  }
}
