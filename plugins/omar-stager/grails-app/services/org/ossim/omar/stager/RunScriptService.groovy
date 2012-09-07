package org.ossim.omar.stager

class RunScriptService {
    def quartzScheduler

    def listJobTriggersByGroup(def group)
    {
        def result = []
        def names = quartzScheduler.getTriggerNames(group);

        for (int j = 0; j < names.length; j++)
        {
            def info = [:]

            def trigger = quartzScheduler.getTrigger(names[j], group);
            info.startTime = trigger.startTime
            info.endTime = trigger.endTime
            info.name = trigger.name
            result << info
        //    var currentJob = quartzScheduler.getJobDetail(names[j], group);
        //    result.fullName = currentJob.fullName
        }

        result
    }

}
