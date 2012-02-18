package org.ossim.omar.security

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

class LdapUtilService
{
  static transactional = true

  def grailsApplication

  def addUser(def userDetails)
  {
    def securityConfig = SpringSecurityUtils.securityConfig

    def ldapServer = securityConfig.ldap.context.server
    def ldapPassword = securityConfig.ldap.context.managerPassword
    def managerDN = securityConfig.ldap.context.managerDn
    def tempFile = File.createTempFile("user", ".ldif", new File("/tmp"))
    def cmd = "ldapmodify -a -x -w ${ldapPassword} -H ${ldapServer} -D ${managerDN} -f ${tempFile.absolutePath}"
    def userDn = securityConfig.ldap.context.userDn

    tempFile.withWriter { out ->
      out << "dn: uid=${userDetails.username},ou=people,${userDn}\n"
      out << "changetype: add\n"
      out << "cn: ${userDetails.username}\n"
      out << "givenName: ${userDetails.userRealName}\n"
      out << "description: ${userDetails.organization}\n"
      out << "telephoneNumber: ${userDetails.phoneNumber}\n"
      out << "objectClass: top\n"
      out << "objectClass: inetOrgPerson\n"
      out << "sn: User\n"
      out << "uid: ${userDetails.username}\n"
      out << "userPassword: {${securityConfig.password.algorithm}}${userDetails.password}\n"
      out << "mail: ${userDetails.email}"
    }

    def exitValue = runCommand(cmd)

    tempFile.delete()

    return exitValue
  }

  private def runCommand(GString cmd)
  {
    //println cmd

    def out = new StringBuilder()
    def err = new StringBuilder()
    def proc = cmd.execute()

    proc.waitForProcessOutput(out, err)

    if ( out )
    {
      println "out:\n$out"
    }

    if ( err )
    {
      println "err:\n$err"
    }

    return proc.exitValue()
  }

  def removeUser(def userDetails)
  {
    def securityConfig = SpringSecurityUtils.securityConfig

    def ldapServer = securityConfig.ldap.context.server
    def ldapPassword = securityConfig.ldap.context.managerPassword
    def managerDN = securityConfig.ldap.context.managerDn
    def tempFile = File.createTempFile("user", ".ldif", new File("/tmp"))
    def cmd = "ldapmodify -a -x -w ${ldapPassword} -H ${ldapServer} -D ${managerDN} -f ${tempFile.absolutePath}"
    def userDn = securityConfig.ldap.context.userDn

    tempFile.withWriter { out ->
      out << "dn: uid=${userDetails.username},ou=people,${userDn}\n"
      out << "changetype: delete\n"
    }

    def exitValue = runCommand(cmd)

    tempFile.delete()

    return exitValue
  }

  def modifyUser(def userDetails)
  {
    def securityConfig = SpringSecurityUtils.securityConfig

    def ldapServer = securityConfig.ldap.context.server
    def ldapPassword = securityConfig.ldap.context.managerPassword
    def managerDN = securityConfig.ldap.context.managerDn
    def tempFile = File.createTempFile("user", ".ldif", new File("/tmp"))
    def cmd = "ldapmodify -a -x -w ${ldapPassword} -H ${ldapServer} -D ${managerDN} -f ${tempFile.absolutePath}"
    def userDn = securityConfig.ldap.context.userDn

    tempFile.withWriter { out ->
      out << "dn: uid=${userDetails.username},ou=people,${userDn}\n"
      out << "changetype: modify\n"
      out << "replace: givenName\n"
      out << "givenName: ${userDetails.userRealName}\n"
      out << "-\n"
      out << "replace: description\n"
      out << "description: ${userDetails.organization}\n"
      out << "-\n"
      out << "replace: telephoneNumber\n"
      out << "telephoneNumber: ${userDetails.phoneNumber}\n"
      out << "-\n"
      out << "replace: mail\n"
      out << "mail: ${userDetails.email}"
    }

    def exitValue = runCommand(cmd)

    tempFile.delete()

    return exitValue
  }


  def changePassword(def userDetails)
  {
    def securityConfig = SpringSecurityUtils.securityConfig

    def ldapServer = securityConfig.ldap.context.server
    def ldapPassword = securityConfig.ldap.context.managerPassword
    def managerDN = securityConfig.ldap.context.managerDn
    def tempFile = File.createTempFile("user", ".ldif", new File("/tmp"))
    def cmd = "ldapmodify -a -x -w ${ldapPassword} -H ${ldapServer} -D ${managerDN} -f ${tempFile.absolutePath}"
    def userDn = securityConfig.ldap.context.userDn

    tempFile.withWriter { out ->
      out << "dn: uid=${userDetails.username},ou=people,${userDn}\n"
      out << "changetype: modify\n"
      out << "replace: userPassword\n"
      out << "userPassword: {${securityConfig.password.algorithm}}${userDetails.password}"
    }

    def exitValue = runCommand(cmd)

    tempFile.delete()

    return exitValue
  }
}
