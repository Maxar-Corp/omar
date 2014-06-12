import groovy.sql.Sql
import org.ossim.omar.security.SecRole
import org.ossim.omar.security.SecUser
import org.ossim.omar.security.SecUserSecRole
import org.ossim.omar.security.Requestmap

import org.ossim.omar.core.Repository
import org.ossim.omar.ChipFormat

import grails.util.GrailsUtil


def springSecurityService = ctx.springSecurityService





def ds=grailsApplication.config.dataSource
def sql = Sql.newInstance(ds.url,
        ds.username,
        ds.password, ds.driverClassName)





