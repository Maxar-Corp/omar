package org.ossim.omar

class Job {
  String    jobId
  String    type
  String    jobDir
  String    name
  String    username
  JobStatus status
  String    statusMessage
  String    message
  Double    percentComplete
  Date      submitDate
  Date      startDate
  Date      endDate
  static mapping = {
    version false
    jobId  index:"job_jobid_idx"
    jobDir type:'text', index:"job_jobdir_idx"
    status index:"job_status_idx"
    name   index:"job_name_idx"
    username index:"job_username_idx"
    message   type:"text"
    submitDate index:"job_submit_date_idx"
    startDate index:"job_start_date_idx"
    endDate index:"job_end_date_idx"
  }
  static constraints = {
    jobId           nullable:false, unique:true
    jobDir          nullable:true
    type            nullable:true
    name            nullable:true
    username        nullable:false
    status          nullable:true
    statusMessage   nullable:true
    message         nullable:true
    percentComplete nullable:true
    submitDate      nullable:true
    startDate       nullable:true
    endDate         nullable:true
  }
  def toMap(){
    [jobId:jobId, jobType:jobType, status:status.toString(), statusMessage:statusMessage, data:data, percentComplete:percentComplete, submitDate:submitDate,
     startDate:startDate, endDate:endDate]
  }
}