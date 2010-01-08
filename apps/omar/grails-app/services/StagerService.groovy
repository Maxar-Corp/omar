class StagerService
{

  boolean transactional = true

  def backgroundService
  def sessionFactory

  def runStager(Repository repository)
  {
    if ( repository )
    {
      def scanStartDate = new Date()

      Repository.executeUpdate("update Repository r set scanStartDate=?, scanEndDate=null where r=?",
          [scanStartDate, repository])

      backgroundService.execute("Staging", {

        println "Staging: ${repository.baseDir}"
        println "Start: ${scanStartDate}"

        def filter = new ImageFileFilter()
        def processor = new ImageDetector()
        def handler = new StagerEventHandler("logs", "repository-${repository.id}")

        filter.addFileFilterEventListener(handler)
        processor.addFileFilterEventListener(handler)
        handler.repository = repository
        handler.sessionFactory = sessionFactory

        FileScanner.visitAllFiles(repository.baseDir as File, filter, processor)
        handler.cleanupGorm()

        def scanEndDate = new Date()

        Repository.executeUpdate("update Repository r set scanEndDate=? where r=?",
            [scanEndDate, repository])

        println " Stop: ${scanEndDate}"
      })
    }
  }
}
