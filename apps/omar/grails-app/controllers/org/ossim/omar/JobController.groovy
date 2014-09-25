package org.ossim.omar


class JobController {

    def index() {
    }

    def list() {

    }

    def get() {
    	def result = Job.findByJobId(params.jobId)
    	
    	render contentType: 'application/json', text: result.toString()
    }
}