package org.ossim.omar

import org.ossim.omar.core.Repository

class RasterDataSet
{
  static hasMany = [fileObjects: RasterFile, rasterEntries: RasterEntry]

  Repository repository

  static constraints = {
    repository(nullable: true)
  }
  static mapping = {
  }

  static RasterDataSet initRasterDataSet(rasterDataSetNode, rasterDataSet = null)
  {
    rasterDataSet = rasterDataSet ?: new RasterDataSet()
    rasterDataSetNode.fileObjects.RasterFile.each {rasterFileNode ->
      RasterFile rasterFile = RasterFile.initRasterFile(rasterFileNode)
      rasterDataSet.addToFileObjects(rasterFile)
    }

    rasterDataSetNode.rasterEntries.RasterEntry.each {rasterEntryNode ->
      RasterEntry rasterEntry = new RasterEntry();
      rasterEntry.rasterDataSet = rasterDataSet;
      RasterEntry.initRasterEntry(rasterEntryNode, rasterEntry)

      if ( rasterEntry.groundGeom )
      {
        rasterDataSet.addToRasterEntries(rasterEntry)
      }
    }

    return rasterDataSet
  }
  def getFileFromObjects(def type="main")
  {
    return fileObjects?.find { it.type == type }
  }
}
