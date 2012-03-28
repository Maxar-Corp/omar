package org.ossim.omar.video

import org.ossim.omar.core.Repository

class VideoDataSetService
{

  static transactional = true

  def deleteFromRepository(Repository repository)
  {
    //VideoDataSet.executeUpdate("delete from VideoDataSet v where v.repository=?", [repository])
    
    def videoDataSets = VideoDataSet.findAllByRepository(repository)

    videoDataSets?.each { it.delete() }
  }
}
