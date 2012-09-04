package org.ossim.omar.ogc

import org.springframework.dao.DataIntegrityViolationException
import org.hibernate.CacheMode

class WmsLogController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
    def sql
    def sessionFactory
    def index() {
        redirect(action: "list", params: params)
    }
    def clear(){
        try{
            sql.execute("delete from wms_log;");
        }
        catch(def e)
        {
            log.info(e)
        }
        // we are executing a raw sql don't let hibernate read from cache
        // force a reload for this session
        //
        sessionFactory.evictQueries()

        redirect(action: "list", params: params)
    }
    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        params.order = params.order?:"desc"
        params.sort = params.sort?:"startDate"
        params.offset = params.offset?:"0"
        def wmsLogInstanceList = WmsLog.list(params)
        def wmsLogInstanceTotal = WmsLog.count()
        if (!wmsLogInstanceTotal)
        {
            flash.message = "WMS log is empty"
        }

        [wmsLogInstanceList: wmsLogInstanceList, wmsLogInstanceTotal:wmsLogInstanceTotal ]
    }

    def create() {
        [wmsLogInstance: new WmsLog(params)]
    }

    def save() {
        def wmsLogInstance = new WmsLog(params)
        if (!wmsLogInstance.save(flush: true)) {
            render(view: "create", model: [wmsLogInstance: wmsLogInstance])
            return
        }

		flash.message = message(code: 'default.created.message', args: [message(code: 'wmsLog.label', default: 'WmsLog'), wmsLogInstance.id])
        redirect(action: "show", id: wmsLogInstance.id)
    }

    def show() {
        def wmsLogInstance = WmsLog.get(params.id)
        if (!wmsLogInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'wmsLog.label', default: 'WmsLog'), params.id])
            redirect(action: "list")
            return
        }

        [wmsLogInstance: wmsLogInstance]
    }

    def edit() {
        def wmsLogInstance = WmsLog.get(params.id)
        if (!wmsLogInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'wmsLog.label', default: 'WmsLog'), params.id])
            redirect(action: "list")
            return
        }

        [wmsLogInstance: wmsLogInstance]
    }

    def update() {
        def wmsLogInstance = WmsLog.get(params.id)
        if (!wmsLogInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'wmsLog.label', default: 'WmsLog'), params.id])
            redirect(action: "list")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (wmsLogInstance.version > version) {
                wmsLogInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'wmsLog.label', default: 'WmsLog')] as Object[],
                          "Another user has updated this WmsLog while you were editing")
                render(view: "edit", model: [wmsLogInstance: wmsLogInstance])
                return
            }
        }

        wmsLogInstance.properties = params

        if (!wmsLogInstance.save(flush: true)) {
            render(view: "edit", model: [wmsLogInstance: wmsLogInstance])
            return
        }

		flash.message = message(code: 'default.updated.message', args: [message(code: 'wmsLog.label', default: 'WmsLog'), wmsLogInstance.id])
        redirect(action: "show", id: wmsLogInstance.id)
    }

    def delete() {
        def wmsLogInstance = WmsLog.get(params.id)
        if (!wmsLogInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'wmsLog.label', default: 'WmsLog'), params.id])
            redirect(action: "list")
            return
        }

        try {
            wmsLogInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'wmsLog.label', default: 'WmsLog'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'wmsLog.label', default: 'WmsLog'), params.id])
            redirect(action: "show", id: params.id)
        }
    }
}
