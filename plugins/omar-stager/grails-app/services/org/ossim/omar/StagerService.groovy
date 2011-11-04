package org.ossim.omar

import org.ossim.omar.Repository

class StagerService
{

  static transactional = true

  def runStager(Repository repository)
  {
    if ( repository )
    {
      def scanStartDate = new Date()

      Repository.executeUpdate("update org.ossim.omar.Repository r set scanStartDate=?, scanEndDate=null where r=?",
              [scanStartDate, repository])

      StagerJob.triggerNow([baseDir: repository.baseDir])
    }
  }
}
