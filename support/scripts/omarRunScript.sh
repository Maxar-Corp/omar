#!/usr/bin/env groovy
environmentVariables =[
   OMAR_URL:"http://localhost:8080/omar",
   OMAR_THUMBNAIL_CACHE:"/data/omar/omar-cache",
   NTHREADS: 4,
   OMARDB:"omardb-${System.env.OSSIM_RELEASE_VERSION_NUMBER?:System.env.OSSIM_VERSION}-dev",
   POSTGRES_USER:"postgres",
   POSTGRES_PASSWORD:"postgres",
   POST_COMMAND_LINE:"NO",
   LOG_FILE:"",
   PID_FILE:"",
   CLASSPATH:"${System.env.OSSIM_DIST_ROOT}/tomcat/webapps/omar/WEB-INF/lib", 
   //STAGE_FILE_FILTER:"", 
   STAGE_FILE_FILTER:"hsi,hri,tiff,tif,ntf,nitf,img",
   HISTOGRAM_OPTIONS:"--create-histogram-fast",
   OVERVIEW_OPTIONS:"--compression-type JPEG --compression-quality 75"
]

import java.lang.management.ManagementFactory
import groovy.io.FileType
import groovy.sql.Sql


if(!(environmentVariables.CLASSPATH as File).exists())
{
   File testFile = new File("${System.env.OMAR_HOME}/target/WEB-INF/lib")
   if(testFile.exists())
   {
      environmentVariables.CLASSPATH=testFile.absolutePath
   }
}
//println "*************************** ${environmentVariables.CLASSPATH}"
class OmarRunScript{
   def env
   def runScriptFile
   def runScriptDirectory
   def envVariables
   def scriptToRun
   def scriptArgs
   def args

   def getRunnableScripts()
   {
      def runnableScripts = []
      try{
      def tempFile = runScriptDirectory as File
         tempFile.traverse( type:FileType.FILES, nameFilter:~/.*.groovy/ ) {
                 
           if(it.name != runScriptFile)
            {
                  runnableScripts << it.name.replaceFirst(~/\.[^\.]+$/, '')
            }
         }

      }
      catch(def e)
      {
         outputLog(e)
      }

      runnableScripts
   }
   def setupCli()
   {
      def cli = new CliBuilder(usage: 'omarRunShript.sh [options] <script> [options] <scriptArgs>')
      cli.stopAtNonOption = true
      cli.with{
         h longOpt: 'help', 'Show usage information'
         _ longOpt:"nthreads", args:1, argName:"nthreads", "nthreads"
         _ longOpt:"omardb", args:1, argName:"omardb", "omardb"
         _ longOpt:"dbuser", args:1, argName:"postgres_user", "postgres username"
         _ longOpt:"dbpassword", args:1, argName:"postgres_password", "postgres password"
         _ longOpt:"filefilter", args:1, argName:"filefilter", "specify a comma separated list of file extensions without spaces.  If empty will try all files supported"
         _ longOpt:"url", args:1, argName:"url", "URL to the OMAR server ex: http://<ip>/omar"
         _ longOpt:"ovropt", args:1, argName:"ovropt", "Overview options surrounded by quotes"
         _ longOpt:"cp", args:1, argName:"cp", "CLASSPATH for where the jar files are located"
      }

      cli.footer = """Supported Scripts:
      ${runnableScripts.join("\n")} """

      cli
   }
   def run(args)
   {
      def cli = setupCli();
      def options = cli.parse(args)
      if(options.h)
      {
         cli.usage()
         return 0;
      }

      if(options.nthreads)
      {
         env.NTHREADS = "${options.nthreads}" as Integer
      }
      if(options.omardb)
         {
            env.OMARDB = options.omardb
         }
      if(options.url)
         {
            env.OMAR_URL = options.url
         }
      if(options.filefilter != false)
      {
         env.STAGE_FILE_FILTER = "${options.filefilter}"
      }
      if(options.dbuser)
      {
         env.POSTGRES_USER = "${options.dbuser}"
      }
      if(options.dbpassword)
      {
         env.POSTGRES_PASSWORD= "${options.dbpassword}"
      }
      if(options.ovropt)
      {
         env.OVERVIEW_OPTIONS="${options.ovropt}"
      }
      if(options.cp)
      {
         env.CLASSPATH="${options.cp}"
      }
      //println options.arguments()
      //println env
      if(!args.length )
      {
         cli.usage()
         return 0;
      }
      this.args = options.arguments()
      //println "===============${this.args}================="
      scriptToRun  = findScriptToRun(new File(this.args[0]))

      if(!scriptToRun)
      {
         outputLog("Unable to find script ${this.args[0]}")
         return 1;
      }

     env.PID_FILE = new File(scriptToRun.toString()+".pid")
     if(checkPidRunning())
      {
         println "Process ${scriptToRun} is already running"
         return 0;
      }
      outputPid();
      loadJars();

      def shell = new GroovyShell();
      this.scriptArgs = [];
      if(this.args.size()>1) (1..this.args.size()-1).each{this.scriptArgs << this.args[it]}
      shell.setVariable("parent", this)
      return shell.evaluate(scriptToRun)
   }
   def findScriptToRun(script)
   {
      def result = script
      // test extension
      //
      result = script.name.lastIndexOf('.') >0?script:new File(script.toString()+".groovy")

      if(!result.exists())
      {
         result = new File(runScriptDirectory, result.name)
         if(!result.exists())
         {
            return "";
         }
      }

      result
   }
   def outputLog(value)
   {
       if(env.LOG_FILE)
       {
         (env.LOG_FILE as File).withWriterAppend(){it << "${value}\n"}
       }
       else
       {
         println value;
       }
   }
   def newSqlConnection()
   {
      Sql.newInstance([
       url: "jdbc:postgresql:${env.OMARDB}",
       user: "${env.POSTGRES_USER}",
       password: "${env.POSTGRES_PASSWORD}",
       driver: "org.postgresql.Driver"
    ]);
   }
   def findWar()
   {
      def version = System.env.OSSIM_RELEASE_VERSION_NUMBER
      if(!version)
      {
         version =  System.env.OSSIM_VERSION
      }
      def test = new File("${System.env.OSSIM_DIST_ROOT}/tomcat/webapps/omar.war ")
      if(!test.exists())
      {
         test = new File("${System.env.OSSIM_INSTALL_PREFIX}/omar/omar.war")
         if(!test.exists())
         {
            test = new File("")
         }
      }
      test.toString()
   }
   def loadJars()
   {
      // For now until we get local repositories setup we will try to find the extracted WAR file location
      //
      if(!env.CLASSPATH)
      {
         // try to find class path
         //
         def test = new File("${System.env.OMAR_HOME}/target/WEB-INF/lib")
         if(!test.exists())
         {
            def warFile = new File(findWar())
            if(warFile.exists())
            {
               def warFileExtractTo = new File(warFile.parent,"omar");
               if(!warFileExtractTo.exists())
               {
                  outputLog("Extracting WAR ${warFile} to location ${warFileExtractTo}")
                  def command = "unzip ${warFile} -d ${warFileExtractTo}"
                  def output  = command.execute();

                  output.consumeProcessOutput()
                  output.waitFor()
               }
               test = new File(warFileExtractTo.toString(), "WEB-INF/lib")
            }
         }
         env.CLASSPATH = test.toString();
      }
      def loader = this.class.classLoader.rootLoader
      def splitClasspath = env.CLASSPATH.split(":");
      splitClasspath.each{
         def file = new File(it)

         if(file.name.endsWith(".jar"))
         {
            jars.each { loader.addURL(file.toURI().toURL()) }
         }
         else
         {
            try{
               file.traverse( type:FileType.FILES, nameFilter:~/.*.jar/ ) {
                  loader.addURL(it.toURI().toURL())
               }

            }
            catch(def e)
            {
               outputLog(e)
            }
         }
      }
   }
   def outputPid()
   {
      def pidString = (ManagementFactory.getRuntimeMXBean().getName() as String);


      def pid = pidString?.find(/\d+/)

      (this.env.PID_FILE as File).withWriter() { it << pid }
      pid
   }

