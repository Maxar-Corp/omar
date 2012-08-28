package org.ossim.omar.stager

import org.ossim.omar.core.Repository
import org.ossim.omar.StagerJob
import org.ossim.omar.core.HttpStatusMessage
import java.util.concurrent.Executors
import java.util.concurrent.Callable
import java.lang.management.ManagementFactory
import static groovyx.gpars.GParsPool.withPool

class StagerService
{
    static transactional = true
    def dataManagerService
    def grailsApplication
    def sessionFactory
    def runStager(Repository repository)
    {

        repository.scanStartDate = new Date()
        repository.scanEndDate = null
        repository.save()

        StagerJob.triggerNow([baseDir: repository.baseDir])
    }
    def cleanUpGorm( )
    {
        def session = sessionFactory.currentSession
        session.flush()
        session.clear()
    }
    def popAndAddStagerQueueItem()
    {
        def result = 0
        def nthreads = grailsApplication.config.stager.queue.threads?:4
        try{
            StagerQueueItem.withTransaction{
                def records = StagerQueueItem.list(cache:false,
                        sort:"dateCreated",
                        max:100,
                        order:"desc")
                records.each{record->
                    record.status = "indexing"
                    record.save()
                }
                withPool() {
                    records.collectParallel{item->
                        def msg = new HttpStatusMessage();
                        dataManagerService.add(msg, [datainfo:item.dataInfo])
                    }

                }
                result += records.size();
                records.each{record->
                    record.delete()
                }
            }
        }
        catch(def e)
        {
            println e
        }

        cleanUpGorm()
        result
    }
}
