package org.ossim.omar

import org.ossim.omar.core.Repository

class StagerService
{

  static transactional = true

  def runStager(Repository repository)
  {

    repository.scanStartDate = new Date()
    repository.scanEndDate = null
    repository.save()

    StagerJob.triggerNow([baseDir: repository.baseDir])
  }
}
