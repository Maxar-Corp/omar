
import groovy.sql.Sql
import java.net.URLEncoder 
import java.net.URL
import java.net.URLConnection
import java.io.OutputStreamWriter
import java.io.File
import groovy.io.FileType
def outputHelp()
{
  println """
This will scan the tables and see if any data is not reachable.  If the data is not reachable then it will be unindexed
out of OMAR™. The thumbnail cache directory is also scanned and synched.  Any data not in the tables but has an associated
thumbnail will also be removed.


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
                        
      OMAR_THUMBNAIL_CACHE  Specifies the location where OMAR keeps its thumbnail cache directory.
                            Typical locaiton is /data/omar/omar-cache

   OPTIONAL ENVIRONMENT VARIABLES
   
      LOG_FILE          identify where the log file is written.  If blank then to standard output.

Staging examples:

    To synchronize the tables then run
    omarRunScript.sh synchFiles

  """
}

if(parent.scriptArgs[0] == "--help")
{
  outputHelp()
  return 0
}

class SynchFiles
{
  def ERROR_MAP = [
                  "NOT_RUNNING" : "Unable to do file synch checking.  OMAR NOT RUNNING!",
                  ]
  def sql;
  def omarUrl;
  def postgresUser;
  def postgresPassword;
  def omarDb;
  def parent;
  def filePathOverride = [];
 

  def synchronizeTables()
  {
    def resultCode = 0;
    def runningCheck = parent.postRunning()
    if(runningCheck != "OMAR_RUNNING")
    {
       parent.outputLog(ERROR_MAP.NOT_RUNNING)
       return 1;
    }

   
    def query;
    if(filePathOverride)
    {
      query = "SELECT name FROM raster_file WHERE "
      
      filePathOverride.size().times{idx->
        query += "name LIKE '${filePathOverride[idx]}' "

        if(idx != filePathOverride.size()-1)
        {
          query += " OR "
        }
      } 
      query+=";"
    }
    else
    {
      query = """
              SELECT name from raster_file;
              """
    } 
    try{
       sql?.eachRow(query) { row ->
       def file = new File(row.name)
          //println "checking file: " + file
          if ( ! file.exists() )
          {
            //println "Removing file: " + file
            def result = parent.postDataManager(file, "removeRaster");
            if(result) parent.outputLog (result);
          }
       }
    }
    catch(def e){
       // will log this into a log file
       parent.outputLog(e);
    }
   
  }

  def synchronizeThumbnails()
  {
      def dir = new File(parent.env.OMAR_THUMBNAIL_CACHE) 
      dir.traverse( type:FileType.FILES, nameFilter:~/.*jpg/ ) 
      { file->
        def filenameOnly =  file.name;
        def splitDash = filenameOnly.split("-");

        try{
          def row = sql?.firstRow("SELECT id FROM raster_entry where id=${splitDash[0] as Integer}");

          if(!row)
          {
            parent.outputLog  ("Removing file: " + file)
            file.delete();
          }
        }
        catch(def e)
        {
          parent.outputLog (e)
        }
      };
  }


  def run(sql)
  {
    this.sql = sql;
    synchronizeTables();
    synchronizeThumbnails();
  }
};

def synchFiles = new SynchFiles()
synchFiles.filePathOverride = []
synchFiles.parent = this.parent
parent.scriptArgs.each{
  synchFiles.filePathOverride << it
}
def sql = parent.newSqlConnection()

synchFiles.run(sql);

sql?.close();

