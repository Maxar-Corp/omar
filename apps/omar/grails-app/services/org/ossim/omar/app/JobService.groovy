package org.ossim.omar.app

import org.ossim.omar.Job
import grails.converters.JSON
import org.ossim.omar.JobStatus
import org.ossim.omar.chipper.FetchDataCommand

class JobService {
  def springSecurityService
  def grailsApplication
  def diskCacheService
  static columnNames = [
          'id','jobId', 'type', 'name', 'username', 'status', 'statusMessage', 'percentComplete', 'submitDate', 'startDate', 'endDate'
  ]

  def createTableModel()
  {
    def clazz = Job.class
    def domain = grailsApplication.getDomainClass( clazz.name )

    def columns = columnNames?.collect {column->
      def property = ( column == 'id' ) ? domain?.identifier : domain?.getPersistentProperty( column )
      def sortable = !(property?.name in ["type"])
      [field: property?.name, type: property?.type, title: property?.naturalName, sortable: sortable]
    }

    def tableModel = [
            columns: [columns]
    ]
   // println tableModel
    return tableModel
  }

  def remove(def params)
  {
    def result = [success:false]

    try{
      DiskCache.withTransaction {
        def row

        if(params?.id != null) row = Job.findById(params?.id?.toInteger());
        else if(params?.jobId) row = Job.findByJobId(params.jobId);

        if(row)
        {
          row.delete()
          result.success = true;
        }
      }
    }
    catch(e)
    {
      result.success = false;
      result.message = e.toString()
    }

    result;
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
           // println "WILL UPDATE ${jsonObj.jobId} WITH NEW STATUS === ${jsonObj.status}"

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
  def getByAllJobIds(def jobIds)
  {
    def splitArray = jobIds.split(",")
    def rows
    println "(${splitArray.collect{"'${it}'" }.join(',')}"
    Job.withTransaction{
      def tempRows = Job.withCriteria {
        sqlRestriction "(job_id in (${splitArray.collect{"'${it}'" }.join(',')}))"
      }
      rows = tempRows.collect { row ->
        columnNames.inject( [:] ) { a, b -> a[b] = row[b].toString(); a }
      }
    }
    [total: rows.length, rows: rows]
  }
  def getByJobId(def jobId)
  {
    def result = [:]

    if(jobId)
    {
      Job.withTransaction{
        result = Job.findByJobId(jobId)
      }
    }

    result
  }
  def getData(FetchDataCommand cmd)
  {

    //println params

//    def max = ( params?.rows as Integer ) ?: 10
//    def offset = ( ( params?.page as Integer ?: 1 ) - 1 ) * max
//    def sort = params?.sort ?: 'id'
//    def dir = params?.order ?: 'asc'
//    def x = [max: max, offset: offset, sort: sort, dir: dir]
//
//    println x
    def total = 0
    def rows  = [:]
    Job.withTransaction{
       total = Job.createCriteria().count {
        if ( cmd.filter )
        {
          sqlRestriction cmd.filter
        }
        }

        def tempRows = Job.withCriteria {
          if ( cmd.filter )
          {
            sqlRestriction cmd.filter
          }
  //      projections {
  //        columnNames.each {
  //          property(it)
  //        }
  //      }
          maxResults( cmd.rows )
          order( cmd.sort, cmd.order )
          firstResult( ( cmd.page - 1 ) * cmd.rows )
        }
        rows = tempRows.collect { row ->
          columnNames.inject( [:] ) { a, b -> a[b] = row[b].toString(); a }
        }
    }

    return [total: total, rows: rows]
  }

}