/*
import org.ossim.omar.Role
import org.ossim.omar.AuthUser
*/

import org.ossim.omar.SecRole
import org.ossim.omar.SecUser
import org.ossim.omar.SecUserSecRole

import org.ossim.omar.Requestmap
import org.ossim.omar.RasterEntrySearchTag
import org.ossim.omar.VideoDataSetSearchTag
import org.ossim.omar.Repository

import grails.util.GrailsUtil


def springSecurityService = ctx.springSecurityService

/*
def authenticateService = ctx.authenticateService

def userRole = Role.findByAuthority("ROLE_USER") ?: new Role(authority: "ROLE_USER").save()
def adminRole = Role.findByAuthority("ROLE_ADMIN") ?: new Role(authority: "ROLE_ADMIN").save()
def downloadRole = Role.findByAuthority("ROLE_DOWNLOAD") ?: new Role(authority: "ROLE_DOWNLOAD").save()

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
  user.addToAuthorities(userRole).save(flush: true)
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

  admin.addToAuthorities(userRole).addToAuthorities(adminRole).save(flush: true)
}
*/

def userRole = SecRole.findByAuthority("ROLE_USER") ?: new SecRole(authority: "ROLE_USER", description: "Standard User").save()
def adminRole = SecRole.findByAuthority("ROLE_ADMIN") ?: new SecRole(authority: "ROLE_ADMIN", description: "Administrator").save()
def downloadRole = SecRole.findByAuthority("ROLE_DOWNLOAD") ?: new SecRole(authority: "ROLE_DOWNLOAD", description: "Download privileges").save()

def userData = [
    [username: "user", password: springSecurityService.encodePassword("user"), enabled: true, accountExpired: false, accountLocked: false, passwordExpired: false, userRealName: "Some User", email: "user@ossim.og"],
    [username: "admin", password: springSecurityService.encodePassword("admin"), enabled: true, accountExpired: false, accountLocked: false, passwordExpired: false, userRealName: "The Admin", email: "admin@ossim.org"],
]

userData.each {
    def user = new SecUser(it).save()

    if ( user ) {
        SecUserSecRole.create(user, userRole)
    }
    else {
        println "User for ${it.username} is null!"
    }
}

SecUserSecRole.create(SecUser.findByUsername("admin"), adminRole)


if ( !Requestmap.findByUrl("/home/**") )
{
  new Requestmap(configAttribute: "ROLE_USER", url: "/home/**").save()
}

if ( !Requestmap.findByUrl("/userPreferences/**") )
{
  new Requestmap(configAttribute: "ROLE_USER", url: "/userPreferences/**").save()
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

def domainControllers = (((grailsApplication.domainClasses)*.logicalPropertyName).sort()) - ["authUser", "dataSet", "role", "requestmap", "report", "search_mobile", "results_mobile", 'list_mobile', 'show_mobile']

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
new Requestmap(configAttribute: "ROLE_USER,ROLE_ADMIN", url: "/session/**").save()





def searchTagData = grailsApplication.config.rasterEntry.searchTagData

searchTagData.each {
  def searchTag = RasterEntrySearchTag.findByName(it.name)

  if ( !searchTag )
  {
    searchTag = new RasterEntrySearchTag(name: it.name, description: it.description)
    searchTag.save()
  }
}

searchTagData = grailsApplication.config.videoDataSet.searchTagData

searchTagData.each {
  def searchTag = VideoDataSetSearchTag.findByName(it.name)

  if ( !searchTag )
  {
    searchTag = new VideoDataSetSearchTag(name: it.name, description: it.description)
    searchTag.save()
  }
}


if ( GrailsUtil.isDevelopmentEnv() )
{
  def baseDirs = ["/", "/data/uav", "/Volumes/Iomega_HDD/data"]

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