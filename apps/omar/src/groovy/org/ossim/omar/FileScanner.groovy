package org.ossim.omar

class FileScanner
{
  public static final String DIRECTORY = "DIRECTORY"
  public static final String FILE = "FILE"
  public static final String UNKNOWN = "UNKNOWN"

  // Process all files and directories under dir

  static void visitAllDirsAndFiles(File fileOrDirectory, FileFilter filter, FileProcessor processor)
  {
    processor?.process(fileOrDirectory)

    if ( fileOrDirectory.isDirectory() )
    {
      List<File> fileList = (filter) ? fileOrDirectory.listFiles(filter) : fileOrDirectory.listFiles()
      Map<String, List<File>> filesByType = groupByType(fileList)

      //  Do files first in sorted order
      (filesByType[FILE]?.sort { it.name })?.each {file -> processor.process(file)}

      // then do directories in sorted order
      (filesByType[DIRECTORY]?.sort { it.name })?.each {directory ->
        if ( processor?.process(directory) )
        {
          List<File> children = (filter) ? directory.listFiles(filter) : directory.listFiles()
          for ( def child in children )
          {
            visitAllDirsAndFiles(child, filter, processor)
          }
        }
      }
    }
  }

  // Process only directories under dir

  static void visitAllDirs(File fileOrDirectory, FileFilter filter, FileProcessor processor)
  {
    if ( fileOrDirectory.isDirectory() && processor?.process(fileOrDirectory) )
    {
      List<File> children = (filter) ? fileOrDirectory.listFiles(filter) : fileOrDirectory.listFiles()
      Map<String, List<File>> filesByType = groupByType(children)

      // Only send directories, ignore files
      for ( def child in filesByType[DIRECTORY]?.sort { it.name} )
      {
        visitAllDirs(child, filter, processor)
      }
    }
  }

  // Process only files under dir

  static void visitAllFiles(File fileOrDirectory, FileFilter filter, FileProcessor processor)
  {
    if ( fileOrDirectory.isDirectory() )
    {
      List<File> children = (filter) ? fileOrDirectory.listFiles(filter) : fileOrDirectory.listFiles()
      Map<String, List<File>> filesByType = groupByType(children)

      //  Do files first in sorted order
      (filesByType[FILE]?.sort { it.name })?.each {file -> visitAllFiles(file, filter, processor)}

      // then do directories in sorted order
      (filesByType[DIRECTORY]?.sort { it.name })?.each {directory -> visitAllFiles(directory, filter, processor)}
    }
    else
    {
      processor?.process(fileOrDirectory)
    }
  }

  // Group file list by type category ( File, Directory, etc )

  static Map<String, List<File>> groupByType(List<File> files)
  {
    def filesByType = files.groupBy {
      if ( it.isFile() )
      {
        return FILE
      }
      else if ( it.isDirectory() )
      {
        return DIRECTORY
      }
      else
      {
        return UNKNOWN
      }
    }

    return filesByType
  }

  public static void main(String[] args)
  {
    if ( args.size() == 2 )
    {
      joms.oms.Init.instance().initialize()
      
      println new Date()
//  def processor = new FileCounter()
      def filter = new ImageFileFilter()
      def processor = new ImageDetector()
      def logger = new StagerEventHandler()

      filter.addFileFilterEventListener(logger)
      processor.addFileFilterEventListener(logger)

      switch ( args[0] )
      {
        case "visitAllDirsAndFiles":
          FileScanner.visitAllDirsAndFiles(args[1] as File, filter, processor)
          break
        case "visitAllDirs":
          FileScanner.visitAllDirs(args[1] as File, filter, processor)
          break
        case "visitAllFiles":
          FileScanner.visitAllFiles(args[1] as File, filter, processor)
          break
      }

      println new Date()
    }
    else
    {
      println "Usage: org.ossim.omar.FileScanner <visitAllDirsAndFiles|visitAllDirs|visitAllFiles> <file or directory>"
    }
  }
}

