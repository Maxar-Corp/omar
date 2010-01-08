class FileListing implements FileProcessor
{
  List files = []
  def maxFiles=999999999
  public boolean process(File fileOrDirectory)
  {
    def result = false
    if(files.size() < maxFiles)
    {
      files.add(fileOrDirectory)
      result = true
    }

    return result;
  }
  void setMaxFiles(int max)
  {
    maxFiles = max
  }
  int getMaxFiles()
  {
    return maxFiles;
  }
  List getFiles()
  {
    return files;
  }
  void clearFiles()
  {
    files.clear();
  }
}