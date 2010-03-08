package org.ossim.omar

/**
 * org.ossim.omar.AuthUser for user account.
 */
class AuthUser implements Serializable
{
  static transients = ['pass']
  static hasMany = [authorities: Role]
  static belongsTo = Role

  static mapping = {
    columns {
      username index: 'auth_user_username_idx'
      authorities column: 'authorities_id', joinTable: 'role_auth_user'
    }
  }

  /** Username     */
  String username
  /** User Real Name    */
  String userRealName
  /** MD5 Password     */
  String passwd
  /** enabled     */
  boolean enabled

  String email
  boolean emailShow

  /** description     */
  String description = ''

  /** plain password to create a MD5 password     */
  String pass = '[secret]'

  static constraints = {
    username(blank: false, unique: true)
    userRealName(blank: false)
    passwd(blank: false)
    enabled()
  }
}
