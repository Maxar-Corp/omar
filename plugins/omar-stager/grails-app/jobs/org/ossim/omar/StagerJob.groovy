package org.ossim.omar

import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationContext


class StagerJob implements ApplicationContextAware
{
  static triggers = {}

  ApplicationContext applicationContext
  def sessionFactory

  def execute(def context)
  {
    def repository = Repository.findByBaseDir(context.mergedJobDataMap['baseDir'])

    log.info "Staging: ${repository.baseDir}"
    log.info "Start: ${repository.scanStartDate}"

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
  }
}
