package org.ossim.omar.security

import org.codehaus.groovy.grails.plugins.springsecurity.NullSaltSource
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

class UserPreferencesController
{
  def ldapUtilService
  def springSecurityService
  def saltSource

  def editProfile = {
    def secUserInstance = SecUser.get(params.id)
    if ( !secUserInstance )
    {
      flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'secUser.label', default: 'SecUser'), params.id])}"
      redirect(controller: "home", action: "index")
    }
    else
    {
      return [secUserInstance: secUserInstance]
    }
  }


  def updateProfile = {
    def secUserInstance = SecUser.get(params.id)
    if ( secUserInstance )
    {
      if ( params.version )
      {
        def version = params.version.toLong()
        if ( secUserInstance.version > version )
        {

          secUserInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'secUser.label', default: 'SecUser')] as Object[], "Another user has updated this SecUser while you were editing")
          render(view: "edit", model: [secUserInstance: secUserInstance])
          return
        }
      }
      secUserInstance.properties = params
      if ( !secUserInstance.hasErrors() && secUserInstance.save(flush: true) )
      {
        if ( secUserInstance?.password == "Authenticated by LDAP" )
        {
          ldapUtilService.modifyUser(secUserInstance)
        }
        //flash.message = "${message(code: 'default.updated.message', args: [message(code: 'secUser.label', default: 'SecUser'), secUserInstance.id])}"
        flash.message = "Updated profile for user: ${secUserInstance.username}."
        redirect(controller: "home", action: "index")
      }
      else
      {
        render(view: "editProfile", model: [secUserInstance: secUserInstance])
      }
    }
    else
    {
      flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'secUser.label', default: 'SecUser'), params.id])}"
      redirect(controller: "home", action: "index")
    }
  }

  def changePassword = { ChangePasswordCommand command ->


    if ( !request.post )
    {
      def user = null

      if ( command.username )
      {
        user = SecUser.findByUsername(command.username)
      }
      else if ( params.id )
      {
        user = SecUser.get(params.id)
      }

      boolean isNotAdmin = SpringSecurityUtils.ifNotGranted("ROLE_ADMIN")

      if ( !user )
      {
        flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'secUser.label', default: 'SecUser'), command.username])}"
        redirect(controller: "home", action: "index")
      }
      else if ( user.id != springSecurityService.principal.id && isNotAdmin)
      {
        flash.message = "You don't have permissions to edit that user!"
        redirect(controller: "home", action: "index")
      }

      command.username = user.username
      command.password = user.password
      command.password2 = user.password

      return [command: command]
    }

    command.validate()

    if ( command.hasErrors() )
    {
      return [command: command]
    }

    def user = SecUser.findByUsername(command.username)

    String salt = saltSource instanceof NullSaltSource ? null : user.username
    String newPassword = springSecurityService.encodePassword(command.password, salt)
    if ( user?.password == "Authenticated by LDAP" )
    {
      ldapUtilService.changePassword([username: user.username, password: newPassword])
    }
    else
    {
      SecUser.withTransaction { status ->
        user.password = newPassword
        user.save()
      }
    }

    springSecurityService.reauthenticate user.username

    //flash.message = message(code: 'spring.security.ui.resetPassword.success')
    flash.message = "Successfully changed password for user: ${user.username}"

    redirect(controller: "home", action: "index")
  }
}

class ChangePasswordCommand
{
  String username
  String password
  String password2

  static constraints = {
    password blank: false, minSize: 8, maxSize: 64, validator: RegisterController.passwordValidator
    password2 validator: RegisterController.password2Validator
  }
}
