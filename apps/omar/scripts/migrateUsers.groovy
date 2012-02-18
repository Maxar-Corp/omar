import org.ossim.omar.security.SecRole
import org.ossim.omar.security.SecUser
import org.ossim.omar.security.SecUserSecRole

import groovy.sql.Sql

def secRoles = SecRole.list().groupBy { it.authority }

if ( !secRoles )
{
  secRoles = [
          "ROLE_USER": [new SecRole(authority: "ROLE_USER", description: "Standard User").save()],
          "ROLE_ADMIN": [new SecRole(authority: "ROLE_ADMIN", description: "Administrator").save()],
          "ROLE_DOWNLOAD": [new SecRole(authority: "ROLE_DOWNLOAD", description: "Download privileges").save()]
  ]
}

println secRoles

def db = [
        url: "jdbc:postgresql_postGIS://localhost:5432/omardb-1.8.8-prod",
        user: "postgres",
        password: "postgres",
        driver: "org.postgis.DriverWrapper"
]

def sql = Sql.newInstance(db.url, db.user, db.password, db.driver)

sql.rows("SELECT * FROM auth_user").each { user ->
  println user

  def secUser = SecUser.findByUsername(user.username)

  if ( !secUser )
  {
    secUser = new SecUser(
            username: user.username,
            userRealName: user.user_real_name,
            password: user.passwd,
            email: user.email,
            enabled: true,
            accountExpired: false,
            accountLocked: false,
            passwordExpired: false
    )

    if ( secUser.save() )
    {
      sql.rows("SELECT * FROM role_auth_user rau, role r WHERE rau.people_id=r.id AND authorities_id=?", [user.id]).each { roleAuthUser ->
        def secRole = secRoles[roleAuthUser.authority][0]

        println "\t${roleAuthUser}"
        println "\t${secRole}"

        SecUserSecRole.create(secUser, secRole)
      }
    }
    else
    {
      secUser.errors.each {
        println it
      }
    }
  }
}

sql.close()