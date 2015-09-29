package org.ossim.omar.stager

import org.quartz.TriggerKey
import org.quartz.core.QuartzScheduler
import org.quartz.impl.StdSchedulerFactory
import org.quartz.JobDetail
import org.quartz.Trigger
import org.quartz.utils.Key
import org.quartz.impl.triggers.SimpleTriggerImpl
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
    def grailsLinkGenerator

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
    def jobs()
    {
        def jobTriggers = "${runScriptService.listScheduledJobs() as JSON}"
        //def jobTriggers = "${runScriptService.listJobTriggersByGroup("") as JSON}"
        response.contentType = "application/json"
        response.outputStream << jobTriggers
        response.outputStream.flush()
        null
    }
    def scripts( )
    {
        if (grailsApplication.config.stager.scripts.forceUseFormatterOnReload)
        {
            params.removeRasterArgs = grailsApplication.config.stager.scripts.formatter.removeFilesArgs()
            params.stageRasterArgs = grailsApplication.config.stager.scripts.formatter.stageFilesArgs()
            params.indexFilesArgs = grailsApplication.config.stager.scripts.formatter.indexFilesArgs()
        }
        else
        {
            params.removeRasterArgs = params.removeRasterArgs?:grailsApplication.config.stager.scripts.formatter.removeFilesArgs()
            params.stageRasterArgs = params.stageRasterArgs?:grailsApplication.config.stager.scripts.formatter.stageFilesArgs()
            params.indexFilesArgs = params.indexFilesArgs?:grailsApplication.config.stager.scripts.formatter.indexFilesArgs()
        }

        //println grailsApplication.config.stager.scripts.formatter.indexFilesPrefix()
        def jobTriggers = "${runScriptService.listScheduledJobs() as JSON}"
        //def jobTriggers = "${runScriptService.listJobTriggersByGroup("") as JSON}"
        def model = [jobTriggers: jobTriggers]
        model << params

       // println model
        render( view: 'scripts', model:model )

    }

    def indexFiles()
    {
        def runScriptArgs = params.runScriptIndexFilesArgs?:""
        def indexFilesArgs = params.indexFilesArgs
        if (!indexFilesArgs)
        {
            flash.message = "Must specify a directory to index."
        }
        else if(!quartzScheduler?.getTrigger(new TriggerKey("indexFiles","STAGER_SCRIPTS")))//"indexFiles", "STAGER_SCRIPTS"))
        {
            def jobDataMap = new JobDataMap()

            jobDataMap.commandLineScript = "${omarRunScript} -u ${grailsApplication.config.omar.serverURL}  --threads ${params.threads} add ${indexFilesArgs}"
           // println jobDataMap.commandLineScript
            def trigger = new SimpleTriggerImpl("indexFiles", "STAGER_SCRIPTS");
            trigger.setJobDataMap(jobDataMap);
            RunScriptJob.schedule(trigger);

            flash.message = "indexFiles ${indexFilesArgs} submitted into Job Queue."
        }
        else
        {
            flash.message = "indexFiles Job already running."
        }
        redirect(action: 'scripts', params:params)
    }

    def removeRaster()
    {
        def runScriptArgs = params.runScriptRemoveRasterArgs?:""
        def removeRasterArgs = params?.removeRasterArgs
        if (!params?.removeRasterArgs)
        {
            flash.message = "Must specify a value to search and remove from tables."
        }
        else if(!quartzScheduler?.getTrigger(new TriggerKey("removeRaster","STAGER_SCRIPTS")))
        {
            def jobDataMap = new JobDataMap()

            jobDataMap.commandLineScript = "${omarRunScript} -u ${grailsApplication.config.omar.serverURL}  --threads ${params.threads} remove ${removeRasterArgs}"
            def trigger = new SimpleTriggerImpl("removeRaster", "STAGER_SCRIPTS");
            trigger.setJobDataMap(jobDataMap);
            RunScriptJob.schedule(trigger);

            flash.message = "removeRaster ${removeRasterArgs} Submitted into Job Queue."
        }
        else
        {
            flash.message = "removeRaster Job already running."
        }

        redirect(action: 'scripts', params:params)
    }
    def removeVideo()
    {
        def runScriptArgs = params.runScriptRemoveVideoArgs?:""
        def removeVideoArgs = params?.removeVideoArgs?.split(" ").join("% ") + "%"
        if (!params?.removeVideoArgs)
        {
            flash.message = "Must specify a value to search and remove from tables."
        }
        else if(!quartzScheduler?.getTrigger("removeVideo ${removeVideoArgs}", "STAGER_SCRIPTS"))
        {
            def jobDataMap = new JobDataMap()
            jobDataMap.commandLineScript = "${omarRunScript} ${runScriptArgs} removeVideo ${removeVideoArgs}"

            def trigger = new SimpleTriggerImpl("removeVideo ${removeVideoArgs}", "STAGER_SCRIPTS");
            trigger.setJobDataMap(jobDataMap);
            RunScriptJob.schedule(trigger);

            flash.message = "removeVideo ${removeVideoArgs} Submitted into Job Queue."
        }
        else
        {
            flash.message = "removeVideo Job already running."
        }

        redirect(action: 'scripts', params:params)
    }

    def stageRaster()
    {
        def runScriptArgs = params.runScriptStageRasterArgs?:"${grailsApplication.config?.stager?.scripts?.stageRasterOptions}"
        def stageRasterArgs = params.stageRasterArgs
        if (!stageRasterArgs)
        {
            flash.message = "Must specify a directory to do a complete stage and index."
        }
        else if(!quartzScheduler?.getTrigger(new TriggerKey("stageRaster","STAGER_SCRIPTS")))//"indexFiles", "STAGER_SCRIPTS"))
        {
            def jobDataMap = new JobDataMap()

            jobDataMap.commandLineScript = "${omarRunScript} --preproc ${runScriptArgs} -u ${grailsApplication.config.omar.serverURL}  --threads ${params.threads} add ${stageRasterArgs}"
            def trigger = new SimpleTriggerImpl("stageRaster", "STAGER_SCRIPTS");
            trigger.setJobDataMap(jobDataMap);
            RunScriptJob.schedule(trigger);

            flash.message = "stageRaster ${runScriptArgs} submitted into Job Queue."
        }
        else
        {
            flash.message = "stageRaster Job already running."
        }

        params.runScriptStageRasterArgs = runScriptArgs
        redirect(action: 'scripts', params:params)
    }
   /*
    def synchFiles()
    {
        def runScriptArgs = params.runScriptSynchFilesArgs?:""

        if(!quartzScheduler?.getTrigger("synchFiles", "STAGER_SCRIPTS"))
        {
            def jobDataMap = new JobDataMap()
            jobDataMap.commandLineScript = "${omarRunScript} ${runScriptArgs} synchFiles"

            def trigger = new SimpleTriggerImpl("synchFiles", "STAGER_SCRIPTS");
            trigger.setJobDataMap(jobDataMap);
            RunScriptJob.schedule(trigger);

            flash.message = "synchFiles submitted into Job Queue."
        }
        else
        {
            flash.message = "Job already running."
        }

        redirect(action: 'scripts', params:params)
    }
     */
    def clearCache()
    {
        def ant = new AntBuilder()

        ant.sequential {
            delete(dir:grailsApplication.config.thumbnail.cacheDir, failonerror:false)
            mkdir(dir:grailsApplication.config.thumbnail.cacheDir)
            flash.message = "OMAR Cache has been deleted."
        }

        redirect(action: 'scripts', params:params)
    }
    void setApplicationContext( org.springframework.context.ApplicationContext applicationContext )
    {
        //omardb = grailsApplication.config.dataSource.url.split(":")[-1]
        url = grailsLinkGenerator.serverBaseURL
        username = grailsApplication.config.dataSource.username
        password = grailsApplication.config.dataSource.password
        def tempRunScript = grailsApplication.config.stager.scripts.runScript

        def tempDbUrl = (grailsApplication.config.dataSource.url =~ "jdbc:(postgresql(_postGIS)?):(//(.*):(\\d+)/)?(.*)" )
        def omardbParts = [
                host: tempDbUrl[0][4],
                port: tempDbUrl[0][5],
                database: tempDbUrl[0][6]
        ]
        omardb = ""
        if (omardbParts.host&&omardbParts.port)
        {
            omardb += "//${omardbParts.host}:${omardbParts.port}/"
        }
        omardb += omardbParts.database
        omarRunScript = "${tempRunScript?:'omarRunScript.sh'}"
    }
}
