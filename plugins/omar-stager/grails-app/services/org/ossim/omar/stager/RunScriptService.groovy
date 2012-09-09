package org.ossim.omar.stager

import org.quartz.SimpleTrigger

class RunScriptService {
    def quartzScheduler

    def listJobTriggersByGroup(def group)
    {
        def table = [:]
        def names = quartzScheduler.getTriggerNames(group);

        def rows = []
        for (int j = 0; j < names.length; j++)
        {
            def trigger = quartzScheduler.getTrigger(names[j], group);
            def data = []
            data<< trigger.name
            data<< trigger.jobName
            data<< trigger.jobGroup
            data<< trigger.timesTriggered?true:false
            data<< trigger.startTime
            rows << data
        }

        table.labels = ["Name", "Job Name", "Group", "Executing", "Start Time"]
        table.rows   = rows

        table
    }

}
