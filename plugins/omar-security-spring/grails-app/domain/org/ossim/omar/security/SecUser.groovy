package org.ossim.omar.security

class SecUser
{
  String username
  String password
  String userRealName
  String organization
  String phoneNumber
  String email

  boolean enabled
  boolean accountExpired
  boolean accountLocked
  boolean passwordExpired

  static constraints = {
    username blank: false, unique: true
    password blank: false
    userRealName nullable: true
    //email email: true, blank: false
    email nullable: true
    organization nullable: true
    phoneNumber nullable: true
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

  def getAuthoritiesAsStringList()
  {
      def result = []
      def authorities = getAuthorities()
      authorities.each{
          result << it.authority
      }
      result
  }
}
