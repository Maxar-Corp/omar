import joms.oms.Video

class ThumbnailService
{
  boolean transactional = false
  static int rasterFileOutputLock
  static int videoFrameOutputLock
  def grailsApplication

  def getThumbnail(String cacheDirPath, String thumbnailPrefix, int size, String mimeType,
                   String inputFilename, String entryId, String projectionType, boolean overwrite = false)
  {
    def outputFile = new File(
        cacheDirPath,
        "${thumbnailPrefix}.jpg"
    )

    def histogramStretchType = "linear_auto_min_max"

    // for now we only support imagespace thumbnails
    //
    if ( projectionType != "imagespace" )
    {
      projectionType = "imagespace"
    }

    if ( !outputFile.exists() || overwrite )
    {
      synchronized(rasterFileOutputLock)
      {
        def stretchTypeToUse = histogramStretchType
        ThumbnailGenerator.writeImageSpaceThumbnail(
            inputFilename,
            entryId,
            outputFile as String,
            mimeType,
            size, size,
            "", // use default
            stretchTypeToUse, true)
       }
    }

    return outputFile
  }

  def getFrame(String cacheDirPath, String thumbnailPrefix, int size, String inputFilename, boolean overwrite = false)
  {
    def outputFile = new File(cacheDirPath, "${thumbnailPrefix}.jpg")

    if ( !outputFile.exists() || overwrite )
    {
      Video video = new Video()

      if ( video.open(inputFilename) )
      {
        video.nextFrame();
        synchronized(videoFrameOutputLock)
        {
          video.writeCurrentFrameToFile(outputFile.absolutePath, size);
        }
        video.close()
        video = null;
      }
    }

    return outputFile
  }

  public File getRasterEntryThumbnailFile(RasterEntry rasterEntry, Map params)
  {
    def projectionType = params.projectionType;
    RasterDataSet rasterDataSet = rasterEntry.rasterDataSet
    RasterFile rasterFile = RasterFile.findWhere(rasterDataSet: rasterDataSet, type: "main")
    def size = params.size?.toInteger()
    def mimeType = params?.mimeType ?: "image/jpeg"
    boolean overwrite = params.overwrite ?: false

    if ( !size )
    size = grailsApplication.config.thumbnail.defaultSize

    String cacheDirPath = grailsApplication.config.thumbnail.cacheDir
    String thumbnailPrefix = "${rasterEntry.id}-${size}-${projectionType}"

    File outputFile = this.getThumbnail(
        cacheDirPath,
        thumbnailPrefix,
        size,
        mimeType,
        rasterFile.name,
        rasterEntry.entryId,
        projectionType,
        overwrite
    )
    return outputFile
  }

  public File getVideoDataSetThumbnailFile(VideoDataSet videoDataSet, Map params)
  {
    VideoFile videoFile = VideoFile.findWhere(videoDataSet: videoDataSet, type: "main")
    def size = params.size?.toInteger() ?: grailsApplication.config.thumbnail.defaultSize
    String cacheDirPath = grailsApplication.config.thumbnail.cacheDir
    String thumbnailPrefix = "${videoDataSet.id}-${size}"
    boolean overwrite = params.overwrite ?: false
    File outputFile = this.getFrame(cacheDirPath, thumbnailPrefix, size, videoFile.name, overwrite)

    return outputFile
  }
}
