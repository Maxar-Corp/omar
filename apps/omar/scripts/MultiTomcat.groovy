includeTargets << grailsScript("Init")
includeTargets << grailsScript("Bootstrap")
includeTargets << new File("${tomcatPluginDir}/scripts/Tomcat.groovy")

target(main: "Deploy to remote Tomcat") {
  depends(bootstrap)
  def cmd = argsMap.params ? argsMap.params[0] : 'deploy'

  switch ( cmd )
  {
  case 'deploy':
    war()
    grailsApp.config.tomcat.servers.each { name, settings ->
      println "Deploying application $serverContextPath to ${name}:"
      deploy(war: warName,
              url: settings.url,
              path: serverContextPath,
              username: settings.username,
              password: settings.password)
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
  }
}

setDefaultTarget(main)
