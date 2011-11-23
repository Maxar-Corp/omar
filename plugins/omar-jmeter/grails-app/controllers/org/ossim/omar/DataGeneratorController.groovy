package org.ossim.omar

class DataGeneratorController {

  def rasterEntries = {
    def rasterList = RasterEntry.list()
    def out = new StringBuffer()
    rasterList.each{x ->
          def bounds = x.groundGeom.bounds;
          out.append( """${x.id},"${bounds.minLon},${bounds.minLat},${bounds.maxLon},${bounds.maxLat}"\n""")
      }
   render(contentType:"text/plain",text:out.toString());
  }
  def videoEntries = {
      def videoList = VideoDataSet.list()
      def out = new StringBuffer()
      videoList.each{x ->
          def bounds = x.groundGeom.bounds;
          out.append( """${x.id},"${bounds.minLon},${bounds.minLat},${bounds.maxLon},${bounds.maxLat}"\n""")
      }
      render(contentType:"text/plain",text:out.toString());
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
