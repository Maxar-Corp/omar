package org.ossim.omar.raster

import org.ossim.omar.core.DateUtil

class RasterEntryExportController
{
  def exportService
  def rasterEntrySearchService
  def grailsApplication

  def index( )
  {
  }

  def export( )
  {
      def format = params.format
    def queryParams = new RasterEntryQuery()
    bindData( queryParams, params )

    queryParams.startDate = DateUtil.initializeDate( "startDate", params )
    queryParams.endDate = DateUtil.initializeDate( "endDate", params )

    def objects = rasterEntrySearchService.runQuery( queryParams, params )

//    def fields = ["id", "acquisitionDate", "groundGeom"] as String[]
//    def labels = ["id", "acquisition_date", "ground_geom"] as String[]

//    def domainClass = grailsApplication.getArtefact("Domain", "org.ossim.omar.raster.RasterEntry")
//    def fields = domainClass.properties*.name
//    def labels = domainClass.properties*.naturalName

    def fields = grailsApplication.config.export.rasterEntry.fields
    def labels = grailsApplication.config.export.rasterEntry.labels
    def formatters = grailsApplication.config.export.rasterEntry.formatters

    if (params.fields)
    {
        def tempFields = params?.fields?.split(",")
        fields = []
        tempFields.each{field->
            fields<<field
        }
    }
    if (params.labels)
    {
        def tempLabels = params?.labels?.split(",")
        labels = []
        tempLabels.each{label->
            labels << label;
        }
    }


    def (file, mimeType) = exportService.export(
            format,
            objects,
            fields,
            labels,
            formatters,
            [featureClass: RasterEntry.class]
    )

      if (file instanceof String)
      {
          //response.setHeader( "Content-disposition", "attachment; filename=" );
          response.contentType = mimeType
          response.outputStream << file
          response.outputStream.flush()
      }
      else
      {
          response.setHeader( "Content-disposition", "attachment; filename=${file.name}" );
          response.contentType = mimeType
          response.outputStream << file.newInputStream()
          response.outputStream.flush()
          file.delete();
      }
  }
}
