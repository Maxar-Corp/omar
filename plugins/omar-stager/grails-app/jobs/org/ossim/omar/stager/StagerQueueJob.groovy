package org.ossim.omar.stager

import java.util.concurrent.atomic.AtomicInteger
import org.quartz.JobExecutionContext
import org.quartz.JobDataMap
import scala.actors.threadpool.AtomicInteger
import org.ossim.omar.core.HttpStatusMessage


class StagerQueueJob
{
  static triggers = {
    simple group: 'StagerQueue', name: 'StagerQueueJob',
        priority: 10, startDelay: 60000, repeatInterval: 10000
  }
  def concurrent = false
  def grailsApplication
  def stagerService
  static entered = 0

  def execute(JobExecutionContext context)
  {
    def start = System.currentTimeMillis()
    int count = 0;
    def tempCount = 0;
    try
    {
      while ( tempCount = stagerService.popAndAddStagerQueueItem() )
      {
        count += tempCount
      }
    }
    catch ( def e )
    {

    }

    def stop = System.currentTimeMillis()
    if ( count )
    {
      log.info( "Staged ${count} images in ${( stop - start ) / 1000} seconds" )
    }
  }
}
