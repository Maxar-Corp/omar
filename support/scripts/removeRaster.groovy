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

      OMAR_THUMBNAIL_CACHE  Specifies the location where OMAR keeps its thumbnail cahce directory.
                            Typical locaiton is /data/omar/omar-cache

   OPTIONAL ENVIRONMENT VARIABLES
   
      LOG_FILE          identify where the log file is written.  If blank then to standard output.

      POST_COMMAND_LINE defaults to using java code for posting.  This value can be YES or NO.  if
                        YES then it will use curl command line application to post to the server.
                        POST_COMMAND_LINE=NO

removeRaster examples:

    To remove all files that start with the path /data/foo we can run the command
    omarRunScript.sh removeRaster /data/foo%
    where the % is a wild card
   
    To remove the index of all files that contain the sub string 2012-05-10 then run the command:
    omarRunScript.sh removeRaster %2012-05-10%
  """
}

if(parent.scriptArgs[0] == "--help")
{
  outputHelp()
  return 0
}

sql = parent.newSqlConnection()


def runningCheck = parent.postRunning()

if(runningCheck != "OMAR_RUNNING")
{
   println "OMAR NOT RUNNING!"
   return 1;
}


def removeRasterFiles()
{
   def query;
   if(parent.scriptArgs.size())
   {
   query = "SELECT name FROM raster_file WHERE "

   parent.scriptArgs.size().times{idx->
     query += "name LIKE '${parent.scriptArgs[idx]}' "

     if(idx != parent.scriptArgs.size()-1)
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
   def dir = new File(parent.env.OMAR_THUMBNAIL_CACHE) 

   try{
    this.sql?.eachRow(query) { row ->
      def file = new File(row.name)
      def result = parent.postDataManager(file, "removeRaster");
      parent.outputLog (result)
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
       def row = this.sql?.firstRow("SELECT id FROM raster_entry where id=${splitDash[0] as Integer}");

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

removeRasterFiles()
synchronizeThumbnails()

sql?.close();

