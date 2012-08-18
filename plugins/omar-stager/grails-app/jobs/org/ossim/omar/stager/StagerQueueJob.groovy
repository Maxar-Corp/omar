package org.ossim.omar.stager

import java.util.concurrent.atomic.AtomicInteger
import static groovyx.gpars.GParsPool.withPool
import org.quartz.JobExecutionContext
import org.quartz.JobDataMap


class StagerQueueJob {
    def timeout = 5000l // execute job once in 5 seconds
    def grailsApplication
    def index
    def execute(JobExecutionContext context) {
        JobDataMap dataMap = context.getMergedJobDataMap();

        index = new AtomicInteger( 0 )

        def threadCount = grailsApplication.config.stager.queue.threads?:4

        withPool(threadCount)
        {
            if ( index?.incrementAndGet() % threadCount == 0 )
            {
                cleanUpGorm()
            }
        }
        // execute task
    }


}
