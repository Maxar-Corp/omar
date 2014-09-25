package org.ossim.omar

class Job {
        String    jobId
        String    jobType
        JobStatus status
        String    statusMessage
        String    data
        Double    percentComplete
        Date      submitDate
        Date      startDate
        Date      endDate
        static mapping = {
                jobId  index:"job_jobid_idx"
                status index:"job_status_idx"
                data   type:"text"
                submitDate index:"job_submit_date_idx"
                startDate index:"job_start_date_idx"
                endDate index:"job_end_date_idx"
        }
        static constraints = {
                jobId           nullable:false, unique:true
                jobType         nullable:true
                status          nullable:true
                statusMessage   nullable:true
                data            nullable:true
                percentComplete nullable:true
                submitDate      nullable:true
                startDate       nullable:true
                endDate         nullable:true
        }
        def toMap(){
        	[jobId:jobId, jobType:jobType, status:status, statusMessage:statusMessage, data:data, percentComplete:percentComplete, submitDate:submitDate,
        	startDate:startDate, endDate:endDate]
        }
}