   def checkPidRunning()
   {
      def result=false
      def PID=""
      if(env.PID_FILE.exists())
      {
         PID = Integer.parseInt(env.PID_FILE.text)
         def command = "kill -s 0 ${PID}"
         def output = command.execute();

         output.waitFor()
         def text = output.err.text //output.consumeProcessOutput()

         if(!text)
         {
            result = true
         }
      }

      result;
   }

   def post(def urlParam, def urlEncodedParams)
   {
      def result
      if(env.POST_COMMAND_LINE == "YES")
      {
         def command = "curl"

         if(urlEncodedParams)
         {
           command += " --data ${urlEncodedParams} ${urlParam}"
         }
         else
         {
           command += " ${urlParam}"
         }
         def output = command.execute();

         result = output.text //output.consumeProcessOutput()
         output.waitFor()
      }
      else
      {
         def wr;
         def url = new URL(urlParam);
         try{
            def connection = url.openConnection(); 
            connection.setDoOutput(true); 
            connection.setRequestMethod("POST"); 
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
            connection.setRequestProperty("charset", "utf-8");

            if(urlEncodedParams)
            {
               wr = new OutputStreamWriter(connection.getOutputStream()); 
               wr.write(urlEncodedParams); 
               wr.flush(); 
            }
            result = connection.getInputStream().getText("utf-8"); 
         }
         catch(def e)
         {
            result = e
         }
         wr?.close(); 
      }

      result
   }
   def postDataManager(filename, command)
   {

      this.post("${env.OMAR_URL}/dataManager/${command}",
            URLEncoder.encode("filename", "UTF-8") + "=" + URLEncoder.encode(filename as String, "UTF-8"))
   }
   def postRunning()
   {
      this.post("${env.OMAR_URL}/running", null)
   }
}
def runScript = new OmarRunScript();


runScript.runScriptFile  = new File(getClass().protectionDomain.codeSource.location.path)
runScript.runScriptDirectory = runScript.runScriptFile.parent
runScript.env = environmentVariables
runScript.run(args);

