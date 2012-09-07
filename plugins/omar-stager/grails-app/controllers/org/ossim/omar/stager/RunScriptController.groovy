package org.ossim.omar.stager

import org.quartz.impl.StdSchedulerFactory
import org.quartz.JobDetail
import org.quartz.Trigger
import org.quartz.utils.Key
import org.quartz.SimpleTrigger
import org.quartz.JobDataMap
import org.ossim.omar.stager.StageImageJob
import org.quartz.Scheduler
import org.ossim.omar.stager.StagerUtil

class RunScriptController {

	def quartzScheduler

    def index() { }

    def indexFiles()
  	{
    	if(!quartzScheduler?.getTrigger("indexFiles ${params.path}", "STAGE"))
    	{
      		def jobDataMap = new JobDataMap()
        	jobDataMap.commandLineScript = "omarRunScript.sh indexFiles ${params.path}"

 		     def trigger = new SimpleTrigger("indexFiles ${params.path}", "STAGE");
    	    trigger.setJobDataMap(jobDataMap);
        	RunScriptJob.schedule(trigger);

	        flash.message = "Job Submitted into Job Que."
    	}
    	else
    	{
        	flash.message = "Job already running."
    	}

	    redirect(controller: 'Repository', action: 'scripts')
 	 }

 	def removeRaster()
  	{
    	if(!quartzScheduler?.getTrigger("removeRaster ${params.path}%", "STAGE"))
    	{
      		def jobDataMap = new JobDataMap()
        	jobDataMap.commandLineScript = "omarRunScript.sh removeRaster ${params.path}%"

 		     def trigger = new SimpleTrigger("removeRaster ${params.path}%", "STAGE");
    	    trigger.setJobDataMap(jobDataMap);
        	RunScriptJob.schedule(trigger);

	        flash.message = "Job Submitted into Job Que."
    	}
    	else
    	{
        	flash.message = "Job already running."
    	}

	    redirect(controller: 'Repository', action: 'scripts')
 	 }

 	def stageRaster()
  	{
    	if(!quartzScheduler?.getTrigger("stageRaster ${params.path}", "STAGE"))
    	{
      		def jobDataMap = new JobDataMap()
        	jobDataMap.commandLineScript = "omarRunScript.sh stageRaster ${params.path}"

 		     def trigger = new SimpleTrigger("stageRaster ${params.path}", "STAGE");
    	    trigger.setJobDataMap(jobDataMap);
        	RunScriptJob.schedule(trigger);

	        flash.message = "Job Submitted into Job Que."
    	}
    	else
    	{
        	flash.message = "Job already running."
    	}

	    redirect(controller: 'Repository', action: 'scripts')
 	 }

 	def synchFiles()
  	{
    	if(!quartzScheduler?.getTrigger("synchFiles", "STAGE"))
    	{
      		def jobDataMap = new JobDataMap()
        	jobDataMap.commandLineScript = "omarRunScript.sh synchFiles"

 		     def trigger = new SimpleTrigger("synchFiles", "STAGE");
    	    trigger.setJobDataMap(jobDataMap);
        	RunScriptJob.schedule(trigger);

	        flash.message = "Job Submitted into Job Que."
    	}
    	else
    	{
        	flash.message = "Job already running."
    	}

	    redirect(controller: 'Repository', action: 'scripts')
 	 }

 	 def clearCache()
  	{
    	def ant = new AntBuilder()

	    ant.sequential {
    	  delete(dir:grailsApplication.config.thumbnail.cacheDir, failonerror:false)
    	  mkdir(dir:grailsApplication.config.thumbnail.cacheDir)
    	  flash.message = "OMAR Cache has been deleted."
  		}
  
  		redirect(controller: 'Repository', action: 'scripts')
  	}
}