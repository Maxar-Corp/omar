package org.ossim.omar
import grails.converters.JSON

class JobController {

    def index() {
    	render view: 'index', model: [:]
    }

    def list() {

    }

    def get() {
    	def result = Job.findByJobId(params.jobId)
    	
    	render contentType: 'application/json', text: (result?.toMap() as JSON).toString()
    }
}