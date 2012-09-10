package org.ossim.omar.stager

import org.quartz.JobDataMap
import groovy.util.logging.Log4j
@Log4j
class RunScriptJob{

	def concurrent = false

    static triggers = {}

    def execute(org.quartz.JobExecutionContext context) {
        JobDataMap dataMap = context.getMergedJobDataMap();
  		
  		//println "Executing ${dataMap.commandLineScript}"
  		def proc = dataMap.commandLineScript.execute();
        proc.in.eachLine { line -> log.info(line) }

        proc?.waitFor()
  	 	
  	 	//println "Finished ${dataMap.commandLineScript}"
    }
}
