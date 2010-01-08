import joms.oms.DataInfo

class DataManagerService
{

  boolean transactional = true

  def addRaster(def filename)
  {
    def status = false

    def file = filename as File

    if ( file?.exists() )
    {
      def xml = StagerUtil.getInfoSynchronized(file)

      if ( xml )
      {
        def oms = new XmlSlurper().parseText(xml)
        def omsInfoParser = new OmsInfoParser()
        def repository = findRepositoryForFile(file)
        def rasterDataSets = omsInfoParser.processRasterDataSets(oms, repository)

        rasterDataSets.each {rasterDataSet ->
          if ( rasterDataSet.save() )
          {
            //stagerHandler.processSuccessful(file, xml)
            status = true
          }
          else
          {
            //stagerHandler.processRejected(file)
          }

          //new StagerQueueItem(file: file.absolutePath, baseDir: parent.baseDir, dataInfo: xml).save()
        }
      }            
    }
    
    return status
  }

  def addVideo(def filename)
  {
    def status = false
    def file = filename as File

    if ( file?.exists() )
    {
      def dataInfo = new DataInfo()
      def canOpen = dataInfo.open(file.absolutePath)

      if ( canOpen )
      {
        def xml = dataInfo.getInfo()?.trim()
        dataInfo.close()
        if ( xml )
        {
          def oms = new XmlSlurper().parseText(xml)
          def omsInfoParser = new OmsInfoParser()
          def repository = findRepositoryForFile(file)
          def videoDataSets = omsInfoParser.processVideoDataSets(oms, repository)

          videoDataSets.each {videoDataSet ->
            if ( videoDataSet.save() )
            {
              //stagerHandler.processSuccessful(file, xml)
              status = true
            }
            else
            {
              //stagerHandler.processRejected(file)
            }

            //new StagerQueueItem(file: file.absolutePath, baseDir: parent.baseDir, dataInfo: xml).save()
          }
        }
      }

      dataInfo.close()
      dataInfo.delete();
    }

    return status
  }

  def removeRaster(def filename)
  {
    def status = false
    def file = filename as File

    if ( file?.exists() )
    {
      def rasterFile = RasterFile.findByNameAndType(file.absolutePath, "main")

      if ( rasterFile )
      {
        rasterFile?.rasterDataSet.delete(flush: true)
        status = true
      }
    }

    return status
  }

  def removeVideo(def filename)
  {
    def status = false
    def file = filename as File

    if ( file?.exists() )
    {
      def videoFile = VideoFile.findByNameAndType(file.absolutePath, "main")

      if ( videoFile )
      {
        videoFile?.videoDataSet.delete(flush: true)
        status = true
      }
    }

    return status
  }

  synchronized def findRepositoryForFile(def file)
  {
    def repositories = (Repository.list()?.sort { it.baseDir.size() })?.reverse()
    def repository = null

    if ( repositories )
    {
      def filename = file?.absolutePath

      for ( it in repositories )
      {
        if ( filename?.startsWith(it.baseDir) )
        {
          repository = it
          break
        }
      }
    }

    if ( !repository )
    {
      repository = new Repository(baseDir: file?.parentFile?.absolutePath)
      repository.save(flush: true)
    }

    return repository
  }
}
