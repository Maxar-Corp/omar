import org.ossim.omar.security.CustomUserDetailsService

class OmarSecuritySpringGrailsPlugin
{
  // the plugin version
  def version = "0.1"
  // the version or versions of Grails the plugin is designed for
  def grailsVersion = "1.3.7 > *"
  // the other plugins this plugin depends on
  def dependsOn = [
          'springSecurityCore': '1.1.3 > *',
          'springSecurityLdap': '1.0.5 > *',
          'mail': '1.0-SNAPSHOT'
  ]
  // resources that are excluded from plugin packaging
  def pluginExcludes = [
          "grails-app/views/error.gsp"
  ]

  def loadAfter = ['spring-security-ldap']

  // TODO Fill in these fields
  def author = "Scott Bortman"
  def authorEmail = "sbortman@radiantblue.com"
  def title = "OMAR spring security"
  def description = '''\\
Adds support for Spring Security to OMAR	
'''

  // URL to the plugin's documentation
  def documentation = "http://grails.org/plugin/omar-security-spring"

  def doWithWebDescriptor = { xml ->
    // TODO Implement additions to web.xml (optional), this event occurs before
  }

  def doWithSpring = {
    // TODO Implement runtime spring config (optional)

    userDetailsService(CustomUserDetailsService)

    ldapUserDetailsMapper(org.ossim.omar.CustomLdapUserDetailsMapper) {
      springSecurityService = ref("springSecurityService")
    }

  }

  def doWithDynamicMethods = { ctx ->
    // TODO Implement registering dynamic methods to classes (optional)
  }

  def doWithApplicationContext = { applicationContext ->
    // TODO Implement post initialization spring config (optional)
  }

  def onChange = { event ->
    // TODO Implement code that is executed when any artefact that this plugin is
    // watching is modified and reloaded. The event contains: event.source,
    // event.application, event.manager, event.ctx, and event.plugin.
  }

  def onConfigChange = { event ->
    // TODO Implement code that is executed when the project configuration changes.
    // The event is the same as for 'onChange'.
  }
}
