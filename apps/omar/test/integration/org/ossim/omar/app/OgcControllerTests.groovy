package org.ossim.omar.app

import grails.test.*
import joms.oms.DataInfo

import org.ossim.omar.ogc.WcsCommand
import org.ossim.omar.ogc.OgcController

class OgcControllerTests extends ControllerUnitTestCase {
    OgcController ogcController

    protected void setUp() {
        super.setUp()
        ogcController = new OgcController()
        mockController(OgcController)
        mockForConstraintsTests(WcsCommand)
    }

    protected void tearDown() {
        super.tearDown()
    }


    void testInvalidWcsCommand() {
        def cmd = new WcsCommand()
        cmd.bbox = "-180,-90,180,90"
        cmd.width="256"
        cmd.height="256"
        cmd.format="image/jpeg"
        cmd.coverage="raster_entry"
        cmd.crs="EPSG:4326"
        cmd.request="GetCoverage"
        assertEquals true, cmd.validate()

    }
}
