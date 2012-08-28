import groovy.io.FileType
import groovy.io.FileVisitResult
import org.apache.commons.io.FilenameUtils 
import joms.oms.ossimImageHandlerRegistry
import joms.oms.Init
import joms.oms.StringVector
import joms.oms.DataInfo
import joms.oms.ImageStager
import java.util.concurrent.Executors
import groovy.sql.Sql
import java.util.concurrent.Callable
import java.lang.management.ManagementFactory
import org.joda.time.DateTime 
import org.joda.time.DateTimeZone
def outputHelp()
{
  println """
    REQUIRED ENVIRONMENT VARIABLES

      OMARDB            identify the postgres database name holding the omar tables.  Version number might
                        be different for your distribution but it will be of the form omardb-<VERSION>-prod. 
                        In this example we will use version 1.8.12:
                        OMARDB=omardb-1.8.12-prod

      POSTGRES_USER     identify the postgres user name that has modifcation priviledges.  Example:
                        POSTGRES_USER=postgres

      POSTGRES_PASSWORD identify the posgres user password
                        POSTGRES_PASSWORD=postgres

      OMAR_URL          identify the OMAR URL.  If running local:
                        OMAR_URL=http://localhost/omar

      PID_FILE          idientify the location where you want this script's process ID written to.  The user 
                        must have write priviledges to the PID_FILE location.
                        PID_FILE=/var/run/omar/parallelStage.groovy.pid

      NTHREADS          controls the number of threads used for the staging process.
                        NTHREADS=4

   OPTIONAL ENVIRONMENT VARIABLES
   
      LOG_FILE          identify where the log file is written.  If blank then to standard output.

      OVERVIEW_OPTIONS  specifies the overview options.  example:
                        OVERVIEW_OPTIONS="--compression-type JPEG --compression-quality 75"  

      HISTOGRAM_OPTIONS specifies the histogram option to use. exmaples:
                        HISTOGRAM_OPTIONS="--create-histogram-fast"
                        HISTOGRAM_OPTIONS="--create-histogram"

      POST_COMMAND_LINE defaults to using java code for posting.  This value can be YES or NO.  if
                        YES then it will use curl command line application to post to the server.
                        POST_COMMAND_LINE=NO

      STAGE_FILE_FILTER Is a comma separated list of extensions without the dot.  Examples:
                        stage only nitf files:
                        STAGE_FILE_FILTER="nitf,ntf"

                        if blank all files that OSSIM core engine used by OMAR will be staged into the system.
                        It is recommended that you use a filter for only data that you want staged.  
Staging examples:

    To stage files in a directory called /data/foo we can run the command
    omarRunScript.sh stageRaster.groovy /data/foo
   
    To stage files in a multiple directories called /data/foo /data/foo2 we can run the command
    omarRunScript.sh stageRaster.groovy /data/foo /data/foo2

  """
}

if(!parent.scriptArgs.size() ||parent.scriptArgs[0] == "--help")
{
  outputHelp()
  return 0
}

def outputPid()
{
   def pidString = (ManagementFactory.getRuntimeMXBean().getName() as String);

   def pid = pidString?.find(/\d+/)

   (parent.env.PID_FILE as File).withWriter() { it << pid }
   pid
}
def timeStamp()
{
  new DateTime(DateTimeZone.UTC)
}
def calculateDisplayTimeString(timeDelta)
{
  def timeValue = timeDelta /1000.0
  def timeUnits = "s"
  if(timeValue >= 60)
  {
    timeValue /= 60;
    timeUnits = "m"
    if(timeValue >= 60)
    {
      timeValue /=60
      timeUnits = "h"

      if(timeValue >= 24)
      {
        timeValue/=24;
        timeUnits = "d"
      }
    }
  }
  
  "${timeValue}${timeUnits}"
}
def outputLog(value)
{
  if(parent.env.LOG_FILE)
  {
    (parent.env.LOG_FILE as File).withWriterAppend(){it << "${value}\n"}
  }
  else
  {
    println value;
  }
}
 def getFileFilterExtList(){
    def extList = []
    if(parent.env.STAGE_FILE_FILTER)
    {
       extList = new String(parent.env.STAGE_FILE_FILTER).split(",")
    }
    else
    {
       def supportedExtensions = new joms.oms.StringVector()
       joms.oms.ossimImageHandlerRegistry.instance().getSupportedExtensions(supportedExtensions);
       supportedExtensions.size().times{extList << supportedExtensions.get(it)}
       supportedExtensions.delete()
       supportedExtensions=null
    }

    extList
 }
 def getFileFilterExtListAsRegEx(){
    def extList = getFileFilterExtList()

    def regExpression =".*("
    def count = 0;
    extList.each{it->
       if(count) regExpression +="|${it}"
       else regExpression += "${it}"
    }

    regExpression
 }

