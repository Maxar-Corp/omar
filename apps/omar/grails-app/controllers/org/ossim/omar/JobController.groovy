package org.ossim.omar
import grails.converters.JSON
import org.ossim.omar.Job
import org.ossim.omar.chipper.FetchDataCommand

class JobController {
  def jobService
  def index() {
    render view: 'index', model:[
                tableModel  : jobService.createTableModel()
           ]
  }
  def getData(FetchDataCommand cmd)
  {
    def data = jobService.getData( cmd )

    render contentType: 'application/json', text: data as JSON
  }

  def get() {
    def result = jobService.getByJobId(params?.jobId)

    render contentType: 'application/json', text: (result?.toMap() as JSON).toString()
  }
}