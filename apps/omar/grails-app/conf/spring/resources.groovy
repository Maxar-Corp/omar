/*
import org.ossim.omar.LdapAuthenticationProcessingFilter
import org.codehaus.groovy.grails.plugins.springsecurity.AuthorizeTools
import org.ossim.omar.AutoCreateLdapUserDetailsMapper
*/
// Place your Spring DSL code here
beans = {
  sql(groovy.sql.Sql, ref('dataSource')) { bean ->
    bean.scope = 'prototype'
  }

/*  def conf = AuthorizeTools.securityConfig.security

  authenticationProcessingFilter(LdapAuthenticationProcessingFilter) {
    authenticationManager = ref('authenticationManager')
    rememberMeServices = ref('rememberMeServices')
    authenticateService = ref('authenticateService')
    authenticationFailureUrl = conf.authenticationFailureUrl
    ajaxAuthenticationFailureUrl = conf.ajaxAuthenticationFailureUrl
    defaultTargetUrl = conf.defaultTargetUrl
    alwaysUseDefaultTargetUrl = conf.alwaysUseDefaultTargetUrl
    filterProcessesUrl = conf.filterProcessesUrl
  }

  ldapUserDetailsMapper(AutoCreateLdapUserDetailsMapper) {
    userDetailsService = ref('userDetailsService')
    passwordAttributeName = conf.ldapPasswordAttributeName
    usePassword = conf.ldapUsePassword
    retrieveDatabaseRoles = conf.ldapRetrieveDatabaseRoles
  }
*/
}