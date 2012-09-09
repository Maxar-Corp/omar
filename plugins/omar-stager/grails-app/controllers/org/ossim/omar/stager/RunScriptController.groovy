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
import grails.converters.JSON

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

    def kill()
    {
        if (params.name&&params.group)
        {
            //quartzScheduler.deleteJob(params.name, params.group);
        }
        null
        //render("killed")
    }

    def scripts( )
    {
        def jobTriggers = "${runScriptService.listJobTriggersByGroup("STAGER_SCRIPTS") as JSON}"
        if (params.renderStream)
        {
            response.contentType = "application/json"
            response.outputStream << jobTriggers
            response.outputStream.flush()
        }
        else
        {
            render( view: 'scripts', model: [
                    jobTriggers: jobTriggers
            ] )
        }
        null
    }

    def indexFiles()
    {
        if (!params.path)
        {
            flash.message = "Must specify a directory to index."
        }
        else if(!quartzScheduler?.getTrigger("indexFiles ${params.path}", "STAGER_SCRIPTS"))
        {
            def jobDataMap = new JobDataMap()
            jobDataMap.commandLineScript = "${omarRunScript} --nthreads ${params.threads} indexFiles ${params.path}"

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

            def trigger = new SimpleTrigger("removeRaster", "STAGER_SCRIPTS");
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
            flash.message = "Must specify a directory to do a complete stage and index."
        }
        else if(!quartzScheduler?.getTrigger("stageRaster ${params.path}", "STAGER_SCRIPTS"))
        {
            def jobDataMap = new JobDataMap()
            jobDataMap.commandLineScript = "${omarRunScript} --nthreads ${params.threads} stageRaster ${params.path}"

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