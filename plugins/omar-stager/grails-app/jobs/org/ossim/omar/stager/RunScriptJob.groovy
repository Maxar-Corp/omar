package org.ossim.omar.stager

import org.quartz.JobDataMap
import org.ossim.omar.core.Utility
import groovy.util.logging.Log4j
@Log4j
class RunScriptJob{

	def concurrent = false

    static triggers = {}

    def execute(org.quartz.JobExecutionContext context) {
        JobDataMap dataMap = context.getMergedJobDataMap();
  		// println dataMap
  		//println "Executing ${dataMap.commandLineScript}"
  		def out = Utility.executeCommand(dataMap.commandLineScript, true).text//.execute();
        log.info(out)

        //proc.in.eachLine { line -> log.info(line) }

        //proc?.waitFor()
  	 	
  	 	//println "Finished ${dataMap.commandLineScript}"
    }
}
