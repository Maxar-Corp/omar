package org.ossim.omar

import grails.test.*
import org.ossim.omar.ogc.WmsCommand

class WmsCommandTests extends GrailsUnitTestCase {
    protected void setUp() {
        super.setUp()
        mockForConstraintsTests(WmsCommand)
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testWmsValidation() {
        def cmd = new WmsCommand()
        cmd.bbox = "-180,-90,180,90"
        cmd.width="256"
        cmd.height="256"
        cmd.format="image/jpeg"
        cmd.layers="raster_entry"
        cmd.srs="EPSG:4326"
        cmd.request="GetMap"
        cmd.styles=""
        assertEquals true, cmd.validate()

        cmd.bbox = null
        assertEquals false, cmd.validate()
        assertEquals true, cmd.createErrorString().contains("BBOX parameter not found")
        cmd.bbox = ""
        assertEquals false, cmd.validate()
        assertEquals true, cmd.createErrorString().contains("BBOX parameter not found")
        cmd.bbox="-180,-90,180,f90"
        assertEquals false, cmd.validate()
        assertEquals true, cmd.createErrorString().contains("BBOX parameter invalid")
        cmd.bbox = "-180,-90,180,90"

        cmd.width = null
        assertEquals false, cmd.validate()
        assertEquals true, cmd.createErrorString().contains("WIDTH parameter not found")
        cmd.width = ""
        assertEquals false, cmd.validate()
        assertEquals true, cmd.createErrorString().contains("WIDTH parameter not found")
        cmd.width="256"

        cmd.height = null
        assertEquals false, cmd.validate()
        assertEquals true, cmd.createErrorString().contains("HEIGHT parameter not found")
        cmd.height = ""
        assertEquals false, cmd.validate()
        assertEquals true, cmd.createErrorString().contains("HEIGHT parameter not found")
        cmd.height="256"

        cmd.format = null
        assertEquals false, cmd.validate()
        assertEquals true, cmd.createErrorString().contains("FORMAT parameter not found")
        cmd.format = ""
        assertEquals false, cmd.validate()
        assertEquals true, cmd.createErrorString().contains("FORMAT parameter not found")
        cmd.format="image/jg"
        assertEquals false, cmd.validate()
        assertEquals true, cmd.createErrorString().contains("FORMAT parameter invalid")
        cmd.format="image/jpeg"
        assertEquals true, cmd.validate()
        cmd.format="image/png"
        assertEquals true, cmd.validate()
        cmd.format="image/gif"
        assertEquals true, cmd.validate()
        cmd.format="image/jpeg"


        cmd.layers = null
        assertEquals false, cmd.validate()
        assertEquals true, cmd.createErrorString().contains("LAYERS parameter not found")
        cmd.layers = ""
        assertEquals false, cmd.validate()
        assertEquals true, cmd.createErrorString().contains("LAYERS parameter not found")
        cmd.layers = "raster_entry"

        cmd.styles = null
        assertEquals false, cmd.validate()
        assertEquals true, cmd.createErrorString().contains("STYLES parameter not found")
        cmd.styles = ""
        assertEquals true, cmd.validate()

        cmd.srs = null
        assertEquals false, cmd.validate()
        assertEquals true, cmd.createErrorString().contains("SRS parameter not found")
        cmd.srs = ""
        assertEquals false, cmd.validate()
        assertEquals true, cmd.createErrorString().contains("SRS parameter not found")
        cmd.srs = "EPS:df"
        assertEquals false, cmd.validate()
        assertEquals true, cmd.createErrorString().contains("SRS parameter EPS:df not supporte")
        cmd.srs = "EPSG:4326"


        cmd.request = null
        assertEquals false, cmd.validate()
        assertEquals true, cmd.createErrorString().contains("REQUEST parameter not found")
        cmd.request = ""
        assertEquals false, cmd.validate()
        assertEquals true, cmd.createErrorString().contains("REQUEST parameter not found")
        cmd.request = "getstuff"
        assertEquals false, cmd.validate()
        assertEquals true, cmd.createErrorString().contains("REQUEST parameter getstuff is")
        cmd.request="GetMap"

        cmd.bgcolor = "0xFFFGFF"
        assertEquals false, cmd.validate()
        assertEquals true, cmd.createErrorString().contains("BGCOLOR parameter invalid")
        cmd.bgcolor = "0xff"
        assertEquals false, cmd.validate()
        assertEquals true, cmd.createErrorString().contains("BGCOLOR parameter invalid")
        cmd.bgcolor = "ffffff"
        assertEquals false, cmd.validate()
        assertEquals true, cmd.createErrorString().contains("BGCOLOR parameter invalid")
        cmd.bgcolor=null


        cmd.rotate="ads"
        assertEquals false, cmd.validate()
        assertEquals true, cmd.createErrorString().contains("ROTATE parameter invalid")
        cmd.rotate="90"
        assertEquals true, cmd.validate()

   }
}
