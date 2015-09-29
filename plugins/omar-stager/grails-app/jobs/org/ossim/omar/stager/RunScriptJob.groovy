package org.ossim.omar.stager

import org.quartz.JobDataMap
import groovy.util.logging.Log4j
@Log4j
class RunScriptJob{

    def concurrent = true

    static triggers = {}

    def execute(org.quartz.JobExecutionContext context) {

        JobDataMap dataMap = context.getMergedJobDataMap();

        // println "COMMAND LINE STUFF: ${dataMap}"

        // println dataMap
        println "Executing ${dataMap.commandLineScript}"
        def err  = new ByteArrayOutputStream()
        def out  = new ByteArrayOutputStream()
        def proc = dataMap.commandLineScript?.execute();
        proc?.consumeProcessOutput(out, err)
        proc?.waitFor()
        log.error(err?.toString())
    }
}
