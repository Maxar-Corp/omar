class ImageDetector implements FileProcessor
{
  //def images = []
  List<FileFilterEventListener> fileFilterEventListeners = []

  @Override
  boolean process(File fileOrDirectory)
  {
    if ( fileOrDirectory.isFile() )
    {
      //def info = ImageInfo.getInfo(fileOrDirectory)
      def info = StagerUtil.getInfo(fileOrDirectory)

      if ( info /*&& info["number_entries"] != "0"*/ )
      {
        //images << fileOrDirectory
        fileFilterEventListeners.each { it.processSuccess(new FileFilterEvent(this, fileOrDirectory))}
        fileFilterEventListeners.each { it.processData(new FileFilterEvent(this, fileOrDirectory, info))}
      }
      else
      {
        fileFilterEventListeners.each { it.processFailure(new FileFilterEvent(this, fileOrDirectory))}
      }
    }
    else if ( fileOrDirectory.isDirectory() )
    {
      fileFilterEventListeners.each { it.processFailure(new FileFilterEvent(this, fileOrDirectory))}
    }

    return true
  }

  public synchronized addFileFilterEventListener(FileFilterEventListener listener)
  {
    fileFilterEventListeners.add(listener)
  }

  public synchronized removeFileFilterEventListener(FileFilterEventListener listener)
  {
    fileFilterEventListeners.remove(listener)
  }

//  @Override
//  public String toString()
//  {
//    return "${images} ${images.size()}"
//  }
}