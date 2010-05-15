package org.ossim.omar

import org.ossim.omar.Repository
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationContext

class StagerService implements ApplicationContextAware
{

  boolean transactional = true

  def backgroundService
  def sessionFactory

  ApplicationContext applicationContext

  def runStager(Repository repository)
  {
    if ( repository )
    {
      def scanStartDate = new Date()

      Repository.executeUpdate("update org.ossim.omar.Repository r set scanStartDate=?, scanEndDate=null where r=?",
          [scanStartDate, repository])

      backgroundService.execute("Staging", {

        log.info "Staging: ${repository.baseDir}"
        log.info "Start: ${scanStartDate}"

        def filter = new ImageFileFilter()
        def processor = new ImageDetector()
        def handler = applicationContext.getBean("stagerEventHandler")

        handler.init("logs", "repository-${repository.id}")

        filter.addFileFilterEventListener(handler)
        processor.addFileFilterEventListener(handler)
        handler.repository = repository
        handler.sessionFactory = sessionFactory

        FileScanner.visitAllFiles(repository.baseDir as File, filter, processor)
        //handler.cleanupGorm()

        def scanEndDate = new Date()

        Repository.executeUpdate("update org.ossim.omar.Repository r set scanEndDate=? where r=?",
            [scanEndDate, repository])

        log.info " Stop: ${scanEndDate}"
      })
    }
  }
}
