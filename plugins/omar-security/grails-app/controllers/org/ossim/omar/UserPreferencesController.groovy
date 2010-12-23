package org.ossim.omar

import org.grails.plugins.springsecurity.service.AuthenticateService

class UserPreferencesController
{
  AuthenticateService authenticateService

  def edit = {
    def person = AuthUser.get(params.id)

    if ( person )
    {
      def authPrincipal = authenticateService.principal()
      if ( !(authPrincipal instanceof String) && authPrincipal.username == person.username )
      {
        return [person: person]
      }
      else
      {
        flash.message = "Not autorized to edit AuthUser with id ${params.id}"
        redirect(controller: "home")
      }
    }
    else
    {
      flash.message = "AuthUser not found with id ${params.id}"
      redirect(controller: "home")
    }
  }

  /**
   * Person update action.
   */
  def update = {

    def person = AuthUser.get(params.id)

    if ( !person )
    {
      flash.message = "AuthUser not found with id ${params.id}"
      redirect(action: edit, id: params.id)
      return
    }

    def oldPassword = person.passwd

    person.properties = params

    if ( params.passwd.equals(params.verifypasswd) )
    {
      person.passwd = authenticateService.passwordEncoder(params.passwd)
    }
    else
    {
      flash.message = "Passwords do not match"
      redirect(action: edit, id: params.id)
      return
    }

    if ( person.save(flush: true) )
    {
      redirect(controller: "home")
    }
    else
    {
      render(view: 'edit', model: [person: person])
    }
  }
}
