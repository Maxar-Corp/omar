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
  String    jobCallback
  Double    percentComplete
  Date      submitDate
  Date      startDate
  Date      endDate
  static mapping = {
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
    jobCallback     nullable:true
    percentComplete nullable:true
    submitDate      nullable:true
    startDate       nullable:true
    endDate         nullable:true
  }
  def toMap(){
    [jobId:jobId,
     jobDir:jobDir,
     jobType:type,
     name:name,
     username:username,
     status:status.toString(),
     statusMessage:statusMessage,
     message:message,
     jobCallback:jobCallback,
     percentComplete:percentComplete,
     submitDate:submitDate,
     startDate:startDate,
     endDate:endDate]
  }

  def getArchive(){
    def result

    if(jobDir)
    {
      def testArchive = "${jobDir}.zip" as File

      if(testArchive.exists()){
        result = testArchive
      }
      else
      {
        testArchive = "${jobDir}.tgz" as File
        if(testArchive.exists()){
          result = testArchive
        }
      }

    }

    result
  }
}