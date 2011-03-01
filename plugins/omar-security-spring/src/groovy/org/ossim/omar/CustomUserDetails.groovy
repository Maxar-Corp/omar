package org.ossim.omar

import org.springframework.security.core.GrantedAuthority
import org.codehaus.groovy.grails.plugins.springsecurity.GrailsUser

/**
 * Created by IntelliJ IDEA.
 * User: sbortman
 * Date: 2/24/11
 * Time: 4:56 PM
 * To change this template use File | Settings | File Templates.
 */
class CustomUserDetails extends GrailsUser
{

  final String displayName

  CustomUserDetails(String username, String password, boolean enabled,
                    boolean accountNonExpired, boolean credentialsNonExpired,
                    boolean accountNonLocked,
                    Collection<GrantedAuthority> authorities,
                    long id, String displayName)
  {

    super(username, password, enabled, accountNonExpired,
            credentialsNonExpired, accountNonLocked, authorities, id)

    this.displayName = displayName
  }
}