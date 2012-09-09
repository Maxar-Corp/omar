package org.ossim.omar.stager

import org.quartz.JobDataMap


class RunScriptJob{

	def concurrent = false

    static triggers = {}

    def execute(org.quartz.JobExecutionContext context) {
        JobDataMap dataMap = context.getMergedJobDataMap();
  		
  		//println "Executing ${dataMap.commandLineScript}"
  		def proc = dataMap.commandLineScript.execute();

  	 	proc?.waitFor()
  	 	
  	 	//println "Finished ${dataMap.commandLineScript}"
    }
}
