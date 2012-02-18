package org.ossim.omar

import grails.test.*
import java.awt.image.BufferedImage
import org.ossim.omar.ogc.WcsCommand
import org.ossim.omar.ogc.OgcExceptionService

class OgcExceptionServiceTests extends GrailsUnitTestCase {
    def ogcExceptionService
    protected void setUp() {
        super.setUp()
        mockForConstraintsTests(WcsCommand)
        ogcExceptionService = new OgcExceptionService()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testImageWcsException() {
        def cmd = new WcsCommand()
        cmd.bbox = "-180,-90,180,90"
        cmd.width="256"
        cmd.height="256"
        cmd.format="image/jpeg"
        cmd.coverage="raster_entry"
        cmd.crs="EPSG:4326"
        cmd.request="GetCoverage"

        cmd.exception = "application/vnd.ogc.se_inimage"
        cmd.request = null
        cmd.validate();
        assertEquals false, cmd.validate()
        assertEquals true, ogcExceptionService.formatWcsException(cmd).message instanceof BufferedImage

        cmd.exception = "inimage"
        cmd.request = null
        cmd.validate();
        assertEquals false, cmd.validate()
        assertEquals true, ogcExceptionService.formatWcsException(cmd).message instanceof BufferedImage
    }

    void testXmlWcsException() {
        def cmd = new WcsCommand()
        cmd.bbox = "-180,-90,180,90"
        cmd.width="256"
        cmd.height="256"
        cmd.format="image/jpeg"
        cmd.coverage="raster_entry"
        cmd.crs="EPSG:4326"
        cmd.request="GetCoverage"

        cmd.exception = "text/xml"
        cmd.request = null
        cmd.validate();
        assertEquals false, cmd.validate()
        assertEquals true, ogcExceptionService.formatWcsException(cmd).message.contains("REQUEST")
        assertEquals true, (ogcExceptionService.formatWcsException(cmd).mimeType == "text/xml")
    }

    void testTextWcsException() {
        def cmd = new WcsCommand()
        cmd.bbox = "-180,-90,180,90"
        cmd.width="256"
        cmd.height="256"
        cmd.format="image/jpeg"
        cmd.coverage="raster_entry"
        cmd.crs="EPSG:4326"
        cmd.request="GetCoverage"

        cmd.exception = "text/plain"
        cmd.request = null
        cmd.validate();
        assertEquals false, cmd.validate()
        assertEquals true, ogcExceptionService.formatWcsException(cmd).message.contains("REQUEST")
        assertEquals true, (ogcExceptionService.formatWcsException(cmd).mimeType == "text/plain")
    }

}
