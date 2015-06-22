package org.ossim.omar.stager

import org.quartz.SimpleTrigger
import org.quartz.TriggerKey
import org.quartz.impl.matchers.GroupMatcher

class RunScriptService
{
  def quartzScheduler

  def listScheduledJobs()
  {
    def table = [:]
    def rows = []

    def jobList = quartzScheduler.getCurrentlyExecutingJobs();

    def currentlyExecuting = []
    jobList.each { jobExecutionContext ->
      def data = []
      def jobDetail = jobExecutionContext.getJobDetail();
      def strJobName = jobDetail.name;
      def strDescription = jobDetail.description;

      // Trigger Details
      def trigger = jobExecutionContext.trigger;
      def strTriggerName = trigger.name;
      def strFireInstanceId = trigger.fireInstanceId;


      def  state = quartzScheduler.getTriggerState( new TriggerKey( strTriggerName, trigger.group ) );

      data << strTriggerName
      data << strJobName
      data << trigger.group
      data << "Yes"
      data << trigger.startTime
      rows << data

      currentlyExecuting << trigger.fullName
    }
    //foreach (def jobDetail in from jobGroupName in scheduler1.JobGroupNames
    //        from jobName in scheduler1.GetJobNames(jobGroupName)
    //        select scheduler1.GetJobDetail(jobName, jobGroupName))
    //        {
    //Get props about job from jobDetail
    //        }
    quartzScheduler.triggerGroupNames.each { triggerGroupName ->
      def triggerKeys = quartzScheduler.getTriggerKeys(
          GroupMatcher.groupEquals( triggerGroupName )
      )

      triggerKeys.each { triggerKey ->
        def trigger = quartzScheduler.getTrigger( triggerKey )
        if ( trigger.nextFireTime && !( trigger.fullName in currentlyExecuting ) )
        {
          def data = []
          data << trigger.name
          data << trigger.name
          data << triggerKey.group
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
    table.rows = rows

    table
  }
}
