package org.ossim.omar

import grails.test.*
import joms.oms.DataInfo
import grails.orm.HibernateCriteriaBuilder
class OgcControllerTests extends ControllerUnitTestCase {
    OgcController ogcController

    protected void setUp() {
        super.setUp()
        ogcController = new OgcController()
        mockController(OgcController)
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

        cmd.bbox = null
        ogcController.wcs(cmd)
        assertEquals true, ogcController.response.contentAsString.contains("BBOX parameter not found")
        cmd.bbox = ""
        ogcController.wcs(cmd)
        assertEquals true, ogcController.response.contentAsString.contains("BBOX parameter not found")
        cmd.bbox="-180,-90,180,90"

        // now test combinations of resx, resy and width, height
        cmd.width  = null
        cmd.height = null
        cmd.resx   = null
        cmd.resy   = null
        ogcController.wcs(cmd)
        def contentString = ogcController.response.contentAsString
        assertEquals true, (contentString.contains("WIDTH parameter not found") ||
                     contentString.contains("HEIGHT parameter not found") ||
                     contentString.contains("RESX parameter not found")||
                     contentString.contains("RESY parameter not found"))

        cmd.width = "256"
        ogcController.wcs(cmd)
        assertEquals true, ogcController.response.contentAsString.contains("HEIGHT parameter not found")
        cmd.width = null
        cmd.height = "256"
        ogcController.wcs(cmd)
        assertEquals true, ogcController.response.contentAsString.contains("WIDTH parameter not found")
        cmd.width  = null
        cmd.height = null
        cmd.resx   = "0.001"
        cmd.resy   = null
        ogcController.wcs(cmd)
        assertEquals true, ogcController.response.contentAsString.contains("RESY parameter not found")
        cmd.resx   = ""
        cmd.resy   = "0.001"
        ogcController.wcs(cmd)
        assertEquals true, ogcController.response.contentAsString.contains("RESX parameter not found")
        cmd.width  = "256"
        cmd.height = "256"
        cmd.resx   = null
        cmd.resy   = null

        cmd.format="" // invalidate format
        ogcController.wcs(cmd)
        assertEquals true, ogcController.response.contentAsString.contains("FORMAT parameter not found")
        cmd.format=null // invalidate format
        ogcController.wcs(cmd)
        assertEquals true, ogcController.response.contentAsString.contains("FORMAT parameter not found")
        cmd.format="image/df" // invalidate format
        ogcController.wcs(cmd)
        assertEquals true, ogcController.response.contentAsString.contains("FORMAT parameter image/df not supported")
        cmd.format="image/jpeg" // give valid value

        cmd.coverage = "" // invalidate coverage
        ogcController.wcs(cmd)
        assertEquals true, ogcController.response.contentAsString.contains("COVERAGE parameter not found")
        cmd.coverage = null // invalidate coverage
        ogcController.wcs(cmd)
        assertEquals true, ogcController.response.contentAsString.contains("COVERAGE parameter not found")
        cmd.coverage = "raster_entry" // give a valid value again

        cmd.crs=""
        ogcController.wcs(cmd)
        assertEquals true, ogcController.response.contentAsString.contains("CRS parameter not found")
        cmd.crs=null
        ogcController.wcs(cmd)
        assertEquals true, ogcController.response.contentAsString.contains("CRS parameter not found")
        cmd.crs="EPSG:3232"
        ogcController.wcs(cmd)
        assertEquals true, ogcController.response.contentAsString.contains("CRS parameter EPSG:3232")
        cmd.crs="EPSG:4326"

        cmd.response_crs="EPSG:3232"
        ogcController.wcs(cmd)
        assertEquals true, ogcController.response.contentAsString.contains("RESPONSE_CRS is specified and is not equal")
        cmd.response_crs=""

        cmd.request=""
        ogcController.wcs(cmd)
        assertEquals true, ogcController.response.contentAsString.contains("REQUEST parameter not found")
        cmd.request=null
        ogcController.wcs(cmd)
        assertEquals true, ogcController.response.contentAsString.contains("REQUEST parameter not found")
        cmd.request="nogood"
        ogcController.wcs(cmd)
        assertEquals true, ogcController.response.contentAsString.contains("REQUEST parameter nogood is invalid")
        cmd.request="GetCoverage"
    }
}
