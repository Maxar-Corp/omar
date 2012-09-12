import org.ossim.omar.security.SecRole
import org.ossim.omar.security.SecUser
import org.ossim.omar.security.SecUserSecRole
import org.ossim.omar.security.Requestmap

import org.ossim.omar.raster.RasterEntrySearchTag
import org.ossim.omar.video.VideoDataSetSearchTag
import org.ossim.omar.core.Repository
import org.ossim.omar.ChipFormat

import grails.util.GrailsUtil


def springSecurityService = ctx.springSecurityService


def userRole = SecRole.findByAuthority("ROLE_USER") ?: new SecRole(authority: "ROLE_USER", description: "Standard User").save()
def adminRole = SecRole.findByAuthority("ROLE_ADMIN") ?: new SecRole(authority: "ROLE_ADMIN", description: "Administrator").save()
def downloadRole = SecRole.findByAuthority("ROLE_DOWNLOAD") ?: new SecRole(authority: "ROLE_DOWNLOAD", description: "Download privileges").save()

def userData = [
        [username: "user", password: springSecurityService.encodePassword("user"), enabled: true, accountExpired: false, accountLocked: false, passwordExpired: false, userRealName: "Some User", email: "user@ossim.og"],
        [username: "admin", password: springSecurityService.encodePassword("admin"), enabled: true, accountExpired: false, accountLocked: false, passwordExpired: false, userRealName: "The Admin", email: "admin@ossim.org"],
]

userData.each {
  def user = new SecUser(it).save()

  if ( user )
  {
    SecUserSecRole.create(user, userRole)
  }
  else
  {
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
        "user", "role", 'secUser', 'secRole'
]

adminControllers.each {adminController ->
  adminController = adminController.toLowerCase()

  if ( !Requestmap.findByUrl("/${adminController}/**") )
  {
    new Requestmap(configAttribute: "ROLE_ADMIN", url: "/${adminController}/**").save()
  }
}

def domainControllers = (((grailsApplication.domainClasses)*.logicalPropertyName).sort()) - ["authUser", "dataSet", "role", "requestmap", "report", "search_mobile", "results_mobile", 'list_mobile', 'show_mobile', 'secUser', 'secRole']

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
new Requestmap(configAttribute: "ROLE_ADMIN", url: "/runscript/**").save()





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


  def chips = ["Large 4X3", "800", "600", "Temporary"]
  def chipFmt = ChipFormat.findByLabel(chips[0])
  if ( !chipFmt )
  {
    new ChipFormat(label:chips[0],width:chips[1],height:chips[2],comment:chips[3]).save()
  }
  chips = ["PowerPoint 1", "976", "780", "NGA analyst recommended"]
  chipFmt = ChipFormat.findByLabel(chips[0])
  if ( !chipFmt )
  {
    new ChipFormat(label:chips[0],width:chips[1],height:chips[2],comment:chips[3]).save()
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
