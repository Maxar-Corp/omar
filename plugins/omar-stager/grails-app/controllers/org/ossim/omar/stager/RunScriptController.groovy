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

import static org.ossim.omar.stager.RunScriptJob.*
import org.springframework.context.ApplicationContextAware

class RunScriptController implements ApplicationContextAware{

    def quartzScheduler
    def grailsApplication
    def omardb
    def url
    def username
    def password
    def omarRunScript
    def runScriptService
    def index() { redirect( action: 'scripts', params: params ) }

    def scripts( )
    {
        println "${runScriptService.listJobTriggersByGroup("STAGER_SCRIPTS")}"
    }

    def indexFiles()
    {
        println "${runScriptService.listJobTriggersByGroup("STAGER_SCRIPTS")}"
        if (!params.path)
        {
            flash.message = "Must specify a directory to index."
        }
        else if(!quartzScheduler?.getTrigger("indexFiles ${params.path}", "STAGER_SCRIPTS"))
        {
            def jobDataMap = new JobDataMap()
            jobDataMap.commandLineScript = "${omarRunScript} indexFiles ${params.path}"

            def trigger = new SimpleTrigger("indexFiles ${params.path}", "STAGER_SCRIPTS");
            trigger.setJobDataMap(jobDataMap);
            RunScriptJob.schedule(trigger);

            flash.message = "Job Submitted into Job Queue."
        }
        else
        {
            flash.message = "Job already running."
        }
        redirect(action: 'scripts')
    }

    def removeRaster()
    {
        if (!params.path)
        {
            flash.message = "Must specify a value to search and remove from tables."
        }
        else if(!quartzScheduler?.getTrigger("removeRaster ${params.path}%", "STAGER_SCRIPTS"))
        {
            def jobDataMap = new JobDataMap()
            jobDataMap.commandLineScript = "${omarRunScript} removeRaster ${params.path}%"

            def trigger = new SimpleTrigger("removeRaster ${params.path}%", "STAGER_SCRIPTS");
            trigger.setJobDataMap(jobDataMap);
            RunScriptJob.schedule(trigger);

            flash.message = "Job Submitted into Job Queue."
        }
        else
        {
            flash.message = "Job already running."
        }

        redirect(action: 'scripts')
    }

    def stageRaster()
    {
        if (!params.path)
        {
            flash.message = "Must specify a directory to do a complete stage and index on."
        }
        else if(!quartzScheduler?.getTrigger("stageRaster ${params.path}", "STAGER_SCRIPTS"))
        {
            def jobDataMap = new JobDataMap()
            jobDataMap.commandLineScript = "${omarRunScript} stageRaster ${params.path}"

            def trigger = new SimpleTrigger("stageRaster ${params.path}", "STAGER_SCRIPTS");
            trigger.setJobDataMap(jobDataMap);
            RunScriptJob.schedule(trigger);

            flash.message = "Job Submitted into Job Queue."
        }
        else
        {
            flash.message = "Job already running."
        }

        redirect(action: 'scripts')
    }

    def synchFiles()
    {
        if(!quartzScheduler?.getTrigger("synchFiles", "STAGER_SCRIPTS"))
        {
            def jobDataMap = new JobDataMap()
            jobDataMap.commandLineScript = "${omarRunScript} synchFiles"

            def trigger = new SimpleTrigger("synchFiles", "STAGER_SCRIPTS");
            trigger.setJobDataMap(jobDataMap);
            RunScriptJob.schedule(trigger);

            flash.message = "Job Submitted into Job Queue."
        }
        else
        {
            flash.message = "Job already running."
        }

        redirect(action: 'scripts')
    }

    def clearCache()
    {
        def ant = new AntBuilder()

        ant.sequential {
            delete(dir:grailsApplication.config.thumbnail.cacheDir, failonerror:false)
            mkdir(dir:grailsApplication.config.thumbnail.cacheDir)
            flash.message = "OMAR Cache has been deleted."
        }

        redirect(action: 'scripts')
    }
    void setApplicationContext( org.springframework.context.ApplicationContext applicationContext )
    {
        omardb = grailsApplication.config.dataSource.url.split(":")[-1]
        url = grailsApplication.config.omar.serverURL
        username = grailsApplication.config.dataSource.username
        password = grailsApplication.config.dataSource.password
        omarRunScript = "omarRunScript.sh --dbuser ${username} --dbpassword ${password} --omardb ${omardb} --url ${url}"
    }
}