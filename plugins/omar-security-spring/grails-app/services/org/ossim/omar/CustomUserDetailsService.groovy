package org.ossim.omar

import org.codehaus.groovy.grails.plugins.springsecurity.GrailsUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.core.authority.GrantedAuthorityImpl
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

class CustomUserDetailsService implements GrailsUserDetailsService
{

  static transactional = true
  private static final List NO_ROLES = [new GrantedAuthorityImpl(SpringSecurityUtils.NO_ROLE)]

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

      return new CustomUserDetails(user.username, user.password, user.enabled,
              !user.accountExpired, !user.passwordExpired,
              !user.accountLocked, authorities ?: NO_ROLES, user.id,
              "${user.userRealName} ${user.email}")
    }
  }
}
