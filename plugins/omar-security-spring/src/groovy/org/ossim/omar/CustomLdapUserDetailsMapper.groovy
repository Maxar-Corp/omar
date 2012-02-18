package org.ossim.omar

import org.springframework.security.ldap.userdetails.UserDetailsContextMapper

import org.springframework.security.core.authority.GrantedAuthorityImpl
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.ldap.core.DirContextOperations
import org.springframework.security.core.GrantedAuthority
import org.springframework.ldap.core.DirContextAdapter
import org.ossim.omar.security.SecUser
import org.ossim.omar.security.SecUserSecRole
import org.ossim.omar.security.SecRole

/**
 * Created by IntelliJ IDEA.
 * User: sbortman
 * Date: 2/24/11
 * Time: 4:43 PM
 * To change this template use File | Settings | File Templates.
 */
class CustomLdapUserDetailsMapper implements UserDetailsContextMapper
{
  def springSecurityService

  UserDetails mapUserFromContext(DirContextOperations ctx,
                                 String username,
                                 Collection<GrantedAuthority> authority)
  {

    //println "ctx=${ctx} username=${username} authority=${authority}"
    //println ctx.dump()

    SecUser.withTransaction { status ->

      // Try and match the authenticated LDAP user to an existing database User.

      def user = SecUser.findByUsername(username)

      if ( !user )
      {
        // In my case, I construct a new User in my database so I can
        // manage state on that user just like I would a basic auth user.
        user = new SecUser(
                username: username,
                password: "Authenticated by LDAP",
                enabled: true,
                accountExpired: false,
                accountLocked: false,
                passwordExpired: false,
                userRealName: ctx.getStringAttribute("cn"),
                email: ctx.getStringAttribute("mail"),
                organization: ctx.getStringAttribute("description"),
                phoneNumber: ctx.getStringAttribute("telephoneNumber")
        )

        if ( !user.save(flush: true) )
        {
          // handle errors on new User
          println "Can't save User: ${username}:"
          user.errors.allErrors.each { println it }
        }

        SecUser.withTransaction {
          SecUserSecRole.create user, SecRole.findByAuthority("ROLE_USER")
        }
      }

      // Now simply create and return an instance of CustomUserDetails
      return new CustomUserDetails(
              user.username,
              user.password,
              user.enabled,
              !user.accountExpired,
              !user.passwordExpired,
              !user.accountLocked,
              //authority ?: NO_ROLES,
              authority ?: [new GrantedAuthorityImpl("ROLE_USER")],
              user.id,
              user.userRealName,
              user.organization,
              user.phoneNumber,
              user.email
      )
    }
  }

  void mapUserToContext(UserDetails user,
                        DirContextAdapter ctx)
  {
    // unused
  }
}