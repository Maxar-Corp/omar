package org.ossim.omar

import org.springframework.beans.factory.InitializingBean
import org.grails.plugins.springsecurity.service.AuthenticateService

import org.springframework.security.providers.UsernamePasswordAuthenticationToken as AuthToken
import org.springframework.security.context.SecurityContextHolder as SCH
import org.ossim.omar.AuthUser
import org.ossim.omar.Role
import org.ossim.omar.EmailerService
import org.springframework.security.context.SecurityContextHolder
import org.joda.time.*

/**
 * Actions over org.ossim.omar.AuthUser object.
 */
class RegisterController implements InitializingBean
{
  def userVerificaiton
  EmailerService emailerService
  AuthenticateService authenticateService
  def daoAuthenticationProvider

  def static allowedMethods = [save: 'POST', update: 'POST']

  /**
   * User Registration Top page
   */
  def index = {

    //if logon user.
    if ( authenticateService.userDomain() )
    {
      log.info("${authenticateService.userDomain()} user hit the register page")
      redirect(action: 'show')
      return
    }

    if ( session.id )
    {
      def person = new AuthUser()
      person.properties = params
      return [person: person]
    }

    redirect(uri: '/')
  }

  /**
   * User Information page for current user.
   */
  def show = {

    //get user id from session's domain class.
    def user = authenticateService.userDomain()
    if ( user )
    {
      render(view: 'show', model: [person: AuthUser.get(user.id)])
    }
    else
    {
      redirect(action: 'index')
    }
  }

  /**
   * Edit page for current user.
   */
  def edit = {

    def person
    def user = authenticateService.userDomain()
    if ( user )
    {
      person = AuthUser.get(user.id)
    }

    if ( !person )
    {
      flash.message = "[Illegal Access] User not found with id ${params.id}"
      redirect(action: 'index')
      return
    }

    [person: person]
  }

  /**
   * update action for current user's edit page
   */
  def update = {

    def person
    def user = authenticateService.userDomain()
    if ( user )
    {
      person = AuthUser.get(user.id)
    }
    else
    {
      redirect(action: 'index')
      return
    }

    if ( !person )
    {
      flash.message = "[Illegal Access] User not found with id ${params.id}"
      redirect(action: 'index', id: params.id)
      return
    }

    //if user want to change password. leave passwd field blank, passwd will not change.
    if ( params.passwd && params.passwd.length() > 0
            && params.repasswd && params.repasswd.length() > 0 )
    {
      if ( params.passwd == params.repasswd )
      {
        person.passwd = authenticateService.passwordEncoder(params.passwd)
      }
      else
      {
        person.passwd = ''
        flash.message = 'The passwords you entered do not match.'
        render(view: 'edit', model: [person: person])
        return
      }
    }

    person.userRealName = params.userRealName
    person.email = params.email
    if ( params.emailShow )
    {
      person.emailShow = true
    }
    else
    {
      person.emailShow = false
    }

    if ( person.save() )
    {
      redirect(action: 'show', id: person.id)
    }
    else
    {
      render(view: 'edit', model: [person: person])
    }
  }

  /**
   * Verify user action.
   */
  def verifyUser = {
    
    def verifyEncoding = AuthUser.findWhere(verificationEncoding:params.verificationEncoding, enabled:false)
    if (verifyEncoding)
    {
      verifyEncoding.enabled = true
	  flash.message = verifyEncoding.username + ' is now enabled and verified.'
      //redirect(controller: 'logout')
	
      redirect(controller: 'login', action: 'auth')
      return
    }
    else
    {
      render "Alert: Invalid user verification encoding. Please contact your system administrator for further assistance."
      return
    }
  }

  /**
   * Person save action.
   */
  def save = {

    if ( authenticateService.userDomain() != null )
    {
      log.info("${authenticateService.userDomain()} user hit the register page")
      redirect(action: 'show')
      return
    }

    def person = new AuthUser()
    person.properties = params

    def config = authenticateService.securityConfig
    def defaultRole = config.security.defaultRole

    def role = Role.findByAuthority(defaultRole)
    if ( !role )
    {
      person.passwd = ''
      flash.message = 'Default org.ossim.omar.Role not found.'
      render(view: 'index', model: [person: person])
      return
    }

    if ( params.captcha.toUpperCase() != session.captcha )
    {
      person.passwd = ''
      flash.message = 'Access code did not match.'
      render(view: 'index', model: [person: person])
      return
    }

    if ( params.passwd != params.repasswd )
    {
      person.passwd = ''
      flash.message = 'The passwords you entered do not match.'
      render(view: 'index', model: [person: person])
      return
    }

    def pass = authenticateService.passwordEncoder(params.passwd)
    person.passwd = pass

	if ( userVerificaiton == "none" )
	{
	  person.enabled = true
	}
	else if ( userVerificaiton == "manual" )
	{
	  person.enabled = false
	}
	else if ( userVerificaiton == "email" )
	{
	  person.enabled = false
	  
	  def jodaDateTime = new org.joda.time.DateTime()
	  def verificationEncoding = (person.username + jodaDateTime.toString()).encodeAsSHA256()
	  
	  person.verificationEncoding = verificationEncoding
		
	  def host = "localhost"
	  def port = "8080"
	  def link = "http://" + host + ":" + port + "/omar/register/verifyUser?verificationEncoding=" + verificationEncoding
	  println link
	}
	
    person.emailShow = true
    person.description = ''
    if ( person.save() )
    {
      role.addToPeople(person)
      if ( config.security.useMail )
      {
        String emailContent = """You have signed up for an account at:

 ${request.scheme}://${request.serverName}:${request.serverPort}${request.contextPath}

 Here are the details of your account:
 -------------------------------------
 LoginName: ${person.username}
 Email: ${person.email}
 Full Name: ${person.userRealName}
 Password: ${params.passwd}
"""

        def email = [
                to: [person.email], // 'to' expects a List, NOT a single email address
                subject: "[${request.contextPath}] Account Signed Up",
                text: emailContent // 'text' is the email body
        ]
        emailerService.sendEmails([email])
      }

      person.save(flush: true)

//      def auth = new AuthToken(person.username, params.passwd)
//      def authtoken = daoAuthenticationProvider.authenticate(auth)
//      SecurityContextHolder.context.authentication = authtoken

      session.invalidate()

       redirect(uri: '/')
    }
    else
    {
      person.passwd = ''
      render(view: 'index', model: [person: person])
    }
  }
  public void afterPropertiesSet()
  {
	userVerificaiton = grailsApplication.config.login?.registration?.userVerificaiton
  }
}
