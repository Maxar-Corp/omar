includeTargets << new File("${tomcatPluginDir}/scripts/Tomcat.groovy")
includeTargets << grailsScript("Init")
includeTargets << grailsScript("Bootstrap")


def printHelp()
{
  println """
    Usage: grails [mode] <command>

    Where mode is either dev (default), prod, or test and
    command is one of the following:

      deploy   - generates war and deploys to tomcats defined in Config.groovy.   MUST BE RUN in prod mode ONLY!
      list     - lists all the apps deployed in each tomcat defined in Config.groovy.
      undeploy - undeploys omar from eachof the tomcats defined in Config.groovy.

   Caveats:

    When re-deploying to a tomcat where omar already exists.   tomcat will have to be restarted due to JNI  class
    loading issues.  Looking into a better solution for this.
  """
}

target(main: "Deploy to remote Tomcat") {
  depends(parseArguments, packageApp)
  def cmd = argsMap.params ? argsMap.params[0] : 'help'


  switch ( cmd )
  {
  case 'deploy':
    if ( grailsEnv == "production" )
    {
      war()
      config.tomcat.servers.each { name, settings ->
        println "Deploying application $serverContextPath to ${name}:"
        deploy(war: warName,
                url: settings.url,
                path: serverContextPath,
                username: settings.username,
                password: settings.password)
      }
    }
    else
    {
      println "This action can only be run in production mode."
    }
    break
  case 'list':
    grailsApp.config.tomcat.servers.each { name, settings ->
      println "Listing applications on ${name}:"

      list(url: settings.url,
              username: settings.username,
              password: settings.password)
    }
    break
  case 'undeploy':
    configureServerContextPath()
    println '''\
                  NOTE: If you experience a classloading error during undeployment you need to take the following steps:

                  * Upgrade to Tomcat 6.0.20 or above
                  * Pass this system argument to Tomcat: -Dorg.apache.catalina.loader.WebappClassLoader.ENABLE_CLEAR_REFERENCES=false

                  See http://tomcat.apache.org/tomcat-6.0-doc/config/systemprops.html for more information
                  '''

    grailsApp.config.tomcat.servers.each { name, settings ->

      println "Undeploying application $serverContextPath from ${name}:"

      undeploy(url: settings.url,
              path: serverContextPath,
              username: settings.username,
              password: settings.password)
    }
    break
  case "help":
    printHelp()
    break
  default:
    printHelp()
    break
  }
}

setDefaultTarget(main)
