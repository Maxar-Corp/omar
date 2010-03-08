package org.ossim.omar

import org.ossim.omar.RasterEntry

class ThumbnailControllerTests extends GroovyTestCase {

    void testSomething() {
      def rasterEntries = RasterEntry.list()

      rasterEntries.each { rasterEntry ->

        def filename = rasterEntry.rasterDataSet.fileObjects[0].name

        println filename

        assert filename != null
      }

    }
}
