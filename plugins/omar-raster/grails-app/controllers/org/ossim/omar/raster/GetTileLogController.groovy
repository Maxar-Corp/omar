package org.ossim.omar.raster

import org.springframework.dao.DataIntegrityViolationException
import groovy.sql.Sql
import org.hibernate.criterion.Order
import org.hibernate.criterion.Projections
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.ossim.omar.security.SecUser

class GetTileLogController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
    def sql;
    def sessionFactory
    def springSecurityService
    def index() {
        redirect(action: "list", params: params)
    }

    def clear(){
        if ( SpringSecurityUtils.ifAllGranted( "ROLE_ADMIN" ) )
        {
            GetTileLog.executeUpdate("DELETE FROM GetTileLog")
        }

        redirect(action: "list", params: params)
        //render (view: "list",
        //        model:[getTileLogInstanceList: GetTileLog.list(params),
        //                getTileLogInstanceTotal: GetTileLog.count()])
    }
    def list() {
        //println params
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        params.order = params.order?:"desc"
        params.sort = params.sort?:"startDate"
        params.offset = params.offset?:"0"
        def getTileLogInstanceList
        def getTileLogInstanceTotal
        if ( SpringSecurityUtils.ifAllGranted( "ROLE_ADMIN" ) )
        {

            getTileLogInstanceList  = GetTileLog.list(params)
            getTileLogInstanceTotal = GetTileLog.count()
        }
        else if (springSecurityService?.principal?.username)
        {
            def username = springSecurityService?.principal?.username
            if (username)
            {
                getTileLogInstanceList = GetTileLog.createCriteria().list( params ) {
                    eq( "userName", username )
                }
                getTileLogInstanceTotal = getTileLogInstanceList.totalCount
            }
            else
            {
                getTileLogInstanceList = []
                getTileLogInstanceTotal = 0
            }
        }

        if (!getTileLogInstanceTotal)
        {
            flash.message = "Image space chipping log is empty"
        }
/*
        def crit = GetTileLog.createCriteria()
        def result = crit.get{
            projections{
                avg "internalTime"
                avg "renderTime"
                avg "totalTime"
            }
            //order("${params.order}", "${params.sort}")
                //avg("internalTime")
        }
        println result;
        */
        [getTileLogInstanceList: getTileLogInstanceList,
         getTileLogInstanceTotal: getTileLogInstanceTotal]
    }

/*
    def create() {
        [getTileLogInstance: new GetTileLog(params)]
    }

    def save() {
        def getTileLogInstance = new GetTileLog(params)
        if (!getTileLogInstance.save(flush: true)) {
            render(view: "create", model: [getTileLogInstance: getTileLogInstance])
            return
        }

		flash.message = message(code: 'default.created.message', args: [message(code: 'getTileLog.label', default: 'GetTileLog'), getTileLogInstance.id])
        redirect(action: "show", id: getTileLogInstance.id)
    }
   */
    def show() {
        def getTileLogInstance = GetTileLog.get(params.id)
        if (!getTileLogInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'getTileLog.label', default: 'GetTileLog'), params.id])
            redirect(action: "list")
            return
        }

        [getTileLogInstance: getTileLogInstance]
    }
/*
    def edit() {
        def getTileLogInstance = GetTileLog.get(params.id)
        if (!getTileLogInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'getTileLog.label', default: 'GetTileLog'), params.id])
            redirect(action: "list")
            return
        }

        [getTileLogInstance: getTileLogInstance]
    }

    def update() {
        def getTileLogInstance = GetTileLog.get(params.id)
        if (!getTileLogInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'getTileLog.label', default: 'GetTileLog'), params.id])
            redirect(action: "list")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (getTileLogInstance.version > version) {
                getTileLogInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'getTileLog.label', default: 'GetTileLog')] as Object[],
                          "Another user has updated this GetTileLog while you were editing")
                render(view: "edit", model: [getTileLogInstance: getTileLogInstance])
                return
            }
        }

        getTileLogInstance.properties = params

        if (!getTileLogInstance.save(flush: true)) {
            render(view: "edit", model: [getTileLogInstance: getTileLogInstance])
            return
        }

		flash.message = message(code: 'default.updated.message', args: [message(code: 'getTileLog.label', default: 'GetTileLog'), getTileLogInstance.id])
        redirect(action: "show", id: getTileLogInstance.id)
    }

    def delete() {
        def getTileLogInstance = GetTileLog.get(params.id)
        if (!getTileLogInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'getTileLog.label', default: 'GetTileLog'), params.id])
            redirect(action: "list")
            return
        }

        try {
            getTileLogInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'getTileLog.label', default: 'GetTileLog'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'getTileLog.label', default: 'GetTileLog'), params.id])
            redirect(action: "show", id: params.id)
        }
    }
 */
}
