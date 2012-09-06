package org.ossim.omar.raster

import org.quartz.impl.StdSchedulerFactory
import org.quartz.JobDetail
import org.quartz.Trigger
import org.quartz.utils.Key
import org.quartz.SimpleTrigger
import org.quartz.JobDataMap
import org.ossim.omar.stager.StageImageJob
import net.sf.ehcache.constructs.blocking.UpdatingCacheEntryFactory
import org.quartz.Scheduler
import org.ossim.omar.stager.StagerUtil

class StageRunnable implements Runnable{
    def stageImageService
    def file;
    def entryId;
    def id;
    def options
    void run(){
        try{
            stageImageService.stageImage(file, entryId, id, options)
        }
        catch(def e)
        {
            println e
        }
    }
}

class StageImageService {
    static transactional = true
    def grailsApplication
   // def quartzScheduler
    def dataInfoService
    def parserPool
    def workerThreadPool
    def checkAndAddStageImageJob(def rasterEntries)
    {
        if(!grailsApplication.config.stager.onDemand) return 0;

        def count = 0;
        for(rasterEntry in rasterEntries)
        {
            if(StagerUtil.needsOvrs(rasterEntry.width, rasterEntry.height, rasterEntry.numberOfResLevels))
            {
                def runnable = new  StageRunnable(stageImageService:this,
                        file:rasterEntry.mainFile.name as File,
                        entryId:rasterEntry.entryId,
                        id: rasterEntry.id,
                        options: [compressionQuality: grailsApplication.config.stager.overview.compressionQuality,
                                compressionType: grailsApplication.config.stager.overview.compressionType,
                                histogramOptions: grailsApplication.config.stager.histogramOptions
                        ])
                workerThreadPool.submit(rasterEntry.mainFile.name+"_${rasterEntry.entryId}", runnable)
            }

            /*
            if (StagerUtil.needsOvrs(rasterEntry.width, rasterEntry.height, rasterEntry.numberOfResLevels))
            {
                if(!quartzScheduler?.getTrigger(rasterEntry.mainFile.name, "STAGE"))
                {
                    def jobDataMap = new JobDataMap()
                    jobDataMap.file = rasterEntry.mainFile.name
                    jobDataMap.entryId = rasterEntry.entryId
                    jobDataMap.id      = rasterEntry.id
                    jobDataMap.options = [compressionQuality: grailsApplication.config.stager.overview.compressionQuality,
                            compressionType: grailsApplication.config.stager.overview.compressionType,
                            histogramOptions: grailsApplication.config.stager.histogramOptions
                    ]
                    def trigger = new SimpleTrigger(rasterEntry.mainFile.name, "STAGE");
                    trigger.setJobDataMap(jobDataMap);
                    StageImageJob.schedule(trigger);
                }
                ++count
            }
            */
        }
        count
    }
    def updateInfo(RasterEntry rasterEntry)
    {
        def file = rasterEntry.rasterDataSet?.getFileFromObjects("main");
        if(rasterEntry&&file)
        {
            def dataInfo = null
            try
            {
                dataInfo = dataInfoService.getInfo(file?.name, rasterEntry.entryId as Integer);
                // println dataInfo
            }
            catch(def e)
            {
                dataInfo = null
            }

            if(dataInfo)
            {
                def parser = parserPool.borrowObject()
                def oms
                try{
                    oms = new XmlSlurper( parser ).parseText( dataInfo )
                }
                catch(def e)
                {
                    oms = null
                }
                parserPool.returnObject( parser )

                if(oms)
                {
                    def rasterEntryXmlNode = oms?.dataSets?.RasterDataSet?.rasterEntries?.RasterEntry

                    RasterEntry.initRasterEntry(rasterEntryXmlNode, rasterEntry)

                    if(rasterEntry.save())
                    {
                    }
                    else
                    {

                    }
                }
            }
        }
    }

    def stageImage(File file, def entryId, def id, def options)
    {
        def compressionType    = options?.compressionType
        def compressionQuality = options?.compressionQuality
        def histogramOptions   = options?.histogramOption

        compressionType    = compressionType?:"NONE"
        compressionQuality = compressionQuality?:100
        compressionType    = compressionType.toUpperCase()
        def output = [ file:"${file}",
                        entryId: entryId,
                        compressionType:compressionType,
                        compressionQuality:compressionQuality,
                        histogramOptions:histogramOptions]

        if(file?.exists())
        {
            //println "STAGING................."
            def histoOption = "--create-histogram"
            def img2rr = "ossim-img2rr --entry ${entryId} ${histoOption} --compression-type ${compressionType} --compression-quality ${compressionQuality} ${file}"
            def proc = img2rr.execute()
            proc.waitFor()
            RasterEntry.withTransaction{
                def rasterEntry = null
                try{
                    if(id)
                    {
                        rasterEntry = RasterEntry.findByIndexId(id)?:RasterEntry.findById(id as Integer)
                    }
                }
                catch (def e)
                {
                    rasterEntry = null
                }
                updateInfo(rasterEntry)
            }
            //def result = RasterDataSet.findWhere(name:"${file}")
            //println "DONE STAGING................."
        }
    }

}
