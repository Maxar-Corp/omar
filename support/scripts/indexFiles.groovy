//@Grapes([
//    @Grab('org.codehaus.gpars:gpars:0.12'),
//    @Grab('commons-pool:commons-pool:1.6'),            
//    @Grab('org.ossim:joms:1.0-SNAPSHOT'),
//    @Grab('postgresql:postgresql:9.1-901.jdbc4'),    
//    @GrabConfig(systemClassLoader=true)                
//])

import groovy.io.FileType
import groovy.sql.Sql

import static groovyx.gpars.GParsPool.withPool

import joms.oms.DataInfo
import joms.oms.Init

import org.apache.commons.pool.BasePoolableObjectFactory; 
import org.apache.commons.pool.impl.GenericObjectPool

import java.sql.Timestamp
import java.util.concurrent.atomic.AtomicInteger 
import java.util.regex.Pattern
import joms.oms.StringVector
import joms.oms.ossimImageHandlerRegistry

def outputHelp()
{
    println "Help for indexFiles!!"
}

def setupCli()
{
    def cli = new CliBuilder(usage: 'indexFiles [options] <scriptArgs>')
    cli.stopAtNonOption = true
    cli.with{
        h longOpt: 'help', 'Show usage information'
        _ longOpt: "filefilter", args:1, "Override the default file filter and specify a comma separated list of file extension to index"
    }

    cli
}
def cli = setupCli()
def options = cli.parse(parent.scriptArgs)

if(options.h)
{
    cli.usage()
    return 0;
}
if(options.filefilter)
{
    parent.env.STAGE_FILE_FILTER = "${options.filefilter}"
}

parent.scriptArgs = options.arguments()
//if(!parent.scriptArgs.size() ||parent.scriptArgs[0] == "--help")
//{
//  outputHelp()
//  return 0
//}

enum Mode {SEQ, POOL}



class DataInfoPoolableObjectFactory extends BasePoolableObjectFactory
{    
    public void destroyObject(Object dataInfo) throws Exception    
    {        
        dataInfo?.close()
        //dataInfo?.delete()
        dataInfo = null
    }    
 
    public Object makeObject()
    {    
        return new DataInfo()
    }
    
    public void passivateObject(Object dataInfo)
    {        
        dataInfo?.close()
    }        
} 

class SqlPoolableObjectFactory extends BasePoolableObjectFactory
{    
    def url
    def user
    def password
    def driver

    public void destroyObject(Object sql) throws Exception    
    {        
        sql.close()        
    }    
 
    public Object makeObject()
    {    
        return Sql.newInstance(url, user, password, driver)
    }    
} 
class indexFileQueue
{
    def parent
    def baseDir

    def db
    def dataInfoPool
    def sqlPool
    def nThreads
    def nameFilter
    def count
    def batchSize = 100

    def batch

    def static final  insertSQL = """
        INSERT INTO stager_queue_item (version, status, file, base_dir, data_info, date_created, last_updated) 
        VALUES (:version, :status, :file, :base_dir, :data_info, :date_created, :last_updated) 
        """
    def static final testFileSQL = "SELECT id FROM stager_queue_item where file = :file"
 
    def runDataInfo(def file)
    {
        def xml = null        
        def dataInfo = dataInfoPool?.borrowObject()
        
        try{
            if ( dataInfo?.open(file.absolutePath) )
            {
                xml = dataInfo?.info
            }  
        }
        catch(def e)
        {
        }
        dataInfo.close();
        dataInfoPool?.returnObject(dataInfo)   

        return xml                                    
    }

    def createRecord(def item, def timestamp)
    {
        def flag = true
        def xml = (flag) ? runDataInfo(item) : null
        def record
        if(flag&&xml || (!flag))
        {
             record= [ 
                version: 0, 
                status: 'new', 
                file: item.absolutePath, 
                base_dir: baseDir.absolutePath, 
                data_info: xml, 
                date_created: timestamp, 
                last_updated: timestamp 
            ]
        }

        record        
    }

