package org.ossim.omar

import grails.test.*

class WcsCommandTests extends GrailsUnitTestCase {
    protected void setUp() {
        super.setUp()
        mockForConstraintsTests(WcsCommand)
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testWcsCommand() {
        def cmd = new WcsCommand()
        cmd.bbox = "-180,-90,180,90"
        cmd.width="256"
        cmd.height="256"
        cmd.format="image/jpeg"
        cmd.coverage="raster_entry"
        cmd.crs="EPSG:4326"
        cmd.request="GetCoverage"
        assertEquals true, cmd.validate()
        cmd.bbox = null
        assertEquals false, cmd.validate()
        assertEquals true, cmd.createErrorString().contains("BBOX parameter not found")
        cmd.bbox = ""
        assertEquals false, cmd.validate()
        assertEquals true, cmd.createErrorString().contains("BBOX parameter not found")
        cmd.bbox="-180,-90,180,90"
        // now test combinations of resx, resy and width, height
        cmd.width  = null
        cmd.height = null
        cmd.resx   = null
        cmd.resy   = null
        assertEquals false, cmd.validate()
        def contentString = cmd.createErrorString()
        assertEquals true, (contentString.contains("WIDTH parameter not found") ||
                     contentString.contains("HEIGHT parameter not found") ||
                     contentString.contains("RESX parameter not found")||
                     contentString.contains("RESY parameter not found"))
        cmd.width = "256"
        assertEquals false, cmd.validate()
        assertEquals true, cmd.createErrorString().contains("HEIGHT parameter not found")
        cmd.width = null
        cmd.height = "256"
        assertEquals false, cmd.validate()
        assertEquals true, cmd.createErrorString().contains("WIDTH parameter not found")
        cmd.width  = null
        cmd.height = null
        cmd.resx   = "0.001"
        cmd.resy   = null
        assertEquals false, cmd.validate()
        assertEquals true, cmd.createErrorString().contains("RESY parameter not found")
        cmd.resx   = ""
        cmd.resy   = "0.001"
        assertEquals false, cmd.validate()
        assertEquals true, cmd.createErrorString().contains("RESX parameter not found")
        cmd.width  = "256"
        cmd.height = "256"
        cmd.resx   = null
        cmd.resy   = null

        cmd.format="" // invalidate format
        assertEquals false, cmd.validate()
        assertEquals true, cmd.createErrorString().contains("FORMAT parameter not found")
        cmd.format=null // invalidate format
        assertEquals false, cmd.validate()
        assertEquals true, cmd.createErrorString().contains("FORMAT parameter not found")
        cmd.format="image/df" // invalidate format
        assertEquals false, cmd.validate()
        assertEquals true, cmd.createErrorString().contains("FORMAT parameter image/df not supported")
        cmd.format="image/jpeg" // give valid value

        cmd.coverage = "" // invalidate coverage
        assertEquals false, cmd.validate()
        assertEquals true, cmd.createErrorString().contains("COVERAGE parameter not found")
        cmd.coverage = null // invalidate coverage
        assertEquals false, cmd.validate()
        assertEquals true, cmd.createErrorString().contains("COVERAGE parameter not found")
        cmd.coverage = "raster_entry" // give a valid value again

        cmd.crs=""
        assertEquals false, cmd.validate()
        assertEquals true, cmd.createErrorString().contains("CRS parameter not found")
        cmd.crs=null
        assertEquals false, cmd.validate()
        assertEquals true, cmd.createErrorString().contains("CRS parameter not found")
        cmd.crs="EPSG:3232"
        assertEquals false, cmd.validate()
        assertEquals true, cmd.createErrorString().contains("CRS parameter EPSG:3232")
        cmd.crs="EPSG:4326"

        cmd.response_crs="EPSG:3232"
        assertEquals false, cmd.validate()
        assertEquals true, cmd.createErrorString().contains("RESPONSE_CRS is specified and is not equal")
        cmd.response_crs=""

        cmd.request=""
        assertEquals false, cmd.validate()
        assertEquals true, cmd.createErrorString().contains("REQUEST parameter not found")
        cmd.request=null
        assertEquals false, cmd.validate()
        assertEquals true, cmd.createErrorString().contains("REQUEST parameter not found")
        cmd.request="nogood"
        assertEquals false, cmd.validate()
        assertEquals true, cmd.createErrorString().contains("REQUEST parameter nogood is invalid")
        cmd.request="GetCoverage"
    }
}
