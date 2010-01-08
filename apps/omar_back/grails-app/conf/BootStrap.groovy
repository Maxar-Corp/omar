import org.grails.plugins.springsecurity.service.AuthenticateService

import grails.util.GrailsUtil

class BootStrap
{

  AuthenticateService authenticateService
  def grailsApplication


  def init = {servletContext ->

    joms.oms.Init.instance().initialize()

    // Fix for "no such property save"  bug in grails 1.1.x.   Should be fixed in 1.2.x
    RasterDataSet.get(-1)
    VideoDataSet.get(-1)

    def userRole = Role.findByAuthority("ROLE_USER")

    if ( !userRole )
    {
      userRole = new Role(authority: "ROLE_USER", description: "user")
      userRole.save()
    }

    def adminRole = Role.findByAuthority("ROLE_ADMIN")

    if ( !adminRole )
    {
      adminRole = new Role(authority: "ROLE_ADMIN", description: "admin")
      adminRole.save()
    }


    def user = AuthUser.findByUsername("user")

    if ( !user )
    {
      user = new AuthUser(
          username: "user",
          userRealName: "user",
          passwd: authenticateService.passwordEncoder("user"),
          enabled: true,
          email: "user@user.com",
          emailShow: false,
          description: "Normal User"
      )
      user.addToAuthorities(userRole)
      user.save()
    }

    def admin = AuthUser.findByUsername("admin")

    if ( !admin )
    {
      admin = new AuthUser(
          username: "admin",
          userRealName: "admin",
          passwd: authenticateService.passwordEncoder("admin"),
          enabled: true,
          email: "admin@admin.com",
          emailShow: false,
          description: "Super User"
      )

      admin.addToAuthorities(userRole)
      admin.addToAuthorities(adminRole)
      admin.save()
    }

    if ( !Requestmap.findByUrl("/home/**") )
    {
      new Requestmap(configAttribute: "ROLE_USER,ROLE_ADMIN", url: "/home/**").save()
    }

    def adminControllers = [
        "user", "role"
    ]

    adminControllers.each {adminController ->
      adminController = adminController.toLowerCase()

      if ( !Requestmap.findByUrl("/${adminController}/**") )
      {
        new Requestmap(configAttribute: "ROLE_ADMIN", url: "/${adminController}/**").save()
      }
    }

    def domainControllers = (((grailsApplication.domainClasses)*.logicalPropertyName).sort()) - ["authUser", "dataSet", "role", "requestmap"]

    domainControllers.each {domainController ->
      domainController = domainController.toLowerCase()

      if ( !Requestmap.findByUrl("/${domainController}/**") )
      {
        new Requestmap(configAttribute: "ROLE_ADMIN", url: "/${domainController}/**").save()
        new Requestmap(configAttribute: "ROLE_USER,ROLE_ADMIN", url: "/${domainController}/index/**").save()
        new Requestmap(configAttribute: "ROLE_USER,ROLE_ADMIN", url: "/${domainController}/list/**").save()
        new Requestmap(configAttribute: "ROLE_USER,ROLE_ADMIN", url: "/${domainController}/show/**").save()
      }
    }

    def searchableControllers = ["rasterEntry", "videoDataSet"]

    searchableControllers.each {controller ->
      controller = controller.toLowerCase()

      if ( !Requestmap.findByUrl("/${controller}/search/**") )
      {
        new Requestmap(configAttribute: "ROLE_USER,ROLE_ADMIN", url: "/${controller}/search/**").save()
        new Requestmap(configAttribute: "ROLE_USER,ROLE_ADMIN", url: "/${controller}/results/**").save()
        new Requestmap(configAttribute: "ROLE_USER,ROLE_ADMIN", url: "/${controller}/kmlnetworklink/**").save()
      }

    }

    new Requestmap(configAttribute: "ROLE_USER,ROLE_ADMIN", url: "/rasterSearch/**").save()


    List<Map<String, String>> searchTagData = [
        [name: "custom", description: "Custom name=value"],
        [name: "file_type", description: "File Type"],
        [name: "class_name", description: "Class Name"]
    ]

    searchTagData.each {
      def searchTag = SearchTag.findByName(it.name)

      if ( !searchTag )
      {
        searchTag = new SearchTag(name: it.name, description: it.description)
        searchTag.save()
      }
    }

    if ( GrailsUtil.isDevelopmentEnv() )
    {
      def baseDirs = ["/Users/sbortman/projects/data", "/Volumes/Passport/data"]

      baseDirs.each {baseDir ->
        def repository = Repository.findByBaseDir(baseDir)

        if ( !repository )
        {
          new Repository(baseDir: baseDir).save()

        }
      }
    }
    else
    {
      def baseDirs = ["/"]

      baseDirs.each {baseDir ->
        def repository = Repository.findByBaseDir(baseDir)

        if ( !repository )
        {
          new Repository(baseDir: baseDir).save()

        }
      }

    }
  }

  def destroy = {
  }
}
