package org.ossim.omar
import grails.converters.JSON
import org.ossim.omar.Job

class JobController {

    def index() {
    	render view: 'index', model: [:]
    }

    def list() {
      println params
      def result = [total:10, rows:[]]
      def offset = 0
      def rows=10
      def page = 0
      if(params.page) page = params.page as Integer
      if(params.rows) rows = params.rows as Integer
      offset = (page-1) * rows
      if(offset < 0) offset = 0
   //   println Job.count()
      Job.withTransaction{
        result.total = Job.count()
        def c = Job.createCriteria()

        def results = c.list(max: rows, offset: offset) {
 //         like("holderFirstName", "Fred%")
 //         and {
 //           between("balance", 500, 1000)
 //           eq("branch", "London")
 //         }
          order(params.sort?:"submitDate", params.order?:"desc")
        }
        results.each{job->
          result.rows << job.toMap()
        }
      }
     //            println    (result as JSON).toString()
      render contentType: 'application/json', text: (result as JSON).toString()
    }

    def get() {
    	def result = Job.findByJobId(params.jobId)
    	
    	render contentType: 'application/json', text: (result?.toMap() as JSON).toString()
    }
}