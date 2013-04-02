import org.ossim.omar.security.SecRole
import org.ossim.omar.security.SecUser
import org.ossim.omar.security.SecUserSecRole
import org.ossim.omar.security.Requestmap

import org.ossim.omar.core.Repository
import org.ossim.omar.ChipFormat

import grails.util.GrailsUtil


def springSecurityService = ctx.springSecurityService

if ( SecRole.count() == 0 && SecUser.count() == 0 && SecUserSecRole.count() == 0 )
{
  def roleData = [
      [authority: "ROLE_USER", description: "Standard User"],
      [authority: "ROLE_ADMIN", description: "Administrator"],
      [authority: "ROLE_DOWNLOAD", description: "Download privileges"]
  ]

  def roles = roleData.collect { SecRole.findOrSaveWhere( it ) }.inject( [:] ) { a, b -> a[b.authority] = b; a }

  def userData = [
      [username: "user", password: springSecurityService.encodePassword( "user" ), enabled: true,
          accountExpired: false, accountLocked: false, passwordExpired: false, userRealName: "Some User",
          email: "user@ossim.og"],
      [username: "admin", password: springSecurityService.encodePassword( "admin" ), enabled: true,
          accountExpired: false, accountLocked: false, passwordExpired: false, userRealName: "The Admin",
          email: "admin@ossim.org"],
  ]

  def users = userData.collect { SecUser.findOrSaveWhere( it ) }.inject( [:] ) { a, b -> a[b.username] = b; a }

  users.each { String username, SecUser user ->
    SecUserSecRole.create( user, roles['ROLE_USER'] )

    if ( username == 'admin' )
    {
      SecUserSecRole.create( user, roles['ROLE_ADMIN'] )
      SecUserSecRole.create( user, roles['ROLE_DOWNLOAD'] )
    }
  }
}

if ( Requestmap.count() == 0 )
{
  def requestmapData = [
      [url: "/home/**", configAttribute: "ROLE_USER"],
      [url: "/userpreferences/**", configAttribute: "ROLE_USER"]
  ]

  requestmapData.each {
    Requestmap.findOrSaveWhere( it )
  }


  def adminControllers = [
      "user", "role", 'secUser', 'secRole'
  ]

  adminControllers.each { adminController ->
    adminController = adminController.toLowerCase()
    Requestmap.findOrSaveWhere( configAttribute: "ROLE_ADMIN", url: "/${adminController}/**" )
  }

  def domainControllers = ( ( ( grailsApplication.domainClasses )*.logicalPropertyName ).sort() )

  domainControllers -= [
      "authUser", "dataSet", "role", "requestmap", "report", "search_mobile", "results_mobile",
      'list_mobile', 'show_mobile', 'secUser', 'secRole'
  ]

  domainControllers.each { domainController ->
    domainController = domainController.toLowerCase()
    Requestmap.findOrSaveWhere( configAttribute: "ROLE_ADMIN", url: "/${domainController}/**" )
    Requestmap.findOrSaveWhere( configAttribute: "ROLE_USER,ROLE_ADMIN", url: "/${domainController}/index/**" )
    Requestmap.findOrSaveWhere( configAttribute: "ROLE_USER,ROLE_ADMIN", url: "/${domainController}/list/**" )
    Requestmap.findOrSaveWhere( configAttribute: "ROLE_USER,ROLE_ADMIN", url: "/${domainController}/show/**" )
  }

  def searchableControllers = ["rasterEntry", "videoDataSet"]

  searchableControllers.each { controller ->
    controller = controller.toLowerCase()

    Requestmap.findOrSaveWhere( configAttribute: "ROLE_USER,ROLE_ADMIN", url: "/${controller}/search/**" )
    Requestmap.findOrSaveWhere( configAttribute: "ROLE_USER,ROLE_ADMIN", url: "/${controller}/results/**" )
    Requestmap.findOrSaveWhere( configAttribute: "ROLE_USER,ROLE_ADMIN", url: "/${controller}/kmlnetworklink/**" )
  }

  Requestmap.findOrSaveWhere( configAttribute: "ROLE_USER,ROLE_ADMIN", url: "/rasterSearch/**" )
  Requestmap.findOrSaveWhere( configAttribute: "ROLE_USER,ROLE_ADMIN", url: "/session/**" )
  Requestmap.findOrSaveWhere( configAttribute: "ROLE_ADMIN", url: "/runscript/**" )

  Requestmap.findOrSaveWhere( configAttribute: "ROLE_ADMIN", url: "/configSettings/**" )
  Requestmap.findOrSaveWhere( configAttribute: "ROLE_ADMIN", url: "/federation/admin/**" )
  Requestmap.findOrSaveWhere( configAttribute: "ROLE_ADMIN", url: "/federation/reconnect/**" )
}


def foo = ['rasterEntry', 'videoDataSet']

foo.each { bar ->
  def searchTagDomainClass = grailsApplication.getArtefactByLogicalPropertyName( 'Domain', "${bar}SearchTag" ).clazz

  if ( searchTagDomainClass.count() == 0 )
  {
    def searchTagData = grailsApplication.config."${bar}".searchTagData

    searchTagData.each {
      searchTagDomainClass.findOrSaveWhere( name: it.name, description: it.description )
    }
  }
}

if ( ChipFormat.count() == 0 )
{
  def chips = [
      [label: "Large 4X3", width: 800, height: 600, comment: "Temporary"],
      [label: "PowerPoint 1", width: 976, height: 780, comment: "NGA analyst recommended"]
  ]

  chips.each {
    ChipFormat.findOrSaveWhere( it )
  }
}

if ( Repository.count() == 0 )
{
  def baseDirs

  if ( GrailsUtil.isDevelopmentEnv() )
  {
    baseDirs = ["/", "/data/uav", "/Volumes/Iomega_HDD/data"]
  }
  else
  {
    baseDirs = ["/"]
  }

  baseDirs.each { baseDir ->
    Repository.findOrSaveWhere( baseDir: baseDir )
  }
}