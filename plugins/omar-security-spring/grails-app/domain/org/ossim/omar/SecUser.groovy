package org.ossim.omar

class SecUser
{
  String username
  String password
  String userRealName
  String email

  boolean enabled
  boolean accountExpired
  boolean accountLocked
  boolean passwordExpired

  static constraints = {
    username blank: false, unique: true
    password blank: false
  }

  static mapping = {
    password column: '`password`'
    columns {
      username index: 'sec_user_username_idx'
    }
  }

  Set<SecRole> getAuthorities()
  {
    SecUserSecRole.findAllByAuthUser(this).collect { it.role } as Set
  }
}
