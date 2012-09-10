package org.ossim.omar.stager

import org.quartz.SimpleTrigger

class RunScriptService {
    def quartzScheduler

    def listJobTriggersByGroup(def group)
    {
        def table = [:]
        def rows = []

        if(group)
        {
            def names = quartzScheduler.getTriggerNames(group);
            names.length.times{j->
                def trigger = quartzScheduler.getTrigger(names[j], group);
                def data = []
                data<< trigger.name
                data<< trigger.jobName
                data<< trigger.jobGroup
                if(trigger.timesTriggered>0)
                    data<<"Yes"
                else
                    data<<"No"
                data<< trigger.startTime
                rows << data
            }
        }
        else
        {
            def groupNames = quartzScheduler.triggerGroupNames
            def groupSize = groupNames.length;
            groupSize.times{idx->
                def names = quartzScheduler.getTriggerNames(groupNames[idx]);
                for (int j = 0; j < names.length; j++)
                {
                    def trigger = quartzScheduler.getTrigger(names[j], groupNames[j]);
                    if(trigger)
                    {
                        def jobDetail = quartzScheduler.getJobDetail(trigger.jobName, trigger.jobGroup)
                        def data = []
                        data<< trigger.name
                        data<< trigger.jobName
                        data<< trigger.jobGroup
                        if(trigger.timesTriggered>0)
                            data<<"Yes"
                        else
                            data<<"No"
                        data<< trigger.startTime
                        rows << data
                    }
                }
            }
        }

        table.labels = ["Name", "Job Name", "Group", "Executing", "Start Time"]
        table.rows   = rows

        table
    }

}
