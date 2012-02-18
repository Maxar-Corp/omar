package org.ossim.omar.security

class SecRole
{
  String authority
  String description

  static mapping = {
    cache true
    columns {
      authority index: 'role_authority_idx'
    }
  }

  static constraints = {
    authority(blank: false, unique: true)
    description()
  }
}
