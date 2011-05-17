package org.ossim.omar

import grails.test.*

class OgcControllerTests extends ControllerUnitTestCase {
    protected void setUp() {
        super.setUp()
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
        def cntr = new OgcController()
        cntr.wcs(cmd)
        // validate initial paramter set
        //
        //assertEquals false, cmd.hasErrors()

        cmd.bbox = null
        cntr.wcs(cmd)
        assertEquals true, cntr.response.contentAsString.contains("BBOX parameter not found")
        cmd.bbox = ""
        cntr.wcs(cmd)
        assertEquals true, cntr.response.contentAsString.contains("BBOX parameter not found")
        cmd.bbox="-180,-90,180,90"

        // now test combinations of resx, resy and width, height
        cmd.width  = null
        cmd.height = null
        cmd.resx   = null
        cmd.resy   = null
        cntr.wcs(cmd)
        def contentString = cntr.response.contentAsString
        assertEquals true, (contentString.contains("WIDTH parameter not found") ||
                     contentString.contains("HEIGHT parameter not found") ||
                     contentString.contains("RESX parameter not found")||
                     contentString.contains("RESY parameter not found"))

        cmd.width = "256"
        cntr.wcs(cmd)
        assertEquals true, cntr.response.contentAsString.contains("HEIGHT parameter not found")
        cmd.width = null
        cmd.height = "256"
        cntr.wcs(cmd)
        assertEquals true, cntr.response.contentAsString.contains("WIDTH parameter not found")
        cmd.width  = null
        cmd.height = null
        cmd.resx   = "0.001"
        cmd.resy   = null
        cntr.wcs(cmd)
        assertEquals true, cntr.response.contentAsString.contains("RESY parameter not found")
        cmd.resx   = ""
        cmd.resy   = "0.001"
        cntr.wcs(cmd)
        assertEquals true, cntr.response.contentAsString.contains("RESX parameter not found")
        cmd.width  = "256"
        cmd.height = "256"
        cmd.resx   = null
        cmd.resy   = null

        cmd.format="" // invalidate format
        cntr.wcs(cmd)
        assertEquals true, cntr.response.contentAsString.contains("FORMAT parameter not found")
        cmd.format=null // invalidate format
        cntr.wcs(cmd)
        assertEquals true, cntr.response.contentAsString.contains("FORMAT parameter not found")
        cmd.format="image/df" // invalidate format
        cntr.wcs(cmd)
        assertEquals true, cntr.response.contentAsString.contains("FORMAT parameter image/df not supported")
        cmd.format="image/jpeg" // give valid value

        cmd.coverage = "" // invalidate coverage
        cntr.wcs(cmd)
        assertEquals true, cntr.response.contentAsString.contains("COVERAGE parameter not found")
        cmd.coverage = null // invalidate coverage
        cntr.wcs(cmd)
        assertEquals true, cntr.response.contentAsString.contains("COVERAGE parameter not found")
        cmd.coverage = "raster_entry" // give a valid value again

        cmd.crs=""
        cntr.wcs(cmd)
        assertEquals true, cntr.response.contentAsString.contains("CRS parameter not found")
        cmd.crs=null
        cntr.wcs(cmd)
        assertEquals true, cntr.response.contentAsString.contains("CRS parameter not found")
        cmd.crs="EPSG:3232"
        cntr.wcs(cmd)
        assertEquals true, cntr.response.contentAsString.contains("CRS parameter EPSG:3232")
        cmd.crs="EPSG:4326"

        cmd.response_crs="EPSG:3232"
        cntr.wcs(cmd)
        assertEquals true, cntr.response.contentAsString.contains("RESPONSE_CRS is specified and is not equal")
        cmd.response_crs=""

        cmd.request=""
        cntr.wcs(cmd)
        assertEquals true, cntr.response.contentAsString.contains("REQUEST parameter not found")
        cmd.request=null
        cntr.wcs(cmd)
        assertEquals true, cntr.response.contentAsString.contains("REQUEST parameter not found")
        cmd.request="nogood"
        cntr.wcs(cmd)
        assertEquals true, cntr.response.contentAsString.contains("REQUEST parameter nogood is invalid")
        cmd.request="GetCoverage"
    }
}
