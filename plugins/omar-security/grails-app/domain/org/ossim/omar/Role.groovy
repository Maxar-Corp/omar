package org.ossim.omar
/**
 * org.ossim.omar.Role class for Authority.
 */
class Role implements Serializable
{
  static hasMany = [people: AuthUser]

  static mapping = {
    columns {
      authority index: 'role_authority_idx'
      people column: 'people_id', joinTable: 'role_auth_user'
    }
  }

  /** description   */
  String description
  /** ROLE String   */
  String authority = 'ROLE_'

  static constraints = {
    authority(blank: false)
    description()
  }
}
