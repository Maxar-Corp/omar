/**
 * Created by IntelliJ IDEA.
 * User: sbortman
 * Date: Dec 18, 2009
 * Time: 5:05:07 PM
 */
class ImageFileFilter implements FileFilter
{
  List<FileFilterEventListener> fileFilterEventListeners = []

  public boolean accept(File file)
  {
    def status = true

    fileFilterEventListeners.each { it.filterFile(new FileFilterEvent(this, file))}

    if ( file?.isFile() )
    {
      status = checkFilename(file) && checkDirectoryBasedImage(file)
    }
    else if ( file?.isDirectory() )
    {
      status = checkDirectory(file)
    }

    if ( status )
    {
      fileFilterEventListeners.each { it.acceptFile(new FileFilterEvent(this, file))}
    }
    else
    {
      fileFilterEventListeners.each { it.rejectFile(new FileFilterEvent(this, file))}
    }

    return status;
  }

  public synchronized addFileFilterEventListener(FileFilterEventListener listener)
  {
    fileFilterEventListeners.add(listener)
  }

  public synchronized removeFileFilterEventListener(FileFilterEventListener listener)
  {
    fileFilterEventListeners.remove(listener)
  }

  static def checkFilename(File file)
  {
    def ext = file.name.toLowerCase().tokenize('.')[-1]
    def cppFiles = ~/(cpp|h|o|obj|exe|dll|so|lib)/
    def javaFiles = ~/(java|class|jar|war)/
    def programming = ~/(py|pl|sh|cgi)/
    def imageSupport = ~/(ovr|spec|kwl|his|omd|statistics|geom|prj|aux|hdr|dim|key)/
    def elevationSupport = ~/(bin|avg|min|max)/
    def worldFiles = ~/(gfw|jpw|pgw|tfw|nfw|sdw|jgw)/
    def vectorSupport = ~/(shx|dbf)/
    def documentation = ~/(txt|xml|xsl|xls|[x]html|htm[l]?|pdf|doc|rtf)/
    def archive = ~/zip|tar|gz|tgz|bz2/
    def macStuff = ~/ds_store/
    def otherFiles = ~/(tmp|log|out|orig[inal]|back|bck|save|ini|new|readme|db|htaccess)/
    def backup = ~/.*\~/
    def landSatSupport = ~/fst/
    def jp2Files = ~/jp2/
    def remoteViewSupport = ~/rv.*/
    def nitfSupport = ~/(imd|rpb|til)/

    def status = true

    if ( file.name.startsWith(".") )
    {
      status = false
    }
    else
    {
      switch ( ext )
      {
        case jp2Files:
        case cppFiles:
        case javaFiles:
        case programming:
        case imageSupport:
        case elevationSupport:
        case worldFiles:
        case vectorSupport:
        case documentation:
        case backup:
        case archive:
        case macStuff:
        case otherFiles:
        case remoteViewSupport:
        case nitfSupport:
          status = false
          break
        case landSatSupport:
          def openLandSat = ~/.*b[1-8][0-2].fst/
          status = !(openLandSat.matcher(file.name.toLowerCase()).matches())
          break
      }
    }

    return status
  }

  static def checkDirectoryBasedImage(File file)
  {
    def status = true
    def peers = file?.parentFile.list()?.collect { it.toLowerCase() }

    if ( peers?.contains("a.toc") || peers?.contains("dht") )
    {
      def name = file.name.toLowerCase()

      switch ( name )
      {
        case "a.toc":
        case "dht":
          status = true
          break
        default:
          status = false
      }
    }
    else if ( peers.find { it.endsWith(".img") } )
    {
      status = file.name.endsWith(".img")
    }

    return status
  }

  static def checkDirectory(File directory)
  {
    def status = true

    if ( new File(directory, "__OSSIM_NO_SCAN__").exists() )
    {
      status = false
    }
    else
    {
      status = checkDirectoryBasedImage(directory)
    }

    return status
  }
}
