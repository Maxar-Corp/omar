includeTargets << grailsScript("Init")

import joms.oms.Init
import joms.oms.DataInfo
import joms.oms.ImageStager


def checkDirectoryBasedImage(File file)
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

def checkDirectory(File directory)
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

def checkFilename(File file)
{
  def ext = file.name.toLowerCase().tokenize('.')[-1]
  def cppFiles = ~/(cpp|h|o|obj|exe|dll|so|lib)/
  def javaFiles = ~/(java|class|jar|war)/
  def programming = ~/(py|pl|sh|cgi)/
  def imageSupport = ~/(ovr|spec|kwl|his|omd|statistics|geom|prj|aux|hdr|dim|key)/
  def elevationSupport = ~/(bin|avg|min|max)/
  def worldFiles = ~/(gfw|jpw|pgw|tfw|nfw|sdw|jgw)/
  def vectorSupport = ~/(sbn|sbx|shx|dbf)/
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


def buildSupportFiles(def file)
{
  def imageStager = new ImageStager()

  imageStager.filename = file.absolutePath
  imageStager.setUseFastHistogramStagingFlag(true)

  def generated = imageStager.stageAll()

  imageStager.delete()

  return generated
}

def getInfo(def file)
{
  def dataInfo = new DataInfo()
  def canOpen = dataInfo.open(file.absolutePath)
  def xml = null

  if ( canOpen )
  {
    def generated = buildSupportFiles(file)

    if ( generated )
    {
      dataInfo.close()
      dataInfo.open(file.absolutePath)
    }

    xml = dataInfo.getInfo()?.trim()
  }

  dataInfo.close()
  dataInfo.delete()

  return xml
}

def process(def fileOrDirectory)
{
  if ( fileOrDirectory.isFile() )
  {
    processFile(fileOrDirectory)
  }
  else if ( fileOrDirectory.isDirectory() )
  {
    processDirectory(fileOrDirectory)
  }
}


def stageFile(def file)
{
  def xml = getInfo(file)

  if ( xml )
  {
    println file
  }
}

def processFile(def file)
{
  if ( checkFilename(file) )
  {
    stageFile(file)
  }
}


def processDirectory(def directory)
{
  if ( checkDirectory(directory) )
  {
    directory.eachFile { file ->
      process(file)
    }
  }
}

target(main: "The description of the script goes here!") {
  depends(parseArguments)

  def ossimArgs = ["omar", "--disable-notify", "ALL"]

  Init.instance().initialize(ossimArgs.size(), ossimArgs as String[])

  def file

  if ( !argsMap?.params )
  {
    Ant.input(addProperty: "file.name", message: "Please enter the name of the file to run:")
    file = Ant.antProject.properties."file.name" as File
  }
  else
  {
    file = argsMap?.params[0] as File
  }

  if ( file?.exists() )
  {
    process(file)
  }
  else
  {
    System.err.println("Cannot find file: ${file}")
    System.exit(-1)
  }
}

setDefaultTarget(main)
