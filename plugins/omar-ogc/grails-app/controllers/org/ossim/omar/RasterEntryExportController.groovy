package org.ossim.omar


class RasterEntryExportController
{
  def exportService
  def rasterEntrySearchService

  def index = {
  }

  def export = {
    def format = params.format
    def queryParams = new RasterEntryQuery()

    bindData(queryParams, params)

    queryParams.startDate = DateUtil.initializeDate("startDate", params)
    queryParams.endDate = DateUtil.initializeDate("endDate", params)

    def objects = rasterEntrySearchService.runQuery(queryParams, params)

    def fields = ["id", "acquisitionDate", "groundGeom"] as String[]
    def labels = ["id", "acquisition_date", "ground_geom"] as String[]

    def formatters = [
            groundGeom: { def bounds = it.envelopeInternal; [bounds.minX, bounds.minY, bounds.maxX, bounds.maxY].join(',') }
    ]

    def (file, mimeType) = exportService.export(
            format,
            objects,
            fields,
            labels,
            formatters,
            [featureClass: RasterEntry.class]
    )

    response.setHeader("Content-disposition", "attachment; filename=${file.name}");
    response.contentType = mimeType
    response.outputStream << file.newInputStream()
    response.outputStream.flush()
  }
}
