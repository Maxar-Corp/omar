package org.ossim.omar.jmeter

import org.hibernate.Criteria
import org.ossim.omar.video.VideoDataSet
import org.ossim.omar.raster.RasterEntry
import org.ossim.omar.raster.RasterEntryQuery
import org.apache.commons.collections.map.CaseInsensitiveMap

class JmeterController {
    def rasterEntrySearchService

   def setupTests = {
  }
  def rasterEntries = {
    def paramsIgnoreCase = new CaseInsensitiveMap( params )
    def max = paramsIgnoreCase.max?paramsIgnoreCase.max as Integer:1000;
    def queryParams = new RasterEntryQuery()
    def out = new StringBuffer()
    paramsIgnoreCase.max = max
    bindData( queryParams, paramsIgnoreCase )
    rasterEntries = rasterEntrySearchService.runQuery( queryParams, paramsIgnoreCase )
    rasterEntries.each{x ->
        def bounds = x.groundGeom.bounds;
        out.append("${x.id}")
        out.append(",${x.indexId}")
        out.append(",\"${bounds.minLon},${bounds.minLat},${bounds.maxLon},${bounds.maxLat}\"")
        out.append(",${x.numberOfBands}")
        out.append(",${x.bitDepth}")
        out.append(",\"${x.filename}\"")
        out.append("\n")
    }
    render(contentType:"text/csv", text:out.toString());
  }
  def videoEntries = {
      def videoList = VideoDataSet.list()
      def out = new StringBuffer()
      videoList.each{x ->
          def bounds = x.groundGeom.bounds;
          out.append( """${x.id},"${bounds.minLon},${bounds.minLat},${bounds.maxLon},${bounds.maxLat}"\n""")
      }
      render(contentType:"text/csv",text:out.toString());
  }
  def rasterFileListing = {
	/*
    def fileListing = new FileListing()
    def filter = new ImageFileFilter()
    def out = new StringBuffer()
    fileListing.setMaxFiles(params.max?params.max.toInteger():100)
    if(params.filename)
    {
      FileScanner.visitAllFiles(new File(params.filename.toString()), filter, fileListing)
      List list = fileListing.getFiles();
      list.each{x ->
        out.append(""""${x.toString()}"\n""")
      }
    }
    render(contentType:"text/plain",text:out.toString());
    */
  }
  def videoFileListing = {
	/*
    def fileListing = new FileListing()
    def filter = new ImageFileFilter()
    def out = new StringBuffer()
    fileListing.setMaxFiles(params.max?params.max.toInteger():100)
    if(params.filename)
    {
      FileScanner.visitAllFiles(new File(params.filename.toString()), filter, fileListing)
      List list = fileListing.getFiles();
      list.each{x ->
        out.append(""""${x.toString()}"\n""")
      }
    }
    render(contentType:"text/plain",text:out.toString());
    */
  }
}
