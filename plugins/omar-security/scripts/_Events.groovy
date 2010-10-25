import groovy.xml.StreamingMarkupBuilder
import grails.util.GrailsUtil
import org.codehaus.groovy.grails.commons.ConfigurationHolder

includeTargets << grailsScript("_GrailsPackage")

eventWebXmlEnd = {String tmpfile ->

  def root = new XmlSlurper().parse(webXmlFile)

  if ( !binding.hasProperty('config') )
  {
    createConfig() // target defined in _GrailsPackage
  }

  switch ( GrailsUtil.environment )
  {
  case "development":
  case "production":
    // create session-config with session-timeout if not exists
    if ( root.'session-config'.isEmpty() )
    {
      def sessionTimeout = config.sessionTimeout ?: 30
      root.appendNode { 'session-config' { 'session-timeout'(sessionTimeout) } }
    }
    break
  }

  webXmlFile.text = new StreamingMarkupBuilder().bind {
    mkp.declareNamespace("": "http://java.sun.com/xml/ns/j2ee")
    mkp.yield(root)
  }
}
