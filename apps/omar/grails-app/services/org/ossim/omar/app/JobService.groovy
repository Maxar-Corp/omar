package org.ossim.omar.app

import org.ossim.omar.Job
import grails.converters.JSON
import org.ossim.omar.JobStatus

class JobService { 

	def getAsJSON(def params) {
		def record = job.findByJobId(params.jobId)
		record.toMap() as JSON
	}

  def updateJob(def jsonObj) {

    try{
      if(jsonObj.jobId != null)
      {

       // println "json"

        Job.withTransaction{
          def record = Job.findByJobId(jsonObj.jobId)
          if(record)
          {
            println "WILL UPDATE ${jsonObj.jobId} WITH NEW STATUS === ${jsonObj.status}"

            def status = "${jsonObj.status?.toUpperCase()}"
            record.statusMessage = jsonObj.statusMessage

            record.status  = JobStatus."${status}"
            switch(record.status)
            {
              case JobStatus.READY:
                record.submitDate = new Date()
                break
              case JobStatus.CANCELED:
              case JobStatus.FINISHED:
              case JobStatus.FAILED:
                record.endDate = new Date()
                break
              case JobStatus.RUNNING:
                record.startDate = new Date()
                break
            }

            record.percentComplete = jsonObj.percentComplete
            record.save()
          }
          else
          {
            println "Job ID not found: ${jsonObj.jobId}"
          }
        }
      }
      else
      {
        println "Job ID NULL????????????????????????? ${jsonObj.jobId}"
      }
    }
    catch(e)
    {
      println "ERROR!!!!!!!!!!!!!!!!!! ${e}"
    }
  }

}