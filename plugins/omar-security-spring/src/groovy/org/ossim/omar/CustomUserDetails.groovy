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

  final String userRealName
  final String organization
  final String phoneNumber
  final String email


  CustomUserDetails(String username,
                    String password,
                    boolean enabled,
                    boolean accountNonExpired,
                    boolean credentialsNonExpired,
                    boolean accountNonLocked,
                    Collection<GrantedAuthority> authorities,
                    long id,
                    String userRealName,
                    String organization,
                    String phoneNumber,
                    String email
  )
  {

    super(username, password, enabled, accountNonExpired,
            credentialsNonExpired, accountNonLocked, authorities, id)

    this.userRealName = userRealName
    this.userRealName = organization
    this.userRealName = phoneNumber
    this.userRealName = email
  }
}