package org.ossim.omar.stager

import org.quartz.JobDataMap
import org.quartz.Job


class StageImageJob{
    static triggers = {}

    def stageImageService

    def execute(org.quartz.JobExecutionContext context) {
        JobDataMap dataMap = context.getMergedJobDataMap();
        stageImageService.stageImage(dataMap.file as File,
                                     dataMap.entryId,
                                     dataMap.id,
                                     dataMap.options);


    }
}
