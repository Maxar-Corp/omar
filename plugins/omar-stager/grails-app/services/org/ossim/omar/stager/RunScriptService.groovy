package org.ossim.omar.stager

import org.quartz.SimpleTrigger

class RunScriptService {
    def quartzScheduler

    def listScheduledJobs()
    {
        def table = [:]
        def rows = []

        def jobList = quartzScheduler.getCurrentlyExecutingJobs();

        def currentlyExecuting = []
        jobList.each{jobExecutionContext->
            def data = []
            def jobDetail = jobExecutionContext.getJobDetail();
            def strJobName = jobDetail.name;
            def strDescription = jobDetail.description;

            // Trigger Details
            def trigger = jobExecutionContext.trigger;
            def strTriggerName = trigger.name;
            def strFireInstanceId = trigger.fireInstanceId;
            int state = quartzScheduler.getTriggerState(strTriggerName,
                                                        trigger.group);
            data<< strTriggerName
            data<< strJobName
            data<< trigger.group
            data<< "Yes"
            data<< trigger.startTime
            rows << data

            currentlyExecuting << trigger.fullName
        }
        //foreach (def jobDetail in from jobGroupName in scheduler1.JobGroupNames
        //        from jobName in scheduler1.GetJobNames(jobGroupName)
        //        select scheduler1.GetJobDetail(jobName, jobGroupName))
        //        {
                    //Get props about job from jobDetail
        //        }
        quartzScheduler.triggerGroupNames.each{triggerGroupName->
            def triggerNames = quartzScheduler.getTriggerNames(triggerGroupName)
            triggerNames.each{triggerName->
                def trigger = quartzScheduler.getTrigger(triggerName, triggerGroupName)
                if(trigger.nextFireTime && !(trigger.fullName in currentlyExecuting) )
                {
                    def data = []
                    data << trigger.name
                    data << trigger.name
                    data << triggerGroupName
                    data << "No"
                    data << ""
                    rows << data
                }
            }
        }
        //foreach (def triggerDetail in from triggerGroupName in scheduler1.TriggerGroupNames
        //        from triggerName in scheduler1.GetTriggerNames(triggerGroupName)
        //        select scheduler1.GetTrigger(triggerName, triggerGroupName))
        //        {
                    //Get props about trigger from triggerDetail
        //        }

        table.labels = ["Name", "Job Name", "Group", "Executing", "Start Time"]
        table.rows   = rows

        table
    }
}
