package org.ossim.omar.stager

import org.ossim.omar.core.Repository
import org.ossim.omar.StagerJob

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