    def insertBatch(def batch, def batchId)
    {
        def batchStart = System.currentTimeMillis()
        def timestamp = new Timestamp(batchStart)
        def mode = Mode.SEQ
        def records

        switch(mode) 
        {
        case Mode.POOL:
            withPool(nThreads) {
                records = batch.collectParallel { item -> createRecord(item, timestamp) }
            }                
            break
        case Mode.SEQ:
            records = batch.collect { item -> createRecord(item, timestamp) }                    
            break            
        }

        def sql = sqlPool.borrowObject()
        try{
            sql.withBatch(batchSize, insertSQL) { stmt ->   

                for(def record in records) 
                {
                    try 
                    {
                        stmt.addBatch(record)
                    } 
                    catch (e) 
                    {
                        //e.printStackTrace()
                    }               
                }
            }
        }
        catch(e)
        {
            println e
        }
        sqlPool.returnObject(sql)

        def batchStop = System.currentTimeMillis()
        def batchTime = batchStop - batchStart
        def threadId = Thread.currentThread().id
 
        println "threadId: ${threadId}, batchId: ${batchId}, batchTime: ${batchTime}, size: ${batch.size()}"        

    }
    def getFileFilterExtList(){
        def extList = []
        if(parent.env.STAGE_FILE_FILTER)
        {
           extList = new String(parent.env.STAGE_FILE_FILTER).split(",")
        }
        else
        {
           def supportedExtensions = new StringVector()
           ossimImageHandlerRegistry.instance().getSupportedExtensions(supportedExtensions);
           supportedExtensions.size().times{extList << supportedExtensions.get(it)}
           supportedExtensions.delete()
           supportedExtensions=null
        }

        extList
     }
     def getFileFilterExtListAsPattern(){
        def extList = getFileFilterExtList()

        def regExpression =".*("
        def count = 0;
        extList.each{it->
           if(count) regExpression +="|${it.toLowerCase()}|${it.toUpperCase()}"
           else regExpression += "${it.toLowerCase()}|${it.toUpperCase()}"
           ++count
        }
        regExpression += ")"

        Pattern.compile(regExpression)
     }

/*
    def processFile(def file)
    {
        batch << file

        if ( count?.incrementAndGet() % batchSize == 0 )
        {
            insertBatch(batch.clone(), count)
            batch = []
        }
    }
*/
    def scan()
    {
        def options = [
            type: FileType.FILES,
            nameFilter: nameFilter
        ]

  //      baseDir?.traverse(options, this.&processFile)


        withPool(nThreads) {
            baseDir?.traverse(options) { file ->
               def sql = sqlPool.borrowObject()
               def row = sql?.firstRow(testFileSQL, [file:file.absolutePath])
                sqlPool.returnObject(sql)
                if(!row)
                {
                    batch << file

                    if ( count?.incrementAndGet() % batchSize == 0 )
                    {
                        this.&insertBatch.callAsync(batch.clone(), count.intValue())
                        batch = []
                    }
                }
            }
            if(batch.size())
            {
                this.&insertBatch.callAsync(batch.clone(), count.intValue())
                batch = []
           }
        }
   }

    def run()
    {
        nThreads = parent.env.NTHREADS?:4
        nameFilter = getFileFilterExtListAsPattern()
        if ( baseDir?.exists() )
        {
            dataInfoPool = new GenericObjectPool(new DataInfoPoolableObjectFactory() )     
            sqlPool = new GenericObjectPool(new SqlPoolableObjectFactory(db) )     

            count = new AtomicInteger(0)
            batch = []

            def start = System.currentTimeMillis()

            scan() 

            try{
                if ( ! batch.isEmpty() )
                {
                    def batchId = count.intValue() - batch.size() + batchSize          
                    insertBatch(batch, batchId)
                }            
            }
            catch(def e)
            {

            }
            def stop = System.currentTimeMillis()

            println "elapsed: ${stop - start}"
            println "  count: ${count?.intValue()}"

            dataInfoPool?.clear()
            dataInfoPool?.close()

            sqlPool?.clear()
            sqlPool?.close()                
        }
    }

}


def db = [
        url: "jdbc:postgresql:${parent.env.OMARDB}",
        //url:'jdbc:postgresql:omardb-1.8.14-prod', 
        user: parent.env.POSTGRES_USER,//'postgres', 
        password:parent.env.POSTGRES_PASSWORD, 
        driver:'org.postgresql.Driver'
]

if(parent.scriptArgs.size())
{
   // grab dirs
   //
   def dirList = []
   parent.scriptArgs.size().times{
      def dir = parent.scriptArgs[it] as File
      def queue = new indexFileQueue(parent:parent, db:db, baseDir: dir)
      queue.run()
    }
}
