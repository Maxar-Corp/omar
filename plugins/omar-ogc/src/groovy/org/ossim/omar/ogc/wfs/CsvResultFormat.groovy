package org.ossim.omar.ogc.wfs

import au.com.bytecode.opencsv.CSVWriter
import geoscript.filter.Filter

/**
 * Created with IntelliJ IDEA.
 * User: sbortman
 * Date: 2/25/13
 * Time: 9:16 AM
 * To change this template use File | Settings | File Templates.
 */
class CsvResultFormat implements ResultFormat
{
  def grailsApplication

  def name = "CSV"
  def contentType = 'text/csv'


  def getFeature(def wfsRequest, def workspace)
  {
    def fields
    def labels
    def formatters
    def typeName = wfsRequest?.typeName.toLowerCase();

    if ( typeName == "raster_entry" )
    {
      fields = grailsApplication.config.export.rasterEntry.fields
      labels = grailsApplication.config.export.rasterEntry.labels
      formatters = grailsApplication.config.export.rasterEntry.formatters
    }
    else if ( typeName == "video_data_set" )
    {
      fields = grailsApplication.config.export.videoDataSet.fields
      labels = grailsApplication.config.export.videoDataSet.labels
      formatters = grailsApplication.config.export.videoDataSet.formatters
    }

    def layer = workspace[wfsRequest?.typeName]
    def filter = [
        filter: wfsRequest?.filter ?: Filter.PASS,
        //sort: ""// [["<COLUMN NAME>","ASC|DESC"]]
    ]
    def filterParams = [
        filter: wfsRequest?.filter ?: Filter.PASS,
        max: wfsRequest.maxFeatures ?: -1,
        start: wfsRequest?.offset ?: -1,
        //sort: [["<COLUMN NAME>","ASC|DESC"]]
    ]
    if ( wfsRequest.sortBy )
    {
      filterParams.sort = wfsRequest.convertSortByToArray();
    }

    def stringBuffer = new StringWriter()
    def csvWriter = new CSVWriter( stringBuffer )

    csvWriter.writeNext( labels as String[] )

    def cursor = layer.getCursor( filterParams )
    while ( cursor?.hasNext() )
    {
      def feature = cursor.next();
      def data = []
      for ( field in fields )
      {
        def adjustedField = field.replaceAll( "[a-z][A-Z]", { v -> "${v[0]}_${v[1].toLowerCase()}" } )

        if ( formatters && formatters[field] )
        {
          data << formatters[field].call( feature[adjustedField] )
        }
        else
        {
          data << feature[adjustedField]
        }
      }

      csvWriter.writeNext( data as String[] )

    }
    csvWriter.close()
    cursor?.close()
    workspace?.close()

    return [stringBuffer.toString(), contentType];
  }
}
