package org.ossim.omar.video

import org.ossim.omar.core.DateUtil

class VideoDataSetExportController
{
  def exportService
  def videoDataSetSearchService
  def grailsApplication

  def index( )
  {
  }

  def export( )
  {
    def format = params.format
    def queryParams = new VideoDataSetQuery()

    bindData( queryParams, params )

    queryParams.startDate = DateUtil.initializeDate( "startDate", params )
    queryParams.endDate = DateUtil.initializeDate( "endDate", params )

    def objects = videoDataSetSearchService.runQuery( queryParams, params )

//    def fields = ["id", "acquisitionDate", "groundGeom"] as String[]
//    def labels = ["id", "acquisition_date", "ground_geom"] as String[]

//    def domainClass = grailsApplication.getArtefact("Domain", "org.ossim.omar.video.VideoDataSet")
//    def fields = domainClass.properties*.name
//    def labels = domainClass.properties*.naturalName

    def fields = grailsApplication.config.export.videoDataSet.fields
    def labels = grailsApplication.config.export.videoDataSet.labels
    def formatters = grailsApplication.config.export.videoDataSet.formatters

    def (file, mimeType) = exportService.export(
            format,
            objects,
            fields,
            labels,
            formatters,
            [featureClass: VideoDataSet.class]
    )

    response.setHeader( "Content-disposition", "attachment; filename=${file.name}" );
    response.contentType = mimeType
    response.outputStream << file.newInputStream()
    response.outputStream.flush()
  }

}