def parallelStagerPid = outputPid();

Init.instance().initialize();
sql = parent.newSqlConnection()

if(!sql)
{
   return 1
}

extList = getFileFilterExtList()

nThreads = parent.env.NTHREADS?:4
threadPool = Executors.newFixedThreadPool(nThreads);
futures = []



def stageAndAddClosure ={file->
   def imageStager = new ImageStager();
   if(imageStager.open(file.toString()))
   {
      def commandLine = ""; 
      def noExt = FilenameUtils.removeExtension(file.toString());

      if(!imageStager.hasOverviews())
      {
         commandLine = "ossim-img2rr ${parent.env.OVERVIEW_OPTIONS}";
         if(!imageStager.hasHistograms())
         {
            commandLine += " ${parent.env.HISTOGRAM_OPTIONS}"
         }
         commandLine += " ${file.absolutePath}"
      }
      else if(!imageStager.hasHistograms())
      {
         commandLine = "ossim-create-histo ${parent.env.HISTOGRAM_OPTIONS} ${file.absolutePath}";
      }
      imageStager?.delete();
      imageStager = null
      def exitCode = 0
      if(commandLine)
      {
         def process = commandLine.execute()
         process.consumeProcessOutput()

         exitCode = process.waitFor()
      }
      if(!exitCode)
      {
        outputLog("${timeStamp()}: " + parent.postDataManager(file, "addRaster"))
         //println "${new DateTime(DateTimeZone.UTC)}: " + postDataManager(parent.env.OMAR_URL, file, "addRaster");
      }
   }
   imageStager?.delete();
   imageStager = null
}


def options = [
    type: FileType.FILES,
    preDir: { dir ->

       def status = FileVisitResult.CONTINUE

       if("toc" in extList)
       {
         def atoc = new File(dir, "a.toc")
         def row = sql?.firstRow("SELECT name FROM raster_file where name = ${atoc.toString()}")
         if ( atoc.exists() &&!row)
         {
             //closure(atoc)
             if(this.threadPool)
             {
                 this.futures << this.threadPool.submit({-> stageAndAddClosure atoc } as Callable);
             }
             status =  FileVisitResult.SKIP_SUBTREE 
         } 
       }
       return status
    },
    //nameFilter: parent.env.STAGE_FILE_FILTER
    filter: { file ->  
        def ext = FilenameUtils.getExtension(file.name).toLowerCase()

         return ((ext in extList)&&(ext!="til"))
     }
]


def startTime = System.currentTimeMillis()
outputLog("parallelStager id ${parallelStagerPid} started: ${timeStamp()}" )
if(parent.scriptArgs.size())
{
   // grab dirs
   //
   def dirList = []
   parent.scriptArgs.size().times{
      dirList << new File(parent.scriptArgs[it])
   }
   //def count = 0;
   dirList.each{dir->
      dir.traverse( options){file->
         def row = sql?.firstRow("SELECT name FROM raster_file where name = ${file.toString()}")
         if(!row)
         {
            futures << threadPool.submit({-> stageAndAddClosure file } as Callable);
         }
      }
   }

   // make sure the list is called
   futures.each{it.get()}

   threadPool.shutdown();
}
sql.close();

def stopTime = System.currentTimeMillis()
def multiplier = 1/(1000*60)

outputLog("parallelStager id ${parallelStagerPid} ended: ${timeStamp()} and took ${calculateDisplayTimeString(stopTime - startTime)}" )


